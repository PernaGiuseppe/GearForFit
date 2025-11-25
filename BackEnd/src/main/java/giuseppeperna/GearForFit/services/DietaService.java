package giuseppeperna.GearForFit.services;

import giuseppeperna.GearForFit.entities.Alimenti.Alimento;
import giuseppeperna.GearForFit.entities.Diete.*;
import giuseppeperna.GearForFit.entities.Utente.TipoPiano;
import giuseppeperna.GearForFit.entities.Utente.TipoUtente;
import giuseppeperna.GearForFit.entities.Utente.Utente;
import giuseppeperna.GearForFit.exceptions.NotFoundException;
import giuseppeperna.GearForFit.exceptions.UnauthorizedException;
import giuseppeperna.GearForFit.payloads.*;
import giuseppeperna.GearForFit.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DietaService {

    @Autowired
    private DietaStandardRepository dietaStandardRepository;

    @Autowired
    private CalcoloBMRService calcoloBMRService;

    @Autowired
    private CalcoloBMRRepository calcoloBMRRepository;

    @Autowired
    private PastoStandardRepository pastoStandardRepository;

    @Autowired
    private DietaStandardAlimentoRepository dietaStandardAlimentoRepository;

    @Autowired
    private AlimentoService alimentoService;

    @Autowired
    private UtenteService utenteService;

    @Autowired
    private DietaUtenteRepository dietaUtenteRepository;

    @Autowired
    private AlimentoRepository alimentoRepository;

    // ----------- METODI PER DIETE STANDARD (ADMIN) -----------

    public List<DietaStandardDTO> getAllDieteStandard() {
        return dietaStandardRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<DietaStandardDTO> getDietaStandardById(Long id) {
        return dietaStandardRepository.findById(id).map(this::convertToDTO);
    }

    /*  public DietaStandardDTO getDietaStandardByTipo(TipoDieta tipoDieta) {
          // 1. findByTipoDieta ora restituisce una List<DietaStandard>
          List<DietaStandard> dieteTrovate = dietaStandardRepository.findByTipoDieta(tipoDieta);

          // 2. Controlla se la lista è vuota
          if (dieteTrovate.isEmpty()) {
              throw new RuntimeException("Nessuna dieta standard trovata per il tipo: " + tipoDieta);
          }

          // 3. Prendi il primo elemento della lista e convertilo in DTO
          DietaStandard dietaDaRestituire = dieteTrovate.get(0);

          return convertToDTO(dietaDaRestituire);
      }*/
    // Ottieni tutte le diete standard filtrate per TipoDieta
    public List<DietaStandardDTO> getDieteStandardByTipo(TipoDieta tipoDieta) {
        List<DietaStandard> dieteTrovate = dietaStandardRepository.findByTipoDieta(tipoDieta);

        // Converti tutte le diete trovate in DTO
        return dieteTrovate.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<Object> getAllDieteFiltered(Utente utente, String tipoFiltro) {
        List<Object> result = new ArrayList<>();

        // 1. Recupero Diete STANDARD
        // Le mostriamo se il filtro è "ALL" oppure "STANDARD"
        if ("ALL".equalsIgnoreCase(tipoFiltro) || "STANDARD".equalsIgnoreCase(tipoFiltro)) {
            List<DietaStandard> standardEntities = dietaStandardRepository.findAll();
            List<DietaStandardDTO> standardDTOs = standardEntities.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            result.addAll(standardDTOs);
        }

        // 2. Recupero Diete PERSONALIZZATE (Assegnate)
        // Le mostriamo se il filtro è "ALL" o "PERSONALIZZATE" E se l'utente ha il piano adeguato (SILVER+)
        boolean canAccessPersonalized = isSilverOrAbove(utente.getTipoPiano());

        if (canAccessPersonalized && ("ALL".equalsIgnoreCase(tipoFiltro) || "PERSONALIZZATE".equalsIgnoreCase(tipoFiltro))) {
            // Recupera le diete assegnate all'utente specifico
            List<DietaUtente> personalizzateEntities = dietaUtenteRepository.findByUtenteId(utente.getId());
            List<DietaUtenteDTO> personalizzateDTOs = personalizzateEntities.stream()
                    .map(this::convertDietaUtenteToDTO)
                    .collect(Collectors.toList());
            result.addAll(personalizzateDTOs);
        }

        return result;
    }

    // Helper per verificare i permessi del piano (Silver o superiore)
    private boolean isSilverOrAbove(giuseppeperna.GearForFit.entities.Utente.TipoPiano piano) {
        return piano == giuseppeperna.GearForFit.entities.Utente.TipoPiano.SILVER ||
                piano == giuseppeperna.GearForFit.entities.Utente.TipoPiano.GOLD ||
                piano == giuseppeperna.GearForFit.entities.Utente.TipoPiano.PREMIUM ||
                piano == giuseppeperna.GearForFit.entities.Utente.TipoPiano.ADMIN;
    }
    @Transactional
    public DietaStandardDTO creaDietaStandard(DietaStandardRequestDTO dietaRequest) {
        DietaStandard nuovaDieta = new DietaStandard();
        nuovaDieta.setNome(dietaRequest.nome());
        nuovaDieta.setDescrizione(dietaRequest.descrizione());
        nuovaDieta.setDurataSettimane(dietaRequest.durataSettimane());
        nuovaDieta.setTipoDieta(dietaRequest.tipoDieta());
        dietaStandardRepository.save(nuovaDieta);

        dietaRequest.pasti().forEach(pastoRequest -> {
            PastoStandard pasto = new PastoStandard();
            pasto.setNomePasto(pastoRequest.nomePasto());
            pasto.setOrdine(pastoRequest.ordine());
            pasto.setDietaStandard(nuovaDieta);
            pastoStandardRepository.save(pasto);

            pastoRequest.alimenti().forEach(alimentoRequest -> {
                Alimento alimento = alimentoService.findAlimentoById(alimentoRequest.alimentoId())
                        .orElseThrow(() -> new RuntimeException("Alimento non trovato"));
                DietaStandardAlimento dsa = new DietaStandardAlimento();
                dsa.setPastoStandard(pasto);
                dsa.setAlimento(alimento);
                dsa.setGrammi(alimentoRequest.grammi());

                // ---> INIZIA LA PARTE MANCANTE <---
                double fattore = (double) alimentoRequest.grammi() / 100.0;
                dsa.setProteineG(alimento.getProteinePer100g() * fattore);
                dsa.setCarboidratiG(alimento.getCarboidratiPer100g() * fattore);
                dsa.setGrassiG(alimento.getGrassiPer100g() * fattore);
                dsa.setCalorie(alimento.getCaloriePer100g() * fattore);
                // ---> FINISCE LA PARTE MANCANTE <---

                dietaStandardAlimentoRepository.save(dsa);
            });
        });
        return convertToDTO(nuovaDieta);
    }

    @Transactional
    public DietaStandardDTO aggiornaDietaStandard(Long id, DietaStandardRequestDTO dietaRequest) {
        DietaStandard dieta = dietaStandardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dieta con id " + id + " non trovata"));

        // Rimuovi i vecchi pasti e alimenti
        dieta.getPasti().forEach(pasto -> dietaStandardAlimentoRepository.deleteAll(pasto.getAlimenti()));
        pastoStandardRepository.deleteAll(dieta.getPasti());

        // Aggiorna i dati della dieta
        dieta.setNome(dietaRequest.nome());
        dieta.setDescrizione(dietaRequest.descrizione());
        dieta.setDurataSettimane(dietaRequest.durataSettimane());
        dieta.setTipoDieta(dietaRequest.tipoDieta());

        // Aggiungi i nuovi pasti e alimenti
        dietaRequest.pasti().forEach(pastoRequest -> {
            PastoStandard pasto = new PastoStandard();
            pasto.setNomePasto(pastoRequest.nomePasto());
            pasto.setOrdine(pastoRequest.ordine());
            pasto.setDietaStandard(dieta);
            pastoStandardRepository.save(pasto);

            pastoRequest.alimenti().forEach(alimentoRequest -> {
                Alimento alimento = alimentoService.findAlimentoById(alimentoRequest.alimentoId())
                        .orElseThrow(() -> new RuntimeException("Alimento non trovato"));
                DietaStandardAlimento dsa = new DietaStandardAlimento();
                dsa.setPastoStandard(pasto);
                dsa.setAlimento(alimento);
                dsa.setGrammi(alimentoRequest.grammi());
                dietaStandardAlimentoRepository.save(dsa);
            });
        });
        return convertToDTO(dieta);
    }

    @Transactional
    public DietaUtenteDTO adminModificaDietaUtente(Long utenteId, Long dietaUtenteId, DietaStandardRequestDTO dietaRequest) {        // 1. Controllo esistenza utente (opzionale ma utile)
        utenteService.findById(utenteId); // 1 Lancia NotFoundException se l'utente non esiste

        // 2. Trova l'assegnazione DietaUtente e verifica che sia dell'utente specificato
        DietaUtente dietaUtente = dietaUtenteRepository.findById(dietaUtenteId)
                .orElseThrow(() -> new NotFoundException("Assegnazione dieta con id " + dietaUtenteId + " non trovata."));

        if (!dietaUtente.getUtente().getId().equals(utenteId)) {
            throw new RuntimeException("L'assegnazione dieta con id " + dietaUtenteId + " non appartiene all'utente con id " + utenteId);
        }

        // 3. Ottieni l'ID della DietaStandard associata
        Long dietaStandardId = dietaUtente.getDietaStandard().getId();

        // 4. Aggiorna il template della DietaStandard associata
        aggiornaDietaStandard(dietaStandardId, dietaRequest);

        // 5. Restituisce il DTO dell'assegnazione aggiornata.
        return convertDietaUtenteToDTO(dietaUtente);
    }
    public List<DietaUtenteDTO> adminGetTutteDieteAssegnate() {
        // Recupera tutte le entità DietaUtente
        List<DietaUtente> diete = dietaUtenteRepository.findAll();

        // Converte in DTO
        return diete.stream()
                .map(this::convertDietaUtenteToDTO)
                .collect(Collectors.toList());
    }
    public DietaUtenteDTO adminGetDietaAssegnataById(Long dietaUtenteId) {
        DietaUtente dietaUtente = dietaUtenteRepository.findById(dietaUtenteId)
                .orElseThrow(() -> new NotFoundException("Dieta assegnata (DietaUtente) con ID " + dietaUtenteId + " non trovata."));

        return convertDietaUtenteToDTO(dietaUtente);
    }
    @Transactional
    public void eliminaDieta(Long id) {
        DietaStandard dieta = dietaStandardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dieta con id " + id + " non trovata"));

        // 1. Trova ed elimina tutte le assegnazioni DietaUtente collegate a questa DietaStandard
        List<DietaUtente> assegnazioni = dietaUtenteRepository.findByDietaStandardId(id); // Utilizza il metodo del repository
        if (!assegnazioni.isEmpty()) {
            dietaUtenteRepository.deleteAll(assegnazioni);
        }

        // 2. Elimina gli alimenti, i pasti standard e la dieta standard stessa
        dieta.getPasti().forEach(pasto -> dietaStandardAlimentoRepository.deleteAll(pasto.getAlimenti()));
        pastoStandardRepository.deleteAll(dieta.getPasti());
        dietaStandardRepository.delete(dieta);
    }

    // ----------- METODI PER DIETE PERSONALIZZATE (UTENTE) -----------

    // Nel metodo salvaCalcoloBMR:
    @Transactional
    public CalcoloBMR salvaCalcoloBMR(Long utenteId, CalcoloBMRDTO dto) {
        Utente utente = utenteService.findById(utenteId);
        CalcoloBMR calcolo = calcoloBMRRepository.findByUtenteId(utenteId)
                .orElse(new CalcoloBMR());

        calcolo.setUtente(utente);
        calcolo.setPeso(dto.peso());
        calcolo.setAltezza(dto.altezza());
        calcolo.setEta(dto.eta());
        calcolo.setSesso(dto.sesso());
        calcolo.setLivelloAttivita(dto.livelloAttivita());
        calcolo.setTipoDieta(dto.tipoDieta());

        // --- INIZIO CORREZIONE ---
        Double bmr = calcoloBMRService.calcolaBMR(calcolo); // CORRETTO: Passa l'entità, non il DTO
        Double tdee = calcoloBMRService.calcoloTDEE(bmr, calcolo.getLivelloAttivita()); // CORRETTO: Usa il nome giusto del metodo

        calcolo.setBmrCalcolato(bmr);
        calcolo.setFabbisognoCaloricoGiornaliero(tdee);
        // --- FINE CORREZIONE ---

        return calcoloBMRRepository.save(calcolo);
    }

    // Questo metodo ora calcola e restituisce un DTO scalato
    public DietaStandardDTO generaDietaStandardPersonalizzata(CalcoloBMR profilo, TipoDieta tipoDieta) {
        // 1. Calcola il BMR e il TDEE
        Double bmr = calcoloBMRService.calcolaBMR(profilo);
        Double tdee = calcoloBMRService.calcoloTDEE(bmr, profilo.getLivelloAttivita());

        // 2. Adatta le calorie in base all'obiettivo
        Double calorieTarget = calcoloBMRService.adattaPerObiettivo(tdee, tipoDieta);

        // 3. Trova la dieta standard (template) più adatta
        DietaStandard dietaScelta = trovaDietaPiuAdatta(calorieTarget);

        // 4. Calcola le calorie *del template*
        double calorieTemplate = calcolaCalorieTotali(dietaScelta);

        // --- INIZIO CORREZIONE LOGICA ---
        // Se le calorie del template sono 0, evita divisione per zero e ritorna il DTO non scalato
        if (calorieTemplate == 0) {
            return convertToDTO(dietaScelta); // Ritorna il DTO non scalato
        }

        // 5. Calcola il fattore di scala
        double fattoreScala = calorieTarget / calorieTemplate;

        // 6. Crea e ritorna un DTO con le quantità scalate
        // Passiamo anche l'ID del template originale, che servirà per l'assegnazione
        return creaDTOscalato(dietaScelta, fattoreScala, dietaScelta.getId(), calorieTemplate);
    }




    private DietaStandard trovaDietaPiuAdatta(double calorieTarget) {
        List<DietaStandard> diete = dietaStandardRepository.findAll();
        DietaStandard miglioreDieta = null;
        double differenzaMinima = Double.MAX_VALUE;

        for (DietaStandard dieta : diete) {
            double calorieDieta = calcolaCalorieTotali(dieta);
            double differenza = Math.abs(calorieDieta - calorieTarget);

            if (differenza < differenzaMinima) {
                differenzaMinima = differenza;
                miglioreDieta = dieta;
            }
        }
        return miglioreDieta;
    }

    /*private double calcolaCalorieTotali(DietaStandard dieta) {
        return dieta.getPasti().stream()
                .flatMap(pasto -> pasto.getAlimenti().stream())
                .mapToDouble(DietaStandardAlimento::getCalorie) // Usa direttamente il campo 'calorie'
                .sum();
    }*/
    public double calcolaCalorieTotali(DietaStandard dieta) {
        if (dieta == null || dieta.getPasti() == null) {
            // Se la dieta o la lista dei pasti è nulla, ritorniamo 0 calorie
            return 0.0;
        }

        return dieta.getPasti().stream()
                // 1. Filtra i pasti che potrebbero avere una lista di alimenti nulla
                .filter(pasto -> pasto.getAlimenti() != null)
                // 2. Appiattisce la lista degli alimenti da tutti i pasti
                .flatMap(pasto -> pasto.getAlimenti().stream())
                // 3. Filtra gli elementi DietaStandardAlimento che sono nulli nella lista
                .filter(dietaAlimento -> dietaAlimento != null)
                // 4. Mappa ogni DietaStandardAlimento al suo valore calorico (QUI ERA IL BUG)
                .mapToDouble(dietaAlimento -> {
                    // Controlla se l'Alimento associato è nullo.
                    // Se è nullo, ritorna 0 per quell'alimento invece di lanciare NPE.
                    Alimento alimento = dietaAlimento.getAlimento();
                    if (alimento == null) {
                        System.err.println("WARN: Alimento associato a DietaStandardAlimento ID: " + dietaAlimento.getId() + " è nullo. Calorie ignorate.");
                        return 0.0;
                    }

                    double grammi = dietaAlimento.getGrammi();
                    // Linea 272 era probabilmente qui
                    return (alimento.getCaloriePer100g() / 100.0) * grammi;
                })
                .sum();
    }


    public DietaStandard findById(long dietaId) {
        return dietaStandardRepository.findById(dietaId)
                .orElseThrow(() -> new NotFoundException("Dieta con ID " + dietaId + " non trovata."));
    }

    @Transactional // Aggiungi @Transactional se non c'è già
    public DietaStandardDTO assegnaDietaAdUtente(Utente utente, TipoDieta tipoDieta) {
        CalcoloBMR bmrDellUtente = calcoloBMRService.getCalcoloBMRByUtente(utente.getId());

        // 1. Questo ora ritorna un DTO *scalato*
        DietaStandardDTO dietaSuggeritaDTO = generaDietaStandardPersonalizzata(bmrDellUtente, tipoDieta);

        // 2. Troviamo il *template* originale usando l'ID dal DTO
        DietaStandard dietaDaAssegnare = findById(dietaSuggeritaDTO.id());

        // 3. Disattiva le altre diete attive (corretto)
        dietaUtenteRepository.findByUtenteIdAndAttivaTrue(utente.getId()).ifPresent(dietaVecchia -> {
            dietaVecchia.setAttiva(false);
            dietaUtenteRepository.save(dietaVecchia);
        });

        // 4. Crea la nuova assegnazione usando il costruttore aggiornato
        DietaUtente nuovaDietaAssegnata = new DietaUtente(utente, dietaDaAssegnare, tipoDieta); // Passa il tipoDieta
        dietaUtenteRepository.save(nuovaDietaAssegnata);

        // 5. Ritorna il DTO scalato per la visualizzazione immediata
        return dietaSuggeritaDTO;
    }

    //  Ritorna un elenco (DTO) di tutte le diete assegnate a un utente.

    public List<DietaUtenteDTO> getDieteAssegnate(Utente utente) {
        return dietaUtenteRepository.findByUtenteId(utente.getId()).stream()
                .map(this::convertDietaUtenteToDTO)
                .collect(Collectors.toList());
    }

    //  Ritorna un una sola dieta assegbata ad un utente.

    public DietaStandardDTO getDietaAssegnataById(Long dietaUtenteId, Utente utente) {
        // 1. Trova l'assegnazione (DietaUtente) e verifica che appartenga all'utente
        DietaUtente dietaAssegnata = dietaUtenteRepository.findByIdAndUtenteId(dietaUtenteId, utente.getId())
                .orElseThrow(() -> new NotFoundException("Assegnazione dieta non trovata o non appartenente all'utente."));

        // 2. Prendi il template DietaStandard associato
        DietaStandard dietaStandardTemplate = dietaAssegnata.getDietaStandard();

        // 3. Ritorna il DTO del template completo (non scalato)
        return convertToDTO(dietaStandardTemplate);
    }
    // Ottieni le diete assegnate all'utente filtrate per TipoDieta
    public List<DietaUtenteDTO> getDieteAssegnateByTipo(Long utenteId, TipoDieta tipoDieta) {
        return dietaUtenteRepository.findByUtenteIdAndTipoDietaObiettivo(utenteId, tipoDieta).stream()
                .map(this::convertDietaUtenteToDTO)
                .collect(Collectors.toList());
    }
    // Ottieni una dieta per ID (standard o assegnata)
    public Object getDietaById(Long dietaId, Utente utente) {
        // Prima prova a cercare tra le diete standard
        Optional<DietaStandard> dietaStandard = dietaStandardRepository.findById(dietaId);
        if (dietaStandard.isPresent()) {
            return convertToDTO(dietaStandard.get());

        }

        // Altrimenti cerca tra le diete assegnate
        Optional<DietaUtente> dietaUtente = dietaUtenteRepository.findById(dietaId);
        if (dietaUtente.isPresent()) {
            DietaUtente dieta = dietaUtente.get();
            // Verifica che l'utente sia autorizzato a vedere questa dieta
            if (utente.getTipoUtente() != TipoUtente.ADMIN &&
                    !dieta.getUtente().getId().equals(utente.getId())) {
                throw new UnauthorizedException("Non sei autorizzato a visualizzare questa dieta.");
            }
            return convertDietaUtenteToDTO(dieta);
        }

        throw new NotFoundException("Dieta non trovata con id: " + dietaId);
    }

    // Ottiene una singola dieta assegnata (per ID) e la ritorna SCALATA.

    public DietaStandardDTO getDietaAssegnataScalata(Long dietaUtenteId, Utente utente) {
        // 1. Trova l'assegnazione e verifica che appartenga all'utente
        DietaUtente dietaAssegnata = dietaUtenteRepository.findByIdAndUtenteId(dietaUtenteId, utente.getId())
                .orElseThrow(() -> new NotFoundException("Assegnazione dieta non trovata o non appartenente all'utente."));

        // 2. Prendi il profilo BMR dell'utente
        CalcoloBMR profilo = calcoloBMRService.getCalcoloBMRByUtente(utente.getId());

        // 3. Ritorna la dieta scalata usando la nostra nuova logica
        return scalaDietaAssegnata(dietaAssegnata, profilo);
    }

    // Imposta una dieta assegnata come "attiva" e disattiva le altre.

    @Transactional
    public DietaUtenteDTO setDietaAttiva(Long dietaUtenteId, Utente utente) {
        // 1. Trova l'assegnazione
        DietaUtente dietaDaAttivare = dietaUtenteRepository.findByIdAndUtenteId(dietaUtenteId, utente.getId())
                .orElseThrow(() -> new NotFoundException("Assegnazione dieta non trovata."));

        // 2. Disattiva quella attualmente attiva (se diversa)
        dietaUtenteRepository.findByUtenteIdAndAttivaTrue(utente.getId()).ifPresent(dietaVecchia -> {
            if (!dietaVecchia.getId().equals(dietaDaAttivare.getId())) {
                dietaVecchia.setAttiva(false);
                dietaUtenteRepository.save(dietaVecchia);
            }
        });

        // 3. Attiva la nuova dieta e salva
        dietaDaAttivare.setAttiva(true);
        DietaUtente salvata = dietaUtenteRepository.save(dietaDaAttivare);

        return convertDietaUtenteToDTO(salvata);
    }

    @Transactional
    public DietaUtenteDTO modificaDietaUtente(Long dietaUtenteId, Utente utente, DietaStandardRequestDTO dietaRequest) {
        // 1. Trova l'assegnazione DietaUtente per l'utente loggato
        DietaUtente dietaUtente = dietaUtenteRepository.findByIdAndUtenteId(dietaUtenteId, utente.getId())
                .orElseThrow(() -> new NotFoundException("Assegnazione dieta con id " + dietaUtenteId + " non trovata per l'utente."));

        // 2. Ottieni l'ID della DietaStandard associata (il template custom)
        Long dietaStandardId = dietaUtente.getDietaStandard().getId();

        // 3. Aggiorna il template della DietaStandard associata
        aggiornaDietaStandard(dietaStandardId, dietaRequest);

        // 4. Restituisce il DTO dell'assegnazione aggiornata.
        return convertDietaUtenteToDTO(dietaUtente);
    }
    // Elimina un'assegnazione di dieta.

    public void eliminaDietaAssegnata(Long dietaUtenteId, Utente utente) {
        DietaUtente dietaDaEliminare = dietaUtenteRepository.findByIdAndUtenteId(dietaUtenteId, utente.getId())
                .orElseThrow(() -> new NotFoundException("Assegnazione dieta non trovata."));

        // Se è attiva, potresti voler gestire la logica (es. impedire l'eliminazione o attivare un'altra)
        // Per ora, eliminiamo e basta.
        dietaUtenteRepository.delete(dietaDaEliminare);
    }


    // ----------- METODI DI CONVERSIONE DTO -----------

    // --- NUOVI METODI PRIVATI PER SCALARE IL DTO ---

    //Crea un DietaStandardDTO con tutti i valori (grammi, macro, calorie) scalati.

    private DietaStandardDTO creaDTOscalato(DietaStandard dietaTemplate, double fattoreScala, Long templateId, double calorieTemplate) {
        // Genera un nome che rifletta la personalizzazione
        String nomePersonalizzato = dietaTemplate.getNome() + " (Personalizzata " + (int)(calorieTemplate * fattoreScala) + " kcal)";

        return new DietaStandardDTO(
                templateId, // Usiamo l'ID del *template* originale
                nomePersonalizzato,
                dietaTemplate.getDescrizione(),
                dietaTemplate.getTipoDieta(),
                dietaTemplate.getDurataSettimane(),
                dietaTemplate.getPasti().stream()
                        .map(pasto -> convertPastoToDTOscalato(pasto, fattoreScala)) // Usa il metodo scalato
                        .collect(Collectors.toList())
        );
    }

    //Metodo helper per scalare un singolo PastoStandard.

    private PastoStandardDTO convertPastoToDTOscalato(PastoStandard pasto, double fattoreScala) {
        return new PastoStandardDTO(
                pasto.getNomePasto(),
                pasto.getOrdine(),
                pasto.getGiornoSettimana(),
                pasto.getAlimenti().stream()
                        .map(dsa -> convertAlimentoToDTOscalato(dsa, fattoreScala)) // Usa il metodo scalato
                        .collect(Collectors.toList())
        );
    }

    // Metodo helper per scalare un singolo AlimentoPasto.

    private AlimentoPastoDTO convertAlimentoToDTOscalato(DietaStandardAlimento dsa, double fattoreScala) {
        Alimento alimento = dsa.getAlimento();
        double grammiOriginali = dsa.getGrammi();

        // 1. Scala i grammi
        double grammiScalati = grammiOriginali * fattoreScala;

        // 2. Arrotonda i grammi per un output realistico (es. 122.34g -> 122g)
        double grammiArrotondati = Math.round(grammiScalati);

        // 3. Usa la stessa logica di 'convertAlimentoToDTO' ma con i grammi arrotondati
        double fattorePer100g = grammiArrotondati / 100.0;

        return new AlimentoPastoDTO(
                alimento.getNome(),
                grammiArrotondati, // I grammi scalati e arrotondati
                (alimento.getProteinePer100g()) * fattorePer100g,
                (alimento.getCarboidratiPer100g()) * fattorePer100g,
                (alimento.getGrassiPer100g()) * fattorePer100g,
                (alimento.getCaloriePer100g()) * fattorePer100g
        );
    }

    // Helper per ricaricare e scalare una DietaUtente già salvata.

    private DietaStandardDTO scalaDietaAssegnata(DietaUtente dietaAssegnata, CalcoloBMR profilo) {
        DietaStandard dietaTemplate = dietaAssegnata.getDietaStandard();
        TipoDieta obiettivo = dietaAssegnata.getTipoDietaObiettivo();

        // Calcola BMR, TDEE e Calorie Target
        Double bmr = calcoloBMRService.calcolaBMR(profilo);
        Double tdee = calcoloBMRService.calcoloTDEE(bmr, profilo.getLivelloAttivita());
        Double calorieTarget = calcoloBMRService.adattaPerObiettivo(tdee, obiettivo);

        // Calcola calorie template e fattore di scala
        double calorieTemplate = calcolaCalorieTotali(dietaTemplate);
        if (calorieTemplate == 0) {
            return convertToDTO(dietaTemplate); // Evita divisione per zero
        }
        double fattoreScala = calorieTarget / calorieTemplate;

        // Ritorna il DTO scalato (usando l'ID dell'assegnazione, non del template)
        return creaDTOscalato(dietaTemplate, fattoreScala, dietaAssegnata.getId() ,calorieTemplate);
    }

    // Helper per convertire DietaUtente in DietaUtenteDTO (per la lista).

    private DietaUtenteDTO convertDietaUtenteToDTO(DietaUtente dietaUtente) {
        // 1. Recupera la DietaStandard associata
        DietaStandard dietaStandard = dietaUtente.getDietaStandard();

        // 2. Converte i PastoStandard in PastoStandardDTO
        List<PastoStandardDTO> pastiDTO = dietaStandard.getPasti().stream()
                .map(this::convertPastoToDTO)
                .collect(Collectors.toList());

        return new DietaUtenteDTO(
                dietaUtente.getId(),
                dietaStandard.getNome(),
                dietaUtente.getTipoDietaObiettivo(),
                dietaUtente.getDataAssegnazione(),
                dietaUtente.isAttiva(),
                pastiDTO
        );
    }
    public DietaStandardDTO convertToDTO(DietaStandard dieta) {
        return new DietaStandardDTO(
                dieta.getId(),
                dieta.getNome(),
                dieta.getDescrizione(),
                dieta.getTipoDieta(),
                dieta.getDurataSettimane(),
                dieta.getPasti().stream().map(this::convertPastoToDTO).collect(Collectors.toList())
        );
    }

    public PastoStandardDTO convertPastoToDTO(PastoStandard pasto) {
        return new PastoStandardDTO(
                pasto.getNomePasto(),
                pasto.getOrdine(),
                pasto.getGiornoSettimana(),
                pasto.getAlimenti().stream().map(this::convertAlimentoToDTO).collect(Collectors.toList())
        );
    }

    public  AlimentoPastoDTO convertAlimentoToDTO(DietaStandardAlimento dietaStandardAlimento) {
        Alimento alimento = dietaStandardAlimento.getAlimento();
        double grammi = dietaStandardAlimento.getGrammi();
        return new AlimentoPastoDTO(
                alimento.getNome(),
                grammi,
                (alimento.getProteinePer100g() / 100.0) * grammi,
                (alimento.getCarboidratiPer100g() / 100.0) * grammi,
                (alimento.getGrassiPer100g() / 100.0) * grammi,
                (alimento.getCaloriePer100g() / 100.0) * grammi
        );
    }

    public Page<DietaStandard> getDiete(int page, int size, String orderBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(orderBy));
        return dietaStandardRepository.findAll(pageable);
    }
}
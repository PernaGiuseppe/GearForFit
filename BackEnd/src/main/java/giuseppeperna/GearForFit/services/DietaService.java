package giuseppeperna.GearForFit.services;

import giuseppeperna.GearForFit.entities.Alimenti.Alimento;
import giuseppeperna.GearForFit.entities.Diete.*;
import giuseppeperna.GearForFit.entities.Utente.Utente;
import giuseppeperna.GearForFit.exceptions.NotFoundException;
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

    public DietaStandardDTO getDietaStandardByTipo(TipoDieta tipoDieta) {
        // 1. findByTipoDieta ora restituisce una List<DietaStandard>
        List<DietaStandard> dieteTrovate = dietaStandardRepository.findByTipoDieta(tipoDieta);

        // 2. Controlla se la lista è vuota
        if (dieteTrovate.isEmpty()) {
            throw new RuntimeException("Nessuna dieta standard trovata per il tipo: " + tipoDieta);
        }

        // 3. Prendi il primo elemento della lista e convertilo in DTO
        // (Qui decidiamo di prendere la prima dieta trovata. Potremmo avere logiche più complesse in futuro)
        DietaStandard dietaDaRestituire = dieteTrovate.get(0);

        return convertToDTO(dietaDaRestituire);
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

    public void eliminaDietaStandard(Long id) {
        DietaStandard dieta = dietaStandardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dieta con id " + id + " non trovata"));

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


    // --- NUOVI METODI PRIVATI PER SCALARE IL DTO ---

    //Crea un DietaStandardDTO con tutti i valori (grammi, macro, calorie) scalati.

    private DietaStandardDTO creaDTOscalato(DietaStandard dietaTemplate, double fattoreScala, Long templateId, double calorieTemplate) {
        // Genera un nome che rifletta la personalizzazione
        String nomePersonalizzato = dietaTemplate.getNome() + " (Personalizzata " + (int)(calorieTemplate * fattoreScala) + " kcal)";

        return new DietaStandardDTO(
                templateId, // Usiamo l'ID del *template* originale
                nomePersonalizzato,
                dietaTemplate.getDescrizione(),
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

    private double calcolaCalorieTotali(DietaStandard dieta) {
        return dieta.getPasti().stream()
                .flatMap(pasto -> pasto.getAlimenti().stream())
                .mapToDouble(DietaStandardAlimento::getCalorie) // Usa direttamente il campo 'calorie'
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

   // Elimina un'assegnazione di dieta.

    public void eliminaDietaAssegnata(Long dietaUtenteId, Utente utente) {
        DietaUtente dietaDaEliminare = dietaUtenteRepository.findByIdAndUtenteId(dietaUtenteId, utente.getId())
                .orElseThrow(() -> new NotFoundException("Assegnazione dieta non trovata."));

        // Se è attiva, potresti voler gestire la logica (es. impedire l'eliminazione o attivare un'altra)
        // Per ora, eliminiamo e basta.
        dietaUtenteRepository.delete(dietaDaEliminare);
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
        return new DietaUtenteDTO(
                dietaUtente.getId(),
                dietaUtente.getDietaStandard().getNome(),
                dietaUtente.getTipoDietaObiettivo(),
                dietaUtente.getDataAssegnazione(),
                dietaUtente.isAttiva()
        );
    }
    // ----------- METODI DI CONVERSIONE DTO -----------

    public DietaStandardDTO convertToDTO(DietaStandard dieta) {
        return new DietaStandardDTO(
                dieta.getId(),
                dieta.getNome(),
                dieta.getDescrizione(),
                dieta.getDurataSettimane(),
                dieta.getPasti().stream().map(this::convertPastoToDTO).collect(Collectors.toList())
        );
    }

    public PastoStandardDTO convertPastoToDTO(PastoStandard pasto) {
        return new PastoStandardDTO(
                pasto.getNomePasto(),
                pasto.getOrdine(),
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

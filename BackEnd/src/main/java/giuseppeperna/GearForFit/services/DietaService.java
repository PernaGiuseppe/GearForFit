package giuseppeperna.GearForFit.services;

import giuseppeperna.GearForFit.entities.Alimenti.Alimento;
import giuseppeperna.GearForFit.entities.Diete.*;
import giuseppeperna.GearForFit.entities.Utente.Utente;
import giuseppeperna.GearForFit.exceptions.NotFoundException;
import giuseppeperna.GearForFit.payloads.*;
import giuseppeperna.GearForFit.repositories.*;
import org.hibernate.query.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.print.Pageable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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
        return dietaStandardRepository.findByTipoDieta(tipoDieta)
                .map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("Dieta non trovata per il tipo: " + tipoDieta));
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

    public DietaStandardDTO generaDietaStandardPersonalizzata(CalcoloBMR profilo, TipoDieta tipoDieta) {
        // 1. Calcola il BMR e il TDEE (fabbisogno calorico giornaliero) usando i metodi corretti
        Double bmr = calcoloBMRService.calcolaBMR(profilo);
        Double tdee = calcoloBMRService.calcoloTDEE(bmr, profilo.getLivelloAttivita());

        // 2. Adatta le calorie in base all'obiettivo (IPOCALORICA, IPERCALORICA, ecc.)
        Double calorieTarget = calcoloBMRService.adattaPerObiettivo(tdee, tipoDieta);

        // 3. Trova la dieta standard più adatta in base alle calorie target
        DietaStandard dietaScelta = trovaDietaPiuAdatta(calorieTarget);

        // 4. Converte l'entità della dieta scelta in un DTO per la risposta
        return convertToDTO(dietaScelta);
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

    public DietaStandardDTO assegnaDietaAdUtente(Utente utente, TipoDieta tipoDieta) {
        CalcoloBMR bmrDellUtente = calcoloBMRService.getCalcoloBMRByUtente(utente.getId());
        DietaStandardDTO dietaSuggeritaDTO = generaDietaStandardPersonalizzata(bmrDellUtente, tipoDieta);
        DietaStandard dietaDaAssegnare = findById(dietaSuggeritaDTO.id());
        dietaUtenteRepository.findByUtenteIdAndAttivaTrue(utente.getId()).ifPresent(dietaVecchia -> {
            dietaVecchia.setAttiva(false);
            dietaUtenteRepository.save(dietaVecchia);
        });
        DietaUtente nuovaDietaAssegnata = new DietaUtente(utente, dietaDaAssegnare);
        dietaUtenteRepository.save(nuovaDietaAssegnata);
        return dietaSuggeritaDTO;
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

    public DietaStandard update(long dietaId, DietaStandardRequestDTO body) {
        DietaStandard trovata = this.findById(dietaId);

        // Rimuovi le vecchie dipendenze per evitare errori
        for (PastoStandard pasto : trovata.getPasti()) {
            dietaStandardAlimentoRepository.deleteAll(pasto.getAlimenti());
        }
        pastoStandardRepository.deleteAll(trovata.getPasti());

        // Aggiorna i campi principali
        trovata.setNome(body.nome());
        trovata.setDescrizione(body.descrizione());
        trovata.setDurataSettimane(body.durataSettimane());
        trovata.setTipoDieta(body.tipoDieta());

        // **Usa la stessa logica del tuo metodo 'save' per ricostruire la dieta**
        List<PastoStandard> nuoviPasti = new ArrayList<>();
        double calorieTotaliNuove = 0;

        for (PastoStandardRequestDTO pastoDTO : body.pasti()) {
            PastoStandard pasto = new PastoStandard();
            pasto.setNomePasto(pastoDTO.nomePasto());
            pasto.setOrdine(pastoDTO.ordine());
            pasto.setDietaStandard(trovata); // Collega alla dieta esistente
            pastoStandardRepository.save(pasto);

            double caloriePasto = 0;
            List<DietaStandardAlimento> alimentiSalvati = new ArrayList<>();
            for (var alimentoPastoDTO : pastoDTO.alimenti()) {
                Alimento alimento = alimentoRepository.findById(alimentoPastoDTO.alimentoId())
                        .orElseThrow(() -> new NotFoundException("Alimento con id " + alimentoPastoDTO.alimentoId() + " non trovato"));
                DietaStandardAlimento dietaAlimento = new DietaStandardAlimento();
                dietaAlimento.setAlimento(alimento);
                dietaAlimento.setGrammi(alimentoPastoDTO.grammi());
                dietaAlimento.setPastoStandard(pasto);
                dietaStandardAlimentoRepository.save(dietaAlimento);
                alimentiSalvati.add(dietaAlimento);
                caloriePasto += (alimento.getCaloriePer100g() / 100.0) * alimentoPastoDTO.grammi();
            }
            pasto.setAlimenti(alimentiSalvati);
            calorieTotaliNuove += caloriePasto;
            nuoviPasti.add(pasto);
        }

        trovata.setCalorieTotali(calorieTotaliNuove); // Ora funziona perché siamo dentro l'entità
        trovata.setPasti(nuoviPasti);

        return dietaStandardRepository.save(trovata);
    }

    public void findByIdAndDelete(long dietaId) {
        DietaStandard trovata = this.findById(dietaId);

        // Rimuovi le dipendenze prima di cancellare
        List<DietaUtente> dieteAssegnate = dietaUtenteRepository.findByDietaStandardId(dietaId);
        if (!dieteAssegnate.isEmpty()) {
            dietaUtenteRepository.deleteAll(dieteAssegnate);
        }

        for (PastoStandard pasto : trovata.getPasti()) {
            dietaStandardAlimentoRepository.deleteAll(pasto.getAlimenti());
        }
        pastoStandardRepository.deleteAll(trovata.getPasti());

        dietaStandardRepository.delete(trovata);
    }
    public Page<DietaStandard> getDiete(int page, int size, String orderBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(orderBy));
        return dietaStandardRepository.findAll(pageable);
    }
}

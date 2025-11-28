package giuseppeperna.GearForFit.services;

import giuseppeperna.GearForFit.entities.Alimenti.Alimento;
import giuseppeperna.GearForFit.entities.Diete.*;
import giuseppeperna.GearForFit.entities.Utente.Utente;
import giuseppeperna.GearForFit.exceptions.NotFoundException;
import giuseppeperna.GearForFit.payloads.*;
import giuseppeperna.GearForFit.repositories.AlimentoRepository;
import giuseppeperna.GearForFit.repositories.DietaRepository;
import giuseppeperna.GearForFit.repositories.DietaStandardAlimentoRepository;
import giuseppeperna.GearForFit.repositories.PastoStandardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DietaService {

    @Autowired
    private DietaRepository dietaRepository;
    @Autowired
    private PastoStandardRepository pastoStandardRepository;
    @Autowired
    private DietaStandardAlimentoRepository dietaStandardAlimentoRepository;
    @Autowired
    private AlimentoRepository alimentoRepository;
    @Autowired
    private CalcoloBMRService calcoloBMRService;

    // ===== ADMIN: CRUD DIETE STANDARD =====

    // Admin crea una nuova dieta standard

    public DietaDTO creaDietaStandard(DietaRequestDTO request) {
        Dieta dieta = new Dieta();
        dieta.setNome(request.nome());
        dieta.setDescrizione(request.descrizione());
        dieta.setDurataSettimane(request.durataSettimane());
        dieta.setTipoDieta(request.tipoDieta());
        dieta.setIsStandard(true);
        dieta.setIsAttiva(false);
        dieta.setUtente(null);

        // Salva dieta prima di aggiungere pasti
        Dieta dietaSalvata = dietaRepository.save(dieta);

        // Aggiungi pasti con alimenti e calcola calorie totali
        double caloTotali = 0.0;
        for (PastoStandardRequestDTO pastoReq : request.pasti()) {  // ← CORRETTO
            PastoStandard pasto = new PastoStandard();
            pasto.setNomePasto(pastoReq.nomePasto());
            pasto.setOrdine(pastoReq.ordine());
            pasto.setGiornoSettimana(pastoReq.giornoSettimana());
            pasto.setDieta(dietaSalvata);

            PastoStandard pastoSalvato = pastoStandardRepository.save(pasto);

            // Aggiungi alimenti al pasto
            for (AlimentoPastoRequestDTO alimentoReq : pastoReq.alimenti()) {
                Alimento alimento = alimentoRepository.findById(alimentoReq.alimentoId())
                        .orElseThrow(() -> new NotFoundException(alimentoReq.alimentoId()));

                DietaStandardAlimento dsa = new DietaStandardAlimento();
                dsa.setPastoStandard(pastoSalvato);
                dsa.setAlimento(alimento);
                dsa.setGrammi(alimentoReq.grammi());

                // Calcola macro per i grammi specificati
                double fattore = alimentoReq.grammi() / 100.0;
                dsa.setProteineG(alimento.getProteinePer100g() * fattore);
                dsa.setCarboidratiG(alimento.getCarboidratiPer100g() * fattore);
                dsa.setGrassiG(alimento.getGrassiPer100g() * fattore);
                dsa.setCalorie(alimento.getCaloriePer100g() * fattore);

                dietaStandardAlimentoRepository.save(dsa);
                caloTotali += dsa.getCalorie();
            }
        }

        dietaSalvata.setCalorieTotali(caloTotali);
        dietaRepository.save(dietaSalvata);

        return convertToDTO(dietaSalvata);
    }

    // Admin modifica una dieta standard

    public DietaDTO modificaDietaStandard(Long id, DietaRequestDTO request) {
        Dieta dieta = dietaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id));

        // Solo diete standard possono essere modificate
        if (!dieta.getIsStandard()) {
            throw new IllegalStateException("Non puoi modificare una dieta custom");
        }

        dieta.setNome(request.nome());
        dieta.setDescrizione(request.descrizione());
        dieta.setDurataSettimane(request.durataSettimane());
        dieta.setTipoDieta(request.tipoDieta());

        // Elimina i vecchi pasti
        dieta.getPasti().forEach(p -> dietaStandardAlimentoRepository.deleteAll(p.getAlimenti()));
        pastoStandardRepository.deleteAll(dieta.getPasti());
        dieta.getPasti().clear();

        // Aggiungi nuovi pasti
        double caloTotali = 0.0;
        for (PastoStandardRequestDTO pastoReq : request.pasti()) {  // ← CORRETTO
            PastoStandard pasto = new PastoStandard();
            pasto.setNomePasto(pastoReq.nomePasto());
            pasto.setOrdine(pastoReq.ordine());
            pasto.setGiornoSettimana(pastoReq.giornoSettimana());
            pasto.setDieta(dieta);

            PastoStandard pastoSalvato = pastoStandardRepository.save(pasto);

            for (AlimentoPastoRequestDTO alimentoReq : pastoReq.alimenti()) {
                Alimento alimento = alimentoRepository.findById(alimentoReq.alimentoId())
                        .orElseThrow(() -> new NotFoundException(alimentoReq.alimentoId()));

                DietaStandardAlimento dsa = new DietaStandardAlimento();
                dsa.setPastoStandard(pastoSalvato);
                dsa.setAlimento(alimento);
                dsa.setGrammi(alimentoReq.grammi());

                double fattore = alimentoReq.grammi() / 100.0;
                dsa.setProteineG(alimento.getProteinePer100g() * fattore);
                dsa.setCarboidratiG(alimento.getCarboidratiPer100g() * fattore);
                dsa.setGrassiG(alimento.getGrassiPer100g() * fattore);
                dsa.setCalorie(alimento.getCaloriePer100g() * fattore);

                dietaStandardAlimentoRepository.save(dsa);
                caloTotali += dsa.getCalorie();
            }
        }

        dieta.setCalorieTotali(caloTotali);
        return convertToDTO(dietaRepository.save(dieta));
    }

    // Admin visualizza qualsiasi dieta (standard o custom) per ID

    @Transactional(readOnly = true)
    public DietaDTO adminGetDietaById(Long dietaId) {
        Dieta dieta = dietaRepository.findById(dietaId)
                .orElseThrow(() -> new NotFoundException(dietaId));

        return convertToDTO(dieta);
    }
    // Admin elimina una dieta (standard o custom)

    public void eliminaDieta(Long id) {
        Dieta dieta = dietaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id));
        dietaRepository.delete(dieta);
    }

    // Visualizza tutte le diete standard (template)

    @Transactional(readOnly = true)
    public List<DietaDTO> getDieteStandard() {
        return dietaRepository.findAllByIsStandardTrue()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Visualizza una dieta standard per ID

    @Transactional(readOnly = true)
    public DietaDTO getDietaStandardById(Long id) {
        Dieta dieta = dietaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id));

        if (!dieta.getIsStandard()) {
            throw new IllegalStateException("Questo endpoint è solo per diete standard");
        }

        return convertToDTO(dieta);
    }
    // Admin: Visualizza tutte le diete custom di uno specifico utente

    @Transactional(readOnly = true)
    public List<DietaDTO> getDieteCustomByUtente(Long utenteId) {
        List<Dieta> diete = dietaRepository.findByUtenteIdAndIsStandardFalse(utenteId);

        if (diete.isEmpty()) {
            throw new NotFoundException("Nessuna dieta custom trovata per l'utente con ID: " + utenteId);
        }

        return diete.stream()
                .map(this::convertToDTO)
                .toList();
    }
    // Admin: Visualizza TUTTE le diete (standard + custom di tutti gli utenti)

    @Transactional(readOnly = true)
    public List<DietaDTO> getAllDiete() {
        List<Dieta> diete = dietaRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));

        if (diete.isEmpty()) {
            throw new NotFoundException("Nessuna dieta trovata nel sistema");
        }

        return diete.stream()
                .map(this::convertToDTO)
                .toList();
    }

    // Ottieni TUTTE le diete custom di TUTTI gli utenti (per ADMIN)
    @Transactional(readOnly = true)
    public List<DietaDTO> getAllDieteCustom() {
        List<Dieta> diete = dietaRepository.findAllByIsStandardFalse();

        // Non lanciare eccezione se vuota - è normale per l'admin
        return diete.stream()
                .map(this::convertToDTO)
                .toList();
    }

    // Ottieni TUTTE le diete standard (per ADMIN)
    @Transactional(readOnly = true)
    public List<DietaDTO> getDieteStandardAdmin() {
        List<Dieta> diete = dietaRepository.findAllByIsStandardTrue();

        return diete.stream()
                .map(this::convertToDTO)
                .toList();
    }

    // ===== UTENTE: GESTIONE DIETE CUSTOM =====

    public DietaDTO creaDialogDietaCustom(Long dietaStandardId, DietaCreateRequestDTO request, Utente utente) {
        // Recupera il template standard
        Dieta template = dietaRepository.findById(dietaStandardId)
                .orElseThrow(() -> new NotFoundException(dietaStandardId));

        if (!template.getIsStandard()) {
            throw new IllegalStateException("Puoi creare una custom solo da una dieta standard");
        }
        // Disattiva la vecchia dieta custom attiva (se esiste)
        dietaRepository.findByUtenteIdAndIsAttivaTrueAndIsStandardFalse(utente.getId())
                .ifPresent(dietaVecchia -> {
                    dietaVecchia.setIsAttiva(false);
                    dietaRepository.save(dietaVecchia);
                });

        // Crea un oggetto CalcoloBMR con i dati dal body
        CalcoloBMR calcoloBMR = CalcoloBMR.builder()
                .utente(utente)
                .peso(request.peso())
                .altezza(request.altezza())
                .eta(request.eta())
                .sesso(request.sesso())
                .livelloAttivita(request.livelloAttivita())
                .tipoDieta(request.tipoDieta())
                .build();

        // Calcola BMR con i dati dell'utente
        Double bmr = calcoloBMRService.calcolaBMR(calcoloBMR);

        // Il secondo è LivelloAttivita (che è dentro CalcoloBMR)
        Double tdee = calcoloBMRService.calcoloTDEE(bmr, calcoloBMR.getLivelloAttivita());

        // Usa TDEE come calorie target (adattate per l'obiettivo)
        Double calorieTarget = adattaCaloriePerObiettivo(tdee, request.tipoDieta());

        // Calcola il fattore di scalatura
        double fattoreScalatura = calorieTarget / template.getCalorieTotali();

        // Crea la nuova dieta custom
        Dieta dietaCustom = new Dieta();
        dietaCustom.setNome(request.nome());
        dietaCustom.setDescrizione(request.descrizione());
        dietaCustom.setDurataSettimane(template.getDurataSettimane());
        dietaCustom.setTipoDieta(request.tipoDieta());
        dietaCustom.setIsStandard(false);
        dietaCustom.setIsAttiva(true);
        dietaCustom.setUtente(utente);

        Dieta dietaCustomSalvata = dietaRepository.save(dietaCustom);

        // Copia pasti dal template, scalando le calorie
        double caloTotali = 0.0;
        for (PastoStandard pastoTemplate : template.getPasti()) {
            PastoStandard pastoCustom = new PastoStandard();  // ← CORRETTO
            pastoCustom.setNomePasto(pastoTemplate.getNomePasto());
            pastoCustom.setOrdine(pastoTemplate.getOrdine());
            pastoCustom.setGiornoSettimana(pastoTemplate.getGiornoSettimana());
            pastoCustom.setDieta(dietaCustomSalvata);

            PastoStandard pastoCustomSalvato = pastoStandardRepository.save(pastoCustom);

            // Copia alimenti dal template, scalando calorie e macro
            for (DietaStandardAlimento alimentoTemplate : pastoTemplate.getAlimenti()) {
                DietaStandardAlimento dsaCustom = new DietaStandardAlimento();
                dsaCustom.setPastoStandard(pastoCustomSalvato);
                dsaCustom.setAlimento(alimentoTemplate.getAlimento());
                dsaCustom.setGrammi(alimentoTemplate.getGrammi());

                // Scala le calorie e i macro
                dsaCustom.setProteineG(alimentoTemplate.getProteineG() * fattoreScalatura);
                dsaCustom.setCarboidratiG(alimentoTemplate.getCarboidratiG() * fattoreScalatura);
                dsaCustom.setGrassiG(alimentoTemplate.getGrassiG() * fattoreScalatura);
                dsaCustom.setCalorie(alimentoTemplate.getCalorie() * fattoreScalatura);

                dietaStandardAlimentoRepository.save(dsaCustom);
                caloTotali += dsaCustom.getCalorie();
            }
        }

        dietaCustomSalvata.setCalorieTotali(caloTotali);
        dietaRepository.save(dietaCustomSalvata);

        return convertToDTO(dietaCustomSalvata);
    }


    /*   * Metodo helper: adatta le calorie in base all'obiettivo dietetico
     * IPOCALORICA: -15% dalle calorie target
     * NORMOCALORICA: 0% (rimane uguale)
     * IPERCALORICA: +15% alle calorie target*/

    private Double adattaCaloriePerObiettivo(Double tdee, TipoDieta tipoDieta) {
        return switch (tipoDieta) {
            case IPOCALORICA -> tdee * 0.85;      // -15%
            case NORMOCALORICA -> tdee;             // 0%
            case IPERCALORICA -> tdee * 1.15;      // +15%
        };
    }

    // Utente visualizza tutte le sue diete custom

    @Transactional(readOnly = true)
    public List<DietaDTO> getDieteCustomUtente(Utente utente) {
        return dietaRepository.findByUtenteIdAndIsStandardFalse(utente.getId())
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Utente visualizza una sua dieta custom specifica

    @Transactional(readOnly = true)
    public DietaDTO getDietaCustomUtenteById(Long id, Utente utente) {
        Dieta dieta = dietaRepository.findByIdAndUtenteId(id, utente.getId())
                .orElseThrow(() -> new NotFoundException(id));  // ← CORRETTO

        if (dieta.getIsStandard()) {
            throw new IllegalStateException("Questo endpoint è solo per diete custom");
        }

        return convertToDTO(dieta);
    }

    // Utente attiva/disattiva una dieta custom

    @Transactional
    public DietaDTO setDietaAttiva(Long id, Boolean isAttiva, Utente utente) {
        Dieta dieta = dietaRepository.findByIdAndUtenteId(id, utente.getId())
                .orElseThrow(() -> new NotFoundException(id));

        if (dieta.getIsStandard()) {
            throw new IllegalStateException("Questo endpoint è solo per diete custom");
        }

        // Se attiva, disattiva la vecchia dieta attiva
        if (isAttiva) {
            dietaRepository.findByUtenteIdAndIsAttivaTrueAndIsStandardFalse(utente.getId())
                    .ifPresent(dietaVecchia -> {
                        if (!dietaVecchia.getId().equals(dieta.getId())) {
                            dietaVecchia.setIsAttiva(false);
                            dietaRepository.save(dietaVecchia);
                        }
                    });
        }

        dieta.setIsAttiva(isAttiva);
        return convertToDTO(dietaRepository.save(dieta));
    }


    // Utente elimina una dieta custom

    public void eliminaDietaCustom(Long id, Utente utente) {
        Dieta dieta = dietaRepository.findByIdAndUtenteId(id, utente.getId())
                .orElseThrow(() -> new NotFoundException(id));  // ← CORRETTO

        if (dieta.getIsStandard()) {
            throw new IllegalStateException("Non puoi eliminare una dieta standard da qui");
        }

        dietaRepository.delete(dieta);
    }

    // ===== UTILITY =====

    // Converte entity Dieta a DTO

    private DietaDTO convertToDTO(Dieta dieta) {
        List<PastoStandardDTO> pastiDTO = dieta.getPasti().stream()
                .map(pasto -> new PastoStandardDTO(
                        pasto.getNomePasto(),
                        pasto.getOrdine(),
                        pasto.getGiornoSettimana(),
                        pasto.getAlimenti().stream()
                                .map(alimento -> new AlimentoPastoDTO(
                                        alimento.getAlimento().getNome(),
                                        alimento.getGrammi(),
                                        alimento.getProteineG(),
                                        alimento.getCarboidratiG(),
                                        alimento.getGrassiG(),
                                        alimento.getCalorie()
                                ))
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());

        return new DietaDTO(
                dieta.getId(),
                dieta.getNome(),
                dieta.getDescrizione(),
                dieta.getDurataSettimane(),
                dieta.getTipoDieta(),
                dieta.getIsStandard(),
                dieta.getUtente() != null ? dieta.getUtente().getId() : null,
                dieta.getIsAttiva(),
                pastiDTO
        );
    }
}

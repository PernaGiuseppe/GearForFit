package giuseppeperna.GearForFit.controllers;

import giuseppeperna.GearForFit.entities.Alimenti.Alimento;
import giuseppeperna.GearForFit.entities.SchedePalestra.*;
import giuseppeperna.GearForFit.entities.Utente.TipoPiano;
import giuseppeperna.GearForFit.entities.Utente.Utente;
import giuseppeperna.GearForFit.exceptions.NotValidException;
import giuseppeperna.GearForFit.payloads.*;
import giuseppeperna.GearForFit.services.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping("/admin")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminController {

    @Autowired
    private UtenteService utenteService;

    @Autowired
    private GruppoMuscolareService gruppoMuscolareService;

    @Autowired
    private AttrezzoService attrezzoService;

    @Autowired
    private EsercizioService esercizioService;

    @Autowired
    private SchedaAllenamentoService schedaAllenamentoService;

    @Autowired
    private QeAService qeAService;

    @Autowired
    private DietaService dietaService;

    @Autowired
    private AlimentoService alimentoService;


// ========== GESTIONE ALIMENTI ==========

    @GetMapping("/alimenti")
    public List<Alimento> getTuttiAlimenti() {
        return alimentoService.findAllAlimenti();
    }

    @GetMapping("/alimenti/{id}")
    public Alimento getAlimento(@PathVariable Long id) {
        return alimentoService.findAlimentoById(id)
                .orElseThrow(() -> new RuntimeException("Alimento non trovato"));
    }

    @DeleteMapping("/alimenti/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminaAlimento(@PathVariable Long id) {
        alimentoService.deleteAlimento(id);
    }

// ========== GESTIONE DIETE ==========

    //Admin crea una nuova dieta standard

    @PostMapping("/diete")
    @ResponseStatus(HttpStatus.CREATED)
    public DietaDTO creaDietaStandard(
            @RequestBody DietaRequestDTO request,
            @AuthenticationPrincipal Utente utente
    ) {
        return dietaService.creaDietaStandard(request);
    }

    //Admin modifica una dieta standard
    //NON USATO NEL FRONT END
    @PutMapping("/diete/{id}")
    public DietaDTO modificaDietaStandard(
            @PathVariable Long id,
            @RequestBody DietaRequestDTO request,
            @AuthenticationPrincipal Utente utente
    ) {
        return dietaService.modificaDietaStandard(id, request);
    }
    // Admin visualizza SOLO diete standard
    @GetMapping("/diete/standard")
    public List<DietaDTO> adminGetDieteStandard() {
        return dietaService.getDieteStandardAdmin();
    }


    // Admin visualizza SOLO diete custom (di tutti gli utenti)
    @GetMapping("/diete/custom")
    public List<DietaDTO> adminGetDieteCustom() {
        return dietaService.getAllDieteCustom();
    }
    // Admin visualizza TUTTE le diete (standard + custom di tutti gli utenti)

    @GetMapping("/diete/all")
    public List<DietaDTO> adminGetAllDiete() {
        return dietaService.getAllDiete();
    }

    // Admin visualizza qualsiasi dieta (standard o custom) per ID

    @GetMapping("/diete/{dietaId}")
    public DietaDTO adminGetDietaById(@PathVariable Long dietaId) {
        return dietaService.adminGetDietaById(dietaId);
    }
    // Admin visualizza tutte le diete custom di uno specifico utente

    @GetMapping("diete/custom/utente/{utenteId}")
    public List<DietaDTO> adminGetDieteCustomByUtente(@PathVariable Long utenteId) {
        return dietaService.getDieteCustomByUtente(utenteId);
    }
    @DeleteMapping("/diete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminaDieta(
            @PathVariable Long id,
            @AuthenticationPrincipal Utente utente
    ) {
        dietaService.eliminaDieta(id);
    }

    // ========== GESTIONE UTENTI ==========

    @GetMapping("/utenti")
    public List<Utente> getTuttiUtenti() {
        return utenteService.getAllUtenti();
    }

    @GetMapping("/utenti/{id}")
    public Utente getUtenteById(@PathVariable Long id) {
        return utenteService.findById(id);
    }

    @PutMapping("/utenti/{id}/piano")
    public Utente modificaPiano(@PathVariable Long id, @RequestParam TipoPiano nuovoPiano) {
        return utenteService.cambiaPiano(id, nuovoPiano);
    }

    @PutMapping("/utenti/reset-password")
    public Utente resetPasswordUtente(@RequestBody @Valid ResetPasswordAdminDTO body) {
        return utenteService.resetPasswordByAdmin(body.utenteId(), body.nuovaPassword());
    }

    @DeleteMapping("/utenti/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminaUtente(@PathVariable Long id) {
        utenteService.eliminaUtente(id);
    }

    // Disattiva un utente
    @PutMapping("/utenti/{id}/disattiva")
    public Utente disattivaUtente(@PathVariable Long id) {
        return utenteService.disattivaUtente(id);
    }

    // Attiva un utente
    @PutMapping("/utenti/{id}/attiva")
    public Utente attivaUtente(@PathVariable Long id) {
        return utenteService.attivaUtente(id);
    }

    // ========== GRUPPI MUSCOLARI ==========

    @PostMapping("/gruppi-muscolari")
    @ResponseStatus(HttpStatus.CREATED)
    public GruppoMuscolare creaGruppoMuscolare(@RequestBody @Valid GruppoMuscolareRequestDTO body) {
        return gruppoMuscolareService.creaGruppoMuscolare(body);
    }

    @GetMapping("/gruppi-muscolari")
    public List<GruppoMuscolare> getTuttiGruppiMuscolari() {
        return gruppoMuscolareService.getTutti();
    }

    @GetMapping("/gruppi-muscolari/{id}")
    public GruppoMuscolare getGruppoMuscolare(@PathVariable Long id) {
        return gruppoMuscolareService.findById(id);
    }

    @PutMapping("/gruppi-muscolari/{id}")
    public GruppoMuscolare aggiornaGruppoMuscolare(
            @PathVariable Long id,
            @RequestBody @Valid GruppoMuscolareRequestDTO body) {
        return gruppoMuscolareService.aggiorna(id, body);
    }

    @DeleteMapping("/gruppi-muscolari/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminaGruppoMuscolare(@PathVariable Long id) {
        gruppoMuscolareService.elimina(id);
    }

    // ========== ATTREZZI ==========

    @PostMapping("/attrezzi")
    @ResponseStatus(HttpStatus.CREATED)
    public Attrezzo creaAttrezzo(@RequestBody @Valid AttrezzoRequestDTO body) {
        return attrezzoService.creaAttrezzo(body);
    }

    @GetMapping("/attrezzi")
    public List<Attrezzo> getTuttiAttrezzi() {
        return attrezzoService.getTutti();
    }

    @GetMapping("/attrezzi/{id}")
    public Attrezzo getAttrezzo(@PathVariable Long id) {
        return attrezzoService.findById(id);
    }

    @PutMapping("/attrezzi/{id}")
    public Attrezzo aggiornaAttrezzo(
            @PathVariable Long id,
            @RequestBody @Valid AttrezzoRequestDTO body) {
        return attrezzoService.aggiorna(id, body);
    }

    @DeleteMapping("/attrezzi/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminaAttrezzo(@PathVariable Long id) {
        attrezzoService.elimina(id);
    }

    // ========== ESERCIZI ==========

    @PostMapping("/esercizi")
    @ResponseStatus(HttpStatus.CREATED)
    public Esercizio creaEsercizio(@RequestBody @Valid EsercizioRequestDTO body) {
        return esercizioService.creaEsercizio(body);
    }

    @GetMapping("/esercizi")
    public List<Esercizio> getTuttiEsercizi() {
        return esercizioService.getAllEsercizi();
    }

    @GetMapping("/esercizi/{id}")
    public Esercizio getEsercizio(@PathVariable Long id) {
        return esercizioService.getEsercizioById(id);
    }

    @PutMapping("/esercizi/{id}")
    public Esercizio aggiornaEsercizio(
            @PathVariable Long id,
            @RequestBody @Valid EsercizioRequestDTO body) {
        return esercizioService.aggiornaEsercizio(id, body);
    }

    @DeleteMapping("/esercizi/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminaEsercizio(@PathVariable Long id) {
        esercizioService.eliminaEsercizio(id);
    }

    @GetMapping("/esercizi/gruppo/{gruppoMuscolareId}")
    public List<Esercizio> getEserciziPerGruppoMuscolare(@PathVariable Long gruppoMuscolareId) {
        return esercizioService.getEserciziByGruppoMuscolare(gruppoMuscolareId);
    }

    @GetMapping("/esercizi/attrezzo/{attrezzoId}")
    public List<Esercizio> getEserciziPerAttrezzo(@PathVariable Long attrezzoId) {
        return esercizioService.getEserciziByAttrezzo(attrezzoId);
    }

    @GetMapping("/esercizi/composti")
    public List<Esercizio> getEserciziComposti() {
        return esercizioService.getEserciziComposti();
    }

    @GetMapping("/esercizi/cerca")
    public List<Esercizio> cercaEsercizi(@RequestParam String nome) {
        return esercizioService.cercaEserciziPerNome(nome);
    }

    @PatchMapping("/esercizi/{id}/upload")
    public Esercizio uploadImmagineEsercizio(
            @PathVariable Long id,
            @RequestParam("immagine") MultipartFile file) throws IOException {
        return esercizioService.uploadImmagine(id, file);
    }

    // ========== SCHEDE ALLENAMENTO (STANDARD - ADMIN) ==========

    // Admin visualizza SOLO schede standard
    @GetMapping("/schede/standard")
    public List<SchedaAllenamentoDTO> adminGetSchedeStandard() {
        return schedaAllenamentoService.getSchedeStandard();
    }
    // Admin visualizza SOLO schede custom (di tutti gli utenti)
    @GetMapping("/schede/custom")
    public List<SchedaAllenamentoDTO> adminGetSchedeCustom() {
        return schedaAllenamentoService.getAllSchedeCustom();
    }

    // GET - Ottieni lista di tutte le schede di allenamento (standard e custom, solo admin)
    @GetMapping("/schede")
    public List<SchedaAllenamentoDTO> getAllSchedeAllenamento() {
        return schedaAllenamentoService.getAllSchedeAsList();
    }
    // GET - Ottieni una scheda di allenamento by id (standard e custom, solo admin)
    @GetMapping("/schede/{id}")
    public SchedaAllenamentoDTO getSchedaByIdConAutorizzazione(
            @PathVariable Long id,
            @AuthenticationPrincipal Utente utente) {
        return schedaAllenamentoService.getSchedaByIdAndAuthorize(id, utente);
    }

    @PostMapping("/schede/standard")
    @ResponseStatus(HttpStatus.CREATED)
    public SchedaAllenamentoDTO creaSchedaStandard(
            @RequestBody @Validated SchedaPersonalizzataRequestDTO body,
            BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            List<String> errorMessages = validationResult.getFieldErrors().stream()
                    .map(fieldError -> fieldError.getField() + " :" + fieldError.getDefaultMessage())
                    .toList();
            throw new NotValidException(errorMessages);
        }
        return schedaAllenamentoService.creaSchedaStandard(body);
    }
    //NON USATO NEL FRONT END
    @PutMapping("/schede/standard/{id}")
    public SchedaAllenamentoDTO aggiornaSchedaStandard(
            @PathVariable Long id,
            @RequestBody @Validated SchedaPersonalizzataRequestDTO body,
            BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            List<String> errorMessages = validationResult.getFieldErrors().stream()
                    .map(fieldError -> fieldError.getField() + " :" + fieldError.getDefaultMessage())
                    .toList();
            throw new NotValidException(errorMessages);
        }
        return schedaAllenamentoService.modificaSchedaStandard(id, body);
    }

    // ADMIN elimina scheda standard

    @DeleteMapping("/schede/standard/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminaSchedaStandard(@PathVariable Long id) {
        schedaAllenamentoService.eliminaSchedaStandard(id);
    }

    // ADMIN elimina scheda standard o custom (degli utenti)

    @DeleteMapping("/schede/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminaSchedaById(@PathVariable Long id) {
        schedaAllenamentoService.eliminaSchedaById(id);
    }


    // ========== SCHEDE PERSONALIZZATE (UTENTE) ==========

    @PostMapping("/schede/utente/{utenteId}")
    @ResponseStatus(HttpStatus.CREATED)
    public SchedaAllenamentoDTO creaSchedaPerUtente(
            @PathVariable Long utenteId,
            @RequestBody @Valid SchedaPersonalizzataRequestDTO body) {
        return schedaAllenamentoService.creaSchedaPersonalizzata(utenteId, body);
    }

    @GetMapping("/schede/utente/{utenteId}")
    public List<SchedaAllenamentoDTO> getSchedeByUtente(@PathVariable Long utenteId) {
        return schedaAllenamentoService.getSchedeByUtente(utenteId);
    }
// ========== GESTIONE SCHEDE ADMIN CON FILTRI ==========

    // Admin filtra schede standard per obiettivo
    @GetMapping("/schede/standard/obiettivo/{obiettivo}")
    public List<SchedaAllenamentoDTO> adminGetSchedeStandardPerObiettivo(
            @PathVariable ObiettivoAllenamento obiettivo) {
        return schedaAllenamentoService.getSchedeStandardPerObiettivo(obiettivo);
    }

    // Admin filtra schede custom per obiettivo
    @GetMapping("/schede/custom/obiettivo/{obiettivo}")
    public List<SchedaAllenamentoDTO> adminGetSchedeCustomPerObiettivo(
            @PathVariable ObiettivoAllenamento obiettivo) {
        return schedaAllenamentoService.getAllSchedeCustomPerObiettivo(obiettivo);
    }

    // Admin filtra TUTTE le schede per obiettivo (standard + custom)
    @GetMapping("/schede/obiettivo/{obiettivo}")
    public List<SchedaAllenamentoDTO> adminGetSchedePerObiettivo(
            @PathVariable ObiettivoAllenamento obiettivo) {
        return schedaAllenamentoService.getAllSchedePerObiettivo(obiettivo);
    }
    // DELETE - Admin elimina una qualsiasi scheda allenamento
    @DeleteMapping("/schede-allenamento/{schedaId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void adminDeleteScheda(@PathVariable Long schedaId) {
        schedaAllenamentoService.adminEliminaScheda(schedaId);
    }
    // ========== Q&A (DOMANDE E RISPOSTE) ==========

    @PostMapping("/qea")
    @ResponseStatus(HttpStatus.CREATED)
    public QeAResponseDTO creaDomanda(@RequestBody @Valid QeARequestDTO body) {
        return qeAService.creaQeA(body);
    }

    @PutMapping("/qea/{id}")
    public QeAResponseDTO aggiornaDomanda(@PathVariable Long id, @RequestBody @Valid QeARequestDTO body) {
        return qeAService.aggiornaQeA(id, body);
    }

    @DeleteMapping("/qea/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminaDomanda(@PathVariable Long id) {
        qeAService.eliminaQeA(id);
    }
}

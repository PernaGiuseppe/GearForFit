package giuseppeperna.GearForFit.controllers;

import giuseppeperna.GearForFit.entities.Alimenti.Alimento;
import giuseppeperna.GearForFit.entities.Alimenti.Carne;
import giuseppeperna.GearForFit.entities.Diete.DietaStandard;
import giuseppeperna.GearForFit.entities.Diete.TipoDieta;
import giuseppeperna.GearForFit.entities.Utente.QeA;
import giuseppeperna.GearForFit.entities.SchedePalestra.*;
import giuseppeperna.GearForFit.entities.Utente.TipoPiano;
import giuseppeperna.GearForFit.entities.Utente.Utente;
import giuseppeperna.GearForFit.payloads.*;
import giuseppeperna.GearForFit.services.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

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

  /*  @PostMapping("/alimenti/carne")
    @ResponseStatus(HttpStatus.CREATED)
    public Carne creaAlimentoCarne(@RequestBody @Valid AlimentoRequestDTO body) {
        Carne carne = new Carne();
        mappaAlimento(carne, body);
        return alimentoService.saveCarne(carne);
    }*/
/*    private void mappaAlimento(Alimento alimento, AlimentoRequestDTO body) {
        alimento.setNome(body.nome());
        alimento.setCaloriePer100g(body.caloriePer100g());
        alimento.setProteinePer100g(body.proteinePer100g());
        alimento.setCarboidratiPer100g(body.carboidratiPer100g());
        alimento.setGrassiPer100g(body.grassiPer100g());
        alimento.setFibrePer100g(body.fibrePer100g());
    }*/

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

// ========== GESTIONE DIETE STANDARD ==========

    @PostMapping("/diete/standard")
    @ResponseStatus(HttpStatus.CREATED)
    public DietaStandardDTO creaDietaStandard(@RequestBody @Valid DietaStandardRequestDTO body) {
        return dietaService.creaDietaStandard(body);
    }

    @PutMapping("/diete/standard/{id}")
    public DietaStandardDTO aggiornaDietaStandard(
            @PathVariable Long id,
            @RequestBody @Valid DietaStandardRequestDTO body) {
        return dietaService.aggiornaDietaStandard(id, body);
    }

    @DeleteMapping("/diete/standard/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminaDietaStandard(@PathVariable Long id) {
        dietaService.eliminaDietaStandard(id);
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

    /* // Disattiva un utente
    @PutMapping("/utenti/{id}/disattiva")
    public Utente disattivaUtente(@PathVariable Long id) {
        return utenteService.disattivaUtente(id);
    }

    // Attiva un utente
    @PutMapping("/utenti/{id}/attiva")
    public Utente attivaUtente(@PathVariable Long id) {
        return utenteService.attivaUtente(id);
    }*/

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

    @PostMapping("/schede/standard")
    @ResponseStatus(HttpStatus.CREATED)
    public SchedaAllenamentoDTO creaSchedaStandard(@RequestBody @Valid SchedaAllenamentoRequestDTO body) {
        return schedaAllenamentoService.creaSchedaStandard(body);
    }

    @GetMapping("/schede/standard")
    public List<SchedaAllenamentoDTO> getSchedeStandard() {
        return schedaAllenamentoService.getSchedeStandard();
    }

    @GetMapping("/schede/standard/obiettivo/{obiettivo}")
    public List<SchedaAllenamentoDTO> getSchedeStandardPerObiettivo(@PathVariable ObiettivoAllenamento obiettivo) {
        return schedaAllenamentoService.getSchedeStandardByObiettivo(obiettivo);
    }

    @GetMapping("/schede/{id}")
    public SchedaAllenamentoDTO getScheda(@PathVariable Long id) {
        return schedaAllenamentoService.getSchedaById(id);
    }

    @PutMapping("/schede/standard/{id}")
    public SchedaAllenamentoDTO aggiornaSchedaStandard(
            @PathVariable Long id,
            @RequestBody @Valid SchedaAllenamentoRequestDTO body) {
        return schedaAllenamentoService.modificaSchedaStandard(id, body);
    }

    @DeleteMapping("/schede/standard/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminaSchedaStandard(@PathVariable Long id) {
        schedaAllenamentoService.eliminaSchedaStandard(id);
    }

    // ========== SCHEDE PERSONALIZZATE (UTENTE) ==========

    @PostMapping("/schede/utente/{utenteId}")
    @ResponseStatus(HttpStatus.CREATED)
    public SchedaAllenamentoDTO creaSchedaPerUtente(
            @PathVariable Long utenteId,
            @RequestBody @Valid SchedaAllenamentoRequestDTO body) {
        return schedaAllenamentoService.creaSchedaPersonalizzata(utenteId, body);
    }

    @GetMapping("/schede/utente/{utenteId}")
    public List<SchedaAllenamentoDTO> getSchedeByUtente(@PathVariable Long utenteId) {
        return schedaAllenamentoService.getSchedeByUtente(utenteId);
    }

    // ========== Q&A (DOMANDE E RISPOSTE) ==========

    @PostMapping("/qea")
    @ResponseStatus(HttpStatus.CREATED)
    public QeAResponseDTO creaDomanda(@RequestBody @Valid QeARequestDTO body) {
        return qeAService.creaQeA(body);
    }

    @GetMapping("/qea")
    public List<QeAResponseDTO> getTutteDomande() {
        return qeAService.getAllQeA();
    }

    @GetMapping("/qea/{id}")
    public QeAResponseDTO getDomandaById(@PathVariable Long id) {
        return qeAService.getQeAById(id);
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

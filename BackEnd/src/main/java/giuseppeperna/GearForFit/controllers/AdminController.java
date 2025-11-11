package giuseppeperna.GearForFit.controllers;

import giuseppeperna.GearForFit.entities.SchedePalestra.ObiettivoAllenamento;
import giuseppeperna.GearForFit.entities.Utente.TipoPiano;
import giuseppeperna.GearForFit.entities.Utente.TipoUtente;
import giuseppeperna.GearForFit.entities.Utente.Utente;
import giuseppeperna.GearForFit.exceptions.NotValidException;
import giuseppeperna.GearForFit.payloads.*;
import giuseppeperna.GearForFit.services.EsercizioService;
import giuseppeperna.GearForFit.services.QeAService;
import giuseppeperna.GearForFit.services.SchedaAllenamentoService;
import giuseppeperna.GearForFit.services.UtenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminController {

    @Autowired
    private UtenteService utenteService;

    @Autowired
    private SchedaAllenamentoService schedaService;

    @Autowired
    private EsercizioService esercizioService;

    @Autowired
    private QeAService qeaService;

    // ============= GESTIONE UTENTI =============

    // Ottieni tutti gli utenti
    @GetMapping("/utenti")
    public List<Utente> getAllUtenti() {
        return utenteService.getAllUtenti();
    }

    // Ottieni un utente specifico
    @GetMapping("/utenti/{id}")
    public Utente getUtente(@PathVariable Long id) {
        return utenteService.findById(id);
    }

    // Crea un nuovo utente (ADMIN crea gli utenti) - CON JSON E PIANO OPZIONALE
    @PostMapping("/utenti")
    @ResponseStatus(HttpStatus.CREATED)
    public Utente creaUtente(@RequestBody @Validated CreaUtenteRequestDTO body, BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            List<String> errorMessages = validationResult.getFieldErrors().stream()
                    .map(fieldError -> fieldError.getField() + " :" + fieldError.getDefaultMessage())
                    .toList();
            throw new NotValidException(errorMessages);
        }

        // Se tipoPiano è specificato, usa il metodo con piano custom
        if (body.tipoPiano() != null) {
            return utenteService.creaUtenteConPiano(
                    body.email(),
                    body.password(),
                    body.nome(),
                    body.cognome(),
                    body.tipoUtente(),
                    body.tipoPiano()
            );
        }

        // Altrimenti usa il metodo standard (FREE di default)
        return utenteService.creaUtente(body.email(), body.password(), body.nome(), body.cognome(), body.tipoUtente());
    }


    // Cambia il ruolo di un utente
    @PutMapping("/utenti/{id}/ruolo")
    public Utente cambiaRuolo(
            @PathVariable Long id,
            @RequestParam TipoUtente nuovoRuolo) {
        return utenteService.cambiaRuolo(id, nuovoRuolo);
    }

    // Cambia il piano di un utente
    @PutMapping("/utenti/{id}/piano")
    public Utente cambiaPiano(
            @PathVariable Long id,
            @RequestParam TipoPiano nuovoPiano) {
        return utenteService.cambiaPiano(id, nuovoPiano);
    }

    /*    // Disattiva un utente
        @PutMapping("/utenti/{id}/disattiva")
        public Utente disattivaUtente(@PathVariable Long id) {
            return utenteService.disattivaUtente(id);
        }

        // Attiva un utente
        @PutMapping("/utenti/{id}/attiva")
        public Utente attivaUtente(@PathVariable Long id) {
            return utenteService.attivaUtente(id);
        }*/

    // Admin resetta password di un utente
    @PutMapping("/utenti/reset-password")
    public Utente resetPasswordUtente(@RequestBody ResetPasswordAdminDTO body) {
        return utenteService.resetPasswordByAdmin(body.utenteId(), body.nuovaPassword());
    }

    // Elimina un utente
    @DeleteMapping("/utenti/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminaUtente(@PathVariable Long id) {
        utenteService.eliminaUtente(id);
    }

    // ============= GESTIONE SCHEDE STANDARD =============

    // Crea una scheda STANDARD (associata all'admin corrente)
    @PostMapping("/schede-standard")
    @ResponseStatus(HttpStatus.CREATED)
    public SchedaAllenamentoDTO creaSchedaStandard(
            @RequestBody @Validated SchedaAllenamentoRequestDTO body,
            BindingResult validationResult,
            Authentication authentication) {
        if (validationResult.hasErrors()) {
            List<String> errorMessages = validationResult.getFieldErrors().stream()
                    .map(fieldError -> fieldError.getField() + " :" + fieldError.getDefaultMessage())
                    .toList();
            throw new NotValidException(errorMessages);
        }
        Utente admin = (Utente) authentication.getPrincipal();
        return schedaService.creaSchedaStandard(admin.getId(), body);
    }

    // Ottieni tutte le schede STANDARD
    @GetMapping("/schede-standard")
    public List<SchedaAllenamentoDTO> ottieniSchedeStandard() {
        return schedaService.ottieniSchedeStandard();
    }

    // Ottieni schede standard per obiettivo
    @GetMapping("/schede-standard/obiettivo/{obiettivo}")
    public List<SchedaAllenamentoDTO> ottieniSchedeStandardPerObiettivo(
            @PathVariable ObiettivoAllenamento obiettivo) {
        return schedaService.ottieniSchedeStandardPerObiettivo(obiettivo);
    }

    // Ottieni una scheda STANDARD per ID
    @GetMapping("/schede-standard/{id}")
    public SchedaAllenamentoDTO ottieniSchedaStandard(@PathVariable Long id) {
        return schedaService.ottieniSchedaPerId(id);
    }

    // Aggiorna una scheda STANDARD
    @PutMapping("/schede-standard/{id}")
    public SchedaAllenamentoDTO aggiornaSchedaStandard(
            @PathVariable Long id,
            @RequestBody @Validated SchedaAllenamentoRequestDTO body,
            BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            List<String> errorMessages = validationResult.getFieldErrors().stream()
                    .map(fieldError -> fieldError.getField() + " :" + fieldError.getDefaultMessage())
                    .toList();
            throw new NotValidException(errorMessages);
        }
        return schedaService.aggiornaScheda(id, body);
    }

    // Elimina una scheda STANDARD
    @DeleteMapping("/schede-standard/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminaSchedaStandard(@PathVariable Long id) {
        schedaService.eliminaScheda(id);
    }

    // Duplica una scheda standard per un utente specifico
    @PostMapping("/schede-standard/{schedaId}/duplica/{utenteId}")
    @ResponseStatus(HttpStatus.CREATED)
    public SchedaAllenamentoDTO duplicaSchedaStandardPerUtente(
            @PathVariable Long schedaId,
            @PathVariable Long utenteId) {
        return schedaService.duplicaSchedaStandardPerUtente(schedaId, utenteId);
    }

    // ============= GESTIONE SCHEDE UTENTE =============

    // Crea una scheda per un utente specifico (scheda custom/personalizzata)
    @PostMapping("/schede/{utenteId}")
    @ResponseStatus(HttpStatus.CREATED)
    public SchedaAllenamentoDTO creaSchedaPerUtente(
            @PathVariable Long utenteId,
            @RequestBody @Validated SchedaAllenamentoRequestDTO body,
            BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            List<String> errorMessages = validationResult.getFieldErrors().stream()
                    .map(fieldError -> fieldError.getField() + " :" + fieldError.getDefaultMessage())
                    .toList();
            throw new NotValidException(errorMessages);
        }
        return schedaService.creaScheda(utenteId, body);
    }

    // Ottieni tutte le schede di un utente specifico
    @GetMapping("/schede/utente/{utenteId}")
    public List<SchedaAllenamentoDTO> ottieniSchedeUtente(@PathVariable Long utenteId) {
        return schedaService.ottieniSchedeUtente(utenteId);
    }

    // ============= GESTIONE ESERCIZI =============

    // Crea un nuovo esercizio
    @PostMapping("/esercizi")
    @ResponseStatus(HttpStatus.CREATED)
    public EsercizioDTO creaEsercizio(
            @RequestBody @Validated EsercizioRequestDTO body,
            BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            List<String> errorMessages = validationResult.getFieldErrors().stream()
                    .map(fieldError -> fieldError.getField() + " :" + fieldError.getDefaultMessage())
                    .toList();
            throw new NotValidException(errorMessages);
        }
        return esercizioService.creaEsercizio(body);
    }

    // Ottieni tutti gli esercizi
    @GetMapping("/esercizi")
    public List<EsercizioDTO> getAllEsercizi() {
        return esercizioService.ottieniTuttiEsercizi();
    }

    // Ottieni un esercizio specifico
    @GetMapping("/esercizi/{id}")
    public EsercizioDTO getEsercizio(@PathVariable Long id) {
        return esercizioService.ottieniEsercizioPerId(id);
    }

    // Cerca esercizi per nome
    @GetMapping("/esercizi/cerca")
    public List<EsercizioDTO> cercaEsercizi(@RequestParam String nome) {
        return esercizioService.cercaEserciziPerNome(nome);
    }

    // Ottieni esercizi per gruppo muscolare
    @GetMapping("/esercizi/gruppo/{gruppoId}")
    public List<EsercizioDTO> getEserciziPerGruppo(@PathVariable Long gruppoId) {
        return esercizioService.ottieniEsercizioPerGruppo(gruppoId);
    }

    // Ottieni esercizi per attrezzo
    @GetMapping("/esercizi/attrezzo/{attrezzoId}")
    public List<EsercizioDTO> getEserciziPerAttrezzo(@PathVariable Long attrezzoId) {
        return esercizioService.ottieniEserciziPerAttrezzo(attrezzoId);
    }

    // Ottieni solo esercizi composti
    @GetMapping("/esercizi/composti")
    public List<EsercizioDTO> getEserciziComposti() {
        return esercizioService.ottieniEserciziComposti();
    }

    // Aggiorna un esercizio
    @PutMapping("/esercizi/{id}")
    public EsercizioDTO aggiornaEsercizio(
            @PathVariable Long id,
            @RequestBody @Validated EsercizioRequestDTO body,
            BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            List<String> errorMessages = validationResult.getFieldErrors().stream()
                    .map(fieldError -> fieldError.getField() + " :" + fieldError.getDefaultMessage())
                    .toList();
            throw new NotValidException(errorMessages);
        }
        return esercizioService.aggiornaEsercizio(id, body);
    }

    // Elimina un esercizio
    @DeleteMapping("/esercizi/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminaEsercizio(@PathVariable Long id) {
        esercizioService.eliminaEsercizio(id);
    }

    // ============= GESTIONE Q&A =============

    // Crea nuova Q&A
    @PostMapping("/qea")
    @ResponseStatus(HttpStatus.CREATED)
    public QeAResponseDTO creaQeA(
            @RequestBody @Validated QeARequestDTO body,
            BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            List<String> errorMessages = validationResult.getFieldErrors().stream()
                    .map(fieldError -> fieldError.getField() + " :" + fieldError.getDefaultMessage())
                    .toList();
            throw new NotValidException(errorMessages);
        }
        return qeaService.creaQeA(body);
    }

    // Ottieni tutte le Q&A
    @GetMapping("/qea")
    public List<QeAResponseDTO> getAllQeA() {
        return qeaService.getAllQeA();
    }

    // Ottieni singola Q&A
    @GetMapping("/qea/{id}")
    public QeAResponseDTO getQeA(@PathVariable Long id) {
        return qeaService.getQeAById(id);
    }

    // Aggiorna Q&A
    @PutMapping("/qea/{id}")
    public QeAResponseDTO aggiornaQeA(
            @PathVariable Long id,
            @RequestBody @Validated QeARequestDTO body,
            BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            List<String> errorMessages = validationResult.getFieldErrors().stream()
                    .map(fieldError -> fieldError.getField() + " :" + fieldError.getDefaultMessage())
                    .toList();
            throw new NotValidException(errorMessages);
        }
        return qeaService.aggiornaQeA(id, body);
    }

    // Elimina Q&A
    @DeleteMapping("/qea/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminaQeA(@PathVariable Long id) {
        qeaService.eliminaQeA(id);
    }
}

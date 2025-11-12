package giuseppeperna.GearForFit.controllers;

import giuseppeperna.GearForFit.payloads.SchedaAllenamentoDTO;
import giuseppeperna.GearForFit.exceptions.NotValidException;
import giuseppeperna.GearForFit.payloads.SchedaAllenamentoRequestDTO;
import giuseppeperna.GearForFit.services.SchedaAllenamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/schede-allenamento")
public class SchedaAllenamentoController {

    @Autowired
    private SchedaAllenamentoService schedaService;

    // ========== SCHEDE STANDARD (ADMIN) ==========

    // ADMIN crea scheda standard
    @PostMapping("/standard")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public SchedaAllenamentoDTO creaSchedaStandard(
            @RequestBody @Validated SchedaAllenamentoRequestDTO body,
            BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            List<String> errorMessages = validationResult.getFieldErrors().stream()
                    .map(fieldError -> fieldError.getField() + " :" + fieldError.getDefaultMessage())
                    .toList();
            throw new NotValidException(errorMessages);
        }
        return schedaService.creaSchedaStandard(body);
    }

    // Ottieni tutte le schede standard
    @GetMapping("/standard")
    public List<SchedaAllenamentoDTO> getSchedeStandard() {
        return schedaService.getSchedeStandard();
    }

    // ADMIN aggiorna scheda standard
    @PutMapping("/standard/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
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
        return schedaService.modificaSchedaStandard(id, body);
    }

    // ADMIN elimina scheda standard
    @DeleteMapping("/standard/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminaSchedaStandard(@PathVariable Long id) {
        schedaService.eliminaSchedaStandard(id);
    }

    // ========== SCHEDE PERSONALIZZATE (UTENTE) ==========

    // Utente crea scheda personalizzata
    @PostMapping("/utente/{utenteId}")
    @ResponseStatus(HttpStatus.CREATED)
    public SchedaAllenamentoDTO creaSchedaPersonalizzata(
            @PathVariable Long utenteId,
            @RequestBody @Validated SchedaAllenamentoRequestDTO body,
            BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            List<String> errorMessages = validationResult.getFieldErrors().stream()
                    .map(fieldError -> fieldError.getField() + " :" + fieldError.getDefaultMessage())
                    .toList();
            throw new NotValidException(errorMessages);
        }
        return schedaService.creaSchedaPersonalizzata(utenteId, body);
    }

    // Ottieni tutte le schede di uno specifico utente
    @GetMapping("/utente/{utenteId}")
    public List<SchedaAllenamentoDTO> getSchedeUtente(@PathVariable Long utenteId) {
        return schedaService.getSchedeByUtente(utenteId);
    }

    // Utente aggiorna la propria scheda personalizzata
    @PutMapping("/utente/{utenteId}/{schedaId}")
    public SchedaAllenamentoDTO aggiornaSchedaPersonalizzata(
            @PathVariable Long utenteId,
            @PathVariable Long schedaId,
            @RequestBody @Validated SchedaAllenamentoRequestDTO body,
            BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            List<String> errorMessages = validationResult.getFieldErrors().stream()
                    .map(fieldError -> fieldError.getField() + " :" + fieldError.getDefaultMessage())
                    .toList();
            throw new NotValidException(errorMessages);
        }
        return schedaService.modificaSchedaPersonalizzata(schedaId, utenteId, body);
    }

    // Utente elimina la propria scheda personalizzata
    @DeleteMapping("/utente/{utenteId}/{schedaId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminaSchedaPersonalizzata(
            @PathVariable Long utenteId,
            @PathVariable Long schedaId) {
        schedaService.eliminaSchedaPersonalizzata(schedaId, utenteId);
    }

    // ========== UTILITY ==========

    // Ottieni una scheda specifica per ID (standard o personalizzata)
    @GetMapping("/{id}")
    public SchedaAllenamentoDTO getSchedaById(@PathVariable Long id) {
        return schedaService.getSchedaById(id);
    }
}

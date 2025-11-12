package giuseppeperna.GearForFit.controllers;

import giuseppeperna.GearForFit.entities.SchedePalestra.SchedaAllenamento;
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

    // ADMIN può creare schede
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public SchedaAllenamento creaScheda(
            @RequestBody @Validated SchedaAllenamentoRequestDTO body,
            BindingResult validationResult) {

        if (validationResult.hasErrors()) {
            List<String> errorMessages = validationResult.getFieldErrors().stream()
                    .map(fieldError -> fieldError.getField() + " :" + fieldError.getDefaultMessage())
                    .toList();
            throw new NotValidException(errorMessages);
        }

        return schedaService.creaScheda(body);
    }

    // Ottieni una scheda specifica per ID
    @GetMapping("/{id}")
    public SchedaAllenamento ottieniScheda(@PathVariable Long id) {
        return schedaService.getSchedaById(id);
    }

    // Ottieni tutte le schede di uno specifico utente
    @GetMapping("/utente/{utenteId}")
    public List<SchedaAllenamento> ottieniSchedeUtente(@PathVariable Long utenteId) {
        return schedaService.getSchedeByUtente(utenteId);
    }

    // ADMIN può aggiornare una scheda
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public SchedaAllenamento aggiornaScheda(
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

    // ADMIN può eliminare una scheda
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminaScheda(@PathVariable Long id) {
        schedaService.eliminaScheda(id);
    }
}

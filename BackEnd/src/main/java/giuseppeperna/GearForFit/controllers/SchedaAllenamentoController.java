package giuseppeperna.GearForFit.controllers;

import giuseppeperna.GearForFit.entities.SchedePalestra.ObiettivoAllenamento;
import giuseppeperna.GearForFit.entities.Utente.Utente;
import giuseppeperna.GearForFit.payloads.SchedaAllenamentoDTO;
import giuseppeperna.GearForFit.exceptions.NotValidException;
import giuseppeperna.GearForFit.payloads.SchedaAllenamentoRequestDTO;
import giuseppeperna.GearForFit.entities.SchedePalestra.ObiettivoAllenamento;
import giuseppeperna.GearForFit.entities.Utente.TipoUtente;
import giuseppeperna.GearForFit.payloads.SchedaPersonalizzataRequestDTO;
import giuseppeperna.GearForFit.services.SchedaAllenamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;

@RestController
@RequestMapping("/schede-allenamento")
public class SchedaAllenamentoController {

    @Autowired
    private SchedaAllenamentoService schedaService;

    // ========== SCHEDE STANDARD (ADMIN) ==========

    // Ottieni tutte le schede standard
    @GetMapping("/standard")
    @PreAuthorize("hasAuthority('ADMIN') or hasAnyAuthority('PIANO_PREMIUM', 'PIANO_GOLD', 'PIANO_SILVER')")
    public List<SchedaAllenamentoDTO> getSchedeStandard() {
        return schedaService.getSchedeStandard();
    }

    @GetMapping("/standard/obiettivo/{obiettivo}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAnyAuthority('PIANO_PREMIUM', 'PIANO_GOLD', 'PIANO_SILVER')")
    public List<SchedaAllenamentoDTO> getSchedeStandardPerObiettivo(@PathVariable ObiettivoAllenamento obiettivo) {
        return schedaService.getSchedeStandardByObiettivo(obiettivo);
    }
    // ========== SCHEDE PERSONALIZZATE (UTENTE) ==========

    // Utente crea scheda personalizzata
    @PostMapping("/utente/{utenteId}")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ADMIN') or hasAnyAuthority('PIANO_PREMIUM', 'PIANO_GOLD')")
    public SchedaAllenamentoDTO creaSchedaPersonalizzata(
            @AuthenticationPrincipal Utente utente,
            @PathVariable Long utenteId,
            // MODIFICATO: Usa il nuovo DTO
            @RequestBody @Validated SchedaPersonalizzataRequestDTO body,
            BindingResult validationResult) {

        // (Controllo di sicurezza... va bene)
        if (!utente.getId().equals(utenteId)) {
            throw new org.springframework.security.access.AccessDeniedException("Non sei autorizzato a creare una scheda per un altro utente.");
        }

        if (validationResult.hasErrors()) {
            List<String> errorMessages = validationResult.getFieldErrors().stream()
                    .map(fieldError -> fieldError.getField() + " : " + fieldError.getDefaultMessage())
                    .toList();
            throw new NotValidException(errorMessages);
        }
        // Ora chiama il service aggiornato
        return schedaService.creaSchedaPersonalizzata(utenteId, body);
    }

    // Ottieni tutte le schede di uno specifico utente
    @GetMapping("/utente/{utenteId}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAnyAuthority('PIANO_PREMIUM', 'PIANO_GOLD')")
    public List<SchedaAllenamentoDTO> getSchedeUtente(
            @AuthenticationPrincipal Utente utente,
            @PathVariable Long utenteId) {
        // Verifica che l'utente autenticato stia richiedendo le proprie schede
        if (!utente.getId().equals(utenteId)) {
            throw new org.springframework.security.access.AccessDeniedException("Non sei autorizzato a visualizzare le schede di un altro utente.");
        }
        return schedaService.getSchedeByUtente(utenteId);
    }

    // GET - Ottieni una singola scheda personalizzata di un utente
    @GetMapping("/utente/{utenteId}/{schedaId}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAnyAuthority('PIANO_PREMIUM', 'PIANO_GOLD')")
    public SchedaAllenamentoDTO getSchedaPersonalizzataById(
            @AuthenticationPrincipal Utente utente, // Inietta l'utente autenticato
            @PathVariable Long utenteId,
            @PathVariable Long schedaId) {

        // Verifica che l'utente autenticato stia richiedendo una propria scheda
        if (!utente.getId().equals(utenteId)) {
            throw new org.springframework.security.access.AccessDeniedException("Non sei autorizzato a visualizzare la scheda di un altro utente.");
        }

        return schedaService.getSchedaByIdAndUtente(schedaId, utenteId);
    }
    @GetMapping("/utente/{utenteId}/obiettivo/{obiettivo}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAnyAuthority('PIANO_PREMIUM', 'PIANO_GOLD')")
    public List<SchedaAllenamentoDTO> getSchedePersonalizzatePerObiettivo(
            @AuthenticationPrincipal Utente utente, // Per il controllo di proprietà
            @PathVariable Long utenteId,
            @PathVariable ObiettivoAllenamento obiettivo) {
        if (utente.getTipoUtente() != TipoUtente.ADMIN && !utente.getId().equals(utenteId)) {
            throw new org.springframework.security.access.AccessDeniedException("Non sei autorizzato a visualizzare le schede di un altro utente.");
        }

        return schedaService.getSchedePersonalizzateByObiettivo(utenteId, obiettivo);
    }
    // Utente aggiorna la propria scheda personalizzata

    @PutMapping("/utente/{utenteId}/{schedaId}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAnyAuthority('PIANO_PREMIUM', 'PIANO_GOLD')")
    public SchedaAllenamentoDTO aggiornaSchedaPersonalizzata(
            @AuthenticationPrincipal Utente utente,
            @PathVariable Long utenteId,
            @PathVariable Long schedaId,
            @RequestBody @Validated SchedaPersonalizzataRequestDTO body,
            BindingResult validationResult) {

        if (!utente.getId().equals(utenteId)) {
            throw new org.springframework.security.access.AccessDeniedException("Non sei autorizzato a modificare la scheda di un altro utente.");
        }

        if (validationResult.hasErrors()) {
            List<String> errorMessages = validationResult.getFieldErrors().stream()
                    .map(fieldError -> fieldError.getField() + " : " + fieldError.getDefaultMessage())
                    .toList();
            throw new NotValidException(errorMessages);
        }

        return schedaService.modificaSchedaPersonalizzata(schedaId, utenteId, body);
    }

    // Utente elimina la propria scheda personalizzata
    @DeleteMapping("/utente/{utenteId}/{schedaId}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAnyAuthority('PIANO_PREMIUM', 'PIANO_GOLD')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminaSchedaPersonalizzata(
            @AuthenticationPrincipal Utente utente,
            @PathVariable Long utenteId,
            @PathVariable Long schedaId) {
        // Verifica che l'utente autenticato stia eliminando una propria scheda
        if (!utente.getId().equals(utenteId)) {
            throw new org.springframework.security.access.AccessDeniedException("Non sei autorizzato a eliminare la scheda di un altro utente.");
        }
        schedaService.eliminaSchedaPersonalizzata(schedaId, utenteId);
    }



}

package giuseppeperna.GearForFit.controllers;

import giuseppeperna.GearForFit.entities.SchedePalestra.ObiettivoAllenamento;
import giuseppeperna.GearForFit.entities.SchedePalestra.SchedaAllenamento;
import giuseppeperna.GearForFit.entities.Utente.Utente;
import giuseppeperna.GearForFit.exceptions.NotFoundException;
import giuseppeperna.GearForFit.payloads.SchedaAllenamentoDTO;
import giuseppeperna.GearForFit.exceptions.NotValidException;
import giuseppeperna.GearForFit.payloads.SchedaAllenamentoRequestDTO;
import giuseppeperna.GearForFit.entities.SchedePalestra.ObiettivoAllenamento;
import giuseppeperna.GearForFit.entities.Utente.TipoUtente;
import giuseppeperna.GearForFit.payloads.SchedaPersonalizzataRequestDTO;
import giuseppeperna.GearForFit.repositories.SchedaAllenamentoRepository;
import giuseppeperna.GearForFit.services.SchedaAllenamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/schede-allenamento")
public class SchedaAllenamentoController {

    @Autowired
    private SchedaAllenamentoService schedaService;
    @Autowired
    private SchedaAllenamentoRepository schedaRepository;


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

    // ========== GET di tutte le schede ==========

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<SchedaAllenamentoDTO>> getAllSchede(
            @AuthenticationPrincipal Utente utente,
            @RequestParam(required = false, defaultValue = "STANDARD") String filtro) {

        List<SchedaAllenamentoDTO> schede = schedaService.getAllSchedeFiltered(utente, filtro);
        return ResponseEntity.ok(schede);
    }
    // GET - Ottieni una singola scheda (Standard o Personalizzata) per ID
    @GetMapping("/{schedaId}")
    @PreAuthorize("isAuthenticated()")
    public SchedaAllenamentoDTO getSchedaById(
            @PathVariable Long schedaId,
            @AuthenticationPrincipal Utente utente) {
        return schedaService.getSchedaById(schedaId, utente);
    }

    // GET tutte le schede (standard + personalizzate) con filtro opzionale per obiettivo
    @GetMapping("/schede")
    @PreAuthorize("isAuthenticated()")
    public List<SchedaAllenamentoDTO> getAllSchede(
            @RequestParam(required = false) String filtro,
            @RequestParam(required = false) ObiettivoAllenamento obiettivo,
            @AuthenticationPrincipal Utente utente) {

        if (obiettivo != null) {
            // Filtra per obiettivo (include sia standard che personalizzate dell'utente)
            return schedaService.getAllSchedeByObiettivo(utente.getId(), obiettivo);
        }

        // Comportamento originale se non c'è filtro obiettivo
        if ("ALL".equalsIgnoreCase(filtro)) {
            List<SchedaAllenamentoDTO> result = new ArrayList<>();
            result.addAll(schedaService.getSchedeStandard());
            result.addAll(schedaService.getSchedeByUtente(utente.getId()));
            return result;
        }

        return schedaService.getSchedeStandard();
    }
    // ========== SCHEDE PERSONALIZZATE (UTENTE) ==========

    // Utente crea scheda personalizzata
    @PostMapping("/me")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ADMIN') or hasAnyAuthority('PIANO_PREMIUM', 'PIANO_GOLD')")
    public SchedaAllenamentoDTO creaMiaSchedaPersonalizzata(
            @AuthenticationPrincipal Utente utente,
            @RequestBody @Validated SchedaPersonalizzataRequestDTO body,
            BindingResult validationResult) {

        if (validationResult.hasErrors()) {
            List<String> errorMessages = validationResult.getFieldErrors().stream()
                    .map(fieldError -> fieldError.getField() + " : " + fieldError.getDefaultMessage())
                    .toList();
            throw new NotValidException(errorMessages);
        }

        return schedaService.creaSchedaPersonalizzata(utente.getId(), body);
    }
 /*   @PostMapping("/utente/{utenteId}")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ADMIN') or hasAnyAuthority('PIANO_PREMIUM', 'PIANO_GOLD')")
    public SchedaAllenamentoDTO creaSchedaPersonalizzata(
            @AuthenticationPrincipal Utente utente,
            @PathVariable Long utenteId,
            @RequestBody @Validated SchedaPersonalizzataRequestDTO body,
            BindingResult validationResult) {

        if (!utente.getId().equals(utenteId)) {
            throw new org.springframework.security.access.AccessDeniedException("Non sei autorizzato a creare una scheda per un altro utente.");
        }

        if (validationResult.hasErrors()) {
            List<String> errorMessages = validationResult.getFieldErrors().stream()
                    .map(fieldError -> fieldError.getField() + " : " + fieldError.getDefaultMessage())
                    .toList();
            throw new NotValidException(errorMessages);
        }
        return schedaService.creaSchedaPersonalizzata(utenteId, body);
    }*/

    // Ottieni tutte le schede di uno specifico utente
    @GetMapping("/me")
    @PreAuthorize("hasAuthority('ADMIN') or hasAnyAuthority('PIANO_PREMIUM', 'PIANO_GOLD')")
    public List<SchedaAllenamentoDTO> getMieSchede(
            @AuthenticationPrincipal Utente utente) {
        return schedaService.getSchedeByUtente(utente.getId());
    }
 /*   @GetMapping("/utente/{utenteId}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAnyAuthority('PIANO_PREMIUM', 'PIANO_GOLD')")
    public List<SchedaAllenamentoDTO> getSchedeUtente(
            @AuthenticationPrincipal Utente utente,
            @PathVariable Long utenteId) {
        // Verifica che l'utente autenticato stia richiedendo le proprie schede
        if (!utente.getId().equals(utenteId)) {
            throw new org.springframework.security.access.AccessDeniedException("Non sei autorizzato a visualizzare le schede di un altro utente.");
        }
        return schedaService.getSchedeByUtente(utenteId);
    }*/

    // GET - Ottieni una singola scheda personalizzata di un utente
    @GetMapping("/me/{schedaId}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAnyAuthority('PIANO_PREMIUM', 'PIANO_GOLD')")
    public SchedaAllenamentoDTO getMiaSchedaPersonalizzata(
            @AuthenticationPrincipal Utente utente,
            @PathVariable Long schedaId) {
        return schedaService.getSchedaByIdAndUtente(schedaId, utente.getId());
    }
/*    @GetMapping("/utente/{utenteId}/{schedaId}")
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
    }*/
 // Ottieni le schede personalizzate dell'utente autenticato filtrate per obiettivo
    @GetMapping("/me/obiettivo/{obiettivo}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAnyAuthority('PIANO_PREMIUM', 'PIANO_GOLD')")
    public List<SchedaAllenamentoDTO> getMieSchedePerObiettivo(
            @AuthenticationPrincipal Utente utente,
            @PathVariable ObiettivoAllenamento obiettivo) {
        return schedaService.getSchedePersonalizzateByObiettivo(utente.getId(), obiettivo);
    }
 /*   @GetMapping("/utente/{utenteId}/obiettivo/{obiettivo}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAnyAuthority('PIANO_PREMIUM', 'PIANO_GOLD')")
    public List<SchedaAllenamentoDTO> getSchedePersonalizzatePerObiettivo(
            @AuthenticationPrincipal Utente utente,
            @PathVariable Long utenteId,
            @PathVariable ObiettivoAllenamento obiettivo) {
        if (utente.getTipoUtente() != TipoUtente.ADMIN && !utente.getId().equals(utenteId)) {
            throw new org.springframework.security.access.AccessDeniedException("Non sei autorizzato a visualizzare le schede di un altro utente.");
        }

        return schedaService.getSchedePersonalizzateByObiettivo(utenteId, obiettivo);
    }
*/
    @PutMapping("/me/schede/{id}/attiva")
    public SchedaAllenamentoDTO setMiaSchedaAttiva(
            @PathVariable Long id,
            @AuthenticationPrincipal Utente utenteLoggato) {

        // 1. Recupero la scheda per verificare a chi appartiene
        SchedaAllenamento scheda = schedaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Scheda allenamento non trovata con id: " + id));

        // 2. Aggiungo il controllo di autorizzazione come da te indicato
        if (utenteLoggato.getTipoUtente() != TipoUtente.ADMIN && !scheda.getUtente().getId().equals(utenteLoggato.getId())) {
            throw new org.springframework.security.access.AccessDeniedException("Non sei autorizzato a modificare la scheda di un altro utente.");
        }

        // 3. Chiama il service. Passo l'utente proprietario della scheda per mantenere la logica corretta nel service.
        return schedaService.setSchedaAttiva(id, scheda.getUtente());
    }

    // Utente aggiorna la propria scheda personalizzata
    @PutMapping("/me/{schedaId}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAnyAuthority('PIANO_PREMIUM', 'PIANO_GOLD')")
    public SchedaAllenamentoDTO aggiornaMiaSchedaPersonalizzata(
            @AuthenticationPrincipal Utente utente,
            @PathVariable Long schedaId,
            @RequestBody @Validated SchedaPersonalizzataRequestDTO body,
            BindingResult validationResult) {

        if (validationResult.hasErrors()) {
            List<String> errorMessages = validationResult.getFieldErrors().stream()
                    .map(fieldError -> fieldError.getField() + " : " + fieldError.getDefaultMessage())
                    .toList();
            throw new NotValidException(errorMessages);
        }

        return schedaService.modificaSchedaPersonalizzata(schedaId, utente.getId(), body);
    }
  /*  @PutMapping("/utente/{utenteId}/{schedaId}")
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
*/
    // Utente elimina la propria scheda personalizzata
  @DeleteMapping("/me/{schedaId}")
  @PreAuthorize("hasAuthority('ADMIN') or hasAnyAuthority('PIANO_PREMIUM', 'PIANO_GOLD')")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void eliminaMiaSchedaPersonalizzata(
          @AuthenticationPrincipal Utente utente,
          @PathVariable Long schedaId) {
      schedaService.eliminaSchedaPersonalizzata(schedaId, utente.getId());
  }
/*    @DeleteMapping("/utente/{utenteId}/{schedaId}")
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
    }*/



}

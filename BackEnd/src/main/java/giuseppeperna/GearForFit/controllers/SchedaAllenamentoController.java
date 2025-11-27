package giuseppeperna.GearForFit.controllers;

import giuseppeperna.GearForFit.entities.SchedePalestra.Esercizio;
import giuseppeperna.GearForFit.entities.SchedePalestra.ObiettivoAllenamento;
import giuseppeperna.GearForFit.entities.SchedePalestra.SchedaAllenamento;
import giuseppeperna.GearForFit.entities.Utente.Utente;
import giuseppeperna.GearForFit.exceptions.NotFoundException;
import giuseppeperna.GearForFit.payloads.*;
import giuseppeperna.GearForFit.exceptions.NotValidException;
import giuseppeperna.GearForFit.entities.SchedePalestra.ObiettivoAllenamento;
import giuseppeperna.GearForFit.entities.Utente.TipoUtente;
import giuseppeperna.GearForFit.repositories.EsercizioRepository;
import giuseppeperna.GearForFit.repositories.SchedaAllenamentoRepository;
import giuseppeperna.GearForFit.services.EsercizioService;
import giuseppeperna.GearForFit.services.SchedaAllenamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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
    private SchedaAllenamentoService schedaAllenamentoService;
    @Autowired
    private SchedaAllenamentoRepository schedaRepository;
    @Autowired
    private EsercizioRepository esercizioRepository;


    // ========== SCHEDE STANDARD (ADMIN) ==========

    //Utente da GOLD in su ottiene tutti gli esercizi per creare schede custom
    @GetMapping("/esercizi")
    @PreAuthorize("hasAuthority('ADMIN') or hasAnyAuthority('PIANO_PREMIUM', 'PIANO_GOLD')")
    public List<Esercizio> getAllEsercizi() {
        return esercizioRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }
    // ========== SCHEDE STANDARD (ADMIN) ==========

    // Ottieni tutte le schede standard
    @GetMapping("/standard")
    @PreAuthorize("hasAuthority('ADMIN') or hasAnyAuthority('PIANO_PREMIUM', 'PIANO_GOLD', 'PIANO_SILVER')")
    public List<SchedaAllenamentoDTO> getSchedeStandard() {
        return schedaAllenamentoService.getSchedeStandard();
    }

    @GetMapping("/standard/obiettivo/{obiettivo}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAnyAuthority('PIANO_PREMIUM', 'PIANO_GOLD', 'PIANO_SILVER')")
    public List<SchedaAllenamentoDTO> getSchedeStandardPerObiettivo(@PathVariable ObiettivoAllenamento obiettivo) {
        return schedaAllenamentoService.getSchedeStandardByObiettivo(obiettivo);
    }

    // ========== GET di tutte le schede ==========

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<SchedaAllenamentoDTO>> getAllSchede(
            @AuthenticationPrincipal Utente utente,
            @RequestParam(required = false, defaultValue = "STANDARD") String filtro) {

        List<SchedaAllenamentoDTO> schede = schedaAllenamentoService.getAllSchedeFiltered(utente, filtro);
        return ResponseEntity.ok(schede);
    }
    // GET - Ottieni una singola scheda (Standard o Personalizzata) per ID
    @GetMapping("/{schedaId}")
    @PreAuthorize("isAuthenticated()")
    public SchedaAllenamentoDTO getSchedaById(
            @PathVariable Long schedaId,
            @AuthenticationPrincipal Utente utente) {
        return schedaAllenamentoService.getSchedaById(schedaId, utente);
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
            return schedaAllenamentoService.getAllSchedeByObiettivo(utente.getId(), obiettivo);
        }

        // Comportamento originale se non c'è filtro obiettivo
        if ("ALL".equalsIgnoreCase(filtro)) {
            List<SchedaAllenamentoDTO> result = new ArrayList<>();
            result.addAll(schedaAllenamentoService.getSchedeStandard());
            result.addAll(schedaAllenamentoService.getSchedeByUtente(utente.getId()));
            return result;
        }

        return schedaAllenamentoService.getSchedeStandard();
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

        return schedaAllenamentoService.creaSchedaPersonalizzata(utente.getId(), body);
    }

    // Ottieni tutte le schede di uno specifico utente
    @GetMapping("/me")
    @PreAuthorize("hasAuthority('ADMIN') or hasAnyAuthority('PIANO_PREMIUM', 'PIANO_GOLD')")
    public List<SchedaAllenamentoDTO> getMieSchede(
            @AuthenticationPrincipal Utente utente) {
        return schedaAllenamentoService.getSchedeByUtente(utente.getId());
    }

    // GET - Ottieni una singola scheda personalizzata di un utente
    @GetMapping("/me/{schedaId}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAnyAuthority('PIANO_PREMIUM', 'PIANO_GOLD')")
    public SchedaAllenamentoDTO getMiaSchedaPersonalizzata(
            @AuthenticationPrincipal Utente utente,
            @PathVariable Long schedaId) {
        return schedaAllenamentoService.getSchedaByIdAndUtente(schedaId, utente.getId());
    }

 // Ottieni le schede personalizzate dell'utente autenticato filtrate per obiettivo
    @GetMapping("/me/obiettivo/{obiettivo}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAnyAuthority('PIANO_PREMIUM', 'PIANO_GOLD')")
    public List<SchedaAllenamentoDTO> getMieSchedePerObiettivo(
            @AuthenticationPrincipal Utente utente,
            @PathVariable ObiettivoAllenamento obiettivo) {
        return schedaAllenamentoService.getSchedePersonalizzateByObiettivo(utente.getId(), obiettivo);
    }

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
        return schedaAllenamentoService.setSchedaAttiva(id, scheda.getUtente());
    }

    @PatchMapping("/me/schede/{schedaId}/serie/{serieId}/peso")
    @PreAuthorize("hasAuthority('ADMIN') or hasAnyAuthority('PIANO_PREMIUM', 'PIANO_GOLD')")
    public ResponseEntity<SerieDTO> aggiornaPesoMiaSerie(
            @PathVariable Long schedaId,
            @PathVariable Long serieId,
            @RequestBody PesoUpdateDTO pesoDTO,
            @AuthenticationPrincipal Utente utenteLoggato
    ) {
        SerieDTO updated = schedaAllenamentoService.aggiornaPesoSerie(schedaId, serieId, pesoDTO.peso(), utenteLoggato);
        return ResponseEntity.ok(updated);
    }
    // Utente elimina la propria scheda personalizzata
  @DeleteMapping("/me/{schedaId}")
  @PreAuthorize("hasAuthority('ADMIN') or hasAnyAuthority('PIANO_PREMIUM', 'PIANO_GOLD')")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void eliminaMiaSchedaPersonalizzata(
          @AuthenticationPrincipal Utente utente,
          @PathVariable Long schedaId) {
      schedaAllenamentoService.eliminaSchedaPersonalizzata(schedaId, utente.getId());
  }




}

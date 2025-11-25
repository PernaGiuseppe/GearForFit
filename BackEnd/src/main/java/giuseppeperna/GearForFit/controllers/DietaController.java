package giuseppeperna.GearForFit.controllers;

import giuseppeperna.GearForFit.entities.Diete.CalcoloBMR;
import giuseppeperna.GearForFit.entities.Diete.DietaStandard;
import giuseppeperna.GearForFit.entities.Diete.TipoDieta;
import giuseppeperna.GearForFit.entities.Utente.Utente;
import giuseppeperna.GearForFit.payloads.*;
import giuseppeperna.GearForFit.services.CalcoloBMRService;
import giuseppeperna.GearForFit.services.DietaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/diete")
public class DietaController {

    @Autowired
    private DietaService dietaService;

    @Autowired
    private CalcoloBMRService calcoloBMRService;

  /*  @PostMapping("/personalizzata")
    @PreAuthorize("hasAuthority('ADMIN') or hasAnyAuthority('PIANO_PREMIUM', 'PIANO_GOLD', 'PIANO_SILVER')")
    public ResponseEntity<DietaStandardDTO> generaDietaPersonalizzata(
            @RequestBody DietaRequest dietaRequest
    ) {
        CalcoloBMRDTO bmrDto = dietaRequest.calcoloBMR();
        CalcoloBMR profilo = new CalcoloBMR();
        profilo.setPeso(bmrDto.peso());
        profilo.setAltezza(bmrDto.altezza());
        profilo.setEta(bmrDto.eta());
        profilo.setSesso(bmrDto.sesso());
        profilo.setLivelloAttivita(bmrDto.livelloAttivita());

        DietaStandardDTO dieta = dietaService.generaDietaStandardPersonalizzata(profilo, dietaRequest.tipoDieta());
        return ResponseEntity.ok(dieta);
    }*/
    @GetMapping("/standard")
    public List<DietaStandardDTO> getDieteStandard() {
        return dietaService.getAllDieteStandard();
    }

    @GetMapping("/standard/{id}")
    public ResponseEntity<DietaStandardDTO> getDietaStandardById(@PathVariable Long id) {
        return dietaService.getDietaStandardById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
 /*   @GetMapping("/standard/tipo")
    public DietaStandardDTO getDietaStandardByTipo(@RequestParam TipoDieta tipoDieta) {
        return dietaService.getDietaStandardByTipo(tipoDieta);
    }*/
    @GetMapping("/standard/tipo")
    @PreAuthorize("isAuthenticated()") // AGGIUNTO
    public List<DietaStandardDTO> getDieteStandardByTipo(@RequestParam TipoDieta tipoDieta) {
        return dietaService.getDieteStandardByTipo(tipoDieta); // Ora restituisce List
    }
    /*@GetMapping
    public org.springframework.data.domain.Page<DietaStandard> getDiete(@RequestParam(defaultValue = "0") int page,
                                                                        @RequestParam(defaultValue = "10") int size,
                                                                        @RequestParam(defaultValue = "id") String orderBy) {
        return dietaService.getDiete(page, size, orderBy);
    }*/
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Object>> getAllDiete(
            @AuthenticationPrincipal Utente utente,
            @RequestParam(required = false, defaultValue = "STANDARD") String filtro) {

        List<Object> diete = dietaService.getAllDieteFiltered(utente, filtro);
        return ResponseEntity.ok(diete);
    }
    // ENDPOINT PER LA GESTIONE DELLA DIETA DELL'UTENTE LOGGATO
    @PostMapping("/me/bmr")
    @PreAuthorize("hasAuthority('ADMIN') or hasAnyAuthority('PIANO_PREMIUM', 'PIANO_GOLD', 'PIANO_SILVER')")
    public CalcoloBMR salvaBMR(@RequestBody CalcoloBMRDTO body, Authentication authentication) {
        Utente utenteLoggato = (Utente) authentication.getPrincipal();
        return dietaService.salvaCalcoloBMR(utenteLoggato.getId(), body);
    }
    @GetMapping("/me/dieta/genera")
    @PreAuthorize("hasAuthority('ADMIN') or hasAnyAuthority('PIANO_PREMIUM', 'PIANO_GOLD', 'PIANO_SILVER')")
    public DietaStandardDTO generaMiaDietaPreview(
            @RequestParam TipoDieta tipoDieta,
            Authentication authentication) {
        Utente utenteLoggato = (Utente) authentication.getPrincipal();
        CalcoloBMR bmr = calcoloBMRService.getCalcoloBMRByUtente(utenteLoggato.getId());
        return dietaService.generaDietaStandardPersonalizzata(bmr, tipoDieta);
    }
    @PostMapping("/me/dieta")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ADMIN') or hasAnyAuthority('PIANO_PREMIUM', 'PIANO_GOLD', 'PIANO_SILVER')")
    public DietaStandardDTO assegnaEMostraMiaDieta(
            @RequestBody TipoDietaRequestDTO body,
            Authentication authentication) {
        Utente utenteLoggato = (Utente) authentication.getPrincipal();
        return dietaService.assegnaDietaAdUtente(utenteLoggato, body.tipoDieta());
    }


    @GetMapping("/me/dieta")
    @PreAuthorize("hasAuthority('ADMIN') or hasAnyAuthority('PIANO_PREMIUM', 'PIANO_GOLD', 'PIANO_SILVER')")
    public List<DietaUtenteDTO> getMieDieteAssegnate(Authentication authentication) {
        Utente utenteLoggato = (Utente) authentication.getPrincipal();
        return dietaService.getDieteAssegnate(utenteLoggato);
    }
    @GetMapping("/me/dieta/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAnyAuthority('PIANO_PREMIUM', 'PIANO_GOLD', 'PIANO_SILVER')")
    public DietaStandardDTO getMiaDietaAssegnataById(
            @PathVariable Long id,
            Authentication authentication) {
        Utente utenteLoggato = (Utente) authentication.getPrincipal();
        return dietaService.getDietaAssegnataById(id, utenteLoggato);
    }
    // GET - Ottieni una singola dieta (Standard o Personalizzata) per ID
    @GetMapping("/{dietaId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getDietaById(
            @PathVariable Long dietaId,
            @AuthenticationPrincipal Utente utente) {
        return ResponseEntity.ok(dietaService.getDietaById(dietaId, utente));
    }

    @GetMapping("/me/dieta/tipo/{tipoDieta}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAnyAuthority('PIANO_PREMIUM', 'PIANO_GOLD', 'PIANO_SILVER')")
    public List<DietaUtenteDTO> getMieDietePerTipo(
            @PathVariable TipoDieta tipoDieta,
            Authentication authentication) {
        Utente utenteLoggato = (Utente) authentication.getPrincipal();
        return dietaService.getDieteAssegnateByTipo(utenteLoggato.getId(), tipoDieta);
    }
    @PutMapping("/me/dieta/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAnyAuthority('PIANO_PREMIUM', 'PIANO_GOLD', 'PIANO_SILVER')")
    public DietaUtenteDTO modificaMiaDietaAssegnata(
            @PathVariable("id") Long dietaUtenteId,
            @RequestBody @Valid DietaStandardRequestDTO body,
            Authentication authentication) {
        Utente utenteLoggato = (Utente) authentication.getPrincipal();
        return dietaService.modificaDietaUtente(dietaUtenteId, utenteLoggato, body);
    }
    @PutMapping("/me/dieta/{id}/attiva")
    @PreAuthorize("hasAuthority('ADMIN') or hasAnyAuthority('PIANO_PREMIUM', 'PIANO_GOLD', 'PIANO_SILVER')")
    public DietaUtenteDTO setMiaDietaAttiva(
            @PathVariable Long id,
            Authentication authentication) {
        Utente utenteLoggato = (Utente) authentication.getPrincipal();
        return dietaService.setDietaAttiva(id, utenteLoggato);
    }
    @DeleteMapping("/me/dieta/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('ADMIN') or hasAnyAuthority('PIANO_PREMIUM', 'PIANO_GOLD', 'PIANO_SILVER')")
    public void eliminaMiaDietaAssegnata(
            @PathVariable Long id,
            Authentication authentication) {
        Utente utenteLoggato = (Utente) authentication.getPrincipal();
        dietaService.eliminaDietaAssegnata(id, utenteLoggato);
    }
    }


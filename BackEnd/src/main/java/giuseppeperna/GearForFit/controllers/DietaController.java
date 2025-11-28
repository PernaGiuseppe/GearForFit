package giuseppeperna.GearForFit.controllers;

import giuseppeperna.GearForFit.entities.Utente.Utente;
import giuseppeperna.GearForFit.payloads.*;
import giuseppeperna.GearForFit.services.DietaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/diete")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN') or hasAnyAuthority('PIANO_PREMIUM', 'PIANO_GOLD', 'PIANO_SILVER', 'PIANO_FREE')")
public class DietaController {

    private final DietaService dietaService;

    // ===== VISUALIZZAZIONE DIETE STANDARD (PUBLIC) =====

    // Visualizza tutte le diete standard disponibili

    @GetMapping("/standard")
    public List<DietaDTO> getDieteStandard() {
        return dietaService.getDieteStandard();
    }

    // Visualizza una dieta standard specifica per ID

    @GetMapping("/standard/{id}")
    public DietaDTO getDietaStandardById(@PathVariable Long id) {
        return dietaService.getDietaStandardById(id);
    }

    // ===== CREAZIONE DIETA CUSTOM =====

    // Utente crea una dieta custom basata su un template standard

    @PostMapping("/standard/{dietaStandardId}/custom")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ADMIN') or hasAnyAuthority('PIANO_PREMIUM', 'PIANO_GOLD', 'PIANO_SILVER')")
    public DietaDTO creaDialogDietaCustom(
            @PathVariable Long dietaStandardId,
            @RequestBody DietaCreateRequestDTO request,
            @AuthenticationPrincipal Utente utente
    ) {
        return dietaService.creaDialogDietaCustom(dietaStandardId, request, utente);
    }

    // ===== GESTIONE DIETE CUSTOM DELL'UTENTE =====

    // Utente crea una dieta custom basata su un template standard

    @GetMapping("/custom")
    @PreAuthorize("hasAuthority('ADMIN') or hasAnyAuthority('PIANO_PREMIUM', 'PIANO_GOLD', 'PIANO_SILVER')")
    public List<DietaDTO> getDieteCustomUtente(
            @AuthenticationPrincipal Utente utente
    ) {
        return dietaService.getDieteCustomUtente(utente);
    }

    // Visualizza una dieta custom specifica dell'utente

    @GetMapping("/custom/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAnyAuthority('PIANO_PREMIUM', 'PIANO_GOLD', 'PIANO_SILVER')")
    public DietaDTO getDietaCustomUtenteById(
            @PathVariable Long id,
            @AuthenticationPrincipal Utente utente
    ) {
        return dietaService.getDietaCustomUtenteById(id, utente);
    }

    // Utente attiva/disattiva una sua dieta custom

    @PatchMapping("/custom/{id}/attiva")
    @PreAuthorize("hasAuthority('ADMIN') or hasAnyAuthority('PIANO_PREMIUM', 'PIANO_GOLD', 'PIANO_SILVER')")
    public DietaDTO setDietaAttiva(
            @PathVariable Long id,
            @RequestBody DietaSetAttivaRequestDTO request,
            @AuthenticationPrincipal Utente utente
    ) {
        return dietaService.setDietaAttiva(id, request.isAttiva(), utente);
    }

    // Utente elimina una sua dieta custom

    @DeleteMapping("/custom/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('ADMIN') or hasAnyAuthority('PIANO_PREMIUM', 'PIANO_GOLD', 'PIANO_SILVER')")
    public void eliminaDietaCustom(
            @PathVariable Long id,
            @AuthenticationPrincipal Utente utente
    ) {
        dietaService.eliminaDietaCustom(id, utente);
    }
}

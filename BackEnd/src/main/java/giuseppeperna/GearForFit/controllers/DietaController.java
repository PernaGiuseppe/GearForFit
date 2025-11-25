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
    public ResponseEntity<List<DietaDTO>> getDieteStandard() {
        List<DietaDTO> diete = dietaService.getDieteStandard();
        return ResponseEntity.ok(diete);
    }

    // Visualizza una dieta standard specifica per ID

    @GetMapping("/standard/{id}")
    public ResponseEntity<DietaDTO> getDietaStandardById(@PathVariable Long id) {
        DietaDTO dieta = dietaService.getDietaStandardById(id);
        return ResponseEntity.ok(dieta);
    }

    // ===== CREAZIONE DIETA CUSTOM =====

    // Utente crea una dieta custom basata su un template standard

    @PostMapping("/standard/{dietaStandardId}/custom")
    @PreAuthorize("hasAuthority('ADMIN') or hasAnyAuthority('PIANO_PREMIUM', 'PIANO_GOLD', 'PIANO_SILVER')")
    public ResponseEntity<DietaDTO> creaDialogDietaCustom(
            @PathVariable Long dietaStandardId,
            @RequestBody DietaCreateRequestDTO request,
            @AuthenticationPrincipal Utente utente
    ) {
        DietaDTO dietaDTO = dietaService.creaDialogDietaCustom(dietaStandardId, request, utente);
        return ResponseEntity.status(HttpStatus.CREATED).body(dietaDTO);
    }

    // ===== GESTIONE DIETE CUSTOM DELL'UTENTE =====

    // Utente crea una dieta custom basata su un template standard

    @GetMapping("/custom")
    @PreAuthorize("hasAuthority('ADMIN') or hasAnyAuthority('PIANO_PREMIUM', 'PIANO_GOLD', 'PIANO_SILVER')")
    public ResponseEntity<List<DietaDTO>> getDieteCustomUtente(
            @AuthenticationPrincipal Utente utente
    ) {
        List<DietaDTO> diete = dietaService.getDieteCustomUtente(utente);
        return ResponseEntity.ok(diete);
    }

    // Visualizza una dieta custom specifica dell'utente

    @GetMapping("/custom/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAnyAuthority('PIANO_PREMIUM', 'PIANO_GOLD', 'PIANO_SILVER')")
    public ResponseEntity<DietaDTO> getDietaCustomUtenteById(
            @PathVariable Long id,
            @AuthenticationPrincipal Utente utente
    ) {
        DietaDTO dieta = dietaService.getDietaCustomUtenteById(id, utente);
        return ResponseEntity.ok(dieta);
    }

    // Utente attiva/disattiva una sua dieta custom

    @PatchMapping("/custom/{id}/attiva")
    @PreAuthorize("hasAuthority('ADMIN') or hasAnyAuthority('PIANO_PREMIUM', 'PIANO_GOLD', 'PIANO_SILVER')")
    public ResponseEntity<DietaDTO> setDietaAttiva(
            @PathVariable Long id,
            @RequestBody DietaSetAttivaRequestDTO request,
            @AuthenticationPrincipal Utente utente
    ) {
        DietaDTO dieta = dietaService.setDietaAttiva(id, request.isAttiva(), utente);
        return ResponseEntity.ok(dieta);
    }

    // Utente elimina una sua dieta custom

    @DeleteMapping("/custom/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAnyAuthority('PIANO_PREMIUM', 'PIANO_GOLD', 'PIANO_SILVER')")
    public ResponseEntity<Void> eliminaDietaCustom(
            @PathVariable Long id,
            @AuthenticationPrincipal Utente utente
    ) {
        dietaService.eliminaDietaCustom(id, utente);
        return ResponseEntity.noContent().build();
    }
}

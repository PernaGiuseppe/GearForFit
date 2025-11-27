package giuseppeperna.GearForFit.controllers;

import giuseppeperna.GearForFit.entities.Diete.CalcoloBMR;
import giuseppeperna.GearForFit.entities.Diete.TipoDieta;
import giuseppeperna.GearForFit.entities.Utente.Utente;
import giuseppeperna.GearForFit.payloads.*;
import giuseppeperna.GearForFit.services.CalcoloBMRService;
import giuseppeperna.GearForFit.services.DietaService;
import giuseppeperna.GearForFit.services.UtenteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/utenti")
@PreAuthorize("isAuthenticated()")
public class UtenteController {

    @Autowired
    private UtenteService utenteService;
    @Autowired
    private CalcoloBMRService calcoloBMRService;
    @Autowired
    private DietaService dietaService;


    @GetMapping("/me")
    public UtenteDTO getMeUtente(@AuthenticationPrincipal Utente utente) {
        return utenteService.getUtenteDTO(utente.getId());
    }

    @PutMapping("/me")
    public UtenteDTO aggiornaMe(
            @RequestBody @Valid AggiornaProfiloDTO body,
            @AuthenticationPrincipal Utente utente) {
        return utenteService.aggiornaUtenteDTO(utente.getId(), body);
    }
    @PutMapping("/me/password")
    @ResponseStatus(HttpStatus.OK)
    public String cambiaPassword(
        @RequestBody @Valid CambiaPasswordDTO body,
        @AuthenticationPrincipal Utente utente) {
      utenteService.cambiaPassword(utente.getId(), body.passwordVecchia(), body.passwordNuova());
       return "Password cambiata con successo"; // Semplice stringa invece di MessageResponseDTO
}
    // Elimina il proprio account
    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminaMioAccount(@AuthenticationPrincipal Utente utente) {
        utenteService.eliminaUtente(utente.getId());
    }
}

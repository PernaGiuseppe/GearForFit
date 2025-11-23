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

    // Ottieni i dati dell'utente loggato
 /*   @GetMapping("/me")
    public Utente getMeUtente(Authentication authentication) {
        Utente utenteLoggato = (Utente) authentication.getPrincipal();
        return utenteService.findById(utenteLoggato.getId());
    }*/
    @GetMapping("/me")
    public UtenteDTO getMeUtente(@AuthenticationPrincipal Utente utente) {
        return utenteService.getUtenteDTO(utente.getId());
    }

    // Aggiorna i dati dell'utente
  /*  @PutMapping("/me")
    public Utente aggiornaMe(@RequestBody AggiornaProfiloDTO body, Authentication authentication) {
        Utente utenteLoggato = (Utente) authentication.getPrincipal();
        return utenteService.aggiornaUtente(utenteLoggato.getId(), body.nome(), body.cognome(), body.email());
    }*/
    @PutMapping("/me")
    public UtenteDTO aggiornaMe(
            @RequestBody @Valid AggiornaProfiloDTO body,
            @AuthenticationPrincipal Utente utente) {
        return utenteService.aggiornaUtenteDTO(utente.getId(), body);
    }

/*    // Cambia la password dell'utente
    @PutMapping("/me/password")
    public Utente cambiaPassword(@RequestBody CambiaPasswordDTO body, Authentication authentication) {
        Utente utenteLoggato = (Utente) authentication.getPrincipal();
        return utenteService.cambiaPassword(utenteLoggato.getId(), body.passwordVecchia(), body.passwordNuova());
    }*/

@PutMapping("/me/password")
@ResponseStatus(HttpStatus.OK)
public String cambiaPassword(
        @RequestBody @Valid CambiaPasswordDTO body,
        @AuthenticationPrincipal Utente utente) {
    utenteService.cambiaPassword(utente.getId(), body.passwordVecchia(), body.passwordNuova());
    return "Password cambiata con successo"; // Semplice stringa invece di MessageResponseDTO
}

 /*   // Elimina il proprio account
    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminaMioAccount(Authentication authentication) {
        Utente utenteLoggato = (Utente) authentication.getPrincipal();
        utenteService.eliminaUtente(utenteLoggato.getId());
    }*/
    // Elimina il proprio account
    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminaMioAccount(@AuthenticationPrincipal Utente utente) {
        utenteService.eliminaUtente(utente.getId());
    }
}

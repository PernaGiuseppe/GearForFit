package giuseppeperna.GearForFit.controllers;

import giuseppeperna.GearForFit.entities.Utente;
import giuseppeperna.GearForFit.payloads.AggiornaProfiloDTO;
import giuseppeperna.GearForFit.payloads.CambiaPasswordDTO;
import giuseppeperna.GearForFit.services.UtenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/utenti")
@PreAuthorize("isAuthenticated()")
public class UtenteController {

    @Autowired
    private UtenteService utenteService;

    // Ottieni i dati dell'utente loggato
    @GetMapping("/me")
    public Utente getMeUtente(Authentication authentication) {
        Utente utenteLoggato = (Utente) authentication.getPrincipal();
        return utenteService.findById(utenteLoggato.getId());
    }

    // Aggiorna i dati dell'utente
    @PutMapping("/me")
    public Utente aggiornaMe(@RequestBody AggiornaProfiloDTO body, Authentication authentication) {
        Utente utenteLoggato = (Utente) authentication.getPrincipal();
        return utenteService.aggiornaUtente(utenteLoggato.getId(), body.nome(), body.cognome(), body.email());
    }

    // Cambia la password dell'utente
    @PutMapping("/me/password")
    public Utente cambiaPassword(@RequestBody CambiaPasswordDTO body, Authentication authentication) {
        Utente utenteLoggato = (Utente) authentication.getPrincipal();
        return utenteService.cambiaPassword(utenteLoggato.getId(), body.passwordVecchia(), body.passwordNuova());
    }

    // Elimina il proprio account
    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminaMioAccount(Authentication authentication) {
        Utente utenteLoggato = (Utente) authentication.getPrincipal();
        utenteService.eliminaUtente(utenteLoggato.getId());
    }
}

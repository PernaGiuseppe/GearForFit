package giuseppeperna.GearForFit.controllers;

import giuseppeperna.GearForFit.entities.Utente;
import giuseppeperna.GearForFit.payloads.CambiaPasswordDTO;
import giuseppeperna.GearForFit.services.UtenteService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public Utente aggiornaMe(
            @RequestParam String nome,
            @RequestParam String cognome,
            @RequestParam String email,
            Authentication authentication) {

        Utente utenteLoggato = (Utente) authentication.getPrincipal();
        return utenteService.aggiornaUtente(utenteLoggato.getId(), nome, cognome, email);
    }

    // Cambia la password dell'utente
    @PutMapping("/me/cambiaPassword")
    public Utente cambiaPassword(
            @RequestBody CambiaPasswordDTO body,
            Authentication authentication) {

        Utente utenteLoggato = (Utente) authentication.getPrincipal();
        return utenteService.cambiaPassword(utenteLoggato.getId(), body.passwordVecchia(), body.passwordNuova());
    }
}

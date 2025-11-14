package giuseppeperna.GearForFit.controllers;

import giuseppeperna.GearForFit.entities.Diete.CalcoloBMR;
import giuseppeperna.GearForFit.entities.Diete.TipoDieta;
import giuseppeperna.GearForFit.entities.Utente.Utente;
import giuseppeperna.GearForFit.payloads.AggiornaProfiloDTO;
import giuseppeperna.GearForFit.payloads.CalcoloBMRDTO;
import giuseppeperna.GearForFit.payloads.CambiaPasswordDTO;
import giuseppeperna.GearForFit.payloads.DietaStandardDTO;
import giuseppeperna.GearForFit.services.CalcoloBMRService;
import giuseppeperna.GearForFit.services.DietaService;
import giuseppeperna.GearForFit.services.UtenteService;
import jakarta.validation.Valid;
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
    @Autowired
    private CalcoloBMRService calcoloBMRService;
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
    @Autowired
    private DietaService dietaService;

    // Salva/aggiorna calcolo BMR personale
    @PostMapping("/me/bmr")
    public CalcoloBMR salvaBMR(@RequestBody @Valid CalcoloBMRDTO body, Authentication authentication) {
        Utente utenteLoggato = (Utente) authentication.getPrincipal();
        return dietaService.salvaCalcoloBMR(utenteLoggato.getId(), body);
    }

    // Ottieni la propria dieta personalizzata
    @GetMapping("/me/dieta")
    public DietaStandardDTO getMiaDieta(
            @RequestParam TipoDieta tipoDieta,
            Authentication authentication) {
        Utente utenteLoggato = (Utente) authentication.getPrincipal();
        CalcoloBMR bmr = calcoloBMRService.getCalcoloBMRByUtente(utenteLoggato.getId());
        return dietaService.generaDietaStandardPersonalizzata(bmr, tipoDieta);
    }

    @PostMapping("/me/dieta")
    @ResponseStatus(HttpStatus.CREATED)
    public DietaStandardDTO assegnaEMostraMiaDieta(
            @RequestBody TipoDietaRequestDTO body, // Prendiamo il tipo dieta dal body
            Authentication authentication) {
        Utente utenteLoggato = (Utente) authentication.getPrincipal();

        return dietaService.assegnaDietaAdUtente(utenteLoggato, body.tipoDieta());
    }

    // DTO per la richiesta nel body
    public record TipoDietaRequestDTO(TipoDieta tipoDieta) {}
}

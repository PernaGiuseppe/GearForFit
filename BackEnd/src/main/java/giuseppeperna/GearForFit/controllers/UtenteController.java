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

    // Salva/aggiorna calcolo BMR personale
    @PostMapping("/me/bmr")
    public CalcoloBMR salvaBMR(@RequestBody @Valid CalcoloBMRDTO body, Authentication authentication) {
        Utente utenteLoggato = (Utente) authentication.getPrincipal();
        return dietaService.salvaCalcoloBMR(utenteLoggato.getId(), body);
    }

    // Ottieni la propria dieta personalizzata

    @PostMapping("/me/dieta")
    @ResponseStatus(HttpStatus.CREATED)
    public DietaStandardDTO assegnaEMostraMiaDieta(
            @RequestBody TipoDietaRequestDTO body, // Prendiamo il tipo dieta dal body
            Authentication authentication) {
        Utente utenteLoggato = (Utente) authentication.getPrincipal();

        return dietaService.assegnaDietaAdUtente(utenteLoggato, body.tipoDieta());
    }
    @GetMapping("/me/dieta/genera")
    public DietaStandardDTO generaMiaDietaPreview(
            @RequestParam TipoDieta tipoDieta, // 'tipoDieta' è l'obiettivo
            Authentication authentication) {
        Utente utenteLoggato = (Utente) authentication.getPrincipal();
        // Prende il BMR salvato
        CalcoloBMR bmr = calcoloBMRService.getCalcoloBMRByUtente(utenteLoggato.getId());
        // Genera il DTO scalato
        return dietaService.generaDietaStandardPersonalizzata(bmr, tipoDieta);
    }

    @GetMapping("/me/dieta")
    public List<DietaUtenteDTO> getMieDieteAssegnate(Authentication authentication) {
        Utente utenteLoggato = (Utente) authentication.getPrincipal();
        return dietaService.getDieteAssegnate(utenteLoggato);
    }

    @GetMapping("/me/dieta/{id}")
    public DietaStandardDTO getMiaDietaAssegnataById(
            @PathVariable Long id,
            Authentication authentication) {
        Utente utenteLoggato = (Utente) authentication.getPrincipal();
        return dietaService.getDietaAssegnataScalata(id, utenteLoggato);
    }
    @PutMapping("/me/dieta/{id}/attiva")
    public DietaUtenteDTO setMiaDietaAttiva(
            @PathVariable Long id,
            Authentication authentication) {
        Utente utenteLoggato = (Utente) authentication.getPrincipal();
        return dietaService.setDietaAttiva(id, utenteLoggato);
    }
    @DeleteMapping("/me/dieta/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminaMiaDietaAssegnata(
            @PathVariable Long id,
            Authentication authentication) {
        Utente utenteLoggato = (Utente) authentication.getPrincipal();
        dietaService.eliminaDietaAssegnata(id, utenteLoggato);
    }

    // DTO per la richiesta nel body
    public record TipoDietaRequestDTO(TipoDieta tipoDieta) {}
}

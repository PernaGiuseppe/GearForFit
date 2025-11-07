package giuseppeperna.GearForFit.controllers;

import giuseppeperna.GearForFit.entities.TipoPiano;
import giuseppeperna.GearForFit.entities.TipoUtente;
import giuseppeperna.GearForFit.entities.Utente;
import giuseppeperna.GearForFit.services.UtenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminController {

    @Autowired
    private UtenteService utenteService;

    // Ottieni tutti gli utenti
    @GetMapping("/utenti")
    public List<Utente> getAllUtenti() {
        return utenteService.getAllUtenti();
    }

    // Ottieni un utente specifico
    @GetMapping("/utenti/{id}")
    public Utente getUtente(@PathVariable Long id) {
        return utenteService.findById(id);
    }

    // Crea un nuovo utente (ADMIN crea gli utenti)
    @PostMapping("/utenti")
    @ResponseStatus(HttpStatus.CREATED)
    public Utente creaUtente(
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String nome,
            @RequestParam String cognome,
            @RequestParam(defaultValue = "UTENTE") TipoUtente tipoUtente) {

        return utenteService.creaUtente(email, password, nome, cognome, tipoUtente);
    }

    // Cambia il ruolo di un utente
    @PutMapping("/utenti/{id}/ruolo")
    public Utente cambiaRuolo(
            @PathVariable Long id,
            @RequestParam TipoUtente nuovoRuolo) {

        return utenteService.cambiaRuolo(id, nuovoRuolo);
    }

    // ← CORRETTO: Cambia il piano di un utente (usa Service)
    @PutMapping("/utenti/{id}/piano")
    public Utente cambiaPiano(
            @PathVariable Long id,
            @RequestParam TipoPiano nuovoPiano) {

        return utenteService.cambiaPiano(id, nuovoPiano);
    }

    // Disattiva un utente
    @PutMapping("/utenti/{id}/disattiva")
    public Utente disattivaUtente(@PathVariable Long id) {
        return utenteService.disattivaUtente(id);
    }

    // Attiva un utente
    @PutMapping("/utenti/{id}/attiva")
    public Utente attivaUtente(@PathVariable Long id) {
        return utenteService.attivaUtente(id);
    }

    // Elimina un utente
    @DeleteMapping("/utenti/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminaUtente(@PathVariable Long id) {
        utenteService.eliminaUtente(id);
    }
}

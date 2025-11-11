package giuseppeperna.GearForFit.controllers;

import giuseppeperna.GearForFit.entities.Utente;
import giuseppeperna.GearForFit.exceptions.UnauthorizedException;
import giuseppeperna.GearForFit.payloads.QeAResponseDTO;
import giuseppeperna.GearForFit.services.QeAService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/qea")
public class QeAController {

    @Autowired
    private QeAService qeaService;

    // Ottieni tutte le Q&A (solo utenti PREMIUM o ADMIN)
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<QeAResponseDTO> getAllQeA(Authentication authentication) {
        Utente utenteLoggato = (Utente) authentication.getPrincipal();

        // Verifica che l'utente sia PREMIUM o ADMIN
        if (!utenteLoggato.getTipoPiano().name().equals("PREMIUM") &&
                !utenteLoggato.getTipoUtente().name().equals("ADMIN")) {
            throw new UnauthorizedException("Solo gli utenti Premium possono accedere a questa funzionalità");
        }

        return qeaService.getAllQeA();
    }

    // Ottieni una singola Q&A (solo PREMIUM o ADMIN)
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public QeAResponseDTO getQeAById(@PathVariable Long id, Authentication authentication) {
        Utente utenteLoggato = (Utente) authentication.getPrincipal();

        if (!utenteLoggato.getTipoPiano().name().equals("PREMIUM") &&
                !utenteLoggato.getTipoUtente().name().equals("ADMIN")) {
            throw new UnauthorizedException("Solo gli utenti Premium possono accedere a questa funzionalità");
        }

        return qeaService.getQeAById(id);
    }
}

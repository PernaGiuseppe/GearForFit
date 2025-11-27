package giuseppeperna.GearForFit.controllers;

import giuseppeperna.GearForFit.entities.Utente.TipoUtente;
import giuseppeperna.GearForFit.entities.Utente.Utente;
import giuseppeperna.GearForFit.exceptions.BadRequestException;
import giuseppeperna.GearForFit.payloads.LoginRequestDTO;
import giuseppeperna.GearForFit.payloads.LoginResponseDTO;
import giuseppeperna.GearForFit.payloads.RegistrazioneRequestDTO;
import giuseppeperna.GearForFit.security.JwtTools;
import giuseppeperna.GearForFit.services.UtenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UtenteService utenteService;

    @Autowired
    private JwtTools jwtTools;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Login
    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody @Validated LoginRequestDTO body) {
        Utente utente = utenteService.findByEmail(body.email());
        if (!passwordEncoder.matches(body.password(), utente.getPassword())) {
            throw new BadRequestException("Credenziali non valide");
        }

        String token = jwtTools.createToken(utente);
        return new LoginResponseDTO(
                token,
                utente.getId(),
                utente.getEmail(),
                utente.getNome(),
                utente.getCognome(),
                utente.getTipoUtente(),
                utente.getTipoPiano()
        );
    }

    // Registrazione pubblica (utente si registra da solo)
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public LoginResponseDTO register(@RequestBody @Validated RegistrazioneRequestDTO body, BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            List<String> errorMessages = validationResult.getFieldErrors().stream()
                    .map(fieldError -> fieldError.getField() + " :" + fieldError.getDefaultMessage())
                    .toList();
            throw new BadRequestException(String.join(", ", errorMessages));
        }

        // Crea utente con piano FREE e ruolo USER di default
        Utente nuovoUtente = utenteService.creaUtente(
                body.email(),
                body.password(),
                body.nome(),
                body.cognome(),
                TipoUtente.USER
        );

        // Genera token e restituisci come al login
        String token = jwtTools.createToken(nuovoUtente);

        return new LoginResponseDTO(
                token,
                nuovoUtente.getId(),
                nuovoUtente.getEmail(),
                nuovoUtente.getNome(),
                nuovoUtente.getCognome(),
                nuovoUtente.getTipoUtente(),
                nuovoUtente.getTipoPiano()
        );
    }
}

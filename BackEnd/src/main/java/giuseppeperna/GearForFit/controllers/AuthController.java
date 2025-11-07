package giuseppeperna.GearForFit.controllers;

import giuseppeperna.GearForFit.entities.Utente;
import giuseppeperna.GearForFit.exceptions.BadRequestException;
import giuseppeperna.GearForFit.payloads.LoginRequestDTO;
import giuseppeperna.GearForFit.payloads.LoginResponseDTO;
import giuseppeperna.GearForFit.security.JwtTools;
import giuseppeperna.GearForFit.services.UtenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

        return new LoginResponseDTO(token, utente.getId());
    }
}

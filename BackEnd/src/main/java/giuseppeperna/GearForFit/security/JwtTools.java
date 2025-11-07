package giuseppeperna.GearForFit.security;

import giuseppeperna.GearForFit.entities.Utente;
import giuseppeperna.GearForFit.exceptions.UnauthorizedException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTools {

    @Value("${jwt.secret}")
    private String secret;

    // Crea il token JWT per l'utente
    public String createToken(Utente utente) {
        return Jwts.builder()
                .issuedAt(new Date(System.currentTimeMillis()))
                // Aggiungi il ruolo dell'utente nel token
                .claim("ruolo", utente.getTipoUtente().name())
                // Token valido per 7 giorni
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7))
                // Subject è l'ID dell'utente
                .subject(String.valueOf(utente.getId()))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .compact();
    }

    // Verifica che il token sia valido
    public void verifyToken(String accessToken) {
        try {
            Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
                    .build()
                    .parse(accessToken);
        } catch (Exception ex) {
            throw new UnauthorizedException("Problemi con il token! Per favore effettua di nuovo il login!");
        }
    }

    // Estrae l'ID dell'utente dal token
    public String extractIdFromToken(String accessToken) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .build()
                .parseSignedClaims(accessToken)
                .getPayload()
                .getSubject();
    }
}

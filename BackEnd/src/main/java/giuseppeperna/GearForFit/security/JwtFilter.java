package giuseppeperna.GearForFit.security;

import giuseppeperna.GearForFit.entities.Utente.Utente;
import giuseppeperna.GearForFit.exceptions.UnauthorizedException;
import giuseppeperna.GearForFit.services.UtenteService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    // Percorsi che non richiedono autenticazione
    private static final List<String> EXCLUDED_PATHS = Arrays.asList(
            "/auth/**"
    );

    @Autowired
    private JwtTools jwtTools;

    @Autowired
    private UtenteService utenteService;

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver resolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        try {
            // Verifica che il token sia presente e nel formato corretto
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new UnauthorizedException("Inserire token nell'Authorization Header nel formato corretto!");
            }

            // Estrai il token dal header (rimuovi "Bearer ")
            String accessToken = authHeader.substring(7);

            // Verifica la validità del token
            jwtTools.verifyToken(accessToken);

            // Estrai l'ID dell'utente dal token
            String id = jwtTools.extractIdFromToken(accessToken);

            // Carica l'utente dal database
            Utente utenteCorrente = utenteService.findById(Long.parseLong(id));

            // Crea l'autenticazione con l'utente
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    utenteCorrente,
                    null,
                    utenteCorrente.getAuthorities()
            );

            // Imposta l'autenticazione nel SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Continua la catena dei filter
            filterChain.doFilter(request, response);

        } catch (UnauthorizedException ex) {
            // Gestisci l'eccezione di autorizzazione
            this.resolver.resolveException(request, response, null, ex);
        }
    }

    // Escludi i percorsi che non richiedono autenticazione
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        AntPathMatcher pathMatcher = new AntPathMatcher();
        return EXCLUDED_PATHS.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, path));
    }
}

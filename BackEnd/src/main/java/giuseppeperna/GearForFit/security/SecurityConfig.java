package giuseppeperna.GearForFit.config;

import giuseppeperna.GearForFit.security.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        // Disabilita login form (usiamo JWT)
        httpSecurity.formLogin(formLogin -> formLogin.disable());

        // Disabilita CSRF (non necessario con token JWT)
        httpSecurity.csrf(csrf -> csrf.disable());

        // Configura la gestione della sessione (stateless con JWT)
        httpSecurity.sessionManagement(sessions ->
                sessions.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        // Aggiungi il JWT filter prima dell'autenticazione username/password
        httpSecurity.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        // Configura le autorizzazioni dei percorsi
        httpSecurity.authorizeHttpRequests(req ->
                req.requestMatchers("/auth/**").permitAll()  // Percorsi pubblici
                        .requestMatchers("/esercizi/**").permitAll() // Esercizi visibili a tutti
                        .anyRequest().authenticated()  // Tutto il resto richiede autenticazione
        );

        // Abilita CORS
        httpSecurity.cors(Customizer.withDefaults());

        return httpSecurity.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Origine del frontend (modifica in base al tuo ambiente)
        configuration.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:3000"));

        // Metodi HTTP consentiti
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

        // Header consentiti
        configuration.setAllowedHeaders(List.of("*"));

        // Permetti le credenziali
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}

package giuseppeperna.GearForFit.entities.Utente;

import giuseppeperna.GearForFit.entities.SchedePalestra.SchedaAllenamento;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "utenti")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Utente implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String cognome;

    @Column(nullable = false)
    private Boolean attivo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoUtente tipoUtente;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoPiano tipoPiano;

    @OneToMany(mappedBy = "utente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SchedaAllenamento> schedeAllenamento;

    @Column(name = "data_creazione", nullable = false, updatable = false)
    private LocalDateTime dataCreazione;

    @PrePersist
    protected void onCreate() {
        this.dataCreazione = LocalDateTime.now();
    }

    // Dentro la classe Utente.java

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(
                new SimpleGrantedAuthority(tipoUtente.name()), // Es. "ADMIN"
                new SimpleGrantedAuthority("PIANO_" + tipoPiano.name()) // Es. "PIANO_PREMIUM"
        );
    }


    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return attivo;
    }

}

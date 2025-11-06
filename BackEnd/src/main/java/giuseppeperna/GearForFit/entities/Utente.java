package giuseppeperna.GearForFit.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "utenti")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Utente {

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
    private Ruolo ruolo;

    @OneToMany(mappedBy = "utente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SchedaAllenamento> schedeAllenamento;

    @Column(name = "data_creazione", nullable = false, updatable = false)
    private java.time.LocalDateTime dataCreazione;

    @PrePersist
    protected void onCreate() {
        this.dataCreazione = java.time.LocalDateTime.now();
    }

    public enum Ruolo {
        ADMIN,
        UTENTE
    }
}


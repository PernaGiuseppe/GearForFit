package giuseppeperna.GearForFit.entities.SchedePalestra;

import giuseppeperna.GearForFit.entities.Utente.Utente;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "schede_allenamento")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SchedaAllenamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(columnDefinition = "TEXT")
    private String descrizione;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ObiettivoAllenamento obiettivo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GiornoSettimana giornoSettimana;

    @Column(nullable = false)
    private boolean isStandard = false; // AGGIUNGI SOLO QUESTO

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utente_id", nullable = false)
    private Utente utente;

    @OneToMany(mappedBy = "schedaAllenamento", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("posizione ASC")
    private List<EsercizioScheda> esercizi;

    @Column(name = "data_creazione", nullable = false, updatable = false)
    private LocalDateTime dataCreazione;

    @Column(name = "data_aggiornamento")
    private LocalDateTime dataAggiornamento;

    @PrePersist
    protected void onCreate() {
        this.dataCreazione = LocalDateTime.now();
        this.dataAggiornamento = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.dataAggiornamento = LocalDateTime.now();
    }
}

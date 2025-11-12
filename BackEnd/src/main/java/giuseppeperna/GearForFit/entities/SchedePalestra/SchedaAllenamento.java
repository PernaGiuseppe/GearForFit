package giuseppeperna.GearForFit.entities.SchedePalestra;

import giuseppeperna.GearForFit.entities.Utente.Utente;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "scheda_allenamento")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SchedaAllenamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    private String descrizione;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ObiettivoAllenamento obiettivo;

    @Enumerated(EnumType.STRING)
    private TipoAllenamento tipoAllenamento;

    @Column(name = "frequenza_settimanale")
    private Integer frequenzaSettimanale;

    @Column(name = "durata_settimane")
    private Integer durataSettimane;

    @Column(name = "livello_esperienza")
    private String livelloEsperienza;

    // ========== NUOVI CAMPI ==========
    @Column(name = "is_standard", nullable = false)
    private Boolean isStandard = false;

    @ManyToOne
    @JoinColumn(name = "utente_id")
    private Utente utente;
    // ================================

    @OneToMany(mappedBy = "scheda", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EsercizioScheda> esercizi = new ArrayList<>();

    @Column(name = "data_creazione")
    private LocalDateTime dataCreazione;

    @PrePersist
    protected void onCreate() {
        this.dataCreazione = LocalDateTime.now();
    }
}

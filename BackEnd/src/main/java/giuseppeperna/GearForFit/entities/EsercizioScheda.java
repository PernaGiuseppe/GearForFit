package giuseppeperna.GearForFit.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "esercizi_scheda")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EsercizioScheda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scheda_allenamento_id", nullable = false)
    private SchedaAllenamento schedaAllenamento;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "esercizio_id", nullable = false)
    private Esercizio esercizio;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "schema_serie_id", nullable = false)
    private SchemaSerie schemaSerie;

    @Column(nullable = false)
    private Integer posizione;

    @Column(nullable = false)
    private Integer secondiRiposo;

    @Column(columnDefinition = "TEXT")
    private String note;
}


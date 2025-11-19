package giuseppeperna.GearForFit.entities.SchedePalestra;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "esercizio_scheda")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class EsercizioScheda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "scheda_id", nullable = false)
    private SchedaAllenamento scheda;

    @ManyToOne
    @JoinColumn(name = "esercizio_id", nullable = false)
    private Esercizio esercizio;

    @ManyToOne
    @JoinColumn(name = "schema_serie_id")
    private SchemaSerie schemaSerie;

    @Enumerated(EnumType.STRING)
    @Column(name = "giorno_settimana")  // ✅ QUESTO È IL GIORNO!
    private GiornoSettimana giornoSettimana;

    @Column(name = "ordine_esecuzione")
    private Integer ordineEsecuzione;

    private String note;
}



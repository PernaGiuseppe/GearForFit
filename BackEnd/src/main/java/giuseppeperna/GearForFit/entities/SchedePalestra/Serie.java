package giuseppeperna.GearForFit.entities.SchedePalestra;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "serie")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Serie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "giorno_id", nullable = false)
    private GiornoAllenamento giorno;

    @ManyToOne
    @JoinColumn(name = "esercizio_id", nullable = false)
    private Esercizio esercizio;  // <-- Sostituisci con il nome corretto della tua entity

    @Column(name = "numero_serie", nullable = false)
    private Integer numeroSerie;

    @Column(name = "numero_ripetizioni", nullable = false)
    private Integer numeroRipetizioni;

    @Column(name = "tempo_recupero_secondi")
    private Integer tempoRecuperoSecondi;
}

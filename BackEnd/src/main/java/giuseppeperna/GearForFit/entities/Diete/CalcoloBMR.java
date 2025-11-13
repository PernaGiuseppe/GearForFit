package giuseppeperna.GearForFit.entities.Diete;

import giuseppeperna.GearForFit.entities.Utente.Utente;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "calcoli_bmr")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalcoloBMR {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "utente_id", nullable = false, unique = true)
    private Utente utente;

    @Column(nullable = false)
    private Double peso; // kg

    @Column(nullable = false)
    private Double altezza; // cm

    @Column(nullable = false)
    private Integer eta;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Sesso sesso;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoDieta tipoDieta;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LivelloAttivita livelloAttivita;

    @Column(nullable = false)
    private Double bmrCalcolato;

    @Column(nullable = false)
    private Double fabbisognoCaloricoGiornaliero;

    public enum Sesso {
        M, F
    }

    public enum LivelloAttivita {
        SEDENTARIO, LEGGERO, MODERATO, INTENSO
    }
}

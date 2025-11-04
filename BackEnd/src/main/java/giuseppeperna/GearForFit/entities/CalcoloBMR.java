package giuseppeperna.GearForFit.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString

public class CalcoloBMR {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;
    private Double peso; // kg
    private Double altezza; // cm
    private Integer eta;
    private String sesso; // M/F
    private String obiettivo; // PERDITA_PESO, DEFINIZIONE, MANTENIMENTO, MASSA
    private String livelloAttivita; // SEDENTARIO, LEGGERO, MODERATO, INTENSO
    private Double bmrCalcolato;
    private Double fabbisognoCaloricoGiornaliero;
}


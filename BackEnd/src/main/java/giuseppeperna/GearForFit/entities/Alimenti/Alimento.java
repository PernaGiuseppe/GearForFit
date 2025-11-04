package giuseppeperna.GearForFit.entities.Alimenti;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_alimento", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class Alimento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    private String nome;
    private Double caloriePer100g;
    private Double proteinePer100g;
    private Double carboidratiPer100g;
    private Double grassiPer100g;
    private Double fibrePer100g;
    private String fonte; // "CREA", "CUSTOM"

}



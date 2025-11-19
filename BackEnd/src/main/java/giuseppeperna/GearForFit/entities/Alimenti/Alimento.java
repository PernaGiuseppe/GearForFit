package giuseppeperna.GearForFit.entities.Alimenti;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "alimenti")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
        name = "tipo_alimento",
        discriminatorType = DiscriminatorType.STRING
)
@DiscriminatorValue("ALIMENTO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class Alimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(nullable = false)
    private String tipologia = "Alimento";

    @Column(nullable = false, unique = true)
    private String nome;

    @Column(name = "calorie_per_100g")
    private double caloriePer100g;

    @Column(name = "proteine_per_100g")
    private double proteinePer100g;

    @Column(name = "carboidrati_per_100g")
    private double carboidratiPer100g;

    @Column(name = "grassi_per_100g")
    private double grassiPer100g;

    @Column(name = "fibre_per_100g")
    private double fibrePer100g;
}

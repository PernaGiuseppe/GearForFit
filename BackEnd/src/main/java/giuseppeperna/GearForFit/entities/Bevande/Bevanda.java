package giuseppeperna.GearForFit.entities.Bevande;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bevande")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
        name = "tipo_bevanda",
        discriminatorType = DiscriminatorType.STRING
)
@DiscriminatorValue("BEVANDA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class Bevanda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(nullable = false)
    private String tipologia = "Bevanda";

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

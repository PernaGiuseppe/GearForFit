package giuseppeperna.GearForFit.entities.Alimenti;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("FRUTTA")
public class Frutta extends Alimento {
    private String stagione;
    private Integer zuccheriTotali;
}

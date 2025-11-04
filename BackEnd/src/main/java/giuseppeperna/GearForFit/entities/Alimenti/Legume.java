package giuseppeperna.GearForFit.entities.Alimenti;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("LEGUME")
public class Legume extends Alimento {
    private Boolean secco;
    private Integer tempoAmmollo;
}

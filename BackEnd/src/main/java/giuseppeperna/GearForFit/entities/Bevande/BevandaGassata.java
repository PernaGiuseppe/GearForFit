package giuseppeperna.GearForFit.entities.Bevande;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("GASSATA")
public class BevandaGassata extends Bevanda {
    private Boolean diet;
    private Integer caffeina;
}

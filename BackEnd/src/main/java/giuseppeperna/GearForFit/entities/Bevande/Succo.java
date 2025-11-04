package giuseppeperna.GearForFit.entities.Bevande;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("SUCCO")
public class Succo extends Bevanda {
    private Integer percentualeFrutta;
    private Boolean zuccheriAggiunti;
}

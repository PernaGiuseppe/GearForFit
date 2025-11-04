package giuseppeperna.GearForFit.entities.Alimenti;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("LATTICINO")
public class Latticino extends Alimento {
    private Boolean lattosio;
    private Double percentualeGrassi;
}

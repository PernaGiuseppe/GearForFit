package giuseppeperna.GearForFit.entities.Bevande;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ANALCOLICO")
public class BevandaAnalcolica extends Bevanda {

}

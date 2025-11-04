package giuseppeperna.GearForFit.entities.Bevande;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ACQUA")
public class Acqua extends Bevanda {
    private String tipologia; // NATURALE, FRIZZANTE
    private Boolean mineralizzata;
}

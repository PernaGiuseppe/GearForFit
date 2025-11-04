package giuseppeperna.GearForFit.entities.Alimenti;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("CARNE")
public class Carne extends Alimento {
    private String tipoCarne; // BIANCA, ROSSA
    private String taglio;
}

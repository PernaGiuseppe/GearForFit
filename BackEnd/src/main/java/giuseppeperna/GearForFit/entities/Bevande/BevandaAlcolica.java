package giuseppeperna.GearForFit.entities.Bevande;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ALCOLICO")
public class BevandaAlcolica extends Bevanda {
    private Double gradazioneAlcolica;
}
package giuseppeperna.GearForFit.entities.Alimenti;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("CARBOIDRATO")
public class Carboidrato extends Alimento {
    private String tipoGrano;
    private Integer indiceGlicemico;
}

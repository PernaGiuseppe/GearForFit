package giuseppeperna.GearForFit.payloads;

import giuseppeperna.GearForFit.entities.Diete.CalcoloBMR;
import giuseppeperna.GearForFit.entities.Diete.LivelloAttivita;
import giuseppeperna.GearForFit.entities.Diete.Sesso;
import giuseppeperna.GearForFit.entities.Diete.TipoDieta;

public record CalcoloBMRDTO(
        double peso,
        double altezza,
        int eta,
        Sesso sesso,
        LivelloAttivita livelloAttivita,
        TipoDieta tipoDieta
){}


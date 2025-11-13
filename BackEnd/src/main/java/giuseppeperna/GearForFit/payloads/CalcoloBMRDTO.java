package giuseppeperna.GearForFit.payloads;

import giuseppeperna.GearForFit.entities.Diete.CalcoloBMR;
import giuseppeperna.GearForFit.entities.Diete.TipoDieta;

public record CalcoloBMRDTO(
        double peso,
        double altezza,
        int eta,
        CalcoloBMR.Sesso sesso,
        CalcoloBMR.LivelloAttivita livelloAttivita,
        TipoDieta tipoDieta
){}


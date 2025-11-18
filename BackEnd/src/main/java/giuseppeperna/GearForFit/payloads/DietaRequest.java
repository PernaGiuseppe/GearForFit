package giuseppeperna.GearForFit.payloads;

import giuseppeperna.GearForFit.entities.Diete.TipoDieta;

public record DietaRequest(
        CalcoloBMRDTO calcoloBMR,
        TipoDieta tipoDieta
){}

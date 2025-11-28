package giuseppeperna.GearForFit.payloads;

import giuseppeperna.GearForFit.entities.Diete.LivelloAttivita;
import giuseppeperna.GearForFit.entities.Diete.Sesso;
import giuseppeperna.GearForFit.entities.Diete.TipoDieta;

public record DietaCreateRequestDTO(
        String nome,
        String descrizione,
        Double peso,
        Double altezza,
        Integer eta,
        Sesso sesso,
        LivelloAttivita livelloAttivita,
        TipoDieta tipoDieta
) {}

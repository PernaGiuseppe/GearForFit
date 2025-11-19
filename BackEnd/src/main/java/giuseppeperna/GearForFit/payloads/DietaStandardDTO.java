package giuseppeperna.GearForFit.payloads;

import giuseppeperna.GearForFit.entities.Diete.TipoDieta;

import java.util.List;

public record DietaStandardDTO(
        Long id,
        String nome,
        String descrizione,
        TipoDieta tipoDieta,
        Integer durataSettimane,
        List<PastoStandardDTO> pasti
) {}

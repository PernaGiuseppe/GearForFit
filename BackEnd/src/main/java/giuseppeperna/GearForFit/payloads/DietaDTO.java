package giuseppeperna.GearForFit.payloads;

import giuseppeperna.GearForFit.entities.Diete.GiornoSettimana;
import giuseppeperna.GearForFit.entities.Diete.TipoDieta;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record DietaDTO(
        Long id,
        String nome,
        String descrizione,
        Integer durataSettimane,
        TipoDieta tipoDieta,
        Boolean isStandard,
        Long utenteId,
        Boolean isAttiva,
        List<PastoStandardDTO> pasti
) {}

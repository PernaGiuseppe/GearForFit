package giuseppeperna.GearForFit.payloads;

import java.util.List;

public record DietaStandardDTO(
        Long id,
        String nome,
        String descrizione,
        Integer durataSettimane,
        List<PastoStandardDTO> pasti
) {}

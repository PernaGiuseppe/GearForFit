package giuseppeperna.GearForFit.payloads;

import java.util.List;

public record PastoStandardDTO(
        String nomePasto,
        int ordine,
        List<AlimentoPastoDTO> alimenti
) {}
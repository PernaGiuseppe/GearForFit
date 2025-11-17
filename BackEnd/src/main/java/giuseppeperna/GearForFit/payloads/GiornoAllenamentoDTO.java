package giuseppeperna.GearForFit.payloads;

import giuseppeperna.GearForFit.entities.SchedePalestra.GiornoSettimana;
import java.util.List;

public record GiornoAllenamentoDTO(
        Long id,
        GiornoSettimana giornoSettimana,
        List<SerieDTO> serie
) {
}
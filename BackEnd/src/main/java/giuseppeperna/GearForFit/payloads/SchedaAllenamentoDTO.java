package giuseppeperna.GearForFit.payloads;

import giuseppeperna.GearForFit.entities.SchedePalestra.ObiettivoAllenamento;
import giuseppeperna.GearForFit.entities.SchedePalestra.TipoAllenamento;
import java.util.List;

public record SchedaAllenamentoDTO(
        Long id,
        String nome,
        String descrizione,
        ObiettivoAllenamento obiettivo,
        TipoAllenamento tipoAllenamento,
        Integer durataSettimane,
        Boolean isStandard,
        Long utenteId,
        Boolean attiva,
        List<GiornoAllenamentoDTO> giorni
) {
}
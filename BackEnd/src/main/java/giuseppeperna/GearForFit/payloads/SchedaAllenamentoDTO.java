package giuseppeperna.GearForFit.payloads;

import giuseppeperna.GearForFit.entities.SchedePalestra.ObiettivoAllenamento;

import java.util.List;

public record SchedaAllenamentoDTO(
        Long id,
        String nome,
        String descrizione,
        ObiettivoAllenamento obiettivo,
        Integer durataSettimane,
        Boolean isStandard,
        Long utenteId,
        Boolean attiva,
        List<GiornoAllenamentoDTO> giorni
) {
}
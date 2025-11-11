package giuseppeperna.GearForFit.payloads;

import giuseppeperna.GearForFit.entities.SchedePalestra.GiornoSettimana;
import giuseppeperna.GearForFit.entities.SchedePalestra.ObiettivoAllenamento;

import java.util.List;

public record SchedaAllenamentoDTO(
        Long id,
        String nome,
        String descrizione,
        ObiettivoAllenamento obiettivo,
        GiornoSettimana giornoSettimana,
        List<EsercizioSchedaDTO> esercizi
) {
}

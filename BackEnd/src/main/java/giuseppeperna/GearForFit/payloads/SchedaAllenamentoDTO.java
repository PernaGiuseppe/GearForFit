package giuseppeperna.GearForFit.payloads;


import giuseppeperna.GearForFit.entities.SchedePalestra.ObiettivoAllenamento;
import giuseppeperna.GearForFit.entities.SchedePalestra.TipoAllenamento;


public record SchedaAllenamentoDTO(
        Long id,
        String nome,
        String descrizione,
        ObiettivoAllenamento obiettivo,
        TipoAllenamento tipoAllenamento,
        Integer durataSettimane,
        Boolean isStandard,  // ✅ NUOVO
        Long utenteId        // ✅ NUOVO
) {
}
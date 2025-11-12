package giuseppeperna.GearForFit.payloads;


import giuseppeperna.GearForFit.entities.SchedePalestra.ObiettivoAllenamento;
import giuseppeperna.GearForFit.entities.SchedePalestra.TipoAllenamento;


public record SchedaAllenamentoDTO(
        Long id,
        String nome,
        String descrizione,
        ObiettivoAllenamento obiettivo,
        TipoAllenamento tipoAllenamento,
        Integer frequenzaSettimanale,
        Integer durataSettimane,
        String livelloEsperienza,
        Boolean isStandard,  // ✅ NUOVO
        Long utenteId        // ✅ NUOVO
) {
}
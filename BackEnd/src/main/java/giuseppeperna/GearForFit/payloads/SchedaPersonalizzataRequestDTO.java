package giuseppeperna.GearForFit.payloads;

import giuseppeperna.GearForFit.entities.SchedePalestra.ObiettivoAllenamento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record SchedaPersonalizzataRequestDTO(
        @NotBlank(message = "Il nome della scheda è obbligatorio")
        String nome,

        String descrizione,

        @NotNull(message = "L'obiettivo è obbligatorio")
        ObiettivoAllenamento obiettivo,

        Integer durataSettimane,

        List<GiornoAllenamentoRequestDTO> giorni
) {
}

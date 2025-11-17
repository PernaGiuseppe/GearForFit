package giuseppeperna.GearForFit.payloads;

import giuseppeperna.GearForFit.entities.SchedePalestra.GiornoSettimana;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record GiornoAllenamentoRequestDTO(
        @NotNull(message = "Il giorno della settimana è obbligatorio")
        GiornoSettimana giornoSettimana,

        @NotNull
        List<SerieRequestDTO> serie
) {
}

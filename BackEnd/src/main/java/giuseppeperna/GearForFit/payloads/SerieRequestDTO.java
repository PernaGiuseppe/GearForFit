package giuseppeperna.GearForFit.payloads;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

public record SerieRequestDTO(
        @NotNull(message = "L'ID dell'esercizio è obbligatorio")
        Long esercizioId,

        @NotNull
        @Min(1)
        Integer numeroSerie,

        @NotNull
        @Min(1)
        Integer numeroRipetizioni,

        Integer tempoRecuperoSecondi
) {
}

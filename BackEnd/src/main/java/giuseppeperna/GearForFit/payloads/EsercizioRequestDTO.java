package giuseppeperna.GearForFit.payloads;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EsercizioRequestDTO(
        @NotBlank(message = "Il nome dell'esercizio è obbligatorio")
        String nome,

        String descrizione,

        String urlImmagine,

        @NotNull(message = "Il gruppo muscolare è obbligatorio")
        Long gruppoMuscolareId,

        @NotNull(message = "L'attrezzo è obbligatorio")
        Long attrezzoId,

        @NotNull(message = "Specificare se è un esercizio composto")
        Boolean isComposto
) {
}

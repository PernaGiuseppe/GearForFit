package giuseppeperna.GearForFit.payloads;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record EsercizioSchedaRequestDTO(
        @NotNull(message = "L'ID dell'esercizio è obbligatorio")
        Long esercizioId,

        @NotNull(message = "L'ID dello schema serie è obbligatorio")
        Long schemaSerieId,

        @NotNull(message = "La posizione è obbligatoria")
        @Min(value = 1, message = "La posizione deve essere >= 1")
        Integer posizione,

        @NotNull(message = "Il riposo è obbligatorio")
        @Min(value = 0, message = "Il riposo non può essere negativo")
        Integer secondiRiposo,

        String note
) {
}

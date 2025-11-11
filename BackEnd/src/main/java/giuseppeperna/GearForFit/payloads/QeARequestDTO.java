package giuseppeperna.GearForFit.payloads;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record QeARequestDTO(
        @NotEmpty(message = "La domanda è obbligatoria")
        @Size(max = 500, message = "La domanda non può superare i 500 caratteri")
        String domanda,

        @NotEmpty(message = "La risposta è obbligatoria")
        @Size(max = 2000, message = "La risposta non può superare i 2000 caratteri")
        String risposta
) {
}

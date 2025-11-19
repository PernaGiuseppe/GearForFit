package giuseppeperna.GearForFit.payloads;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AggiornaProfiloDTO(
        @NotBlank(message = "Il nome è obbligatorio")
        String nome,

        @NotBlank(message = "Il cognome è obbligatorio")
        String cognome,

        @NotBlank(message = "L'email è obbligatoria")
        @Email(message = "Email non valida")
        String email
) {
}

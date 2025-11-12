package giuseppeperna.GearForFit.payloads;

import giuseppeperna.GearForFit.entities.Utente.TipoPiano;
import giuseppeperna.GearForFit.entities.Utente.TipoUtente;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreaUtenteRequestDTO(
        @NotBlank(message = "L'email è obbligatoria")
        @Email(message = "Email non valida")
        String email,

        @NotBlank(message = "La password è obbligatoria")
        @Size(min = 6, message = "La password deve contenere almeno 6 caratteri")
        String password,

        @NotBlank(message = "Il nome è obbligatorio")
        String nome,

        @NotBlank(message = "Il cognome è obbligatorio")
        String cognome,

        @NotNull(message = "Il tipo utente è obbligatorio")
        TipoUtente tipoUtente,

        // Opzionale: se non specificato, default FREE
        TipoPiano tipoPiano
) {
}

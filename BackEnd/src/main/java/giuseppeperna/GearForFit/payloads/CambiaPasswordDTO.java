package giuseppeperna.GearForFit.payloads;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CambiaPasswordDTO(
        @NotBlank(message = "La password vecchia è obbligatoria")
        String passwordVecchia,

        @NotBlank(message = "La password nuova è obbligatoria")
        @Size(min = 6, message = "La password deve essere di almeno 6 caratteri")
        String passwordNuova
) {}
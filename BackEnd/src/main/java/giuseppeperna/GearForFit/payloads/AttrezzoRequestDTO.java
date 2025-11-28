package giuseppeperna.GearForFit.payloads;

import jakarta.validation.constraints.NotBlank;

public record AttrezzoRequestDTO(
        @NotBlank(message = "Il nome è obbligatorio")
        String nome
) {}

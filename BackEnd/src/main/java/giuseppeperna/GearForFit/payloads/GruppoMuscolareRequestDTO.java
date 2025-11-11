package giuseppeperna.GearForFit.payloads;

import jakarta.validation.constraints.NotBlank;

public record GruppoMuscolareRequestDTO(
        @NotBlank(message = "Il nome è obbligatorio")
        String nome
) {}

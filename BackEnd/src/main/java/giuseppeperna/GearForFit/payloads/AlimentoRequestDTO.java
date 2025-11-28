package giuseppeperna.GearForFit.payloads;

import jakarta.validation.constraints.*;

public record AlimentoRequestDTO(
        @NotBlank String nome,
        @NotNull @Min(0) Double caloriePer100g,
        @NotNull @Min(0) Double proteinePer100g,
        @NotNull @Min(0) Double carboidratiPer100g,
        @NotNull @Min(0) Double grassiPer100g,
        @Min(0) Double fibrePer100g
) {}
package giuseppeperna.GearForFit.payloads;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AlimentoPastoRequestDTO(
        @NotNull Long alimentoId,
        @NotNull @Min(1) Integer grammi
) {}

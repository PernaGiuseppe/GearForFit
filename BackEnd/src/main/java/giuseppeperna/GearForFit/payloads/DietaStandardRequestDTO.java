package giuseppeperna.GearForFit.payloads;

import giuseppeperna.GearForFit.entities.Diete.TipoDieta;
import jakarta.validation.constraints.*;
import java.util.List;

public record DietaStandardRequestDTO(
        @NotBlank(message = "Il nome non può essere vuoto")
        String nome,

        String descrizione,

        Integer durataSettimane,

        @NotNull(message = "Il tipo di dieta non può essere nullo")
        TipoDieta tipoDieta,

        @NotEmpty(message = "Deve esserci almeno un pasto")
        List<PastoStandardRequestDTO> pasti
) {}

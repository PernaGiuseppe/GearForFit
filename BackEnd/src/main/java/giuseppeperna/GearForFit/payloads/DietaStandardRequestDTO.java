package giuseppeperna.GearForFit.payloads;

import giuseppeperna.GearForFit.entities.Diete.TipoDieta;
import jakarta.validation.constraints.*;
import java.util.List;

public record DietaStandardRequestDTO(
        String nome,
        String descrizione,
        int durataSettimane,
        TipoDieta tipoDieta,
        List<PastoStandardRequestDTO> pasti
) {}

package giuseppeperna.GearForFit.payloads;
import giuseppeperna.GearForFit.entities.Diete.GiornoSettimana;
import jakarta.validation.constraints.*;
import java.util.List;
public record PastoStandardRequestDTO(
        String nomePasto,
        int ordine,
        GiornoSettimana giornoSettimana,
        List<DietaStandardAlimentoDTO> alimenti
) {}

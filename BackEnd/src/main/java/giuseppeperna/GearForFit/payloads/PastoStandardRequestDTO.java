package giuseppeperna.GearForFit.payloads;
import jakarta.validation.constraints.*;
import java.util.List;
public record PastoStandardRequestDTO(
        String nomePasto,
        int ordine,
        List<DietaStandardAlimentoDTO> alimenti // <-- DEVE USARE IL DTO CORRETTO
) {}

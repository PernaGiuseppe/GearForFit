package giuseppeperna.GearForFit.payloads;
import giuseppeperna.GearForFit.entities.Diete.TipoDieta;
import java.time.LocalDate;
import java.util.List;


public record DietaUtenteDTO(
        Long id,
        String nomeDietaTemplate,
        TipoDieta tipoDietaObiettivo,
        LocalDate dataAssegnazione,
        boolean attiva,
        List<PastoStandardDTO> pasti
) {}

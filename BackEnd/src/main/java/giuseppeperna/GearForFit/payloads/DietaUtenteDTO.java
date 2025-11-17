package giuseppeperna.GearForFit.payloads;
import giuseppeperna.GearForFit.entities.Diete.TipoDieta;
import java.time.LocalDate;


public record DietaUtenteDTO(
        Long id, // L'ID dell'assegnazione (DietaUtente)
        String nomeDietaTemplate,
        TipoDieta tipoDietaObiettivo,
        LocalDate dataAssegnazione,
        boolean attiva
) {}

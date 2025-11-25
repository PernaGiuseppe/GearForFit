package giuseppeperna.GearForFit.payloads;

import giuseppeperna.GearForFit.entities.Diete.GiornoSettimana;
import java.util.List;

public record PastoRequestDTO(
        String nomePasto,
        int ordine,
        GiornoSettimana giornoSettimana,
        List<AlimentoPastoRequestDTO> alimenti
) {}


package giuseppeperna.GearForFit.payloads;

import giuseppeperna.GearForFit.entities.TipoPiano;
import giuseppeperna.GearForFit.entities.TipoUtente;

public record LoginResponseDTO(
        String token,
        Long utenteId,
        String email,
        String nome,
        String cognome,
        TipoUtente tipoUtente,
        TipoPiano tipoPiano

) {
}

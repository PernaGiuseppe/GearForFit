package giuseppeperna.GearForFit.payloads;

import giuseppeperna.GearForFit.entities.Utente.TipoPiano;
import giuseppeperna.GearForFit.entities.Utente.TipoUtente;

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

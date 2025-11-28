package giuseppeperna.GearForFit.payloads;

import giuseppeperna.GearForFit.entities.Utente.TipoPiano;
import giuseppeperna.GearForFit.entities.Utente.TipoUtente;

public record UtenteDTO(
        Long id,
        String email,
        String nome,
        String cognome,
        TipoUtente tipoUtente,
        TipoPiano tipoPiano,
        Boolean attivo
) {
}

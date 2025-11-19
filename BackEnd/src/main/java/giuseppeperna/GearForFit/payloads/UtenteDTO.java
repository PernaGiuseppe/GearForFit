package giuseppeperna.GearForFit.payloads;

public record UtenteDTO(
        Long id,
        String email,
        String nome,
        String cognome,
        String tipoUtente,
        Boolean attivo
) {
}

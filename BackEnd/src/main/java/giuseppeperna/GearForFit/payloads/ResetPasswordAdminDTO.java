package giuseppeperna.GearForFit.payloads;

public record ResetPasswordAdminDTO(
        Long utenteId,
        String nuovaPassword
) {
}

package giuseppeperna.GearForFit.payloads;

public record CambiaPasswordDTO(
        String passwordVecchia,
        String passwordNuova
) {
}

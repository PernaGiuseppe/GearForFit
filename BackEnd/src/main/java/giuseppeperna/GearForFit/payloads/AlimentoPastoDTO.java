package giuseppeperna.GearForFit.payloads;


public record AlimentoPastoDTO(
        String nome,
        double grammi,
        double proteine,
        double carboidrati,
        double grassi,
        double calorie
) {}

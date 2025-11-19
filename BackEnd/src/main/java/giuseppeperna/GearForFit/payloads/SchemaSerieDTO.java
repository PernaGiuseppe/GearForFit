package giuseppeperna.GearForFit.payloads;

public record SchemaSerieDTO(
        Long id,
        String nome,
        String tipo,
        Integer serie,
        Integer ripetizioni,
        String ripetizioni_piramidale,
        String descrizione
) {
}

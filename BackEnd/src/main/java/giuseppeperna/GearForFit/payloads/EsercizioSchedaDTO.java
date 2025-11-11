package giuseppeperna.GearForFit.payloads;

public record EsercizioSchedaDTO(
        Long id,
        Integer posizione,
        EsercizioDTO esercizio,
        SchemaSerieDTO schemaSerie,
        Integer secondiRiposo,
        String note
) {
}

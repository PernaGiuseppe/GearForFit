package giuseppeperna.GearForFit.payloads;

public record SerieDTO(
        Long id,
        Long esercizioId,
        String nomeEsercizio,
        Integer numeroSerie,
        Integer numeroRipetizioni,
        Integer tempoRecuperoSecondi,
        String peso
) {
}
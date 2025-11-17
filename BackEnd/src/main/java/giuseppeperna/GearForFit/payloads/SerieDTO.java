package giuseppeperna.GearForFit.payloads;

public record SerieDTO(
        Long id,
        Long esercizioId,
        String nomeEsercizio, // Molto utile per il frontend
        Integer numeroSerie,
        Integer numeroRipetizioni,
        Integer tempoRecuperoSecondi
) {
}
package giuseppeperna.GearForFit.payloads;

public record EsercizioDTO(
        Long id,
        String nome,
        String descrizione,
        String urlImmagine,
        String gruppoMuscolare,
        String attrezzo
) {
}

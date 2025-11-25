package giuseppeperna.GearForFit.payloads;

import giuseppeperna.GearForFit.entities.Diete.GiornoSettimana;
import giuseppeperna.GearForFit.entities.Diete.TipoDieta;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record DietaStandardDTO(
        Long id,
        String nome,
        String descrizione,
        TipoDieta tipoDieta,
        Integer durataSettimane,
        List<PastoStandardDTO> pasti
) {    // Metodo helper per raggruppare i pasti per giorno
    public Map<GiornoSettimana, List<PastoStandardDTO>> pastiPerGiorno() {
        return pasti.stream()
                .collect(Collectors.groupingBy(PastoStandardDTO::giornoSettimana));
    }}

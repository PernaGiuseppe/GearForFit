package giuseppeperna.GearForFit.payloads;

import giuseppeperna.GearForFit.entities.GiornoSettimana;
import giuseppeperna.GearForFit.entities.ObiettivoAllenamento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record SchedaAllenamentoRequestDTO(
        @NotBlank(message = "Il nome della scheda è obbligatorio")
        String nome,

        String descrizione,

        @NotNull(message = "L'obiettivo è obbligatorio")
        ObiettivoAllenamento obiettivo,

        @NotNull(message = "Il giorno della settimana è obbligatorio")
        GiornoSettimana giornoSettimana,

        @NotNull(message = "Deve contenere almeno un esercizio")
        List<EsercizioSchedaRequestDTO> esercizi
) {
}

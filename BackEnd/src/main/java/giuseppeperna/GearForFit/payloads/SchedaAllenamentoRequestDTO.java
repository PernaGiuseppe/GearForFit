package giuseppeperna.GearForFit.payloads;

import giuseppeperna.GearForFit.entities.SchedePalestra.GiornoSettimana;
import giuseppeperna.GearForFit.entities.SchedePalestra.ObiettivoAllenamento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record SchedaAllenamentoRequestDTO(
        @NotBlank(message = "Il nome della scheda è obbligatorio")
        String nome,

        String descrizione,

        @NotNull(message = "L'obiettivo è obbligatorio")
        ObiettivoAllenamento obiettivoAllenamento, // CORRETTO: era "obiettivo"

        @NotNull(message = "Il giorno della settimana è obbligatorio")
        GiornoSettimana giornoSettimana,

        Long utenteId, // AGGIUNTO: campo mancante

        List<EsercizioSchedaRequestDTO> esercizi // OPZIONALE
) {}

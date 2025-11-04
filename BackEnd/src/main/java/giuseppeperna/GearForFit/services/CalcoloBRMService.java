package giuseppeperna.GearForFit.services;

import giuseppeperna.GearForFit.entities.CalcoloBMR;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service

public class CalcoloBRMService {
    public Double calcolaBMR(CalcoloBMR profilo) {
        double bmr;
        if (profilo.getSesso().equals("M")) {
            bmr = (10 * profilo.getPeso()) +
                    (6.25 * profilo.getAltezza()) -
                    (5 * profilo.getEta()) + 5;
        } else {
            bmr = (10 * profilo.getPeso()) +
                    (6.25 * profilo.getAltezza()) -
                    (5 * profilo.getEta()) - 161;
        }
        return bmr;
    }

    public Double calcoloTDEE(Double bmr, String livelloAttivita) {
        // Moltiplica BMR per fattore attività
        Map<String, Double> fattori = Map.of(
                "SEDENTARIO", 1.2,
                "LEGGERO", 1.375,
                "MODERATO", 1.55,
                "INTENSO", 1.725
        );
        return bmr * fattori.get(livelloAttivita);
    }

    public Double adattaPerObiettivo(Double tdee, String obiettivo) {
        return switch (obiettivo) {
            case "PERDITA_PESO" -> tdee - 500; // deficit 500 kcal
            case "DEFINIZIONE" -> tdee - 300;  // deficit moderato
            case "MANTENIMENTO" -> tdee;
            case "MASSA" -> tdee + 300;        // surplus 300 kcal
            default -> tdee;
        };
    }
}

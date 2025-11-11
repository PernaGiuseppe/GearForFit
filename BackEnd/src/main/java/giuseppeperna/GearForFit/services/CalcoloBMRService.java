package giuseppeperna.GearForFit.services;

import giuseppeperna.GearForFit.entities.CalcoloBMR;
import giuseppeperna.GearForFit.entities.SchedePalestra.TipoDieta;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CalcoloBMRService {

    public Double calcolaBMR(CalcoloBMR profilo) {
        double bmr;

        if (profilo.getSesso() == CalcoloBMR.Sesso.M) {
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

    public Double calcoloTDEE(Double bmr, CalcoloBMR.LivelloAttivita livelloAttivita) {
        Map<CalcoloBMR.LivelloAttivita, Double> fattori = Map.of(
                CalcoloBMR.LivelloAttivita.SEDENTARIO, 1.2,
                CalcoloBMR.LivelloAttivita.LEGGERO, 1.375,
                CalcoloBMR.LivelloAttivita.MODERATO, 1.55,
                CalcoloBMR.LivelloAttivita.INTENSO, 1.725
        );
        return bmr * fattori.get(livelloAttivita);
    }

    public Double adattaPerObiettivo(Double tdee, TipoDieta tipoDieta) {
        return switch (tipoDieta) {
            case IPOCALORICA -> tdee - 300;
            case IPERCALORICA -> tdee + 300;
            case NORMOCALORICA -> tdee;
        };
    }

}

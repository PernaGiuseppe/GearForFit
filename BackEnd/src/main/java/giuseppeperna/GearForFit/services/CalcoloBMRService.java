package giuseppeperna.GearForFit.services;

import giuseppeperna.GearForFit.entities.Diete.CalcoloBMR;
import giuseppeperna.GearForFit.entities.Diete.TipoDieta;
import giuseppeperna.GearForFit.repositories.CalcoloBMRRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CalcoloBMRService {
    @Autowired
    private CalcoloBMRRepository calcoloBMRRepository;

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
    public CalcoloBMR getCalcoloBMRByUtente(Long utenteId) {
        return calcoloBMRRepository.findByUtenteId(utenteId) // <-- Usa l'istanza e passa il Long
                .orElseThrow(() -> new RuntimeException("Calcolo BMR non trovato per l'utente con ID: " + utenteId));
    }
}

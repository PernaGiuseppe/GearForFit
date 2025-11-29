package giuseppeperna.GearForFit.services;

import giuseppeperna.GearForFit.entities.Diete.CalcoloBMR;
import giuseppeperna.GearForFit.entities.Diete.LivelloAttivita;
import giuseppeperna.GearForFit.entities.Diete.Sesso;
import giuseppeperna.GearForFit.entities.Diete.TipoDieta;
import giuseppeperna.GearForFit.repositories.CalcoloBMRRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CalcoloBMRService {

    @Autowired
    private CalcoloBMRRepository calcoloBMRRepository;

    //Calcola il BMR (Basal Metabolic Rate) usando la formula di Harris-Benedict

    public Double calcolaBMR(CalcoloBMR profilo) {
        double bmr;

        if (profilo.getSesso() == Sesso.M) {
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

    // Calcola il TDEE (Total Daily Energy Expenditure) = BMR * fattore attività

    public Double calcoloTDEE(Double bmr, LivelloAttivita livelloAttivita) {  // ← Usa LivelloAttivita separato!
        Map<LivelloAttivita, Double> fattori = Map.of(
                LivelloAttivita.SEDENTARIO, 1.2,
                LivelloAttivita.LEGGERO, 1.375,
                LivelloAttivita.MODERATO, 1.55,
                LivelloAttivita.INTENSO, 1.725
        );

        return bmr * fattori.get(livelloAttivita);
    }

    // Adatta le calorie in base all'obiettivo dietetico

    public Double adattaPerObiettivo(Double tdee, TipoDieta tipoDieta) {
        return switch (tipoDieta) {
            case IPOCALORICA -> tdee * 0.85;      // -15%
            case NORMOCALORICA -> tdee;             // 0%
            case IPERCALORICA -> tdee * 1.15;      // +15%
        };
    }

    // Recupera il CalcoloBMR di un utente

    public CalcoloBMR getCalcoloBMRByUtente(Long utenteId) {
        return calcoloBMRRepository.findByUtenteId(utenteId)
                .orElseThrow(() -> new RuntimeException("Calcolo BMR non trovato per l'utente con ID: " + utenteId));
    }
}

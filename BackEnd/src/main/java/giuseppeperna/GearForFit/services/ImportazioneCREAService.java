package giuseppeperna.GearForFit.services;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import giuseppeperna.GearForFit.entities.Alimenti.*;
import giuseppeperna.GearForFit.repositories.AlimentoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImportazioneCREAService {

    private final AlimentoRepository alimentoRepository;

    public void importaAlimentiDaCSV() throws IOException, CsvException {
        // Leggi dalla cartella resources usando ClassPathResource
        ClassPathResource resource = new ClassPathResource("data/crea_alimenti.csv");

        try (CSVReader reader = new CSVReader(new InputStreamReader(resource.getInputStream()))) {
            List<String[]> rows = reader.readAll();

            for (int i = 1; i < rows.size(); i++) {
                String[] row = rows.get(i);

                String nome = row[0];
                String categoria = row[1];
                Double calorie = parseDouble(row[2]);
                Double proteine = parseDouble(row[3]);
                Double carboidrati = parseDouble(row[4]);
                Double grassi = parseDouble(row[5]);
                Double fibre = parseDouble(row[6]);

                Alimento alimento = creaAlimentoDaCategoria(categoria);
                alimento.setNome(nome);
                alimento.setCaloriePer100g(calorie);
                alimento.setProteinePer100g(proteine);
                alimento.setCarboidratiPer100g(carboidrati);
                alimento.setGrassiPer100g(grassi);
                alimento.setFibrePer100g(fibre);
                alimento.setFonte("CREA");

                alimentoRepository.save(alimento);
            }

            log.info("✅ Importati {} alimenti dal CSV", rows.size() - 1);
        }
    }

    private Alimento creaAlimentoDaCategoria(String categoria) {
        return switch (categoria.toLowerCase()) {
            case "verdure e ortaggi", "verdure" -> new Verdura();
            case "carni" -> new Carne();
            case "cereali e derivati", "cereali" -> new Carboidrato();
            case "frutta" -> new Frutta();
            case "latte e derivati", "latticini" -> new Latticino();
            case "legumi" -> new Legume();
            default -> new Verdura();
        };
    }

    private Double parseDouble(String value) {
        try {
            return value == null || value.isEmpty() ? 0.0 : Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}

package giuseppeperna.GearForFit.runners;

import com.opencsv.CSVReader;
import giuseppeperna.GearForFit.entities.Alimenti.*;
import giuseppeperna.GearForFit.entities.Bevande.BevandaAlcolica;
import giuseppeperna.GearForFit.entities.Bevande.BevandaAnalcolica;
import giuseppeperna.GearForFit.entities.Bevande.BevandaEnergetica;
import giuseppeperna.GearForFit.repositories.AlimentoRepository;
import giuseppeperna.GearForFit.repositories.BevandaRepository;
import giuseppeperna.GearForFit.services.AlimentoService;
import giuseppeperna.GearForFit.services.BevandaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.InputStreamReader;
import java.util.Objects;

@Component
@Order(1)
@RequiredArgsConstructor
@Slf4j
public class ElementiCsvRunner implements CommandLineRunner {

    private final AlimentoService alimentoService;
    private final BevandaService bevandaService;
    private final AlimentoRepository alimentoRepository;
    private final BevandaRepository bevandaRepository;

    @Override
    public void run(String... args) throws Exception {
        // Controlla se il database è già popolato
        long countAlimenti = alimentoRepository.count();
        long countBevande = bevandaRepository.count();

        if (countAlimenti > 0 || countBevande > 0) {
            log.info("✓ Database già popolato. Alimenti: {}, Bevande: {}. Import saltato.", countAlimenti, countBevande);
            return;
        }

        log.info("🍽️  Database vuoto. Importazione da CSV unificato in corso...");

        int countAlimentiImportati = 0;
        int countBevandeImportate = 0;

        try (CSVReader reader = new CSVReader(
                new InputStreamReader(
                        Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("data/elementi_unificati.csv"))
                )
        )) {
            String[] nextLine;
            boolean firstLine = true;

            while ((nextLine = reader.readNext()) != null) {
                // Salta l'intestazione
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                try {
                    String tipo = nextLine[0];                          // type (ALIMENTO/BEVANDA)
                    String categoria = nextLine[1];                     // category
                    String nome = nextLine[2];                          // name
                    double calorie = parseDouble(nextLine[3]);          // energy_kcal
                    double proteine = parseDouble(nextLine[4]);         // proteins
                    double carboidrati = parseDouble(nextLine[5]);      // available_carbohydrates
                    double grassi = parseDouble(nextLine[6]);           // lipids
                    double fibre = parseDouble(nextLine[7]);            // total_fiber

                    // ===== ALIMENTI =====
                    if ("ALIMENTO".equalsIgnoreCase(tipo)) {
                        switch (categoria) {
                            case "CARNE" -> {
                                Carne carne = new Carne();
                                carne.setNome(nome);
                                carne.setCaloriePer100g(calorie);
                                carne.setProteinePer100g(proteine);
                                carne.setCarboidratiPer100g(carboidrati);
                                carne.setGrassiPer100g(grassi);
                                carne.setFibrePer100g(fibre);
                                alimentoService.saveCarne(carne);
                                countAlimentiImportati++;
                            }
                            case "PESCE" -> {
                                Pesce pesce = new Pesce();
                                pesce.setNome(nome);
                                pesce.setCaloriePer100g(calorie);
                                pesce.setProteinePer100g(proteine);
                                pesce.setCarboidratiPer100g(carboidrati);
                                pesce.setGrassiPer100g(grassi);
                                pesce.setFibrePer100g(fibre);
                                alimentoService.savePesce(pesce);
                                countAlimentiImportati++;
                            }
                            case "CARBOIDRATO" -> {
                                Carboidrato carb = new Carboidrato();
                                carb.setNome(nome);
                                carb.setCaloriePer100g(calorie);
                                carb.setProteinePer100g(proteine);
                                carb.setCarboidratiPer100g(carboidrati);
                                carb.setGrassiPer100g(grassi);
                                carb.setFibrePer100g(fibre);
                                alimentoService.saveCarboidrato(carb);
                                countAlimentiImportati++;
                            }
                            case "FRUTTA" -> {
                                Frutta frutta = new Frutta();
                                frutta.setNome(nome);
                                frutta.setCaloriePer100g(calorie);
                                frutta.setProteinePer100g(proteine);
                                frutta.setCarboidratiPer100g(carboidrati);
                                frutta.setGrassiPer100g(grassi);
                                frutta.setFibrePer100g(fibre);
                                alimentoService.saveFrutta(frutta);
                                countAlimentiImportati++;
                            }
                            case "VERDURA" -> {
                                Verdura verdura = new Verdura();
                                verdura.setNome(nome);
                                verdura.setCaloriePer100g(calorie);
                                verdura.setProteinePer100g(proteine);
                                verdura.setCarboidratiPer100g(carboidrati);
                                verdura.setGrassiPer100g(grassi);
                                verdura.setFibrePer100g(fibre);
                                alimentoService.saveVerdura(verdura);
                                countAlimentiImportati++;
                            }
                            case "LATTICINO" -> {
                                Latticino latt = new Latticino();
                                latt.setNome(nome);
                                latt.setCaloriePer100g(calorie);
                                latt.setProteinePer100g(proteine);
                                latt.setCarboidratiPer100g(carboidrati);
                                latt.setGrassiPer100g(grassi);
                                latt.setFibrePer100g(fibre);
                                alimentoService.saveLatticino(latt);
                                countAlimentiImportati++;
                            }
                            case "LEGUME" -> {
                                Legume legume = new Legume();
                                legume.setNome(nome);
                                legume.setCaloriePer100g(calorie);
                                legume.setProteinePer100g(proteine);
                                legume.setCarboidratiPer100g(carboidrati);
                                legume.setGrassiPer100g(grassi);
                                legume.setFibrePer100g(fibre);
                                alimentoService.saveLegume(legume);
                                countAlimentiImportati++;
                            }
                            case "DOLCE" -> {
                                Dolce dolce = new Dolce();
                                dolce.setNome(nome);
                                dolce.setCaloriePer100g(calorie);
                                dolce.setProteinePer100g(proteine);
                                dolce.setCarboidratiPer100g(carboidrati);
                                dolce.setGrassiPer100g(grassi);
                                dolce.setFibrePer100g(fibre);
                                alimentoService.saveDolce(dolce);
                                countAlimentiImportati++;
                            }
                            case "CONDIMENTO" -> {
                                Condimento cond = new Condimento();
                                cond.setNome(nome);
                                cond.setCaloriePer100g(calorie);
                                cond.setProteinePer100g(proteine);
                                cond.setCarboidratiPer100g(carboidrati);
                                cond.setGrassiPer100g(grassi);
                                cond.setFibrePer100g(fibre);
                                alimentoService.saveCondimento(cond);
                                countAlimentiImportati++;
                            }
                        }
                    }
                    // ===== BEVANDE =====
                    else if ("BEVANDA".equalsIgnoreCase(tipo)) {
                        switch (categoria) {
                            case "ALCOLICA" -> {
                                BevandaAlcolica bev = new BevandaAlcolica();
                                bev.setNome(nome);
                                bev.setCaloriePer100g(calorie);
                                bev.setProteinePer100g(proteine);
                                bev.setCarboidratiPer100g(carboidrati);
                                bev.setGrassiPer100g(grassi);
                                bev.setFibrePer100g(fibre);
                                bevandaService.saveBevandaAlcolica(bev);
                                countBevandeImportate++;
                            }
                            case "ANALCOLICA" -> {
                                BevandaAnalcolica bev = new BevandaAnalcolica();
                                bev.setNome(nome);
                                bev.setCaloriePer100g(calorie);
                                bev.setProteinePer100g(proteine);
                                bev.setCarboidratiPer100g(carboidrati);
                                bev.setGrassiPer100g(grassi);
                                bev.setFibrePer100g(fibre);
                                bevandaService.saveBevandaAnalcolica(bev);
                                countBevandeImportate++;
                            }
                            case "ENERGETICA" -> {
                                BevandaEnergetica bev = new BevandaEnergetica();
                                bev.setNome(nome);
                                bev.setCaloriePer100g(calorie);
                                bev.setProteinePer100g(proteine);
                                bev.setCarboidratiPer100g(carboidrati);
                                bev.setGrassiPer100g(grassi);
                                bev.setFibrePer100g(fibre);
                                bevandaService.saveBevandaEnergetica(bev);
                                countBevandeImportate++;
                            }
                        }
                    }
                } catch (Exception e) {
                    log.warn("⚠️ Errore nel parsing della riga. Continuando...", e);
                }
            }
            log.info("✅ Import completato! Alimenti: {}, Bevande: {}", countAlimentiImportati, countBevandeImportate);
        } catch (Exception e) {
            log.error("❌ Errore critico durante l'import: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private double parseDouble(String value) {
        if (value == null || value.isEmpty() || value.equals("tr")) {
            return 0.0;
        }
        try {
            return Double.parseDouble(value.replace(",", "."));
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}

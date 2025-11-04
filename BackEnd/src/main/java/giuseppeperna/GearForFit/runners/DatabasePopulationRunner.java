package giuseppeperna.GearForFit.runners;

import giuseppeperna.GearForFit.repositories.AlimentoRepository;
import giuseppeperna.GearForFit.services.ImportazioneCREAService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
@RequiredArgsConstructor
@Slf4j
public class DatabasePopulationRunner implements CommandLineRunner {

    private final AlimentoRepository alimentoRepository;
    private final ImportazioneCREAService importazioneCREAService;

    @Override
    public void run(String... args) throws Exception {
        if (alimentoRepository.count() == 0) {
            log.info("🥗 Database alimenti vuoto. Importazione da CSV...");
            importazioneCREAService.importaAlimentiDaCSV();
        } else {
            log.info("✓ Database alimenti già popolato ({} elementi)", alimentoRepository.count());
        }
    }
}

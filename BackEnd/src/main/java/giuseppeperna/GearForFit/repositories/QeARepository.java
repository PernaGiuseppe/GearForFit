package giuseppeperna.GearForFit.repositories;

import giuseppeperna.GearForFit.entities.Utente.QeA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QeARepository extends JpaRepository<QeA, Long> {

    // Cerca per keyword nella domanda o risposta
    List<QeA> findByDomandaContainingIgnoreCaseOrRispostaContainingIgnoreCase(String domanda, String risposta);
}

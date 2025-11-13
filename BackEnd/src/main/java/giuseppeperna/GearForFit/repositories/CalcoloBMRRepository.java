package giuseppeperna.GearForFit.repositories;

import giuseppeperna.GearForFit.entities.Diete.CalcoloBMR;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CalcoloBMRRepository extends JpaRepository<CalcoloBMR, Long> {
    Optional<CalcoloBMR> findByUtenteId(Long utenteId);
}

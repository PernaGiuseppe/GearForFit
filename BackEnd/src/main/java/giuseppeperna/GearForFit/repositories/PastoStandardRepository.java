package giuseppeperna.GearForFit.repositories;

import giuseppeperna.GearForFit.entities.Diete.PastoStandard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PastoStandardRepository extends JpaRepository<PastoStandard, Long> {
}
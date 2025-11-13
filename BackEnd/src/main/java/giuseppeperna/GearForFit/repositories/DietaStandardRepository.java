package giuseppeperna.GearForFit.repositories;

import giuseppeperna.GearForFit.entities.Diete.DietaStandard;
import giuseppeperna.GearForFit.entities.Diete.TipoDieta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DietaStandardRepository extends JpaRepository<DietaStandard, Long> {

    Optional<DietaStandard> findByTipoDieta(TipoDieta tipoDieta);
}


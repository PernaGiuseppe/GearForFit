package giuseppeperna.GearForFit.repositories;

import giuseppeperna.GearForFit.entities.Diete.DietaStandardAlimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DietaStandardAlimentoRepository extends JpaRepository<DietaStandardAlimento, Long> {
}

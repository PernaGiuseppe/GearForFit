package giuseppeperna.GearForFit.repositories;

import giuseppeperna.GearForFit.entities.SchedePalestra.Serie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SerieRepository extends JpaRepository<Serie, Long> {
}

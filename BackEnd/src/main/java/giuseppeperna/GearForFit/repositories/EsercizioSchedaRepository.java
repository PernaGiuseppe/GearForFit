package giuseppeperna.GearForFit.repositories;

import giuseppeperna.GearForFit.entities.EsercizioScheda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EsercizioSchedaRepository extends JpaRepository<EsercizioScheda, Long> {
}

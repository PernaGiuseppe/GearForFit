package giuseppeperna.GearForFit.repositories;

import giuseppeperna.GearForFit.entities.Utente.QeA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QeARepository extends JpaRepository<QeA, Long> {
}

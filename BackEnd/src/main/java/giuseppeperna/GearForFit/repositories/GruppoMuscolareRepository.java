package giuseppeperna.GearForFit.repositories;

import giuseppeperna.GearForFit.entities.GruppoMuscolare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GruppoMuscolareRepository extends JpaRepository<GruppoMuscolare, Long> {

    // Metodo per trovare un gruppo per nome
    Optional<GruppoMuscolare> findByNome(String nome);
}

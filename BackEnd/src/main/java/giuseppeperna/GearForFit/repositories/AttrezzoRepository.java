package giuseppeperna.GearForFit.repositories;

import giuseppeperna.GearForFit.entities.Attrezzo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AttrezzoRepository extends JpaRepository<Attrezzo, Long> {

    // Metodo per trovare un attrezzo per nome
    Optional<Attrezzo> findByNome(String nome);
}

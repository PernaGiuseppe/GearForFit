package giuseppeperna.GearForFit.repositories;

import giuseppeperna.GearForFit.entities.Utente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UtenteRepository extends JpaRepository<Utente, Long> {

    // Metodo per trovare un utente per email
    Optional<Utente> findByEmail(String email);
}

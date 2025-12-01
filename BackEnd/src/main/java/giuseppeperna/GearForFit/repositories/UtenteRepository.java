package giuseppeperna.GearForFit.repositories;

import giuseppeperna.GearForFit.entities.Utente.Utente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UtenteRepository extends JpaRepository<Utente, Long> {

    // Trova tutti gli utenti ordinati per ID ascendente
    List<Utente> findAllByOrderByIdAsc();

    // Metodo per trovare un utente per email
    Optional<Utente> findByEmail(String email);
}

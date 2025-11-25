package giuseppeperna.GearForFit.repositories;

import giuseppeperna.GearForFit.entities.Diete.Dieta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;  // ← CORRETTO!

@Repository
public interface DietaRepository extends JpaRepository<Dieta, Long> {

    // Template standard
    List<Dieta> findAllByIsStandardTrue();

    // Diete custom dell'utente
    List<Dieta> findByUtenteIdAndIsStandardFalse(Long utenteId);

    // Verifica se attiva
    Optional<Dieta> findByIdAndUtenteIdAndIsAttivaTrueAndIsStandardFalse(Long id, Long utenteId);

    // Get custom per utente
    Optional<Dieta> findByIdAndUtenteId(Long id, Long utenteId);

    //Trova la dieta attiva di un utente (solo custom)
    Optional<Dieta> findByUtenteIdAndIsAttivaTrueAndIsStandardFalse(Long utenteId);
}

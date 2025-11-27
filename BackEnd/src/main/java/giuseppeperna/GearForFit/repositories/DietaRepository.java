package giuseppeperna.GearForFit.repositories;

import giuseppeperna.GearForFit.entities.Diete.Dieta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;  // ← CORRETTO!

@Repository
public interface DietaRepository extends JpaRepository<Dieta, Long> {

    // Template standard
    @Query("SELECT d FROM Dieta d WHERE d.isStandard = true ORDER BY d.id ASC")
    List<Dieta> findAllByIsStandardTrue();
    @Query("SELECT d FROM Dieta d WHERE d.isStandard = false ORDER BY d.id ASC")
    List<Dieta> findAllByIsStandardFalse();

    // Diete custom dell'utente
    @Query("SELECT d FROM Dieta d WHERE d.utente.id = :utenteId AND d.isStandard = false ORDER BY d.id ASC")
    List<Dieta> findByUtenteIdAndIsStandardFalse(@Param("utenteId") Long utenteId);

    // Verifica se attiva
    Optional<Dieta> findByIdAndUtenteIdAndIsAttivaTrueAndIsStandardFalse(Long id, Long utenteId);

    // Get custom per utente
    Optional<Dieta> findByIdAndUtenteId(Long id, Long utenteId);

    //Trova la dieta attiva di un utente (solo custom)
    Optional<Dieta> findByUtenteIdAndIsAttivaTrueAndIsStandardFalse(Long utenteId);

    List<Dieta> findByIsStandardFalse();


}

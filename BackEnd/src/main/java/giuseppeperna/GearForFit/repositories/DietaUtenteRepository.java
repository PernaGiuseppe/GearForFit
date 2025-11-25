package giuseppeperna.GearForFit.repositories;

import giuseppeperna.GearForFit.entities.Diete.DietaUtente;
import giuseppeperna.GearForFit.entities.Diete.TipoDieta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DietaUtenteRepository extends JpaRepository<DietaUtente, Long> {
    // Trova tutte le diete assegnate a un utente
    List<DietaUtente> findByUtenteId(Long utenteId);

    // Trova la dieta attualmente attiva per un utente
    Optional<DietaUtente> findByUtenteIdAndAttivaTrue(Long utenteId);

    List<DietaUtente> findByDietaStandardId(Long dietaId);

    Optional<DietaUtente> findByIdAndUtenteId(Long id, Long utenteId);

    List<DietaUtente> findByUtenteIdAndTipoDietaObiettivo(Long utenteId, TipoDieta tipoDietaObiettivo);

    boolean existsByDietaStandardIdAndIdNot(Long dietaStandardId, Long dietaUtenteId);

    long countByDietaStandardId(Long dietaStandardId);

}

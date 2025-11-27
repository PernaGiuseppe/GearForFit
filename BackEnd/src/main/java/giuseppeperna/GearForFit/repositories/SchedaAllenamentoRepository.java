package giuseppeperna.GearForFit.repositories;

import giuseppeperna.GearForFit.entities.SchedePalestra.SchedaAllenamento;
import giuseppeperna.GearForFit.entities.SchedePalestra.ObiettivoAllenamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface SchedaAllenamentoRepository extends JpaRepository<SchedaAllenamento, Long> {

    @Query("SELECT s FROM SchedaAllenamento s WHERE s.obiettivo = :obiettivo ORDER BY s.id ASC")
    List<SchedaAllenamento> findByObiettivo(@Param("obiettivo") ObiettivoAllenamento obiettivo);

    @Query("SELECT s FROM SchedaAllenamento s WHERE s.isStandard = true ORDER BY s.id ASC")
    List<SchedaAllenamento> findByIsStandardTrue();

    @Query("SELECT s FROM SchedaAllenamento s WHERE s.isStandard = true AND s.obiettivo = :obiettivo ORDER BY s.id ASC")
    List<SchedaAllenamento> findByIsStandardTrueAndObiettivo(@Param("obiettivo") ObiettivoAllenamento obiettivo);

    @Query("SELECT s FROM SchedaAllenamento s WHERE s.utente.id = :utenteId ORDER BY s.id ASC")
    List<SchedaAllenamento> findByUtenteId(@Param("utenteId") Long utenteId);

    @Query("SELECT s FROM SchedaAllenamento s WHERE s.utente.id = :utenteId AND s.obiettivo = :obiettivo ORDER BY s.id ASC")
    List<SchedaAllenamento> findByUtenteIdAndObiettivo(@Param("utenteId") Long utenteId, @Param("obiettivo") ObiettivoAllenamento obiettivo);

    @Query("SELECT s FROM SchedaAllenamento s WHERE s.isStandard = true OR s.utente.id = :utenteId ORDER BY s.id ASC")
    List<SchedaAllenamento> findSchedeVisibiliPerUtente(@Param("utenteId") Long utenteId);


    Optional<SchedaAllenamento> findByIdAndUtenteId(Long id, Long utenteId);

    Optional<SchedaAllenamento> findByUtenteIdAndAttivaTrue(Long utenteId);
    List<SchedaAllenamento> findByIsStandardFalse();
    List<SchedaAllenamento> findByIsStandardFalseAndObiettivo(ObiettivoAllenamento obiettivo);



}
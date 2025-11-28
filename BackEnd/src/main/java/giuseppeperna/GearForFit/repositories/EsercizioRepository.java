package giuseppeperna.GearForFit.repositories;

import giuseppeperna.GearForFit.entities.SchedePalestra.Esercizio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EsercizioRepository extends JpaRepository<Esercizio, Long> {

    @Query("SELECT e FROM Esercizio e WHERE e.gruppoMuscolare.id = :gruppoId ORDER BY e.id ASC")
    List<Esercizio> findByGruppoMuscolareId(@Param("gruppoId") Long gruppoId);

    @Query("SELECT e FROM Esercizio e WHERE e.attrezzo.id = :attrezzoId ORDER BY e.id ASC")
    List<Esercizio> findByAttrezzoId(@Param("attrezzoId") Long attrezzoId);

    @Query("SELECT e FROM Esercizio e WHERE e.isComposto = true ORDER BY e.id ASC")
    List<Esercizio> findByIsCompostoTrue();

    @Query("SELECT e FROM Esercizio e WHERE LOWER(e.nome) LIKE LOWER(CONCAT('%', :nome, '%')) ORDER BY e.id ASC")
    List<Esercizio> findByNomeContainingIgnoreCase(@Param("nome") String nome);



}

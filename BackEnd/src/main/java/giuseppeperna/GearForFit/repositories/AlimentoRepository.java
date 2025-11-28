package giuseppeperna.GearForFit.repositories;

import giuseppeperna.GearForFit.entities.Alimenti.Alimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlimentoRepository extends JpaRepository<Alimento, Long> {

    Optional<Alimento> findByNome(String nome);

    @Query("SELECT a FROM Alimento a WHERE LOWER(a.nome) LIKE LOWER(CONCAT('%', :nome, '%')) ORDER BY a.id ASC")
    List<Alimento> findByNomeContainingIgnoreCase(@Param("nome") String nome);

    @Query("SELECT a FROM Alimento a WHERE a.caloriePer100g BETWEEN :min AND :max ORDER BY a.id ASC")
    List<Alimento> findByCaloriePer100gBetween(@Param("min") double min, @Param("max") double max);

    @Query("SELECT a FROM Alimento a WHERE a.proteinePer100g BETWEEN :min AND :max ORDER BY a.id ASC")
    List<Alimento> findByProteinePer100gBetween(@Param("min") double min, @Param("max") double max);

    @Query("SELECT a FROM Alimento a WHERE a.carboidratiPer100g BETWEEN :min AND :max ORDER BY a.id ASC")
    List<Alimento> findByCarboidratiPer100gBetween(@Param("min") double min, @Param("max") double max);

    @Query("SELECT a FROM Alimento a WHERE a.grassiPer100g BETWEEN :min AND :max ORDER BY a.id ASC")
    List<Alimento> findByGrassiPer100gBetween(@Param("min") double min, @Param("max") double max);

    @Query("SELECT a FROM Alimento a WHERE a.fibrePer100g BETWEEN :min AND :max ORDER BY a.id ASC")
    List<Alimento> findByFibrePer100gBetween(@Param("min") double min, @Param("max") double max);

}

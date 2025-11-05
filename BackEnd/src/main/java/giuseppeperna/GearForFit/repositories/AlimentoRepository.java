package giuseppeperna.GearForFit.repositories;

import giuseppeperna.GearForFit.entities.Alimenti.Alimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlimentoRepository extends JpaRepository<Alimento, Long> {

    Optional<Alimento> findByNome(String nome);

    List<Alimento> findByNomeContainingIgnoreCase(String nome);

    List<Alimento> findByCaloriePer100gBetween(double min, double max);

    List<Alimento> findByProteinePer100gBetween(double min, double max);

    List<Alimento> findByCarboidratiPer100gBetween(double min, double max);

    List<Alimento> findByGrassiPer100gBetween(double min, double max);

    List<Alimento> findByFibrePer100gBetween(double min, double max);
}

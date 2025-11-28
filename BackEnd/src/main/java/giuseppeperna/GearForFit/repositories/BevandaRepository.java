package giuseppeperna.GearForFit.repositories;

import giuseppeperna.GearForFit.entities.Bevande.Bevanda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BevandaRepository extends JpaRepository<Bevanda, Long> {

    Optional<Bevanda> findByNome(String nome);

    List<Bevanda> findByNomeContainingIgnoreCase(String nome);

    List<Bevanda> findByCaloriePer100gBetween(double min, double max);

    List<Bevanda> findByProteinePer100gBetween(double min, double max);

}

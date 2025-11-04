package giuseppeperna.GearForFit.repositories;


import giuseppeperna.GearForFit.entities.Bevande.Bevanda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BevandaRepository extends JpaRepository<Bevanda, Long> {
    List<Bevanda> findByNomeContainingIgnoreCase(String nome);

    List<Bevanda> findByFonte(String fonte);
}

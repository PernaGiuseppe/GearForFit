package giuseppeperna.GearForFit.repositories;

import giuseppeperna.GearForFit.entities.Alimenti.Alimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlimentoRepository extends JpaRepository<Alimento, Long> {
    List<Alimento> findByNomeContainingIgnoreCase(String nome);

    List<Alimento> findByFonte(String fonte);
}

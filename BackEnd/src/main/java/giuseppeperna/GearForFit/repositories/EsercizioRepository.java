package giuseppeperna.GearForFit.repositories;

import giuseppeperna.GearForFit.entities.SchedePalestra.Esercizio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EsercizioRepository extends JpaRepository<Esercizio, Long> {
    
    List<Esercizio> findByGruppoMuscolareId(Long gruppoId);

    List<Esercizio> findByAttrezzoId(Long attrezzoId);

    List<Esercizio> findByIsCompostoTrue();

    List<Esercizio> findByNomeContainingIgnoreCase(String nome);


}

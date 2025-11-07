package giuseppeperna.GearForFit.repositories;

import giuseppeperna.GearForFit.entities.Esercizio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EsercizioRepository extends JpaRepository<Esercizio, Long> {

    // Metodo per trovare esercizi per gruppo muscolare
    List<Esercizio> findByGruppoMuscolareId(Long gruppoId);
}

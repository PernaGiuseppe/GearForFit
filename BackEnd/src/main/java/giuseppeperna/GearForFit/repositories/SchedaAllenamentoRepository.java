package giuseppeperna.GearForFit.repositories;

import giuseppeperna.GearForFit.entities.ObiettivoAllenamento;
import giuseppeperna.GearForFit.entities.SchedaAllenamento;
import giuseppeperna.GearForFit.entities.Utente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SchedaAllenamentoRepository extends JpaRepository<SchedaAllenamento, Long> {

    // Metodo per trovare tutte le schede di uno specifico utente
    List<SchedaAllenamento> findByUtente(Utente utente);

    // AGGIUNGI QUESTI METODI
    List<SchedaAllenamento> findByIsStandardTrue();

    List<SchedaAllenamento> findByIsStandardTrueAndObiettivo(ObiettivoAllenamento obiettivo);
}
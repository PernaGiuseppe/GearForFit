package giuseppeperna.GearForFit.repositories;

import giuseppeperna.GearForFit.entities.SchedePalestra.SchedaAllenamento;
import giuseppeperna.GearForFit.entities.SchedePalestra.ObiettivoAllenamento;
import giuseppeperna.GearForFit.entities.SchedePalestra.TipoAllenamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SchedaAllenamentoRepository extends JpaRepository<SchedaAllenamento, Long> {

    List<SchedaAllenamento> findByObiettivo(ObiettivoAllenamento obiettivo);

    List<SchedaAllenamento> findByTipoAllenamento(TipoAllenamento tipoAllenamento);

    List<SchedaAllenamento> findByObiettivoAndTipoAllenamento(
            ObiettivoAllenamento obiettivo,
            TipoAllenamento tipoAllenamento
    );

    // Schede standard (admin)
    List<SchedaAllenamento> findByIsStandardTrue();

    List<SchedaAllenamento> findByIsStandardTrueAndObiettivo(ObiettivoAllenamento obiettivo);

    List<SchedaAllenamento> findByIsStandardTrueAndTipoAllenamento(TipoAllenamento tipoAllenamento);

    // Schede personalizzate (utente)
    List<SchedaAllenamento> findByUtenteId(Long utenteId);

    List<SchedaAllenamento> findByUtenteIdAndObiettivo(Long utenteId, ObiettivoAllenamento obiettivo);

    List<SchedaAllenamento> findByUtenteIdAndTipoAllenamento(Long utenteId, TipoAllenamento tipoAllenamento);

}
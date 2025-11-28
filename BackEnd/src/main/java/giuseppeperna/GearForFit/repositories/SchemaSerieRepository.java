package giuseppeperna.GearForFit.repositories;

import giuseppeperna.GearForFit.entities.SchedePalestra.SchemaSerie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SchemaSerieRepository extends JpaRepository<SchemaSerie, Long> {

    // Metodo opzionale per trovare uno schema per nome
    Optional<SchemaSerie> findByNome(String nome);
}

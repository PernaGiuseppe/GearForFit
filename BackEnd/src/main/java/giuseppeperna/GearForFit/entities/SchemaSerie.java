package giuseppeperna.GearForFit.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "schemi_serie")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SchemaSerie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoSerie tipo;

    @Column(nullable = false)
    private Integer serie;

    @Column(nullable = false)
    private Integer ripetizioni;

    @Column(name = "ripetizioni_piramidale", columnDefinition = "TEXT")
    private String ripetizioni_piramidale;

    @Column(columnDefinition = "TEXT")
    private String descrizione;

    public enum TipoSerie {
        STANDARD, PIRAMIDALE
    }
}


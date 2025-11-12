package giuseppeperna.GearForFit.entities.SchedePalestra;


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
    private TipoAllenamento tipoAllenamento;

    @Column(nullable = false)
    private Integer serie;

    @Column(nullable = false)
    private Integer ripetizioni;

    @Column(name = "descrizione_schemaSerie")
    private String descrizione;
    //se piramidale 6/8/10, o a scendere
    //se stripping quante reps per quante volte

}


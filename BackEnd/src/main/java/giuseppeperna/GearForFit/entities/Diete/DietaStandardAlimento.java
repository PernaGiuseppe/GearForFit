package giuseppeperna.GearForFit.entities.Diete;
import com.fasterxml.jackson.annotation.JsonIgnore;
import giuseppeperna.GearForFit.entities.Alimenti.Alimento;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "dieta_standard_alimento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DietaStandardAlimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer grammi;
    private Double proteineG;
    private Double carboidratiG;
    private Double grassiG;
    private Double calorie;

    @ManyToOne
    @JoinColumn(name = "pasto_standard_id", nullable = false)
    @JsonIgnore
    private PastoStandard pastoStandard;

    @ManyToOne
    @JoinColumn(name = "alimento_id", nullable = false)
    @JsonIgnore
    private Alimento alimento;
}
package giuseppeperna.GearForFit.entities.Diete;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pasto_standard")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PastoStandard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nomePasto;  // es. Colazione, Pranzo, Cena, Spuntino

    private Integer ordine; // Ordine del pasto nella giornata

    @ManyToOne
    @JoinColumn(name = "dieta_standard_id", nullable = false)
    private DietaStandard dietaStandard;

    @OneToMany(mappedBy = "pastoStandard", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DietaStandardAlimento> alimenti = new ArrayList<>();
}

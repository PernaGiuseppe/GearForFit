package giuseppeperna.GearForFit.entities.Diete;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "dieta_standard")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DietaStandard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoDieta tipoDieta;

    @Column(nullable = false)
    private String nome;

    private String descrizione;

    private Integer durataSettimane;

    private double calorieTotali;
    @OneToMany(mappedBy = "dietaStandard", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PastoStandard> pasti = new ArrayList<>();
}
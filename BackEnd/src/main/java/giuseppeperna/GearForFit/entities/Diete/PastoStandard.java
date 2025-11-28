package giuseppeperna.GearForFit.entities.Diete;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @ManyToOne
    @JoinColumn(name = "dieta_id")
    @JsonIgnore
    private Dieta dieta;

    private String nomePasto;
    private Integer ordine;

    @Enumerated(EnumType.STRING)
    private GiornoSettimana giornoSettimana;

    @OneToMany(mappedBy = "pastoStandard", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DietaStandardAlimento> alimenti = new ArrayList<>();
}

package giuseppeperna.GearForFit.entities.SchedePalestra;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "giorno_allenamento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GiornoAllenamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "scheda_id", nullable = false)
    private SchedaAllenamento scheda;

    @Enumerated(EnumType.STRING)
    @Column(name = "giorno_settimana", nullable = false)
    private GiornoSettimana giornoSettimana;

    @OneToMany(mappedBy = "giorno", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Serie> serie = new ArrayList<>();
}

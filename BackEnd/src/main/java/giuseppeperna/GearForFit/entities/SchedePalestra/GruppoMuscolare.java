package giuseppeperna.GearForFit.entities.SchedePalestra;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "muscle_groups")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GruppoMuscolare {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nome;
    
}

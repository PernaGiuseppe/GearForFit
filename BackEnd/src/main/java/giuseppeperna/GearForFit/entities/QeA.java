package giuseppeperna.GearForFit.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "qea")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class QeA {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String domanda;

    @Column(nullable = false, length = 2000)
    private String risposta;
}

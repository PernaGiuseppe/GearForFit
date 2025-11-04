package giuseppeperna.GearForFit.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString

public class Esercizio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;
    private String nome;
    @Column(length = 2000)
    private String descrizione;
    private String immagineUrl;
    private String gruppoMuscolare;
    private String difficolta;
}

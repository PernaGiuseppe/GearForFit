package giuseppeperna.GearForFit.entities.Diete;
import giuseppeperna.GearForFit.entities.Utente.Utente;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "diete")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Dieta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    private String descrizione;

    private Integer durataSettimane;

    @Column(nullable = false)
    private double calorieTotali = 0.0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoDieta tipoDieta;

    @Column(name = "is_standard")
    private Boolean isStandard;

    @ManyToOne
    @JoinColumn(name = "utente_id", nullable = true)
    private Utente utente;

    @Column(name = "is_attiva")
    private Boolean isAttiva;

    @OneToMany(mappedBy = "dieta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PastoStandard> pasti = new ArrayList<>();
}
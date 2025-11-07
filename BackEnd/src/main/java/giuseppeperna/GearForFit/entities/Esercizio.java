package giuseppeperna.GearForFit.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "esercizi")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Esercizio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(name = "descrizione_esercizio")
    private String descrizione;

    @Column(nullable = false)
    private String urlImmagine;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gruppo_muscolare_id", nullable = false)
    private GruppoMuscolare gruppoMuscolare;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "attrezzo_id", nullable = false)
    private Attrezzo attrezzo;

    @Column(nullable = false)
    private Boolean isComposto;

    @Column(name = "data_creazione", nullable = false, updatable = false)
    private java.time.LocalDateTime dataCreazione;

    @PrePersist
    protected void onCreate() {
        this.dataCreazione = java.time.LocalDateTime.now();
    }
}

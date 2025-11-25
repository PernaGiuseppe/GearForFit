package giuseppeperna.GearForFit.entities.Diete;
import giuseppeperna.GearForFit.entities.Utente.Utente;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "diete_utenti")
@Getter
@Setter
@NoArgsConstructor
public class DietaUtente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "utente_id", nullable = false)
    private Utente utente;

    @ManyToOne
    @JoinColumn(name = "dieta_standard_id", nullable = false)
    private DietaStandard dietaStandard;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_dieta_obiettivo", nullable = false)
    private TipoDieta tipoDietaObiettivo;

    @Column(name = "data_assegnazione")
    private LocalDate dataAssegnazione;

    @Column(name = "is_attiva")
    private boolean attiva;

    public DietaUtente(Utente utente, DietaStandard dietaStandard, TipoDieta tipoDietaObiettivo) {
        this.utente = utente;
        this.dietaStandard = dietaStandard;
        this.tipoDietaObiettivo = tipoDietaObiettivo; // Salva l'obiettivo
        this.dataAssegnazione = LocalDate.now();
        this.attiva = true; // La nuova dieta Ã¨ sempre quella attiva
    }
}

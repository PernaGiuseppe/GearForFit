package giuseppeperna.GearForFit.runners;

import giuseppeperna.GearForFit.entities.Utente.TipoPiano;
import giuseppeperna.GearForFit.entities.Utente.TipoUtente;
import giuseppeperna.GearForFit.repositories.UtenteRepository;
import giuseppeperna.GearForFit.services.UtenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class AdminRunner implements CommandLineRunner {

    @Autowired
    private UtenteService utenteService;

    @Autowired
    private UtenteRepository utenteRepository;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    @Value("${admin.nome}")
    private String adminNome;

    @Value("${admin.cognome}")
    private String adminCognome;

    @Override
    public void run(String... args) throws Exception {

        // Verifica se esiste già un admin con questa email
        if (utenteRepository.findByEmail(adminEmail).isEmpty()) {

            System.out.println("Creazione admin in corso...⚙️  ");
            
            utenteService.creaUtenteConPiano(
                    adminEmail,
                    adminPassword,
                    adminNome,
                    adminCognome,
                    TipoUtente.ADMIN,
                    TipoPiano.ADMIN
            );
            System.out.println("✅ Admin creato con successo!");


        } else {
            System.out.println("✅ Admin già presente - Nessuna azione richiesta");
        }
    }
}


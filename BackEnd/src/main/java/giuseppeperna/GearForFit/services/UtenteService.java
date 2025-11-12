package giuseppeperna.GearForFit.services;

import giuseppeperna.GearForFit.entities.Utente.TipoPiano;
import giuseppeperna.GearForFit.entities.Utente.TipoUtente;
import giuseppeperna.GearForFit.entities.Utente.Utente;
import giuseppeperna.GearForFit.exceptions.BadRequestException;
import giuseppeperna.GearForFit.exceptions.NotFoundException;
import giuseppeperna.GearForFit.exceptions.UnauthorizedException;
import giuseppeperna.GearForFit.repositories.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UtenteService {

    @Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Trova un utente per ID
    public Utente findById(Long id) {
        return utenteRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id));
    }

    // Trova un utente per email
    public Utente findByEmail(String email) {
        return utenteRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Utente con email " + email + " non trovato"));
    }

    // Ottieni tutti gli utenti
    public List<Utente> getAllUtenti() {
        return utenteRepository.findAll();
    }

    // ← METODO PRINCIPALE (per utenti normali - sempre FREE)
    // ← METODO PRINCIPALE (per utenti normali - sempre FREE) - CON TUTTI I CONTROLLI
    public Utente creaUtente(String email, String password, String nome, String cognome, TipoUtente tipoUtente) {

        // CONTROLLO EMAIL VUOTA
        if (email == null || email.isBlank()) {
            throw new BadRequestException("Email non può essere vuota");
        }

        // CONTROLLO EMAIL DUPLICATA
        if (utenteRepository.findByEmail(email).isPresent()) {
            throw new BadRequestException("Email già registrata: " + email);
        }

        // CONTROLLO PASSWORD VUOTA E LUNGHEZZA
        if (password == null || password.isBlank()) {
            throw new BadRequestException("Password non può essere vuota");
        }

        if (password.length() < 6) {
            throw new BadRequestException("Password deve contenere almeno 6 caratteri");
        }

        // CONTROLLO NOME VUOTO
        if (nome == null || nome.isBlank()) {
            throw new BadRequestException("Nome non può essere vuoto");
        }

        // CONTROLLO COGNOME VUOTO
        if (cognome == null || cognome.isBlank()) {
            throw new BadRequestException("Cognome non può essere vuoto");
        }

        // CONTROLLO TIPO UTENTE
        if (tipoUtente == null) {
            throw new BadRequestException("Tipo utente non può essere nullo");
        }

        Utente nuovoUtente = Utente.builder()
                .email(email.toLowerCase().trim())
                .password(passwordEncoder.encode(password))
                .nome(nome.trim())
                .cognome(cognome.trim())
                .tipoUtente(tipoUtente)
                .tipoPiano(TipoPiano.FREE)  // ← SEMPRE FREE per i nuovi utenti
                .attivo(true)
                .build();

        return utenteRepository.save(nuovoUtente);
    }


    // ← METODO OVERLOAD (per admin - con piano ADMIN)
    public Utente creaUtenteConPiano(String email, String password, String nome, String cognome, TipoUtente tipoUtente, TipoPiano tipoPiano) {

        // CONTROLLO EMAIL VUOTA
        if (email == null || email.isBlank()) {
            throw new BadRequestException("Email non può essere vuota");
        }

        // CONTROLLO EMAIL DUPLICATA
        if (utenteRepository.findByEmail(email).isPresent()) {
            throw new BadRequestException("Email già registrata: " + email);
        }

        // CONTROLLO PASSWORD VUOTA E LUNGHEZZA
        if (password == null || password.isBlank()) {
            throw new BadRequestException("Password non può essere vuota");
        }

        if (password.length() < 6) {
            throw new BadRequestException("Password deve contenere almeno 6 caratteri");
        }

        // CONTROLLO NOME VUOTO
        if (nome == null || nome.isBlank()) {
            throw new BadRequestException("Nome non può essere vuoto");
        }

        // CONTROLLO COGNOME VUOTO
        if (cognome == null || cognome.isBlank()) {
            throw new BadRequestException("Cognome non può essere vuoto");
        }

        // CONTROLLO TIPO UTENTE
        if (tipoUtente == null) {
            throw new BadRequestException("Tipo utente non può essere nullo");
        }

        // CONTROLLO TIPO PIANO
        if (tipoPiano == null) {
            throw new BadRequestException("Tipo piano non può essere nullo");
        }

        Utente nuovoUtente = Utente.builder()
                .email(email.toLowerCase().trim())
                .password(passwordEncoder.encode(password))
                .nome(nome.trim())
                .cognome(cognome.trim())
                .tipoUtente(tipoUtente)
                .tipoPiano(tipoPiano)
                .attivo(true)
                .build();

        return utenteRepository.save(nuovoUtente);
    }

    // Aggiorna un utente
    public Utente aggiornaUtente(Long id, String nome, String cognome, String email) {

        // CONTROLLO ID
        if (id == null || id <= 0) {
            throw new BadRequestException("ID non valido");
        }

        Utente utente = findById(id);

        // CONTROLLO EMAIL DUPLICATA (se viene modificata)
        if (email != null && !email.isBlank()) {
            if (!utente.getEmail().equals(email) && utenteRepository.findByEmail(email).isPresent()) {
                throw new BadRequestException("Email già registrata: " + email);
            }
            utente.setEmail(email.toLowerCase().trim());
        }

        // CONTROLLO NOME
        if (nome != null && !nome.isBlank()) {
            utente.setNome(nome.trim());
        }

        // CONTROLLO COGNOME
        if (cognome != null && !cognome.isBlank()) {
            utente.setCognome(cognome.trim());
        }

        return utenteRepository.save(utente);
    }

    // Cambia la password di un utente
    public Utente cambiaPassword(Long id, String passwordVecchia, String passwordNuova) {

        // CONTROLLO ID
        if (id == null || id <= 0) {
            throw new BadRequestException("ID non valido");
        }

        // CONTROLLO PASSWORD VECCHIA
        if (passwordVecchia == null || passwordVecchia.isBlank()) {
            throw new BadRequestException("Password vecchia non può essere vuota");
        }

        // CONTROLLO PASSWORD NUOVA
        if (passwordNuova == null || passwordNuova.isBlank()) {
            throw new BadRequestException("Password nuova non può essere vuota");
        }

        if (passwordNuova.length() < 6) {
            throw new BadRequestException("Password nuova deve contenere almeno 6 caratteri");
        }

        // CONTROLLO PASSWORD DIVERSE
        if (passwordVecchia.equals(passwordNuova)) {
            throw new BadRequestException("La password nuova deve essere diversa da quella vecchia");
        }

        Utente utente = findById(id);

        // VERIFICA PASSWORD VECCHIA CORRETTA
        if (!passwordEncoder.matches(passwordVecchia, utente.getPassword())) {
            throw new UnauthorizedException("Password non corretta");
        }

        utente.setPassword(passwordEncoder.encode(passwordNuova));

        return utenteRepository.save(utente);
    }

    // Cambia il ruolo di un utente (solo ADMIN)
    public Utente cambiaRuolo(Long id, TipoUtente nuovoRuolo) {

        // CONTROLLO ID
        if (id == null || id <= 0) {
            throw new BadRequestException("ID non valido");
        }

        // CONTROLLO NUOVO RUOLO
        if (nuovoRuolo == null) {
            throw new BadRequestException("Tipo utente non può essere nullo");
        }

        Utente utente = findById(id);
        utente.setTipoUtente(nuovoRuolo);

        return utenteRepository.save(utente);
    }

    // Cambia il piano di un utente
    public Utente cambiaPiano(Long id, TipoPiano nuovoPiano) {

        // CONTROLLO ID
        if (id == null || id <= 0) {
            throw new BadRequestException("ID non valido");
        }

        // CONTROLLO NUOVO PIANO
        if (nuovoPiano == null) {
            throw new BadRequestException("Tipo piano non può essere nullo");
        }

        Utente utente = findById(id);

        // PROTEZIONE: Non permettere downgrade del piano ADMIN
        if (utente.getTipoPiano().equals(TipoPiano.ADMIN) && !nuovoPiano.equals(TipoPiano.ADMIN)) {
            throw new BadRequestException("Il piano ADMIN non può essere modificato");
        }

        utente.setTipoPiano(nuovoPiano);

        return utenteRepository.save(utente);
    }

    /*   // Disattiva un utente
       public Utente disattivaUtente(Long id) {

           // CONTROLLO ID
           if (id == null || id <= 0) {
               throw new BadRequestException("ID non valido");
           }

           Utente utente = findById(id);

           // PROTEZIONE: Non permettere disattivazione dell'admin
           if (utente.getTipoUtente().equals(TipoUtente.ADMIN)) {
               throw new BadRequestException("Non è possibile disattivare un admin");
           }

           utente.setAttivo(false);

           return utenteRepository.save(utente);
       }

       // Attiva un utente
       public Utente attivaUtente(Long id) {

           // CONTROLLO ID
           if (id == null || id <= 0) {
               throw new BadRequestException("ID non valido");
           }

           Utente utente = findById(id);
           utente.setAttivo(true);

           return utenteRepository.save(utente);
       }*/
    // Admin resetta password utente (senza conoscere quella vecchia)
    public Utente resetPasswordByAdmin(Long userId, String nuovaPassword) {
        Utente utente = findById(userId);
        utente.setPassword(passwordEncoder.encode(nuovaPassword));
        return utenteRepository.save(utente);
    }

    // Elimina un utente
    public void eliminaUtente(Long id) {

        // CONTROLLO ID
        if (id == null || id <= 0) {
            throw new BadRequestException("ID non valido");
        }

        Utente utente = findById(id);

        // PROTEZIONE: Non permettere eliminazione dell'admin
        if (utente.getTipoUtente().equals(TipoUtente.ADMIN)) {
            throw new BadRequestException("Non è possibile eliminare un admin");
        }

        utenteRepository.delete(utente);
    }
}

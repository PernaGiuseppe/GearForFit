package giuseppeperna.GearForFit.services;

import giuseppeperna.GearForFit.entities.SchedePalestra.*;
import giuseppeperna.GearForFit.entities.Utente.Utente;
import giuseppeperna.GearForFit.exceptions.NotFoundException;
import giuseppeperna.GearForFit.payloads.SchedaAllenamentoRequestDTO;
import giuseppeperna.GearForFit.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SchedaAllenamentoService {

    @Autowired
    private SchedaAllenamentoRepository schedaRepository;

    @Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    private EsercizioRepository esercizioRepository;

    @Autowired
    private SchemaSerieRepository schemaSerieRepository;

    // ============= METODI DI LETTURA =============

    public SchedaAllenamento getSchedaById(Long id) {
        return schedaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Scheda non trovata"));
    }

    public List<SchedaAllenamento> getAllSchede() {
        return schedaRepository.findAll();
    }

    public List<SchedaAllenamento> getSchedeByUtente(Long utenteId) {
        Utente utente = utenteRepository.findById(utenteId)
                .orElseThrow(() -> new NotFoundException("Utente non trovato"));
        return schedaRepository.findByUtente(utente);
    }

    // ============= METODI CRUD (ADMIN) =============

    public SchedaAllenamento creaScheda(SchedaAllenamentoRequestDTO request) {
        SchedaAllenamento scheda = SchedaAllenamento.builder()
                .nome(request.nome())
                .descrizione(request.descrizione())
                .obiettivo(request.obiettivoAllenamento())
                .giornoSettimana(request.giornoSettimana())
                .build();

        if (request.utenteId() != null) {
            Utente utente = utenteRepository.findById(request.utenteId())
                    .orElseThrow(() -> new NotFoundException("Utente non trovato"));
            scheda.setUtente(utente);
        }

        return schedaRepository.save(scheda);
    }

    public SchedaAllenamento aggiornaScheda(Long id, SchedaAllenamentoRequestDTO request) {
        SchedaAllenamento scheda = getSchedaById(id);

        scheda.setNome(request.nome());
        scheda.setDescrizione(request.descrizione());
        scheda.setObiettivo(request.obiettivoAllenamento());
        scheda.setGiornoSettimana(request.giornoSettimana());

        if (request.utenteId() != null) {
            Utente utente = utenteRepository.findById(request.utenteId())
                    .orElseThrow(() -> new NotFoundException("Utente non trovato"));
            scheda.setUtente(utente);
        }

        return schedaRepository.save(scheda);
    }

    public void eliminaScheda(Long id) {
        SchedaAllenamento scheda = getSchedaById(id);
        schedaRepository.delete(scheda);
    }

    // Assegna scheda a utente
    public SchedaAllenamento assegnaScheda(Long schedaId, Long utenteId) {
        SchedaAllenamento scheda = getSchedaById(schedaId);
        Utente utente = utenteRepository.findById(utenteId)
                .orElseThrow(() -> new NotFoundException("Utente non trovato"));

        scheda.setUtente(utente);
        return schedaRepository.save(scheda);
    }

    // ============= SCHEDE STANDARD =============

    public SchedaAllenamento creaSchedaStandard(SchedaAllenamentoRequestDTO request) {
        SchedaAllenamento scheda = SchedaAllenamento.builder()
                .nome(request.nome())
                .descrizione(request.descrizione())
                .obiettivo(request.obiettivoAllenamento())
                .giornoSettimana(request.giornoSettimana())
                .isStandard(true)
                .build();

        SchedaAllenamento scheduleSalvata = schedaRepository.save(scheda);

        // Aggiungi esercizi se presenti
        if (request.esercizi() != null && !request.esercizi().isEmpty()) {
            List<EsercizioScheda> esercizi = request.esercizi().stream()
                    .map(eReq -> {
                        Esercizio esercizio = esercizioRepository.findById(eReq.esercizioId())
                                .orElseThrow(() -> new NotFoundException("Esercizio non trovato"));

                        SchemaSerie schemaSerie = schemaSerieRepository.findById(eReq.schemaSerieId())
                                .orElseThrow(() -> new NotFoundException("Schema serie non trovato"));

                        return EsercizioScheda.builder()
                                .schedaAllenamento(scheduleSalvata)
                                .esercizio(esercizio)
                                .schemaSerie(schemaSerie)
                                .posizione(eReq.posizione())
                                .secondiRiposo(eReq.secondiRiposo())
                                .note(eReq.note())
                                .build();
                    })
                    .collect(Collectors.toList());

            scheduleSalvata.setEsercizi(esercizi);
            return schedaRepository.save(scheduleSalvata);
        }

        return scheduleSalvata;
    }

    public List<SchedaAllenamento> getSchedeStandard() {
        return schedaRepository.findByIsStandardTrue();
    }

    public List<SchedaAllenamento> getSchedeStandardPerObiettivo(ObiettivoAllenamento obiettivo) {
        return schedaRepository.findByIsStandardTrueAndObiettivo(obiettivo);
    }

    public SchedaAllenamento duplicaSchedaStandardPerUtente(Long schedaStandardId, Long utenteId) {
        SchedaAllenamento schedaStandard = getSchedaById(schedaStandardId);

        if (!schedaStandard.isStandard()) {
            throw new RuntimeException("La scheda selezionata non è una scheda standard");
        }

        Utente utente = utenteRepository.findById(utenteId)
                .orElseThrow(() -> new NotFoundException("Utente non trovato"));

        SchedaAllenamento nuovaScheda = SchedaAllenamento.builder()
                .nome(schedaStandard.getNome() + " (Copia)")
                .descrizione(schedaStandard.getDescrizione())
                .obiettivo(schedaStandard.getObiettivo())
                .giornoSettimana(schedaStandard.getGiornoSettimana())
                .isStandard(false)
                .utente(utente)
                .build();

        SchedaAllenamento nuovaSchedaSalvata = schedaRepository.save(nuovaScheda);

        // Copia gli esercizi
        List<EsercizioScheda> eserciziCopia = schedaStandard.getEsercizi().stream()
                .map(es -> EsercizioScheda.builder()
                        .schedaAllenamento(nuovaSchedaSalvata)
                        .esercizio(es.getEsercizio())
                        .schemaSerie(es.getSchemaSerie())
                        .posizione(es.getPosizione())
                        .secondiRiposo(es.getSecondiRiposo())
                        .note(es.getNote())
                        .build())
                .collect(Collectors.toList());

        nuovaSchedaSalvata.setEsercizi(eserciziCopia);
        return schedaRepository.save(nuovaSchedaSalvata);
    }
}

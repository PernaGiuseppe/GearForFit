package giuseppeperna.GearForFit.services;

import giuseppeperna.GearForFit.entities.*;
import giuseppeperna.GearForFit.payloads.EsercizioSchedaDTO;
import giuseppeperna.GearForFit.payloads.SchedaAllenamentoDTO;
import giuseppeperna.GearForFit.payloads.SchedaAllenamentoRequestDTO;
import giuseppeperna.GearForFit.repositories.EsercizioRepository;
import giuseppeperna.GearForFit.repositories.SchedaAllenamentoRepository;
import giuseppeperna.GearForFit.repositories.SchemaSerieRepository;
import giuseppeperna.GearForFit.repositories.UtenteRepository;
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

    public SchedaAllenamentoDTO creaScheda(Long utenteId, SchedaAllenamentoRequestDTO request) {

        Utente utente = utenteRepository.findById(utenteId)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        SchedaAllenamento scheda = SchedaAllenamento.builder()
                .nome(request.nome())
                .descrizione(request.descrizione())
                .obiettivo(request.obiettivo())
                .giornoSettimana(request.giornoSettimana())
                .utente(utente)
                .build();

        SchedaAllenamento scheduleSalvata = schedaRepository.save(scheda);

        // Aggiungi gli esercizi
        List<EsercizioScheda> esercizi = request.esercizi().stream()
                .map(eReq -> {
                    Esercizio esercizio = esercizioRepository.findById(eReq.esercizioId())
                            .orElseThrow(() -> new RuntimeException("Esercizio non trovato"));

                    SchemaSerie schemaSerie = schemaSerieRepository.findById(eReq.schemaSerieId())
                            .orElseThrow(() -> new RuntimeException("Schema serie non trovato"));

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
        SchedaAllenamento schedaFinale = schedaRepository.save(scheduleSalvata);

        return convertiADTO(schedaFinale);
    }

    public SchedaAllenamentoDTO ottieniSchedaPerId(Long schedaId) {
        SchedaAllenamento scheda = schedaRepository.findById(schedaId)
                .orElseThrow(() -> new RuntimeException("Scheda non trovata"));
        return convertiADTO(scheda);
    }

    public List<SchedaAllenamentoDTO> ottieniSchedeUtente(Long utenteId) {
        Utente utente = utenteRepository.findById(utenteId)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        return schedaRepository.findByUtente(utente).stream()
                .map(this::convertiADTO)
                .collect(Collectors.toList());
    }

    public SchedaAllenamentoDTO aggiornaScheda(Long schedaId, SchedaAllenamentoRequestDTO request) {
        SchedaAllenamento scheda = schedaRepository.findById(schedaId)
                .orElseThrow(() -> new RuntimeException("Scheda non trovata"));

        scheda.setNome(request.nome());
        scheda.setDescrizione(request.descrizione());
        scheda.setObiettivo(request.obiettivo());
        scheda.setGiornoSettimana(request.giornoSettimana());

        // Rimuovi vecchi esercizi
        scheda.getEsercizi().clear();

        // Aggiungi nuovi esercizi
        List<EsercizioScheda> esercizi = request.esercizi().stream()
                .map(eReq -> {
                    Esercizio esercizio = esercizioRepository.findById(eReq.esercizioId())
                            .orElseThrow(() -> new RuntimeException("Esercizio non trovato"));

                    SchemaSerie schemaSerie = schemaSerieRepository.findById(eReq.schemaSerieId())
                            .orElseThrow(() -> new RuntimeException("Schema serie non trovato"));

                    return EsercizioScheda.builder()
                            .schedaAllenamento(scheda)
                            .esercizio(esercizio)
                            .schemaSerie(schemaSerie)
                            .posizione(eReq.posizione())
                            .secondiRiposo(eReq.secondiRiposo())
                            .note(eReq.note())
                            .build();
                })
                .collect(Collectors.toList());

        scheda.setEsercizi(esercizi);
        SchedaAllenamento schedaAggiornata = schedaRepository.save(scheda);

        return convertiADTO(schedaAggiornata);
    }

    public void eliminaScheda(Long schedaId) {
        SchedaAllenamento scheda = schedaRepository.findById(schedaId)
                .orElseThrow(() -> new RuntimeException("Scheda non trovata"));
        schedaRepository.delete(scheda);
    }

    // MODIFICA: Questo metodo ora crea un record SchedaAllenamentoDTO
    // Sostituisce l'uso di SchedaAllenamentoDTO.builder() con il costruttore del record
    private SchedaAllenamentoDTO convertiADTO(SchedaAllenamento scheda) {
        return new SchedaAllenamentoDTO(
                scheda.getId(),
                scheda.getNome(),
                scheda.getDescrizione(),
                scheda.getObiettivo(),
                scheda.getGiornoSettimana(),
                scheda.getEsercizi().stream()
                        .map(this::convertiEsercizioADTO)
                        .collect(Collectors.toList())
        );
    }

    // MODIFICA: Questo metodo ora crea un record EsercizioSchedaDTO
    // Sostituisce l'uso di EsercizioSchedaDTO.builder() con il costruttore del record
    private EsercizioSchedaDTO convertiEsercizioADTO(EsercizioScheda esercizioScheda) {
        return new EsercizioSchedaDTO(
                esercizioScheda.getId(),
                esercizioScheda.getPosizione(),
                null, // Puoi popolare EsercizioDTO se necessario
                null, // Puoi popolare SchemaSerieDTO se necessario
                esercizioScheda.getSecondiRiposo(),
                esercizioScheda.getNote()
        );
    }
}

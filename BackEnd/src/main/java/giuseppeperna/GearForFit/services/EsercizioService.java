package giuseppeperna.GearForFit.services;

import giuseppeperna.GearForFit.entities.SchedePalestra.Attrezzo;
import giuseppeperna.GearForFit.entities.SchedePalestra.Esercizio;
import giuseppeperna.GearForFit.entities.SchedePalestra.GruppoMuscolare;
import giuseppeperna.GearForFit.exceptions.NotFoundException;
import giuseppeperna.GearForFit.payloads.EsercizioDTO;
import giuseppeperna.GearForFit.payloads.EsercizioRequestDTO;
import giuseppeperna.GearForFit.repositories.AttrezzoRepository;
import giuseppeperna.GearForFit.repositories.EsercizioRepository;
import giuseppeperna.GearForFit.repositories.GruppoMuscolareRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class EsercizioService {

    @Autowired
    private EsercizioRepository esercizioRepository;

    @Autowired
    private GruppoMuscolareRepository gruppoMuscolareRepository;

    @Autowired
    private AttrezzoRepository attrezzoRepository;

    // ============= METODI DI LETTURA =============

    public EsercizioDTO ottieniEsercizioPerId(Long id) {
        Esercizio esercizio = esercizioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Esercizio con id " + id + " non trovato"));
        return convertiADTO(esercizio);
    }

    public List<EsercizioDTO> ottieniTuttiEsercizi() {
        return esercizioRepository.findAll().stream()
                .map(this::convertiADTO)
                .collect(Collectors.toList());
    }

    public List<EsercizioDTO> ottieniEsercizioPerGruppo(Long gruppoId) {
        return esercizioRepository.findByGruppoMuscolareId(gruppoId).stream()
                .map(this::convertiADTO)
                .collect(Collectors.toList());
    }

    // ============= METODI CRUD (ADMIN) =============

    // Crea un nuovo esercizio
    public EsercizioDTO creaEsercizio(EsercizioRequestDTO request) {
        // Verifica che gruppo muscolare e attrezzo esistano
        GruppoMuscolare gruppo = gruppoMuscolareRepository.findById(request.gruppoMuscolareId())
                .orElseThrow(() -> new NotFoundException("Gruppo muscolare con id " + request.gruppoMuscolareId() + " non trovato"));

        Attrezzo attrezzo = attrezzoRepository.findById(request.attrezzoId())
                .orElseThrow(() -> new NotFoundException("Attrezzo con id " + request.attrezzoId() + " non trovato"));

        // Crea l'esercizio
        Esercizio esercizio = Esercizio.builder()
                .nome(request.nome())
                .descrizione(request.descrizione())
                .urlImmagine(request.urlImmagine())
                .gruppoMuscolare(gruppo)
                .attrezzo(attrezzo)
                .isComposto(request.isComposto())
                .build();

        Esercizio savedEsercizio = esercizioRepository.save(esercizio);
        return convertiADTO(savedEsercizio);
    }

    // Aggiorna un esercizio esistente
    public EsercizioDTO aggiornaEsercizio(Long id, EsercizioRequestDTO request) {
        Esercizio esercizio = esercizioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Esercizio con id " + id + " non trovato"));

        // Verifica che gruppo muscolare e attrezzo esistano
        GruppoMuscolare gruppo = gruppoMuscolareRepository.findById(request.gruppoMuscolareId())
                .orElseThrow(() -> new NotFoundException("Gruppo muscolare con id " + request.gruppoMuscolareId() + " non trovato"));

        Attrezzo attrezzo = attrezzoRepository.findById(request.attrezzoId())
                .orElseThrow(() -> new NotFoundException("Attrezzo con id " + request.attrezzoId() + " non trovato"));

        // Aggiorna i campi
        esercizio.setNome(request.nome());
        esercizio.setDescrizione(request.descrizione());
        esercizio.setUrlImmagine(request.urlImmagine());
        esercizio.setGruppoMuscolare(gruppo);
        esercizio.setAttrezzo(attrezzo);
        esercizio.setIsComposto(request.isComposto());

        Esercizio updatedEsercizio = esercizioRepository.save(esercizio);
        return convertiADTO(updatedEsercizio);
    }

    // Elimina un esercizio
    public void eliminaEsercizio(Long id) {
        Esercizio esercizio = esercizioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Esercizio con id " + id + " non trovato"));
        esercizioRepository.delete(esercizio);
    }

    // Filtra esercizi per nome (ricerca)
    public List<EsercizioDTO> cercaEserciziPerNome(String nome) {
        return esercizioRepository.findByNomeContainingIgnoreCase(nome).stream()
                .map(this::convertiADTO)
                .collect(Collectors.toList());
    }

    // Filtra esercizi per attrezzo
    public List<EsercizioDTO> ottieniEserciziPerAttrezzo(Long attrezzoId) {
        return esercizioRepository.findByAttrezzoId(attrezzoId).stream()
                .map(this::convertiADTO)
                .collect(Collectors.toList());
    }

    // Filtra esercizi composti
    public List<EsercizioDTO> ottieniEserciziComposti() {
        return esercizioRepository.findByIsCompostoTrue().stream()
                .map(this::convertiADTO)
                .collect(Collectors.toList());
    }

    // ============= METODO HELPER =============

    private EsercizioDTO convertiADTO(Esercizio esercizio) {
        return new EsercizioDTO(
                esercizio.getId(),
                esercizio.getNome(),
                esercizio.getDescrizione(),
                esercizio.getUrlImmagine(),
                esercizio.getGruppoMuscolare().getNome(),
                esercizio.getAttrezzo().getNome(),
                esercizio.getIsComposto()
        );
    }
}

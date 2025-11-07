package giuseppeperna.GearForFit.services;

import giuseppeperna.GearForFit.entities.Esercizio;
import giuseppeperna.GearForFit.payloads.EsercizioDTO;
import giuseppeperna.GearForFit.repositories.EsercizioRepository;
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

    public EsercizioDTO ottieniEsercizioPerId(Long id) {
        Esercizio esercizio = esercizioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Esercizio non trovato"));
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

    // MODIFICA: Questo metodo ora crea un record EsercizioDTO
    // Sostituisce l'uso di EsercizioDTO.builder() con il costruttore del record
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

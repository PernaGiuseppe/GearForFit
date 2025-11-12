package giuseppeperna.GearForFit.services;

import giuseppeperna.GearForFit.entities.Utente.QeA; // ✅ CORRETTO
import giuseppeperna.GearForFit.exceptions.NotFoundException;
import giuseppeperna.GearForFit.payloads.QeARequestDTO;
import giuseppeperna.GearForFit.payloads.QeAResponseDTO;
import giuseppeperna.GearForFit.repositories.QeARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QeAService {

    @Autowired
    private QeARepository qeaRepository;

    // Ottieni tutte le Q&A
    public List<QeAResponseDTO> getAllQeA() {
        return qeaRepository.findAll().stream()
                .map(qea -> new QeAResponseDTO(qea.getId(), qea.getDomanda(), qea.getRisposta()))
                .collect(Collectors.toList());
    }

    // Ottieni una singola Q&A per ID
    public QeAResponseDTO getQeAById(Long id) {
        QeA qea = qeaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Q&A con id " + id + " non trovata"));
        return new QeAResponseDTO(qea.getId(), qea.getDomanda(), qea.getRisposta());
    }

    // Crea nuova Q&A (solo ADMIN)
    public QeAResponseDTO creaQeA(QeARequestDTO body) {
        QeA qea = new QeA();
        qea.setDomanda(body.domanda());
        qea.setRisposta(body.risposta());
        QeA savedQeA = qeaRepository.save(qea);
        return new QeAResponseDTO(savedQeA.getId(), savedQeA.getDomanda(), savedQeA.getRisposta());
    }

    // Aggiorna Q&A (solo ADMIN)
    public QeAResponseDTO aggiornaQeA(Long id, QeARequestDTO body) {
        QeA qea = qeaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Q&A con id " + id + " non trovata"));
        qea.setDomanda(body.domanda());
        qea.setRisposta(body.risposta());
        QeA updatedQeA = qeaRepository.save(qea);
        return new QeAResponseDTO(updatedQeA.getId(), updatedQeA.getDomanda(), updatedQeA.getRisposta());
    }

    // Elimina Q&A (solo ADMIN)
    public void eliminaQeA(Long id) {
        QeA qea = qeaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Q&A con id " + id + " non trovata"));
        qeaRepository.delete(qea);
    }

    // Cerca Q&A per keyword
    public List<QeAResponseDTO> cercaQeA(String keyword) {
        return qeaRepository.findByDomandaContainingIgnoreCaseOrRispostaContainingIgnoreCase(keyword, keyword)
                .stream()
                .map(qea -> new QeAResponseDTO(qea.getId(), qea.getDomanda(), qea.getRisposta()))
                .collect(Collectors.toList());
    }
}

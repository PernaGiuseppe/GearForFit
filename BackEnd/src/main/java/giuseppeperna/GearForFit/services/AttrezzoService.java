package giuseppeperna.GearForFit.services;

import giuseppeperna.GearForFit.entities.SchedePalestra.Attrezzo;
import giuseppeperna.GearForFit.exceptions.NotFoundException;
import giuseppeperna.GearForFit.payloads.AttrezzoRequestDTO;
import giuseppeperna.GearForFit.repositories.AttrezzoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AttrezzoService {

    @Autowired
    private AttrezzoRepository attrezzoRepository;

    public Attrezzo creaAttrezzo(AttrezzoRequestDTO body) {
        Attrezzo attrezzo = new Attrezzo();
        attrezzo.setNome(body.nome());
        return attrezzoRepository.save(attrezzo);
    }

    public List<Attrezzo> getTutti() {
        return attrezzoRepository.findAll();
    }

    public Attrezzo findById(Long id) {
        return attrezzoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Attrezzo non trovato"));
    }

    public Attrezzo aggiorna(Long id, AttrezzoRequestDTO body) {
        Attrezzo attrezzo = findById(id);
        attrezzo.setNome(body.nome());
        return attrezzoRepository.save(attrezzo);
    }

    public void elimina(Long id) {
        Attrezzo attrezzo = findById(id);
        attrezzoRepository.delete(attrezzo);
    }
}

package giuseppeperna.GearForFit.services;

import giuseppeperna.GearForFit.entities.SchedePalestra.GruppoMuscolare;
import giuseppeperna.GearForFit.exceptions.NotFoundException;
import giuseppeperna.GearForFit.payloads.GruppoMuscolareRequestDTO;
import giuseppeperna.GearForFit.repositories.GruppoMuscolareRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GruppoMuscolareService {

    @Autowired
    private GruppoMuscolareRepository gruppoMuscolareRepository;

    public GruppoMuscolare creaGruppoMuscolare(GruppoMuscolareRequestDTO body) {
        GruppoMuscolare gruppo = new GruppoMuscolare();
        gruppo.setNome(body.nome());
        return gruppoMuscolareRepository.save(gruppo);
    }

    public List<GruppoMuscolare> getTutti() {
        return gruppoMuscolareRepository.findAll();
    }

    public GruppoMuscolare findById(Long id) {
        return gruppoMuscolareRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Gruppo muscolare non trovato"));
    }

    public GruppoMuscolare aggiorna(Long id, GruppoMuscolareRequestDTO body) {
        GruppoMuscolare gruppo = findById(id);
        gruppo.setNome(body.nome());
        return gruppoMuscolareRepository.save(gruppo);
    }

    public void elimina(Long id) {
        GruppoMuscolare gruppo = findById(id);
        gruppoMuscolareRepository.delete(gruppo);
    }
}

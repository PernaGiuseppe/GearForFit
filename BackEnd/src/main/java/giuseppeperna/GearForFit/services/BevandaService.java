package giuseppeperna.GearForFit.services;

import giuseppeperna.GearForFit.entities.Bevande.Bevanda;
import giuseppeperna.GearForFit.entities.Bevande.BevandaAlcolica;
import giuseppeperna.GearForFit.entities.Bevande.BevandaAnalcolica;
import giuseppeperna.GearForFit.entities.Bevande.BevandaEnergetica;
import giuseppeperna.GearForFit.repositories.BevandaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BevandaService {

    @Autowired
    private BevandaRepository bevandaRepository;

    // ==================== MEDTODI CREATE ====================

    public BevandaAlcolica saveBevandaAlcolica(BevandaAlcolica bevanda) {
        bevanda.setTipologia("Bevanda");
        return (BevandaAlcolica) bevandaRepository.save(bevanda);
    }

    public BevandaAnalcolica saveBevandaAnalcolica(BevandaAnalcolica bevanda) {
        bevanda.setTipologia("Bevanda");
        return (BevandaAnalcolica) bevandaRepository.save(bevanda);
    }

    public BevandaEnergetica saveBevandaEnergetica(BevandaEnergetica bevanda) {
        bevanda.setTipologia("Bevanda");
        return (BevandaEnergetica) bevandaRepository.save(bevanda);
    }

    // ==================== MEDTODI GET ====================

    public List<Bevanda> findAllBevande() {
        return bevandaRepository.findAll();
    }

    public Page<Bevanda> findAllBevande(Pageable pageable) {
        return bevandaRepository.findAll(pageable);
    }

    public Optional<Bevanda> findBevandaById(Long id) {
        return bevandaRepository.findById(id);
    }

    public Optional<Bevanda> findBevandaByNome(String nome) {
        return bevandaRepository.findByNome(nome);
    }

    public List<Bevanda> searchBevandeByNome(String nome) {
        return bevandaRepository.findByNomeContainingIgnoreCase(nome);
    }

    // ==================== FILTRI SECONDO ATTRIBUTI ====================

    public List<Bevanda> findBevandeByCaloricRange(double min, double max) {
        return bevandaRepository.findByCaloriePer100gBetween(min, max);
    }

    public List<Bevanda> findBevandeByProteinRange(double min, double max) {
        return bevandaRepository.findByProteinePer100gBetween(min, max);
    }


    // ==================== MEDTODI UPDATE ====================

    public Bevanda updateBevanda(Long id, Bevanda bevandaAggiornata) {
        return bevandaRepository.findById(id)
                .map(bevanda -> {
                    bevanda.setNome(bevandaAggiornata.getNome());
                    bevanda.setCaloriePer100g(bevandaAggiornata.getCaloriePer100g());
                    bevanda.setProteinePer100g(bevandaAggiornata.getProteinePer100g());
                    bevanda.setCarboidratiPer100g(bevandaAggiornata.getCarboidratiPer100g());
                    bevanda.setGrassiPer100g(bevandaAggiornata.getGrassiPer100g());
                    bevanda.setFibrePer100g(bevandaAggiornata.getFibrePer100g());
                    return bevandaRepository.save(bevanda);
                })
                .orElseThrow(() -> new RuntimeException("Bevanda non trovata con ID: " + id));
    }

    // ==================== MEDTODI DELETE ====================

    public void deleteBevanda(Long id) {
        if (!bevandaRepository.existsById(id)) {
            throw new RuntimeException("Bevanda non trovata con ID: " + id);
        }
        bevandaRepository.deleteById(id);
    }

    public void deleteAllBevande() {
        bevandaRepository.deleteAll();
    }

    // ==================== MEDTODI UTILITY ====================

    public long countBevande() {
        return bevandaRepository.count();
    }

    public boolean existsById(Long id) {
        return bevandaRepository.existsById(id);
    }
}

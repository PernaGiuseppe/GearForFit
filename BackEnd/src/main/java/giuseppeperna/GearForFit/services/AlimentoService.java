package giuseppeperna.GearForFit.services;

import giuseppeperna.GearForFit.entities.Alimenti.*;
import giuseppeperna.GearForFit.repositories.AlimentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AlimentoService {

    @Autowired
    private AlimentoRepository alimentoRepository;

    // ==================== METODI POST ====================

    public Carne saveCarne(Carne alimento) {
        alimento.setTipologia("Alimento");
        return (Carne) alimentoRepository.save(alimento);
    }

    public Condimento saveCondimento(Condimento alimento) {
        alimento.setTipologia("Alimento");
        return (Condimento) alimentoRepository.save(alimento);
    }

    public Dolce saveDolce(Dolce alimento) {
        alimento.setTipologia("Alimento");
        return (Dolce) alimentoRepository.save(alimento);
    }

    public Carboidrato saveCarboidrato(Carboidrato alimento) {
        alimento.setTipologia("Alimento");
        return (Carboidrato) alimentoRepository.save(alimento);
    }

    public Frutta saveFrutta(Frutta alimento) {
        alimento.setTipologia("Alimento");
        return (Frutta) alimentoRepository.save(alimento);
    }

    public Latticino saveLatticino(Latticino alimento) {
        alimento.setTipologia("Alimento");
        return (Latticino) alimentoRepository.save(alimento);
    }

    public Legume saveLegume(Legume alimento) {
        alimento.setTipologia("Alimento");
        return (Legume) alimentoRepository.save(alimento);
    }

    public Pesce savePesce(Pesce alimento) {
        alimento.setTipologia("Alimento");
        return (Pesce) alimentoRepository.save(alimento);
    }


    public Verdura saveVerdura(Verdura alimento) {
        alimento.setTipologia("Alimento");
        return (Verdura) alimentoRepository.save(alimento);
    }
    // ==================== METODI GET ====================

    public List<Alimento> findAllAlimenti() {
        return alimentoRepository.findAll();
    }

    public Page<Alimento> findAllAlimenti(Pageable pageable) {
        return alimentoRepository.findAll(pageable);
    }

    public Optional<Alimento> findAlimentoById(Long id) {
        return alimentoRepository.findById(id);
    }

    public Optional<Alimento> findAlimentoByNome(String nome) {
        return alimentoRepository.findByNome(nome);
    }

    public List<Alimento> searchAlimentiByNome(String nome) {
        return alimentoRepository.findByNomeContainingIgnoreCase(nome);
    }

    // ==================== FILTRI SECONDO ATTRIBUTI ====================

    public List<Alimento> findAlimentiByCaloricRange(double min, double max) {
        return alimentoRepository.findByCaloriePer100gBetween(min, max);
    }

    public List<Alimento> findAlimentiByProteinRange(double min, double max) {
        return alimentoRepository.findByProteinePer100gBetween(min, max);
    }

    public List<Alimento> findAlimentiByCarboidratRange(double min, double max) {
        return alimentoRepository.findByCarboidratiPer100gBetween(min, max);
    }

    public List<Alimento> findAlimentiByFatRange(double min, double max) {
        return alimentoRepository.findByGrassiPer100gBetween(min, max);
    }

    public List<Alimento> findAlimentiByFiberRange(double min, double max) {
        return alimentoRepository.findByFibrePer100gBetween(min, max);
    }

    // ==================== METODI UPDATE ====================

    public Alimento updateAlimento(Long id, Alimento alimentoAggiornato) {
        return alimentoRepository.findById(id)
                .map(alimento -> {
                    alimento.setNome(alimentoAggiornato.getNome());
                    alimento.setCaloriePer100g(alimentoAggiornato.getCaloriePer100g());
                    alimento.setProteinePer100g(alimentoAggiornato.getProteinePer100g());
                    alimento.setCarboidratiPer100g(alimentoAggiornato.getCarboidratiPer100g());
                    alimento.setGrassiPer100g(alimentoAggiornato.getGrassiPer100g());
                    alimento.setFibrePer100g(alimentoAggiornato.getFibrePer100g());
                    return alimentoRepository.save(alimento);
                })
                .orElseThrow(() -> new RuntimeException("Alimento non trovato con ID: " + id));
    }

    // ==================== METODI DELETE ====================

    public void deleteAlimento(Long id) {
        if (!alimentoRepository.existsById(id)) {
            throw new RuntimeException("Alimento non trovato con ID: " + id);
        }
        alimentoRepository.deleteById(id);
    }

    public void deleteAllAlimenti() {
        alimentoRepository.deleteAll();
    }

    // ==================== METODI UTILITY ====================

    public long countAlimenti() {
        return alimentoRepository.count();
    }

    public boolean existsById(Long id) {
        return alimentoRepository.existsById(id);
    }
}

package giuseppeperna.GearForFit.controllers;

import giuseppeperna.GearForFit.entities.SchedePalestra.Esercizio;
import giuseppeperna.GearForFit.services.EsercizioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/esercizi")
@PreAuthorize("hasAuthority('ADMIN')")
public class EsercizioController {

    @Autowired
    private EsercizioService esercizioService;

    // Ottieni tutti gli esercizi
    @GetMapping
    public List<Esercizio> ottieniTuttiEsercizi() {
        return esercizioService.getAllEsercizi();
    }

    // Ottieni esercizio per ID
    @GetMapping("/{id}")
    public Esercizio ottieniEsercizioPerId(@PathVariable Long id) {
        return esercizioService.getEsercizioById(id);
    }

    // Ottieni esercizi per gruppo muscolare
    @GetMapping("/gruppo/{gruppoId}")
    public List<Esercizio> ottieniEsercizioPerGruppo(@PathVariable Long gruppoId) {
        return esercizioService.getEserciziByGruppoMuscolare(gruppoId);
    }
}

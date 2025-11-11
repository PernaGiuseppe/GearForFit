package giuseppeperna.GearForFit.controllers;

import giuseppeperna.GearForFit.payloads.EsercizioDTO;
import giuseppeperna.GearForFit.services.EsercizioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/esercizi")
public class EsercizioController {

    @Autowired
    private EsercizioService esercizioService;

    // Ottieni tutti gli esercizi
    @GetMapping
    public List<EsercizioDTO> ottieniTuttiEsercizi() {
        return esercizioService.ottieniTuttiEsercizi();
    }

    // Ottieni esercizio per ID
    @GetMapping("/{id}")
    public EsercizioDTO ottieniEsercizioPerId(@PathVariable Long id) {
        return esercizioService.ottieniEsercizioPerId(id);
    }

    // Ottieni esercizi per gruppo muscolare
    @GetMapping("/gruppo/{gruppoId}")
    public List<EsercizioDTO> ottieniEsercizioPerGruppo(@PathVariable Long gruppoId) {
        return esercizioService.ottieniEsercizioPerGruppo(gruppoId);
    }
}

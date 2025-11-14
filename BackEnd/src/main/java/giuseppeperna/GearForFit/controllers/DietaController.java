package giuseppeperna.GearForFit.controllers;

import giuseppeperna.GearForFit.entities.Diete.CalcoloBMR;
import giuseppeperna.GearForFit.entities.Diete.DietaStandard;
import giuseppeperna.GearForFit.entities.Diete.TipoDieta;
import giuseppeperna.GearForFit.payloads.CalcoloBMRDTO;
import giuseppeperna.GearForFit.payloads.DietaStandardDTO;
import giuseppeperna.GearForFit.services.DietaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/diete")
public class DietaController {

    @Autowired
    private DietaService dietaService;

    @PostMapping("/personalizzata")
    public ResponseEntity<DietaStandardDTO> generaDietaPersonalizzata(
            @RequestBody DietaRequest dietaRequest
    ) {
        CalcoloBMRDTO bmrDto = dietaRequest.calcoloBMR();
        CalcoloBMR profilo = new CalcoloBMR();
        profilo.setPeso(bmrDto.peso());
        profilo.setAltezza(bmrDto.altezza());
        profilo.setEta(bmrDto.eta());
        profilo.setSesso(bmrDto.sesso());
        profilo.setLivelloAttivita(bmrDto.livelloAttivita());

        DietaStandardDTO dieta = dietaService.generaDietaStandardPersonalizzata(profilo, dietaRequest.tipoDieta());
        return ResponseEntity.ok(dieta);
    }
    @GetMapping("/standard")
    public List<DietaStandardDTO> getDieteStandard() {
        return dietaService.getAllDieteStandard();
    }
    @GetMapping("/standard/{id}")
    public ResponseEntity<DietaStandardDTO> getDietaStandardById(@PathVariable Long id) {
        return dietaService.getDietaStandardById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    public record DietaRequest(
            CalcoloBMRDTO calcoloBMR,
            TipoDieta tipoDieta
    ){}

   /* @GetMapping("/{dietaId}")
    public DietaStandardDTO getDietaById(@PathVariable long dietaId) {
        DietaStandard dieta = dietaService.findById(dietaId);
        return dietaService.convertToDTO(dieta);
    }*/

   /* @GetMapping("/{dietaId}")
    public DietaStandardDTO getDietaById(@PathVariable long dietaId) {
        return dietaService.convertToDTO(dietaService.findById(dietaId));
    }
*/
    @GetMapping
    public org.springframework.data.domain.Page<DietaStandard> getDiete(@RequestParam(defaultValue = "0") int page,
                                                                        @RequestParam(defaultValue = "10") int size,
                                                                        @RequestParam(defaultValue = "id") String orderBy) {
        return dietaService.getDiete(page, size, orderBy);
    }
    }


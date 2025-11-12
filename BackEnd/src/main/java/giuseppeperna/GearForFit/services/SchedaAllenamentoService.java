package giuseppeperna.GearForFit.services;

import giuseppeperna.GearForFit.entities.SchedePalestra.*;
import giuseppeperna.GearForFit.entities.Utente.Utente;
import giuseppeperna.GearForFit.exceptions.NotFoundException;
import giuseppeperna.GearForFit.exceptions.UnauthorizedException;
import giuseppeperna.GearForFit.payloads.SchedaAllenamentoDTO;
import giuseppeperna.GearForFit.payloads.SchedaAllenamentoRequestDTO;
import giuseppeperna.GearForFit.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SchedaAllenamentoService {

    @Autowired
    private SchedaAllenamentoRepository schedaRepository;

    @Autowired
    private UtenteRepository utenteRepository;

    // ========== SCHEDE STANDARD (ADMIN) ==========

    public SchedaAllenamentoDTO creaSchedaStandard(SchedaAllenamentoRequestDTO body) {
        SchedaAllenamento scheda = new SchedaAllenamento();
        scheda.setNome(body.nome());
        scheda.setDescrizione(body.descrizione());
        scheda.setObiettivo(body.obiettivo());
        scheda.setTipoAllenamento(body.tipoAllenamento());
        scheda.setFrequenzaSettimanale(body.frequenzaSettimanale());
        scheda.setDurataSettimane(body.durataSettimane());
        scheda.setLivelloEsperienza(body.livelloEsperienza());
        scheda.setIsStandard(true);
        scheda.setUtente(null);

        SchedaAllenamento saved = schedaRepository.save(scheda);
        return mapToDTO(saved);
    }

    public List<SchedaAllenamentoDTO> getSchedeStandard() {
        return schedaRepository.findByIsStandardTrue()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<SchedaAllenamentoDTO> getSchedeStandardByObiettivo(ObiettivoAllenamento obiettivo) {
        return schedaRepository.findByIsStandardTrueAndObiettivo(obiettivo)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public SchedaAllenamentoDTO modificaSchedaStandard(Long schedaId, SchedaAllenamentoRequestDTO body) {
        SchedaAllenamento scheda = schedaRepository.findById(schedaId)
                .orElseThrow(() -> new NotFoundException("Scheda non trovata"));

        if (!scheda.getIsStandard()) {
            throw new UnauthorizedException("Questa non è una scheda standard");
        }

        scheda.setNome(body.nome());
        scheda.setDescrizione(body.descrizione());
        scheda.setObiettivo(body.obiettivo());
        scheda.setTipoAllenamento(body.tipoAllenamento());
        scheda.setFrequenzaSettimanale(body.frequenzaSettimanale());
        scheda.setDurataSettimane(body.durataSettimane());
        scheda.setLivelloEsperienza(body.livelloEsperienza());

        SchedaAllenamento updated = schedaRepository.save(scheda);
        return mapToDTO(updated);
    }

    public void eliminaSchedaStandard(Long schedaId) {
        SchedaAllenamento scheda = schedaRepository.findById(schedaId)
                .orElseThrow(() -> new NotFoundException("Scheda non trovata"));

        if (!scheda.getIsStandard()) {
            throw new UnauthorizedException("Questa non è una scheda standard");
        }

        schedaRepository.delete(scheda);
    }

    // ========== SCHEDE PERSONALIZZATE (UTENTE) ==========

    public SchedaAllenamentoDTO creaSchedaPersonalizzata(Long utenteId, SchedaAllenamentoRequestDTO body) {
        Utente utente = utenteRepository.findById(utenteId)
                .orElseThrow(() -> new NotFoundException("Utente non trovato"));

        SchedaAllenamento scheda = new SchedaAllenamento();
        scheda.setNome(body.nome());
        scheda.setDescrizione(body.descrizione());
        scheda.setObiettivo(body.obiettivo());
        scheda.setTipoAllenamento(body.tipoAllenamento());
        scheda.setFrequenzaSettimanale(body.frequenzaSettimanale());
        scheda.setDurataSettimane(body.durataSettimane());
        scheda.setLivelloEsperienza(body.livelloEsperienza());
        scheda.setIsStandard(false);
        scheda.setUtente(utente);

        SchedaAllenamento saved = schedaRepository.save(scheda);
        return mapToDTO(saved);
    }

    public List<SchedaAllenamentoDTO> getSchedeByUtente(Long utenteId) {
        return schedaRepository.findByUtenteId(utenteId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public SchedaAllenamentoDTO modificaSchedaPersonalizzata(Long schedaId, Long utenteId, SchedaAllenamentoRequestDTO body) {
        SchedaAllenamento scheda = schedaRepository.findById(schedaId)
                .orElseThrow(() -> new NotFoundException("Scheda non trovata"));

        if (scheda.getIsStandard()) {
            throw new UnauthorizedException("Non puoi modificare una scheda standard");
        }
        if (scheda.getUtente() == null || !scheda.getUtente().getId().equals(utenteId)) {
            throw new UnauthorizedException("Non puoi modificare questa scheda");
        }

        scheda.setNome(body.nome());
        scheda.setDescrizione(body.descrizione());
        scheda.setObiettivo(body.obiettivo());
        scheda.setTipoAllenamento(body.tipoAllenamento());
        scheda.setFrequenzaSettimanale(body.frequenzaSettimanale());
        scheda.setDurataSettimane(body.durataSettimane());
        scheda.setLivelloEsperienza(body.livelloEsperienza());

        SchedaAllenamento updated = schedaRepository.save(scheda);
        return mapToDTO(updated);
    }

    public void eliminaSchedaPersonalizzata(Long schedaId, Long utenteId) {
        SchedaAllenamento scheda = schedaRepository.findById(schedaId)
                .orElseThrow(() -> new NotFoundException("Scheda non trovata"));

        if (scheda.getIsStandard()) {
            throw new UnauthorizedException("Non puoi eliminare una scheda standard");
        }
        if (scheda.getUtente() == null || !scheda.getUtente().getId().equals(utenteId)) {
            throw new UnauthorizedException("Non puoi eliminare questa scheda");
        }

        schedaRepository.delete(scheda);
    }

    // ========== UTILITY ==========

    public SchedaAllenamentoDTO getSchedaById(Long id) {
        SchedaAllenamento scheda = schedaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Scheda non trovata con ID: " + id));
        return mapToDTO(scheda);
    }

    private SchedaAllenamentoDTO mapToDTO(SchedaAllenamento scheda) {
        return new SchedaAllenamentoDTO(
                scheda.getId(),
                scheda.getNome(),
                scheda.getDescrizione(),
                scheda.getObiettivo(),
                scheda.getTipoAllenamento(),
                scheda.getFrequenzaSettimanale(),
                scheda.getDurataSettimane(),
                scheda.getLivelloEsperienza(),
                scheda.getIsStandard(),
                scheda.getUtente() != null ? scheda.getUtente().getId() : null
        );
    }
}
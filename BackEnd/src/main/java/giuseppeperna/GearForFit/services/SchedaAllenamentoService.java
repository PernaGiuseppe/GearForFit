package giuseppeperna.GearForFit.services;

import giuseppeperna.GearForFit.entities.SchedePalestra.*;
import giuseppeperna.GearForFit.entities.Utente.TipoPiano;
import giuseppeperna.GearForFit.entities.Utente.TipoUtente;
import giuseppeperna.GearForFit.entities.Utente.Utente;
import giuseppeperna.GearForFit.exceptions.NotFoundException;
import giuseppeperna.GearForFit.exceptions.UnauthorizedException;
import giuseppeperna.GearForFit.payloads.*;
import giuseppeperna.GearForFit.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SchedaAllenamentoService {

    @Autowired
    private SchedaAllenamentoRepository schedaRepository;

    @Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    private EsercizioRepository esercizioRepository;

    // ========== SCHEDE STANDARD (ADMIN) ==========

    public SchedaAllenamentoDTO creaSchedaStandard(SchedaPersonalizzataRequestDTO body) {
        SchedaAllenamento scheda = new SchedaAllenamento();
        scheda.setNome(body.nome());
        scheda.setDescrizione(body.descrizione());
        scheda.setObiettivo(body.obiettivo());
        scheda.setTipoAllenamento(body.tipoAllenamento());
        scheda.setDurataSettimane(body.durataSettimane());

        scheda.setIsStandard(true);
        scheda.setUtente(null);

        if (body.giorni() != null && !body.giorni().isEmpty()) {
            List<GiornoAllenamento> giorni = new ArrayList<>();
            for (GiornoAllenamentoRequestDTO giornoDTO : body.giorni()) {
                GiornoAllenamento giorno = new GiornoAllenamento();
                giorno.setGiornoSettimana(giornoDTO.giornoSettimana());
                giorno.setScheda(scheda);

                List<Serie> serieList = new ArrayList<>();
                for (SerieRequestDTO serieDTO : giornoDTO.serie()) {
                    Esercizio esercizio = esercizioRepository.findById(serieDTO.esercizioId())
                            .orElseThrow(() -> new NotFoundException("Esercizio con ID " + serieDTO.esercizioId() + " non trovato"));

                    Serie serie = new Serie();
                    serie.setEsercizio(esercizio);
                    serie.setNumeroSerie(serieDTO.numeroSerie());
                    serie.setNumeroRipetizioni(serieDTO.numeroRipetizioni());
                    serie.setTempoRecuperoSecondi(serieDTO.tempoRecuperoSecondi());
                    serie.setGiorno(giorno);
                    serieList.add(serie);
                }
                giorno.setSerie(serieList);
                giorni.add(giorno);
            }
            scheda.setGiorni(giorni);
        }

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

    public SchedaAllenamentoDTO modificaSchedaStandard(Long schedaId, SchedaPersonalizzataRequestDTO body) {
        SchedaAllenamento scheda = schedaRepository.findById(schedaId)
                .orElseThrow(() -> new NotFoundException("Scheda non trovata"));

        if (!scheda.getIsStandard()) {
            throw new UnauthorizedException("Questa non è una scheda standard");
        }

        scheda.setNome(body.nome());
        scheda.setDescrizione(body.descrizione());
        scheda.setObiettivo(body.obiettivo());
        scheda.setTipoAllenamento(body.tipoAllenamento());
        scheda.setDurataSettimane(body.durataSettimane());

        scheda.setIsStandard(true);
        scheda.setUtente(null);

        scheda.getGiorni().clear();

        if (body.giorni() != null && !body.giorni().isEmpty()) {
            List<GiornoAllenamento> nuoviGiorni = new ArrayList<>();
            for (GiornoAllenamentoRequestDTO giornoDTO : body.giorni()) {
                GiornoAllenamento giorno = new GiornoAllenamento();
                giorno.setGiornoSettimana(giornoDTO.giornoSettimana());
                giorno.setScheda(scheda);

                List<Serie> serieList = new ArrayList<>();
                for (SerieRequestDTO serieDTO : giornoDTO.serie()) {
                    Esercizio esercizio = esercizioRepository.findById(serieDTO.esercizioId())
                            .orElseThrow(() -> new NotFoundException("Esercizio con ID " + serieDTO.esercizioId() + " non trovato"));

                    Serie serie = new Serie();
                    serie.setEsercizio(esercizio);
                    serie.setNumeroSerie(serieDTO.numeroSerie());
                    serie.setNumeroRipetizioni(serieDTO.numeroRipetizioni());
                    serie.setTempoRecuperoSecondi(serieDTO.tempoRecuperoSecondi());
                    serie.setGiorno(giorno);
                    serieList.add(serie);
                }
                giorno.setSerie(serieList);
                nuoviGiorni.add(giorno);
            }
            scheda.getGiorni().addAll(nuoviGiorni);
        }

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

    // ========== SCHEDE PERSONALIZZATE e STANDART (ADMIN) ==========

    // Metodo per l'admin per ottenere tutte le schede come lista
    public List<SchedaAllenamentoDTO> getAllSchedeAsList() {
        List<SchedaAllenamento> schede = schedaRepository.findAll();
        return schede.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    // Admin modifica una scheda personalizzata di un utente

    public SchedaAllenamentoDTO adminModificaSchedaPersonalizzata(Long schedaId, SchedaPersonalizzataRequestDTO body) {
        SchedaAllenamento scheda = schedaRepository.findById(schedaId)
                .orElseThrow(() -> new NotFoundException("Scheda non trovata con ID: " + schedaId));

        // Controllo di sicurezza: assicura che sia una scheda personalizzata
        if (scheda.getIsStandard()) {
            throw new UnauthorizedException("Questa è una scheda standard. Per modificarla, usa l'endpoint /admin/schede/standard/{id}");
        }
        scheda.setNome(body.nome());
        scheda.setDescrizione(body.descrizione());
        scheda.setObiettivo(body.obiettivo());
        scheda.setTipoAllenamento(body.tipoAllenamento());
        scheda.setDurataSettimane(body.durataSettimane());

        scheda.getGiorni().clear();

        if (body.giorni() != null && !body.giorni().isEmpty()) {
            List<GiornoAllenamento> nuoviGiorni = new ArrayList<>();
            for (GiornoAllenamentoRequestDTO giornoDTO : body.giorni()) {
                GiornoAllenamento giorno = new GiornoAllenamento();
                giorno.setGiornoSettimana(giornoDTO.giornoSettimana());
                giorno.setScheda(scheda);

                List<Serie> serieList = new ArrayList<>();
                for (SerieRequestDTO serieDTO : giornoDTO.serie()) {
                    Esercizio esercizio = esercizioRepository.findById(serieDTO.esercizioId())
                            .orElseThrow(() -> new NotFoundException("Esercizio con ID " + serieDTO.esercizioId() + " non trovato"));

                    Serie serie = new Serie();
                    serie.setEsercizio(esercizio);
                    serie.setNumeroSerie(serieDTO.numeroSerie());
                    serie.setNumeroRipetizioni(serieDTO.numeroRipetizioni());
                    serie.setTempoRecuperoSecondi(serieDTO.tempoRecuperoSecondi());
                    serie.setGiorno(giorno);
                    serieList.add(serie);
                }
                giorno.setSerie(serieList);
                nuoviGiorni.add(giorno);
            }
            scheda.getGiorni().addAll(nuoviGiorni);
        }

        SchedaAllenamento updated = schedaRepository.save(scheda);
        return mapToDTO(updated);
    }
    // Metodo per l'ADMIN per eliminare QUALSIASI scheda
    public void adminEliminaScheda(Long schedaId) {
        SchedaAllenamento scheda = schedaRepository.findById(schedaId)
                .orElseThrow(() -> new NotFoundException("Scheda non trovata con ID: " + schedaId));
        schedaRepository.delete(scheda);
    }

    // ========== SCHEDE PERSONALIZZATE (UTENTE) ==========

    public SchedaAllenamentoDTO creaSchedaPersonalizzata(Long utenteId, SchedaPersonalizzataRequestDTO body) {
        Utente utente = utenteRepository.findById(utenteId)
                .orElseThrow(() -> new NotFoundException("Utente non trovato"));

        SchedaAllenamento scheda = new SchedaAllenamento();
        scheda.setNome(body.nome());
        scheda.setDescrizione(body.descrizione());
        scheda.setObiettivo(body.obiettivo());
        scheda.setTipoAllenamento(body.tipoAllenamento());
        scheda.setDurataSettimane(body.durataSettimane());
        scheda.setIsStandard(false);
        scheda.setUtente(utente);

        // === NUOVA LOGICA PER SALVARE GIORNI E SERIE ===
        if (body.giorni() != null && !body.giorni().isEmpty()) {
            List<GiornoAllenamento> giorni = new ArrayList<>();
            for (GiornoAllenamentoRequestDTO giornoDTO : body.giorni()) {
                GiornoAllenamento giorno = new GiornoAllenamento();
                giorno.setGiornoSettimana(giornoDTO.giornoSettimana());
                giorno.setScheda(scheda); // Collega al genitore

                List<Serie> serieList = new ArrayList<>();
                for (SerieRequestDTO serieDTO : giornoDTO.serie()) {
                    // Trova l'esercizio dal DB
                    Esercizio esercizio = esercizioRepository.findById(serieDTO.esercizioId())
                            .orElseThrow(() -> new NotFoundException("Esercizio con ID " + serieDTO.esercizioId() + " non trovato"));

                    Serie serie = new Serie();
                    serie.setEsercizio(esercizio);
                    serie.setNumeroSerie(serieDTO.numeroSerie());
                    serie.setNumeroRipetizioni(serieDTO.numeroRipetizioni());
                    serie.setTempoRecuperoSecondi(serieDTO.tempoRecuperoSecondi());
                    serie.setGiorno(giorno); // Collega al genitore
                    serieList.add(serie);
                }
                giorno.setSerie(serieList);
                giorni.add(giorno);
            }
            scheda.setGiorni(giorni);
        }
        // ===============================================

        SchedaAllenamento saved = schedaRepository.save(scheda);
        return mapToDTO(saved);
    }

    public List<SchedaAllenamentoDTO> getSchedeByUtente(Long utenteId) {
        return schedaRepository.findByUtenteId(utenteId)
                .stream()
                .map(this::mapToDTO) // mapToDTO ora ritorna i dati completi
                .collect(Collectors.toList());
    }

    public SchedaAllenamentoDTO modificaSchedaPersonalizzata(Long schedaId, Long utenteId, SchedaPersonalizzataRequestDTO body) {
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
        scheda.setDurataSettimane(body.durataSettimane());

        // Svuota i giorni esistenti (grazie a orphanRemoval=true verranno cancellati)
        scheda.getGiorni().clear();

        if (body.giorni() != null && !body.giorni().isEmpty()) {
            List<GiornoAllenamento> nuoviGiorni = new ArrayList<>();
            for (GiornoAllenamentoRequestDTO giornoDTO : body.giorni()) {
                GiornoAllenamento giorno = new GiornoAllenamento();
                giorno.setGiornoSettimana(giornoDTO.giornoSettimana());
                giorno.setScheda(scheda);

                List<Serie> serieList = new ArrayList<>();
                for (SerieRequestDTO serieDTO : giornoDTO.serie()) {
                    Esercizio esercizio = esercizioRepository.findById(serieDTO.esercizioId())
                            .orElseThrow(() -> new NotFoundException("Esercizio con ID " + serieDTO.esercizioId() + " non trovato"));

                    Serie serie = new Serie();
                    serie.setEsercizio(esercizio);
                    serie.setNumeroSerie(serieDTO.numeroSerie());
                    serie.setNumeroRipetizioni(serieDTO.numeroRipetizioni());
                    serie.setTempoRecuperoSecondi(serieDTO.tempoRecuperoSecondi());
                    serie.setGiorno(giorno);
                    serieList.add(serie);
                }
                giorno.setSerie(serieList);
                nuoviGiorni.add(giorno);
            }
            // Aggiungi i nuovi giorni alla lista (che ora è gestita dalla sessione JPA)
            scheda.getGiorni().addAll(nuoviGiorni);
        }
        // ===============================================

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
    public SchedaAllenamentoDTO getSchedaByIdAndAuthorize(Long id, Utente utente) {
        // 1. Trova la scheda o lancia un'eccezione
        SchedaAllenamento scheda = schedaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Scheda non trovata con ID: " + id));

        // 2. Controlla se la scheda è standard o personalizzata e applica le regole
        if (scheda.getIsStandard()) {
            // CASO 1: È una scheda standard (is_standard = true)
            // Accessibile solo da ADMIN, PREMIUM, GOLD, SILVER
            boolean hasAccess = utente.getTipoUtente() == TipoUtente.ADMIN ||
                    utente.getTipoPiano() == TipoPiano.PREMIUM ||
                    utente.getTipoPiano() == TipoPiano.GOLD ||
                    utente.getTipoPiano() == TipoPiano.SILVER;
            if (!hasAccess) {
                throw new AccessDeniedException("Il tuo piano (" + utente.getTipoPiano() + ") non consente l'accesso alle schede standard.");
            }
        } else {
            // CASO 2: È una scheda personalizzata (is_standard = false)
            // Accessibile solo dal suo proprietario o da un ADMIN
            boolean isAdmin = utente.getTipoUtente() == TipoUtente.ADMIN;
            // Controlla che la scheda abbia un utente associato prima di confrontare gli ID
            boolean isOwner = scheda.getUtente() != null && scheda.getUtente().getId().equals(utente.getId());

            if (!isAdmin && !isOwner) {
                throw new AccessDeniedException("Non sei autorizzato a visualizzare questa scheda personalizzata.");
            }
        }

        // 3. Se l'utente è autorizzato, mappa e restituisci il DTO
        return mapToDTO(scheda);
    }

  private SchedaAllenamentoDTO mapToDTO(SchedaAllenamento scheda) {
      List<GiornoAllenamentoDTO> giorniDTO = scheda.getGiorni().stream()
              .map(giorno -> {
                  List<SerieDTO> serieDTO = giorno.getSerie().stream()
                          .map(serie -> new SerieDTO(
                                  serie.getId(),
                                  serie.getEsercizio().getId(),
                                  serie.getEsercizio().getNome(),
                                  serie.getNumeroSerie(),
                                  serie.getNumeroRipetizioni(),
                                  serie.getTempoRecuperoSecondi()
                          )).collect(Collectors.toList());
                  return new GiornoAllenamentoDTO(
                          giorno.getId(),
                          giorno.getGiornoSettimana(),
                          serieDTO
                  );
              }).collect(Collectors.toList());
      return new SchedaAllenamentoDTO(
              scheda.getId(),
              scheda.getNome(),
              scheda.getDescrizione(),
              scheda.getObiettivo(),
              scheda.getTipoAllenamento(),
              scheda.getDurataSettimane(),
              scheda.getIsStandard(),
              scheda.getUtente() != null ? scheda.getUtente().getId() : null,
              giorniDTO
      );
  }

    public SchedaAllenamentoDTO getSchedaByIdAndUtente(Long schedaId, Long utenteId) {
        SchedaAllenamento scheda = schedaRepository.findById(schedaId)
                .orElseThrow(() -> new NotFoundException("Scheda con id " + schedaId + " non trovata"));

        if (scheda.getUtente() == null || !scheda.getUtente().getId().equals(utenteId)) {
            throw new UnauthorizedException("Non sei autorizzato a visualizzare questa scheda o la scheda non appartiene a un utente.");
        }

        return mapToDTO(scheda);
    }

    public List<SchedaAllenamentoDTO> getSchedePersonalizzateByObiettivo(Long utenteId, ObiettivoAllenamento obiettivo) {
        List<SchedaAllenamento> schede = schedaRepository.findByUtenteIdAndObiettivo(utenteId, obiettivo);

        return schede.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    public void eliminaSchedaById(Long id) {
        SchedaAllenamento scheda = schedaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Scheda con id " + id + " non trovata"));
        schedaRepository.delete(scheda);
    }

}
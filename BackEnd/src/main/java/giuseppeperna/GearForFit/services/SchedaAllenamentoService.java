package giuseppeperna.GearForFit.services;

import giuseppeperna.GearForFit.entities.SchedePalestra.*;
import giuseppeperna.GearForFit.entities.Utente.TipoPiano;
import giuseppeperna.GearForFit.entities.Utente.TipoUtente;
import giuseppeperna.GearForFit.entities.Utente.Utente;
import giuseppeperna.GearForFit.exceptions.NotFoundException;
import giuseppeperna.GearForFit.exceptions.UnauthorizedException;
import giuseppeperna.GearForFit.payloads.*;
import giuseppeperna.GearForFit.repositories.EsercizioRepository;
import giuseppeperna.GearForFit.repositories.SchedaAllenamentoRepository;
import giuseppeperna.GearForFit.repositories.SerieRepository;
import giuseppeperna.GearForFit.repositories.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    private SerieRepository serieRepository;

    // ========== SCHEDE STANDARD (ADMIN) ==========

    public SchedaAllenamentoDTO creaSchedaStandard(SchedaPersonalizzataRequestDTO body) {
        SchedaAllenamento scheda = new SchedaAllenamento();
        scheda.setNome(body.nome());
        scheda.setDescrizione(body.descrizione());
        scheda.setObiettivo(body.obiettivo());
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
        List<SchedaAllenamento> schede = schedaRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
        return schede.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
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


    @Transactional
    public SerieDTO aggiornaPesoSerie(Long schedaId, Long serieId, String peso, Utente utenteLoggato) {
        // 1. Verifica che la scheda appartenga all'utente loggato
        SchedaAllenamento scheda = schedaRepository.findByIdAndUtenteId(schedaId, utenteLoggato.getId())
                .orElseThrow(() -> new NotFoundException("Scheda allenamento non trovata o non autorizzato"));

        // 2. Trova la serie nel database
        Serie serie = serieRepository.findById(serieId)
                .orElseThrow(() -> new NotFoundException("Serie non trovata"));

        // 3. Verifica che la serie appartenga effettivamente a questa scheda
        if (!serie.getGiorno().getScheda().getId().equals(schedaId)) {
            throw new IllegalArgumentException("La serie non appartiene a questa scheda");
        }

        // 4. Aggiorna il peso
        serie.setPeso(peso);
        Serie salvata = serieRepository.save(serie);

        // 5. Mappa a DTO e ritorna
        return new SerieDTO(
                salvata.getId(),
                salvata.getEsercizio().getId(),
                salvata.getEsercizio().getNome(),
                salvata.getNumeroSerie(),
                salvata.getNumeroRipetizioni(),
                salvata.getTempoRecuperoSecondi(),
                salvata.getPeso()
        );
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

    public List<SchedaAllenamentoDTO> getAllSchedeFiltered(Utente utente, String tipoFiltro) {
        List<SchedaAllenamentoDTO> result = new ArrayList<>();

        // 1. Recupero Schede STANDARD
        // Le mostriamo se il filtro è "ALL" o "STANDARD"
        // Tutti gli utenti loggati vedono le standard (in base alla logica definita)
        if ("ALL".equalsIgnoreCase(tipoFiltro) || "STANDARD".equalsIgnoreCase(tipoFiltro)) {
            List<SchedaAllenamento> standardEntities = schedaRepository.findByIsStandardTrue();
            List<SchedaAllenamentoDTO> standardDTOs = standardEntities.stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());
            result.addAll(standardDTOs);
        }

        // 2. Recupero Schede PERSONALIZZATE
        // Le mostriamo se il filtro è "ALL" o "PERSONALIZZATE" E se l'utente ha il piano adeguato (GOLD+)
        boolean canAccessPersonalized = isGoldOrAbove(utente.getTipoPiano());

        if (canAccessPersonalized && ("ALL".equalsIgnoreCase(tipoFiltro) || "PERSONALIZZATE".equalsIgnoreCase(tipoFiltro))) {
            // Recupera le schede create dall'utente (o assegnate a lui)
            List<SchedaAllenamento> personalizzateEntities = schedaRepository.findByUtenteId(utente.getId());
            List<SchedaAllenamentoDTO> personalizzateDTOs = personalizzateEntities.stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());
            result.addAll(personalizzateDTOs);
        }

        return result;
    }

    // Ottieni una scheda per ID (standard o personalizzata)
    public SchedaAllenamentoDTO getSchedaById(Long schedaId, Utente utente) {
        SchedaAllenamento scheda = schedaRepository.findById(schedaId)
                .orElseThrow(() -> new NotFoundException("Scheda allenamento non trovata con id: " + schedaId));

        // Se è una scheda personalizzata, verifica che l'utente abbia i permessi
        if (!scheda.getIsStandard()) {
            // Solo l'owner o un admin può vedere una scheda personalizzata
            if (utente.getTipoUtente() != TipoUtente.ADMIN &&
                    !scheda.getUtente().getId().equals(utente.getId())) {
                throw new UnauthorizedException("Non sei autorizzato a visualizzare questa scheda.");
            }
        }

        return mapToDTO(scheda);
    }

    // Ottieni tutte le schede (standard + personalizzate utente) filtrate per obiettivo
    public List<SchedaAllenamentoDTO> getAllSchedeByObiettivo(Long utenteId, ObiettivoAllenamento obiettivo) {
        List<SchedaAllenamentoDTO> result = new ArrayList<>();

        // Schede standard con l'obiettivo specificato
        List<SchedaAllenamento> schedeStandard = schedaRepository.findByIsStandardTrueAndObiettivo(obiettivo);
        result.addAll(schedeStandard.stream().map(this::mapToDTO).collect(Collectors.toList()));

        // Schede personalizzate dell'utente con l'obiettivo specificato
        List<SchedaAllenamento> schedePersonali = schedaRepository.findByUtenteIdAndObiettivo(utenteId, obiettivo);
        result.addAll(schedePersonali.stream().map(this::mapToDTO).collect(Collectors.toList()));

        return result;
    }

    // Helper per verificare i permessi del piano (Gold o superiore)
    private boolean isGoldOrAbove(giuseppeperna.GearForFit.entities.Utente.TipoPiano piano) {
        return piano == giuseppeperna.GearForFit.entities.Utente.TipoPiano.GOLD ||
                piano == giuseppeperna.GearForFit.entities.Utente.TipoPiano.PREMIUM ||
                piano == giuseppeperna.GearForFit.entities.Utente.TipoPiano.ADMIN;
    }

    @Transactional
    public SchedaAllenamentoDTO setSchedaAttiva(Long schedaId, Utente utente) {
        // 1. Trova la scheda da attivare, assicurandoti che sia dell'utente corretto e che sia personalizzata
        SchedaAllenamento schedaDaAttivare = schedaRepository.findByIdAndUtenteId(schedaId, utente.getId())
                .orElseThrow(() -> new NotFoundException("Scheda allenamento non trovata."));

        if (schedaDaAttivare.getIsStandard()) {
            throw new IllegalArgumentException("Solo le schede personalizzate possono essere attivate.");
        }

        // 2. Disattiva quella attualmente attiva (se diversa)
        schedaRepository.findByUtenteIdAndAttivaTrue(utente.getId()).ifPresent(schedaVecchia -> {
            if (!schedaVecchia.getId().equals(schedaDaAttivare.getId())) {
                schedaVecchia.setAttiva(false);
                schedaRepository.save(schedaVecchia);
            }
        });

        // 3. Attiva la nuova scheda e salva
        schedaDaAttivare.setAttiva(true);
        SchedaAllenamento salvata = schedaRepository.save(schedaDaAttivare);

        return convertToResponseDTO(salvata);
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

    // Ottieni tutte le schede custom (di tutti gli utenti)
    public List<SchedaAllenamentoDTO> getAllSchedeCustom() {
        List<SchedaAllenamento> schede = schedaRepository.findByIsStandardFalse();
        return schede.stream().map(this::mapToDTO).toList();
    }

    // Ottieni schede standard filtrate per obiettivo
    public List<SchedaAllenamentoDTO> getSchedeStandardPerObiettivo(ObiettivoAllenamento obiettivo) {
        List<SchedaAllenamento> schede = schedaRepository
                .findByIsStandardTrueAndObiettivo(obiettivo);
        return schede.stream().map(this::mapToDTO).toList();
    }

    // Ottieni schede custom filtrate per obiettivo (di tutti gli utenti)
    public List<SchedaAllenamentoDTO> getAllSchedeCustomPerObiettivo(ObiettivoAllenamento obiettivo) {
        List<SchedaAllenamento> schede = schedaRepository
                .findByIsStandardFalseAndObiettivo(obiettivo);
        return schede.stream().map(this::mapToDTO).toList();
    }

    // Ottieni TUTTE le schede (standard + custom) filtrate per obiettivo
    public List<SchedaAllenamentoDTO> getAllSchedePerObiettivo(ObiettivoAllenamento obiettivo) {
        List<SchedaAllenamento> schede = schedaRepository
                .findByObiettivo(obiettivo);
        return schede.stream().map(this::mapToDTO).toList();
    }

    // ========== UTILITY ==========
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
                                    serie.getTempoRecuperoSecondi(),
                                    serie.getPeso()
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
                scheda.getDurataSettimane(),
                scheda.getIsStandard(),
                scheda.getUtente() != null ? scheda.getUtente().getId() : null,
                scheda.getAttiva(),
                giorniDTO
        );
    }

    private SchedaAllenamentoDTO convertToResponseDTO(SchedaAllenamento scheda) {
        List<GiornoAllenamentoDTO> giorniDTO = new ArrayList<>();

        return new SchedaAllenamentoDTO(
                scheda.getId(),
                scheda.getNome(),
                scheda.getDescrizione(),
                scheda.getObiettivo(),
                scheda.getDurataSettimane(),
                scheda.getIsStandard(),
                scheda.getUtente() != null ? scheda.getUtente().getId() : null,
                scheda.getAttiva(),
                giorniDTO);
    }
}
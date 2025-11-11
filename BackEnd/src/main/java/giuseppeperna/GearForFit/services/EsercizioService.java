package giuseppeperna.GearForFit.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import giuseppeperna.GearForFit.entities.SchedePalestra.Attrezzo;
import giuseppeperna.GearForFit.entities.SchedePalestra.Esercizio;
import giuseppeperna.GearForFit.entities.SchedePalestra.GruppoMuscolare;
import giuseppeperna.GearForFit.exceptions.NotFoundException;
import giuseppeperna.GearForFit.payloads.EsercizioRequestDTO;
import giuseppeperna.GearForFit.repositories.AttrezzoRepository;
import giuseppeperna.GearForFit.repositories.EsercizioRepository;
import giuseppeperna.GearForFit.repositories.GruppoMuscolareRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@Transactional
public class EsercizioService {

    @Autowired
    private EsercizioRepository esercizioRepository;

    @Autowired
    private GruppoMuscolareRepository gruppoMuscolareRepository;

    @Autowired
    private AttrezzoRepository attrezzoRepository;

    @Autowired
    private Cloudinary cloudinary; // ✅ AGGIUNTO: Bean Cloudinary

    // ============= METODI DI LETTURA =============

    public Esercizio getEsercizioById(Long id) {
        return esercizioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Esercizio con id " + id + " non trovato"));
    }

    public List<Esercizio> getAllEsercizi() {
        return esercizioRepository.findAll();
    }

    public List<Esercizio> getEserciziByGruppoMuscolare(Long gruppoId) {
        return esercizioRepository.findByGruppoMuscolareId(gruppoId);
    }

    public List<Esercizio> getEserciziByAttrezzo(Long attrezzoId) {
        return esercizioRepository.findByAttrezzoId(attrezzoId);
    }

    // ============= METODI CRUD (ADMIN) =============

    public Esercizio creaEsercizio(EsercizioRequestDTO request) {
        GruppoMuscolare gruppo = gruppoMuscolareRepository.findById(request.gruppoMuscolareId())
                .orElseThrow(() -> new NotFoundException("Gruppo muscolare con id " + request.gruppoMuscolareId() + " non trovato"));

        Attrezzo attrezzo = attrezzoRepository.findById(request.attrezzoId())
                .orElseThrow(() -> new NotFoundException("Attrezzo con id " + request.attrezzoId() + " non trovato"));

        Esercizio esercizio = Esercizio.builder()
                .nome(request.nome())
                .descrizione(request.descrizione())
                .urlImmagine(request.urlImmagine())
                .gruppoMuscolare(gruppo)
                .attrezzo(attrezzo)
                .isComposto(request.isComposto())
                .build();

        return esercizioRepository.save(esercizio);
    }

    public Esercizio aggiornaEsercizio(Long id, EsercizioRequestDTO request) {
        Esercizio esercizio = getEsercizioById(id);

        GruppoMuscolare gruppo = gruppoMuscolareRepository.findById(request.gruppoMuscolareId())
                .orElseThrow(() -> new NotFoundException("Gruppo muscolare con id " + request.gruppoMuscolareId() + " non trovato"));

        Attrezzo attrezzo = attrezzoRepository.findById(request.attrezzoId())
                .orElseThrow(() -> new NotFoundException("Attrezzo con id " + request.attrezzoId() + " non trovato"));

        esercizio.setNome(request.nome());
        esercizio.setDescrizione(request.descrizione());
        esercizio.setUrlImmagine(request.urlImmagine());
        esercizio.setGruppoMuscolare(gruppo);
        esercizio.setAttrezzo(attrezzo);
        esercizio.setIsComposto(request.isComposto());

        return esercizioRepository.save(esercizio);
    }

    public void eliminaEsercizio(Long id) {
        Esercizio esercizio = getEsercizioById(id);
        esercizioRepository.delete(esercizio);
    }

    // Filtra esercizi per nome (ricerca)
    public List<Esercizio> cercaEserciziPerNome(String nome) {
        return esercizioRepository.findByNomeContainingIgnoreCase(nome);
    }

    // Filtra esercizi composti
    public List<Esercizio> getEserciziComposti() {
        return esercizioRepository.findByIsCompostoTrue();
    }

    public Esercizio uploadImmagine(Long id, MultipartFile file) throws IOException {
        // 1. Trova esercizio
        Esercizio esercizio = esercizioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Esercizio con id " + id + " non trovato"));

        // 2. Upload su Cloudinary
        String urlImmagine = (String) cloudinary.uploader()
                .upload(file.getBytes(), ObjectUtils.emptyMap())
                .get("url");

        // 3. Aggiorna URL
        esercizio.setUrlImmagine(urlImmagine);

        // 4. Salva
        return esercizioRepository.save(esercizio);
    }
}

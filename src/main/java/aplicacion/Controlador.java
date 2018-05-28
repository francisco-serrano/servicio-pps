package aplicacion;

import aplicacion.corrector.Correccion;
import aplicacion.corrector.CorrectorOrtografico;
import aplicacion.detector.DetectorRecursos;
import aplicacion.detector.Recurso;
import aplicacion.synset.ConsultorSynsets;
import com.google.common.base.Splitter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class Controlador {

    private CorrectorOrtografico corrector = new CorrectorOrtografico();
    private DetectorRecursos detector = new DetectorRecursos();
    private ConsultorSynsets consultorSynsets;

    @Autowired
    public void setDatabase(Database database) {
        consultorSynsets = new ConsultorSynsets(database);
    }

    @RequestMapping(value = "/corrector", method = RequestMethod.GET, produces = "application/json")
    public List<Correccion> check(@RequestParam(value = "text") String plainText) {
        return corrector.check(plainText);
    }

    @RequestMapping(value = "/detector", method = RequestMethod.GET, produces = "application/json")
    public List<Recurso> detect(@RequestParam(value = "text") String plainText) {
        return detector.detect(plainText);
    }

    @RequestMapping(value = "/synsets", method = RequestMethod.GET, produces = "application/json")
    public Map<String, Map<Integer, Collection<String>>> getSynsets(@RequestParam(value = "text") String words) {
        Map<String, Map<Integer, Collection<String>>> synsets = new HashMap<>();

        List<String> wordList = Splitter.on("_").splitToList(words);
        for (String word : wordList)
            synsets.put(word, consultorSynsets.getSynsets(word).asMap());

        return synsets;
    }

    @RequestMapping(value = "/select_synset", method = RequestMethod.PUT, produces = "text/plain")
    public boolean selectSynset(@RequestParam(value = "synset_id") int synsetId) {
        return consultorSynsets.selectSynset(synsetId);
    }

    @RequestMapping(value = "/synsetcounter", method = RequestMethod.GET, produces = "application/json")
    public Map<Integer, AtomicLong> getSynsetCounter() {
        return consultorSynsets.getSynsetCounter();
    }
}
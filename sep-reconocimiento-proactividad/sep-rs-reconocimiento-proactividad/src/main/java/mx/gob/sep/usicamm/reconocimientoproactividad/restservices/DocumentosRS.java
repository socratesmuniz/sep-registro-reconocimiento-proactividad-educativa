package mx.gob.sep.usicamm.reconocimientoproactividad.restservices;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import lombok.extern.slf4j.Slf4j;
import mx.gob.sep.usicamm.reconocimientoproactividad.negocio.documentos.FichaRegistro;
import mx.gob.sep.usicamm.reconocimientoproactividad.restservices.aspectos.LogRESTTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author AlbertoSánchezLópez
 */
@Slf4j
@RestController
@RequestMapping("documentos")
public class DocumentosRS {
    private final FichaRegistro fichaRegistro;

    @Autowired
    public DocumentosRS(FichaRegistro fichaRegistro){
        this.fichaRegistro=fichaRegistro;
    }

    @LogRESTTime
    @PostMapping(value = "ficha-registro/get/{curp}")
    public ResponseEntity<?> getFichaPreRegistro(@PathVariable String curp){
        HttpHeaders headers = new HttpHeaders();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            this.fichaRegistro.generaFicha(baos, curp);
            headers.add("Content-Disposition", "attachment; filename=fichaRegistro.pdf");

            return ResponseEntity.ok().headers(headers).body(new InputStreamResource(new ByteArrayInputStream(baos.toByteArray())));
        } catch (Exception ex) {
            log.error("Problema para generar la ficha", ex);
            return ResponseEntity.unprocessableEntity().body("Error en la generacion de la ficha del registro: " + ex.getMessage());
        }
    }    
}

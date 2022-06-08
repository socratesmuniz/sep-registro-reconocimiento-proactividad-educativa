package mx.gob.sep.usicamm.reconocimientoproactividad.restservices;

import java.util.concurrent.Callable;
import lombok.extern.slf4j.Slf4j;
import mx.gob.sep.usicamm.reconocimientoproactividad.negocio.CatalogosService;
import mx.gob.sep.usicamm.reconocimientoproactividad.negocio.excepciones.AccesoDatosExcepcion;
import mx.gob.sep.usicamm.reconocimientoproactividad.negocio.excepciones.OperacionInvalidaBdException;
import mx.gob.sep.usicamm.reconocimientoproactividad.restservices.aspectos.LogRESTTime;
import mx.gob.sep.usicamm.reconocimientoproactividad.restservices.objetos.RespuestaRestParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author albertosanchezlopez
 */
@Slf4j
@RestController
@RequestMapping("api")
public class CatalogosRS {
    private final CatalogosService service;

    @Autowired
    public CatalogosRS(CatalogosService catalogosService){
        this.service=catalogosService;
    }

    @LogRESTTime
    @GetMapping(value="/entidades/get")
    public ResponseEntity<?> getEntidades(){
        return RespuestaRestParser.parse(new Callable<Object>() {
            public Object call() throws AccesoDatosExcepcion, OperacionInvalidaBdException {
                return service.getEntidades();
            }
         }, null);
    }

    @LogRESTTime
    @GetMapping(value="/sostenimientos-entidad/get/{cveEntidad}")
    public ResponseEntity<?> getSostenimientos(@PathVariable Integer cveEntidad, @RequestParam(defaultValue="0") Integer cveMunicipio,
            @RequestParam(defaultValue="0") Integer cveSostenimiento){
        return RespuestaRestParser.parse(new Callable<Object>() {
            public Object call() throws AccesoDatosExcepcion, OperacionInvalidaBdException {
                return service.getSostenimientos(cveEntidad, cveMunicipio);
            }
         }, null);
    }
}

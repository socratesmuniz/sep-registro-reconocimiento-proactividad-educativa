package mx.gob.sep.usicamm.reconocimientoproactividadeducativa.restservices;

import java.util.concurrent.Callable;
import lombok.extern.slf4j.Slf4j;
import mx.gob.sep.usicamm.reconocimientoproactividadeducativa.negocio.DocentesService;
import mx.gob.sep.usicamm.reconocimientoproactividadeducativa.negocio.excepciones.AccesoDatosExcepcion;
import mx.gob.sep.usicamm.reconocimientoproactividadeducativa.negocio.excepciones.OperacionInvalidaBdException;
import mx.gob.sep.usicamm.reconocimientoproactividadeducativa.restservices.aspectos.LogRESTTime;
import mx.gob.sep.usicamm.reconocimientoproactividadeducativa.restservices.objetos.RespuestaRestParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author hiryu
 */
@Slf4j
@RestController
@RequestMapping("api")
public class DocentesRS {
    private final DocentesService service;

    @Autowired
    public DocentesRS(DocentesService docentesService){
        this.service=docentesService;
    }
    
    @LogRESTTime
    @GetMapping(value="/docentes/get-curp/{curp}")
    public ResponseEntity<?> getDocente(@PathVariable String curp){
        return RespuestaRestParser.parse(new Callable<Object>() {
            public Object call() throws AccesoDatosExcepcion, OperacionInvalidaBdException {
                return service.getDocente(curp);
            }
         }, null);
    }
}

package mx.gob.sep.usicamm.reconocimientoproactividad.restservices;

import java.util.concurrent.Callable;
import lombok.extern.slf4j.Slf4j;
import mx.gob.sep.usicamm.reconocimientoproactividad.entidades.ParticipacionDTO;
import mx.gob.sep.usicamm.reconocimientoproactividad.negocio.RegistroParticipacionService;
import mx.gob.sep.usicamm.reconocimientoproactividad.negocio.excepciones.AccesoDatosExcepcion;
import mx.gob.sep.usicamm.reconocimientoproactividad.negocio.excepciones.NegocioExcepcion;
import mx.gob.sep.usicamm.reconocimientoproactividad.negocio.excepciones.OperacionInvalidaBdException;
import mx.gob.sep.usicamm.reconocimientoproactividad.negocio.utils.Constantes;
import mx.gob.sep.usicamm.reconocimientoproactividad.restservices.aspectos.LogRESTTime;
import mx.gob.sep.usicamm.reconocimientoproactividad.restservices.objetos.RespuestaRestParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author hiryu
 */
@Slf4j
@RestController
@RequestMapping("api")
public class RegistroParticipacionRS {
    private final RegistroParticipacionService service;
    private final ControlAcceso controlAcceso;

    @Autowired
    public RegistroParticipacionRS(RegistroParticipacionService participacionService, ControlAcceso controlAcceso){
        this.service=participacionService;
        this.controlAcceso=controlAcceso;
    }

    @LogRESTTime
    @GetMapping(value="participaciones/get/{docente}")
    public ResponseEntity<?> getParticipacion(@PathVariable Integer docente, @RequestParam(defaultValue="0") Integer cveEntidad,
            @RequestParam(defaultValue="0") Integer anioParticipacion){
        return RespuestaRestParser.parse(new Callable<Object>() {
            public Object call() throws AccesoDatosExcepcion, OperacionInvalidaBdException {
                return service.recuperaParticipacion(docente, cveEntidad, anioParticipacion);
            }
         }, null);
    }

    @LogRESTTime
    @PostMapping(value="participaciones/add/{cveDocenteLogin}")
    public ResponseEntity<?> addParticipacion(@RequestHeader("Authorization") String token, @PathVariable Integer cveDocenteLogin, @RequestBody ParticipacionDTO datos){
        if (this.controlAcceso.validaTokenSistema(token, cveDocenteLogin)){
            return RespuestaRestParser.parse(new Callable<Object>() {
                public Object call() throws AccesoDatosExcepcion, OperacionInvalidaBdException, NegocioExcepcion {
                    return service.actualizaParticipacion(datos);
                }
             }, Constantes.MSG_PARTICIPACION_UPT_OK);
        }
        else{
            return RespuestaRestParser.getDefaultInvalidTokenResponse();
        }
    }

    @LogRESTTime
    @GetMapping(value="documentos/get/{docente}")
    public ResponseEntity<?> getDocumento(@PathVariable Integer docente, @RequestParam(defaultValue="0") Integer cveEntidad,
            @RequestParam(defaultValue="0") Integer anioParticipacion){
        return RespuestaRestParser.parse(new Callable<Object>() {
            public Object call() throws AccesoDatosExcepcion, OperacionInvalidaBdException {
                return service.recuperaDocumento(docente, cveEntidad, anioParticipacion);
            }
         }, null);
    }
}

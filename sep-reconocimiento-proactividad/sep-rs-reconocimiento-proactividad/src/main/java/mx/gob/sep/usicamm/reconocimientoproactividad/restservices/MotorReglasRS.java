package mx.gob.sep.usicamm.reconocimientoproactividad.restservices;

import gob.sep.usicamm.reglas_negocio.Validacion;
import gob.sep.usicamm.reglas_negocio.exception.ReglaNegocioException;
import gob.sep.usicamm.reglas_negocio.models.RequestValidacion;
import gob.sep.usicamm.reglas_negocio.models.ResponseValidacion;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import lombok.extern.slf4j.Slf4j;
import mx.gob.sep.usicamm.reconocimientoproactividad.configuracion.ConfiguracionAplicacion;
import mx.gob.sep.usicamm.reconocimientoproactividad.restservices.aspectos.LogRESTTime;
import mx.gob.sep.usicamm.reconocimientoproactividad.restservices.objetos.RespuestaRestParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
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
public class MotorReglasRS {
    private final ConfiguracionAplicacion config;
    private final ControlAcceso controlAcceso;

    @Autowired
    public MotorReglasRS(ControlAcceso loginService, ConfiguracionAplicacion globalValue){
        this.controlAcceso=loginService;
        this.config=globalValue;
    }
    
    @LogRESTTime
    @PostMapping(value="motor-reglas/validacion/{cveDocente}")
    public ResponseEntity<?> validaRegla(@RequestHeader("Authorization") String token, @PathVariable Integer cveDocente, @RequestParam(defaultValue="") String modulo, @RequestBody Map<String, String> mapValores){
        if(this.controlAcceso.validaTokenSistema(token, cveDocente)){
            return RespuestaRestParser.parse(new Callable<Object>() {
                public Object call() throws ReglaNegocioException  {
                    ResponseValidacion response;
                    HashMap<String,Object> res=new HashMap();
                    RequestValidacion requestValidacion = new RequestValidacion();
                    requestValidacion.setCveReglaNegocioSistemaInformacion(config.REGLAS_NEGOCIO_SISTEMA);
                    requestValidacion.setCveDocente(cveDocente);
                    requestValidacion.setCveMotorReglaNegocio(obtieneClaveRegla(modulo)); 
                    requestValidacion.setParametros(mapValores);

                    response = Validacion.run(config.REGLAS_NEGOCIO_URL, requestValidacion);

                    res.put("validacion", response.getValidacion());
                    res.put("errores", (response.getMensajeError()==null? null: response.getMensajeError().split("\\|")));
                    return res;
                }
             }, "Datos validados correctamente");
        }
        else{
            return RespuestaRestParser.getDefaultInvalidTokenResponse();
        }    
    }
    
    
    
    
    
    private int obtieneClaveRegla(String modulo){
        if(modulo!=null && modulo.equals("PAR")){
            return this.config.REGLA_PARTICIPACION;
        }
        else if(modulo!=null && modulo.equals("DP")){
            return this.config.REGLA_DATOS_PERSONALES;
        }
        else if(modulo!=null && modulo.equals("RG")){
            return this.config.REGLA_DATOS_REGISTRO;
        }
        else{
            return 0;
        }
    }
}

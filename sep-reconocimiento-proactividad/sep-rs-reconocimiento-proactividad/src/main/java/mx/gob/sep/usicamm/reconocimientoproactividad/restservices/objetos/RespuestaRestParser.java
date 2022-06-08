package mx.gob.sep.usicamm.reconocimientoproactividadeducativa.restservices.objetos;

import java.util.concurrent.Callable;
import lombok.extern.slf4j.Slf4j;
import mx.gob.sep.usicamm.reconocimientoproactividadeducativa.negocio.excepciones.AccesoDatosExcepcion;
import mx.gob.sep.usicamm.reconocimientoproactividadeducativa.negocio.excepciones.NegocioExcepcion;
import mx.gob.sep.usicamm.reconocimientoproactividadeducativa.negocio.utils.Constantes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 *
 * @author AlbertoSánchezLópez
 */
@Slf4j
public class RespuestaRestParser {
    private RespuestaRestParser(){}
    
    public static ResponseEntity<?> getDefaultInvalidTokenResponse(){
        return new ResponseEntity(new RespuestaRest(RespuestaRest.CODIGO_ERROR, Constantes.MSG_TOKEN_INVALIDO), HttpStatus.FORBIDDEN);
    }
    
    public static ResponseEntity<?> parse(Callable<Object> func, String msg){
        RespuestaRest res= new RespuestaRest();
        HttpStatus edo=HttpStatus.OK;

        try{
            res.setResponse( func.call() );
            res.setMsg(msg);
        }
        catch(NegocioExcepcion ex){
            res.setCode(RespuestaRest.CODIGO_ERROR);
            res.setMsg(ex.getMessage());
        }
        catch(AccesoDatosExcepcion ex){
            res.setCode(RespuestaRest.CODIGO_ERROR);
            res.setMsg(Constantes.MSG_ERROR_CONECCION);
            res.setDetail(ex.getMessage());
            edo=HttpStatus.INTERNAL_SERVER_ERROR;
            log.error("El servidor genero un error: "+ex.getMessage(), ex);
            
        }
        catch(Exception ex){
            res.setCode(RespuestaRest.CODIGO_ERROR);
            res.setMsg(Constantes.MSG_ERROR_SERVIDOR);
            res.setDetail(ex.getMessage());
            edo=HttpStatus.INTERNAL_SERVER_ERROR;
            log.error("El servidor genero un error: "+ex.getMessage(), ex);
        }
        
        return new ResponseEntity<>(res, edo);
    }
}

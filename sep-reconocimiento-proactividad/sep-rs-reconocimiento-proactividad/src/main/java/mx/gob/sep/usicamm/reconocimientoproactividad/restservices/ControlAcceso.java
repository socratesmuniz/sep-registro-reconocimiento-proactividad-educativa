package mx.gob.sep.usicamm.reconocimientoproactividadeducativa.restservices;



import java.time.LocalDate;
import lombok.extern.slf4j.Slf4j;
import mx.gob.sep.usicamm.reconocimientoproactividadeducativa.configuracion.ConfiguracionAplicacion;
import mx.gob.sep.usicamm.reconocimientoproactividadeducativa.negocio.utils.Constantes;
import mx.gob.sep.usicamm.reconocimientoproactividadeducativa.negocio.utils.MD5HashGenerator;
import org.springframework.stereotype.Component;

/**
 *
 * @author hiryu
 */
@Slf4j
@Component
public class ControlAcceso {
    private final ConfiguracionAplicacion config;
    
    public ControlAcceso(ConfiguracionAplicacion config){
        this.config=config;
    }
    
    /**
     * Revisa si el TOKEN de sistema es valido
     *
     * @param token TOKEN a validar
     * @param cveDocente Clave del docente que genero el TOKEN
     * @return true, si es valido
     */
    public boolean validaTokenSistema(final String token, final Integer cveDocente){
        String pass=cveDocente + this.config.TOKEN_CORREO + Constantes.FORMATTER_LOGIN_VENUS.format(LocalDate.now());
        String myHash=MD5HashGenerator.getMD5Hash(pass);
        log.debug("TS:" + myHash);

        return token.equals(myHash);
    }
}

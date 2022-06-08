package mx.gob.sep.usicamm.reconocimientoproactividad.negocio;

import java.time.LocalDate;
import lombok.extern.slf4j.Slf4j;
import mx.gob.sep.usicamm.reconocimientoproactividad.configuracion.ConfiguracionAplicacion;
import mx.gob.sep.usicamm.reconocimientoproactividad.negocio.utils.Constantes;
import mx.gob.sep.usicamm.reconocimientoproactividad.negocio.utils.MD5HashGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author albertosanchezlopez
 */
@Service
@Slf4j
public class DatosPersonalesService {
    private final ConfiguracionAplicacion config;
    
    @Autowired
    public DatosPersonalesService(ConfiguracionAplicacion config) {
        this.config=config;
    }
    
    
    /**
     * Genera un token para la solicitud de los servicios
     * @param semilla Texto base para el TOKEN
     * @return TOKEN
     */
    public String generaTokenSistema(String semilla) {
        return MD5HashGenerator.getMD5Hash(semilla+this.config.TOKEN_CORREO+Constantes.FORMATTER_LOGIN_VENUS.format(LocalDate.now()));
    }
}
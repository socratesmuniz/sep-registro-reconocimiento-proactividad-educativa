package mx.gob.sep.usicamm.reconocimientoproactividad.accesodatos;

import mx.gob.sep.usicamm.reconocimientoproactividad.configuracion.ConfiguracionAplicacion;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 *
 * @author hiryu
 */
public abstract class BaseDAO {
    final JdbcTemplate jdbcTemplate;
    final ConfiguracionAplicacion config;

    public BaseDAO(JdbcTemplate jdbcTemplate, ConfiguracionAplicacion config){
        this.jdbcTemplate=jdbcTemplate;
        this.config=config;
    }
}

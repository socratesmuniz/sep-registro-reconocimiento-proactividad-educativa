package mx.gob.sep.usicamm.reconocimientoproactividad.accesodatos;

import java.sql.Types;
import mx.gob.sep.usicamm.reconocimientoproactividad.accesodatos.aspectos.LogExecutionTime;
import mx.gob.sep.usicamm.reconocimientoproactividad.accesodatos.mapeos.MapperUtil;
import mx.gob.sep.usicamm.reconocimientoproactividad.accesodatos.util.SQL;
import mx.gob.sep.usicamm.reconocimientoproactividad.configuracion.ConfiguracionAplicacion;
import mx.gob.sep.usicamm.reconocimientoproactividad.entidades.UsuarioDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 *
 * @author albertosanchezlopez
 */
@Repository
public class LoginDAO extends BaseDAO{
    
    @Autowired
    public LoginDAO(JdbcTemplate jdbcTemplate, ConfiguracionAplicacion config){
        super(jdbcTemplate, config);
    }
    
    @LogExecutionTime
    public UsuarioDTO selectUsuario(String curp) {
        return jdbcTemplate.query(SQL.SQL_SEL_USUARIO,
                new Object[]{curp},
                new int[]{Types.VARCHAR},
                MapperUtil::mapRowUsuario
        ).stream().findFirst().orElse(null);
    }

    @LogExecutionTime
    public boolean insertLog(String curp, Integer evento, String ip_cliente, String navegador, String token) {
        return jdbcTemplate.update(SQL.SQL_INS_LOG_ACCESO, this.config.CVE_SISTEMA, curp, evento, ip_cliente, navegador, token, "F") > 0;
    }
    
    @LogExecutionTime
    public String selectDominio(int entidad) {
        return jdbcTemplate.query(SQL.SQL_SEL_DOMINIOS,
                new Object[]{entidad},
                new int[]{Types.INTEGER},
                MapperUtil::mapRowString
        ).stream().findFirst().orElse(null);
    }
    
    @LogExecutionTime
    public Integer selectParametro(String parametro) {
        return this.jdbcTemplate.query(SQL.SQL_SEL_PARAM,
                new Object[]{parametro, this.config.CVE_SISTEMA},
                new int[]{Types.VARCHAR, Types.INTEGER},
                MapperUtil::mapRowInteger).stream().findFirst().orElse(0);
    }
}

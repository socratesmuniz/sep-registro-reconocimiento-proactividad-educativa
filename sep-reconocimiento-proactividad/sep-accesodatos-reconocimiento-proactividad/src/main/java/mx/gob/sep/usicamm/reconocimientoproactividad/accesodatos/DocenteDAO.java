package mx.gob.sep.usicamm.reconocimientoproactividadeducativa.accesodatos;

import java.sql.Types;
import mx.gob.sep.usicamm.reconocimientoproactividadeducativa.accesodatos.aspectos.LogExecutionTime;
import mx.gob.sep.usicamm.reconocimientoproactividadeducativa.accesodatos.mapeos.MapperUtil;
import mx.gob.sep.usicamm.reconocimientoproactividadeducativa.accesodatos.util.SQL;
import mx.gob.sep.usicamm.reconocimientoproactividadeducativa.configuracion.ConfiguracionAplicacion;
import mx.gob.sep.usicamm.reconocimientoproactividadeducativa.entidades.DocenteDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 *
 * @author hiryu
 */
@Repository
public class DocenteDAO extends BaseDAO{
    
    public DocenteDAO(JdbcTemplate jdbcTemplate, ConfiguracionAplicacion config) {
        super(jdbcTemplate, config);
    }

    @LogExecutionTime
    public DocenteDTO selectDocenteByCurp(String curp){
        return jdbcTemplate.query(SQL.SQL_SEL_DOCENTE_BY_CURP,
                new Object[]{this.config.CVE_SISTEMA, curp},
                new int[]{Types.INTEGER, Types.VARCHAR}, 
                MapperUtil::mapRowDocente).stream().findFirst().orElse(null);
    }
}

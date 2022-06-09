package mx.gob.sep.usicamm.reconocimientoproactividad.accesodatos;

import java.sql.Types;
import java.util.List;
import mx.gob.sep.usicamm.reconocimientoproactividad.accesodatos.aspectos.LogExecutionTime;
import mx.gob.sep.usicamm.reconocimientoproactividad.accesodatos.mapeos.MapperUtil;
import mx.gob.sep.usicamm.reconocimientoproactividad.accesodatos.util.SQL;
import mx.gob.sep.usicamm.reconocimientoproactividad.configuracion.ConfiguracionAplicacion;
import mx.gob.sep.usicamm.reconocimientoproactividad.entidades.CatalogoDTO;
import mx.gob.sep.usicamm.reconocimientoproactividad.entidades.CctDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 *
 * @author albertosanchezlopez
 */
@Repository
public class CatalogosDAO extends BaseDAO{
    
    @Autowired
    public CatalogosDAO(JdbcTemplate jdbcTemplate, ConfiguracionAplicacion config){
        super(jdbcTemplate, config);
    }

    @LogExecutionTime
    public List<CatalogoDTO> selectEntidades(){
        return jdbcTemplate.query(SQL.SQL_SEL_CAT_ENTIDADES, 
                MapperUtil::mapRowCatalogo);
    }

    @LogExecutionTime
    public List<CatalogoDTO> selectSostenimientos(int entidad){
        return jdbcTemplate.query(SQL.SQL_SEL_CAT_SOSTENIMIENTOS,
                new Object[]{entidad}, 
                new int[]{Types.INTEGER}, 
                MapperUtil::mapRowCatalogo);
    }

    @LogExecutionTime
    public List<CatalogoDTO> selectServicios(){
        return jdbcTemplate.query(SQL.SQL_SEL_CAT_SERVICIOS,
                new Object[]{this.config.CVE_SISTEMA, this.config.CVE_NIVEL}, 
                new int[]{Types.INTEGER, Types.INTEGER}, 
                MapperUtil::mapRowCatalogo);
    }

    @LogExecutionTime
    public CctDTO selectCctByCve(String cve){
        return jdbcTemplate.query(SQL.SQL_SEL_CAT_CCT,
                new Object[]{cve}, 
                new int[]{Types.VARCHAR}, 
                MapperUtil::mapRowCct).stream().findFirst().orElse(null);
    }
}

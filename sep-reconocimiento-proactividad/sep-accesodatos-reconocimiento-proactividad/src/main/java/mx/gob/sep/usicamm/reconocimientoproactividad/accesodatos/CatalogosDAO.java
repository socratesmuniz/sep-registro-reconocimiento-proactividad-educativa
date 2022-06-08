package mx.gob.sep.usicamm.reconocimientoproactividad.accesodatos;

import java.sql.Types;
import java.util.List;
import mx.gob.sep.usicamm.reconocimientoproactividad.accesodatos.aspectos.LogExecutionTime;
import mx.gob.sep.usicamm.reconocimientoproactividad.accesodatos.mapeos.MapperUtil;
import mx.gob.sep.usicamm.reconocimientoproactividad.accesodatos.util.SQL;
import mx.gob.sep.usicamm.reconocimientoproactividad.configuracion.ConfiguracionAplicacion;
import mx.gob.sep.usicamm.reconocimientoproactividad.entidades.CatalogoDTO;
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
    public List<CatalogoDTO> selectSostenimientos(int entidad, Integer municipio){
        return jdbcTemplate.query(SQL.SQL_SEL_CAT_SOSTENIMIENTOS_ENTIDAD,
                new Object[]{config.CVE_PROCESO, config.CVE_NIVEL, config.CVE_CICLO_ESCOLAR, entidad}, 
                new int[]{Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER}, 
                MapperUtil::mapRowCatalogo);
    }
}

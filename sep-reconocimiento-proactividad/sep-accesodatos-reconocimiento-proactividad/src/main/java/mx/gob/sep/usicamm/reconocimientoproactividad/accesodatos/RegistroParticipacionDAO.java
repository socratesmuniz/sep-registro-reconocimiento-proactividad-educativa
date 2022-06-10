package mx.gob.sep.usicamm.reconocimientoproactividad.accesodatos;

import java.sql.Types;
import mx.gob.sep.usicamm.reconocimientoproactividad.accesodatos.aspectos.LogExecutionTime;
import mx.gob.sep.usicamm.reconocimientoproactividad.accesodatos.mapeos.MapperUtil;
import mx.gob.sep.usicamm.reconocimientoproactividad.accesodatos.util.SQL;
import mx.gob.sep.usicamm.reconocimientoproactividad.configuracion.ConfiguracionAplicacion;
import mx.gob.sep.usicamm.reconocimientoproactividad.entidades.ParticipacionDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 *
 * @author hiryu
 */
@Repository
public class RegistroParticipacionDAO extends BaseDAO{
    
    public RegistroParticipacionDAO(JdbcTemplate jdbcTemplate, ConfiguracionAplicacion config) {
        super(jdbcTemplate, config);
    }

    @LogExecutionTime
    public ParticipacionDTO selectParticipacion(int docente, int entidad, int anioAplicacion){
        return jdbcTemplate.query(SQL.SQL_SEL_REGISTRO_PARTICIPACION,
                new Object[]{this.config.CVE_SISTEMA, this.config.CVE_NIVEL, docente, entidad, anioAplicacion},
                new int[]{Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER}, 
                MapperUtil::mapRowParticipacion).stream().findFirst().orElse(null);
    }

    @LogExecutionTime
    public boolean insertParticipacion(ParticipacionDTO participacion) {
        return jdbcTemplate.update(SQL.SQL_INS_REGISTRO_PARTICIPACION,
                config.CVE_NIVEL,
                participacion.getCveDocente(),
                participacion.getCveEntidad(),
                participacion.getAnioAplicacion(),
                participacion.getCveSostenimiento(),
                participacion.getCveServicioEducativo(),
                participacion.getCveModalidad(),
                participacion.getCveCct()
                ) > 0;
    }

    @LogExecutionTime
    public boolean updateParticipacion(ParticipacionDTO participacion) {
        return jdbcTemplate.update(SQL.SQL_UPD_REGISTRO_PARTICIPACION,
                participacion.getCveSostenimiento(),
                participacion.getCveServicioEducativo(),
                participacion.getCveModalidad(),
                participacion.getCveCct(),
                config.CVE_NIVEL,
                participacion.getCveDocente(),
                participacion.getCveEntidad(),
                participacion.getAnioAplicacion()
                ) > 0;
    }

    @LogExecutionTime
    public boolean deleteParticipacion(ParticipacionDTO participacion) {
        return jdbcTemplate.update(SQL.SQL_DEL_REGISTRO_PARTICIPACION,
                config.CVE_NIVEL,
                participacion.getCveDocente(),
                participacion.getCveEntidad(),
                participacion.getAnioAplicacion()
                ) > 0;
    }

    @LogExecutionTime
    public boolean agregaBitacora(int evento, int docente, int entidad, int anioParticipacion){
        return (this.jdbcTemplate.update(SQL.SQL_BIT_REGISTRO_PARTICIPACION,
                this.config.CVE_SISTEMA, 
                evento,
                docente,
                docente,
                this.config.CVE_NIVEL,
                docente,
                entidad,
                anioParticipacion
        ) >0);
    }

    @LogExecutionTime
    public String selectDetalleTrabajo(int docente, int entidad, int anioAplicacion){
        return jdbcTemplate.query(SQL.SQL_SEL_REGISTRO_TRABAJO,
                new Object[]{this.config.CVE_NIVEL, docente, entidad, anioAplicacion},
                new int[]{Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER}, 
                MapperUtil::mapRowString).stream().findFirst().orElse(null);
    }

    @LogExecutionTime
    public boolean insertDetalleTrabajo(ParticipacionDTO participacion) {
        return jdbcTemplate.update(SQL.SQL_INS_REGISTRO_TRABAJO,
                config.CVE_NIVEL,
                participacion.getCveDocente(),
                participacion.getCveEntidad(),
                participacion.getAnioAplicacion(),
                participacion.getNombreTrabajo()
                ) > 0;
    }

    @LogExecutionTime
    public boolean updateDetalleTrabajo(ParticipacionDTO participacion) {
        return jdbcTemplate.update(SQL.SQL_UPD_REGISTRO_TRABAJO,
                participacion.getNombreTrabajo(),
                config.CVE_NIVEL,
                participacion.getCveDocente(),
                participacion.getCveEntidad(),
                participacion.getAnioAplicacion()
                ) > 0;
    }
}

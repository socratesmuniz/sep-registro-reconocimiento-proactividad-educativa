package mx.gob.sep.usicamm.reconocimientoproactividadeducativa.accesodatos;

import java.sql.Types;
import mx.gob.sep.usicamm.reconocimientoproactividadeducativa.accesodatos.aspectos.LogExecutionTime;
import mx.gob.sep.usicamm.reconocimientoproactividadeducativa.accesodatos.mapeos.MapperUtil;
import mx.gob.sep.usicamm.reconocimientoproactividadeducativa.accesodatos.util.SQL;
import mx.gob.sep.usicamm.reconocimientoproactividadeducativa.configuracion.ConfiguracionAplicacion;
import mx.gob.sep.usicamm.reconocimientoproactividadeducativa.entidades.ParticipacionDTO;
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
    public ParticipacionDTO selectParticipacion(Integer cveDocente){
        return jdbcTemplate.query(SQL.SQL_SEL_REGISTRO_PARTICIPACION,
                new Object[]{config.CVE_SISTEMA, config.CVE_PROCESO, config.CVE_NIVEL, config.CVE_CICLO_ESCOLAR, cveDocente},
                new int[]{Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER}, 
                MapperUtil::mapRowParticipacion).stream().findFirst().orElse(null);
    }

    @LogExecutionTime
    public boolean insertParticipacion(ParticipacionDTO participacion, Integer usuario) {
        return jdbcTemplate.update(SQL.SQL_INS_REGISTRO_PARTICIPACION,
                config.CVE_PROCESO,
                config.CVE_NIVEL,
                config.CVE_CICLO_ESCOLAR,
                participacion.getCveDocente(),
                participacion.getCveEntidad(),
                participacion.getCveSostenimiento(),
                participacion.getCveServicioEducativo(),
                participacion.getCveModalidad(),
                participacion.getCveCct(),
                usuario
                ) > 0;
    }

    @LogExecutionTime
    public boolean updateParticipacion(ParticipacionDTO participacion) {
        return jdbcTemplate.update(SQL.SQL_UPD_REGISTRO_PARTICIPACION,
                participacion.getCveSostenimiento(),
                participacion.getCveServicioEducativo(),
                participacion.getCveModalidad(),
                participacion.getCveCct(),
                config.CVE_PROCESO,
                config.CVE_NIVEL,
                config.CVE_CICLO_ESCOLAR,
                participacion.getCveDocente(),
                participacion.getCveEntidad()
                ) > 0;
    }

    @LogExecutionTime
    public boolean deleteParticipacion(ParticipacionDTO participacion) {
        return jdbcTemplate.update(SQL.SQL_DEL_REGISTRO_PARTICIPACION,
                config.CVE_PROCESO,
                config.CVE_NIVEL,
                config.CVE_CICLO_ESCOLAR,
                participacion.getCveDocente(),
                participacion.getCveEntidad()
                ) > 0;
    }

    @LogExecutionTime
    public boolean agregaBitacora(int evento, Integer cveDocente, Integer cveDocenteModificador, Integer cveEntidad){
        return (this.jdbcTemplate.update(SQL.SQL_BIT_REGISTRO_PARTICIPACION,
                this.config.CVE_SISTEMA, 
                evento,
                cveDocente,
                cveDocenteModificador,
                this.config.CVE_PROCESO,
                this.config.CVE_NIVEL,
                this.config.CVE_CICLO_ESCOLAR,
                cveDocente,
                cveEntidad
        ) >0);
    }
}

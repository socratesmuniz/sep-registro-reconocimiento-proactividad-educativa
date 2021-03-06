package mx.gob.sep.usicamm.reconocimientoproactividad.accesodatos.mapeos;

import java.sql.ResultSet;
import java.sql.SQLException;
import mx.gob.sep.usicamm.reconocimientoproactividad.entidades.*;

/**
 *
 * @author albertosanchezlopez
 */
public class MapperUtil {
    public static CatalogoDTO mapRowCatalogo(ResultSet resultSet, int rowNum) throws SQLException {
        return new CatalogoDTO(
                resultSet.getString("cve"),
                resultSet.getString("descripcion"));
    }

    public static UsuarioDTO mapRowUsuario(ResultSet resultSet, int rowNum) throws SQLException {
        return new UsuarioDTO(
                resultSet.getInt("cve_docente"),
                resultSet.getString("nombre"),
                resultSet.getString("primer_apellido"),
                resultSet.getString("segundo_apellido")
        );
    }

    public static CctDTO mapRowCct(ResultSet resultSet, int rowNum) throws SQLException {
        return new CctDTO(
                resultSet.getString("cv_cct"),
                resultSet.getString("c_nombre"),
                resultSet.getString("inmueble_c_vialidad_principal"),
                resultSet.getString("inmueble_c_nom_mun"),
                resultSet.getString("inmueble_c_nom_loc"),
                resultSet.getString("inmueble_n_extnum"),
                resultSet.getString("contacto_c_telefono"),
                resultSet.getString("c_tipo")
        );
    }

    public static Integer mapRowInteger(ResultSet resultSet, int rowNum) throws SQLException {
        return resultSet.getInt(1);
    }

    public static String mapRowString(ResultSet resultSet, int rowNum) throws SQLException {
        return resultSet.getString(1);
    }

    public static DocenteDTO mapRowDocente(ResultSet resultSet, int rowNum) throws SQLException {
        return new DocenteDTO(
                resultSet.getInt("cve_docente"),
                resultSet.getString("curp"),
                resultSet.getString("nombre"),
                resultSet.getString("primer_apellido"),
                resultSet.getString("segundo_apellido"),
                resultSet.getString("telefono1"),
                resultSet.getString("telefono2"),
                resultSet.getString("correo1"),
                resultSet.getString("correo2"),
                resultSet.getString("domicilio"),
                resultSet.getString("consideraciones")
        );
    }

    public static ParticipacionDTO mapRowParticipacion(ResultSet resultSet, int rowNum) throws SQLException {
        ParticipacionDTO dto=new ParticipacionDTO();
        
        dto.setCveDocente(resultSet.getInt("cve_docente"));
        dto.setCurpDocente(resultSet.getString("curp"));
        dto.setNombreDocente(resultSet.getString("nombre"));
        dto.setPrimerApellidoDocente(resultSet.getString("primer_apellido"));
        dto.setSegundoApellidoDocente(resultSet.getString("segundo_apellido"));
        dto.setCveEntidad(resultSet.getInt("cve_cat_entidad_federativa"));
        dto.setEntidad(resultSet.getString("entidad"));
        dto.setAnioAplicacion(resultSet.getInt("anio_aplicacion"));
        dto.setCveSostenimiento(resultSet.getInt("cve_cat_tipo_sostenimiento"));
        dto.setSostenimiento(resultSet.getString("sostenimiento"));
        dto.setCveServicioEducativo(resultSet.getInt("cve_cat_servicio_educativo"));
        dto.setServicioEducativo(resultSet.getString("servicio_educativo"));
        dto.setCveModalidad(resultSet.getInt("cve_cat_modalidad"));
        dto.setCveCct(resultSet.getString("cve_cat_cct"));
        dto.setCct(resultSet.getString("cct"));
        dto.setHuella(resultSet.getString("huella"));
        dto.setFechaRegistro(resultSet.getTimestamp("fecha_registro"));
        dto.setEstatus(resultSet.getInt("cve_cat_estatus_registro"));
                
        return dto;
    }

    public static ArchivoDTO mapRowArchivo(ResultSet resultSet, int rowNum) throws SQLException {
        ArchivoDTO dto=new ArchivoDTO();
        
        dto.setMimeType("application/pdf");
        dto.setNombreInterno(resultSet.getString("nombre_interno"));
        dto.setNombreOriginal(resultSet.getString("nombre_archivo"));
        
        return dto;
    }
}

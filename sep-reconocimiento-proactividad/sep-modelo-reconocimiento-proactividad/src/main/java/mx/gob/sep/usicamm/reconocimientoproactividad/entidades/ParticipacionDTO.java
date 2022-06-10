package mx.gob.sep.usicamm.reconocimientoproactividad.entidades;

import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author hiryu
 */
@Getter
@Setter
public class ParticipacionDTO {
    private Integer cveDocente;
    private String curpDocente;
    private String nombreDocente;
    private String primerApellidoDocente;
    private String segundoApellidoDocente;
    private Integer cveEntidad;
    private String entidad;
    private Integer anioAplicacion;
    private String nombreTrabajo;
    private String resumenPracticaEducativa;
    private Integer cveSostenimiento;
    private String sostenimiento;
    private Integer cveServicioEducativo;
    private String servicioEducativo;
    private Integer cveModalidad;
    private String modalidad;
    private String cveCct;
    private String cct;
    private String huella;
    private Date fechaRegistro;
    private List<ArchivoDTO> archivos;
    
    public ParticipacionDTO(){
    }
}

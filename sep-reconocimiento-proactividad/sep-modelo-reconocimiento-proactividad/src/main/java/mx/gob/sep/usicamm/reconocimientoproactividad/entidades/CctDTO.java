package mx.gob.sep.usicamm.reconocimientoproactividad.entidades;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author AlbertoSánchezLópez
 */
@Getter 
@Setter
@ToString
@AllArgsConstructor
public class CctDTO {
    private String cve;
    private String nombre;
    private String direccion;
    private String municipio;
    private String localidad;
    private String numeroExterior;
    private String telefono;
    private String tipo;
}

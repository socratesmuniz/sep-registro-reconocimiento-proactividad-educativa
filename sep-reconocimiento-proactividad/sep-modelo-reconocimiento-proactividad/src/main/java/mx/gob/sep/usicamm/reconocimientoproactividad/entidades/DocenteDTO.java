package mx.gob.sep.usicamm.reconocimientoproactividadeducativa.entidades;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author hiryu
 */
@Getter 
@Setter
@ToString
@AllArgsConstructor
public class DocenteDTO {
    private Integer cveDocente;
    private String curp;
    private String nombre;
    private String primerApellido;
    private String segundoApellido;
    private String telefono1;
    private String telefono2;
    private String correo1;
    private String correo2;
    private String domicilio;
    private String consideraciones;
}

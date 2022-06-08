package mx.gob.sep.usicamm.reconocimientoproactividadeducativa.entidades;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author albertosanchezlopez
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
public class UsuarioDTO {
    private Integer cveDocente;
    private String nombre;
    private String primerApellido;
    private String segundoApellido;
    
    public UsuarioDTO(){
    }
}

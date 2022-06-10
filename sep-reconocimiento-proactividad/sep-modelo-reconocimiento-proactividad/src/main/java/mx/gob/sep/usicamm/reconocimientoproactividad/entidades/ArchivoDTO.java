package mx.gob.sep.usicamm.reconocimientoproactividad.entidades;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author AlbertoSánchezLópez
 */
@Getter
@Setter
@AllArgsConstructor
public class ArchivoDTO {
    private String nombreOriginal;
    private String mimeType;
    private String nombreInterno;
    private String contenidoBase64;
    
    public ArchivoDTO(){
    }
}

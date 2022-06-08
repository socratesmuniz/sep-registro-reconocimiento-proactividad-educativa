package mx.gob.sep.usicamm.reconocimientoproactividad.negocio.excepciones;

/**
 *
 * @author hiryu
 */
public class OperacionInvalidaBdException extends Exception{
    public OperacionInvalidaBdException(Exception ex){
        super(ex);
    }
}

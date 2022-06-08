package mx.gob.sep.usicamm.reconocimientoproactividadeducativa.negocio.excepciones;

/**
 *
 * @author hiryu
 */
public class OperacionInvalidaBdException extends Exception{
    public OperacionInvalidaBdException(Exception ex){
        super(ex);
    }
}

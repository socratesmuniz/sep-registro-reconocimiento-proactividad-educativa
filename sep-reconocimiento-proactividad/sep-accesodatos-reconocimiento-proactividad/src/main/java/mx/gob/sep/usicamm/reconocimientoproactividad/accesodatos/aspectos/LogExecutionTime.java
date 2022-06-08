package mx.gob.sep.usicamm.reconocimientoproactividadeducativa.accesodatos.aspectos;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author albertosanchezlopez
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public  @interface LogExecutionTime {
    
}

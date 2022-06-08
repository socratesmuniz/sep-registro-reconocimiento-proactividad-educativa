package mx.gob.sep.usicamm.reconocimientoproactividad.restservices.aspectos;

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
public @interface LogRESTTime {
    String serviceName() default "SERVICIO DE ADMISION BASICA";
}

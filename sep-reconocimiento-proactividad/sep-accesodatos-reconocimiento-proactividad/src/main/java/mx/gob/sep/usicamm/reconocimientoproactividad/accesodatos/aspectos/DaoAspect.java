package mx.gob.sep.usicamm.reconocimientoproactividad.accesodatos.aspectos;

import java.util.Map;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Component;

/**
 *
 * @author rodrigo
 */
@Aspect
@Component
public class DaoAspect {
    private final static Logger LOG = LoggerFactory.getLogger(DaoAspect.class);

    @Before("execution(* org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate.*(..))")
    public void aroundAdviceDao(JoinPoint jp) throws Throwable {
        Object[] methodArgs = jp.getArgs();
        String statement = "";
        if (methodArgs != null && methodArgs.length > 0 && methodArgs[0] != null && methodArgs[0] instanceof String) {
            statement = (String) methodArgs[0];
        }

        if (!statement.equals("") && methodArgs.length > 1 && methodArgs[1] != null && methodArgs[1] instanceof MapSqlParameterSource) {
            MapSqlParameterSource mapSqlParameterSource = (MapSqlParameterSource) methodArgs[1];
            Map<String, Object> parametros = mapSqlParameterSource.getValues();
            for (String key : parametros.keySet()) {
                if (parametros.get(key) != null && parametros.get(key) instanceof Integer) {
                    statement = statement.replace(":" + key, ((Integer) parametros.get(key)) + "");
                } else if (parametros.get(key) != null && parametros.get(key) instanceof String) {
                    statement = statement.replace(":" + key, "'" + (String) parametros.get(key) + "'");
                }
            }
        }

        LOG.debug("SQL: " + statement);
    }
}

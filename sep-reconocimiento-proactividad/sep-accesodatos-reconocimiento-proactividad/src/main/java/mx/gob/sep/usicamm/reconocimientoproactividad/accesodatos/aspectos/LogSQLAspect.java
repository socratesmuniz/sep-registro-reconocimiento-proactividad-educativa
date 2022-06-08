package mx.gob.sep.usicamm.reconocimientoproactividad.accesodatos.aspectos;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LogSQLAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogSQLAspect.class);
    @Autowired
    private DataSource ds;

    @Before("execution(* org.springframework.jdbc.core.JdbcOperations.*(String, ..))")
    public void logSQL(JoinPoint jp) throws Throwable {
        Object[] methodArgs = jp.getArgs(), sqlArgs = null;
        String statement = methodArgs[0].toString();
        
        // find the SQL arguments (parameters)
        for (int i = 1, n = methodArgs.length; i < n; i++) {
            Object arg = methodArgs[i];
            if (arg instanceof Object[]) {
                sqlArgs = (Object[])arg;
                break;
            }
        }
        
        // fill in any SQL parameter place-holders (?'s)
        String completedStatement = (sqlArgs == null ? statement : fillParameters(statement, sqlArgs));
        // log it
        LOGGER.debug("SQL: " + completedStatement);
    }


    private String fillParameters(String statement, Object[] sqlArgs) {
        StringBuilder completedSqlBuilder = new StringBuilder(Math.round(statement.length() * 1.2f));
        int index, // will hold the index of the next ?
                prevIndex = 0; // will hold the index of the previous ? + 1

        // loop through each SQL argument
        for (Object arg : sqlArgs) {
            index = statement.indexOf("?", prevIndex);
            if (index == -1){
                break; // bail out if there's a mismatch in # of args vs. ?'s
            }

            // append the chunk of SQL coming before this ?
            completedSqlBuilder.append(statement.substring(prevIndex, index));
            // append the replacement for the ?
            if (arg == null){
                completedSqlBuilder.append("NULL");
            }
            else{
                completedSqlBuilder.append(arg.toString());
            }

            prevIndex = index + 1;
        }

        // add the rest of the SQL if any
        if (prevIndex != statement.length()){
            completedSqlBuilder.append(statement.substring(prevIndex));
        }

        return completedSqlBuilder.toString();
    }

    @Before("execution(* mx.gob.sep.usicamm.reconocimientoproactividad.accesodatos.*.*(..))")
    public void logBeforeConnection(JoinPoint jp) throws Throwable {
        logDataSourceInfos("Before", jp);
    }

    public void logDataSourceInfos(final String time, final JoinPoint jp) {
        final String method = String.format("%s:%s", jp.getTarget().getClass().getName(), jp.getSignature().getName());
        LOGGER.info(String.format("%s - conexiones activas:%d, conexiones en espera:%d, hilos en espera de una conexion:%d.", method, ds.getNumActive(), ds.getNumIdle(), ds.getWaitCount()));
    }
}

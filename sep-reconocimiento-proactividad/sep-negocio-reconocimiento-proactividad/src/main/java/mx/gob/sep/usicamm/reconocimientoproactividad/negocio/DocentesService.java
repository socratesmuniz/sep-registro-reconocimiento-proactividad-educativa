package mx.gob.sep.usicamm.reconocimientoproactividad.negocio;

import lombok.extern.slf4j.Slf4j;
import mx.gob.sep.usicamm.reconocimientoproactividad.accesodatos.DocenteDAO;
import mx.gob.sep.usicamm.reconocimientoproactividad.entidades.DocenteDTO;
import mx.gob.sep.usicamm.reconocimientoproactividad.negocio.excepciones.AccesoDatosExcepcion;
import mx.gob.sep.usicamm.reconocimientoproactividad.negocio.excepciones.OperacionInvalidaBdException;
import mx.gob.sep.usicamm.reconocimientoproactividad.negocio.utils.Constantes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.stereotype.Service;

/**
 *
 * @author hiryu
 */
@Service
@Slf4j
public class DocentesService {
    private final DocenteDAO docenteDAO;
    
    @Autowired
    public DocentesService(DocenteDAO repository){
        this.docenteDAO = repository;
    }

    /**
     * Recupera el docente relacionado con un curp
     * @param curp CURP a buscar
     * @return Docente relacionado con el CURP
     * @throws mx.gob.sep.usicamm.reconocimientoproactividad.negocio.excepciones.AccesoDatosExcepcion
     * @throws mx.gob.sep.usicamm.reconocimientoproactividad.negocio.excepciones.OperacionInvalidaBdException
     */
    public DocenteDTO getDocente(String curp) throws AccesoDatosExcepcion, OperacionInvalidaBdException{        
        try {
            return this.docenteDAO.selectDocenteByCurp(curp);
        } 
        catch (CannotGetJdbcConnectionException e){
            log.error(Constantes.BD_EXCEPTION_JDBC, e);
            throw new AccesoDatosExcepcion(e);
        } 
        catch (BadSqlGrammarException | UncategorizedSQLException e){
            log.error(Constantes.BD_EXCEPTION_SQL, e);
            throw new OperacionInvalidaBdException(e);
        }
    }
}

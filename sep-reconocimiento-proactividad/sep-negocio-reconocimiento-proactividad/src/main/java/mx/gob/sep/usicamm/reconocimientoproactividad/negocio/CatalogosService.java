package mx.gob.sep.usicamm.reconocimientoproactividad.negocio;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import mx.gob.sep.usicamm.reconocimientoproactividad.accesodatos.CatalogosDAO;
import mx.gob.sep.usicamm.reconocimientoproactividad.entidades.CatalogoDTO;
import mx.gob.sep.usicamm.reconocimientoproactividad.entidades.CctDTO;
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
 * @author albertosanchezlopez
 */
@Service
@Slf4j
public class CatalogosService {
    private final CatalogosDAO catalogosDAO;
    
    @Autowired
    public CatalogosService(CatalogosDAO repository){
        this.catalogosDAO = repository;
    }

    /**
     * Recupera la lista de entidades federativas que existen para el proceso
     * @return Lista de entidades federativas
     * @throws mx.gob.sep.usicamm.reconocimientoproactividad.negocio.excepciones.AccesoDatosExcepcion
     * @throws mx.gob.sep.usicamm.reconocimientoproactividad.negocio.excepciones.OperacionInvalidaBdException
     */
    public List<CatalogoDTO> getEntidades() throws AccesoDatosExcepcion, OperacionInvalidaBdException{
        try {
            return this.catalogosDAO.selectEntidades();
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

    /**
     * Recupero los datos del catalogo de sostenimientos
     * @param cveEntidad Clave de la entidad de la que se desean los sostenimientos
     * @return Lista de sostenimientos
     * @throws AccesoDatosExcepcion 
     * @throws mx.gob.sep.usicamm.reconocimientoproactividad.negocio.excepciones.OperacionInvalidaBdException 
     */
    public List<CatalogoDTO> getSostenimientos(Integer cveEntidad) throws AccesoDatosExcepcion, OperacionInvalidaBdException{
        try {
            return this.catalogosDAO.selectSostenimientos(cveEntidad);
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

    /**
     * Recupero los datos del catalogo de servicios educativos
     * @return Lista de servicios educativos
     * @throws AccesoDatosExcepcion 
     * @throws mx.gob.sep.usicamm.reconocimientoproactividad.negocio.excepciones.OperacionInvalidaBdException 
     */
    public List<CatalogoDTO> getServicios() throws AccesoDatosExcepcion, OperacionInvalidaBdException{
        try {
            return this.catalogosDAO.selectServicios();
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

    /**
     * Recupero los datos de un CCT
     * @param cve Clave del CCT del que se desean los datos
     * @return Datos del CCT
     * @throws AccesoDatosExcepcion 
     * @throws mx.gob.sep.usicamm.reconocimientoproactividad.negocio.excepciones.OperacionInvalidaBdException 
     */
    public CctDTO getCct(String cve) throws AccesoDatosExcepcion, OperacionInvalidaBdException{
        try {
            return this.catalogosDAO.selectCctByCve(cve);
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

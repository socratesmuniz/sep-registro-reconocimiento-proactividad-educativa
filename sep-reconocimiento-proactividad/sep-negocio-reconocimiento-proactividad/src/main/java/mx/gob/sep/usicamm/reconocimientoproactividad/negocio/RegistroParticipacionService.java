package mx.gob.sep.usicamm.reconocimientoproactividadeducativa.negocio;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import mx.gob.sep.usicamm.reconocimientoproactividadeducativa.accesodatos.RegistroParticipacionDAO;
import mx.gob.sep.usicamm.reconocimientoproactividadeducativa.configuracion.ConfiguracionAplicacion;
import mx.gob.sep.usicamm.reconocimientoproactividadeducativa.entidades.CatalogoDTO;
import mx.gob.sep.usicamm.reconocimientoproactividadeducativa.entidades.ParticipacionDTO;
import mx.gob.sep.usicamm.reconocimientoproactividadeducativa.negocio.excepciones.AccesoDatosExcepcion;
import mx.gob.sep.usicamm.reconocimientoproactividadeducativa.negocio.excepciones.NegocioExcepcion;
import mx.gob.sep.usicamm.reconocimientoproactividadeducativa.negocio.excepciones.OperacionInvalidaBdException;
import mx.gob.sep.usicamm.reconocimientoproactividadeducativa.negocio.utils.Constantes;
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
public class RegistroParticipacionService {
    private final ConfiguracionAplicacion config;
    private final RegistroParticipacionDAO registroParticipacionDAO;
    
    @Autowired
    public RegistroParticipacionService(ConfiguracionAplicacion config, RegistroParticipacionDAO registroParticipacionDAO) {
        this.config=config;
        this.registroParticipacionDAO=registroParticipacionDAO;
    }

    public boolean actualizaParticipacion(Integer usuario, ParticipacionDTO datos) throws AccesoDatosExcepcion, NegocioExcepcion, OperacionInvalidaBdException{
        List<ParticipacionDTO> pars;
        boolean exito=true;
        
        try {
            exito=exito & this.registroParticipacionDAO.insertParticipacion(datos, usuario);
            
            return exito;
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

    public ParticipacionDTO recuperaParticipacion(Integer cveDocente) throws AccesoDatosExcepcion, OperacionInvalidaBdException{
        try {
            ParticipacionDTO par = this.registroParticipacionDAO.selectParticipacion(cveDocente);
            
            return par;
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
    
    
    
    
    private String generaHuella(int docente, int entidad){
        StringBuilder sb=new StringBuilder("");
        ParticipacionDTO pTO=this.registroParticipacionDAO.selectParticipacion(docente);
        
        sb.append("docente:").append(pTO.getCveDocente()).append("|")
                .append("entidad:").append(pTO.getCveEntidad()).append("|")
                .append("sostenimiento:").append(pTO.getCveSostenimiento()).append("|")
                .append("servicioEducativo:").append(pTO.getCveServicioEducativo()).append("|");
        
        log.info("Cadena original: "+sb.toString());
        
        return cifraHuella(sb.toString());
    }
    
    /**
     * Genera el cifrado de los datos a considerar para la huella (SHA-512)
     * @param str Cadena original
     * @return Hash de los datos
     */
    private String cifraHuella(String str){
        byte[] digest, buffer = str.getBytes();
        String hash = "";

        try{
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(buffer);
            digest = md.digest();

            for(byte aux : digest) {
                int b = aux & 0xff;
                if (Integer.toHexString(b).length() == 1) hash += "0";
                hash += Integer.toHexString(b);
            }
        }
        catch(NoSuchAlgorithmException ex){
            log.error("Se tuvo un problema con el cifrado de datos ", ex);
        }

        return hash;
    }
}

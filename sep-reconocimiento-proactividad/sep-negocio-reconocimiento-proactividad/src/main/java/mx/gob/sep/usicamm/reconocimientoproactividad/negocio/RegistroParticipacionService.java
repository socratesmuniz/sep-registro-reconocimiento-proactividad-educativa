package mx.gob.sep.usicamm.reconocimientoproactividad.negocio;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import lombok.extern.slf4j.Slf4j;
import mx.gob.sep.usicamm.reconocimientoproactividad.accesodatos.RegistroParticipacionDAO;
import mx.gob.sep.usicamm.reconocimientoproactividad.configuracion.ConfiguracionAplicacion;
import mx.gob.sep.usicamm.reconocimientoproactividad.entidades.ParticipacionDTO;
import mx.gob.sep.usicamm.reconocimientoproactividad.negocio.excepciones.AccesoDatosExcepcion;
import mx.gob.sep.usicamm.reconocimientoproactividad.negocio.excepciones.NegocioExcepcion;
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
public class RegistroParticipacionService {
    private final ConfiguracionAplicacion config;
    private final RegistroParticipacionDAO registroParticipacionDAO;
    
    @Autowired
    public RegistroParticipacionService(ConfiguracionAplicacion config, RegistroParticipacionDAO registroParticipacionDAO) {
        this.config=config;
        this.registroParticipacionDAO=registroParticipacionDAO;
    }

    public boolean actualizaParticipacion(ParticipacionDTO datos) throws AccesoDatosExcepcion, NegocioExcepcion, OperacionInvalidaBdException{
        boolean exito=true;
        
        try {
            //valida que no tenga una participacion
            ParticipacionDTO rTmp = this.registroParticipacionDAO.selectParticipacion(datos.getCveDocente(), datos.getCveEntidad(), datos.getAnioAplicacion());
            if(rTmp!=null){
                throw new NegocioExcepcion("El participante ya tiene una participación registrada");
            }
            
            exito=exito|this.registroParticipacionDAO.insertParticipacion(datos);
            if(exito){
                exito=exito|this.registroParticipacionDAO.insertDetalleTrabajo(datos);
            }
            
            return exito;
        } 
        catch (CannotGetJdbcConnectionException e){
            log.error(Constantes.BD_EXCEPTION_JDBC, e);
            this.eliminaParticipacion(datos);
            throw new AccesoDatosExcepcion(e);
        } 
        catch (BadSqlGrammarException | UncategorizedSQLException e){
            log.error(Constantes.BD_EXCEPTION_SQL, e);
            this.eliminaParticipacion(datos);
            throw new OperacionInvalidaBdException(e);
        }
    }

    public ParticipacionDTO recuperaParticipacion(Integer cveDocente, Integer cveEntidad, Integer anioAplicacion) throws AccesoDatosExcepcion, OperacionInvalidaBdException{
        ParticipacionDTO par;
        try {
            //valida que no tenga una participacion
            par=this.registroParticipacionDAO.selectParticipacion(cveDocente, cveEntidad, anioAplicacion);
            if(par!=null){
                par.setNombreTrabajo(this.registroParticipacionDAO.selectDetalleTrabajo(cveDocente, cveEntidad, anioAplicacion));
            }
            
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
                
    public String generaHuella(ParticipacionDTO participacion){
        StringBuilder sb=new StringBuilder("");
        
        sb.append("docente:").append(participacion.getCveDocente()).append("|")
                .append("entidad:").append(participacion.getCveEntidad()).append("|")
                .append("añoParticipacion:").append(participacion.getAnioAplicacion()).append("|")
                .append("nombreTrabajo:").append(participacion.getNombreTrabajo()).append("|")
                .append("sostenimiento:").append(participacion.getCveSostenimiento()).append("|")
                .append("servicioEducativo:").append(participacion.getCveServicioEducativo()).append("|")
                .append("modalidad:").append(participacion.getCveModalidad()).append("|")
                .append("cct:").append(participacion.getCveCct()).append("|");
        
        log.info("Cadena original: "+sb.toString());
        
        return cifraHuella(sb.toString());
    }
    
    
    

    public void eliminaParticipacion(ParticipacionDTO datos) throws AccesoDatosExcepcion, OperacionInvalidaBdException{
        try {
            this.registroParticipacionDAO.deleteParticipacion(datos);
        } 
        catch (Exception e){
            log.error(Constantes.BD_EXCEPTION_JDBC, e);
        }
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

package mx.gob.sep.usicamm.reconocimientoproactividad.negocio;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import mx.gob.sep.usicamm.reconocimientoproactividad.accesoarchivos.ArchivoDAO;
import mx.gob.sep.usicamm.reconocimientoproactividad.accesodatos.RegistroParticipacionDAO;
import mx.gob.sep.usicamm.reconocimientoproactividad.configuracion.ConfiguracionAplicacion;
import mx.gob.sep.usicamm.reconocimientoproactividad.entidades.ArchivoDTO;
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
    private final ArchivoDAO archivoDAO;
    
    @Autowired
    public RegistroParticipacionService(ConfiguracionAplicacion config, RegistroParticipacionDAO registroParticipacionDAO,
            ArchivoDAO archivoDAO) {
        this.config=config;
        this.registroParticipacionDAO=registroParticipacionDAO;
        this.archivoDAO=archivoDAO;
    }

    public boolean actualizaParticipacion(ParticipacionDTO datos) throws AccesoDatosExcepcion, NegocioExcepcion, OperacionInvalidaBdException{
        boolean exito=true;
        List<ArchivoDTO> docs;
        
        try {
            //valida si tiene una participacion
            ParticipacionDTO rTmp = this.registroParticipacionDAO.selectParticipacion(datos.getCveDocente());
            datos.setHuella(this.generaHuella(datos));
            
            //si no tiene agrego los datos
            if(rTmp==null){
                exito=exito|this.registroParticipacionDAO.insertParticipacion(datos);
            }
            else{
                exito=exito|this.registroParticipacionDAO.updateParticipacion(datos);
            }
                        
            if(exito){
                this.registroParticipacionDAO.deleteDetalleTrabajo(datos);
                exito=exito|this.registroParticipacionDAO.insertDetalleTrabajo(datos);
                
                //elimino los documentos por si existen
                docs=this.registroParticipacionDAO.selectDocumentos(datos.getCveDocente(), datos.getCveEntidad(), datos.getAnioAplicacion());
                for(int i=0; docs!=null && i<docs.size(); i++){
                    this.archivoDAO.eliminaArchivo(docs.get(i).getNombreInterno());
                }
                
                //registros los documentos que envian
                this.registroParticipacionDAO.deleteArchivos(datos.getCveDocente(), datos.getCveEntidad(), datos.getAnioAplicacion());
                for(int i=0; datos.getArchivos()!=null && i<datos.getArchivos().size(); i++){
                    datos.getArchivos().get(i).setNombreInterno((datos.getCveEntidad()<10? "0"+datos.getCveEntidad(): datos.getCveEntidad())+"-"
                            +datos.getCurpDocente()+"-BASICA");
                    exito=exito|this.registroParticipacionDAO.insertArchivo(datos.getCveDocente(), datos.getCveEntidad(), datos.getAnioAplicacion(),
                            datos.getArchivos().get(i));
                    
                    if(exito){
                        exito=exito|this.archivoDAO.guardaArchivo(datos.getArchivos().get(i).getNombreInterno(), datos.getArchivos().get(i).getContenidoBase64());
                    }
                }
            }
            
            return exito;
        } 
        catch (CannotGetJdbcConnectionException e){
            log.error(Constantes.BD_EXCEPTION_JDBC, e);
            this.eliminaDetalleTrabajos(datos);
            this.eliminaParticipacion(datos);
            throw new AccesoDatosExcepcion(e);
        } 
        catch (BadSqlGrammarException | UncategorizedSQLException e){
            log.error(Constantes.BD_EXCEPTION_SQL, e);
            this.eliminaDetalleTrabajos(datos);
            this.eliminaParticipacion(datos);
            throw new OperacionInvalidaBdException(e);
        }
        catch (IOException e){
            log.error(Constantes.FILE_EXCEPTION, e);
            this.eliminaDocumentos(datos);
            this.eliminaDetalleTrabajos(datos);
            this.eliminaParticipacion(datos);
            throw new AccesoDatosExcepcion(e);
        } 
    }

    public boolean finalizaParticipacion(Integer cveDocente) throws AccesoDatosExcepcion, NegocioExcepcion, OperacionInvalidaBdException{
        try {
            //valida que no tenga una participacion
            ParticipacionDTO pTmp = this.registroParticipacionDAO.selectParticipacion(cveDocente);
            if(pTmp==null){
                throw new NegocioExcepcion("El participante no tiene una participación registrada");
            }            
            else if(pTmp.getEstatus()!=Constantes.REGISTRADO){
                throw new NegocioExcepcion("La participación esta en un estado no válido");
            }            
            
            return this.registroParticipacionDAO.updateEstatus(Constantes.CERRADO, cveDocente, pTmp.getCveEntidad(), pTmp.getAnioAplicacion());
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
        ParticipacionDTO par;
        try {
            par=this.registroParticipacionDAO.selectParticipacion(cveDocente);
            if(par!=null){
                par.setNombreTrabajo(this.registroParticipacionDAO.selectDetalleTrabajo(cveDocente, par.getCveEntidad(), par.getAnioAplicacion()));
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

    public ArchivoDTO recuperaDocumento(Integer cveDocente, Integer cveEntidad, Integer anioAplicacion) throws AccesoDatosExcepcion, OperacionInvalidaBdException{
        List<ArchivoDTO> docs;
        try {
            docs=this.registroParticipacionDAO.selectDocumentos(cveDocente, cveEntidad, anioAplicacion);
            if(docs!=null && !docs.isEmpty()){
                docs.get(0).setContenidoBase64( this.archivoDAO.recuperaArchivo(docs.get(0).getNombreInterno()) );
                
                return docs.get(0);
            }
            
            return null;
        } 
        catch (CannotGetJdbcConnectionException e){
            log.error(Constantes.BD_EXCEPTION_JDBC, e);
            throw new AccesoDatosExcepcion(e);
        } 
        catch (BadSqlGrammarException | UncategorizedSQLException e){
            log.error(Constantes.BD_EXCEPTION_SQL, e);
            throw new OperacionInvalidaBdException(e);
        }
        catch (IOException e){
            log.error(Constantes.FILE_EXCEPTION, e);
            throw new AccesoDatosExcepcion(e);
        } 
    }
    
    
    

    private void eliminaParticipacion(ParticipacionDTO datos) throws AccesoDatosExcepcion, OperacionInvalidaBdException{
        try {
            this.registroParticipacionDAO.deleteParticipacion(datos);
        } 
        catch (Exception e){
            log.error(Constantes.BD_EXCEPTION_JDBC, e);
        }
    }

    private void eliminaDetalleTrabajos(ParticipacionDTO datos) throws AccesoDatosExcepcion, OperacionInvalidaBdException{
        try {
            this.registroParticipacionDAO.deleteDetalleTrabajo(datos);
        } 
        catch (Exception e){
            log.error(Constantes.BD_EXCEPTION_JDBC, e);
        }
    }

    private void eliminaDocumentos(ParticipacionDTO datos) throws AccesoDatosExcepcion, OperacionInvalidaBdException{
        try {
            this.registroParticipacionDAO.deleteArchivos(datos.getCveDocente(), datos.getCveEntidad(), datos.getAnioAplicacion());
        } 
        catch (Exception e){
            log.error(Constantes.BD_EXCEPTION_JDBC, e);
        }
    }
                
    private String generaHuella(ParticipacionDTO participacion){
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

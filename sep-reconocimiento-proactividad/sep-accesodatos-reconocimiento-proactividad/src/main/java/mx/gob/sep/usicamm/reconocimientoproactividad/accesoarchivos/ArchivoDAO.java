package mx.gob.sep.usicamm.reconocimientoproactividad.accesoarchivos;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 * @author AlbertoSánchezLópez
 */
@Component
@Slf4j
public class ArchivoDAO {
    @Value("${cfg.documentos.ruta.local}")
    private String rutaBase;
    
    public ArchivoDAO(){        
    }
    
    public boolean guardaArchivo(String nombre, String contenidoBase64) throws IOException{
        this.validaConformacionBase();
        File fTmp=new File(this.rutaBase);
        if(!fTmp.exists()){
            throw new IOException("La ruta donde intenta guardar '"+this.rutaBase+"' no existe");
        }
        
        fTmp=new File(this.rutaBase+nombre);
        log.debug("Intenta guardar archivo "+fTmp.getAbsolutePath());
        
        if(fTmp.exists()){
            throw new IOException("El archivo que se intenta guardar '"+fTmp.getAbsolutePath()+"' ya existe");
        }
     
        Files.write(Paths.get(fTmp.getAbsolutePath()), Base64.getDecoder().decode(contenidoBase64));
        
        return true;
    }
    
    public String recuperaArchivo(String nombre) throws IOException{
        this.validaConformacionBase();
        
        File fTmp=new File(this.rutaBase+nombre);
        log.debug("Intenta recuperar archivo "+fTmp.getAbsolutePath());
        
        if(!fTmp.exists()){
            throw new IOException("El archivo que se intenta recuperar '"+fTmp.getAbsolutePath()+"' no existe");
        }
     
        byte[] content=Files.readAllBytes(Paths.get(fTmp.getAbsolutePath()));        
        if(content!=null){
            log.debug("archivo: "+Base64.getEncoder().encodeToString(content));
            return Base64.getEncoder().encodeToString(content);
        }
        else{
            throw new IOException("No se logro recuperar el contenido de '"+fTmp.getAbsolutePath()+"'");
        }
    }
    
    
    
    private void validaConformacionBase(){
        if(this.rutaBase!=null && !this.rutaBase.endsWith(File.separator)){
            this.rutaBase+=File.separatorChar;
        }
    }
}

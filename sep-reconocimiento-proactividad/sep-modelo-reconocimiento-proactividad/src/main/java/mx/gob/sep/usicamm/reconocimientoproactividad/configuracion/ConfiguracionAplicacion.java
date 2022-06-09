package mx.gob.sep.usicamm.reconocimientoproactividad.configuracion;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 * @author hiryu
 */
@Component
public class ConfiguracionAplicacion {
    public ConfiguracionAplicacion(){
    }
    
    @Value("${cfg.convocatoria.niveleducativo}")
    public int CVE_NIVEL;

    @Value("${security.token.correo}")
    public String TOKEN_CORREO;

    @Value("${app.clave.sistema}")
    public int CVE_SISTEMA;

    @Value("${cfg.convocatoria.cicloescolar}")
    public int CVE_CICLO_ESCOLAR;

    @Value("${cfg.filesystems.logos}")
    public String RUTA_LOGOS;

    @Value("${cfg.token.validacion}")
    public Boolean VALIDACION_TOKEN;

    @Value("${cfg.documentos.logo.locales}")
    public Boolean LOGOS_LOCALES;

    @Value("${cfg.documentos.marca-agua}")
    public Boolean MARCA_AGUA;

    @Value("${cfg.documentos.marca-agua.texto}")
    public String TEXTO_MARCA_AGUA;

    //Motor de Reglas
    @Value("${app.reglas.negocio.servicio}")
    public String REGLAS_NEGOCIO_URL;

    @Value("${app.reglas.negocio.clave}")
    public String REGLAS_NEGOCIO_PASS;
    
    @Value("${app.reglas.negocio.cve-regla-sistema}")
    public int REGLAS_NEGOCIO_SISTEMA;
    
    @Value("${cfg.reglas.negocio.acceso}")
    public int REGLA_ACCESO;
    
    @Value("${cfg.reglas.negocio.participacion}")
    public int REGLA_PARTICIPACION;
    
    @Value("${cfg.reglas.negocio.datosPersonales}")
    public int REGLA_DATOS_PERSONALES;
    
    @Value("${cfg.reglas.negocio.registro}")
    public int REGLA_DATOS_REGISTRO;
    //Motor de Reglas
}

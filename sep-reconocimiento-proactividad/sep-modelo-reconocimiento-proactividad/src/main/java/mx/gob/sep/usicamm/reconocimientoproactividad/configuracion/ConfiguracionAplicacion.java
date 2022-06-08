package mx.gob.sep.usicamm.reconocimientoproactividadeducativa.configuracion;

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
    
    @Value("${cfg.convocatoria.proceso}")
    public int CVE_PROCESO;

    @Value("${cfg.convocatoria.niveleducativo}")
    public int CVE_NIVEL;

    @Value("${security.token.correo}")
    public String TOKEN_CORREO;

    @Value("${app.clave.sistema}")
    public int CVE_SISTEMA;

    @Value("${cfg.convocatoria.cicloescolar}")
    public int CVE_CICLO_ESCOLAR;

    @Value("${cfg.sistemas.enlaces.clave}")
    public int CVE_SISTEMA_ENLACES;

    @Value("${cfg.convocatoria.folio.sufijo}")
    public String FOLIO_SUFIJO;

    @Value("${cfg.convocatoria.folio.ciclo}")
    public String FOLIO_CICLO;

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
    //Motor de Reglas
}
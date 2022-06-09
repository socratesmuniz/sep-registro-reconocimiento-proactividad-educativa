package mx.gob.sep.usicamm.reconocimientoproactividad.registro.util;

import gob.sep.usicamm.reglas_negocio.Validacion;
import gob.sep.usicamm.reglas_negocio.exception.ReglaNegocioException;
import gob.sep.usicamm.reglas_negocio.models.RequestValidacion;
import gob.sep.usicamm.reglas_negocio.models.ResponseValidacion;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import mx.gob.sep.usicamm.reconocimientoproactividad.accesodatos.LoginDAO;
import mx.gob.sep.usicamm.reconocimientoproactividad.configuracion.ConfiguracionAplicacion;
import mx.gob.sep.usicamm.reconocimientoproactividad.entidades.UsuarioDTO;
import mx.gob.sep.usicamm.reconocimientoproactividad.negocio.DatosPersonalesService;
import mx.gob.sep.usicamm.reconocimientoproactividad.negocio.utils.Constantes;
import mx.gob.sep.usicamm.reconocimientoproactividad.negocio.utils.MD5HashGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

/**
 *
 * @author albertosanchezlopez
 */
@Service
@Slf4j
public class ValidacionAcceso{
    private final ConfiguracionAplicacion config;
    private final DatosPersonalesService datosPersonalesService;
    private final LoginDAO repository;

    @Autowired
    public ValidacionAcceso(ConfiguracionAplicacion config, LoginDAO repository, DatosPersonalesService datosPersonalesService){
        this.repository=repository;
        this.config=config;
        this.datosPersonalesService=datosPersonalesService;
    }

    /**
     * Autentica a un usuario de mesa
     *
     * @param curp CURP del usuario que intenta ingresar
     * @param token Token del usuario que intenta ingresar
     * @param model
     * @param IP
     * @param browser
     * @param host
     * @return Vista a la que se debe direcionar
     */
    public String autenticaUsuarioMesa(String curp, String token, Model model, String IP, String browser, String host){
        ResponseValidacion response;
        Map<String,String> datosValidacion;
        RequestValidacion requestValidacion;
        
        log.info("Esta conectandose a: "+host);
        
        if(Constantes.NONE.equals(curp) || this.validaParametroToken(token)){
            model.addAttribute(Constantes.MSG_ERROR, Constantes.NO_DATA_AUTHENTICATE);
        } 
        else{            
            if(this.validaToken(token, curp)){
                try{
                    UsuarioDTO usuario=this.repository.selectUsuario(curp);

                    if(usuario != null){
                        //realiza la validacion del motor de reglas
                        datosValidacion=new HashMap();
                        datosValidacion.put("curp", curp);
                        datosValidacion.put("cve_docente", ""+usuario.getCveDocente());
                        datosValidacion.put("ip", IP);
                        datosValidacion.put("tipo", "mesa");
                        
                        requestValidacion=new RequestValidacion();
                        requestValidacion.setCveReglaNegocioSistemaInformacion(this.config.REGLAS_NEGOCIO_SISTEMA);
                        requestValidacion.setCveDocente(usuario.getCveDocente());
                        requestValidacion.setCveMotorReglaNegocio(this.config.REGLA_ACCESO); 
                        requestValidacion.setParametros(datosValidacion);

                        response=Validacion.run(this.config.REGLAS_NEGOCIO_URL, requestValidacion);
                        // continua con el flujo de validación
                        if(response.getValidacion()){
                            model.addAttribute("curp", curp);
                            model.addAttribute("token", this.datosPersonalesService.generaTokenSistema(String.valueOf(usuario.getCveDocente())));
                            model.addAttribute("tokenOriginal", token);
                            model.addAttribute("cveDocente", usuario.getCveDocente());
                            model.addAttribute("nombre", usuario.getNombre() + " " + usuario.getPrimerApellido() + " " + usuario.getSegundoApellido());

                            log.info("Logeo correcto, inicia el proceso");
                            return ConstantesWeb.TEMPLATE_HOME;
                        }
                        //restringe el acceso
                        else{
                            log.info("Se limito el acceso por motor de reglas");
                            model.addAttribute(Constantes.MSG_ERROR, response.getMensajeError());
                        }
                    } 
                    else{
                        model.addAttribute(Constantes.MSG_ERROR, Constantes.USER_NOT_FOUND);
                    }
                } 
                catch(CannotGetJdbcConnectionException e){
                    log.error(Constantes.BD_EXCEPTION_JDBC, e.getLocalizedMessage());
                    model.addAttribute(Constantes.MSG_ERROR, Constantes.NOT_SOURCE);
                } 
                catch(BadSqlGrammarException | UncategorizedSQLException e){
                    log.error(Constantes.BD_EXCEPTION_SQL, e.getLocalizedMessage());
                    model.addAttribute(Constantes.MSG_ERROR, Constantes.BAD_SQL_GRAMMAR);
                }
                catch(ReglaNegocioException e){
                    log.error("Problema para realizar la validación de reglas", e);
                    model.addAttribute(Constantes.MSG_ERROR, "No se logro realizar la validación de reglas");
                }
            } 
            else{
                log.info("Usuario no localizado");
                this.repository.insertLog(curp, Constantes.LOGIN_USUARIO_NO_AUTORIZADO, IP, browser, token);
                model.addAttribute(Constantes.MSG_ERROR, Constantes.AUTHENTICATE_EXPIRED_INCORRECT);
            }
        }

        return ConstantesWeb.TEMPLATE_AVISO;
    }

    /**
     * Revisa si el TOKEN es valido
     *
     * @param token TOKEN a validar
     * @param curp CURP d ela persona que genero el TOKEN
     * @return true, si es valido
     */
    public boolean validaToken(String token, String curp){
        log.debug("Validacion de Token: "+this.config.VALIDACION_TOKEN);
        if(this.config.VALIDACION_TOKEN==null || !this.config.VALIDACION_TOKEN){
            return true;
        }
        
        //si se tiene que validar el token, realiza validacion
        String pass=curp + Constantes.TOKEN_VENUS + Constantes.FORMATTER_LOGIN_VENUS.format(LocalDate.now());
        String myHash=MD5HashGenerator.getMD5Hash(pass);
        log.info(myHash);

        return token.equals(myHash);
    }
    
    public boolean validaParametroToken(String token){
        if(this.config.VALIDACION_TOKEN==null || !this.config.VALIDACION_TOKEN){
            log.debug("No requiere parametro token");
            return false;
        }
        else{
            log.debug("Requiere parametro token, con resultado: "+Constantes.NONE.equals(token));
            return Constantes.NONE.equals(token);
        }
    }
}

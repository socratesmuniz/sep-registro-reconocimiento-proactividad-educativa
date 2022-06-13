package mx.gob.sep.usicamm.reconocimientoproactividad.negocio.utils;

import java.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

/**
 *
 * @author albertosanchezlopez
 */
public enum Constantes {
    MSG_OK;
    @Autowired
    Environment environment;
    
    public static final int COD_OK=0;

    public final static int LOGIN_CORRECTO=1;
    public final static int LOGIN_USUARIO_NO_AUTORIZADO=5;

    public final static int REGISTRADO=2;
    public final static int CERRADO=9;
    
    public static final String NONE="NONE";
    public static final String MSG_ERROR="error";
    public static final String NO_TOKEN="NOTOKEN";
    
    public static final String PARAMETRO_DOMINIO="acceso_x_dominio_REC";
    
    //Mensajes
    public static final String MSG_TOKEN_INVALIDO="Token proporcionado expiró o es incorrecto, intente ingresar nuevamente";
    public static final String MSG_ERROR_SERVIDOR="El servidor no puede atender su solicitud de forma correcta";
    public static final String MSG_ERROR_CONECCION="El servidor no se puede comunicar con el repositorio de datos";

    public static final String MSG_PARTICIPACION_ADD_OK="Se registraron exitosamente los datos de la participación";
    public static final String MSG_PARTICIPACION_DEL_OK="Se eliminaron exitosamente los datos de la participación";
    public static final String MSG_PARTICIPACION_UPT_OK="La participación ha sido actualizada con exito";
    
    public static final String MSG_BIT_CAMBIO_DOCENTE="Cambio realizado por docente";
    public static final String MSG_BIT_CAMBIO_MESA="Cambio realizado por la mesa de registro";
    public static final String MSG_BIT_CAMBIO_AUTORIDAD="Cambio realizado por la autoridad educativa";
    public static final String MSG_BIT_ROLLBACK_DOCENTE="Rollback por sistema en uso de docente";
    public static final String MSG_BIT_ROLLBACK_MESA="Rollback por sistema en uso de mesa de registro";
    
    public static final String TOKEN_VENUS="CN5PD-V3NU5-P455W";

    public static final DateTimeFormatter FORMATTER_LOGIN_VENUS=DateTimeFormatter.ofPattern("dd-MM-yyyy");
    public static final String MD5_ALGORITHM="MD5";

    //Mensajes de BD
    public static final String FILE_EXCEPTION="Incidencia FILE: {}";
    public static final String BD_EXCEPTION_JDBC="Incidencia JDBC: {}";
    public static final String BD_EXCEPTION_SQL="Incidencia SQL: {}";
    public static final String BD_START_OPERATION="INICIA OPER BD";
    public static final String BD_END_OPERATION="FIN OPER BD ------| Tiempo transcurrido: {} ms";

    
    public static final int BITACORA_ALTA=1;
    public static final int BITACORA_BAJA=2;
    public static final int BITACORA_CAMBIO=3;

    public static final String NO_DATA_AUTHENTICATE = "Datos de autenticación no proporcionados";
    public static final String USER_NOT_FOUND = "Usuario no encontrado";
    public static final String USER_NOT_LEVEL = "Usuario no tiene permisos";
    public static final String DOMAIN_INVALID = "No se tiene autorización por parte de la entidad para acceder al dominio actual";
    public static final String NOT_SOURCE = "Origen de datos no disponible";
    public static final String BAD_SQL_GRAMMAR = "Instrucción mal ejecutada";
    public static final String AUTHENTICATE_EXPIRED_INCORRECT = "Token proporcionado expiró o es incorrecto";
    public static final String FIN_PROCESO_PARTICIPANTE = "El proceso del participante ha concluido";
}

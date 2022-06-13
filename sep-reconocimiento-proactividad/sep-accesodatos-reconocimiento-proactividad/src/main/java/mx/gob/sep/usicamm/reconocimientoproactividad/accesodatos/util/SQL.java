package mx.gob.sep.usicamm.reconocimientoproactividad.accesodatos.util;

/**
 *
 * @author albertosanchezlopez
 */
public enum SQL {
    SCHEMA;

    // --- Catalogos ---
    public static final String SQL_SEL_CAT_ENTIDADES="SELECT cve_entidad cve, descripcion_corta descripcion FROM cat_entidades_federativas WHERE cve_entidad<=32 ORDER BY descripcion;";
    public static final String SQL_SEL_CAT_SOSTENIMIENTOS="SELECT ES.cve_cat_tipo_sostenimiento cve, S.descripcion_corta descripcion FROM entidades_sostenimientos ES INNER JOIN cat_tipos_sostenimientos S ON ES.cve_cat_tipo_sostenimiento=S.cve_tipo_sostenimiento AND fecha_inicio_vigencia<=CURRENT_DATE() AND (fecha_fin_vigencia IS NULL OR fecha_fin_vigencia>=CURRENT_DATE) AND ES.cve_cat_entidad_federativa=? ORDER BY 2;";
    public static final String SQL_SEL_CAT_SERVICIOS="SELECT CONCAT(CMS.cve_cat_servicio_educativo, '-', CMS.cve_cat_modalidad) cve, CMS.descripcion_usuario descripcion FROM ctrl_cat_modalidades_servicios_educativos CMS WHERE CMS.cve_cat_sistema_informacion=? AND CMS.cve_cat_nivel_educativo=? ORDER BY 2;";
    public static final String SQL_SEL_CAT_CCT="SELECT cv_cct, c_nombre, inmueble_c_vialidad_principal, inmueble_c_nom_mun, inmueble_c_nom_loc, inmueble_n_extnum, contacto_c_telefono, c_tipo FROM cat_cct WHERE cv_cct=?;";
    // --- Catalogos ---

    
    // --- Usuario ---
    public static final String SQL_SEL_USUARIO="SELECT d.cve_docente, d.nombre, d.primer_apellido, d.segundo_apellido FROM docentes d WHERE d.curp=?";
    public static final String SQL_SEL_DOMINIOS="SELECT dominio_publico FROM balanceador WHERE cve_entidad=?";
    public static final String SQL_INS_LOG_ACCESO="INSERT INTO log_login(cve_sistema_informacion, solicitante, cve_estatus_login, ip_cliente, info_navegador, firma, de_prueba) VALUES (?, ?, ?, ?, ?, ?, ?);";
    public static final String SQL_SEL_PARAM="SELECT valor_entero FROM satap_parametros WHERE nombre_parametro=? AND cve_sistema_informacion=? AND fecha_inicio_vigencia<NOW() AND (fecha_fin_vigencia IS NULL OR fecha_fin_vigencia>NOW());";
    // --- Usuario ---

    
    // --- Docente ---
    public static final String SQL_SEL_DOCENTE_BY_CURP="SELECT D.cve_docente, D.curp, D.nombre, D.primer_apellido, D.segundo_apellido, (SELECT NT.numero_telefonico FROM numeros_telefonicos NT WHERE D.cve_docente=NT.cve_docente ORDER BY NT.fecha_creacion DESC limit 0,1) telefono1, (SELECT NT.numero_telefonico FROM numeros_telefonicos NT WHERE D.cve_docente=NT.cve_docente ORDER BY NT.fecha_creacion DESC limit 1,1) telefono2, (SELECT CE.correo_electronico FROM correos_electronicos CE WHERE D.cve_docente=CE.cve_docente ORDER BY CE.fecha_creacion DESC limit 0,1) correo1, (SELECT CE.correo_electronico FROM correos_electronicos CE WHERE D.cve_docente=CE.cve_docente ORDER BY CE.fecha_creacion DESC limit 1,1) correo2, CONCAT(DOM.calle, ' ', DOM.numero_exterior, ', Col. ', DOM.colonia, ', ', CM.descripcion_larga) domicilio, (SELECT GROUP_CONCAT(CCP.descripcion_larga) FROM rel_docentes_consideraciones_particulares RCP LEFT JOIN cat_consideraciones_particulares CCP ON RCP.cve_consideracion_particular=CCP.cve_consideracion_particular INNER JOIN ctrl_cat_consideraciones_particulares CC ON RCP.cve_consideracion_particular=CC.cve_cat_consideracion_particular AND CC.cve_cat_sistema_informacion=? WHERE RCP.cve_docente=D.cve_docente) consideraciones FROM docentes D LEFT JOIN domicilios DOM ON D.cve_docente=DOM.cve_docente LEFT JOIN cat_municipios CM ON DOM.cve_entidad=CM.cve_entidad AND DOM.cve_municipio=CM.cve_municipio WHERE D.curp=?;";
    // --- Docente ---

    
    // --- Registro ---
    public static final String SQL_INS_REGISTRO_PARTICIPACION="INSERT INTO registro_reconocimiento_proactividad_participacion(cve_cat_nivel_educativo, cve_docente, cve_cat_entidad_federativa, anio_aplicacion, cve_cat_tipo_sostenimiento, cve_cat_servicio_educativo, cve_cat_modalidad, cve_cat_cct, huella, fecha_registro, cve_cat_estatus_registro) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), 2);";
    public static final String SQL_UPD_REGISTRO_PARTICIPACION="UPDATE registro_reconocimiento_proactividad_participacion SET cve_cat_tipo_sostenimiento=?, cve_cat_servicio_educativo=?, cve_cat_modalidad=?, cve_cat_cct=?, huella=? WHERE cve_cat_nivel_educativo=? AND cve_docente=? AND cve_cat_entidad_federativa=? AND anio_aplicacion=?;";
    public static final String SQL_UPD_ESTATUS_PARTICIPACION="UPDATE registro_reconocimiento_proactividad_participacion SET cve_cat_estatus_registro=?, fecha_cambio_estado=NOW() WHERE cve_cat_nivel_educativo=? AND cve_docente=? AND cve_cat_entidad_federativa=? AND anio_aplicacion=?;";
    public static final String SQL_SEL_REGISTRO_PARTICIPACION="SELECT RP.cve_docente, D.curp, D.nombre, D.primer_apellido, D.segundo_apellido, RP.cve_cat_entidad_federativa, E.descripcion_larga entidad, RP.anio_aplicacion, RP.cve_cat_tipo_sostenimiento, S.descripcion_corta sostenimiento, RP.cve_cat_servicio_educativo, RP.cve_cat_modalidad, SE.descripcion_usuario servicio_educativo, RP.cve_cat_cct, C.c_nombre cct, RP.huella, RP.fecha_registro, RP.cve_cat_estatus_registro FROM registro_reconocimiento_proactividad_participacion RP INNER JOIN docentes D ON RP.cve_docente=D.cve_docente INNER JOIN cat_entidades_federativas E ON RP.cve_cat_entidad_federativa=E.cve_entidad INNER JOIN cat_tipos_sostenimientos S ON RP.cve_cat_tipo_sostenimiento=S.cve_tipo_sostenimiento INNER JOIN ctrl_cat_modalidades_servicios_educativos SE ON RP.cve_cat_servicio_educativo=SE.cve_cat_servicio_educativo AND RP.cve_cat_modalidad=SE.cve_cat_modalidad AND SE.cve_cat_sistema_informacion=? AND SE.fecha_inicio_vigencia<=CURRENT_DATE AND (SE.fecha_fin_vigencia IS NULL OR SE.fecha_fin_vigencia>=CURRENT_DATE) INNER JOIN cat_cct C ON RP.cve_cat_cct=C.cv_cct WHERE RP.cve_cat_nivel_educativo=? AND RP.cve_docente=?;";
    public static final String SQL_DEL_REGISTRO_PARTICIPACION="DELETE FROM registro_reconocimiento_proactividad_participacion WHERE cve_cat_nivel_educativo=? AND cve_docente=? AND cve_cat_entidad_federativa=? AND anio_aplicacion=?;";
    public static final String SQL_BIT_REGISTRO_PARTICIPACION="INSERT INTO bitacora_abc_sistemas(nombre_tabla, cve_sistema_informacion, cve_tipo_movimiento, cve_docente_modificada, cve_docente_quien_modifica, atributos) SELECT 'registro_reconocimiento_proactividad_participacion', ?, ?, ?, ?, JSON_OBJECT('rid', rid, 'cve_cat_nivel_educativo', cve_cat_nivel_educativo, 'cve_docente', cve_docente, 'cve_cat_entidad_federativa', cve_cat_entidad_federativa, 'anio_aplicacion', anio_aplicacion, 'cve_cat_tipo_sostenimiento', cve_cat_tipo_sostenimiento, 'cve_cat_servicio_educativo', cve_cat_servicio_educativo, 'cve_cat_modalidad', cve_cat_modalidad, 'cve_cat_cct', cve_cat_cct, 'huella', huella, 'fecha_registro', fecha_registro, 'fecha_creacion', fecha_creacion, 'fecha_ultima_actualizacion', fecha_ultima_actualizacion) json FROM registro_reconocimiento_proactividad_participacion WHERE cve_cat_nivel_educativo=? AND cve_docente=? AND cve_cat_entidad_federativa=? AND anio_aplicacion=?;";
    
    public static final String SQL_INS_REGISTRO_TRABAJO="INSERT INTO registro_reconocimiento_proactividad_trabajo(cve_cat_nivel_educativo, cve_docente, cve_cat_entidad_federativa, anio_aplicacion, detalle_trabajo) VALUES(?, ?, ?, ?, ?);";
    public static final String SQL_UPD_REGISTRO_TRABAJO="UPDATE registro_reconocimiento_proactividad_trabajo SET detalle_trabajo=? WHERE cve_cat_nivel_educativo=? AND cve_docente=? AND cve_cat_entidad_federativa=? AND anio_aplicacion=?;";
    public static final String SQL_SEL_REGISTRO_TRABAJO="SELECT detalle_trabajo FROM registro_reconocimiento_proactividad_trabajo WHERE cve_cat_nivel_educativo=? AND cve_docente=? AND cve_cat_entidad_federativa=? AND anio_aplicacion=?;";
    public static final String SQL_DEL_REGISTRO_TRABAJOS="DELETE FROM registro_reconocimiento_proactividad_trabajo WHERE cve_cat_nivel_educativo=? AND cve_docente=? AND cve_cat_entidad_federativa=? AND anio_aplicacion=?;";
    
    public static final String SQL_INS_REGISTRO_DOCUMENTO="INSERT INTO registro_reconocimiento_proactividad_documentos(cve_cat_nivel_educativo, cve_docente, cve_cat_entidad_federativa, anio_aplicacion, nombre_archivo, nombre_interno) VALUES(?, ?, ?, ?, ?, ?);";
    public static final String SQL_UPD_REGISTRO_DOCUMENTO="UPDATE registro_reconocimiento_proactividad_documentos SET nombre_archivo=?, nombre_interno=? WHERE cve_cat_nivel_educativo=? AND cve_docente=? AND cve_cat_entidad_federativa=? AND anio_aplicacion=?;";
    public static final String SQL_SEL_REGISTRO_DOCUMENTO="SELECT nombre_archivo, nombre_interno FROM registro_reconocimiento_proactividad_documentos WHERE cve_cat_nivel_educativo=? AND cve_docente=? AND cve_cat_entidad_federativa=? AND anio_aplicacion=?;";
    public static final String SQL_DEL_REGISTRO_DOCUMENTOS="DELETE FROM registro_reconocimiento_proactividad_documentos WHERE cve_cat_nivel_educativo=? AND cve_docente=? AND cve_cat_entidad_federativa=? AND anio_aplicacion=?;";
    // --- Registro ---
}



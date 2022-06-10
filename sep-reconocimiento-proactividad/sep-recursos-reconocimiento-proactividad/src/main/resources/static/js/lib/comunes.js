/* 
 * Libreria con funciones comunes para todos los modulos
 */
constantesComunes=function(){
    const API_URL='./api/';
    
    return{
        //constantes
        COD_OK: 0
        //rutas de api
    };
};




quitTab=function(cmd) {
    if(cmd ==='quit') {
        open(location, '_self').close();
    }
    return false;
};

getBrowserName=function(){
    let userAgent=navigator.userAgent;
    console.log("Agent: "+userAgent);
    
    if(userAgent.match(/edg/i)){
        return "edge";
    }
    else if(userAgent.match(/trident/i)){
        return "iexplorer";
    }
    else if(userAgent.match(/opr\//i)){
        return "opera";
    }
    else if(userAgent.match(/chrome|chronium|crios/i)){
        return "chrome";
    }
    else if(userAgent.match(/safari/i)){
        return "safari";
    }
    else if(userAgent.match(/firefox|fxios/i)){
        return "firefox";
    }
    else{
        return "unknow";
    }
};

validaBrowser=function(){
    let soportados=['chrome', 'firefox'];
    let soportado=false;

    let navegador=getBrowserName();
    console.log("Navegador detectado: "+navegador);
    
    soportados.forEach((item)=>{
        if(item===navegador){
            soportado=true;          
        }
    });
    
    return {
        soportado: soportado,
        browser: navegador
    };
};

getDescripcionElemento=function(arreglo, cve){
    let desc=null;

    if(arreglo!==null && arreglo!==undefined){
        arreglo.map((elem)=>{
            if(""+elem.cve===""+cve){
                desc=elem.descripcion;
            }
        });
    }

    return desc;
};

b64toBlob=function(b64Data, contentType='', sliceSize=512){
    const byteCharacters = atob(b64Data);
    const byteNumbers = new Array(byteCharacters.length);
    for (let i = 0; i < byteCharacters.length; i++) {
      byteNumbers[i] = byteCharacters.charCodeAt(i);
    }
    const byteArray = new Uint8Array(byteNumbers);
    const blob = new Blob([byteArray], {type: contentType});
    
    return blob;
};



/********************
 Para el manejo de errores
 ********************/
const COD_EDO_SIN_ESTADO=0;
const COD_EDO_REGISTRADO=2;
const COD_EDO_CERRADO=9;
const COD_EDO_RECHAZADO=3;





/********************
 Para el manejo de errores
 ********************/
const COD_OK=0;
const COD_ERROR=1;


const getMsgStatus = function (status) {
    return apiError[status] ? apiError[status] : mensajesGenericos.intentar_nuevamente;
};


const mensajesGenericos = {
    token: "Token proporcionado expiró o es incorrecto",
    sintaxis: "El servidor no pudo entender la solicitud debido a una sintaxis no válida.",
    api: "API no encontrado",
    error_parametros: "Los parámetros enviados no son los correctos",
    timeout: "El tiempo de espera expiró",
    intentar_nuevamente: "Se ha producido un error, vuelva a intentarlo más tarde",
    formato_multimedia_incorrecto: "El formato multimedia  no es compatible",
    error_inseperado: "Error al procesar la solicitud por favor intentelo más tarde"
};

const apiError = {
    400: mensajesGenericos.error_parametros,
    401: mensajesGenericos.token,
    403: mensajesGenericos.token,
    404: mensajesGenericos.api,
    405: mensajesGenericos.api,
    406: mensajesGenericos.error_parametros,
    407: mensajesGenericos.token,
    408: mensajesGenericos.timeout,
    409: mensajesGenericos.token,
    410: mensajesGenericos.intentar_nuevamente,
    411: mensajesGenericos.intentar_nuevamente,
    412: mensajesGenericos.token,
    415: mensajesGenericos.formato_multimedia_incorrecto,
    500: mensajesGenericos.error_inseperado,
    501: mensajesGenericos.error_inseperado,
    504: mensajesGenericos.timeout
};

constantesUtils=function(){
    const API_URL='../api/';
    
    return{
        //constantes
        COD_OK: 0,
        COD_ERROR: 1,
        //rutas de api
        API_STATUS: API_URL+'valida-estatus-docente/',        
    };
};

/**
 * Metodo que valida el estado del docente para sacar a la persona de la mesa en caso qeu el docente que esta editando ya este aceptado o rechazado
 * @param {type} objetoHttp
 * @param {type} scope
 * @param {type} responseError
 * @param {type} callback
 */
const validaEstatusDocente = function (objetoHttp, scope, responseError, data, callback) {
    let url=constantesUtils().API_STATUS;
    
    if(data.raiz===true){
        url=url.substring(1);
    }    
    
    objetoHttp.get(url + `${data.cveDocente}/${data.cveRegistroConvocatoria}`)
            .then(function (response) {
                if (response.data.code === constantesUtils().COD_OK) {
                    if (response.data.response.validacion == 0) {
                        window.location = response.data.response.url
                    } else if (typeof callback === "function") {
                        console.log("callback")
                        callback();
                    }
                }
                if (response.data.code === constantesUtils().COD_ERROR && typeof scope.messageAPI.showMsg === "function") {
                    scope.messageAPI.showMsg(response.data.code, response.data.msg);
                }
            }, responseError);
};

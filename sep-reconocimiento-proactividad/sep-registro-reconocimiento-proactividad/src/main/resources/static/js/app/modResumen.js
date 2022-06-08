angular.module('modResumen', ['ngSanitize', 'ngRoute'])
    .constant('RUTAS', {
        URL_REGISTRO: './registro',
        URL_FACTORES: './factores',
        URL_DATOS_PERSONALES: './datos-personales'
    })
    .constant('API',
        (function (){
            const API_URL='./api/';
            return {
                //catalogos
                //datos de operacion
                API_GET_DOCENTE: API_URL + 'docentes/get-curp/',
                API_REQUISITOS: API_URL + 'requisitos/get',
                API_FACTORES: API_URL + 'factores/get',
                API_GET_PARTICIPACION: API_URL + 'participaciones/get/',
                API_GET_REQ_RESPUESTAS: API_URL + 'requisitos/get-respuestas/',
                API_GET_FAC_RESPUESTAS: API_URL + 'factores/get-respuestas/',
                API_GET_CURSOS_SELECCIONADOS: API_URL + 'factores-cursos/get-seleccionados/',
                API_PREREGISTRO: API_URL + 'registro/add-pre/',
                API_GET_ANIOS_PARTICIPACION: API_URL + 'anios-participacion/get/',
                
                API_FICHA_REGISTRO: './documentos/ficha-registro/get/'
            };
        })()
    )
    .run(function ($rootScope, $sce, $http){
        $http.defaults.headers.common.Authorization=sessionStorage.getItem('token');

        //Modal de alerta
        $rootScope.confirmationAlert=function (msg1, msg2, acceptFunction, cancelFunction) {
            $rootScope.loading=false;
            msg1 || (msg1="");
            msg2 || (msg2="");
            $rootScope.modal={
                visible: true,
                modalMsg1: msg1,
                modalMsg2: $sce.trustAsHtml(msg2),
                buttonMsgOk: "Continuar",
                buttonMsgCancel: "Cancelar",
                clase: "animated bounceInLeft",
                funcionModalAceptar: acceptFunction,
                funcionModalCancelar: cancelFunction
            };
        };

        //metodo de default para cerrar modales
        $rootScope.closeAnimatedModal=function () {
            $rootScope.modal.clase="animated bounceOutRight";
            setTimeout(function () {
                $rootScope.modal.visible=false;
                $rootScope.$apply();
            }, 600);

        };
    })
    .controller('ctrlResumen', ['$rootScope', '$scope', '$sce', '$rootScope', 'API', 'RUTAS', '$http', function ($rootScope, $scope, $sce, $rootScope, API, RUTAS, $http){
        // --- constantes ---
        const MODULO_REGISTRO='RG';
        
        // --- variables ---
        //datos de formulario
        $scope.data={};
        $scope.data.datosDocente={};
        $scope.data.datosParticipacion={};
        //datos de sesion
        $scope.mainData={};
        $scope.cargando=false;
        $scope.data.errores=[];

        $scope.messageAPI={
            show: false,
            msg: '',
            isSuccess: false,
            isError: false,

            clean: function (){
                this.show=false;
                this.isSuccess=false;
                this.isError=false;
                this.msg='';
            },

            showMsg: function (code, msg){
                $scope.cargando=true;                
                
                if (code===COD_OK){
                    this.isSuccess=true;
                } else {
                    this.isError=true;
                }
                this.show=true;
                this.msg=msg;
            }
        };
        
        $scope.container={
            showLoading: true,
            showContinuar: false
        };    

        $scope.avanceFormulario={
            datosPersonales: true,
            valoracion: true,
            requisitos: true,
            factores: true,
            resumen: false,
            registrado: false
        };

        //Catálogos
        $scope.requisitos=[];
        $scope.factores=[];
        $scope.respuestasRequisitos=[];
        $scope.respuestasFactores=[];
        $scope.cursosSeleccionados=[];
        $scope.aniosParticipacion=[];


        // --- funciones ---

        // Funciones genericas
        let responseError=function(reason){
            $scope.cargando=false;
            $scope.messageAPI.showMsg(COD_ERROR, getMsgStatus(reason.status));
            console.log("Error localizado en la API: "+reason.status);
        };

        $scope.initValues=function (){
            $scope.mainData.cveUsuario=sessionStorage.getItem('cveUsuario');
            $scope.mainData.cveDocente=sessionStorage.getItem('cveDocente');
            $scope.mainData.curpDocente=sessionStorage.getItem('curpDocente');
            $scope.mainData.cveEntidad=sessionStorage.getItem('cveEntidad');
            $scope.mainData.cveMunicipio=sessionStorage.getItem('cveMunicipio');
            $scope.mainData.cveValoracion=sessionStorage.getItem('cveValoracion');
            $scope.mainData.nombreEntidad=sessionStorage.getItem('nombreEntidad');
            console.log("Entidad: "+$scope.mainData.nombreEntidad);
            
            $scope.getDatosDocente();
            $scope.getParticipacion();
            $scope.getRequisitos();
            $scope.getFactores();
            $scope.getRespuestasRequisitos();
            $scope.getRespuestasFactores();
            $scope.getCursosSeleccionados();
        };

        
        $scope.getDatosDocente=function(){
            $scope.messageAPI.clean();
            $scope.cargando=true;

            if($scope.mainData.curpDocente!==null && $scope.mainData.curpDocente!==""){
                $http.get(API.API_GET_DOCENTE+$scope.mainData.curpDocente).then(function(response){
//                        $scope.cargando=false;
                        $scope.container.showLoading=false;

                        if (response.data.code===COD_OK){
                            $scope.data.datosDocente=response.data.response;
                        }
                        else {
                            $scope.messageAPI.showMsg(response.data.code, response.data.msg);
                        }
                    }, responseError);
            }
        };

        $scope.getRequisitos=function(){
            $scope.cargando=true;

            $http.get(API.API_REQUISITOS+"?cveEntidad="+$scope.mainData.cveEntidad
                        +"&cveTipoValoracion="+$scope.mainData.cveValoracion).then(function(response){
//                    $scope.cargando=false;
                    $scope.container.showLoading=false;
                    
                    if (response.data.code===COD_OK){
                        $scope.requisitos=response.data.response;
                    } 
                    else {
                        $scope.requisitos=[];
                        $scope.messageAPI.showMsg(response.data.code, response.data.msg);
                    }
                }, responseError);
        };

        $scope.getFactores=function(){
            $scope.cargando=true;

            $http.get(API.API_FACTORES+"?cveEntidad="+$scope.mainData.cveEntidad).then(function(response){
//                    $scope.cargando=false;
                    $scope.container.showLoading=false;
                    
                    if (response.data.code===COD_OK){
                        $scope.factores=response.data.response;
                    } 
                    else {
                        $scope.factores=[];
                        $scope.messageAPI.showMsg(response.data.code, response.data.msg);
                    }
                }, responseError);
        };

        $scope.getParticipacion=function (){
            $scope.cargando=true;

            $http.get(API.API_GET_PARTICIPACION+$scope.mainData.cveDocente).then(function (response){                
                if (response.data.code===COD_OK){
                    if(response.data.response!==null){
                        $scope.data.datosParticipacion=response.data.response;
                        $scope.getAniosParticipacionParticipante();
                    }
                } 
                else {
                    $scope.messageAPI.showMsg(response.data.code, response.data.msg);
                }
            }, responseError);
        };

        $scope.getAniosParticipacionParticipante=function(){
            $scope.cargando=true;

            $http.get(API.API_GET_ANIOS_PARTICIPACION+$scope.mainData.cveDocente+"?cveEntidad="+$scope.mainData.cveEntidad).then(function (response){                
                $scope.cargando=false;
                if (response.data.code===COD_OK){
                    if(response.data.response!==null){
                        $scope.aniosParticipacion=response.data.response;
                    }
                } 
                else {
                    $scope.messageAPI.showMsg(response.data.code, response.data.msg);
                    $scope.aniosParticipacion=[];
                }
            }, responseError);
        };

        $scope.getRespuestasRequisitos=function(){
            $http.get(API.API_GET_REQ_RESPUESTAS+$scope.mainData.cveDocente+"?cveEntidad="+$scope.mainData.cveEntidad).then(function(response){
                    $scope.container.showLoading=false;
                    
                    if (response.data.code===COD_OK){
                        if(response.data.response!==null){
                            $scope.respuestasRequisitos=response.data.response;
                        }
                    } 
                    else {
                        $scope.messageAPI.showMsg(response.data.code, response.data.msg);
                    }
                }, responseError);
        };

        $scope.getRespuestasFactores=function(){
            $http.get(API.API_GET_FAC_RESPUESTAS+$scope.mainData.cveDocente+"?cveEntidad="+$scope.mainData.cveEntidad).then(function(response){
                    $scope.cargando=false;
                    $scope.container.showLoading=false;
                    
                    if (response.data.code===COD_OK){
                        if(response.data.response!==null){
                            $scope.respuestasFactores=response.data.response;
                        }
                    } 
                    else {
                        $scope.messageAPI.showMsg(response.data.code, response.data.msg);
                    }
                }, responseError);
        };

        $scope.getCursosSeleccionados=function(){
            $http.get(API.API_GET_CURSOS_SELECCIONADOS+$scope.mainData.cveDocente+"?cveEntidad="+$scope.mainData.cveEntidad
                    +"&cveValoracion="+$scope.mainData.cveValoracion).then(function(response){
                $scope.cargando=false;
                if (response.data.code===COD_OK){
                    $scope.cursosSeleccionados=response.data.response;
                } 
                else {
                    $scope.cursosSeleccionados=[];
                    $scope.messageAPI.showMsg(response.data.code, response.data.msg);
                }
            }, responseError);
        };


        $scope.validaDatosRegistro=function(){
            $scope.cargando=true;
            let request={
                cveDocente: $scope.mainData.cveDocente,
                curp: $scope.mainData.curpDocente,
                cveEntidad: $scope.mainData.cveEntidad,
                cveValoracion: $scope.mainData.cveValoracion
            };
            
            $scope.data.errores=[];
            validaRegla($scope, $http, responseError, $scope.mainData.cveUsuario, MODULO_REGISTRO, request, $scope.generarFicha);
        };


        // -------- operaciones con los datos operativos

        $scope.getRespuestaRequisito=function(req){
            for(let i=0; $scope.respuestasRequisitos!==null && i<$scope.respuestasRequisitos.length; i++){
                //console.log("R: "+req+", Res:"+$scope.respuestasRequisitos[i].cveRequisito);
                if(''+req===''+$scope.respuestasRequisitos[i].cveRequisito){
                    return $scope.respuestasRequisitos[i].respuesta;
                }
            }
            
            return null;
        };

        $scope.getRespuestaFactor=function(fac){
            for(let i=0; $scope.respuestasFactores!==null && i<$scope.respuestasFactores.length; i++){
                if(''+fac===''+$scope.respuestasFactores[i].cveRequisito){
                    return $scope.respuestasFactores[i].respuesta;
                }
            }
            
            return null;
        };
        
        $scope. generarFicha=function(){
            $scope.cargando=true;
            let request={
                cveDocente: $scope.mainData.cveDocente,
                cveEntidad: $scope.mainData.cveEntidad,
                cveValoracion: $scope.mainData.cveValoracion
            };
            
            $http.post(API.API_PREREGISTRO+$scope.mainData.cveUsuario, request).then(function(response){
                $scope.cargando=false;
                if (response.data.code === COD_OK){
                    $scope.container.showContinuar=true;
                    $scope.imprimirRegistro();
                } 
                else {
                    $scope.messageAPI.showMsg(response.data.code, response.data.msg);
                }
            }, responseError);
        };

        $scope.imprimirRegistro=function (){
            $scope.cargando=true;
            
            try{
                var xhr=new XMLHttpRequest();
                xhr.open("POST", API.API_FICHA_REGISTRO+$scope.mainData.curpDocente+"?entidad="+$scope.recuperaNombreLogo());
                xhr.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
                xhr.responseType="arraybuffer";
                xhr.onload=function () {
                    $scope.cargando=false;
                    if(this.status === 200) {
                        console.log(" encuentra Ficha: " + xhr.response);
                        var blob=new Blob([xhr.response], {type: "application/pdf"});   
                        console.log("tamaño: "+blob.size);    
                        var a=document.createElement('a');
                            a.href=URL.createObjectURL(blob);
                            a.download="fichaRegistroRTUT.pdf";
                            a.click();
                
                        $scope.$apply();
                    }
                    else{
                        $scope.messageAPI.showMsg(this.status, "No se logro generar la ficha");
                    }
                };
                xhr.send(JSON.stringify(
                    {
                    })
                );
            }
            catch(ex){
                console.log("Se presento un problema al generar la ficha: "+ex);
            }
        };

        $scope.formateaValor=function (valor) {
            if(valor!==null && valor!==""){
                return valor;
            }
            else{
                return $sce.trustAsHtml("<span class='informacion-sin-capturar'>Informaci&oacuten no proporcionada</span>");
            }
        };

        $scope.recuperaNombreLogo=function(){
            console.log("Municipio: "+$scope.mainData.cveMunicipio);
            
            if($scope.mainData.cveMunicipio!==null && $scope.mainData.cveMunicipio>0){
                return "Baja California (Tijuana)";
            }
            else{
                return $scope.mainData.nombreEntidad;
            }
        };

        $scope.continuar=function(){
            location.href=RUTAS.URL_REGISTRO;
        };

        $scope.regresaModuloAnterior=function(){
            location.href=RUTAS.URL_FACTORES;
        };
        
        $scope.regresaBusquedaDocente=function(){
            $rootScope.confirmationAlert("Aviso importante", 
                    "El proceso actual aún no ha concluido, posteriormente podrá trabajar nuevamente con el registro. No olvide finalizar el proceso en el futuro. ¿Desea continuar?", 
                    function(){
                        sessionStorage.removeItem('cveValoracion');
                        location.href=RUTAS.URL_DATOS_PERSONALES;
                    }, $rootScope.closeAnimatedModal);
        };
    }]);

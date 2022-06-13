angular.module('modResumen', ['ngSanitize', 'ngRoute'])
    .constant('RUTAS', {
        URL_PARTICIPACION: './participacion',
        URL_REGISTRO: './finalizado'
    })
    .constant('API',
        (function (){
            const API_URL='./api/';
            return {
                //catalogos
                API_GET_DOCENTE: API_URL + 'docentes/get-curp/',
                //datos de operacion
                API_GET_REGISTRO: API_URL + 'participaciones/get/',
                API_SET_ESTADO: API_URL + 'participacion/finish/',
                API_GET_DOCUMENTOS: API_URL + 'documentos/get/'
            };
        })()
    )
    .run(function ($rootScope, $sce, $http){
        $http.defaults.headers.common.Authorization=sessionStorage.getItem('token');

        $rootScope.disclaimerModal=function (modal, msg1, msg2, acceptFunction, cancelFunction){
            $rootScope.loading=false;
            msg1 || (msg1="");
            msg2 || (msg2="");

            modal.visible=true;
            modal.botonOkText="Finalizar registro";
            modal.modalMsgNote="*No olvide imprimir o guardar la contancia una vez finalizado el proceso.";
            modal.modalMsg1=msg1;
            modal.modalMsg2=$sce.trustAsHtml(msg2);
            modal.clase="animated bounceInLeft";
            modal.funcionModalAceptar=acceptFunction;
            modal.funcionModalCancelar=cancelFunction;                
        };

        $rootScope.closeDisclaimerModal=function (modal) {
            modal.clase="animated bounceOutRight";
            setTimeout(function () {
                modal.visible=false;
                $rootScope.$apply();
            }, 600);
        };
    })
    .controller('ctrlResumen', ['$rootScope', '$scope', '$sce', 'API', 'RUTAS', '$http', function ($rootScope, $scope, $sce, API, RUTAS, $http){
        // --- constantes ---
        const MODULO_REGISTRO='RG';
        
        // --- variables ---
        $rootScope.mDisclaimer={visible: false};
        $scope.mAcepto="";        
        //datos de formulario
        $scope.data={};
        $scope.data.datosDocente={};
        $scope.data.datosParticipacion={};
        $scope.data.documentos=null;
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
            participacion: true,
            resumen: false,
            registrado: false
        };

        //Catálogos


        // --- funciones ---

        // Funciones genericas
        let responseError=function(reason){
            $scope.cargando=false;
            $scope.messageAPI.showMsg(COD_ERROR, getMsgStatus(reason.status));
            console.log("Error localizado en la API: "+reason.status);
        };

        $scope.initValues=function (){
            $scope.mainData.token=sessionStorage.getItem('token');
            $scope.mainData.cveDocente=sessionStorage.getItem('cveDocente');
            $scope.mainData.curp=sessionStorage.getItem('curp');
            
            $scope.getDatosDocente();
        };

        
        $scope.getDatosDocente=function(){
            $scope.messageAPI.clean();
            $scope.cargando=true;

            if($scope.mainData.curp!==null && $scope.mainData.curp!==""){
                $http.get(API.API_GET_DOCENTE+$scope.mainData.curp).then(function(response){
                        $scope.cargando=false;
                        $scope.container.showLoading=false;

                        if (response.data.code===COD_OK){
                            $scope.data.datosDocente=response.data.response;
                            $scope.getDatosParticipacion();
                        }
                        else {
                            $scope.messageAPI.showMsg(response.data.code, response.data.msg);
                        }
                    }, responseError);
            }
        };
        
        $scope.getDatosParticipacion=function(){
            $scope.cargando=true;

            $http.get(API.API_GET_REGISTRO+$scope.mainData.cveDocente).then(function(response){
                    $scope.cargando=false;

                    if (response.data.code===COD_OK){
                        $scope.data.datosParticipacion=response.data.response;
                        $scope.getDocumentos();
                    }
                    else {
                        $scope.messageAPI.showMsg(response.data.code, response.data.msg);
                    }
                }, responseError);
        };
        
        $scope.getDocumentos=function(){
            $scope.cargando=true;

            $http.get(API.API_GET_DOCUMENTOS+$scope.mainData.cveDocente+"?cveEntidad="+$scope.data.datosParticipacion.cveEntidad
                        +"&anioParticipacion="+$scope.data.datosParticipacion.anioAplicacion).then(function(response){
                    $scope.cargando=false;

                    if (response.data.code===COD_OK){
                        $scope.data.documentos=response.data.response;
                        $scope.container.showContinuar=true;
                    }
                    else {
                        $scope.messageAPI.showMsg(response.data.code, response.data.msg);
                    }
                }, responseError);
        };
        
        $scope.finalizaRegistro=function(){
            $scope.cargando=true;

            $http.post(API.API_SET_ESTADO+$scope.mainData.cveDocente).then(function(response){
                    $scope.cargando=false;

                    if (response.data.code===COD_OK){
                        location.href=RUTAS.URL_REGISTRO;
                    }
                    else {
                        $scope.messageAPI.showMsg(response.data.code, response.data.msg);
                    }
                }, responseError);
        };


        $scope.validaDatos=function(){
            $scope.cargando=true;
            let request={
                cveDocente: $scope.mainData.cveDocente,
                curp: $scope.mainData.curp,
                cveEntidad: $scope.data.datosParticipacion.cveEntidad,
                anioAplicacion: $scope.data.datosParticipacion.anioAplicacion
            };
            
            $scope.data.errores=[];
            validaRegla($scope, $http, responseError, $scope.mainData.token, $scope.mainData.cveDocente, MODULO_REGISTRO, 
                    request, $scope.finalizar);
        };
        
        $scope.finalizar=function(){
            $scope.cargando=false;
            var accept=function () {
                $scope.cargando=true;
                
                $scope.finalizaRegistro();
                $rootScope.closeDisclaimerModal($rootScope.mDisclaimer);
            };

            var cancel=function () {
                $rootScope.closeDisclaimerModal($rootScope.mDisclaimer);
            };

            $scope.messageAPI.clean();
            $rootScope.disclaimerModal($rootScope.mDisclaimer,
                    "Esta a punto de finalizar su registro.",
                    "Si está de acuerdo, escriba la palabra ACEPTO",
                    accept,
                    cancel);
            
        };
        
        $scope.muestraArchivo=function(){
            var blob=b64toBlob($scope.data.documentos.contenidoBase64, "application/pdf");
            
            var a=document.createElement('a');
            a.href=URL.createObjectURL(blob);
            a.download=$scope.data.documentos.nombreOriginal;
            a.click();

            $scope.$apply();
        };


        // -------- operaciones con los datos operativos

        $scope.formateaValor=function (valor) {
            if(valor!==null && valor!==""){
                return valor;
            }
            else{
                return $sce.trustAsHtml("<span class='informacion-sin-capturar'>Informaci&oacuten no proporcionada</span>");
            }
        };

        $scope.regresaModuloAnterior=function(){
            location.href=RUTAS.URL_PARTICIPACION;
        };
    }]);

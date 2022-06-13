angular.module('homeModule', ['ngSanitize', 'ngRoute'])
    .constant('RUTAS', {
        URL_PARTICIPACION: './participacion',
        URL_RESUMEN: './resumen',
        URL_CERRADO: './finalizado'
    })
    .constant('API',
        (function () {
            const API_URL='./api/';
            return {
                //datos de catalogos
                API_GET_DOCENTE: API_URL + 'docentes/get-curp/',
                //datos de operacion
                API_GET_REGISTRO: API_URL + 'participaciones/get/'
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
                buttonMsgOk: 'Cargar información',
                buttonMsgCancel: 'Cerrar la aplicación',
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
    .controller('homeCtrl', ['$rootScope', '$scope', 'RUTAS', 'API', '$http', '$window',function ($rootScope, $scope, RUTAS, API, $http, $window) {
        //constantes
        const MODULO_DATOS_PERSONALES='DP';

        //variables
        //datos de formulario
        $scope.data={};
        $scope.data.datosParticipacion={};
        $scope.supportBrowser=validaBrowser();
        //datos de sesion
        $scope.mainData={};
        $scope.cargando=false;
        $scope.data.errores=[];

        $scope.messageAPI={
            show: false,
            msg: '',
            isSuccess: false,
            isError: false,

            clean: function(){
                this.show=false;
                this.isSuccess=false;
                this.isError=false;
                this.msg='';
            },

            showMsg: function(code, msg){
                if(code===COD_OK){
                    this.isSuccess=true;
                } 
                else{
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
            datosPersonales: false,
            participacion: false,
            resumen: false,
            registrado: false
        };


        //funciones
        let responseError = function (reason) {
            $scope.cargando = false;
            $scope.messageAPI.showMsg(COD_ERROR, getMsgStatus(reason.status));
            console.log(COD_ERROR+" - "+getMsgStatus(reason.status));
        };

        $scope.initValues=function (curp, cveDocente, token, tokenOriginal, url) {
            sessionStorage.removeItem('cveEntidad');
            sessionStorage.removeItem('cveAnioAplicacion');

            console.log("URL: "+url);
            $scope.mainData.urlDatosPersonales=url;
            if(curp!==null && curp!=='' && curp!=='null' && curp!==undefined){
                $scope.mainData.curp=curp;
                sessionStorage.setItem('curp', curp);
            }
            if(cveDocente!==null && cveDocente!=='' && cveDocente!=='null' && cveDocente!==undefined){
                $scope.mainData.cveDocente=cveDocente;
                sessionStorage.setItem('cveDocente', cveDocente);
            }
            else if(sessionStorage.getItem('cveDocente')!==null){
                $scope.mainData.cveDocente=sessionStorage.getItem('cveDocente');
            }
            if(token!==null && token!=='' && token!=='null' && token!==undefined){
                $scope.mainData.token=token;
                sessionStorage.setItem('token', token);
            }
            if(tokenOriginal!==null && tokenOriginal!=='' && tokenOriginal!=='null' && tokenOriginal!==undefined){
                $scope.mainData.tokenOriginal=tokenOriginal;
                sessionStorage.setItem('tokenOriginal', tokenOriginal);
            }
            
            $scope.getDatosDocente();
            console.log("cargando: "+$scope.cargando+", cveDocente:"+cveDocente+", curp:"+curp);
        };

        
        $scope.getDatosDocente=function(){
            $scope.messageAPI.clean();
            $scope.data.errores=[];
            $scope.cargando=true;
            
            if($scope.mainData.curp!==null && $scope.mainData.curp!==""){
                $http.get(API.API_GET_DOCENTE+$scope.mainData.curp).then(function(response){
                        $scope.cargando=false;

                        if (response.data.code===COD_OK){
                            if(response.data.response!==null){
                                $scope.data.datosDocente=response.data.response;
                                $scope.getDatosParticipacion();
                            }
                            else{
                                $scope.messageAPI.showMsg(COD_ERROR, "No se tienen datos del participante");
                                $scope.container.showContinuar=false;
                            }
                        }
                        else {
                            $scope.messageAPI.showMsg(response.data.code, response.data.msg);
                            $scope.container.showContinuar=false;
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
                        $scope.validaDatos();
                    }
                    else {
                        $scope.messageAPI.showMsg(response.data.code, response.data.msg);
                    }
                }, responseError);
        };
            
        $scope.validaDatos=function(){
            $scope.cargando=true;
            let request={
                cve_docente: $scope.data.datosDocente.cveDocente,
                curp: $scope.data.datosDocente.curp,
                nombre: $scope.data.datosDocente.nombre,
                telefono1: $scope.data.datosDocente.telefono1,
                correo1: $scope.data.datosDocente.correo1,
                entidad: $scope.data.cveEntidad
            };
            
            $scope.data.errores=[];
            validaRegla($scope, $http, responseError, $scope.mainData.token, $scope.mainData.cveDocente, MODULO_DATOS_PERSONALES, 
                    request, $scope.revisaErrores);
        };


        $scope.revisaErrores=function(){
            $scope.cargando=false;
            
            if($scope.data.errores && $scope.data.errores.length===0){
                $scope.container.showContinuar=true;
            }
        };
        
        $scope.abreDatosPersonales=function(){
            $window.open($scope.mainData.urlDatosPersonales+$scope.mainData.curp+"&token="+$scope.mainData.tokenOriginal, "datospersonales");
        };
        
        $scope.getNombreCompleto=function(docente){
            return docente.nombre+" "+docente.primerApellido+" "+docente.segundoApellido;
        };
        
        $scope.continuar=function(){
            if($scope.data.datosParticipacion && ($scope.data.datosParticipacion.estatus==='2' || $scope.data.datosParticipacion.estatus===2)){
                $rootScope.confirmationAlert("Ya tiene un proceso iniciado.", 
                    "Recuerde que debe concluir su proceso para obetener su comprobante. ¿Desea carga la participación existente?", function(){
                        location.href=RUTAS.URL_RESUMEN;
                    },
                    function(){
                        quitTab('quit');
                    }
                );
            }
            else if($scope.data.datosParticipacion && !($scope.data.datosParticipacion.estatus==='2' || $scope.data.datosParticipacion.estatus===2)){
                $rootScope.confirmationAlert("Ya tiene un proceso finalizado.", 
                    "¿Desea carga la participación finalizada?", function(){
                        location.href=RUTAS.URL_CERRADO;
                    },
                    function(){
                        quitTab('quit');
                    }
                );
            }
            else{
                location.href=RUTAS.URL_PARTICIPACION;
            }
        };
    }]);

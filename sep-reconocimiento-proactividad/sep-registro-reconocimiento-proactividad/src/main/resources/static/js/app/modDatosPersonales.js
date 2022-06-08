angular.module('modDatosPersonales', ['ngSanitize', 'ngRoute'])
    .constant('RUTAS', {
        URL_VALORACION: './participacion',
        URL_CIERRE: './registrado'
    })
    .constant('API',
        (function () {
            const API_URL='./api/';
            return {
                //datos de catalogos
                //datos de operacion
                API_GET_PENDIENTES: API_URL + 'pendientes-cierre/',
                API_GET_DOCENTE: API_URL + 'docentes/get-curp/',
                API_GET_PARTICIPACION: API_URL + 'participaciones/get/'
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
                buttonMsgCancel: 'Buscar otro CURP',
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
    .controller('ctrlDatosPersonales', ['$rootScope', '$scope', 'API', 'RUTAS', '$http',function ($rootScope, $scope, API, RUTAS, $http) {
        //constantes
        const MODULO_DATOS_PERSONALES='DP';

        //variables
        //datos de formulario
        $scope.data={};
        $scope.data.curp=null;
        $scope.data.datosDocente={};
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
            showContinuar: false,
            showPendientes: false
        };    

        $scope.avanceFormulario={
            datosPersonales: false,
            valoracion: false,
            requisitos: false,
            factores: false,
            resumen: false,
            registrado: false
        };
        
        $scope.pendientes=[];


        //funciones
        let responseError = function (reason) {
            $scope.cargando = false;
            $scope.messageAPI.showMsg(COD_ERROR, getMsgStatus(reason.status));
            console.log(COD_ERROR+" - "+getMsgStatus(reason.status));
        };

        $scope.initValues=function () {
            $scope.mainData.cveUsuario=sessionStorage.getItem('cveUsuario');
            $scope.mainData.cveEntidad=sessionStorage.getItem('cveEntidad');
            $scope.mainData.cveMunicipio=sessionStorage.getItem('cveMunicipio');

            $scope.getPendientes();
        };



        $scope.getPendientes=function(){
            $scope.cargando=true;
            $http.get(API.API_GET_PENDIENTES+$scope.mainData.cveUsuario).then(function(response){
                $scope.container.showLoading=false;
                $scope.cargando=false;
                if (response.data.code===COD_OK){
                    $scope.pendientes=response.data.response;
                } 
                else {
                    $scope.pendientes=[];
                    $scope.messageAPI.showMsg(response.data.code, response.data.msg);
                }
            }, responseError);
        };

        $scope.getParticipacion=function(){
            $scope.cargando=true;

            $http.get(API.API_GET_PARTICIPACION+$scope.data.datosDocente.cveDocente).then(function (response){
                $scope.cargando=false;
                
                if (response.data.code===COD_OK){
                    if(response.data.response!==null){
                        console.log("Se tiene una participacion");
                        //reviso si aun no esta finalizado el proceso
                        if( response.data.response.cveEstado===COD_EDO_SIN_ESTADO || response.data.response.cveEstado===COD_EDO_REGISTRADO ){
                            $rootScope.confirmationAlert("La CURP proporcionada ya tiene un proceso iniciado. ", 
                                "Se tiene una participación en '"+response.data.response.entidad
                                        +(response.data.response.municipio!==null && response.data.response.municipio!==undefined && response.data.response.municipio!==''? "/"+response.data.response.municipio: "")
                                        +"'. ¿Desea continuar con el proceso existente?", function(){
                                    location.href=RUTAS.URL_VALORACION;
                                }, $scope.nuevaBusqueda);        
                        }
                        else{
                            $rootScope.confirmationAlert("La CURP proporcionada tiene su proceso finalizado. ", "El registro de la CURP está concluido", 
                            function(){ $scope.cargaParticipacionExistente(response.data.response); }, $scope.nuevaBusqueda);
                            console.log("La participacion esta concluida");
                        }
                    }
                    else{
                        console.log("No tiene una participacion");
                        location.href=RUTAS.URL_VALORACION;
                    }
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
            validaRegla($scope, $http, responseError, $scope.mainData.cveUsuario, MODULO_DATOS_PERSONALES, request, $scope.getParticipacion);
        };

        // -------- operaciones con los datos operativos
        
        $scope.cargaParticipacionExistente=function(data){
            sessionStorage.setItem('cveValoracion', data.cveValoracion);
            
            location.href=RUTAS.URL_CIERRE;
        };
        
        $scope.nuevaBusqueda=function(){
            $scope.data.curp=null;
            $scope.data.datosDocente={};
            $rootScope.closeAnimatedModal();
        };
        
        $scope.buscaDocente=function(){
            let curp=document.getElementById("curp");
            $scope.messageAPI.clean();
            $scope.data.errores=[];
            $scope.cargando=true;
            
            curp.value=curp.value.toUpperCase();

            if($scope.data.curp!==null && $scope.data.curp!==""){
                $http.get(API.API_GET_DOCENTE+$scope.data.curp).then(function(response){
                        $scope.cargando=false;

                        if (response.data.code===COD_OK){
                            if(response.data.response!==null){
                                $scope.data.datosDocente=response.data.response;
                                $scope.data.datosDocente.cveConsideracion=""+response.data.response.cveConsideracion;
                                $scope.container.showContinuar=true;
                                sessionStorage.setItem('cveDocente', $scope.data.datosDocente.cveDocente);
                                sessionStorage.setItem('curpDocente', $scope.data.datosDocente.curp);
                            }
                            else{
                                $scope.messageAPI.showMsg(COD_ERROR, "La maestra o maestro no existe");
                                $scope.container.showContinuar=false;
                            }
                        }
                        else {
                            $scope.messageAPI.showMsg(response.data.code, response.data.msg);
                            $scope.container.showContinuar=false;
                        }
                    }, responseError);
            }
            else{
                $scope.cargando=false;
                $scope.messageAPI.showMsg(COD_ERROR, "Es necesario se proporcione un CURP");
            }
        };
        
        $scope.tecleaRFC=function(keyEvent){
            let curp=document.getElementById("curp");
            
            if(keyEvent.which===13){
                curp.value=curp.value.toUpperCase();
                $scope.buscaDocente();
            }
        };
        
        /*$scope.continuar=function(){
            location.href="participacion";
        };*/
            
        $scope.cargaPendiente=function(cveDocente, curp){
            console.log("carga datos de "+cveDocente);
            $scope.data.datosDocente.cveDocente=cveDocente;
            $scope.ocultaPendientes();
            sessionStorage.setItem('cveDocente', cveDocente);
            sessionStorage.setItem('curpDocente', curp);
            $scope.getParticipacion();
        };
        
        $scope.muestraPendientes=function(){
            $scope.container.showPendientes=true;
        };
        
        $scope.ocultaPendientes=function(){
            $scope.container.showPendientes=false;
        };
    }]);

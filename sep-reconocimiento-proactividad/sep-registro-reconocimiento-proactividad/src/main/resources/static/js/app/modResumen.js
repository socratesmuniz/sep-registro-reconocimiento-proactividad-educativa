angular.module('modResumen', ['ngSanitize', 'ngRoute'])
    .constant('RUTAS', {
        URL_PARTICIPACION: './participacion',
        URL_REGISTRO: './registro'
    })
    .constant('API',
        (function (){
            const API_URL='./api/';
            return {
                //catalogos
                API_GET_DOCENTE: API_URL + 'docentes/get-curp/'
                //datos de operacion
            };
        })()
    )
    .run(function ($rootScope, $sce, $http){
        $http.defaults.headers.common.Authorization=sessionStorage.getItem('token');
    })
    .controller('ctrlResumen', ['$rootScope', '$scope', '$sce', '$rootScope', 'API', 'RUTAS', '$http', function ($rootScope, $scope, $sce, $rootScope, API, RUTAS, $http){
        // --- constantes ---
        const MODULO_REGISTRO='RG';
        
        // --- variables ---
        //datos de formulario
        $scope.data={};
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
            $scope.mainData.cveDocente=sessionStorage.getItem('cveDocente');
            $scope.mainData.curp=sessionStorage.getItem('curp');
            $scope.mainData.cveEntidad=sessionStorage.getItem('cveEntidad');
            $scope.mainData.nombreEntidad=sessionStorage.getItem('nombreEntidad');
            $scope.mainData.cveAnioAplicacion=sessionStorage.getItem('cveAnioAplicacion');
            $scope.mainData.nombreTrabajo=sessionStorage.getItem('nombreTrabajo');
            $scope.mainData.cveSostenimiento=sessionStorage.getItem('cveSostenimiento');
            $scope.mainData.nombreSostenimiento=sessionStorage.getItem('nombreSostenimiento');
            $scope.mainData.cveServicio=sessionStorage.getItem('cveServicio');
            $scope.mainData.cveModalidad=sessionStorage.getItem('cveModalidad');
            $scope.mainData.nombreServicio=sessionStorage.getItem('nombreServicio');
            $scope.mainData.cveCct=sessionStorage.getItem('cveCct');
            $scope.mainData.nombreCct=sessionStorage.getItem('nombreCct');
            console.log("Entidad: "+$scope.mainData.nombreEntidad);
            
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
                            $scope.container.showContinuar=true;
                        }
                        else {
                            $scope.messageAPI.showMsg(response.data.code, response.data.msg);
                        }
                    }, responseError);
            }
        };


        $scope.validaDatos=function(){
            $scope.cargando=true;
            let request={
                cveDocente: $scope.mainData.cveDocente,
                curp: $scope.mainData.curp,
                cveEntidad: $scope.mainData.cveEntidad
            };
            
            $scope.data.errores=[];
            validaRegla($scope, $http, responseError, $scope.mainData.cveDocente, MODULO_REGISTRO, request, $scope.guardaDatos);
        };
        
        $scope.guardaDatos=function(){
            
//            location.href=RUTAS.URL_REGISTRO;
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

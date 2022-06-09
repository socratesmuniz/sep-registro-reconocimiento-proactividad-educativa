angular.module('modParticipacion', ['ngSanitize', 'ngRoute'])
    .constant('RUTAS', {
        URL_RESUMEN: './resumen'
    })
    .constant('API',
        (function (){
            const API_URL='./api/';
            return {
                //catalogos
                API_ENTIDADES: API_URL + 'entidades/get',
                API_SOSTENIMIENTOS: API_URL + 'sostenimientos-entidad/get/',
                API_SERVICIOS: API_URL + 'servicios/get',
                API_CCT: API_URL + 'cct/get/'
                //datos de operacion
            };
        })()
    )
    .run(function ($rootScope, $sce, $http){
        $http.defaults.headers.common.Authorization=sessionStorage.getItem('token');
    })
    .controller('ctrlParticipacion', ['$scope', 'API', 'RUTAS', '$http', function ($scope, API, RUTAS, $http){
        // --- constantes ---
        const MODULO_PARTICIPACION='PAR';
        
        // --- variables ---
        //datos de formulario
        $scope.data={};
        //datos de sesion
        $scope.mainData={};
        $scope.cargando=false;
        //datos de seleccion
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
            participacion: false,
            resumen: false,
            registrado: false
        };
        
        //Catálogos
        $scope.entidades=[];
        $scope.sostenimientos=[];
        $scope.anios=[];
        $scope.servicios=[];


        // --- funciones ---

        // Funciones genericas
        let responseError=function(reason){
            $scope.cargando=false;
            $scope.messageAPI.showMsg(COD_ERROR, getMsgStatus(reason.status));
            console.log("Error localizado en la API: "+reason.status);
        };

        $scope.initValues=function (){
            $scope.mainData.cveDocente=sessionStorage.getItem('cveDocente');
            
            $scope.getAniosAplicacion();
            $scope.getEntidades();
            $scope.getServicios();
            $scope.cargaDatosExistentes();
        };


        $scope.getAniosAplicacion=function (){
            for(let i=2020; i<=2022; i++){
                $scope.anios.push({cve: ''+i, descripcion: ''+i});
            }
        };

        $scope.getEntidades=function (){
            $scope.cargando=true;
            $http.get(API.API_ENTIDADES).then(function (response){
                $scope.container.showLoading=false;
                $scope.cargando=false;
                
                if (response.data.code===COD_OK){
                    $scope.entidades=response.data.response;
                } 
                else {
                    $scope.entidades=[];
                    $scope.messageAPI.showMsg(response.data.code, response.data.msg);
                }
            }, responseError);
        };

        $scope.getServicios=function (){
            $scope.cargando=true;
            $http.get(API.API_SERVICIOS).then(function (response){
                $scope.container.showLoading=false;
                $scope.cargando=false;
                
                if (response.data.code===COD_OK){
                    $scope.servicios=response.data.response;
                } 
                else {
                    $scope.servicios=[];
                    $scope.messageAPI.showMsg(response.data.code, response.data.msg);
                }
            }, responseError);
        };

        $scope.getSostenimientos=function (entidad){
            if(entidad){
                $scope.messageAPI.clean();
                $scope.cargando=true;
                $http.get(API.API_SOSTENIMIENTOS+entidad).then(function (response){
                    $scope.cargando=false;

                    if (response.data.code===COD_OK){
                        $scope.sostenimientos=response.data.response;
                    } 
                    else {
                        $scope.sostenimientos=[];
                        $scope.messageAPI.showMsg(response.data.code, response.data.msg);
                    }
                }, responseError);
            }
        };

        $scope.getCct=function (cveCct){
            if(cveCct){
                $scope.messageAPI.clean();
                $scope.cargando=true;
                $http.get(API.API_CCT+cveCct).then(function (response){
                    $scope.cargando=false;

                    if (response.data.code===COD_OK){
                        if(response.data.response!==null){
                            $scope.data.cct=response.data.response;
                            $scope.container.showContinuar=true;
                        }
                        else{
                            $scope.messageAPI.showMsg(COD_ERROR, "El CCT no es válido");
                            $scope.container.showContinuar=false;
                        }
                    } 
                    else {
                        $scope.data.cct={};
                        $scope.messageAPI.showMsg(response.data.code, response.data.msg);
                        $scope.container.showContinuar=false;
                    }
                }, responseError);
            }
        };

            
        $scope.validaDatos=function(){
            $scope.cargando=true;
            let request={
                cveDocente: $scope.mainData.cveDocente,
                entidad: $scope.data.cveEntidad,
                anioAplicacion: $scope.data.cveAnioAplicacion,
                sostenimiento: $scope.data.cveSostenimiento,
                servicio: $scope.recuperaServicio(),
                modalidad: $scope.recuperaModalidad(),
                cct: $scope.data.cveCct
            };
            
            $scope.data.errores=[];
            validaRegla($scope, $http, responseError, $scope.mainData.cveDocente, MODULO_PARTICIPACION, request, $scope.continuar);
        };


        // -------- operaciones con los datos operativos
        
        $scope.cambiaEntidad=function(){
            $scope.data.cveSostenimiento=null;
            $scope.sostenimientos=[];
            
            $scope.getSostenimientos($scope.data.cveEntidad);
        };
        
        $scope.cambiaCct=function(){
            $scope.data.cct={};
            
            $scope.getCct($scope.data.cveCct);
        };

        $scope.cargaDatosExistentes=function(){
            if(sessionStorage.getItem("cveEntidad")!==null){
                $scope.data.cveEntidad=sessionStorage.getItem("cveEntidad");
                $scope.getSostenimientos($scope.data.cveEntidad);
            }
            if(sessionStorage.getItem("cveAnioAplicacion")!==null){
                $scope.data.cveAnioAplicacion=sessionStorage.getItem("cveAnioAplicacion");
            }
            if(sessionStorage.getItem("nombreTrabajo")!==null){
                $scope.data.nombreTrabajo=sessionStorage.getItem("nombreTrabajo");
            }
            if(sessionStorage.getItem("cveSostenimiento")!==null){
                $scope.data.cveSostenimiento=sessionStorage.getItem("cveSostenimiento");
            }
            if(sessionStorage.getItem("cveServicio")!==null){
                $scope.data.cveServicio=sessionStorage.getItem("cveServicio")+'-'+sessionStorage.getItem("cveModalidad");
            }
            if(sessionStorage.getItem("cveCct")!==null){
                $scope.data.cveCct=sessionStorage.getItem("cveCct");
                $scope.getCct($scope.data.cveCct);
                $scope.participacionForm.$setDirty();
            }
        };
        
        $scope.continuar=function(){
            sessionStorage.setItem('cveEntidad', $scope.data.cveEntidad);
            sessionStorage.setItem('nombreEntidad', getDescripcionElemento($scope.entidades, $scope.data.cveEntidad));
            sessionStorage.setItem('cveAnioAplicacion', $scope.data.cveAnioAplicacion);
            sessionStorage.setItem('nombreTrabajo', $scope.data.nombreTrabajo);
            sessionStorage.setItem('cveSostenimiento', $scope.data.cveSostenimiento);
            sessionStorage.setItem('nombreSostenimiento', getDescripcionElemento($scope.sostenimientos, $scope.data.cveSostenimiento));
            sessionStorage.setItem('cveServicio', $scope.recuperaServicio());
            sessionStorage.setItem('cveModalidad', $scope.recuperaModalidad());
            sessionStorage.setItem('nombreServicio', getDescripcionElemento($scope.servicios, $scope.data.cveServicio));
            sessionStorage.setItem('cveCct', $scope.data.cveCct);
            sessionStorage.setItem('nombreCct', $scope.data.cct.nombre);
            
            location.href=RUTAS.URL_RESUMEN;
        };

        $scope.recuperaServicio=function(){
            if($scope.data.cveServicio.indexOf('-')>0){
                return $scope.data.cveServicio.substring(0, $scope.data.cveServicio.indexOf('-'));
            }
            
            return $scope.data.cveServicio;
        };

        $scope.recuperaModalidad=function(){
            if($scope.data.cveServicio.indexOf('-')>0){
                console.log("clave servicio: '"+$scope.data.cveServicio+"'");
                console.log("clave modalidad localizada: "+$scope.data.cveServicio.substring($scope.data.cveServicio.indexOf('-')+1));
                return $scope.data.cveServicio.substring($scope.data.cveServicio.indexOf('-')+1);
            }
            
            return null;
        };
    }]);

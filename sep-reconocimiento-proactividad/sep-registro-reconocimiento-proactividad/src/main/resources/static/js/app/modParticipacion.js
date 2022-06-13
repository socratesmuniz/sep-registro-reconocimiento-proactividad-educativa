angular.module('modParticipacion', ['ngSanitize', 'ngRoute', 'naif.base64'])
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
                API_CCT: API_URL + 'cct/get/',
                //datos de operacion
                API_REGISTRO: API_URL + 'participaciones/add/',
                API_GET_REGISTRO: API_URL + 'participaciones/get/',
                API_GET_DOCUMENTOS: API_URL + 'documentos/get/'
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
        $scope.data.archivoResumen=null;
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
            $scope.mainData.token=sessionStorage.getItem('token');
            $scope.mainData.cveDocente=sessionStorage.getItem('cveDocente');
            $scope.mainData.curp=sessionStorage.getItem('curp');
            
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

        $scope.getDocumentos=function(){
            $scope.cargando=true;

            $http.get(API.API_GET_DOCUMENTOS+$scope.mainData.cveDocente+"?cveEntidad="+$scope.data.datosParticipacion.cveEntidad
                        +"&anioParticipacion="+$scope.data.datosParticipacion.anioAplicacion).then(function(response){
                    $scope.cargando=false;

                    if (response.data.code===COD_OK){
                        $scope.data.documentos=response.data.response;
                        if($scope.data.documentos){
                            $scope.data.archivoResumen={
                                filename: $scope.data.documentos.nombreOriginal,
                                base64: $scope.data.documentos.contenidoBase64,
                                filetype: "application/pdf'",
                                filesize: 10000
                            };
                            document.getElementById("archivoResumen").value=$scope.data.documentos.nombreOriginal;
                        }
                        else{
                            $scope.data.archivoResumen=null;
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
                cveDocente: $scope.mainData.cveDocente,
                entidad: $scope.data.cveEntidad,
                anioAplicacion: $scope.data.cveAnioAplicacion,
                sostenimiento: $scope.data.cveSostenimiento,
                servicio: $scope.recuperaServicio(),
                modalidad: $scope.recuperaModalidad(),
                cct: $scope.data.cveCct
            };
            
            $scope.data.errores=[];
            validaRegla($scope, $http, responseError, $scope.mainData.token, $scope.mainData.cveDocente, MODULO_PARTICIPACION, 
                    request, $scope.guardaDatos);
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
            $scope.cargando=true;

            $http.get(API.API_GET_REGISTRO+$scope.mainData.cveDocente).then(function(response){
                    $scope.cargando=false;

                    if (response.data.code===COD_OK){
                        $scope.data.datosParticipacion=response.data.response;
                        if($scope.data.datosParticipacion){
//                            $scope.getDocumentos();
                            console.log("Tiene datos existentes")
                            
                            $scope.data.cveEntidad=""+$scope.data.datosParticipacion.cveEntidad;
                            $scope.getSostenimientos($scope.data.cveEntidad);
                            $scope.data.cveAnioAplicacion=""+$scope.data.datosParticipacion.anioAplicacion;
                            $scope.data.cveSostenimiento=""+$scope.data.datosParticipacion.cveSostenimiento;
                            $scope.data.cveServicio=$scope.data.datosParticipacion.cveServicioEducativo+"-"+$scope.data.datosParticipacion.cveModalidad;
                            $scope.data.cveCct=$scope.data.datosParticipacion.cveCct;
                            $scope.getCct($scope.data.cveCct);
                            $scope.data.nombreTrabajo=$scope.data.datosParticipacion.nombreTrabajo;
                        }
                    }
                    else {
                        $scope.messageAPI.showMsg(response.data.code, response.data.msg);
                    }
                }, responseError);
        };
        
        $scope.guardaDatos=function(){
            $scope.cargando=true;
            let request={
                cveDocente: $scope.mainData.cveDocente,
                curpDocente: $scope.mainData.curp,
                cveEntidad: $scope.data.cveEntidad,
                anioAplicacion: $scope.data.cveAnioAplicacion,
                nombreTrabajo: $scope.data.nombreTrabajo,
                cveSostenimiento: $scope.data.cveSostenimiento,
                cveServicioEducativo: $scope.recuperaServicio(),
                cveModalidad: $scope.recuperaModalidad(),
                cveCct: $scope.data.cveCct,
                archivos: [{
                    nombreOriginal: $scope.data.archivoResumen.filename,
                    contenidoBase64: $scope.data.archivoResumen.base64
                }]
            };
                        
            $http.post(API.API_REGISTRO+$scope.mainData.cveDocente, request).then(function(response){
                $scope.cargando=false;
                if (response.data.code === COD_OK){                    
                    location.href=RUTAS.URL_RESUMEN;
                } 
                else {
                    $scope.messageAPI.showMsg(response.data.code, response.data.msg);
                }
            }, responseError);
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
        
        $scope.tamanioFormateado=function(){
            if($scope.data.archivoResumen){
                let tam=$scope.data.archivoResumen.filesize/1024/1024;

                return tam.toFixed(2);
            }
            return "";
        };
    }]);

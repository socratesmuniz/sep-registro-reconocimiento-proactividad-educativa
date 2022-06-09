angular.module('homeModule', ['ngSanitize', 'ngRoute'])
    .constant('RUTAS', {
        URL_PARTICIPACION: './participacion'
    })
    .constant('API',
        (function () {
            const API_URL='./api/';
            return {
                //datos de catalogos
                API_GET_DOCENTE: API_URL + 'docentes/get-curp/',
                //datos de operacion
            };
        })()
    )
    .run(function ($rootScope, $sce, $http){
        $http.defaults.headers.common.Authorization=sessionStorage.getItem('token');
    })
    .controller('homeCtrl', ['$scope', 'RUTAS', 'API', '$http', '$window',function ($scope, RUTAS, API, $http, $window) {
        //constantes
        const MODULO_DATOS_PERSONALES='DP';

        //variables
        //datos de formulario
        $scope.data={};
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


        //funciones
        let responseError = function (reason) {
            $scope.cargando = false;
            $scope.messageAPI.showMsg(COD_ERROR, getMsgStatus(reason.status));
            console.log(COD_ERROR+" - "+getMsgStatus(reason.status));
        };

        $scope.initValues=function (curp, cveDocente, token, tokenOriginal, url) {
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
                                $scope.validaDatos();
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
            validaRegla($scope, $http, responseError, $scope.mainData.cveDocente, MODULO_DATOS_PERSONALES, request, $scope.revisaErrores);
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
            location.href=RUTAS.URL_PARTICIPACION;
        };
    }]);

angular.module('homeModule', ['ngSanitize', 'ngRoute'])
    .constant('RUTAS', {
        URL_COMENZAR: './datos-personales'
    })
    .constant('API',
        (function () {
            const API_URL='./api/';
            return {
                //datos de catalogos
                API_MENSAJE : API_URL+'bienvenida'
                //datos de operacion
            };
        })()
    )
    .run(function () {

    })
    .controller('homeCtrl', ['$scope', 'RUTAS', 'API', '$http',function ($scope, RUTAS, API, $http) {
        //constantes

        //variables
        //datos de formulario
        $scope.data={};
        $scope.supportBrowser=validaBrowser();
        //datos de sesion
        $scope.mainData={};
        $scope.cargando=false;

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
            showLoading: true
        };    


        //funciones
        let responseError = function (reason) {
            $scope.cargando = false;
            $scope.messageAPI.showMsg(COD_ERROR, getMsgStatus(reason.status));
            console.log(COD_ERROR+" - "+getMsgStatus(reason.status));
        };

        $scope.initValues=function (curp, cveDocente, cveEntidad, cveMunicipio, token, tokenOriginal) {
            if(curp!==null && curp!=='' && curp!=='null' && curp!==undefined){
                $scope.mainData.usuarioCurp=curp;
                sessionStorage.setItem('usuarioCurp', curp);
            }
            if(cveDocente!==null && cveDocente!=='' && cveDocente!=='null' && cveDocente!==undefined){
                $scope.mainData.cveUsuario=cveDocente;
                sessionStorage.setItem('cveUsuario', cveDocente);
            }
            else if(sessionStorage.getItem('cveUsuario')!==null){
                $scope.mainData.cveUsuario=sessionStorage.getItem('cveUsuario');
            }
            if(cveEntidad!==null && cveEntidad!=='' && cveEntidad!=='null' && cveEntidad!==undefined){
                $scope.mainData.cveEntidad=cveEntidad;
                sessionStorage.setItem('cveEntidad', cveEntidad);
            }
            else if(sessionStorage.getItem('cveEntidad')!==null){
                $scope.mainData.cveEntidad=sessionStorage.getItem('cveEntidad');
            }
            if(cveMunicipio!==null && cveMunicipio!=='' && cveMunicipio!=='null' && cveMunicipio!==undefined){
                $scope.mainData.cveMunicipio=cveMunicipio;
                sessionStorage.setItem('cveMunicipio', cveMunicipio);
            }
            else if(sessionStorage.getItem('cveMunicipio')!==null){
                $scope.mainData.cveMunicipio=sessionStorage.getItem('cveMunicipio');
            }
            if(token!==null && token!=='' && token!=='null' && token!==undefined){
                $scope.mainData.token=token;
                sessionStorage.setItem('token', token);
            }
            if(tokenOriginal!==null && tokenOriginal!=='' && tokenOriginal!=='null' && tokenOriginal!==undefined){
                sessionStorage.setItem('tokenOriginal', tokenOriginal);
            }
            
            $scope.getMensaje();
            console.log("cargando: "+$scope.cargando+", cveDocente:"+cveDocente+", curp:"+curp+", cveUsuario:"+$scope.mainData.cveUsuario);
            console.log("Entidad del usuario:"+$scope.mainData.cveEntidad);
            console.log("Municipio del usuario:"+$scope.mainData.cveMunicipio);
        };


        $scope.getMensaje=function(){
            $scope.messageAPI.clean();
            $scope.cargando=true;

            $http.get(API.API_MENSAJE).then(function (response){
                    $scope.cargando=false;
                    
                    if(response.data.code===COD_OK) {
                        $scope.container.showLoading=false;

                        if(response.data.response){
                            $scope.msg=response.data.response;
                        }
                    } else{
                        $scope.messageAPI.showMsg(response.data.code, response.data.msg);
                    }
                }, responseError);
        };

        
        $scope.getNombreCompleto=function(docente){
            return docente.nombre+" "+docente.primerApellido+" "+docente.segundoApellido;
        };
        
        $scope.iniciar=function(){
            location.href=RUTAS.URL_COMENZAR;
        };
    }]);

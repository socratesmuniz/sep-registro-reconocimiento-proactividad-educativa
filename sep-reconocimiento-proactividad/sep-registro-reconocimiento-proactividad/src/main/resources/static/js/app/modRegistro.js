angular.module('modRegistro', ['ngSanitize', 'ngRoute'])
    .constant('RUTAS', {
        URL_RESUMEN: './resumen',
        URL_CIERRE: './registrado'
    })
    .constant('API',
        (function (){
            const API_URL='./api/';
            return {
                //catalogos
                //datos de operacion
                API_REGISTRO: API_URL + 'registro/add/',
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
            modal.modalMsgNote="No olvide imprimir o guardar la contancia que se va a generar.";
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
    .controller('ctrlRegistro', ['$scope', '$rootScope', 'API', 'RUTAS', '$http', function ($scope, $rootScope, API, RUTAS, $http){
        // --- constantes ---
        
        // --- variables ---
        $rootScope.mDisclaimer={visible: false};
        $scope.mAcepto="";        
        //datos de formulario
        $scope.data={};
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
            valoracion: true,
            requisitos: true,
            factores: true,
            resumen: true,
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
            $scope.mainData.cveUsuario=sessionStorage.getItem('cveUsuario');
            $scope.mainData.cveDocente=sessionStorage.getItem('cveDocente');
            $scope.mainData.curpDocente=sessionStorage.getItem('curpDocente');
            $scope.mainData.cvePuestoPromocion=sessionStorage.getItem('cvePuestoPromocion');
            $scope.mainData.cveEntidad=sessionStorage.getItem('cveEntidad');
            $scope.mainData.cveValoracion=sessionStorage.getItem('cveValoracion');
            
            $scope.container.showLoading=false;
        };


        // -------- operaciones con los datos operativos

        $scope.finalizar=function (){
            var accept=function () {
                $scope.cargando=true;
                
                $scope.guardaDatos();
                $rootScope.closeDisclaimerModal($rootScope.mDisclaimer);
            };

            var cancel=function () {
                $rootScope.closeDisclaimerModal($rootScope.mDisclaimer);
            };

            $scope.messageAPI.clean();
            $rootScope.disclaimerModal($rootScope.mDisclaimer,
                    "Esta a punto de finalizar el registro del docente.",
                    "Si está de acuerdo, escriba la palabra ACEPTO",
                    accept,
                    cancel);
        };

        $scope.guardaDatos=function(){
            $scope.cargando=true;
            let request={
                cveDocente: $scope.mainData.cveDocente,
                cveEntidad: $scope.mainData.cveEntidad,
                cveValoracion: $scope.mainData.cveValoracion
            };
            
            $http.post(API.API_REGISTRO+$scope.mainData.cveUsuario, request).then(function(response){
                $scope.cargando=false;
                if (response.data.code === COD_OK){
                    $scope.container.showContinuar=true;

                    sessionStorage.removeItem('cvePuestoPromocion');
                    location.href=RUTAS.URL_CIERRE;
                } 
                else {
                    $scope.messageAPI.showMsg(response.data.code, response.data.msg);
                }
            }, responseError);
        };

        $scope.regresaModuloAnterior=function(){
            location.href=RUTAS.URL_RESUMEN;
        };
    }]);

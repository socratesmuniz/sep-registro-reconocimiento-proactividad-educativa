angular.module('modFinalizado', ['ngSanitize', 'ngRoute'])
    .constant('API',
        (function (){
            const API_URL='./api/';
            return {
                //catalogos
                API_GET_DOCENTE: API_URL + 'docentes/get-curp/',
                //datos de operacion
                API_GET_PARTICIPACION: API_URL + 'participaciones/get/',

                API_DOCUMENTO: API_URL + 'documentos/get/',                
                API_FICHA_REGISTRO: './documentos/ficha-registro/get/',
            };
        })()
    )
    .run(function ($rootScope, $sce, $http){
        $http.defaults.headers.common.Authorization=sessionStorage.getItem('token');
    })
    .controller('ctrlFinalizado', ['$scope', 'API', '$http', function ($scope, API, $http){
        // --- constantes ---
        
        // --- variables ---
        //datos de formulario
        $scope.data={};
        $scope.data.datosDocente={};
        $scope.data.datosParticipacion={};
        //datos de sesion
        $scope.mainData={};
        $scope.cargando=false;

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
            showLoading: true
        };    

        $scope.avanceFormulario={
            datosPersonales: true,
            participacion: true,
            resumen: true,
            registrado: true
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
            $scope.mainData.cveAnioAplicacion=sessionStorage.getItem('cveAnioAplicacion');
            
            $scope.getDatosDocente();
            $scope.getParticipacion();
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
                        }
                        else {
                            $scope.messageAPI.showMsg(response.data.code, response.data.msg);
                        }
                    }, responseError);
            }
        };

        $scope.getParticipacion=function (){
            $scope.cargando=true;

            $http.get(API.API_GET_PARTICIPACION+$scope.mainData.cveDocente+"?cveEntidad="+$scope.mainData.cveEntidad
                    +"&anioParticipacion="+$scope.mainData.cveAnioAplicacion).then(function (response){
                $scope.cargando=false;
                
                if (response.data.code===COD_OK){
                    if(response.data.response!==null){
                        $scope.data.datosParticipacion=response.data.response;
                    }
                } 
                else {
                    $scope.messageAPI.showMsg(response.data.code, response.data.msg);
                }
            }, responseError);
        };


        // -------- operaciones con los datos operativos

        $scope.imprimirRegistro=function (){
            $scope.cargando=true;
            
            try{
                var xhr=new XMLHttpRequest();
                xhr.open("POST", API.API_FICHA_REGISTRO+$scope.mainData.curp+"?entidad="+$scope.mainData.cveEntidad
                        +"&anioParticipacion="+$scope.mainData.cveAnioAplicacion);
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
                            a.download="comprobanteRPE.pdf";
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
        
        $scope.descargaResumen=function (){
            $scope.cargando=true;
            
            try{
                var xhr=new XMLHttpRequest();
                xhr.open("GET", API.API_DOCUMENTO+$scope.mainData.cveDocente+"?cveEntidad="+$scope.mainData.cveEntidad
                        +"&anioParticipacion="+$scope.mainData.cveAnioAplicacion);
                xhr.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
                xhr.responseType="json";
                xhr.onload=function () {
                    $scope.cargando=false;
                    if(this.status === 200) {
                        let doc=xhr.response.response;
                        console.log(" encuentra Dcoumento: " + doc);                                                
                        console.log(" nombre Dcoumento: " + doc.nombreOriginal);                                                
                        var blob=b64toBlob(doc.contenidoBase64, "application/pdf");   
                        console.log("tamaño: "+blob.size);    
                        var a=document.createElement('a');
                            a.href=URL.createObjectURL(blob);
                            a.download=doc.nombreOriginal;
                            a.click();
                
                        $scope.$apply();
                    }
                    else{
                        $scope.messageAPI.showMsg(this.status, "No se logro descargar el documento");
                    }
                };
                xhr.send(JSON.stringify(
                    {
                    })
                );
            }
            catch(ex){
                console.log("Se presento un problema al descargar el documento: "+ex);
            }
        };
    }]);

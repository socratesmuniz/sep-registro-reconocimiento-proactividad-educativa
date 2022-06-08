angular.module('modFinalizado', ['ngSanitize', 'ngRoute'])
    .constant('RUTAS', {
        URL_PARTICIPACION: './datos-personales'
    })
    .constant('API',
        (function (){
            const API_URL='./api/';
            return {
                //catalogos
                //datos de operacion
                API_GET_DOCENTE: API_URL + 'docentes/get-curp/',
                API_GET_PARTICIPACION: API_URL + 'participaciones/get/',
                
                API_FICHA_REGISTRO: './documentos/ficha-registro/get/',
                API_FICHA_RECHAZO: './documentos/ficha-rechazo/get/'
            };
        })()
    )
    .run(function ($rootScope, $sce, $http){
        $http.defaults.headers.common.Authorization=sessionStorage.getItem('token');
    })
    .controller('ctrlFinalizado', ['$scope', 'API', 'RUTAS', '$http', function ($scope, API, RUTAS, $http){
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
            $scope.mainData.cveEntidad=sessionStorage.getItem('cveEntidad');
            $scope.mainData.cveValoracion=sessionStorage.getItem('cveValoracion');
            
            $scope.getDatosDocente();
            $scope.getParticipacion();
        };

        
        $scope.getDatosDocente=function(){
            $scope.messageAPI.clean();
            $scope.cargando=true;

            if($scope.mainData.curpDocente!==null && $scope.mainData.curpDocente!==""){
                $http.get(API.API_GET_DOCENTE+$scope.mainData.curpDocente).then(function(response){
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

            $http.get(API.API_GET_PARTICIPACION+$scope.mainData.cveDocente).then(function (response){
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

        $scope.getNombreEstado=function(estado){
            if(estado===COD_EDO_CERRADO){
                return "Registrado";
            }
            else if(estado===COD_EDO_RECHAZADO){
                return "No cumplimiento de requisitos";
            }
            else{
                return "Desconocido";
            }
        };

        $scope.imprimirRegistro=function (){
            $scope.cargando=true;
            
            try{
                var xhr=new XMLHttpRequest();
                xhr.open("POST", API.API_FICHA_REGISTRO+$scope.mainData.curpDocente+"?entidad="+$scope.recuperaNombreLogo());
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
                            a.download="fichaRegistroRTUT.pdf";
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
                window.location=RUTAS.URL_FINALIZADO;
            }
        };

        $scope.imprimirRechazo=function (){
            $scope.cargando=true;
            
            try{
                var xhr=new XMLHttpRequest();
                xhr.open("POST", API.API_FICHA_RECHAZO+$scope.mainData.curpDocente+"?entidad="+$scope.recuperaNombreLogo());
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
                            a.download="fichaNoCumplimientoRTUT.pdf";
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
                window.location=RUTAS.URL_FINALIZADO;
            }
        };
        
        $scope.textoFinalizacion=function(){
            if($scope.data.datosParticipacion.cveEstado===3 || $scope.data.datosParticipacion.cveEstado==='3'){
                return "En virtud que no se cumple con alguno de los requisitos establecidos en el artículo 9 del Acuerdo que contiene las disposiciones, criterios e indicadores para la realización del proceso de reconocimiento de la asesoría, apoyo y acompañamiento, ciclo escolar 2022-2023 y en la convocatoria correspondiente, se ha generado la constancia de no cumplimiento de requisitos";
            }
            else{
                return "Ha finalizado el registro para el proceso de reconocimiento de la asesoría, apoyo y acompañamiento, ciclo escolar 2022-2023";
            }
        };

        $scope.recuperaNombreLogo=function(){
            console.log("Municipio: "+$scope.data.datosParticipacion.cveMunicipio);
            
            if($scope.data.datosParticipacion.cveMunicipio!==null && $scope.data.datosParticipacion.cveMunicipio>0){
                return "Baja California (Tijuana)";
            }
            else{
                return $scope.data.datosParticipacion.entidad;
            }
        };

        $scope.regresaModuloAnterior=function(){
            sessionStorage.removeItem('cveValoracion');
            
            location.href=RUTAS.URL_PARTICIPACION;
        };
    }]);

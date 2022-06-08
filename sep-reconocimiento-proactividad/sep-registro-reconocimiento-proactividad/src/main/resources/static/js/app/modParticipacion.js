angular.module('modParticipacion', ['ngSanitize', 'ngRoute'])
    .constant('RUTAS', {
        URL_REQUISITOS: './requisitos',
        URL_CIERRE: './registrado'
    })
    .constant('API',
        (function (){
            const API_URL='./api/';
            return {
                //catalogos
                API_ENTIDADES: API_URL + 'entidades/get',
                API_SOSTENIMIENTOS: API_URL + 'sostenimientos-entidad/get/',
                API_PUESTOS: API_URL + 'puestos-sostenimiento/get/',
                API_SERVICIOS: API_URL + 'servicios-puesto/get/',
                API_CATEGORIAS: API_URL + 'categorias-servicio/get/',
                API_TIPOS_PLAZA: API_URL + 'tiposplaza-categoria/get/',
                API_VALORACIONES: API_URL + 'valoraciones-tipoplaza/get/',
                API_MODALIDADES_TUTORIA: API_URL + 'modalidades-tutoria/get',
                API_ANIOS_PARTICIPACION: API_URL + 'anios-participacion/get',
                //datos de operacion
                API_ADD_PARTICIPACION: API_URL + 'participaciones/add/',
                API_GET_PARTICIPACION: API_URL + 'participaciones/get/',
//                API_GET_ANIOS_PARTICIPACION: API_URL + 'anios-participacion/get/',
                API_SELECCIONA_ANIO_PARTICIPACION: API_URL + 'anios-participacion/selection/'
            };
        })()
    )
    .run(function ($rootScope, $sce, $http){
        $http.defaults.headers.common.Authorization=sessionStorage.getItem('token');
    })
    .controller('ctrlParticipacion', ['$scope', 'API', 'RUTAS', '$http', function ($scope, API, RUTAS, $http){
        // --- constantes ---
        const MODULO_PARTICIPACION='VL';
        const MODULO_PLAZA='PL';
        
        // --- variables ---
        //datos de formulario
        $scope.data={};
        $scope.data.participacion={};
        $scope.data.plazas=[];
        $scope.dataPlaza={
            plaza: '',
            horas: '',
            cct: ''
        };
        //datos de sesion
        $scope.mainData={};
        //datos de seleccion
        $scope.checkboxModelAniosParticipacion={}; 
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
            showContinuar: false,
            showAdicionPlaza: false
        };

        $scope.avanceFormulario={
            datosPersonales: true,
            valoracion: false,
            requisitos: false,
            factores: false,
            resumen: false,
            registrado: false
        };
        
        //Catálogos
        $scope.entidades=[];
        $scope.puestos=[];
        $scope.servicios=[];
        $scope.categorias=[];
        $scope.valoraciones=[];
        $scope.tiposPlaza=[];
        $scope.modalidadesTutoria=[];
        $scope.aniosParticipacion=[];
        $scope.horas=[];


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
            $scope.mainData.cveEntidad=sessionStorage.getItem('cveEntidad');
            $scope.mainData.cveMunicipio=sessionStorage.getItem('cveMunicipio');
            
            $scope.getHoras();
            $scope.getModalidadesTutoria();
            $scope.getAniosParticipacion();
            $scope.getEntidades();
            $scope.cambiaEntidad();
            $scope.getParticipacion();
            
            $scope.container.showContinuar=true;
        };

        $scope.getHoras=function (){
            for(let i=1; i<=42; i++){
                $scope.horas.push({cve: ''+i, descripcion: ''+i});
            }
        };

        $scope.getEntidades=function (){
            $http.get(API.API_ENTIDADES).then(function (response){
                $scope.container.showLoading=false;
                
                if (response.data.code===COD_OK){
                    $scope.entidades=response.data.response;
                } 
                else {
                    $scope.entidades=[];
                    $scope.messageAPI.showMsg(response.data.code, response.data.msg);
                }
            }, responseError);
        };

        $scope.getModalidadesTutoria=function (){
            $http.get(API.API_MODALIDADES_TUTORIA).then(function (response){
                $scope.container.showLoading=false;
                
                if (response.data.code===COD_OK){
                    $scope.modalidadesTutoria=response.data.response;
                } 
                else {
                    $scope.modalidadesTutoria=[];
                    $scope.messageAPI.showMsg(response.data.code, response.data.msg);
                }
            }, responseError);
        };

        $scope.getAniosParticipacion=function (){
            $http.get(API.API_ANIOS_PARTICIPACION).then(function (response){
                $scope.container.showLoading=false;
                
                if (response.data.code===COD_OK){
                    $scope.aniosParticipacion=response.data.response;
                } 
                else {
                    $scope.aniosParticipacion=[];
                    $scope.messageAPI.showMsg(response.data.code, response.data.msg);
                }
            }, responseError);
        };

        $scope.getSostenimientos=function (entidad, municipio){
            if(entidad && municipio){
                $http.get(API.API_SOSTENIMIENTOS+entidad+"?cveMunicipio="+municipio).then(function (response){
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

        $scope.getPuestos=function (entidad, municipio, sostenimiento){
            if(entidad && municipio && sostenimiento){
                $http.get(API.API_PUESTOS+entidad+"?cveMunicipio="+municipio+"&cveSostenimiento="+sostenimiento).then(function (response){
                    $scope.cargando=false;

                    if (response.data.code===COD_OK){
                        $scope.puestos=response.data.response;
                    } 
                    else {
                        $scope.puestos=[];
                        $scope.messageAPI.showMsg(response.data.code, response.data.msg);
                    }
                }, responseError);
            }
        };

        $scope.getServicios=function (entidad, municipio, sostenimiento, puesto){
            if(entidad && municipio && sostenimiento && puesto){
                $http.get(API.API_SERVICIOS+entidad+"?cveMunicipio="+municipio+"&cveSostenimiento="+sostenimiento+"&cvePuesto="+puesto).then(function (response){
                    $scope.cargando=false;

                    if (response.data.code===COD_OK){
                        $scope.servicios=response.data.response;
                    } 
                    else {
                        $scope.servicios=[];
                        $scope.messageAPI.showMsg(response.data.code, response.data.msg);
                    }
                }, responseError);
            }
        };

        $scope.getCategorias=function (entidad, municipio, sostenimiento, puesto, servicio, modalidad){
            if(entidad && municipio && sostenimiento && puesto && servicio){
                $http.get(API.API_CATEGORIAS+entidad+"?cveMunicipio="+municipio+"&cveSostenimiento="+sostenimiento+"&cvePuesto="+puesto
                        +"&cveServicio="+servicio+(modalidad!==null? "&cveModalidad="+modalidad: "")).then(function (response){
                    $scope.cargando=false;

                    if (response.data.code===COD_OK){
                        $scope.categorias=response.data.response;
                    } 
                    else {
                        $scope.categorias=[];
                        $scope.messageAPI.showMsg(response.data.code, response.data.msg);
                    }
                }, responseError);
            }
        };

        $scope.getTiposPlaza=function (entidad, municipio, sostenimiento, puesto, servicio, modalidad, categoria){
            if(entidad && municipio && sostenimiento && puesto && servicio && categoria){
                $http.get(API.API_TIPOS_PLAZA+entidad+"?cveMunicipio="+municipio+"&cveSostenimiento="+sostenimiento+"&cvePuesto="+puesto
                        +"&cveServicio="+servicio+(modalidad!==null? "&cveModalidad="+modalidad: "")
                        +"&cveCategoria="+categoria).then(function (response){
                    $scope.container.showLoading=false;

                    if (response.data.code===COD_OK){
                        $scope.tiposPlaza=response.data.response;
                    } 
                    else {
                        $scope.tiposPlaza=[];
                        $scope.messageAPI.showMsg(response.data.code, response.data.msg);
                    }
                }, responseError);
            }
        };

        $scope.getValoraciones=function (entidad, municipio, sostenimiento, puesto, servicio, modalidad, categoria, tipoPlaza){
            if(entidad && municipio && sostenimiento && puesto && servicio && categoria && tipoPlaza){
                $scope.cargando=true;
                $http.get(API.API_VALORACIONES+entidad+"?cveMunicipio="+municipio+"&cveSostenimiento="+sostenimiento+"&cvePuesto="+puesto
                        +"&cveServicio="+servicio+(modalidad!==null? "&cveModalidad="+modalidad: "")
                        +"&cveCategoria="+categoria+"&cveTipoPlaza="+tipoPlaza)
                        .then(function (response){
                    $scope.cargando=false;

                    if (response.data.code===COD_OK){
                        $scope.valoraciones=response.data.response;
                    } 
                    else {
                        $scope.valoraciones=[];
                        $scope.messageAPI.showMsg(response.data.code, response.data.msg);
                    }
                }, responseError);
            }
        };

        $scope.getParticipacion=function(){
            $scope.cargando=true;

            $http.get(API.API_GET_PARTICIPACION+$scope.mainData.cveDocente).then(function (response){
                $scope.cargando=false;
                
                if (response.data.code===COD_OK){
                    if(response.data.response!==null){
                        $scope.data.participacion=response.data.response;
                        $scope.data.cveSostenimiento=""+$scope.data.participacion.cveSostenimiento;
                        $scope.getPuestos($scope.mainData.cveEntidad, $scope.mainData.cveMunicipio, $scope.data.cveSostenimiento);
                        $scope.data.cvePuesto=""+$scope.data.participacion.cvePuestoActual;
                        $scope.getServicios($scope.mainData.cveEntidad, $scope.mainData.cveMunicipio, $scope.data.cveSostenimiento, $scope.data.cvePuesto);
                        $scope.data.cveServicio=''+$scope.data.participacion.cveServicioEducativo;
                        if($scope.data.participacion.cveModalidad!==null && $scope.data.participacion.cveModalidad!==undefined
                                 && $scope.data.participacion.cveModalidad!==0){
                            $scope.data.cveServicio+='-'+$scope.data.participacion.cveModalidad;
                            console.log("Se tiene modalidad: "+$scope.data.participacion.cveModalidad);
                        }
                        $scope.getCategorias($scope.mainData.cveEntidad, $scope.mainData.cveMunicipio, $scope.data.cveSostenimiento, $scope.data.cvePuesto, 
                                $scope.recuperaServicio(), $scope.recuperaModalidad());
                        $scope.data.cveCategoria=$scope.data.participacion.cveCategoriaActual;
                        $scope.getTiposPlaza($scope.mainData.cveEntidad, $scope.mainData.cveMunicipio, $scope.data.cveSostenimiento, 
                                $scope.data.cvePuesto, $scope.recuperaServicio(), $scope.recuperaModalidad(), $scope.data.cveCategoria);
                        $scope.data.cveTipoPlaza=""+$scope.data.participacion.cveTipoPlaza;
                        $scope.getValoraciones($scope.mainData.cveEntidad, $scope.mainData.cveMunicipio, $scope.data.cveSostenimiento, 
                                $scope.data.cvePuesto, $scope.recuperaServicio(), $scope.recuperaModalidad(), $scope.data.cveCategoria,
                                $scope.data.cveTipoPlaza);
                        $scope.data.cveValoracion=""+$scope.data.participacion.cveValoracion;
                        $scope.data.cveCct=$scope.data.participacion.cveCct;
                        $scope.data.cveModalidadTutoria=($scope.data.participacion.cveModalidadTutoria!==null? ""+$scope.data.participacion.cveModalidadTutoria: null);
                        $scope.data.horasPlazas=($scope.data.participacion.horasPlazas!==null? ""+$scope.data.participacion.horasPlazas: null);
                        $scope.valoracionForm.$setDirty();

                        if($scope.data.participacion.aniosParticipacion!==null){
                            $scope.data.participacion.aniosParticipacion.map((item) => {
                                $scope.checkboxModelAniosParticipacion={
                                    ...$scope.checkboxModelAniosParticipacion,
                                    [item]: true
                                };
                            });

                            console.log("Sel: "+Object.keys($scope.checkboxModelAniosParticipacion).length);
                        }
                        
                        $scope.data.plazas=$scope.data.participacion.plazas;
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
                cve_docente: $scope.mainData.cveDocente,
                entidad: $scope.mainData.cveEntidad,
                municipio: ($scope.mainData.cveMunicipio===0 || $scope.mainData.cveMunicipio==='0'? null: $scope.mainData.cveMunicipio),
                sostenimiento: $scope.data.cveSostenimiento,
                puesto: $scope.data.cvePuesto,
                servicio: $scope.recuperaServicio(),
                modalidad: $scope.recuperaModalidad(),
                categoria: $scope.data.cveCategoria,
                tipo_valoracion: $scope.data.cveValoracion,
                tipo_plaza: $scope.data.cveTipoPlaza,
                cct: $scope.data.cveCct,
                modalidadTutoria: ($scope.data.cveModalidadTutoria===undefined? null: $scope.data.cveModalidadTutoria),
                numeroPlazas: $scope.data.plazas.length,
                horasPlazas: $scope.data.horasPlazas
            };
            
            $scope.data.errores=[];
            validaRegla($scope, $http, responseError, $scope.mainData.cveUsuario, MODULO_PARTICIPACION, request, $scope.guardaDatos);
        };


        // -------- operaciones con los datos operativos
        
        $scope.cambiaEntidad=function(){
            $scope.data.cveSostenimiento=null;
            $scope.data.cvePuesto=null;
            $scope.data.cveServicio=null;
            $scope.data.cveCategoria=null;
            $scope.data.cveTipoPlaza=null;
            $scope.data.cveValoracion=null;
            $scope.sostenimientos=[];
            $scope.puestos=[];
            $scope.servicios=[];
            $scope.categorias=[];
            $scope.tiposPlaza=[];
            $scope.valoraciones=[];
            
            $scope.getSostenimientos($scope.mainData.cveEntidad, $scope.mainData.cveMunicipio);
        };
        
        $scope.cambiaSostenimiento=function(){
            $scope.data.cvePuesto=null;
            $scope.data.cveServicio=null;
            $scope.data.cveCategoria=null;
            $scope.data.cveTipoPlaza=null;
            $scope.data.cveValoracion=null;
            $scope.puestos=[];
            $scope.servicios=[];
            $scope.categorias=[];
            $scope.tiposPlaza=[];
            $scope.valoraciones=[];
            
            $scope.getPuestos($scope.mainData.cveEntidad, $scope.mainData.cveMunicipio, $scope.data.cveSostenimiento);
        };
        
        $scope.cambiaPuesto=function(){
            $scope.data.cveServicio=null;
            $scope.data.cveCategoria=null;
            $scope.data.cveTipoPlaza=null;
            $scope.data.cveValoracion=null;
            $scope.servicios=[];
            $scope.categorias=[];
            $scope.tiposPlaza=[];
            $scope.valoraciones=[];
            
            $scope.getServicios($scope.mainData.cveEntidad, $scope.mainData.cveMunicipio, $scope.data.cveSostenimiento, $scope.data.cvePuesto);
        };
        
        $scope.cambiaServicio=function(){
            $scope.data.cveCategoria=null;
            $scope.data.cveTipoPlaza=null;
            $scope.data.cveValoracion=null;
            $scope.categorias=[];
            $scope.tiposPlaza=[];
            $scope.valoraciones=[];
            
            $scope.getCategorias($scope.mainData.cveEntidad, $scope.mainData.cveMunicipio, $scope.data.cveSostenimiento, $scope.data.cvePuesto, 
                    $scope.recuperaServicio(), $scope.recuperaModalidad());
        };
        
        $scope.cambiaCategoria=function(){
            $scope.data.cveTipoPlaza=null;
            $scope.data.cveValoracion=null;
            $scope.tiposPlaza=[];
            $scope.valoraciones=[];
            
            $scope.getTiposPlaza($scope.mainData.cveEntidad, $scope.mainData.cveMunicipio, $scope.data.cveSostenimiento, $scope.data.cvePuesto, 
                    $scope.recuperaServicio(), $scope.recuperaModalidad(), $scope.data.cveCategoria);
        };
        
        $scope.cambiaTipoPlaza=function(){
            $scope.data.cveValoracion=null;
            $scope.valoraciones=[];
            
            //inicializa las plazas
            $scope.data.plazas=[];
            
            $scope.getValoraciones($scope.mainData.cveEntidad, $scope.mainData.cveMunicipio, $scope.data.cveSostenimiento, $scope.data.cvePuesto, 
                    $scope.recuperaServicio(), $scope.recuperaModalidad(), $scope.data.cveCategoria, $scope.data.cveTipoPlaza);
        };

        $scope.guardaDatos=function(){
            $scope.cargando=true;

            let anios=new Array();
            let clavesAnios=Object.keys($scope.checkboxModelAniosParticipacion);
            for(let i=0; i<clavesAnios.length; i++){
                if($scope.checkboxModelAniosParticipacion[clavesAnios[i]]===true){
                    anios.push(clavesAnios[i]);
                }
            }
            
            let request={
                cveDocente: $scope.mainData.cveDocente,
                cveEntidad: $scope.mainData.cveEntidad,
                cveMunicipio: ($scope.mainData.cveMunicipio===0 || $scope.mainData.cveMunicipio==='0'? null: $scope.mainData.cveMunicipio),
                cveSostenimiento: $scope.data.cveSostenimiento,
                cvePuestoActual: $scope.data.cvePuesto,
                cveServicioEducativo: $scope.recuperaServicio(),
                cveModalidad: $scope.recuperaModalidad(),
                cveCategoriaActual: $scope.data.cveCategoria,
                cveValoracion: $scope.data.cveValoracion,
                cveTipoPlaza: $scope.data.cveTipoPlaza,
                cveCct: $scope.data.cveCct,
                cveModalidadTutoria: ($scope.data.cveModalidadTutoria===undefined? null: $scope.data.cveModalidadTutoria),
                aniosParticipacion: anios,
                plazas: $scope.data.plazas,
                horasPlazas: $scope.data.horasPlazas
            };

            $http.post(API.API_ADD_PARTICIPACION+$scope.mainData.cveUsuario, request).then(function(response){
                    $scope.cargando=false;
                    if (response.data.code===COD_OK){
                        if(response.data.response===true || response.data.response==='true'){
                            sessionStorage.setItem('nombreEntidad', getDescripcionElemento($scope.entidades, $scope.mainData.cveEntidad));
                            sessionStorage.setItem('cveValoracion', $scope.data.cveValoracion);
                            sessionStorage.setItem('nombreValoracion', getDescripcionElemento($scope.valoraciones, $scope.data.cveValoracion));
                            $scope.messageAPI.showMsg(response.data.code, response.data.msg);

                            location.href=RUTAS.URL_REQUISITOS;
                        }
                        else{
                            $scope.messageAPI.showMsg(COD_ERROR, "No se logro actualizar la información");
                        }
                    }
                    else {
                        $scope.messageAPI.showMsg(response.data.code, response.data.msg);
                    }
                }, responseError);            
        };
        
        $scope.seleccionaAnioParticipacion=function(elemento){
            let clavesAnios=Object.keys($scope.checkboxModelAniosParticipacion);
            for(let i=0; i<clavesAnios.length; i++){
                if($scope.checkboxModelAniosParticipacion[clavesAnios[i]]===true){
                    $scope.container.showContinuar=true;
                    return;
                }
            }
            
            $scope.container.showContinuar=false;
        };
        
        $scope.recuperaNombreEntidad=function(){
            if($scope.mainData.cveMunicipio!==null && $scope.mainData.cveMunicipio>0){
                return "Tijuana";
            }
            return getDescripcionElemento($scope.entidades, $scope.mainData.cveEntidad);
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

        /*
         * Plazas
         */

            
        $scope.validaDatosPlaza=function(){
            $scope.cargando=true;
            let request={
                plaza: $scope.dataPlaza.plaza,
                horas: $scope.dataPlaza.horas,
                cct: $scope.dataPlaza.cct,
                tipoPlaza: $scope.data.cveTipoPlaza,
                entidad: $scope.mainData.cveEntidad,
                cveDocente: $scope.mainData.cveDocente
            };
            
            $scope.data.errores=[];
            validaRegla($scope, $http, responseError, $scope.mainData.cveUsuario, MODULO_PLAZA, request, $scope.agregaPlaza);
        };
            
        $scope.agregaPlaza=function(){
            $scope.data.plazas.push($scope.dataPlaza);            
            $scope.cargando=false;
            
            $scope.dataPlaza={
                plaza: '',
                horas: '',
                cct: ''
            };
            $scope.calculaHorasPlazas();
            $scope.container.showAdicionPlaza=false;
        };
        
        $scope.eliminaPlaza=function(idx){
            console.log("Intenta remover plaza: "+idx+", ("+$scope.data.plazas+")");
            $scope.data.plazas.splice(idx, 1);
            console.log("plazas: "+$scope.data.plazas);
            $scope.container.showAdicionPlaza=false;
        };
        
        $scope.validaAdicionPlaza=function(){
            if($scope.data.cveTipoPlaza==='1' && $scope.data.plazas.length<1){
                return true;
            }
            if($scope.data.cveTipoPlaza==='2' && $scope.data.plazas.length<42){
                return true;
            }
            
            return false;
        };
        
        $scope.calculaHorasPlazas=function(){
            let horas=0;
            
            if($scope.data.cveTipoPlaza==='2'){
                for(let i=0; i<$scope.data.plazas.length; i++){
                    horas+=parseInt($scope.data.plazas[i].horas);
                }
            }
            
            $scope.data.horasPlazas=horas;
        };
        
        $scope.habilitaAdicionPlaza=function(){
            $scope.container.showAdicionPlaza=true;
        };
    }]);

/* 
 * Libreria con funciones comunes para el modo de edicion
 */
constantesMotorReglas=function(){
    const API_URL='./api/';
    
    return{
        //constantes
        COD_OK: 0,
        //rutas de api
        API_MOTOR_REGLAS_VALIDACION: API_URL+'motor-reglas/validacion/'
    };
};

validaRegla=function($scope, $http, responseError, cveDocenteLogin, cveRegla, datos, callbackPostValidacion){
    if(cveRegla){
        $http.post(constantesMotorReglas().API_MOTOR_REGLAS_VALIDACION+cveDocenteLogin+"?modulo="+cveRegla, datos).then(function(response){
            if(response.data.code===constantesMotorReglas().COD_OK){
                if(response.data.response.validacion===true){
                    $scope.data.errores=[];
                    callbackPostValidacion();
                }
                else{
                    $scope.cargando=false;
                    $scope.data.errores=response.data.response.errores;
                    $scope.messageAPI.show=false;
                }
            }
            else{
                $scope.cargando=false;
                $scope.data.errores=[response.data.msg];
            }
        }, responseError);
    }
    else{
        $scope.cargando=false;
        $scope.data.errores=["No existe una regla para el módulo"];
    }
};

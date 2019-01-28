
ctmApp.register.controller('formalAssessmentCreateList', ['$http','$scope','$location','$routeParams', function ($http,$scope,$location,$routeParams) {
    // 新增操作
    $scope.Create=function(id){
        $location.path("/formalAssessmentCreate/"+id);
    };
}]);
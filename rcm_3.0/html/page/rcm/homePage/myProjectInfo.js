
ctmApp.register.controller('myProjectInfo', ['$http','$routeParams','$scope','$location','$filter',function ($http,$routeParams,$scope,$location,$filter) {
	$scope.returnUrl = $routeParams.url;
	
}]);

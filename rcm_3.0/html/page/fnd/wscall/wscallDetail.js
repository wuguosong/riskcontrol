ctmApp.register.controller('wscallDetail', [ '$http', '$scope', '$location','$routeParams', 
function($http, $scope, $location, $routeParams) {
	var id = $routeParams.id;
	$scope.oldUrl = $routeParams.url;

	$http({
		method : 'post',
		url : srvUrl + "wscall/queryById.do",
		data : $.param({
			"id" : id
		})
	}).success(function(data) {
		if (data.success) {
			$scope.log = data.result_data;
		} else {
			$.alert(data.result_name);
		}
	});
} ]);
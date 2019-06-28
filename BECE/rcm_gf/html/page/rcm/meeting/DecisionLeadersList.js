ctmApp.register.controller('DecisionLeadersListController', ['$http','$routeParams','$scope','$location','$routeParams','$filter', function ($http,$routeParams,$scope,$location,$routeParams,$filter) {
	$scope.dictId = $routeParams.id;
	$scope.myCurrUrl = "#"+$location.path();
	$scope.queryByPage = function () {
		$http({
			method:'post',  
		    url:srvUrl+"role/queryMeetingLeaderByPage.do",
		    data: $.param({"page":JSON.stringify($scope.paginationConf)})
		}).success(function(result){
			if(result.success){
			    $scope.userList = result.result_data.list;
				$scope.paginationConf.totalItems = result.result_data.totalItems;
			}else{
				$.alert(result.result_name);
			}
		});
    };
    
	$scope.create = function () {
		var returnUrl = $filter('encodeURI')($scope.myCurrUrl,"VALUE");
		$location.path("/DecisionLeadersInfo/create/0/"+$scope.dictId+"/"+returnUrl);
	}
	
	 $scope.getSelectInfo = function(elementName){
    	var checkboxs = document.getElementsByName(elementName);
        var array = new Array(),num=0;
        for(var i=0,length = checkboxs.length;i<length;i++)
        {
            if(checkboxs[i].checked)
            {
            	array[num] = checkboxs[i].value;
                num++;
            }
        }
        return array;
    }
	
	$scope.update = function () {
		var array = $scope.getSelectInfo("checkbox");
		if(1 != array.length){
			$.alert("请选择一条信息！");
            return false;
		}
		var returnUrl = $filter('encodeURI')($scope.myCurrUrl,"VALUE");
		$location.path("/DecisionLeadersInfo/update/"+array[0]+"/"+$scope.dictId+"/"+returnUrl);
	}
	
	$scope.remove = function () {
		var array = $scope.getSelectInfo("checkbox");
		if(1 != array.length){
			$.alert("请选择一条信息！");
            return false;
		}
		$.confirm("确认要删除吗?", function(){
			$http({
				method:'post',  
			    url:srvUrl+"role/deleteMeetingLeaderById.do",
			    data: $.param({"id":array[0]})
			}).success(function(result){
				if(result.success){
					$scope.executeQueryByPage();
				}else{
					$.alert(result.result_name);
				}
			});
		});
	}
    
    if(null != $scope.paginationConf && null == $scope.paginationConf.queryObj){
		$scope.paginationConf.queryObj = {}	
    }
    $scope.executeQueryByPage = function(){
		$scope.paginationConf.currentPage = 1;
		$scope.queryByPage();
	};
    $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.queryByPage);
}]);

ctmApp.register.controller('GrouUserRepoUnitList', ['$http','$routeParams','$scope','$location','$routeParams','$filter',function ($http,$routeParams,$scope,$location,$routeParams,$filter) {
	$scope.queryRepoUnitByPage = function () {
		show_Mask();
		$http({
			method:'post',  
		    url:srvUrl+"repoUnitUser/queryByPage.do",
		    data: $.param({"page":JSON.stringify($scope.paginationConf)})
		}).success(function(result){
			if(result.success){
				$scope.repoUnitList = result.result_data.list;
				$scope.paginationConf.totalItems = result.result_data.totalItems;
			}else{
				$.alert(result.result_name);
			}
			hide_Mask();
		}).error(function(data, status, headers, config){
			hide_Mask();
		});
    };
    $scope.executeQueryRepoUnitByPage = function(){
		$scope.paginationConf.currentPage = 1;
		$scope.queryRepoUnitByPage();
	};
    $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.queryRepoUnitByPage);
    
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
    
    $scope.createGroup = function(){
		var returnUrl = $filter('encodeURI')("#/GrouUserRepoUnitList","VALUE");
        $location.path("/GrouUserRepoUnit/Create/0"+"/"+returnUrl);
    };
    
    $scope.updateGroup = function(){
    	var array = $scope.getSelectInfo("checkbox");
		if(1 != array.length){
			$.alert("请选择一条信息！");
            return false;
		}
		var returnUrl = $filter('encodeURI')("#/GrouUserRepoUnitList","VALUE");
        $location.path("/GrouUserRepoUnit/Update/"+array[0]+"/"+returnUrl);
    };
    
    $scope.deleteGroup = function(){
    	var array = $scope.getSelectInfo("checkbox");
		if(1 != array.length){
			$.alert("请选择一条信息！");
            return false;
		}
		$.confirm("确认要删除吗?", function(){
			$http({
				method:'post',  
			    url:srvUrl+"repoUnitUser/deleteById.do",
			    data: $.param({"id":array[0]})
			}).success(function(result){
				if(result.success){
					$scope.queryRepoUnitByPage();
				}else{
					$.alert(result.result_name);
				}
			});
		});
    };
}]);

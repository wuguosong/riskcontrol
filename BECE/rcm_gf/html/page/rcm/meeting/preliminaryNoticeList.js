ctmApp.register.controller('PreliminaryNoticeList', ['$http','$routeParams','$scope','$location','$routeParams','$filter', function ($http,$routeParams,$scope,$location,$routeParams,$filter) {
	$scope.oldUrl = $routeParams.url;
	
	if($scope.paginationConf.queryObj == null || $scope.paginationConf.queryObj == ''){
		$scope.paginationConf.queryObj = {};	
	}
	
	$scope.queryByPage = function () {
        $http({
			method:'post',  
		    url:srvUrl+"preliminaryNotice/queryByPage.do",
		    data: $.param({"page":JSON.stringify($scope.paginationConf)})
		}).success(function(result){
			if(result.success){
				var noticeList = result.result_data.list;
				for(var x = 0; x < noticeList.length; x++)
				{
					noticeList[x].ATTACHMENT_OBJECT = {"fileName":noticeList[x].ATTACHMENT_NAME,"filePath":noticeList[x].ATTACHMENT};
				}
				$scope.noticeList = noticeList;
				$scope.paginationConf.totalItems = result.result_data.totalItems;
			}else{
				$.alert(result.result_name);
			}
		});
    };
    
//    if(null != $scope.paginationConf && null != $scope.paginationConf.queryObj){
//		$scope.paginationConf.queryObj = {}	
//	}
    
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
    
    $scope.executeQueryByPage = function(){
		$scope.paginationConf.currentPage = 1;
		$scope.queryByPage();
	};
	
	$scope.create = function () {
		var returnUrl = $filter('encodeURI')("#/preliminaryNoticeList","VALUE");
		$location.path("/preliminaryNoticeInfo/create/0/"+returnUrl);
	}
	
	$scope.update = function () {
		var array = $scope.getSelectInfo("checkbox");
		if(1 != array.length){
			$.alert("请选择一条信息！");
            return false;
		}
		var returnUrl = $filter('encodeURI')("#/preliminaryNoticeList","VALUE");
		$location.path("/preliminaryNoticeInfo/update/"+array[0]+"/"+returnUrl);
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
			    url:srvUrl+"preliminaryNotice/deleteById.do",
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
	
    $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.queryByPage);
}]);

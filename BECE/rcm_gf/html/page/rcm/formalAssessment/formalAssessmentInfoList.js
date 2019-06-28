ctmApp.register.controller('FormalAssessmentInfoList', ['$routeParams','$http','$scope','$location', function ($routeParams,$http,$scope,$location) {
	
	var tabIndex = $routeParams.tabIndex;
	
	//控制具体显示那个tab标签页  012
	$('#myTab li:eq('+tabIndex+') a').tab('show');
	$scope.tabIndex = tabIndex;
	
	//按钮控制器
	$scope.buttonControl=srvUrl=="http://riskcontrol.bewg.net.cn/rcm-rest/";
	$scope.initData = function(){
		$scope.getForamlAssessmentList();
		$scope.getForamlAssessmentSubmitedList();
	}
	//查询正式评审基本信息列表--起草中
	$scope.getForamlAssessmentList = function(){
		if($scope.paginationConf.queryObj == null || $scope.paginationConf.queryObj == ''){
			$scope.paginationConf.queryObj = {};
		}
		$scope.paginationConf.queryObj.needCreateBy = '1';
		$http({
			method:'post',  
		    url: srvUrl + "formalAssessmentInfo/queryFormalAssessmentList.do",
		    data: $.param({"page":JSON.stringify($scope.paginationConf)})
		}).success(function(result){
			$scope.formalAssessmentList = result.result_data.list;
			$scope.paginationConf.totalItems = result.result_data.totalItems;
		});
	}
	//查询正式评审基本信息列表--已提交
	$scope.getForamlAssessmentSubmitedList = function(){
		show_Mask();
		$http({
			method:'post',  
			url: srvUrl + "formalAssessmentInfo/queryFormalAssessmentSubmitedList.do",
			data: $.param({"page":JSON.stringify($scope.paginationConfes)})
		}).success(function(result){
			$scope.formalAssessmentSubmitedList = result.result_data.list;
			$scope.paginationConfes.totalItems = result.result_data.totalItems;
			hide_Mask();
		}).error(function(data, status, headers, config){
			hide_Mask();
		});
	}
    $scope.order=function(o,v){
        if(o=="time"){
            $scope.orderby=v;
            $scope.orderbystate=null;
            if(v=="asc"){
                $("#orderasc").addClass("cur");
                $("#orderdesc").removeClass("cur");
            }else{
                $("#orderdesc").addClass("cur");
                $("#orderasc").removeClass("cur");
            }
        }else{
            $scope.orderbystate=v;
            $scope.orderby=null;
            if(v=="asc"){
                $("#orderascstate").addClass("cur");
                $("#orderdescstate").removeClass("cur");
            }else{
                $("#orderdescstate").addClass("cur");
                $("#orderascstate").removeClass("cur");
            }
        }
        $scope.getForamlAssessmentList();
    }
    // 新增操作
    $scope.createProject=function(){
        $location.path("/formalAssessmentInfo/0/1");
    };

    // 修改操作
    $scope.updateProject=function(){
        var chkObjs = $("input[type=checkbox][name=uncommittedReportCheckbox]:checked");
        if(chkObjs.length == 0){
            $.alert("请选择要修改的数据！");
            return false;
        }

        if(chkObjs.length > 1){
            $.alert("请只选择一条数据进行修改!");
            return false;
        }
        var idsStr = "";
        for(var i = 0; i < chkObjs.length; i++){
            idsStr = idsStr + chkObjs[i].value + ",";
        }
        idsStr = idsStr.substring(0, idsStr.length - 1);
        $location.path("/formalAssessmentInfo/" + idsStr + "/2");
    };

    $scope.deleteProject = function () {
        var chkObjs = $("input[type=checkbox][name=uncommittedReportCheckbox]:checked");
        if(chkObjs.length == 0){
            $.alert("请选择要删除的数据！");
            return false;
        }
        $.confirm("删除后不可恢复，确认删除吗？", function() {
            var idsStr = "";
            for(var i = 0; i < chkObjs.length; i++){
                if(i == chkObjs.length-1){
                    idsStr = idsStr + chkObjs[i].value;
                } else {
                    idsStr = idsStr + chkObjs[i].value + ",";
                }
            }
            $http({
                method:'post',
                url: srvUrl + "formalAssessmentInfoCreate/deleteProject.do",
                data: $.param({"ids":JSON.stringify(idsStr)})
            }).success(function(data){
                if(data.success){
                    $scope.initData();
                    $.alert("执行成功");
                }else{
                    $.alert(data);
                }
            });
        });
    }
    // 通过$watch currentPage和itemperPage 当他们一变化的时候，重新获取数据条目
    $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.getForamlAssessmentList);
    $scope.$watch('paginationConfes.currentPage + paginationConfes.itemsPerPage', $scope.getForamlAssessmentSubmitedList);
}]);

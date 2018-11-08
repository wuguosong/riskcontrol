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
    $scope.delete=function(){
        var chk_list=document.getElementsByName("checkbox");
        var uid = "",num=0;
        for(var i=0;i<chk_list.length;i++)
        {
            if(chk_list[i].checked)
            {
                num++;
                uid = uid+','+chk_list[i].value;
            }
        }
        if(uid!=''){
            uid=uid.substring(1,uid.length);
        }
        if(num==0){
        	$.alert("请选择其中一条或多条数据删除！");
            return false;
        }
        var obj={"_id": uid};
        var aMethed = 'formalAssessment/ProjectFormalReview/delete';
        $scope.httpData(aMethed, obj).success(
            function (data, status, headers, config) {
                if (data.result_code == "R") {
                    $.alert('报告未删除！删除申请单之前请先删除该项目下的报告单！');
                    return false;
                }else{
                    $.confirm("确定要删除？", function () {
                        $scope.httpData(aMethed, obj).success(
                            function (data, status, headers, config) {
                                $scope.getForamlAssessmentList();
                            }
                        ).error(function (data, status, headers, config) {
                            alert(status);
                        });
                    });
                }
            }
        ).error(function (data, status, headers, config) {
            alert(status);
        });
    };
    // 通过$watch currentPage和itemperPage 当他们一变化的时候，重新获取数据条目
    $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.getForamlAssessmentList);
    $scope.$watch('paginationConfes.currentPage + paginationConfes.itemsPerPage', $scope.getForamlAssessmentSubmitedList);
}]);

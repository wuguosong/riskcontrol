ctmApp.register.controller('DecisionLeadersInfoController', ['$routeParams','$http','$scope','$location','Upload','$filter',
                                                function ($routeParams,$http,$scope,$location,Upload,$filter) {
	$scope.oldUrl = $routeParams.url;
	$scope.user = {};
	$scope.user.decisionLeader = {};
	$scope.user.MEETING_LEADER_STATUS = "0";
	$scope.action =  $routeParams.action;
	$scope.dictId =  $routeParams.dictId;
	$scope.user.USER_ROLE_ID =  $scope.action == "create" ? null : $routeParams.id;
	$scope.title = $scope.action == "create" ? "新增" : "修改";
	$scope.decisionLeaderMapped={"nameField":"NAME","valueField":"VALUE"};

	$scope.initData = function(){
    	if($scope.action == "create"){
			$http({
				method:'post',  
			    url: srvUrl + "role/queryRoleUserLastIndexByCode.do",
			    data: $.param({"code":constants.DICT_DECISION_LEADERS})
			}).success(function(result){
				$scope.user.ORDER_BY = result.result_data;
			});
    	}else{
    		$scope.queryMeetingLeaderById();
    	}
    }
    
	$scope.queryMeetingLeaderById = function(){
		$http({
			method:'post',  
		    url: srvUrl + "role/queryMeetingLeaderById.do",
		    data: $.param({"user_role_id":$scope.user.USER_ROLE_ID})
		}).success(function(result){
			$scope.user = result.result_data;
			$scope.user.decisionLeader = {"NAME":$scope.user.NAME,"VALUE":$scope.user.UUID}
		});
	}
	
	$scope.save = function(){
		if($scope.user.decisionLeader == null || $scope.user.decisionLeader.VALUE == null || $scope.user.decisionLeader.VALUE == ""){
			$.alert("委员不能为空!");return false;
		}
		var ORDER_BY_INPUT = $scope.user.ORDER_BY;
    	var ORDER_BY_INPUT_NUMBER = parseInt(ORDER_BY_INPUT);
    	if (ORDER_BY_INPUT == null || ORDER_BY_INPUT == "" || ORDER_BY_INPUT == "0" || isNaN(ORDER_BY_INPUT_NUMBER) || ORDER_BY_INPUT_NUMBER.toString().length > 2)
    	{
    		$.alert("序号不能为空 或 长度不能大于2位的数字!");
    		return false;
    	}
    	$scope.user.ORDER_BY = new String($scope.user.ORDER_BY);
    	
		var url = "";
		if($scope.user.USER_ROLE_ID == null){
			url = "role/createMeetingLeader.do";
		}else{
			url = "role/updateMeetingLeader.do";
		}

		$scope.user.NAME = $scope.user.decisionLeader.NAME;
		$scope.user.UUID = $scope.user.decisionLeader.VALUE;
		
		show_Mask();
		$http({
			method:'post',  
		    url: srvUrl + url,
		    data: $.param({"json":JSON.stringify($scope.user)})
		}).success(function(result){
			hide_Mask();
			if(result.success){
				$scope.user.USER_ROLE_ID  = result.result_data;
				$.alert("保存成功!");
			}else{
				$.alert(result.result_name);
			}
		}).error(function(data,status,headers,config){
			hide_Mask();
        	$.alert(status);
        });
	}
	$scope.initData();
}]);
ctmApp.register.controller('ProfessionTeamAdd', ['$http','$scope','$location','$routeParams',
    function ($http,$scope,$location,$routeParams) {
	//初始化数据
	$scope.oldUrl = $routeParams.url;
	var action = $routeParams.action;
	var uid=$routeParams.uuid;
	$scope.pro={};
	$scope.pro.uuid = uid;
	$scope.reviewLeader={"nameField":"NAME","valueField":"VALUE"};
	$scope.memberName={"nameField":"NAME","valueField":"VALUE"};
	
	//新增
	$scope.save = function(){
			if($scope.pro.team.REVIEW_TYPE ==null || $scope.pro.team.REVIEW_TYPE==""){
				$.alert("部门名称不能为空");
				return false;
			}
			if($scope.pro.team.ORDERNUM ==null || $scope.pro.team.ORDERNUM==""){
				$.alert("排列序号不能为空");
				return false;
			}
			if($scope.pro.review_leader ==null || $scope.pro.review_leader==""){
				$.alert("组长名称不能为空");
				return false;
			}
			if($scope.pro.review_team_membername ==null || $scope.pro.review_team_membername==""){
				$.alert("组员名称不能为空");
				return false;
			}
			if($scope.pro.team.STATUS ==null || $scope.pro.team.STATUS==""){
				$.alert("请选择是否启用");
				return false;
			}
		     var url;
		     if(action=="Update" ){
		    	 url = 'profession/updateTeam.do';
	         } else {
	        	 url = 'profession/addTeam.do';
	         }
		     
	         $http({
		 			method:'post',  
		 			url:srvUrl+url, 
		 		    data: $.param({"profession":JSON.stringify(angular.copy($scope.pro))})
		 		}).success(function(result){
		 			if(result.success){
		 				$location.path("ProfessionList");
		 			}else{
		 				$.alert(result.result_name);
		 			}
		 		});
	}
    //查询一个用户
	  $scope.getTeamByID = function (id) {
		   var  url = 'profession/getTeamByID.do';
	        $http({
				method:'post',  
			    url: srvUrl + url,
			    data: $.param({"teamId":uid})
			}).success(function(data){
				$scope.pro = data.result_data;
                var arrlead=data.result_data.team;
                var arrIdname=data.result_data.teamItem;
                var arrID=[],arrName=[];
                $scope.pro.review_leader = {"NAME":arrlead.REVIEW_LEADER,"VALUE":arrlead.REVIEW_LEADER_ID};
                $scope.pro.review_team_membername = [];
                for(var i=0;i<arrIdname.length;i++){
                	var a = {};
                	a.NAME = arrIdname[i].REVIEW_TEAM_MEMBERNAME;
                	a.VALUE = arrIdname[i].REVIEW_TEAM_MEMBERID;
                	$scope.pro.review_team_membername.push(a);
               }
			});
      };
	if(action=="Update"){
        $scope.getTeamByID(uid);
    }
 }]);


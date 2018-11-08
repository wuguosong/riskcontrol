/**
 * Created by gl on 2016/8/4.
 */

ctmApp.register.controller('ProfessionList', ['$http','$scope','$location','$routeParams',
function ($http,$scope,$location,$routeParams){
	
	//初始化列表数据
	var id=$routeParams.id;
	$scope.initData = function(){
		$scope.getProfessionList();
	}
	
	//查询所有的组，不分启用禁用
	$scope.getProfessionList = function(){
		$http({
			method:'post',  
		    url: srvUrl + "profession/queryAllTeams.do",
		    data: $.param({"page":JSON.stringify($scope.paginationConf)})
		}).success(function(result){
			$scope.professionList = result.result_data.list;
			$scope.paginationConf.totalItems = result.result_data.totalItems;
		});
	}
	
	//新增
	$scope.Create=function(id){
		var url=window.btoa(encodeURIComponent(escape("#/ProfessionList")))
        $location.path("/ProfessionTeamAdd/Create/"+id+"/"+url);
    };
    //修改
    $scope.updateTeam=function (){
    	var chks = $("input[type=checkbox][name=checkbox]:checked");
    	if(chks.length != 1){
    		$.alert("请选择其中一条数据编辑！");
    		return;
    	}
    	var uid = chks.get(0).value;
    	var url=window.btoa(encodeURIComponent(escape("#/ProfessionList")))
    	$location.path("ProfessionTeamAdd/Update/"+uid+"/"+url);
    }
    //禁用
    $scope.disableTeam=function (){
    	var chks = $("input[type=checkbox][name=checkbox]:checked");
    	if(chks.length != 1){
    		$.alert("请选择要删除禁用的数据！");
    		return;
    	}
    	var uid = chks.get(0).value;
    	  $http({
  			method:'post',  
  		    url: srvUrl + "profession/updateTeamSatusById.do",
  		    data: $.param({"teamId":uid})
  		}).success(function(result){
  			if(result.success){
  				$scope.initData();
  				$.alert(result.result_name);
 			}else{
 				$.alert(result.result_name);
 			}
  		});
    }
	//查看所有组员
    $scope.viewAll= function(id){
    	$http({
			method:'post',  
		    url: srvUrl + "profession/queryMembersByTeamId.do",
		    data: $.param({"teamId":id})
		}).success(function(result){
			$scope.teamUserList  = result.result_data;
		});
    }
    $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.getProfessionList);
}]);

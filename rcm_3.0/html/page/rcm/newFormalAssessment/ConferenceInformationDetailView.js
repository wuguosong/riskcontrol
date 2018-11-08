ctmApp.register.controller('ConferenceInformationDetailView', ['$http','$scope','$location','$routeParams', 
 function ($http,$scope,$location,$routeParams) {
	//初始化数据
	var objId = $routeParams.id;
	$scope.oldUrl = $routeParams.url;
	$scope.initData = function(){
		$scope.formalDivisionMapped={"nameField":"name","valueField":"value"};
		$scope.formalInvestmentMapped={"nameField":"name","valueField":"value"};
		$scope.pfrAgenda={"nameField":"name","valueField":"value"};
		$scope.pfrContacts={"nameField":"name","valueField":"value"};
		$scope.getFormalAssessmentByID(objId);
		$scope.getConferenceInfoByID(objId);
	    $scope.nameValue=[{name:'A级（困难评审)',value:1},{name:'B级（中级评审)',value:2},{name:'C级（简单评审)',value:3}];
	}
	
	
	//根据businessid查询正式评审信息
	$scope.getFormalAssessmentByID=function(id){
		var  url = 'formalAssessmentInfo/getFormalAssessmentByID.do';
		$http({
			method:'post',  
		    url:srvUrl+url, 
		    data: $.param({"id":id})
		}).success(function(data){
			$scope.pfr  = data.result_data.formalAssessmentMongo;
			var pt1NameArr=[];
			var pt1=$scope.pfr.apply.serviceType;
			if(null!=pt1 && pt1.length>0){
				for(var i=0;i<pt1.length;i++){
					pt1NameArr.push(pt1[i].VALUE);
				}
				$scope.pfr.apply.serviceType=pt1NameArr.join(",");
			}
		});
	}
	//正式评审参会信息详情
	$scope.getConferenceInfoByID=function(id){
		var  url = 'meeting/queryConferenceInfomationById.do';
		$http({
			method:'post',  
		    url:srvUrl+url, 
		    data: $.param({"formalId":id})
		}).success(function(data){
			$scope.meeting= data.result_data;
		});
	}
	
	$scope.initData();
	 
}]);


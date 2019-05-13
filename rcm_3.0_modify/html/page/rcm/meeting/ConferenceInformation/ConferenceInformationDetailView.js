ctmApp.register.controller('ConferenceInformationDetailView', ['$http','$scope','$location','$routeParams', 
 function ($http,$scope,$location,$routeParams) {
	//初始化数据
	var objId = $routeParams.id;
	$scope.oldUrl = $routeParams.url;
	$scope.flag = $routeParams.flag;
	$scope.initData = function(){
		$scope.formalDivisionMapped={"nameField":"name","valueField":"value"};
		$scope.formalInvestmentMapped={"nameField":"name","valueField":"value"};
		$scope.projectAgenda={"nameField":"name","valueField":"value"};
		$scope.projectContacts={"nameField":"name","valueField":"value"};
		$scope.getFormalAssessmentByID(objId);
		$scope.getConferenceInfoByID(objId);
	}
	
	
	//根据businessid查询正式评审信息
	$scope.getFormalAssessmentByID=function(id){
		var  url = 'formalAssessmentInfoCreate/getProjectByID.do';
		$http({
			method:'post',  
		    url:srvUrl+url, 
		    data: $.param({"id":id})
		}).success(function(data){
			$scope.project  = data.result_data.mongoData;
			var pt1NameArr=[];
			var pt1=$scope.project.apply.serviceType;
			if(null!=pt1 && pt1.length>0){
				for(var i=0;i<pt1.length;i++){
					pt1NameArr.push(pt1[i].VALUE);
				}
				$scope.project.apply.serviceType=pt1NameArr.join(",");
			}
		});
	};
	//正式评审参会信息详情
	$scope.getConferenceInfoByID=function(id){
        var  url = '';
        if($scope.flag == 1){
            url = 'meeting/queryConferenceInfomationById.do';
        } else {
            url = 'preMeetingInfo/queryMeetingInfoById.do';
        }
		$http({
			method:'post',  
		    url:srvUrl+url, 
		    data: $.param({"formalId":id})
		}).success(function(data){
			$scope.meeting= data.result_data

			// 处理需要显示的数据
            var mdNameArr=[],miNameArr=[];

            var md=$scope.meeting.division;
            if(null!=md && md.length>0){
                for(var j=0;j<md.length;j++){
                    mdNameArr.push(md[j].name);
                }
                $scope.meeting.division=mdNameArr.join(",");
            }
            var mi=$scope.meeting.investment;
            if(null!=mi && mi.length>0){
                for(var j=0;j<mi.length;j++){
                    miNameArr.push(mi[j].name);
                }
                $scope.meeting.investment=miNameArr.join(",");
            }
		});
	};
	
	$scope.initData();
	 
}]);


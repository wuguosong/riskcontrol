ctmApp.register.controller('IndividualTable', ['$http','$scope','$location', function ($http,$scope,$location) {
	
	$scope.initData = function(){
		$scope.initializeCurrUserTaskInfo();
		$scope.initNotificationFlatformInfo();
		$scope.queryMeetingNoticeTop();
	}
	
	//初始化获取个人任务信息
	$scope.initializeCurrUserTaskInfo = function(){
		$http({
			method:'post',  
		    url:srvUrl+"workFlow/initializeCurrUserTaskInfo.do"
		}).success(function(result){
			if(result.success){
				var resultData = result.result_data;
				$scope.now = resultData.now.substring(0,10);
				var d=new Date($scope.now);
				var weekDay = ["星期天", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"];
				$scope.week = weekDay[d.getDay()];
				$scope.myTaskCount = resultData.myTaskCount;
				$scope.overTaskCount = resultData.overTaskCount;
//				$scope.completedTaskCount = resultData.completedTaskCount;
				
				$scope.myTaskList = resultData.myTaskList;
				$scope.overTaskList = resultData.overTaskList;
//				$scope.completedTaskList = resultData.completedTaskList;
			}else{
				$.alert(result.result_name);
			}
		});
	};
    
	//初始化公告平台信息
    $scope.initNotificationFlatformInfo = function(){
    	 $http({
  			method:'post',  
  		    url: srvUrl + "notificationFlatform/queryNotifTop.do"
  		}).success(function(result){
  			 if(result.success){
  				$scope.notificationFlatform = result.result_data;
  	  			 for (var int = 0; int < $scope.notificationFlatform.length; int++) {
  	  				 var topic = $scope.notificationFlatform[int].TOPIC;
  	  				 if(topic.length > 17){//为避免页面样式变形，如果公告标题长度超过17位，则以'...'替代标题17位之后的文字
  	  					$scope.notificationFlatform[int].TOPIC = topic.substring(0,17) + "...";
  	  				 }
  				}
  			 }else{
  				 $.alert(result.result_name);
  			 }
  		}).error(function(data,status,headers, config){
  			$.alert(status);
  		});
    }
    //初始化会议通知
    $scope.queryMeetingNoticeTop = function(){
    	$http({
			method:'post',  
		    url: srvUrl + "preliminaryNotice/queryTop.do"
		}).success(function(result){
			 $scope.meetingNoticeList = result.result_data;
		}).error(function(data,status,headers, config){
			$.alert(status);
		});
    }
    $scope.initData();
}]);
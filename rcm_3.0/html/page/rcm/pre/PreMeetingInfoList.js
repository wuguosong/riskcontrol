/********
 * Created by shaosimin on 17/9/6.
 * 投标评审参会信息
 *********/
ctmApp.register.controller('PreMeetingInfoList', ['$http','$scope','$location','$routeParams',
function ($http,$scope,$location,$routeParams) {
	$scope.tabIndex = $routeParams.tabIndex;
	//初始化列表数据
	$scope.initData = function(){
		$scope.getInformationList();
		$scope.getInformationListed();
		$scope.getNotMeetingList();
	}
	//查询参会信息列表(未处理)
	$scope.getInformationList = function(){
		$http({
			method:'post',  
		    url: srvUrl + "preMeetingInfo/queryInformationList.do",
		    data: $.param({"page":JSON.stringify($scope.paginationConf)})
		}).success(function(result){
			$scope.preMeetingInfo = result.result_data.list;
			$scope.paginationConf.totalItems = result.result_data.totalItems;
		});
	}
	//查询参会信息列表(已处理)
	$scope.getInformationListed = function(){
		$http({
			method:'post',  
		    url: srvUrl + "preMeetingInfo/queryInformationListed.do",
		    data: $.param({"page":JSON.stringify($scope.paginationConfes)})
		}).success(function(result){
			$scope.preMeetingInfoed = result.result_data.list;
			$scope.paginationConfes.totalItems = result.result_data.totalItems;
		});
	}
	//无需上会信息列表
	$scope.getNotMeetingList = function(){
		$http({
			method:'post',  
		    url: srvUrl + "preMeetingInfo/queryNotMeetingList.do",
		    data: $.param({"page":JSON.stringify($scope.paginationConfNotMeeting)})
		}).success(function(result){
			$scope.needMeeting = result.result_data.list;
			$scope.paginationConfNotMeeting.totalItems = result.result_data.totalItems;
		});
	}
	$scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.getInformationList);
	$scope.$watch('paginationConfes.currentPage + paginationConfes.itemsPerPage', $scope.getInformationListed);
	$scope.$watch('paginationConfNotMeeting.currentPage + paginationConfNotMeeting.itemsPerPage', $scope.getNotMeetingList);
	$scope.paginationConfNotMeeting = {
            lastCurrentTimeStamp:'',
            currentPage: 1,
            totalItems: 0,
            itemsPerPage: 10,
            pagesLength: 10,
            perPageOptions: [10, 20, 30, 40, 50],
            onChange: function(){
            }
        };
	//新增会议信息
	$scope.showSubmitModal = function(){
		var checkLength = $(".px:checked").length;
		if(checkLength ==0){
			$.alert("请选择其中一条数据！");
	        return false;
		}else if(checkLength > 1){
			$.alert("只能选择其中一条数据！");
	        return false;
		}else{
			var businessId = $(".px:checked").val();
			var url=window.btoa(encodeURIComponent(escape("#/PreMeetingInfoList/0")))
			$location.path("/PreMeetingInfoCreate/"+businessId+"/"+url);
		}
	}
	//无需上会
	$scope.noNeedMeeting = function(){
		  var hyxxSelected = $("input[name=checkbox]:checked");
		  if(hyxxSelected.length <1){
			  $.alert("请选择无需上会的项目！");
			  return false;
	      }
		  
	        var hyxxIds = "";
        	for(var i=0; i < hyxxSelected.length; i++){
        		for(var m = 0; m < $scope.preMeetingInfo.length; m++){
        			if(hyxxSelected[i].value == $scope.preMeetingInfo[m].BUSINESSID 
        					&& $scope.preMeetingInfo[m].WF_STATE=='2'){
        				hyxxIds = hyxxIds + hyxxSelected[i].value + ",";
        			}
        		}
        	}
        	if(hyxxIds == ""){
        		$.alert("您选中的投标评审项目流程全部未结束，不能执行该操作！");
        		return false;
        	}
        	$.confirm("确认选中项目无需上会吗？",function(){
				//点击确定执行的程序
        		if(hyxxIds.length > 0){
            		hyxxIds = hyxxIds.substring(0, hyxxIds.length-1);
            	}
				$http({
					method:'post',  
					url: srvUrl + "preMeetingInfo/noNeedMeeting.do",
					data: $.param({
						"businessId": hyxxIds,
						"stage": $scope.stage,
						"need_meeting": $scope.need_meeting
					})
				}).success(function(result){
					 $.alert(result.result_name);
					 $scope.initData();
				});
			});	        	
	        
	}
	$scope.alert=function(){
		$.alert("此项目为无需上会项目，不能进行此操作");
	}
	$scope.initData();
}]);

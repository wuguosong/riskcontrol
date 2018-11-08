/********
 * Created by shaosimin on 17/7/5.
 * 正式评审会议信息
 *********/
ctmApp.register.controller('ConferenceInformation', ['$http','$scope','$location','$routeParams',
function ($http,$scope,$location,$routeParams) {
	$scope.tabIndex = $routeParams.tabIndex;
	//初始化列表数据
	$scope.initData = function(){
		$scope.getInformationList();
		$scope.getInformationListed();
	}
	//查询会议信息列表(未处理)
	$scope.getInformationList = function(){
		$http({
			method:'post',  
		    url: srvUrl + "information/queryInformationList.do",
		    data: $.param({"page":JSON.stringify($scope.paginationConf)})
		}).success(function(result){
			$scope.formalAssessmentList = result.result_data.list;
			$scope.paginationConf.totalItems = result.result_data.totalItems;
		});
	}
	//查询会议信息列表(已处理)
	$scope.getInformationListed = function(){
		show_Mask();
		$http({
			method:'post',  
		    url: srvUrl + "information/queryInformationListed.do",
		    data: $.param({"page":JSON.stringify($scope.paginationConfes)})
		}).success(function(result){
			$scope.formalAssessmentListed = result.result_data.list;
			$scope.paginationConfes.totalItems = result.result_data.totalItems;
			hide_Mask();
		}).error(function(data, status, headers, config){
			hide_Mask();
		});
	}
	$scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.getInformationList);
	$scope.$watch('paginationConfes.currentPage + paginationConfes.itemsPerPage', $scope.getInformationListed);
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
			var url=window.btoa(encodeURIComponent(escape("#/ConferenceInformation/0")))
			$location.path("/ConferenceInformationCreate/"+businessId+"/"+url);
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
        		for(var m = 0; m < $scope.formalAssessmentList.length; m++){
        			if(hyxxSelected[i].value == $scope.formalAssessmentList[m].BUSINESSID 
        					&& $scope.formalAssessmentList[m].WF_STATE=='2'){
        				hyxxIds = hyxxIds + hyxxSelected[i].value + ",";
        			}
        		}
        	}
        	if(hyxxIds == ""){
        		$.alert("您选中的正式评审项目流程全部未结束，不能执行该操作！");
        		return false;
        	}
        	$.confirm("确认选中项目无需上会吗？",function(){
				//点击确定执行的程序
        		if(hyxxIds.length > 0){
            		hyxxIds = hyxxIds.substring(0, hyxxIds.length-1);
            	}
				$http({
					method:'post',  
					url: srvUrl + "information/noNeedMeeting.do",
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

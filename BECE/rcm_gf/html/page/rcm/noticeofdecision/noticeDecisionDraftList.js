ctmApp.register.controller('NoticeDecisionDraftList', ['$http','$scope','$location','$routeParams', function ($http,$scope,$location,$routeParams) {
	$scope.orderby='desc';
	$scope.tabIndex = $routeParams.tabIndex;
	
	
	//控制具体显示那个tab标签页  012
	$('#myTab li:eq('+$scope.tabIndex+') a').tab('show');
	
//    $scope.paginationConf.queryObj = {userId:$scope.credentials.UUID};
//    $scope.paginationConfes.queryObj = {userId:$scope.credentials.UUID};
//    $scope.paginationConfMeeting.queryObj = {userId:$scope.credentials.UUID};
    
    //查询可以起草的决策通知书
    $scope.listBusinessUnitCommit = function () {
        var url = 'noticeDecisionDraftInfo/queryFormalForCreate.do';
        $http({
        	method:'post',
        	url: srvUrl + url,
        }).success(function (data) {
                $scope.pprs = data.result_data;
        });
    };
    
    // 查询决策通知书--待提交状态
    $scope.queryApplyList=function(){
    	$http({
			method:'post',  
		    url: srvUrl + "noticeDecisionDraftInfo/queryApplyList.do",
		    data: $.param({"page":JSON.stringify($scope.paginationConf)})
		}).success(function(result){
			var data = result.result_data;
			$scope.nod1 = result.result_data.list;
			$scope.paginationConf.totalItems = result.result_data.totalItems;
		});
    };
    //查询决策通知书---未上会信息
    $scope.notMeetingList=function(){
    	$http({
			method:'post',  
		    url: srvUrl + "noticeDecisionDraftInfo/notMeetingList.do",
		    data: $.param({"page":JSON.stringify($scope.paginationConfMeeting)})
		}).success(function(result){
			var data = result.result_data;
			$scope.nod2 = result.result_data.list;
			$scope.paginationConfMeeting.totalItems = result.result_data.totalItems;
		});
    };
    // 查询决策通知书--已提交状态
    $scope.queryApplyedList=function(){
    	show_Mask();
    	$http({
			method:'post',  
		    url: srvUrl + "noticeDecisionDraftInfo/queryApplyedList.do",
		    data: $.param({"page":JSON.stringify($scope.paginationConfes)})
		}).success(function(result){
			hide_Mask();
			var data = result.result_data;
			$scope.nod3 = result.result_data.list;
			$scope.paginationConfes.totalItems = result.result_data.totalItems;
		});
    };
    
    // 通过$watch currentPage和itemperPage 当他们一变化的时候，重新获取数据条目
    $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage',  $scope.queryApplyList);
    $scope.$watch('paginationConfMeeting.currentPage + paginationConfMeeting.itemsPerPage',  $scope.notMeetingList);
    $scope.$watch('paginationConfes.currentPage + paginationConfes.itemsPerPage',  $scope.queryApplyedList);
    
    $scope.paginationConfMeeting = {
            lastCurrentTimeStamp:'',
            currentPage: 1,
            totalItems: 0,
            itemsPerPage: 10,
            pagesLength: 10,
            perPageOptions: [10, 20, 30, 40, 50],
            onChange: function(){
            }
        };
    
    $scope.order=function(v){
        $scope.paginationConf.queryObj.ASCDESC= v;
        if(v=='asc'){
            $("#orderasc").addClass("cur");
            $("#orderdesc").removeClass("cur");
        }else{
            $("#orderdesc").addClass("cur");
            $("#orderasc").removeClass("cur");
        }
        $scope.initDefaultData();
    }
    // 删除操作
    $scope.Delete = function () {
    	var chks = $("input[type=checkbox][name=chk_start]:checked");
    	if(chks.length < 1){
    		$.alert("请选择要删除的数据！");
    		return;
    	}
    	var ids = "";
    	for(var i = 0; i < chks.length; i++){
    		ids = ids + chks[i].value+',';
    	}
    	ids = ids.substring(0, ids.length - 1);
        $http({
			method:'post',  
		    url: srvUrl + "noticeDecisionDraftInfo/delete.do",
		    data: $.param({"formalIds":ids})
		}).success(function(result){
            alert("删除成功！");
            $scope.queryApplyedList();
            $scope.queryApplyList();
		});
    };
    //修改
    $scope.update = function () {
    	var chks = $("input[type=checkbox][name=chk_start]:checked");
    	if(chks.length != 1){
    		$.alert("请选择其中一条数据编辑！");
    		return;
    	}
    	var uid = chks.get(0).value;
        $location.path("/NoticeDecisionDraftDetail/Update/" + uid);
    }
    //完成
    $scope.complete = function () {    
    	$.alert("该功能仅当选中项目已经过会才能使用，请确认选中的项目已经过会！");
		var chks = $("input[type=checkbox][name=chk_start]:checked");
    	if(chks.length != 1){
    		$.alert("请选择其中一条数据编辑！");
    		return;
    	}
    	var uid = chks.get(0).value;
    	var returnUrl = $filter("encodeURI")("#/NoticeDecisionDraftList");
    	$location.path("/NoticeDecisionDraftCompleteDetail/Complete/" + uid +"/"+returnUrl);
    }
    //导出
    $scope.import=function(){
        var aMethed =  'formalAssessment/NoticeOfDecision/noticeOfDecisionReport';
        $scope.httpData(aMethed,$scope.credentials.UUID).success(
            function (data) {
                if(data.result_code=="S"){
                    var file=data.result_data.filePath;
                    var fileName=data.result_data.fileName;
                    window.location.href = srvUrl+"common/RcmFile/downLoad?filePath="+encodeURI(file)+"&fileName="+encodeURI(fileName);
                }
            }

        ).error(function (data, status, headers, config) {
            alert(status);
        });
    }
    
}]);

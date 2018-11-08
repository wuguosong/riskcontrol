ctmApp.register.controller('NoticeDecisionList', ['$http','$scope','$location', function ($http,$scope,$location) {
    $scope.orderby='desc';
    $scope.paginationConf.queryObj = {userId:$scope.credentials.UUID};
    $scope.paginationConfes.queryObj = {userId:$scope.credentials.UUID};
    
    $scope.listBusinessUnitCommit = function () {
        var aMethed = 'common/commonMethod/selectProjectFormalForNoticeof';
        $scope.httpData(aMethed).success(
            function (data, status, headers, config) {
                $scope.pprs = data.result_data;
            }
        ).error(function (data, status, headers, config) {
            alert(status);
        });
    };
    
    // 查询决策通知书--待提交状态
    $scope.queryApplyList=function(){
    	$http({
			method:'post',  
		    url: srvUrl + "noticeDecisionInfo/queryApplyList.do",
		    data: $.param({"page":JSON.stringify($scope.paginationConf)})
		}).success(function(result){
			var data = result.result_data;
			$scope.nod1 = result.result_data.list;
			$scope.paginationConf.totalItems = result.result_data.totalItems;
		});
    };
    // 查询决策通知书--已提交状态
    $scope.queryApplyedList=function(){
    	$http({
			method:'post',  
		    url: srvUrl + "noticeDecisionInfo/queryApplyedList.do",
		    data: $.param({"page":JSON.stringify($scope.paginationConfes)})
		}).success(function(result){
			var data = result.result_data;
			$scope.nod2 = result.result_data.list;
			$scope.paginationConfes.totalItems = result.result_data.totalItems;
		});
    };
    
    // 通过$watch currentPage和itemperPage 当他们一变化的时候，重新获取数据条目
    $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage',  $scope.queryApplyList);
    $scope.$watch('paginationConfes.currentPage + paginationConfes.itemsPerPage',  $scope.queryApplyedList);
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

        var chk_list = document.getElementsByName("checkbox");
        var uid = "", num = 0;
        for (var i = 0; i < chk_list.length; i++) {
            if (chk_list[i].checked) {
                num++;
                uid = uid + ',' + chk_list[i].value;
            }
        }
        if (uid != '') {
            uid = uid.substring(1, uid.length);
        }
        if (num == 0) {
            alert("请选择其中一条或多条数据删除！");
            return false;
        }
        if (num > 1) {
        	alert("只能选择其中一条数据进行删除！");
        	return false;
        }
        var obj = {"_id": uid};
        var aMethed = 'formalAssessment/NoticeOfDecision/delete';
        $http({
			method:'post',  
		    url: srvUrl + "noticeDecisionInfo/delete.do",
		    data: $.param({"projectid":uid})
		}).success(function(result){
            alert("删除成功！");
            $scope.queryApplyedList();
            $scope.queryApplyList();
		});
    };
    $scope.update = function (state) {
        var chk_list = document.getElementsByName("checkbox");
        var uid = "", num = 0;
        for (var i = 0; i < chk_list.length; i++) {
            if (chk_list[i].checked) {
                num++;
                uid = uid + ',' + chk_list[i].value;
            }
        }
        if (uid != '') {
            uid = uid.substring(1, uid.length);
        }
        if (num == 0) {
            alert("请选择其中一条或多条数据编辑！");
            return false;
        }
        if (num > 1) {
            alert("只能选择其中一条数据进行编辑！");
            return false;
        } else {
            $location.path("/NoticeDecisionDetail/Update/" + uid);
        }
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

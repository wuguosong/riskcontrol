ctmApp.register.controller('MeetingInfoList', ['$http','$scope','$location','$routeParams', function ($http,$scope,$location,$routeParams) {
	//通报事项分页
	$scope.pageConf = {
        lastCurrentTimeStamp:'',
        currentPage: 1,
        totalItems: 0,
        itemsPerPage: 10,
        pagesLength: 10,
        perPageOptions: [10, 20, 30, 40, 50],
        onChange: function(){
        }
    };
	
	$scope.queryTbsxListByPage = function(){
		$http({
			method:'post',  
		    url:srvUrl+"meeting/queryTbsxListByPage.do", 
		    data: $.param({"page":JSON.stringify($scope.pageConf)})
		}).success(function(result){
			if(result.success){
				$scope.bulletins = result.result_data.list;
                $scope.pageConf.totalItems = result.result_data.totalItems;
			}else{
				$.alert(result.result_name);
			}
		});
	};
	
	$scope.orderby=-1;
    $scope.queryList=function(){
        if($scope.paginationConf.currentPage === 1){
            //如果当前页为第一页，则此时请求$scope.ListAll();不会触发两次操作
            $scope.ListAll();
        }else{
            //如果当前页不是第一页，则可以通过修改currentPage来触发$scope.ListAll();
            $scope.paginationConf.currentPage = 1;
        }
    };
    $scope.ListAll=function(){
        $scope.conf={currentPage:$scope.paginationConf.currentPage,itemsPerPage:$scope.paginationConf.itemsPerPage,
            user_id:$scope.credentials.UUID,'ORDERVAL':$scope.orderby,'projectName':$scope.projectName,'projectForm':$scope.projectForm};
        var url =  'projectPreReview/Meeting/getAll';
        $scope.httpData(url,$scope.conf).success(
            function (data, status, headers, config) {
                $scope.meeting = data.result_data.list;
                $scope.paginationConf.totalItems = data.result_data.totalItem;
               // $scope.serviceType = data.result_data.serviceType;
            }
        ).error(function (data, status, headers, config) {
            alert(status);
        });
    };
    $scope.order=function(v){
        $scope.orderby=v;
        if(v==1){
            $("#orderasc").addClass("cur");
            $("#orderdesc").removeClass("cur");
        }else{
            $("#orderdesc").addClass("cur");
            $("#orderasc").removeClass("cur");
        }
        $scope.ListAll();
    }
    $scope.orderPast=function(v){
        $scope.orderby=v;
        if(v==1){
            $("#orderascPast").addClass("cur");
            $("#orderdescPast").removeClass("cur");
        }else{
            $("#orderdescPast").addClass("cur");
            $("#orderascPast").removeClass("cur");
        }
        $scope.ListMeetingAll();
    }
    $scope.queryMeetingList=function(){
        if($scope.paginationConfes.currentPage === 1){
            //如果当前页为第一页，则此时请求$scope.ListAll();不会触发两次操作
            $scope.ListMeetingAll();
        }else{
            //如果当前页不是第一页，则可以通过修改currentPage来触发$scope.ListAll();
            $scope.paginationConfes.currentPage = 1;
        }
    };
    $scope.ListMeetingAll=function(){
        $scope.confes={currentPage:$scope.paginationConfes.currentPage,itemsPerPage:$scope.paginationConfes.itemsPerPage,
            'ORDERVAL':$scope.orderby,'projectName':$scope.projectName,'meetingTime':$scope.meetingTime};
        var aMethed = 'projectPreReview/Meeting/getMeetingAll';
        $scope.httpData(aMethed,$scope.confes).success(
            function (data, status, headers, config) {
                $scope.meetingcall = data.result_data.list;
                $scope.paginationConfes.totalItems = data.result_data.totalItem;
            }
        ).error(function (data, status, headers, config) {
            alert(status);
        });
    };
    $scope.toApply =function(uid){
        var chk_list=document.getElementsByName("checkbox");
        var uid = "",num=0;
        for(var i=0;i<chk_list.length;i++)
        {
            if(chk_list[i].checked)
            {
                num++;
                uid = uid+','+chk_list[i].value;
            }
        }
        if(uid!=''){
            uid=uid.substring(1,uid.length);
        }
        
        var tbsxSelected = $("input[name=tbsxChk]:checked");
        var tbsxIds = "";
        if(tbsxSelected.length > 0){
        	for(var i=0; i < tbsxSelected.length; i++){
        		tbsxIds = tbsxIds + tbsxSelected[i].value + ",";
        	}
        }
        if(tbsxIds.length > 0){
        	tbsxIds = tbsxIds.substring(0, tbsxIds.length-1);
        }
        if(num==0 && tbsxIds == ""){
        	$.alert("请选择需要通知上会的项目！");
            return false;
        }else  {
        	if(tbsxIds != ""){
        		uid = uid + "_" + tbsxIds;
        	}
            $location.path("/MeetingApply/"+uid);
        }
    }
    $scope.update=function(state){
        var chk_list=document.getElementsByName("checkbox");
        var uid = "",num=0;
        for(var i=0;i<chk_list.length;i++)
        {
            if(chk_list[i].checked)
            {
                num++;
                uid = uid+','+chk_list[i].value;
            }
        }
        if(uid!=''){
            uid=uid.substring(1,uid.length);
        }
        if(num==0){
        	$.alert("请选择其中一条或多条数据编辑！");
            return false;
        }
        if(num>1){
        	$.alert("只能选择其中一条数据进行编辑！");
            return false;
        }else{
            $location.path("/ProjectFormalReviewDetail/Update/"+uid);
        }
    }
    $scope.$watch('pageConf.currentPage + pageConf.itemsPerPage', $scope.queryTbsxListByPage);
    $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.ListAll);
    $scope.$watch('paginationConfes.currentPage + paginationConfes.itemsPerPage',  $scope.ListMeetingAll);
    $scope.sysAndMeetAdmin=function(){
        var aMethed = 'common/commonMethod/getSystemAndMeetAdmin';
        $scope.user={user_id:$scope.credentials.UUID};
        $scope.httpData(aMethed,$scope.user).success(
            function (data, status, headers, config) {
                $scope.userAdmin = data.result_data;
            }
        ).error(function (data, status, headers, config) {
            alert(status);
        });
    };
    $scope.sysAndMeetAdmin();
    $scope.import=function(){
        var aMethed =  'projectPreReview/Meeting/meetingListReport';
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
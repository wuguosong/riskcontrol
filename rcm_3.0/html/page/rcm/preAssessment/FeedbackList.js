ctmApp.register.controller('FeedbackList', ['$http','$scope','$location','$routeParams',
function ($http,$scope,$location,$routeParams) {
	
	if($scope.paginationConf.queryObj==null || $scope.paginationConf.queryObj == ''){
		$scope.paginationConf.queryObj = {};
	}
	$scope.pprs={};
        $scope.ppr={};
        $scope.orderby=-1;
        //查义所有的操作
        $scope.listProjectName=function(){
            $scope.conf={currentPage:$scope.paginationConf.currentPage,pageSize:$scope.paginationConf.itemsPerPage,queryObj:{'WF_STATE':'2'}};
            var url = 'projectPreReview/ProjectPreReview/getAll';
            $scope.httpData(url,$scope.conf).success(function(data){
                // 变更分页的总数
                $scope.pprs  = data.result_data.list;

            });
        };

        $scope.ListAll=function(){
        	
            $scope.confPam={currentPage:$scope.paginationConf.currentPage,itemsPerPage:$scope.paginationConf.itemsPerPage,
                'ORDERVAL':$scope.orderby, 'projectName':$scope.paginationConf.queryObj.projectName,'projectOpentime':$scope.paginationConf.queryObj.projectOpentime,'tenderResults':$scope.paginationConf.queryObj.tenderResults};
            var url =  'projectPreReview/Feedback/getAll';
            $scope.httpData(url,$scope.confPam).success(
                function (data, status, headers, config) {
                    $scope.ppr = data.result_data.list;
                    $scope.paginationConf.totalItems = data.result_data.totalItem;
                }
            ).error(function (data, status, headers, config) {
                alert(status);
            });
        };
    $scope.order=function(v){

            $scope.orderby=v;
            if(v=="1"){
                $("#orderasc").addClass("cur");
                $("#orderdesc").removeClass("cur");
            }else{
                $("#orderdesc").addClass("cur");
                $("#orderasc").removeClass("cur");
            }
        $scope.ListAll();
    }
        $scope.create=function(){
            $location.path("/AuctionResultFeedback/Create/0");
        }
            //删除操作
            $scope.Delete=function(){

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
                    	$.alert("请选择其中一条或多条数据删除！");
                        return false;
                    }
                    var obj={"_id": uid};
                    var aMethed = 'projectPreReview/Feedback/deleteFeedback';
                    $scope.httpData(aMethed,obj).success(
                        function (data, status, headers, config) {
                            $scope.ListAll();
                            $.alert("删除成功！");
                        }
                    ).error(function (data, status, headers, config) {
                        alert(status);
                    });
            };
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
                $location.path("/AuctionResultFeedback/Update/"+uid);
            }
        }

        //通过$watch currentPage和itemperPage 当他们一变化的时候，重新获取数据条目
        $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.ListAll);
        $scope.import=function(){
            var url = 'projectPreReview/Feedback/feedbackReport';
            $scope.httpData(url).success(function (data) {
                if (data.result_code=="S") {
                    var files=data.result_data.filePath;
                    var fileName=data.result_data.fileName;
                    window.location.href = srvUrl+"common/RcmFile/downLoad?filePath="+encodeURI(files)+"&fileName="+encodeURI(fileName);;

                } else {
                	$.alert("生成失败");
                }
            });
        }
}]);
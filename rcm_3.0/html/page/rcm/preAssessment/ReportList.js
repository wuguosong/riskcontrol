ctmApp.register.controller('ReportList', ['$http','$scope','$location','$routeParams', function ($http,$scope,$location,$routeParams) {
    $scope.pre={};
    $scope.pre.apply = {};
    $scope.pre.taskallocation={};
    $scope.pre.approveAttachment = {};
    //查义所有的操作
    $scope.x={};
    $scope.listProjectName=function(){
        var aMethed = 'common/commonMethod/getProjectPreReviewNoReportList';
        $scope.params={type:'pre',user_id:$scope.credentials.UUID};
        $scope.httpData(aMethed,$scope.params).success(
            function (data, status, headers, config) {
                $scope.pprs = data.result_data;
            }
        ).error(function (data, status, headers, config) {
            alert(status);
        });
        $scope.x.pmodel = "normal";
    };
    //查义所有的操作
    $scope.ListAll=function(){
        $scope.conf={currentPage:$scope.paginationConf.currentPage,pageSize:$scope.paginationConf.itemsPerPage,
            queryObj:{'user_id':$scope.credentials.UUID,'PROJECT_NAME':$scope.PROJECT_NAME,'WF_STATE':$scope.WF_STATE,'APPLY_TIME':$scope.APPLY_TIME,
            'REPORT_CREATE_DATE':0,'ASCDESC':$scope.orderby,'WFSTATEASCDESC':$scope.orderbystate}};
        var url =  'projectPreReview/ProjectPreReview/getAllReport';
        $scope.httpData(url,$scope.conf).success(function(data){
            // 变更分页的总数
            $scope.report  = data.result_data.list;
            $scope.paginationConf.totalItems = data.result_data.totalItems;
        });
    };
    $scope.order=function(o,v){
        if(o=="time"){
            $scope.orderby=v;
            $scope.orderbystate=null;
            if(v=="asc"){
                $("#orderasc").addClass("cur");
                $("#orderdesc").removeClass("cur");
            }else{
                $("#orderdesc").addClass("cur");
                $("#orderasc").removeClass("cur");
            }
        }else{
            $scope.orderbystate=v;
            $scope.orderby=null;
            if(v=="asc"){
                $("#orderascstate").addClass("cur");
                $("#orderdescstate").removeClass("cur");
            }else{
                $("#orderdescstate").addClass("cur");
                $("#orderascstate").removeClass("cur");
            }
        }
        $scope.ListAll();
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
        $.confirm("确定要删除？", function() {
            var aMethed = 'projectPreReview/ProjectPreReview/deleteProjectPreReport';
            $scope.httpData(aMethed, obj).success(
                function (data, status, headers, config) {
                    $scope.ListAll();
                }
            ).error(function (data, status, headers, config) {
                alert(status);
            });
        });
    };
    $scope.update=function(){
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
            $location.path("/PreReviewReport/Update/"+uid+"@@2");
        }
    }
    $scope.submitMaterialToP=function(){
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
        	$.alert("请选择其中一条数据提交！");
            return false;
        }
        if(num>1){
        	$.alert("只能选择其中一条数据进行提交！");
            return false;
        }else{
            $location.path("/BiddingInfo/"+uid);
        }
    }
    $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.ListAll);

    $scope.import=function(){
        var aMethed =  'projectPreReview/ProjectPreReview/importPreReportAll';
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

    $scope.preBiddingInfo=function(){
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
            $.alert("请选择其中一条数据！");
            return false;
        }
        if(num>1){
            $.alert("只能选择其中一条数据！");
            return false;
        }else{
            var aMethod = 'rcm/ProjectInfo/selectPrjReviewView';
            $scope.httpData(aMethod, {businessId:uid}).success(
                function (data, status, headers, config) {
                    var result = data.result_data;
                    if(result && result.TASK_NAME=='出具预评审报告'){
                        $location.path("/BiddingInfo/"+uid);
                    }else{
                        $.alert("当前节点为：("+result.TASK_NAME+"),不能提交决策委员会!");
                    }
                }
            ).error(function (data, status, headers, config) {
                alert(status);
            });
        }
    }

    $scope.forReport=function(model,uuid){
        if(model==null || model==""){
            $.alert("请选择项目模式!");
            return false;
        }else if(uuid==null || uuid=="") {
            $.alert("请选择项目!");
            return false;
        }else{
            $("#addModal").modal('hide');
            var id=uuid+"@"+model+"@2";
            $location.path("/PreReviewReport/Create/"+id);
        }
    }
}]);

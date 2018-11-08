/**
 * Created by gl on 2016/8/30.
 */
ctmApp.register.controller('ProjectFormalReviewList', ['$http','$scope','$location', function ($http,$scope,$location) {
	$scope.buttonControl=srvUrl=="http://riskcontrol.bewg.net.cn/rcm-rest/";
	//查义所有的操作
    $scope.ListAll=function(){
        $scope.conf={currentPage:$scope.paginationConf.currentPage,pageSize:$scope.paginationConf.itemsPerPage,
            queryObj:{'user_id':$scope.credentials.UUID,'PROJECT_NAME':$scope.PROJECT_NAME,'INVESTMENT_MANAGER_NAME':$scope.INVESTMENT_MANAGER_NAME,
            'PROJECT_MODEL_NAMES':$scope.PROJECT_MODEL_NAMES, 'COMPANY_HEADER_NAME':$scope.COMPANY_HEADER_NAME, 'LEGALREVIEWLEADER_NAME':$scope.LEGALREVIEWLEADER_NAME, 'APPLYTIMEFROM':$scope.formalApplyTimeFrom, 'APPLYTIMETO':$scope.formalApplyTimeTo,
            'CREATE_TIME':$scope.CREATE_TIME,'REPORTING_UNIT_NAME':$scope.REPORTING_UNIT_NAME,'WF_STATE':$scope.WF_STATE,'IS_SUPPLEMENT_REVIEW':$scope.IS_SUPPLEMENT_REVIEW,'ASCDESC':$scope.orderby,'WFSTATEASCDESC':$scope.orderbystate}};
        var url =  'formalAssessment/ProjectFormalReview/getAll';
        $scope.httpData(url,$scope.conf).success(function(data){
            // 变更分页的总数
            $scope.projectFormalReview  = data.result_data.list;
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
    $scope.delete=function(){

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
            var aMethed = 'formalAssessment/ProjectFormalReview/delete';
        $scope.httpData(aMethed, obj).success(
            function (data, status, headers, config) {
                if (data.result_code == "R") {
                        $.alert('报告未删除！删除申请单之前请先删除该项目下的报告单！');
                        return false;
                }else{
                    $.confirm("确定要删除？", function () {
                        $scope.httpData(aMethed, obj).success(
                            function (data, status, headers, config) {
                                $scope.ListAll();
                            }
                        ).error(function (data, status, headers, config) {
                            alert(status);
                        });
                    });
                }
            }
                ).error(function (data, status, headers, config) {
            alert(status);
        });
    };
  /*  $scope.httpData(aMethed, obj).success(
        function (data, status, headers, config) {
            $scope.ListAll();
        }
    ).error(function (data, status, headers, config) {
        alert(status);
    });*/
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
    $scope.import=function(){
        var url = 'formalAssessment/ProjectFormalReview/importFormalAssessmentReport';
        $scope.httpData(url,$scope.credentials.UUID).success(function (data) {
            if (data.result_code=="S") {
                var files=data.result_data.filePath;
                var fileName=data.result_data.fileName;
                window.location.href = srvUrl+"common/RcmFile/downLoad?filePath="+encodeURI(files)+"&fileName="+encodeURI(fileName);;

            } else {
            	$.alert("生成失败");
            }
        });
    }
    // 通过$watch currentPage和itemperPage 当他们一变化的时候，重新获取数据条目
    $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.ListAll);

    //flag="4"代表是从正式评审申请列表进入工单详情
    $scope.flag="4";
}]);
ctmApp.register.controller('ProjectFormalReviewListReadOnly', ['$http','$scope','$location', function ($http,$scope,$location) {
    //查义所有的操作
    $scope.ListAll=function(){
        $scope.conf={currentPage:$scope.paginationConf.currentPage,pageSize:$scope.paginationConf.itemsPerPage,
            queryObj:{'PROJECT_NAME':$scope.PROJECT_NAME,'INVESTMENT_MANAGER_NAME':$scope.INVESTMENT_MANAGER_NAME,
            'PROJECT_MODEL_NAMES':$scope.PROJECT_MODEL_NAMES, 'COMPANY_HEADER_NAME':$scope.COMPANY_HEADER_NAME,
                'LEGALREVIEWLEADER_NAME':$scope.LEGALREVIEWLEADER_NAME,'REPORTING_UNIT_NAME':$scope.REPORTING_UNIT_NAME,'CREATE_TIME':$scope.CREATE_TIME,
                'WF_STATE':$scope.WF_STATE,'IS_SUPPLEMENT_REVIEW':$scope.IS_SUPPLEMENT_REVIEW,'ASCDESC':$scope.orderby,
                'WFSTATEASCDESC':$scope.orderbystate}};
        var url =  'formalAssessment/ProjectFormalReview/getProjectFormalReviewReadOnlyAll';
        $scope.httpData(url,$scope.conf).success(function(data){
            // 变更分页的总数
            $scope.projectFormalReview  = data.result_data.list;
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
    $scope.sysAndLeaderAdmin=function(){
        var url = 'common/commonMethod/getSystemAndLeaderAdmin';
        $scope.user={user_id:$scope.credentials.UUID};
        $scope.httpData(url,$scope.user).success(
            function (data, status, headers, config) {
                $scope.userAdmin = data.result_data;
            }
        ).error(function (data, status, headers, config) {
            alert(status);
        });
    };
    angular.element(document).ready(function() {
        $scope.sysAndLeaderAdmin();
    });

    // 通过$watch currentPage和itemperPage 当他们一变化的时候，重新获取数据条目
    $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.ListAll);

    //flag="6"代表是从部门工情况正式评审申请列表进入工单详情
    $scope.flag="6";
}]);
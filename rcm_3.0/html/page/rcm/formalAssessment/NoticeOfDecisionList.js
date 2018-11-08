ctmApp.register.controller('NoticeOfDecisionList', ['$http','$scope','$location', function ($http,$scope,$location) {
    $scope.orderby='desc';
    //查义所有的操作
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
    $scope.paginationConf.queryObj = {userId:$scope.credentials.UUID};
    $scope.listAll = function(){
        $scope.httpData('rcm/NoticeOfDecisionInfo/getAll',$scope.paginationConf).success(function(data){
            if(data.result_code == "S"){
                $scope.nod = data.result_data.list;
                $scope.paginationConf.totalItems = data.result_data.totalItems;
            }
        });
    }
    // 通过$watch currentPage和itemperPage 当他们一变化的时候，重新获取数据条目
    $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage',  $scope.listAll);
    $scope.order=function(v){
        $scope.paginationConf.queryObj.ASCDESC= v;
        if(v=='asc'){
            $("#orderasc").addClass("cur");
            $("#orderdesc").removeClass("cur");
        }else{
            $("#orderdesc").addClass("cur");
            $("#orderasc").removeClass("cur");
        }
        $scope.listAll();
    }
   /* $scope.ListAll = function () {
        $scope.conf={currentPage:$scope.paginationConf.currentPage,pageSize:$scope.paginationConf.itemsPerPage,lastCurrentTimeStamp:$scope.paginationConf.lastCurrentTimeStamp,
            'ORDERVAL':$scope.orderby,'projectName':$scope.projectName,'evaluationScale':$scope.evaluationScale,'dateOfMeeting':$scope.dateOfMeeting};
        var aMethod = 'formalAssessment/NoticeOfDecision/getAll';
        $scope.httpData(aMethod,$scope.conf).success(
            function (data, status, headers, config) {
                $scope.nod = data.result_data.list;
                $scope.paginationConf.totalItems = data.result_data.totalItem;
            }
        ).error(function (data, status, headers, config) {
            alert(status);
        });
    };
    */
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
        var obj = {"_id": uid};
        var aMethed = 'formalAssessment/NoticeOfDecision/delete';
        $scope.httpData(aMethed, obj).success(
            function (data, status, headers, config) {
                $scope.listAll();
                alert("删除成功！");
            }
        ).error(function (data, status, headers, config) {
            alert(status);
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
            $location.path("/NoticeOfDecision/Update/" + uid);
        }
    }
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

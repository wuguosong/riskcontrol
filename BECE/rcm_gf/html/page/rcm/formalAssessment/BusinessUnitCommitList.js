ctmApp.register.controller('BusinessUnitCommitList', ['$http','$scope','$location','$routeParams',
function ($http,$scope,$location,$routeParams) {
    $scope.orderby=-1;
    if($scope.paginationConf.queryObj==null || $scope.paginationConf.queryObj == ''){
		$scope.paginationConf.queryObj = {};
	}
    //查义所有的操作
     $scope.listBusinessUnitCommit = function () {
        var aMethed = 'common/commonMethod/getProjectFormalReviewList';
         $scope.conf={user_id:$scope.credentials.UUID,'pfrBusinessUnitCommit':"true"};
         $scope.httpData(aMethed,  $scope.conf).success(
         function (data, status, headers, config) {
                 $scope.pprs = data.result_data;

             }
         ).error(function (data, status, headers, config) {
             alert(status);
         });
     };
    $scope.ListAll = function () {
        var USER_ID=$scope.credentials.UUID;
        $scope.confBusiness={currentPage:$scope.paginationConf.currentPage,itemsPerPage:$scope.paginationConf.itemsPerPage,
            'ORDERVAL':$scope.orderby,'projectName':$scope.paginationConf.queryObj.projectName,'reportingUnit':$scope.paginationConf.queryObj.reportingUnit,'pfrBusinessUnitDate':$scope.paginationConf.queryObj.pfrBusinessUnitDate,'user_id':USER_ID};
        var aMethod = 'formalAssessment/FormalReport/getbusinessUnitCommitList';
        $scope.httpData(aMethod, $scope.confBusiness).success(
            function (data, status, headers, config) {
                $scope.ppr = data.result_data.list;
                $scope.paginationConf.totalItems = data.result_data.totalItem;
            }
        ).error(function (data, status, headers, config) {
            alert(status);
        });
    };
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
        	$.alert("请选择其中一条或多条数据删除！");
            return false;
        }
        var obj = {"_id": uid};
        var aMethed = 'formalAssessment/FormalReport/deleteBusinessUnitCommit';
        $scope.httpData(aMethed, obj).success(
            function (data, status, headers, config) {
                $scope.ListAll();
                $.alert("删除成功！");
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
        	$.alert("请选择其中一条或多条数据编辑！");
            return false;
        }
        if (num > 1) {
        	$.alert("只能选择其中一条数据进行编辑！");
            return false;
        } else {
            $location.path("/BusinessUnitCommit/Update/" + uid);
        }
    }

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
    $scope.import=function(){
        var aMethed =  'formalAssessment/FormalReport/businessUnitListReport';
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
    $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage',  $scope.ListAll);

}]);

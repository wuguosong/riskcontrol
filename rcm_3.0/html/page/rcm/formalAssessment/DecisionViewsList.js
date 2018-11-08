ctmApp.register.controller('DecisionViewsList', ['$http','$scope','$location','$routeParams', function ($http,$scope,$location,$routeParams) {
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
    //查义所有的操作
    $scope.ListAll=function(){
        $scope.conf={currentPage:$scope.paginationConf.currentPage,itemsPerPage:$scope.paginationConf.itemsPerPage,
            'ORDERVAL':$scope.orderby,'projectName':$scope.projectName};
        var aMethed = 'projectPreReview/Meeting/getPolicyDecisionAll';
        $scope.httpData(aMethed,$scope.conf).success(
            function (data, status, headers, config) {
                $scope.reportPfr = data.result_data.list;
                $scope.paginationConf.totalItems = data.result_data.totalItem;
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
    // 通过$watch currentPage和itemperPage 当他们一变化的时候，重新获取数据条目
    $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage',  $scope.ListAll);
    $scope.import=function(){
        var aMethed =  'projectPreReview/Meeting/formalReviewListReport';
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

ctmApp.register.controller('DecisionViewDetail', ['$http','$scope','$location','$routeParams', function ($http,$scope,$location,$routeParams) {
    var objId = $routeParams.id;
    $scope.isshow=true;
    $scope.getByID=function(id){
        var  url = 'projectPreReview/Meeting/findFormalAndReport';
        $scope.httpData(url,id).success(function(data){
            $scope.Onemeeting  = data.result_data.Meeting;
            $scope.formalReport  = data.result_data.Report;
            $scope.pfr  = data.result_data.Formal;
            $scope.formalID=$scope.formalReport.projectFormalId;
            var ptNameArr=[],investmentaNameArr=[];
            var pt=$scope.pfr.apply.projectType;
            if(null!=pt && pt.length>0){
                for(var i=0;i<pt.length;i++){
                    ptNameArr.push(pt[i].VALUE);
                }
                $scope.pfr.apply.projectType=ptNameArr.join(",");
            }
            if(undefined!=$scope.Onemeeting){
                var investmenta=$scope.Onemeeting.investment;
                if(null!=investmenta && investmenta.length>0){
                    for(var k=0;k<investmenta.length;k++){
                        investmentaNameArr.push(investmenta[k].name);
                    }
                    $scope.Onemeeting.investmentName=investmentaNameArr.join(",");
                }
            }else{
                $scope.isshow=false;
            }
            $scope.getProjectPreReviewYJDWByID($scope.formalID);
        });
    }
    $scope.downLoadFileReport = function(filePath){
        window.location.href = srvUrl+"common/RcmFile/downLoad?filePath="+encodeURI(filePath)+"&fileName="+encodeURI("正式评审报告.docx");
    }
    $scope.downLoadFile = function(idx){
        var filePath = idx.filePath, fileName = idx.fileName;
        window.location.href = srvUrl+"common/RcmFile/downLoad?filePath="+filePath+"&fileName="+encodeURI(fileName);
    }
    $scope.getSelectTypeByCodetype=function(typeCode){
        var  url = 'common/commonMethod/selectDataDictionByCode';
        $scope.httpData(url,typeCode).success(function(data){
            if(data.result_code == 'S'){
                $scope.projectlisttype=data.result_data;
            }else{
                alert(data.result_name);
            }
        });
    }
    $scope.getByID(objId);
    $scope.getProjectPreReviewYJDWByID=function(id){
        var  url = 'rcm/ProjectInfo/getTaskOpinionTwo';
        $scope.panam={taskDefKey:'usertask16' ,businessId:id};
        $scope.httpData(url,$scope.panam).success(function(data){
            if(data.result_code === 'S') {
                var yijd = data.result_data;
                if (null != yijd) {
                    $scope.yjdw = yijd.DEPTNAME+" _ "+yijd.USERNAME+"："+yijd.OPINION;
                }else{
                    $scope.yjdw=null;
                }
            }
        });
    }
    angular.element(document).ready(function() {
        $scope.getSelectTypeByCodetype('14');
    });
}]);
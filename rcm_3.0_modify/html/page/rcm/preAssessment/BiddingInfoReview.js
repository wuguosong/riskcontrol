ctmApp.register.controller('BiddingInfoReview', ['$http','$scope','$location','$routeParams', function ($http,$scope,$location,$routeParams) {
    var objId=  $routeParams.id;

    //查义所有的操作
    $scope.ListAll=function(objId){
        var aMethed = 'projectPreReview/Feedback/listOne';
        $scope.httpData(aMethed,objId).success(
            function (data, status, headers, config) {
                $scope.ppr = data.result_data;
                if($scope.ppr.reviewReport!=null){
                    var vl=$scope.ppr.reviewReport.incomeEvaluation;
                    if(vl!=null && vl.length>0){
                        $scope.ppr.profitInvestment=vl[0].profitInvestment;
                    }
                }
                var pthNameArr=[];

              /*  var pth=$scope.ppr.apply.companyHeader;
                if(null!=pth && pth.length>0){
                    for(var i=0;i<pth.length;i++){
                        pthNameArr.push(pth[i].name);
                    }
                    $scope.ppr.apply.companyHeaderName=pthNameArr.join(",");
                }*/
            }
        ).error(function (data, status, headers, config) {
            alert(status);
        });
    };
    $scope.downLoadFile = function(df){
        window.location.href = srvUrl+"common/RcmFile/downLoad?filePath="+df.filePath+"&fileName="+encodeURI(df.fileName);
    }
    $scope.downLoadFileReport = function(filePath,filename){
        if(undefined!=filePath && null!=filePath){
            var index = filePath.lastIndexOf(".");
            var str = filePath.substring(index + 1, filePath.length);
            window.location.href = srvUrl+"common/RcmFile/downLoad?filePath="+encodeURI(filePath)+"&fileName="+encodeURI(filename+"-预评审报告."+str);
        }else{
            $.alert("附件未找到！");
            return false;
        }
    }
    /*查询固定成员审批列表*/
    $scope.getProjectPreReviewGDCYByID=function(id){
        var  url = 'rcm/ProjectInfo/getTaskOpinion';
        $scope.panam={taskDefKey:'usertask4' ,businessId:id};
        $scope.httpData(url,$scope.panam).success(function(data){
            $scope.gdcy  = data.result_data;
           /* var gdcy=data.result_data;
            if(null!=gdcy){
                var name=",";
                for(var i=0;i<gdcy.length;i++){
                    name=name+gdcy[i].USERNAME+":"+gdcy[i].OPINION+"/n";
                }
                name=name.substring(1,name.length);
                $scope.gdcy=name;
            }*/
        });
    }

    /*查询一级审批列表*/
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
    $scope.getProjectPreReviewYJDWByID(objId);
    $scope.getProjectPreReviewGDCYByID(objId);

    //初始化
    $scope.ListAll(objId);
}]);

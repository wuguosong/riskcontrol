/**
 * Created by gl on 2016/9/14.
 */

ctmApp.register.controller('ReviewTeamComments', ['$http','$scope','$location','$routeParams', function ($http,$scope,$location,$routeParams) {
    //初始化
    var complexId = $routeParams.id;
    var params = complexId.split("@");
    if(null!=params[4] && ""!=params[4]){
        $scope.flag=params[4];
    }
     var objId =params[0];
    var taskID='wwxosnsosjdsnsjnddID';
    $scope.taskID=taskID;
    $scope.pfr={};
    $scope.pfr.taskallocation={};
    $scope.pfr.apply = {};
    $scope.dic=[];
    $scope.pfr.approveAttachment = {};
    //提交流程
    //提交流程
    $scope.showSubmitModal=function(){
      //  $scope.saveFormalReviewComments(function(){
            var reviewLeader =  $scope.pfr.taskallocation.reviewLeader;//评审负责人
            $scope.approve = [{
                submitInfo:{
                    taskId:params[3],
                    runtimeVar:{inputUser:reviewLeader.VALUE},
                    currentTaskVar:{opinion:'请审批'}
                },
                showInfo:{destination:'评审负责人确认',approver:reviewLeader.NAME}
            }];
            $('#submitModal').modal('show');
       // });
    }
    $scope.wfInfo = {processDefinitionId:params[1],processInstanceId:params[2]};

    $scope.saveFormalReviewComments=function(openPopWin){
        var  url = 'formalAssessment/ProjectFormalReview/saveProjectFormalByID';
        var proReviewComments = $("#pfrReviewComments").val();
        if((proReviewComments==null || proReviewComments=="" )){
        	$.alert("请填写专业评审意见");
            return false;
        }
        $scope.pfr.pfrReviewComments=proReviewComments;
        $scope.httpData(url,$scope.pfr).success(function(data){
            if(data.result_code === 'S'){
                openPopWin();
            }
        });
    }
    $scope.getProjectFormalReviewByID=function(id){
        var  url = 'formalAssessment/ProjectFormalReview/getProjectFormalReviewByID';
        $scope.httpData(url,id).success(function(data){
            var ptNameArr=[],pmNameArr=[],fgNameArr=[],pthNameArr=[];
            $scope.pfr  = data.result_data;
            /*var pth=$scope.pfr.apply.companyHeader;
            if(null!=pth && pth.length>0){
                for(var i=0;i<pth.length;i++){
                    pthNameArr.push(pth[i].name);
                }
                $scope.pfr.apply.companyHeaderName=pthNameArr.join(",");
            }*/
            var pt1NameArr=[];
            var pt1=$scope.pfr.apply.serviceType;
            if(null!=pt1 && pt1.length>0){
                for(var i=0;i<pt1.length;i++){
                    pt1NameArr.push(pt1[i].VALUE);
                }
                $scope.pfr.apply.serviceType=pt1NameArr.join(",");
            }
            var pt=$scope.pfr.apply.projectType;
            if(null!=pt && pt.length>0){
                for(var i=0;i<pt.length;i++){
                    ptNameArr.push(pt[i].VALUE);
                }
                $scope.pfr.apply.projectType=ptNameArr.join(",");
            }
            var pm=$scope.pfr.apply.projectModel;
            if(null!=pm && pm.length>0){
                for(var j=0;j<pm.length;j++){
                    pmNameArr.push(pm[j].VALUE);
                }
                $scope.pfr.apply.projectModel=pmNameArr.join(",");
            }
            var fg=$scope.pfr.taskallocation.fixedGroup;
            if(null!=fg && fg.length>0){
                for(var k=0;k<fg.length;k++){
                    fgNameArr.push(fg[k].NAME);
                }
                $scope.pfr.taskallocation.fixedGroup=fgNameArr.join(",");
            }

            $scope.fileName=[];
            var filenames=$scope.pfr.attachment;
            for(var i=0;i<filenames.length;i++){
                var arr={UUID:filenames[i].UUID,ITEM_NAME:filenames[i].ITEM_NAME};
                $scope.fileName.push(arr);
            }
            $scope.getNoticeOfDecstionByProjectFormalID($scope.pfr.apply.projectNo);
            $scope.getProjectPreReviewYJDWByID(objId);
        });
    }

    $scope.getProjectFormalReviewByID(objId);
    /*查询一级审批列表*/
    $scope.getProjectPreReviewYJDWByID=function(id){
        var  url = 'rcm/ProjectInfo/getTaskOpinionTwo';
        $scope.panam={taskDefKey:'usertask16' ,businessId:id};
        $scope.httpData(url,$scope.panam).success(function(data){
            if(data.result_code === 'S') {
                var yijd = data.result_data;
                if (null != yijd) {
                    $scope.yjdw = yijd.DEPTNAME+" _ "+yijd.USERNAME+"："+yijd.OPINION;
                    if(null!=$scope.pfr.cesuanFileOpinion && ''!=$scope.pfr.cesuanFileOpinion){
                        $scope.yjdw = $scope.yjdw+" ；<br/>项目投资测算意见："+$scope.pfr.cesuanFileOpinion;
                    }
                    if(null!=$scope.pfr.tzProtocolOpinion && ''!=$scope.pfr.tzProtocolOpinion){
                        $scope.yjdw = $scope.yjdw+" ；<br/>项目投资协议意见：" + $scope.pfr.tzProtocolOpinion;
                    }
                    $scope.yjdw=Utils.encodeTextAreaString($scope.yjdw);
                }else{
                    $scope.yjdw=null;
                }
            }
        });
    }
    var Utils = {};
// textArea换行符转<br/>
    Utils.encodeTextAreaString= function(str) {
        var reg = new RegExp("<br/>", "g");
        str = str.replace(reg, "\n");
        return str;
    }

    //根据id查询决策通知书决策意见
    $scope.getNoticeOfDecstionByProjectFormalID=function(pid){
        var url="formalAssessment/NoticeOfDecision/getNoticeOfDecstionByProjectFormalID";
        $scope.httpData(url,pid).success(function(data){
            if(data.result_code === 'S'){
                if(undefined!=data.result_data) {
                    $scope.noticofDec=data.result_data;
                    var c = $scope.noticofDec.consentToInvestment;
                    if (c == "1") {
                        $scope.consentToInvestment = "同意投资";
                    } else if (c == "2") {
                        $scope.consentToInvestment = "不同意投资";
                    } else if (c == 3) {
                        $scope.consentToInvestment = "同意有条件投资";
                    }
                    $scope.implementationMatters = $scope.noticofDec.implementationMatters;
                    $scope.dateOfMeeting = $scope.noticofDec.dateOfMeeting;
                }else{
                    $scope.consentToInvestment=null;
                    $scope.implementationMatters=null;
                }
            }else{
                alert(data.result_name);
            }
        });
    }

    $scope.getSelectTypeByCode=function(typeCode){
        var  url = 'common/commonMethod/selectDataDictionByCode';
        $scope.httpData(url,typeCode).success(function(data){
            if(data.result_code === 'S'){
                $scope.optionTypeList=data.result_data;
            }else{
                alert(data.result_name);
            }
        });
    }
/*    $scope.getSelectTypeByCodeL=function(typeCode){
        var  url = 'common/commonMethod/selectDataDictionByCode';
        $scope.httpData(url,typeCode).success(function(data){
            if(data.result_code === 'S'){

                $scope.optionTypeListL=data.result_data;
            }else{
                alert(data.result_name);
            }
        });
    }*/
    angular.element(document).ready(function() {
        $scope.getSelectTypeByCode("06");
        /*$scope.getSelectTypeByCodeL("09");*/
    });
}]);
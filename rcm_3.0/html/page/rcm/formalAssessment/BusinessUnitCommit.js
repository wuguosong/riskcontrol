
ctmApp.register.controller('BusinessUnitCommit', ['$http','$scope','$location','$routeParams', function ($http,$scope,$location,$routeParams) {
    //初始化
	$scope.oldUrl = $routeParams.url;
	
    var objId = $routeParams.id;
    $scope.id=objId;
    $scope.pfr={};
    $scope.pfr.apply = {};
    $scope.pfr.taskallocation={};
    $scope.pfr.approveAttachment = {};
    $scope.pfr.pfrBusinessUnitCommit=[];
    $scope.pfr._id=objId;
    $scope.addProfit = function(){
        function addBlankRow(array){
            var blankRow = {
                profitSubject:'',
                profitInvestment:'',
                profitPrivateCapital:'',
                profitCashFlowInfo:''
            }
            array.push(blankRow);
        }
        if(undefined==$scope.pfr.pfrBusinessUnitCommit){
            $scope.pfr.pfrBusinessUnitCommit=[];
        }
        addBlankRow($scope.pfr.pfrBusinessUnitCommit);

    }
    //移除第二部分对应数据
    $scope.deleteProfit = function(){
        var commentsObj = $scope.pfr.pfrBusinessUnitCommit;
        if(commentsObj!=null){
            for(var i=0;i<commentsObj.length;i++){
                if(commentsObj[i].selected){
                    commentsObj.splice(i,1);
                    i--;
                }
            }
        }
    }
    $scope.getProjectFormalReviewByID=function(id){
        var  url = 'formalAssessment/FormalReport/getByID';
        $scope.httpData(url,id).success(function(data){
            $scope.pfr  = data.result_data;
            if(undefined==$scope.pfr.pfrBusinessUnitCommit){
                $scope.addProfit();
            }
        });
    }
    $scope.saveBusinessUnitCommit= function () {
        var USER_ID=$scope.credentials.UUID;
        if ($("#Business").valid()) {
            var postObj;
            var  url = 'formalAssessment/FormalReport/saveBusinessUnitCommitList';
            $scope.pfr.pfrBusinessUnitCommitState="1";
            $scope.pfr.pfrBusinessCreate_by=USER_ID;
            postObj = $scope.httpData(url, $scope.pfr);
            postObj.success(function (data) {
                if (data.result_code === 'S') {
                	$.alert("保存成功");
                    $location.path("/BusinessUnitCommitList");
                } else {
                    alert(data.result_name);
                }
            });
        }
    }
    $scope.suubmitBusinessUnitCommit= function () {
        var USER_ID=$scope.credentials.UUID;
        if ($("#Business").valid()) {
            $.confirm("确认要提交吗？提交后不可以再修改信息！", function(s) {
                var postObj;
                var  url = 'formalAssessment/FormalReport/saveBusinessUnitCommitList';
                $scope.pfr.pfrBusinessUnitCommitState="2";
                $scope.pfr.pfrBusinessCreate_by=USER_ID;
                postObj =$scope.httpData(url, $scope.pfr);
                postObj.success(function (data) {
                    if (data.result_code === 'S') {
                        $.alert("提交成功");

                        var objectId = $scope.pfr._id;
                        var sendData = 'ws.client.contract/SendProjectThread/sendToContractSys';
                        var storeId = 'ws.client.contract/SendProjectThread/store';
                        $scope.httpData(sendData, objectId).error(function (data, status, headers, config) {
                                $scope.httpData(storeId, objectId)
                            });

                        $location.path("/BusinessUnitCommitList");
                    } else {
                        alert(data.result_name);
                    }
                });
            });
        }
    }
    var action =$routeParams.action;
    $scope.actionpam =$routeParams.action;
    if (action == 'Update') {
        $scope.getProjectFormalReviewByID(objId);
        $scope.titleName = "业务单位承诺修改";
    } else if (action == 'View') {
        $scope.getProjectFormalReviewByID(objId);
        $('button').attr("disabled",true);
        $("#savebtn").hide();
        $("#submitbtn").hide();
        $("#addbtn").hide();
        $("#delbtn").hide();
        $scope.titleName = "业务单位承诺查看";
    } else if (action == 'Create') {
        $scope.getProjectFormalReviewByID(objId);
        $scope.titleName = "业务单位承诺新增";
    }
    $scope.setDirectiveParamTwo=function(num){
        $scope.columnsNum=num;
    }
    $scope.setDirectiveOrgList=function(id,name){
        var num=$scope.columnsNum;
        $scope.pfr.pfrBusinessUnitCommit[num].responsibilityDept = {"name":name,value:id};
    }
    $scope.$on('ngRepeatFinished', function () {
        var optionsDate = {
            todayBtn: "linked",
            orientation: $('body').hasClass('right-to-left') ? "auto right" : 'auto auto'
        }
        $('.loadtime').datepicker(optionsDate);
    });
}]);
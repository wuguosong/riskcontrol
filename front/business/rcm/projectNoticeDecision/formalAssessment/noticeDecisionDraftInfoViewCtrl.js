define(['app', 'Service'], function (app) {
    app
        .register.controller('noticeDecisionDraftInfoViewCtrl', ['$http', '$scope', '$location', '$stateParams', 'BEWG_URL', 'Window', 'CommonService',
        function ($http, $scope, $location, $stateParams, BEWG_URL, Window, CommonService) {
            console.log('noticeDecisionDraftInfoView');

            //初始化
            var businessId = $stateParams.id;
            $scope.action =  $stateParams.action;
            var tabIndex =  $stateParams.tabIndex;
            $scope.nod={};
            $scope.initData = function(){
                $scope.getNoticeDecstionByID(businessId);
            }

            //提交
            $scope.showSubmitModal = function(){
                Window.confirm("提交后将不可修改，确定提交？".result.then(function(){
                    $http({
                        method:'post',
                        url: SRV_URL + $scope.localUrl,
                        data: $.param({"nod":JSON.stringify($scope.nod)})
                    }).success(function(result){
                        if (result.success) {
                            $http({
                                method:'post',
                                url: SRV_URL + "noticeDecisionDraftInfo/submitUpdateStage.do",
                                data: $.param({
                                    "projectFormalId":$scope.nod.projectFormalId,
                                    "stage": "3.9"
                                })
                            }).success(function(result){
                                Window.alert(result.result_name);
                               /* var oldUrl=window.btoa(encodeURIComponent(escape("#/NoticeDecisionDraftList/1")))*/
                                $location.path("/index/NoticeDecisionDraftDetailView/submitted/"+businessId);
                            });
                        } else {
                            Window.alert(result.result_name);
                        }
                    });
                }));

            }

            //查询决策通知书详情信息
            $scope.getNoticeDecstionByID=function(id){
                var  url = 'noticeDecisionDraftInfo/queryNoticeDecstion.do';
                $http({
                    method:'post',
                    url: SRV_URL + url,
                    data: $.param({"formalId":businessId})
                }).success(function(data){
                    var noticeDecision = data.result_data;
                    $scope.requireTask1 = noticeDecision.require1 != null && noticeDecision.require1 != '';
                    $scope.requireTask2 = noticeDecision.require2 != null && noticeDecision.require2 != '';
                    $scope.requireTask3 = noticeDecision.require3 != null && noticeDecision.require3 != '';

                    $scope.nod = data.result_data;

                    var haderNameArr = [], haderValueArr = [];
                    if (null != $scope.nod.personLiable) {
                        var header = $scope.nod.personLiable;
                        if (null != header && header.length > 0) {
                            for (var i = 0; i < header.length; i++) {
                                haderNameArr.push(header[i].name);
                                haderValueArr.push(header[i].value);
                            }
                            commonModelValue("personLiable", haderValueArr, haderNameArr);
                            $scope.nod.personLiableName = haderNameArr.join(",");
                        }
                    }
                    //查询项目信息的业务类型
                    $http({
                        method:'post',
                        url: SRV_URL + "noticeDecisionDraftInfo/querySaveDefaultInfo.do",
                        data: $.param({"businessId":businessId})
                    }).success(function(result){
                        $scope.pfr = result.result_data;
                        var pt1NameArr=[];
                        $scope.serviceController=false;
                        var serviceType=$scope.pfr.apply.serviceType;
                        if(null!=serviceType && serviceType.length>0){
                            for(var i=0;i<serviceType.length;i++){
                                pt1NameArr.push(serviceType[i].KEY);
                                if(serviceType[i].KEY=='1401' || serviceType[i].KEY=='1402'){
                                    $scope.serviceController=true;
                                }
                            }
                            $scope.pfr.apply.serviceType=pt1NameArr.join(",");
                        }
                    });
                });
            }
            //生成word文档
            $scope.createWord=function(){
                startLoading();
                var  url = 'noticeDecisionDraftInfo/getNoticeOfDecisionWord.do';
                $http({
                    method:'post',
                    url: SRV_URL + url,
                    data: $.param({"formalId":businessId})
                }).success(function(data){
                    if(data.success){
                        var filesPath=data.result_data.filePath;
                        var filesName=data.result_data.fileName;
                        //window.location.href = SRV_URL+"common/RcmFile/downLoad?filePath="+encodeURI(filesPath)+"&fileName="+encodeURI(filesName);
                        window.location.href = SRV_URL+"file/downloadFile.do?filepaths="+encodeURI(encodeURI(filesPath))+"&filenames="+encodeURI(encodeURI(filesName));
                    }else{
                        Window.alert("报表生成失败，请查看必填项是否填写完毕；如果全部正常填写请联系管理员！");
                    }
                })
                endLoading();
            }
            $scope.initData();

            var commonModelValue=function(paramsVal,arrID,arrName){
                var leftstr2="<li class=\"select2-search-choice\"><div>";
                var centerstr2="</div><a class=\"select2-search-choice-close\" tabindex=\"-1\" onclick=\"delSelect(this,'";
                var addID2="');\"  ></a><div class=\"full-drop\"><input type=\"hidden\" id=\"\"  value=\"";
                var rightstr2="\"></div></li>";
                for(var i=0;i<arrName.length;i++){
                    $("#header"+paramsVal).find(".select2-search-field").before(leftstr2+arrName[i]+centerstr2+paramsVal+"','"+arrName[i]+","+arrID[i]+addID2+arrID[i]+rightstr2);
                }
            }

            $scope.initPage = function(){
                if($scope.nod.oracle.WF_STATE!='0'){
                    $scope.wfInfo.businessId = businessId;
                    $scope.refreshImg = Math.random()+1;
                }

            }
            $scope.titleName = "决策通知书详情信息";
            $scope.wfInfo = {processKey:'noticeDecision'};

            $scope.initPingShenZongTouZiList = function(){
                $http({
                    method:'post',
                    url: SRV_URL + "dict/queryDictItemByDictTypeCode.do",
                    params:{"code":BEWG_URL.DICT_JC_PSZTZ}
                }).success(function(result){
                    $scope.pingShenZongTouZiList = result.result_data;
                    $scope.pingShenZongTouZiList.push({"ITEM_NAME":"","ITEM_CODE":""});
                });
            }
            $scope.initPingShenZongTouZiList();

            // 返回列表
            $scope.cancel = function (){
                $location.path("/index/NoticeDecisionDraftList/" + tabIndex);
            }

        }]);
});
define(['app', 'Service'], function (app) {
    app
        .register.controller('bpmnCtrl', ['$http', '$scope', '$location', '$stateParams', 'BEWG_URL', 'Window',
        function ($http, $scope, $location, $stateParams, BEWG_URL, Window) {
            console.log('bpmn');

            $scope.wait={};
            $scope.deploy_deploy = function(){
                var params={"bpmnType": $scope.deploy_bpmnType};
                $http({
                    method:'post',
                    url:BEWG_URL.DeployBpmn,
                    params: params
                }).success(function(data){
                    if(data.success){
                        Window.alert("执行成功！");
                    }else{
                        Window.alert(data.result_name);
                    }
                });
            };
            $scope.process_clear = function(){
                var params = {
                    "bpmnType": $scope.process_bpmnType,
                    "businessKey": $scope.process_businessId
                };
                $http({
                    method:'post',
                    url:BEWG_URL.StopProcessBpmn,
                    params: params
                }).success(function(data){
                    if(data.success){
                        Window.alert("执行成功！");
                    }else{
                        Window.alert(data.result_name);
                    }
                });
            };
            $scope.project_clear = function(){
                $.confirm("删除后不可恢复，请慎重！",function(a){
                    var params = {
                        "bpmnType": $scope.project_bpmnType,
                        "businessKey": $scope.project_businessId
                    };
                    $http({
                        method:'post',
                        url:BEWG_URL.ClearBpmn,
                        params: params
                    }).success(function(data){
                        if(data.success){
                            Window.alert("执行成功！");
                        }else{
                            Window.alert(data.result_name);
                        }
                    });
                });
            };
            $scope.notice_repeatCallOne = function(){
                var params = {
                    "id": $scope.noticeOfDecisionId
                };
                $http({
                    method:'post',
                    url:BEWG_URL.RepeatCallByNoticeIdBpmn,
                    params: params
                }).success(function(data){
                    Window.alert(data.result_name);
                });

            }

            $scope.notice_initReportStatus = function(){
                var params = {
                    "id": $scope.noticeOfDecisionId
                };
                $http({
                    method:'post',
                    url:BEWG_URL.InitReportStatusBpmn,
                    params:{"params":$scope.daiBan}
                }).success(function(data){
                    Window.alert(data.result_name);
                });
            }
            $scope.sendTask = function(){

                if($scope.wait.taskId == "" || $scope.wait.taskId == null || $scope.wait.taskId == undefined){
                    Window.alert("taskId不能为空！");
                    return;
                }
                if($scope.wait.url == "" || $scope.wait.url == null || $scope.wait.url == undefined){
                    Window.alert("路径不能为空！");
                    return;
                }
                if($scope.wait.createDate == "" || $scope.wait.createDate == null || $scope.wait.createDate == undefined){
                    Window.alert("创建日期不能为空！");
                    return;
                }
                if($scope.wait.title == "" || $scope.wait.title == null || $scope.wait.title == undefined){
                    Window.alert("标题不能为空！");
                    return;
                }
                if($scope.wait.owner == "" || $scope.wait.owner == null || $scope.wait.owner == undefined){
                    Window.alert("接受者ID不能为空！");
                    return;
                }
                if($scope.wait.status == "" || $scope.wait.status == null || $scope.wait.status == undefined){
                    Window.alert("状态必选！");
                    return;
                }
                if($scope.wait.sender == "" || $scope.wait.sender == null || $scope.wait.sender == undefined){
                    Window.alert("发送人姓名不能为空！");
                    return;
                }

                $http({
                    method:'post',
                    url:BEWG_URL.SendTaskBpmn,
                    data: $.param({"json":JSON.stringify($scope.wait)})
                }).success(function(data){
                    if(data.success){
                        Window.alert("发送成功！");
                    }else{
                        Window.alert(data.result_name);
                    }
                });
            }
            $scope.executeTargetInterface = function(){
                $http({
                    method:'post',
                    url:BEWG_URL.InitWithJsonBpmn,
                    data: $.param({"beanName":$scope.beanName,"json":$scope.beanParam})
                }).success(function(data){
                    if(data.success){
                        Window.alert(data.result_name);
                    }else{
                        Window.alert(data.result_name);
                    }
                });
            }
        }]);
});
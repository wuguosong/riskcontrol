ctmApp.register.controller('BulletinMattersDetailViewModify', ['$http','$scope','$location', '$routeParams', '$filter',
    function ($http,$scope,$location, $routeParams, $filter) {
        $scope.queryParamId = $routeParams.id;
        $scope.oldUrl = $routeParams.url;
        $scope.initDefaultData = function(){
            $scope.wfInfo = {processKey:'bulletin', "businessId":$scope.queryParamId};
            $scope.initUpdate($scope.queryParamId);
        };

        //处理附件列表
        $scope.reduceAttachment = function(attachment, id){
            $scope.newAttachment = attach_list("bulletin", id, "BulletinMattersDetail").result_data;
            for(var i in attachment){
                var file = attachment[i];
                console.log(file);
                for (var j in $scope.newAttachment){
                    if (file.fileId == $scope.newAttachment[j].fileid){
                        $scope.newAttachment[j].newFile = '0';
                        $scope.newAttachment[j].fileName = file.oldFileName;
                        $scope.newAttachment[j].lastUpdateBy = file.lastUpdateBy;
                        $scope.newAttachment[j].lastUpdateData = file.lastUpdateData;
                        $scope.newAttachment[j].uuid = file.uuid;
                        break;
                    }
                }

                $scope.newAttachment_FK = attach_list("bulletin", id, "BulletinMattersDetail_FK").result_data;
            }
        };
        $scope.initUpdate = function(id){
            var url = srvUrl + "bulletinInfo/queryViewDefaultInfo.do";
            $http({
                method:'post',
                url: url,
                data: $.param({"businessId":id})
            }).success(function(result){
                console.log('BulletinMattersDetailView')
                var data = result.result_data;
                $scope.bulletinOracle = data.bulletinOracle;
                $scope.bulletin = data.bulletinMongo;
                $scope.auditLogs = data.logs;

                // 处理附件
                $scope.reduceAttachment(data.bulletinMongo.attachmentList, id);

                $scope.initPage();
                hide_Mask();
            });
        };
        $scope.initDefaultData();
        $scope.initPage = function(){
            if($scope.bulletinOracle.AUDITSTATUS=="1" || $scope.bulletinOracle.AUDITSTATUS=="2"){
                //流程已启动
                $("#submitBtn").hide();
                $scope.wfInfo.businessId = $scope.queryParamId;
                $scope.refreshImg = Math.random()+1;
            }else{
                //未启动流程
                $("#submitBtn").show();
            }
        }
        //提交
        $scope.showSubmitModal = function(){
            $scope.approve = {
                operateType: "submit",
                processKey: "bulletin",
                businessId: $scope.queryParamId,
                callbackSuccess: function(result){
                    $.alert(result.result_name);
                    $('#submitModal').modal('hide');
                    $("#submitBtn").hide();
                    $scope.initData();
                },
                callbackFail: function(result){
                    $.alert(result.result_name);
                }
            };
            $('#submitModal').modal('show');
        };
        $scope.selectAll = function(){
            if($("#all").attr("checked")){
                $(":checkbox[name='choose']").attr("checked",1);
            }else{
                $(":checkbox[name='choose']").attr("checked",false);
            }
        }
        //$scope._init_uuid_ = $scope.credentials.UUID;
        //$scope._init_messages_array_ = _init_query_messages_list_($routeParams.id);
        ////////////审批阶段对留言编辑权限的控制
        //var curTask = wf_getCurrentTask('bulletin', $routeParams.id);
        //$scope._message_publish_reply_ = !isEmpty(curTask) && curTask.TASK_DEF_KEY_ != 'usertask7';
    }]);
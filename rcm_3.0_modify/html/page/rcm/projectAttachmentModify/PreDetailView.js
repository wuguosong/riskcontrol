ctmApp.register.controller('PreDetailViewModify', ['$routeParams','$http','$scope','$location', function ($routeParams,$http,$scope,$location) {
	$scope.businessId = $routeParams.id;
	$scope.oldUrl = $routeParams.url;
	
	//初始化数据
	$scope.initData = function(){
		$scope.WF_STATE = '0';
		$scope.initUpdate($scope.businessId);
		$scope.queryAuditLogsByBusinessId($scope.businessId);
		$scope.initPage();
	}
	
	//流程图相关
	$scope.initPage = function(){
		$scope.wfInfo = {processKey:'preReview'};
		$scope.wfInfo.businessId = $scope.businessId;
		$scope.refreshImg = Math.random()+1;
	}

    //处理附件列表
    $scope.reduceAttachment = function(attachment, id){
        $scope.newAttachment = attach_list("preReview", id, "preInfo").result_data;
        for(var i in attachment){
            var file = attachment[i];
            console.log(file);
            for (var j in $scope.newAttachment){
                if (file.fileId == $scope.newAttachment[j].fileid){
                    $scope.newAttachment[j].fileName = file.fileName;
                    $scope.newAttachment[j].type = file.type;
                    $scope.newAttachment[j].itemType = file.itemType;
                    $scope.newAttachment[j].programmed = file.programmed;
                    $scope.newAttachment[j].approved = file.approved;
                    $scope.newAttachment[j].lastUpdateBy = file.lastUpdateBy;
                    $scope.newAttachment[j].lastUpdateData = file.lastUpdateData;
                    break;
                }
            }

        }
    };
	
	//获取预评审信息
	$scope.initUpdate = function(businessId){
		$http({
			method:'post',  
		    url:srvUrl+'preInfo/getPreByID.do', 
		    data: $.param({"businessId":businessId})
		}).success(function(data){
			$scope.pre  = data.result_data.mongo;
			$scope.pre.oracle  = data.result_data.oracle;
			$scope.pre.apply.projectModel;

            // 处理附件需要的数据
            $scope.serviceType = angular.copy($scope.pre.apply.serviceType);
            $scope.projectModel = angular.copy($scope.pre.apply.projectModel);

			$scope.attach = data.result_data.attach;
			//处理附件
            $scope.reduceAttachment(data.result_data.mongo.attachmentList, businessId);
			
			
			var ptNameArr=[],pmNameArr=[],pthNameArr=[],fgNameArr=[],pt1NameArr=[];
			var pm=$scope.pre.apply.projectModel;
			if(null!=pm && pm.length>0){
				for(var j=0;j<pm.length;j++){
					pmNameArr.push(pm[j].VALUE);
				}
				$scope.pre.apply.projectModel=pmNameArr.join(",");
			}
			
			var pt1=$scope.pre.apply.serviceType;
			$scope.serviceTypes = $scope.pre.apply.serviceType;
			if(null!=pt1 && pt1.length>0){
				for(var i=0;i<pt1.length;i++){
					pt1NameArr.push(pt1[i].VALUE);
				}
				$scope.pre.apply.serviceType=pt1NameArr.join(",");
			}
			hide_Mask();
		});
	}
	
	//获取审核日志
	$scope.queryAuditLogsByBusinessId = function (businessId){
    	var  url = 'preAudit/queryAuditedLogsById.do';
        $http({
			method:'post',
		    url: srvUrl + url,
		    data: $.param({"businessId":businessId})
		}).success(function(result){
			$scope.auditLogs = result.result_data;
		});
	};

    // 验证项目是否在流程或者评审中
    $scope.vaildProject = function(){
        var type = "preReview";
        $http({
            method:'post',
            url: srvUrl + 'common/validateProject.do',
            data: $.param({"type":type, "id": $scope.businessId})
        }).success(function(result){
            if (!result.approval.success){
                alert(result.approval.message);
                return;
            } else if (!result.review.success){
                alert(result.review.message);
                return;
            } else {
                $scope.beforeSubmit();
            }
        });
    };

    // 确认提交前验证附件
    $scope.beforeSubmit = function(){
        var serviceCode = $scope.serviceType[0].KEY;
        var projectModelName = '';
        if(isEmpty($scope.projectModel[0])) {
            projectModelName = $scope.projectModel.VALUE;
        } else {
            projectModelName = $scope.projectModel[0].VALUE;
        }
        var functionType = '预评审';
        $http({
            method:'post',
            url: srvUrl + 'preInfoCreate/checkAttachment.do',
            data: $.param({"json":JSON.stringify({"businessId":$scope.businessId,"serviceCode":serviceCode, "projectModelName": projectModelName, "functionType": functionType})})
        }).success(function(result){
            if (result.success) {
                if(!isEmpty(result.result_data)){
                    if (result.result_data[0].code != '500'){
                        var type = '';
                        var pmNameArr=[];
                        var pm=result.result_data;
                        if(null!=pm && pm.length>0){
                            for(var j=0;j<pm.length;j++){
                                pmNameArr.push(pm[j].ITEM_NAME);
                            }
                            type = pmNameArr.join("、");
                        }
                        alert("附件类型为" + type + "的附件没有添加，请添加后再提交！");
                        return;
                    } else {
                        alert("该模式附件类型为空，请联系管理员！");
                        return;
                    }
                } else {
                    $scope.showSubmitModal();
                }
            } else {
                $.alert(result.result_name);
            }
        });
    };
	
	//弹出审批框新版
	//提交
	$scope.showSubmitModal = function(){
		$scope.approve = {
			operateType: "submit",
			processKey: "formalAssessment",
			businessId: $scope.businessId,
			callbackSuccess: function(result){
				$.alert(result.result_name);
				$('#submitModal').modal('hide');
				$("#submibtnn").hide();
				$scope.initData();
			},
			callbackFail: function(result){
				$.alert(result.result_name);
			}
		};
		$('#submitModal').modal('show');
	}
	$scope.initData();
    $scope._init_uuid_ = $scope.credentials.UUID;
    $scope._init_messages_array_ = _init_query_messages_list_($routeParams.id);
    ////////////审批阶段对留言编辑权限的控制
    //var curTask = wf_getCurrentTask('preReview', $routeParams.id);
    //$scope._message_publish_reply_ = !isEmpty(curTask) && curTask.TASK_DEF_KEY_ != 'usertask8';
}]);

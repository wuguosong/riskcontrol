ctmApp.register.controller('FormalAssessmentAuditDetailView',['$http','$scope','$location','$routeParams','Upload','$timeout', '$filter',
 function ($http,$scope,$location,$routeParams,Upload,$timeout,$filter) {

	$scope.isShowOld = false;

     $scope.url = $routeParams.url;
	 /*加签功能初始化必需数据*/
     $scope.changeUserMapper = {"nameField": "NAME", "valueField": "VALUE"};
     $scope.checkedUser = {};
     $scope.callback = function () {
         $("#userSinDialog").modal('hide');
         $("#submitModal").modal('show');
     };
	 /*加签功能初始化必需数据*/
	//初始化
	$scope.businessId = $routeParams.id;
	$scope.taskMark = $routeParams.taskMark;
	$scope.isTaskEdit = 'false';
	$scope.isTaskLegalEdit = 'false';
	$scope.myTaskallocation = {"reviewLeader":null,"legalReviewLeader":null,"fixedGroup":[]};
//	//保存按钮控制器
//	$scope.showSaveBtn = false;
	//判断新旧流程
	if($scope.businessId.indexOf("@")>0){
		//获取路径
		$scope.oldURL = $location.url();
		//旧流程
		var params = $routeParams.id.split("@");
		$scope.businessId = params[0];
		$scope.isOldFlow = true;//新旧流程标识
		var action = $routeParams.action;
		if(null!=params[4] && ""!=params[4]){
			$scope.flag=params[4];
		}
	}else{
		//新流程
		$scope.oldUrl = $routeParams.url;
	}
	$scope.pfr={};
	$scope.pfr.apply = {};
	$scope.pfr.taskallocation={};
	$scope.pfr.approveAttachment = {};
	$scope.pfr.pfrBusinessUnitCommit=[];
	$scope.pfr.id=$scope.businessId;
	$scope.dic=[];
	$scope.task={};
	$scope.pfr.approveLegalAttachment = {};

	//专业评审控制器
	$scope.approveShow = true;
	//法律评审控制器
	$scope.approveLegalShow = true;

	//专家评审控制器
	$scope.approveMajorShow = true;
	$scope.isApproveMajorShow ='false';
	//专家评审意见
	$scope.pfr.approveMajorCommontsShow = true;

	//当前用户id
	$scope.currentUserId={};
	//旧流程当前节点判断
	$scope.oldFlowTask = function(){
		if($scope.oldURL.indexOf("ProjectFormalReviewDetail/Edit")>0){
			//投资经理修改、起草
			$scope.showController.isInvestmentManager = true;
		}else if($scope.oldURL.indexOf("PreReviewCommentsL/edit1")>0){
			//法律评审
			$scope.showController.isLegalReviewLeader = true;
			$scope.showSaveBtn = true;
			$("#legalReviewComments").show();
		}else if($scope.oldURL.indexOf("PreReviewCommentsR/approve")>0){
			//专业评审
			$scope.showController.isReviewLeader = true;
			$scope.showSaveBtn = true;
			$("#reviewComments").show();
		}else if($scope.oldURL.indexOf("PrimaryLegalFeedback")>0){
			//基层法务反馈
			$scope.showController.isGrassRootsLegal = true;
			$scope.showSaveBtn = true;
			$("#legalReviewComments").show();
		}else if($scope.oldURL.indexOf("ManagerFeedback/edit")>0){
			//投资经理反馈
			$scope.showController.isInvestmentManagerBack = true;
			$scope.showSaveBtn = true;
			$("#reviewComments").show();
		}else if($scope.oldURL.indexOf("TaskAssignment")>0){
			//分配任务节点
			$scope.showController.isTask = true;
			$scope.isTaskLegalEdit = 'true';
			$scope.isTaskEdit = 'true'
			$("#taskAssignment1").show();
		}
	}

	//新流程相关
	$scope.initPage = function(){
		if($scope.isOldFlow ){
			//旧流程
			$scope.oldFlowTask();
			if(typeof (params[1])=='string' && typeof (params[2])=='string' && params[1]!='' && params[2]!=''){//已经启动流程
				$scope.wfInfo = {processDefinitionId:params[1],processInstanceId:params[2],processKey:'formalAssessment'};
			}else{//未启动流程
				$scope.wfInfo = {processKey:'formalAssessment'};
			}
		}else{
			//新流程
			$scope.wfInfo = {processKey:'formalReview'};
			$scope.wfInfo.businessId = $scope.businessId;
			$scope.refreshImg = Math.random()+1;
		}
	}

	$scope.dealVersion = function(fileList){
		if(null == fileList|| !Array.isArray(fileList) || fileList.length == 0 ){
			return [];
		}
		var attachmentList = $scope.pfr.attachment;
		var versionObj = {};
		//1将所有类型附件的最大版本号找到
		for(var i = 0 ; i<attachmentList.length; i++){
			var files = attachmentList[i].files;
			versionObj[attachmentList[i].UUID] = 0 ;
			for(var y = 0 ; y<files.length; y++){
				if((files[y].version+0) > versionObj[attachmentList[i].UUID]){
					versionObj[attachmentList[i].UUID] = files[y].version;
				}
			}
		}
		//2循环反馈的附件，给没有版本号的附件设置版本号
		for(var i = 0 ; i<fileList.length; i++){
			var uuid = fileList[i].attachmentUList.UUID;

			if( (null == fileList[i].attachment_new || '' == fileList[i].attachment_new) ||
				(fileList[i].attachment_new.version != null && '' != fileList[i].attachment_new.version)){

				if( typeof(fileList[i].attachment_new.version) == "number"){
					fileList[i].attachment_new.version = fileList[i].attachment_new.version+'';
				}
				continue;
			}

			fileList[i].attachment_new.version = versionObj[uuid]*1+1+'';
			versionObj[uuid] = versionObj[uuid]*1+1;
		}
		return fileList;
	}
	$scope.reduceVersion = function(approveLegalAttachment){
		if(null == approveLegalAttachment || '' == approveLegalAttachment){
			return null;
		}
		if(null != approveLegalAttachment.attachmentNew && approveLegalAttachment.attachmentNew.length != 0 && Array.isArray(approveLegalAttachment.attachmentNew) ){
			for(var i = 0 ; i<approveLegalAttachment.attachmentNew.length; i++){
				if(null != approveLegalAttachment.attachmentNew[i].attachment_new && '' != approveLegalAttachment.attachmentNew[i].attachment_new){
					approveLegalAttachment.attachmentNew[i].attachment_new.version = approveLegalAttachment.attachmentNew[i].attachment_new.version +'';
				}
			}
		}
		return approveLegalAttachment;
	}

	//局部保存
	$scope.save = function(callback){
		show_Mask();

		//法律评审负责人//基层法务
		if($scope.showController.isLegalReviewLeader || $scope.showController.isGrassRootsLegal){
			var attachmentNew =  $scope.pfr.approveLegalAttachment.attachmentNew;
			for(var i in attachmentNew){
				if(attachmentNew[i].isLegalEdit =='1'){
					if(attachmentNew[i].attachmentUList == null || attachmentNew[i].attachmentUList=="" || attachmentNew[i].attachmentUList==undefined){
						$.alert("请选择需要更新的附件！");
						hide_Mask();
						return false;
					}
				}
			}
//			if($scope.showController.isGrassRootsLegal){
				//处理附件版本号
				var attachmentList = $scope.pfr.attachment;
				var approveAttachment = $scope.pfr.approveLegalAttachment;
				if(null != approveAttachment.attachmentNew && approveAttachment.attachmentNew.length > 0 ){
					var attachmentNew = approveAttachment.attachmentNew;
					$scope.pfr.approveLegalAttachment.attachmentNew = $scope.dealVersion(attachmentNew);
				}
//			}
			//处理版本号是数字问题
			$scope.pfr.approveLegalAttachment = $scope.reduceVersion($scope.pfr.approveLegalAttachment);
			$http({
				method:'post',
			    url: srvUrl + "formalAssessmentInfo/saveLegalReviewInfo.do",
			    data:$.param({"businessId":$scope.businessId,"json":JSON.stringify(angular.copy($scope.pfr.approveLegalAttachment))})
			}).success(function(data){
				if(data.success){
					if(callback){

						//验证法律负责人
						if($scope.showController.isLegalReviewLeader){
							var commentsList =  $scope.pfr.approveLegalAttachment.commentsList;
							for(var i in commentsList){
								if(commentsList[i].isLegalEdit =='1'){
									if(commentsList[i].opinionType == null || commentsList[i].opinionType==""){
										$.alert("请填写意见类型！");
										hide_Mask();
										return false;
									}
								}
							}
							if($scope.pfr.approveLegalAttachment.riskWarning == null || $scope.pfr.approveLegalAttachment.riskWarning ==""|| $scope.pfr.approveLegalAttachment.riskWarning ==undefined){
								$.alert("请填写法律风险提示！");
								hide_Mask();
								return false;
							}
							var attachmentNew =  $scope.pfr.approveLegalAttachment.attachmentNew;
							for(var i in attachmentNew){
								if(attachmentNew[i].isLegalEdit =='1'){
									if(attachmentNew[i].attachmentUList == null || attachmentNew[i].attachmentUList=="" || attachmentNew[i].attachmentUList==undefined){
										$.alert("请选择需要更新的附件！");
										hide_Mask();
										return false;
									}
								}
							}
						}
						//验证基层法务反馈内容
						if($scope.showController.isGrassRootsLegal){
							var commentsList =  $scope.pfr.approveLegalAttachment.commentsList;
							for(var i in commentsList){
								if(commentsList[i].isGrassEdit =='1'){
									if(commentsList[i].commentDepartment == null || commentsList[i].commentDepartment==""){
										$.alert("请根据法律负责人的要求填写反馈内容！");
										hide_Mask();
										return false;
									}
								}
							}
						}

						hide_Mask();
						callback();
					}else{
						$scope.initData();
						hide_Mask();
						$.alert(data.result_name);
					}
				}else{
					hide_Mask();
					$.alert("保存失败");
				}
			});
		}else if($scope.showController.isReviewLeader || $scope.showController.isInvestmentManagerBack){
			//评审负责人 //投资经理反馈

			// 保存是否上会字段
            $http({
                method: 'post',
                url: srvUrl + "formalAssessmentInfoCreate/saveNeedMeeting.do",
                data: $.param({
                    "businessId": $scope.approve.businessId,
                    "needMeeting": $scope.pfrOracle.NEED_MEETING
                })
            }).success(function (result) {
                hide_Mask();
                var attachmentNew =  $scope.pfr.approveAttachment.attachmentNew;
                for(var i in attachmentNew){
                    if(attachmentNew[i].isReviewLeaderEdit =='1'){
                        if(attachmentNew[i].attachmentUList == null || attachmentNew[i].attachmentUList=="" || attachmentNew[i].attachmentUList==undefined){
                            $.alert("请选择需要更新的附件！");
                            hide_Mask();
                            return false;
                        }
                    }
                }
//			if($scope.showController.isInvestmentManagerBack){
                //处理附件版本号
                var attachmentList = $scope.pfr.attachment;
                var approveAttachment = $scope.pfr.approveAttachment;
                if(null != approveAttachment.attachmentNew && approveAttachment.attachmentNew.length > 0 ){
                    var attachmentNew = approveAttachment.attachmentNew;
                    $scope.pfr.approveAttachment.attachmentNew = $scope.dealVersion(attachmentNew);
                }
//			}
                $scope.pfr.approveAttachment = $scope.reduceVersion($scope.pfr.approveAttachment);
                $http({
                    method:'post',
                    url: srvUrl + "formalAssessmentInfo/saveReviewInfo.do",
                    data:$.param({"businessId":$scope.businessId,"json":JSON.stringify(angular.copy($scope.pfr.approveAttachment)),"professionalReviewersJson":JSON.stringify($scope.myTaskallocation.professionalReviewers)})
                }).success(function(data){
                    if(!data.success){
                        $.alert("保存失败");
                        hide_Mask();
                        return;
                    }
                    if(callback){
                        //验证
                        if($scope.showController.isReviewLeader){
                            var commentsList =  $scope.pfr.approveAttachment.commentsList;
                            for(var i in commentsList){
                                if(commentsList[i].isReviewLeaderEdit =='1'){
                                    if(commentsList[i].opinionType == null || commentsList[i].opinionType==""){
                                        $.alert("请填写意见类型！");
                                        hide_Mask();
                                        return false;
                                    }
                                }
                            }
                            if($scope.pfr.approveAttachment.riskWarning == null || $scope.pfr.approveAttachment.riskWarning ==""|| $scope.pfr.approveAttachment.riskWarning ==undefined){
                                $.alert("请填写重点风险提示！");
                                hide_Mask();
                                return false;
                            }

                            var attachmentNew =  $scope.pfr.approveAttachment.attachmentNew;
                            for(var i in attachmentNew){
                                if(attachmentNew[i].isReviewLeaderEdit =='1'){
                                    if(attachmentNew[i].attachmentUList == null || attachmentNew[i].attachmentUList=="" || attachmentNew[i].attachmentUList==undefined){
                                        $.alert("请选择需要更新的附件！");
                                        hide_Mask();
                                        return false;
                                    }
                                }
                            }
                        }
                        if($scope.showController.isInvestmentManagerBack){
                            var commentsList =  $scope.pfr.approveAttachment.commentsList;
                            for(var i in commentsList){
                                if(commentsList[i].isInvestmentManagerBackEdit =='1'){
                                    if(commentsList[i].commentDepartment == null || commentsList[i].commentDepartment==""){
                                        $.alert("请根据评审负责人的要求填写反馈内容！");
                                        hide_Mask();
                                        return false;
                                    }
                                }
                            }
                        }
                        hide_Mask();
                        callback();
                    }else{
                        $.alert(data.result_name);
                        $scope.initData();
                        hide_Mask();
                    }

                });
            });

		}else if($scope.showController.isMajorMember ){
			//保存  专家评审意见
			$http({
				method:'post',
			    url: srvUrl + "formalAssessmentInfo/saveMajMemberInfo.do",
			    data:$.param({"businessId":$scope.businessId,"json":JSON.stringify($scope.pfr.approveMajorCommonts)})
			}).success(function(data){
				if(!data.success){
					$.alert("保存失败");
					hide_Mask();
					return;
				}
				if(callback){
					//验证
					if($scope.showController.isMajorMember){
						var falg = true;
						for(var i = 0; i < $scope.pfr.approveMajorCommonts.length; i++){
							var applyID = $scope.pfr.approveMajorCommonts[i].majorApply.VALUE;
							if($scope.currentUserId == applyID){ //当前用户的id和页面tab的id一直才需要校验
								var conclusion = $scope.pfr.approveMajorCommonts[i].conclusion;
								if(conclusion == null || conclusion =="" || conclusion ==undefined){
									$.alert("请填写专业评审结论！");
									hide_Mask();
									return false;
									break;
								}
								var commentsList = $scope.pfr.approveMajorCommonts[i].commentsList;
								for(var j = 0; j < commentsList.length; j++){
									var professionalQuestion = commentsList[j].professionalQuestion;
									if(professionalQuestion == null || professionalQuestion =="" || professionalQuestion ==undefined){
										$.alert("请填写专业问题！");
										hide_Mask();
										return false;
										break;
									}

								}
							}


						}
						//return falg;
					}
					/*
					if($scope.showController.isMajorMember){
						if($scope.pfr.approveMajorCommonts.description == null || $scope.pfr.approveMajorCommonts.description ==""|| $scope.pfr.approveMajorCommonts.description ==undefined){
							$.alert("请填写特殊说明！");
							return false;
						}
					}
					*/
					hide_Mask();
					callback();
				}else{
					$.alert(data.result_name);
					hide_Mask();
				}

			});
		}else{
			hide_Mask();
			if(callback){
				callback();
			}
		}
	}

	/*
	 * 根据业务id查询节点信息
	 */
	$scope.getTaskInfoByBusinessId = function(businessId,processKey){
		var paramObj = {"businessId":businessId,"processKey":processKey};
		if($scope.taskMark != null && $scope.taskMark !=''){
			paramObj.taskMark = $scope.taskMark;
		}

		$http({
			method:'post',
		    url: srvUrl + "formalAssessmentAudit/queryTaskInfoByBusinessId.do",
		    data:$.param(paramObj)
		}).success(function(data){
			$scope.hasWaiting = data.result_data.taskId==null?false:true;
			if($location.url().indexOf("ProjectFormalReviewDetailView/View")>0){
				$scope.hasWaiting = false;
			}
			$scope.taskInfo = data.result_data;
			if(data.result_data.description != null && data.result_data.description != ""){
				//判断是否为旧流程
				if(!$scope.isOldFlow){
					var document = JSON.parse(data.result_data.description);
					//大区
					if(document.isLargeArea != undefined && document.isLargeArea!=null&&document.isLargeArea!=""){
						$scope.showController.isLargeArea = true;
					}
					//大区反馈
					else if(document.isLargeAreaBack != undefined && document.isLargeAreaBack!=null&&document.isLargeAreaBack!=""){
						$scope.showController.isLargeAreaBack = true;
						$("#reviewComments").show();
					}
					//投资中心/水环境
					else if (document.isServiewType != undefined && document.isServiewType!=null&&document.isServiewType!=""){
						$scope.showController.isServiewType = true;
						$("#reviewComments").show();
					}
					//任务分配
					else if (document.isTask != undefined && document.isTask!=null && document.isTask!=""){
						$scope.showController.isTask = true;

						if(document.isSignLegal != null){
							$scope.showController.isSignLegal = true;
						}else{
							$scope.isTaskLegalEdit = 'true';
						}

						$scope.isTaskEdit = 'true'
						$("#taskAssignment1").show();
					}
					//投资经理
					else if (document.isInvestmentManager != undefined && document.isInvestmentManager!=null&&document.isInvestmentManager!=""){
						$scope.showController.isInvestmentManager = true;
					}
					//法律评审负责人
					else if (document.isLegalReviewLeader != undefined && document.isLegalReviewLeader!=null&&document.isLegalReviewLeader!=""){
						$scope.showController.isLegalReviewLeader = true;
						$scope.showSaveBtn = true;
						$("#legalReviewComments").show();
					}
					//基层法务
					else if (document.isGrassRootsLegal != undefined && document.isGrassRootsLegal != null && document.isGrassRootsLega != ""){
						$scope.showController.isGrassRootsLegal = true;
						$scope.showSaveBtn = true;
						$("#legalReviewComments").show();
					}
					//一级法务
					else if (document.isFirstLevelLawyer != undefined && document.isFirstLevelLawyer!=null&&document.isFirstLevelLawyer!=""){
						$scope.showController.isFirstLevelLawyer = true;
						$("#legalReviewComments").show();
					}
					//固定小组成员
					else if (document.isGroupMember != undefined && document.isGroupMember!=null&&document.isGroupMember!=""){
						$scope.showController.isGroupMember = true;
					}
					//评审负责人
					else if (document.isReviewLeader != undefined && document.isReviewLeader!=null&&document.isReviewLeader!=""){
						//专家评审选择框
						$scope.showController.isReviewLeader = true;
						$scope.isApproveMajorShow = 'true';
						$scope.showSaveBtn = true;
						$("#reviewComments").show();
						//专家评审选择 div
						$("#taskAssignment5").show();
					}
					//专家评审填写意见
					else if (document.isMajorMember != undefined && document.isMajorMember!=null&&document.isMajorMember!=""){
						$scope.showController.isMajorMember = true;
						$("#majorMemberComments").show();
						$scope.showSaveBtn = true;
					}
					//评审负责人确认
					else if (document.isReviewLeaderConfirm != undefined && document.isReviewLeaderConfirm!=null&&document.isReviewLeaderConfirm!=""){
						$scope.showController.isReviewLeaderConfirm = true;
					}
					//投资经理反馈
					else if (document.isInvestmentManagerBack != undefined && document.isInvestmentManagerBack!=null&&document.isInvestmentManagerBack!=""){
						$scope.showController.isInvestmentManagerBack = true;
						$scope.showSaveBtn = true;
						$("#reviewComments").show();
					}
					//
					else if(document.isSelectLegalLeader != null && document.isSelectLegalLeader  != ''){
						$scope.showController.isSelectLegalLeader = true;
						$scope.isTaskLegalEdit = 'true';
					}


					if($scope.taskMark != null && $scope.taskMark != ''){
						$scope.showController = {};
						if($scope.taskMark == 'managerTask'){
							//投资经理起草
							$scope.showController.isInvestmentManager = true;
						}else if($scope.taskMark == 'businessAreaTask'){
							//业务区审批
						}else if($scope.taskMark == 'businessLeaderTask'){
							//分管领导审批
						}else if($scope.taskMark == 'largeAreaTask'){
							//大区审批
							$scope.showController.isLargeArea = true;
						}else if($scope.taskMark == 'serviceTypeTask'){
							//双投审批
							$scope.showController.isServiewType = true;
							$("#reviewComments").show();
						}else if($scope.taskMark == 'missionTask'){
							//任务分配节点
							$scope.showController.isTask = true;
							//判断是否是单独分配评审负责人
							if(document.isSignLegal != null){
								$scope.showController.isSignLegal = true;
							}else{
								$scope.isTaskLegalEdit = 'true';
							}
							$scope.isTaskEdit = 'true'
							$("#taskAssignment1").show();
						}else if($scope.taskMark == 'selectLegalLeaderTask'){
							//法律分配节点
							$scope.showController.isSelectLegalLeader = true;
							$scope.isTaskLegalEdit = 'true';
						}else if($scope.taskMark == 'legalLeaderTask'){
							//法律负责人节点
							$scope.showController.isLegalReviewLeader = true;
							$scope.showSaveBtn = true;
							$("#legalReviewComments").show();
						}else if($scope.taskMark == 'grassRootsLegalTask'){
							//基层法务节点
							$scope.showController.isGrassRootsLegal = true;
							$scope.showSaveBtn = true;
							$("#legalReviewComments").show();
						}else if($scope.taskMark == 'firstLawyerTask'){
							//一级法务节点
							$scope.showController.isFirstLevelLawyer = true;
							$("#legalReviewComments").show();
						}else if($scope.taskMark == 'reviewLeaderTask'){
							//评审负责人节点
							$scope.showController.isReviewLeader = true;
							$scope.showSaveBtn = true;
							$("#reviewComments").show();
							//专家评审选择 div
							$("#taskAssignment5").show();

						}else if($scope.taskMark == 'managerBackTask'){
							//投资经理反馈节点
							$scope.showController.isInvestmentManagerBack = true;
							$scope.showSaveBtn = true;
							$("#reviewComments").show();
						}else if($scope.taskMark == 'businessAreaBackTask'){
							//业务区反馈节点
						}else if($scope.taskMark == 'businessLeaderBackTask'){
							//分管领导反馈节点
						}else if($scope.taskMark == 'reviewAreaTask'){
							//大区反馈节点
							$scope.showController.isLargeAreaBack = true;
							$("#reviewComments").show();
						}else if($scope.taskMark == 'reviewConfirmTask'){
							//评审负责人确认节点
							$scope.showController.isReviewLeaderConfirm = true;
						}
					}

				}
			}
		});
	}

//	$scope.getUserByRoleCode = function(code){
//		$http({
//			method:'post',
//		    url: srvUrl + "role/queryRoleuserByCode.do",
//		    data:$.param({"code":code})
//		}).success(function(data){
//			for(var i in data.result_data){
//				if(data.result_data[i].VALUE == $scope.credentials.UUID){
//					$scope.showController.TaskPanel = true;
//				}
//			}
//		});
//	}
	//初始化数据
	$scope.initData = function(){
        if ($scope.url == $filter('encodeURI')('#/FormalAssessmentAuditList/0')){
            $scope.WF_STATE = '0';
        } else {
            $scope.WF_STATE = '1';
        }
		//面板控制器
		$scope.showController={};
		//保存按钮控制器
		$scope.showSaveBtn = false;
		$scope.initPage();
		$scope.initUpdate($scope.businessId);
		$scope.getTaskInfoByBusinessId($scope.businessId,$scope.wfInfo.processKey);
		$scope.queryAuditLogsByBusinessId($scope.businessId);
//		$scope.getUserByRoleCode("5");
//		$scope.getSelectTypeByCodeRole("8");
	}

//	$scope.getSelectTypeByCodeRole=function(typeCode){
//        var  url = 'common/commonMethod/getRoleuserByCode';
//        $scope.httpData(url,typeCode).success(function(data){
//            if(data.result_code === 'S'){
//                $scope.userRoleListall=data.result_data;
//            }else{
//                alert(data.result_name);
//            }
//        });
//    }

    /* $scope.getReplaceReasonsList = function(businessId){
         var  url = 'cloud/getReplaceReasonList.do';
         $http({
             method:'post',
             url:srvUrl+url,
             data: $.param({"id":businessId})
         }).success(function(result){
         	$scope.replaceReasons = result.result_data;
         });
	 };*/

	//固定小组成员选人指令提供参数
	$scope.queryParams = {code:"4"};
	$scope.fixGroupModelCallBack = function(checkedUsers){
		$scope.myTaskallocation.fixedGroup = checkedUsers;
		var name = "";
		for(var i in checkedUsers){
			name += ","+checkedUsers[i].NAME;
		}
		name = name.substring(1);
		$scope.fixgroupMemberName = name;

	}
	$scope.showFixGroupModel = function(){
		$("#fixGroupModel").modal('show');
	}


	//专业评审选人指令提供参数
	$scope.queryParams = {code:"4"};
	$scope.professionalReviewersModelCallBack = function(checkedUsers){
		$scope.myTaskallocation.professionalReviewers = checkedUsers;
		var name = "";
		for(var i in checkedUsers){
			name += ","+checkedUsers[i].NAME;
		}
		name = name.substring(1);
		$scope.professionalReviewers = name;

	}
	$scope.showProfessionalReviewersModel = function(){
		$("#professionalReviewersModel").modal('show');
	}
	//获取审核日志
	$scope.queryAuditLogsByBusinessId = function (businessId){
    	var  url = 'formalAssessmentAudit/queryAuditedLogsById.do';
        $http({
			method:'post',
		    url: srvUrl + url,
		    data: $.param({"businessId":businessId})
		}).success(function(result){
			$scope.auditLogs = result.result_data;
		});
	}
	//提交流程 旧流程
	$scope.showOldSubmitModal = function(type){

		$scope.save(function(){
			if($scope.oldURL.indexOf("ProjectFormalReviewDetail/Edit")>0){
				$scope.investmentManagerNodeAudit();
			}else if($scope.oldURL.indexOf("ProjectFormalReviewView")>0){
				$scope.reviewLeaderConfirmNodeAudit();
			}else if($scope.oldURL.indexOf("ManagerFeedback")>0){
				//投资经理反馈
				$scope.investManagerFeedbackNodeAudit();
			}else if($scope.oldURL.indexOf("ReviewTeamComments")>0){
				//固定小组成员
				$scope.fixGroupNodeAudit();
			}else if($scope.oldURL.indexOf("PreReviewCommentsR")>0){
				//评审负责人
				$scope.reviewLeaderNodeAudit();
			}else if($scope.oldURL.indexOf("PrimaryLegalFeedback")>0){
				//基层法务反馈
				$scope.LegalFeedbackNodeAudit();
			}else if($scope.oldURL.indexOf("PreReviewCommentsL")>0){
				//法律评审
				$scope.legalNodeAudit();
			}else if($scope.oldURL.indexOf("TaskAssignment")>0){
				$scope.taskNodeAudit();
			}else if($routeParams.action=='View'){
				var companyHeader = $scope.pfr.apply.companyHeader;
				$scope.approve = [{
					submitInfo:{
						startVar:{processKey:'formalAssessment',businessId:params[0],subject:$scope.pfr.apply.projectName+'正式评审申请',inputUser:$scope.credentials.UUID},
						runtimeVar:{inputUser:companyHeader.VALUE},
						currentTaskVar:{opinion:'请审批'},
                        businessId:params[0]
					},
					showInfo:{destination:'单位负责人审核',approver: companyHeader.NAME}
				}];
				if($routeParams.action =='View' && params[1]){
					$scope.approve[0].submitInfo.taskId=params[3];
				}
			}else if($routeParams.action=='view1'){
				var approveUser =  $scope.pfr.apply.investmentPerson;
				if(approveUser == null || typeof(approveUser)=='undefined' || approveUser.NAME==null||approveUser.NAME==""){
					approveUser = $scope.pfr.apply.directPerson;
				}
				var investmentManager = $scope.pfr.apply.investmentManager;//投资经理
				var nextNode;
				if(approveUser==null||approveUser.NAME==null||approveUser==""||approveUser.NAME==""){
					var url=srvUrl+"common/commonMethod/getRoleuserByCode";
			        $.ajax({
			        	url: url,
			        	type: "POST",
			        	data:"5",
			        	dataType: "json",
			        	async: false,
			        	success: function(data){
		    				if(data.result_code == 'S'){
		    					var ma = data.result_data[0];
		    					var investmentManager = $scope.pfr.apply.investmentManager;//投资经理
		    					if(ma){
		    						nextNode = {
		    							submitInfo:{
		    								taskId:params[3],
		    								businessId:params[0],
		    								runtimeVar:{inputUser:ma.VALUE,toTask:'1',isSkipServiceType:'1'},
		    								currentTaskVar:{opinion:'请审批'},
		    								noticeInfo:{
		    									infoSubject:$scope.pfr.apply.projectName+'正式评审申请进入风控审批阶段，请悉知！',
		    									businessId:params[0],
		    									remark:'',
		    									formKey:'/ProjectFormalReviewDetailView/View/'+params[0]+'@'+params[1]+'@'+params[2],
		    									createBy:$scope.credentials.UUID,
		    									reader:$scope.removeObjByValue(removeDuplicate($scope.relationUsers),ma.VALUE),
		    									type:'1',
		    									custText01:'正式评审'
		    								}
		    							},
		    							showInfo:{destination:'分配评审任务',approver: ma.NAME}
		    						};
		    					}else{
									$.alert("评审分配人不存在，请先设置评审分配人！");
								}
		    				}
			        	}
			        });
				}else{
					nextNode = {
						submitInfo:{
							taskId:params[3],
							businessId:params[0],
							runtimeVar:{inputUser:approveUser.VALUE,toTask:'1',isSkipServiceType:'0'},
							currentTaskVar:{opinion:'请审批'}
						},
						showInfo:{destination:'投资中心/水环境投资中心审核',approver: approveUser.NAME}
					};
				}
				var preNode = {
					submitInfo:{
						taskId:params[3],
						businessId:params[0],
						runtimeVar:{inputUser:investmentManager.VALUE,toTask:'-1'},
						currentTaskVar:{opinion:'请修改'}
					},
					showInfo:{destination:'退回起草人',approver:investmentManager.NAME}
				};
				$scope.approve=[nextNode, preNode];
			}else if($routeParams.action=='view2'){
				var url=srvUrl+"common/commonMethod/getRoleuserByCode";
		        $.ajax({
		        	url: url,
		        	type: "POST",
		        	data:"5",
		        	dataType: "json",
		        	async: false,
		        	success: function(data){
	    				if(data.result_code == 'S'){
	    					var ma = data.result_data[0];
	    					var investmentManager = $scope.pfr.apply.investmentManager;
	    					if(ma){
	    						var currentTaskVar = {opinion:'请审批'};
	    						if(true){
	    							$("#cesuanFileOpinionDiv").show();
	    							$("#tzProtocolOpinionDiv").show();
	    							currentTaskVar.cesuanFileOpinion='';
	    							currentTaskVar.tzProtocolOpinion='';
	    						}
	    						$scope.approve = [{
	    							submitInfo:{
	    								taskId:params[3],
	    								businessId:params[0],
	    								runtimeVar:{inputUser:ma.VALUE,toTask:'1'},
	    								currentTaskVar:currentTaskVar,
	    								noticeInfo:{
	    									infoSubject:$scope.pfr.apply.projectName+'正式评审申请进入风控审批阶段，请悉知！',
	    									businessId:params[0],
	    									remark:'',
	    									formKey:'/ProjectFormalReviewDetailView/View/'+params[0]+'@'+params[1]+'@'+params[2],
	    									createBy:$scope.credentials.UUID,
	    									reader:$scope.removeObjByValue(removeDuplicate($scope.relationUsers),ma.VALUE),
	    									type:'1',
	    									custText01:'正式评审'
	    								}
	    							},
	    							showInfo:{destination:'分配评审任务',approver: ma.NAME}
	    						},{
	    							submitInfo:{
	    								taskId:params[3],
	    								businessId:params[0],
	    								runtimeVar:{inputUser:investmentManager.VALUE,toTask:'-1'},
	    								currentTaskVar:{opinion:'请修改'}
	    							},
	    							showInfo:{destination:'退回起草人',approver:investmentManager.NAME}
	    						}];
	    					}else{
								$.alert("评审分配人不存在，请先设置评审分配人！");
							}
	    				}
		        	}
		        });
			}
			$('#oldSubmitModal').modal('show');
		});
	}
	//投资经理修改节点信息
	$scope.investmentManagerNodeAudit = function(){
		var companyHeader = $scope.pfr.apply.companyHeader;
		$scope.approve = [{
			submitInfo:{
				startVar:{processKey:'formalAssessment',businessId:$scope.pfr.id,subject:$scope.pfr.apply.projectName+'正式评审申请',inputUser:$scope.credentials.UUID},
				runtimeVar:{inputUser:companyHeader.VALUE},
				currentTaskVar:{opinion:'请审批'},
                businessId:params[0]
			},
			showInfo:{destination:'单位负责人审核',approver: companyHeader.NAME}
		}];
		if(($routeParams.action =='Update' || $routeParams.action=='Edit') && params[1]){
			$scope.approve[0].submitInfo.taskId=params[3];
		}
	}

	//提交材料及报告提交框信息
	$scope.reviewLeaderConfirmNodeAudit = function(){
        var curUser = $scope.credentials;
        var newArray = $scope.userRoleListall.concat($scope.relationUsers);
        $scope.relationUsers = removeDuplicate(newArray);
        $scope.approve = [{
            toNodeType:'end',
            submitInfo:{
                taskId:params[3],
                noticeInfo:{
                    infoSubject:$scope.pfr.apply.projectName+'正式评审决策材料',
                    businessId:$scope.pfr.id,
                    remark:'',
                    formKey:'/FormalBiddingInfoReview/'+$scope.businessId+'/0',
                    createBy:curUser.UUID,
                    reader:$scope.relationUsers,
                    type:'1',
                    custText01:'正式评审'
                },
                runtimeVar:{},
                businessId:params[0]
            }
        }];
	}

	//投资经理反馈提交框信息
	$scope.investManagerFeedbackNodeAudit = function(){
            var investmentManager = $scope.pfr.apply.investmentManager;//投资经理
            if(action == 'edit'){
                var companyHeader = $scope.pfr.apply.companyHeader;//单位负责人
                $scope.approve = [{
                    submitInfo:{
                        taskId:params[3],
                        runtimeVar:{inputUser:companyHeader.VALUE},
                        currentTaskVar:{opinion:'请审批'},
                        businessId:params[0]
                    },
                    showInfo:{destination:'单位负责人审核',approver: companyHeader.NAME}
                }];
            }else if(action == 'view1'){
                var approveUser =  $scope.pfr.apply.investmentPerson;
                if(approveUser == null || typeof(approveUser)=='undefined'|| approveUser.NAME==null||approveUser.NAME==""){
                    approveUser = $scope.pfr.apply.directPerson;
                }
                if(approveUser == null ||approveUser==""||approveUser.NAME==null|| approveUser.NAME == ""){
                	var reviewLeader =  $scope.pfr.taskallocation.reviewLeader;//评审负责人
                    $scope.approve = [{
                        submitInfo:{
                            taskId:params[3],
                            runtimeVar:{inputUser:reviewLeader.VALUE,toTask:'1',isSkipServiceType:"1"},
                            currentTaskVar:{opinion:'请审批'},
                            businessId:params[0]
                        },
                        showInfo:{destination:'评审负责人确认',approver:reviewLeader.NAME}
                    }];
                }else{
                	$scope.approve = [{
                        submitInfo:{
                            taskId:params[3],
                            runtimeVar:{inputUser:approveUser.VALUE,toTask:'1',isSkipServiceType:"0"},
                            currentTaskVar:{opinion:'请审批'},
                            businessId:params[0]
                        },
                        showInfo:{destination:'投资中心/水环境投资中心审核',approver: approveUser.NAME}
                    }];
                }
            }else if(action=='view2'){
                var reviewLeader =  $scope.pfr.taskallocation.reviewLeader;//评审负责人
                $scope.approve = [{
                    submitInfo:{
                        taskId:params[3],
                        runtimeVar:{inputUser:reviewLeader.VALUE,toTask:'1'},
                        currentTaskVar:{opinion:'请审批'},
                        businessId:params[0]
                    },
                    showInfo:{destination:'评审负责人确认',approver:reviewLeader.NAME}
                }];
            }
            var backInfo = {
                submitInfo:{
                    taskId:params[3],
                    runtimeVar:{inputUser:investmentManager.VALUE,toTask:'-1'},
                    currentTaskVar:{opinion:'请修改'},
                    businessId:params[0]
                },
                showInfo:{destination:'退回投资经理反馈',approver:investmentManager.NAME}
            };
            if(action.indexOf('view')!=-1){
                $scope.approve.push(backInfo);
            }
	}
	//旧流程固定小组成员提交框信息
	$scope.fixGroupNodeAudit = function(){
        var reviewLeader =  $scope.pfr.taskallocation.reviewLeader;//评审负责人
        $scope.approve = [{
            submitInfo:{
                taskId:params[3],
                runtimeVar:{inputUser:reviewLeader.VALUE},
                currentTaskVar:{opinion:'请审批'},
                businessId:params[0]
            },
            showInfo:{destination:'评审负责人确认',approver:reviewLeader.NAME}
        }];
	}

	//旧流程评审负责人初审提交框信息
	$scope.reviewLeaderNodeAudit = function(){

        var currentUserName = $scope.credentials.userName;
        var investmentManager = $scope.pfr.apply.investmentManager;//投资经理
        var reviewLeader =  $scope.pfr.taskallocation.reviewLeader;//评审负责人
        var action = $routeParams.action;
        if(action=='approve'){
        	var task = null;
      	  	var url=srvUrl+"bpmn/queryTaskById.do";
      		var reqparams = {
      			taskId: params[3]
      		};
      		$.ajax({
      		  url: url,
      	    	type: "POST",
      	    	data: reqparams,
      	    	dataType: "json",
      	    	async: false,
      	    	success: function(data){
      	    		if(data.success){
      	    			task = data.result_data;
      	            }else{
      	                $.alert(data.result_name);
      	            }
      	    	}
      	    });
        	if(task != null && task.description == "isnew"){
        		//新流程
        		$scope.isNewBpmn = false;
        		$scope.approve = [];
        		for(var m = 0; m < task.outSequences.length; m++){
        			var sequenceFlow = new Object();
        			var flow = task.outSequences[m];
        			var document = flow.documentation;
        			var infos = document.split(",");
        			var destination = infos[0];
        			sequenceFlow.submitInfo = {
        				taskId: params[3],
        				businessId: $scope.businessId
        			};
        			if(infos.length == 1){
        				sequenceFlow.toNodeType = "end";
        				sequenceFlow.submitInfo.runtimeVar= {"sequenceFlow":flow.id};
        			}else{
        				var users = null;
        				var signs = infos[1].split(":");
        				//查到待审批人
        				$.ajax({
        					url:srvUrl+"formalAudit/queryAuditUsers.do",
        					type: "POST",
        					data:{"sign":signs[0],"businessId":$scope.businessId},
                  	    	dataType: "json",
                  	    	async: false,
                  	    	success: function(data){
                  	    		if(data.success){
                  	    			users = data.result_data;
                  	    		}else{
                  	    			$.alert(data.result_name);
                  	    			return false;
                  	    		}
                  	    	}
        				});
        				if(users == null || users.length == 0){
        					continue;
        				}
        				var userEntry = $scope.mapEntry(users);
        				sequenceFlow.submitInfo.currentTaskVar={opinion:'请审批'};
        				sequenceFlow.submitInfo.runtimeVar={
        					"sequenceFlow":	flow.id
        				};
        				if(signs[1].indexOf("List")>=0){
        					sequenceFlow.submitInfo.runtimeVar[signs[1]]=userEntry.values;
        				}else{
        					sequenceFlow.submitInfo.runtimeVar[signs[1]]=userEntry.values[0];
        				}
        				sequenceFlow.showInfo={
        					"destination":destination,
        					"approver":userEntry.names.join(",")
        				};
        			}
        			$scope.approve.push(sequenceFlow);
        		}
        	}else if(task != null){
        		//旧流程
        		$scope.isNewBpmn = false;
        		 $scope.approve = [{
                     submitInfo:{
                         taskId:params[3],
                         businessId: $scope.businessId,
                         runtimeVar:{assigneeList:fgIdArr,toTask:'1'},
                         currentTaskVar:{opinion:'请审批'}
                     },
                     showInfo:{destination:'固定小组成员审批',approver:fgNameArr.join(',')}
                 },{
                     submitInfo:{
                         taskId:params[3],
                         businessId: $scope.businessId,
                         runtimeVar:{toTask:'0'}
                     },
                     toNodeType:'end'
                 }];
        	}else if(task == null){
        		$.alert("任务已经不在当前节点了！");
        		return;
        	}
        }else if(action=='confirm'){
            $scope.approve = [{
                submitInfo:{
                    taskId:params[3],
                    businessId: $scope.businessId,
                    runtimeVar:{inputUser:reviewLeader.VALUE,toTask:'1'},
                    currentTaskVar:{opinion:'请审批'}
                },
                showInfo:{destination:'提交正式评审报告及材料',approver:reviewLeader.NAME}
            },{
                submitInfo:{
                    taskId:params[3],
                    businessId: $scope.businessId,
                    runtimeVar:{inputUser:investmentManager.VALUE,toTask:'2'},
                    currentTaskVar:{opinion:'请修改'}
                },
                showInfo:{destination:'投资经理反馈',approver:investmentManager.NAME}
            },{
                submitInfo:{
                    taskId:params[3],
                    businessId: $scope.businessId,
                    runtimeVar:{toTask:'0'}
                },
                toNodeType:'end'
            }];
        }
	}



	//旧流程基层法务反馈提交框信息
	$scope.LegalFeedbackNodeAudit = function(){
        var companyHeader = $scope.pfr.apply.companyHeader;//单位负责人
        $scope.approve = [{
            submitInfo:{
                taskId:params[3],
                runtimeVar:{inputUser:companyHeader.VALUE},
                currentTaskVar:{opinion:'请审批'},
                businessId:params[0]
            },
            showInfo:{destination:'单位负责人审核',approver: companyHeader.NAME}
        }];
	}
	//旧法律初审节点提交框信息
	$scope.legalNodeAudit = function(){
        var grassrootsLegalStaff = $scope.pfr.apply.grassrootsLegalStaff;//基层法务人员
        var legalLabel = "基层法务人员反馈";
        //如果基层法务人员不存在，用投资经理替代
        if(grassrootsLegalStaff==null||""==grassrootsLegalStaff||
        		grassrootsLegalStaff.VALUE==null||grassrootsLegalStaff.VALUE==""){
        	var tzUserName = $scope.pfr.apply.create_name;
        	if(tzUserName == null){
        		tzUserName = $scope.pfr.createby.NAME;
        	}
        	var tzUserValue = $scope.pfr.apply.create_by;
        	if(tzUserValue == null){
        		tzUserValue = $scope.pfr.createby.VALUE;
        	}
        	grassrootsLegalStaff = {NAME:tzUserName,VALUE:tzUserValue};
        }
        var reviewLeader =  $scope.pfr.taskallocation.reviewLeader;//评审负责人
        var reject = {
            submitInfo:{
                taskId:params[3],
                runtimeVar:{inputUser:grassrootsLegalStaff.VALUE,toTask:'-1'},
                currentTaskVar:{opinion:'请修改'},
                businessId:params[0]
            },
            showInfo:{destination:legalLabel,approver:grassrootsLegalStaff.NAME}
        };
        if($routeParams.action=='edit1'){
        	$scope.approve = [{
                submitInfo:{
                    taskId:params[3],
                    businessId: $scope.businessId,
                    runtimeVar:{inputUser:grassrootsLegalStaff.VALUE,toTask:'1'},
                    currentTaskVar:{opinion:'请审批'}
                },
                showInfo:{destination:legalLabel,approver:grassrootsLegalStaff.NAME}
            },{
                submitInfo:{
                    taskId:params[3],
                    businessId: $scope.businessId,
                    runtimeVar:{inputUser:reviewLeader.VALUE,toTask:'2'},
                    currentTaskVar:{opinion:'请审批'}
                },
                showInfo:{destination:'提交正式评审报告及材料',approver:reviewLeader.NAME}
            }];
        }else if($routeParams.action=='view1'){
        	var serviceTypes = $scope.serviceTypes;
        	if(serviceTypes == null || serviceTypes.length == null || serviceTypes.length < 1){
        		$.alert("该项目对应的业务类型为空，请检查项目信息是否正确！");
        		return;
        	}
        	var serviceTypeKeys = new Array();
        	for(var i = 0; i < serviceTypes.length; i++){
        		serviceTypeKeys.push(serviceTypes[i].KEY);
        	}
        	var approveUser;
        	var url = srvUrl+"firstlevelLawyer/queryByServiceTypes.do";
        	$.ajax({
            	url: url,
            	type: "POST",
            	data: {"serviceTypeKeys": serviceTypeKeys},
            	dataType: "json",
            	traditional: true,
            	async: false,
            	success: function(data){
            		if(data.success){
                    	var lawyers = data.result_data;
                    	approveUser = new Array();
                    	for(var m = 0; m < lawyers.length; m++){
                    		if(lawyers[m] != null && lawyers[m] != ""){
                    			var lawyer = lawyers[m];
                    			if(lawyer.LAWYERID != null && lawyer.LAWYERID.length > 0){
                    				approveUser.push({"name":lawyer.LAWYERNAME,"value":lawyer.LAWYERID});
                    			}
                    		}
                    	}
                    }else{
                        $.alert(data.result_name);
                    }
            	}
            });
            if(approveUser == null || typeof(approveUser)=='undefined'){
                return;
            }
            //判断一级法务人员是否存在
            if(approveUser.length > 0){
            	var auditUserIds = [], auditUserValues = [];
            	for(var m = 0; m < approveUser.length; m++){
            		var auditUser = approveUser[m];
            		auditUserIds.push(auditUser.value);
            		auditUserValues.push(auditUser.name);
            	}
            	$scope.approve = [{
                    submitInfo:{
                        taskId:params[3],
                        businessId: $scope.businessId,
                        runtimeVar:{firstLevelLawyers:auditUserIds,toTask:'1',isSkipFirstLawyer:"0"},
                        currentTaskVar:{opinion:'请审批'}
                    },
                    showInfo:{destination:'一级法务人员审批',approver: auditUserValues.join(",")}
                },reject];
            }else{
            	var legalReviewLeader = $scope.pfr.taskallocation.legalReviewLeader;
                $scope.approve = [{
                    submitInfo:{
                        taskId:params[3],
                        businessId: $scope.businessId,
                        runtimeVar:{inputUser:legalReviewLeader.VALUE,toTask:'1',isSkipFirstLawyer:"1"},
                        currentTaskVar:{opinion:'请审批'}
                    },
                    showInfo:{destination:'法律负责人审批',approver:legalReviewLeader.NAME}
                },reject];
            }
        }else if($routeParams.action=='view2'){
            var legalReviewLeader = $scope.pfr.taskallocation.legalReviewLeader;
            $scope.approve = [{
                submitInfo:{
                    taskId:params[3],
                    businessId: $scope.businessId,
                    runtimeVar:{inputUser:legalReviewLeader.VALUE,toTask:'1'},
                    currentTaskVar:{opinion:'请审批'}
                },
                showInfo:{destination:'法律负责人审批',approver:legalReviewLeader.NAME}
            },reject];
        }
	}
	//旧分配任务节点提交框信息
	$scope.taskNodeAudit = function(){
    	var currentUserName = $scope.credentials.userName;
    	var approveUser = $scope.myTaskallocation.reviewLeader;
    	var legalReviewLeader = $scope.myTaskallocation.legalReviewLeader;
        var investmentManager = $scope.pfr.apply.investmentManager;//投资经理
        $scope.approve = {"isAllocateTask":true};
        //判断是否为新流程
        var task = null;
  	  	var url=srvUrl+"bpmn/queryTaskById.do";
  		var reqparams = {
  			taskId: params[3]
  		};
  		$.ajax({
  		  url: url,
  	    	type: "POST",
  	    	data: reqparams,
  	    	dataType: "json",
  	    	async: false,
  	    	success: function(data){
  	    		if(data.success){
  	    			task = data.result_data;
  	            }else{
  	                $.alert(data.result_name);
  	            }
  	    	}
  	    });
    	if(task != null && task.description == "isnew"){
    		//新流程
    		$scope.approve.approve=[];
    		for(var m = 0; m < task.outSequences.length; m++){
    			var sequenceFlow = new Object();
    			var flow = task.outSequences[m];
    			var document = flow.documentation;
    			var infos = document.split(",");
    			var destination = infos[0];
    			sequenceFlow.submitInfo = {
    				taskId: params[3],
    				businessId:params[0]
    			};
    			sequenceFlow.submitInfo.runtimeVar = {};
    			if(infos.length == 1){
    				sequenceFlow.toNodeType = "end";
    				sequenceFlow.submitInfo.runtimeVar= {"sequenceFlow":flow.id};
    			}else{
    				var users = null;
    				var auditUsers = infos[1].split(";");
    				var psfzr = "formalAudit.queryPsFuzeren:inputUser";
    				var flfzr = "formalAudit.queryLeagueFuzeren:legalReviewLeader";
    				var tzjl = "formalAudit.queryTzManager:inputUser";
    				var showUserNameList = "";
    				for(var h = 0; h < auditUsers.length; h++){
    					if(psfzr == auditUsers[h]){
    						if(approveUser == null || approveUser.VALUE == null){
    							approveUser = {"NAME":"","VALUE":""};
    							sequenceFlow.submitInfo.runtimeVar.inputUser="";
    						}else{
    							sequenceFlow.submitInfo.runtimeVar.inputUser=approveUser.VALUE;//评审负责人uuid
    						}
    					}else if(flfzr == auditUsers[h]){
    						if(legalReviewLeader == null || legalReviewLeader.VALUE == null){
    							legalReviewLeader = {"NAME":"","VALUE":""};
    							sequenceFlow.submitInfo.runtimeVar.legalReviewLeader="";
    						}else{
    							sequenceFlow.submitInfo.runtimeVar.legalReviewLeader=legalReviewLeader.VALUE;//法律评审负责人uuid
    						}
    					}else if(tzjl == auditUsers[h]){
    						sequenceFlow.submitInfo.runtimeVar.inputUser=investmentManager.VALUE;//投资经理UUID
    					}else{
    						$.alert("出错了！");
    						return;
    					}
    				}
    				if(auditUsers.length==1){
    					showUserNameList = investmentManager.NAME;
    				}else if(auditUsers.length==2){
    					showUserNameList = approveUser.NAME+'/'+legalReviewLeader.NAME;
    				}
    				sequenceFlow.showInfo={
    					"destination":destination,
    					"approver": showUserNameList
    				};
    				sequenceFlow.submitInfo.currentTaskVar={opinion:'请审批'};
    				sequenceFlow.submitInfo.runtimeVar.sequenceFlow=flow.id;
    			}
    			$scope.approve.approve.push(sequenceFlow);
    		}
    	}else{
    		$scope.approve.approve = [{
                submitInfo:{
                    taskId:params[3],
                    businessId:params[0],
                    runtimeVar:{inputUser:approveUser.VALUE,legalReviewLeader:legalReviewLeader.VALUE,toTask:'1'},
                    currentTaskVar:{opinion:'请审批'}
                },
                showInfo:{destination:'评审负责人及法律负责人审批',approver:approveUser.NAME+'/'+legalReviewLeader.NAME}
            },{
                submitInfo:{
                    taskId:params[3],
                    businessId:params[0],
                    runtimeVar:{inputUser:investmentManager.VALUE,toTask:"2"},
                    currentTaskVar:{opinion:'请修改'}
                },
                showInfo:{destination:'退回起草人',approver:investmentManager.NAME}
            },{
                submitInfo:{
                    taskId:params[3],
                    businessId:params[0],
                    runtimeVar:{toTask:'0'}
                },
                toNodeType:'end'
            }];
    	}
	}

	//弹出审批框新版
	//提交
	$scope.showSubmitModal = function(){
		$scope.save(function(){
			if($scope.taskMark == null || $scope.taskMark == ''){

				$http({
					method:'post',
					url: srvUrl + "formalAssessmentAudit/querySingleProcessOptions.do",
					data: $.param({
						"businessId":  $scope.businessId
					})
				}).success(function(result){
					$scope.approve = {
							operateType: "audit",
							processKey: "formalReview",
							processOptions: result.result_data,
							businessId:  $scope.businessId,
							callbackSuccess: function(result){
								$.alert(result.result_name);
								// $('#submitModal').modal('hide');
								$scope.initData();
							},
							callbackFail: function(result){
								$.alert(result.result_name);
							}
					};
					$scope.approve.showController = $scope.showController;
                    if (!isEmpty($scope.curLog)) {
                        if (!isEmpty($scope.curLog.CHANGETYPE)) {
                            if($scope.curLog.CHANGETYPE != ''){
                                $scope.approve.operateType = "change";
                            }
                        }
                    }
					// $('#submitModal').modal('show');
				});
			}else{
				$http({
					method:'post',
					url: srvUrl + "formalAssessmentAudit/querySingleProcessOptions.do",
					data: $.param({
						"businessId":  $scope.businessId,
						"taskMark":$scope.taskMark
					})
				}).success(function(result){
					$scope.approve = {
							operateType: "audit",
							processKey: "formalReview",
							processOptions: result.result_data,
							businessId:  $scope.businessId,
							callbackSuccess: function(result){
								$.alert(result.result_name);
								$('#submitModal').modal('hide');
								$scope.initData();
							},
							callbackFail: function(result){
								$.alert(result.result_name);
							}
					};
					$scope.approve.showController = $scope.showController;
                    if (!isEmpty($scope.curLog)) {
                        if (!isEmpty($scope.curLog.CHANGETYPE)) {
                            if($scope.curLog.CHANGETYPE != ''){
                                $scope.approve.operateType = "change";
                            }
                        }
                    }
					// $('#submitModal').modal('show');
				});
			}

		});
	};

     //处理附件列表
     $scope.reduceAttachment = function(attachment, id){
         $scope.newAttachment = attach_list("formalReview", id, "formalAssessmentInfo").result_data;
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


     //初始化数据
	$scope.initUpdate=function(id){
		var  url = 'formalAssessmentInfo/getFormalAssessmentByID.do';
		$http({
			method:'post',
		    url:srvUrl+url,
		    data: $.param({"id":id})
		}).success(function(data){
			$scope.pfr  = data.result_data.formalAssessmentMongo;
			$scope.pfrOracle  = data.result_data.formalAssessmentOracle;

            // 附件显示
            var time = $scope.pfr.create_date.substring(0,10);
            var thisTime = '2019-6-3';
            if (time < thisTime){
                $scope.isShowOld = true;
            }

			// 默认上会
			if (isEmpty($scope.pfr.needMeeting)) {
                $scope.pfr.needMeeting = '1';
			};

			// 处理附件需要的数据
			$scope.serviceType = angular.copy($scope.pfr.apply.serviceType);
			$scope.projectModel = angular.copy($scope.pfr.apply.projectModel);

			//处理任务人
			//数据回显
			if($scope.pfr.taskallocation !=null ){
				if($scope.pfr.taskallocation.reviewLeader!=null){
					$scope.myTaskallocation.reviewLeader = $scope.pfr.taskallocation.reviewLeader;
				}
				if($scope.pfr.taskallocation.legalReviewLeader!=null){
					$scope.myTaskallocation.legalReviewLeader = $scope.pfr.taskallocation.legalReviewLeader;
				}
				if($scope.pfr.taskallocation.fixedGroup!=null){
					$scope.myTaskallocation.fixedGroup = $scope.pfr.taskallocation.fixedGroup;
				}
				if($scope.pfr.taskallocation.professionalReviewers!=null){
					$scope.myTaskallocation.professionalReviewers = $scope.pfr.taskallocation.professionalReviewers;
					$scope.currentUserId = data.result_data.currentUserId;
				}
			}

			$scope.attach = data.result_data.attach;
            // 处理附件
            $scope.reduceAttachment(data.result_data.formalAssessmentMongo.attachmentList, id);

			var ptNameArr=[],pmNameArr=[],pthNameArr=[],fgNameArr=[];
			var pt1NameArr=[];
			var pt1=$scope.pfr.apply.serviceType;
			$scope.serviceTypes = $scope.pfr.apply.serviceType;
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
			if(undefined!=$scope.pfr.taskallocation){
				if(undefined!=$scope.pfr.taskallocation.fixedGroup){
					var fg=$scope.pfr.taskallocation.fixedGroup;
					if(null!=fg && fg.length>0){
						for(var k=0;k<fg.length;k++){
							fgNameArr.push(fg[k].NAME);
						}
						$scope.pfr.taskallocation.fixedGroup=fgNameArr.join(",");
					}
				}
			}
			if(undefined==$scope.pfr.approveLegalAttachment){
				$scope.approveLegalShow = false;
                $scope.pfr.approveLegalAttachment = {};
                $scope.addFormalLegalComment();
            }
			if(undefined==$scope.pfr.approveAttachment){
				$scope.approveShow = false;
				$scope.pfr.approveAttachment = {};
				$scope.addFormalComment();
			}
			//显示专业评审
			if(undefined==$scope.pfr.approveMajorCommonts){
				$scope.approveMajorShow = false;
			}

			$scope.fileName=[];
			var filenames=$scope.pfr.attachment;
                if (filenames != null && filenames != [] && filenames != undefined) {
                    for(var i=0;i<filenames.length;i++){
                        var arr={UUID:filenames[i].UUID,ITEM_NAME:filenames[i].ITEM_NAME};
                        $scope.fileName.push(arr);
                    }
                }
			if (null != $scope.pfr.apply.expectedContractDate) {
				$scope.changDate($scope.pfr.apply.expectedContractDate);
			}

            // 回显数据-补充评审相关
            if($scope.pfr.is_supplement_review == 1){
                $scope.getNoticeOfDecstionByProjectFormalID($scope.pfr.apply.projectNo);
            };

			hide_Mask();
		});
        /*// 查询文件变更记录
        $scope.getReplaceReasonsList($scope.businessId);*/
	};

     // 根据id查询决策通知书决策意见
     $scope.getNoticeOfDecstionByProjectFormalID = function(pid){
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
                     } else if (c == '3') {
                         $scope.consentToInvestment = "同意有条件投资";
                     } else {
                         $scope.consentToInvestment = "择期决议";
                     }
                     $scope.executiveRequirements = $scope.noticofDec.implementationRequirements;
                     $scope.implementationMatters = $scope.noticofDec.implementationMatters;
                     $scope.dateOfMeeting = $scope.noticofDec.dateOfMeeting;
                 }else{
                     $scope.consentToInvestment = null;
                     $scope.implementationMatters = null;
                     $scope.executiveRequirements = null;
                 }
             }else{
                 alert(data.result_name);
             }
         });
     };


	$scope.changDate=function(values){
		var date = new Date();
		var paddNum = function(num){
			num += "";
			return num.replace(/^(\d)$/,"0$1");
		}
		var nowDate=date.getFullYear()+"-"+paddNum(date.getMonth()+1)+"-"+paddNum(date.getDate());
		var d=DateDiff(values,nowDate);
		$("#shouwdate").text("距离签约时间还差："+d+"天");
	}
	$scope.getSelectTypeByCodeL=function(typeCode){
        var  url = 'common/commonMethod/selectDataDictionByCode';
        $http({
			method:'post',
		    url: srvUrl + url,
		    data: typeCode
		}).success(function(data){
			if(data.result_code === 'S'){
                $scope.optionTypeListL=data.result_data;
            }else{
                alert(data.result_name);
            }
		});
    }
	$scope.getSelectTypeByCode=function(typeCode){
		var  url = 'common/commonMethod/selectDataDictionByCode';
		$http({
			method:'post',
		    url: srvUrl + url,
		    data: typeCode
		}).success(function(data){
			if(data.result_code === 'S'){
				$scope.optionTypeList=data.result_data;
			}else{
				alert(data.result_name);
			}
		});
	}
	$scope.getSelectTypeByCode2=function(typeCode){
		var  url = 'common/commonMethod/selectDataDictionByCode';
		$http({
			method:'post',
		    url: srvUrl + url,
		    data: typeCode
		}).success(function(data){
			if(data.result_code === 'S'){
				$scope.optionTypeList2=data.result_data;
			}else{
				alert(data.result_name);
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

	$scope.addFormalComment = function(){
        function addBlankRow(array){
            var blankRow = {
                opinionType:'',
                isReviewLeaderEdit:'1',
                isInvestmentManagerBackEdit:'1',
                commentConent:'',
                commentDepartment:'',
                commentFeedback:'',
                commentDate:''
            }
            var size = array.length;
            array[size]=blankRow;
        }
        if(undefined==$scope.pfr.approveAttachment){
            $scope.pfr.approveAttachment={commentsList:[]};

        }
        if(undefined==$scope.pfr.approveAttachment.commentsList){
            $scope.pfr.approveAttachment.commentsList=[];
        }
        addBlankRow($scope.pfr.approveAttachment.commentsList);
    }

    $scope.deleteFormalComment = function(n){
        var commentsObj=null;
        if(n==0) {
            commentsObj = $scope.pfr.approveAttachment.commentsList;
        }else if(n==1){
            commentsObj = $scope.pfr.approveAttachment.attachmentNew;
        }
        if(commentsObj!=null){
            for(var i=0;i<commentsObj.length;i++){
                if(commentsObj[i].selected){
                    commentsObj.splice(i,1);
                    i--;
                }
            }
        }
    }

    $scope.addFormalAttachment = function(){
        function addBlankRow(array){
            var blankRow = {
                attachment_new:'',
            	isReviewLeaderEdit:'1',
                isInvestmentManagerBackEdit:'1'
            }
            var size = array.length;
            array[size]=blankRow;
        }
        if(undefined==$scope.pfr.approveAttachment){
            $scope.pfr.approveAttachment={attachmentNew:[]};
        }
        if(undefined==$scope.pfr.approveAttachment.attachmentNew){
            $scope.pfr.approveAttachment.attachmentNew=[];
        }
        addBlankRow($scope.pfr.approveAttachment.attachmentNew);
    }

	//添加法律评审意见
	$scope.addFormalLegalComment = function(){
        function addBlankRow(array){
            var blankRow = {
                opinionType:'',
                isLegalEdit:'1',
                isGrassEdit:'1',
                commentConent:'',
                commentDepartment:'',
                commentFeedback:'',
                commentDate:''
            }
            var size=0;
            for(attr in array){
                size++;
            }
            array[size]=blankRow;
        }
        if(undefined==$scope.pfr.approveLegalAttachment){
            $scope.pfr.approveLegalAttachment={commentsList:[]};

        }
        if(undefined==$scope.pfr.approveLegalAttachment.commentsList){
            $scope.pfr.approveLegalAttachment.commentsList=[];
        }
        addBlankRow($scope.pfr.approveLegalAttachment.commentsList);
    }
	//删除法律评审意见
	$scope.deleteFormalLegalComment = function(n){
        var commentsObj=null;
        if(n==0) {
            commentsObj = $scope.pfr.approveLegalAttachment.commentsList;
        }else if(n==1){
            commentsObj = $scope.pfr.approveLegalAttachment.attachmentNew;
        }
        if(commentsObj!=null){
            for(var i=0;i<commentsObj.length;i++){
                if(commentsObj[i].selected){
                    commentsObj.splice(i,1);
                    i--;
                }
            }
        }
    }
	//新增法律评审附件
	$scope.addFormalLegalAttachment = function(){
	     function addBlankRow(array){
	         var blankRow = {
        		 isLegalEdit:'1',
                 isGrassEdit:'1',
	             attachment_new:''
	         }
	         var size=0;
	         for(attr in array){
	             size++;
	         }
	         array[size]=blankRow;
	     }
	     if(undefined==$scope.pfr.approveLegalAttachment){
	         $scope.pfr.approveLegalAttachment={attachmentNew:[]};
	     }
	     if(undefined==$scope.pfr.approveLegalAttachment.attachmentNew){
	         $scope.pfr.approveLegalAttachment.attachmentNew=[];
	     }
	     addBlankRow($scope.pfr.approveLegalAttachment.attachmentNew);
	}
	//保存法律初步评审意见
//	$scope.saveFormalLegarReviewComtents=function(openPopWin){
//        var url = 'formalAssessment/ProjectFormalReview/saveProjectFormalLegarComentsByID';
//        var riskWarning = $scope.pfr.approveLegalAttachment.riskWarning;
//        if ((riskWarning == null || riskWarning == "" )) {
//            $.alert("请填写初步评审意见");
//            return false;
//        }
//        if(typeof(openPopWin)=='function') {
//        	$http({
//    			method:'post',
//    		    url: srvUrl + url,
//    		    data: $scope.pfr
//    		}).success(function(data){
//    			if (data.result_code === 'S') {
//                    if (typeof(openPopWin) == 'function') {
//                        openPopWin();
//                    } else {
//                        $.alert("保存成功");
//                    }
//                }
//    		});
//        }else{
//        	$http({
//    			method:'post',
//    		    url: srvUrl + url,
//    		    data: $scope.pfr
//    		}).success(function(data){
//    			if (data.result_code === 'S') {
//                    $.alert("保存成功");
//                }
//    		});
//        }
//    }

	//添加专家评审意见
	$scope.addFormalMajorComment = function(obj){
        if(obj.commentsList == null){
        	obj.commentsList = [];
        }
        var blankRow = {
			 commentConent:'',
             commentDate:''
         }
         obj.commentsList.push(blankRow);
    }
	//删除专家评审意见
	$scope.deleteFormalMajorComment = function(obj, outIndex){
		if(obj.commentsList == null){
        	return;
        }
		var chks = $("input[type=checkbox][name=com_"+outIndex+"]:checked").get();
		for(var i = 0; i < obj.commentsList.length; i++){
			for(var j=0; j < chks.length; j++){
				if(i == chks[j].value){
					obj.commentsList[i].flag = true;
					continue;
				}
			}
		}
		for(var i = 0; i < obj.commentsList.length; i++){
			if(obj.commentsList[i].flag){
				obj.commentsList.splice(i,1);
                i--;
			}

		}
    }


	//上传
	$scope.errorAttach=[];
	//评审负责人上传文件
	$scope.upload2Review = function (file,errorFile, idx) {
        if(errorFile && errorFile.length>0){
        	var errorMsg = fileErrorMsg(errorFile);
            $scope.errorAttach[idx]={msg:errorMsg};
        }else if(file){
            $scope.errorAttach[idx]={msg:''};
            Upload.upload({
                url:srvUrl+'common/RcmFile/upload',
                data: {file: file, folder:'',typeKey:'formalAssessmentPath'}
            }).then(function (resp) {
                $scope.pfr.approveAttachment.commentsList[idx].files=resp.data.result_data[0];
            }, function (resp) {
                console.log('Error status: ' + resp.status);
            }, function (evt) {
                var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                $scope["progress"+idx]=progressPercentage == 100 ? "":progressPercentage+"%";
            });
        }
    };
	$scope.uploadReview = function (file,errorFile, idx,name,versionNun) {
	        if(errorFile && errorFile.length>0){
	        	var errorMsg = fileErrorMsg(errorFile);
	            $scope.errorAttach[idx]={msg:errorMsg};
	        }else if(file){
	            $scope.errorAttach[idx]={msg:''};
	            Upload.upload({
	                url:srvUrl+'common/RcmFile/upload',
	                data: {file: file, folder:'',typeKey:'formalAssessmentPath'}
	            }).then(function (resp) {
	                var retData = resp.data.result_data[0];
	                var myDate = new Date();
	                var paddNum = function(num){
	                    num += "";
	                    return num.replace(/^(\d)$/,"0$1");
	                }
	                retData.upload_date=myDate.getFullYear()+"-"+paddNum(myDate.getMonth()+1)+"-"+ myDate.getDate()+" "+myDate.getHours()+":"+myDate.getMinutes()+":"+myDate.getSeconds();
	                retData.programmed={NAME:$scope.credentials.userName,VALUE:$scope.credentials.UUID};
	                retData.approved={NAME:$scope.credentials.userName,VALUE:$scope.credentials.UUID};
	                if(versionNun != null && versionNun != ''){
	                	retData.version = versionNun;
	                }
	                $scope.pfr.approveAttachment.attachmentNew[idx].attachment_new=retData;

	            }, function (resp) {
	                console.log('Error status: ' + resp.status);
	            }, function (evt) {
	                var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
	                $scope["progress"+idx]=progressPercentage == 100 ? "":progressPercentage+"%";
	            });
	        }
	 };
	//leageReview
	//需更新文件上传需更新文件
	$scope.uploadlegalReview = function (file,errorFile, idx,name,versionNun) {
        if(errorFile && errorFile.length>0){
        	var errorMsg = fileErrorMsg(errorFile);
            $scope.errorAttach[idx]={msg:errorMsg};
        }else if(file){
        	//验证是否选择附件类型；
        	var attachmentNew =  $scope.pfr.approveLegalAttachment.attachmentNew;
				if(attachmentNew[idx].isLegalEdit =='1'){
					if(attachmentNew[idx].attachmentUList == null || attachmentNew[idx].attachmentUList=="" || attachmentNew[idx].attachmentUList==undefined){
						$.alert("请选择需要替换的附件！");
						return false;
					}
				}
            $scope.errorAttach[idx]={msg:''};
            Upload.upload({
                url:srvUrl+'common/RcmFile/upload',
                data: {file: file, folder:'',typeKey:'formalAssessmentPath'}
            }).then(function (resp) {
                var retData = resp.data.result_data[0];
                var myDate = new Date();
                var paddNum = function(num){
                    num += "";
                    return num.replace(/^(\d)$/,"0$1");
                }
                retData.upload_date=myDate.getFullYear()+"-"+paddNum(myDate.getMonth()+1)+"-"+ myDate.getDate()+" "+myDate.getHours()+":"+myDate.getMinutes()+":"+myDate.getSeconds();
                retData.programmed={NAME:$scope.credentials.userName,VALUE:$scope.credentials.UUID};
                retData.approved={NAME:$scope.credentials.userName,VALUE:$scope.credentials.UUID};
                if(versionNun != null && versionNun != ''){
                	retData.version = versionNun;
                }
                $scope.pfr.approveLegalAttachment.attachmentNew[idx].attachment_new=retData;
            }, function (resp) {
                console.log('Error status: ' + resp.status);
            }, function (evt) {
                var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                $scope["progress"+idx]=progressPercentage == 100 ? "":progressPercentage+"%";
            });
        }
    };
    //leageReview
    //基层法务反馈附件上传
	$scope.upload2 = function (file,errorFile, idx) {
        if(errorFile && errorFile.length>0){
        	var errorMsg = fileErrorMsg(errorFile);
            $scope.errorAttach[idx]={msg:errorMsg};
        }else if(file){
            $scope.errorAttach[idx]={msg:''};
            Upload.upload({
                url:srvUrl+'common/RcmFile/upload',
                data: {file: file, folder:'',typeKey:'formalAssessmentPath'}
            }).then(function (resp) {
                $scope.pfr.approveLegalAttachment.commentsList[idx].files=resp.data.result_data[0];
            }, function (resp) {
                console.log('Error status: ' + resp.status);
            }, function (evt) {
                var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                $scope["progress"+idx]=progressPercentage == 100 ? "":progressPercentage+"%";
            });
        }
    };
	angular.element(document).ready(function() {
		$scope.getSelectTypeByCode("06");
		$scope.getSelectTypeByCode2("09");
		$scope.getSelectTypeByCodeL("09");
	});
	$scope.initData();
	$scope.curLog = wf_getTaskLog("formalReview", $routeParams.id, $scope.credentials.UUID);
	/*********************************流程相关代码**********************************/
     $scope.changeTypeSelected = "";
     $scope.changeTypes = [{key: 'before', value: '前加签'}, {key: 'after', value: '后加签'}];
     //初始化提示信息框
     $("[data-toggle='tooltip']").tooltip();
     //验证任务人员
     $scope.callfunction = function (functionName) {
         var func = eval(functionName);
         //创建函数对象，并调用
         return new func(arguments[1]);
     }
     /**加签参数初始化add by LiPan 2019-03-08**/
     $scope.showSelectPerson = function () {
         $("#submitModal").modal('hide');
         $("#userSinDialog").modal('show');
     };
     $scope.changeWork = function () {
         console.log($scope.approve);
         //人员验证
         if ($scope.checkedUser.NAME == null || $scope.checkedUser.NAME == '') {
             $.alert("请选择目标人员！");
             return;
         }
         if ($scope.checkedUser.VALUE == $scope.credentials.UUID) {
             $.alert("不能转办给自己！");
             return;
         }
         if ($scope.checkedUser.VALUE == $scope.curLog.AUDITUSERID) {
             $.alert("不能转办给最初人员！");
             return;
         }
         if ($scope.flowVariables == null || $scope.flowVariables.opinion == null || $scope.flowVariables.opinion == "") {
             $.alert("审批意见不能为空！");
             return;
         }
         /*if ($scope.flowVariables.opinion.length < 20) {
             $.alert("审批意见不能少于20字！");
             return;
         }*/
         if ($scope.flowVariables.opinion.length > 650) {
             $.alert("审批意见不能超过650字！");
             return;
         }
         if ($scope.changeTypeSelected == 'after') {
             var validate = wf_validateSign('formalReview', $scope.approve.businessId);
             if (!isEmpty(validate.code)) {
                 $.alert(validate.comment);
                 return;
             }
         }
         //执行转办操作
         show_Mask();
         $http({
             method: 'post',
             url: srvUrl + "sign/doSign.do",
             data: $.param({
                 'type': $scope.changeTypeSelected,
                 'business_module': 'formalReview',
                 "business_id": $scope.approve.businessId,
                 "user_json": JSON.stringify($scope.checkedUser),
                 "task_id": $scope.curLog.TASKID,
                 "option": $scope.flowVariables.opinion
             })
         }).success(function (result) {
             hide_Mask();
             if ($scope.approve.callbackSuccess != null && result.success) {
                 $scope.approve.callbackSuccess(result);
             } else if ($scope.approve.callbackFail != null && !result.success) {
                 $scope.approve.callbackFail(result);
             } else {
                 $.alert(result.result_name);
             }
         });
     };
     $scope.workOver = function () {
         //人员验证
         if ($scope.flowVariables == null || $scope.flowVariables.opinion == null || $scope.flowVariables.opinion == "") {
             $.alert("审批意见不能为空！");
             return;
         }
         /*if ($scope.flowVariables.opinion.length < 20) {
             $.alert("审批意见不能少于20字！");
             return;
         }*/
         if ($scope.flowVariables.opinion.length > 650) {
             $.alert("审批意见不能超过650字！");
             return;
         }
         //执行办结操作
         show_Mask();
         $http({
             method: 'post',
             url: srvUrl + "sign/endSign.do",
             data: $.param({
                 "business_module": "formalReview",
                 "business_id": $scope.approve.businessId,
                 "task_id": $scope.curLog.TASKID,
                 "option": $scope.flowVariables.opinion
             })
         }).success(function (result) {
             hide_Mask();
             if ($scope.approve.callbackSuccess != null && result.success) {
                 $scope.approve.callbackSuccess(result);
             } else if ($scope.approve.callbackFail != null && !result.success) {
                 $scope.approve.callbackFail(result);
             } else {
                 $.alert(result.result_name);
             }
         });
     };
     /**加签参数初始化add by LiPan 2019-03-08**/

     //判断是否显示toConfirm的打分项
     $scope.showMarkMethod = function (documentation) {

         if (documentation != null && documentation != "") {
             var docObj = JSON.parse(documentation);
             if (docObj.mark == "reviewPassMark") {
                 $scope.showReviewToConfirm = true;
                 $scope.showReviewConfirmToEnd = false;
                 $scope.showMark = true;
             }
             if (docObj.mark == "legalPassMark") {
                 $scope.showMark = true;
                 $scope.showLegalToConfirm = true;

             }
             if (docObj.mark == "toEnd") {
                 $scope.showMark = false;
                 $scope.showLegalToConfirm = false;
                 $scope.showReviewToConfirm = false;
                 $scope.showReviewConfirmToEnd = true;
             }
         } else {
//					$scope.showMark = false;
             $scope.showLegalToConfirm = false;
             $scope.showReviewToConfirm = false;
             $scope.showReviewConfirmToEnd = false;
         }
     }

     $scope.checkMark = function () {
         if ($scope.approve == null) {
             return;
         }
         var processOptions = $scope.approve.processOptions;
		 if(processOptions != null && processOptions.length > 0){// 判断操作，防止页面JS报错
            if (processOptions[0].documentation != null && processOptions[0].documentation != '') {
                var docObj = JSON.parse(processOptions[0].documentation);

                if (docObj.mark == "reviewPassMark") {
                    $scope.showReviewToConfirm = true;
                }
                if (docObj.mark == "legalPassMark") {
                    $scope.showLegalToConfirm = true;
                }
            }
		 }else{
		 	// TODO 是否需要其它操作？
		 }


         //流程选项
         for (var i in processOptions) {
             var documentation = processOptions[i].documentation;
             if (documentation != null && documentation != "") {
                 var docObj = JSON.parse(documentation);
                 if (docObj.mark == "toEnd") {
                     $scope.newEndOption = true;
                 }
                 if (docObj.mark == "reviewPassMark" || docObj.mark == "legalPassMark") {
//							$scope.showMark = true;
                     if (i == 0) {
                         $scope.showMark = true;
                     }
                     //查询后台的评价记录
                     $.ajax({
                         type: 'post',
                         url: srvUrl + "formalMark/queryMarks.do",
                         data: $.param({"businessId": $scope.approve.businessId}),
                         dataType: "json",
                         async: false,
                         success: function (result) {
                             if (result.success) {
                                 if (result.result_data == null) {
                                     if (docObj.mark == "reviewPassMark") {
                                         $scope.showReviewFirstMark = true;
                                         $scope.showMark = true;
                                     }
                                     if (docObj.mark == "legalPassMark") {
                                         $scope.showLegalFirstMark = true;
                                         $scope.showMark = true;
                                     }
                                 } else {
                                     if (docObj.mark == "reviewPassMark") {
                                         if (result.result_data.REVIEWFILEACCURACY == null || result.result_data.REVIEWFILEACCURACY == "" || result.result_data.FLOWMARK == null || result.result_data.FLOWMARK == '') {
                                             $scope.showReviewFirstMark = true;
                                         }
                                     }
                                     if (docObj.mark == "legalPassMark") {
                                         if (result.result_data.LEGALFILEACCURACY == null || result.result_data.LEGALFILEACCURACY == "") {
                                             $scope.showLegalFirstMark = true;
                                         }
                                     }
                                 }
                             } else {
                                 alert(result.result_name);
                                 return;
                             }
                         }
                     });
                 }
             }
         }
     }
     var validServiceType = function () {
         var result = {success: true, result_name: ""};

         if ($scope.pfrOracle.SERVICETYPE_ID != '1401' && $scope.pfrOracle.SERVICETYPE_ID != '1402') {
             result.success = false;
             result.result_name = "此项目非传统水务、水环境项目！无法选择该选项	！";
         }
         return result;
     };
     var validCheckedFzr = function () {
         var result = {success: true, result_name: ""};

         if ($scope.myTaskallocation == null || $scope.myTaskallocation == "") {
             result.success = false;
             result.result_name = "请分配负责人！";
         }
         if ($scope.approve.showController.isSignLegal == null || $scope.approve.showController.isSignLegal == '') {
             if ($scope.myTaskallocation.legalReviewLeader.NAME == null || $scope.myTaskallocation.legalReviewLeader.NAME == "") {
                 result.success = false;
                 result.result_name = "请选择法律评审负责人！";
             }
         }
         if ($scope.myTaskallocation.reviewLeader.NAME == null || $scope.myTaskallocation.reviewLeader.NAME == "") {
             result.success = false;
             result.result_name = "请选择评审负责人！";
         }
         return result;
     };
     var validCheckedFLFzr = function () {
         var result = {success: true, result_name: ""};

         if ($scope.myTaskallocation == null || $scope.myTaskallocation == "") {
             result.success = false;
             result.result_name = "请分配负责人！";
         }
         if ($scope.myTaskallocation.legalReviewLeader.NAME == null || $scope.myTaskallocation.legalReviewLeader.NAME == "") {
             result.success = false;
             result.result_name = "请选择法律评审负责人！";
         }
         return result;
     };

     var validCheckedMajor = function () {
         var result = {success: true, result_name: ""};

         if ($scope.myTaskallocation.professionalReviewers == null || $scope.myTaskallocation.professionalReviewers == "") {
             result.success = false;
             result.result_name = "请选择专业评审人员！";
         }
         return result;
     };
     $scope.submitInfo = {};
     $scope.submitInfo.currentTaskVar = {};
     $scope.submitNext = function () {
         /********Add By LiPan
          * 此处发现选择了"加签"单选以后,
          * $scope.showReviewToConfirm的值依然是true
          * 加签操作不起作用,所以加上了该段代码
          * ********/
         if ($("input[name='bpmnProcessOption']:checked").val() == 'CHANGE') {
             $scope.showReviewToConfirm = false;
             $scope.showLegalToConfirm = false;
         }
         if ($("input[name='bpmnProcessOption']:checked").val() == 'WORKOVER') {
             $scope.showReviewToConfirm = false;
             $scope.showLegalToConfirm = false;
         }
         if ("submit" == $scope.approve.operateType) {
             $scope.submit();
         } else if ("audit" == $scope.approve.operateType) {
             if ($scope.showReviewConfirmToEnd) {
                 $.confirm("您选择了终止项目，意味着您将废弃此项目！是否确认？", function () {
                     $scope.auditSingle();
                 });
             } else if ($scope.showLegalToConfirm) {
                 $.confirm("您选择了评审负责人确认，意味着您已经和基层法务沟通完毕，流程将进入下一环节！是否确认？", function () {
                     $scope.auditSingle();
                 });
             } else if ($scope.showReviewToConfirm) {
                 $.confirm("您选择了评审负责人确认选项，意味着您已经和投资经理沟通完毕，流程将进入下一环节！是否确认？", function () {
                     $scope.auditSingle();
                 });
             } else if ($("input[name='bpmnProcessOption']:checked").val() == 'CHANGE') {
                 $scope.changeWork();
             } else if ($("input[name='bpmnProcessOption']:checked").val() == 'WORKOVER') {
                 $scope.workOver();
             } else {
                 $scope.auditSingle();
             }
         } else if ("change" == $scope.approve.operateType) {
             if ($("input[name='bpmnProcessOption']:checked").val() == 'CHANGE') {
                 $scope.changeWork();
             } else if ($("input[name='bpmnProcessOption']:checked").val() == 'WORKOVER') {
                 $scope.workOver();
             }
         } else {
             $.alert("操作状态不明确！");
         }
     };
     $scope.submit = function () {
         var url = srvUrl + "formalAssessmentAudit/startSingleFlow.do";
         show_Mask();
         $http({
             method: 'post',
             url: url,
             data: $.param({
                 "processKey": $scope.approve.processKey,
                 "businessId": $scope.approve.businessId
             })
         }).success(function (result) {
             hide_Mask();
             if ($scope.approve.callbackSuccess != null && result.success) {
                 $scope.approve.callbackSuccess(result);
             } else if ($scope.approve.callbackFail != null && !result.success) {
                 $scope.approve.callbackFail(result);
             } else {
                 $.alert(result.result_name);
             }
         });
     };

     $(".mark").keyup(function () {
         if (this.value.length == 1) {
             this.value = this.value.replace(/[^0-9]/g, '');
         } else {
             this.value = this.value.replace(/\D/g, '')
         }
         if (this.value > this.attributes.max.value * 1) {
             this.value = null;
         }
     });

     $scope.auditSingle = function () {
         //打分项验证-start
         if ($scope.showMark && ($scope.showReviewFirstMark || $scope.showReviewToConfirm || $scope.showLegalToConfirm)) {
             if (!$scope.mark) {
                 $.alert("请评分！");
                 return;
             }
             if ($scope.showReviewFirstMark) {
                 if ($scope.mark.flowMark == null) {
                     $.alert("请对审批流程熟悉度评分！");
                     return;
                 }
                 if ($scope.mark.moneyCalculate == null) {
                     $.alert("请对核心财务测算能力评分！");
                     return;
                 }
                 if ($scope.mark.reviewFileAccuracy == null) {
                     $.alert("请对资料的准确性评分！");
                     return;
                 }
                 if ($scope.mark.planDesign == null) {
                     $.alert("请对核心的方案设计能力评分！");
                     return;
                 }
             }
             if ($scope.showLegalFirstMark) {
             }
             if ($scope.showReviewToConfirm) {
                 if ($scope.mark.fileContent == null) {
                     $.alert("请对资料的完整性评分！");
                     return;
                 }
                 if ($scope.mark.fileTime == null) {
                     $.alert("请对资料的及时性评分！");
                     return;
                 }
                 if ($scope.mark.riskControl == null) {
                     $.alert("请对核心风险识别及规避能力评分！");
                     return;
                 }

             }
             if ($scope.showLegalToConfirm) {
                 if ($scope.mark.legalFileAccuracy == null) {
                     $.alert("请对资料的准确性评分！");
                     return;
                 }
                 if ($scope.mark.talks == null) {
                     $.alert("请对核心的协议谈判能力评分！");
                     return;
                 }
             }

             //存分
             $.ajax({
                 type: 'post',
                 url: srvUrl + "formalMark/saveOrUpdate.do",
                 data: $.param({
                     "json": JSON.stringify($scope.mark),
                     "businessId": $scope.approve.businessId
                 }),
                 dataType: "json",
                 async: false,
                 success: function (result) {
                     if (!result.success) {
                         alert(result.result_name);
                         return;
                     }
                 }
             });

         }
         //打分项验证-end

         if ($scope.approve.showController.isServiewType) {
             if ($scope.submitInfo.currentTaskVar == null || $scope.submitInfo.currentTaskVar.cesuanFileOpinion == null || $scope.submitInfo.currentTaskVar.cesuanFileOpinion == "") {
                 $.alert("测算文件意见不能为空！");
                 return;
             }
             if ($scope.submitInfo.currentTaskVar == null || $scope.submitInfo.currentTaskVar.tzProtocolOpinion == null || $scope.submitInfo.currentTaskVar.tzProtocolOpinion == "") {
                 $.alert("投资协议意见不能为空！");
                 return;
             }
             else {
                 //保存意见到mongo
                 $http({
                     method: 'post',
                     url: srvUrl + "formalAssessmentInfo/updateServiceTypeOpinion.do",
                     data: $.param({
                         "serviceTypeOpinion": JSON.stringify($scope.submitInfo.currentTaskVar),
                         "businessId": $scope.approve.businessId
                     })
                 }).success(function (result) {

                 });
             }
         }

         //验证确认节点是否选择上会
         if ($scope.approve.showController.isReviewLeaderConfirm) {
             if ($scope.pfr == null || $scope.pfr.needMeeting == null) {
                 $.alert("请选择是否需要上会！");
                 return;
             }

             $.ajax({
                 type: 'post',
                 url: srvUrl + "formalAssessmentInfoCreate/saveNeedMeeting.do",
                 data: $.param({
                     "needMeeting": $scope.pfr.needMeeting,
                     'businessId': $scope.approve.businessId
                 }),
                 dataType: "json",
                 async: false,
                 success: function (result) {
                     if (!result.success) {
                         alert(result.result_name);
                         return;
                     }
                 }
             });
         }

         if ($scope.flowVariables == null || $scope.flowVariables == 'undefined' || $scope.flowVariables.opinion == undefined || $scope.flowVariables.opinion == null || $scope.flowVariables.opinion == "") {
             $.alert("审批意见不能为空！");
             return;
         }
         /*if ($scope.flowVariables.opinion.length < 20) {
             $.alert("审批意见不能少于20字！");
             return;
         }*/
         if ($scope.flowVariables.opinion.length > 650) {
             $.alert("审批意见不能超过650字！");
             return;
         }
         if ($scope.approve.showController.isGroupMember) {
             //保存小组成员意见到mongo

             var json = {
                 "opinion": $scope.flowVariables.opinion,
                 "businessId": $scope.approve.businessId,
                 "user": $scope.credentials
             };

             $http({
                 method: 'post',
                 url: srvUrl + "formalAssessmentInfo/saveFixGroupOption.do",
                 data: $.param({"json": JSON.stringify(json)})
             }).success(function (result) {
             });
         }
         var url = srvUrl + "formalAssessmentAudit/auditSingle.do";
         var documentation = $("input[name='bpmnProcessOption']:checked").attr("aaa");

         if (documentation != null && documentation != "") {
             var docObj = JSON.parse(documentation);
             if (docObj.preAction) {
                 var preActionArr = docObj.preAction;
                 for (var i in preActionArr) {
                     if (preActionArr[i].callback == 'validServiceType') {
                         var result = $scope.callfunction(preActionArr[i].callback);
                         if (!result.success) {
                             $.alert(result.result_name);
                             return;
                         }
                     } else if (preActionArr[i].callback == 'validCheckedFzr') {
                         var result = $scope.callfunction(preActionArr[i].callback);
                         if (!result.success) {
                             $.alert(result.result_name);
                             return;
                         } else {
                             //保存任务人员信息
                             $scope.myTaskallocation.businessId = $scope.approve.businessId;
                             $.ajax({
                                 type: 'post',
                                 url: srvUrl + "formalAssessmentAudit/saveTaskToMongo.do",
                                 data: $.param({"task": JSON.stringify($scope.myTaskallocation)}),
                                 dataType: "json",
                                 async: false,
                                 success: function (result) {
                                     if (!result.success) {
                                         alert(result.result_name);
                                         return;
                                     }
                                 }
                             });
                         }
                     } else if (preActionArr[i].callback == 'validCheckedMajor') {

                         var result = $scope.callfunction(preActionArr[i].callback);
                         if (!result.success) {
                             $.alert(result.result_name);
                             return;
                         } else {
                             //保存专业评审人员信息
                             if ($scope.approve.showController.isTask && $scope.professionalReviewers.NAME == null || $scope.myTaskallocation.professionalReviewers.NAME == "") {
                                 $.alert("请选择专业评审人员！");
                                 return;
                             }
                             $scope.myTaskallocation.businessId = $scope.approve.businessId;
                             $.ajax({
                                 type: 'post',
                                 url: srvUrl + "formalAssessmentAudit/saveTaskToMongo.do",
                                 data: $.param({"task": JSON.stringify($scope.myTaskallocation)}),
                                 dataType: "json",
                                 async: false,
                                 success: function (result) {
                                     if (!result.success) {
                                         alert(result.result_name);
                                         return;
                                     }
                                 }
                             });
                         }
                     } else if (preActionArr[i].callback == 'validCheckedFLFzr') {

                         var result = $scope.callfunction(preActionArr[i].callback);
                         if (!result.success) {
                             $.alert(result.result_name);
                             return;
                         } else {
                             //保存专业评审人员信息
                             $scope.myTaskallocation.businessId = $scope.approve.businessId;
                             $.ajax({
                                 type: 'post',
                                 url: srvUrl + "formalAssessmentAudit/saveTaskToMongo.do",
                                 data: $.param({"task": JSON.stringify($scope.myTaskallocation)}),
                                 dataType: "json",
                                 async: false,
                                 success: function (result) {
                                     if (!result.success) {
                                         alert(result.result_name);
                                         return;
                                     }
                                 }
                             });
                         }
                     }
                 }
             }
         }

         show_Mask();
         $http({
             method: 'post',
             url: url,
             data: $.param({
                 "processKey": $scope.approve.processKey,
                 "businessId": $scope.approve.businessId,
                 "opinion": $scope.flowVariables.opinion,
                 "processOption": $("input[name='bpmnProcessOption']:checked").val()
             })
         }).success(function (result) {
             hide_Mask();
             if ($scope.approve.callbackSuccess != null && result.success) {
                 $scope.approve.callbackSuccess(result);
             } else if ($scope.approve.callbackFail != null && !result.success) {
                 $scope.approve.callbackFail(result);
             } else {
                 $.alert(result.result_name);
             }
         });
     };

     /*************************    新增保存是否参会字段   ******************************/
    /* $scope.checkReport = function () {
         $scope.pfr.needReport = "1";
         $scope.pfr.decisionOpinion = null;
     };*/
     /*************************    新增保存是否参会字段   ******************************/


     $scope.$watch("approve", $scope.checkMark);
     $scope.showSubmitModal();
     /***************************知会和加签*******************************/
     $scope._quickComments = ['同意', '不同意'];// 快捷意见下拉选项初始化
     $scope._quickFillComments = function(_eleName, _ngModel){// 快捷意见内容改变事件
         var _quickComments = $('input[name="'+_eleName+'"]:checked').val();
         eval('$scope.' + _ngModel + '="' + _quickComments + '"');
         $scope._signComments = _quickComments;
     };
     // 选择知会人弹窗用户多选数据初始化
     $scope._notifyMappedKeyValue = {"nameField": "NAME", "valueField": "VALUE"};
     $scope._notifyCheckedUsers = [];
     $scope._notifyTempCheckedUsers = [];
     $scope._notifyTempCheckedUsersDisabled = [];
     // 移除知会人
     $scope._notifyRemoveSelectedUser = function (_user) {
         for (var _i = 0; _i < $scope._notifyTempCheckedUsers.length; _i++) {
             if (_user.VALUE == $scope._notifyTempCheckedUsers[_i].VALUE) {
                 $scope._notifyTempCheckedUsers.splice(_i, 1);
                 break;
             }
         }
         _notifyUsersDelete("formalReview", $routeParams.id, _user);
         $scope._notifyInitNotifiesUser("formalReview", $routeParams.id);
         $scope._notifyCheckedUsers = $scope._notifyTempCheckedUsers;
         $scope._notifyRemoveSelectedUser_p(_user);
     };
     $scope._notifyRemoveSelectedUser_p = function (_user) {
         for (var _i = 0; _i < $scope._notifyTempCheckedUsers.length; _i++) {
             if (_user.VALUE == $scope._notifyTempCheckedUsers[_i].VALUE) {
                 $scope._notifyTempCheckedUsers.splice(_i, 1);
                 break;
             }
         }
         $scope._notifyCheckedUsers = $scope._notifyTempCheckedUsers;
     };
     // 指令执行的方法
     $scope._notifyParentSaveSelected = function(){
         var _notifyExecuteEval = '';
         _notifyExecuteEval += '$scope.$parent._notifyCheckedUsers = $scope.checkedUsers;';
         _notifyExecuteEval += '$scope.$parent._notifyTempCheckedUsers = $scope.tempCheckedUsers;';
         _notifyExecuteEval += '$scope.$parent._notifySaveNotifiesUser($scope.$parent.approve.processKey, $scope.$parent.approve.businessId, $scope.$parent._notifyTempCheckedUsers);';
         return _notifyExecuteEval;
     };
     // 初始化知会人信息
     $scope._notifyInitNotifiesUser = function(_business_module, _business_id){
         $scope._notifyCheckedUsers = [];
         $scope._notifyTempCheckedUsers = [];
         $scope._notifyTempCheckedUsersDisabled = [];
     	 var init_notifyTempCheckedUsers = notify_notifiesCheckedTranslate(_business_module, _business_id);
         $scope._notifyCheckedUsers.splice(0, $scope._notifyCheckedUsers.length);
         for (var i = 0; i < init_notifyTempCheckedUsers.length; i++) {
             if(init_notifyTempCheckedUsers[i].AUTH != $scope.credentials.UUID){
                 $scope._notifyTempCheckedUsersDisabled.push(init_notifyTempCheckedUsers[i]);
			 }else{
                 var user = {};
                 user[$scope._notifyMappedKeyValue.nameField] = init_notifyTempCheckedUsers[i].NAME;
                 user[$scope._notifyMappedKeyValue.valueField] = init_notifyTempCheckedUsers[i].VALUE;
                 $scope._notifyCheckedUsers.push(user);
                 $scope._notifyTempCheckedUsers.push(user);
                 delete user.$$hashKey;
			 }
         }
     };
     // 保存知会人信息
     $scope._notifySaveNotifiesUser = function(_business_module, _business_id, _notifyTempCheckedUsers){
         var _notifiesUser = notify_mergeTempCheckedUsers(_notifyTempCheckedUsers);
         notify_saveNotifies(_business_module, _business_id, _notifiesUser);
         $scope._notifyInitNotifiesUser("formalReview", $routeParams.id);
     };
     // 展示加签弹窗
     $scope._showSignDialog = function(){
         $("#_signModalDialog").modal('show');
     };
     $scope._signComments = '';// 加签意见初始化
     // 执行加签
     $scope._executeSign = function(){
         $scope.checkedUser = $scope._signTempCheckedUser;// $scope.checkedUser 为流程中选中的加签用户变量
         // 校验
         if(isEmpty($scope.changeTypeSelected)){
             $.alert('请选择加签类型!');
             return;
         }
         if(isEmpty($scope.checkedUser.VALUE) || isEmpty($scope.checkedUser.NAME)){
             $.alert('请选择加签人员!');
             return;
         }
         $("#_signModalDialog").modal('hide');
     };
     // 指令后置方法
     $scope._signCallback = function(){
         $("#_signUserSinDialog").modal('hide');
         $("#_signModalDialog").modal('show');
     };
     $scope._signMappedKeyValue = {"nameField": "NAME", "valueField": "VALUE"};
     $scope._signCheckedUser = {};
     $scope._signTempCheckedUser = {};
     // 向指令传递的方法
     $scope._signParentSaveSelected = function(){
         var _signExecuteEval = '';
         _signExecuteEval += '$scope.$parent._signCheckedUser = $scope.checkedUser;';
         _signExecuteEval += '$scope.$parent._signTempCheckedUser = $scope.tempCheckedUser;';
         _signExecuteEval += '$scope.$parent._signCallback();';
         return _signExecuteEval;
     };
     // 移除加签用户
     $scope._signRemoveSelectedUser = function(){
         $scope.checkedUser = {};
         $scope._signTempCheckedUser = {};
         $scope._signCheckedUser = {};
     };
     // 加签类型选择改变时执行的方法
     $scope._signChangeTypeSelected = function(_changeTypeSelected){
         $scope.changeTypeSelected = _changeTypeSelected;
         $('#_signChangeRadio').prop("checked", true);
     };
     // 加签意见填充
     $scope._signFillSignComments = function(_eleId){
         $scope._signComments = $('#' + _eleId).val();
     };
     $scope._notifyInitNotifiesUser("formalReview", $routeParams.id);
     /***************************知会和加签*******************************/
     $scope._init_uuid_ = $scope.credentials.UUID;
     $scope._init_messages_array_ = _init_query_messages_list_($routeParams.id);
     //////// 审批阶段，对留言编辑权限的控制
	 //var curTask = wf_getCurrentTask('formalReview', $routeParams.id);
	 //$scope._message_publish_reply_ = !isEmpty(curTask);//&& curTask.TASK_DEF_KEY_ != 'usertask17';
     var curTask = wf_getCurrentProject('formalReview', $routeParams.id);
     $scope._message_publish_reply_ = !isEmpty(curTask) && (curTask.WF_STATE == 0 || curTask.WF_STATE == 1);


     /******************************* 审批标签权限管理 *********************************/
     var curTask = wf_getCurrentTask('formalReview', $routeParams.id);
     var cur
}]);
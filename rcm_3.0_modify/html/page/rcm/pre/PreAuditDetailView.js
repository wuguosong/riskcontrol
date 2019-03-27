ctmApp.register.controller('PreAuditDetailView', ['$routeParams','$http','$scope','$location','Upload', '$filter', function ($routeParams,$http,$scope,$location,Upload, $filter) {
	
	var businessId = $routeParams.id;
    $scope.url = $routeParams.url;
	
	//任务人员选择
	$scope.isTaskEdit = 'false';
	
	$scope.myTaskallocation = {"reviewLeader":null,"majorMember":[]};
	
	//专业评审控制器
	$scope.approveShow = true;
	$scope.pre={};
	$scope.pre.apply = {};
	$scope.pre.taskallocation={};
	/*加签功能初始化必需数据*/
    $scope.changeUserMapper = {"nameField": "NAME", "valueField": "VALUE"};
    $scope.checkedUser = {};
    $scope.callback = function () {
        $("#userSinDialog").modal('hide');
        $("#submitModal").modal('show');
    };
	/*加签功能初始化必需数据*/
	
	//判断新旧流程
	if(businessId.indexOf("@")>0){
		//获取路径
		$scope.oldURL = $location.url();
		//旧流程
		var params = $routeParams.id.split("@");
		businessId = params[0];
		$scope.isOldFlow = true;//新旧流程标识
		var action = $routeParams.action;
		if(null!=params[4] && ""!=params[4]){
			$scope.flag=params[4];
		}
		$scope.findRelationUser(businessId);
	}else{
		//新流程
		$scope.oldUrl = $routeParams.url;
	}
	
	
	//初始化数据
	$scope.initData = function(){
		if ($scope.url == $filter('encodeURI')('#/PreAuditList/0')){
			$scope.WF_STATE = '0';
		} else {
            $scope.WF_STATE = '1';
		}
		//任务人员选择
		$scope.isTaskEdit = 'false';
		//面板控制器
		$scope.showController={};
		//保存按钮控制器
		$scope.showSaveBtn = false;
		$scope.getSelectTypeByCode("06");
		$scope.initUpdate(businessId);
		$scope.queryAuditLogsByBusinessId(businessId);
		$scope.initPage();
		$scope.getTaskInfoByBusinessId(businessId,$scope.wfInfo.processKey);
	}
	
	//旧流程当前节点判断
	$scope.oldFlowTask = function(){
		if($scope.oldURL.indexOf("ProjectPreReviewDetail/Update")>0){
			//投资经理修改、起草
			$scope.showController.isInvestmentManager = true;
		}else if($scope.oldURL.indexOf("ProjectPreReviewViewReport/Approve")>0 || $scope.oldURL.indexOf("PreReviewReport")>0){
			//旧 提交报告 相当于新的确认节点
			$scope.showController.isReviewLeaderConfirm = true;
		}
		
		else if($scope.oldURL.indexOf("companyHeaderApprove")>0){
			//大区审批
			$scope.showController.isLargeArea = true;
			$scope.showSaveBtn = true;
			$("#reviewComments").show();
		}
		else if($scope.oldURL.indexOf("businessHeaderApprove")>0){
			//投资中心/水环境
			$scope.showController.isServiewType = true;
			$scope.showSaveBtn = true;
			$("#reviewComments").show();
		}
		else if($scope.oldURL.indexOf("ReviewLeader")>0){
			//分配任务节点
			$scope.showController.isTask = true;
			$scope.isTaskEdit = 'true'
			$("#taskAssignment1").show();
		}
		else if($scope.oldURL.indexOf("PreReviewComments/approve")>0){
			//评审负责人 1
			$scope.showController.isReviewLeader = true;
			$scope.showSaveBtn = true;
			$("#reviewComments").show();
		}else if($scope.oldURL.indexOf("PreReviewComments/confirm")>0){
			//评审负责人 2
			$scope.showController.isReviewLeader = true;
			$scope.showSaveBtn = true;
			$("#reviewComments").show();
		}
		else if($scope.oldURL.indexOf("ProfessionalReviewComments")>0){
			//固定小组成员
			$scope.showController.isGroupMember = true;
		}
		else if($scope.oldURL.indexOf("CommentsFeedback/edit")>0){
			//投资经理反馈
			$scope.showController.isInvestmentManagerBack = true;
			$scope.showSaveBtn = true;
			$("#reviewComments").show();
		}
		else if($scope.oldURL.indexOf("CommentsFeedback/view1")>0){
			//大区反馈
			$scope.showController.isLargeAreaBack = true;
		}
		else if($scope.oldURL.indexOf("CommentsFeedback/view2")>0){
			//大区反馈
			$scope.showController.isServiewTypeBack = true;
		}
		
	}
	
	/*
	 * 根据业务id查询节点信息
	 * 旧流程只用了hasWaiting
	 * 
	 */
	$scope.getTaskInfoByBusinessId = function(businessId,processKey){
		$http({
			method:'post',  
		    url: srvUrl + "preAudit/queryTaskInfoByBusinessId.do",
		    data:$.param({"businessId":businessId,"processKey":processKey})
		}).success(function(data){
			$scope.hasWaiting = data.result_data.taskId==null?false:true;
			if($location.url().indexOf("ProjectPreReviewView")>0 && $location.url().indexOf("ProjectPreReviewViewReport/Approve")<0 ){
				$scope.hasWaiting = false;
			}
			$scope.taskInfo = data.result_data;
			if(data.result_data.description != null && data.result_data.description != ""){
				//判断是否为旧流程
				if(!$scope.isOldFlow){
					var document = JSON.parse(data.result_data.description);
					//大区
					if(document.isLargeArea != null && document.isLargeArea != ""){
						$scope.showController.isLargeArea = true;
					}
					//大区反馈
					else if(document.isLargeAreaBack != null && document.isLargeAreaBack != ""){
						$scope.showController.isLargeAreaBack = true;
						$("#reviewComments").show();
					}
					//投资中心/水环境
					else if (document.isServiewType != null && document.isServiewType != ""){
						$scope.showController.isServiewType = true;
						$("#reviewComments").show();
					} 
					//任务分配
					else if (document.isTask != null && document.isTask != ""){
						$scope.showController.isTask = true;
						$scope.isTaskEdit = 'true'
						$("#taskAssignment1").show();
					} 
					//投资经理
					else if (document.isInvestmentManager != null && document.isInvestmentManager != ""){
						$scope.showController.isInvestmentManager = true;
					} 
					//固定小组成员
					else if (document.isGroupMember != null && document.isGroupMember != ""){
						$scope.showController.isGroupMember = true;
					}
					//评审负责人
					else if (document.isReviewLeader != null && document.isReviewLeader != ""){
						$scope.showController.isReviewLeader = true;
						$scope.showSaveBtn = true;
						$("#reviewComments").show();
					}
					//评审负责人确认
					else if (document.isReviewLeaderConfirm != null && document.isReviewLeaderConfirm != ""){
						$scope.showController.isReviewLeaderConfirm = true;
					} 
					//投资经理反馈
					else if (document.isInvestmentManagerBack != null && document.isInvestmentManagerBack != ""){
						$scope.showController.isInvestmentManagerBack = true;
						$scope.showSaveBtn = true;
						$("#reviewComments").show();
					}
					//专业评审人员
					else if (document.isMajorMember != null && document.isMajorMember != ""){
						$scope.showController.isMajorMember = true;
						$scope.showSaveBtn = true;
						$("#majorMemberComments").show();
					}
				}
			}
		});
	}
	
	//新流程相关
	$scope.initPage = function(){
		if($scope.isOldFlow){
			debugger;
			//旧流程
			$scope.oldFlowTask();
			if(typeof (params[1])=='string' && typeof (params[2])=='string' && params[1]!='' && params[2]!=''){//已经启动流程
				$scope.wfInfo = {processDefinitionId:params[1],processInstanceId:params[2],processKey:'preAssessment'};
			}else{//未启动流程
				$scope.wfInfo = {processKey:'preAssessment'};
			}
		}else{
			//新流程
			$scope.wfInfo = {processKey:'preReview'};
			$scope.wfInfo.businessId = businessId;
			$scope.refreshImg = Math.random()+1;
		}
	}
	var fgIdArr = [];
	var fgNameArr = [];

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
			
			$scope.attach = data.result_data.attach;
			//处理附件
            $scope.reduceAttachment(data.result_data.mongoData.attachmentList, businessId);
			
			if(!$scope.pre.approveAttachment){
				$scope.addFormalComment();
				$scope.approveShow = false;
				$scope.pre.approveAttachment = {};
			}
			
			//日期处理
			if($scope.pre.create_date){
				$scope.pre.create_date = $scope.pre.create_date.substring(0,10);
			}
			if($scope.pre.apply.paymentTime){
				$scope.pre.apply.paymentTime = $scope.pre.apply.paymentTime.substring(0,10);
			}
			if($scope.pre.apply.tenderTime){
				$scope.pre.apply.tenderTime = $scope.pre.apply.tenderTime.substring(0,10); 
			}
			
			$scope.pre.oracle  = data.result_data.oracle;
			$scope.pre.apply.projectModel;
			if($scope.pre.taskallocation){
				var fg=$scope.pre.taskallocation.fixedGroup;
				if(null!=fg && fg.length>0){
					for(var k=0;k<fg.length;k++){
						fgNameArr.push(fg[k].NAME);
						fgIdArr.push(fg[k].VALUE);
					}
					$scope.pre.taskallocation.fixedGroup=fgNameArr.join(",");
				}
			}
			//处理任务人
			if($scope.pre.taskallocation !=null ){
				if($scope.pre.taskallocation.reviewLeader!=null){
					$scope.myTaskallocation.reviewLeader = $scope.pre.taskallocation.reviewLeader;
				}
			}
			
			var ptNameArr=[],pmNameArr=[],pthNameArr=[],pt1NameArr=[];
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
			
			$scope.fileName=[];
			var filenames=$scope.pre.attachment;
			for(var i=0;i<filenames.length;i++){
				var arr={UUID:filenames[i].UUID,ITEM_NAME:filenames[i].ITEM_NAME};
				$scope.fileName.push(arr);
			}
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
	}
	$scope.dealVersion = function(fileList){
		if(null == fileList|| !Array.isArray(fileList) || fileList.length == 0 ){
			return [];
		}
		var attachmentList = $scope.pre.attachment;
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
	//保存
	$scope.save = function(callBack){
		show_Mask();
		if($scope.showController.isReviewLeader || $scope.showController.isInvestmentManagerBack){
			var attachmentNew =  $scope.pre.approveAttachment.attachmentNew;
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
				var attachmentList = $scope.pre.attachment;
				var approveAttachment = $scope.pre.approveAttachment;
				if(null != approveAttachment.attachmentNew && approveAttachment.attachmentNew.length > 0 ){
					var attachmentNew = approveAttachment.attachmentNew;
					$scope.pre.approveAttachment.attachmentNew = $scope.dealVersion(attachmentNew);
				}
//			}
			if($scope.showController.isInvestmentManagerBack){
				//投资经理保存前加附件验证
				var attachmentNew = $scope.pre.approveAttachment.attachmentNew
				if(attachmentNew != null && attachmentNew !=""){
					for(var i in attachmentNew){
						if(attachmentNew[i].isInvestmentManagerBackEdit =='1' || attachmentNew[i].isInvestmentManagerBackEdit == null){
							if(attachmentNew[i].attachment_new != null && attachmentNew[i].attachment_new != ""){
								if(attachmentNew[i].attachment_new.version == null || attachmentNew[i].attachment_new.version == ""){
									$.alert("请上传最新版本附件！");
									hide_Mask();
									return false;
								}
							}
						}
					}
				}
			}
			$scope.pre.approveAttachment = $scope.reduceVersion($scope.pre.approveAttachment);
			//评审负责人 //投资经理反馈
			$http({
				method:'post',  
			    url: srvUrl + "preInfo/saveReviewInfo.do",
			    data:$.param({"businessId":businessId,"json":JSON.stringify(angular.copy($scope.pre.approveAttachment))})
			}).success(function(data){
				if(!data.success){
					$.alert("保存失败");
					hide_Mask();
					return;
				}				
				if(callBack){
					//验证
					if($scope.showController.isReviewLeader){
						var commentsList =  $scope.pre.approveAttachment.commentsList;
						if(commentsList){
							for(var i in commentsList){
								if(commentsList[i].isReviewLeaderEdit =='1'){
									if(commentsList[i].opinionType == null || commentsList[i].opinionType==""){
										$.alert("请填写意见类型！");
										hide_Mask();
										return false;
									}
								}
							}
						}
						if($scope.pre.approveAttachment.riskWarning == null || $scope.pre.approveAttachment.riskWarning ==""|| $scope.pre.approveAttachment.riskWarning ==undefined){
							$.alert("请填写重点风险提示！");
							hide_Mask();
							return false;
						}
						var attachmentNew =  $scope.pre.approveAttachment.attachmentNew;
						for(var i in attachmentNew){
							if(attachmentNew[i].isReviewLeaderEdit =='1'){
								if(attachmentNew[i].attachmentUList == null || attachmentNew[i].attachmentUList=="" || attachmentNew[i].attachmentUList==undefined){
									$.alert("请选择需要更新的附件！");
									hide_Mask();
									return false;
								}
							}
						}
					}if($scope.showController.isInvestmentManagerBack){
						var commentsList =  $scope.pre.approveAttachment.commentsList;
						if(commentsList){
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
						var attachmentNew = $scope.pre.approveAttachment.attachmentNew
						if(attachmentNew != null && attachmentNew !=""){
							for(var i in attachmentNew){
								if(attachmentNew[i].isInvestmentManagerBackEdit =='1'){
									if(attachmentNew[i].attachment_new != null && attachmentNew[i].attachment_new != ""){
										if(attachmentNew[i].attachment_new.approved == null || attachmentNew[i].attachment_new.approved == "" || angular.equals({}, attachmentNew[i].attachment_new.approved)){
											$.alert("请选择附件审核人！");
											hide_Mask();
											return false;
										}
										if(attachmentNew[i].attachment_new.programmed == null || attachmentNew[i].attachment_new.programmed == ""|| angular.equals({}, attachmentNew[i].attachment_new.programmed)){
											$.alert("请选择附件编制人！");
											hide_Mask();
											return false;
										}
									}
								}
							}
						}
					}
					hide_Mask();
					callBack();
				}else{
					$scope.initData();
					hide_Mask();
					$.alert(data.result_name);
				}
			});
		}else if($scope.showController.isMajorMember){
			//保存专业评审人员填写的信息
//			$http({
//				method:'post',  
//			    url: srvUrl + "preInfo/saveMajorMemberInfo.do",
//			    data:$.param({"businessId":businessId,"json":JSON.stringify(angular.copy($scope.pre.approveAttachment))})
//			}).success(function(data){
//				if(!data.success){
//					$.alert("保存失败");
//					return;
//				}
//				if(callBack){
//					//验证
//					callBack();
//				}else{
//					$.alert(data.result_name);
//				}
//			});
		}else{
			if(callBack){
				hide_Mask();
				callBack();
			}
		}
		
	}
	//审批旧版
	$scope.showOldSubmitModal = function(){
		$scope.save(function(){
			if($scope.oldURL.indexOf("ProjectPreReviewDetail/Update")>0){
				//投资经理起草
				$scope.investmentManagerNodeAudit();
			}else if($scope.oldURL.indexOf("ProjectPreReviewViewReport/Approve")>0 || $scope.oldURL.indexOf("PreReviewReport")>0 ){
				//原提交报告
				//现评审负责人确认
				$scope.reviewLeaderConfirmNodeAudit();
			}else if($scope.oldURL.indexOf("ReviewLeader")>0){
				//分配任务
				$scope.taskNodeAudit();
			}else if($scope.oldURL.indexOf("PreReviewComments/approve")>0){
				//评审负责人
				$scope.reviewLeaderNodeAudit();
			}else if($scope.oldURL.indexOf("CommentsFeedback/edit")>0){
				//投资经理反馈
				$scope.investManagerFeedbackNodeAudit();
			}else if($scope.oldURL.indexOf("companyHeaderApprove")>0){
				//大区
				$scope.largeAreaNodeAudit();
			}else if($scope.oldURL.indexOf("CommentsFeedback/view1")>0){
				//大区反馈
				$scope.largeAreaFeedbackNodeAudit();
			}else if($scope.oldURL.indexOf("PreReviewComments/confirm")>0){
				//评审负责人
				$scope.reviewLeaderNodeAudit();
			}else if($scope.oldURL.indexOf("ProfessionalReviewComments")>0){
				//固定小组成员
				$scope.fixGroupNodeAudit();
			}else if($scope.oldURL.indexOf("CommentsFeedback/view2")>0){
				//投资中心/水环境反馈
				$scope.serviceTypeFeedbackNodeAudit();
			}
			else if($scope.oldURL.indexOf("businessHeaderApprove")>0){
				//投资中心/水环境
				$scope.serviceTypeNodeAudit();
			}
			
			$('#oldSubmitModal').modal('show');
		});
	}
	
	$scope.investmentManagerNodeAudit = function(){
		var companyHeader = $scope.pre.apply.companyHeader;
        $scope.approve = [{
            submitInfo:{
                startVar:{processKey:'preAssessment',businessId:$scope.pre.id,subject:$scope.pre.apply.projectName+'预评审申请',inputUser:$scope.credentials.UUID},
                runtimeVar:{inputUser:companyHeader.VALUE},
                currentTaskVar:{opinion:'请审批'}
            },
            showInfo:{destination:'单位负责人审核',approver: companyHeader.NAME}
        }];
        $scope.approve[0].submitInfo.taskId=params[3];
	}
	
	$scope.reviewLeaderConfirmNodeAudit = function(){

		$scope.approve = [{
			toNodeType:'end',
			submitInfo:{
				taskId:params[3],
				noticeInfo:{
					infoSubject:$scope.pre.apply.projectName+'预评审决策材料',
					businessId:params[0],
					remark:'',
					formKey:'/BiddingInfoReview/'+params[0],
					createBy:$scope.credentials.UUID,
					type:'1',
					custText01:'预评审'
				},
				runtimeVar:{}
			}
		}];
	}
	
	$scope.taskNodeAudit = function(){
    	var currentUserName = $scope.credentials.userName;
        var approveUser = $scope.myTaskallocation.reviewLeader;
        var investmentManager = $scope.pre.apply.investmentManager;
        if(!approveUser.VALUE){
        	approveUser = {"NAME":"","VALUE":""};
        }
        
        //查询评审组长
        $scope.httpData("rcm/Pteam/getTeamLeaderByMemberId",{teamMemberId:approveUser.VALUE,type:'1'}).success(function(data){
            if(data.result_code=='S'){
            	$scope.approve = {"isAllocateTask":true};
                $scope.approve.approve = [{
                    submitInfo:{
                        taskId:params[3],
                        businessId:params[0],
                        runtimeVar:{inputUser:approveUser.VALUE,toTask:0},
                        currentTaskVar:{opinion:'请审批'},
                        noticeInfo:{
                            infoSubject:$scope.pre.apply.projectName+'预评审任务分配给了'+approveUser.NAME+'，请悉知！',
                            businessId:params[0],
                            remark:'',
                            formKey:'/ProjectPreReviewView/'+params[0]+'@'+params[1]+'@'+params[2],
                            createBy:$scope.credentials.UUID,
                            reader:[data.result_data],
                            type:'1',
                            custText01:'预评审'
                        }
                    },
                    showInfo:{destination:'评审负责人审批',approver:approveUser.NAME}
                },{
                    submitInfo:{
                        taskId:params[3],
                        businessId:params[0],
                        runtimeVar:{inputUser:investmentManager.VALUE,toTask:2},
                        currentTaskVar:{opinion:'请修改'}
                    },
                    showInfo:{destination:'退回起草人',approver:investmentManager.NAME}
                },{
                    submitInfo:{
                    	businessId:params[0],
                        taskId:params[3],
                        runtimeVar:{toTask:1}
                    },
                    toNodeType:'end'
                }];
            }else{
                $.alert('查询评审组长失败');
            }
        });
	}
	
	$scope.reviewLeaderNodeAudit = function(){
        var opinion = "", destination="", approverName="";
        var currentUserName = $scope.credentials.userName;
        var investMentUser = $scope.pre.apply.investmentManager;
        $scope.approve = [{
            submitInfo:{
                taskId:params[3],
                runtimeVar:{inputUser:investMentUser.VALUE,toTask:0},
                currentTaskVar:{opinion:"请反馈"}
            },
            showInfo:{destination: '投资经理反馈',approver:investMentUser.NAME}
        },{
            toNodeType:'end',
            submitInfo:{
                taskId:params[3],
                runtimeVar:{toTask:2}
            },
            showInfo:{destination: "终止流程"}
        }];
        if(null == fgIdArr){
        	$scope.approve.push({
        		submitInfo:{
        			taskId:params[3],
        			runtimeVar:{assigneeList:fgIdArr,toTask:1},
        			currentTaskVar:{opinion:'请审批'}
        		},
        		showInfo:{destination: "固定小组成员审批",approver:fgNameArr.join(',')}
        	});
        }
        
        var passParam={
            submitInfo:{
                taskId:params[3],
                runtimeVar:{inputUser:$scope.credentials.UUID,toTask:2},
                currentTaskVar:{opinion:"请填写评审报告"}
            },
            showInfo:{destination: "填写评审报告",approver:currentUserName}
        };
        if($routeParams.action=='approve'){
            passParam.submitInfo.runtimeVar.toTask='3';
        }
        $scope.approve.splice(2,0,passParam);
	}
	
	$scope.investManagerFeedbackNodeAudit = function(){

        //子流程中多个节点都调用此页面，流程要根据url中的action决定流程下一步处理人,下一节点名称
        var currentUserName = $scope.credentials.userName;
        var inputUser,inputUserName, destination, isSkipServiceType;
        var companyHeader = $scope.pre.apply.companyHeader;
        inputUser = companyHeader.VALUE;
        inputUserName = companyHeader.NAME;
        destination = "单位负责人审批";
        $scope.approve = [{
            submitInfo:{
                taskId: params[3],
                runtimeVar: {inputUser: inputUser,toTask:0, isSkipServiceType:isSkipServiceType},
                currentTaskVar: {opinion: '请审批'}
            },
            showInfo:{destination: destination, approver:inputUserName}
        }];
        $scope.approve[0].submitInfo.runtimeVar = {inputUser:inputUser};
	}
	
	$scope.largeAreaNodeAudit = function(){

		var userName = $scope.credentials.userName;
		var approveUser =  $scope.pre.apply.investmentPerson;
		if(approveUser == null || typeof(approveUser)=='undefined'|| approveUser.NAME==null||approveUser.NAME==""){
			approveUser = $scope.pre.apply.directPerson;
		}
		var investmentManager = $scope.pre.apply.investmentManager;//投资经理
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
						var investmentManager = $scope.pre.apply.investmentManager;//投资经理
						if(ma){
							nextNode = {
								submitInfo:{
									taskId:params[3],
									runtimeVar:{inputUser:ma.VALUE,toTask:0,isSkipServiceType:"1"},
									currentTaskVar:{opinion:'请审批'},
									businessId:params[0],
									noticeInfo:{
										infoSubject:$scope.pre.apply.projectName+'预评审申请进入风控审批阶段，请悉知！',
										businessId:params[0],
										remark:'',
										formKey:'/ProjectPreReviewView/'+params[0]+'@'+params[1]+'@'+params[2],
										createBy:$scope.credentials.UUID,
										reader:$scope.removeObjByValue(removeDuplicate($scope.relationUsers),ma.VALUE),
										type:'1',
										custText01:'预评审'
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
						runtimeVar:{inputUser:approveUser.VALUE,toTask:0,isSkipServiceType:"0"},
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
	}
	
	$scope.largeAreaFeedbackNodeAudit = function(){
        //子流程中多个节点都调用此页面，流程要根据url中的action决定流程下一步处理人,下一节点名称
        var currentUserName = $scope.credentials.userName;
        var action = $routeParams.action, inputUser,inputUserName, destination, isSkipServiceType;
        var approveUser =  $scope.pre.apply.investmentPerson;
        if(approveUser == null || typeof(approveUser)=='undefined'|| approveUser.NAME==null||approveUser.NAME==""){
            approveUser = $scope.pre.apply.directPerson;
        }
        if(approveUser==null||approveUser.NAME==null||approveUser==""||approveUser.NAME==""){
        	var approveUser =  $scope.pre.taskallocation.reviewLeader;
            inputUser = approveUser.VALUE;
            inputUserName = approveUser.NAME;
            destination = "评审负责人确认";
            isSkipServiceType = "1";
        }else{
        	inputUser = approveUser.VALUE;
            inputUserName = approveUser.NAME;
            destination = "投资中心/水环境投资中心审核";
            isSkipServiceType = "0";
        }
        $scope.approve = [{
            submitInfo:{
                taskId: params[3],
                runtimeVar: {inputUser: inputUser,toTask:0, isSkipServiceType:isSkipServiceType},
                currentTaskVar: {opinion: '请审批'}
            },
            showInfo:{destination: destination, approver:inputUserName}
        }];
        var investmentManager = $scope.pre.apply.investmentManager;
        var backInfo = {
            submitInfo:{
                taskId:params[3],
                runtimeVar:{inputUser:investmentManager.VALUE,toTask:'-1'},
                currentTaskVar:{opinion:'请修改'}
            },
            showInfo:{destination:'退回投资经理反馈',approver:investmentManager.NAME}
        }
        $scope.approve.push(backInfo);
	}
	
	$scope.fixGroupNodeAudit = function(){
		var userName = $scope.credentials.userName;
		var approveUser =  $scope.pre.taskallocation.reviewLeader;
		$scope.approve = [{
			submitInfo:{
				taskId:params[3],
				runtimeVar:{inputUser:approveUser.VALUE},
				currentTaskVar:{opinion:'请审批'},
				newTaskVar:{submitBy:userName,emergencyLevel:'一般'}
			},
			showInfo:{destination:'评审负责人确认',approver: approveUser.NAME}
		}];
	}
	
	$scope.serviceTypeFeedbackNodeAudit = function(){
		//子流程中多个节点都调用此页面，流程要根据url中的action决定流程下一步处理人,下一节点名称
        var currentUserName = $scope.credentials.userName;
        var action = $routeParams.action, inputUser,inputUserName, destination, isSkipServiceType;
            var approveUser =  $scope.pre.taskallocation.reviewLeader;
            inputUser = approveUser.VALUE;
            inputUserName = approveUser.NAME;
            destination = "评审负责人确认";
        $scope.approve = [{
            submitInfo:{
                taskId: params[3],
                runtimeVar: {inputUser: inputUser,toTask:0, isSkipServiceType:isSkipServiceType},
                currentTaskVar: {opinion: '请审批'}
            },
            showInfo:{destination: destination, approver:inputUserName}
        }];
        var investmentManager = $scope.pre.apply.investmentManager;
        var backInfo = {
            submitInfo:{
                taskId:params[3],
                runtimeVar:{inputUser:investmentManager.VALUE,toTask:'-1'},
                currentTaskVar:{opinion:'请修改'}
            },
            showInfo:{destination:'退回投资经理反馈',approver:investmentManager.NAME}
        }
        $scope.approve.push(backInfo);
	}
	
	$scope.serviceTypeNodeAudit = function(){

		$scope.httpData('common/commonMethod/getRoleuserByCode','5').success(function(data){
			if(data.result_code == 'S'){
				var ma = data.result_data[0];
				var investmentManager = $scope.pre.apply.investmentManager;
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
							runtimeVar:{inputUser:ma.VALUE,toTask:0},
							currentTaskVar:currentTaskVar,
							businessId:params[0],
							noticeInfo:{
								infoSubject:$scope.pre.apply.projectName+'预评审申请进入风控审批阶段，请悉知！',
								businessId:params[0],
								remark:'',
								formKey:'/ProjectPreReviewView/'+params[0]+'@'+params[1]+'@'+params[2],
								createBy:$scope.credentials.UUID,
								reader:$scope.removeObjByValue(removeDuplicate($scope.relationUsers),ma.VALUE),
								type:'1',
								custText01:'预评审'
							}
						},
						showInfo:{destination:'分配评审任务',approver: ma.NAME}
					},{
						submitInfo:{
							taskId:params[3],
							businessId:params[0],
							runtimeVar:{inputUser:investmentManager.VALUE,toTask:1},
							currentTaskVar:{opinion:'请修改'}
						},
						showInfo:{destination:'退回起草人',approver:investmentManager.NAME}
					}];
				}else{
					$.alert("评审分配人不存在，请先设置评审分配人！");
				}
			}
		})
	}
	
	//弹出审批框新版
	$scope.showSubmitModal = function(){
		$scope.save(function(){
			$http({
	    		method:'post',  
			    url: srvUrl + "preAudit/querySingleProcessOptions.do",
			    data: $.param({
			    	"businessId":businessId
			    })
	    	}).success(function(result){
	    		$scope.approve = {
					operateType: "audit",
					processKey: "preReview",
					processOptions: result.result_data,
					businessId:  businessId,
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
                if ($scope.curLog.CHANGETYPE) {
                    if($scope.curLog.CHANGETYPE != ''){
                        $scope.approve.operateType = "change";
                    }
                }
	    		$('#submitModal').modal('show');
	    	});
		});
	}
	
	
	//获取意见类型
	$scope.getSelectTypeByCode=function(typeCode){
		var  url = 'common/commonMethod/selectDataDictionByCode';
		$http({
			method:'post',  
		    url: srvUrl + url,
		    data: typeCode
		}).success(function(data){
			if(data.success){
				$scope.optionTypeList=data.result_data;
			}else{
				alert(data.result_name);
			}
		});
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
        if(undefined==$scope.pre.approveAttachment){
            $scope.pre.approveAttachment={commentsList:[]};

        }
        if(undefined==$scope.pre.approveAttachment.commentsList){
            $scope.pre.approveAttachment.commentsList=[];
        }
        addBlankRow($scope.pre.approveAttachment.commentsList);
    }

    $scope.deleteFormalComment = function(n){
        var commentsObj=null;
        if(n==0) {
            commentsObj = $scope.pre.approveAttachment.commentsList;
        }else if(n==1){
            commentsObj = $scope.pre.approveAttachment.attachmentNew;
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
        if(undefined==$scope.pre.approveAttachment){
            $scope.pre.approveAttachment={attachmentNew:[]};
        }
        if(undefined==$scope.pre.approveAttachment.attachmentNew){
            $scope.pre.approveAttachment.attachmentNew=[];
        }
        addBlankRow($scope.pre.approveAttachment.attachmentNew);
    }
	
	//上传
	$scope.errorAttach=[];
	//评审负责人上传文件
	$scope.upload2Review = function (file,errorFile, idx) {
        if(errorFile && errorFile.length>0){
        	var errorMsg = fileErrorMsg(errorFile);
        	$scope.errorAttach[idx]={msg:errorMsg};
        }else if(file){
            var fileFolder = "preAssessment/";
                var dates=$scope.pre.create_date+"";
                var no=$scope.pre.apply.projectNo;
                var strs= new Array(); //定义一数组
                strs=dates.split("-"); //字符分割
                dates=strs[0]+strs[1]; //分割后的字符输出
                fileFolder=fileFolder+dates+"/"+no;
            $scope.errorAttach[idx]={msg:''};
            Upload.upload({
                url:srvUrl+'common/RcmFile/upload',
                data: {file: file, folder:'',typeKey:'preAssessmentPath'}
            }).then(function (resp) {
                $scope.pre.approveAttachment.commentsList[idx].files=resp.data.result_data[0];
            }, function (resp) {
                console.log('Error status: ' + resp.status);
            }, function (evt) {
                var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                $scope["progress"+idx]=progressPercentage == 100 ? "":progressPercentage+"%";
            });
        }
    };
	$scope.uploadReview = function (file,errorFile, idx,name,versionNun) {
			if(name == null){
				$.alert("请选择需要替换的附件");
				return;
			}
	        if(errorFile && errorFile.length>0){
	        	var errorMsg = fileErrorMsg(errorFile);
	        	$scope.errorAttach[idx]={msg:errorMsg};
	        }else if(file){

	            $scope.errorAttach[idx]={msg:''};
	            Upload.upload({
	                url:srvUrl+'common/RcmFile/upload',
	                data: {file: file, folder:'',typeKey:'preAssessmentPath'}
	            }).then(function (resp) {
	                var retData = resp.data.result_data[0];
	               
	                var myDate = new Date();
	                var paddNum = function(num){
	                    num += "";
	                    return num.replace(/^(\d)$/,"0$1");
	                }
	                retData.upload_date=myDate.getFullYear()+"-"+paddNum(myDate.getMonth()+1)+"-"+ myDate.getDate()+" "+myDate.getHours()+":"+myDate.getMinutes()+":"+myDate.getSeconds();
	                if($scope.pre.approveAttachment.attachmentNew[idx].attachment_new == null || $scope.pre.approveAttachment.attachmentNew[idx].attachment_new == ""){
	                	$scope.pre.approveAttachment.attachmentNew[idx].attachment_new = [];
	                }
	                if(versionNun != null && versionNun != ''){
	                	$scope.pre.approveAttachment.attachmentNew[idx].attachment_new.version = versionNun;
	                }
	                $scope.pre.approveAttachment.attachmentNew[idx].attachment_new.fileName=retData.fileName;
	                $scope.pre.approveAttachment.attachmentNew[idx].attachment_new.filePath=retData.filePath;
	                $scope.pre.approveAttachment.attachmentNew[idx].attachment_new.upload_date=retData.upload_date;
	            }, function (resp) {
	                console.log('Error status: ' + resp.status);
	            }, function (evt) {
	                var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
	                $scope["progress"+idx]=progressPercentage == 100 ? "":progressPercentage+"%";
	            });
	        }
	 };
	
	$scope.initData();
    $scope.curLog = wf_getTaskLog("preReview", $routeParams.id, $scope.credentials.UUID);
}]);

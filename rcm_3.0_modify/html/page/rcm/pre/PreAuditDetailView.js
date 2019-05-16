ctmApp.register.controller('PreAuditDetailView', ['$routeParams','$http','$scope','$location','Upload', '$filter', function ($routeParams,$http,$scope,$location,Upload, $filter) {
    $scope.businessId = $routeParams.id;
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
			$scope.reduceAttachment(data.result_data.mongo.attachmentList, businessId);

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
			if(filenames != null && filenames.length > 0){
                for(var i=0;i<filenames.length;i++){
                    var arr={UUID:filenames[i].UUID,ITEM_NAME:filenames[i].ITEM_NAME};
                    $scope.fileName.push(arr);
                }
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
                if (!isEmpty($scope.curLog)) {
                    if (!isEmpty($scope.curLog.CHANGETYPE)) {
                        if($scope.curLog.CHANGETYPE != ''){
                            $scope.approve.operateType = "change";
                        }
                    }
                }
	    		// $('#submitModal').modal('show');
	    	});
		});
	};


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
    /************************************流程代码*************************/
    $scope.changeTypeSelected = "";
    $scope.changeTypes = [{key: 'before', value: '前加签'}, {key: 'after', value: '后加签'}];
    $scope.pre = {};
    //默认不上会
    $scope.pre.needMeeting = '0';
    //默认不出报告
    $scope.pre.needReport = '1';
    $scope.pre.decisionOpinion = false;
    //验证任务人员
    $scope.callfunction = function (functionName) {
        var func = eval(functionName);
        //创建函数对象，并调用
        return new func(arguments[1]);
    }
    var validServiceType = function () {
        var result = {success: true, result_name: ""};
        if ($scope.pre.oracle.SERVICETYPE_ID != "1401" && $scope.pre.oracle.SERVICETYPE_ID != "1402") {
            result.success = false;
            result.result_name = "此项目非传统水务、水环境项目！无法选择此选项！";
        }
        return result;
    };
    //验证任务人员信息(分配任务节点)
    var validCheckedFzr = function () {
        var result = {success: true, result_name: ""};
        if ($scope.myTaskallocation == null || $scope.myTaskallocation == "") {
            result.success = false;
            result.result_name = "请分配负责人！";
        }
        if ($scope.myTaskallocation.reviewLeader.NAME == null || $scope.myTaskallocation.reviewLeader.NAME == "") {
            result.success = false;
            result.result_name = "请选择评审负责人！";
        }
        return result;
    };
    /**加签参数初始化add by LiPan 2019-03-08**/
    $scope.showSelectPerson = function () {
        $("#submitModal").modal('hide');
        $("#userSinDialog").modal('show');
    };
    $scope.changeWork = function () {
        console.log($scope.approve);
        //人员验证
        if ($scope.checkedUser.NAME == null || $scope.checkedUser.NAME == '') {
            $.alert("请选择加签人！");
            return;
        }
        if ($scope.checkedUser.VALUE == $scope.credentials.UUID) {
            $.alert("加签人不能是自己！");
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
        if ($scope.flowVariables.opinion.length < 20) {
            $.alert("审批意见不能少于20字！");
            return;
        }
        if ($scope.flowVariables.opinion.length > 650) {
            $.alert("审批意见不能超过650字！");
            return;
        }
        if ($scope.changeTypeSelected == 'after') {
            var validate = wf_validateSign('preReview', $scope.approve.businessId);
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
                'business_module': 'preReview',
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
        if ($scope.flowVariables.opinion.length < 20) {
            $.alert("审批意见不能少于20字！");
            return;
        }
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
                "business_module": "preReview",
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
        //验证意见（投资中心/水环境）
    var validOpinions = function () {
            var result = {success: true, result_name: ""};
            if ($scope.submitInfo.currentTaskVar == null || $scope.submitInfo.currentTaskVar.cesuanFileOpinion == null || $scope.submitInfo.currentTaskVar.cesuanFileOpinion == "") {
                result.success = false;
                result.result_name = "请填写测算文件意见！";
            }
            if ($scope.submitInfo.currentTaskVar == null || $scope.submitInfo.currentTaskVar.tzProtocolOpinion == null || $scope.submitInfo.currentTaskVar.tzProtocolOpinion == "") {
                result.success = false;
                result.result_name = "请填写投资协议意见！";
            }
            return result;
        }

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
        if(processOptions == null || processOptions.length < 1){
            return;
        }
        if (processOptions[0].documentation != null && processOptions[0].documentation != '') {
            var docObj = JSON.parse(processOptions[0].documentation);

            if (docObj.mark == "reviewPassMark") {
                $scope.showReviewToConfirm = true;
            }
            if (docObj.mark == "legalPassMark") {
                $scope.showLegalToConfirm = true;
            }
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
                    if (i == 0) {
                        $scope.showMark = true;
                    }
                    //查询后台的评价记录
                    $.ajax({
                        type: 'post',
                        url: srvUrl + "preMark/queryMarks.do",
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
        }
        if ($("input[name='bpmnProcessOption']:checked").val() == 'WORKOVER') {
            $scope.showReviewToConfirm = false;
        }
        /********Add By LiPan********/
        if ("submit" == $scope.approve.operateType) {
            $scope.submit();
        } else if ("audit" == $scope.approve.operateType) {
            if ($scope.showReviewConfirmToEnd) {
                $.confirm("您选择了终止项目，意味着您将废弃此项目！是否确认？", function () {
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
    $scope.checkReport = function () {
        $scope.pre.needReport = "1";
        $scope.pre.decisionOpinion = null;
    };
    $scope.submit = function () {
        var url = srvUrl + "preAudit/startSingleFlow.do";
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

    $scope.auditSingle = function () {
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
                url: srvUrl + "preMark/saveOrUpdate.do",
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
        //验证确认节点是否选择上会，与报告
        if ($scope.approve.showController.isReviewLeaderConfirm) {
            if ($scope.pre == null || $scope.pre.needMeeting == null) {
                $.alert("请选择是否需要上会！");
                return;
            }
            if ($scope.pre.needReport == null) {
                $.alert("请选择是否需要出评审报告！");
                return;
            }

            if ($scope.pre.needReport == 0) {
                if ($scope.pre.noReportReason == null || $scope.pre.noReportReason == '') {
                    $.alert("请填写不出报告原因！");
                    return;
                }
                if ($scope.pre.noReportReason.length > 200) {
                    $.alert("不出报告原因不能大于200字！");
                    return;
                }
            }
            if ($scope.pre.decisionOpinion) {
                $scope.pre.decisionOpinion = '6';
            } else {
                $scope.pre.decisionOpinion = '5';
            }
            $.ajax({
                type: 'post',
                url: srvUrl + "preInfo/saveNeedMeetingAndNeedReport.do",
                data: $.param({
                    "pre": JSON.stringify($scope.pre),
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
        if ($scope.flowVariables.opinion.length < 20) {
            $.alert("审批意见不能少于20字！");
            return;
        }
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
                url: srvUrl + "preInfo/saveFixGroupOption.do",
                data: $.param({"json": JSON.stringify(json)})
            }).success(function (result) {
            });
        }
        var url = srvUrl + "preAudit/auditSingle.do";
        var documentation = $("input[name='bpmnProcessOption']:checked").attr("aaa");

        if (documentation != null && documentation != "") {
            var docObj = JSON.parse(documentation);
            if (docObj.preAction) {
                var preActionArr = docObj.preAction;
                for (var i in preActionArr) {
                    //validServiceType
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
                                url: srvUrl + "preInfo/saveTaskPerson.do",
                                data: $.param({
                                    "task": JSON.stringify(angular.copy($scope.myTaskallocation)),
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
                    } else if (preActionArr[i].callback == 'validOpinions') {
                        var result = $scope.callfunction(preActionArr[i].callback);
                        if (!result.success) {
                            $.alert(result.result_name);
                            return;
                        } else {
                            //保存意见信息
                            $.ajax({
                                type: 'post',
                                url: srvUrl + "preInfo/saveServiceTypeOpinion.do",
                                data: $.param({
                                    "serviceTypeOpinion": JSON.stringify($scope.submitInfo.currentTaskVar),
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
                    } else if (preActionArr[i].callback == 'validCheckedMajorMember') {

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
    $scope.$watch("approve", $scope.checkMark);
    $scope.showSubmitModal();
	/**************知会和加签******************/
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
        _notifyUsersDelete("preReview", $routeParams.id, _user);
        $scope._notifyInitNotifiesUser("preReview", $routeParams.id);
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
        $scope._notifyInitNotifiesUser("preReview", $routeParams.id);
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
    $scope._notifyInitNotifiesUser("preReview", $routeParams.id);
    /**************知会和加签******************/
    $scope._init_uuid_ = $scope.credentials.UUID;
    $scope._init_messages_array_ = _init_query_messages_list_($routeParams.id);
    ////////////审批阶段对留言编辑权限的控制
    var curTask = wf_getCurrentTask('preReview', $routeParams.id);
    $scope._message_publish_reply_ = !isEmpty(curTask) ;//&& curTask.TASK_DEF_KEY_ != 'usertask8';
}]);

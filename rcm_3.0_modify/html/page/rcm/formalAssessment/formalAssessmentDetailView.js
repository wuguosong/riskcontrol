ctmApp.register.controller('FormalAssessmentDetailView',['$http','$scope','$location','$routeParams','Upload','$timeout', 
 function ($http,$scope,$location,$routeParams,Upload,$timeout) {
	//初始化
	$scope.oldUrl = $routeParams.url;
	var objId = $routeParams.id;
	$scope.businessId = $routeParams.id;
	$scope.pfr={};
	$scope.pfr.apply = {};
	$scope.pfr.taskallocation={};
	$scope.pfr.approveAttachment = {};
	$scope.pfr.pfrBusinessUnitCommit=[];
	$scope.pfr._id=objId;
	$scope.dic=[];
	$scope.myTaskallocation = {};
	
	//专业评审控制器
	$scope.approveShow = true;
	//法律评审控制器
	$scope.approveLegalShow = true;
	//初始化数据
	$scope.initData = function(){
		$scope.initUpdate(objId);
		$scope.getProjectPreReviewGDCYByID(objId);
		$scope.initPage();
		$scope.queryAuditLogsByBusinessId(objId);
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
	
	//流程图相关
	$scope.initPage = function(){
		$scope.wfInfo.businessId = objId;
		$scope.refreshImg = Math.random()+1;
	}
	$scope.wfInfo = {processKey:'formalReview'};

	/*//处理附件列表
    $scope.reduceAttachment = function(attachment){
    	$scope.newAttachment = [];
    	for(var i in attachment){
    		var files = attachment[i].files;
    		if(files!=null && undefined!=files){
    			var item_name = attachment[i].ITEM_NAME;
    			var uuid = attachment[i].UUID;
    			for(var j in files){
    				files[j].ITEM_NAME=item_name;
    				files[j].UUID=uuid;
    				$scope.newAttachment.push(files[j]);
    			}
    		}
    		
    	}
    }*/
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
	
	
	$scope.initUpdate=function(id){
		
		var  url = 'formalAssessmentInfo/getFormalAssessmentByID.do';
		$http({
			method:'post',  
		    url:srvUrl+url, 
		    data: $.param({"id":id})
		}).success(function(data){
			$scope.pfr  = data.result_data.formalAssessmentMongo;
			$scope.pfrOracle  = data.result_data.formalAssessmentOracle;
			$scope.pfr.oracle  = data.result_data.formalAssessmentOracle

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
//                $scope.addFormalLegalComment();
            }
			if(undefined==$scope.pfr.approveAttachment){
				$scope.approveShow = false;
				$scope.pfr.approveAttachment = {};
			}
			//显示专业评审
			if(undefined==$scope.pfr.approveMajorCommonts){
				$scope.approveMajorShow = false;
			}
			
			$scope.fileName=[];
			var filenames=$scope.pfr.attachment;
			console.log(filenames);
			if (filenames != null && filenames != [] && filenames != undefined) {
                for(var i=0;i<filenames.length;i++){
                    var arr={UUID:filenames[i].UUID,ITEM_NAME:filenames[i].ITEM_NAME};
                    $scope.fileName.push(arr);
                }
			}
			if (null != $scope.pfr.apply.expectedContractDate) {
				$scope.changDate($scope.pfr.apply.expectedContractDate);
			}
			hide_Mask();
		});
	}
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
	$scope.getSelectTypeByCode2=function(typeCode){
		var  url = 'common/commonMethod/selectDataDictionByCode';
		$scope.httpData(url,typeCode).success(function(data){
			if(data.result_code === 'S'){
				$scope.optionTypeList2=data.result_data;
			}else{
				alert(data.result_name);
			}
		});
	}
	/*查询固定成员审批列表*/
	$scope.getProjectPreReviewGDCYByID=function(id){
		var  url = 'rcm/ProjectInfo/getTaskOpinion';
		$scope.panam={taskDefKey:'usertask4' ,businessId:id};
		$scope.httpData(url,$scope.panam).success(function(data){
			$scope.gdcy  = data.result_data;
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
					if(null!=$scope.pfr.cesuanFileOpinion && ''!=$scope.pfr.cesuanFileOpinion){
						$scope.yjdw = $scope.yjdw+" ；<br/>项目投资测算意见："+$scope.pfr.cesuanFileOpinion;
					}
					$scope.yjdw=Utils.encodeTextAreaString($scope.yjdw);
				}else{
					$scope.yjdw=null;
				}
				
				var tzcs = data.result_data;
				if (null != tzcs) {
					if(null!=$scope.pfr.tzProtocolOpinion && ''!=$scope.pfr.tzProtocolOpinion){
						$scope.xyyj ="项目投资协议意见：" + $scope.pfr.tzProtocolOpinion;
					}
					$scope.xyyj=Utils.encodeTextAreaString($scope.xyyj);
				}else{
					$scope.xyyj=null;
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
//	$scope.getNoticeOfDecstionByProjectFormalID=function(pid){
//		var url="formalAssessment/NoticeOfDecision/getNoticeOfDecstionByProjectFormalID";
//		$scope.httpData(url,pid).success(function(data){
//			if(data.result_code === 'S'){
//				if(undefined!=data.result_data) {
//					$scope.noticofDec=data.result_data;
//					var c = $scope.noticofDec.consentToInvestment;
//					if (c == "1") {
//						$scope.consentToInvestment = "同意投资";
//					} else if (c == "2") {
//						$scope.consentToInvestment = "不同意投资";
//					} else if (c == 3) {
//						$scope.consentToInvestment = "同意有条件投资";
//					}
//					$scope.implementationMatters = $scope.noticofDec.implementationMatters;
//					$scope.dateOfMeeting = $scope.noticofDec.dateOfMeeting;
//				}else{
//					$scope.consentToInvestment=null;
//					$scope.implementationMatters=null;
//				}
//			}else{
//				alert(data.result_name);
//			}
//		});
//	}

     // 验证项目是否在流程或者评审中
     $scope.vaildProject = function(){
         var type = "formalReview";
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
         var functionType = '正式评审';
         $http({
             method:'post',
             url: srvUrl + 'formalAssessmentInfoCreate/checkAttachment.do',
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

	//模态框
	$scope.showSubmitModal = function(){
		$scope.approve = {
			operateType: "submit",
			processKey: "formalAssessment",
			businessId: objId,
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
	};

	$scope.initData();

	angular.element(document).ready(function() {
		$scope.getSelectTypeByCode("06");
		$scope.getSelectTypeByCode2("09");
	});
	/////////////////
     //$scope._init_uuid_ = $scope.credentials.UUID;
     //$scope._init_messages_array_ = _init_query_messages_list_($routeParams.id);
     //var curTask = wf_getCurrentTask('formalReview', $routeParams.id);
     //$scope._message_publish_reply_ = !isEmpty(curTask) && curTask.TASK_DEF_KEY_ != 'usertask17';
}]);
/********
 * Created by wangjian on 16/4/11.
 * 通报项目 审核
 *********/
ctmApp.register.controller('BulletinMattersAudit', ['$http','$scope','$location','$routeParams',
function ($http,$scope,$location,$routeParams) {
	$scope.tabIndex = $routeParams.tabIndex;
	$scope.initDefaultData = function(){
		var url = srvUrl + "bulletinInfo/queryListDefaultInfo.do";
		$http({
			method:'post',  
		    url: url
		}).success(function(result){
			var data = result.result_data;
			$scope.tbsxType = data.tbsxType;
		});
	};
	$scope.initDefaultData();
	//查询起草状态列表
	$scope.queryApplyList=function(){
		$http({
			method:'post',  
		    url:srvUrl+"bulletinAudit/queryWaitList.do", 
		    data: $.param({"page":JSON.stringify($scope.paginationConf)})
		}).success(function(data){
			if(data.success){
				$scope.bulletins = data.result_data.list;
                $scope.paginationConf.totalItems = data.result_data.totalItems;
			}else{
				$.alert(data.result_name);
			}
		});
    };
    //查询已提交列表
    $scope.queryApplyedList=function(){
    	$http({
			method:'post',  
		    url:srvUrl+"bulletinAudit/queryAuditedList.do", 
		    data: $.param({"page":JSON.stringify($scope.paginationConfes)})
		}).success(function(data){
			if(data.success){
				$scope.applyedBulletins = data.result_data.list;
                $scope.paginationConfes.totalItems = data.result_data.totalItems;
			}else{
				$.alert(data.result_name);
			}
		});
    };
    $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage',  $scope.queryApplyList);
    $scope.$watch('paginationConfes.currentPage + paginationConfes.itemsPerPage',  $scope.queryApplyedList);
    
    $scope.update = function(){
    	var chkObjs = $("input[type=checkbox][name=applyChk]:checked");
    	if(chkObjs.length == 0 || chkObjs.length > 1){
    		$.alert("请选择一条数据编辑！");
    		return false;
    	}
    	var businessId = $(chkObjs[0]).val();
        $location.path("/BulletinMattersDetail/"+businessId);
    };
    
    $scope.deleteBatch = function(){
    	var chkObjs = $("input[type=checkbox][name=applyChk]:checked");
    	if(chkObjs.length == 0){
    		$.alert("请选择要删除的数据！");
    		return false;
    	}
    	var idsStr = "";
    	for(var i = 0; i < chkObjs.length; i++){
    		idsStr = idsStr + chkObjs[i].value + ",";
    	}
    	idsStr = idsStr.substring(0, idsStr.length - 1);
    	$http({
			method:'post',  
		    url:srvUrl+"bulletinInfo/deleteByIds.do", 
		    data: $.param({"ids": idsStr})
		}).success(function(data){
			if(data.success){
				$scope.queryApplyList();
			}
			$.alert(data.result_name);
		});
    }
}]);

ctmApp.register.controller('BulletinMattersAuditView', ['$http','$scope','$location', '$routeParams', '$filter','Upload',
	function ($http,$scope,$location, $routeParams, $filter,Upload) {
	var queryParamId = $routeParams.id;
	$scope.oldUrl = $routeParams.url;
	$scope.showController = {};
	//风控负责人
//	$scope.bulletin={"taskallocation":{"riskLeader":{},"reviewLeader":{},"legalLeader":{}}};
	$scope.myTaskallocation = {"riskLeader":null,"reviewLeader":null,"legalLeader":null};
	$scope.changeUserMapper={"nameField":"NAME","valueField":"VALUE"};
	$scope.checkedUser={};
	$scope.callback = function(){
		$("#userSinDialog").modal('hide');
		$("#submitModal").modal('show');
	}
	/*
	 * 根据业务id查询节点信息
	 */
	$scope.getTaskInfoByBusinessId = function(businessId,processKey){
		$scope.isTaskEdit = 'false';
		$scope.showController = {};
		
		$http({
			method:'post',  
		    url: srvUrl + "bulletinAudit/queryTaskInfoByBusinessId.do",
		    data:$.param({"businessId":businessId,"processKey":processKey})
		}).success(function(data){
//			$scope.taskId = data.result_data.taskId;
//			$scope.hasWaiting = data.result_data.taskId==null?false:true;
			
			var logs = $scope.auditLogs;
			for(var i in logs){
				if(logs[i].ISWAITING == '1'){
					if(logs[i].AUDITUSERID == $scope.credentials.UUID){
						$scope.showSubmit = true;
						$scope.curLog = logs[i];
					}
				}
			}
			
			
			$scope.taskInfo = data.result_data;
			if(data.result_data.description != null && data.result_data.description != ""){
				var document = JSON.parse(data.result_data.description);
				$scope.document = document;
				//任务分配（选法律和评审负责人）
				if (document.isTask != undefined && document.isTask!=null&&document.isTask!=""){
					$scope.showController.isTask = true;
					$scope.isTaskEdit = 'true'
				} 
				//任务分配风控（选法律或评审）
				else if (document.isRiskTask != undefined && document.isRiskTask != null && document.isRiskTask != ""){
					$scope.showController.isRiskTask = true;
					$scope.isTaskEdit = 'true'
				} 
				//法律评审负责人
				else if (document.isLegalLeader != undefined && document.isLegalLeader!=null && document.isLegalLeader != ""){
					$scope.showController.isLegalLeader = true;
				}
				//评审负责人
				else if (document.isReviewLeader != undefined && document.isReviewLeader != null && document.isReviewLeader != ""){
					$scope.showController.isReviewLeader = true;
				}
				//风控负责人
				else if (document.isRiskLeader != undefined && document.isRiskLeader != null && document.isRiskLeader != ""){
					$scope.showController.isRiskLeader = true;
				}
			}
		});
	}
	
	//保存方法
	$scope.save = function(callback){
		if($scope.showController.isLegalLeader){
			//保存法律负责任信息
			$http({
				method:'post',  
			    url: srvUrl + "bulletinInfo/saveLegalLeaderAttachment.do",
			    data:$.param({
			    	"businessId":queryParamId,
			    	"attachment":JSON.stringify($scope.bulletin.legalLeaderAttachment),
			    	"opinion":$scope.bulletin.legalLeaderOpinion
			    })
			}).success(function(data){
				if(data.success){
					if(callback){
						
						//验证
						if($scope.bulletin.legalLeaderOpinion == null || $scope.bulletin.legalLeaderOpinion == ""){
							$.alert("法律评审负责人意见不能为空！");
							return;
						}
						if($scope.bulletin.legalLeaderAttachment != null && $scope.bulletin.legalLeaderAttachment != ""){
							var legalLeaderAttachment = $scope.bulletin.legalLeaderAttachment;
							
							for(var i in legalLeaderAttachment){
								if(legalLeaderAttachment[i].fileName == null || legalLeaderAttachment[i].fileName == ''){
									$.alert("您有附件未上传，请上传附件！");
									return;
								}
							}
							callback();
						}
						if($scope.bulletin.legalLeaderAttachment == null || $scope.bulletin.legalLeaderAttachment == ""){
							$.confirm("您没有上传任何附件，提交后将不能再更改！<br/>是否确认提交！",function(){
								callback();
								return;
							});
						}
						
					}else{
						$.alert(data.result_name);
					}
				}else{
					$.alert(data.result_name);
				}
			});
		}else if($scope.showController.isRiskLeader){
			//保存风控负责人信息
			$http({
				method:'post',  
			    url: srvUrl + "bulletinInfo/saveRiskLeaderAttachment.do",
			    data:$.param({
			    	"businessId":queryParamId,
			    	"attachment":JSON.stringify($scope.bulletin.riskLeaderAttachment),
			    	"opinion":$scope.bulletin.riskLeaderOpinion
			    })
			}).success(function(data){
				if(data.success){
					if(callback){
						//验证
						if($scope.bulletin.riskLeaderOpinion == null || $scope.bulletin.riskLeaderOpinion == ""){
							$.alert("风控评审负责人意见不能为空！");
							return;
						}
						if($scope.bulletin.riskLeaderAttachment != null && $scope.bulletin.riskLeaderAttachment != ""){
							var riskLeaderAttachment = $scope.bulletin.riskLeaderAttachment;
							
							for(var i in riskLeaderAttachment){
								if(riskLeaderAttachment[i].fileName == null || riskLeaderAttachment[i].fileName == ''){
									$.alert("您有附件未上传，请上传附件！");
									return;
								}
							}
							callback();
						}
						if($scope.bulletin.riskLeaderAttachment == null || $scope.bulletin.riskLeaderAttachment == ""){
							$.confirm("您没有上传任何附件，提交后将不能再更改！<br/>是否确认提交！",function(){
								callback();
								return;
							});
						}
					}else{
						$.alert(data.result_name);
					}
				}else{
					$.alert(data.result_name);
				}
			});
			
		}else if($scope.showController.isReviewLeader){
			//保存评审负责人信息
			$http({
				method:'post',  
			    url: srvUrl + "bulletinInfo/saveReviewLeaderAttachment.do",
			    data:$.param({
			    	"businessId":queryParamId,
			    	"attachment":JSON.stringify($scope.bulletin.reviewLeaderAttachment),
			    	"opinion":$scope.bulletin.reviewLeaderOpinion
			    })
			}).success(function(data){
				if(data.success){
					if(callback){
						//验证
						if($scope.bulletin.reviewLeaderOpinion == null || $scope.bulletin.reviewLeaderOpinion == ""){
							$.alert("评审负责人意见不能为空！");
							return;
						}
						if($scope.bulletin.reviewLeaderAttachment != null && $scope.bulletin.reviewLeaderAttachment != ""){
							var reviewLeaderAttachment = $scope.bulletin.reviewLeaderAttachment;
							
							for(var i in reviewLeaderAttachment){
								if(reviewLeaderAttachment[i].fileName == null || reviewLeaderAttachment[i].fileName == ''){
									$.alert("您有附件未上传，请上传附件！");
									return;
								}
							}
							callback();
						}
						if($scope.bulletin.reviewLeaderAttachment == null || $scope.bulletin.reviewLeaderAttachment == ""){
							$.confirm("您没有上传任何附件，提交后将不能再更改！<br/>是否确认提交！",function(){
								callback();
								return;
							});
						}
					}else{
						$.alert(data.result_name);
					}
				}else{
					$.alert(data.result_name);
				}
			});
			
		}else if($scope.showController.isTask || $scope.showController.isRiskTask){
			if(callback){
				callback();
			}
		}else{
			if(callback){
				callback();
			}
		}
	}
	$scope.initDefaultData = function(){
		$scope.wfInfo = {processKey:'bulletin', "businessId":queryParamId};
		$scope.initData();
	};
	$scope.initData = function(){
		var url = srvUrl + "bulletinInfo/queryViewDefaultInfo.do";
		$http({
			method:'post',  
		    url: url,
		    data: $.param({"businessId": queryParamId})
		}).success(function(result){
			var data = result.result_data;
			$scope.bulletinOracle = data.bulletinOracle;
			$scope.bulletin = data.bulletinMongo;

			if(data.bulletinMongo.taskallocation !=null ){
				if(data.bulletinMongo.taskallocation.reviewLeader!=null){
					$scope.myTaskallocation.reviewLeader = data.bulletinMongo.taskallocation.reviewLeader;
				}
				if(data.bulletinMongo.taskallocation.legalLeader!=null){
					$scope.myTaskallocation.legalLeader = data.bulletinMongo.taskallocation.legalLeader;
				}
				if(data.bulletinMongo.taskallocation.riskLeader!=null){
					$scope.myTaskallocation.riskLeader = data.bulletinMongo.taskallocation.riskLeader;
				}
			}
			$scope.auditLogs = data.logs;
			$scope.initPage();
			$scope.getTaskInfoByBusinessId(queryParamId,"bulletin");
		});
	};
	$scope.initDefaultData();
	$scope.initPage = function(){
		$scope.wfInfo.businessId = queryParamId;
		$scope.refreshImg = Math.random()+1;
	}
	
	//附件---->新增列表---->评审负责人附件
    $scope.addReviewLeaderAttachment = function(){
        function addBlankRow(array){
            var blankRow = {
                file_content:''
            }
            var size=0;
            for(attr in array){
                size++;
            }
            array[size]=blankRow;
        }
        if(undefined==$scope.bulletin.reviewLeaderAttachment){
            $scope.bulletin.reviewLeaderAttachment=[];
        }
        addBlankRow($scope.bulletin.reviewLeaderAttachment);
    }
    //附件---->新增列表---->法律负责人附件
    $scope.addLegalLeaderAttachment = function(){
        function addBlankRow(array){
            var blankRow = {
                file_content:''
            }
            var size=0;
            for(attr in array){
                size++;
            }
            array[size]=blankRow;
        }
        if(undefined==$scope.bulletin.legalLeaderAttachment){
            $scope.bulletin.legalLeaderAttachment=[];
        }
        addBlankRow($scope.bulletin.legalLeaderAttachment);
    }
    //附件---->新增列表---->风控负责人附件
    $scope.addRiskLeaderAttachment = function(){
    	function addBlankRow(array){
    		var blankRow = {
    				file_content:''
    		}
    		var size=0;
    		for(attr in array){
    			size++;
    		}
    		array[size]=blankRow;
    	}
    	if(undefined==$scope.bulletin.riskLeaderAttachment){
    		$scope.bulletin.riskLeaderAttachment=[];
    	}
    	addBlankRow($scope.bulletin.riskLeaderAttachment);
    }
    
    //附件列表---->删除指定的列表---->评审负责人
    $scope.deleteReviewLeaderAttachment = function(){
        var commentsObj = $scope.bulletin.reviewLeaderAttachment;
        if(commentsObj!=null){
            for(var i=0;i<commentsObj.length;i++){
                if(commentsObj[i].selected){
                    commentsObj.splice(i,1);
                    i--;
                }
            }
        }
    }
    //附件列表---->删除指定的列表---->法律负责人
    $scope.deleteLegalLeaderAttachment = function(){
        var commentsObj = $scope.bulletin.legalLeaderAttachment;
        if(commentsObj!=null){
            for(var i=0;i<commentsObj.length;i++){
                if(commentsObj[i].selected){
                    commentsObj.splice(i,1);
                    i--;
                }
            }
        }
    }
    //附件列表---->删除指定的列表---->风控负责人
    $scope.deleteRiskLeaderAttachment = function(){
    	var commentsObj = $scope.bulletin.riskLeaderAttachment;
    	if(commentsObj!=null){
    		for(var i=0;i<commentsObj.length;i++){
    			if(commentsObj[i].selected){
    				commentsObj.splice(i,1);
    				i--;
    			}
    		}
    	}
    }
    
    //附件列表---->上传附件---->评审负责人
    $scope.errorAttach=[];
    $scope.uploadReviewLeaderAttachment = function (file,errorFile, idx) {
        if(errorFile && errorFile.length>0){
        	var errorMsg = fileErrorMsg(errorFile);
            $scope.errorAttach[idx]={msg:errorMsg};
        }else if(file){
        	
        	if(file.name){
       		 //检查压缩文件
       	    	var index = file.name.lastIndexOf('.');
       	    	var suffix  = file.name.substring(index+1);
       	    	if("rar" == suffix || "zip" == suffix || "7z" == suffix){
       	    	    $.alert("附件不能是压缩文件！");
       	    		return false;
       	    	}
           	}
        	
            var fileFolder = "bulletin/review/";
            var dates=$scope.bulletin.createTime;
            var no=$scope.bulletin.id;
            var strs= new Array(); //定义一数组
            strs=dates.split("-"); //字符分割
            dates=strs[0]+strs[1]; //分割后的字符输出
            fileFolder=fileFolder+dates+"/"+no;

            $scope.errorAttach[idx]={msg:''};
            Upload.upload({
                url:srvUrl+'file/uploadFile.do',
                data: {file: file, folder:fileFolder}
            }).then(function (resp) {
                var retData = resp.data.result_data[0];
                $scope.bulletin.reviewLeaderAttachment[idx]=retData;
            }, function (resp) {
                $.alert(resp.status);
            }, function (evt) {
                var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                $scope["progress"+idx]=progressPercentage == 100 ? "":progressPercentage+"%";
            });
        }
    };
    //附件列表---->上传附件---->法律负责人
    $scope.uploadLegalLeaderAttachment = function (file,errorFile, idx) {
        if(errorFile && errorFile.length>0){
        	var errorMsg = fileErrorMsg(errorFile);
            $scope.errorAttach[idx]={msg:errorMsg};
        }else if(file){
        	
        	if(file.name){
       		 //检查压缩文件
       	    	var index = file.name.lastIndexOf('.');
       	    	var suffix  = file.name.substring(index+1);
       	    	if("rar" == suffix || "zip" == suffix || "7z" == suffix){
       	    	    $.alert("附件不能是压缩文件！");
       	    		return false;
       	    	}
           	}
        	
        	
            var fileFolder = "bulletin/legal/";
            var dates=$scope.bulletin.createTime;
            var no=$scope.bulletin.id;
            var strs= new Array(); //定义一数组
            strs=dates.split("-"); //字符分割
            dates=strs[0]+strs[1]; //分割后的字符输出
            fileFolder=fileFolder+dates+"/"+no;

            $scope.errorAttach[idx]={msg:''};
            Upload.upload({
                url:srvUrl+'file/uploadFile.do',
                data: {file: file, folder:fileFolder}
            }).then(function (resp) {
                var retData = resp.data.result_data[0];
                $scope.bulletin.legalLeaderAttachment[idx]=retData;
            }, function (resp) {
                $.alert(resp.status);
            }, function (evt) {
                var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                $scope["progress"+idx]=progressPercentage == 100 ? "":progressPercentage+"%";
            });
        }
    };
    //附件列表---->上传附件---->风控负责人
    $scope.uploadRiskLeaderAttachment = function (file,errorFile, idx) {
    	if(errorFile && errorFile.length>0){
    		var errorMsg = fileErrorMsg(errorFile);
    		$scope.errorAttach[idx]={msg:errorMsg};
    	}else if(file){
    		
    		if(file.name){
       		 //检查压缩文件
       	    	var index = file.name.lastIndexOf('.');
       	    	var suffix  = file.name.substring(index+1);
       	    	if("rar" == suffix || "zip" == suffix || "7z" == suffix){
       	    	    $.alert("附件不能是压缩文件！");
       	    		return false;
       	    	}
           	}
    		
    		var fileFolder = "bulletin/risk/";
    		var dates=$scope.bulletin.createTime;
    		var no=$scope.bulletin.id;
    		var strs= new Array(); //定义一数组
    		strs=dates.split("-"); //字符分割
    		dates=strs[0]+strs[1]; //分割后的字符输出
    		fileFolder=fileFolder+dates+"/"+no;
    		
    		$scope.errorAttach[idx]={msg:''};
    		Upload.upload({
                url:srvUrl+'file/uploadFile.do',
                data: {file: file, folder:fileFolder}
    		}).then(function (resp) {
    			var retData = resp.data.result_data[0];
    			$scope.bulletin.riskLeaderAttachment[idx]=retData;
    		}, function (resp) {
    			$.alert(resp.status);
    		}, function (evt) {
    			var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
    			$scope["progress"+idx]=progressPercentage == 100 ? "":progressPercentage+"%";
    		});
    	}
    };
	//提交
	$scope.showSubmitModal = function(){
		$scope.save(function(){
			var url = srvUrl + "bulletinAudit/querySingleProcessOptions.do";
			$http({
	    		method:'post',  
			    url: url,
			    data: $.param({
			    	"businessId": queryParamId
			    })
	    	}).success(function(result){
	    		$scope.approve = {
	    			showController:$scope.showController,
					operateType: "audit",
//					taskId:$scope.taskId,
					processKey: "bulletin",
					processOptions: result.result_data,
					businessId: queryParamId,
					callbackSuccess: function(result){
						$.alert(result.result_name);
						$('#submitModal').modal('hide');
						$("#submitBtn").hide();
						$("#saveBtn").hide();
						$scope.initData();
					},
					callbackFail: function(result){
						$.alert(result.result_name);
					},
					document:$scope.document
				};
	    		/*if($scope.curLog.OLDUSERID != null && $scope.curLog.OLDUSERID != '' && $scope.curLog.OLDUSERID != $scope.credentials.UUID){
	    			$scope.approve.operateType="change";
	    		}*/
                if ($scope.curLog.CHANGETYPE) {
                    if ($scope.curLog.CHANGETYPE != '') {
                        $scope.approve.operateType = "change";
                    }
                }
				$('#submitModal').modal('show');
	    	});
		});
	};
	
	$scope.selectAll = function(){
		if($("#all").attr("checked")){
			$(":checkbox[name='choose']").attr("checked",1);
		}else{
			$(":checkbox[name='choose']").attr("checked",false);
		}
	}

}]);

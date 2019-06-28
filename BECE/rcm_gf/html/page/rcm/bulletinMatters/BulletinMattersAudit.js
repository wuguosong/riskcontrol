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

	$scope.isShowOld = false;

	$scope.businessId = $routeParams.id;
	$scope.queryParamId = $routeParams.id;
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
	$scope.curLog = wf_getTaskLog("bulletin", $routeParams.id, $scope.credentials.UUID);
	/*
	 * 根据业务id查询节点信息
	 */
	$scope.getTaskInfoByBusinessId = function(businessId,processKey){
		$scope.isTaskEdit = 'false';
        $scope.isLegalEdit = 'false';
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
                    $scope.isLegalEdit = 'true';
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
			    	"businessId":$scope.queryParamId,
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
			    	"businessId":$scope.queryParamId,
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
			    	"businessId":$scope.queryParamId,
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
	};
	$scope.initDefaultData = function(){
        /*if ($scope.oldUrl == $filter('encodeURI')('#/BulletinMattersAudit/0')){
            $scope.WF_STATE = '0';
        } else {
            $scope.WF_STATE = '1';
        }
        $scope.showSubmit = false;*/
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

            }
        };

	$scope.initUpdate = function(id){
		var url = srvUrl + "bulletinInfo/queryViewDefaultInfo.do";
		$http({
			method:'post',
		    url: url,
		    data: $.param({"businessId": id})
		}).success(function(result){
			var data = result.result_data;
			$scope.bulletinOracle = data.bulletinOracle;
			$scope.bulletin = data.bulletinMongo;
			if($scope.bulletinOracle.BULLETINTYPECODE != 'TBSX_BUSINESS_SUBCOMPANYTZ'){
                $scope.isNotCityService = true;
			}else{
                $scope.isNotCityService = false;
			}

            // 附件显示
            var time = $scope.bulletin.createTime.substring(0,10);
            var thisTime = '2019-6-3';
            time = time.replace(/-/g, '/');
            thisTime = thisTime.replace(/-/g, '/');
            if (new Date(time) < new Date(thisTime)){
                $scope.isShowOld = true;
            }

            // 处理附件
            $scope.reduceAttachment(data.bulletinMongo.attachmentList, id);

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
			$scope.getTaskInfoByBusinessId($scope.queryParamId,"bulletin");
            hide_Mask();
		});
	};
	$scope.initDefaultData();
	$scope.initPage = function(){
		$scope.wfInfo.businessId = $scope.queryParamId;
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
			    	"businessId": $scope.queryParamId
			    })
	    	}).success(function(result){
	    		$scope.approve = {
	    			showController:$scope.showController,
					operateType: "audit",
//					taskId:$scope.taskId,
					processKey: "bulletin",
					processOptions: result.result_data,
					businessId: $scope.queryParamId,
					callbackSuccess: function(result){
	    				debugger;
						$.alert(result.result_name);
						$('#submitModal').modal('hide');
						$("#submitBtn").hide();
						$("#saveBtn").hide();
                        $scope.initPage();
						$scope.showSubmit = false;

					},
					callbackFail: function(result){
						$.alert(result.result_name);
					},
					document:$scope.document
				};
	    		/*if($scope.curLog.OLDUSERID != null && $scope.curLog.OLDUSERID != '' && $scope.curLog.OLDUSERID != $scope.credentials.UUID){
	    			$scope.approve.operateType="change";
	    		}*/
                if (!isEmpty($scope.curLog)) {
                    // $scope.showSubmit = ($scope.WF_STATE == '0');
                    if (!isEmpty($scope.curLog.CHANGETYPE)) {
                        $scope.approve.operateType = "change";
                    }
                }
				// $('#submitModal').modal('show');
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
	/***************************业务流程方法开始***********************************/
    $scope.changeTypeSelected = "";
        $scope.changeTypes = [{key: 'before', value: '前加签'}, {key: 'after', value: '后加签'}];
        $scope.callfunction = function (functionName) {
            var func = eval(functionName);
            //创建函数对象，并调用
            return new func(arguments[1]);
        }

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
        var validCheckedFLFzr = function(){
            var result = {success: true, result_name: ""};
            if ($scope.myTaskallocation.legalLeader.NAME == null || $scope.myTaskallocation.legalLeader.NAME == "") {
                result.success = false;
                result.result_name = "请选择法律评审负责人！";
            }
            return result;
        };

        var validCheckedRiskFzr = function () {

            var result = {success: true, result_name: ""};

            if ($scope.myTaskallocation == null || $scope.myTaskallocation == "") {
                result.success = false;
                result.result_name = "请分配负责人！";
            }
            if ($scope.myTaskallocation.riskLeader.NAME == null || $scope.myTaskallocation.riskLeader.NAME == "") {
                result.success = false;
                result.result_name = "请选择风控负责人！";
            }
            return result;
        };
        $scope.showSelectPerson = function () {
            $("#submitModal").modal('hide');
            $("#userSinDialog").modal('show');
        }
        $scope.submitNext = function () {
			/*if ($("#workOver").attr("checked")) {*/
            if ($("input[name='bpmnProcessOption']:checked").val() == 'WORKOVER') {
                $scope.workOver();
            } /*else if ($("input[name='bpmnProcessOption']#change:checked").length > 0) {*/
            else if ($("input[name='bpmnProcessOption']:checked").val() == 'CHANGE') {
                var changeTypeSelected = $scope.changeTypeSelected;
                if (changeTypeSelected == null || changeTypeSelected == '' || changeTypeSelected == "") {
                    $.alert("请选择加签类型！");
                } else {
                    $scope.changeWork();
                }
            } else if ("submit" == $scope.approve.operateType) {
                $scope.submit();
            } else if ("audit" == $scope.approve.operateType) {
                $scope.auditSingle();
            } else {
                $.alert("操作状态不明确！");
            }
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
                    "business_module": "bulletin",
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
        }

        $scope.changeWork = function () {
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
           /* if ($scope.flowVariables.opinion.length < 20) {
                $.alert("审批意见不能少于20字！");
                return;
            }*/
            if ($scope.flowVariables.opinion.length > 650) {
                $.alert("审批意见不能超过650字！");
                return;
            }
            if ($scope.changeTypeSelected == 'after') {
                var validate = wf_validateSign('bulletin', $scope.approve.businessId);
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
                    'business_module': 'bulletin',
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


        }
        $scope.submit = function () {
            var url = srvUrl + "bulletinAudit/startSingleFlow.do";
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

            var url = srvUrl + "bulletinAudit/auditSingle.do";
            var documentation = $("input[name='bpmnProcessOption']:checked").attr("aaa");

            if (documentation != null && documentation != "") {
                var docObj = JSON.parse(documentation);
                if (docObj.preAction) {
                    var preActionArr = docObj.preAction;
                    for (var i in preActionArr) {
                        if (preActionArr[i].callback == 'validCheckedFzr' || preActionArr[i].callback == 'validCheckedFLFzr') {
                            var result = $scope.callfunction(preActionArr[i].callback);
                            if (!result.success) {
                                $.alert(result.result_name);
                                return;
                            } else {
                                //保存任务人员信息
                                $.ajax({
                                    type: 'post',
                                    url: srvUrl + "bulletinInfo/saveTaskPerson.do",
                                    data: $.param({
                                        "json": JSON.stringify(angular.copy($scope.myTaskallocation)),
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
                        } else if (preActionArr[i].callback == 'validCheckedRiskFzr') {
                            var result = $scope.callfunction(preActionArr[i].callback);
                            if (!result.success) {
                                $.alert(result.result_name);
                                return;
                            } else {
                                $.ajax({
                                    type: 'post',
                                    url: srvUrl + "bulletinInfo/saveTaskPerson.do",
                                    data: $.param({
                                        "json": JSON.stringify($scope.myTaskallocation),
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
        $scope.showSubmitModal();
        /***************************业务流程方法结束***********************************/
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
            _notifyUsersDelete('bulletin', $routeParams.id, _user);
            $scope._notifyInitNotifiesUser('bulletin', $routeParams.id);
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
            $scope._notifyInitNotifiesUser('bulletin', $routeParams.id);
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
        $scope._notifyInitNotifiesUser("bulletin", $routeParams.id);
        /***************************知会和加签*******************************/
        $scope._init_uuid_ = $scope.credentials.UUID;
        $scope._init_messages_array_ = _init_query_messages_list_($routeParams.id);
        ////////////审批阶段对留言编辑权限的控制
        //var curTask = wf_getCurrentTask('bulletin', $routeParams.id);
        //$scope._message_publish_reply_ = !isEmpty(curTask) ;//&& curTask.TASK_DEF_KEY_ != 'usertask7';
        //var curTask = wf_getCurrentProject('bulletin', $routeParams.id);
        //!isEmpty(curTask) && (curTask.AUDITSTATUS == 0 || curTask.AUDITSTATUS == 1);
        $scope._message_publish_reply_ = validateMessageOpenAuthority('bulletin', $routeParams.id);
        ////////////知会人弹窗显示节点设置
        $scope.showNotifyPopup = validateNotifyShowAuthority('bulletin', $routeParams.id);
        /******************************* 审批标签权限管理 *********************************/
        var curTask = wf_getCurrentTask('bulletin', $routeParams.id);
        if (isEmpty(curTask)) {
            $scope.isShowEdit = false;
        } else {
            $scope.isShowEdit = true;
        }
}]);

ctmApp.register.controller('NoticeDecisionAuditView',['$http','$scope','$location','$routeParams','Upload','$timeout', function ($http,$scope,$location,$routeParams,Upload,$timeout) {
    //初始化
	var businessId = $routeParams.id;
	$scope.old=businessId.indexOf("@")>0;
	if($scope.old){
		var params = $routeParams.id.split("@");
	    if(null!=params[4] && ""!=params[4]){
	        $scope.flag=params[4];
	        businessId=params[0];
	    }
	    $scope.isShowOldSubmitBtn = true;
	    if ($routeParams.action == "view") {
            $scope.isShowOldSubmitBtn = false;
        }
	}
    $scope.oldUrl = $routeParams.url;
    $scope.action =  $routeParams.action;
    var action = $routeParams.action;
    $scope.nod={};
    $scope.dic=[];
    
    $scope.save=function(showPopWin) {
    	$http({
			method:'post',  
			url: srvUrl + "noticeDecisionAudit/updateAttachment.do",
			data: $.param({
				"businessId": businessId,
				"attachment": JSON.stringify($scope.nod.attachment)
			})
		}).success(function(result){
            if(typeof(showPopWin)=='function'){
                showPopWin();
            }
		});
    }
    
    $scope.initData = function(){
    	$scope.getNoticeByID(businessId);
    	$scope.getNoticeAuditLogsByID(businessId);
    	$scope.initPage();
    }
    
	
    //获取流程日志
    $scope.getNoticeAuditLogsByID=function(businessId){
    	var  url = 'noticeDecisionAudit/queryAuditedLogsById.do';
        $http({
			method:'post',  
		    url: srvUrl + url,
		    data: $.param({"businessId":businessId})
		}).success(function(result){
			$scope.auditLogs = result.result_data;
		});
    }
    
    //获取决策通知书详细信息
    $scope.getNoticeByID=function(id){
        var  url = 'noticeDecisionInfo/getNoticeDecstionByID.do';
        $http({
			method:'post',  
		    url: srvUrl + url,
		    data: $.param({"id":id})
		}).success(function(data){
                $scope.nod = data.result_data;
                var haderNameArr = [], haderValueArr = [];
                if (null != $scope.nod.personLiable) {
                    var header = $scope.nod.personLiable;
                    if (null != header && header.length > 0) {
                        for (var i = 0; i < header.length; i++) {
                            haderNameArr.push(header[i].name);
                            haderValueArr.push(header[i].value);
                        }
                            $scope.nod.personLiableName = haderNameArr.join(",");
                    }
                }
		});
    }
    $scope.errorAttach=[];
    $scope.upload = function (file,errorFile, idx) {
        if(errorFile && errorFile.length>0){
        	var errorMsg = fileErrorMsg(errorFile);
        	$scope.errorAttach[idx]={msg:errorMsg};
        }else if(file){
            var fileFolder = "noticeOfDecision/";
                var dates=$scope.nod.create_date;
                var strs= new Array(); //定义一数组
                strs=dates.split("-"); //字符分割
                dates=strs[0]+strs[1]; //分割后的字符输出
                fileFolder=fileFolder+dates;
            $scope.errorAttach[idx]={msg:''};
            Upload.upload({
                url:srvUrl+'common/RcmFile/upload',
                data: {file: file, folder:fileFolder}
            }).then(function (resp) {
                var retData = resp.data.result_data[0];
                $scope.nod.attachment= retData;
            }, function (resp) {
                console.log('Error status: ' + resp.status);
            }, function (evt) {
                var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                $scope["progress"+idx]=progressPercentage == 100 ? "":progressPercentage+"%";
            });
        }
    };
    $scope.initPage = function(){
    	if($scope.old){
    		 if(typeof (params[1])=='string' && typeof (params[2])=='string' && params[1]!='' && params[2]!=''){//已经启动流程
    		        $scope.wfInfo = {processDefinitionId:params[1],processInstanceId:params[2]};
    		    }else{//未启动流程
    		        $scope.wfInfo = {processKey:'noticeOfDecision'};
    		    }
    	}else{
    		$scope.wfInfo={};
    		$scope.wfInfo = {processKey:'noticeDecision'};
    		$scope.wfInfo.businessId = $routeParams.id;
    		$scope.refreshImg = Math.random()+1;
    	}
		
	};
    

     //弹出审批框
	//提交(新)
	$scope.showSubmitModalNew = function(){
		var url = srvUrl + "noticeDecisionAudit/querySingleProcessOptions.do";
		$http({
    		method:'post',  
		    url: url,
		    data: $.param({
		    	"businessId": businessId
		    })
    	}).success(function(result){
    		$scope.approve = {
				operateType: "audit",
				processKey: "noticeDecision",
				processOptions: result.result_data,
				businessId: businessId,
				callbackSuccess: function(result){
					$.alert(result.result_name);
					$('#submitModal').modal('hide');
					$("#submitBtn").hide();
					$scope.initData();
				},
				callbackFail: function(result){
					$.alert(result.result_name);
					$('#submitModal').modal('hide');
				}
			};
    		$('#submitModal').modal('show');
    	});
	};
	
	//弹出审批框
    //提交(旧)
    $scope.showSubmitModalOld = function(){
        var reject ={
            submitInfo:{
                taskId:params[3],
                runtimeVar:{inputUser:$scope.nod.createBy.value, toTask:'-1'},
                currentTaskVar:{opinion:'请修改'}
            },
            showInfo:{destination:'退回起草人',approver: $scope.nod.createBy.name}
        };
        if(action =='Create'|| action =='Update'){
        	var url = srvUrl + "role/queryRoleuserByCode.do";
        	$http({
        		mothod:"post",
        		url:url,
        		params: {
        			"code":"5"
        		}
        	}).success(function(data){
                    if(data.success == true) {
                        var ma = data.result_data[0];
                        $scope.approve = [{
                            submitInfo:{
                                startVar:{processKey:'noticeOfDecision',businessId:$scope.nod._id,subject:$scope.nod.projectName+'决策通知书申请',inputUser:$scope.credentials.UUID},
                                runtimeVar:{inputUser:ma.VALUE},
                                currentTaskVar:{opinion:'请审批'}
                            },
                            showInfo:{destination:'风控分管领导审批',approver: ma.NAME}
                        }];
                        if($routeParams.action =='Update' && params[3]){
                            $scope.approve[0].submitInfo.taskId=params[3];
                        }
                        $('#submitModalOld').modal('show');
                    }
                });
        }else if(action == 'view1'){
        	$scope.queryOpenMeetingPerson($scope.nod.projectFormalId);
        	if($scope.openMeetingPerson == null){
        		return;
        	}
        	$scope.approve = [{
        		 submitInfo: {
	                taskId:params[3],
	                runtimeVar:{inputUser:$scope.openMeetingPerson.value, toTask:'1'},
	                currentTaskVar:{opinion:'请修改'}
        		 },
                 showInfo:{destination:'总裁办审批',approver: $scope.openMeetingPerson.name}
        	},reject];
        	$('#submitModalOld').modal('show');
        }else if(action == 'Edit'){
        	var url = srvUrl + "role/queryRoleuserByCode.do";
    		$http({
        		method:'post',  
    		    url: url,
    		    params: {
    		    	"code": "9"
    		    }
        	}).success(function(data){
                    if(data.success == true) {
                        var ma = data.result_data[0];
                        $scope.approve = [{
                            submitInfo:{
                                taskId:params[3],
                                runtimeVar:{inputUser:ma.VALUE, toTask:'1'},
                                currentTaskVar:{opinion:'请审批'}
                            },
                            showInfo:{destination:'领导签发',approver: ma.NAME}
                        }];
                        $scope.approve.push(reject);
                        $('#submitModalOld').modal('show');
                    }
                });
        }else if(action=='view2'){
        	$scope.queryOpenMeetingPerson($scope.nod.projectFormalId);
        	if($scope.openMeetingPerson == null){
        		return;
        	}
        	$scope.approve = [{
	       		 submitInfo: {
		                taskId:params[3],
		                runtimeVar:{inputUser:$scope.openMeetingPerson.value, toTask:'1'},
		                currentTaskVar:{opinion:'请审批'}
	       		 },
	                showInfo:{destination:'上传决策通知书',approver: $scope.openMeetingPerson.name}
	       	 },reject];
	       	$('#submitModalOld').modal('show');
        }else if(action=='view3'){
        	$scope.save(function() {
                $scope.approve = [{
                    toNodeType: 'end',
                    submitInfo: {
                        taskId: params[3]
                    }
                }];
                $('#submitModalOld').modal('show');
            });
        }
    };

	   $scope.queryOpenMeetingPerson = function(businessId){
	    	var url=srvUrl+"projectPreReview/Meeting/queryByBusinessId";
	        $.ajax({
	        	url: url,
	        	type: "POST",
	        	data: businessId,
	        	dataType: "json",
	        	async: false,
	        	success: function(data){
	        		if(data.success){
	        			if(data.result_data!=null && data.result_data.openMeetingPerson!=null){
	            			$scope.openMeetingPerson = data.result_data.openMeetingPerson;
	            		}else{
	            			 $.alert('上会人员不确定！');
	            		}
	        		}else{
	        			$.alert(data.result_name);
	        		}
	        	}
	        });
	    }
	   
	 //生成word文档
    $scope.createWord = function(){
    	startLoading();
    	var  url = 'noticeDecisionDraftInfo/getNoticeOfDecisionWord.do';
        $http({
        	method:'post',  
		    url: srvUrl + url,
		    data: $.param({"formalId":$scope.nod.projectFormalId})
        }).success(function(data){
        	if(data.success){
        		var filesPath=data.result_data.filePath;
                var filesName=data.result_data.fileName;
                window.location.href = srvUrl+"file/downloadFile.do?filepaths="+encodeURI(encodeURI(filesPath))+"&filenames="+encodeURI(encodeURI(filesName));
        	}else{
        		 $.alert("文档生成失败，请查看必填项是否填写完毕；如果全部正常填写请联系管理员！");
        	}
        })
        endLoading();
    };
	
	$scope.initData();
}]);

	
	
function delSelect(o,paramsVal,name,id){
    $(o).parent().remove();
    accessScope("#"+paramsVal+"Name", function(scope){
            var names=scope.nod.personLiable;
            var retArray = [];
            for(var i=0;i<names.length;i++){
                if(id !== names[i].value){
                    retArray.push(names[i]);
                }
            }
            if(retArray.length>0){
                scope.nod.personLiable=retArray;
            }else{
                scope.nod.personLiable={name:'',value:''};
                $("#personLiableName").val("");
            }
    });
}

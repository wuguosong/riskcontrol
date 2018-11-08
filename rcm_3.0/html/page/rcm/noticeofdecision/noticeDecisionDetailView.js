ctmApp.register.controller('NoticeDecisionDetailView',['$http','$scope','$location','$routeParams','Upload','$timeout', function ($http,$scope,$location,$routeParams,Upload,$timeout) {
    //初始化
    var businessId = $routeParams.id;
    $scope.oldUrl = $routeParams.url;
    $scope.action =  $routeParams.action;
    $scope.nod={};
    $scope.dic=[];
    
    $scope.initData = function(){
    	$scope.getNoticeDecstionByID(businessId);
    	$scope.getNoticeAuditLogsByID(businessId);
    	
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
    //查询决策通知书详情信息
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
                        commonModelValue("personLiable", haderValueArr, haderNameArr);
                            $scope.nod.personLiableName = haderNameArr.join(",");
                    }
                }
                
                $scope.initPage()
		});
        
        
    }

    $scope.setDirectiveParam=function(columnName){
        $scope.columnName=columnName;
    }
    $scope.setDirectiveOrgList=function(id,name){
        var params=$scope.columnName;
        if(params=="subjectOfImplementation"){
            $scope.nod.subjectOfImplementation = {"name":name,value:id};
            $("#subjectOfImplementationName").val(name);
            $("label[for='subjectOfImplementationName']").remove();
        }
        if(params=="responsibilityUnit"){
            $scope.nod.responsibilityUnit = {"name":name,value:id};
            $("#responsibilityUnitName").val(name);
            $("label[for='responsibilityUnitName']").remove();
        }
    }
    var commonModelValue=function(paramsVal,arrID,arrName){
        var leftstr2="<li class=\"select2-search-choice\"><div>";
        var centerstr2="</div><a class=\"select2-search-choice-close\" tabindex=\"-1\" onclick=\"delSelect(this,'";
        var addID2="');\"  ></a><div class=\"full-drop\"><input type=\"hidden\" id=\"\"  value=\"";
        var rightstr2="\"></div></li>";
        for(var i=0;i<arrName.length;i++){
            $("#header"+paramsVal).find(".select2-search-field").before(leftstr2+arrName[i]+centerstr2+paramsVal+"','"+arrName[i]+","+arrID[i]+addID2+arrID[i]+rightstr2);
        }
    }
    //模式窗口需要设置到父窗口调用该方法
    $scope.setDirectiveUserList=function(arrID,arrName,arrUserNamesValue){
           $("#headerpersonLiable").find(".select2-choices .select2-search-choice").remove();
            $scope.nod.personLiable =arrUserNamesValue;  //赋值保存到数据库
            $("#personLiableName").val(arrUserNamesValue);
            $("label[for='personLiableName']").remove();
            commonModelValue("personLiable",arrID,arrName);
    }
    
    $scope.initPage = function(){
    	
    	if($scope.nod.oracle.WF_STATE!='0'){
    		$scope.wfInfo.businessId = businessId;
    		$scope.refreshImg = Math.random()+1;
    	}
    	
	}
        $scope.initData();
        $('#content-wrapper input').attr("disabled",true);
        $('.a').attr("onclick","");
        $('textarea').attr("readonly",true);
        $('select').attr("disabled",true);
        $("#savebtn").hide();
        $scope.titleName = "决策通知书信息及审批意见汇总";
    $scope.wfInfo = {processKey:'noticeDecision'};
    //弹出审批框
    //提交(新)
	$scope.showSubmitModalNew = function(){
		$scope.approve = {
			operateType: "submit",
			processKey: "noticeDecision",
			businessId: businessId,
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
	
    /*查询固定成员审批列表*/
    $scope.getNoticeOfDecisionLeader=function(id){
        var  url = 'rcm/ProjectInfo/getTaskOpinion';
        $scope.panam={taskDefKey:'usertask4' ,businessId:id};
        $scope.httpData(url,$scope.panam).success(function(data){
            if (data.result_code === 'S') {
                $scope.noticeLeader = data.result_data;
            }
        });
    }
    
    //生成word文档
    $scope.createWord=function(){
        startLoading();
        var url = 'formalAssessment/NoticeOfDecision/getNoticeOfDecisionWord';
        $scope.httpData(url, businessId).success(function (data) {
            if (data.result_code=="S") {
                var filesPath=data.result_data.filePath;
                var filesName=data.result_data.fileName;
                window.location.href = srvUrl+"common/RcmFile/downLoad?filePath="+encodeURI(filesPath)+"&fileName="+encodeURI(filesName);
            } else {
                $.alert("报表生成失败，请查看必填项是否填写完毕；如果全部正常填写请联系管理员！");
            }
        });
        endLoading();
    }
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

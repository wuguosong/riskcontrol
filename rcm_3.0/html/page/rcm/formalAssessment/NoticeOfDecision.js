ctmApp.register.controller('NoticeOfDecision',['$http','$scope','$location','$routeParams','Upload','$timeout', function ($http,$scope,$location,$routeParams,Upload,$timeout) {
    //初始化
    var params = $routeParams.id.split("@");
    if(null!=params[4] && ""!=params[4]){
        $scope.flag=params[4];
    }
    var objId = params[0];
    var action = $routeParams.action;
    $scope.action = action;
    $scope.actionPam = $routeParams.action;
    $scope.nod={};
    $scope.dic=[];
    $scope.save=function(showPopWin) {
        if ($("#noticeName").valid()) {
            var postObj;
            var url;
            if (typeof ($scope.nod._id) != "undefined") {
                url =  'formalAssessment/NoticeOfDecision/update';
            } else {
                url =  'formalAssessment/NoticeOfDecision/create';
            }
            postObj = $scope.httpData(url, $scope.nod);
            postObj.success(function (data) {
                if (data.result_code === 'S') {
                    //$location.path("/NoticeOfDecisionList");
                    $scope.nod._id = data.result_data;
                    if(typeof(showPopWin)=='function'){
                        showPopWin();
                    }else{
                        $.alert('保存成功');
                    }
                } else {
                    $.alert(data.result_name);
                }
            })
        }
    }
    $scope.getProjectFormalReviewByID=function(id){
        var  url = 'formalAssessment/ProjectFormalReview/getProjectFormalReviewByID';
        $scope.httpData(url,id).success(function(data){
            if (data.result_code === 'S') {
                $scope.pfr = data.result_data;
                $scope.nod.projectFormalId = $scope.pfr._id;
                $scope.nod.projectName = $scope.pfr.apply.projectName;
                $scope.nod.reportingUnit = $scope.pfr.apply.reportingUnit.name;
                $scope.nod.projectNo = $scope.pfr.apply.projectNo;
                $scope.nod.controllerVal="NoticeOfDecision";
            }
        });

    }
    $scope.getNoticeByID=function(id){
        var  url = 'formalAssessment/NoticeOfDecision/getNoticeOfDecstionByID';
        $scope.httpData(url,id).success(function(data){
            if (data.result_code === 'S') {
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
                        if ($routeParams.action.indexOf("view")!=-1) {
                            $scope.nod.personLiableName = haderNameArr.join(",");
                        }
                    }
                }
                if ($scope.actionPam == "view") {
                    $("#submibtnn").hide();
                }
            }
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
    if(action=="Create"){
        $scope.flag="4";
        $scope.getProjectFormalReviewByID(objId);
        $scope.nod.createBy = {name:$scope.credentials.userName, value:$scope.credentials.UUID};
        $scope.titleName = "决策通知书新增";
    }else if(action=="Update" || action=="Edit"){
        if(action=="Update"){
            $scope.flag="4";
        }
        $scope.getNoticeByID(objId);
        $scope.titleName = "决策通知书修改";
    }else if(action.indexOf("view")!=-1){
        $scope.getNoticeByID(objId);
        $('#content-wrapper input').attr("disabled",true);
        $('.a').attr("onclick","");
        $('textarea').attr("readonly",true);
        $('select').attr("disabled",true);
        $("#savebtn").hide();
        $scope.titleName = "决策通知书信息及审批意见汇总";
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
                $scope.nod.attachment=[retData];
            }, function (resp) {
                console.log('Error status: ' + resp.status);
            }, function (evt) {
                var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                $scope["progress"+idx]=progressPercentage == 100 ? "":progressPercentage+"%";
            });
        }
    };
//    $scope.downLoadFile = function(idx){
//        var filePath = $scope.formalReport.attachment[0].filePath, fileName = $scope.formalReport.attachment[0].fileName;
//        window.location.href = srvUrl+"common/RcmFile/downLoad?filePath="+filePath+"&fileName="+encodeURI(fileName);
//    }
    //弹出审批框
    $scope.showSubmitModal = function(){
        var reject ={
            submitInfo:{
                taskId:params[3],
                runtimeVar:{inputUser:$scope.nod.createBy.value, toTask:'-1'},
                currentTaskVar:{opinion:'请修改'}
            },
            showInfo:{destination:'退回起草人',approver: $scope.nod.createBy.name}
        };
        if(action =='Create'|| action =='Update'){
            $scope.save(function(){
                $scope.httpData('common/commonMethod/getRoleuserByCode','5').success(function(data){
                    if(data.result_code == 'S') {
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
                        $('#submitModal').modal('show');
                    }
                });
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
        	$('#submitModal').modal('show');
        }else if(action == 'Edit'){
            $scope.save(function(){
                $scope.httpData('common/commonMethod/getRoleuserByCode','9').success(function(data){
                    if(data.result_code == 'S') {
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
                        $('#submitModal').modal('show');
                    }
                });
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
	       	$('#submitModal').modal('show');
        }else if(action=='view3'){
        	$scope.save(function() {
                $scope.approve = [{
                    toNodeType: 'end',
                    submitInfo: {
                        taskId: params[3]
                    }
                }];
                $('#submitModal').modal('show');
            });
        }
    };
    if(typeof (params[1])=='string' && typeof (params[2])=='string' && params[1]!='' && params[2]!=''){//已经启动流程
        $scope.wfInfo = {processDefinitionId:params[1],processInstanceId:params[2]};
    }else{//未启动流程
        $scope.wfInfo = {processKey:'noticeOfDecision'};
    }
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
    if(action!="Create") {
        $scope.getNoticeOfDecisionLeader(objId);
    }
    
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
    $scope.createWord=function(){
        startLoading();
        var url = 'formalAssessment/NoticeOfDecision/getNoticeOfDecisionWord';
        $scope.httpData(url, objId).success(function (data) {
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

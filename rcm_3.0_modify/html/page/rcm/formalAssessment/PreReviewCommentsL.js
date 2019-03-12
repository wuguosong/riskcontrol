ctmApp.register.controller('PreReviewCommentsL', ['$http','$scope','$location','$routeParams','Upload','$timeout', function ($http,$scope,$location,$routeParams,Upload,$timeout) {
        //初始化
    var complexId = $routeParams.id;
    var params = complexId.split("@");
    if(null!=params[4] && ""!=params[4]){
        $scope.flag=params[4];
    }
    var objId = params[0];
    $scope.isdisabled=false;
    $scope.taskID=params[3];
        $scope.pfr={};
        $scope.pfr.taskallocation={};
        $scope.pfr.apply = {};
        $scope.dic=[];
        $scope.pfr.approveAttachment = {};
        $scope.pfr.approveLegalAttachment = {};

        $scope.addFormalLegalComment = function(){
            function addBlankRow(array){
                var blankRow = {
                    opinionType:'',
                    commentConent:'',
                    commentDepartment:'',
                    commentFeedback:'',
                    commentDate:'',
                    taskId:$scope.taskID
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

        $scope.addFormalLegalAttachment = function(){
            function addBlankRow(array){
                var blankRow = {
                    attachment_new:'',
                    taskId:$scope.taskID
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

        $scope.saveFormalLegarReviewComtents=function(openPopWin){
            var url = 'formalAssessment/ProjectFormalReview/saveProjectFormalLegarComentsByID';
            var riskWarning = $scope.pfr.approveLegalAttachment.riskWarning;
            if ((riskWarning == null || riskWarning == "" )) {
                $.alert("请填写初步评审意见");
                return false;
            }
            if(typeof(openPopWin)=='function') {
                $scope.httpData(url, $scope.pfr).success(function (data) {
                    if (data.result_code === 'S') {
                        if (typeof(openPopWin) == 'function') {
                            openPopWin();
                        } else {
                            $.alert("保存成功");
                        }
                    }
                });
            }else{
                $scope.httpData(url, $scope.pfr).success(function (data) {
                    if (data.result_code === 'S') {
                        $.alert("保存成功");
                    }
                });
            }
        }
        $scope.getProjectPreReviewByID=function(id){
            var  url = 'formalAssessment/ProjectFormalReview/getProjectFormalReviewByID';
            $scope.httpData(url,id).success(function(data){
                var ptNameArr=[],pmNameArr=[],fgNameArr=[],pthNameArr=[];
                $scope.pfr  = data.result_data;
                /*var pth=$scope.pfr.apply.companyHeader;
                if(null!=pth && pth.length>0){
                    for(var i=0;i<pth.length;i++){
                        pthNameArr.push(pth[i].name);
                    }
                    $scope.pfr.apply.companyHeaderName=pthNameArr.join(",");
                }*/
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
                var fg=$scope.pfr.taskallocation.fixedGroup;
                if(null!=fg && fg.length>0){
                    for(var k=0;k<fg.length;k++){
                        fgNameArr.push(fg[k].NAME);
                    }
                    $scope.pfr.taskallocation.fixedGroup=fgNameArr.join(",");
                }

                $scope.fileName=[];
                var filenames=$scope.pfr.attachment;
                for(var i=0;i<filenames.length;i++){
                    var arr={UUID:filenames[i].UUID,ITEM_NAME:filenames[i].ITEM_NAME};
                    $scope.fileName.push(arr);
                }
                if(undefined==$scope.pfr.approveLegalAttachment){
                    $scope.pfr.approveLegalAttachment = {};
                    $scope.addFormalLegalComment();
                    $scope.addFormalLegalAttachment();
                }
                $scope.getNoticeOfDecstionByProjectFormalID($scope.pfr.apply.projectNo);
                $scope.getProjectPreReviewYJDWByID(objId);
                // $("select[id^='attachmentNew']").attr("disabled","disabled");
            });

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
    $scope.getSelectTypeByCodeL=function(typeCode){
        var  url = 'common/commonMethod/selectDataDictionByCode';
        $scope.httpData(url,typeCode).success(function(data){
            if(data.result_code === 'S'){

                $scope.optionTypeListL=data.result_data;
            }else{
                alert(data.result_name);
            }
        });
    }
        $scope.getProjectPreReviewByID(objId);
    //根据id查询决策通知书决策意见
    $scope.getNoticeOfDecstionByProjectFormalID=function(pid){
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
                    } else if (c == 3) {
                        $scope.consentToInvestment = "同意有条件投资";
                    }
                    $scope.implementationMatters = $scope.noticofDec.implementationMatters;
                    $scope.dateOfMeeting = $scope.noticofDec.dateOfMeeting;
                }else{
                    $scope.consentToInvestment=null;
                    $scope.implementationMatters=null;
                }
            }else{
                alert(data.result_name);
            }
        });
    }
        angular.element(document).ready(function() {
            $scope.getSelectTypeByCodeL("09");
            $scope.getSelectTypeByCode("06");
        });
        $scope.downLoadFile = function(df){
            window.location.href = srvUrl+"common/RcmFile/downLoad?filePath="+df.filePath+"&fileName="+encodeURI(df.fileName);
        }
    /*查询固定成员审批列表*/
    $scope.getProjectPreReviewGDCYByID=function(id){
        var  url = 'rcm/ProjectInfo/getTaskOpinion';
        $scope.panam={taskDefKey:'usertask4' ,businessId:id};
        $scope.httpData(url,$scope.panam).success(function(data){
            $scope.gdcy  = data.result_data;

        });
    }
    $scope.getProjectPreReviewGDCYByID(objId);
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
                    if(null!=$scope.pfr.tzProtocolOpinion && ''!=$scope.pfr.tzProtocolOpinion){
                        $scope.yjdw = $scope.yjdw+" ；<br/>项目投资协议意见：" + $scope.pfr.tzProtocolOpinion;
                    }
                    $scope.yjdw=Utils.encodeTextAreaString($scope.yjdw);
                }else{
                    $scope.yjdw=null;
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

    //附件上传
    $scope.getnum=function(name) {
        var numbers= 0;
        var attachmentList = $scope.pfr.attachment;
        for (var ii = 0; ii < attachmentList.length; ii++) {
            if(attachmentList[ii].ITEM_NAME==name.ITEM_NAME){
                if(undefined!=attachmentList[ii].files && null!=attachmentList[ii].files && ""!=attachmentList[ii].files){
                    numbers= attachmentList[ii].files.length;
                }else if( null==attachmentList[ii].files || ""==attachmentList[ii].files){
                    numbers= 0;
                }
            }
        }
        if(undefined!=$scope.pfr.approveAttachment) {
            var attachment_newList = $scope.pfr.approveAttachment.attachmentNew;
            for (var j = 0; j < attachment_newList.length; j++) {
                if (null != attachment_newList[j].attachmentUList && "" != attachment_newList[j].attachmentUList) {
                    if (attachment_newList[j].attachmentUList.ITEM_NAME == name.ITEM_NAME) {
                        if (undefined != attachment_newList[j].attachment_new && null != attachment_newList[j].attachment_new && "" != attachment_newList[j].attachment_new) {
                            var s = attachment_newList[j].attachment_new.version;
                            if (undefined == s || s > numbers) {
                                numbers = numbers * 1 + 1;
                            }
                        } else if (null == attachment_newList[j].attachment_new || "" == attachment_newList[j].attachment_new) {
                            if (numbers != "0") {
                                //  numbers = numbers * 1 + 1;
                            }
                        }
                    }
                }
            }
        }
        if(undefined!=$scope.pfr.approveLegalAttachment) {
            var attachment_newListL = $scope.pfr.approveLegalAttachment.attachmentNew;
            for (var k = 0; k < attachment_newListL.length; k++) {
                if (null!=attachment_newListL[k].attachmentUList && ""!=attachment_newListL[k].attachmentUList) {
                    if (attachment_newListL[k].attachmentUList.ITEM_NAME == name.ITEM_NAME) {
                        if (undefined != attachment_newListL[k].attachment_new && null != attachment_newListL[k].attachment_new && "" != attachment_newListL[k].attachment_new) {
                            var s = attachment_newListL[k].attachment_new.version;
                            if (undefined == s || s > numbers) {
                                numbers = numbers * 1 + 1;
                            }
                        } else if (null == attachment_newListL[k].attachment_new || "" == attachment_newListL[k].attachment_new) {
                            if (numbers != "0") {
                                // numbers = numbers * 1 + 1;
                            }
                        }
                    }
                }
            }
        }
        if(numbers!=0){
            numbers = numbers * 1;
        }
        return numbers;
    }
    $scope.errorAttach=[];
    $scope.upload = function (file,errorFile, idx,name,versionNun) {
        if(errorFile && errorFile.length>0){
        	var errorMsg = fileErrorMsg(errorFile);
            $scope.errorAttach[idx]={msg:errorMsg};
        }else if(file){
            var fileFolder = "pfrAssessment/";
            var dates=$scope.pfr.create_date;
            var no=$scope.pfr.apply.projectNo;
            var strs= new Array(); //定义一数组
            strs=dates.split("-"); //字符分割
            dates=strs[0]+strs[1]; //分割后的字符输出
            fileFolder=fileFolder+dates+"/"+no;

            $scope.errorAttach[idx]={msg:''};
            Upload.upload({
                url:srvUrl+'common/RcmFile/upload',
                data: {file: file, folder:fileFolder}
            }).then(function (resp) {
                var retData = resp.data.result_data[0];
                if(undefined==versionNun){
                    var numm= $scope.getnum(name);
                    if(numm>=1){
                        retData.version = (numm*1+1);
                    }else{
                        retData.version = "1";
                    }
                }else{
                    retData.version = versionNun;
                }
                var myDate = new Date();
                var paddNum = function(num){
                    num += "";
                    return num.replace(/^(\d)$/,"0$1");
                }
                retData.upload_date=myDate.getFullYear()+"-"+paddNum(myDate.getMonth()+1)+"-"+ myDate.getDate()+" "+myDate.getHours()+":"+myDate.getMinutes()+":"+myDate.getSeconds();
                retData.programmed={name:$scope.credentials.userName,value:$scope.credentials.UUID};
                retData.approved={name:$scope.credentials.userName,value:$scope.credentials.UUID};
                $scope.pfr.approveLegalAttachment.attachmentNew[idx].attachment_new=retData;
            }, function (resp) {
                console.log('Error status: ' + resp.status);
            }, function (evt) {
                var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                $scope["progress"+idx]=progressPercentage == 100 ? "":progressPercentage+"%";
            });
        }
    };
    if($routeParams.action.indexOf('edit')!=-1){
        $scope.isLawerReview = true;
        $scope.titleName = "填写法律初步评审意见";
    }else{
        $scope.isdisabled=true;
        $scope.titleName = "法律初步评审意见反馈";
    }
    //提交流程
    $scope.showSubmitModal=function(){
        $scope.saveFormalLegarReviewComtents(function(){
            var grassrootsLegalStaff = $scope.pfr.apply.grassrootsLegalStaff;//基层法务人员
            var legalLabel = "基层法务人员反馈";
            //如果基层法务人员不存在，用投资经理替代
            if(grassrootsLegalStaff==null||""==grassrootsLegalStaff||
            		grassrootsLegalStaff.value==null||grassrootsLegalStaff.value==""){
            	var tzUserName = $scope.pfr.apply.create_name;
            	if(tzUserName == null){
            		tzUserName = $scope.pfr.create_name;
            	}
            	var tzUserValue = $scope.pfr.apply.create_by;
            	if(tzUserValue == null){
            		tzUserValue = $scope.pfr.create_by;
            	}
            	grassrootsLegalStaff = {name:tzUserName,value:tzUserValue};
//            	legalLabel = "投资经理反馈";
            }
            var reviewLeader =  $scope.pfr.taskallocation.reviewLeader;//评审负责人
            var reject = {
                submitInfo:{
                    taskId:params[3],
                    runtimeVar:{inputUser:grassrootsLegalStaff.value,toTask:'-1'},
                    currentTaskVar:{opinion:'请修改'}
                },
                showInfo:{destination:legalLabel,approver:grassrootsLegalStaff.name}
            };
            if($routeParams.action=='edit1'){
            	$scope.approve = [{
                    submitInfo:{
                        taskId:params[3],
                        businessId: objId,
                        runtimeVar:{inputUser:grassrootsLegalStaff.value,toTask:'1'},
                        currentTaskVar:{opinion:'请审批'}
                    },
                    showInfo:{destination:legalLabel,approver:grassrootsLegalStaff.name}
                },{
                    submitInfo:{
                        taskId:params[3],
                        businessId: objId,
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
                        		var lawyer = lawyers[m];
                        		if(lawyer.LAWYERID != null && lawyer.LAWYERID.length > 0){
                        			approveUser.push({"name":lawyer.LAWYERNAME,"value":lawyer.LAWYERID});
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
                            businessId: objId,
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
                            businessId: objId,
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
                        businessId: objId,
                        runtimeVar:{inputUser:legalReviewLeader.VALUE,toTask:'1'},
                        currentTaskVar:{opinion:'请审批'}
                    },
                    showInfo:{destination:'法律负责人审批',approver:legalReviewLeader.NAME}
                },reject];
            }
            /*else if($routeParams.action=='edit2'){
                $scope.approve = [{
                    submitInfo:{
                        taskId:params[3],
                        runtimeVar:{inputUser:reviewLeader.VALUE,toTask:'1'},
                        currentTaskVar:{opinion:'请审批'}
                    },
                    showInfo:{destination:'提交正式评审报告及材料',approver:reviewLeader.NAME}
                },reject];
            }*/
            $('#submitModal').modal('show');
        });
    }
    $scope.wfInfo = {processDefinitionId:params[1],processInstanceId:params[2]};
}]);

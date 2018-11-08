ctmApp.register.controller('CommentsFeedback',['$http','$scope','$location','$routeParams','Upload','$timeout', function ($http,$scope,$location,$routeParams,Upload,$timeout) {
    //初始化
    var complexId = $routeParams.id;
    var params = complexId.split("@");
    var objId = params[0];
    $scope.taskID=params[3];

    if(null!=params[4] && ""!=params[4]){
        $scope.flag=params[4];
    }
    
    $scope.pre={};
    $scope.pre.taskallocation={};
    $scope.pre.apply = {};
    $scope.dic=[];
    $scope.pre.approveAttachment = {};

    $scope.savePreReviewComtents=function(showPopWin) {
        var attachmentUList = $("#attachmentUList").val();
       // if(attachmentUList!="") {
            var url = 'projectPreReview/ProjectPreReview/saveProjectPreComentsByID';
            var myDate = new Date();
            var paddNum = function(num){
                num += "";
                return num.replace(/^(\d)$/,"0$1");
            }
            if (typeof(showPopWin) == 'function') {
                var comlistdate = $scope.pre.approveAttachment.commentsList;
                for (var i = 0; i < comlistdate.length; i++) {
                    var va = comlistdate[i].commentDate;
                    if (va == '' || undefined == va) {
                        $scope.pre.approveAttachment.commentsList[i].commentDate = myDate.getFullYear() + "-" + paddNum(myDate.getMonth() + 1) + "-" + myDate.getDate();
                    }
                    var feedbackTaskID = comlistdate[i].feedbackTaskID;
                    if (feedbackTaskID == '' || undefined == feedbackTaskID) {
                        $scope.pre.approveAttachment.commentsList[i].feedbackTaskID = $scope.taskID;
                    }
                }
            }
            $scope.httpData(url, $scope.pre).success(function (data) {
                if (data.result_code == 'S') {
                    if (typeof(showPopWin) == 'function') {
                        showPopWin();
                    } else {
                        $.alert("保存成功");
                    }
                }
            });

        /* }
        else {
     $.alert("请添加附件！");
     }*/
    }
    $scope.feedbackTaskID=params[3];
    $scope.getProjectPreReviewByID=function(id){
        var  url = 'projectPreReview/ProjectPreReview/getProjectPreReviewByID';
        $scope.httpData(url,id).success(function(data){
            var pthNameArr=[],ptNameArr=[],pmNameArr=[],fgNameArr=[],rlNameArr=[];
            $scope.pre  = data.result_data;
           /* var pth=$scope.pre.apply.companyHeader;
            if(null!=pth && pth.length>0){
                for(var i=0;i<pth.length;i++){
                    pthNameArr.push(pth[i].name);
                }
                $scope.pre.apply.companyHeaderName=pthNameArr.join(",");
            }*/
            var pt1NameArr=[];
            var pt1=$scope.pre.apply.serviceType;
            if(null!=pt1 && pt1.length>0){
                for(var i=0;i<pt1.length;i++){
                    pt1NameArr.push(pt1[i].VALUE);
                }
                $scope.pre.apply.serviceType=pt1NameArr.join(",");
            }
            var pt=$scope.pre.apply.projectType;
            if(null!=pt && pt.length>0){
                for(var i=0;i<pt.length;i++){
                    ptNameArr.push(pt[i].VALUE);
                }
                $scope.pre.apply.projectType=ptNameArr.join(",");
            }
            var pm=$scope.pre.apply.projectModel;
            if(null!=pm && pm.length>0){
                for(var j=0;j<pm.length;j++){
                    pmNameArr.push(pm[j].VALUE);
                }
                $scope.pre.apply.projectModel=pmNameArr.join(",");
            }
            var fg=$scope.pre.taskallocation.fixedGroup;
            if(null!=fg && fg.length>0){
                for(var k=0;k<fg.length;k++){
                    fgNameArr.push(fg[k].NAME);
                }
                $scope.pre.taskallocation.fixedGroup=fgNameArr.join(",");
            }

            $scope.fileName=[];
            var filenames=$scope.pre.attachment;
            for(var i=0;i<filenames.length;i++){
                var arr={UUID:filenames[i].UUID,ITEM_NAME:filenames[i].ITEM_NAME};
                $scope.fileName.push(arr);
            }

            var commentsList =  $scope.pre.approveAttachment.commentsList;
            $scope.feedbackTaskID_val=0;
            for(var i=0;i<commentsList.length;i++){
                if(undefined!=commentsList[i].feedbackTaskID) {
                    if ($scope.feedbackTaskID_val < commentsList[i].feedbackTaskID) {
                        $scope.feedbackTaskID_val = commentsList[i].feedbackTaskID;
                    }
                }else{
                    commentsList[i].feedbackTaskID=params[3];
                    $scope.feedbackTaskID_val=params[3];
                }
            }
            if($scope.feedbackTaskID_val <=$scope.feedbackTaskID){
                $scope.feedbackTaskID=$scope.feedbackTaskID_val;
            }
            if(null!=$scope.pre.apply.tenderTime) {
                $scope.changDate($scope.pre.apply.tenderTime);
            }
            $scope.getProjectPreReviewYJDWByID(objId);
        });

    }
    $scope.changDate=function(values) {
        var nowDate=null;
        nowDate=$scope.pre.create_date;
        if(null==nowDate || ""==nowDate){
            var date = new Date();
            var paddNum = function (num) {
                num += "";
                return num.replace(/^(\d)$/, "0$1");
            }
            nowDate = date.getFullYear() + "-" + paddNum(date.getMonth() + 1) + "-" + paddNum(date.getDate());
        }else{
            nowDate=nowDate.substr(0,10);
        }
        var d=DateDiff(values,nowDate);
        if(d<=5){
            $("#showdate").text("申请距离投标时间："+d+"天").attr("style","color:#ff0000;font-weight:bold");
        }else{
            $("#showdate").text("申请距离投标时间："+d+"天").removeAttr("style","color:#ff0000");
        }
    }
    $scope.getSelectTypeByCode=function(typeCode){
        var  url = 'common/commonMethod/selectDataDictionByCode';
        $scope.httpData(url,typeCode).success(function(data){
            if(data.result_code === 'S'){
             //   $scope.dic=[];
                $scope.optionTypeList=data.result_data;
            }else{
                $.alert(data.result_name);
            }
        });
    }

    $scope.getProjectPreReviewByID(objId);
    angular.element(document).ready(function() {
        $scope.getSelectTypeByCode("06");
    });
    $scope.getnum=function(name) {
        var numbers= 0;
        var attachmentList = $scope.pre.attachment;
        for (var ii = 0; ii < attachmentList.length; ii++) {
            if(attachmentList[ii].ITEM_NAME==name.ITEM_NAME){
                if(undefined!=attachmentList[ii].files && null!=attachmentList[ii].files && ""!=attachmentList[ii].files){
                    numbers= attachmentList[ii].files.length;
                }else if( null==attachmentList[ii].files || ""==attachmentList[ii].files){
                    numbers= 0;
                }
            }
        }
        var attachment_newList= $scope.pre.approveAttachment.attachmentNew;
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
                            // numbers = numbers * 1 + 1;
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
    /*查询一级审批列表*/
    $scope.getProjectPreReviewYJDWByID=function(id){
        var  url = 'rcm/ProjectInfo/getTaskOpinionTwo';
        $scope.panam={taskDefKey:'usertask16' ,businessId:id};
        $scope.httpData(url,$scope.panam).success(function(data){
            if(data.result_code === 'S') {
                var yijd = data.result_data;
                if (null != yijd) {
                    $scope.yjdw = yijd.DEPTNAME+" _ "+yijd.USERNAME+"："+yijd.OPINION;
                    if(null!=$scope.pre.cesuanFileOpinion && ''!=$scope.pre.cesuanFileOpinion){
                        $scope.yjdw = $scope.yjdw+" ；<br/>项目投资测算意见："+$scope.pre.cesuanFileOpinion;
                    }
                    if(null!=$scope.pre.tzProtocolOpinion && ''!=$scope.pre.tzProtocolOpinion){
                        $scope.yjdw = $scope.yjdw+" ；<br/>项目投资协议意见：" + $scope.pre.tzProtocolOpinion;
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
    $scope.errorAttach=[];
    $scope.upload = function (file,errorFile, idx,name,versionNun) {
        if(errorFile && errorFile.length>0){
        	var errorMsg = fileErrorMsg(errorFile);
        	$scope.errorAttach[idx]={msg:errorMsg};
        }else if(file){
            var fileFolder = "preAssessment/";
                var dates=$scope.pre.create_date;
                var no=$scope.pre.apply.projectNo;
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
                $scope.pre.approveAttachment.attachmentNew[idx].attachment_new=retData;
                }, function (resp) {
                console.log('Error status: ' + resp.status);
            }, function (evt) {
                var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                $scope["progress"+idx]=progressPercentage == 100 ? "":progressPercentage+"%";
            });
        }
    };
    $scope.upload2 = function (file,errorFile, idx) {
        if(errorFile && errorFile.length>0){
        	var errorMsg = fileErrorMsg(errorFile);
        	$scope.errorAttach[idx]={msg:errorMsg};
        }else if(file){
            var fileFolder = "preAssessment/";
            var dates=$scope.pre.create_date;
            var no=$scope.pre.apply.projectNo;
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
                retData.version = "1";
                var myDate = new Date();
                var paddNum = function(num){
                    num += "";
                    return num.replace(/^(\d)$/,"0$1");
                }
                retData.upload_date=myDate.getFullYear()+"-"+paddNum(myDate.getMonth()+1)+"-"+ myDate.getDate()+" "+myDate.getHours()+":"+myDate.getMinutes()+":"+myDate.getSeconds();
                $scope.pre.approveAttachment.commentsList[idx].files=retData;
            }, function (resp) {
                console.log('Error status: ' + resp.status);
            }, function (evt) {
                var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                $scope["progress"+idx]=progressPercentage == 100 ? "":progressPercentage+"%";
            });
        }
    };

    $scope.downLoadFile = function(df){
        window.location.href = srvUrl+"common/RcmFile/downLoad?filePath="+df.filePath+"&fileName="+encodeURI(df.fileName);
    }
    $scope.actionPam = $routeParams.action;
    //是否显示保存按钮
   // $scope.isShow = $routeParams.action==='edit'?true:false;
    $scope.isShow =true;
    //提交流程
    $scope.showSubmitModal = function(){
        $scope.savePreReviewComtents(function(){
            //子流程中多个节点都调用此页面，流程要根据url中的action决定流程下一步处理人,下一节点名称
            var currentUserName = $scope.credentials.userName;
            var action = $routeParams.action, inputUser,inputUserName, destination, isSkipServiceType;
            if(action=="edit"){
                var companyHeader = $scope.pre.apply.companyHeader;
                inputUser = companyHeader.value;
                inputUserName = companyHeader.name;
                destination = "单位负责人审批";
            }else if(action == "view1"){
                var approveUser =  $scope.pre.apply.investmentPerson;
                if(approveUser == null || typeof(approveUser)=='undefined'|| approveUser.name==null||approveUser.name==""){
                    approveUser = $scope.pre.apply.directPerson;
                }
                if(approveUser==null||approveUser.name==null||approveUser==""||approveUser.name==""){
                	var approveUser =  $scope.pre.taskallocation.reviewLeader;
                    inputUser = approveUser.VALUE;
                    inputUserName = approveUser.NAME;
                    destination = "评审负责人确认";
                    isSkipServiceType = "1";
                }else{
                	inputUser = approveUser.value;
                    inputUserName = approveUser.name;
                    destination = "投资中心/水环境投资中心审核";
                    isSkipServiceType = "0";
                }
            }else if(action == "view2"){
                var approveUser =  $scope.pre.taskallocation.reviewLeader;
                inputUser = approveUser.VALUE;
                inputUserName = approveUser.NAME;
                destination = "评审负责人确认";
            }
            $scope.approve = [{
                submitInfo:{
                    taskId: params[3],
                    runtimeVar: {inputUser: inputUser,toTask:0, isSkipServiceType:isSkipServiceType},
                    currentTaskVar: {opinion: '请审批'}
                },
                showInfo:{destination: destination, approver:inputUserName}
            }];
            if(action=='edit'){
                $scope.approve[0].submitInfo.runtimeVar = {inputUser:inputUser};
            }else{
                var investmentManager = $scope.pre.apply.investmentManager;
                var backInfo = {
                    submitInfo:{
                        taskId:params[3],
                        runtimeVar:{inputUser:investmentManager.value,toTask:'-1'},
                        currentTaskVar:{opinion:'请修改'}
                    },
                    showInfo:{destination:'退回投资经理反馈',approver:investmentManager.name}
                }
                $scope.approve.push(backInfo);
            }
            $('#submitModal').modal('show');
        });
    }
    $scope.wfInfo = {processDefinitionId:params[1],processInstanceId:params[2]};
}]);

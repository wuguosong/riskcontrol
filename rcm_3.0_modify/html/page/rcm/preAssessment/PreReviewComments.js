ctmApp.register.controller('PreReviewComments', ['$http','$scope','$location','$routeParams','Upload','$timeout', function ($http,$scope,$location,$routeParams,Upload,$timeout) {
    //初始化
    var complexId = $routeParams.id;
    var params = complexId.split("@");
    var objId = params[0];
    var taskID= params[3];
    if(null!=params[4] && ""!=params[4]){
        $scope.flag=params[4];
    }
    $scope.taskID=taskID;
    $scope.pre={};
    $scope.pre.taskallocation={};
    $scope.pre.apply = {};
    $scope.dic=[];
    $scope.pre.approveAttachment = {commentsList:[]};


    function addBlankRow(array){
        var blankRow = {
            comment_type:'',
            comment_feedback:'',
            comment_department:'',
            attachment_old:'',
            attachment_new:''
        }
        array.push(blankRow);
    }


    function addBlankRow(array){
        var blankRow = {
            opinionType:'',
            commentConent:'',
            commentDepartment:'',
            commentFeedback:'',
            commentDate:'',
            attachment_new:'',
            taskId:''
        }
        array.push(blankRow);
    }
    if($scope.pre.approveAttachment.commentsList==null){
        $scope.pre.approveAttachment.commentsList = [];
        addBlankRow($scope.pre.approveAttachment.commentsList);
    }
    if($scope.pre.approveAttachment.attachmentNew==null){
        $scope.pre.approveAttachment.attachmentNew = [];
        addBlankRow($scope.pre.approveAttachment.attachmentNew);
    }
    $scope.addComment = function(){
        function addBlankRow(array){
            var blankRow = {
                opinionType:'',
                commentConent:'',
                commentDepartment:'',
                commentFeedback:'',
                commentDate:'',
                taskId:taskID
            }
            var size=0;
            for(attr in array){
                size++;
            }
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

    $scope.deleteComment = function(n){
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

    $scope.addAttachment = function(){
        function addBlankRow(array){
            var blankRow = {
                attachment_new:'',
                taskId:taskID
            }
            var size=0;
            for(attr in array){
                size++;
            }
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

    $scope.savePreReviewComtents=function(showPopWin){
        var  url = 'projectPreReview/ProjectPreReview/saveProjectPreComentsByID';
        if(typeof $scope.pre.approveAttachment =='undefined'){
            $.alert("请填写初步评审意见");
            return false;
        }
        $scope.httpData(url,$scope.pre).success(function(data){
            if(data.result_code === 'S') {
                if (typeof(showPopWin) == 'function') {
                    showPopWin();
                } else {
                    $.alert("保存成功");
                }
            }else{
                $.alert("保存失败");
            }
        });
    }
    var fgIdArr = [],fgNameArr=[];
    $scope.getProjectPreReviewByID=function(id){
        var  url = 'projectPreReview/ProjectPreReview/getProjectPreReviewByID';
        $scope.httpData(url,id).success(function(data){
            var ptNameArr=[],pmNameArr=[],rlNameArr=[],pthNameArr=[];
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
                    fgIdArr.push(fg[k].VALUE);
                }
                $scope.pre.taskallocation.fixedGroup=fgNameArr.join(",");
            }

            $scope.fileName=[];
            var filenames=$scope.pre.attachment;
            for(var i=0;i<filenames.length;i++){
                var arr={UUID:filenames[i].UUID,ITEM_NAME:filenames[i].ITEM_NAME};
                $scope.fileName.push(arr);
            }
            if(undefined==$scope.pre.approveAttachment){
                $scope.pre.approveAttachment = {};
                $scope.addComment();
                $scope.addAttachment();
             }
            $("select[id^='attachmentNew']").attr("disabled","disabled");
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
               // $scope.dic=[];
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
    $scope.downLoadFile = function(df){
        window.location.href = srvUrl+"common/RcmFile/downLoad?filePath="+df.filePath+"&fileName="+encodeURI(df.fileName);
    }
   $scope.changevalue=function(o){
        var  url = 'projectPreReview/ProjectPreReview/getProjectPreReviewBy';
       $scope.httpData(url,o).success(function(data){
            if(data.result_code === 'S'){
                $scope.optionTypeList=data.result_data;
            }else{
                $.alert(data.result_name);
            }
        });
    }
    $scope.actionPam = $routeParams.action;
    $scope.showSubmitModal = function(){
        $scope.savePreReviewComtents(function(){
            var opinion = "", destination="", approverName="";
            var currentUserName = $scope.credentials.userName;
            var investMentUser = $scope.pre.apply.investmentManager;
            $scope.approve = [{
                submitInfo:{
                    taskId:params[3],
                    runtimeVar:{inputUser:investMentUser.value,toTask:0},
                    currentTaskVar:{opinion:"请反馈"}
                },
                showInfo:{destination: '投资经理反馈',approver:investMentUser.name}
            },{
                submitInfo:{
                    taskId:params[3],
                    runtimeVar:{assigneeList:fgIdArr,toTask:1},
                    currentTaskVar:{opinion:'请审批'}
                },
                showInfo:{destination: "固定小组成员审批",approver:fgNameArr.join(',')}
            },{
                toNodeType:'end',
                submitInfo:{
                    taskId:params[3],
                    runtimeVar:{toTask:2}
                },
                showInfo:{destination: "结束流程"}
            }];
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
            $('#submitModal').modal('show');
        });
    }
    $scope.wfInfo = {processDefinitionId:params[1],processInstanceId:params[2]};

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
                if(attachment_newList[j].attachmentUList.ITEM_NAME==name.ITEM_NAME) {
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
}]);

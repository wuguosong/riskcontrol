ctmApp.register.controller('SummaryOfComments', ['$http','$scope','$location','$routeParams', function ($http,$scope,$location,$routeParams) {
    //初始化
    var complexId = $routeParams.id;
    var params = complexId.split("@");
    var objId = params[0];
    var taskID= params[3];;
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
            var num = 0;
            for (var parent in commentsObj) {
                for (var child in commentsObj[parent]) {
                    if (commentsObj[parent][child] == true) {
                        delete commentsObj[num];
                    }
                }
                num++;
            }
            var newComments = [];
            var size = 0;
            for (var obj in commentsObj) {
                newComments[size] = commentsObj[obj];
                size++;
            }
        }
        if(n==0) {
            $scope.pre.approveAttachment.commentsList = newComments;
        }else if(n==1){
            $scope.pre.approveAttachment.attachmentNew = newComments;
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
            /*var pth=$scope.pre.apply.companyHeader;
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
                alert(data.result_name);
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
        var  url = 'http://localhost:8080/rcm-rest/projectPreReview/ProjectPreReview/getProjectPreReviewBy';
        $http.post(url,o).success(function(data){
            if(data.result_code === 'S'){
                $scope.optionTypeList=data.result_data;
            }else{
                alert(data.result_name);
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
                showInfo:{destination: "评审小组固定成员审批",approver:fgNameArr.join(',')}
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
            if($routeParams.action=='confirm'){
                $scope.approve.push(passParam);
            }
            $('#submitModal').modal('show');
        });
    }
    $scope.wfInfo = {processDefinitionId:params[1],processInstanceId:params[2]};
}]);

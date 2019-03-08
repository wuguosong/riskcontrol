define(['app', 'Service', 'select2'], function (app) {
    app
        .register.controller('dropOutReportCtrl', ['$http', '$scope', '$location', '$stateParams', 'BEWG_URL', 'Window',
        function ($http, $scope, $location, $stateParams, BEWG_URL, Window, Upload) {
            console.log('dropOutReport');


            //初始化
            $scope.tabIndex =  $stateParams.tabIndex;
            $scope.controller_val = $location.$$url.split("/")[1];
            var complexId = $stateParams.id;
            $scope.paramId = complexId;
            var params = complexId.split("@");
            if(null!=params[1] && ""!=params[1]){
                $scope.flag=params[1];
            }
            if(null!=params[2] && ""!=params[2] && null!=params[3] && ""!=params[3] && null!=params[4] && ""!=params[4] && null!=params[5] && ""!=params[5] && null!=params[6] && ""!=params[6]){
                $scope.reportReturnId=params[2]+"@"+params[3]+"@"+params[4]+"@"+params[5]+"@"+params[6];
            }
            var objId = params[0];
            var taskID='wwxosnsosjdsnsjnddID';
            $scope.taskID=taskID;
            $scope.formalReport={};
            $scope.dic=[];
            function FormatDate() {
                var date = new Date();
                var paddNum = function(num){
                    num += "";
                    return num.replace(/^(\d)$/,"0$1");
                }
                return date.getFullYear()+""+paddNum(date.getMonth()+1)+""+paddNum(date.getDate());
            }

            var action = $stateParams.action;
            $scope.actionparm=$stateParams.action;
            $scope.initData = function(){
                if(action=="Create"){
                    $scope.title = "正式评审报告-新增";
                    $scope.getProjectFormalReviewByID(objId);
                    $scope.formalReport.create_by=$scope.credentials.UUID;
                    $scope.formalReport.create_name=$scope.credentials.userName;
                    $("#wordbtn").hide();
                }else if(action=="Update"){
                    $scope.title = "正式评审报告-修改";
                    $scope.getByID(objId);
                }else if(action=="View"){
                    $scope.title = "正式评审报告-查看";
                    $scope.getByID(objId);
                    $("#savebtn").hide();
                }
            }

            //给第三部分添加行
            $scope.addtableRow=function(n){
                function addcellRow(array){
                    var blankRow = {
                        riskPoint:'',
                        riskContent:'',
                        pointsAndProblems:'',
                        opinionAndCommitment:''
                    }
                    array.push(blankRow);
                }
                if(undefined==$scope.formalReport){
                    $scope.formalReport={projectConcernsIssues:{}};
                }
                if(n==1){
                    if(undefined==$scope.formalReport.projectConcernsIssues){
                        $scope.formalReport.projectConcernsIssues={leadershipDecision:[]};
                    }
                    if(undefined==$scope.formalReport.projectConcernsIssues.leadershipDecision){
                        $scope.formalReport.projectConcernsIssues.leadershipDecision=[];
                    }
                    addcellRow($scope.formalReport.projectConcernsIssues.leadershipDecision);
                }else if(n==2){
                    if(undefined==$scope.formalReport.projectConcernsIssues){
                        $scope.formalReport.projectConcernsIssues={investmentConditions:[]};
                    }
                    if(undefined==$scope.formalReport.projectConcernsIssues.investmentConditions){
                        $scope.formalReport.projectConcernsIssues.investmentConditions=[];
                    }
                    addcellRow($scope.formalReport.projectConcernsIssues.investmentConditions);
                }else if(n==3){
                    if(undefined==$scope.formalReport.projectConcernsIssues){
                        $scope.formalReport.projectConcernsIssues={implementationRequirements:[]};
                    }
                    if(undefined==$scope.formalReport.projectConcernsIssues.implementationRequirements){
                        $scope.formalReport.projectConcernsIssues.implementationRequirements=[];
                    }
                    addcellRow($scope.formalReport.projectConcernsIssues.implementationRequirements);
                }else if(n==4){
                    if(undefined==$scope.formalReport.projectConcernsIssues){
                        $scope.formalReport.projectConcernsIssues={prompt:[]};
                    }
                    if(undefined==$scope.formalReport.projectConcernsIssues.prompt){
                        $scope.formalReport.projectConcernsIssues.prompt=[];
                    }
                    addcellRow($scope.formalReport.projectConcernsIssues.prompt);
                }
            }
            //第三分部删除行
            $scope.delrow=function(name,num){
                if(num==1) {
                    var names = $scope.formalReport.projectConcernsIssues.leadershipDecision;
                    names.splice(name, 1);
                    if (names.length > 0) {
                        $scope.formalReport.projectConcernsIssues.leadershipDecision = names;
                    } else {
                        $scope.formalReport.projectConcernsIssues.leadershipDecision = [];
                    }
                }else if(num==2) {
                    var names = $scope.formalReport.projectConcernsIssues.investmentConditions;
                    names.splice(name, 1);
                    if (names.length > 0) {
                        $scope.formalReport.projectConcernsIssues.investmentConditions = names;
                    } else {
                        $scope.formalReport.projectConcernsIssues.investmentConditions = [];
                    }
                }else if(num==3) {
                    var names = $scope.formalReport.projectConcernsIssues.implementationRequirements;
                    names.splice(name, 1);
                    if (names.length > 0) {
                        $scope.formalReport.projectConcernsIssues.implementationRequirements = names;
                    } else {
                        $scope.formalReport.projectConcernsIssues.implementationRequirements = [];
                    }
                }else if(num==4) {
                    var names = $scope.formalReport.projectConcernsIssues.prompt;
                    names.splice(name, 1);
                    if (names.length > 0) {
                        $scope.formalReport.projectConcernsIssues.prompt = names;
                    } else {
                        $scope.formalReport.projectConcernsIssues.prompt = [];
                    }
                }
            }
            $scope.addOption = function(){
                function addBlankRow(array){
                    var blankRow = {
                        option_content:'',
                        option_execute:'',
                        taskId:taskID
                    }
                    var size=0;
                    for(attr in array){
                        size++;
                    }
                    array[size]=blankRow;
                }

                if(undefined==$scope.formalReport){
                    $scope.formalReport={optionList:[]};

                }
                if(undefined==$scope.formalReport.optionList){
                    $scope.formalReport.optionList=[];
                }
                addBlankRow($scope.formalReport.optionList);
            }
            $scope.addRequire = function(){
                function addBlankRow(array){
                    var blankRow = {
                        require_content:'',
                        require_execute:'',
                        taskId:taskID
                    }
                    var size=0;
                    for(attr in array){
                        size++;
                    }
                    array[size]=blankRow;
                }

                if(undefined==$scope.formalReport){
                    $scope.formalReport={requireList:[]};

                }
                if(undefined==$scope.formalReport.requireList){
                    $scope.formalReport.requireList=[];
                }
                addBlankRow($scope.formalReport.requireList);
            }
            $scope.commonDdelete = function(n){
                var commentsObj=null;
                if(n==0) {
                    commentsObj = $scope.formalReport.optionList;
                }else if(n==1){
                    commentsObj = $scope.formalReport.requireList;
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

            $scope.getProjectFormalReviewByID=function(id){
                $http({
                    method:'post',
                    url: BEWG_URL.SelectReportPfrView,
                    data: $.param({"id":id})
                }).success(function(data){

                    $scope.pfr  = data.result_data;
                    $scope.formalReport.projectFormalId=$scope.pfr.id;
                    $scope.formalReport.projectName=$scope.pfr.apply.projectName;
                    $scope.formalReport.projectNo=$scope.pfr.apply.projectNo;
                    if(null!=$scope.pfr.apply.reportingUnit){
                        $scope.formalReport.reportingUnit=$scope.pfr.apply.reportingUnit.name;
                    }
                    var ptNameArr=[];
                    var pt=$scope.pfr.apply.projectType;
                    if(null!=pt && pt.length>0){
                        for(var i=0;i<pt.length;i++){
                            ptNameArr.push(pt[i].VALUE);
                        }
                        $scope.formalReport.projectTypeName=ptNameArr.join(",");
                    }
                    $scope.formalReport.controllerVal = $scope.controller_val;
                    $scope.formalReport.attachment=[{'file':'','fileName':''}];
                    $scope.addtableRow(1);
                    $scope.addtableRow(2);
                    $scope.addtableRow(3);
                    $scope.addtableRow(4);
                    if(undefined==$scope.formalReport.optionList){
                        $scope.addOption();
                    }
                    if(undefined==$scope.formalReport.requireList){
                        $scope.addRequire();
                    }
                    if(null==$scope.formalReport.risksProblems) {
                        $scope.formalReport.risksProblems="根据法律专业评审意见（XX条）以及技术专业评审意见（具体详见附件1），现就本项目提出XX条风险及问题，其中第1条至第XX条是需要决策机构重点关注，其余为通报列示供决策参考。";
                    }
                }).error(function(data,status,headers, config){
                    Window.alert(status);
                })
            }

            $scope.saveReport3 = function(callBack){
                if(typeof callBack == 'function') {
                    if ($("#DropOutReport").valid()) {
                        var url_post;
                        if (typeof ($scope.formalReport._id) != "undefined") {
                            url_post = BEWG_URL.UpdateReportPfr;
                        } else {
                            var boolean = $scope.isReportExist();
                            if(boolean){
                                Window.alert("请勿重复保存数据!");
                                return false;
                            }
                            url_post = BEWG_URL.SaveReportPfr;
                        }
                        $http({
                            method:'post',
                            url: url_post,
                            data: $.param({"json":JSON.stringify($scope.formalReport)})
                        }).success(function(data){
                            if(data.success){
                                $scope.formalReport._id = data.result_data;
                                if(typeof callBack == 'function'){
                                    callBack();
                                }else{
                                    Window.alert("保存成功!");
                                }
                            }else{
                                Window.alert(data.result_name);
                            }
                        }).error(function(data,status,headers,config){
                            Window.alert(status);
                        });
                    }
                }else{
                    var url_post;
                    if (typeof ($scope.formalReport._id) != "undefined") {
                        url_post = BEWG_URL.UpdateReportPfr;
                    } else {
                        var boolean = $scope.isReportExist();
                        if(boolean){
                            Window.alert("请勿重复保存数据!");
                            return false;
                        }
                        url_post = BEWG_URL.SaveReportPfr;
                    }
                    $http({
                        method:'post',
                        url: url_post,
                        data: $.param({"json":JSON.stringify($scope.formalReport)})
                    }).success(function(data){
                        if(data.success){
                            $scope.formalReport._id = data.result_data;
                            if (typeof callBack == 'function') {
                                callBack();
                            } else {
                                Window.alert("保存成功");
                            }
                            $("#wordbtn").show();
                        }else{
                            Window.alert(data.result_name);
                        }
                    }).error(function(data,status,headers,config){
                        Window.alert(status);
                    });
                }
            }

            $scope.isReportExist = function(){
                var boolean = false;
                $.ajax({
                    url: BEWG_URL.IsExistReportPfr,
                    type: "POST",
                    dataType: "json",
                    data: {"businessid": objId},
                    async: false,
                    success: function(data){
                        if(data.result_data > 0){
                            boolean = true;
                        }
                    }
                });
                return boolean;
            }

            //检查是否可提交
            $scope.isPossible2Submit = function(){
                var boolean = false;
                $.ajax({
                    url: BEWG_URL.IsSubmitReportPfr,
                    type: "POST",
                    dataType: "json",
                    data: {"projectFormalId":$scope.formalReport.projectFormalId},
                    async: false,
                    success: function(data){
                        if(data.result_data){
                            boolean = true;
                        }
                    }
                });
                return boolean;
            }

            //弹出是否上会的提示
            $scope.showSubmitModal = function() {
                show_Mask();
                var flag = $scope.isPossible2Submit();
                if(flag){
                    $scope.saveReport3(function(){
                        $http({
                            method:'post',
                            url: BEWG_URL.SubmitReportPfr,
                            data: $.param({"id":$scope.formalReport._id,"projectFormalId":$scope.formalReport.projectFormalId})
                        }).success(function(data){
                            if(data.success){
                                var istrue = $scope.createwords();
                                if(istrue){
                                    hide_Mask();
                                    Window.alert("提交成功!");
                                    $('input').attr("readonly","readonly");
                                    $('textarea').attr("readonly","readonly");
                                    $('button').attr("disabled","disabled");
                                    $('#wordbtn').attr("disabled",false);
                                    $("#savebtn").hide();
                                    $("#submitbnt").hide();
                                    $(".modal-footer button").attr({"disabled":false});
                                }else{
                                    hide_Mask();
                                    Window.alert("提交失败，请检查是否填写完整后重新提交或联系管理员!");
                                    return false;
                                }
                            }
                        }).error(function(data,status,headers, config){
                            hide_Mask();
                            Window.alert(status);
                            return false;
                        });
                    });
                }else{
                    hide_Mask();
                    Window.alert("请确保流程已结束!");
                    return false;
                }
            }
            //点击提交时先生成word，生成成功后方可上会
            $scope.createwords = function(){
                var boolean = true;
                $.ajax({
                    url: BEWG_URL.CreateReportPfrWord,
                    type: "POST",
                    dataType: "json",
                    data: {"id":$scope.formalReport._id},
                    async: false,
                    success: function(data){
                        if(!data.success){
                            boolean = false;
                            Window.alert("提交前系统报表生成失败，请查看必填项是否填写完毕；如果全部正常填写请联系管理员!");
                        }
                    }
                });
                return boolean;
            }

            $scope.getByID=function(id){
                $http({
                    method:'post',
                    url: BEWG_URL.SelectReportPrfrById,
                    data: $.param({"id":id})
                }).success(function(data){
                    $scope.formalReport  = data.result_data;
                    var files=$scope.formalReport.attachment;
                    if(files==""){
                        $scope.formalReport.attachment=[{'file':'','fileName':''}];
                    }
                    if(action=="View"){
                        $('input').attr("readonly","readonly");
                        $('textarea').attr("readonly","readonly");
                        $('button').attr("disabled","disabled");
                        $("#submitbnt").attr("disabled",false);
                        $('#wordbtn').attr("disabled",false);
                    }
                }).error(function(data,status,headers, config){
                    Window.alert(status);
                });
            }

            $scope.createWord = function(id){
                startLoading();
                $http({
                    method:'post',
                    url: BEWG_URL.CreateReportPfrWord,
                    data: $.param({"id":id})
                }).success(function(data){
                    if (data.success) {
                        var filesPath = data.result_data.filePath;
                        var index = filesPath.lastIndexOf(".");
                        var str = filesPath.substring(index + 1, filesPath.length);
                        var filesName = data.result_data.fileName;

                        window.location.href = srvUrl+"file/downloadFile.do?filepaths="+encodeURI(filesPath)+"&filenames="+encodeURI(encodeURI("正式评审-"+filesName+"报告-单体模式.")) + str;
                    } else {
                        Window.alert("报表生成失败，请查看必填项是否填写完毕；如果全部正常填写请联系管理员！");
                    }
                }).error(function(data,status,headers, config){
                    Window.alert(status);
                });
                endLoading();
            }
            $scope.errorAttach=[];
            $scope.uploadfformal2 = function (file,errorFile, idx) {
                if(errorFile && errorFile.length>0){
                    var errorMsg = fileErrorMsg(errorFile);
                    $scope.errorAttach[idx]={msg:errorMsg};
                }else if(file){
                    var fileFolder = "formalReport/";
                    if($routeParams.action=='Create') {
                        if(undefined==$scope.formalReport.projectNo){
                            Window.alert("请先选择项目然后上传附件");
                            return false;
                        }
                        var no=$scope.formalReport.projectNo;
                        fileFolder=fileFolder+FormatDate()+"/"+no;
                    }else{
                        var dates=$scope.formalReport.create_date;
                        var no=$scope.formalReport.projectNo;
                        var strs= new Array(); //定义一数组
                        strs=dates.split("-"); //字符分割
                        dates=strs[0]+strs[1]; //分割后的字符输出
                        fileFolder=fileFolder+dates+"/"+no;
                    }
                    $scope.errorAttach[idx]={msg:''};

                    Upload.upload({
                        url: BEWG_URL.UploadFile,
                        data: {file: file, folder:fileFolder}
                    }).then(function (resp) {
                        var retData = resp.data.result_data[0];
                        retData.version = "0";
                        $scope.formalReport.attachment=[retData];
                    }, function (resp) {
                        Window.alert(resp.status);
                    }, function (evt) {
                        var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                        $scope["progress"+idx]=progressPercentage == 100 ? "":progressPercentage+"%";
                    });
                }
            };

            $scope.downLoadFile = function(idx){
                var filePath = $scope.formalReport.attachment[0].filePath, fileName = $scope.formalReport.attachment[0].fileName;
                window.location.href = srvUrl+"file/downloadFile.do?filepaths="+encodeURI(filePath)+"&filenames="+encodeURI(encodeURI(fileName));
            }

            // 返回列表
            $scope.cancel = function (){
                $location.path("/index/FormalReportList_new/" + $scope.tabindex);
            };

            $scope.initData();

        }]);
});
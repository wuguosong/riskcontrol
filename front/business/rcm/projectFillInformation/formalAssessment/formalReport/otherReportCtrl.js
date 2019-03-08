define(['app', 'Service', 'select2'], function (app) {
    app
        .register.controller('otherReportCtrl', ['$http', '$scope', '$location', '$stateParams', 'BEWG_URL', 'Window',
        function ($http, $scope, $location, $stateParams, BEWG_URL, Window, Upload) {
            console.log('otherReport');


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
            $scope.formalReport={};

            var action = $stateParams.action;
            $scope.initData = function(){
                if(action == "Create"){
                    $scope.title = "正式评审报告-新增";
                    $scope.getProjectFormalReviewByID(objId);
                    $scope.formalReport.create_by=$scope.credentials.UUID;
                    $scope.formalReport.create_name=$scope.credentials.userName;
                    $("#wordbtn").hide();
                }else if(action == "Update"){
                    $scope.title = "正式评审报告-修改";
                    $scope.getByID(objId);
                }else if(action == "View"){
                    $scope.title = "正式评审报告-查看";
                    $scope.getByID(objId);
                    $("#savebtn").hide();
                    $("#btnfile").attr("disabled","disabled");
                }
            }


            $scope.dic=[];
            function FormatDate() {
                var date = new Date();
                var paddNum = function(num){
                    num += "";
                    return num.replace(/^(\d)$/,"0$1");
                }
                return date.getFullYear()+""+paddNum(date.getMonth()+1);
            }

            $scope.saveReport7 = function(callBack){

                var filep=$scope.formalReport.filePath;
                if(null==filep || ""==filep){
                    Window.alert("请上传附件");
                    return false;
                }

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
                    /*hide_Mask();*/
                    if(data.success){
                        $scope.formalReport._id = data.result_data;
                        if(typeof callBack == 'function'){
                            callBack();
                        }else{
                            Window.alert("保存成功");
                        }
                        $("#wordbtn").show();
                    }else {
                        alert(data.result_name);
                    }
                }).error(function(data,status,headers,config){
                    Window.alert(status);
                });
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

            //提交报告并更改状态
            $scope.showSubmitModal = function() {
                var flag = $scope.isPossible2Submit();
                if(flag){
                    $scope.saveReport7(function(){
                        show_Mask();
                        $http({
                            method:'post',
                            url: BEWG_URL.SubmitReportPfr,
                            data: $.param({"id":$scope.formalReport._id,"projectFormalId":$scope.formalReport.projectFormalId})
                        }).success(function(data){
                            if(!data.success){
                                hide_Mask();
                                Window.alert(data.result_name);
                                return false;
                            }
                            hide_Mask();
                            Window.alert("提交成功!");
                            $('button').attr("disabled","disabled");
                            $("#savebtn").hide();
                            $("#submitbnt").hide();
                            $(".modal-footer button").attr({"disabled":false});
                        }).error(function(data,status,headers,config){
                            hide_Mask();
                            Window.alert(status);
                        });
                    });
                }else{
                    hide_Mask();
                    Window.alert("请确保流程已结束!");
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
                    var ptNameArr=[],pmNameArr=[];
                    var pt=$scope.pfr.apply.projectType;
                    if(null!=pt && pt.length>0){
                        for(var i=0;i<pt.length;i++){
                            ptNameArr.push(pt[i].VALUE);
                        }
                        $scope.formalReport.projectTypeName=ptNameArr.join(",");
                    }
                    $scope.formalReport.controllerVal = $scope.controller_val;
                }).error(function(data,status,headers,config){
                    Window.alert(status);
                });
            }

            $scope.getByID = function(id){
                $http({
                    method:'post',
                    url: BEWG_URL.SelectReportPrfrById,
                    data: $.param({"id":id})
                }).success(function(data){
                    $scope.formalReport  = data.result_data;
                    if(action=="View"){
                        $('button').attr("disabled","disabled");
                        $("#submitbnt").attr("disabled",false);
                        $('#wordbtn').attr("disabled",false);
                    }
                }).error(function(data,status,headers,config){
                    Window.alert(status);
                });
            }
            $scope.errorAttach=[];
            $scope.uploadfformal2 = function (file,errorFile, idx) {
                if(null!=file) {
                    var str = file.name;
                    var index = str.lastIndexOf(".");
                    str = str.substring(index + 1, str.length);
                    if (str == "doc" || str == "DOC" || str == "docx" || str == "DOCX") {
                    }else{
                        Window.alert("请上传word文件！");
                        return false;
                    }
                }
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
                        $scope.formalReport.filePath=retData.filePath;
                        $scope.formalReport.fileName=retData.fileName;
                    }, function (resp) {
                        console.log('Error status: ' + resp.status);
                    }, function (evt) {
                        var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                        $scope["progress"+idx]=progressPercentage == 100 ? "":progressPercentage+"%";
                    });
                }
            };

            $scope.downLoadFile = function(filePath,filename){
                var isExists = validFileExists(filePath);
                if(!isExists){
                    Window.alert("要下载的文件已经不存在了！");
                    return false;
                }
                if(filename!=null && filename.length>12){
                    filename = filename.substring(0, 12)+"...";
                }else{
                    filename = filename.substring(0,filename.lastIndexOf("."));
                }

                if(undefined!=filePath && null!=filePath){
                    var index = filePath.lastIndexOf(".");
                    var str = filePath.substring(index + 1, filePath.length);
                    window.location.href = srvUrl+"file/downloadFile.do?filepaths="+encodeURI(encodeURI(filePath))+"&filenames="+encodeURI(encodeURI("正式评审-"+filename+"报告-其他模式.")) + str;

                    var a = document.createElement('a');
                    a.id = 'tagOpenWin';
                    a.target = '_blank';
                    a.href = url;
                    document.body.appendChild(a);

                    var e = document.createEvent('MouseEvent');
                    e.initEvent('click', false, false);
                    document.getElementById("tagOpenWin").dispatchEvent(e);
                    $(a).remove();
                }else{
                    Window.alert("附件未找到！");
                    return false;
                }
            }
            
            // 返回列表
            $scope.cancel = function (){
                $location.path("/index/FormalReportList_new/" + $scope.tabindex);
            };

            $scope.initData();

        }]);
});
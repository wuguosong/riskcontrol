define(['app', 'Service'], function (app) {
    app
        .register.controller('preOtherReportCtrl', ['$http', '$scope', '$location', '$stateParams', 'BEWG_URL', 'Window',
        function ($http, $scope, $location, $stateParams, BEWG_URL, Window) {
            console.log('preOtherReport');

            $scope.pmodel =  $stateParams.pmodel;
            $scope.controller_val = $location.$$url.split("/")[1];
            $scope.paramId = $stateParams.id;
            $scope.paramAction = $stateParams.action;

            $scope.pre={};
            $scope.pre.reviewReport={};

            $scope._id = "";

            $scope.formatDate = function(){
                var date = new Date();
                var paddNum = function(num){
                    num += "";
                    return num.replace(/^(\d)$/,"0$1");
                }
                return date.getFullYear()+"-"+paddNum(date.getMonth()+1)+"-"+date.getDate()+" "+date.getHours()+":"+date.getMinutes()+":"+date.getSeconds();
            }

            $scope.FormatDate = function() {
                var date = new Date();
                var paddNum = function(num){
                    num += "";
                    return num.replace(/^(\d)$/,"0$1");
                }
                return date.getFullYear()+""+paddNum(date.getMonth()+1);
            }

            $scope.initData = function(){
                if($scope.paramAction == "Create"){
                    $scope.title = "投资评审报告-新增";
                    $scope.getPreProjectByID($scope.paramId);
                }else if($scope.paramAction == "Update"){
                    $scope.title = "投资评审报告-修改";
                    $scope.getByID($scope.paramId);
                }
            }

            $scope.getPreProjectByID = function(id){
                $http({
                    method:'post',
                    url: BEWG_URL.SelectPreProjectById,
                    data: $.param({"id":id})
                }).success(function(data){
                    $scope.pre  = data.result_data;

                    if(undefined == $scope.pre.reviewReport){
                        $scope.pre.reviewReport={};
                        $scope.pre.reviewReport.create_by = $scope.credentials.UUID;
                        $scope.pre.reviewReport.create_name = $scope.credentials.userName;
                        $scope.pre.reviewReport.models = $scope.pmodel;
                        $scope.pre.reviewReport.create_date =$scope.formatDate();
                    }

                    $scope.pre.controllerVal = $scope.controller_val;
                }).error(function(data,status,headers,config){
                    Window.alert(status);
                });
            }

            $scope.savePreReportById = function(callBack){

                var filep = $scope.pre.reviewReport.filePath;
                if(null==filep || ""==filep){
                    Window.alert("请上传附件");
                    return false;
                }
                show_Mask();
                var url_post;
                if ($scope._id != "") {
                    url_post = BEWG_URL.UpdateReportPre;
                    $scope.pre.reviewReport.update_date = $scope.formatDate();
                    $scope.pre.reviewReport.update_by = $scope.credentials.UUID;
                    $scope.pre.reviewReport.update_name = $scope.credentials.userName;
                }else{
                    url_post = BEWG_URL.SaveReportPre;
                }
                $http({
                    method:'post',
                    url: url_post,
                    data: $.param({"json":JSON.stringify($scope.pre)})
                }).success(function(data){
                    /*hide_Mask();*/
                    if(data.success){
                        $scope._id = data.result_data;
                        if(typeof callBack == 'function'){
                            callBack();
                        }else{
                            Window.alert(data.result_name);
                        }
                    }else {
                        alert(data.result_name);
                    }
                }).error(function(data,status,headers,config){
                    Window.alert(status);
                });
            }

            $scope.submitPreReport = function(){
                if( $scope.isPossible2Submit()){
                    $scope.savePreReportById(function(){
                        var filep = $scope.pre.reviewReport.filePath;
                        if(null == filep || "" == filep){
                            Window.alert("请上传附件");
                            return false;
                        }
                        show_Mask();
                        $http({
                            method:'post',
                            url: BEWG_URL.SubmitReportPre,
                            data: $.param({"businessid":$scope.paramId})
                        }).success(function(data){
                            hide_Mask();
                            if(data.success){
                                $('#savebtnreport').hide();
                                $('#submitbnt').hide();
                                Window.alert(data.result_name);
                            }else {
                                Window.alert(data.result_name);
                            }
                        }).error(function(data,status,headers,config){
                            Window.alert(status);
                        });
                    });
                }else{
                    Window.alert("请确保流程已结束!");
                }
            }

            $scope.getByID = function(id){
                $http({
                    method:'post',
                    url: BEWG_URL.SelectReportPreById,
                    data: $.param({"id":id})
                }).success(function(data){
                    $scope.pre  = data.result_data;
                    $scope._id = $scope.pre._id;
                }).error(function(data,status,headers,config){
                    Window.alert(status);
                });
            }

            //检查报告是否可提交
            $scope.isPossible2Submit = function(){
                var boolean = false;
                $.ajax({
                    url: BEWG_URL.IsSubmitReportPre,
                    type: "POST",
                    dataType: "json",
                    data: {"businessid":$scope.paramId},
                    async: false,
                    success: function(data){
                        if(data.result_data){
                            boolean = true;
                        }
                    }
                });
                return boolean;
            }

            $scope.errorAttach=[];
            $scope.uploadPreReportFile = function (file,errorFile, idx) {
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
                    var fileFolder = "preAuditReport/";
                    if($scope.paramAction == 'Create') {
                        if(undefined == $scope.pre.apply.projectNo){
                            Window.alert("请先选择项目然后上传附件");
                            return false;
                        }
                        var no = $scope.pre.apply.projectNo;
                        fileFolder = fileFolder +  $scope.FormatDate() + "/"+no;
                    }else{
                        var dates = $scope.pre.reviewReport.create_date;
                        if(undefined == dates || null == dates ){
                            dates = $scope.FormatDate();
                        }
                        var no = $scope.pre.apply.projectNo;
                        var strs = new Array(); //定义一数组
                        strs = dates.split("-"); //字符分割
                        dates = strs[0] + strs[1]; //分割后的字符输出
                        fileFolder = fileFolder + dates + "/" + no;
                    }
                    $scope.errorAttach[idx] = {msg:''};
                    Upload.upload({
                        url:srvUrl+'file/uploadFile.do',
                        data: {file: file, folder:fileFolder}
                    }).then(function (resp) {
                        var retData = resp.data.result_data[0];
                        $scope.pre.reviewReport.filePath = retData.filePath;
                        $scope.pre.reviewReport.fileName = retData.fileName;
                    }, function (resp) {
                        Window.alert(resp.status);
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
                    var url = srvUrl+"file/downloadFile.do?filepaths="+encodeURI(encodeURI(filePath))+"&filenames="+encodeURI(encodeURI("投资评审-"+filename+"报告.")) + str;

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

            $scope.initData();
            // 返回列表
            $scope.cancel = function (){
                $location.path("/index/PreAuditReportList/0");
            };
        }]);
});
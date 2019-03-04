define(['app', 'Service'], function (app) {
    app
        .register.controller('templateFlieInfoCtrl', ['$http', '$scope', '$location', '$stateParams', 'BEWG_URL', 'Window',
        function ($http, $scope, $location, $stateParams, BEWG_URL, Window) {
            console.log('templateFlieInfo');

            $scope.objId =  $stateParams.id;
            $scope.actionpam =$stateParams.action;

            $scope.templateFlieInfo = {};
            $scope.Info ={};
            $scope.formatDate = function() {
                var date = new Date();
                var paddNum = function(num){
                    num += "";
                    return num.replace(/^(\d)$/,"0$1");
                }
                return date.getFullYear()+""+paddNum(date.getMonth()+1)+""+date.getDate();
            }

            $scope.init = function(){
                if ($scope.actionpam == 'Create') {
                    $scope.titleName = "模板文件--新增";
                } else if ($scope.actionpam == 'Modify') {
                    $scope.titleName = "模板文件--修改";
                    $scope.getTemplateFlieInfo($scope.actionpam);
                }
            }

            // 查询模板文件详情
            $scope.getTemplateFlieInfo = function(id){

                $http({
                    method:'post',
                    url: BEWG_URL.SelectTemplateFileById,
                    data: $.param({"id":$scope.objId})
                }).success(function(result){
                    $scope.templateFlieInfo = result.result_data;
                    if($scope.templateFlieInfo.CONTENT != undefined || "" != $scope.templateFlieInfo.CONTENT){
                        $scope.templateFlieInfo.CONTENT = $scope.templateFlieInfo.CONTENT.replace(/<\/br>/g,'\n');
                    }
                }).error(function(data,status,headers,config){
                    Window.alert(status);
                });
            }

            //保存模板文件信息
            $scope.saveTemalateFile = function(flag){
                /*show_Mask();*/
                var url = "";
                if($scope.objId == "0"){
                    url = BEWG_URL.SaveTemplateFile;
                }else{
                    url = BEWG_URL.UpdateTemplateFile;
                }

                $http({
                    method:'post',
                    url: url,
                    data: $.param({"json":JSON.stringify($scope.templateFlieInfo)})
                }).success(function(result){
                    if(result.success){
                        $scope.templateFlieInfo.ID = result.result_data;
                        $scope.objId = result.result_data ;
                        if (typeof callBack == 'function') {
                            callBack();
                        } else {
                            if (flag != 1) {
                                Window.alert(result.result_name);
                            }
//    				$("#savebtn").hide();
                           /* hide_Mask();*/
                        }
                    }else{
                        if (flag != 1) {
                            Window.alert(result.result_name);
                        }
                        /*hide_Mask();*/
                    }
                }).error(function(data,status,headers,config){
                    Window.alert(status);
                    /*hide_Mask()*/;
                });
            }

            //提交模板文件信息
            $scope.submitTemalateFile = function(){
                $scope.saveTemalateFile(1);
                $http({
                    method:'post',
                    url: BEWG_URL.SubmitTemplateFile,
                    data: $.param({"id":$scope.objId})
                }).success(function(result){
                    if(result.success){
                        Window.alert(result.result_name);
                        $scope.hideOrDisableAttr();
                        /* hide_Mask();*/
                    }else{
                        Window.alert(result.result_name);
                        /* hide_Mask();*/
                    }
                }).error(function(data,status,headers,config){
                    Window.alert(status);
                    /*hide_Mask();*/
                });
            }

            //文件上传
            $scope.errorAttach=[];
            $scope.uploadNotificationAttachment = function (file,errorFile, idx) {
                if(errorFile && errorFile.length>0){
                    var errorMsg = fileErrorMsg(errorFile);
                    $scope.errorAttach[idx]={msg:errorMsg};
                }else if(file){
                    var fileFolder = "templateFile/";

                    if($scope.actionpam == 'Create'){
                        fileFolder = fileFolder + $scope.formatDate();
                    }else{
                        if($scope.notification != undefined){
                            fileFolder = $scope.notification.filePath;
                        }else{
                            fileFolder = fileFolder + $scope.formatDate();
                        }
                    }

                    $scope.errorAttach[idx]={msg:''};

                    Upload.upload({
                        url:BEWG_URL.UplodeTemplateFile,
                        data: {file: file, folder:fileFolder}
                    }).then(function (resp) {
                        $scope.templateFlieInfo.FILE_PATH = resp.data.result_data[0].filePath;
                        $scope.templateFlieInfo.FILE_NAME = resp.data.result_data[0].fileName;
                    }, function (resp) {
                        Window.alert(resp.status);
                    }, function (evt) {
                        var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                        $scope["progress"+idx]=progressPercentage == 100 ? "":progressPercentage+"%";
                    });
                }
            };

            //文件下载
            $scope.downLoadFile = function(filePath,fileName){
                window.location.href = srvUrl+"file/downloadFile.do?filepaths="+encodeURI(filePath)+"&filenames="+encodeURI(encodeURI(fileName));
            }

            $scope.hideOrDisableAttr = function(){
//	    	$('#title').attr("readonly","readonly");
//            $('#desc').attr("readonly","readonly");
//            $("#fileSelectDiv").hide();
//			$("#savebtn").hide();
                $("#submitbtn").hide();
            }

            //将模板文件内容的换行符替换为</br>
            $scope.keyUp = function(event){
                if(event.keyCode == 13){
                    var content = new Array();
                    content.push($scope.templateFlieInfo.CONTENT);
                    if(content.length > 0){
                        var contents = content[0];
                        $scope.Info.CONTENTS = contents.replace(/\n/g,"</br>");
                    }
                }
            }

            // 返回列表
            $scope.cancel = function (){
                $location.path("/index/templateFlieList");
            }
            $scope.init();
        }]);
});
define(['app', 'Service'], function (app) {
    app
        .register.controller('regulationsInfoCtrl', ['$http', '$scope', '$location', '$stateParams', 'BEWG_URL', 'Window',
        function ($http, $scope, $location, $stateParams, BEWG_URL, Window) {
            console.log('regulationsInfo');

            $scope.objId =  $stateParams.id;
            $scope.actionpam =$stateParams.action;

            $scope.regulationsInfo = {};
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
                    $scope.titleName = "规章制度--新增";
                } else if ($scope.actionpam == 'Modify') {
                    $scope.titleName = "规章制度--修改";
                    $scope.getRegulationsInfo($scope.actionpam);
                }
            }

            // 查询规章制度详情
            $scope.getRegulationsInfo = function(id){
                $http({
                    method:'post',
                    url: BEWG_URL.SelectRegulationById,
                    data: $.param({"id":$scope.objId})
                }).success(function(result){
                    $scope.regulationsInfo = result.result_data;
                    if($scope.regulationsInfo.CONTENT != undefined || "" != $scope.regulationsInfo.CONTENT){
                        $scope.regulationsInfo.CONTENT = $scope.regulationsInfo.CONTENT.replace(/<\/br>/g,'\n');
                    }
                }).error(function(data,status,headers,config){
                    Window.alert(status);
                });
            }

            //保存规章制度信息
            $scope.saveRegulationsInfo = function(flag){
                /*show_Mask();*/
                var url = "";
                if($scope.objId == "0"){
                    url = BEWG_URL.SaveRegulation;
                }else{
                    url = BEWG_URL.UpdateRegulation;
                }

                $http({
                    method:'post',
                    url: url,
                    data: $.param({"json":JSON.stringify($scope.regulationsInfo)})
                }).success(function(result){
                    if(result.success){
                        $scope.regulationsInfo.ID = result.result_data;
                        $scope.objId = result.result_data ;
                        if (typeof callBack == 'function') {
                            callBack();
                        } else {
                            if (flag != 1) {
                                Window.alert(result.result_name);
                            }

                            /*hide_Mask();*/
                        }
                    }else{
                        if (flag != 1) {
                            Window.alert(result.result_name);
                        }
                        /*hide_Mask();*/
                    }
                }).error(function(data,status,headers,config){
                    Window.alert(status);
                    /*hide_Mask();*/
                });
            };

            //提交规章制度信息
            $scope.submitRegulationsInfo = function(){
                $scope.saveRegulationsInfo(1);
                $http({
                    method:'post',
                    url: BEWG_URL.SubmitRegulation,
                    data: $.param({"id":$scope.objId})
                }).success(function(result){
                    if(result.success){
                        Window.alert(result.result_name);
                        $scope.hideOrDisableAttr();
                        /*hide_Mask();*/
                    }else{
                        Window.alert(result.result_name);
                        /*hide_Mask();*/
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
                    var fileFolder = "regulation/";

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
                        url: BEWG_URL.UplodeRegulation,
                        data: {file: file, folder:fileFolder}
                    }).then(function (resp) {
                        $scope.regulationsInfo.FILE_PATH = resp.data.result_data[0].filePath;
                        $scope.regulationsInfo.FILE_NAME = resp.data.result_data[0].fileName;
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

            //将规章制度;内容的换行符替换为</br>
            $scope.keyUp = function(event){
                if(event.keyCode == 13){
                    var content = new Array();
                    content.push($scope.regulationsInfo.CONTENT);
                    if(content.length > 0){
                        var contents = content[0];
                        $scope.Info.CONTENTS = contents.replace(/\n/g,"</br>");
                    }
                }
            }

            // 返回列表
            $scope.cancel = function (){
                $location.path("/index/regulationsList");
            }
            $scope.init();
        }]);
});
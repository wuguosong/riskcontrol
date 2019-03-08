define(['app', 'Service'], function (app) {
    app
        .register.controller('notificationInfoCtrl', ['$http', '$scope', '$location', '$stateParams', 'BEWG_URL', 'Window',
        function ($http, $scope, $location, $stateParams, BEWG_URL, Window) {
            console.log('notificationInfo');

            $scope.objId =  $stateParams.id;
            $scope.actionpam =$stateParams.action;

            $scope.notificationInfo = {};
            $scope.isNotExpire = true;

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
                    $scope.titleName = "公告平台--新增";
                } else if ($scope.actionpam == 'Modify') {
                    $scope.titleName = "公告平台--修改";
                    $scope.getNotificationInfo($scope.actionpam);
                }
                //查询公告类型
                $scope.queryDicCodeOfnotification();
            }

            // 查询公告信息详情
            $scope.getNotificationInfo = function(id){
                $http({
                    method:'post',
                    url: BEWG_URL.SelectNotificationById,
                    data: $.param({"id":$scope.objId})
                }).success(function(result){
                    $scope.notificationInfo = result.result_data;
                    if($scope.notificationInfo.CONTENT != undefined || "" != $scope.notificationInfo.CONTENT){
                        $scope.notificationInfo.CONTENT = $scope.notificationInfo.CONTENT.replace(/<\/br>/g,'\n');
                    }
                    $scope.isNotExpire = $scope.notificationInfo.EXPIRE_DATE == null || $scope.notificationInfo.EXPIRE_DATE == '';
                }).error(function(data,status,headers,config){
                    Window.alert(status);
                });
            }

            //保存公告信息
            $scope.saveNotification = function(flag){
                /*if(!$("#Notification").valid()) {
                    return false;
                }*/
                var is_not_expire = $scope.isNotExpire;
                if(null == $scope.notificationInfo.TYPE || "" == $scope.notificationInfo.TYPE){
                    Window.alert("请选择公告类型!");return false;
                }

                if((null == $scope.notificationInfo.EXPIRE_DATE || "" == $scope.notificationInfo.EXPIRE_DATE) && !is_not_expire){
                    Window.alert("请选择过期时间!");return false;
                }

                /*show_Mask();*/
                var url = "";

                if($scope.objId == "0"){
                    url = BEWG_URL.SaveNotification;
                }else{
                    url = BEWG_URL.UpdateNotification;
                }

                $scope.notificationInfo.CONTENT = $scope.notificationInfo.CONTENT.replace(/\n/g,"</br>");

                $http({
                    method:'post',
                    url: url,
                    data: $.param({"json":JSON.stringify($scope.notificationInfo)})
                }).success(function(result){
                    if(result.success){
                        $scope.notificationInfo.ID = result.result_data;
                        $scope.objId = result.result_data ;
                        if (typeof callBack == 'function') {
                            callBack();
                        } else {
                            if (flag != 1){
                                Window.alert(result.result_name);
                            }
//    				$("#savebtn").hide();
                            /*hide_Mask();*/
                        }
                    }else{
                        if (flag != 1){
                            Window.alert(result.result_name);
                        }
                        /*hide_Mask();*/
                    }
                }).error(function(data,status,headers,config){
                    Window.alert(status);
                    /*hide_Mask();*/
                });
            }

            //提交公告信息
            $scope.submitNotification = function(flag){
                $scope.saveNotification(1);
                    $http({
                        method:'post',
                        url: BEWG_URL.SubmitNotification,
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
                    var fileFolder = "notification/";

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
                        url:BEWG_URL.UploadFile,
                        data: {file: file, folder:fileFolder}
                    }).then(function (resp) {
                        $scope.notificationInfo.FILEPATH = resp.data.result_data[0].filePath;
                        $scope.notificationInfo.FILENAME = resp.data.result_data[0].fileName;
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
                $('#content-wrapper input').attr("readonly","readonly");
                $('textarea').attr("readonly","readonly");
                $("select").attr("disabled","disabled");
                $("#fileSelectDiv").hide();
                $("#savebtn").hide();
                $("#submitbtn").hide();
            }

            //查询公告类型
            $scope.queryDicCodeOfnotification = function(){
                $http({
                    method:'post',
                    url: BEWG_URL.SelectDictItemByCodeNotification,
                    data: $.param({"code":'TYGGLX'})
                }).success(function(result){
                    $scope.notificationsDicCode = result.result_data;
                }).error(function(data,status,headers,config){
                    Window.alert(status);
                });
            }

            // 返回列表
            $scope.cancel = function (){
                $location.path("/index/notificationList");
            }
            $scope.init();
        }]);
});
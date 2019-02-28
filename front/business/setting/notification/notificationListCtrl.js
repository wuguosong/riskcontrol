define(['app', 'Service'], function (app) {
    app
        .register.controller('notificationListCtrl', ['$http', '$scope', '$location', 'BEWG_URL', 'Window',
        function ($http, $scope, $location, BEWG_URL, Window) {
            console.log("notificationList");

            if(null != $scope.paginationConf && null == $scope.paginationConf.queryObj){
                $scope.paginationConf.queryObj = {}
                $scope.paginationConf.queryObj.type = "";
            }

            $scope.initData = function(){
                $scope.getNotificationsList();
                $scope.queryDicCodeOfnotification();
            }
            //查询平台公告列表
            $scope.getNotificationsList = function(){
                $http({
                    method:'post',
                    url: BEWG_URL.SelectAllNotification,
                    data: $.param({"page":JSON.stringify($scope.paginationConf)})
                }).success(function(result){
                    $scope.notifications = result.result_data.list;
                    $scope.paginationConf.totalItems = result.result_data.totalItems;
                }).error(function(data,status,headers,config){
                    Window.alert(status);
                });
            }

            //新增公告
            $scope.addNotification = function(){
                $location.path("/index/notificationInfo/Create/0");
            }

            //修改公告
            $scope.modifyNotification = function(){
                var chkObjs = $("input[type=checkbox][name=notifications_checkbox]:checked");
                if(chkObjs.length == 0){
                    Window.alert("请选择要修改的数据！");
                    return false;
                }

                if(chkObjs.length > 1){
                    Window.alert("请只选择一条数据进行修改!");
                    return false;
                }
                var idsStr = "";
                for(var i = 0; i < chkObjs.length; i++){
                    idsStr = idsStr + chkObjs[i].value + ",";
                }
                idsStr = idsStr.substring(0, idsStr.length - 1);

                /*$location.path("/notificationInfo/Modify/" + idsStr+"/"+$filter('encodeURI')('#/notificationList'));*/
                $location.path("/index/notificationInfo/Modify/" + idsStr);
            }

            //删除公告
            $scope.deleteNotifications = function() {
                var chkObjs = $("input[type=checkbox][name=notifications_checkbox]:checked");
                if (chkObjs.length == 0) {
                    Window.alert("请选择要删除的数据！");
                    return false;
                }

                Window.confirm('注意', "删除后不可恢复，确认删除吗？").result.then(function (btn) {
                    var idsStr = "";
                    for (var i = 0; i < chkObjs.length; i++) {
                        var chkValue = chkObjs[i].value.split("/");
                        var chkValue_len = chkValue.length;
                        idsStr = idsStr + chkValue[chkValue_len - 1] + ",";
                    }
                    idsStr = idsStr.substring(0, idsStr.length - 1);
                    $http({
                        method: 'post',
                        url: BEWG_URL.DeleteNotification,
                        data: $.param({"ids": idsStr})
                    }).success(function (data) {
                        if (data.success) {
                            $scope.getNotificationsList();
                        } else {
                            Window.alert(data.result_name);
                        }
                    }).error(function (data, status, headers, config) {
                        Window.alert(status);
                    });
                });
            };

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
            };

            // 跳转查看页面
            $scope.toNotification = function (id){
                $location.path("/index/notificationInfoView/View/"+id);
            };

            // 配置分页基本参数
            $scope.paginationConf = {
                currentPage: 1,
                itemsPerPage: 10,
                perPageOptions: [10, 20, 30, 40, 50]
            };

            // 通过$watch currentPage和itemperPage 当他们发生变化的时候，重新获取数据条目
            $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.getNotificationsList);

            $scope.initData();
        }]);
});

define(['app'], function (app) {
    app
        .register.controller('userListCtrl', ['$http', '$scope', '$location', 'BEWG_URL', 'Alert',
        function ($http, $scope, $location, BEWG_URL, Alert) {
            console.log("user");
            
            //查义所有的操作
            $scope.queryList=function(){
                if($scope.paginationConf.currentPage === 1){
                    //如果当前页为第一页，则此时请求$scope.ListAll();不会触发两次操作
                    $scope.ListAll();
                }else{
                    //如果当前页不是第一页，则可以通过修改currentPage来触发$scope.ListAll();
                    $scope.paginationConf.currentPage = 1;
                }
            };

            //查看操作
            $scope.View=function(uuid){
                $location.path("/userInfo/View/"+uuid);
            };
            $scope.viewUser=function ()
            {
                var chk_list=document.getElementsByName("checkbox");
                var uid = "",num=0;
                for(var i=0;i<chk_list.length;i++)
                {
                    if(chk_list[i].checked)
                    {
                        num++;
                        uid = uid+','+chk_list[i].value;
                    }
                }
                if(uid!=''){
                    uid=uid.substring(1,uid.length);
                }else {
                    Alert.alert("请选择其中一条数据查看！");
                    return false;
                }
                if(num>1){
                    Alert.alert("只能选择其中一条数据查看！");
                    return false;
                }else{
                    location.href="#/index/userInfo/View/"+uid;
                }
            }

            $scope.ListAll=function(){
                $scope.paginationConf.queryObj = $scope.queryObj;
                $http({
                    method:'post',
                    url: BEWG_URL.srvUrl + "user/getAll.do",
                    data:$.param({"page":JSON.stringify($scope.paginationConf)})
                }).success(function(data){
                    console.log(data.result_data.list);
                    if(data.success){
                        // 变更分页的总数
                        $scope.test = data.result_name;
                        $scope.sysUser  = data.result_data.list;
                        $scope.paginationConf.totalItems = data.result_data.totalItems;
                    }else{
                        Alert.alert(data.result_name);
                    }
                });
            };

            // 跳转分配角色页面
            $scope.toUserRole = function(uuid){
                $location.path("/index/userRole/"+uuid);
            }

            // 配置分页基本参数
            $scope.paginationConf = {
                currentPage: 1,
                itemsPerPage: 10,
                perPageOptions: [10, 20, 30, 40, 50]
            };
            $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.ListAll);
        }]);
});
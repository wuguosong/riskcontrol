define(['app', 'Service', 'dtree', 'dtree-menu'], function (app) {
    app
        .register.controller('sysFunListCtrl', ['$http', '$scope', '$location', '$stateParams', 'BEWG_URL', 'Window',
        function ($http, $scope, $location, $stateParams, BEWG_URL, Window) {
            console.log('sysFunList');

            //查义所有的操作
            var id=0;
            $scope.queryList=function(){

                if($scope.paginationConf.currentPage === 1){
                    //如果当前页为第一页，则此时请求$scope.ListAll();不会触发两次操作
                    $scope.ListAll();
                }else{
                    //如果当前页不是第一页，则可以通过修改currentPage来触发$scope.ListAll();
                    $scope.paginationConf.currentPage = 1;
                }
            };

            $scope._toback = function(){
                $scope.ListAll();
            }
            //查看操作
            $scope.View=function(func_id){
                $location.path("/index/sysFunInfo/View/"+func_id);
            };
            //新建操作
            $scope.Create=function(id){

                $location.path("/index/sysFunInfo/Create/"+id);
            };
            $scope.updateSysFunction=function ()
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
                }
                if(num==0){
                    Window.alert("请选择编辑的数据！");
                    //$location.path("/sysFunList/0");
                    $scope.queryList();
                }else if(num>1){
                    Window.alert("只能选择其中一条数据进行编辑！");
                    return false;
                }else{
                    $location.path("/index/sysFunInfo/Update/"+uid);
                }
            }
            $scope.viewSysFunction=function ()
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
                }
                if(num==0){
                    Window.alert("请选择查看的数据！");
                    $scope.queryList();
                }else if (num>1){
                    Window.alert("只能选择其中一条数据查看！");
                    return false;
                }else{
                    location.href="#/index/sysFunInfo/View/"+uid;
                }
            }

            $scope.deleteSysFunction=function(){
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
                }
                if(num==0){
                    Window.alert("未选择删除的数据！");
                    return false;
                    /* }else if(num>1) {
                          alert("请选择其中一条数据进行删除！");*/
                } else{
                    $scope.httpData(BEWG_URL.SelectSysFunCode, uid).success(
                        function (data, status, headers, config) {
                            if (data.result_data == false) {
                                Window.alert('子目录未删除！');
                                return false;
                            } else {
                                Window.confirm('注意', "确定要删除？").result.then(function (btn) {
                                    $scope.httpData(BEWG_URL.DeleteSysFunById, uid).success(
                                        function (data, status, headers, config) {
                                            $scope.queryList();
                                            $scope.getOrgAll2();
                                        }
                                    ).error(function (data, status, headers, config) {
                                        Window.alert(status);
                                    });
                                });
                            }
                        }
                    ).error(function (data, status, headers, config) {
                        alert(status);
                    });
                }
            }

            $scope.updateSysFunctionState=function(state){
                Window.confirm('注意', "确定要启用？").result.then(function (btn){
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
                    }
                    if(num==0){
                        Window.alert("请选择其中一条或多条数据启用！");
                        return false;
                    }else{
                        statevar={"func_id": uid,"state":state};
                        $scope.httpData(BEWG_URL.UpdateSysFunStateById,statevar).success(function(data){
                            $scope.getAll();
                        });
                        $scope.httpData(BEWG_URL.UpdateSysFunStateById,statevar).success(
                            function (data, status, headers, config) {
                                $scope.getAll();

                            }
                        ).error(function (data, status, headers, config) {
                            alert(status);
                        });
                    }
                });
            }
            var orgs=$stateParams.funcId;
            $scope.ListAll=function(){
                var func_name=$("#FUNC_NAME").val();
                var url=$("#URL").val();
                $scope.conf={currentPage:$scope.paginationConf.currentPage,pageSize:$scope.paginationConf.itemsPerPage,queryObj:{'ORGID':orgs,'FUNC_NAME':func_name,'URL':url}};

                var cc= orgs;
                $scope.httpData(BEWG_URL.SelectAllSysFun,$scope.conf).success(function(data){
                    // 变更分页的总数
                    $scope.sysfun  = data.result_data.list;
                    $scope.paginationConf.totalItems = data.result_data.totalItems;
                });
            };

            // 配置分页基本参数
            $scope.paginationConf = {
                currentPage: 1,
                itemsPerPage: 10,
                perPageOptions: [10, 20, 30, 40, 50]
            };
            // 通过$watch currentPage和itemperPage 当他们一变化的时候，重新获取数据条目
            $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage',  $scope.ListAll);
            //获取组织结构角色
            $scope.getOrgAll2=function(){
                $scope.httpData(BEWG_URL.SelectAllOrgSysFun,{}).success(function(data){
                    var  orgArr  = data.result_data;
                    dtrees2 = new dtreeMenu('dtrees2');
                    dtrees2.add(0,-1,'系统菜单管理',"#/index/sysFunList/0");
                    for(var i =0;i<orgArr.length;i++){
                        var orgLink="#/index/sysFunList/"+orgArr[i].FUNC_ID;
                        dtrees2.add(orgArr[i].FUNC_ID ,orgArr[i].FUNC_PID ,orgArr[i].FUNC_NAME,orgLink);
                    }
                    document.getElementById("treeID2").innerHTML=dtrees2;
                });
            };

            //手动初始化，目的先显示组织树
            angular.element(document).ready(function() {
                $scope.getOrgAll2();
            });
        }]);
});
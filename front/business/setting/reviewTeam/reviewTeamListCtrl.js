define(['app', 'Service'], function (app) {
    app
        .register.controller('reviewTeamListCtrl', ['$http', '$scope', '$location', 'BEWG_URL', 'Window',
        function ($http, $scope, $location, BEWG_URL, Window) {
            console.log("reviewTeamList");
;
            $scope.queryList=function(){

                if($scope.paginationConf.currentPage === 1){
                    //如果当前页为第一页，则此时请求$scope.ListAll();不会触发两次操作
                    $scope.ListAll();
                }else{
                    //如果当前页不是第一页，则可以通过修改currentPage来触发$scope.ListAll();
                    $scope.paginationConf.currentPage = 1;
                }
            };
            $scope.ListAll=function(){
                $scope.conf={currentPage:$scope.paginationConf.currentPage,pageSize:$scope.paginationConf.itemsPerPage,
                    queryObj:{'TEAM_NAME':$scope.TEAM_NAME,'TEAM_LEADER':$scope.TEAM_LEADER}};
                $scope.httpData(BEWG_URL.SelectAllReviewTeams,$scope.conf).success(function(data){
                    // 变更分页的总数
                    $scope.team  = data.result_data.list;
                    $scope.paginationConf.totalItems = data.result_data.totalItems;
                });
            };
            $scope.viewAll= function(id){
                $scope.httpData(BEWG_URL.SelectAllReviewTeamsForView,id).success(function(data){
                    // 变更分页的总数
                    $scope.teamUserList  = data.result_data;
                });
            }
            //新建操作
            $scope.Create=function(){
                $location.path("/index/ReviewTeamInfo/Create/0");
            };
            $scope.updateTeam=function (){
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
                    $scope.queryList();
                }else if(num>1){
                    Window.alert("只能选择其中一条数据进行编辑！");
                    return false;
                }else{
                    $location.path("/index/ReviewTeamInfo/Update/"+uid);
                }
            }

            $scope.deleteTeam=function(){
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
                    Window.confirm('注意', "删除后不可恢复，确认删除吗？").result.then(function (btn) {
                        $scope.httpData(BEWG_URL.DelectReviewTeam, uid).success(
                            function (data, status, headers, config) {
                                $scope.ListAll();
                            }
                        ).error(function (data, status, headers, config) {
                            alert(status);
                        });
                    });
                }

            };

            // 配置分页基本参数
            $scope.paginationConf = {
                currentPage: 1,
                itemsPerPage: 10,
                perPageOptions: [10, 20, 30, 40, 50]
            };
            // 通过$watch currentPage和itemperPage 当他们一变化的时候，重新获取数据条目
            $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage',  $scope.ListAll);
        }]);
});

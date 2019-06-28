/**
 * Created by gl on 2016/8/4.
 */

ctmApp.register.controller('ReviewTeamList', ['$http','$scope','$location','$routeParams',function ($http,$scope,$location,$routeParams)
{

//初始化
   /* var team_name=$routeParams.TEAM_NAME;
    var uuid=$routeParams.UUID;
    $scope.TEAM_NAME=team_name;
    $scope.UUID=uuid;*/
    //查义所有的操作
    //var id=0;
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
        var  url = 'rcm/Pteam/getAll';
        $scope.httpData(url,$scope.conf).success(function(data){
            // 变更分页的总数
            $scope.team  = data.result_data.list;
            $scope.paginationConf.totalItems = data.result_data.totalItems;
        });
    };
    $scope.viewAll= function(id){
        var  url = 'rcm/Pteam/viewAll';
        $scope.httpData(url,id).success(function(data){
            // 变更分页的总数
            $scope.teamUserList  = data.result_data;
        });
    }
    //新建操作
    $scope.Create=function(id){

        $location.path("/ReviewTeamAdd/Create/"+id);
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
    	$.alert("请选择编辑的数据！");
        $scope.queryList();
    }else if(num>1){
    	$.alert("只能选择其中一条数据进行编辑！");
        return false;
    }else{
        $location.path("/ReviewTeamAdd/Update/"+uid);
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
        	$.alert("未选择删除的数据！");
            return false;
            /* }else if(num>1) {
             alert("请选择其中一条数据进行删除！");*/
        } else{
            $.confirm("确定要删除？", function() {
                var aMethed = 'rcm/Pteam/deleteTeamByID';
                $scope.httpData(aMethed, uid).success(
                    function (data, status, headers, config) {
                        $location.path("/ReviewTeamList/" );
                    }
                ).error(function (data, status, headers, config) {
                    alert(status);
                });
            });
        }

    }


    // 配置分页基本参数
    $scope.paginationConf = {
        currentPage: 1,
        itemsPerPage: 10,
        perPageOptions: [10, 20, 30, 40, 50]
    };
    // 通过$watch currentPage和itemperPage 当他们一变化的时候，重新获取数据条目
    $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage',  $scope.ListAll);

}]);
function callbackV(code,name){

}
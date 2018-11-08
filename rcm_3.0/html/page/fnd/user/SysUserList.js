/**
 * Created by gl on 2016/8/4.
 */

ctmApp.register.controller('SysUserList', ['$http','$scope','$location','$routeParams',function ($http,$scope,$location,$routeParams)
  {
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
    //新建操作
//    $scope.Create=function(id){
//        $location.path("/SysUserAdd/Create/"+id);
//    };
    //查看操作
    $scope.View=function(uuid){
        $location.path("/SysUserAdd/View/"+uuid);
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
            	$.alert("请选择其中一条数据查看！");
                return false;
            }
            if(num>1){
            	$.alert("只能选择其中一条数据查看！");
                return false;
            }else{
                location.href="#/SysUserAdd/View/"+uid;
            }
        }
//        $scope.deltteUser=function(){
//            $.confirm("确定要删除？", function(){
//            var chk_list=document.getElementsByName("checkbox");
//            var uid = "",num=0;
//            for(var i=0;i<chk_list.length;i++)
//            {
//                if(chk_list[i].checked)
//                {
//                    num++;
//                    uid = uid+','+chk_list[i].value;
//                }
//            }
//            if(uid!=''){
//                uid=uid.substring(1,uid.length);
//            }
//            if(num==0){
//            	$.alert("请选择其中一条数据进行删除！");
//                return false;
//            }else{
//                var aMethed = 'fnd/SysUser/delectSysUserByID';
//                $scope.httpData(aMethed,uid).success(
//                    function (data, status, headers, config) {
//                        $scope.queryList();
//                    }
//                ).error(function (data, status, headers, config) {
//                    alert(status);
//                });
//            }
//            });
//        }
      $scope.ListAll=function(){
          $scope.paginationConf.queryObj = $scope.queryObj;
          $http({
  			method:'post',  
  		    url: srvUrl + "user/getAll.do",
  		    data:$.param({"page":JSON.stringify($scope.paginationConf)})
  		}).success(function(data){
        	  if(data.success){
        		  // 变更分页的总数
        		  $scope.sysUser  = data.result_data.list;
        		  $scope.paginationConf.totalItems = data.result_data.totalItems;
        	  }else{
        		  $.alert(data.result_name);
        	  }
          });
      };
      
      $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage + queryObj.ORGID', $scope.ListAll);
}]);
/**
 * Created by gl on 2016/8/4.
 */

ctmApp.register.controller('DataOptionList', ['$http','$scope','$location','$routeParams','$timeout',function ($http,$scope,$location,$routeParams,$timeout)
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
    $scope.fk_id = $routeParams.UUID;
    $scope.ListAll=function(){
    	 /*var item_name=$("#ITEM_NAME").val();
        var item_code=$("#ITEM_CODE").val();*/
        var queryInfo = {currentPage:$scope.paginationConf.currentPage,pageSize:$scope.paginationConf.itemsPerPage,queryObj:{'ITEM_NAME':$scope.ITEM_NAME,'ITEM_CODE':$scope.ITEM_CODE,"FK_UUID":$scope.fk_id}};
         $http({
 			method:'post',  
 		    url:srvUrl+"dict/queryDictItemByDictTypeAndPage.do", 
 		    data: $.param({"page":JSON.stringify(queryInfo)})
 		}).success(function(data){
 			if(data.success){
 				$scope.item  = data.result_data.list;
 	            $scope.paginationConf.totalItems = data.result_data.totalItems;
 	           //初始化提示信息框
 				 $timeout(function(){
 					angular.element(document).ready(function() {
 						var dd = $("[data-toggle='tooltip']");
 						dd.tooltip();
 					 });
 				 },10);
 			}else{
 				$.alert(data.result_name);
 			}
 		});
     };

    //新建操作
    $scope.Create=function(id){
        $location.path("/DataOptionEdit/Create/"+id+"/"+$scope.fk_id);
    };
    
    $scope.updateItem=function (id)
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
        if (num == 0) {
        	$.alert("未选择编辑的数据！");
            return false;
        }else if (num > 1) {
        	$.alert("请选择一条数据编辑！");
            return false;
        }else{
            $location.path("/DataOptionEdit/Update/"+uid+"/"+$scope.fk_id);
        }
    }
    $scope.deleteItem=function(){

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
            	$.alert("未选中删除数据！");
                return false;
            }else{
                $.confirm("确定要删除？", function() {
                    $http({
            			method:'post',  
            		    url:srvUrl+"dict/deleteDictItemByIds.do", 
            		    data: $.param({"uuids":uid})
            		}).success(function(data){
            			if(data.success){
            				 $location.path("/DataOptionList/" + $scope.fk_id + "/");
            			}else{
            				$.alert(data.result_name);
            			}
            		});
                });
            }

    }
    $scope.paginationConf = {
        currentPage: 1,
        itemsPerPage: 10,
        perPageOptions: [10, 20, 30, 40, 50]
    };
    $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage',  $scope.ListAll);

}]);
function callbackV(code,name){

}
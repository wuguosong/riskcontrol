/**
 * Created by gl on 2016/8/4.
 */

ctmApp.register.controller('DataDictionaryList', ['$http','$scope','$location','$routeParams','$timeout',function ($http,$scope,$location,$routeParams,$timeout)
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

    $scope.ListAll=function(){
       // var dictionary_code=$("#DICTIONARY_CODE").val();
       // var dictionary_name=$("#DICTIONARY_NAME").val();
       var queryInfo = {currentPage:$scope.paginationConf.currentPage,pageSize:$scope.paginationConf.itemsPerPage,queryObj:{'DICTIONARY_CODE':$scope.DICTIONARY_CODE,'DICTIONARY_NAME':$scope.DICTIONARY_NAME}};
        $http({
			method:'post',  
		    url:srvUrl+"dict/queryDictTypeByPage.do", 
		    data: $.param({"page":JSON.stringify(queryInfo)})
		}).success(function(data){
			if(data.success){
		       $scope.dictionary  = data.result_data.list;
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
        $location.path("/DataDictionaryEdit/Create/"+id);
    };
    
    $scope.updateDictionary=function ()
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
            $location.path("/DataDictionaryEdit/Update/"+uid);
        }
    }
    $scope.back=function(){
        $scope.queryList();
    }
    $scope.deleteDictionary=function(){
            var chk_list = document.getElementsByName("checkbox");
            var uid = "", num = 0;
            for (var i = 0; i < chk_list.length; i++) {
                if (chk_list[i].checked) {
                    num++;
                    uid = uid + ',' + chk_list[i].value;
                }
            }
            if (uid != '') {
                uid = uid.substring(1, uid.length);
            }
            if (num == 0) {
            	$.alert("未选择删除的数据！");
                return false;
            }else {
                $http({
        			method:'post',  
        		    url:srvUrl+"dict/queryDictItemByDictTypeIds.do", 
        		    data: $.param({"uuids":uid})
        		}).success(function(data){
        			if(data.success){
        				$.confirm("确定要删除？", function() {
        					$http({
                    			method:'post',  
                    		    url:srvUrl+"dict/deleteDictTypeByIds.do", 
                    		    data: $.param({"uuids":uid})
                    		}).success(function(data){
                    			if(data.success){
                    				 $scope.queryList();
                    			}else{
                    				$.alert(data.result_name);
                    			}
                    		});
        				});
        			}else{
        				$.alert(data.result_name);
        			}
        		});
            }
    }

    // 配置分页基本参数
    $scope.paginationConf = {
        currentPage: 1,
        itemsPerPage: 10,
        perPageOptions: [10, 20, 30, 40, 50]
    };
    $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.ListAll);


}]);
function callbackV(code,name){

}
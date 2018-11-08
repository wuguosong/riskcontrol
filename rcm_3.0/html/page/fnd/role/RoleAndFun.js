/**
 * Created by gl on 2016/8/16.
 */
ctmApp.register.controller('RoleAndFun', ['$http','$scope','$location','$routeParams', function ($http,$scope,$location,$routeParams) {
		$scope.roleId = $routeParams.roleId;
		$scope.roleCode = $routeParams.roleCode;
        $scope.oldUrl = $routeParams.url;
        $scope.sysRole = {};
        //保存操作
        $scope.save = function () {
            var str="",num=0;;
            $("input[type='checkbox']:checked").each(function() {
                str+= $(this).attr("value")+",";
                num++;
            });
            if(str!=''){
                str=str.substring(0,str.length-1);
            }
            $http({
    			method:'post',  
    		    url:srvUrl+"role/createOrUpdateRoleAndFunc.do",
    		    data: $.param({"UUID": str, "ROLE_ID": $scope.roleId,'ROLE_CODE':$scope.roleCode})
    		}).success(function(result){
    			$.alert("保存成功！");
    		});
        };
        //根据roleId获取菜单列表
        $scope.getRoleAndFunc = function () {
            $("#RoleTreeID a").attr("class","");
            dtrees3.setDefaultIsCheck();
            $http({
    			method:'post',  
    		    url:srvUrl+"role/getRoleAndFunc.do",
    		    data: $.param({"roleId":$scope.roleId})
    		}).success(function(result){
                var funcs = result.result_data;
                var strs='';
                for(var j=0;j<funcs.length;j++){
                    strs=strs+","+funcs[j];
                }
                strs=strs.substring(1,strs.length);
                if(null!=strs && ''!=strs && strs.length>0){
                    dtrees3.setDefaultCheck(strs);
                }
                $("#"+$scope.roleId).attr("class","selected");
            });
        };

        //获取菜单结构
        $scope.getFunc=function(){
        	$http({
    			method:'post',  
    		    url:srvUrl+'role/queryFunc.do'
    		}).success(function(result){
    			 var orgArr = result.result_data;
                 dtrees3 = new dTree('dtrees3');
                 dtrees3.add(0,-1,'菜单管理');
                 for(var i =0;i<orgArr.length;i++){
                   dtrees3.add(orgArr[i].FUNC_ID ,orgArr[i].FUNC_PID,orgArr[i].FUNC_NAME);
                 }
                 dtrees3.config.check=true;
                 document.getElementById("treeID3").innerHTML=dtrees3;
                 $scope.getRoleAndFunc();
    		});
        };

        //初始化
      //getUrlParam("userCode");
        $scope.getFunc();
}]);
function callbackV(id,name){

}
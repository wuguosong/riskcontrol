ctmApp.register.controller('RoleAndOrg', ['$http','$scope','$location','$routeParams', function ($http,$scope,$location,$routeParams) {
		$scope.roleId = $routeParams.roleId;
		$scope.roleCode = $routeParams.roleCode;
        $scope.oldUrl = $routeParams.url;
        $scope.sysRole = {};

        // 获取系统当前时间
        $scope.getDate = function () {
            var myDate = new Date();
            //获取当前年
            var year = myDate.getFullYear();
            //获取当前月
            var month = myDate.getMonth() + 1;
            //获取当前日
            var date = myDate.getDate();
            var h = myDate.getHours(); //获取当前小时数(0-23)
            var m = myDate.getMinutes(); //获取当前分钟数(0-59)
            var s = myDate.getSeconds();
            var now = year + '-' + month + "-" + date + " " + h + ':' + m + ":" + s;
            return now;
        };

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
    		    url:srvUrl+"role/createOrUpdateRoleAndOrg.do",
    		    data: $.param({"UUID": str, "ROLE_ID": $scope.roleId, "CREATE_DATE": $scope.getDate(), 'ROLE_CODE':$scope.roleCode})
    		}).success(function(result){
    			$.alert("保存成功！");
    		});
        };
        //根据roleId获取菜单列表
        $scope.getRoleAndOrg = function () {
            $("#RoleTreeID a").attr("class","");
            dtrees3.setDefaultIsCheck();
            $http({
    			method:'post',
    		    url:srvUrl+"role/getRoleAndOrg.do",
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
        $scope.getOrg=function(){
        	$http({
    			method:'post',
    		    url:srvUrl+'role/queryOrg.do'
    		}).success(function(result){
    			 var orgArr = result.result_data;
                 dtrees3 = new dTree('dtrees3');
                 dtrees3.add(0,-1,'组织管理');
                 for(var i =0;i<orgArr.length;i++){
                   dtrees3.add(orgArr[i].ORGPKVALUE ,orgArr[i].PARENTPKVALUE ,orgArr[i].NAME,orgArr[i].ID);
                 }
                 dtrees3.config.check=true;
                 document.getElementById("treeID3").innerHTML=dtrees3;
                 $scope.getRoleAndOrg();
    		});
        };

        //初始化
      //getUrlParam("userCode");
        $scope.getOrg();
}]);
function callbackV(id,name){

}
/**
 * Created by gl on 2016/8/4.
 */

ctmApp.register.controller('SysUserAdd', ['$http','$scope','$location','$routeParams',
    function ($http,$scope,$location,$routeParams) {
        var uid=0;
        $scope.sysUser = {};
        //保存操作
        $scope.save = function () {
            var postObj;
            var url ='';
            if(typeof $scope.sysUser.UUID!=null  && $scope.sysUser.UUID!=''  && $scope.sysUser.UUID!="undefined"){
                url = 'fnd/SysUser/updateSysUser';
                postObj=$scope.httpData(url,$scope.sysUser);
            }else{
                url = 'fnd/SysUser/createSysUser';
                postObj=$scope.httpData(url,$scope.sysUser);
            }
            postObj.success(function(data){
                    if(data.result_code === 'S'){
                       // location.href="#/SysUserList/0";
                        $location.path("/SysUserList/0");
                    }else{
                        alert(data.result_name);
                    }
                }
            )
        };
        //验证操作
        $scope.ValidateUser = function () {
        };
        //取消操作
        $scope.cancel = function () {
            $location.path("/SysUserList/0");
        };
        //查询一个用户
        $scope.getSysUserByID = function (uuid) {
//            var aMethed = 'user/getSysUserByID.do';
//            $scope.httpData(aMethed,uuid).success(
//                function (data, status, headers, config) {
//                    $scope.sysUser = data.result_data.userDate;
//                    $scope.userPotition = data.result_data.postition;
//                    uid=$scope.sysUser.ORG_CODE;
//                }
//            ).error(function (data, status, headers, config) {
//                alert(status);
//            });
            $http({
      			method:'post',  
      		    url: srvUrl + "user/getSysUserByID.do",
      		    data:$.param({"id":uuid})
      		}).success(function(data){
            	if(data.success){
            		$scope.sysUser = data.result_data.userDate;
            		$scope.userPotition = data.result_data.postition;
            		uid=$scope.sysUser.ORG_CODE;
            	}else{
            		$.alert(data.result_name);
            	}
            });
        };
        //选择组织
        $scope.SelOrg = function(){
            var options = {
                "backdrop" : "static"
            }
            $('#basicModal').modal(options);
        };
        //初始化
        var uuid = $routeParams.uuid;//getUrlParam("userCode");
        //定义窗口action
        var action =$routeParams.action; //getUrlParam('action');
        if (action == 'Update') {
            //初始化状态
            $scope.getSysUserByID(uuid);
            //编辑状态则初始化下拉列表内容
        } else if (action == 'View') {
            $scope.getSysUserByID(uuid);
            $('#savebtn').hide();
            $('#cancelbtn').hide();
            $('#viewlbtn').show();
            //设置控件只读
        } else if (action == 'Create') {
            uid=uuid;
            //取默认值
            $scope.sysUser.TYPE_CODE = '';//
            $scope.sysUser.UUID = '';//
            $scope.sysUser.NAME = '';
            $scope.sysUser.CODE = '';//去数据库里查询
            $scope.sysUser.SEX_CODE = '';
            $scope.sysUser.DEPT_CODE = '';//去数据库里查询
            $scope.sysUser.ACCOUNT = '';
            $scope.sysUser.EMAIL = '';
            $scope.sysUser.ORG_CODE = uid;//去数据库里查询
            $scope.sysUser.STATE = '';
            $scope.sysUser.ID_CARD = '';//去数据库里查询
        };

        /*//获取组织结构角色
        $scope.getOrgAll=function(){
            var  url ='fnd/Group/getOrg';
         $scope.httpData(url,{}).success(function(data){
                $scope.groupAll= data.result_data;
                var  orgArr  = data.result_data;
                dtrees = new dTree('dtrees');
                for(var i =0;i<orgArr.length;i++){
                    var orgLink="#/SysUserList/"+orgArr[i].ORG_PK_VALUE;
                    dtrees.add(orgArr[i].ORG_PK_VALUE ,orgArr[i].PARENT_PK_VALUE ,orgArr[i].NAME,orgLink,orgArr[i].NAME);
                }
                document.getElementById("treeID").innerHTML=dtrees;
            });
        };
        //手动初始化，目的先显示组织树
        angular.element(document).ready(function() {
            $scope.getOrgAll();
        });*/
    }]);

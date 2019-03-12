/**
 * Created by Administrator on 2016/8/11.
 */
/**
 * Created by gl on 2016/8/4.
 */

ctmApp.register.controller('DataOptionEdit', ['$http','$scope','$location','$routeParams',
    function ($http,$scope,$location,$routeParams) {
        var uid=0;
        $scope.item = {};
        //保存操作
        $scope.save = function () {
        	if($scope.item.CUST_NUMBER01 == null || $scope.item.CUST_NUMBER01 == ""){
    			$.alert("序号必填!");return false;
    		}
        	if($scope.item.ITEM_NAME == null || $scope.item.ITEM_NAME == ""){
    			$.alert("字典项名称必填!");return false;
    		}
        	if($scope.item.ITEM_CODE == null || $scope.item.ITEM_CODE == ""){
    			$.alert("字典项编码必填!");return false;
    		}
            $http({
    			method:'post', 
    			url: srvUrl + "dict/saveOrUpdateDictItem.do",
    			data: $.param({"json": JSON.stringify($scope.item)})
    		}).success(function (data) {
    			if (data.success) {
    				$.alert("保存成功!");
    			}
    			else{
    				$.alert(data.result_name);
    			}
    		});
        };
        //查询一个用户
        $scope.getOptionByID = function (uuid) {
            $http({
    			method:'post', 
    			url: srvUrl + "dict/getDictItemById.do",
    			data: $.param({"uuid": uuid})
    		}).success(function (data) {
    			if (data.success) {
    				$scope.item = data.result_data;
    			}
    			else{
    				$.alert(data.result_name);
    			}
    		});
        };

        //取消操作
        $scope.cancel = function () {
            $location.path("/DataOptionList/"+fk_id);
        };


        //初始化
        var uuid = $routeParams.uuid;//getUrlParam("userCode");

        var fk_id = $routeParams.fk_Id;

        //定义窗口action
        var action =$routeParams.action; //getUrlParam('action');
        if (action == 'Update') {
            //初始化状态f
            $scope.getOptionByID(uuid);
            //编辑状态则初始化下拉列表内容
        }else if (action == 'View') {
            $scope.getOptionByID(uuid);
            $('#savebtn').hide();
            $('#cancelbtn').hide();
            $('#viewlbtn').show();
            //设置控件只读
        }else if (action == 'Create') {
        	uid=uuid;
            //取默认值
            $scope.item.ITEM_NAME = '';
            $scope.item.ITEM_CODE = '';
            $scope.item.IS_ENABLED = '1';
            $scope.item.UUID = '';
            $scope.item.FK_DICTIONARY_UUID = fk_id;
            $scope.item.BUSINESS_TYPE='';
            $http({
    			method:'post', 
    			url: srvUrl + "dict/getDictItemLastIndexByDictType.do",
    			data: $.param({"FK_UUID": fk_id})
    		}).success(function (data) {
    			 if (data.success) {
    				 $scope.item.CUST_NUMBER01 = data.result_data;
     			}
    		});
        };
    }]);

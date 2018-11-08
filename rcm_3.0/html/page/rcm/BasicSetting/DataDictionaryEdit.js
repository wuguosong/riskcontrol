/**
 * Created by Administrator on 2016/8/11.
 */
/**
 * Created by gl on 2016/8/4.
 */

ctmApp.register.controller('DataDictionaryEdit', ['$http','$scope','$location','$routeParams',
    function ($http,$scope,$location,$routeParams) {
        var uid=0;
        $scope.dictionary = {};
        //保存操作
        $scope.save = function () {
        	if($scope.dictionary.DICTIONARY_NAME == null || $scope.dictionary.DICTIONARY_NAME == ""){
    			$.alert("字典名称必填!");return false;
    		}
        	if($scope.dictionary.DICTIONARY_CODE == null || $scope.dictionary.DICTIONARY_CODE == ""){
    			$.alert("字典编码必填!");return false;
    		}
    		$http({
    			method:'post', 
    			url: srvUrl + "dict/saveOrUpdateDictType.do",
    			data: $.param({"json": JSON.stringify($scope.dictionary)})
    		}).success(function (data) {
    			if (data.success) {
    				$.alert("保存成功!");
    			}
    			else{
    				$.alert(data.result_name);
    			}
    		});
        };
        
        //回显数据
        $scope.getDictionaryByID = function (uuid) {
            $http({
    			method:'post', 
    			url: srvUrl + "dict/getDictTypeById.do",
    			data: $.param({"uuid": uuid})
    		}).success(function (data) {
    			if (data.success) {
    				$scope.dictionary = data.result_data;
    			}
    			else{
    				$.alert(data.result_name);
    			}
    		});
        };

        //初始化
        var uuid = $routeParams.uuid;//getUrlParam("userCode");

        //定义窗口action
        var action =$routeParams.action; //getUrlParam('action');
        if (action == 'Update') {
            //初始化状态f
            $scope.getDictionaryByID(uuid);
            //编辑状态则初始化下拉列表内容
        }else if (action == 'View') {
            $scope.getDictionaryByID(uuid);
            $('#savebtn').hide();
            $('#cancelbtn').hide();
            $('#viewlbtn').show();
            //设置控件只读
        }else if (action == 'Create') {
            uid=uuid;
            //取默认值
            $scope.dictionary.DICTIONARY_NAME = '';
            $scope.dictionary.DICTIONARY_CODE = '';
            $scope.dictionary.DICTIONARY_DESC = '';
            $scope.dictionary.UUID = '';
        };
    }]);

/**
 * Created by Administrator on 2016/8/11.
 */
/**
 * Created by gl on 2016/8/4.
 */

ctmApp.register.controller('BusinessModelEdit', ['$http','$scope','$location','$routeParams',
    function ($http,$scope,$location,$routeParams) {
        var uid=0;
        $scope.item = {};
        //保存操作
        $scope.save = function () {
            var postObj;
            var url ='';
            if(typeof $scope.item.UUID!=null  && $scope.item.UUID!=''  && $scope.item.UUID!="undefined"){
                url = 'rcm/Business/updateBusiness';
                postObj=$scope.httpData(url,$scope.item);
            }else{
                url = 'rcm/DataOption/CreateBusiness';
                postObj=$scope.httpData(url,$scope.item);
            }
            postObj.success(function(data){
                    if(data.result_code === 'S'){
                        $location.path("/BusinessModelList/");
                    }else{
                    	$.alert("编码不能重复或必填项不能为空!");
                    }
                }
            )
        };
        //查询一个用户
        $scope.getOptionByID = function (uuid) {
            var aMethed = 'rcm/DataOption/getOptionByID';
            $scope.httpData(aMethed,uuid).success(
                function (data, status, headers, config) {
                    $scope.item = data.result_data;
                }
            ).error(function (data, status, headers, config) {
                alert(status);
            });
        };

        //取消操作
        $scope.cancel = function () {
            $location.path("/DataOptionList/");
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
            $scope.item.IS_ENABLED = '';
            $scope.item.UUID = '';
        };
    }]);

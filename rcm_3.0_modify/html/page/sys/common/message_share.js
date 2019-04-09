/**
 * Created by Administrator on 2019/4/8 0008.
 */
ctmApp.register.controller('shareMessageCtrl', ['$http', '$scope', '$location', '$routeParams', 'Upload', '$timeout', '$filter',
    function ($http, $scope, $location, $routeParams, Upload, $timeout, $filter) {
        $scope.url = $routeParams.url;
        $scope.message = {};
        $scope._messages_array_ = [];
        $scope._query_messages_list_ = function (procInstId, parentId) {
            $http({
                method: 'post',
                url: srvUrl + 'message/queryMessagesList.do',
                data: $.param({
                    'procInstId': procInstId,
                    'parentId': parentId
                }),
                headers: {'Content-Type': 'application/x-www-form-urlencoded'}
            }).success(function (data) {
                $scope._messages_array_ = data;
            });
        };
        // 用户的其他操作,预留方法
        $scope._user_about_click_ = function(_source_, _user_id_, _user_name_){
            console.log("查看用户：" + _source_ + " " + _user_id_ + " " + _user_name_);
        };
        // 检测滑动
        $scope._check_collapse_event_ = function(_obj_, _idx_){
            var _class = $('#_message_panel_body_' + _idx_).attr("class");
            var _span = '';
            if("panel-collapse collapse in" == _class){
                _span += _obj_['createdName'] + '&nbsp;';
                _span += _obj_['messageDate'] + '&nbsp;';
                if(!isEmpty(_obj_.messageContent)){
                    if(_obj_.messageContent.length >= 10){
                        _span += _obj_.messageContent.substr(0, 11) + '......';
                    }else{
                        _span += _obj_.messageContent;
                    }
                }
            }
            $('#_span_hide_content_' + _idx_).html(_span);
        };
        $scope.initMessage = function(messageId){
            $http({
                method: 'post',
                url: srvUrl + 'message/get.do',
                data: $.param({
                    'messageId': messageId
                }),
                headers: {'Content-Type': 'application/x-www-form-urlencoded'}
            }).success(function (data) {
                $scope.message = data['result_data'];
                if(!isEmpty($scope.message)){
                    $scope._query_messages_list_($scope.message.procInstId, 0);
                }
            });
        };
        $scope.initMessage($routeParams.id);
    }]);
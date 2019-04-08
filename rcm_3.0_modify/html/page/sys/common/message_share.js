/**
 * Created by Administrator on 2019/4/8 0008.
 */
ctmApp.register.controller('shareMessageCtrl', ['$http', '$scope', '$location', '$routeParams', 'Upload', '$timeout', '$filter',
    function ($http, $scope, $location, $routeParams, Upload, $timeout, $filter) {
        $scope.url = $routeParams.url;
        $scope.message = {};
        $scope._messages_array_ = [];
        $scope.queryMessagesList = function (procInstId, parentId) {
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
                $scope.queryMessagesList($scope.message.procInstId, 0);
            });
        };
        $scope.initMessage($routeParams.id);
    }]);
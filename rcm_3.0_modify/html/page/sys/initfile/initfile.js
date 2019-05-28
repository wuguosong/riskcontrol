ctmApp.register.controller('initFileCtrl', ['$http', '$scope', '$location', '$routeParams', 'Upload', '$timeout', '$filter', '$window',
    function ($http, $scope, $location, $routeParams, Upload, $timeout, $filter, $window) {
        $scope.processKey = 'preReview';
        $scope.businessKey = '5ceb51067ad1b44900b2b2f2';
        $scope._breaks = [];
        $scope.selectBreaks = function () {
            if($scope._breaks.length <= 0){
                $scope._breaks = _getProcessUserTaskStates($scope.processKey, $scope.businessKey);
            }
            console.log($scope._breaks);
        };
        console.log(wf_getCurrentTask($scope.processKey, $scope.businessKey));
    }]);
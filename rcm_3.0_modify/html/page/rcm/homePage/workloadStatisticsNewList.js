/********
 * 工作量统计
 *********/
ctmApp.register.controller('workloadStatisticsNewList', ['$http', '$scope', '$location', function ($http, $scope, $location) {

    $scope.initializeCount = function () {

        // 判断首页快捷菜单栏显示
        var currentUser = $scope.credentials;
        console.log($scope.credentials);
        for (var i = 0; i < currentUser.roles.length; i++) {
            console.log(currentUser.roles[i].CODE);
            if (currentUser.roles[i].CODE == 'ASSESSOR_CONFIG') {
                $scope.showWork = 'review';
            } else if (currentUser.roles[i].CODE == 'LEGAL_CONFIG'){
                $scope.showWork = 'legal';
            } else if (currentUser.userName == '魏伟') {
                $scope.showWork = 'review';
            } else if (currentUser.userName == '陈国宁') {
                $scope.showWork = 'legal';
            }
        }
        console.log($scope.showWork);

        $http({
            method: 'post',
            url: srvUrl + "deptwork/getAllStaffWork.do"
        }).success(function (result) {
            $scope.allReviewStaffWork = result.result_data.allStaffWork;

            console.log(result);
        });
        $http({
            method: 'post',
            url: srvUrl + "deptwork/getAllLegalStaffWork.do"
        }).success(function (result) {
            $scope.allLegalStaffWork = result.result_data.allStaffWork;
        });

    };

    //初始化
    angular.element(document).ready(function () {
        $scope.initializeCount();
    });

    $scope.queryFindReview = function (staffWork) {
        $scope.staff = staffWork;
        $http({
            method: 'post',
            url: srvUrl + "deptwork/getOneStaffWork.do",
            data: $.param({
                "TEAM_MEMBER_ID": staffWork.TEAM_MEMBER_ID,
            })
        }).success(function (result) {
            $scope.projects = result.result_data;
        });
    }

    $scope.queryFindLegal = function (staffWork) {
        $scope.staff = staffWork;
        $http({
            method: 'post',
            url: srvUrl + "deptwork/getOneLegalStaffWork.do",
            data: $.param({
                "TEAM_MEMBER_ID": staffWork.TEAM_MEMBER_ID,
            })
        }).success(function (result) {
            $scope.projects = result.result_data;
        });
    }

}]);
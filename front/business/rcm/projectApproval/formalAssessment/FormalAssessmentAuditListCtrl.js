/**
 * Created by yaphet on 2017/5/22.
 */
define(['app', 'Service', 'ng-route', 'require','datepicher'], function (app) {
    app.register.controller('FormalAssessmentAuditListCtrl', ['$http', '$scope', '$stateParams', '$location', 'CommonService', 'Window', function ($http, $scope, $stateParams, $location, CommonService, Window) {
        $scope.buttonControl = SRV_URL == "http://riskcontrol.bewg.net.cn/rcm-rest/";
        // 配置分页基本参数
        $scope.paginationConfes = {
            currentPage: 1,
            itemsPerPage: 10,
            perPageOptions: [10, 20, 30, 40, 50]
        };
        $scope.paginationConf = {
            currentPage: 1,
            itemsPerPage: 10,
            perPageOptions: [10, 20, 30, 40, 50]
        };
        var tabIndex = $stateParams.tabIndex;

        //控制具体显示那个tab标签页  012
        $('#myTab li:eq(' + tabIndex + ') a').tab('show');
        $scope.tabIndex = tabIndex;

        $scope.initData = function () {
            $scope.getWaitFormalAssessmentList();
            $scope.getAuditedFormalAssessmentList();
        }
        $scope.getWaitFormalAssessmentList = function () {
            $http({
                method: 'post',
                url: SRV_URL + "formalAssessmentAudit/queryWaitList.do",
                data: $.param({"page": JSON.stringify($scope.paginationConf)})
            }).success(function (result) {
                $scope.waitFormalAssessmentList = result.result_data.list;
                $scope.paginationConf.totalItems = result.result_data.totalItems;
            });
        }
        $scope.getAuditedFormalAssessmentList = function () {
            $http({
                method: 'post',
                url: SRV_URL + "formalAssessmentAudit/queryAuditedList.do",
                data: $.param({"page": JSON.stringify($scope.paginationConfes)})
            }).success(function (result) {
                $scope.auditedFormalAssessmentList = result.result_data.list;
                $scope.paginationConfes.totalItems = result.result_data.totalItems;
            });
        }

        //查义所有的操作
        $scope.order = function (o, v) {
            if (o == "time") {
                $scope.orderby = v;
                $scope.orderbystate = null;
                if (v == "asc") {
                    $("#orderasc").addClass("cur");
                    $("#orderdesc").removeClass("cur");
                } else {
                    $("#orderdesc").addClass("cur");
                    $("#orderasc").removeClass("cur");
                }
            } else {
                $scope.orderbystate = v;
                $scope.orderby = null;
                if (v == "asc") {
                    $("#orderascstate").addClass("cur");
                    $("#orderdescstate").removeClass("cur");
                } else {
                    $("#orderdescstate").addClass("cur");
                    $("#orderascstate").removeClass("cur");
                }
            }
            $scope.ListAll();
        }
        $scope.delete = function () {

            var chk_list = document.getElementsByName("checkbox");
            var uid = "", num = 0;
            for (var i = 0; i < chk_list.length; i++) {
                if (chk_list[i].checked) {
                    num++;
                    uid = uid + ',' + chk_list[i].value;
                }
            }
            if (uid != '') {
                uid = uid.substring(1, uid.length);
            }
            if (num == 0) {
                Window.alert("请选择其中一条或多条数据删除！");
                return false;
            }
            var obj = {"_id": uid};
            var aMethed = 'formalAssessment/ProjectFormalReview/delete';
            $scope.httpData(aMethed, obj).success(
                function (data, status, headers, config) {
                    if (data.result_code == "R") {
                        Window.alert('报告未删除！删除申请单之前请先删除该项目下的报告单！');
                        return false;
                    } else {
                        Window.confirm('注意', "确定要删除？").result.then(function (btn) {
                            $scope.httpData(aMethed, obj).success(
                                function (data, status, headers, config) {
                                    $scope.ListAll();
                                }
                            ).error(function (data, status, headers, config) {
                                Window.alert(status);
                            });
                        });
                    }
                }
            ).error(function (data, status, headers, config) {
                Window.alert(status);
            });
        };
        /*  $scope.httpData(aMethed, obj).success(
         function (data, status, headers, config) {
         $scope.ListAll();
         }
         ).error(function (data, status, headers, config) {
         alert(status);
         });*/
        $scope.update = function (state) {
            var chk_list = document.getElementsByName("checkbox");
            var uid = "", num = 0;
            for (var i = 0; i < chk_list.length; i++) {
                if (chk_list[i].checked) {
                    num++;
                    uid = uid + ',' + chk_list[i].value;
                }
            }
            if (uid != '') {
                uid = uid.substring(1, uid.length);
            }
            if (num == 0) {
                Window.alert("请选择其中一条或多条数据编辑！");
                return false;
            }
            if (num > 1) {
                Window.alert("只能选择其中一条数据进行编辑！");
                return false;
            } else {
                $location.path("/ProjectFormalReviewDetail/Update/" + uid);
            }
        }
        $scope.import = function () {
            var url = 'formalAssessment/ProjectFormalReview/importFormalAssessmentReport';
            $scope.httpData(url, $scope.credentials.UUID).success(function (data) {
                if (data.result_code == "S") {
                    var files = data.result_data.filePath;
                    var fileName = data.result_data.fileName;
                    window.location.href = SRV_URL + "common/RcmFile/downLoad?filePath=" + encodeURI(files) + "&fileName=" + encodeURI(fileName);
                    ;
                } else {
                    Window.alert("生成失败");
                }
            });
        }
        // 通过$watch currentPage和itemperPage 当他们一变化的时候，重新获取数据条目
        $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.getWaitFormalAssessmentList);
        $scope.$watch('paginationConfes.currentPage + paginationConfes.itemsPerPage', $scope.getAuditedFormalAssessmentList);

        $scope.initData();
    }]);
});

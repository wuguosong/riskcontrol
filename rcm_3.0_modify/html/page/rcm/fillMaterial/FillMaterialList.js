ctmApp.register.controller('fillMaterialList', ['$http', '$scope', '$location', '$routeParams', '$filter',
    function ($http, $scope, $location, $routeParams, $filter) {
        $scope.tabIndex = $routeParams.tabIndex;
        $scope.initData = function () {
            $scope.queryNoSubmitList();
            $scope.querySubmitList();
        }
        //查询待填写资料
        $scope.queryNoSubmitList = function () {
            if ($scope.paginationConf.queryObj == null || $scope.paginationConf.queryObj == '') {
                $scope.paginationConf.queryObj = {};
            }
            $scope.paginationConf.queryObj.userId = $scope.credentials.UUID;
            $http({
                method: 'post',
                url: srvUrl + "fillMaterials/queryNoSubmitList.do",
                data: $.param({
                    "page": JSON.stringify($scope.paginationConf),
                    "json": JSON.stringify($scope.paginationConf.queryObj)
                })
            }).success(function (result) {
                $scope.noSubmitList = result.result_data.list;
                $scope.paginationConf.totalItems = result.result_data.totalItems;
            });
        };

        //查询待填写资料
        $scope.querySubmitList = function () {
            if ($scope.paginationConfes.queryObj == null || $scope.paginationConfes.queryObj == '') {
                $scope.paginationConfes.queryObj = {};
            }
            $scope.paginationConfes.queryObj.userId = $scope.credentials.UUID;
            $http({
                method: 'post',
                url: srvUrl + "fillMaterials/querySubmitList.do",
                data: $.param({
                    "page": JSON.stringify($scope.paginationConfes),
                    "json": JSON.stringify($scope.paginationConfes.queryObj)
                })
            }).success(function (result) {
                $scope.submitList = result.result_data.list;
                $scope.paginationConfes.totalItems = result.result_data.totalItems;
            });
        };


        // 通过$watch currentPage和itemperPage 当他们一变化的时候，重新获取数据条目
        $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.queryNoSubmitList);

        $scope.$watch('paginationConfes.currentPage + paginationConfes.itemsPerPage', $scope.querySubmitList);

        $scope.r = {};
        //新建评审报告
        $scope.openRFIReport = function (noSubmit) {
            $scope.toCreateReport = noSubmit;
            $scope.r.pmodel = "FormalReviewReport/Create";
        };

        //编辑评审报告
        $scope.createRFIReport = function (model, uuid) {
            var ind = model.lastIndexOf("/");
            var modelAction = model.substring(ind + 1, model.length);
            if (modelAction == 'Update') {
                $.alert("请选择项目模式!");
                return false;
            } else if (uuid == null || uuid == "") {
                $.alert("请选择一个项目!");
                return false;
            } else {
                var routePath = model.substring(0, ind);
                $('#addModal').modal('hide');
                $location.path("/" + routePath + "/0/Create/" + uuid + "@2/" + $filter('encodeURI')('#/IndividualTable'));
            }
        }

        /**
         * 查询项目部分信息，查看是新项目还是老项目，决定跳转路径
         * */
        $scope.getInfo = function (id) {
            $http({
                method: 'post',
                url: srvUrl + "formalReport/findFormalAndReport.do",
                data: $.param({"projectFormalId": id})
            }).success(function (data) {
                $scope.projectSummary = data.result_data.summary;
                $scope.stage = data.result_data.stage;

                console.log($scope.stage)
                var path = $filter('encodeURI')('#/IndividualTable');
                /*if ($scope.projectSummary == null || $scope.projectSummary == undefined){
                    $location.path("/FormalBiddingInfo_view/"+ id + "/" + path);
                } else {
                    $location.path("/FormalBiddingInfoPreview/"+ id + "/" + path + "/3");
                }*/
                // #/FormalBiddingInfo/5afcc2e6ddd03412cebef6e5@2/JTI1MjMlMkZGb3JtYWxCaWRkaW5nSW5mb0xpc3QlMkYw/0
                if ($scope.projectSummary == null || $scope.projectSummary == undefined) {
                    // $location.path("/FormalBiddingInfoPreviewOld/" + id + "/" + path);
                    $location.path("/FormalBiddingInfoPreviewOld/"+ id + "/" + path + "/3");
                } else if ($scope.stage == 7) {
                    $location.path("/FormalBiddingInfoPreview/" + id + "/" + path + "/3");
                } else {
                    $location.path("/FormalBiddingInfo/" + id + "/" + path + "/3");
                }
            }).error(function (data, status, header, config) {
                $.alert(status);
            });
        }

        $scope.x = {};
        $scope.openPREReport = function (noSubmit) {
            $scope.preProjectName = noSubmit;
            $scope.x.pmodel = "normal";
        };

        //新建投资评审报告
        $scope.forPreReport = function (model, uuid, comId) {
            if (model == null || model == "") {
                $.alert("请选择项目模式!");
                return false;
            } else if (uuid == null || uuid == "") {
                $.alert("请选择项目!");
                return false;
            } else {
                $("#addModal2").modal('hide');
                var routePath = "";
                if (model == "normal") {
                    routePath = "PreNormalReport";
                }

                if (model == "other") {
                    routePath = "PreOtherReport";
                }
                $location.path("/" + routePath + "/" + model + "/Create/" + uuid + "/" + $filter('encodeURI')('#/IndividualTable'));
            }
        }

        $scope.openRBIMeeting = function (noSubmit) {
            $scope.businessId = noSubmit.BUSINESSID;
        }
        $scope.mettingSummary = "";
        $scope.mettingSubmit = function () {
            if ($scope.mettingSummary == null || $scope.mettingSummary == "") {
                $.alert("会议纪要不得为空！");
                return false;
            }
            //show_Mask();
            //保存附件到mongo
            $http({
                method: 'post',
                url: srvUrl + "bulletinInfo/saveMettingSummary.do",
                data: $.param({
                    "businessId": $scope.businessId,
                    "mettingSummaryInfo": $scope.mettingSummary
                })
            }).success(function (result) {
                $('#submitModal').modal('hide');
                $.alert(result.result_name);
                $scope.$parent.initDefaultData();
                $scope.mettingSummary = "";
            });
        };
        $scope.cancel = function () {
            $scope.mettingSummary = "";
            $scope.mettingSummarys = "";
        }

        $scope.queryRBIMeeting = function (submit) {
            $http({
                method: 'post',
                url: srvUrl + "bulletinInfo/queryRBIMettingSummarys.do",
                data: $.param({
                    "businessId": submit.BUSINESSID,
                })
            }).success(function (result) {
                $scope.mettingSummarys = result.result_data.mettingSummary;
            });
        }

    }]);
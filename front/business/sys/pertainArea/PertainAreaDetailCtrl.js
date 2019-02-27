define(['app', 'Service'], function (app) {
    app.register.controller('PertainAreaDetailCtrl', ['$http', '$scope', '$location', '$stateParams','Window',
        function ($http, $scope, $location, $stateParams, Window) {
            $scope.oldUrl = $stateParams.url;
            $scope.initData = function () {
                if ('create' == $stateParams.action) {
                    $scope.action = "新增";
                } else if ('update' == $stateParams.action) {
                    $scope.action = "修改";
                    $scope.userId = $stateParams.id;
                    //查询数据
                    $scope.getById();
                }
                $scope.getServiceType();
            }

            //显示遮罩层
            $scope.show_mask = function () {
                $("#mask_").css("height", $(document).height());
                $("#mask_").css("line-height", $(document).height() + "px");
                $("#mask_").css("width", $(document).width());
                $("#mask_").show();
            };

            //隐藏遮罩层
            $scope.hide_mask = function () {
                $("#mask_").hide();
            };

            $scope.getServiceType = function () {
                var url =  'common/commonMethod/selectDataDictionByCode';
                $scope.httpData(url, "14").success(function (data) {
                    if (data.result_code == 'S') {
                        $scope.serviceTypeList = data.result_data;
                    } else {
                        Window.alert(data.result_name);
                    }
                });
            }

            $scope.columns = [{
                "fieldName": "单位名称",
                "fieldValue": "NAME"
            }, {
                "fieldName": "上级单位名称",
                "fieldValue": "PNAME"
            }];

            $scope.orgMappedKeyValue = {"nameField": "NAME", "valueField": "ORGPKVALUE"};
            $scope.directPersonMapped = {"nameField": "DIRECTPERSON_NAME", "valueField": "DIRECTPERSON_ID"};

            $scope.getById = function () {
                $scope.show_mask();
                $http({
                    method: 'post',
                    url: SRV_URL + "pertainArea/getByUserId.do",
                    data: $.param({"userId": $scope.userId})
                }).success(function (result) {
                    $scope.hide_mask();
                    if (result.success) {
                        $scope.leader.user = {
                            "NAME": result.result_data.LEADERNAME,
                            "VALUE": result.result_data.LEADERID
                        };
                        $scope.leader.org = {
                            "NAME": result.result_data.ORGNAME,
                            "ORGPKVALUE": result.result_data.ORGPKVALUE
                        };
                        $scope.leader.TYPE = result.result_data.TYPE;
                        $scope.leader.SERVICETYPE = result.result_data.SERVICETYPE;
                        $scope.leader.oldUserId = result.result_data.LEADERID;
                    } else {
                        Window.alert(result.result_name);
                    }
                });
            }
            $scope.selectAll = function () {
                if ($("#selectAll").attr("checked")) {
                    $(":checkbox[name='serviceType']").attr("checked", 1);
                } else {
                    $(":checkbox[name='serviceType']").attr("checked", false);
                }
            }
            $scope.save = function () {
                //数据验证
                if ($scope.leader.org.ORGPKVALUE == null || $scope.leader.org.ORGPKVALUE == '') {
                    Window.alert("请选择所属单位!");
                    return;
                }
                if ($scope.leader.user.VALUE == null || $scope.leader.user.VALUE == '') {
                    Window.alert("请选择负责人!");
                    return;
                }
                if ($scope.leader.TYPE == null || $scope.leader.TYPE == '') {
                    Window.alert("请选择人员职务!");
                    return;
                }

                if ($scope.leader.TYPE == '1') {
                    var serviceTypeIds = "";
                    $(":checkbox[name='serviceType']:checked").each(function () {
                        serviceTypeIds += "," + this.value;
                    });
                    $scope.leader.SERVICETYPE = serviceTypeIds.substring(1);

                    if ($scope.leader.SERVICETYPE.indexOf('all') >= 0) $scope.leader.SERVICETYPE = 'all';
                    if ($scope.leader.SERVICETYPE == null || $scope.leader.SERVICETYPE == '') {
                        Window.alert("请选择负责的项目类型!");
                        return;
                    }
                }
                $scope.show_mask();
                var url;
                if ($stateParams.action == "create") {
                    url = "pertainArea/save.do";
                } else if ($stateParams.action == "update") {
                    url = "pertainArea/updateByUserId.do";
                }

                if ($scope.leader.TYPE != '1') {
                    $scope.leader.SERVICETYPE = null;
                }

                $http({
                    method: 'post',
                    url: SRV_URL + url,
                    data: $.param({
                        "json": JSON.stringify({
                                "orgId": $scope.leader.org.ORGPKVALUE,
                                "userId": $scope.leader.user.VALUE,
                                "type": $scope.leader.TYPE,
                                "oldUserId": $scope.leader.oldUserId,
                                "serviceType": $scope.leader.SERVICETYPE
                            }
                        )
                    })
                }).success(function (result) {
                    $scope.hide_mask();
                    if (result.success) {
                        Window.alert(result.result_name);
                        $location.path(PATH_URL_INDEX + "/PertainAreaLeaderList");
                    } else {
                        Window.alert(result.result_name);
                    }
                });

            }

            $scope.initData();

            //取消操作
            $scope.cancel = function () {
                $location.path(PATH_URL_INDEX + "/PertainAreaLeaderList");
            };
        }]);
});



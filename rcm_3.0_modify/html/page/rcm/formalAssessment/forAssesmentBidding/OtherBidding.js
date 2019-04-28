ctmApp.register.controller('OtherBidding', ['$http', '$scope', '$location', '$routeParams', 'Upload', '$timeout',
    function ($http, $scope, $location, $routeParams, Upload, $timeout) {
        //初始化
        $scope.oldUrl = $routeParams.url;
        $scope.tabIndex = $routeParams.tabIndex;
        $scope.controller_val = $location.$$url.split("/")[1];
        var complexId = $routeParams.id;
        $scope.paramId = complexId;
        var params = complexId.split("@");
        if (null != params[1] && "" != params[1]) {
            $scope.flag = params[1];
        }
        if (null != params[2] && "" != params[2] && null != params[3] && "" != params[3] && null != params[4] && "" != params[4] && null != params[5] && "" != params[5] && null != params[6] && "" != params[6]) {
            $scope.reportReturnId = params[2] + "@" + params[3] + "@" + params[4] + "@" + params[5] + "@" + params[6];
        }
        var objId = params[0];
        $scope.formalReport = {};

        var action = $routeParams.action;
        $scope.initData = function () {
            if (action == "Create") {
                $scope.title = "正式评审决策会材料-新增";
                $scope.getProjectFormalReviewByID(objId);
                $scope.formalReport.create_by = $scope.credentials.UUID;
                $scope.formalReport.create_name = $scope.credentials.userName;
                $("#wordbtn").hide();
            } else if (action == "Update") {
                $scope.title = "正式评审决策会材料-修改";
                $scope.getByID(objId);
            } else if (action == "View") {
                $scope.title = "正式评审决策会材料-查看";
                $scope.getByID(objId);
                $("#savebtn").hide();
                $("#btnfile").attr("disabled", "disabled");
            }
        };

        $scope.getByID = function (id) {
            $http({
                method: 'post',
                url: srvUrl + 'formalReport/getByID.do',
                data: $.param({"id": id})
            }).success(function (data) {
                $scope.formalReport = data.result_data;
                if (action == "View") {
                    $('button').attr("disabled", "disabled");
                    $("#submitbnt").attr("disabled", false);
                    $('#wordbtn').attr("disabled", false);
                }
            }).error(function (data, status, headers, config) {
                $.alert(status);
            });
        }

        $scope.getProjectFormalReviewByID=function(id){
            $http({
                method:'post',
                url:srvUrl+"formalReport/getProjectFormalReviewByID.do",
                data: $.param({"id":id})
            }).success(function(data){
                $scope.pfr  = data.result_data;
                $scope.formalReport.projectFormalId=$scope.pfr.id;
                $scope.formalReport.projectName=$scope.pfr.apply.projectName;
                $scope.formalReport.projectNo=$scope.pfr.apply.projectNo;
                if(null!=$scope.pfr.apply.reportingUnit){
                    $scope.formalReport.reportingUnit=$scope.pfr.apply.reportingUnit.name;
                }
                var ptNameArr=[],pmNameArr=[];
                var pt=$scope.pfr.apply.projectType;
                if(null!=pt && pt.length>0){
                    for(var i=0;i<pt.length;i++){
                        ptNameArr.push(pt[i].VALUE);
                    }
                    $scope.formalReport.projectTypeName=ptNameArr.join(",");
                }
                $scope.formalReport.controllerVal = $scope.controller_val;
            }).error(function(data,status,headers,config){
                $.alert(status);
            });
        }

        $scope.initData();

        $scope.saveOnly = function () {
            var data = $scope.dataForSave();
            $scope.saveOrSubmit(data, "so");
        };

        $scope.submitSave = function () {
            var data = $scope.dataForSave();
            $scope.saveOrSubmit(data, "ss");
        };

        $scope.dataForSave = function () {
            $scope.projectSummary.projectFormalId = $scope.formalReport.projectFormalId; // 正式评审项目id
            $scope.formalReport.projectSummary = $scope.projectSummary;
            return $scope.formalReport;
        };

        $scope.saveOrSubmit = function (pData, method) {
            console.log(pData);
            show_Mask();
            $http({
                method: 'post',
                url: srvUrl + "formalReport/addPptecision.do",
                data: $.param({"json": angular.toJson(pData), "method": method})
            }).success(function (data) {
                hide_Mask();
                if (data.success) {
                    var alertData = "";
                    if (method == "so") {
                        alertData = "保存成功!";
                    } else if (method == "ss") {
                        if (data.result_data) {
                            alertData = "提交成功!";
                            $location.path("/FormalBiddingInfoPreview/" + $scope.businessId + "/" + $filter('encodeURI') + "/2");
                        } else {
                            $.alert("请确保参会信息已填写完毕!");
                            return false;
                        }
                    }
                    $.alert(alertData);
                } else {
                    $.alert(data.result_name);
                }
            }).error(function (data, status, headers, config) {
                $.alert(status);
            });
        }


    }]);

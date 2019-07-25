ctmApp.register.controller('OtherBidding', ['$http', '$scope', '$location', '$routeParams', 'Upload', '$timeout', '$filter',
    function ($http, $scope, $location, $routeParams, Upload, $timeout, $filter) {
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
        $scope.paramBusinessId = params[0];
        var objId = params[0];
        $scope.formalReport = {};
        $scope.projectSummary = {};

        var action = $routeParams.action;
        $scope.returnStatus = false;

        $scope.$watch('returnStatus', function() {
            if ($scope.returnStatus) {
                $scope.dic2=[];
                $scope.dic2.nameValue=[{name:'A级（困难评审)',value:1},{name:'B级（中级评审)',value:2},{name:'C级（简单评审)',value:3}]
            }
        });

        // 定义投资类型
        $scope.INVESTMENT_TYPE = [];
        $scope.getSelectINVESTMENT_TYPE = function (typeCode) {
            var url = 'common/commonMethod/selectDataDictionByCode';
            $scope.httpData(url, typeCode).success(function (data) {
                if (data.result_code == 'S') {
                    $scope.INVESTMENT_TYPE = data.result_data;
                } else {
                    alert(data.result_name);
                }
            });
            /*$scope.INVESTMENT_TYPE = selectDocItem(typeCode);*/
        }

        //处理附件列表
        $scope.reduceAttachment = function(attachment, id){
            $scope.newAttachment = attach_list("formalReview", id, "formalAssessmentInfo").result_data;
            for(var i in attachment){
                var file = attachment[i];
                for (var j in $scope.newAttachment){
                    if (file.fileId == $scope.newAttachment[j].fileid){
                        $scope.newAttachment[j].fileName = file.fileName;
                        $scope.newAttachment[j].type = file.type;
                        $scope.newAttachment[j].itemType = file.itemType;
                        $scope.newAttachment[j].programmed = file.programmed;
                        $scope.newAttachment[j].approved = file.approved;
                        $scope.newAttachment[j].lastUpdateBy = file.lastUpdateBy;
                        $scope.newAttachment[j].lastUpdateData = file.lastUpdateData;
                        $scope.newAttachment[j].isMettingAttachment = file.isMettingAttachment;
                        break;
                    }
                }
            }
        };

        // 初始化提交决策会材料数据
        $scope.initUpdate = function (projectFormalId) {
            $http({
                method: 'post',
                url: srvUrl + "formalReport/findFormalAndReport.do",
                data: $.param({"projectFormalId": projectFormalId})
            }).success(function (data) {
                if(data.result_data.Report) {
                    $scope.formalReport = data.result_data.Report;
                } else {
                    $scope.formalReport = {};
                }
                $scope.pfr = data.result_data.Formal;
                if (isEmpty(data.result_data.MeetInfo)){
                    $scope.meetInfo = {};
                } else {
                    $scope.meetInfo = data.result_data.MeetInfo;
                }

                // 处理附件需要的数据
                $scope.projectModel = angular.copy($scope.pfr.apply.projectModel);
                $scope.serviceType = angular.copy($scope.pfr.apply.serviceType);
                // 处理附件
                $scope.reduceAttachment(data.result_data.Formal.attachmentList, projectFormalId);
                hide_Mask();
                $scope.returnStatus = true;
                $scope.getSelectINVESTMENT_TYPE('INVESTMENT_TYPE');
            })
        }

        // 验证附件
        $scope.validateVoidFile = function () {
            for (var i in $scope.newAttachment) {
                if ($scope.newAttachment[i].fileName == null || $scope.newAttachment[i] == '') {
                    return false;
                }
            }
            return true;
        }

        $scope.initData = function () {
            $scope.initUpdate(objId);
            if (action == "Create") {
                $scope.title = "正式评审决策会材料-新增";
                $scope.isEdite = true;
                // $scope.getProjectFormalReviewByID(objId);
                $scope.formalReport.create_by = $scope.credentials.UUID;
                $scope.formalReport.create_name = $scope.credentials.userName;
                $("#wordbtn").hide();
            } else if (action == "Update") {
                $scope.isEdite = true;
                $scope.title = "正式评审决策会材料-修改";
                // $scope.getSummaryPPTByID(objId);
            } else if (action == "View") {
                $scope.title = "正式评审决策会材料-查看";
                // $scope.getSummaryPPTByID(objId);
                $scope.isEdite = false;
            }
            console.log($scope.isEdite)
        };

        $scope.getSummaryPPTByID = function (id) {
            $http({
                method: 'post',
                url: srvUrl + 'formalReport/getSummaryPPTByID.do',
                data: $.param({"businessId": id, "type": "pfr"})
            }).success(function (data) {
                $scope.formalReport = data.result_data.project;
                $scope.formalReport.projectName = $scope.formalReport.PROJECTNAME;
                $scope.formalReport.projectFormalId = $scope.formalReport.BUSINESSID;
                $scope.projectSummary = data.result_data.summary;
                if (action == "View") {
                    $('button').attr("disabled", "disabled");
                    $("#submitbnt").attr("disabled", false);
                    $('#wordbtn').attr("disabled", false);
                }
            }).error(function (data, status, headers, config) {
                $.alert(status);
            });
        }

        $scope.getProjectFormalReviewByID = function (id) {
            $http({
                method: 'post',
                url: srvUrl + "formalReport/getProjectFormalReviewByID.do",
                data: $.param({"id": id})
            }).success(function (data) {
                $scope.pfr = data.result_data;
                $scope.formalReport.projectFormalId = $scope.pfr.id;
                $scope.formalReport.projectName = $scope.pfr.apply.projectName;
                $scope.formalReport.projectNo = $scope.pfr.apply.projectNo;
                if (null != $scope.pfr.apply.reportingUnit) {
                    $scope.formalReport.reportingUnit = $scope.pfr.apply.reportingUnit.name;
                }
                var ptNameArr = [], pmNameArr = [];
                var pt = $scope.pfr.apply.projectType;
                if (null != pt && pt.length > 0) {
                    for (var i = 0; i < pt.length; i++) {
                        ptNameArr.push(pt[i].VALUE);
                    }
                    $scope.formalReport.projectTypeName = ptNameArr.join(",");
                }
                $scope.formalReport.controllerVal = $scope.controller_val;
            }).error(function (data, status, headers, config) {
                $.alert(status);
            });
        }

        $scope.initData();

        $scope.saveOnly = function () {
            //验证空附件
            if (!$scope.validateVoidFile()) {
                $.alert("附件不能为空！");
                return;
            }
            if (!$scope.saveMeetingInfo()) {
                return;
            }

            var data = $scope.dataForSave();
            console.log(data)
            $scope.saveOrSubmit(data, "so");
        };

        $scope.submitSave = function () {
            if (!$scope.saveMeetingInfo()) {
                return;
            }
            var data = $scope.dataForSave();
            $scope.saveOrSubmit(data, "ss");
        };

        $scope.dataForSave = function () {
            $scope.formalReport.projectFormalId = objId; // 正式评审项目id
            // $scope.formalReport.projectSummary = $scope.projectSummary;
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
                            $scope.isEdite = false;
                            alertData = "提交成功!";
                            $("#savebtn").hide();
                            $("#btnfile").attr("disabled", "disabled");
                            // $location.path("/FormalBiddingInfoPreview/" + $scope.businessId + "/" + $filter('encodeURI') + "/2");
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


        // 预览时将数据存入浏览器缓存，以便退出预览时使用
        $scope.saveDataToLocalStorage = function () {
            if(!window.localStorage){
                alert("浏览器不支持localstorage");
                return false;
            }else {
                var storage = window.localStorage;
                storage.newAttachment = JSON.stringify($scope.newAttachment);
            }
        }

        // 整理预览界面需要的json数据
        $scope.previewJson = function () {
            var formalPreview = {};
            formalPreview.projectName = $scope.formalReport.projectName;
            formalPreview.newAttachment = $scope.newAttachment;
            return formalPreview;
        }

        // 进入预览页面
        $scope.toPreview = function () {
            // $scope.saveDataToLocalStorage();
            if($scope.isEdite) {
                // falg返回时可编辑
                $location.path("/OtherBiddingInfoPreview/" + objId + "/" + $filter('encodeURI') + "/1");
            } else {
                // falg返回时预览
                $location.path("/OtherBiddingInfoPreview/" + objId + "/" + $filter('encodeURI') + "/2");
            }

        }

        // 保存项目评级相关内容
        $scope.saveMeetingInfo = function (){
            console.log($scope.meetInfo);
            if ($scope.meetInfo != null && $scope.meetInfo != "") {
                $scope.meetInfo.formalId = objId;
                var myMeetingInfo = angular.copy($scope.meetInfo);
                myMeetingInfo.apply = $scope.pfr.apply;
                // 保存项目评级
                $.ajax({
                    type: 'post',
                    url: srvUrl + "information/addConferenceInformation.do",
                    data: $.param({
                        "information": JSON.stringify(myMeetingInfo),
                        "businessId": objId
                    }),
                    dataType: "json",
                    async: false,
                    success: function (result) {
                        if (!result.success) {
                            alert(result.result_name);
                            return false;
                        }
                    }
                });
                return true;
            } else {
                //没有项目评级
                return true;
            }
        }


    }]);

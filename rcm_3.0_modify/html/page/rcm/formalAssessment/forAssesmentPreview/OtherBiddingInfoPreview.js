ctmApp.register.controller('OtherBiddingInfoPreview', ['$http','$scope','$location','$routeParams','Upload','$filter',
    function ($http,$scope,$location,$routeParams,Upload,$filter) {
        // 预览传来的参数
        $scope.formalPreview = $routeParams.formalPreview;
        $scope.changeValue = 1;   // 标识切换的页面

        // 待决策项目审阅传来的参数
        $scope.waitId = $routeParams.id;
        $scope.flag = $routeParams.flag;

        //处理附件列表
        $scope.reduceAttachment = function(attachment, id){
            console.log(attach_list("formalReview", id, "formalAssessmentInfo"))
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
            console.log($scope.newAttachment)
        };

        // 待决策项目传阅 初始化数据
        $scope.initUpdate = function (id) {
            $http({
                method: 'post',
                url: srvUrl + "formalReport/findFormalAndReport.do",
                data: $.param({"projectFormalId": $scope.waitId})
            }).success(function (data) {
                $scope.formalReport = data.result_data.Report;
                $scope.pfr = data.result_data.Formal;
                $scope.meetInfo = data.result_data.MeetInfo;
                $scope.applyDate = data.result_data.applyDate;
                $scope.stage = data.result_data.stage;
                $scope.projectSummary = data.result_data.summary;

                // 处理附件
                $scope.reduceAttachment(data.result_data.Formal.attachmentList, id);

                //新增附件类型
                $scope.attach = data.result_data.attach;
                console.log($scope.attach)
            })
        }

        $scope.initUpdate($scope.waitId);

    }]
);
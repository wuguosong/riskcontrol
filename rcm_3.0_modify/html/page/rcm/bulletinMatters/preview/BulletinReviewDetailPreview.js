ctmApp.register.controller('BulletinReviewDetailPreview', ['$http','$scope','$location', '$routeParams', '$filter', '$routeParams',
    function ($http,$scope,$location, $routeParams, $filter,$routeParams) {
        $scope.oldUrl = $routeParams.url;
        var routeParams = $routeParams.id.split("_");
        $scope.flag = $routeParams.flag;
        var queryParamId = routeParams[0];
        $scope.businessid = routeParams[0];
        $scope.tabIndex = routeParams[1];
        $scope.initDefaultData = function(){
            $scope.WF_STATE = '2';
            $scope.initUpdate();
        };


        //处理附件列表
        $scope.reduceAttachment = function(attachment, id){
            $scope.newAttachment = attach_list("bulletin", id, "BulletinMattersDetail").result_data;
            for(var i in attachment){
                var file = attachment[i];
                console.log(file);
                for (var j in $scope.newAttachment){
                    if (file.fileId == $scope.newAttachment[j].fileid){
                        $scope.newAttachment[j].newFile = '0';
                        $scope.newAttachment[j].fileName = file.oldFileName;
                        $scope.newAttachment[j].lastUpdateBy = file.lastUpdateBy;
                        $scope.newAttachment[j].lastUpdateData = file.lastUpdateData;
                        $scope.newAttachment[j].uuid = file.uuid;
                        break;
                    }
                }
            }
        };

        $scope.initUpdate = function(){
            var url = srvUrl + "bulletinReview/queryViewDefaultInfo.do";
            $http({
                method:'post',
                url: url,
                data: $.param({"businessId": queryParamId})
            }).success(function(result){
                var data = result.result_data;
                $scope.bulletinOracle = data.bulletinOracle;
                $scope.bulletin = data.bulletinMongo;
                // 处理附件
                $scope.reduceAttachment(data.bulletinMongo.attachmentList, queryParamId);
            });
        };
        $scope.save = function(){
            var params = {
                businessId: queryParamId,
                decisionOpinionList: $scope.selfOpinion
            };
            $http({
                method:'post',
                url:srvUrl+"meeting/saveReviewInfo.do",
                data: $.param({"data":JSON.stringify(params)})
            }).success(function(result){
                $.alert(result.result_name);
                $scope.initDefaultData();
            });
        };

        // 滑动切换时，上面的过程跟着切换
        $scope.changeStyle = function (num) {
            var tabId = ['decisionMatters', 'windControlAdvice', 'relevantAttachments'];
            angular.forEach(tabId, function (data, index, array) {
                if (index != num) {
                    angular.element("#"+data).removeClass('chose');
                } else {
                    angular.element("#"+data).addClass('chose');
                }
            });
        };

        // 展开展示信息
        $scope.expandMore = function (parentId, val) {
            angular.element("#"+parentId).addClass('hideOpen');
            $scope[val] = true;
            angular.element("")
        }

        $scope.initDefaultData();
    }]);

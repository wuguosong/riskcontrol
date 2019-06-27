ctmApp.register.controller('BulletinReviewDetailPreview', ['$http','$scope','$location', '$routeParams', '$filter', '$routeParams','Upload',
    function ($http,$scope,$location, $routeParams, $filter,$routeParams, Upload) {
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
        };

        $scope._message_publish_reply_ = validateMessageOpenAuthority('bulletin', $scope.businessid);


        //附件列表---->上传附件---->风控负责人--临时添加
        $scope.uploadRiskLeaderAttachment = function (file,errorFile, idx) {
            debugger
            if(errorFile && errorFile.length>0){
                var errorMsg = fileErrorMsg(errorFile);
                $scope.errorAttach[idx]={msg:errorMsg};
            }else if(file){

                if(file.name){
                    //检查压缩文件
                    var index = file.name.lastIndexOf('.');
                    var suffix  = file.name.substring(index+1);
                    if("rar" == suffix || "zip" == suffix || "7z" == suffix){
                        $.alert("附件不能是压缩文件！");
                        return false;
                    }
                }

                var fileFolder = "bulletin/risk/";
                var dates=$scope.bulletin.createTime;
                var no=$scope.bulletin.id;
                var strs= new Array(); //定义一数组
                strs=dates.split("-"); //字符分割
                dates=strs[0]+strs[1]; //分割后的字符输出
                fileFolder=fileFolder+dates+"/"+no;

               /* $scope.errorAttach[idx]={msg:''};*/
                Upload.upload({
                    url:srvUrl+'file/uploadFile.do',
                    data: {file: file, folder:fileFolder}
                }).then(function (resp) {
                    var retData = resp.data.result_data[0];
                    $scope.bulletin.riskLeaderAttachment[idx]=retData;
                }, function (resp) {
                    $.alert(resp.status);
                }, function (evt) {
                    var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                    $scope["progress"+idx]=progressPercentage == 100 ? "":progressPercentage+"%";
                });
            }
        };

        //附件---->新增列表---->风控负责人附件
        $scope.addRiskLeaderAttachment = function(){
            function addBlankRow(array){
                var blankRow = {
                    file_content:''
                }
                var size=0;
                for(attr in array){
                    size++;
                }
                array[size]=blankRow;
            }
            if(undefined==$scope.bulletin.riskLeaderAttachment){
                $scope.bulletin.riskLeaderAttachment=[];
            }
            addBlankRow($scope.bulletin.riskLeaderAttachment);
        }

        //附件列表---->删除指定的列表---->评审负责人
        $scope.deleteReviewLeaderAttachment = function(){
            var commentsObj = $scope.bulletin.reviewLeaderAttachment;
            if(commentsObj!=null){
                for(var i=0;i<commentsObj.length;i++){
                    if(commentsObj[i].selected){
                        commentsObj.splice(i,1);
                        i--;
                    }
                }
            }
        };

        //保存方法
        $scope.save = function(callback){

            //保存风控负责人信息
            $http({
                method:'post',
                url: srvUrl + "bulletinInfo/saveRiskLeaderAttachment.do",
                data:$.param({
                    "businessId":$scope.businessid,
                    "attachment":JSON.stringify($scope.bulletin.riskLeaderAttachment),
                    "opinion":$scope.bulletin.riskLeaderOpinion
                })
            }).success(function(data){
                debugger
                if(data.success){

                        for(var i in $scope.bulletin.riskLeaderAttachment){
                            if($scope.bulletin.riskLeaderAttachment[i].fileName == null || $scope.bulletin.riskLeaderAttachment[i].fileName == ''){
                                $.alert("您有附件未上传，请上传附件！");
                                return;
                            }
                        }
                    $.alert(data.result_name);
                }else{
                    $.alert(data.result_name);
                }
            });
        };


        $scope.initDefaultData();
    }]);

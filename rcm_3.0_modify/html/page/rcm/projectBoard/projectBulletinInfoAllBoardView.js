ctmApp.register.controller('projectBulletinInfoAllBoardView', ['$http','$scope','$location', '$routeParams', '$filter',
 function ($http,$scope,$location, $routeParams, $filter) {
	$scope.queryParamId = $routeParams.id;
	$scope.oldUrl = $routeParams.url;
	$scope.initDefaultData = function(){
		$scope.wfInfo = {processKey:'bulletin', "businessId":$scope.queryParamId};
		$scope.initUpdate($scope.queryParamId);
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

	$scope.initUpdate = function(id){
		var url = srvUrl + "bulletinInfo/queryViewDefaultInfo.do";
		$http({
			method:'post',  
		    url: url,
		    data: $.param({"businessId":id})
		}).success(function(result){
			var data = result.result_data;
			$scope.bulletinOracle = data.bulletinOracle;
			$scope.bulletin = data.bulletinMongo;
			$scope.auditLogs = data.logs;
			console.log($scope.bulletinOracle);

            // 处理附件
            $scope.reduceAttachment(data.bulletinMongo.attachmentList, id);

			$scope.initPage();
		});
	};
	$scope.initDefaultData();
	$scope.initPage = function(){
		if($scope.bulletinOracle.AUDITSTATUS=="1" || $scope.bulletinOracle.AUDITSTATUS=="2"){
			//流程已启动
			$("#submitBtn").hide();
			$scope.wfInfo.businessId = $scope.queryParamId;
			$scope.refreshImg = Math.random()+1;
		}else{
			//未启动流程
			$("#submitBtn").show();
		}
	}
	$scope.selectAll = function(){
		if($("#all").attr("checked")){
			$(":checkbox[name='choose']").attr("checked",1);
		}else{
			$(":checkbox[name='choose']").attr("checked",false);
		}
	}
	$scope._init_uuid_ = $scope.credentials.UUID;
}]);
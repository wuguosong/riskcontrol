/********
 * 历史数据详情准备
 *********/
ctmApp.register.controller('projectHistoryInfoAllBoardView', ['$http','$scope','$location', '$routeParams', '$filter','Upload',
 function ($http,$scope,$location, $routeParams, $filter, Upload) {

     var currentUserRoles = $scope.credentials.roles;
     $scope.showCesuan = false;
     for (var i = 0; i < currentUserRoles.length; i++) {
         if (currentUserRoles[i].CODE == 'RISK_CESUAN') {
             $scope.showCesuan = true;
         }
     }

	$scope.id = $routeParams.id;
	$scope.oldUrl = $routeParams.url;

     $scope.initData = function () {
         $scope.initUpdate($scope.id);
     };

     //处理附件列表
     $scope.reduceAttachment = function(attachment, id){
         $scope.newAttachment = attach_list("historyData", id, "pfrHistoryInfo").result_data;
         for(var i in attachment){
             var file = attachment[i];
             console.log(file.fileId);
             for (var j in $scope.newAttachment){
                 if($scope.newAttachment[j].fileid % 10000 == 0) {
                     if ((file.fileId * 10000) == $scope.newAttachment[j].fileid){
                         $scope.newAttachment[j].newFile = '0';
                         $scope.newAttachment[j].fileName = file.fileName;
                         $scope.newAttachment[j].type = file.type;
                         $scope.newAttachment[j].last_date = timestampToTime($scope.newAttachment[j].last_dateline);
                         break;
                     }
                 } else {
                     if (file.fileId== $scope.newAttachment[j].fileid){
                         $scope.newAttachment[j].newFile = '0';
                         $scope.newAttachment[j].fileName = file.fileName;
                         $scope.newAttachment[j].type = file.type;
                         $scope.newAttachment[j].last_date = timestampToTime($scope.newAttachment[j].last_dateline);
                         break;
                     }
                 }
             }

         }
     };

     // 处理附件时间
     $scope.reduceAttachDate = function (fieldName, pageLocation) {
         $scope.attch[fieldName] = attach_list("historyData", $scope.id, pageLocation).result_data;

         angular.forEach($scope.attch[fieldName], function (data, index) {
             data.last_date = timestampToTime(data.last_dateline);
             data.newFile = '0';
         });
     };

	$scope.initUpdate = function(id){
		var url = srvUrl + "historyData/getHistoryById.do";
		$http({
			method:'post',  
		    url: url,
		    data: $.param({"id": id})
		}).success(function(result){
            if(result.result_code == 'S'){
                var data = result.result_data;
                $scope.historyInfo = data.historyInfo;
                $scope.mongoData = data.mongoData;
                $scope.reduceAttachment(data.mongoData.attachmentList, id);
                $scope.attch = [];

                // 获取附件并转化时间
                $scope.reduceAttachDate ('investmnetAgreementList', 'pfrHistoryInvestmnetAgreement');
                $scope.reduceAttachDate ('windControlCalualationList', 'pfrHistoryWindControlCalualation');


                // 回显数据-业务类型
                if($scope.mongoData.serviceType != undefined && $scope.mongoData.serviceType != null && $scope.mongoData.serviceType != {} ){
                    commonModelValue2('oneservicetypebox',$scope.mongoData.serviceType);
                    $scope.historyInfo.serviceType = $scope.mongoData.serviceType;
                }

                // 回显数据-商业模式
                if($scope.mongoData.projectModel != undefined && $scope.mongoData.projectModel != null && $scope.mongoData.projectModel != {} ){
                    $("#projectmodeboxName").select2("val", " ");
                    commonModelValue2('projectmodebox',$scope.mongoData.projectModel);
                    $scope.historyInfo.projectModel = $scope.mongoData.projectModel;
                }
            }else{
                $.alert(result.result_name);
            }

            hide_Mask();
		});
	};

     $scope.initData();
}]);

ctmApp.register.controller('projectPreHistoryInfoAllBoardView', ['$http','$scope','$location', '$routeParams', '$filter',
 function ($http,$scope,$location, $routeParams, $filter) {

     var currentUserRoles = $scope.credentials.roles;
     $scope.showCesuan = false;
     for (var i = 0; i < currentUserRoles.length; i++) {
         if (currentUserRoles[i].CODE == 'RISK_CESUAN') {
             $scope.showCesuan = true;
         }
     }

     $scope.id = $routeParams.id;
     $scope.oldUrl = $routeParams.url;

     $scope.initData = function () {
         $scope.initUpdate($scope.id);
     };

     //处理附件列表
     $scope.reduceAttachment = function(attachment, id){
         $scope.newAttachment = attach_list("historyData", id, "preHistoryInfo").result_data;
         for(var i in attachment){
             var file = attachment[i];
             for (var j in $scope.newAttachment){
                 if($scope.newAttachment[j].fileid % 10000 == 0) {
                     if ((file.fileId * 10000) == $scope.newAttachment[j].fileid){
                         $scope.newAttachment[j].newFile = '0';
                         $scope.newAttachment[j].fileName = file.fileName;
                         $scope.newAttachment[j].type = file.type;
                         $scope.newAttachment[j].last_date = timestampToTime($scope.newAttachment[j].last_dateline);
                         break;
                     }
                 } else {
                     if (file.fileId== $scope.newAttachment[j].fileid){
                         $scope.newAttachment[j].newFile = '0';
                         $scope.newAttachment[j].fileName = file.fileName;
                         $scope.newAttachment[j].type = file.type;
                         $scope.newAttachment[j].last_date = timestampToTime($scope.newAttachment[j].last_dateline);
                         break;
                     }
                 }
             }
         }
     };

     // 处理附件时间
     $scope.reduceAttachDate = function (fieldName, pageLocation) {
         $scope.attch[fieldName] = attach_list("historyData", $scope.id, pageLocation).result_data;

         angular.forEach($scope.attch[fieldName], function (data, index) {
             data.last_date = timestampToTime(data.last_dateline);
             data.newFile = '0';
         });
     };

     $scope.initUpdate = function(id){
         var url = srvUrl + "historyData/getHistoryById.do";
         $http({
             method:'post',
             url: url,
             data: $.param({"id": id})
         }).success(function(result){
             if(result.result_code == 'S'){
                 var data = result.result_data;
                 $scope.historyInfo = data.historyInfo;
                 $scope.mongoData = data.mongoData;
                 $scope.reduceAttachment(data.mongoData.attachmentList, id);
                 $scope.attch = [];

                 // 获取附件并转化时间
                 $scope.reduceAttachDate ('investmnetAgreementList', 'preHistoryInvestmnetAgreement');
                 $scope.reduceAttachDate ('windControlCalualationList', 'preHistoryWindControlCalualation');


                 // 回显数据-业务类型
                 if($scope.mongoData.serviceType != undefined && $scope.mongoData.serviceType != null && $scope.mongoData.serviceType != {} ){
                     commonModelValue2('oneservicetypebox',$scope.mongoData.serviceType);
                     $scope.historyInfo.serviceType = $scope.mongoData.serviceType;
                 }

                 // 回显数据-商业模式
                 if($scope.mongoData.projectModel != undefined && $scope.mongoData.projectModel != null && $scope.mongoData.projectModel != {} ){
                     $("#projectmodeboxName").select2("val", " ");
                     commonModelValue2('projectmodebox',$scope.mongoData.projectModel);
                     $scope.historyInfo.projectModel = $scope.mongoData.projectModel;
                 }
             }else{
                 $.alert(result.result_name);
             }

             hide_Mask();
         });
     };

     $scope.initData();
}]);
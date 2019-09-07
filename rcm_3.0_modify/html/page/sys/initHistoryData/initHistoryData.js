/********
 * 历史数据相关
 *********/
ctmApp.register.controller('initHistoryData', ['$http','$scope','$location','$routeParams',
function ($http,$scope,$location,$routeParams) {

	$scope.queryList=function(){
		$http({
			method:'post',  
		    url:srvUrl+"historyData/getHistoryList.do",
		    data: $.param({"page":JSON.stringify($scope.paginationConf)})
		}).success(function(data){
			if(data.result_code == 'S'){
				$scope.historyList = data.result_data.list;
                $scope.paginationConf.totalItems = data.result_data.totalItems;
                $scope.paginationConf.totalItems = 311;
                console.log($scope.paginationConf.totalItems);
			}else{
				$.alert(data.result_name);
			}
		});
    };

    /*$scope.saveList = function (){
        $http({
            method:'post',
            url:srvUrl+"historyData/saveOrUpdate.do"
        }).success(function(result){
            if(result.result_code == 'S'){
                $.alert(result.result_name);
            }else{
                $.alert(result.result_name);
            }

            hide_Mask();
        });
    };*/

	$scope.cancel = function () {
		$scope.paginationConf.queryObj.projectName = '';
        $scope.paginationConf.queryObj.projectType = '';
        $scope.queryList();
	};

    $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage',  $scope.queryList);
}]);

ctmApp.register.controller('pfrHistoryInfo', ['$http','$scope','$location', '$routeParams', '$filter','Upload',
 function ($http,$scope,$location, $routeParams, $filter, Upload) {

	$scope.id = $routeParams.id;
	$scope.oldUrl = $routeParams.url;

     $scope.initData = function () {
         $scope.attachmentType = [{ITEM_CODE: "0501", ITEM_NAME: "投资分析报告"},
                                    {ITEM_CODE: "0601", ITEM_NAME: "投资部门测算"},
                                    {ITEM_CODE: "0602", ITEM_NAME: "专业职能部门意见"},
                                    {ITEM_CODE: "0509", ITEM_NAME: "其他文件"}];
         $scope.initUpdate($scope.id);
     };

     //处理附件列表
     $scope.reduceAttachment = function(attachment, id){
         $scope.newAttachment = attach_list("historyData", id, "pfrHistoryInfo").result_data;
         for(var i in attachment){
             var file = attachment[i];
             for (var j in $scope.newAttachment){
                 if (file.fileId == $scope.newAttachment[j].fileid){
                     $scope.newAttachment[j].newFile = '0';
                     $scope.newAttachment[j].fileName = file.fileName;
                     $scope.newAttachment[j].type = file.type;
                     $scope.newAttachment[j].last_date = timestampToTime($scope.newAttachment[j].last_dateline);
                     break;
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

	$scope.update = function (){
        $http({
            method:'post',
            url: srvUrl + 'historyData/saveOrUpdate.do',
            data: $.param({"json":JSON.stringify($scope.historyInfo)})
        }).success(function(result){
            if (result.result_code === 'S') {
                if(typeof(showPopWin)=='function'){
                    showPopWin();
                }else{
                    $.alert('保存成功');
                }
            } else {
                $.alert(result.result_name);
                $scope.initUpdate($scope.id);
            }
        });
    };

	// 填写相关方法
     // 回显select2下拉框的值
     var commonModelValue2=function(paramsVal,arr){
         var leftstr2="<li class=\"select2-search-choice\"><div>";
         var centerstr2="</div><a class=\"select2-search-choice-close\" tabindex=\"-1\" onclick=\"delSelect(this,'"
         var rightstr2="');\"  ></a></li>";
         for(var i=0;i<arr.length;i++){
             console.log(leftstr2+arr[i].VALUE + centerstr2+paramsVal+"','"+arr[i].VALUE+"','"+arr[i].KEY+rightstr2);
             $("#header"+paramsVal).find(".select2-search-field").before(leftstr2+arr[i].VALUE + centerstr2+paramsVal+"','"+arr[i].VALUE+"','"+arr[i].KEY+rightstr2);
         }
     };

     // 获取业务类型
     $scope.getSyncbusinessmodel=function(keys){
         var url="businessDict/queryBusinessType.do";
         $scope.httpData(url,keys).success(function(data){
             if(data.success){
                 $scope.Syncbusinessmodel=data.result_data;
             }else{
                 alert(data.result_name);
             }
         });
     };

     // 获取商业模式
     $scope.getprojectmodel=function(key){
         var url= "common/commonMethod/selectsyncbusinessmodel";
         $scope.httpData(url, key).success(function(data){
             if(data.result_code === 'S'){
                 $scope.projectModelValue=data.result_data;
             }else{
                 alert(data.result_name);
             }
         });
     };

     $scope.initData();

     angular.element(document).ready(function() {
         // 初始化业务类型下拉框
         $scope.getSyncbusinessmodel('0');
         // 初始化项目模式的值
         $scope.getprojectmodel('1');
     });
}]);

ctmApp.register.controller('preHistoryInfo', ['$http','$scope','$location', '$routeParams', '$filter',
 function ($http,$scope,$location, $routeParams, $filter) {
     $scope.id = $routeParams.id;
     $scope.oldUrl = $routeParams.url;

     $scope.initData = function () {
         $scope.attachmentType = [{ITEM_CODE: "0501", ITEM_NAME: "投资分析报告"},
                                    {ITEM_CODE: "0601", ITEM_NAME: "投资部门测算"},
                                    {ITEM_CODE: "0602", ITEM_NAME: "专业职能部门意见"},
                                    {ITEM_CODE: "0509", ITEM_NAME: "其他文件"}];
         $scope.initUpdate($scope.id);
     };

     //处理附件列表
     $scope.reduceAttachment = function(attachment, id){
         $scope.newAttachment = attach_list("historyData", id, "preHistoryInfo").result_data;
         for(var i in attachment){
             var file = attachment[i];
             for (var j in $scope.newAttachment){
                 if (file.fileId == $scope.newAttachment[j].fileid){
                     $scope.newAttachment[j].newFile = '0';
                     $scope.newAttachment[j].fileName = file.fileName;
                     $scope.newAttachment[j].type = file.type;
                     $scope.newAttachment[j].last_date = timestampToTime($scope.newAttachment[j].last_dateline);
                     break;
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

     $scope.update = function (){
         $http({
             method:'post',
             url: srvUrl + 'historyData/saveOrUpdate.do',
             data: $.param({"json":JSON.stringify($scope.historyInfo)})
         }).success(function(result){
             if (result.result_code === 'S') {
                 if(typeof(showPopWin)=='function'){
                     showPopWin();
                 }else{
                     $.alert('保存成功');
                 }
             } else {
                 $.alert(result.result_name);
                 $scope.initUpdate($scope.id);
             }
         });
     };

     // 填写相关方法
     // 回显select2下拉框的值
     var commonModelValue2=function(paramsVal,arr){
         var leftstr2="<li class=\"select2-search-choice\"><div>";
         var centerstr2="</div><a class=\"select2-search-choice-close\" tabindex=\"-1\" onclick=\"delSelect(this,'"
         var rightstr2="');\"  ></a></li>";
         for(var i=0;i<arr.length;i++){
             console.log(leftstr2+arr[i].VALUE + centerstr2+paramsVal+"','"+arr[i].VALUE+"','"+arr[i].KEY+rightstr2);
             $("#header"+paramsVal).find(".select2-search-field").before(leftstr2+arr[i].VALUE + centerstr2+paramsVal+"','"+arr[i].VALUE+"','"+arr[i].KEY+rightstr2);
         }
     };

     // 获取业务类型
     $scope.getSyncbusinessmodel=function(keys){
         var url="businessDict/queryBusinessType.do";
         $scope.httpData(url,keys).success(function(data){
             if(data.success){
                 $scope.Syncbusinessmodel=data.result_data;
             }else{
                 alert(data.result_name);
             }
         });
     };

     // 获取商业模式
     $scope.getprojectmodel=function(key){
         var url= "common/commonMethod/selectsyncbusinessmodel";
         $scope.httpData(url, key).success(function(data){
             if(data.result_code === 'S'){
                 $scope.projectModelValue=data.result_data;
             }else{
                 alert(data.result_name);
             }
         });
     };

     $scope.initData();

     angular.element(document).ready(function() {
         // 初始化业务类型下拉框
         $scope.getSyncbusinessmodel('0');
         // 初始化项目模式的值
         $scope.getprojectmodel('1');
     });
}]);

function delSelect(o,paramsVal,name,id){
    $(o).parent().remove();
    accessScope("#"+paramsVal+"Name", function(scope){
        if(paramsVal=="oneservicetypebox"){
            var names=scope.historyInfo.serviceType;
            var newNames = $(names).map(function(){
                if(this.KEY!=id){
                    return this;
                }
            }).get();
            if(newNames.length>0){
                scope.historyInfo.serviceType=newNames;
            }else{
                scope.historyInfo.serviceType=null;
            }

        }else if(paramsVal=="projectmodebox"){
            var names=scope.historyInfo.projectModel;
            var newNames = $(names).map(function(){
                if(this.KEY!=id){
                    return this;
                }
            }).get();
            if(newNames.length>0){
                scope.historyInfo.projectModel=newNames;
            }else{
                scope.historyInfo.projectModel=null;
            }
        }
    });
}
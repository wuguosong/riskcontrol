ctmApp.register.controller('NoticeDecisionDetail',['$http','$scope','$location','$routeParams','Upload','$timeout', function ($http,$scope,$location,$routeParams,Upload,$timeout) {
    //初始化
    var objId = $routeParams.id;
    var action = $routeParams.action;
    $scope.nod={};
    $scope.dic=[];
    $scope.save=function(showPopWin) {
        if ($("#noticeName").valid()) {
            var postObj;
            var localUrl;
            if (typeof ($scope.nod._id) != "undefined") {
            	localUrl = 'noticeDecisionInfo/update.do';
            } else {
            	localUrl = 'noticeDecisionInfo/create.do';
            }
            $http({
    			method:'post',  
    		    url: srvUrl + localUrl,
    		    data: $.param({"nod":JSON.stringify($scope.removeHashKey($scope.nod))})
    		}).success(function(result){
    			 if (result.result_code === 'S') {
                     $scope.nod._id = result.result_data;
                     if(typeof(showPopWin)=='function'){
                         showPopWin();
                     }else{
                         $.alert('保存成功!');
                     }
                 } else {
                     $.alert(result.result_name);
                 }
    		});
        }
    }
    $scope.getProjectFormalReviewByID=function(id){
    	$http({
			method:'post',  
		    url: srvUrl + "noticeDecisionInfo/querySaveDefaultInfo.do",
		    data: $.param({"businessId":id})
		}).success(function(result){
			$scope.pfr = result.result_data;
	        $scope.nod.projectFormalId = $scope.pfr._id;
	        $scope.nod.projectName = $scope.pfr.apply.projectName;
	        $scope.nod.reportingUnit={};
	        $scope.nod.reportingUnit.name = $scope.pfr.apply.reportingUnit.VALUE;
	        $scope.nod.reportingUnit.value = $scope.pfr.apply.reportingUnit.KAY;
	        $scope.nod.projectNo = $scope.pfr.apply.projectNo;
	        $scope.nod.controllerVal="NoticeDecision";
		});
    }
    $scope.getNoticeByID=function(id){
        var  url = 'formalAssessment/NoticeOfDecision/getNoticeOfDecstionByID';
        $http({
			method:'post',  
		    url: srvUrl + "noticeDecisionInfo/queryUpdateInitInfo.do",
		    data: $.param({"projectId":id})
		}).success(function(result){
			$scope.nod = result.result_data;
			var haderNameArr = [], haderValueArr = [];
			if (null != $scope.nod.personLiable) {
               var header = $scope.nod.personLiable;
               if (null != header && header.length > 0) {
                   for (var i = 0; i < header.length; i++) {
                       haderNameArr.push(header[i].name);
                       haderValueArr.push(header[i].value);
                   }
                   commonModelValue("personLiable", haderValueArr, haderNameArr);
               }
			}
		});
    }
    $scope.setDirectiveParam=function(columnName){
        $scope.columnName=columnName;
    }
    $scope.setDirectiveOrgList=function(id,name){
        var params=$scope.columnName;
        if(params=="subjectOfImplementation"){
            $scope.nod.subjectOfImplementation = {"name":name,value:id};
            $("#subjectOfImplementationName").val(name);
            $("label[for='subjectOfImplementationName']").remove();
        }
        if(params=="responsibilityUnit"){
            $scope.nod.responsibilityUnit = {"name":name,value:id};
            $("#responsibilityUnitName").val(name);
            $("label[for='responsibilityUnitName']").remove();
        }
    }
    $scope.removeHashKey = function(obj) {
    	return angular.copy(obj)
    }
    var commonModelValue=function(paramsVal,arrID,arrName){
        var leftstr2="<li class=\"select2-search-choice\"><div>";
        var centerstr2="</div><a class=\"select2-search-choice-close\" tabindex=\"-1\" onclick=\"delSelect(this,'";
        var addID2="');\"  ></a><div class=\"full-drop\"><input type=\"hidden\" id=\"\"  value=\"";
        var rightstr2="\"></div></li>";
        for(var i=0;i<arrName.length;i++){
            $("#header"+paramsVal).find(".select2-search-field").before(leftstr2+arrName[i]+centerstr2+paramsVal+"','"+arrName[i]+","+arrID[i]+addID2+arrID[i]+rightstr2);
        }
    }
    //模式窗口需要设置到父窗口调用该方法
    $scope.setDirectiveUserList=function(arrID,arrName,arrUserNamesValue){
           $("#headerpersonLiable").find(".select2-choices .select2-search-choice").remove();
            $scope.nod.personLiable =arrUserNamesValue;  //赋值保存到数据库
            $("#personLiableName").val(arrUserNamesValue);
            $("label[for='personLiableName']").remove();
            commonModelValue("personLiable",arrID,arrName);
    }
    if(action=="Create"){
        $scope.getProjectFormalReviewByID(objId);
        $scope.nod.createBy = {name:$scope.credentials.userName, value:$scope.credentials.UUID};
        $scope.titleName = "决策通知书新增";
    }
    if(action=="Update"){
        $scope.getNoticeByID(objId);
        $scope.titleName = "决策通知书修改";
    }
}]);

	function delSelect(o,paramsVal,name,id){
	    $(o).parent().remove();
	    accessScope("#"+paramsVal+"Name", function(scope){
		        var names=scope.nod.personLiable;
		        var retArray = [];
		        for(var i=0;i<names.length;i++){
		            if(id !== names[i].value){
		                retArray.push(names[i]);
		            }
		        }
		        if(retArray.length>0){
		            scope.nod.personLiable=retArray;
		        }else{
		            scope.nod.personLiable={name:'',value:''};
		            $("#personLiableName").val("");
		        }
		});
	}

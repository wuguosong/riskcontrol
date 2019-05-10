ctmApp.register.controller('NoticeDecisionDraftCompleteDetail',['$http','$scope','$location','$routeParams','Upload','$timeout', function ($http,$scope,$location,$routeParams,Upload,$timeout) {
    //初始化
    $scope.businessid = $routeParams.id;
    $scope.action = $routeParams.action;
    $scope.oldUrl = $routeParams.url;
    $scope.mappedKeyValue={"nameField":"NAME","valueField":"ORGPKVALUE"};
    $scope.nod={};
    $scope.dic=[];
    $scope.columns = [{
		"fieldName" : "单位名称" ,
		"fieldValue" : "NAME"
	},{
		"fieldName" : "上级单位名称" ,
		"fieldValue" : "PNAME"
	}];
    $scope.initData = function(){
    	$scope.getNoticeDecstionByID($scope.businessid);
        $scope.initAttchment();
    	$scope.noticePersonLiable={"nameField":"name","valueField":"value"};
    	$scope.responsibilityUnitMapped={"nameField":"NAME","valueField":"ORGPKVALUE"};
    	$scope.subjectOfImplementationMapped={"nameField":"NAME","valueField":"ORGPKVALUE"};
    }
    //确认
	$scope.complete = function(tag){
		if($("#noticeName").valid()) {
			$scope.nod.subjectOfImplementation.name = $scope.nod.subjectOfImplementation.NAME;
			$scope.nod.subjectOfImplementation.value = $scope.nod.subjectOfImplementation.ORGPKVALUE;
			$scope.nod.responsibilityUnit.name = $scope.nod.responsibilityUnit.NAME;
			$scope.nod.responsibilityUnit.value = $scope.nod.responsibilityUnit.ORGPKVALUE;
			if($scope.nod.subjectOfImplementation == null || $scope.nod.subjectOfImplementation.value == null|| $scope.nod.subjectOfImplementation.value == ""){
				 $.alert('项目实施主体不能为空!');return false;
			}
			if($scope.nod.responsibilityUnit == null || $scope.nod.responsibilityUnit.value == null|| $scope.nod.subjectOfImplementation.value == ""){
				 $.alert('责任单位 不能为空!');return false;
			}
			if($scope.nod.personLiable == null || $scope.nod.personLiable.length == 0){
				 $.alert('责任人 不能为空!');return false;
			}
			show_Mask();
			$http({
				method:'post',  
				url: srvUrl + "noticeDecisionDraftInfo/update.do",
				data: $.param({"nod":JSON.stringify($scope.removeHashKey($scope.nod))})
			}).success(function(result){
				hide_Mask();
	   			 if (result.success) {
	   				 if(tag == "createWord"){
	   					$scope.createWord();
	   				 }else{
	   					 $.alert('执行成功!');
	   				 }
	             } else {
	                 $.alert(result.result_name);
	             }
			});
	  }
	};
    /*// 初始化附件
	$scope.initAttchment = function (){
		debugger
        $scope.attachment = attach_list("FormalDecisionDraftInfo", $scope.businessid, "pfrDecisionDraft").result_data;
        if (!isEmpty($scope.attachment)) {
            $('#noticeAttechment').
		}
	};*/

   //查询决策通知书详情信息
   $scope.getNoticeDecstionByID=function(id){
        var  url = 'noticeDecisionDraftInfo/queryNoticeDecstion.do';
        $http({
			method:'post',  
		    url: srvUrl + url,
		    data: $.param({"formalId":id})
		}).success(function(data){
			var noticeDecision = data.result_data;
			if(noticeDecision.personLiable == null){
				noticeDecision.personLiable = [];
			}
			if(noticeDecision.subjectOfImplementation == null){
				noticeDecision.subjectOfImplementation = {};
			}
			if(noticeDecision.responsibilityUnit == null){
				noticeDecision.responsibilityUnit = {};
			}
			$scope.requireTask1 = noticeDecision.require1 != null && noticeDecision.require1 != '';
			$scope.requireTask2 = noticeDecision.require2 != null && noticeDecision.require2 != '';
			$scope.requireTask3 = noticeDecision.require3 != null && noticeDecision.require3 != '';
			
            $scope.nod = noticeDecision;
            $scope.nod.subjectOfImplementation.NAME = $scope.nod.subjectOfImplementation.name;
			$scope.nod.subjectOfImplementation.ORGPKVALUE = $scope.nod.subjectOfImplementation.value;
			$scope.nod.responsibilityUnit.NAME = $scope.nod.responsibilityUnit.name;
			$scope.nod.responsibilityUnit.ORGPKVALUE = $scope.nod.responsibilityUnit.value;
			
			if($scope.nod.reviOfTotaInveType == null){
				$scope.nod.reviOfTotaInveType = "";
			}
			
            //查询评审报告的业务(项目)类型
          	$http({
				method:'post',  
			    url: srvUrl + "formalReport/getByBusinessId.do",
			    data: $.param({"businessId":$scope.businessid})
			}).success(function(result){
				$scope.serviceType14011402 = result.result_data.SERVICETYPE == "1401" || result.result_data.SERVICETYPE == "1402";
			});
		});
    }
    $scope.downLoadBiddingFile = function(idx){
    	var isExists = validFileExists(idx.filePath);
    	if(!isExists){
    		$.alert("要下载的文件已经不存在了！");
    		return;
    	}
		var filePath = idx.filePath, fileName = idx.fileName;
		if(fileName!=null && fileName.length>22){
			var extSuffix = fileName.substring(fileName.lastIndexOf("."));
			fileName = fileName.substring(0, 22);
			fileName = fileName + extSuffix;
    	}
		
		var url = srvUrl+"file/downloadFile.do?filepaths="+encodeURI(filePath)+"&filenames="+encodeURI(encodeURI(fileName));
		var a = document.createElement('a');
	    a.id = 'tagOpenWin';
	    a.target = '_blank';
	    a.href = url;
	    document.body.appendChild(a);

	    var e = document.createEvent('MouseEvent');     
	    e.initEvent('click', false, false);     
	    document.getElementById("tagOpenWin").dispatchEvent(e);
		$(a).remove();
	}
    
    $scope.initData();
    $scope.removeHashKey = function(obj) {
    	return angular.copy(obj)
    }
    $scope.createWord = function(){
    	startLoading();
        var  url = 'noticeDecisionDraftInfo/getNoticeOfDecisionWord.do';
        $http({
        	method:'post',  
		    url: srvUrl + url,
		    data: $.param({"formalId":$scope.businessid})
        }).success(function(data){
        	if(data.success){
        		var filesPath=data.result_data.filePath;
                var filesName=data.result_data.fileName;
                window.location.href = srvUrl+"file/downloadFile.do?filepaths="+encodeURI(encodeURI(filesPath))+"&filenames="+encodeURI(encodeURI(filesName));
        	}else{
        		 $.alert("文档生成失败，请查看必填项是否填写完毕；如果全部正常填写请联系管理员！");
        	}
        })
        endLoading();
    };
    
    //提交(弹出框)
	$scope.showSubmitModal = function(){
		$scope.nod.subjectOfImplementation.name = $scope.nod.subjectOfImplementation.NAME;
		$scope.nod.subjectOfImplementation.value = $scope.nod.subjectOfImplementation.ORGPKVALUE;
		$scope.nod.responsibilityUnit.name = $scope.nod.responsibilityUnit.NAME;
		$scope.nod.responsibilityUnit.value = $scope.nod.responsibilityUnit.ORGPKVALUE;
		if($scope.nod.subjectOfImplementation == null || $scope.nod.subjectOfImplementation.value == null|| $scope.nod.subjectOfImplementation.value == ""){
			 $.alert('项目实施主体不能为空!');return false;
		}
		if($scope.nod.responsibilityUnit == null || $scope.nod.responsibilityUnit.value == null|| $scope.nod.subjectOfImplementation.value == ""){
			 $.alert('责任单位 不能为空!');return false;
		}
		if($scope.nod.personLiable == null || $scope.nod.personLiable.length == 0){
			 $.alert('责任人 不能为空!');return false;
		}
		$('#submitModal').modal('show');
	}
	$scope.confirm = function(attc){
		var  url = 'noticeDecisionConfirmInfo/comfirm.do';
		if(attc == null || attc.fileName == null || attc.fileName == ""){
			$.alert("请上传决策通知书附件！");
			return;
		}
		show_Mask();
		var attachment = JSON.stringify(attc);
		var params = {
			"formalId":$scope.businessid,
			"attachment": attachment,
			"nod":JSON.stringify($scope.removeHashKey($scope.nod))
		};
        $http({
        	method:'post',  
		    url: srvUrl + url,
		    data: $.param(params)
        }).success(function(data){
        	hide_Mask();
        	$('#submitModal').modal('hide');
        	if(data.success){
        		if($scope.nod.attachment == null){
        			$scope.nod.attachment = [];
        		}
        		$scope.nod.attachment[0] = attc;
        		$.alert(data.result_name);
    		 var oldUrl=window.btoa(encodeURIComponent(escape("#/NoticeDecisionConfirmList/1")))
			 $location.path("NoticeDecisionConfirmDetailView/1/"+$scope.businessid+"/"+oldUrl);
        		$("#submibtnn").hide();
        	}else{
        		$.alert(data.result_name);
        	}
        })
	}
	
    $scope.initPingShenZongTouZiList = function(){
		$http({
			method:'post',  
		    url: srvUrl + "dict/queryDictItemByDictTypeCode.do",
		    params:{"code":constants.DICT_JC_PSZTZ}
		}).success(function(result){
			$scope.pingShenZongTouZiList = result.result_data;
			$scope.pingShenZongTouZiList.push({"ITEM_NAME":"","ITEM_CODE":""});
		});
	}
    $scope.initPingShenZongTouZiList();
}]);


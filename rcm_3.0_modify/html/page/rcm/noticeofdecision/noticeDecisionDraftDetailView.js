ctmApp.register.controller('NoticeDecisionDraftDetailView',['$http','$scope','$location','$routeParams','Upload','$timeout', function ($http,$scope,$location,$routeParams,Upload,$timeout) {
    //初始化
    var businessId = $routeParams.id;
    $scope.oldUrl = $routeParams.url;
    $scope.action =  $routeParams.action;
    $scope.nod={};
    $scope.initData = function(){
    	$scope.getNoticeDecstionByID(businessId);
    }
    
    //提交
	$scope.showSubmitModal = function(){
		$.confirm("提交后将不可修改，确定提交？",function(){
			$http({
	 			method:'post',  
	 		    url: srvUrl + $scope.localUrl,
	 		    data: $.param({"nod":JSON.stringify($scope.nod)})
	 		}).success(function(result){
	 			 if (result.success) {
	 				$http({
	 					method:'post',  
	 					url: srvUrl + "noticeDecisionDraftInfo/submitUpdateStage.do",
	 					data: $.param({
	 						"projectFormalId":$scope.nod.projectFormalId,
	 						"stage": "3.9"
	 					})
	 				}).success(function(result){
	 					 $.alert(result.result_name);
	 					 var oldUrl=window.btoa(encodeURIComponent(escape("#/NoticeDecisionDraftList/1")))
	 					 $location.path("NoticeDecisionDraftDetailView/submitted/"+businessId+"/"+oldUrl);
	 				});
	              } else {
	                  $.alert(result.result_name);
	              }
	 		});
		});
			
	}
    
    //查询决策通知书详情信息
	   $scope.getNoticeDecstionByID=function(id){
	        var  url = 'noticeDecisionDraftInfo/queryNoticeDecstion.do';
	        $http({
				method:'post',  
			    url: srvUrl + url,
			    data: $.param({"formalId":businessId})
			}).success(function(data){
				var noticeDecision = data.result_data;
    			$scope.requireTask1 = noticeDecision.require1 != null && noticeDecision.require1 != '';
    			$scope.requireTask2 = noticeDecision.require2 != null && noticeDecision.require2 != '';
    			$scope.requireTask3 = noticeDecision.require3 != null && noticeDecision.require3 != '';
				
	                $scope.nod = data.result_data;
	                
	                var haderNameArr = [], haderValueArr = [];
	                if (null != $scope.nod.personLiable) {
	                    var header = $scope.nod.personLiable;
	                    if (null != header && header.length > 0) {
	                        for (var i = 0; i < header.length; i++) {
	                            haderNameArr.push(header[i].name);
	                            haderValueArr.push(header[i].value);
	                        }
	                        commonModelValue("personLiable", haderValueArr, haderNameArr);
	                            $scope.nod.personLiableName = haderNameArr.join(",");
	                    }
	                }
	                //查询项目信息的业务类型
	              	$http({
	    				method:'post',  
	    			    url: srvUrl + "noticeDecisionDraftInfo/querySaveDefaultInfo.do",
	    			    data: $.param({"businessId":businessId})
	    			}).success(function(result){
	    				$scope.pfr = result.result_data;
	    				var pt1NameArr=[];
	          	        $scope.serviceController=false;
	      			    var serviceType=$scope.pfr.apply.serviceType;
		      			if(null!=serviceType && serviceType.length>0){
		      				for(var i=0;i<serviceType.length;i++){
		      					pt1NameArr.push(serviceType[i].KEY);
		      					if(serviceType[i].KEY=='1401' || serviceType[i].KEY=='1402'){
		      						$scope.serviceController=true;
		      					}
		      				}
		      				$scope.pfr.apply.serviceType=pt1NameArr.join(",");
		      			}
	    			});
			});
	    }
    //生成word文档
    $scope.createWord=function(){
        startLoading();
        var  url = 'noticeDecisionDraftInfo/getNoticeOfDecisionWord.do';
        $http({
        	method:'post',  
		    url: srvUrl + url,
		    data: $.param({"formalId":businessId})
        }).success(function(data){
        	if(data.success){
        		var filesPath=data.result_data.filePath;
                var filesName=data.result_data.fileName;
                //window.location.href = srvUrl+"common/RcmFile/downLoad?filePath="+encodeURI(filesPath)+"&fileName="+encodeURI(filesName);
                window.location.href = srvUrl+"file/downloadFile.do?filepaths="+encodeURI(encodeURI(filesPath))+"&filenames="+encodeURI(encodeURI(filesName));
        	}else{
        		 $.alert("报表生成失败，请查看必填项是否填写完毕；如果全部正常填写请联系管理员！");
        	}
        })
        endLoading();
    }
    $scope.initData();
    
    var commonModelValue=function(paramsVal,arrID,arrName){
        var leftstr2="<li class=\"select2-search-choice\"><div>";
        var centerstr2="</div><a class=\"select2-search-choice-close\" tabindex=\"-1\" onclick=\"delSelect(this,'";
        var addID2="');\"  ></a><div class=\"full-drop\"><input type=\"hidden\" id=\"\"  value=\"";
        var rightstr2="\"></div></li>";
        for(var i=0;i<arrName.length;i++){
            $("#header"+paramsVal).find(".select2-search-field").before(leftstr2+arrName[i]+centerstr2+paramsVal+"','"+arrName[i]+","+arrID[i]+addID2+arrID[i]+rightstr2);
        }
    }

    $scope.initPage = function(){
    	if($scope.nod.oracle.WF_STATE!='0'){
    		$scope.wfInfo.businessId = businessId;
    		$scope.refreshImg = Math.random()+1;
    	}
    	
	}
    $scope.titleName = "决策通知书详情信息";
    $scope.wfInfo = {processKey:'noticeDecision'};
	
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


ctmApp.register.controller('NoticeDecisionConfirmDetailView',['$http','$scope','$location','$routeParams','Upload','$timeout', 
                                                           function ($http,$scope,$location,$routeParams,Upload,$timeout) {
    //初始化
    var formalId = $routeParams.id;
    $scope.oldUrl = $routeParams.url;
    $scope.tabIndex =  $routeParams.tabIndex;
    $scope.nod={};
    $scope.initData = function(){
    	$scope.getNoticeDecstionByID();
    }
    //查询决策通知书详情信息
	   $scope.getNoticeDecstionByID=function(){
	        var  url = 'noticeDecisionDraftInfo/queryNoticeDecstion.do';
	        $http({
				method:'post',  
			    url: srvUrl + url,
			    data: $.param({"formalId":formalId})
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
	                
	                //查询评审报告的业务(项目)类型
	              	$http({
	    				method:'post',  
	    			    url: srvUrl + "formalReport/getByBusinessId.do",
	    			    data: $.param({"businessId":formalId})
	    			}).success(function(result){
	    				$scope.serviceController = result.result_data.SERVICETYPE == "1401"  || serviceType[i].KEY=='1402';
	    			});
			});
	    }

	   //提交(弹出框)
		$scope.showSubmitModal = function(){
			$('#submitModal').modal('show');
		}
		$scope.confirm = function(attc){
			var  url = 'noticeDecisionConfirmInfo/comfirm.do';
			if(attc == null || attc.fileName == null || attc.fileName == ""){
				$.alert("请上传决策通知书附件！");
				return;
			}
			var attachment = JSON.stringify(attc);
			var params = {
				"formalId":formalId,
				"attachment": attachment
			};
	        $http({
	        	method:'post',  
			    url: srvUrl + url,
			    data: $.param(params)
	        }).success(function(data){
	        	$('#submitModal').modal('hide');
	        	if(data.success){
	        		$.alert(data.result_name);
	        		$("#submibtnn").hide();
	        	}else{
	        		$.alert(data.result_name);
	        	}
	        })
		}
	   
		   //生成word文档
	    $scope.createWord=function(){
	        startLoading();
	        var  url = 'noticeDecisionDraftInfo/getNoticeOfDecisionWord.do';
	        $http({
	        	method:'post',  
			    url: srvUrl + url,
			    data: $.param({"formalId":formalId})
	        }).success(function(data){
	        	if(data.success){
	        		var filesPath=data.result_data.filePath;
	                var filesName=data.result_data.fileName;
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
    		$scope.wfInfo.formalId = formalId;
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


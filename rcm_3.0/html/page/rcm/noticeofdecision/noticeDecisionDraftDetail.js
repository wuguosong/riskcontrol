ctmApp.register.controller('NoticeDecisionDraftDetail',['$http','$scope','$location','$routeParams','Upload','$timeout', function ($http,$scope,$location,$routeParams,Upload,$timeout) {
    //初始化
    var objId = $routeParams.id;
    var action = $routeParams.action;
    $scope.nod={};
    $scope.dic=[];
    $scope.mappedKeyValue={"nameField":"NAME","valueField":"ORGPKVALUE"};
    $scope.noticePersonLiable={"nameField":"name","valueField":"value"};
    $scope.nod.responsibilityUnit = {};
    $scope.nod.subjectOfImplementation = {};
    $scope.columns = [{
		"fieldName" : "单位名称" ,
		"fieldValue" : "NAME"
	},{
		"fieldName" : "上级单位名称" ,
		"fieldValue" : "PNAME"
	}];
    $scope.localUrl = 'noticeDecisionDraftInfo/update.do';
    //保存
    $scope.save=function(showPopWin) {
    	show_Mask();
    	if($scope.nod.subjectOfImplementation != null){
	    	$scope.nod.subjectOfImplementation.name = $scope.nod.subjectOfImplementation.NAME;
			$scope.nod.subjectOfImplementation.value = $scope.nod.subjectOfImplementation.ORGPKVALUE;
    	}
    	if($scope.nod.responsibilityUnit != null){
    		$scope.nod.responsibilityUnit.name = $scope.nod.responsibilityUnit.NAME;
    		$scope.nod.responsibilityUnit.value = $scope.nod.responsibilityUnit.ORGPKVALUE;
    	}
    	$scope.nod = angular.copy($scope.nod);
        $http({
			method:'post',  
		    url: srvUrl + $scope.localUrl,
		    data: $.param({"nod":JSON.stringify($scope.removeHashKey($scope.nod))})
		}).success(function(result){
			$scope.localUrl = 'noticeDecisionDraftInfo/update.do';
			hide_Mask();
			 if (result.success) {
				 if (typeof ($scope.nod._id) == "undefined") {
					 $scope.nod._id = result.result_data;
				 }
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
    //提交
	$scope.showSubmitModal = function(){
		if($scope.nod.subjectOfImplementation != null){
	    	$scope.nod.subjectOfImplementation.name = $scope.nod.subjectOfImplementation.NAME;
			$scope.nod.subjectOfImplementation.value = $scope.nod.subjectOfImplementation.ORGPKVALUE;
    	}
    	if($scope.nod.responsibilityUnit != null){
    		$scope.nod.responsibilityUnit.name = $scope.nod.responsibilityUnit.NAME;
    		$scope.nod.responsibilityUnit.value = $scope.nod.responsibilityUnit.ORGPKVALUE;
    	}
		$.confirm("提交后将不可修改，确定提交？",function(){
			show_Mask();
			$http({
	 			method:'post',  
	 		    url: srvUrl + $scope.localUrl,
	 		    data: $.param({"nod":JSON.stringify($scope.removeHashKey($scope.nod))})
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
	 					hide_Mask();
	 					 $.alert(result.result_name);
	 					 var oldUrl=window.btoa(encodeURIComponent(escape("#/NoticeDecisionDraftList/1")))
	 					 $location.path("NoticeDecisionDraftDetailView/submitted/"+objId+"/"+oldUrl);
	 				});
	              } else {
	                  $.alert(result.result_name);
	              }
	 		});
		});
			
	}
	//新增时查询项目信息
    $scope.getProjectFormalReviewByID=function(id){
    	//查询项目基本信息
    	$http({
			method:'post',  
		    url: srvUrl + "noticeDecisionDraftInfo/querySaveDefaultInfo.do",
		    data: $.param({"businessId":id})
		}).success(function(result){
			$scope.pfr = result.result_data;
			//新增决策通知书
			if(action == "Create"){
				$scope.localUrl = 'noticeDecisionDraftInfo/create.do';
				if($scope.nod == null){
					$scope.nod = {};
				}
		        $scope.nod.projectFormalId = $scope.pfr._id;
		        $scope.nod.projectName = $scope.pfr.apply.projectName;
	  	        $scope.nod.reportingUnit={};
	  	        $scope.nod.reportingUnit.name = $scope.pfr.apply.reportingUnit.VALUE;
	  	        $scope.nod.reportingUnit.value = $scope.pfr.apply.reportingUnit.KEY;
	  	        $scope.nod.pertainArea = $scope.pfr.apply.pertainArea;
	  	        $scope.nod.projectNo = $scope.pfr.apply.projectNo;
	  	        $scope.nod.controllerVal="NoticeDecision";
	  	        $scope.nod.createBy = {name:$scope.credentials.userName, value:$scope.credentials.UUID};
	  	        //项目业务类型
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
				if($scope.nod.reviOfTotaInveType == null){
					$scope.nod.reviOfTotaInveType = "";
				}
			}else if(action == "Update"){
				//修改决策通知书
				//查询决策通知书
				$http({
					method:'post',  
					url: srvUrl + 'noticeDecisionDraftInfo/queryNoticeDecstion.do',
					data: $.param({"formalId":id})
				}).success(function(data){
					var noticeDecision = data.result_data;
	    			$scope.requireTask1 = noticeDecision.require1 != null && noticeDecision.require1 != '';
	    			$scope.requireTask2 = noticeDecision.require2 != null && noticeDecision.require2 != '';
	    			$scope.requireTask3 = noticeDecision.require3 != null && noticeDecision.require3 != '';
	    			
					$scope.nod = data.result_data;
					if($scope.nod.reviOfTotaInveType == null){
						$scope.nod.reviOfTotaInveType = "";
					}
					
					$scope.localUrl = 'noticeDecisionDraftInfo/update.do';
					if (null != $scope.nod.personLiable) {
						var haderNameArr = [], haderValueArr = [];
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
				});
			}
		});
    }

    //查询决策通知书详情信息
	   $scope.getNoticeDecstionByID=function(id){
		   //查询决策通知书
	        var  url = 'noticeDecisionDraftInfo/queryNoticeDecstion.do';
	        $http({
				method:'post',  
			    url: srvUrl + url,
			    data: $.param({"formalId":objId})
			}).success(function(data){
				var noticeDecision = data.result_data;
    			$scope.requireTask1 = noticeDecision.require1 != null && noticeDecision.require1 != '';
    			$scope.requireTask2 = noticeDecision.require2 != null && noticeDecision.require2 != '';
    			$scope.requireTask3 = noticeDecision.require3 != null && noticeDecision.require3 != '';
    			
    			
	                $scope.nod = data.result_data;
	                var haderNameArr = [], haderValueArr = [];
	                if (null != $scope.nod && null != $scope.nod.personLiable) {
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
	                if($scope.nod.subjectOfImplementation == null){
	                	$scope.nod.subjectOfImplementation = {};
	                }
	                
	                if($scope.nod.responsibilityUnit == null){
	                	$scope.nod.responsibilityUnit={};
	                }
	                
	                if($scope.nod.subjectOfImplementation != null){
	                	$scope.nod.subjectOfImplementation.NAME = $scope.nod.subjectOfImplementation.name;
	                	$scope.nod.subjectOfImplementation.ORGPKVALUE = $scope.nod.subjectOfImplementation.value;
	                }
	                if($scope.nod.responsibilityUnit){
	                	$scope.nod.responsibilityUnit.NAME = $scope.nod.responsibilityUnit.name;
	                	$scope.nod.responsibilityUnit.ORGPKVALUE = $scope.nod.responsibilityUnit.value;
	                }
	                //查询项目基本信息的业务类型
	             	$http({
	    				method:'post',  
	    			    url: srvUrl + "noticeDecisionDraftInfo/querySaveDefaultInfo.do",
	    			    data: $.param({"businessId":id})
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
	          						break;
	          					}
	          				}
	          				$scope.pfr.apply.serviceType=pt1NameArr.join(",");
	          			}
	    			});
			});
	    }
	   
	   if(action=="Create"){
	    	$scope.getProjectFormalReviewByID(objId);
	        $scope.titleName = "决策通知书新增";
	        if($scope.nod == null){
	        	$scope.nod = {};
	        }
	        if($scope.nod.subjectOfImplementation == null){
            	$scope.nod.subjectOfImplementation = {};
            }
            
            if($scope.nod.responsibilityUnit == null){
            	$scope.nod.responsibilityUnit={};
            }
	    }
	    if(action=="Update"){
	        $scope.getNoticeDecstionByID(objId);
	        $scope.titleName = "决策通知书修改";
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
    $scope.removeHashKey = function(obj) {
    	return angular.copy(obj)
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

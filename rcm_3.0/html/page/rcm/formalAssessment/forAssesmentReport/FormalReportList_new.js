/********
 * Created by dsl on 17/6/26.
 * 正式评审项目
 *********/
ctmApp.register.controller('FormalReportList_new', ['$http','$scope','$location','$routeParams','$filter',
function ($http,$scope,$location,$routeParams,$filter) {
	$scope.tabIndex = $routeParams.tabIndex;
//	$scope.queryObject = {};
//	$scope.hi_queryObject = {};
	if($scope.paginationConf.queryObj == null || $scope.paginationConf.queryObj == ''){
		$scope.paginationConf.queryObj = {};
	}
	if($scope.paginationConfes.queryObj == null || $scope.paginationConfes.queryObj == ''){
		$scope.paginationConfes.queryObj = {};
	}
	
	//初始化页面所需数据
	$scope.initData = function(){
		$scope.initUncommittedReport();
		$scope.initSubmittedReport();
	}
	
	//获取待提交评审报告的项目
	$scope.initUncommittedReport=function(){	
//		$scope.dataJson = {
//				projectName:$scope.queryObject.PROJECT_NAME,
//				createBy:$scope.queryObject.CREATEBY,
//				pertainareaname:$scope.queryObject.PERTAINAREANAME
//		}
		$http({
			method:'post',  
		    url:srvUrl+"formalReport/queryUncommittedReportByPage.do", 
		    data: $.param({
		    			"page":JSON.stringify($scope.paginationConf),
		    			"json":JSON.stringify($scope.paginationConf.queryObj)
		    			})
		}).success(function(data){
			if(data.success){
				$scope.uncommittedReport = data.result_data.list;
				$scope.paginationConf.totalItems = data.result_data.totalItems;
			}else{
				$.alert(data.result_name);
			}
		}).error(function(data,status,headers,config){
			$.alert(status);
		});
    };
    
    //获取已提交评审报告的项目
    $scope.initSubmittedReport = function(){
//    	$scope.dataJson = {
//    			projectName:$scope.hi_queryObject.HI_PROJECT_NAME,
//    			createBy:$scope.hi_queryObject.HI_CREATEBY,
//    			pertainareaname:$scope.hi_queryObject.HI_PERTAINAREANAME
//    	}
    	show_Mask();
    	$http({
    		method:'post',
    		url:srvUrl+'formalReport/querySubmittedReportByPage.do',
    		 data: $.param({
	    			"page":JSON.stringify($scope.paginationConfes),
	    			"json":JSON.stringify($scope.paginationConfes.queryObj)
	    			})
    	}).success(function(data){
    		if(data.success){
    			$scope.submittedReport = data.result_data.list;
                $scope.paginationConfes.totalItems = data.result_data.totalItems;
    		}else{
    			$.alert(data.result_name);
    		}
    		hide_Mask();
		}).error(function(data, status, headers, config){
			hide_Mask();
		});
    };
    
    $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage',  $scope.initUncommittedReport);
	$scope.$watch('paginationConfes.currentPage + paginationConfes.itemsPerPage',  $scope.initSubmittedReport);
    
    //批量删除待提交报告的项目中的报告
    $scope.batchDeleteFormalReport = function(){
    	var chkObjs = $("input[type=checkbox][name=uncommittedReportCheckbox]:checked");
    	if(chkObjs.length == 0){
    		$.alert("请选择要删除的数据！");
    		return false;
    	}
    	$.confirm("删除后不可恢复，确认删除吗？", function() {
    		var idsStr = "";
        	for(var i = 0; i < chkObjs.length; i++){
        		var chkValue = chkObjs[i].value.split("/") ;
        		var chkValue_len = chkValue.length;
        		idsStr = idsStr + chkValue[chkValue_len - 1] + ",";
        		//idsStr = idsStr + chkObjs[i].value + ",";
        	}
        	idsStr = idsStr.substring(0, idsStr.length - 1);
        	$http({
    			method:'post',  
    		    url:srvUrl+"formalReport/batchDeleteUncommittedReport.do", 
    		    data: $.param({"ids": idsStr})
    		}).success(function(data){
    			if(data.success){
    				$scope.initUncommittedReport();
    			}else{
    				$.alert(data);
    			}
    		}).error(function(data,status,headers, config){
        		$.alert(status);
        	});
    	});
    }
    
    $scope.updateReportInfo = function(){
    	var chkObjs = $("input[type=checkbox][name=uncommittedReportCheckbox]:checked");
    	if(chkObjs.length == 0){
    		$.alert("请选择要修改的数据！");
    		return false;
    	}
    	
    	if(chkObjs.length > 1){
    		$.alert("请只选择一条数据进行修改!");
    		return false;
    	}
    	var idsStr = "";
    	for(var i = 0; i < chkObjs.length; i++){
    		idsStr = idsStr + chkObjs[i].value + ",";
    	}
    	idsStr = idsStr.substring(0, idsStr.length - 1);
    	$location.path("/" + idsStr+"@2/"+$filter('encodeURI')('#/FormalReportList_new/0'));
    }
    
    //导出项目列表
    $scope.exportReportInfo = function(){
    	$http({
    		method:'post',
    		url:srvUrl+'formalReport/exportReportInfo.do'
    	}).success(function(data){
    		if(data.success){
    			var files = data.result_data.filePath;
    			var index = files.lastIndexOf(".");
                var str = files.substring(index + 1, files.length);
                var fileName = data.result_data.fileName;
    			window.location.href = srvUrl+"file/downloadFile.do?filepaths="+encodeURI(files)+"&filenames="+encodeURI(encodeURI("正式评审-"+fileName+"报告-单体模式."))+str;
    		}else{
    			$.alert("文档生成失败!");
    		}
    	}).error(function(data,status,headers,config){
			$.alert(status);
		});
    }
    
    $scope.r={};
    //列出未新建评审报告的项目
    $scope.listNotNewlyBuiltProject = function(){
    	$http({
    		method:'post',
    		url:srvUrl+'formalReport/queryNotNewlyBuiltProject.do'
    	}).success(function(data){
    		 if(data.success){
    			 $scope.pprs = data.result_data;
    		 }
    	}).error(function(data,status,headers, config){
    		$.alert(status);
    	});
    	
    	$scope.r.pmodel="FormalReviewReport/Create";
    }
    
    //新建评审报告
    $scope.forReport = function(model,uuid){
    	var ind = model.lastIndexOf("/");
    	var modelAction = model.substring(ind + 1,model.length);
    	if(modelAction == 'Update'){
    		$.alert("请选择项目模式!");
    		return false;
    	}else if(uuid == null || uuid == ""){
    		$.alert("请选择一个项目!");
    		return false;
    	}else{
    		var routePath = model.substring(0,ind);
    		 $('#addModal').modal('hide');
			 $location.path("/"+routePath+"/0/Create/"+uuid+"@2/"+$filter('encodeURI')('#/FormalReportList_new/0'));
    	}
    }
    
    //提交决策委员会材料
    $scope.formalBiddingInfo = function(){
    	var chkObjs = $("input[type=checkbox][name=uncommittedReportCheckbox]:checked");
    	var chkObjs_len = chkObjs.length;
    	if(chkObjs_len == 0){
    		$.alert("请选择一条数据！");
    		return false;
    	}
    	
    	if(chkObjs_len > 1){
    		$.alert("只能选择一条数据!");
    		return false;
    	}
    	
    	var businessId = "";
    	for(var i = 0; i < chkObjs.length; i++){
    		var chkValue = chkObjs[i].value.split("/") ;
    		var chkValue_len = chkValue.length;
    		businessId = chkValue[chkValue_len - 1];
    	}
    	
        $http({
    		method:'post',
    		url:srvUrl+'formalReport/selectPrjReviewView.do',
    		data: $.param({"businessId": businessId})
    	}).success(function(data){
            var result = data.result_data;
            if(""!=result.TASK_NAME && null!=result.TASK_NAME){
            	$.alert("当前节点为：("+result.TASK_NAME+"),不能提交决策委员会!");
                $location.path("/ProjectFormalReviewView/View/"+result.BUSINESSID);
            }else{
            	$.alert("还没有进行审批,不能提交决策委员会!");
                $location.path("/ProjectFormalReviewDetailView/Update/"+result.BUSINESSID);
            }
    	}).error(function(data,status,headers,config){
    		$.alert(status);
    	});
    
    }
	
	$scope.initData();

}]);
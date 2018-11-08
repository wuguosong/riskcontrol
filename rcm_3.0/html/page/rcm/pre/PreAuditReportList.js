/********
 * Created by dsl on 17/8/24.
 * 投标评审报告
 *********/
ctmApp.register.controller('PreAuditReportList', ['$http','$scope','$location','$routeParams','$filter',
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
		$scope.initUncommittedPreAuditReport();
		$scope.initSubmittedPreAuditReport();
	}
	
	//获取待提交评审报告的项目
	$scope.initUncommittedPreAuditReport = function(){	
//		$scope.dataJson = {
//				projectName:$scope.queryObject.PROJECT_NAME,
//				createBy:$scope.queryObject.CREATEBY,
//				pertainareaname:$scope.queryObject.PERTAINAREANAME
//		}
		$http({
			method:'post',  
		    url:srvUrl+"preAuditReport/queryUncommittedReportByPage.do", 
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
    $scope.initSubmittedPreAuditReport = function(){
//    	$scope.dataJson = {
//    			projectName:$scope.hi_queryObject.HI_PROJECT_NAME,
//    			createBy:$scope.hi_queryObject.HI_CREATEBY,
//    			pertainareaname:$scope.hi_queryObject.HI_PERTAINAREANAME
//    	}
    	show_Mask();
    	$http({
    		method:'post',
    		url:srvUrl+'preAuditReport/querySubmittedReportByPage.do',
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
    
    $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage',  $scope.initUncommittedPreAuditReport);
	$scope.$watch('paginationConfes.currentPage + paginationConfes.itemsPerPage',  $scope.initSubmittedPreAuditReport);
    
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
    		    url:srvUrl+"preAuditReport/batchDeletePreReport.do", 
    		    data: $.param({"ids": idsStr})
    		}).success(function(data){
    			if(data.success){
    				$scope.initUncommittedPreAuditReport();
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
    	$location.path("/" + idsStr+"/"+$filter('encodeURI')('#/PreAuditReportList/0'));
    }
    
    $scope.r={};
    //列出未新建评审报告的项目
    $scope.listNotNewlyPreAuditProject = function(){
    	$http({
    		method:'post',
    		url:srvUrl+'preAuditReport/queryNotNewlyPreAuditProject.do'
    	}).success(function(data){
    		 if(data.success){
    			 $scope.pprs = data.result_data;
    		 }
    	}).error(function(data,status,headers, config){
    		$.alert(status);
    	});
    	
    	$scope.r.pmodel="FormalReviewReport/Create";
    }
    
  //新建投资评审报告
    $scope.forPreReport=function(model,uuid,comId){
        if(model==null || model==""){
            $.alert("请选择项目模式!");
            return false;
        }else if(uuid==null || uuid=="") {
            $.alert("请选择项目!");
            return false;
        }else{
            $("#addModal").modal('hide');
            var id=uuid+"@"+model+"@1@"+comId;
            //通用模板
            //http://10.10.20.38/html/index.html#/PreReviewReport/Create/5924ebc222ddf21cc1750c06@normal@1@5924ebc222ddf21cc1750c06@preAssessment:4:50004@60317@210020@1
            
            //上传电子版预评审报告
            //http://10.10.20.38/html/index.html#/PreReviewReport/Create/5924ebc222ddf21cc1750c06@other@1@5924ebc222ddf21cc1750c06@preAssessment:4:50004@60317@210020@1
            
            ///PreReviewReport/:action/:id/:url
            //var routePath = model.substring(0,ind);
            ////$location.path("/"+routePath+"/0/Create/"+uuid+"@2/"+$filter('encodeURI')('#/FormalReportList_new/0'));
            $location.path("/PreReviewReport/Create/"+uuid+"@"+model+"@1@/"+$filter('encodeURI')('#/PreAuditReportList/0'));
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
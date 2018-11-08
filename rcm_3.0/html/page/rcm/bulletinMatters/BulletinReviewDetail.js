/********
 * Created by wufucan on 17/3/11.
 * 通报项目
 *********/
ctmApp.register.controller('BulletinMatters', ['$http','$scope','$location', 
                                               function ($http,$scope,$location) {
	$scope.initDefaultData = function(){
		var url = srvUrl + "bulletinInfo/queryListDefaultInfo.do";
		$http({
			method:'post',  
		    url: url
		}).success(function(result){
			var data = result.result_data;
			$scope.tbsxType = data.tbsxType;
		});
	};
	$scope.initDefaultData();
	//查询起草状态列表
	$scope.queryApplyList=function(){
		$http({
			method:'post',  
		    url:srvUrl+"bulletinInfo/queryApplyList.do", 
		    data: $.param({"page":JSON.stringify($scope.paginationConf)})
		}).success(function(data){
			if(data.success){
				$scope.bulletins = data.result_data.list;
                $scope.paginationConf.totalItems = data.result_data.totalItems;
			}else{
				$.alert(data.result_name);
			}
		});
    };
    //查询已提交列表
    $scope.queryApplyedList=function(){
    	$http({
			method:'post',  
		    url:srvUrl+"bulletinInfo/queryApplyedList.do", 
		    data: $.param({"page":JSON.stringify($scope.paginationConfes)})
		}).success(function(data){
			if(data.success){
				$scope.applyedBulletins = data.result_data.list;
                $scope.paginationConfes.totalItems = data.result_data.totalItems;
			}else{
				$.alert(data.result_name);
			}
		});
    };
    $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage',  $scope.queryApplyList);
    $scope.$watch('paginationConfes.currentPage + paginationConfes.itemsPerPage',  $scope.queryApplyedList);
    
    $scope.update = function(){
    	var chkObjs = $("input[type=checkbox][name=applyChk]:checked");
    	if(chkObjs.length == 0 || chkObjs.length > 1){
    		$.alert("请选择一条数据编辑！");
    		return false;
    	}
    	var businessId = $(chkObjs[0]).val();
        $location.path("/BulletinMattersDetail/"+businessId);
    };
    
    $scope.deleteBatch = function(){
    	var chkObjs = $("input[type=checkbox][name=applyChk]:checked");
    	if(chkObjs.length == 0){
    		$.alert("请选择要删除的数据！");
    		return false;
    	}
    	var idsStr = "";
    	for(var i = 0; i < chkObjs.length; i++){
    		idsStr = idsStr + chkObjs[i].value + ",";
    	}
    	idsStr = idsStr.substring(0, idsStr.length - 1);
    	$http({
			method:'post',  
		    url:srvUrl+"bulletinInfo/deleteByIds.do", 
		    data: $.param({"ids": idsStr})
		}).success(function(data){
			if(data.success){
				$scope.queryApplyList();
			}
			$.alert(data.result_name);
		});
    }
	
}]);

ctmApp.register.controller('BulletinMattersDetail', ['$http','$scope','$location', '$routeParams', '$filter','Upload',
 function ($http,$scope,$location, $routeParams, $filter, Upload) {
	var queryParamId = $routeParams.id;
	$scope.bulletin = {
		createTime: $filter("date")(new Date(), "yyyy-MM-dd HH:mm:ss"), 
		applyUser: {VALUE: $scope.credentials.UUID,NAME: $scope.credentials.userName},
		businessPerson: {}
	};
	$scope.initDefaultData = function(){
		if("0" == queryParamId){
			$scope.initCreate();
		}else{
			$scope.initUpdate();
		}
	};
	$scope.initCreate = function(){
		var url = srvUrl + "bulletinInfo/queryCreateDefaultInfo.do";
		$http({
			method:'post',  
		    url: url
		}).success(function(result){
			var data = result.result_data;
			$scope.bulletin.applyUnit = {
				NAME: data.REPORTINGUNIT_NAME,
				VALUE: data.REPORTINGUNIT_ID
			};
			$scope.bulletin.unitPerson = {
				NAME: data.COMPANYHEADER_NAME,
				VALUE: data.COMPANYHEADER_ID
			};
			$scope.bulletin.applyUser = {
				NAME: data.APPLYUSER_NAME,
				VALUE: data.APPLYUSER_ID
			};
			$scope.tbsxType = data.tbsxType;
			$scope.businessUsers = data.businessUsers;
		});
	};
	$scope.initUpdate = function(){
		var url = srvUrl + "bulletinInfo/queryUpdateDefaultInfo.do";
		$http({
			method:'post',  
		    url: url,
		    data: $.param({"businessId": queryParamId})
		}).success(function(result){
			var data = result.result_data;
			$scope.bulletin = data.bulletinMongo;
			$scope.tbsxType = data.tbsxType;
			$scope.businessUsers = data.businessUsers;
		});
	};
	$scope.initDefaultData();
	if(1>2){
		//已提交流程
//		$scope.wfInfo = {processInstanceId:processInstanceId};
	}else{
		//未启动流程
		$scope.wfInfo = {processKey:'bulletin'};
	}
	/**
	 * 修改事项类型事件
	 */
	$scope.changeTbsxType = function(){
		var tbsxTypeModel = $scope.tbsxTypeModel;
		if(tbsxTypeModel == null){
			return;
		}
		if(tbsxTypeModel == ""){
			$scope.bulletin.bulletinType = "";
			
			$scope.bulletin.businessPerson.NAME = "";
			$scope.bulletin.businessPerson.VALUE = "";
		}else{
			var jsonObj = JSON.parse(tbsxTypeModel);
			$scope.bulletin.bulletinType = {NAME:jsonObj.ITEM_NAME, VALUE:jsonObj.UUID};
			for(var i = 0; i < $scope.businessUsers.length; i++){
				var user = $scope.businessUsers[i];
				if(user.CODE == jsonObj.ITEM_CODE){
					$scope.bulletin.businessPerson.NAME = user.USERNAME;
					$scope.bulletin.businessPerson.VALUE = user.USER_ID;
					break;
				}
			}
		}
	}
	//保存
	$scope.save = function(callBack){
		if(!$("#myForm").valid()) {
			$.alert("有必填项未填！");
			return false;
		}
		var url = srvUrl + "bulletinInfo/saveOrUpdate.do";
		$http({
			method:'post', 
			url: url,
			data: $.param({"json": JSON.stringify($scope.bulletin)})
		}).success(function (data) {
			if (data.success) {
				$scope.bulletin._id = data.result_data;
				if (callBack && typeof callBack == 'function') {
					callBack();
				}
			}
			if(callBack == null){
				$.alert(data.result_name);
			}
		});
	};
	//新增附件
	$scope.addFileList = function(){
        function addBlankRow(array){
            var blankRow = {
                file_content:''
            };
            var size=0;
            for(attr in array){
                size++;
            }
            array[size]=blankRow;
        }
        if(undefined==$scope.bulletin){
            $scope.bulletin={fileList:[]};

        }
        if(undefined==$scope.bulletin.fileList){
            $scope.bulletin.fileList=[];
        }
        addBlankRow($scope.bulletin.fileList);
    };
    $scope.commonDdelete = function(){
        var commentsObj = $scope.bulletin.fileList;
        if(commentsObj!=null){
            for(var i=0;i<commentsObj.length;i++){
                if(commentsObj[i].selected){
                    commentsObj.splice(i,1);
                    i--;
                }
            }
        }
    };
    $scope.errorAttach=[];
    $scope.upload = function (file,errorFile, idx) {
        if(errorFile && errorFile.length>0){
        	var errorMsg = fileErrorMsg(errorFile);
            $scope.errorAttach[idx]={msg:errorMsg};
        }else if(file){
            var fileFolder = "bulletin/attachments/"+$filter("date")(new Date(), "yyyyMM");
            $scope.errorAttach[idx]={msg:''};
            Upload.upload({
                url:srvUrl+'common/RcmFile/upload',
                data: {file: file, folder:fileFolder}
            }).then(function (resp) {
                var retData = resp.data.result_data[0];
                $scope.bulletin.fileList[idx].files=retData;
            }, function (resp) {
                console.log('Error status: ' + resp.status);
            }, function (evt) {
                var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                $scope["progress"+idx]=progressPercentage == 100 ? "":progressPercentage+"%";
            });
        }
    };
    
}]);

ctmApp.register.controller('BulletinMattersDetailView', ['$http','$scope','$location', '$routeParams', '$filter',
 function ($http,$scope,$location, $routeParams, $filter) {
	var queryParamId = $routeParams.id;
	$scope.initDefaultData = function(){
		$scope.initData();
	};
	$scope.initData = function(){
		var url = srvUrl + "bulletinInfo/queryViewDefaultInfo.do";
		$http({
			method:'post',  
		    url: url,
		    data: $.param({"businessId": queryParamId})
		}).success(function(result){
			var data = result.result_data;
			$scope.bulletinOracle = data.bulletinOracle;
			$scope.bulletin = data.bulletinMongo;
			$scope.auditLogs = data.logs;
			$scope.initPage();
		});
	};
	$scope.initDefaultData();
	$scope.initPage = function(){
		if($scope.bulletinOracle.AUDITSTATUS=="1" || $scope.bulletinOracle.AUDITSTATUS=="2"){
			//流程已启动
			$("#submitBtn").hide();
			$scope.wfInfo.businessId = queryParamId;
			$scope.refreshImg = Math.random()+1;
		}else{
			//未启动流程
			$("#submitBtn").show();
		}
	}
	$scope.wfInfo = {processKey:'bulletin'};
	//提交
	$scope.showSubmitModal = function(){
		$scope.approve = {
			operateType: "submit",
			processKey: "bulletin",
			businessId: queryParamId,
			callbackSuccess: function(result){
				$.alert(result.result_name);
				$('#submitModal').modal('hide');
				$("#submitBtn").hide();
				$scope.initData();
			},
			callbackFail: function(result){
				$.alert(result.result_name);
			}
		};
		$('#submitModal').modal('show');
	};
}]);

ctmApp.register.controller('BulletinMaterial', ['$http','$scope','$location', '$routeParams', function ($http,$scope,$location, $routeParams) {

}]);
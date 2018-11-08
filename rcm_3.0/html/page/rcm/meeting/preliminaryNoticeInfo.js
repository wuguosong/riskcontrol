ctmApp.register.controller('PreliminaryNoticeInfo', ['$routeParams','$http','$scope','$location','Upload','$filter',
                                                function ($routeParams,$http,$scope,$location,Upload,$filter) {
	$scope.oldUrl = $routeParams.url;
	$scope.id =  $routeParams.id;
	$scope.action =  $routeParams.action;
	$scope.title = $scope.action == "create" ? "新增" : "修改";
	
    $scope.initData = function(){
    	$scope.noticeWyUserMapped={"nameField":"NAME","valueField":"VALUE"};
    	$scope.noticeFkUserMapped={"nameField":"ITEM_NAME","valueField":"ITEM_CODE"};
    	$scope.noticeQtUserMapped={"nameField":"NAME","valueField":"VALUE"};
    	if($scope.action == "create"){
    		$scope.noticeInfo = {};
    		$scope.noticeInfo.ATTACHMENT_OBJECT = {};
    		$scope.noticeInfo.noticeWyUser = [];
    		$scope.noticeInfo.noticeFkUser = [];
    		$scope.noticeInfo.noticeQtUser = [];
    	}else{
    		$scope.queryInfo();
    	}
    }
    
    // 查询会议通知信息详情
	$scope.queryInfo = function(){
		$http({
			method:'post',  
		    url: srvUrl + "preliminaryNotice/queryById.do",
		    data: $.param({"id":$scope.id})
		}).success(function(result){
			$scope.noticeInfo = result.result_data;
			
			if(null == $scope.noticeInfo.noticeWyUser){
				$scope.noticeInfo.noticeWyUser = [];
			}
    		
    		if(null == $scope.noticeInfo.noticeFkUser){
    			$scope.noticeInfo.noticeFkUser = [];
			}
    		
    		if(null == $scope.noticeInfo.noticeQtUser){
    			$scope.noticeInfo.noticeQtUser = [];
			}
			$scope.noticeInfo.ATTACHMENT_OBJECT = {"fileName":$scope.noticeInfo.ATTACHMENT_NAME,"filePath":$scope.noticeInfo.ATTACHMENT}
		});
	}
	
	//文件上传
	 $scope.errorAttach=[];
	 $scope.uploadAttachmentFile = function (file,errorFile, idx,event) {
        if(errorFile && errorFile.length>0){
        	var errorMsg = fileErrorMsg(errorFile);
            $scope.errorAttach[idx]={msg:errorMsg};
        }else if(file){
            var fileFolder = "premeeting/" + $scope.formatDate();
            $scope.errorAttach[idx]={msg:''};
            
            Upload.upload({
                url:srvUrl+'file/uploadFile.do',
                data: {file: file, folder:fileFolder}
            }).then(function (resp) {
            	$scope.noticeInfo.ATTACHMENT_OBJECT.filePath = resp.data.result_data[0].filePath;
            	$scope.noticeInfo.ATTACHMENT_OBJECT.fileName = resp.data.result_data[0].fileName;
            	
            	$scope.noticeInfo.ATTACHMENT = $scope.noticeInfo.ATTACHMENT_OBJECT.filePath;
        		$scope.noticeInfo.ATTACHMENT_NAME = $scope.noticeInfo.ATTACHMENT_OBJECT.fileName;
            }, function (resp) {
            	$.alert(resp.status);
            }, function (evt) {
                var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                $scope["progress"+idx]=progressPercentage == 100 ? "":progressPercentage+"%";
            });
        }
    };
    $scope.formatDate = function() {
        var date = new Date();
        var paddNum = function(num){
            num += "";
            return num.replace(/^(\d)$/,"0$1");
        }
        return date.getFullYear()+""+paddNum(date.getMonth()+1)+""+date.getDate();
    }
//    $scope.enterEvent = function(event){
//		 if(event.keyCode == 13){
//			 alert("禁用");
//			 return false;
//		 }
//	}
    
    // 查询会议通知信息详情
	$scope.save = function(callbackName){
		if($scope.noticeInfo.TITLE == null || $scope.noticeInfo.TITLE == ""){
			$.alert("主题必填!");return false;
		}
		show_Mask();
		var url = "";
		if($scope.noticeInfo.ID == null){
			url = "preliminaryNotice/create.do";
		}else{
			url = "preliminaryNotice/update.do";
		}
		$http({
			method:'post',  
		    url: srvUrl + url,
		    data: $.param({"json":JSON.stringify($scope.removeHashKey($scope.noticeInfo))})
		}).success(function(result){
			hide_Mask();
			$scope.noticeInfo.ID = result.result_data;
			
			if(callbackName == "submit"){
				$scope.goSubmit();
			}else{
				$.alert("保存成功");
			}
		}).error(function(data,status,headers,config){
			hide_Mask();
        	$.alert(status);
        });
	}
    $scope.removeHashKey = function(obj) {
    	return angular.copy(obj)
    }
	
    $scope.submit = function(){
		//验证
		if($scope.noticeInfo.TITLE == null || $scope.noticeInfo.TITLE == ""){
			$.alert("主题不能为空!");return false;
		}
		if($scope.noticeInfo.RELEASE_TIME == null || $scope.noticeInfo.RELEASE_TIME == ""){
			$.alert("发布时间不能为空!");return false;
		}
		if($scope.noticeInfo.CONTENT == null || $scope.noticeInfo.CONTENT == ""){
			$.alert("内容不能为空!");return false;
		}
		if($scope.noticeInfo.ATTACHMENT == null || $scope.noticeInfo.ATTACHMENT == ""){
			$.alert("项目详情不能为空!");return false;
		}
		if($scope.noticeInfo.noticeWyUser == null || $scope.noticeInfo.noticeWyUser.length == 0){
			$.alert("决策会委员不能为空!");return false;
		}
		//保存
		$scope.save("submit");
    }
    //提交
	$scope.goSubmit = function(){
		show_Mask();
		$http({
			method:'post',  
		    url: srvUrl + "preliminaryNotice/submit.do",
		    data: $.param({"id":$scope.noticeInfo.ID})
		}).success(function(result){
			hide_Mask();
			$.alert("提交成功");
			$location.path("/preliminaryNoticeList");
		}).error(function(data,status,headers,config){
			hide_Mask();
        	$.alert(status);
        });
	}
	
	$scope.initData();
}]);
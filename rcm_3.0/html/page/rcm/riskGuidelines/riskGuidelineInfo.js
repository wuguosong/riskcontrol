ctmApp.register.controller('riskGuidelineInfo', ['$routeParams','$http','$scope','$location','Upload','$filter',
                                                function ($routeParams,$http,$scope,$location,Upload,$filter) {
	$scope.oldUrl = $routeParams.url;
	$scope.objId =  $routeParams.id;
    $scope.actionpam =$routeParams.action;
    
    $scope.rideGuidelineInfo = {};
    $scope.Info ={};
    $scope.formatDate = function() {
        var date = new Date();
        var paddNum = function(num){
            num += "";
            return num.replace(/^(\d)$/,"0$1");
        }
        return date.getFullYear()+""+paddNum(date.getMonth()+1)+""+date.getDate();
    }
    
    $scope.init = function(){
    	if ($scope.actionpam == 'Create') {
            $scope.titleName = "风险指引--新增";
        } else if ($scope.actionpam == 'Modify') {
        	$scope.titleName = "风险指引--修改";
            $scope.getRideGuidelineInfo($scope.actionpam);
        } 
    }
    
    // 查询公告信息详情
	$scope.getRideGuidelineInfo = function(id){
		
		$http({
			method:'post',  
		    url: srvUrl + "riskGuidelinesform/queryRideGuidelineInfo.do",
		    data: $.param({"id":$scope.objId})
		}).success(function(result){
			$scope.rideGuidelineInfo = result.result_data;
			if($scope.rideGuidelineInfo.CONTENT != undefined || "" != $scope.rideGuidelineInfo.CONTENT){
				$scope.rideGuidelineInfo.CONTENT = $scope.rideGuidelineInfo.CONTENT.replace(/<\/br>/g,'\n');
			}
		}).error(function(data,status,headers,config){
        	$.alert(status);
        });
	}
	
	//保存公告信息
	$scope.saveRideGuideline = function(callBack){
		show_Mask();
		var url = "";
		if($scope.objId == "0"){
			url = "riskGuidelinesform/addRiskGuideline.do";
		}else{
			url = "riskGuidelinesform/modifyRiskGuideline.do";
		}
		
		$http({
			method:'post',  
		    url: srvUrl + url,
		    data: $.param({"json":JSON.stringify($scope.rideGuidelineInfo)})
		}).success(function(result){
			if(result.success){
				$scope.rideGuidelineInfo.ID = result.result_data;
				$scope.objId = result.result_data ;
				if (typeof callBack == 'function') {
                    callBack();
                } else {
                	$.alert(result.result_name);
//    				$("#savebtn").hide();
    				hide_Mask();
                }
			}else{
				$.alert(result.result_name);
				hide_Mask();
			}
		}).error(function(data,status,headers,config){
        	$.alert(status);
        	hide_Mask();
        });
	}
	
	//提交公告信息
	$scope.submitRideGuideline = function(){
		$scope.saveRideGuideline(function(){
			$http({
				method:'post',  
			    url: srvUrl + "riskGuidelinesform/submitRideGuideline.do",
			    data: $.param({"id":$scope.objId})
			}).success(function(result){
				if(result.success){
					$.alert(result.result_name);
					$scope.hideOrDisableAttr();
					hide_Mask();
				}else{
					$.alert(result.result_name);
					hide_Mask();
				}
			}).error(function(data,status,headers,config){
	        	$.alert(status);
	        	hide_Mask();
	        });
		});
	}
	
	//文件上传
	 $scope.errorAttach=[];
	 $scope.uploadNotificationAttachment = function (file,errorFile, idx) {
	        if(errorFile && errorFile.length>0){
	        	var errorMsg = fileErrorMsg(errorFile);
	        	$scope.errorAttach[idx]={msg:errorMsg};
	        }else if(file){
	            var fileFolder = "rideGuideline/";
	            
	            if($scope.actionpam == 'Create'){
	            	fileFolder = fileFolder + $scope.formatDate();
	            }else{
	            	if($scope.notification != undefined){
	            		fileFolder = $scope.notification.filePath;
	            	}else{
	            		fileFolder = fileFolder + $scope.formatDate();
	            	}
	            }
	            
	            $scope.errorAttach[idx]={msg:''};
	            
	            Upload.upload({
	                url:srvUrl+'file/uploadFile.do',
	                data: {file: file, folder:fileFolder}
	            }).then(function (resp) {
	            	$scope.rideGuidelineInfo.FILE_PATH = resp.data.result_data[0].filePath;
	            	$scope.rideGuidelineInfo.FILE_NAME = resp.data.result_data[0].fileName;
	            }, function (resp) {
	            	$.alert(resp.status);
	            }, function (evt) {
	                var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
	                $scope["progress"+idx]=progressPercentage == 100 ? "":progressPercentage+"%";
	            });
	        }
	    };
	    
	    //文件下载
	    $scope.downLoadFile = function(filePath,fileName){
	        window.location.href = srvUrl+"file/downloadFile.do?filepaths="+encodeURI(filePath)+"&filenames="+encodeURI(encodeURI(fileName));
	    }
	    
	    $scope.hideOrDisableAttr = function(){
//	    	$('#title').attr("readonly","readonly");
//            $('#desc').attr("readonly","readonly");
//            $("#fileSelectDiv").hide();
//			$("#savebtn").hide();
			$("#submitbtn").hide();
	    }
	    
	//将风险内容的换行符替换为</br>
	$scope.keyUp = function(event){
		if(event.keyCode == 13){
		  var content = new Array();
		  content.push($scope.rideGuidelineInfo.CONTENT);
		   if(content.length > 0){
			    var contents = content[0];
			    $scope.Info.CONTENTS = contents.replace(/\n/g,"</br>");
		    }
		 }
	}
	
	    $scope.init();
}]);
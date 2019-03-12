ctmApp.register.controller('changeFile', [ '$http', '$scope', '$location','Upload',
function($http, $scope, $location,Upload) {
	$scope.upload = function (file,errorFile, idx) {
    	if(errorFile && errorFile.length>0){
    		var errorMsg = fileErrorMsg(errorFile);
    		$scope.errorMsg[idx]={msg:errorMsg};
    	}else if(file){
    		if($scope.file.toPath == null || $scope.file.toPath == ''){
    			$.alert("请填写文件路径！");
    			return ;
    		}
    		$.confirm("是否确认替换文件！",function(){
    			Upload.upload({
        			url:srvUrl+'common/RcmFile/change',
        			data: {file: file, folder:'',typeKey:'changeFrontFilePath',toPath:$scope.file.toPath}
        		}).then(function (resp) {
        			var retData = resp.data.result_data[0];
        			if($scope.file == null){
        				$scope.file={};
        			}
        			$scope.file.fileName = retData.fileName;
        			$scope.file.filePath = retData.filePath;
        			$.alert("文件替换成功！");
        		}, function (resp) {
        			$.alert(resp.status);
        		}, function (evt) {
        			var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                    $scope["progress"+idx]=progressPercentage == 100 ? "":progressPercentage+"%";
        		});
    		})
    		
    	}
    };
    
    $scope.queryFiles = function(){
    	show_Mask();
		$http({
			method:'post',  
		    url: srvUrl + "file/queryFile.do",
		    data: $.param({"url":$scope.url})
		}).success(function(result){
			hide_Mask();
			if(result.success){
				$scope.fileList = result.result_data;
			}else{
				$.alert(result.result_name);
			}
		});
    }
	
    $scope.clickDir = function(f){
    	$scope.url = f.filePath;
    	$scope.queryFiles();
    }
    
} ]);

/********
 * Created by yaphet on 17/12/27.
 * 通报项目审阅
 *********/
ctmApp.register.controller('bulletinReviewFileDetail', ['Upload','$http','$scope','$location', '$routeParams', '$filter', '$routeParams', 
function (Upload,$http,$scope,$location, $routeParams, $filter,$routeParams) {
	$scope.oldUrl = $routeParams.url;
	$scope.businessId = $routeParams.id;
	$scope.initDefaultData = function(){
		$scope.initData();
	};
	$scope.initData = function(){
		var url = srvUrl + "bulletinReview/queryViewDefaultInfo.do";
		$http({
			method:'post',  
		    url: url,
		    data: $.param({"businessId": $scope.businessId})
		}).success(function(result){
			var data = result.result_data;
			$scope.bulletinOracle = data.bulletinOracle;
			$scope.bulletin = data.bulletinMongo;
		});
	};
	$scope.saveFile = function(){
		var saveSuccessFlag = {
			"baseFile":false,
			"reviewLeaderAttachment":false,
			"legalLeaderAttachment":false,
			"riskLeaderAttachment":false
		};
		$http({
			method:'post',  
		    url:srvUrl+"bulletinInfo/updateBaseFile.do",
		    data: $.param({
		    	"businessId":$scope.businessId,
		    	"attachment":JSON.stringify($scope.bulletin.fileList)
		    	})
		}).success(function(result){
			if(!result.success){
				$.alert(result.result_name);
			}else{
				saveSuccessFlag.baseFile = true;
				if(saveSuccessFlag.baseFile && 
					saveSuccessFlag.reviewLeaderAttachment && 
					saveSuccessFlag.legalLeaderAttachment && 
					saveSuccessFlag.riskLeaderAttachment ){
					$.alert(result.result_name);
				}
			}
		});
		$http({
			method:'post',  
			url:srvUrl+"bulletinInfo/saveReviewLeaderAttachment.do",
			data: $.param({
				"businessId":$scope.businessId,
				"attachment":JSON.stringify($scope.bulletin.reviewLeaderAttachment)
			})
		}).success(function(result){
			if(!result.success){
				$.alert(result.result_name);
			}else{
				saveSuccessFlag.reviewLeaderAttachment = true;
				if(saveSuccessFlag.baseFile && 
						saveSuccessFlag.reviewLeaderAttachment && 
						saveSuccessFlag.legalLeaderAttachment && 
						saveSuccessFlag.riskLeaderAttachment ){
						$.alert(result.result_name);
					}
			}
		});
		
		$http({
			method:'post',  
			url:srvUrl+"bulletinInfo/saveLegalLeaderAttachment.do",
			data: $.param({
				"businessId":$scope.businessId,
				"attachment":JSON.stringify($scope.bulletin.legalLeaderAttachment)
			})
		}).success(function(result){
			if(!result.success){
				$.alert(result.result_name);
			}else{
				saveSuccessFlag.legalLeaderAttachment = true;
				if(saveSuccessFlag.baseFile && 
						saveSuccessFlag.reviewLeaderAttachment && 
						saveSuccessFlag.legalLeaderAttachment && 
						saveSuccessFlag.riskLeaderAttachment ){
						$.alert(result.result_name);
					}
			}
		});
		
		$http({
			method:'post',  
			url:srvUrl+"bulletinInfo/saveRiskLeaderAttachment.do",
			data: $.param({
				"businessId":$scope.businessId,
				"attachment":JSON.stringify($scope.bulletin.riskLeaderAttachment)
			})
		}).success(function(result){
			if(!result.success){
				$.alert(result.result_name);
			}else{
				saveSuccessFlag.riskLeaderAttachment = true;
				if(saveSuccessFlag.baseFile && 
						saveSuccessFlag.reviewLeaderAttachment && 
						saveSuccessFlag.legalLeaderAttachment && 
						saveSuccessFlag.riskLeaderAttachment ){
						$.alert(result.result_name);
					}
				
			}
		});
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
                $.alert("文件替换成功！请执行保存操作！否则操作无效！");
            }, function (resp) {
                console.log('Error status: ' + resp.status);
            }, function (evt) {
                var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                $scope["progress"+idx]=progressPercentage == 100 ? "":progressPercentage+"%";
            });
        }
    }
    //新增基础附件
	$scope.addFileList = function(){
        function addBlankRow(array){
            var blankRow = {
                file_content:'',
                selected: false
            };
            var size=array.length;
            array[size]=blankRow;
        }
        if(undefined==$scope.bulletin){
            $scope.bulletin={fileList:[]};

        }
        if(undefined==$scope.bulletin.fileList){
            $scope.bulletin.fileList=[];
        }
        addBlankRow($scope.bulletin.fileList);
    }
	//新增风控附件
	$scope.addFileList1 = function(){
    	function addBlankRow1(array){
    		var blankRow = {
    				newFile:true
    		}
    		var size = array.length;
    		array[size]=blankRow;
    	}
    	
    	var reviewLeader = $scope.bulletin.taskallocation.reviewLeader;
    	var riskLeader = $scope.bulletin.taskallocation.riskLeader;
    	
    	if(reviewLeader != null && reviewLeader != "" && reviewLeader.VALUE != null){
    		console.log(reviewLeader);
    		if(undefined==$scope.bulletin.reviewLeaderAttachment){
                $scope.bulletin.reviewLeaderAttachment=[];
            }
        	addBlankRow1($scope.bulletin.reviewLeaderAttachment);
    	}
    	if(riskLeader != null && riskLeader != "" && riskLeader.VALUE != null){
    		console.log(riskLeader);
    		if(undefined==$scope.bulletin.riskLeaderAttachment){
    			 $scope.bulletin.riskLeaderAttachment=[];
            }
        	addBlankRow1($scope.bulletin.riskLeaderAttachment);
    	}
    	
    	
    }
    //删除风控附件
    $scope.deleteFileList = function(){
    	var legalLeaderAttachment = $scope.bulletin.legalLeaderAttachment;
    	if(legalLeaderAttachment!=null){
    		for(var i=0;i<legalLeaderAttachment.length;i++){
    			if(legalLeaderAttachment[i].selected){
    				legalLeaderAttachment.splice(i,1);
    				i--;
    			}
    		}
    	}
    	var reviewLeaderAttachment = $scope.bulletin.reviewLeaderAttachment;
    	if(reviewLeaderAttachment!=null){
    		for(var i=0;i<reviewLeaderAttachment.length;i++){
    			if(reviewLeaderAttachment[i].selected){
    				reviewLeaderAttachment.splice(i,1);
    				i--;
    			}
    		}
    	}
    	var riskLeaderAttachment = $scope.bulletin.riskLeaderAttachment;
    	if(riskLeaderAttachment!=null){
    		for(var i=0;i<riskLeaderAttachment.length;i++){
    			if(riskLeaderAttachment[i].selected){
    				riskLeaderAttachment.splice(i,1);
    				i--;
    			}
    		}
    	}
    }
    //删除基础附件
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
    }
    //附件列表---->上传附件---->评审负责人
    $scope.errorAttach=[];
    $scope.uploadReviewLeaderAttachment = function (file,errorFile, idx) {
        if(errorFile && errorFile.length>0){
        	var errorMsg = fileErrorMsg(errorFile);
            $scope.errorAttach[idx]={msg:errorMsg};
        }else if(file){
            var fileFolder = "bulletin/review/";
            var dates=$scope.bulletin.createTime;
            var no=$scope.bulletin.id;
            var strs= new Array(); //定义一数组
            strs=dates.split("-"); //字符分割
            dates=strs[0]+strs[1]; //分割后的字符输出
            fileFolder=fileFolder+dates+"/"+no;

            $scope.errorAttach[idx]={msg:''};
            Upload.upload({
                url:srvUrl+'file/uploadFile.do',
                data: {file: file, folder:fileFolder}
            }).then(function (resp) {
                var retData = resp.data.result_data[0];
                $scope.bulletin.reviewLeaderAttachment[idx]=retData;
                $.alert("文件替换成功！请执行保存操作！否则操作无效！");
            }, function (resp) {
                $.alert(resp.status);
            }, function (evt) {
                var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                $scope["progress"+idx]=progressPercentage == 100 ? "":progressPercentage+"%";
            });
        }
    }
    //附件列表---->上传附件---->法律负责人
    $scope.uploadLegalLeaderAttachment = function (file,errorFile, idx) {
        if(errorFile && errorFile.length>0){
        	var errorMsg = fileErrorMsg(errorFile);
            $scope.errorAttach[idx]={msg:errorMsg};
        }else if(file){
            var fileFolder = "bulletin/legal/";
            var dates=$scope.bulletin.createTime;
            var no=$scope.bulletin.id;
            var strs= new Array(); //定义一数组
            strs=dates.split("-"); //字符分割
            dates=strs[0]+strs[1]; //分割后的字符输出
            fileFolder=fileFolder+dates+"/"+no;

            $scope.errorAttach[idx]={msg:''};
            Upload.upload({
                url:srvUrl+'file/uploadFile.do',
                data: {file: file, folder:fileFolder}
            }).then(function (resp) {
                var retData = resp.data.result_data[0];
                $scope.bulletin.legalLeaderAttachment[idx]=retData;
                $.alert("文件替换成功！请执行保存操作！否则操作无效！");
            }, function (resp) {
                $.alert(resp.status);
            }, function (evt) {
                var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                $scope["progress"+idx]=progressPercentage == 100 ? "":progressPercentage+"%";
            });
        }
    };
    //附件列表---->上传附件---->风控负责人
    $scope.uploadRiskLeaderAttachment = function (file,errorFile, idx) {
    	if(errorFile && errorFile.length>0){
    		var errorMsg = fileErrorMsg(errorFile);
    		$scope.errorAttach[idx]={msg:errorMsg};
    	}else if(file){
    		var fileFolder = "bulletin/risk/";
    		var dates=$scope.bulletin.createTime;
    		var no=$scope.bulletin.id;
    		var strs= new Array(); //定义一数组
    		strs=dates.split("-"); //字符分割
    		dates=strs[0]+strs[1]; //分割后的字符输出
    		fileFolder=fileFolder+dates+"/"+no;
    		
    		$scope.errorAttach[idx]={msg:''};
    		Upload.upload({
    			url:srvUrl+'file/uploadFile.do',
    			data: {file: file, folder:fileFolder}
    		}).then(function (resp) {
    			var retData = resp.data.result_data[0];
    			$scope.bulletin.riskLeaderAttachment[idx]=retData;
    			$.alert("文件替换成功！请执行保存操作！否则操作无效！");
    		}, function (resp) {
    			$.alert(resp.status);
    		}, function (evt) {
    			var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
    			$scope["progress"+idx]=progressPercentage == 100 ? "":progressPercentage+"%";
    		});
    	}
    };
    
	$scope.initDefaultData();
}]);

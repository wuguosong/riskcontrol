ctmApp.register.controller('PreBiddingInfoMeetingReview', ['$http','$scope','$location','$routeParams','Upload',
 function ($http,$scope,$location,$routeParams,Upload) {
	$scope.oldUrl = $routeParams.url;
    //申请报告ID
    var objId = $routeParams.id;
    $scope.preBidding={};
  //初始化页面所需数据
    $scope.initData = function(){
    	 $scope.selectFlag = 'false';
    	 $scope.hasWaiting = true;
    	 $('#content-wrapper input').attr("disabled","disabled");
         $('textarea').attr("readonly","readonly");
         $('select').attr("readonly","readonly");
		$scope.getByID(objId);
		$scope.getSelectTypeByCodetype('14');
	}
	 $scope.getByID = function(businessId){
	        $http({
				method:'post',  
			    url:srvUrl+"preBidding/getByBusinessId.do", 
			    data: $.param({"businessId":businessId})
			}).success(function(data){
				 $scope.pfr  = data.result_data.preMongo;
		         $scope.preBidding  = $scope.pfr;
		         $scope.preBidding.id = $scope.pfr.id;
		         $scope.applyDate = data.result_data.applyDate;
		         $scope.stage = data.result_data.stage;
		         $scope.reportOracle = data.result_data.reportOracle;
		         //处理附件列表
		         $scope.reduceAttachment($scope.pfr.attachment);
		         //新增附件类型
		         $scope.attach  = data.result_data.attach;
		         //控制新增文件
		         $scope.newPfr  = $scope.pfr;
		         var ptNameArr=[],fgNameArr=[],fgValueArr=[],investmentaNameArr=[];
		         var pt=$scope.pfr.apply.projectType;
		         if(null!=pt && pt.length>0){
		            for(var i=0;i<pt.length;i++){
		                ptNameArr.push(pt[i].VALUE);
		             }
		            $scope.pfr.apply.projectType=ptNameArr.join(",");
		         }
			}).error(function(data,status,header,config){
				$.alert(status);
			});
	    }
	 
	//处理附件列表
    $scope.reduceAttachment = function(attachment){
    	$scope.newAttachment = [];
			for(var i in attachment){
	    		var files = attachment[i].files;
	    		if(files!=null && undefined!=files){
	    			var item_name = attachment[i].ITEM_NAME;
	    			var uuid = attachment[i].UUID;
	    			for(var j in files){
	    				files[j].ITEM_NAME=item_name;
	    				files[j].UUID=uuid;
	    				//只显示选中的附件
		    				if($scope.isValueExist($scope.preBidding.policyDecision.decisionMakingCommitteeStaffFiles, 'filePath', files[j].filePath))
		    				{
		    					$scope.newAttachment.push(files[j]);
		    				}
	    			}
	    		}
	    	}
    }
	    
	$scope.downLoadFormalBiddingInfoFile = function(filePath,filename){
		var isExists = validFileExists(filePath);
    	if(!isExists){
    		$.alert("要下载的文件已经不存在了！");
    		return false;
    	}
    	if(filename!=null && filename.length>12){
    		filename = filename.substring(0, 12)+"...";
    	}else{
    		filename = filename.substring(0,filename.lastIndexOf("."));
    	}
    	
        if(undefined!=filePath && null!=filePath){
            var index = filePath.lastIndexOf(".");
            var str = filePath.substring(index + 1, filePath.length);
            var url = srvUrl+"file/downloadFile.do?filepaths="+encodeURI(filePath)+"&filenames="+encodeURI(encodeURI(filename + "-正式评审报告.")) + str;
            
            var a = document.createElement('a');
    	    a.id = 'tagOpenWin';
    	    a.target = '_blank';
    	    a.href = url;
    	    document.body.appendChild(a);

    	    var e = document.createEvent('MouseEvent');     
    	    e.initEvent('click', false, false);     
    	    document.getElementById("tagOpenWin").dispatchEvent(e);
    		$(a).remove();
        }else{
            $.alert("附件未找到！");
            return false;
        }
	}
	$scope.downLoadBiddingFile = function(idx){
    	var isExists = validFileExists(idx.filePath);
    	if(!isExists){
    		$.alert("要下载的文件已经不存在了！");
    		return;
    	}
		var filePath = idx.filePath, fileName = idx.fileName;
		if(fileName!=null && fileName.length>22){
			var extSuffix = fileName.substring(fileName.lastIndexOf("."));
			fileName = fileName.substring(0, 22);
			fileName = fileName + extSuffix;
    	}
		
		var url = srvUrl+"file/downloadFile.do?filepaths="+encodeURI(filePath)+"&filenames="+encodeURI(encodeURI(fileName));
		var a = document.createElement('a');
	    a.id = 'tagOpenWin';
	    a.target = '_blank';
	    a.href = url;
	    document.body.appendChild(a);

	    var e = document.createEvent('MouseEvent');     
	    e.initEvent('click', false, false);     
	    document.getElementById("tagOpenWin").dispatchEvent(e);
		$(a).remove();
	}
	
	 $scope.getSelectTypeByCodetype = function(typeCode){
        var  url = 'common/commonMethod/selectDataDictionByCode';
        $scope.httpData(url,typeCode).success(function(data){
            if(data.result_code == 'S'){
                $scope.projectlisttype=data.result_data;
            }else{
                alert(data.result_name);
            }
        });
    }
    $scope.initData();
}]);
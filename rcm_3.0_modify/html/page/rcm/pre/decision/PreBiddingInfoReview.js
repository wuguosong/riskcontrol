ctmApp.register.controller('PreBiddingInfoReview', ['$http','$scope','$location','$routeParams','Upload',
 function ($http,$scope,$location,$routeParams,Upload) {
	$scope.oldUrl = $routeParams.url;
	var businessId = $routeParams.id;
    $scope.preBidding={};
    $scope.FormatDate = function() {
        var date = new Date();
        var paddNum = function(num){
            num += "";
            return num.replace(/^(\d)$/,"0$1");
        }
        return date.getFullYear()+"-"+paddNum(date.getMonth()+1)+"-"+date.getDate();
    }
    
  //初始化页面所需数据
	$scope.initData = function(){
		$scope.getByID(businessId);
		//$scope.getNoticeDecstionByBusinessId(businessId);
		$scope.getSelectTypeByCodetype('14');
	}
	$scope.getNoticeDecstionByBusinessId = function(businessId){
        $http({
			method:'post',
		    url:srvUrl+"noticeDecisionInfo/getNoticeDecstionByBusinessId.do", 
		    data: $.param({"businessId":businessId})
		}).success(function(data){
	         $scope.noticeDecision = data.result_data;
		}).error(function(data,status,header,config){
			$.alert(status);
		});
    }
	
	 $scope.getByID = function(projectFormalId){
	        $http({
				method:'post',
			    url:srvUrl+"preBidding/findFormalAndReport.do", 
			    data: $.param({"projectFormalId":projectFormalId})
			}).success(function(data){
				 $scope.pfr  = data.result_data.Formal;
		         $scope.preBidding  = $scope.pfr.reviewReport;
		         $scope.meetInfo = data.result_data.MeetInfo;
		         $scope.applyDate = data.result_data.applyDate;
		         //处理附件列表
		         $scope.reduceAttachment(data.result_data.Formal.attachment,$scope.preBidding);
		         //新增附件类型
		         $scope.attach  = data.result_data.attach;
		         //控制新增文件
		         $scope.newPfr  = data.result_data.Formal;
		         $scope.formalID = $scope.pfr.id;
		         var ptNameArr=[],fgNameArr=[],fgValueArr=[],investmentaNameArr=[];
		         var pt=$scope.pfr.apply.projectType;
		         if(null!=pt && pt.length>0){
		            for(var i=0;i<pt.length;i++){
		                ptNameArr.push(pt[i].VALUE);
		             }
		            $scope.pfr.apply.projectType=ptNameArr.join(",");
		         }
		         
		         //设置页面为只读
	        	 $('#content-wrapper input').attr("disabled","disabled");
	             $('textarea').attr("readonly","readonly");
	             $('select').attr("readonly","readonly");
	             $scope.isBtnShow = true;
			}).error(function(data,status,header,config){
				$.alert(status);
			});
	    }
	 
	//处理附件列表
	    $scope.reduceAttachment = function(attachment,preBidding){
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
		    				if($scope.isValueExist(preBidding.policyDecision.decisionMakingCommitteeStaffFiles, 'filePath', files[j].filePath))
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
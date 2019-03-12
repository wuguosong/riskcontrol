ctmApp.register.controller('PreBiddingInfoView', ['$http','$scope','$location','$routeParams','Upload',
 function ($http,$scope,$location,$routeParams,Upload) {
	$scope.oldUrl = $routeParams.url;
    //申请报告ID
    var complexId = $routeParams.id;
    var params = complexId.split("@");
    var objId = params[0];
    var isView = params[1];
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
    $scope.hasWaiting = false;
    $scope.initData = function(){
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
		         
		         if(!($scope.stage == '6' || $scope.stage == '7')){
		        	 $scope.selectFlag = 'true';
		        	 $scope.hasWaiting = true;
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
		    				$scope.newAttachment.push(files[j]);
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
	//处理附件列表,为提交包装数据
    $scope.reduceAttachmentForSubmit = function(attachment){
    	var newAttachment = $scope.newAttachment;
    	
    	var now = $scope.getDate();
    	//根据uuid处理版本号，上传日期当前
    	//获取之前uuid
    	for(var j  = 0 in $scope.newPfr.attachment){
    		for(var i = 0 in newAttachment){
    			if(newAttachment[i].newFile){
    				if(newAttachment[i].newItem == undefined){
    					$.alert("资源名称不能为空!");
    					return false;
    				}
    				newAttachment[i].ITEM_NAME=newAttachment[i].newItem.ITEM_NAME;
    				newAttachment[i].UUID=newAttachment[i].newItem.UUID;
    				
    				$scope.newAttachment[i].ITEM_NAME=newAttachment[i].newItem.ITEM_NAME;
    				$scope.newAttachment[i].UUID=newAttachment[i].newItem.UUID;
    				
	    			if(newAttachment[i].UUID==$scope.newPfr.attachment[j].UUID){
	    				//之前版本号
	    				//console.log($scope.newPfr.attachment[j].files);
	    				//处理版本号问题
	    				if(undefined==$scope.newPfr.attachment[j].files){
	    					newAttachment[i].version=1;
	    				}else{
	    					var versionNum = $scope.newPfr.attachment[j].files.length;
	    					newAttachment[i].version=versionNum*1+1;
	    				}
	    				newAttachment[i].newFile=false;
	    				newAttachment[i].upload_date=now;
	    				newAttachment[i].programmed={};
	    				newAttachment[i].programmed.name=$scope.credentials.userName;
	    				newAttachment[i].programmed.value=$scope.credentials.UUID;
	    				newAttachment[i].approved={};
	    				newAttachment[i].approved.name=$scope.credentials.userName;
	    				newAttachment[i].approved.value=$scope.credentials.UUID;
	    				if(undefined==$scope.newPfr.attachment[j].files){
	    					$scope.newPfr.attachment[j].files=[];
	    					$scope.newPfr.attachment[j].files.push(newAttachment[i]);
	    				}else{
	    					$scope.newPfr.attachment[j].files.push(newAttachment[i]);
	    				}
	    			}
	    			
    			}
    		}
    	}
    	return $scope.newPfr.attachment;
    }
    $scope.getDate = function(){
		var myDate = new Date();
		//获取当前年
		var year = myDate.getFullYear();
		//获取当前月
		var month = myDate.getMonth() + 1;
		//获取当前日
		var date = myDate.getDate();
		var h = myDate.getHours(); //获取当前小时数(0-23)
		var m = myDate.getMinutes(); //获取当前分钟数(0-59)
		var s = myDate.getSeconds();
		var now = year + '-' + month + "-" + date + " " + h + ':' + m + ":" + s;
		return now;
	}
	$scope.validateVoidFile = function(){
		for(var i in $scope.newAttachment){
			if($scope.newAttachment[i].fileName == null || $scope.newAttachment[i] == ''){
				return false;
			}
		}
		return true;
	}
	$scope.saveOnly = function(){
		//验证空附件
		if(!$scope.validateVoidFile()){
			$.alert("附件不能为空！");
			return;
		}
		var data = $scope.dataForSave();
		if(data == false){
			return;
		}
		$scope.saveOrSubmit(data,"so");
	}
$scope.saveOrSubmit = function(pData,method){
		
		$http({
	       	method:'post',  
   	       	url:srvUrl+"preBidding/addPolicyDecision.do", 
		    data:  $.param({"json":JSON.stringify(pData),"method":method})
	       	}).success(function(data){
	   	       	if(data.success){
	   	       	var alertData = "";
	   			if(method == "so"){
	   				alertData = "保存成功!";
	   			}else if(method == "ss"){
	   				if(data.result_data){
	   					alertData = "提交成功!";
	   					$location.path("/PreBiddingInfoView/"+businessId+"@view/"+$filter('encodeURI')('#/PreBiddingInfoList/1'));
	   				}else{
	   					$.alert("请确保参会信息已填写完毕!");
	   					return false;
	   				}
	   			}
	   	       		$.alert(alertData);
				}else{
					$.alert(data.result_data);
				}
	     	}).error(function(data,status,headers, config){
	       			$.alert(status);
	       	});
	}
	$scope.dataForSave = function(){
		var newAttachment = $scope.reduceAttachmentForSubmit($scope.newAttachment);	
		
		if(newAttachment == false){
    		return false;
    	}
		
		var chk_list = $("input[name='choose']");
	    var fid = "";
	    var uuidarr=[],itemarr=[],programmedarr=[],approvedarr=[],fileNamearr=[],filePatharr=[],versionarr=[],upload_datearr=[],programmeIddarr=[],approvedIdarr=[];
	    for(var i=0;i<chk_list.length;i++) {
	    	if(chk_list[i].checked)
	          {
	           fid = chk_list[i].value;
	           var arrfid=fid.split("||");
	           if(arrfid[0]==null || arrfid[0]==""){
	           uuidarr.push($scope.newAttachment[i].newItem.UUID);
	          }else{
	           uuidarr.push(arrfid[0]);
	          }
	           if(arrfid[1]==null || arrfid[1]==""){
	              itemarr.push($scope.newAttachment[i].newItem.ITEM_NAME);
	           	}else{
	           		itemarr.push(arrfid[1]);
	            }
	                programmedarr.push(arrfid[2]);
	                approvedarr.push(arrfid[3]);
	                fileNamearr.push(arrfid[4]);
	                filePatharr.push(arrfid[5]);
	                versionarr.push(arrfid[6]);
	                upload_datearr.push(arrfid[7]);
	                programmeIddarr.push(arrfid[8]);
	                approvedIdarr.push(arrfid[9]);
	            }
	        }
	    
	    var newFiles = $("input[name='choosem']")
        for(var i=0;i<newFiles.length;i++) {
            if(newFiles[i].checked)
            {
                fid = newFiles[i].value;
                var arrfid=fid.split("||");
                uuidarr.push(arrfid[0]);
                itemarr.push(arrfid[1]);
                programmedarr.push(arrfid[2]);
                approvedarr.push(arrfid[3]);
                fileNamearr.push(arrfid[4]);
                filePatharr.push(arrfid[5]);
                versionarr.push(arrfid[6]);
                upload_datearr.push(arrfid[7]);
                programmeIddarr.push(arrfid[8]);
                approvedIdarr.push(arrfid[9]);
            }
        }
	    
	        var array=[];
	        if(undefined==$scope.preBidding.policyDecision){
	            $scope.preBidding.policyDecision={};
	        }
	        $scope.preBidding.policyDecision.submitName=$scope.credentials.userName;
	        $scope.preBidding.policyDecision.submitDate = $scope.FormatDate();
	        if(undefined==$scope.preBidding.policyDecision.decisionMakingCommitteeStaffFiles){
	            $scope.preBidding.policyDecision.decisionMakingCommitteeStaffFiles=[];
	        }
	        
	        for(var j=0;j<fileNamearr.length;j++) {
	            $scope.vvvv = {};
	            $scope.vvvv.UUID =uuidarr[j];
	            $scope.vvvv.ITEM_NAME =itemarr[j];
	            $scope.vvvv.programmed =programmedarr[j];
	            $scope.vvvv.approved =approvedarr[j];
	            $scope.vvvv.fileName =fileNamearr[j];
	            $scope.vvvv.filePath =filePatharr[j];
	            $scope.vvvv.version =versionarr[j];
	            if(upload_datearr[j]==""){
	            	upload_datearr[j] = $scope.getDate();
	            }
	            $scope.vvvv.upload_date =upload_datearr[j];
	            $scope.vvvv.programmedId =programmeIddarr[j];
	            $scope.vvvv.approvedID =approvedIdarr[j];
	            array.push($scope.vvvv);
	        }
	        $scope.preBidding.policyDecision.decisionMakingCommitteeStaffFiles = array;
	        
	        $scope.preBidding.ac_attachment = $scope.pfr.attachment;
	        $scope.preBidding.reviewReport = $scope.pfr.reviewReport;
        return  $scope.preBidding;
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
	
	  //报告上传
	 $scope.errorAttach=[];
    $scope.uploadReprot = function (file,errorFile, idx) {
        if(errorFile && errorFile.length>0){
        	var errorMsg = fileErrorMsg(errorFile);
        	$scope.errorAttach[idx]={msg:errorMsg};
        }else if(file){
            $scope.errorAttach[idx]={msg:''};
            Upload.upload({
                url:srvUrl+'file/uploadFile.do',
                data: {file: file, folder:'',typeKey:'preAssessmentReportPath'}
            }).then(function (resp) {
                var retData = resp.data.result_data[0];
    			$scope.pfr.reviewReport.filePath = retData.filePath;
    			$.alert("文件替换成功！请执行保存操作！否则操作无效！");
            }, function (resp) {
                $.alert(resp.status);
            }, function (evt) {
                var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                $scope["progress"+idx]=progressPercentage == 100 ? "":progressPercentage+"%";
            });
        }
    };
    $scope.errorMsg=[];
    $scope.upload2 = function (file,errorFile, idx) {
    	if(errorFile && errorFile.length>0){
    		var errorMsg = fileErrorMsg(errorFile);
        	$scope.errorMsg[idx]={msg:errorMsg};
    	}else if(file){
    		var fileFolder = "pfrAssessment/";
    		var dates=$scope.preBidding.create_date;
    		var no=$scope.preBidding.projectNo;
    		var strs= new Array(); //定义一数组
    		strs=dates.split("-"); //字符分割
    		dates=strs[0]+strs[1]; //分割后的字符输出
    		fileFolder=fileFolder+dates+"/"+no;
    		Upload.upload({
    			url:srvUrl+'file/uploadFile.do',
    			data: {file: file, folder:fileFolder}
    		}).then(function (resp) {
    			var retData = resp.data.result_data[0];
    			var aaa = $scope.newAttachment;
    			var bbb = $scope.newAttachment[idx];
    			$scope.newAttachment[idx].fileName = retData.fileName;
    			$scope.newAttachment[idx].filePath = retData.filePath;
    			$.alert("文件替换成功！请执行保存操作！否则操作无效！");
    		}, function (resp) {
    			$.alert(resp.status);
    		}, function (evt) {
    			var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                $scope["progress"+idx]=progressPercentage == 100 ? "":progressPercentage+"%";
    		});
    	}
    };
    $scope.upload = function (file,errorFile, idx) {
        if(errorFile && errorFile.length>0){
        	var errorMsg = fileErrorMsg(errorFile);
        	$scope.errorAttach[idx]={msg:errorMsg};
        }else if(file){
            var fileFolder = "preBidding/";
            var dates=$scope.preBidding.create_date;
            var no=$scope.preBidding.projectNo;
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
                $scope.preBidding.policyDecision.fileList[idx].files=retData;
                $.alert("文件替换成功！请执行保存操作！否则操作无效！");
            }, function (resp) {
                $.alert(resp.status);
            }, function (evt) {
                var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                $scope["progress"+idx]=progressPercentage == 100 ? "":progressPercentage+"%";
            });
        }
    };
    
    //检查压缩文件
    $scope.checkFileZip = function(name,ev){
    	if(name == null){
    		var e=ev||window.event;//获取事件
    	    var ele=e.target||e.srcElement;//获取触发事件的元素
    	    ele.checked=false;
    		$.alert("请上传文件！");
    		return false;
    	}
    	var index = name.lastIndexOf('.');
    	var suffix  = name.substring(index+1);
    	if("rar" == suffix || "zip" == suffix || "7z" == suffix){
    		var e=ev||window.event;//获取事件
    	    var ele=e.target||e.srcElement;//获取触发事件的元素
    	    ele.checked=false;
    	    $.alert("上会附件不能是压缩文件！");
    		return false;
    	}
    	return true;
    }
    
    $scope.initData();
}]);
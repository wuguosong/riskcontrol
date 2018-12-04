ctmApp.register.controller('FormalBiddingInfo', ['$http','$scope','$location','$routeParams','Upload','$filter',
 function ($http,$scope,$location,$routeParams,Upload,$filter) {
	$scope.selectFlag = 'false';
	$scope.oldUrl = $routeParams.url;
    //申请报告ID
    var complexId = $routeParams.id;
    var params = complexId.split("@");
    var objId = params[0];
    $scope.formalReport={};
    $scope.formalReport.policyDecision={};
    
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
		$scope.getByID(objId);
		$scope.getMarks(objId);
		$scope.getSelectTypeByCode("8");
	    $scope.getSelectTypeByCodetype('14');
	}
	$scope.saveMarks = function(){
		if($scope.mark != null && $scope.mark != ""){
			if($scope.mark.flowMark == null){
				$.alert("请对审批流程熟悉度评分！");
				return false;  
			}
			if($scope.mark.fileTime == null){
				$.alert("请对资料的及时性评分！");
				return false;  
			}
			if($scope.mark.fileContent == null){
				$.alert("请对资料的完整性评分！");
				return false;  
			}
			if($scope.mark.moneyCalculate == null){
				$.alert("请对核心财务测算能力评分！");
				return false;  
			}
			if($scope.mark.reviewFileAccuracy == null){
				$.alert("请对资料的准确性评分！");
				return false;  
			}
			if($scope.mark.riskControl == null){
				$.alert("请对核心风险识别及规避能力评分！");
				return false;  
			}
			if($scope.mark.planDesign == null){
				$.alert("请对核心的方案设计能力评分！");
				return false;  
			}
			if($scope.mark.legalFileAccuracy == null){
				$.alert("请对资料的准确性评分！");
				return false;  
			}
			if($scope.mark.talks == null){
				$.alert("请对核心的协议谈判能力评分！");
				return false;  
			}
			
			//存分
			$.ajax({
				type:'post',  
				url: srvUrl + "formalMark/saveOrUpdate.do",
				data: $.param({"json":JSON.stringify($scope.mark),
					   "businessId":objId
				}),
				dataType: "json",
	        	async: false,
				success:function(result){
					if(!result.success){
						alert(result.result_name);
						return false;
					}
				}
			});
				return true;
		}else{
			//没有项目评分
			return true;
		}
	}
	
	$scope.getMarks = function(objId){
		
//		$(".mark").bind('input propertychange', function() {
//			if(this.value>this.attributes.max.value*1){
//				$.alert("此项最高分为"+this.attributes.max.value+"分！");
//				this.value=this.attributes.max.value*1;
//			}
//			if(this.value.indexOf(".")>0){
//				$.alert("此项只能填写整数！");
//				this.value = 0;
//			}
//		});
		$(".mark").keyup( function() {
			if(this.value.length==1){
				this.value=this.value.replace(/[^1-9]/g,'');
			}else{
				this.value=this.value.replace(/\D/g,'')
			}
			if(this.value>this.attributes.max.value*1){
				this.value=null;
			}
		});
		
		//初始化提示信息框
		$("[data-toggle='tooltip']").tooltip();
		
		$http({
			method:'post',  
		    url:srvUrl+"formalMark/queryMarks.do", 
		    data: $.param({"businessId":objId})
		}).success(function(result){
			if(result.success){
				var mark = result.result_data;
				if(mark != null && mark !=''){
					$scope.mark = {};
					if(mark.FLOWMARK != null && mark.FLOWMARK !=''){
						$scope.mark.flowMark = mark.FLOWMARK;
					}
					if(mark.FLOWMARKREASON != null && mark.FLOWMARKREASON !=''){
						$scope.mark.flowMarkReason = mark.FLOWMARKREASON;
					}
					if(mark.FILECOPY != null && mark.FILECOPY !=''){
						$scope.mark.fileCopy = mark.FILECOPY;
					}
					if(mark.FILECOPYREASON != null && mark.FILECOPYREASON !=''){
						$scope.mark.fileCopyReason = mark.FILECOPYREASON;
					}
					if(mark.FILETIME != null && mark.FILETIME !=''){
						$scope.mark.fileTime = mark.FILETIME;
					}
					if(mark.FILETIMEREASON != null && mark.FILETIMEREASON !=''){
						$scope.mark.fileTimeReason = mark.FILETIMEREASON;
					}
					if(mark.FILECONTENT != null && mark.FILECONTENT !=''){
						$scope.mark.fileContent = mark.FILECONTENT;
					}
					if(mark.FILECONTENTREASON != null && mark.FILECONTENTREASON !=''){
						$scope.mark.fileContentReason = mark.FILECONTENTREASON;
					}
					if(mark.MONEYCALCULATE != null && mark.MONEYCALCULATE !=''){
						$scope.mark.moneyCalculate = mark.MONEYCALCULATE;
					}
					if(mark.MONEYCALCULATEREASON != null && mark.MONEYCALCULATEREASON !=''){
						$scope.mark.moneyCalculateReason = mark.MONEYCALCULATEREASON;
					}
					if(mark.REVIEWFILEACCURACY != null && mark.REVIEWFILEACCURACY !=''){
						$scope.mark.reviewFileAccuracy = mark.REVIEWFILEACCURACY;
					}
					if(mark.REVIEWFILEACCURACYREASON != null && mark.REVIEWFILEACCURACYREASON !=''){
						$scope.mark.reviewFileAccuracyReason = mark.REVIEWFILEACCURACYREASON;
					}
					if(mark.RISKCONTROL != null && mark.RISKCONTROL !=''){
						$scope.mark.riskControl = mark.RISKCONTROL;
					}
					if(mark.RISKCONTROLREASON != null && mark.RISKCONTROLREASON !=''){
						$scope.mark.riskControlReason = mark.RISKCONTROLREASON;
					}
					if(mark.PLANDESIGN != null && mark.PLANDESIGN !=''){
						$scope.mark.planDesign = mark.PLANDESIGN;
					}
					if(mark.PLANDESIGNREASON != null && mark.PLANDESIGNREASON !=''){
						$scope.mark.planDesignReason = mark.PLANDESIGNREASON;
					}
					if(mark.LEGALFILEACCURACY != null && mark.LEGALFILEACCURACY !=''){
						$scope.mark.legalFileAccuracy = mark.LEGALFILEACCURACY;
					}
					if(mark.LEGALFILEACCURACYREASON != null && mark.LEGALFILEACCURACYREASON !=''){
						$scope.mark.legalFileAccuracyReason = mark.LEGALFILEACCURACYREASON;
					}
					if(mark.TALKS != null && mark.TALKS !=''){
						$scope.mark.talks = mark.TALKS;
					}
					if(mark.TALKSREASON != null && mark.TALKSREASON !=''){
						$scope.mark.talksReason = mark.TALKSREASON;
					}
					if(mark.HEGUITOTALMARK != null && mark.HEGUITOTALMARK !=''){
						$scope.mark.HEGUITOTALMARK = mark.HEGUITOTALMARK;
					}
					if(mark.FILETOTALMARK != null && mark.FILETOTALMARK !=''){
						$scope.mark.FILETOTALMARK = mark.FILETOTALMARK;
					}
					if(mark.HEXINTOTALMARK != null && mark.HEXINTOTALMARK !=''){
						$scope.mark.HEXINTOTALMARK = mark.HEXINTOTALMARK;
					}
					if(mark.ALLTOTALMARK != null && mark.ALLTOTALMARK !=''){
						var vv = Math.pow(10,1);
						$scope.mark.ALLTOTALMARK = Math.round(mark.ALLTOTALMARK*vv)/vv;
					}
				}
			}else{
				$.alert(result.result_name);
			}
		});
	}
	 $scope.getByID = function(projectFormalId){
	        $http({
				method:'post',  
			    url:srvUrl+"formalReport/findFormalAndReport.do", 
			    data: $.param({"projectFormalId":projectFormalId})
			}).success(function(data){
		         $scope.formalReport  = data.result_data.Report;
		         $scope.pfr  = data.result_data.Formal;
		         $scope.meetInfo = data.result_data.MeetInfo;
		         $scope.applyDate = data.result_data.applyDate;
		         $scope.stage = data.result_data.stage;
		         
		         //处理附件列表
		         $scope.reduceAttachment(data.result_data.Formal.attachment);
		         //新增附件类型
		         $scope.attach  = data.result_data.attach;
		         //控制新增文件
		         $scope.newPfr  = data.result_data.Formal;
		         $scope.formalID=$scope.formalReport.projectFormalId;
		         var ptNameArr=[],fgNameArr=[],fgValueArr=[],investmentaNameArr=[];
		         var pt=$scope.pfr.apply.projectType;
		         if(null!=pt && pt.length>0){
		            for(var i=0;i<pt.length;i++){
		                ptNameArr.push(pt[i].VALUE);
		             }
		            $scope.pfr.apply.projectType=ptNameArr.join(",");
		         }
		         
		         if($scope.stage == '3.9'){
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
	    		if(files!=null){
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
	    
	    $scope.getSelectTypeByCode = function(typeCode){
	        var  url = 'common/commonMethod/getRoleuserByCode';
	        $scope.httpData(url,typeCode).success(function(data){
	            if(data.result_code === 'S'){
	                $scope.userRoleListall=data.result_data;
	            }else{
	                alert(data.result_name);
	            }
	        });
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
	
	$scope.validateVoidFile = function(){
		for(var i in $scope.newAttachment){
			if($scope.newAttachment[i].fileName == null || $scope.newAttachment[i] == ''){
				return false;
			}
		}
		return true;
	}
	
	$scope.saveOnly = function(){
		if(!$scope.saveMarks()){
			return;
		}
		$scope.getMarks(objId);
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
	
	$scope.submitSave = function() {
		if(!$scope.saveMarks()){
			return;
		}
		//验证空附件
		if(!$scope.validateVoidFile()){
			$.alert("附件不能为空！");
			return;
		}
		if($scope.validateFormalBiddingInfo()){
			if($scope.isAttachmentBeChosen()){
				var data = $scope.dataForSubmit();
				if(data == false){
					 return;
				}
				$scope.saveOrSubmit(data,"ss");
			}
		}
	}
	
	$scope.saveOrSubmit = function(pData,method){
		show_Mask();
		$http({
	       	method:'post',  
   	       	url:srvUrl+"formalReport/addPolicyDecision.do", 
		    data:  $.param({"json":JSON.stringify(pData),"method":method})
	       	}).success(function(data){
	       	hide_Mask();
   	       	if(data.success){
   	       		var alertData = "";
	   			if(method == "so"){
	   				alertData = "保存成功!";
	   			}else if(method == "ss"){
	   				if(data.result_data){
	   					alertData = "提交成功!";
	   					$location.path("/FormalBiddingInfo_view/"+objId+"@view/"+$filter('encodeURI')('#/FormalBiddingInfoList/1'));
	   				}else{
	   					$.alert("请确保参会信息已填写完毕!");
	   					return false;
	   				}
	   			}
	   	       		$.alert(alertData);
			}else{
				$.alert(data.result_name);
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
	        if(undefined==$scope.formalReport.policyDecision){
	            $scope.formalReport.policyDecision={};
	        }
	        $scope.formalReport.policyDecision.submitName=$scope.credentials.userName;
	        $scope.formalReport.policyDecision.submitDate = $scope.FormatDate();
	        if(undefined==$scope.formalReport.policyDecision.decisionMakingCommitteeStaffFiles){
	            $scope.formalReport.policyDecision.decisionMakingCommitteeStaffFiles=[];
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
	        $scope.formalReport.policyDecision.decisionMakingCommitteeStaffFiles = array;
	        
	        $scope.formalReport.ac_attachment = $scope.pfr.attachment;
        
        return  $scope.formalReport;
	}
    
    $scope.dataForSubmit = function(){
    	
    	var newAttachment = $scope.reduceAttachmentForSubmit($scope.newAttachment);
    	
    	if(newAttachment == false){
    		return false;
    	}
    	
        var file=$scope.formalReport.policyDecision.fileList;

        var chk_list = $("input[name='choose']")
        var fid = "";
        var uuidarr=[],itemarr=[],programmedarr=[],approvedarr=[],fileNamearr=[],filePatharr=[],versionarr=[],upload_datearr=[],programmeIddarr=[],approvedIdarr=[];
        
        for(var i = 0;i<chk_list.length;i++){
        	var fid = chk_list[i].value;
            var arrfid=fid.split("||");
            
//            if(arrfid[2] == "" || arrfid[8] == ""){
//            	$.alert("编制人不能为空!");
//            	return false;
//            }
            
//            if(arrfid[3] == "" || arrfid[9] == ""){
//            	$.alert("审核人不能为空!");
//            	return false;
//            }
//            
            if(arrfid[4] == "" || arrfid[4] == null){
            	$.alert("请上传附件！");
            	return false;
            }
        }
       
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
        
        for(var i = 0;i<newFiles.length;i++){
        	var fid = newFiles[i].value;
            var arrfid=fid.split("||");
            
            if(arrfid[4] == "" || arrfid[4] == null){
            	$.alert("请上传附件！");
            	return false;
            }
        }
        
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
        if(undefined==$scope.formalReport.policyDecision){
            $scope.formalReport.policyDecision={};
        }
        $scope.formalReport.policyDecision.submitName=$scope.credentials.userName;
        $scope.formalReport.policyDecision.submitDate=$scope.FormatDate();
        if(undefined==$scope.formalReport.policyDecision.decisionMakingCommitteeStaffFiles){
            $scope.formalReport.policyDecision.decisionMakingCommitteeStaffFiles=[];
        }
        
        for(var j=0;j<fileNamearr.length;j++) {
        	
            $scope.vvvv = {};
            $scope.vvvv.UUID = uuidarr[j];
            $scope.vvvv.ITEM_NAME = itemarr[j];
            $scope.vvvv.programmed = programmedarr[j];
            $scope.vvvv.approved = approvedarr[j];
            $scope.vvvv.fileName = fileNamearr[j];
            $scope.vvvv.filePath = filePatharr[j];
            $scope.vvvv.version = versionarr[j];
            if(upload_datearr[j] == ""){
            	upload_datearr[j] = $scope.getDate();
            }
            $scope.vvvv.upload_date = upload_datearr[j];
            $scope.vvvv.programmedId = programmeIddarr[j];
            $scope.vvvv.approvedID = approvedIdarr[j];
            array.push($scope.vvvv);
        }
        $scope.formalReport.policyDecision.decisionMakingCommitteeStaffFiles = array;
        
        $scope.formalReport.ac_attachment = $scope.pfr.attachment;
        
        return $scope.formalReport;
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
	    				newAttachment[i].programmed=newAttachment[i].programmed;
//	    				newAttachment[i].programmed.name=$scope.credentials.userName;
//	    				newAttachment[i].programmed.value=$scope.credentials.UUID;
	    				newAttachment[i].approved=newAttachment[i].approved;
//	    				newAttachment[i].approved.name=$scope.credentials.userName;
//	    				newAttachment[i].approved.value=$scope.credentials.UUID;
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
    
    //验证必填项
    $scope.validateFormalBiddingInfo = function(){
    	var boolean = false;
    	var jprojectType =  $("#jprojectType").val();
    	var jinvestmentAmount = $("#jinvestmentAmount").val();
    	var jprojectSize = $("#jprojectSize").val();
    	var rateOfReturn =  $("#rateOfReturn").val();
    	var fkPsResult = $("#fkPsResult").val();
    	var fkRiskTip =  $("#fkRiskTip").val();
    	var newItem = $("select[class='newItem']");
    	
    	if(null == jprojectType || "" == jprojectType){
     	   $.alert("请选择项目类型!"); return boolean;
     	}else if(null == jinvestmentAmount || "" == jinvestmentAmount){
    	    $.alert("投资金额不能为空!"); return boolean;
    	}else if(null == jprojectSize || "" == jprojectSize){
    	   $.alert("项目规模不能为空!"); return boolean;
    	}else if(null == rateOfReturn || "" == rateOfReturn){
    	   $.alert("投资收益率不能为空!"); return boolean;
    	}else if(null == fkPsResult || "" == fkPsResult){
    	   $.alert("风控中心评审结论不能为空!"); return boolean;
    	}else if(null == fkRiskTip || "" == fkRiskTip){
    	   $.alert("风控重点风险提示不能为空!"); return boolean;
    	}else{
    		boolean = true;	
    	}
    	
    	return boolean;
    }
    
    $scope.isAttachmentBeChosen = function(){
    	var boolean  = false;
    	if($("input[name='choose']:checked").length+$("input[name='choosem']:checked").length==0){
    	  	$.alert("您没有选择要提交的附件!");
      	}else{
      		boolean = true;
      	}
    	
    	return boolean;
    }
    
    //附件列表---->新增列表
    $scope.addFileList = function(){
        function addBlankRow(array){
            var blankRow = {
                file_content:''
            }
            var size=0;
            for(attr in array){
                size++;
            }
            array[size]=blankRow;
        }
        if(undefined==$scope.formalReport.policyDecision){
            $scope.formalReport.policyDecision={fileList:[]};
        }
        if(undefined==$scope.formalReport.policyDecision.fileList){
            $scope.formalReport.policyDecision.fileList=[];
        }
        addBlankRow($scope.formalReport.policyDecision.fileList);
    }
    
    //附件列表---->删除指定的列表
    $scope.commonDdelete = function(){
        var commentsObj = $scope.formalReport.policyDecision.fileList;
        if(commentsObj!=null){
            for(var i=0;i<commentsObj.length;i++){
                if(commentsObj[i].selected){
                    commentsObj.splice(i,1);
                    i--;
                }
            }
        }
    }
    
    //附件列表---->上传附件
    $scope.errorAttach=[];
    $scope.uploadReprot = function (file,errorFile, idx) {
        if(errorFile && errorFile.length>0){
        	var errorMsg = fileErrorMsg(errorFile);
            $scope.errorAttach[idx]={msg:errorMsg};
        }else if(file){
            $scope.errorAttach[idx]={msg:''};
            Upload.upload({
                url:srvUrl+'file/uploadFile.do',
                data: {file: file, folder:'',typeKey:'formalAssessmentReportPath'}
            }).then(function (resp) {
                var retData = resp.data.result_data[0];
    			$scope.formalReport.filePath = retData.filePath;
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
            var fileFolder = "formalReport/";
            var dates=$scope.formalReport.create_date;
            var no=$scope.formalReport.projectNo;
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
                $scope.formalReport.policyDecision.fileList[idx].files=retData;
                $.alert("文件替换成功！请执行保存操作！否则操作无效！");
            }, function (resp) {
                $.alert(resp.status);
            }, function (evt) {
                var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                $scope["progress"+idx]=progressPercentage == 100 ? "":progressPercentage+"%";
            });
        }
    };
    
    //业务单位上报评审文件-投资部门提供---->新增列表
    $scope.addFileList1 = function(){
    	function addBlankRow1(array){
    		var blankRow = {
    				newFile:true
    		}
    		var size = array.length;
    		array[size]=blankRow;
    	}
    	if(undefined==$scope.newPfr.attachment){
            $scope.newAttachment={files:[]};
        }
    	addBlankRow1($scope.newAttachment);
    }
    
    //业务单位上报评审文件-投资部门提供---->删除指定的列表
    $scope.deleteFileList = function(){
    	var i  = 0 ;
    	$(".deleteSelect:checked").each(function(){
    		if(i>0){
    			$scope.newAttachment.splice(this.value-i,1);
    		}else{
    			$scope.newAttachment.splice(this.value,1);
    		}
    		i++;
    	});
    	$(".deleteSelect:checked").attr("checked",false);
    }
    
    //业务单位上报评审文件-投资部门提供---->上传附件
    $scope.errorMsg=[];
    $scope.upload2 = function (file,errorFile, idx) {
    	if(errorFile && errorFile.length>0){
    		var errorMsg = fileErrorMsg(errorFile);
    		$scope.errorMsg[idx]={msg:errorMsg};
    	}else if(file){
    		var fileFolder = "pfrAssessment/";
    		var dates=$scope.formalReport.create_date;
    		var no=$scope.formalReport.projectNo;
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

    // -------------------Sam Gao 2018-11-28日修改--------------------------

     $scope.projectOverview = {
         orderno: null, // 排序编号
         code: null, // 行code
         value: null, // 行描述
         content: null, // 行内容
         attachmentFile: null, // 附件地址
         attachmentValue: null, // 附件名称
         start: null, // 是否启用 0：未启用；1：启用
         edit: null, // 是否可以修改行描述 0：不可修改； 1：可修改
         modify: null // 是否可以编辑 0:不可以编辑；1：可编辑
     };

     $scope.jprojectType = function (type) {
		if (type.ITEM_CODE == "1401") {
            $scope.projectOverviewList = [
				{
					orderno: "0",
               		code: "regionalBackground",
					value: "项目背景",
					content: null,
					attachmentFile: null,
					attachmentValue: null,
					start: "1",
                    edit: "0",
					modify: "1"
				},
                {
                    orderno: "1",
                    code: "projectType",
                    value: "项目模式",
                    content: null,
                    attachmentFile: null,
                    attachmentValue: null,
                    start: "1",
                    edit: "0",
                    modify: "1"
                },
                {
                    orderno: "2",
                    code: "totaleInvestment",
                    value: "项目总投资",
                    content: null,
                    attachmentFile: null,
                    attachmentValue: null,
                    start: "1",
                    edit: "0",
                    modify: "1"
                },
                {
                    orderno: "3",
                    code: "constructionContent",
                    value: "项目内容",
                    content: null,
                    attachmentFile: null,
                    attachmentValue: null,
                    start: "1",
                    edit: "0",
                    modify: "1"
                },
                {
                    orderno: "4",
                    code: "regionalBackground",
                    value: "项目期限",
                    content: null,
                    attachmentFile: null,
                    attachmentValue: null,
                    start: "1",
                    edit: "0",
                    modify: "1"
                },
                {
                    orderno: "5",
                    code: "other0",
                    value: "其他",
                    content: null,
                    attachmentFile: null,
                    attachmentValue: null,
                    start: "1",
					edit: "1",
                    modify: "1"
                }
			];
            angular.forEach($scope.projectOverviewList, function (data, index) {
                data.orderno = parseInt(data.orderno);
            });

            // $scope.projectOverviewOne = {};
			// 升序方法
            $scope.descOrderno = function (projectOverview) {
            	if (projectOverview.orderno != 0) {
                    $scope.old = angular.copy(projectOverview);
                    angular.forEach($scope.projectOverviewList, function(data, index, array){
                        // 给上层序号+1
                        if (data.orderno == (parseInt(projectOverview.orderno) - 1))
                        {
                            data.orderno = parseInt(data.orderno + 1);
                        }
                    });
                    // 给自己序号-1
                    projectOverview.orderno = ($scope.old.orderno  - 1);
				}
            };
            // 降序方法
            $scope.ascOrderno = function (projectOverview) {
            	console.log(projectOverview)
                console.log($scope.projectOverviewList)
                if (projectOverview.orderno != ($scope.projectOverviewList.length - 1)) {
                    $scope.old = angular.copy(projectOverview);
                    angular.forEach($scope.projectOverviewList, function(data, index, array){
                        // 给下层序号-1
                        if (data.orderno == (parseInt(projectOverview.orderno) + 1))
                        {
                            data.orderno = parseInt(data.orderno) - 1;
                        }
                    });
                    // 给自己序号+1
                    projectOverview.orderno = (parseInt($scope.old.orderno)  + 1);
                }
            };
			// 启用或者关闭编辑界面
            $scope.closeOverview = function (projectOverview) {
                projectOverview.modify = projectOverview.modify == "0" ?  "1" : "0";
            }
            // 新增projectOverview
            $scope.addOverview = function () {
                $scope.newOtherCodeIndex = 0;
                angular.forEach($scope.	projectOverviewList, function(data, index, array){
                    if (data.code.slice(0,5) == "other") {
                        $scope.newOtherCodeIndex = $scope.newOtherCodeIndex < parseInt(data.code.slice(5)) ? parseInt(data.code.slice(5)) : $scope.newOtherCodeIndex;
					}
                });
                $scope.newProjectOverview = angular.copy($scope.projectOverview);
                $scope.newProjectOverview.orderno = $scope.projectOverviewList.length;
                $scope.newProjectOverview.value = "其他";
                $scope.newProjectOverview.start = "1";
;          		$scope.newProjectOverview.edit = "1";
                $scope.newProjectOverview.modify = "1";
                $scope.projectOverviewList.push($scope.newProjectOverview);
            };

            // 数组指定key值排序
            var compare = function (prop) {
                return function (obj1, obj2) {
                    var val1 = obj1[prop];
                    var val2 = obj2[prop];
                    if (!isNaN(Number(val1)) && !isNaN(Number(val2))) {
                        val1 = Number(val1);
                        val2 = Number(val2);
                    }
                    if (val1 < val2) {
                        return -1;
                    } else if (val1 > val2) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            }








		}
     }
}]);
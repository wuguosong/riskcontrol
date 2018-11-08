ctmApp.register.controller('FormalBiddingInfo_view', ['$http','$scope','$location','$routeParams','Upload',
 function ($http,$scope,$location,$routeParams,Upload) {
	$scope.oldUrl = $routeParams.url;
    //申请报告ID
    var complexId = $routeParams.id;
    var params = complexId.split("@");
    var objId = params[0];
    var isView = params[1];
    $scope.formalReport={};
    $scope.hasWaiting = false;
    $scope.selectFlag = 'false';
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
		$scope.getMarks(objId);
		$scope.getByID(objId);
		$scope.getSelectTypeByCodetype('14');
	}
	$scope.getMarks = function(objId){
		$(".mark").keyup( function() {
		    if(this.value>this.attributes.max.value*1){
		    	 $.alert("此项最高分为"+this.attributes.max.value+"分！");
		    	 this.value=this.attributes.max.value*1;
		    }
		    if(this.value.indexOf(".")>0){
		    	$.alert("此项只能填写整数！");
		    	this.value = 0;
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
		         
		         if(!($scope.stage == '6' || $scope.stage == '7')){
		        	 $scope.selectFlag = 'true';
		        	 $scope.hasWaiting = true;
		         }
			}).error(function(data,status,header,config){
				$.alert(status);
			});
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
	 $scope.saveOrSubmit = function(pData,method){
			
			$http({
		       	method:'post',  
	   	       	url:srvUrl+"formalReport/addPolicyDecision.do", 
			    data:  $.param({"json":JSON.stringify(pData),"method":method})
		       	}).success(function(data){
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
		    				newAttachment[i].approved=newAttachment[i].approved;
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
	 $scope.validateVoidFile = function(){
			for(var i in $scope.newAttachment){
				if($scope.newAttachment[i].fileName == null || $scope.newAttachment[i] == ''){
					return false;
				}
			}
			return true;
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
	$scope.upload = function (file,errorFile, idx) {
        if(errorFile && errorFile.length>0){
        	var errorMsg = fileErrorMsg(errorFile);
            $scope.errorAttach[idx]={msg:errorMsg};
        }else if(file){
            $scope.errorAttach[idx]={msg:''};
            Upload.upload({
                url:srvUrl+'file/uploadFile.do',
                data: {file: file, folder:'',typeKey:'formalAssessmentPath'}
            }).then(function (resp) {
                var retData = resp.data.result_data[0];
                $scope.formalReport.policyDecision.fileList[idx].files=retData;
                $.alert("文件修改成功！请执行保存操作！否则操作无效！");
            }, function (resp) {
                $.alert(resp.status);
            }, function (evt) {
                var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                $scope["progress"+idx]=progressPercentage == 100 ? "":progressPercentage+"%";
            });
        }
    };
    
    $scope.upload2 = function (file,errorFile, idx) {
    	if(errorFile && errorFile.length>0){
    		var errorMsg = fileErrorMsg(errorFile);
    		$scope.errorMsg[idx]={msg:errorMsg};
    	}else if(file){
    		Upload.upload({
    			url:srvUrl+'file/uploadFile.do',
    			data: {file: file, folder:'',typeKey:'formalAssessmentPath'}
    		}).then(function (resp) {
    			var retData = resp.data.result_data[0];
    			var aaa = $scope.newAttachment;
    			var bbb = $scope.newAttachment[idx];
    			$scope.newAttachment[idx].fileName = retData.fileName;
    			$scope.newAttachment[idx].filePath = retData.filePath;
    			$.alert("文件修改成功！请执行保存操作！否则操作无效！");
    		}, function (resp) {
    			$.alert(resp.status);
    		}, function (evt) {
    			var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                $scope["progress"+idx]=progressPercentage == 100 ? "":progressPercentage+"%";
    		});
    	}
    };
    
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
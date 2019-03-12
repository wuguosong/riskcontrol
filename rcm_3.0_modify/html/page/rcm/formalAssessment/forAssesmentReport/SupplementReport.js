ctmApp.register.controller('SupplementReport',['$http','$scope','$location','$routeParams', function ($http,$scope,$location,$routeParams) {
    //初始化
	$scope.oldUrl = $routeParams.url;
	$scope.tabIndex =  $routeParams.tabIndex;
	$scope.controller_val = $location.$$url.split("/")[1];
    var complexId = $routeParams.id;
    $scope.paramId = complexId;
    var params = complexId.split("@");
    if(null!=params[1] && ""!=params[1]){
        $scope.flag=params[1];
    }
    if(null!=params[2] && ""!=params[2] && null!=params[3] && ""!=params[3] && null!=params[4] && ""!=params[4] && null!=params[5] && ""!=params[5] && null!=params[6] && ""!=params[6]){
        $scope.reportReturnId=params[2]+"@"+params[3]+"@"+params[4]+"@"+params[5]+"@"+params[6];
    }
    var objId = params[0];
    var taskID='wwxosnsosjdsnsjnddID';
    $scope.taskID=taskID;
    $scope.formalReport={};
    $scope.dic=[];
    
    var action = $routeParams.action;
    $scope.actionparm=$routeParams.action;
    $scope.initData = function(){
    	 if(action=="Create"){
    		 $scope.title = "正式评审报告-新增";
    	     $scope.getProjectFormalReviewByID(objId);
    	     $scope.formalReport.create_by=$scope.credentials.UUID;
    	     $scope.formalReport.create_name=$scope.credentials.userName;
    	     $("#wordbtn").hide();
    	 }else if(action=="Update"){
    		 $scope.title = "正式评审报告-修改";
    	     $scope.getByID(objId);
    	 }else if(action=="View"){
    		 $scope.title = "正式评审报告-查看";
    	     $scope.getByID(objId);
    	     $("#savebtn").hide();
    	 }
    }
 
    //给第三部分添加行
    $scope.addtableRow=function(n){
        function addcellRow(array){
            var blankRow = {
                riskPoint:'',
                riskContent:'',
                pointsAndProblems:'',
                opinionAndCommitment:''
            }
            array.push(blankRow);
        }
        if(undefined==$scope.formalReport){
            $scope.formalReport={projectConcernsIssues:{}};
        }
        if(n==1){
            if(undefined==$scope.formalReport.projectConcernsIssues){
                $scope.formalReport.projectConcernsIssues={leadershipDecision:[]};
            }
            if(undefined==$scope.formalReport.projectConcernsIssues.leadershipDecision){
                $scope.formalReport.projectConcernsIssues.leadershipDecision=[];
            }
            addcellRow($scope.formalReport.projectConcernsIssues.leadershipDecision);
        }else if(n==2){
            if(undefined==$scope.formalReport.projectConcernsIssues){
                $scope.formalReport.projectConcernsIssues={investmentConditions:[]};
            }
            if(undefined==$scope.formalReport.projectConcernsIssues.investmentConditions){
                $scope.formalReport.projectConcernsIssues.investmentConditions=[];
            }
            addcellRow($scope.formalReport.projectConcernsIssues.investmentConditions);
        }else if(n==3){
            if(undefined==$scope.formalReport.projectConcernsIssues){
                $scope.formalReport.projectConcernsIssues={implementationRequirements:[]};
            }
            if(undefined==$scope.formalReport.projectConcernsIssues.implementationRequirements){
                $scope.formalReport.projectConcernsIssues.implementationRequirements=[];
            }
            addcellRow($scope.formalReport.projectConcernsIssues.implementationRequirements);
        }else if(n==4){
            if(undefined==$scope.formalReport.projectConcernsIssues){
                $scope.formalReport.projectConcernsIssues={prompt:[]};
            }
            if(undefined==$scope.formalReport.projectConcernsIssues.prompt){
                $scope.formalReport.projectConcernsIssues.prompt=[];
            }
            addcellRow($scope.formalReport.projectConcernsIssues.prompt);
        }
    }
    //第三分部删除行
    $scope.delrow=function(name,num){
        if(num==1) {
            var names = $scope.formalReport.projectConcernsIssues.leadershipDecision;
            names.splice(name, 1);
            if (names.length > 0) {
                $scope.formalReport.projectConcernsIssues.leadershipDecision = names;
            } else {
                $scope.formalReport.projectConcernsIssues.leadershipDecision = [];
            }
        }else if(num==2) {
            var names = $scope.formalReport.projectConcernsIssues.investmentConditions;
            names.splice(name, 1);
            if (names.length > 0) {
                $scope.formalReport.projectConcernsIssues.investmentConditions = names;
            } else {
                $scope.formalReport.projectConcernsIssues.investmentConditions = [];
            }
        }else if(num==3) {
            var names = $scope.formalReport.projectConcernsIssues.implementationRequirements;
            names.splice(name, 1);
            if (names.length > 0) {
                $scope.formalReport.projectConcernsIssues.implementationRequirements = names;
            } else {
                $scope.formalReport.projectConcernsIssues.implementationRequirements = [];
            }
        }else if(num==4) {
            var names = $scope.formalReport.projectConcernsIssues.prompt;
            names.splice(name, 1);
            if (names.length > 0) {
                $scope.formalReport.projectConcernsIssues.prompt = names;
            } else {
                $scope.formalReport.projectConcernsIssues.prompt = [];
            }
        }
    }
    $scope.addOption = function(){
        function addBlankRow(array){
            var blankRow = {
                option_content:'',
                option_execute:'',
                taskId:taskID
            }
            var size=0;
            for(attr in array){
                size++;
            }
            array[size]=blankRow;
        }

        if(undefined==$scope.formalReport){
            $scope.formalReport={optionList:[]};

        }
        if(undefined==$scope.formalReport.optionList){
            $scope.formalReport.optionList=[];
        }
        addBlankRow($scope.formalReport.optionList);
    }
    $scope.addRequire = function(){
        function addBlankRow(array){
            var blankRow = {
                require_content:'',
                require_execute:'',
                taskId:taskID
            }
            var size=0;
            for(attr in array){
                size++;
            }
            array[size]=blankRow;
        }

        if(undefined==$scope.formalReport){
            $scope.formalReport={requireList:[]};

        }
        if(undefined==$scope.formalReport.requireList){
            $scope.formalReport.requireList=[];
        }
        addBlankRow($scope.formalReport.requireList);
    }
    $scope.addAdjustment=function(){
        function addBlankRow(array){
            var blankRow = {
                adjustTargets:'',
                oldReview:'',
                jumpingReason:'',
                afterReview:'',
                taskId:taskID
            }
            var size=0;
            for(attr in array){
                size++;
            }
            array[size]=blankRow;
        }

        if(undefined==$scope.formalReport){
            $scope.formalReport={adjustmentAnalysis:[]};

        }
        if(undefined==$scope.formalReport.adjustmentAnalysis){
            $scope.formalReport.adjustmentAnalysis=[];
        }
        addBlankRow($scope.formalReport.adjustmentAnalysis);
    }
    $scope.commonDdelete = function(n){
        var commentsObj=null;
        if(n==0) {
            commentsObj = $scope.formalReport.optionList;
        }else if(n==1){
            commentsObj = $scope.formalReport.requireList;
        }else if(n==8){
            commentsObj = $scope.formalReport.adjustmentAnalysis;
        }else if(n==10) {
            var commentsObj = $scope.formalReport.adjustmentAnalysis;
        }
        if(commentsObj!=null){
            for(var i=0;i<commentsObj.length;i++){
                if(commentsObj[i].selected){
                    commentsObj.splice(i,1);
                    i--;
                }
            }
        }

    }
    
    $scope.getProjectFormalReviewByID = function(id){
    	$http({
			method:'post',  
		    url:srvUrl+"formalReport/getProjectFormalReviewByID.do", 
		    data: $.param({"id":id})
		}).success(function(data){

            $scope.pfr  = data.result_data;
            $scope.formalReport.projectFormalId=$scope.pfr.id;
            $scope.formalReport.projectName=$scope.pfr.apply.projectName;
            $scope.formalReport.projectNo=$scope.pfr.apply.projectNo;
            if(null!=$scope.pfr.apply.reportingUnit){
                $scope.formalReport.reportingUnit=$scope.pfr.apply.reportingUnit.name;
            }
            var ptNameArr=[];
            var pt=$scope.pfr.apply.projectType;
            if(null!=pt && pt.length>0){
                for(var i=0;i<pt.length;i++){
                    ptNameArr.push(pt[i].VALUE);
                }
                $scope.formalReport.projectTypeName=ptNameArr.join(",");
            }
            $scope.formalReport.controllerVal = $scope.controller_val;
            $scope.addtableRow(1);
            $scope.addtableRow(2);
            $scope.addtableRow(3);
            $scope.addtableRow(4);
            if(undefined==$scope.formalReport.optionList){
                $scope.addOption();
            }
            if(undefined==$scope.formalReport.requireList){
                $scope.addRequire();
            }
            if(undefined==$scope.formalReport.adjustmentAnalysis){
                $scope.addAdjustment();
            }
            if(null==$scope.formalReport.mianReason) {
                $scope.formalReport.mianReason="本项目现有XX项重要边界条件变化，具体调整事项及原因如下：";
            }
            if(null==$scope.formalReport.borderCondition) {
                $scope.formalReport.borderCondition="本次边界条件调整对项目重要财务指标影响如下：";
            }
            if(null==$scope.formalReport.risksProblems) {
                $scope.formalReport.risksProblems="因上文的重要条件调整，本项目新增关键风险因素如下：";
            }

        
		}).error(function(data,status,headers,config){
			$.alert(status);
		});
    }

    $scope.saveReport6 = function(callBack){
        if(typeof callBack == 'function') {
            if ($("#SupplementReport").valid()) {
                var url_post;
                if (typeof ($scope.formalReport._id) != "undefined") {
                	url_post = 'formalReport/updateReport.do';
                }else{
                	var boolean = $scope.isReportExist();
                	if(boolean){
                		$.alert("请勿重复保存数据!");
                		return false;
                	}
                	url_post = 'formalReport/createNewReport.do';
                }
                $http({
        			method:'post',  
        		    url:srvUrl+url_post, 
        		    data: $.param({"json":JSON.stringify($scope.formalReport)})
        		}).success(function(data){
        			if(data.success){
        				$scope.formalReport._id = data.result_data;
        				if (typeof callBack == 'function') {
                            callBack();
                        } else {
                            $.alert("保存成功");
                        }
                        $("#wordbtn").show();
        			}else{
        				$.alert(data.result_name);
        			}
        		}).error(function(data,status,headers,config){
        			$.alert(status);
        		});
            }
        }else{
            var url_post;
            if (typeof ($scope.formalReport._id) != "undefined") {
            	url_post = 'formalReport/updateReport.do';
            }else{
            	var boolean = $scope.isReportExist();
            	if(boolean){
            		$.alert("请勿重复保存数据!");
            		return false;
            	}
            	url_post = 'formalReport/createNewReport.do';
            }
            $http({
    			method:'post',  
    		    url:srvUrl+url_post, 
    		    data: $.param({"json":JSON.stringify($scope.formalReport)})
    		}).success(function(data){
    			if(data.success){
    				$scope.formalReport._id = data.result_data;
    				if (typeof callBack == 'function') {
                        callBack();
                    } else {
                        $.alert("保存成功");
                    }
                    $("#wordbtn").show();
    			}else {
                    alert(data.result_name);
                }
    		}).error(function(data,status,headers,config){
    			$.alert(status);
    		});
        }
    }
    
    $scope.isReportExist = function(){
    	var boolean = false;
    	$.ajax({
			url: srvUrl+'formalReport/isReportExist.do',
			type: "POST",
			dataType: "json",
			data: {"businessid": objId},
			async: false,
			success: function(data){
				if(data.result_data > 0){
	  				boolean = true;
	  			}
			}
		});
    	return boolean;
    }
    
    //检查是否可提交
    $scope.isPossible2Submit = function(){
    	var boolean = false;
    	$.ajax({
			url: srvUrl+'formalReport/isPossible2Submit.do',
			type: "POST",
			dataType: "json",
			data: {"projectFormalId":$scope.formalReport.projectFormalId},
			async: false,
			success: function(data){
				if(data.result_data){
	  				boolean = true;
	  			}
			}
		});
        return boolean;
    }
    
    //提交报告并更改状态
    $scope.showSubmitModal = function() {
    	show_Mask();
    	var flag = $scope.isPossible2Submit();
    	if(flag){
 			   $scope.saveReport6(function(){
 	               $http({
 	         			method:'post',  
 	         		    url:srvUrl+'formalReport/submitAndupdate.do', 
 	         		    data: $.param({"id":$scope.formalReport._id,"projectFormalId":$scope.formalReport.projectFormalId})
 	         		}).success(function(data){
 	         			if(data.success){
 		     				var istrue = $scope.createwords();
 		     				if(istrue){
 		     					hide_Mask();
 		     					$.alert("提交成功!");
 		     					$('input').attr("readonly","readonly");
 		     	                $('textarea').attr("readonly","readonly");
 		     	                $('button').attr("disabled","disabled");
 		     	                $('#wordbtn').attr("disabled",false);
 		     	                $("#savebtn").hide();
 		     	                $("#submitbnt").hide();
 		     	                $(".modal-footer button").attr({"disabled":false});
 		     				}else{
 		     					hide_Mask();
 		     					$.alert("提交失败，请检查是否填写完整后重新提交或联系管理员!");
 		     					return false;
 		     				}
 		     			}
 	         		}).error(function(data,status,headers, config){
 	         			hide_Mask();
 	         			$.alert(status);
 	         			return false;
 	         		});
 	           });
    	}else{
    		hide_Mask();
    		$.alert("请确保流程已结束!");
    		return false;
    	}
    }
    //点击提交时先生成word，生成成功后方可上会
    $scope.createwords = function(){
    	var boolean = true;
    	$.ajax({
			url: srvUrl+'formalReport/getPfrAssessmentWord.do',
			type: "POST",
			dataType: "json",
			data: {"id":$scope.formalReport._id},
			async: false,
			success: function(data){
				if(!data.success){
					boolean = false;
					$.alert("提交前系统报表生成失败，请查看必填项是否填写完毕；如果全部正常填写请联系管理员!");
	  			}
			}
		});
        return boolean;
    }
   
    $scope.getByID = function(id){
    	 $http({
   			method:'post',  
   		    url:srvUrl+'formalReport/getByID.do', 
   		    data: $.param({"id":id})
   		}).success(function(data){
   			 $scope.formalReport  = data.result_data;
            if(action=="View"){
                $('input').attr("readonly","readonly");
                $('textarea').attr("readonly","readonly");
                $('button').attr("disabled","disabled");
                $("#submitbnt").attr("disabled",false);
                $('#wordbtn').attr("disabled",false);
            }
   		}).error(function(data,status,headers,config){
   			$.alert(status);
   		});
    }
    
    $scope.createWord = function(id){
        startLoading();
        $http({
			method:'post',  
		    url:srvUrl+'formalReport/getPfrAssessmentWord.do', 
		    data: $.param({"id":id})
		}).success(function(data){
	        if (data.success) {
	            var filesPath=data.result_data.filePath;
	            var index = filesPath.lastIndexOf(".");
	            var str = filesPath.substring(index + 1, filesPath.length);
	            var filesName=data.result_data.fileName;
	            window.location.href = srvUrl+"file/downloadFile.do?filepaths="+encodeURI(filesPath)+"&filenames="+encodeURI(encodeURI("正式评审-"+filesName+"报告-单体模式.")) + str;
	
	        } else {
	            $.alert("报表生成失败，请查看必填项是否填写完毕；如果全部正常填写请联系管理员！");
	        }
		}).error(function(data,status,headers, config){
			$.alert(status);
		});
        endLoading();
    }
    
    $scope.initData();
}]);

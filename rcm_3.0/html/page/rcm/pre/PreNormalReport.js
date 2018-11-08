ctmApp.register.controller('PreNormalReport',['$http','$scope','$location','$routeParams','Upload','$timeout', function ($http,$scope,$location,$routeParams,Upload,$timeout) {
    
	$scope.oldUrl = $routeParams.url;
	
	$scope.pre={};
    $scope.pre.apply = {};
    $scope.pre.taskallocation={};
    $scope.pre.approveAttachment = {};
    $scope.pre.reviewReport={};
    $scope.pre.reviewReport.essentialInformation=[];
    $scope.pre.reviewReport.incomeEvaluation=[];
    $scope.dic=[];
    $scope.setDirectiveParam=function(columnName){
        $scope.columnName=columnName;
    }

    //初始化
    $scope.controller_val = $location.$$url.split("/")[1];
    $scope.paramId = $routeParams.id;
    $scope.pmodel =  $routeParams.pmodel;
    $scope.paramAction = $routeParams.action;
    
    $scope._id = "";
    
    $scope.formatDate = function(){
        var date = new Date();
        var paddNum = function(num){
            num += "";
            return num.replace(/^(\d)$/,"0$1");
        }
        return date.getFullYear()+"-"+paddNum(date.getMonth()+1)+"-"+date.getDate()+" "+date.getHours()+":"+date.getMinutes()+":"+date.getSeconds();
    }
    
    $scope.initData = function(){
    	if($scope.paramAction == "Create"){
    		$scope.title = "投资评审报告-新增";
    		$scope.getPreProjectByID($scope.paramId);
    	}else if($scope.paramAction == "Update"){
    		$scope.title = "投资评审报告-修改";
    		 $scope.getByID($scope.paramId);
    	}
    }
    
  //查义所有的操作
    $scope.getPreProjectByID = function(id){
        $http({
    		method:'post',
    		url:srvUrl+'preAuditReport/getPreProjectFormalReviewByID.do',
    		data: $.param({"id":id})
	    	}).success(function(data){
	   		 if(data.success){
	                $scope.pre = data.result_data;
	                $scope.pre.apply.companyHeaderName=$scope.pre.apply.companyHeader.name;
	                $scope.pre.controllerVal = $scope.controller_val;
	                
	                var myDate = new Date();
	                var dates = $scope.formatDate();
	                if(undefined==$scope.pre.reviewReport){
	                        $scope.pre.reviewReport={};
	                        $scope.pre.reviewReport.create_by = $scope.credentials.UUID;
	                        $scope.pre.reviewReport.create_name = $scope.credentials.userName;
	                        $scope.pre.reviewReport.models = $scope.pmodel;
	                        $scope.pre.reviewReport.create_date =dates;
	                        
	                        $scope.pre.reviewReport.essentialInformation= {};
	                        $scope.pre.reviewReport.projectConcernsIssues= {};

	                        if ($scope.isEmpty($scope.pre.reviewReport.projectConcernsIssues)) {
	                            $scope.pre.reviewReport.projectConcernsIssues = {projectSummary: null};
	                            if (null == $scope.pre.reviewReport.projectConcernsIssues.projectSummary) {
	                                $scope.pre.reviewReport.projectConcernsIssues.projectSummary = "根据XX事业部现阶段提供的招标文件及XX部门提供的XX文件，现阶段的项目关注要点及问题总结如下：";
	                            }
	                            $scope.addtableRow(1);
	                            $scope.addtableRow(2);
	                            $scope.addtableRow(3);
	                            $scope.addtableRow(4);
	                        }
	                        $scope.pre.reviewReport.essentialInformation.paymentTime=$scope.pre.apply.paymentTime;
	                        $scope.pre.reviewReport.essentialInformation.tenderTime=$scope.pre.apply.tenderTime;
	                        var wayValue='1';
	                        if(undefined!=$scope.pre.reviewReport.essentialInformation.inputWay){
	                            wayValue= $scope.pre.reviewReport.essentialInformation.inputWay;
	                        }else{
	                            $scope.pre.reviewReport.essentialInformation.inputWay='1';
	                        }

	                        if(wayValue==1){
	                            $("input[name='preAssessmentScale']").css({"display":"block"});
	                            $("#unit").css({"display":"block"});
	                            $("textarea[name='preAssessmentScale']").css({"display":"none"});
	                        }else{
	                            $("#unit").parent().removeClass("has-feedback");
	                            $("input[name='preAssessmentScale']").css({"display":"none"});
	                            $("#unit").css({"display":"none"});
	                            $("textarea[name='preAssessmentScale']").css({"display":"block"});
	                        }

	                        if(undefined==$scope.pre.reviewReport.incomeEvaluation || null==$scope.pre.reviewReport.incomeEvaluation){
	                            $scope.addProfit();
	                        }
	                    $("#wordbtn").hide();
	                }else{
	                	$scope.pre.reviewReport.update_date=dates;
	                }
			 }else{
				 $.alert(data.result_name);
			 }
			}).error(function(data,status,headers, config){
				$.alert(status);
			});
    };
    
    $scope.saveReviewReportById = function(callBack){

        var bankLoan = $("#bankLoan").val();
        var privateCapital = $("#privateCapital").val();
        var sum = parseFloat(bankLoan) + parseFloat(privateCapital);
        var total = parseFloat(100);
        if (sum == total) {
	        var url_post;
	        if ($scope._id != "") {
	        	url_post = 'preAuditReport/updateReviewReport.do';
	        	$scope.pre.reviewReport.update_date = $scope.formatDate();
	        	$scope.pre.reviewReport.update_by = $scope.credentials.UUID;
	            $scope.pre.reviewReport.update_name = $scope.credentials.userName;
	        }else{
	        	url_post = 'preAuditReport/saveReviewReportById.do';
	        }
	        $http({
				method:'post',  
			    url:srvUrl + url_post, 
			    data: $.param({"json":JSON.stringify($scope.pre)})
			}).success(function(data){
				if(data.success){
	                $scope._id = data.result_data;
	                if(typeof callBack == 'function'){
	                	callBack();
	                }else{
	                	$("#wordbtn").show();
	                	$.alert(data.result_name);
	                }
				}else {
	                $.alert(data.result_name);
	            }
			}).error(function(data,status,headers,config){
				$.alert(status);
			});
        } else {
            $.alert("必填项未填或银行贷款和自有资金之合须等于100%！");
        }
    }
    
    $scope.submitReviewReport = function(){
    	if($scope.isPossible2Submit()){
    		$scope.saveReviewReportById(function(){
                $http({
        			method:'post',  
        		    url:srvUrl + 'preAuditReport/submitAndupdate.do', 
        		    data: $.param({"businessid":$scope.paramId})
        		}).success(function(data){
        			if(data.success){
        				$("#savebtnreport").hide();
        				$("#submitbnt").hide();
        				$("#wordbtn").hide();
        				$.alert(data.result_name);
        			}else {
                        $.alert(data.result_name);
                    }
        		}).error(function(data,status,headers,config){
        			$.alert(status);
        		});
    		});
    	}else{
    		$.alert("请确保流程已结束!");
    	}
    }
    
    $scope.getByID = function(id){
    	$http({
   			method:'post',  
   		    url:srvUrl+'preAuditReport/getById.do', 
   		    data: $.param({"id":id})
   		}).success(function(data){
            $scope.pre  = data.result_data;
            $scope._id = $scope.pre._id;
   		}).error(function(data,status,headers,config){
   			$.alert(status);
   		});
    }
    $scope.createWord = function(id){
    	show_Mask();
    	$http({
   			method:'post',  
   		    url:srvUrl+'preAuditReport/getPreWordReport.do', 
   		    data: $.param({"id":id})
   		}).success(function(data){
   			hide_Mask();
   			if(data.success){
   				var filePath=data.result_data.filePath;
                var fileName=data.result_data.fileName;
                window.location.href = srvUrl+"file/downloadFile.do?filepaths="+encodeURI(encodeURI(filePath))+"&filenames="+encodeURI(encodeURI("投资评审-"+fileName+"报告.docx"));
   			}else{
   				$.alert("提交系统报表生成失败，请查看必填项是否填写完毕；如果全部正常填写请联系管理员！");
   			}
   		}).error(function(data,status,headers,config){
   			hide_Mask();
   			$.alert(status);
   		});
    }
    
    //检查报告是否可提交
    $scope.isPossible2Submit = function(){
    	var boolean = false;
    	$.ajax({
			url: srvUrl+'preAuditReport/isPossible2Submit.do',
			type: "POST",
			dataType: "json",
			data: {"businessid":$scope.paramId},
			async: false,
			success: function(data){
				if(data.result_data){
	  				boolean = true;
	  			}
			}
		});
        return boolean;
    }
    
    $scope.isEmpty = function(obj)
    {
        for (var name
            in obj)
        {
            return false;
        }
        return true;
    };
    
  //给第二部分添加行
    $scope.addProfit = function(){
        function addBlankRow(array){
            var blankRow = {
                profitSubject:'',
                profitInvestment:'',
                profitPrivateCapital:'',
                profitCashFlowInfo:''
            }
            array.push(blankRow);
        }
        if(undefined==$scope.pre.reviewReport.incomeEvaluation){
            $scope.pre.reviewReport.incomeEvaluation=[];
        }
        addBlankRow($scope.pre.reviewReport.incomeEvaluation);

    }
    
  //移除第二部分对应数据
    $scope.deleteProfit = function(){
            var commentsObj = $scope.pre.reviewReport.incomeEvaluation;
            if(commentsObj!=null){
                for(var i=0;i<commentsObj.length;i++){
                    if(commentsObj[i].selected){
                        commentsObj.splice(i,1);
                        i--;
                    }
                }
            }
    }
    //给第三部分添加行
    $scope.addtableRow=function(n){
        function addcellRow(array){
            var blankRow = {
                selectType:'',
                pointsAndProblems:'',
                opinionAndCommitment:''
            }
            array.push(blankRow);
        }
        if(undefined==$scope.pre.reviewReport){
            $scope.pre.reviewReport={projectConcernsIssues:{}};
        }
        if(n==1){
            if(undefined==$scope.pre.reviewReport.projectConcernsIssues){
                $scope.pre.reviewReport.projectConcernsIssues={influenceBidding:[]};
            }
            if(undefined==$scope.pre.reviewReport.projectConcernsIssues.influenceBidding){
                $scope.pre.reviewReport.projectConcernsIssues.influenceBidding=[];
            }
            addcellRow($scope.pre.reviewReport.projectConcernsIssues.influenceBidding);
        }else if(n==2){
            if(undefined==$scope.pre.reviewReport.projectConcernsIssues){
                $scope.pre.reviewReport.projectConcernsIssues={focusOnMatter:[]};
            }
            if(undefined==$scope.pre.reviewReport.projectConcernsIssues.focusOnMatter){
                $scope.pre.reviewReport.projectConcernsIssues.focusOnMatter=[];
            }
            addcellRow($scope.pre.reviewReport.projectConcernsIssues.focusOnMatter);
        }else if(n==3){
            if(undefined==$scope.pre.reviewReport.projectConcernsIssues){
                $scope.pre.reviewReport.projectConcernsIssues={agreementPoints:[]};
            }
            if(undefined==$scope.pre.reviewReport.projectConcernsIssues.agreementPoints){
                $scope.pre.reviewReport.projectConcernsIssues.agreementPoints=[];
            }
            addcellRow($scope.pre.reviewReport.projectConcernsIssues.agreementPoints);
        }
    }
    //第三分部删除行
    $scope.delrow=function(name,num){
            if(num==1) {
                var names = $scope.pre.reviewReport.projectConcernsIssues.influenceBidding;
                names.splice(name, 1);
                if (names.length > 0) {
                    $scope.pre.reviewReport.projectConcernsIssues.influenceBidding = names;
                } else {
                    $scope.pre.reviewReport.projectConcernsIssues.influenceBidding = [];
                }
            }else if(num==2) {
                var names = $scope.pre.reviewReport.projectConcernsIssues.focusOnMatter;
                names.splice(name, 1);
                if (names.length > 0) {
                    $scope.pre.reviewReport.projectConcernsIssues.focusOnMatter = names;
                } else {
                    $scope.pre.reviewReport.projectConcernsIssues.focusOnMatter = [];
                }
            }else if(num==3) {
                var names = $scope.pre.reviewReport.projectConcernsIssues.agreementPoints;
                names.splice(name, 1);
                if (names.length > 0) {
                    $scope.pre.reviewReport.projectConcernsIssues.agreementPoints = names;
                } else {
                    $scope.pre.reviewReport.projectConcernsIssues.agreementPoints = [];
                }
            }
    }
  
    $scope.initData();
}]);

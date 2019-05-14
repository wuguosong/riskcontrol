ctmApp.register.controller('EquityAcquisitionReport',['$http','$scope','$location','$routeParams','Upload','$timeout', function ($http,$scope,$location,$routeParams,Upload,$timeout) {
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
    
    function FormatDate() {
        var date = new Date();
        var paddNum = function(num){
            num += "";
            return num.replace(/^(\d)$/,"0$1");
        }
        return date.getFullYear()+""+paddNum(date.getMonth()+1);
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
    $scope.addLaw = function(){
        function addBlankRow(array){
            var blankRow = {
                lawOpinion:'',
                pendingConfirmation:false,
                canNotBeModified:false,
                taskId:taskID
            }
            var size=0;
            for(attr in array){
                size++;
            }
            array[size]=blankRow;
        }

        if(undefined==$scope.formalReport){
            $scope.formalReport={lawList:[]};

        }
        if(undefined==$scope.formalReport.lawList){
            $scope.formalReport.lawList=[];
        }
        addBlankRow($scope.formalReport.lawList);
    }
    $scope.addTechnology = function(){
        function addBlankRow(array){
            var blankRow = {
                technology_opinion:'',
                taskId:taskID
            }
            var size=0;
            for(attr in array){
                size++;
            }
            array[size]=blankRow;
        }

        if(undefined==$scope.formalReport){
            $scope.formalReport={technologyList:[]};

        }
        if(undefined==$scope.formalReport.technologyList){
            $scope.formalReport.technologyList=[];
        }
        addBlankRow($scope.formalReport.technologyList);
    }
    $scope.addFinance = function(){
        function addBlankRow(array){
            var blankRow = {
                finance_opinion:'',
                taskId:taskID
            }
            var size=0;
            for(attr in array){
                size++;
            }
            array[size]=blankRow;
        }

        if(undefined==$scope.formalReport){
            $scope.formalReport={financeList:[]};

        }
        if(undefined==$scope.formalReport.financeList){
            $scope.formalReport.financeList=[];
        }
        addBlankRow($scope.formalReport.financeList);
    }
    $scope.addhrResource = function(){
        function addBlankRow(array){
            var blankRow = {
                hrResource_opinion:'',
                taskId:taskID
            }
            var size=0;
            for(attr in array){
                size++;
            }
            array[size]=blankRow;
        }

        if(undefined==$scope.formalReport){
            $scope.formalReport={hrResourceList:[]};

        }
        if(undefined==$scope.formalReport.hrResourceList){
            $scope.formalReport.hrResourceList=[];
        }
        addBlankRow($scope.formalReport.hrResourceList);
    }
    $scope.addValueEstimation=function(){
        function addBlankRow(array){
            var blankRow = {
                valueEstimation_discount:'',
                valueEstimation_A:'',
                valueEstimation_B:'',
                valueEstimation_C:'',
                taskId:taskID
            }
            var size=0;
            for(attr in array){
                size++;
            }
            array[size]=blankRow;
        }

        if(undefined==$scope.formalReport){
            $scope.formalReport={valueEstimationList:[]};

        }
        if(undefined==$scope.formalReport.valueEstimationList){
            $scope.formalReport.valueEstimationList=[];
        }
        addBlankRow($scope.formalReport.valueEstimationList);
    }
    $scope.addRawMaterials = function(){
        function addBlankRow(array){
            var blankRow = {
                rawMaterialName:'',
                marketContent:'',
                marketDataResource:'',
                areaContent:'',
                areaDataResource:'',
                bidCompanyContent:'',
                bidCompanyDataResource:'',
                taskId:taskID
            }
            var size=0;
            for(attr in array){
                size++;
            }
            array[size]=blankRow;
        }

        if(undefined==$scope.formalReport){
            $scope.formalReport={rawMaterial:[]};

        }
        if(undefined==$scope.formalReport.rawMaterial){
            $scope.formalReport.rawMaterial=[];
        }
        addBlankRow($scope.formalReport.rawMaterial);
    }
    $scope.commonDdelete = function(n){
        var commentsObj=null;
        if(n==0) {
            commentsObj = $scope.formalReport.optionList;
        }else if(n==1){
            commentsObj = $scope.formalReport.requireList;
        }else if(n==2) {
            commentsObj = $scope.formalReport.lawList;
        }else if(n==3){
            commentsObj = $scope.formalReport.technologyList;
        }else if(n==4) {
            commentsObj = $scope.formalReport.financeList;
        }else if(n==5) {
            commentsObj = $scope.formalReport.hrResourceList;
        }else if(n==6) {
            commentsObj = $scope.formalReport.rawMaterial;
        }else if(n==7){
            commentsObj = $scope.formalReport.valueEstimationList;
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
    
    $scope.getProjectFormalReviewByID=function(id){
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
            var ptNameArr=[],pmNameArr=[];
            var pt=$scope.pfr.apply.projectType;
            if(null!=pt && pt.length>0){
                for(var i=0;i<pt.length;i++){
                    ptNameArr.push(pt[i].VALUE);
                }
                $scope.formalReport.projectTypeName=ptNameArr.join(",");
            }
            $scope.formalReport.controllerVal = $scope.controller_val;
            $scope.formalReport.attachment=[{'file':'','fileName':''}];
            $scope.addtableRow(1);
            $scope.addtableRow(2);
            $scope.addtableRow(3);
            $scope.addtableRow(4);
            if(undefined==$scope.formalReport.optionList){
                $scope.addOption();
            }
            if(undefined==$scope.formalReport.requireList){
                $scope.addRequire();
            } if(undefined==$scope.formalReport.lawList){
                $scope.addLaw();
            }
            if(undefined==$scope.formalReport.technologyList){
                $scope.addTechnology();
            }
            if(undefined==$scope.formalReport.financeList){
                $scope.addFinance();
            }
            if(undefined==$scope.formalReport.hrResourceList){
                $scope.addhrResource();
            }
            if(undefined==$scope.formalReport.valueEstimationList){
                $scope.addValueEstimation();
            }
            if(undefined==$scope.formalReport.rawMaterial){
                $scope.addRawMaterials();
            }
            if(null==$scope.formalReport.projectOverview) {
                $scope.formalReport.projectOverview="项目由XX事业部通过XX途径获得，我方接手后由XX事业部负责运营。 本项目的盈利模式为XX。";
            }
            if(null==$scope.formalReport.equityAcquisitionMode) {
                $scope.formalReport.equityAcquisitionMode="拟收购价款XX万元，通过现金方式由XX公司从XX公司处收购XX公司XX%股权，明确如何承接标的公司债务等重要信息价款支付进度为：第一笔：XX；第二笔：XX；第三笔：XX 如有新增投资，也需要在此处说明，并且明确资金来源";
            }
            $scope.addbusinessModel();
            $scope.addcostEstimate();
        
		}).error(function(data,status,headers,config){
			$.alert(status);
		});
    }
    
    $scope.saveReport5 = function(callBack){
        if(typeof callBack == 'function'){
        if ($("#EquityAcquisitionReport").valid()) {
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
    		    url:srvUrl + url_post, 
    		    data: $.param({"json":JSON.stringify($scope.formalReport)})
    		}).success(function(data){
    			if(data.success){
                    $scope.formalReport._id = data.result_data;
                    if(typeof callBack == 'function'){
                        callBack();
                    }else{
                    	$.alert("保存成功");
                    }
                    $("#wordbtn").show();
    			} else {
                    alert(data.result_name);
                }
    		}).error(function(data,status,headers,config){
    			$.alert(status);
    		});
        }else {
            alert("请填写必填数据！")
            hide_Mask();
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
    		    url:srvUrl + url_post, 
    		    data: $.param({"json":JSON.stringify($scope.formalReport)})
    		}).success(function(data){
    			if(data.success){
                    $scope.formalReport._id = data.result_data;
                    if(typeof callBack == 'function'){
                        callBack();
                    }else{
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
 				$scope.saveReport5(function(){
 	    			$http({
 	         			method:'post',  
 	         		    url:srvUrl + 'formalReport/submitAndupdate.do', 
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
 		     	                $("#savebtn").hide();
 		     	                $("#submitbnt").hide();
 		     	                $('#wordbtn').attr("disabled",false);
 		     	                $(".modal-footer button").attr({"disabled":false});
 	         				}else{
 	         					hide_Mask();
 	         					$.alert("提交失败，请检查是否填写完整后重新提交或联系管理员!");
 	         					return false;
 	         				}
 	         			}
 	         		}).error(function(data,status,headers,config){
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
    
    $scope.setDirectiveParam=function(columnName){
        $scope.columnName=columnName;
    }
    $scope.setDirectiveOrgList=function(id,name){
        var params=$scope.columnName;
        if(params=="invertmentUnit"){
            $scope.formalReport.invertmentUnit = {"name":name,value:id};
            $("#invertmentUnitName").val(name);
            $("label[for='invertmentUnitName']").remove();
        }
        if(params=="operationsUnit"){
            $scope.formalReport.operationsUnit= {"name":name,value:id};
            $("#operationsUnitName").val(name);
            $("label[for='operationsUnitName']").remove();
        }
    }
    
    $scope.getByID = function(id){
    	 $http({
  			method:'post',  
  		    url:srvUrl+'formalReport/getByID.do', 
  		    data: $.param({"id":id})
  		}).success(function(data){
            $scope.formalReport  = data.result_data;
            var files=$scope.formalReport.attachment;
            if(files==""){
                $scope.formalReport.attachment=[{'file':'','fileName':''}];
            }
            if(action=="View"){
                $('input').attr("readonly","readonly");
                $('textarea').attr("readonly","readonly");
                $('button').attr("disabled","disabled");
                $("#submitbnt").attr("disabled",false);
                $('#wordbtn').attr("disabled",false);
            }
  		}).error(function(data,status,header,config){
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
    $scope.errorAttach=[];
    $scope.uploadfformal2 = function (file,errorFile, idx) {
        if(errorFile && errorFile.length>0){
        	var errorMsg = fileErrorMsg(errorFile);
            $scope.errorAttach[idx]={msg:errorMsg};
        }else if(file){
            var fileFolder = "formalReport/";
            if($routeParams.action=='Create') {
                if(undefined==$scope.formalReport.projectNo){
                    $.alert("请先选择项目然后上传附件");
                    return false;
                }
                var no=$scope.formalReport.projectNo;
                fileFolder=fileFolder+FormatDate()+"/"+no;
            }else{
                var dates=$scope.formalReport.create_date;
                var no=$scope.formalReport.projectNo;
                var strs= new Array(); //定义一数组
                strs=dates.split("-"); //字符分割
                dates=strs[0]+strs[1]; //分割后的字符输出
                fileFolder=fileFolder+dates+"/"+no;
            }
            $scope.errorAttach[idx]={msg:''};
            Upload.upload({
                url:srvUrl+'file/uploadFile.do',
                data: {file: file, folder:fileFolder}
            }).then(function (resp) {
                var retData = resp.data.result_data[0];
                retData.version = "0";
                $scope.formalReport.attachment=[retData];
            }, function (resp) {
                console.log('Error status: ' + resp.status);
            }, function (evt) {
                var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                $scope["progress"+idx]=progressPercentage == 100 ? "":progressPercentage+"%";
            });
        }
    };

    $scope.downLoadFile = function(idx){
        var filePath = $scope.formalReport.attachment[0].filePath, fileName = $scope.formalReport.attachment[0].fileName;
        window.location.href = srvUrl+"file/downloadFile.do?filepaths="+encodeURI(filePath)+"&filenames="+encodeURI(encodeURI(fileName));
    }
    $scope.getSelectTypeByCode=function(typeCode){
        var  url = 'common/commonMethod/selectDataDictionByCode';
        $scope.httpData(url,typeCode).success(function(data){
            if(data.result_code === 'S'){
                $scope.bussnessTypeList=data.result_data;
            }else{
                alert(data.result_name);
            }
        });
    }
    $scope.getSelectTypeByCode2 = function(typeCode){
        var  url = 'common/commonMethod/selectDataDictionByCode';
        $scope.httpData(url,typeCode).success(function(data){
            if(data.result_code === 'S'){
                $scope.csTypeList=data.result_data;
            }else{
                alert(data.result_name);
            }
        });
    }
    $scope.getSelectTypeByCode3 = function(typeCode){
        var  url = 'common/commonMethod/selectDataDictionByCode';
        $scope.httpData(url,typeCode).success(function(data){
            if(data.result_code === 'S'){
                $scope.techTypeList=data.result_data;
            }else{
                alert(data.result_name);
            }
        });
    }
    angular.element(document).ready(function() {
        $scope.getSelectTypeByCode("12");
        $scope.getSelectTypeByCode2("11");
       $scope.getSelectTypeByCode3("05");
    });
    $scope.addbusinessModel = function(){
        var boolean=true;
        function addBlankRow(array){
            var blankRow = {
                value:null
            }
            var size=0;
            for(attr in array){
                size++;
              /*  if(size==3){
                    boolean=false;
                    break;
                }*/
            }
           /* if(size==3){
                alert("已经超出限制！");
              return false;
            }*/
            array[size]=blankRow;
        }
        if(undefined==$scope.formalReport){
            $scope.formalReport={fProjectName:[],businessModel:[],waterStandard:[],
                technologyType:[],operationLife:[],operationStatus:[],
                residualConcessionPeriod:[],evaluationScale:[],
                contractScale:[],actualWaterTreatment:[],
                measuringWaterQuantity:[],agreementPrice:[],priceAdjustment:[]
            };
        }
        if(undefined==$scope.formalReport.fProjectName){
            $scope.formalReport.fProjectName=[];
        }
        addBlankRow($scope.formalReport.fProjectName);
      if(boolean) {
          if (undefined == $scope.formalReport.businessModel) {
              $scope.formalReport.businessModel = [];
          }
          addBlankRow($scope.formalReport.businessModel);

          if (undefined == $scope.formalReport.waterStandard) {
              $scope.formalReport.waterStandard = [];
          }
          addBlankRow($scope.formalReport.waterStandard);

          if (undefined == $scope.formalReport.technologyType) {
              $scope.formalReport.technologyType = [];
          }
          addBlankRow($scope.formalReport.technologyType);

          if (undefined == $scope.formalReport.operationLife) {
              $scope.formalReport.operationLife = [];
          }
          addBlankRow($scope.formalReport.operationLife);

          if (undefined == $scope.formalReport.operationStatus) {
              $scope.formalReport.operationStatus = [];
          }
          addBlankRow($scope.formalReport.operationStatus);

          if (undefined == $scope.formalReport.residualConcessionPeriod) {
              $scope.formalReport.residualConcessionPeriod = [];
          }
          addBlankRow($scope.formalReport.residualConcessionPeriod);

          if (undefined == $scope.formalReport.evaluationScale) {
              $scope.formalReport.evaluationScale = [];
          }
          addBlankRow($scope.formalReport.evaluationScale);

          if (undefined == $scope.formalReport.contractScale) {
              $scope.formalReport.contractScale = [];
          }
          addBlankRow($scope.formalReport.contractScale);
          if (undefined == $scope.formalReport.actualWaterTreatment) {
              $scope.formalReport.actualWaterTreatment = [];
          }
          addBlankRow($scope.formalReport.actualWaterTreatment);
          if (undefined == $scope.formalReport.measuringWaterQuantity) {
              $scope.formalReport.measuringWaterQuantity = [];
          }
          addBlankRow($scope.formalReport.measuringWaterQuantity);
          if (undefined == $scope.formalReport.agreementPrice) {
              $scope.formalReport.agreementPrice = [];
          }
          addBlankRow($scope.formalReport.agreementPrice);
          if (undefined == $scope.formalReport.priceAdjustment) {
              $scope.formalReport.priceAdjustment = [];
          }
          addBlankRow($scope.formalReport.priceAdjustment);
      }
    }
    $scope.commonbusinessModelDdelete = function(n){
       var fProjectNames= $scope.formalReport.fProjectName;
        if(null!= fProjectNames){
            fProjectNames.splice(n,1);
        }
        var businessModels=  $scope.formalReport.businessModel;
        if(null!=businessModels){
             businessModels.splice(n,1);
        }
        var waterStandards = $scope.formalReport.waterStandard;
        if(null!=waterStandards){
             waterStandards.splice(n,1);
        }
        var technologyTypes = $scope.formalReport.technologyType;
        if(null!=technologyTypes){
             technologyTypes.splice(n,1);
        }
        var operationLifes =$scope.formalReport.operationLife;
        if(null!=operationLifes){
             operationLifes.splice(n,1);
        }
        var operationStatuss=$scope.formalReport.operationStatus;
        if(null!=operationStatuss){
             operationStatuss.splice(n,1);
        }
        var residualConcessionPeriods=$scope.formalReport.residualConcessionPeriod;
        if(null!=residualConcessionPeriods){
             residualConcessionPeriods.splice(n,1);
        }
        var evaluationScales=$scope.formalReport.evaluationScale;
        if(null!=evaluationScales){
             evaluationScales.splice(n,1);
        }
        var contractScales=$scope.formalReport.contractScale;
        if(null!=contractScales){
             contractScales.splice(n,1);
        }
        var actualWaterTreatments=$scope.formalReport.actualWaterTreatment;
        if(null!=actualWaterTreatments){
            actualWaterTreatments.splice(n,1);
        }
        var measuringWaterQuantitys=$scope.formalReport.measuringWaterQuantity;
        if(null!=measuringWaterQuantitys){
             measuringWaterQuantitys.splice(n,1);
        }
        var agreementPrices=$scope.formalReport.agreementPrice;
        if(null!=agreementPrices){
             agreementPrices.splice(n,1);
        }
        var priceAdjustments=$scope.formalReport.priceAdjustment;
        if(null!=priceAdjustments){
             priceAdjustments.splice(n,1);
        }
    }

    $scope.addcostEstimate = function(){
        var boolean=true;
        function addBlankRow(array){
            var blankRow = {
                value:null
            }
            var size=0;
            for(attr in array){
                size++;
                /*if(size==6){
                    boolean=false;
                    break;
                }*/
            }
           /* if(size==6){
                alert("已经超出限制！");
                return false;
            }*/
            array[size]=blankRow;
        }

        if(undefined==$scope.formalReport){
            $scope.formalReport={costEstimate:{}};
        }
        if(undefined==$scope.formalReport.costEstimate){
            $scope.formalReport.costEstimate={fProjectName:[],title:[],electricityTariff:[]
                ,basicElectricity:[],reagentCost:[],disposalFee:[],waterRates:[],
                laborCost:[],maintenanceCost:[],laboratoryTestingFee:[],propertyInsurancePremium:[],
                taxation:[],heatingFee:[],managementExpense:[],valueAddedTax:[],incomeTax:[]
            };
        }
            if(undefined==$scope.formalReport.costEstimate.fProjectName){
                $scope.formalReport.costEstimate.fProjectName=[];
            }
            addBlankRow($scope.formalReport.costEstimate.fProjectName);
        if(boolean) {
            function addTitle(array){
                var size= 0,res;
                for(attr in array){

                    size++;
                }
                if(size%2==0){
                    res='万元/年';
                }else{
                    res='元/吨水';
                }
                var blankRow = {
                    value:res
                }
                array[size]=blankRow;
            }
            for(var i=0;i<2;i++) {
                if (undefined == $scope.formalReport.costEstimate.electricityTariff) {
                    $scope.formalReport.costEstimate.electricityTariff = [];
                }
                addBlankRow($scope.formalReport.costEstimate.electricityTariff);
                if (undefined == $scope.formalReport.costEstimate.title) {
                    $scope.formalReport.costEstimate.title = [];
                }
                addTitle($scope.formalReport.costEstimate.title);
                if (undefined == $scope.formalReport.costEstimate.basicElectricity) {
                    $scope.formalReport.costEstimate.basicElectricity = [];
                }
                addBlankRow($scope.formalReport.costEstimate.basicElectricity);
                if (undefined == $scope.formalReport.costEstimate.reagentCost) {
                    $scope.formalReport.costEstimate.reagentCost = [];
                }
                addBlankRow($scope.formalReport.costEstimate.reagentCost);
                if (undefined == $scope.formalReport.costEstimate.disposalFee) {
                    $scope.formalReport.costEstimate.disposalFee = [];
                }
                addBlankRow($scope.formalReport.costEstimate.disposalFee);
                if (undefined == $scope.formalReport.costEstimate.waterRates) {
                    $scope.formalReport.costEstimate.waterRates = [];
                }
                addBlankRow($scope.formalReport.costEstimate.waterRates);
                if (undefined == $scope.formalReport.costEstimate.laborCost) {
                    $scope.formalReport.costEstimate.laborCost = [];
                }
                addBlankRow($scope.formalReport.costEstimate.laborCost);
                if (undefined == $scope.formalReport.costEstimate.maintenanceCost) {
                    $scope.formalReport.costEstimate.maintenanceCost = [];
                }
                addBlankRow($scope.formalReport.costEstimate.maintenanceCost);
                if (undefined == $scope.formalReport.costEstimate.laboratoryTestingFee) {
                    $scope.formalReport.costEstimate.laboratoryTestingFee = [];
                }
                addBlankRow($scope.formalReport.costEstimate.laboratoryTestingFee);
                if (undefined == $scope.formalReport.costEstimate.propertyInsurancePremium) {
                    $scope.formalReport.costEstimate.propertyInsurancePremium = [];
                }
                addBlankRow($scope.formalReport.costEstimate.propertyInsurancePremium);
                if (undefined == $scope.formalReport.costEstimate.taxation) {
                    $scope.formalReport.costEstimate.taxation = [];
                }
                addBlankRow($scope.formalReport.costEstimate.taxation);
                if (undefined == $scope.formalReport.costEstimate.heatingFee) {
                    $scope.formalReport.costEstimate.heatingFee = [];
                }
                addBlankRow($scope.formalReport.costEstimate.heatingFee);
                if (undefined == $scope.formalReport.costEstimate.managementExpense) {
                    $scope.formalReport.costEstimate.managementExpense = [];
                }
                addBlankRow($scope.formalReport.costEstimate.managementExpense);
                if (undefined == $scope.formalReport.costEstimate.valueAddedTax) {
                    $scope.formalReport.costEstimate.valueAddedTax = [];
                }
                addBlankRow($scope.formalReport.costEstimate.valueAddedTax);
                if (undefined == $scope.formalReport.costEstimate.incomeTax) {
                    $scope.formalReport.costEstimate.incomeTax = [];
                }
                addBlankRow($scope.formalReport.costEstimate.incomeTax);
            }
        }
    }
    $scope.commoncostEstimateDdelete = function(n) {
        var fProjectNames = $scope.formalReport.costEstimate.fProjectName;
        if (null != fProjectNames) {
             fProjectNames.splice(n,1);
        }
    for(var i=0;i<2;i++){
        var title = $scope.formalReport.costEstimate.title;
        if (null != title) {
             title.splice(n,1);
        }
        var electricityTariff = $scope.formalReport.costEstimate.electricityTariff;
        if (null != electricityTariff) {
             electricityTariff.splice(n,1);
        }
        var basicElectricity = $scope.formalReport.costEstimate.basicElectricity;
        if (null != basicElectricity) {
             basicElectricity.splice(n,1);
        }
        var reagentCost = $scope.formalReport.costEstimate.reagentCost;
        if (null != reagentCost) {
             reagentCost.splice(n,1);
        }
        var disposalFee = $scope.formalReport.costEstimate.disposalFee;
        if (null != disposalFee) {
             disposalFee.splice(n,1);
        }
        var waterRates = $scope.formalReport.costEstimate.waterRates;
        if (null != waterRates) {
             waterRates.splice(n,1);
        }
        var laborCost = $scope.formalReport.costEstimate.laborCost;
        if (null != laborCost) {
             laborCost.splice(n,1);
        }
        var maintenanceCost = $scope.formalReport.costEstimate.maintenanceCost;
        if (null != maintenanceCost) {
             maintenanceCost.splice(n,1);
        }
        var laboratoryTestingFee = $scope.formalReport.costEstimate.laboratoryTestingFee;
        if (null != laboratoryTestingFee) {
             laboratoryTestingFee.splice(n,1);
        }
        var propertyInsurancePremium = $scope.formalReport.costEstimate.propertyInsurancePremium;
        if (null != propertyInsurancePremium) {
             propertyInsurancePremium.splice(n,1);
        }
        var taxation = $scope.formalReport.costEstimate.taxation;
        if (null != taxation) {
             taxation.splice(n,1);
        }
        var heatingFee = $scope.formalReport.costEstimate.heatingFee;
        if (null != heatingFee) {
             heatingFee.splice(n,1);
        }
        var managementExpense = $scope.formalReport.costEstimate.managementExpense;
        if (null != managementExpense) {
             managementExpense.splice(n,1);
        }
        var valueAddedTax = $scope.formalReport.costEstimate.valueAddedTax;
        if (null != valueAddedTax) {
             valueAddedTax.splice(n,1);
        }
        var incomeTax = $scope.formalReport.costEstimate.incomeTax;
        if (null != incomeTax) {
             incomeTax.splice(n,1);
        }
    }
    }
    
    $scope.initData();
}]);

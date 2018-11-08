/********
 * Created by wangjian on 16/4/11.
 * 用户管理控制器
 *********/
ctmApp.register.controller('ProjectFormalReviewDetail', ['$http','$scope','$location','$routeParams','Upload','$timeout', function ($http,$scope,$location,$routeParams,Upload,$timeout) {
	$scope.global_projectTypes=new Array();
	$scope.pfr={};
	$scope.pfr.apply={};
	$scope.pfr.taskallocation={};
	$scope.pfr.taskallocation.reviewLeader=null;
	$scope.pfr.taskallocation.fixedGroup=null;
	$scope.columnName="";
	$scope.columnsName="";
	$scope.columnsNum="";
	$scope.isFinished=false;
	$scope.dicSyn=[];
	$scope.investmentPerson=null;
	$scope.directPerson=null;
	//$scope.projectTypeValue=[];
	$scope.setDirectiveParam=function(columnName,param){
		$scope.columnName=columnName;
	}
	$scope.setDirectiveParamTwo=function(columnName,num){
		$scope.columnsName=columnName;
		$scope.columnsNum=num;
	}
	function FormatDate() {
		var date = new Date();
		var paddNum = function(num){
			num += "";
			return num.replace(/^(\d)$/,"0$1");
		}
		return date.getFullYear()+""+paddNum(date.getMonth()+1);
	}
	$scope.getProjectFormalReviewByID=function(id){
		var  url = 'formalAssessment/ProjectFormalReview/getProjectFormalReviewByID';
		$scope.httpData(url,id).success(function(data) {
			var ptNameArr = [], ptValueArr = [], pmNameArr = [], pmValueArr = [], haderNameArr = [], haderValueArr = [];
			$scope.pfr = data.result_data;
			if (null != $scope.pfr.apply.companyHeader) {
				/*var header=$scope.pfr.apply.companyHeader;
				 if(null!=header && header.length>0){
				 for(var i=0;i<header.length;i++){
				 haderNameArr.push(header[i].name);
				 haderValueArr.push(header[i].value);
				 }
				 commonModelValue("companyHeader",haderValueArr,haderNameArr);
				 $scope.pfr.apply.companyHeaderName=haderNameArr.join(",");
				 }*/
				commonModelOneValue("companyHeader", $scope.pfr.apply.companyHeader.value, $scope.pfr.apply.companyHeader.name);
			}

			if (null != $scope.pfr.apply.investmentManager) {
				commonModelOneValue("investmentManager", $scope.pfr.apply.investmentManager.value, $scope.pfr.apply.investmentManager.name);
			}
			if (null != $scope.pfr.apply.grassrootsLegalStaff) {
				if (null != $scope.pfr.apply.grassrootsLegalStaff.value) {
					commonModelOneValue("grassrootsLegalStaff", $scope.pfr.apply.grassrootsLegalStaff.value, $scope.pfr.apply.grassrootsLegalStaff.name);
				}
			}
			if (null != $scope.pfr.apply.investmentPerson) {
				commonModelOneValue("investmentPerson", $scope.pfr.apply.investmentPerson.value, $scope.pfr.apply.investmentPerson.name);
			}
			if (null != $scope.pfr.apply.directPerson) {
				commonModelOneValue("directPerson", $scope.pfr.apply.directPerson.value, $scope.pfr.apply.directPerson.name);
			}
			if (null != $scope.pfr.apply.pertainArea) {
				commonModelOneValue("pertainArea", $scope.pfr.apply.pertainArea.VALUE, $scope.pfr.apply.pertainArea.KEY);
			}
			var isChecked = $scope.pfr.apply.investmentModel;
			if (isChecked) {
				$scope.getprojectmodel('1');
			} else {
				$scope.getprojectmodel('2');
			}
			var pt1NameArr = [], pt1ValueArr = [];
			var pt1 = $scope.pfr.apply.serviceType;
			if (null != pt1 && pt1.length > 0) {
				for (var i = 0; i < pt1.length; i++) {
					pt1NameArr.push(pt1[i].VALUE);
					pt1ValueArr.push(pt1[i].KEY);
				}
				commonModelValue2("oneservicetypebox", pt1ValueArr, pt1NameArr);
			}
			var pt = $scope.pfr.apply.projectType;
			if (null != pt && pt.length > 0) {
				for (var i = 0; i < pt.length; i++) {
					ptNameArr.push(pt[i].VALUE);
					ptValueArr.push(pt[i].KEY);
				}
				commonModelValue2("projecttypebox", ptValueArr, ptNameArr);
			}
			var pm = $scope.pfr.apply.projectModel;
			if (null != pm && pm.length > 0) {
				for (var j = 0; j < pm.length; j++) {
					pmNameArr.push(pm[j].VALUE);
					pmValueArr.push(pm[j].KEY);
				}
				commonModelValue2("projectmodebox", pmValueArr, pmNameArr);
			}
			$scope.isFinished = true;
			if (null != $scope.pfr.apply.investmentPerson) {
				$scope.investmentPerson = $scope.pfr.apply.investmentPerson;
			}
			if (null != $scope.pfr.apply.directPerson) {
				$scope.directPerson = $scope.pfr.apply.directPerson;
			}
			$scope.getNoticeOfDecstionByProjectFormalID($scope.pfr.apply.projectNo);
		});
	}
	//根据id查询决策通知书决策意见
	$scope.getNoticeOfDecstionByProjectFormalID=function(pid){
		var url="formalAssessment/NoticeOfDecision/getNoticeOfDecstionByProjectFormalID";
		$scope.httpData(url,pid).success(function(data){
			if(data.result_code === 'S'){
				if(undefined!=data.result_data) {
					$scope.noticofDec=data.result_data;
					var c = $scope.noticofDec.consentToInvestment;
					if (c == "1") {
						$scope.consentToInvestment = "同意投资";
					} else if (c == "2") {
						$scope.consentToInvestment = "不同意投资";
					} else if (c == 3) {
						$scope.consentToInvestment = "同意有条件投资";
					}
					$scope.implementationMatters = $scope.noticofDec.implementationMatters;
					$scope.dateOfMeeting = $scope.noticofDec.dateOfMeeting;
				}else{
					$scope.consentToInvestment=null;
					$scope.implementationMatters=null;
				}
			}else{
				alert(data.result_name);
			}
		});
	}
	//初始化
	var complexId = $routeParams.id;
	var params = complexId.split("@");
	var objId = params[0];

	if($routeParams.action!='Create'){
		$scope.isHide = true; //use to hide button "导出申请单"
	}else{
		$scope.isHide = false;
	}
	$scope.generateApplication=function(){
		startLoading();
		var url = 'formalAssessment/ProjectFormalReview/getFormalApplication';
		$scope.httpData(url,objId).success(function (data) {
			if (data.result_code=="S") {
				var filesPath=data.result_data.filePath;
				var filesName=data.result_data.fileName;
				window.location.href = srvUrl+"common/RcmFile/downLoad?filePath="+encodeURI(filesPath)+"&fileName="+encodeURI(filesName+"正式评审申请单.docx");
			} else {
				$.alert("申请单导出失败，请查看必填项是否填写完毕；如果全部正常填写请联系管理员！");
			}
		});
		endLoading();
	}
	$scope.getSelectGroupUserUnitByAccount=function(account){
		var url="fnd/Group/getGroupUserReportingUnit";
		$scope.httpData(url,account).success(function(data){
			if(data.result_code === 'S'){
				if(undefined!=data.result_data) {
					$scope.pfr.apply.reportingUnit = {
						name: data.result_data.REPORTINGUNIT_NAME,
						value: data.result_data.REPORTINGUNIT_ID
					};
					$scope.pfr.apply.pertainArea = {
	                    	KEY:data.result_data.orgpkValue,
	                    	VALUE:data.result_data.orgpkName
	                };
					$scope.pfr.apply.companyHeader = {
						name: data.result_data.COMPANYHEADER_NAME,
						value: data.result_data.COMPANYHEADER_ID
					};
					if (null != data.result_data.COMPANYHEADER_ID && "" != data.result_data.COMPANYHEADER_ID) {
						commonModelOneValue("companyHeader", data.result_data.COMPANYHEADER_ID, data.result_data.COMPANYHEADER_NAME);
						$("#companyHeaderName").val(data.result_data.COMPANYHEADER_NAME);
						$("label[for='companyHeaderName']").remove();
					}
					$scope.pfr.apply.grassrootsLegalStaff = {
						name: data.result_data.GRASSROOTSLEGALSTAFF_NAME,
						value: data.result_data.GRASSROOTSLEGALSTAFF_ID
					};
					if (null != data.result_data.GRASSROOTSLEGALSTAFF_ID && "" != data.result_data.GRASSROOTSLEGALSTAFF_ID) {
						commonModelOneValue("grassrootsLegalStaff", data.result_data.GRASSROOTSLEGALSTAFF_ID, data.result_data.GRASSROOTSLEGALSTAFF_NAME);
						$("#grassrootsLegalStaffName").val(data.result_data.GRASSROOTSLEGALSTAFF_ID);
						$("label[for='grassrootsLegalStaffName']").remove();
					}
				}else{
					$.alert("请完善单位-单位负责人-基层法务基础设置!");
					//$location.path("/ProjectFormalReviewList");
				}
			}else{
				alert(data.result_name);
			}
		});
	}
	//定义窗口action
	var action =$routeParams.action;
	if (action == 'Update') {
		$scope.getProjectFormalReviewByID(objId);
		$scope.titleName = "项目正式评审申请修改";
	} else if (action == 'View') {
		$scope.getProjectFormalReviewByID(objId);
		$('#savebtn').hide();
		$('#submitlbtn').hide();
		$('#content-wrapper input').attr("disabled","disabled");
		$('textarea').attr("disabled","disabled");
		$('button').attr("disabled","disabled");
	} else if (action == 'Create') {
		$scope.getSelectGroupUserUnitByAccount($scope.credentials.userID);
		$scope.isFinished=true;
		$scope.pfr.is_supplement_review="0";
		$scope.pfr.istz="0";
		$scope.pfr.create_by=$scope.credentials.UUID;
		$scope.pfr.create_name=$scope.credentials.userName;
		$scope.pfr.apply.investmentManager = {name:$scope.credentials.userName,value:$scope.credentials.UUID};
		$scope.titleName = "项目正式评审申请新增";
	}else if(action=="Edit"){
		$scope.getProjectFormalReviewByID(objId);
		$scope.titleName = "项目正式评审申请修改";
	}
	$scope.setDirectiveCompanyList=function(code,name){
		$scope.pfr.apply.projectNo=code;
		$scope.pfr.apply.projectName=name;
		$("#projectName").val(name);
		$("label[for='projectName']").remove();
	}
	var ztree1, setting1 = {
		callback:{
			onClick:function(event, treeId, treeNode){
				accessScope("#reportingUnitName", function(scope){
					scope.pfr.apply.reportingUnit = {"name":treeNode.name,value:treeNode.id};
					$("#reportingUnitName").val(name);
					$("label[for='reportingUnitName']").remove();
				});
			},
			beforeExpand:function(treeId, treeNode){
				if(typeof(treeNode.children)=='undefined'){
					$scope.addTreeNode1(treeNode);
				}
			}
		}
	};
	$scope.addTreeNode1 = function (parentNode){
		var pid = '';
		if(parentNode && parentNode.id) pid = parentNode.id;
		$scope.httpData('fnd/Group/getOrg', {parentId:pid}).success(function(data){
			if (!data || data.result_code != 'S') return null;
			var nodeArray = data.result_data;
			if(nodeArray<1) return null;
			for(var i=0;i<nodeArray.length;i++){
				curNode = nodeArray[i];
				var iconUrl = 'assets/javascripts/zTree/css/zTreeStyle/img/department.png';
				if(curNode.cat && curNode.cat=='Org'){
					iconUrl = 'assets/javascripts/zTree/css/zTreeStyle/img/org.png';
				}
				curNode.icon = iconUrl;
			}
			if(pid == ''){//当前加载的是根节点
				ztree1.addNodes(null, nodeArray);
				var rootNode = ztree1.getNodes()[0];
				$scope.addTreeNode1(rootNode);
				rootNode.open = true;
				ztree1.refresh();
			}else{
				ztree1.addNodes(parentNode, nodeArray, true);
			}
		});
	}
	angular.element(document).ready(function() {
		ztree1 = $.fn.zTree.init($("#treeID1"), setting1);
		$scope.addTreeNode1('');
	});
	$scope.save= function (callBack) {
		if (callBack && typeof callBack == 'function') {
		if($("#myFormPfR").valid()) {
			var postObj;
			var url="";
			if (typeof ($scope.pfr._id) != "undefined") {
				url =  'formalAssessment/ProjectFormalReview/update';
			} else {
				url =  'formalAssessment/ProjectFormalReview/createProjectFormalReview';
			}
			var projectType = $scope.pfr.apply.projectType;
        	var arr = new Array();
        	for(var i = 0; i < projectType.length; i++){
        		var obj = new Object();
        		obj.KEY=projectType[i].KEY.substring(projectType[i].KEY.indexOf("_")+1)
        		obj.VALUE=projectType[i].VALUE;
        		obj.PARENT_ID=projectType[i].PARENT_ID;
        		arr.push(obj);
        	}
        	$scope.pfr.apply.projectType=arr;
			postObj = $scope.httpData(url, $scope.pfr);
			postObj.success(function (data) {
				$scope.pfr._id = data.result_data;
				if (data.result_code === 'S') {
					if (callBack && typeof callBack == 'function') {
						callBack();
					} else {
						$.alert("保存成功");
					}
				} else {
					$.alert(data.result_name);
				}
			});
		 }
		}else{
			var postObj;
			var url="";
			if (typeof ($scope.pfr._id) != "undefined") {
				url =  'formalAssessment/ProjectFormalReview/update';
			} else {
				url =  'formalAssessment/ProjectFormalReview/createProjectFormalReview';
			}
			var projectName=$scope.pfr.apply.projectName;
			if(null==projectName || ""==projectName){
				$.alert("项目名称必填！");
				return false;
			}
			var projectType = $scope.pfr.apply.projectType;
			var arr = new Array();
			for(var i = 0; i < projectType.length; i++){
				var obj = new Object();
				obj.KEY=projectType[i].KEY.substring(projectType[i].KEY.indexOf("_")+1)
				obj.VALUE=projectType[i].VALUE;
				obj.PARENT_ID=projectType[i].PARENT_ID;
				arr.push(obj);
			}
			$scope.pfr.apply.projectType=arr;
			postObj = $scope.httpData(url, $scope.pfr);
			postObj.success(function (data) {
				$scope.pfr._id = data.result_data;
				if (data.result_code === 'S') {
					if (callBack && typeof callBack == 'function') {
						callBack();
					} else {
						$.alert("保存成功");
					}
				} else {
					$.alert(data.result_name);
				}
			});
		}
	}
	var commonModelValue=function(paramsVal,arrID,arrName){
		$("#header"+paramsVal+"Name").find(".select2-choices").html("<li class=\"select2-search-field\"><input id=\"s2id_autogen2\" class=\"select2-input\" type=\"text\" spellcheck=\"false\" autocapitalize=\"off\" autocorrect=\"off\" autocomplete=\"off\" style=\"width: 16px;\"> </li>");
		var leftstr="<li class=\"select2-search-choice\"><div>";
		var centerstr="</div><a class=\"select2-search-choice-close\" tabindex=\"-1\" onclick=\"delObj(this,'";
		var addID="');\"  ></a><div class=\"full-drop\"><input type=\"hidden\" id=\"\"  value=\"";
		var rightstr="\"></div></li>";
		for(var i=0;i<arrName.length;i++){
			$("#header"+paramsVal+"Name").find(".select2-search-field").before(leftstr+arrName[i]+centerstr+paramsVal+"','"+arrID[i]+"','"+arrName[i]+addID+arrID[i]+rightstr);
		}
	}
	var commonModelOneValue=function(paramsVal,arrID,arrName){
		$("#header"+paramsVal+"Name").find(".select2-choices").html("<li class=\"select2-search-field\"><input id=\"s2id_autogen2\" class=\"select2-input\" type=\"text\" spellcheck=\"false\" autocapitalize=\"off\" autocorrect=\"off\" autocomplete=\"off\" style=\"width: 16px;\"> </li>");
		var leftstr="<li class=\"select2-search-choice\"><div>";
		var centerstr="</div><a class=\"select2-search-choice-close\" tabindex=\"-1\" onclick=\"delObj(this,'";
		var addID="');\"  ></a><div class=\"full-drop\"><input type=\"hidden\" id=\"\"  value=\"";
		var rightstr="\"></div></li>";
		$("#header"+paramsVal+"Name").find(".select2-search-field").before(leftstr+arrName+centerstr+paramsVal+"','"+arrID+"','"+arrName+addID+arrID+rightstr);
	}
	var commonModelValue2=function(paramsVal,arrID,arrName){
		var leftstr2="<li class=\"select2-search-choice\"><div>";
		var centerstr2="</div><a class=\"select2-search-choice-close\" tabindex=\"-1\" onclick=\"delSelect(this,'";
		var addID2="');\"  ></a><div class=\"full-drop\"><input type=\"hidden\" id=\"\"  value=\"";
		var rightstr2="\"></div></li>";
		for(var i=0;i<arrName.length;i++){
			$("#header"+paramsVal).find(".select2-search-field").before(leftstr2+arrName[i]+centerstr2+paramsVal+"','"+arrName[i]+"','"+arrID[i]+addID2+arrID[i]+rightstr2);
		}
	}
	//模式窗口需要设置到父窗口调用该方法
	$scope.setDirectiveUserList=function(arrID,arrName,arrUserNamesValue){
		var paramsVal=$scope.columnName;
		if(paramsVal=="companyHeader"){
			$scope.pfr.apply.companyHeader =arrUserNamesValue;  //赋值保存到数据库
			$("#companyHeaderName").val(arrUserNamesValue);
			$("label[for='companyHeaderName']").remove();
		}
		commonModelValue(paramsVal,arrID,arrName);
	}
	//附件中选择人员设值
	$scope.setDirectiveRadioUserList=function(value,name){
		var paramsVal=$scope.columnsName;
		var paramsNum=$scope.columnsNum;
		if(paramsVal=="programmed"){
			if(undefined==$scope.pfr.attachment[paramsNum].files){
				$scope.pfr.attachment[paramsNum].files=[];
			}
			if(undefined==$scope.pfr.attachment[paramsNum].files[0]){
				$scope.pfr.attachment[paramsNum].files[0]={programmed:{}};
			}
			if(undefined==$scope.pfr.attachment[paramsNum].files[0].programmed){
				$scope.pfr.attachment[paramsNum].files[0].programmed={};
			}
			$scope.pfr.attachment[paramsNum].files[0].programmed={name:name,value:value};
		}else if(paramsVal=="approved"){
			if(undefined==$scope.pfr.attachment[paramsNum].files){
				$scope.pfr.attachment[paramsNum].files=[];
			}
			if(undefined==$scope.pfr.attachment[paramsNum].files[0]){
				$scope.pfr.attachment[paramsNum].files[0]={approved:{}};
			}
			if(undefined==$scope.pfr.attachment[paramsNum].files[0].approved){
				$scope.pfr.attachment[paramsNum].files[0].approved={};
			}
			$scope.pfr.attachment[paramsNum].files[0].approved={name:name,value:value};
		}else if(paramsVal=="companyHeader"){
			$scope.pfr.apply.companyHeader ={name:name,value:value};//赋值保存到数据库
			$("#companyHeaderName").val(name);
			$("label[for='companyHeaderName']").remove();
			commonModelOneValue(paramsVal,value,name);
		}else if(paramsVal=="investmentManager"){
			$scope.pfr.apply.investmentManager ={name:name,value:value};//赋值保存到数据库
			$("#investmentManagerName").val(name);
			$("label[for='investmentManagerName']").remove();
			commonModelOneValue(paramsVal,value,name);
		}else if(paramsVal=="grassrootsLegalStaff"){
			$scope.pfr.apply.grassrootsLegalStaff ={name:name,value:value};//赋值保存到数据库
			$("#grassrootsLegalStaffName").val(name);
			$("label[for='grassrootsLegalStaffName']").remove();
			commonModelOneValue(paramsVal,value,name);
		}else if(paramsVal=="investmentPerson"){
			$scope.pfr.apply.investmentPerson ={name:name,value:value};//赋值保存到数据库
			$("#investmentPersonName").val(name);
			$("label[for='investmentPersonName']").remove();
			commonModelOneValue(paramsVal,value,name);
		}else if(paramsVal=="directPerson"){
			$scope.pfr.apply.directPerson ={name:name,value:value};//赋值保存到数据库
			$("#directPersonName").val(name);
			$("label[for='directPersonName']").remove();
			commonModelOneValue(paramsVal,value,name);
		}
	}
	//
	$scope.getSelectTypeByCode=function(typeCode){
		var url="formalAssessment/ProjectFormalReview/SelectType";
		$scope.httpData(url,typeCode).success(function(data){
			if(data.result_code === 'S'){
				$scope.dic=[];
				//$scope.dic.projectTypeValue=data.result_data.projectType;
				$scope.dic.projectModelValue=data.result_data.projectModel;
				$scope.dic.companyCategorys=data.result_data.companyCategorys;
			}else{
				alert(data.result_name);
			}
		});
	}
	$scope.$watch("pfr.apply.serviceType", function(){
    	var projectTypeArr=[];
		var serviceTypeKeyArr=[];
        var serviceTypes = $scope.pfr.apply.serviceType;
        if(null!=serviceTypes && serviceTypes.length>0){
            for(var i=0;i<serviceTypes.length;i++){
            	for(var m = 0; m < $scope.global_projectTypes.length; m++){
            		var pt = $scope.global_projectTypes[m];
            		if(serviceTypes[i].KEY == pt.PARENT_ID){
            			projectTypeArr.push({"KEY":pt.PARENT_ID+"_"+pt.KEY, "VALUE":pt.VALUE,"PARENT_ID":pt.PARENT_ID});
            		}
            	}
				serviceTypeKeyArr.push(serviceTypes[i].KEY);
            }
            $scope.projectTypeValueKEY = projectTypeArr;
        } else{
            $scope.projectTypeValueKEY = [];
        }
        var projectTypes = $scope.pfr.apply.projectType;
        if(projectTypes!=null && projectTypes.length>0 && projectTypes[0].KEY.indexOf("_")<0){
        	//如果是第一次修改，key要拼id
        	var arr = new Array();
        	for(var i = 0; i<projectTypes.length; i++){
        		var obj = new Object();
        		obj.KEY=projectTypes[i].PARENT_ID+"_"+projectTypes[i].KEY;
        		obj.VALUE=projectTypes[i].VALUE;
        		obj.PARENT_ID=projectTypes[i].PARENT_ID;
        		arr.push(obj);
        	}
        	projectTypes = arr;
        }
        var result = [];
        var ids = [], names = [];
        for(var i = 0; projectTypes!=null && i<projectTypes.length; i++){
        	for(var m = 0; m < $scope.projectTypeValueKEY.length; m++){
        		var str = $scope.projectTypeValueKEY[m].KEY;
        		if(projectTypes[i].KEY == str && 
        				projectTypes[i].PARENT_ID == $scope.projectTypeValueKEY[m].PARENT_ID){
        			result.push(projectTypes[i]);
        			ids.push(projectTypes[i].KEY);
        			names.push(projectTypes[i].VALUE);
        			break;
        		}
        	}
        }
		if(null!=serviceTypeKeyArr && serviceTypeKeyArr.length>0){
			var investmentperson=[],investmentpersonID=[],investmentpersonNAME=[];
			var directperson=[],directpersonID=[],directpersonNAME=[];
			$scope.httpData("fnd/Group/findDirectUserReportingUnitList",{TYPE:serviceTypeKeyArr.join(","),REPORTINGUNIT_ID:$scope.pfr.apply.reportingUnit.value}).success(function(data){
				if(data.result_code == "S") {
					var newArray = data.result_data;
					if (newArray.length > 0) {
						for (var i = 0; i < newArray.length; i++) {
							if(undefined!=newArray[i].INVESTMENTPERSON_ID && null!=newArray[i].INVESTMENTPERSON_ID && ''!=newArray[i].INVESTMENTPERSON_ID) {
								if (investmentpersonID.indexOf(newArray[i].INVESTMENTPERSON_ID) == -1) {
									investmentperson.push({
										value: newArray[i].INVESTMENTPERSON_ID,
										name: newArray[i].INVESTMENTPERSON_NAME
									});
									investmentpersonID.push(newArray[i].INVESTMENTPERSON_ID);
									investmentpersonNAME.push(newArray[i].INVESTMENTPERSON_NAME);
								}
							}
							if(undefined!=newArray[i].DIRECTPERSON_ID && null!=newArray[i].DIRECTPERSON_ID && ''!=newArray[i].DIRECTPERSON_ID) {
								if (directpersonID.indexOf(newArray[i].DIRECTPERSON_ID) == -1) {
									directperson.push({
										value: newArray[i].DIRECTPERSON_ID,
										name: newArray[i].DIRECTPERSON_NAME
									});
									directpersonID.push(newArray[i].DIRECTPERSON_ID);
									directpersonNAME.push(newArray[i].DIRECTPERSON_NAME);
								}
							}
						}
						$scope.investmentPersonList = investmentperson;
						$scope.directpersonList = directperson;

							if(investmentperson.length==1){
								$scope.pfr.apply.investmentPerson =investmentperson[0];
								$("#investmentPersonName").val(investmentperson[0]);
								$("label[for='investmentPersonName']").remove();
							}else{
								$scope.pfr.apply.investmentPerson =null;
								$("#investmentPersonName").val("");
							}
							if(directperson.length==1){
								$scope.pfr.apply.directPerson =directperson[0];
								$("#directPersonName").val(directperson[0]);
								$("label[for='directPersonName']").remove();
							}else{
								$scope.pfr.apply.directPerson = null;
								$("#directPersonName").val("");
							}
						if(null!=$scope.investmentPerson) {
							$scope.pfr.apply.investmentPerson = $scope.investmentPerson;
							for (var i in $scope.investmentPersonList) {
								if ($scope.investmentPersonList[i] == $scope.pfr.apply.investmentPerson) {//将d是4的城市设为选中项.
									$scope.pfr.apply.investmentPerson = $scope.investmentPersonList[i];
									break;
								}
							}
							$scope.investmentPerson=null;
						}
						if(null!=$scope.directPerson) {
							$scope.pfr.apply.directPerson = $scope.directPerson;
							for (var i in $scope.directpersonList) {
								if ($scope.directpersonList[i] == $scope.pfr.apply.directPerson) {//将d是4的城市设为选中项.
									$scope.pfr.apply.directPerson = $scope.directpersonList[i];
									break;
								}
							}
							$scope.directPerson=null;
						}
					} else {
						$scope.investmentPersonList = null;
						$scope.directpersonList = null;
						$scope.pfr.apply.investmentPerson =null;
						$("#investmentPersonName").val("");
						$scope.pfr.apply.directPerson =null;
						$("#directPersonName").val("");
					}
				}
			});
		}else{
			$scope.investmentPersonList = null;
			$scope.directpersonList = null;
			$scope.pfr.apply.investmentPerson =null;
			$("#investmentPersonName").val("");
			$scope.pfr.apply.directPerson =null;
			$("#directPersonName").val("");
		}
		/*$timeout(function() {

		}, 300,true);*/
        $scope.pfr.apply.projectType=result;
        $("#headerprojecttypebox").find(".select2-choices .select2-search-choice").remove();
        commonModelValue2("projecttypebox", ids, names);
}, true);
	$scope.$watch("pfr.apply.projectModel", function(){
		var projectTypeArr=[];
		var projectTypes = $scope.pfr.apply.projectModel;
		if(null!=projectTypes && projectTypes.length>0){
			for(var i=0;i<projectTypes.length;i++){
				projectTypeArr.push(projectTypes[i].KEY);
			}
		}
		if(projectTypeArr.length>0 && $scope.isFinished){
			var dictype=$scope.pfr.apply.investmentModel;
			if(dictype){
				dictype="1";
			}else{
				dictype="2";
			}
			$scope.httpData("formalAssessment/ProjectFormalReview/findAttachmentList",{projectTypes:projectTypeArr.join(","),dicTypes:dictype}).success(function(data){
				if(data.result_code == "S"){
					var newArray = data.result_data, oldArray = $scope.pfr.attachment,
						oldArray = mergeArray(oldArray, newArray,'ITEM_NAME');
					$scope.pfr.attachment = oldArray;
				}
			});
		}else{
			$scope.pfr.attachment = [];
		}
	});
	$scope.getSyncbusinessmodel=function(keys){
		var url="businessDict/queryBusinessType.do";
		$scope.httpData(url,keys).success(function(data){
			if(data.success){
				$scope.dicSyn.Syncbusinessmodel=data.result_data;
			}else{
				alert(data.result_name);
			}
		});
		
		//加载所有的二级业务类型数据，存入全局变量
        var url=srvUrl+"common/commonMethod/queryAllProjectTypes";
        $.ajax({
        	url: url,
        	type: "GET",
        	dataType: "json",
        	async: false,
        	success: function(data){
        		if(data.result_code === 'S'){
                	$scope.global_projectTypes=data.result_data;
                }else{
                    alert(data.result_name);
                }
        	}
        });
	}
	$scope.changeProjectType=function(value){
		$("#s2id_projecttypeboxName").find(".select2-choices .select2-search-choice").remove();
		$scope.pfr.apply.projectType=null;
		var url="common/commonMethod/selectsyncbusinessmodelBykey";
		var dataV={'parentId':value.KEY};
		$scope.httpData(url,dataV).success(function(data){
			if(data.result_code === 'S'){
				$scope.projectTypeValueKEY=data.result_data;
			}else{
				$.alert(data.result_name);
			}
		});
	}
	$scope.getprojectmodel=function(keys){
		var url= "common/commonMethod/selectsyncbusinessmodel";
		$scope.httpData(url,keys).success(function(data){
			if(data.result_code === 'S'){
				$scope.dicSyn.projectModelValue=data.result_data;
			}else{
				alert(data.result_name);
			}
		});
	}
	$scope.changeInvestmentModel=function(){
		var pid="";
		if($("#investmentModel").is(':checked')){
			pid="1";
		}else{
			pid="2";
		}
		$("#s2id_projectmodeboxName").find(".select2-choices .select2-search-choice").remove();
		$scope.pfr.apply.projectModel=null;
		$scope.getprojectmodel(pid);
	}
	$scope.changeSupplementReview=function(){
		if($("#supplementReview").is(':checked')){
			$scope.pfr.is_supplement_review=1;
			if (typeof ($scope.pfr.apply.projectNo) != "undefined") {
				$scope.getNoticeOfDecstionByProjectFormalID($scope.pfr.apply.projectNo);
			}else{
				$scope.pfr.apply.supplementReview=false;
				$.alert("请输入项目名称后点击");
				$("#supplementReview").attr('checked',false);
				$scope.pfr.is_supplement_review=0;
			}
		}else{
			$scope.pfr.is_supplement_review=0;
		}
	}
	$scope.changDate=function(values){
		var date = new Date();
		var paddNum = function(num){
			num += "";
			return num.replace(/^(\d)$/,"0$1");
		}
		var nowDate=date.getFullYear()+"-"+paddNum(date.getMonth()+1)+"-"+paddNum(date.getDate());
		var d=DateDiff(values,nowDate);
		$("#shouwdate").text("距离签约时间还差："+d+"天");
		$scope.pfr.apply.distanceContractDate=d;
	}
	$scope.errorAttach=[];
	$scope.upload = function (file,errorFile, idx) {
		if(errorFile && errorFile.length>0){
			var errorMsg = fileErrorMsg(errorFile);
			$scope.errorAttach[idx]={msg:errorMsg};
		}else if(file){
			var fileFolder = "pfrAssessment/";
			if($routeParams.action=='Create') {
				if(undefined==$scope.pfr.apply.projectNo){
					$.alert("请先选择项目然后上传附件");
					return false;
				}
				var no=$scope.pfr.apply.projectNo;
				fileFolder=fileFolder+FormatDate()+"/"+no;
			}else{
				var dates=$scope.pfr.create_date;
				var no=$scope.pfr.apply.projectNo;
				var strs= new Array(); //定义一数组
				strs=dates.split("-"); //字符分割
				dates=strs[0]+strs[1]; //分割后的字符输出
				fileFolder=fileFolder+dates+"/"+no;
			}
			$scope.errorAttach[idx]={msg:''};
			Upload.upload({
				url:srvUrl+'common/RcmFile/upload',
				data: {file: file, folder:fileFolder}
			}).then(function (resp) {
				var retData = resp.data.result_data[0];
				retData.version = "1";
				var myDate = new Date();
				var paddNum = function(num){
					num += "";
					return num.replace(/^(\d)$/,"0$1");
				}
				retData.upload_date=myDate.getFullYear()+"-"+paddNum(myDate.getMonth()+1)+"-"+ myDate.getDate()+" "+myDate.getHours()+":"+myDate.getMinutes()+":"+myDate.getSeconds();
				if(undefined!=null!=$scope.pfr.attachment[idx].files && null!=$scope.pfr.attachment[idx].files){
					if(undefined!=null!=$scope.pfr.attachment[idx].files[0] && null!=$scope.pfr.attachment[idx].files[0]) {
						if (null != $scope.pfr.attachment[idx].files[0].programmed) {
							retData.programmed = $scope.pfr.attachment[idx].files[0].programmed;
						}
						if(null!=$scope.pfr.attachment[idx].files[0].approved){
							retData.approved=$scope.pfr.attachment[idx].files[0].approved;
						}
					}
				}
				$scope.pfr.attachment[idx].files=[retData];
			}, function (resp) {
				console.log('Error status: ' + resp.status);
			}, function (evt) {
				var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
				$scope["progress"+idx]=progressPercentage == 100 ? "":progressPercentage+"%";
			});
		}
	};
	//手动初始化，目的先显示组织树
	angular.element(document).ready(function() {
		$scope.getSelectTypeByCode('01,02,05');
		$scope.getSyncbusinessmodel('0');
		if(action=="Create"){
			$scope.getprojectmodel('2');
		}
	});

	//弹出审批框
	$scope.showSubmitModal = function(){
//		$scope.save(function(){
			var companyHeader = $scope.pfr.apply.companyHeader;
			$scope.approve = [{
				submitInfo:{
					startVar:{processKey:'formalAssessment',businessId:$scope.pfr._id,subject:$scope.pfr.apply.projectName+'正式评审申请',inputUser:$scope.credentials.UUID},
					runtimeVar:{inputUser:companyHeader.value},
					currentTaskVar:{opinion:'请审批'}
				},
				showInfo:{destination:'单位负责人审核',approver: companyHeader.name}
			}];
			if(($routeParams.action =='Update' || $routeParams.action=='Edit') && params[1]){
				$scope.approve[0].submitInfo.taskId=params[3];
			}
			$('#submitModal').modal('show');
//		});
	};
	if(typeof (params[1])=='string' && typeof (params[2])=='string' && params[1]!='' && params[2]!=''){//已经启动流程
		$scope.wfInfo = {processDefinitionId:params[1],processInstanceId:params[2]};
	}else{//未启动流程
		$scope.wfInfo = {processKey:'formalAssessment'};
	}
}]);

/*function callbackV(pkvalue,name){
	var treeCode=  $("#treeCode").val();
	if(treeCode=='treeCode1'){
		accessScope("#reportingUnitName", function(scope){
			scope.pfr.apply.reportingUnit = {"name":name,value:pkvalue};
		});
	}
}*/
function delObj(o,paramsVal,value,name){
	$(o).parent().remove();
	accessScope("#"+paramsVal+"Name", function(scope){
		if(paramsVal=="companyHeader"){
			scope.pfr.apply.companyHeader={name:'',value:''};
			$("#companyHeaderName").val("");
		}else if(paramsVal=="investmentManager"){
			scope.pfr.apply.investmentManager ={name:'',value:''};
			$("#investmentManagerName").val("");
		}else if(paramsVal=="grassrootsLegalStaff"){
			scope.pfr.apply.grassrootsLegalStaff ={name:'',value:''};//赋值保存到数据库
			$("#grassrootsLegalStaffName").val("");
		}else if(paramsVal=="investmentPerson"){
			scope.pfr.apply.investmentPerson ={name:'',value:''};//赋值保存到数据库
			$("#investmentPersonName").val("");
		}else if(paramsVal=="directPerson"){
			scope.pfr.apply.directPerson ={name:'',value:''};//赋值保存到数据库
			$("#directPersonName").val("");
		}
	});
}
function delSelect(o,paramsVal,name,id){
	$(o).parent().remove();
	accessScope("#"+paramsVal+"Name", function(scope){
		if(paramsVal=="oneservicetypebox"){
			var names=scope.pfr.apply.serviceType;
			var newNames = $(names).map(function(){
            	if(this.KEY!=id){
            		return this;
            	}
            }).get();
            if(newNames.length>0){
            	scope.pfr.apply.serviceType=newNames;
            }else{
                scope.pfr.apply.serviceType=null;
            }
            
		}else if(paramsVal=="projecttypebox"){
			var names=scope.pfr.apply.projectType;
			var newNames = $(names).map(function(){
            	if(this.KEY!=id){
            		return this;
            	}
            }).get();
			if(newNames.length>0){
				scope.pfr.apply.projectType=newNames;
			}else{
				scope.pfr.apply.projectType=null;
			}
		}else if(paramsVal=="projectmodebox"){
			var names=scope.pfr.apply.projectModel;
			var newNames = $(names).map(function(){
            	if(this.KEY!=id){
            		return this;
            	}
            }).get();
			if(newNames.length>0){
				scope.pfr.apply.projectModel=newNames;
			}else{
				scope.pfr.apply.projectModel=null;
			}
		}
	});
}


ctmApp.register.controller('ProjectFormalReviewDetailView',
['$http','$scope','$location','$routeParams','Upload','$timeout', 
 function ($http,$scope,$location,$routeParams,Upload,$timeout) {
	//初始化
	var action = $routeParams.action;
	var complexId = $routeParams.id;
	var params = complexId.split("@");
	var objId = params[0];
	if(null!=params[4] && ""!=params[4]){
		$scope.flag=params[4];
	}
	$scope.pfr={};
	$scope.pfr.apply = {};
	$scope.pfr.taskallocation={};
	$scope.pfr.approveAttachment = {};
	$scope.pfr.pfrBusinessUnitCommit=[];
	$scope.pfr._id=objId;
	$scope.dic=[];

	$scope.generateApplication=function(){
		startLoading();
		var url = 'formalAssessment/ProjectFormalReview/getFormalApplication';
		$scope.httpData(url,objId).success(function (data) {
			if (data.result_code=="S") {
				var filesPath=data.result_data.filePath;
				var filesName=data.result_data.fileName;
				window.location.href = srvUrl+"common/RcmFile/downLoad?filePath="+encodeURI(filesPath)+"&fileName="+encodeURI(filesName+"正式评审申请单.docx");
			} else {
				$.alert("申请单导出失败，请查看必填项是否填写完毕；如果全部正常填写请联系管理员！");
			}
		});
		endLoading();
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
    
	$scope.getProjectFormalReviewByID=function(id){
		var  url = 'formalAssessment/ProjectFormalReview/getProjectFormalReviewByID';
		$scope.httpData(url,id).success(function(data){
			$scope.pfr  = data.result_data;
			$scope.attach = data.result_data.attach;
			//处理附件
            $scope.reduceAttachment(data.result_data.attachment);
			var ptNameArr=[],pmNameArr=[],pthNameArr=[],fgNameArr=[];
			/*var pth=$scope.pfr.apply.companyHeader;
			if(null!=pth && pth.length>0){
				for(var i=0;i<pth.length;i++){
					pthNameArr.push(pth[i].name);
				}
				$scope.pfr.apply.companyHeaderName=pthNameArr.join(",");
			}*/
			var pt1NameArr=[];
			var pt1=$scope.pfr.apply.serviceType;
			if(null!=pt1 && pt1.length>0){
				for(var i=0;i<pt1.length;i++){
					pt1NameArr.push(pt1[i].VALUE);
				}
				$scope.pfr.apply.serviceType=pt1NameArr.join(",");
			}
			var pt=$scope.pfr.apply.projectType;
			if(null!=pt && pt.length>0){
				for(var i=0;i<pt.length;i++){
					ptNameArr.push(pt[i].VALUE);
				}
				$scope.pfr.apply.projectType=ptNameArr.join(",");
			}
			var pm=$scope.pfr.apply.projectModel;
			if(null!=pm && pm.length>0){
				for(var j=0;j<pm.length;j++){
					pmNameArr.push(pm[j].VALUE);
				}
				$scope.pfr.apply.projectModel=pmNameArr.join(",");
			}
			if(undefined!=$scope.pfr.taskallocation){
				if(undefined!=$scope.pfr.taskallocation.fixedGroup){
					var fg=$scope.pfr.taskallocation.fixedGroup;
					if(null!=fg && fg.length>0){
						for(var k=0;k<fg.length;k++){
							fgNameArr.push(fg[k].NAME);
						}
						$scope.pfr.taskallocation.fixedGroup=fgNameArr.join(",");
					}
				}
			}
			$scope.fileName=[];
			var filenames=$scope.pfr.attachment;
			for(var i=0;i<filenames.length;i++){
				var arr={UUID:filenames[i].UUID,ITEM_NAME:filenames[i].ITEM_NAME};
				$scope.fileName.push(arr);
			}
			if(action=="View"){
				//查询项目的状态，如果不为起草中，则隐藏提交按钮
				var queryProjectStatusUrl = srvUrl+"rcm/ProjectInfo/selectByBusinessId"; 
				$.ajax({
					url: queryProjectStatusUrl,
					type: "POST",
					dataType: "json",
					data: objId,
					async: false,
					success: function(data){
						if(data.result_data==null || data.result_data.WF_STATE!="0"){
							$("#submitlbtn").hide();
						}
					}
				});
			}
			if (null != $scope.pfr.apply.expectedContractDate) {
				$scope.changDate($scope.pfr.apply.expectedContractDate);
			}
			$scope.getNoticeOfDecstionByProjectFormalID($scope.pfr.apply.projectNo);
			$scope.getProjectPreReviewYJDWByID(objId);
		});

	}
	$scope.changDate=function(values){
		var date = new Date();
		var paddNum = function(num){
			num += "";
			return num.replace(/^(\d)$/,"0$1");
		}
		var nowDate=date.getFullYear()+"-"+paddNum(date.getMonth()+1)+"-"+paddNum(date.getDate());
		var d=DateDiff(values,nowDate);
		$("#shouwdate").text("距离签约时间还差："+d+"天");
	}

	$scope.getSelectTypeByCode=function(typeCode){
		var  url = 'common/commonMethod/selectDataDictionByCode';
		$scope.httpData(url,typeCode).success(function(data){
			if(data.result_code === 'S'){
				$scope.optionTypeList=data.result_data;
			}else{
				alert(data.result_name);
			}
		});
	}
	$scope.getSelectTypeByCode2=function(typeCode){
		var  url = 'common/commonMethod/selectDataDictionByCode';
		$scope.httpData(url,typeCode).success(function(data){
			if(data.result_code === 'S'){
				$scope.optionTypeList2=data.result_data;
			}else{
				alert(data.result_name);
			}
		});
	}
	/*查询固定成员审批列表*/
	$scope.getProjectPreReviewGDCYByID=function(id){
		var  url = 'rcm/ProjectInfo/getTaskOpinion';
		$scope.panam={taskDefKey:'usertask4' ,businessId:id};
		$scope.httpData(url,$scope.panam).success(function(data){
			$scope.gdcy  = data.result_data;

		});
	}
	/*查询一级审批列表*/
	$scope.getProjectPreReviewYJDWByID=function(id){
		var  url = 'rcm/ProjectInfo/getTaskOpinionTwo';
		$scope.panam={taskDefKey:'usertask16' ,businessId:id};
		$scope.httpData(url,$scope.panam).success(function(data){
			if(data.result_code === 'S') {
				var yijd = data.result_data;
				if (null != yijd) {
					$scope.yjdw = yijd.DEPTNAME+" _ "+yijd.USERNAME+"："+yijd.OPINION;
					if(null!=$scope.pfr.cesuanFileOpinion && ''!=$scope.pfr.cesuanFileOpinion){
						$scope.yjdw = $scope.yjdw+" ；<br/>项目投资测算意见："+$scope.pfr.cesuanFileOpinion;
					}
					/*if(null!=$scope.pfr.tzProtocolOpinion && ''!=$scope.pfr.tzProtocolOpinion){
						$scope.yjdw = $scope.yjdw+" ；<br/>项目投资协议意见：" + $scope.pfr.tzProtocolOpinion;
					}*/
					$scope.yjdw=Utils.encodeTextAreaString($scope.yjdw);
				}else{
					$scope.yjdw=null;
				}
				
				var tzcs = data.result_data;
				if (null != tzcs) {
					if(null!=$scope.pfr.tzProtocolOpinion && ''!=$scope.pfr.tzProtocolOpinion){
						$scope.xyyj ="项目投资协议意见：" + $scope.pfr.tzProtocolOpinion;
					}
					$scope.xyyj=Utils.encodeTextAreaString($scope.xyyj);
				}else{
					$scope.xyyj=null;
				}
				
			}
		});
	}
	var Utils = {};
// textArea换行符转<br/>
	Utils.encodeTextAreaString= function(str) {
		var reg = new RegExp("<br/>", "g");
		str = str.replace(reg, "\n");
		return str;
	}
	//根据id查询决策通知书决策意见
	$scope.getNoticeOfDecstionByProjectFormalID=function(pid){
		var url="formalAssessment/NoticeOfDecision/getNoticeOfDecstionByProjectFormalID";
		$scope.httpData(url,pid).success(function(data){
			if(data.result_code === 'S'){
				if(undefined!=data.result_data) {
					$scope.noticofDec=data.result_data;
					var c = $scope.noticofDec.consentToInvestment;
					if (c == "1") {
						$scope.consentToInvestment = "同意投资";
					} else if (c == "2") {
						$scope.consentToInvestment = "不同意投资";
					} else if (c == 3) {
						$scope.consentToInvestment = "同意有条件投资";
					}
					$scope.implementationMatters = $scope.noticofDec.implementationMatters;
					$scope.dateOfMeeting = $scope.noticofDec.dateOfMeeting;
				}else{
					$scope.consentToInvestment=null;
					$scope.implementationMatters=null;
				}
			}else{
				alert(data.result_name);
			}
		});
	}
	$scope.getProjectFormalReviewByID(objId);



	$scope.getProjectPreReviewGDCYByID(objId);

	angular.element(document).ready(function() {
		$scope.getSelectTypeByCode("06");
		$scope.getSelectTypeByCode2("09");
	});

//	if($routeParams.action=='view2'){
		//需要给相关人发通知
		$scope.findRelationUser(objId);
//	}

	//提交流程
	$scope.showSubmitModal = function(type){
		if($routeParams.action=='View'){
			var companyHeader = $scope.pfr.apply.companyHeader;
			$scope.approve = [{
				submitInfo:{
					startVar:{processKey:'formalAssessment',businessId:$scope.pfr._id,subject:$scope.pfr.apply.projectName+'正式评审申请',inputUser:$scope.credentials.UUID},
					runtimeVar:{inputUser:companyHeader.value},
					currentTaskVar:{opinion:'请审批'}
				},
				showInfo:{destination:'单位负责人审核',approver: companyHeader.name}
			}];
			if($routeParams.action =='View' && params[1]){
				$scope.approve[0].submitInfo.taskId=params[3];
			}
		}else if($routeParams.action=='view1'){
			var approveUser =  $scope.pfr.apply.investmentPerson;
			if(approveUser == null || typeof(approveUser)=='undefined' || approveUser.name==null||approveUser.name==""){
				approveUser = $scope.pfr.apply.directPerson;
			}
			var investmentManager = $scope.pfr.apply.investmentManager;//投资经理
			var nextNode;
			if(approveUser==null||approveUser.name==null||approveUser==""||approveUser.name==""){
				var url=srvUrl+"common/commonMethod/getRoleuserByCode";
		        $.ajax({
		        	url: url,
		        	type: "POST",
		        	data:"5",
		        	dataType: "json",
		        	async: false,
		        	success: function(data){
	    				if(data.result_code == 'S'){
	    					var ma = data.result_data[0];
	    					var investmentManager = $scope.pfr.apply.investmentManager;//投资经理
	    					if(ma){
	    						nextNode = {
	    							submitInfo:{
	    								taskId:params[3],
	    								businessId:params[0],
	    								runtimeVar:{inputUser:ma.VALUE,toTask:'1',isSkipServiceType:'1'},
	    								currentTaskVar:{opinion:'请审批'},
	    								noticeInfo:{
	    									infoSubject:$scope.pfr.apply.projectName+'正式评审申请进入风控审批阶段，请悉知！',
	    									businessId:params[0],
	    									remark:'',
	    									formKey:'/ProjectFormalReviewDetailView/View/'+params[0]+'@'+params[1]+'@'+params[2],
	    									createBy:$scope.credentials.UUID,
	    									reader:$scope.removeObjByValue(removeDuplicate($scope.relationUsers),ma.VALUE),
	    									type:'1',
	    									custText01:'正式评审'
	    								}
	    							},
	    							showInfo:{destination:'分配评审任务',approver: ma.NAME}
	    						};
	    					}else{
								$.alert("评审分配人不存在，请先设置评审分配人！");
							}
	    				}
		        	}
		        });
			}else{
				nextNode = {
					submitInfo:{
						taskId:params[3],
						businessId:params[0],
						runtimeVar:{inputUser:approveUser.value,toTask:'1',isSkipServiceType:'0'},
						currentTaskVar:{opinion:'请审批'}
					},
					showInfo:{destination:'投资中心/水环境投资中心审核',approver: approveUser.name}
				};
			}
			var preNode = {
				submitInfo:{
					taskId:params[3],
					businessId:params[0],
					runtimeVar:{inputUser:investmentManager.value,toTask:'-1'},
					currentTaskVar:{opinion:'请修改'}
				},
				showInfo:{destination:'退回起草人',approver:investmentManager.name}
			};
			$scope.approve=[nextNode, preNode];
		}else if($routeParams.action=='view2'){
			var url=srvUrl+"common/commonMethod/getRoleuserByCode";
	        $.ajax({
	        	url: url,
	        	type: "POST",
	        	data:"5",
	        	dataType: "json",
	        	async: false,
	        	success: function(data){
    				if(data.result_code == 'S'){
    					var ma = data.result_data[0];
    					var investmentManager = $scope.pfr.apply.investmentManager;
    					if(ma){
    						var currentTaskVar = {opinion:'请审批'};
    						if(true){
    							$("#cesuanFileOpinionDiv").show();
    							$("#tzProtocolOpinionDiv").show();
    							currentTaskVar.cesuanFileOpinion='';
    							currentTaskVar.tzProtocolOpinion='';
    						}
    						$scope.approve = [{
    							submitInfo:{
    								taskId:params[3],
    								businessId:params[0],
    								runtimeVar:{inputUser:ma.VALUE,toTask:'1'},
    								currentTaskVar:currentTaskVar,
    								noticeInfo:{
    									infoSubject:$scope.pfr.apply.projectName+'正式评审申请进入风控审批阶段，请悉知！',
    									businessId:params[0],
    									remark:'',
    									formKey:'/ProjectFormalReviewDetailView/View/'+params[0]+'@'+params[1]+'@'+params[2],
    									createBy:$scope.credentials.UUID,
    									reader:$scope.removeObjByValue(removeDuplicate($scope.relationUsers),ma.VALUE),
    									type:'1',
    									custText01:'正式评审'
    								}
    							},
    							showInfo:{destination:'分配评审任务',approver: ma.NAME}
    						},{
    							submitInfo:{
    								taskId:params[3],
    								businessId:params[0],
    								runtimeVar:{inputUser:investmentManager.value,toTask:'-1'},
    								currentTaskVar:{opinion:'请修改'}
    							},
    							showInfo:{destination:'退回起草人',approver:investmentManager.name}
    						}];
    					}else{
							$.alert("评审分配人不存在，请先设置评审分配人！");
						}
    				}
	        	}
	        });
		}
		$scope.approve[0].redirectUrl="ProjectFormalReviewList";
		$('#submitModal').modal('show');
	}
	if(typeof (params[1])=='string' && typeof (params[2])=='string' && params[1]!='' && params[2]!=''){//已经启动流程
		$scope.wfInfo = {processDefinitionId:params[1],processInstanceId:params[2]};
	}else{//未启动流程
		$scope.wfInfo = {processKey:'formalAssessment'};
	}
}]);

ctmApp.register.controller('ProjectFormalReviewView', ['$http','$scope','$location','$routeParams','Upload','$timeout', function ($http,$scope,$location,$routeParams,Upload,$timeout) {
	//初始化
	$scope.comId= $routeParams.id;
	var complexId = $routeParams.id;
	var params = complexId.split("@");
	if(null!=params[4] && ""!=params[4]){
		$scope.flag=params[4];
	}
	var objId = params[0];
	$scope.pfr={};
	$scope.pfr.apply = {};
	$scope.pfr.taskallocation={};
	$scope.pfr.approveAttachment = {};
	$scope.pfr.pfrBusinessUnitCommit=[];
	$scope.pfr._id=objId;
	$scope.dic=[];

	$scope.getProjectFormalReviewByID=function(id){
		var  url = 'formalAssessment/ProjectFormalReview/getProjectFormalReviewByID';
		$scope.httpData(url,id).success(function(data){
			$scope.pfr  = data.result_data;
			var ptNameArr=[],pmNameArr=[],pthNameArr=[];
			/*var pth=$scope.pfr.apply.companyHeader;
			if(null!=pth && pth.length>0){
				for(var i=0;i<pth.length;i++){
					pthNameArr.push(pth[i].name);
				}
				$scope.pfr.apply.companyHeaderName=pthNameArr.join(",");
			}*/
			var pt1NameArr=[];
			var pt1=$scope.pfr.apply.serviceType;
			if(null!=pt1 && pt1.length>0){
				for(var i=0;i<pt1.length;i++){
					pt1NameArr.push(pt1[i].VALUE);
				}
				$scope.pfr.apply.serviceType=pt1NameArr.join(",");
			}
			var pt=$scope.pfr.apply.projectType;
			if(null!=pt && pt.length>0){
				for(var i=0;i<pt.length;i++){
					ptNameArr.push(pt[i].VALUE);
				}
				$scope.pfr.apply.projectType=ptNameArr.join(",");
			}
			var pm=$scope.pfr.apply.projectModel;
			if(null!=pm && pm.length>0){
				for(var j=0;j<pm.length;j++){
					pmNameArr.push(pm[j].VALUE);
				}
				$scope.pfr.apply.projectModel=pmNameArr.join(",");
			}
			$scope.getProjectPreReviewYJDWByID(objId);
		});

	}
	$scope.getProjectFormalReviewByID(objId);
	/*查询一级审批列表*/
	$scope.getProjectPreReviewYJDWByID=function(id){
		var  url = 'rcm/ProjectInfo/getTaskOpinionTwo';
		$scope.panam={taskDefKey:'usertask16' ,businessId:id};
		$scope.httpData(url,$scope.panam).success(function(data){
			if(data.result_code === 'S') {
				var yijd = data.result_data;
				if (null != yijd) {
					$scope.yjdw = yijd.DEPTNAME+" _ "+yijd.USERNAME+"："+yijd.OPINION;
					if(null!=$scope.pfr.cesuanFileOpinion && ''!=$scope.pfr.cesuanFileOpinion){
						$scope.yjdw = $scope.yjdw+" ；<br/>项目投资测算意见："+$scope.pfr.cesuanFileOpinion;
					}
					/*if(null!=$scope.pfr.tzProtocolOpinion && ''!=$scope.pfr.tzProtocolOpinion){
						$scope.yjdw = $scope.yjdw+" ；<br/>项目投资协议意见：" + $scope.pfr.tzProtocolOpinion;
					}*/
					$scope.yjdw=Utils.encodeTextAreaString($scope.yjdw);
				}else{
					$scope.yjdw=null;
				}
				
				var tzcs = data.result_data;
				if (null != tzcs) {
					if(null!=$scope.pfr.tzProtocolOpinion && ''!=$scope.pfr.tzProtocolOpinion){
						$scope.xyyj ="项目投资协议意见：" + $scope.pfr.tzProtocolOpinion;
					}
					$scope.xyyj=Utils.encodeTextAreaString($scope.xyyj);
				}else{
					$scope.xyyj=null;
				}
				
			}
		});
	}
	var Utils = {};
// textArea换行符转<br/>
	Utils.encodeTextAreaString= function(str) {
		var reg = new RegExp("<br/>", "g");
		str = str.replace(reg, "\n");
		return str;
	}

	//判断是否存在正式评审报告
	$scope.formalReport = {};
	$scope.httpData('rcm/ProjectInfo/selectByBusinessId',objId).success(function(data){
		if(data.result_code == 'S'){
			var result = data.result_data;
			if(result && result.REPORT_ID){
				$scope.formalReport = {id:result.REPORT_ID, key:result.CONTROLLER_VAL};
				$scope.hasReportAllready = true;
			}else{
				$scope.hasReportAllready = false;
			}
		}
	});

	//+"@"+params[1]+"@"+params[2]+"@"+params[3]
	//编辑评审报告
	$scope.editFormalReport = function(){
		$location.path($scope.formalReport.key+'/Update/'+$scope.formalReport.id+"@1@"+complexId);
	}
	//创建评审报告
	$scope.createFormalReport = function(){
		$location.path("FormalReportList");
	}
	$scope.wfInfo = {processDefinitionId:params[1],processInstanceId:params[2]};
}]);
/**
 * Created by gl on 2016/8/14.
 */
ctmApp.register.controller('ProjectPreReviewDetail', ['$http','$scope','$location','$routeParams','Upload','$timeout', function ($http,$scope,$location,$routeParams,Upload,$timeout) {
    //初始化页面数据字典
	$scope.global_projectTypes=new Array();
    $scope.dic={};
    $scope.pre = {};
    $scope.pre.apply = {};
    $scope.pre.taskallocation={};
    $scope.pre.taskallocation.reviewLeader=null;
    $scope.pre.taskallocation.fixedGroup=null;
    $scope.columnName="";
    $scope.columnsName="";
    $scope.columnsNum="";
    $scope.isFinished=false;
    $scope.flag=false;
    $scope.dicSyn=[];
    $scope.investmentPerson=null;
    $scope.directPerson=null;
    if($routeParams.action!='Create'){
        $scope.isHide = true; //use to hide button "导出申请单"
    }else{
        $scope.isHide = false;
    }
    $scope.generateApplication=function(){
        startLoading();
        var url = 'projectPreReview/ProjectPreReview/getPreApplication';
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

    function FormatDate() {
        var date = new Date();
        var paddNum = function(num){
            num += "";
            return num.replace(/^(\d)$/,"0$1");
        }
        return date.getFullYear()+""+paddNum(date.getMonth()+1);
    }
    if($routeParams.action=='Create'){
        $scope.pre.apply.investmentManager = {name:$scope.credentials.userName,value:$scope.credentials.UUID};
    }
   // $scope.projectTypeValue=[];
    $scope.setDirectiveParam=function(columnName){
        $scope.columnName=columnName;
    }
    $scope.setDirectiveParamTwo=function(columnName,num){
        $scope.columnsName=columnName;
        $scope.columnsNum=num;
    }
    $scope.getProjectPreReviewByID=function(id){
        var  url = 'projectPreReview/ProjectPreReview/getProjectPreReviewByID';
        $scope.httpData(url,id).success(function(data){
            var ptNameArr=[],ptValueArr=[],pmNameArr=[],pmValueArr=[],haderNameArr=[],haderValueArr=[];
            $scope.pre  = data.result_data;
            if(null!=$scope.pre.apply.companyHeader){
                /*var header=$scope.pre.apply.companyHeader;
                if(null!=header && header.length>0){
                    for(var i=0;i<header.length;i++){
                        haderNameArr.push(header[i].name);
                        haderValueArr.push(header[i].value);
                    }
                    commonModelValue("companyHeader",haderValueArr,haderNameArr);
                    $scope.pre.apply.companyHeaderName=haderNameArr.join(",");
                }*/
                commonModelOneValue("companyHeader",$scope.pre.apply.companyHeader.value,$scope.pre.apply.companyHeader.name);
            }
            if(null!=$scope.pre.apply.investmentManager){
                commonModelOneValue("investmentManager",$scope.pre.apply.investmentManager.value,$scope.pre.apply.investmentManager.name);
            }
            if(null!=$scope.pre.apply.grassrootsLegalStaff){
                if(null!=$scope.pre.apply.grassrootsLegalStaff.value) {
                    commonModelOneValue("grassrootsLegalStaff", $scope.pre.apply.grassrootsLegalStaff.value, $scope.pre.apply.grassrootsLegalStaff.name);
                }
            }
            if(null!=$scope.pre.apply.investmentPerson){
                commonModelOneValue("investmentPerson",$scope.pre.apply.investmentPerson.value,$scope.pre.apply.investmentPerson.name);
            }
            if(null!=$scope.pre.apply.directPerson){
                commonModelOneValue("directPerson",$scope.pre.apply.directPerson.value,$scope.pre.apply.directPerson.name);
            }
            if(null!=$scope.pre.apply.pertainArea){
                commonModelOneValue("pertainArea",$scope.pre.apply.pertainArea.VALUE,$scope.pre.apply.pertainArea.KEY);
            }
            var isChecked=$scope.pre.apply.investmentModel;
            if(isChecked){
                $scope.getprojectmodel('1');
            }else{
                $scope.getprojectmodel('2');
            }
            var pt1NameArr=[],pt1ValueArr=[];
            var pt1=$scope.pre.apply.serviceType;
            if(null!=pt1 && pt1.length>0){
                for(var i=0;i<pt1.length;i++){
                    pt1NameArr.push(pt1[i].VALUE);
                    pt1ValueArr.push(pt1[i].KEY);
                }
                commonModelValue2("oneservicetypebox",pt1ValueArr,pt1NameArr);
            }
            var pt=$scope.pre.apply.projectType;
            if(null!=pt && pt.length>0){
                for(var i=0;i<pt.length;i++){
                    ptNameArr.push(pt[i].VALUE);
                    ptValueArr.push(pt[i].KEY);
                }
                commonModelValue2("twoservicetypebox",ptValueArr,ptNameArr);
            }
            var pm=$scope.pre.apply.projectModel;
            if(null!=pm && pm.length>0){
                for(var j=0;j<pm.length;j++){
                    pmNameArr.push(pm[j].VALUE);
                    pmValueArr.push(pm[j].KEY);
                }
                 commonModelValue2("projectmodebox",pmValueArr,pmNameArr);
            }
            $scope.isFinished=true;
            if(null!=$scope.pre.apply.investmentPerson){
                $scope.flag=true;
                $scope.investmentPerson=$scope.pre.apply.investmentPerson;
            }
            if(null!=$scope.pre.apply.directPerson){
                $scope.flag=true;
                $scope.directPerson=$scope.pre.apply.directPerson;
            }
            if(null!=$scope.pre.apply.tenderTime) {
                $scope.changDate($scope.pre.apply.tenderTime);
            }
        });
    }
    $scope.changDate=function(values) {
        var nowDate=null;
        nowDate=$scope.pre.create_date;
        if(null==nowDate || ""==nowDate){
            var date = new Date();
            var paddNum = function (num) {
                num += "";
                return num.replace(/^(\d)$/, "0$1");
            }
            nowDate = date.getFullYear() + "-" + paddNum(date.getMonth() + 1) + "-" + paddNum(date.getDate());
        }else{
            nowDate=nowDate.substr(0,10);
        }
        var d=DateDiff(values,nowDate);
        if(d<=5){
            $("#showdate").text("申请距离投标时间："+d+"天").attr("style","color:#ff0000;font-weight:bold");
        }else{
            $("#showdate").text("申请距离投标时间："+d+"天").removeAttr("style","color:#ff0000");
        }
    }
    $scope.getSelectGroupUserUnitByAccount=function(account){
        var url="fnd/Group/getGroupUserReportingUnit";
        $scope.httpData(url,account).success(function(data){
            if(data.result_code === 'S'){
                if(undefined!=data.result_data) {
                    $scope.pre.apply.reportingUnit = {
                        name: data.result_data.REPORTINGUNIT_NAME,
                        value: data.result_data.REPORTINGUNIT_ID
                    };
                    $scope.pre.apply.pertainArea = {
                    	KEY:data.result_data.orgpkValue,
                    	VALUE:data.result_data.orgpkName
                    };
                    $scope.pre.apply.companyHeader = {
                        name: data.result_data.COMPANYHEADER_NAME,
                        value: data.result_data.COMPANYHEADER_ID
                    };
                    if (null != data.result_data.COMPANYHEADER_ID && "" != data.result_data.COMPANYHEADER_ID) {
                        commonModelOneValue("companyHeader", data.result_data.COMPANYHEADER_ID, data.result_data.COMPANYHEADER_NAME);
                        $("#companyHeaderName").val(data.result_data.COMPANYHEADER_NAME);
                        $("label[for='companyHeaderName']").remove();
                    }
                    $scope.pre.apply.grassrootsLegalStaff = {
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
                    //$location.path("/ProjectPreReview");
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
    //定义窗口action
    var action =$routeParams.action;
    if (action == 'Update') {
        $scope.getProjectPreReviewByID(objId);
        $scope.titleName = "项目投标评审申请修改";
    } else if (action == 'View') {
        $scope.getProjectPreReviewByID(objId);
        /* $('#savebtn').hide();
         $('#cancelbtn').hide();
         $('#content-wrapper input').attr("disabled",true);
         $('button').attr("disabled",true);
         $('select').attr("disabled",true);
         $(".select2-search-choice-close").attr("onclick", "");*/
        $scope.titleName = "项目投标评审申请查看";
    } else if (action == 'Create') {
        $scope.pre.istz="0";
        $scope.getSelectGroupUserUnitByAccount($scope.credentials.userID);
        $scope.isFinished=true;
        $scope.pre.create_by=$scope.credentials.UUID;
        $scope.pre.create_name=$scope.credentials.userName;
        $scope.titleName = "项目投标评审申请新增";
    }

    $scope.setDirectiveCompanyList=function(code,name){
        $scope.pre.apply.projectNo=code;
        $scope.pre.apply.projectName=name;
        $("#projectName").val(name);
        $("label[for='projectName']").remove();
    }
    var ztree, setting = {
        callback:{
            onClick:function(event, treeId, treeNode){
                accessScope("#reportingUnitName", function(scope){
                    scope.pre.apply.reportingUnit = {"name":treeNode.name,value:treeNode.id};
                    $("#reportingUnitName").val(name);
                    $("label[for='reportingUnitName']").remove();
                });
            },
            beforeExpand:function(treeId, treeNode){
                if(typeof(treeNode.children)=='undefined'){
                    $scope.addTreeNode(treeNode);
                }
            }
        }
    };
    $scope.addTreeNode = function (parentNode){
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
                ztree.addNodes(null, nodeArray);
                var rootNode = ztree.getNodes()[0];
                $scope.addTreeNode(rootNode);
                rootNode.open = true;
                ztree.refresh();
            }else{
                ztree.addNodes(parentNode, nodeArray, true);
            }
        });
    }
    angular.element(document).ready(function() {
        ztree = $.fn.zTree.init($("#treeID1"), setting);
        $scope.addTreeNode('');
    });

    $scope.save= function (callBack) {
        if (callBack && typeof callBack == 'function') {
        	if ($("#myFormPfR").valid()) {
                var postObj;
                var url = "";
                if (typeof ($scope.pre._id) != "undefined") {
                    url = 'projectPreReview/ProjectPreReview/updateProjectPreReview';
                } else {
                    url = 'projectPreReview/ProjectPreReview/create';
                }
                var projectType = $scope.pre.apply.projectType;
                var arr = new Array();
                for (var i = 0; i < projectType.length; i++) {
                    var obj = new Object();
                    obj.KEY = projectType[i].KEY.substring(projectType[i].KEY.indexOf("_") + 1)
                    obj.VALUE = projectType[i].VALUE;
                    obj.PARENT_ID = projectType[i].PARENT_ID;
                    arr.push(obj);
                }
                $scope.pre.apply.projectType = arr;
                postObj = $scope.httpData(url, $scope.pre);
                postObj.success(function (data) {
                    if (data.result_code === 'S') {
                        $scope.pre._id = data.result_data;
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
            var url = "";
            if (typeof ($scope.pre._id) != "undefined") {
                url = 'projectPreReview/ProjectPreReview/updateProjectPreReview';
            } else {
                url = 'projectPreReview/ProjectPreReview/create';
            }
            var projectName=$scope.pre.apply.projectName;
            if(null==projectName || ""==projectName){
                $.alert("项目名称必填！");
                return false;
            }
            var projectType = $scope.pre.apply.projectType;
            var arr = new Array();
            for (var i = 0; i < projectType.length; i++) {
                var obj = new Object();
                obj.KEY = projectType[i].KEY.substring(projectType[i].KEY.indexOf("_") + 1)
                obj.VALUE = projectType[i].VALUE;
                obj.PARENT_ID = projectType[i].PARENT_ID;
                arr.push(obj);
            }
            $scope.pre.apply.projectType = arr;
            postObj = $scope.httpData(url, $scope.pre);
            postObj.success(function (data) {
                if (data.result_code === 'S') {
                    $scope.pre._id = data.result_data;
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
            $("#header"+paramsVal+"Name").find(".select2-search-field").before(leftstr+arrName[i]+centerstr+paramsVal+"','"+arrID[i]+addID+arrID[i]+rightstr);
        }
    }
    var commonModelOneValue=function(paramsVal,arrID,arrName){
        $("#header"+paramsVal+"Name").find(".select2-choices").html("<li class=\"select2-search-field\"><input id=\"s2id_autogen2\" class=\"select2-input\" type=\"text\" spellcheck=\"false\" autocapitalize=\"off\" autocorrect=\"off\" autocomplete=\"off\" style=\"width: 16px;\"> </li>");
        var leftstr="<li class=\"select2-search-choice\"><div>";
        var centerstr="</div><a class=\"select2-search-choice-close\" tabindex=\"-1\" onclick=\"delObj(this,'";
        var addID="');\"  ></a><div class=\"full-drop\"><input type=\"hidden\" id=\"\"  value=\"";
        var rightstr="\"></div></li>";
        $("#header"+paramsVal+"Name").find(".select2-search-field").before(leftstr+arrName+centerstr+paramsVal+"','"+arrID+addID+arrID+rightstr);
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
            $scope.pre.apply.companyHeader =arrUserNamesValue;  //赋值保存到数据库
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
            if(undefined==$scope.pre.attachment[paramsNum].files){
                $scope.pre.attachment[paramsNum].files=[];
            }
            if(undefined==$scope.pre.attachment[paramsNum].files[0]){
                $scope.pre.attachment[paramsNum].files[0]={programmed:{}};
            }
            if(undefined==$scope.pre.attachment[paramsNum].files[0].programmed){
                $scope.pre.attachment[paramsNum].files[0].programmed={};
            }
            $scope.pre.attachment[paramsNum].files[0].programmed={name:name,value:value};
        }else if(paramsVal=="approved"){
            if(undefined==$scope.pre.attachment[paramsNum].files){
                $scope.pre.attachment[paramsNum].files=[];
            }
            if(undefined==$scope.pre.attachment[paramsNum].files[0]){
                $scope.pre.attachment[paramsNum].files[0]={approved:{}};
            }
            if(undefined==$scope.pre.attachment[paramsNum].files[0].approved){
                $scope.pre.attachment[paramsNum].files[0].approved={};
            }
            $scope.pre.attachment[paramsNum].files[0].approved={name:name,value:value};
        }else if(paramsVal=="companyHeader"){
            $scope.pre.apply.companyHeader ={name:name,value:value};//赋值保存到数据库
            $("#companyHeaderName").val(name);
            $("label[for='companyHeaderName']").remove();
            commonModelOneValue(paramsVal,value,name);
        }else if(paramsVal=="investmentManager"){
            $scope.pre.apply.investmentManager ={name:name,value:value};//赋值保存到数据库
            $("#investmentManagerName").val(name);
            $("label[for='investmentManagerName']").remove();
            commonModelOneValue(paramsVal,value,name);
        }else if(paramsVal=="grassrootsLegalStaff"){
            $scope.pre.apply.grassrootsLegalStaff ={name:name,value:value};//赋值保存到数据库
            $("#grassrootsLegalStaffName").val(name);
            $("label[for='grassrootsLegalStaffName']").remove();
            commonModelOneValue(paramsVal,value,name);
        }else if(paramsVal=="investmentPerson"){
            $scope.pre.apply.investmentPerson ={name:name,value:value};//赋值保存到数据库
            $("#investmentPersonName").val(name);
            $("label[for='investmentPersonName']").remove();
            commonModelOneValue(paramsVal,value,name);
        }else if(paramsVal=="directPerson"){
            $scope.pre.apply.directPerson ={name:name,value:value};//赋值保存到数据库
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
              //  $scope.dic.projectTypeValue=data.result_data.projectType;
                $scope.dic.projectModelValue=data.result_data.projectModel;
                $scope.dic.companyCategorys=data.result_data.companyCategorys;
            }else{
                alert(data.result_name);
            }
        });
    }

    $scope.getSyncbusinessmodel=function(keys){
    	//初始化一级业务类型下拉列表数据
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

    $scope.$watch("pre.apply.serviceType", function(){
        	var projectTypeArr=[];
            var serviceTypeKeyArr=[];
            var serviceTypes = $scope.pre.apply.serviceType;
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
            var projectTypes = $scope.pre.apply.projectType;
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

            $scope.httpData("fnd/Group/findDirectUserReportingUnitList",{TYPE:serviceTypeKeyArr.join(","),REPORTINGUNIT_ID:$scope.pre.apply.reportingUnit.value}).success(function(data){
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
                        if (investmentperson.length == 1) {
                            $scope.pre.apply.investmentPerson = investmentperson[0];
                            $("#investmentPersonName").val(investmentperson[0]);
                            $("label[for='investmentPersonName']").remove();
                        } else {
                            $scope.pre.apply.investmentPerson = null;
                            $("#investmentPersonName").val("");
                        }
                        if (directperson.length == 1) {
                            $scope.pre.apply.directPerson = directperson[0];
                            $("#directPersonName").val(directperson[0]);
                            $("label[for='directPersonName']").remove();
                        } else {
                            $scope.pre.apply.directPerson = null;
                            $("#directPersonName").val("");
                        }
                        if(null!=$scope.investmentPerson) {
                            $scope.pre.apply.investmentPerson = $scope.investmentPerson;
                            for (var i in $scope.investmentPersonList) {
                                if ($scope.investmentPersonList[i] == $scope.pre.apply.investmentPerson) {//将d是4的城市设为选中项.
                                    $scope.pre.apply.investmentPerson = $scope.investmentPersonList[i];
                                    break;
                                }
                            }
                            $scope.investmentPerson=null;
                        }
                        if(null!=$scope.directPerson) {
                            $scope.pre.apply.directPerson = $scope.directPerson;
                            for (var i in $scope.directpersonList) {
                                if ($scope.directpersonList[i] == $scope.pre.apply.directPerson) {//将d是4的城市设为选中项.
                                    $scope.pre.apply.directPerson = $scope.directpersonList[i];
                                    break;
                                }
                            }
                            $scope.directPerson=null;
                        }
                    } else {
                        $scope.investmentPersonList = null;
                        $scope.directpersonList = null;
                        $scope.pre.apply.investmentPerson =null;
                        $("#investmentPersonName").val("");
                        $scope.pre.apply.directPerson =null;
                        $("#directPersonName").val("");
                    }
                }
            });
        }else{
            $scope.investmentPersonList = null;
            $scope.directpersonList = null;
            $scope.pre.apply.investmentPerson =null;
            $("#investmentPersonName").val("");
            $scope.pre.apply.directPerson =null;
            $("#directPersonName").val("");
        }
            $scope.pre.apply.projectType=result;
            $("#s2id_twoservicetypeboxName").find(".select2-choices .select2-search-choice").remove();
            commonModelValue2("twoservicetypebox", ids, names);
    }, true);
    $scope.changeProjectType=function(value){
        $("#s2id_twoservicetypeboxName").find(".select2-choices .select2-search-choice").remove();
        $scope.pre.apply.projectType=null;
        var url="common/commonMethod/selectsyncbusinessmodelBykey";
        var dataV={'parentId':value.KEY};
        $scope.httpData(url,dataV).success(function(data){
            if(data.result_code === 'S'){
                $scope.projectTypeValueKEY=data.result_data;
            }else{
                alert(data.result_name);
            }
        });
    }
    $scope.getprojectmodel=function(keys){
        var url="common/commonMethod/selectsyncbusinessmodel";
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
        $scope.pre.apply.projectModel=null;
        $scope.getprojectmodel(pid);
    }

    //手动初始化，目的先显示组织树
    angular.element(document).ready(function() {
        $scope.getSelectTypeByCode('01,02,05');
        $scope.getSyncbusinessmodel('0');
        if(action=="Create"){
            $scope.getprojectmodel('2');
        }
    });



   //$scope.pre.attachment = [{UUID:'386c61d5-16ec-4692-a99f-ef9c1fda8b17', ITEM_NAME:'《财务专业尽职调查意见》'}, {UUID:'2f8e4de6-bd56-4399-a935-487b208e9896', ITEM_NAME:'《投资分析报告》'}];
    //监控项目类型，修改附件列表
    $scope.$watch("pre.apply.projectModel", function(){
        var projectTypeArr=[];
        var projectTypes = $scope.pre.apply.projectModel;
        if(null!=projectTypes && projectTypes.length>0){
            for(var i=0;i<projectTypes.length;i++){
                projectTypeArr.push(projectTypes[i].KEY);
            }
        }
        if( projectTypeArr.length>0 && $scope.isFinished){
            var dictype=$scope.pre.apply.investmentModel;
            if(dictype){
                dictype="1";
            }else{
                dictype="2";
            }
            $scope.httpData("projectPreReview/ProjectPreReview/findAttachmentList",{projectTypes:projectTypeArr.join(","),dicTypes:dictype}).success(function(data){
                if(data.result_code == "S"){
                    var newArray = data.result_data, oldArray = $scope.pre.attachment;
                    mergeArray(oldArray, newArray,'ITEM_NAME');
                    $scope.pre.attachment = newArray;
                }
            });
        }else{
            $scope.pre.attachment = [];
        }
    });
    //附件上传
    //$scope.pre.attachment=[];
    $scope.errorAttach=[];
    $scope.upload = function (file,errorFile, idx) {
        if(errorFile && errorFile.length>0){
        	var errorMsg = fileErrorMsg(errorFile);
        	$scope.errorAttach[idx]={msg:errorMsg};
        }else if(file){
            var fileFolder = "preAssessment/";
            if($routeParams.action=='Create') {
                if(undefined==$scope.pre.apply.projectNo){
                    $.alert("请先选择项目然后上传附件");
                    return false;
                }
                var no=$scope.pre.apply.projectNo;
                fileFolder=fileFolder+FormatDate()+"/"+no;
            }else{
                var dates=$scope.pre.create_date;
                var no=$scope.pre.apply.projectNo;
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
               if(undefined!=null!=$scope.pre.attachment[idx].files && null!=$scope.pre.attachment[idx].files){
                   if(undefined!=null!=$scope.pre.attachment[idx].files[0] && null!=$scope.pre.attachment[idx].files[0]) {
                       if (null != $scope.pre.attachment[idx].files[0].programmed) {
                           retData.programmed = $scope.pre.attachment[idx].files[0].programmed;
                       }
                       if(null!=$scope.pre.attachment[idx].files[0].approved){
                           retData.approved=$scope.pre.attachment[idx].files[0].approved;
                       }
                   }
               }
                $scope.pre.attachment[idx].files=[retData];
            }, function (resp) {
                console.log('Error status: ' + resp.status);
            }, function (evt) {
                var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                $scope["progress"+idx]=progressPercentage == 100 ? "":progressPercentage+"%";
            });
        }
    };

    $scope.downLoadFile = function(df){
        window.location.href = srvUrl+"common/RcmFile/downLoad?filePath="+df.filePath+"&fileName="+encodeURI(df.fileName);
    }

    //弹出审批框
    $scope.showSubmitModal = function(){
        $scope.save(function(){
            var companyHeader = $scope.pre.apply.companyHeader;
            $scope.approve = [{
                submitInfo:{
                    startVar:{processKey:'preAssessment',businessId:$scope.pre._id,subject:$scope.pre.apply.projectName+'预评审申请',inputUser:$scope.credentials.UUID},
                    runtimeVar:{inputUser:companyHeader.value},
                    currentTaskVar:{opinion:'请审批'}
                },
                showInfo:{destination:'单位负责人审核',approver: companyHeader.name}
            }];
            if($routeParams.action =='Update' && params[1]){
                $scope.approve[0].submitInfo.taskId=params[3];
            }
            $('#submitModal').modal('show');
        });
    };
    if(typeof (params[1])=='string' && typeof (params[2])=='string' && params[1]!='' && params[2]!=''){//已经启动流程
        $scope.wfInfo = {processDefinitionId:params[1],processInstanceId:params[2]};
    }else{//未启动流程
        $scope.wfInfo = {processKey:'preAssessment'};
    }



}]);

function delObj(o,paramsVal,id){
    $(o).parent().remove();
    accessScope("#"+paramsVal+"Name", function(scope){
        if(paramsVal=="companyHeader"){
            scope.pre.apply.companyHeader={name:'',value:''};
            $("#companyHeaderName").val("");
        }else if(paramsVal=="investmentManager"){
            scope.pre.apply.investmentManager ={name:'',value:''};
            $("#investmentManagerName").val("");
        }else if(paramsVal=="grassrootsLegalStaff"){
            scope.pre.apply.grassrootsLegalStaff ={name:'',value:''};//赋值保存到数据库
            $("#grassrootsLegalStaffName").val("");
        }else if(paramsVal=="investmentPerson"){
            scope.pre.apply.investmentPerson ={name:'',value:''};//赋值保存到数据库
            $("#investmentPersonName").val("");
        }else if(paramsVal=="directPerson"){
            scope.pre.apply.directPerson ={name:'',value:''};//赋值保存到数据库
            $("#directPersonName").val("");
        }
    });
}
function delSelect(o,paramsVal,name,id){
    $(o).parent().remove();
    accessScope("#"+paramsVal+"Name", function(scope){
        if(paramsVal=="oneservicetypebox"){
            var names=scope.pre.apply.serviceType;
            var newNames = $(names).map(function(){
            	if(this.KEY!=id){
            		return this;
            	}
            }).get();
            if(newNames.length>0){
            	scope.pre.apply.serviceType=newNames;
            }else{
                scope.pre.apply.serviceType=null;
            }
        }else if(paramsVal=="twoservicetypebox"){
            var names=scope.pre.apply.projectType;
            var newNames = $(names).map(function(){
            	if(this.KEY!=id){
            		return this;
            	}
            }).get();
            if(newNames.length>0){
                scope.pre.apply.projectType=newNames;
            }else{
                scope.pre.apply.projectType=null;
            }
        }else if(paramsVal=="projectmodebox"){
            var names=scope.pre.apply.projectModel;
            var newNames = $(names).map(function(){
            	if(this.KEY!=id){
            		return this;
            	}
            }).get();
            if(newNames.length>0){
                scope.pre.apply.projectModel=newNames;
            }else{
                scope.pre.apply.projectModel=null;
            }
        }
    });
}
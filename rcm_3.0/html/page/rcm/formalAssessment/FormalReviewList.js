ctmApp.register.controller('FormalReviewList', ['$http','$routeParams','$scope','$location', function ($http,$routeParams,$scope,$location) {
    $scope.orderby=-1;
    
    $scope.initDefaultData = function(){
    	var url = srvUrl + "dropDownBox/queryDropDownBoxByCode.do";
		$http({
			method:'post',  
		    url: url,
		    params:{"code":"14"}
		}).success(function(result){
			var data = result.result_data;
			$scope.projectType = data.typeList;
		});
    }
    
    $scope.initDefaultData();
    $scope.clearSelector = function(){
    	$scope.projectName1=null;
    	$scope.reportingUnit1=null;
    	$scope.projectSizelt=null;
    	$scope.projectSizegt=null;
    	$scope.projectType1=null;
    	$("#projectName1").val("");
    	$("#reportingUnit1").val("");
    	$("#projectSizelt").val("");
    	$("#projectSizegt").val("");
    	$("#projectType1").val("");
    }
    
    $scope.myClickMethod = function(){
    	var tabUrl = $("#myTab .active a").attr("href");
    	var tabid = tabUrl.substring(tabUrl.length-1,tabUrl.length);
    	if(tabid == 1){
    		$scope.ListMeetingAll();
    	}else if(tabid == 2){
    		$scope.ListAll();
    	}
    }
    
    $scope.tabNum=$routeParams.tabNum;
    $('#myTab li:eq('+$scope.tabNum+') a').tab('show');//展示tab页面
    $scope.queryList=function(){
        if($scope.paginationConf.currentPage === 1){
            $scope.ListAll();
        }else{
            $scope.paginationConf.currentPage = 1;
        }
    };
    //查义所有的操作
    $scope.ListAll=function(){
        $scope.conf={currentPage:$scope.paginationConf.currentPage,itemsPerPage:$scope.paginationConf.itemsPerPage,
            'user_id':$scope.credentials.UUID, 'ORDERT':$scope.orderbyt,'ORDERG':$scope.orderbyg,'ORDERJ':$scope.orderbyj,'projectName':$scope.projectName1,'projectType':$scope.projectType1,'reportingUnit':$scope.reportingUnit1,'projectSizelt':$scope.projectSizelt,'projectSizegt':$scope.projectSizegt};
        var aMethed = 'projectPreReview/Meeting/getPolicyDecisionAll';
        $scope.httpData(aMethed,$scope.conf).success(
            function (data, status, headers, config) {
                $scope.pfrReport = data.result_data.list;
                $scope.paginationConf.totalItems = data.result_data.totalItem;
                $scope.projectSizeTotal=data.result_data.map.projectSizeTotal;
                $scope.investmentAmountTotal=data.result_data.map.investmentAmountTotal;
                $scope.isYiyuan = data.result_data.map.yiyuan;
                $scope.huanweiNum=data.result_data.map.huanweiNum;
            }
        ).error(function (data, status, headers, config) {
            alert(status);
        });
    };
    $scope.order=function(o,v){
        if(o=="t"){
            $scope.orderbyt=v;
            $scope.orderbyg=null;
            $scope.orderbyj=null;
            if(v=="1"){
                $("#tasc").addClass("cur");
                $("#tdesc").removeClass("cur");
            }else{
                $("#tdesc").addClass("cur");
                $("#tasc").removeClass("cur");
            }
        }else if(o=="g"){
            $scope.orderbyg=v;
            $scope.orderbyt=null;
            $scope.orderbyj=null;
            if(v=="1"){
                $("#gasc").addClass("cur");
                $("#gdesc").removeClass("cur");
            }else{
                $("#gdesc").addClass("cur");
                $("#gasc").removeClass("cur");
            }
        }else if(o=="j"){
            $scope.orderbyj=v;
            $scope.orderbyg=null;
            $scope.orderbyt=null;
            if(v=="1"){
                $("#jasc").addClass("cur");
                $("#jdesc").removeClass("cur");
            }else{
                $("#jdesc").addClass("cur");
                $("#jasc").removeClass("cur");
            }
        }
        $scope.ListAll();
    }

    $scope.ordertwo=function(o,v){
        if(o=="t"){
            $scope.orderbyt=v;
            $scope.orderbyg=null;
            $scope.orderbyj=null;
            if(v=="1"){
                $("#ttasc").addClass("cur");
                $("#ttdesc").removeClass("cur");
            }else{
                $("#ttdesc").addClass("cur");
                $("#ttasc").removeClass("cur");
            }
        }else if(o=="g"){
            $scope.orderbyg=v;
            $scope.orderbyt=null;
            $scope.orderbyj=null;
            if(v=="1"){
                $("#tgasc").addClass("cur");
                $("#tgdesc").removeClass("cur");
            }else{
                $("#tgdesc").addClass("cur");
                $("#tgasc").removeClass("cur");
            }
        }else if(o=="j"){
            $scope.orderbyj=v;
            $scope.orderbyg=null;
            $scope.orderbyt=null;
            if(v=="1"){
                $("#tjasc").addClass("cur");
                $("#tjdesc").removeClass("cur");
            }else{
                $("#tjdesc").addClass("cur");
                $("#tjasc").removeClass("cur");
            }
        }
        $scope.ListMeetingAll();
    }
  //待决策项目
    $scope.queryTwoList=function(){
        if($scope.paginationConfes.currentPage === 1){
            $scope.ListMeetingAll();
        }else{
            $scope.paginationConfes.currentPage = 1;
        }
    };
    $scope.ListMeetingAll=function(){
        $scope.aaa={currentPage:$scope.paginationConfes.currentPage,itemsPerPage:$scope.paginationConfes.itemsPerPage,
            'user_id':$scope.credentials.UUID,'ORDERT':$scope.orderbyt,'ORDERG':$scope.orderbyg,'ORDERJ':$scope.orderbyj,'projectName':$scope.projectName1,'projectType':$scope.projectType1,'reportingUnit':$scope.reportingUnit1,'projectSizelt':$scope.projectSizelt,'projectSizegt':$scope.projectSizegt};
        var aMethed = 'projectPreReview/Meeting/getPolicyDecisionTwo';
        $scope.httpData(aMethed,$scope.aaa).success(
            function (data, status, headers, config) {
                $scope.meetingcall = data.result_data.list;
                $scope.paginationConfes.totalItems = data.result_data.totalItem;
                $scope.projectSizeTotal1=data.result_data.map.projectSizeTotal;
                $scope.investmentAmountTotal1=data.result_data.map.investmentAmountTotal;
                $scope.isYiyuan1 = data.result_data.map.yiyuan;
                $scope.huanweiNum1=data.result_data.map.huanweiNum;
            }
        ).error(function (data, status, headers, config) {
            alert(status);
        });
    };
    // 通过$watch currentPage和itemperPage 当他们一变化的时候，重新获取数据条目
    $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage',  $scope.ListAll);
    $scope.$watch('paginationConfes.currentPage + paginationConfes.itemsPerPage',  $scope.ListMeetingAll);

    $scope.import=function(){
        var aMethed =  'projectPreReview/Meeting/formalReviewListReport';
        $scope.httpData(aMethed,{}).success(
            function (data) {
                if(data.result_code=="S"){
                    var file=data.result_data.filePath;
                    var fileName=data.result_data.fileName;
                    window.location.href = srvUrl+"common/RcmFile/downLoad?filePath="+encodeURI(file)+"&fileName="+encodeURI(fileName);
                }
            }

        ).error(function (data, status, headers, config) {
            alert(status);
        });
    }

    $scope.importtwo=function(){
        var aMethed =  'projectPreReview/Meeting/formalReviewListReporttwo';
        $scope.httpData(aMethed,{}).success(
            function (data) {
                if(data.result_code=="S"){
                    var file=data.result_data.filePath;
                    var fileName=data.result_data.fileName;
                    window.location.href = srvUrl+"common/RcmFile/downLoad?filePath="+encodeURI(file)+"&fileName="+encodeURI(fileName);
                }
            }

        ).error(function (data, status, headers, config) {
            alert(status);
        });
    }
}]);


ctmApp.register.controller('FormalBiddingInfo', ['$http','$scope','$location','$routeParams','Upload',
 function ($http,$scope,$location,$routeParams,Upload) {
	
	function getDate(){
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
    //申请报告ID
    var complexId = $routeParams.id;
    var params = complexId.split("@");
    if(null!=params[1] && ""!=params[1]){
        $scope.flag=params[1];
    }
    var objId = params[0];
    $scope.formalReport={};
    $scope.formalReport.policyDecision={};
    $scope.Onemeeting={};
    function FormatDate() {
        var date = new Date();
        var paddNum = function(num){
            num += "";
            return num.replace(/^(\d)$/,"0$1");
        }
        return date.getFullYear()+"-"+paddNum(date.getMonth()+1)+"-"+date.getDate();
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
    
  //处理附件列表,为提交包装数据
    $scope.reduceAttachmentForSubmit = function(attachment){
    	var newAttachment = $scope.newAttachment;
    	
    	var now = getDate();
    	//根据uuid处理版本号，上传日期当前
    	//获取之前uuid
    	for(var j  = 0 in $scope.newPfr.attachment){
    		for(var i = 0 in newAttachment){
    			if(newAttachment[i].newFile){
    				newAttachment[i].ITEM_NAME=newAttachment[i].newItem.ITEM_NAME;
    				newAttachment[i].UUID=newAttachment[i].newItem.UUID;
    				
    				$scope.newAttachment[i].ITEM_NAME=newAttachment[i].newItem.ITEM_NAME;
    				$scope.newAttachment[i].UUID=newAttachment[i].newItem.UUID;
    				
    				
    				
	    			if(newAttachment[i].UUID==$scope.newPfr.attachment[j].UUID){
	    				//之前版本号
//	    				console.log($scope.newPfr.attachment[j].files);
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
    
    $scope.getByID=function(id){
        var  url = 'projectPreReview/Meeting/findFormalAndReport';
        $scope.httpData(url,id).success(function(data){
            $scope.Onemeeting  = data.result_data.Meeting;
            $scope.formalReport  = data.result_data.Report;
            $scope.pfr  = data.result_data.Formal;
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
            if(undefined!=$scope.Onemeeting){
                var fg=$scope.Onemeeting.decisionMakingCommitteeStaff;
                if(null!=fg && fg.length>0){
                    for(var k=0;k<fg.length;k++){
                        fgNameArr.push(fg[k].NAME);
                        fgValueArr.push(fg[k].VALUE);
                    }
                    commonModelValue2(fgValueArr,fgNameArr);
                }
                var investmenta=$scope.Onemeeting.investment;
                if(null!=investmenta && investmenta.length>0){
                    for(var k=0;k<investmenta.length;k++){
                        investmentaNameArr.push(investmenta[k].name);
                    }
                    $scope.Onemeeting.investmentName=investmentaNameArr.join(",");
                }
            }
            //查询申请单相关人，用于发送通知-勿删
            $scope.findRelationUser($scope.pfr._id);
            $scope.getProjectPreReviewYJDWByID($scope.formalID);
        });
    }
    var commonModelValue2=function(arrID,arrName){
        var leftstr2="<li class=\"select2-search-choice\"><div>";
        var centerstr2="</div><a class=\"select2-search-choice-close\" tabindex=\"-1\" onclick=\"delObjMember(this,'";
        var addID2="');\"  ></a><div class=\"full-drop\"><input type=\"hidden\" id=\"\"  value=\"";
        var rightstr2="\"></div></li>";
        for(var i=0;i<arrName.length;i++){
            $("#fixed-member-box").find(".select2-search-field").before(leftstr2+arrName[i]+centerstr2+arrID[i]+addID2+arrID[i]+rightstr2);
        }
    }
    
    $scope.saveOnly=function(){
        var chk_list=document.getElementsByName("choose");
        var fid = "";
        var uuidarr=[],itemarr=[],programmedarr=[],approvedarr=[],fileNamearr=[],filePatharr=[],versionarr=[],upload_datearr=[],programmeIddarr=[],approvedIdarr=[];
        for(var i=0;i<chk_list.length;i++) {
            if(chk_list[i].checked)
            {
                fid = chk_list[i].value;
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
        }else{
            //  $scope.formalReport.policyDecision={};
        }
        $scope.formalReport.policyDecision.submitName=$scope.credentials.userName;
        $scope.formalReport.policyDecision.submitDate=FormatDate();
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
            	upload_datearr[j] = getDate();
            }
            $scope.vvvv.upload_date =upload_datearr[j];
            $scope.vvvv.programmedId =programmeIddarr[j];
            $scope.vvvv.approvedID =approvedIdarr[j];
            array.push($scope.vvvv);
        }
        $scope.formalReport.policyDecision.decisionMakingCommitteeStaffFiles = array;
        var postObj;
        var url ='formalAssessment/FormalReport/addPolicyDecision';
        postObj=$scope.httpData(url,$scope.formalReport);
        postObj.success(function(data){
            if(data.result_code === 'S'){
                $.alert("保存成功");
            }else{
                alert(data.result_name);
            }
        });
    }
    $scope.save=function(showPopWin){
    	 var newAttachment = $scope.reduceAttachmentForSubmit($scope.newAttachment);
    	
    	
        var jprojectSize= $("#jprojectSize").val();
        var jinvestmentAmount=$("#jinvestmentAmount").val();
        var jprojectType=  $("#jprojectType").val();
        var rateOfReturn=  $("#rateOfReturn").val();
        if(null==jprojectSize || ""==jprojectSize){
            $.alert("项目规模不能为空");
            return false;
        }
        if(null==jinvestmentAmount || ""==jinvestmentAmount){
            $.alert("投资金额不能为空"); return false;
        }
        if(null==jprojectType || ""==jprojectType){
            $.alert("请选择项目类型"); return false;
        }
        if(null==rateOfReturn || ""==rateOfReturn){
            $.alert("投资收益率不能为空"); return false;
        }
        var file=$scope.formalReport.policyDecision.fileList;

        var chk_list=document.getElementsByName("choose");
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
        
        var newFiles=document.getElementsByName("choosem");
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
        $scope.formalReport.policyDecision.submitDate=FormatDate();
        if(undefined==$scope.formalReport.policyDecision.decisionMakingCommitteeStaffFiles){
            $scope.formalReport.policyDecision.decisionMakingCommitteeStaffFiles=[];
        }
//        array = $scope.formalReport.policyDecision.decisionMakingCommitteeStaffFiles;
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
            	upload_datearr[j] = getDate();
            }
            $scope.vvvv.upload_date =upload_datearr[j];
            $scope.vvvv.programmedId =programmeIddarr[j];
            $scope.vvvv.approvedID =approvedIdarr[j];
            array.push($scope.vvvv);
        }
        $scope.formalReport.policyDecision.decisionMakingCommitteeStaffFiles = array;
       /* if(fileNamearr.length<1){
            $.alert("请选择相关附件再提交！");
            return false;
        }*/
        var postObj;
        
        $scope.formalReport.aaa = $scope.pfr.attachment;
        
        
       
        
        var url ='formalAssessment/FormalReport/addPolicyDecision';
        postObj=$scope.httpData(url,$scope.formalReport);
        postObj.success(function(data){
            if(data.result_code === 'S'){
                showPopWin();
            }else{
                alert(data.result_name);
            }
        });
    }
    $scope.getSelectTypeByCode=function(typeCode){
        var  url = 'common/commonMethod/getRoleuserByCode';
        $scope.httpData(url,typeCode).success(function(data){
            if(data.result_code === 'S'){
                $scope.userRoleListall=data.result_data;
            }else{
                alert(data.result_name);
            }
        });
    }
    $scope.getSelectTypeByCodetype=function(typeCode){
        var  url = 'common/commonMethod/selectDataDictionByCode';
        $scope.httpData(url,typeCode).success(function(data){
            if(data.result_code == 'S'){
                $scope.projectlisttype=data.result_data;
            }else{
                alert(data.result_name);
            }
        });
    }
    $scope.getByID(objId);

    angular.element(document).ready(function() {
        $scope.getSelectTypeByCode("8");
        $scope.getSelectTypeByCodetype('14');
    });

    //弹出审批框
    $scope.showSubmitModal = function(type){
    	//验证上会附件	
    	if($("input[name='choose']:checked").length+$("input[name='choosem']:checked").length==0){
    	  	$.alert("您没有选择上会附件");
    	  	return;
      	}
        var curUser = $scope.credentials;
        var aMethod = 'rcm/ProjectInfo/selectPrjReviewView';
        $scope.httpData(aMethod, {reportId:objId}).success(
            function (data) {
                var result = data.result_data;
                if(!result.TASK_NAME || result.TASK_NAME!=='提交报告及材料'){
                    $.alert("当前节点为("+result.TASK_NAME+"),不能提交流程");
                    return;
                }
                //验证是否选择上会附件
                

                $scope.save(function(){
                    var newArray = $scope.userRoleListall.concat($scope.relationUsers);
                    $scope.relationUsers = removeDuplicate(newArray);
                    $scope.approve = [{
                        toNodeType:'end',
                        redirectUrl:'/ProjectFormalReviewList',
                        submitInfo:{
                            taskId:result.TASK_ID,
                            noticeInfo:{
                                infoSubject:$scope.pfr.apply.projectName+'正式评审决策材料',
                                businessId:$scope.pfr._id,
                                remark:'',
                                formKey:'/FormalBiddingInfoReview/'+objId+'/0',
                                createBy:curUser.UUID,
                                reader:$scope.relationUsers,
                                type:'1',
                                custText01:'正式评审'
                            }
                        }
                    }];
                    $('#submitModal').modal('show');
                });
            }
        );
    }
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
    /*查询一级审批列表*/
    $scope.getProjectPreReviewYJDWByID=function(id){
        var  url = 'rcm/ProjectInfo/getTaskOpinionTwo';
        $scope.panam={taskDefKey:'usertask16' ,businessId:id};
        $scope.httpData(url,$scope.panam).success(function(data){
            if(data.result_code === 'S') {
                var yijd = data.result_data;
                if (null != yijd) {
                    $scope.yjdw = yijd.DEPTNAME+" _ "+yijd.USERNAME+"："+yijd.OPINION;
                }else{
                    $scope.yjdw=null;
                }
            }
        });
    }

    $scope.errorAttach=[];
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
                url:srvUrl+'common/RcmFile/upload',
                data: {file: file, folder:fileFolder}
            }).then(function (resp) {
                var retData = resp.data.result_data[0];
                $scope.formalReport.policyDecision.fileList[idx].files=retData;
            }, function (resp) {
                console.log('Error status: ' + resp.status);
            }, function (evt) {
                var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                $scope["progress"+idx]=progressPercentage == 100 ? "":progressPercentage+"%";
            });
        }
    };
    
    //新增数组
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
    //删除指定数组
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
    
    
    $scope.upload2 = function (file,errorFile, outId) {
    	if(errorFile && errorFile.length>0){
    		var errorMsg = fileErrorMsg(errorFile);
    		$scope.errorAttach[idx]={msg:errorMsg};
    	}else if(file){
    		var fileFolder = "pfrAssessment/";
    		var dates=$scope.formalReport.create_date;
    		var no=$scope.formalReport.projectNo;
    		var strs= new Array(); //定义一数组
    		strs=dates.split("-"); //字符分割
    		dates=strs[0]+strs[1]; //分割后的字符输出
    		fileFolder=fileFolder+dates+"/"+no;
    		Upload.upload({
    			url:srvUrl+'common/RcmFile/upload',
    			data: {file: file, folder:fileFolder}
    		}).then(function (resp) {
    			var retData = resp.data.result_data[0];
    			$scope.newAttachment[outId].fileName=retData.fileName;
    			$scope.newAttachment[outId].filePath=retData.filePath;
    		}, function (resp) {
    			console.log('Error status: ' + resp.status);
    		}, function (evt) {
//    			var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
    		});
    	}
    };
}]);
function delObjMember(o,id){
    $(o).parent().remove();
    accessScope("#fixed-member", function(scope){
        var names=scope.ArrOnemeeting.decisionMakingCommitteeStaff;
        names.splice(id,1);
        scope.Onemeeting.decisionMakingCommitteeStaff=names;
    });
}

ctmApp.register.controller('FormalBiddingInfoReview', ['$http','$scope','$location','$routeParams', 
                                                       function ($http,$scope,$location,$routeParams) {
    var objId = $routeParams.id;
    $scope.path = $routeParams.path;
    $scope.itrue=false;
    $scope.ishow=true;
    $scope.formalID=null;
    $scope.noticeDecision = null;
    $scope.initData = function(){
    	$scope.getByID(objId);
    }
    $scope.initNoticeDecision = function(){
    	$http({
    		method:'post',  
		    url:srvUrl+"noticeOfDecision/queryNoticeDecisionByFormalId.do", 
		    data: $.param({"businessId":$scope.formalID})
    	}).success(function(result){
    		$scope.noticeDecision = result.result_data;
    	});
    }
    $scope.getByID=function(id){
        var  url = 'projectPreReview/Meeting/findFormalAndReport';
        $scope.httpData(url,id).success(function(data){
            $scope.Onemeeting  = data.result_data.Meeting;
            $scope.formalReport  = data.result_data.Report;
            $scope.pfr  = data.result_data.Formal;
            $scope.formalID=$scope.formalReport.projectFormalId;
            //加载决策通知书
            $scope.initNoticeDecision();
            
            var ptNameArr=[],investmentaNameArr=[];
            var pt=$scope.pfr.apply.projectType;
            if(null!=pt && pt.length>0){
                for(var i=0;i<pt.length;i++){
                    ptNameArr.push(pt[i].VALUE);
                }
                $scope.pfr.apply.projectType=ptNameArr.join(",");
            }
            function addBlankRow(array){
                var blankRow = {
                    particularView:'',
                    aagreeOrDisagree:'',
                    userId:$scope.credentials.UUID,
                    userName:$scope.credentials.userName
                }
                array[0]=blankRow;
            }

            if(undefined!=$scope.Onemeeting&&$scope.Onemeeting.decisionMakingCommitteeStaff!=null){
            	$scope.selfOpinion = {};
            	$scope.isSelectToOpinion = false;
            	for(var i = 0; i < $scope.Onemeeting.decisionMakingCommitteeStaff.length; i++){
            		if($scope.credentials.UUID == $scope.Onemeeting.decisionMakingCommitteeStaff[i].VALUE){
            			$scope.selfOpinion.userId = $scope.credentials.UUID;
            			$scope.selfOpinion.userName = $scope.credentials.userName;
            			$scope.isSelectToOpinion = true;
            			break;
            		}
            	}
            	if($scope.Onemeeting.decisionOpinionList == null){
            		$scope.Onemeeting.decisionOpinionList = [];
            	}
            	var allOpinion = $scope.Onemeeting.decisionOpinionList;
            	
            	$scope.otherOpinionList = [];
            	if($scope.isSelectToOpinion){
                	for(var i = 0; i < allOpinion.length; i++){
                		if($scope.credentials.UUID == allOpinion[i].userId){
                			$scope.selfOpinion = allOpinion[i];
                		}else{
                			if(allOpinion[i].particularView != null && ""!=allOpinion[i].particularView){
                				$scope.otherOpinionList.push(allOpinion[i]);
                			}
                		}
                	}
            	}else{
            		for(var i = 0; i < allOpinion.length; i++){
            			if(allOpinion[i].particularView != null && ""!=allOpinion[i].particularView){
            				$scope.otherOpinionList.push(allOpinion[i]);
            			}
                	}
            	}
            	if($scope.selfOpinion.aagreeOrDisagree!=null){
            		 $scope.itrue = true;
            		 $("#submitjcbtn").hide();
            	}
            	if(!$scope.isSelectToOpinion && $scope.otherOpinionList.length == 0){
            		$scope.ishow=false;
            		$("#submitjcbtn").hide();
            	}
            	if(!$scope.isSelectToOpinion){
            		$("#submitjcbtn").hide();
            	}
            }else{
                $scope.ishow=false;
                $("#submitjcbtn").hide();
            }
            $scope.getProjectPreReviewYJDWByID($scope.formalID);
        });
    }
    $scope.initData();
    $scope.save=function(){
        var aagreeOrDisagree= $("input[name='aagreeOrDisagree']:checked").val();
        var particularView=$(".particularView").val();
        if(null==aagreeOrDisagree || ""==aagreeOrDisagree){
            $.alert("决策意见不能为空");
            return false;
        }
        $scope.selfOpinion.aagreeOrDisagree = aagreeOrDisagree;
        if(null==particularView || ""==$.trim(particularView)){
            $.alert("具体意见不能为空");
            return false;
        }
         $.confirm('提交之后将无法修改！',function(){
              var postObj;
              var url = 'projectPreReview/Meeting/updateMeetingForPolicyDecision';
              $scope.Onemeeting.decisionOpinionList.push($scope.selfOpinion);
              postObj = $scope.httpData(url, $scope.Onemeeting);
              postObj.success(function (data) {
                  if (data.result_code === 'S') {
                      $location.path("/FormalReviewList/0");
                  } else {
                      alert(data.result_name);
                  }
              }); 
         });
    }
    $scope.getSelectTypeByCodetype=function(typeCode){
        var  url = 'common/commonMethod/selectDataDictionByCode';
        $scope.httpData(url,typeCode).success(function(data){
            if(data.result_code == 'S'){
                $scope.projectlisttype=data.result_data;
            }else{
                alert(data.result_name);
            }
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
                }else{
                    $scope.yjdw=null;
                }
            }
        });
    }

    function diffDate(evalue) {
        var dB = new Date(evalue.replace(/-/g, "/"));
        if (new Date() >= Date.parse(dB)) {
            return 1;
        }
        return 0;
    }
    angular.element(document).ready(function() {
        $scope.getSelectTypeByCodetype('14');
    });
}]);

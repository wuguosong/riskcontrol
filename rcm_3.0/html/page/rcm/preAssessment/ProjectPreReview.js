/********
 * Created by wangjian on 16/4/11.
 * 用户管理控制器
 *********/
ctmApp.register.controller('ProjectPreReview', ['$http','$scope','$location', function ($http,$scope,$location) {

	function FormatDate() {
		var date = new Date();
		var paddNum = function(num){
			num += "";
			return num.replace(/^(\d)$/,"0$1");
		}
		return date.getFullYear()+""+paddNum(date.getMonth()+1)+""+paddNum(date.getDate())+"-"+paddNum(date.getHours())+""+paddNum(date.getMinutes())+""+paddNum(date.getSeconds());
	}
	
	$scope.buttonControl=srvUrl=="http://riskcontrol.bewg.net.cn/rcm-rest/";
	$scope.orderby='desc';
	$scope.buttonControl=srvUrl=="http://riskcontrol.bewg.net.cn/rcm-rest/";
	//查义所有的操作
	$scope.ListAll=function(){
		$scope.conf={currentPage:$scope.paginationConf.currentPage,pageSize:$scope.paginationConf.itemsPerPage,queryObj:{'user_id':$scope.credentials.UUID,'PROJECT_NAME':$scope.PROJECT_NAME,'INVESTMENT_MANAGER_NAME':$scope.INVESTMENT_MANAGER_NAME,
		'PROJECT_MODEL_NAMES':$scope.PROJECT_MODEL_NAMES,'PROJECT_TYPE_NAMES':$scope.PROJECT_TYPE_NAMES,'COMPANY_HEADER_NAME':$scope.COMPANY_HEADER_NAME,'CREATE_TIME':$scope.CREATE_TIME,'WF_STATE':$scope.WF_STATE,'ASCDESC':$scope.orderby,'WFSTATEASCDESC':$scope.orderbystate,
			'APPLYTIMEFROM':$scope.applyTimeFrom, 'APPLYTIMETO':$scope.applyTimeTo,'REPORTING_UNIT_NAME':$scope.REPORTING_UNIT_NAME}};
		var url = 'projectPreReview/ProjectPreReview/getAll';
		$scope.httpData(url,$scope.conf).success(function(data){
			// 变更分页的总数
			$scope.projectPreReview  = data.result_data.list;
			$scope.paginationConf.totalItems = data.result_data.totalItems;
		});
	};
	$scope.order=function(o,v){
		if(o=="time"){
			$scope.orderby=v;
			$scope.orderbystate=null;
			if(v=="asc"){
				$("#orderasc").addClass("cur");
				$("#orderdesc").removeClass("cur");
			}else{
				$("#orderdesc").addClass("cur");
				$("#orderasc").removeClass("cur");
			}
		}else{
			$scope.orderbystate=v;
			$scope.orderby=null;
			if(v=="asc"){
				$("#orderascstate").addClass("cur");
				$("#orderdescstate").removeClass("cur");
			}else{
				$("#orderdescstate").addClass("cur");
				$("#orderascstate").removeClass("cur");
			}
		}
		$scope.ListAll();
	}

	$scope.import=function(){
		var aMethed =  'projectPreReview/ProjectPreReview/importPreAll';
		$scope.httpData(aMethed,$scope.credentials.UUID).success(
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
	//删除操作
	$scope.Delete=function(){

			var chk_list=document.getElementsByName("checkbox");
			var uid = "",num=0;
			for(var i=0;i<chk_list.length;i++)
			{
				if(chk_list[i].checked)
				{
					num++;
					uid = uid+','+chk_list[i].value;
				}
			}
			if(uid!=''){
				uid=uid.substring(1,uid.length);
			}

		if(num==0){
				$.alert("请选择其中一条或多条数据删除！");
				return false;
			}
			var obj={"_id": uid};
			var aMethed = 'projectPreReview/ProjectPreReview/deleteProjectPreReview';
			$scope.httpData(aMethed, obj).success(
				function (data, status, headers, config) {
					console.log(data);
					if (data.result_code == "R") {
						$.alert('报告未删除！删除申请单之前请先删除该项目下的报告单！');
						return false;
					}else{
						$.confirm("确定要删除？", function () {
							$scope.httpData(aMethed, obj).success(
								function (data, status, headers, config) {
									$scope.ListAll();
								}
							).error(function (data, status, headers, config) {
								alert(status);
							});
						});
					}
				}
			).error(function (data, status, headers, config) {
				alert(status);
			});
		};
	$scope.update=function(state){
		var chk_list=document.getElementsByName("checkbox");
		var uid = "",num=0;
		for(var i=0;i<chk_list.length;i++)
		{
			if(chk_list[i].checked)
			{
				num++;
				uid = uid+','+chk_list[i].value;
			}
		}
		if(uid!=''){
			uid=uid.substring(1,uid.length);
		}
		if(num==0){
			$.alert("请选择其中一条或多条数据编辑！");
			return false;
		}
		if(num>1){
			$.alert("只能选择其中一条数据进行编辑！");
			return false;
		}else{
			$location.path("/ProjectPreReviewDetail/Update/"+uid);
		}
	}
	// 通过$watch currentPage和itemperPage 当他们一变化的时候，重新获取数据条目
	$scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage',  $scope.ListAll);

	//flag="4"代表是从预评审申请列表进入工单详情
	$scope.flag="4";
}]);

ctmApp.register.controller('BiddingInfo', ['$http','$scope','$location', '$routeParams', function ($http,$scope,$location, $routeParams) {
	//初始化
	$scope.dic={};
	$scope.pre = {};
	$scope.pre.apply = {};
	$scope.pre.taskallocation={};
	$scope.pre.taskallocation.reviewLeader=null;
	$scope.pre.taskallocation.fixedGroup=null;
	var complexId = $routeParams.id;
	var params = complexId.split("@");
	var currentUserName = $scope.credentials.userName;
	$scope.paramId = $routeParams.id;
	//初始化决策委员会成员
	var roleId = '2df64ade-c20d-4d74-bad6-61b93fd7d88f';
	$scope.httpData('common/commonMethod/getRoleuser',roleId).success(function(data){
		if(data.result_code == 'S'){
			$scope.policyUsers = data.result_data.userRoleList;
		}
	});
	$scope.getProjectPreReviewByID=function(id){
		var  url = 'projectPreReview/ProjectPreReview/getProjectPreReviewByID';
		$scope.httpData(url,id).success(function(data){
			var pthNameArr=[],ptNameArr=[],pmNameArr=[],fgNameArr=[],fgValueArr=[];
			$scope.pre  = data.result_data;
			/*var pth=$scope.pre.apply.companyHeader;
			if(null!=pth && pth.length>0){
				for(var i=0;i<pth.length;i++){
					pthNameArr.push(pth[i].name);
				}
				$scope.pre.apply.companyHeaderName=pthNameArr.join(",");
			}*/
			var pt1NameArr=[];
			var pt1=$scope.pre.apply.serviceType;
			if(null!=pt1 && pt1.length>0){
				for(var i=0;i<pt1.length;i++){
					pt1NameArr.push(pt1[i].VALUE);
				}
				$scope.pre.apply.serviceType=pt1NameArr.join(",");
			}
			var pt=$scope.pre.apply.projectType;
			if(null!=pt && pt.length>0){
				for(var i=0;i<pt.length;i++){
					ptNameArr.push(pt[i].VALUE);
				}
				$scope.pre.apply.projectType=ptNameArr.join(",");
			}
			var pm=$scope.pre.apply.projectModel;
			if(null!=pm && pm.length>0){
				for(var j=0;j<pm.length;j++){
					pmNameArr.push(pm[j].VALUE);
				}
				$scope.pre.apply.projectModel=pmNameArr.join(",");
			}
			var fg=$scope.pre.decisionMakingCommitteeStaff;

			if(null!=fg && fg.length>0){
				for(var k=0;k<fg.length;k++){
					fgNameArr.push(fg[k].NAME);
					fgValueArr.push(fg[k].UUID);
				}
				commonModelValue2(fgValueArr,fgNameArr);
			}
			$("select[id^='attachmentNew']").attr("disabled","disabled");
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
	$scope.getSelectTypeByCode=function(typeCode){
		var  url = 'common/commonMethod/getRoleuserByCode';
		$scope.httpData(url,typeCode).success(function(data){
			if(data.result_code === 'S'){
				//$scope.dic=[];
				$scope.userRoleListall=data.result_data;
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
			/*var gdcy=data.result_data;
			if(null!=gdcy){
				var name=",";
				for(var i=0;i<gdcy.length;i++){
					name=name+gdcy[i].USERNAME+":"+gdcy[i].OPINION;
				}
				name=name.substring(1,name.length);
				$scope.gdcy=name;
			}*/
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
	$scope.getProjectPreReviewGDCYByID(params[0]);
	$scope.getProjectPreReviewYJDWByID(params[0]);
	angular.element(document).ready(function() {
		$scope.getSelectTypeByCode("2");
	});

	$scope.showSubmitModal = function(type){
		var curUser = $scope.credentials;
		$scope.httpData('rcm/ProjectInfo/selectPrjReviewView',{businessId:params[0]}).success(function(data){
			var taskId = params[3];
			var result = data.result_data;
			if(!result.TASK_NAME || result.TASK_NAME!=='出具预评审报告'){
				$.alert("当前节点为("+result.TASK_NAME+"),不能提交流程");
				return;
			}else{
				taskId = result.TASK_ID;
			}
			$scope.save(function(){
				var newArray = $scope.relationUsers.concat($scope.pre.decisionMakingCommitteeStaff);
				$scope.relationUsers = removeDuplicate(newArray);
				$scope.approve = [{
					toNodeType:'end',
					redirectUrl:'/ProjectPreReview',
					submitInfo:{
						taskId:taskId,
						noticeInfo:{
							infoSubject:$scope.pre.apply.projectName+'预评审决策材料',
							businessId:params[0],
							remark:'',
							formKey:'/BiddingInfoReview/'+params[0],
							createBy:curUser.UUID,
							reader:$scope.relationUsers,
							type:'1',
							custText01:'预评审'
						},
						runtimeVar:{}
					}
				}];
				$('#submitModal').modal('show');
			});

		});

	}
	//获取所有的相关人
	$scope.findRelationUser(params[0]);

	$scope.save=function(showPopWin){
		var postObj;
		var url ='projectPreReview/ProjectPreReview/saveProjectPreReviewperple';
		var perple=$scope.pre.decisionMakingCommitteeStaff;
		if(null==perple || perple==""){
			$.alert("请选择参会人员！");
			return false;
		}
		var chk_list=document.getElementsByName("choose");
		var fid = "",num=0;
		var uuidarr=[],itemarr=[],programmedarr=[],approvedarr=[],fileNamearr=[],filePatharr=[],versionarr=[],upload_datearr=[],programmeIddarr=[],approvedIdarr=[];
		for(var i=0;i<chk_list.length;i++)
		{
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

		var array=$scope.pre.decisionMakingCommitteeStaffFiles=[];
		for(var j=0;j<fileNamearr.length;j++) {
			$scope.vvvv = {};
			$scope.vvvv.UUID =uuidarr[j];
			$scope.vvvv.ITEM_NAME =itemarr[j];
			$scope.vvvv.programmed =programmedarr[j];
			$scope.vvvv.approved =approvedarr[j];
			$scope.vvvv.fileName =fileNamearr[j];
			$scope.vvvv.filePath =filePatharr[j];
			$scope.vvvv.version =versionarr[j];
			$scope.vvvv.upload_date =upload_datearr[j];
			$scope.vvvv.programmedId =programmeIddarr[j];
			$scope.vvvv.approvedID =approvedIdarr[j];
			array.push($scope.vvvv);
		}

		postObj=$scope.httpData(url,$scope.pre);
		postObj.success(function(data){
			if(data.result_code === 'S'){
				if(typeof showPopWin=='function'){
					showPopWin();
				}else{
					$.alert("保存成功");
				}
			}else{
				alert(data.result_name);
			}
		});
	}
	$scope.getProjectPreReviewByID(params[0]);
	$scope.downLoadFileReport = function(filePath,filename){
		if(undefined!=filePath && null!=filePath){
			var index = filePath.lastIndexOf(".");
			var str = filePath.substring(index + 1, filePath.length);
			window.location.href = srvUrl+"common/RcmFile/downLoad?filePath="+encodeURI(filePath)+"&fileName="+encodeURI(filename+"-预评审报告."+str);
		}else{
			$.alert("附件未找到！");
			return false;
		}
	}
}]);
function delObjMember(o,id){
	$(o).parent().remove();
	accessScope("#fixed-member", function(scope){
		var names=scope.pre.decisionMakingCommitteeStaff;
		names.splice(id,1);
		scope.pre.decisionMakingCommitteeStaff=names;
	});
}

ctmApp.register.controller('ProfessionalReviewComments', ['$http','$scope','$location','$routeParams', function ($http,$scope,$location,$routeParams) {
	//初始化
	var complexId = $routeParams.id;
	var params = complexId.split("@");
	if(null!=params[4] && ""!=params[4]){
		$scope.flag=params[4];
	}
	//提交流程
	$scope.showSubmitModal = function(){
	//	$scope.saveProReviewComments(function(){
			var userName = $scope.credentials.userName;
			var approveUser =  $scope.pre.taskallocation.reviewLeader;
			$scope.approve = [{
				submitInfo:{
					taskId:params[3],
					runtimeVar:{inputUser:approveUser.VALUE},
					currentTaskVar:{opinion:'请审批'},
					newTaskVar:{submitBy:userName,emergencyLevel:'一般'}
				},
				showInfo:{destination:'评审负责人确认',approver: approveUser.NAME}
			}];
			$('#submitModal').modal('show');
		//});
	}
	$scope.wfInfo = {processDefinitionId:params[1],processInstanceId:params[2]};
	$scope.saveProReviewComments=function(showPopWin){
		var  url = 'projectPreReview/ProjectPreReview/saveProjectPreReviewByID';
		var proReviewComments = $scope.pre.proReviewComments;
		if((proReviewComments==null || proReviewComments=="" )){
			$.alert("请填写专业评审意见");
			return false;
		}
		$scope.httpData(url,$scope.pre).success(function(data){
			if(data.result_code === 'S'){
				if(showPopWin && typeof(showPopWin)=='function'){
					showPopWin();
				}else{
					$.alert("保存成功");
				}
			}
		});
	}
	$scope.getProjectPreReviewByID=function(id){
		var  url = 'projectPreReview/ProjectPreReview/getProjectPreReviewByID';
		$scope.httpData(url,id).success(function(data){
			var ptNameArr=[],pthNameArr=[],pmNameArr=[],fgNameArr=[],rlNameArr=[];
			$scope.pre  = data.result_data;
			/*var pth=$scope.pre.apply.companyHeader;
			if(null!=pth && pth.length>0){
				for(var i=0;i<pth.length;i++){
					pthNameArr.push(pth[i].name);
				}
				$scope.pre.apply.companyHeaderName=pthNameArr.join(",");
			}*/
			var pt1NameArr=[];
			var pt1=$scope.pre.apply.serviceType;
			if(null!=pt1 && pt1.length>0){
				for(var i=0;i<pt1.length;i++){
					pt1NameArr.push(pt1[i].VALUE);
				}
				$scope.pre.apply.serviceType=pt1NameArr.join(",");
			}
			var pt=$scope.pre.apply.projectType;
			if(null!=pt && pt.length>0){
				for(var i=0;i<pt.length;i++){
					ptNameArr.push(pt[i].VALUE);
				}
				$scope.pre.apply.projectType=ptNameArr.join(",");
			}
			var pm=$scope.pre.apply.projectModel;
			if(null!=pm && pm.length>0){
				for(var j=0;j<pm.length;j++){
					pmNameArr.push(pm[j].VALUE);
				}
				$scope.pre.apply.projectModel=pmNameArr.join(",");
			}
			var fg=$scope.pre.taskallocation.fixedGroup;
			if(null!=fg && fg.length>0){
				for(var k=0;k<fg.length;k++){
					fgNameArr.push(fg[k].NAME);
				}
				$scope.pre.taskallocation.fixedGroup=fgNameArr.join(",");
			}

			$scope.dic = $scope.dic | {};
			$scope.fileName=[];
			var filenames=$scope.pre.attachment;
			for(var i=0;i<filenames.length;i++){
				var arr={UUID:filenames[i].UUID,ITEM_NAME:filenames[i].ITEM_NAME};
				$scope.fileName.push(arr);
			}
			if(null!=$scope.pre.apply.tenderTime) {
				$scope.changDate($scope.pre.apply.tenderTime);
			}
			$scope.getProjectPreReviewYJDWByID(id);
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
	$scope.getSelectTypeByCode=function(typeCode){
		var  url = 'common/commonMethod/selectDataDictionByCode';
		$scope.httpData(url,typeCode).success(function(data){
			if(data.result_code === 'S'){
				// $scope.dic=[];
				$scope.optionTypeList=data.result_data;
			}else{
				$.alert(data.result_name);
			}
		});
	}
	angular.element(document).ready(function() {
		$scope.getSelectTypeByCode("06");

	});
	$scope.getProjectPreReviewByID(params[0]);
	/*查询一级审批列表*/
	$scope.getProjectPreReviewYJDWByID=function(id){
		var  url = 'rcm/ProjectInfo/getTaskOpinionTwo';
		$scope.panam={taskDefKey:'usertask16' ,businessId:id};
		$scope.httpData(url,$scope.panam).success(function(data){
			if(data.result_code === 'S') {
				var yijd = data.result_data;
				if (null != yijd) {
					$scope.yjdw = yijd.DEPTNAME+" _ "+yijd.USERNAME+"："+yijd.OPINION;
					if(null!=$scope.pre.cesuanFileOpinion && ''!=$scope.pre.cesuanFileOpinion){
						$scope.yjdw = $scope.yjdw+" ；<br/>项目投资测算意见："+$scope.pre.cesuanFileOpinion;
					}
					if(null!=$scope.pre.tzProtocolOpinion && ''!=$scope.pre.tzProtocolOpinion){
						$scope.yjdw = $scope.yjdw+" ；<br/>项目投资协议意见：" + $scope.pre.tzProtocolOpinion;
					}
					$scope.yjdw=Utils.encodeTextAreaString($scope.yjdw);
				}else{
					$scope.yjdw=null;
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

}]);

ctmApp.register.controller('ProjectPreReviewView', ['$http','$scope','$location','$routeParams', function ($http,$scope,$location,$routeParams) {
	$scope.pre = {};
	$scope.pre.apply = {};
	var complexId = $routeParams.id;
	var params = complexId.split("@");
	var objId = params[0];
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
				$("#submitbtn").hide();
			}
		}
	});
	
	if(null!=params[4] && ""!=params[4]){
		$scope.flag=params[4];
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

	$scope.getProjectPreReviewByID=function(id){
		var  url = 'projectPreReview/ProjectPreReview/getProjectPreReviewByID';
		$scope.httpData(url,id).success(function(data){
			var pthNameArr=[],ptNameArr=[],ccNameArr=[],pmNameArr=[],fgNameArr=[];
			$scope.pre  = data.result_data;
			/*var pth=$scope.pre.apply.companyHeader;
			if(null!=pth && pth.length>0){
				for(var i=0;i<pth.length;i++){
					pthNameArr.push(pth[i].name);
				}
				$scope.pre.apply.companyHeaderName=pthNameArr.join(",");
			}*/
			var pt1NameArr=[];
			var pt1=$scope.pre.apply.serviceType;
			if(null!=pt1 && pt1.length>0){
				for(var i=0;i<pt1.length;i++){
					pt1NameArr.push(pt1[i].VALUE);
				}
				$scope.pre.apply.serviceType=pt1NameArr.join(",");
			}
			var pt=$scope.pre.apply.projectType;
			if(null!=pt && pt.length>0){
				for(var i=0;i<pt.length;i++){
					ptNameArr.push(pt[i].VALUE);
				}
				$scope.pre.apply.projectType=ptNameArr.join(",");
			}
			var pm=$scope.pre.apply.projectModel;
			if(null!=pm && pm.length>0){
				for(var j=0;j<pm.length;j++){
					pmNameArr.push(pm[j].VALUE);
				}
				$scope.pre.apply.projectModel=pmNameArr.join(",");
			}
			if(undefined!=$scope.pre.taskallocation)
			{
				if(undefined!=$scope.pre.taskallocation.fixedGroup){
					var fg = $scope.pre.taskallocation.fixedGroup;
					if (null != fg && fg.length > 0) {
						for (var k = 0; k < fg.length; k++) {
							fgNameArr.push(fg[k].NAME);
						}
						$scope.pre.taskallocation.fixedGroup = fgNameArr.join(",");
					}
				}
			}

			//$scope.dic = $scope.dic | {};
			$scope.fileName=[];
			var filenames=$scope.pre.attachment;
			for(var i=0;i<filenames.length;i++){
				var arr={UUID:filenames[i].UUID,ITEM_NAME:filenames[i].ITEM_NAME};
				$scope.fileName.push(arr);
			}
			if(null!=$scope.pre.apply.tenderTime) {
				$scope.changDate($scope.pre.apply.tenderTime);
			}
			$scope.getProjectPreReviewYJDWByID(objId);
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
    /*查询固定成员审批列表*/
	$scope.getProjectPreReviewGDCYByID=function(id){
		var  url = 'rcm/ProjectInfo/getTaskOpinion';
		$scope.panam={taskDefKey:'usertask4' ,businessId:id};
		$scope.httpData(url,$scope.panam).success(function(data){
			$scope.gdcy  = data.result_data;

		});
	}
	$scope.getSelectTypeByCode=function(typeCode){
		var  url = 'common/commonMethod/selectDataDictionByCode';
		$scope.httpData(url,typeCode).success(function(data){
			if(data.result_code === 'S'){
				// $scope.dic=[];
				$scope.optionTypeList=data.result_data;
			}else{
				$.alert(data.result_name);
			}
		});
	}
	$scope.getProjectPreReviewByID(objId);
	$scope.getProjectPreReviewGDCYByID(objId);
	/*查询一级审批列表*/
	$scope.getProjectPreReviewYJDWByID=function(id){
		var  url = 'rcm/ProjectInfo/getTaskOpinionTwo';
		$scope.panam={taskDefKey:'usertask16' ,businessId:id};
		$scope.httpData(url,$scope.panam).success(function(data){
			if(data.result_code === 'S') {
				//$scope.yjdw = data.result_data;
				var yijd = data.result_data;
				if (null != yijd) {
					$scope.yjdw = yijd.DEPTNAME+" _ "+yijd.USERNAME+"："+yijd.OPINION;
					if(null!=$scope.pre.cesuanFileOpinion && ''!=$scope.pre.cesuanFileOpinion){
						$scope.yjdw = $scope.yjdw+" ；<br/>项目投资测算意见："+$scope.pre.cesuanFileOpinion;
					}
					if(null!=$scope.pre.tzProtocolOpinion && ''!=$scope.pre.tzProtocolOpinion){
						$scope.yjdw = $scope.yjdw+" ；<br/>项目投资协议意见：" + $scope.pre.tzProtocolOpinion;
					}
					$scope.yjdw=Utils.encodeTextAreaString($scope.yjdw);
				}else{
					$scope.yjdw=null;
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

	angular.element(document).ready(function() {
		$scope.getSelectTypeByCode("06");

	});
	$scope.showSubmitModal = function(){
			var companyHeader = $scope.pre.apply.companyHeader;
			$scope.approve = [{
				submitInfo:{
					startVar:{processKey:'preAssessment',businessId:$scope.pre._id,subject:$scope.pre.apply.projectName+'预评审申请',inputUser:$scope.credentials.UUID},
					runtimeVar:{inputUser:companyHeader.value},
					currentTaskVar:{opinion:'请审批'}
				},
				showInfo:{destination:'单位负责人审核',approver: companyHeader.name}
			}];
			if(params[1]){
				$scope.approve[0].submitInfo.taskId=params[3];
			}
			$scope.approve[0].redirectUrl="ProjectPreReview";
			$('#submitModal').modal('show');
	};
	if(typeof (params[1])=='string' && typeof (params[2])=='string' && params[1]!='' && params[2]!=''){//已经启动流程
        $scope.wfInfo = {processDefinitionId:params[1],processInstanceId:params[2]};
    }else{//未启动流程
        $scope.wfInfo = {processKey:'preAssessment'};
    }
}]);

ctmApp.register.controller('ProjectPreReviewViewReport', ['$http','$scope','$location','$routeParams', function ($http,$scope,$location,$routeParams) {
	$scope.pre = {};
	$scope.pre.apply = {};
	$scope.comId= $routeParams.id;
	var complexId = $routeParams.id;
	var params = complexId.split("@");
	var objId = params[0];
	if(null!=params[1] && ""!=params[1]){
		$("#submitbtn").hide();
	}
	if(null!=params[4] && ""!=params[4]){
		$scope.flag=params[4];
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

	$scope.getProjectPreReviewByID=function(id){
		var  url = 'projectPreReview/ProjectPreReview/getProjectPreReviewByID';
		$scope.httpData(url,id).success(function(data){
			var pthNameArr=[],ptNameArr=[],ccNameArr=[],pmNameArr=[],fgNameArr=[];
			$scope.pre  = data.result_data;
			/*var pth=$scope.pre.apply.companyHeader;
			 if(null!=pth && pth.length>0){
			 for(var i=0;i<pth.length;i++){
			 pthNameArr.push(pth[i].name);
			 }
			 $scope.pre.apply.companyHeaderName=pthNameArr.join(",");
			 }*/
			var pt1NameArr=[];
			var pt1=$scope.pre.apply.serviceType;
			if(null!=pt1 && pt1.length>0){
				for(var i=0;i<pt1.length;i++){
					pt1NameArr.push(pt1[i].VALUE);
				}
				$scope.pre.apply.serviceType=pt1NameArr.join(",");
			}
			var pt=$scope.pre.apply.projectType;
			if(null!=pt && pt.length>0){
				for(var i=0;i<pt.length;i++){
					ptNameArr.push(pt[i].VALUE);
				}
				$scope.pre.apply.projectType=ptNameArr.join(",");
			}
			var pm=$scope.pre.apply.projectModel;
			if(null!=pm && pm.length>0){
				for(var j=0;j<pm.length;j++){
					pmNameArr.push(pm[j].VALUE);
				}
				$scope.pre.apply.projectModel=pmNameArr.join(",");
			}
			if(undefined!=$scope.pre.taskallocation)
			{
				if(undefined!=$scope.pre.taskallocation.fixedGroup){
					var fg = $scope.pre.taskallocation.fixedGroup;
					if (null != fg && fg.length > 0) {
						for (var k = 0; k < fg.length; k++) {
							fgNameArr.push(fg[k].NAME);
						}
						$scope.pre.taskallocation.fixedGroup = fgNameArr.join(",");
					}
				}
			}

			//$scope.dic = $scope.dic | {};
			$scope.fileName=[];
			var filenames=$scope.pre.attachment;
			for(var i=0;i<filenames.length;i++){
				var arr={UUID:filenames[i].UUID,ITEM_NAME:filenames[i].ITEM_NAME};
				$scope.fileName.push(arr);
			}
			if(null!=$scope.pre.apply.tenderTime) {
				$scope.changDate($scope.pre.apply.tenderTime);
			}
			$scope.getProjectPreReviewYJDWByID(objId);
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
	$scope.getSelectTypeByCode=function(typeCode){
		var  url = 'common/commonMethod/selectDataDictionByCode';
		$scope.httpData(url,typeCode).success(function(data){
			if(data.result_code === 'S'){
				// $scope.dic=[];
				$scope.optionTypeList=data.result_data;
			}else{
				$.alert(data.result_name);
			}
		});
	}
	$scope.getProjectPreReviewByID(objId);
	/*查询一级审批列表*/
	$scope.getProjectPreReviewYJDWByID=function(id){
		var  url = 'rcm/ProjectInfo/getTaskOpinionTwo';
		$scope.panam={taskDefKey:'usertask16' ,businessId:id};
		$scope.httpData(url,$scope.panam).success(function(data){
			if(data.result_code === 'S') {
				//$scope.yjdw = data.result_data;
				var yijd = data.result_data;
				if (null != yijd) {
					$scope.yjdw = yijd.DEPTNAME+" _ "+yijd.USERNAME+"："+yijd.OPINION;
					if(null!=$scope.pre.cesuanFileOpinion && ''!=$scope.pre.cesuanFileOpinion){
						$scope.yjdw = $scope.yjdw+" ；<br/>项目投资测算意见："+$scope.pre.cesuanFileOpinion;
					}
					if(null!=$scope.pre.tzProtocolOpinion && ''!=$scope.pre.tzProtocolOpinion){
						$scope.yjdw = $scope.yjdw+" ；<br/>项目投资协议意见：" + $scope.pre.tzProtocolOpinion;
					}
					$scope.yjdw=Utils.encodeTextAreaString($scope.yjdw);
				}else{
					$scope.yjdw=null;
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

	angular.element(document).ready(function() {
		$scope.getSelectTypeByCode("06");

	});

	if(typeof (params[1])=='string' && typeof (params[2])=='string' && params[1]!='' && params[2]!=''){//已经启动流程
		$scope.wfInfo = {processDefinitionId:params[1],processInstanceId:params[2]};
	}else{//未启动流程
		$scope.wfInfo = {processKey:'preAssessment'};
	}
	//判断是否存在正式评审报告
	$scope.preReport = {};
	$scope.httpData('rcm/ProjectInfo/selectByBusinessId',objId).success(function(data){
		if(data.result_code == 'S'){
			var result = data.result_data;
			if(result && result.REPORT_CREATE_DATE){
				$scope.hasReportAllready = true;
			}else{
				$scope.hasReportAllready = false;
			}
		}
	});
	//编辑评审报告
	$scope.editPreReport = function(comId){
		$location.path('PreReviewReport/Update/'+objId+"@@1@"+comId);
	}
	//创建评审报告
	$scope.createPreReport = function(){
		$location.path("ReportList");
	}

}]);

ctmApp.register.controller('companyHeaderApprove', ['$http','$scope','$location','$routeParams', function ($http,$scope,$location,$routeParams) {
	//初始化
	var complexId = $routeParams.id;
	var params = complexId.split("@");
	$scope.findRelationUser(params[0]);

	if(null!=params[4] && ""!=params[4]){
		$scope.flag=params[4];
	}
	//提交流程
	$('#submitModal').on('show.bs.modal',function(e){
		var userName = $scope.credentials.userName;
		var approveUser =  $scope.pre.apply.investmentPerson;
		if(approveUser == null || typeof(approveUser)=='undefined'|| approveUser.name==null||approveUser.name==""){
			approveUser = $scope.pre.apply.directPerson;
		}
		var investmentManager = $scope.pre.apply.investmentManager;//投资经理
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
						var investmentManager = $scope.pre.apply.investmentManager;//投资经理
						if(ma){
							nextNode = {
								submitInfo:{
									taskId:params[3],
									runtimeVar:{inputUser:ma.VALUE,toTask:0,isSkipServiceType:"1"},
									currentTaskVar:{opinion:'请审批'},
									businessId:params[0],
									noticeInfo:{
										infoSubject:$scope.pre.apply.projectName+'预评审申请进入风控审批阶段，请悉知！',
										businessId:params[0],
										remark:'',
										formKey:'/ProjectPreReviewView/'+params[0]+'@'+params[1]+'@'+params[2],
										createBy:$scope.credentials.UUID,
										reader:$scope.removeObjByValue(removeDuplicate($scope.relationUsers),ma.VALUE),
										type:'1',
										custText01:'预评审'
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
						runtimeVar:{inputUser:approveUser.value,toTask:0,isSkipServiceType:"0"},
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
		
	});
	$scope.wfInfo = {processDefinitionId:params[1],processInstanceId:params[2]};

	$scope.getProjectPreReviewByID=function(id){
		var  url = 'projectPreReview/ProjectPreReview/getProjectPreReviewByID';
		$scope.httpData(url,id).success(function(data){
			var ptNameArr=[],pthNameArr=[],pmNameArr=[],fgNameArr=[],rlNameArr=[];
			$scope.pre  = data.result_data;
			/*var pth=$scope.pre.apply.companyHeader;
			if(null!=pth && pth.length>0){
				for(var i=0;i<pth.length;i++){
					pthNameArr.push(pth[i].name);
				}
				$scope.pre.apply.companyHeaderName=pthNameArr.join(",");
			}*/
			var pt1NameArr=[];
			var pt1=$scope.pre.apply.serviceType;
			if(null!=pt1 && pt1.length>0){
				for(var i=0;i<pt1.length;i++){
					pt1NameArr.push(pt1[i].VALUE);
				}
				$scope.pre.apply.serviceType=pt1NameArr.join(",");
			}
			var pt=$scope.pre.apply.projectType;
			if(null!=pt && pt.length>0){
				for(var i=0;i<pt.length;i++){
					ptNameArr.push(pt[i].VALUE);
				}
				$scope.pre.apply.projectType=ptNameArr.join(",");
			}
			var pm=$scope.pre.apply.projectModel;
			if(null!=pm && pm.length>0){
				for(var j=0;j<pm.length;j++){
					pmNameArr.push(pm[j].VALUE);
				}
				$scope.pre.apply.projectModel=pmNameArr.join(",");
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
	$scope.getProjectPreReviewByID(params[0]);
}]);

ctmApp.register.controller('businessHeaderApprove', ['$http','$scope','$location','$routeParams', function ($http,$scope,$location,$routeParams) {
	//初始化
	var complexId = $routeParams.id;
	var params = complexId.split("@");
	$scope.findRelationUser(params[0]);
	if(null!=params[4] && ""!=params[4]){
		$scope.flag=params[4];
	}
	//提交流程弹出审批框
	$('#submitModal').on('show.bs.modal',function(e){
		$scope.httpData('common/commonMethod/getRoleuserByCode','5').success(function(data){
			if(data.result_code == 'S'){
				var ma = data.result_data[0];
				var investmentManager = $scope.pre.apply.investmentManager;
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
							runtimeVar:{inputUser:ma.VALUE,toTask:0},
							currentTaskVar:currentTaskVar,
							businessId:params[0],
							noticeInfo:{
								infoSubject:$scope.pre.apply.projectName+'预评审申请进入风控审批阶段，请悉知！',
								businessId:params[0],
								remark:'',
								formKey:'/ProjectPreReviewView/'+params[0]+'@'+params[1]+'@'+params[2],
								createBy:$scope.credentials.UUID,
								reader:$scope.removeObjByValue(removeDuplicate($scope.relationUsers),ma.VALUE),
								type:'1',
								custText01:'预评审'
							}
						},
						showInfo:{destination:'分配评审任务',approver: ma.NAME}
					},{
						submitInfo:{
							taskId:params[3],
							businessId:params[0],
							runtimeVar:{inputUser:investmentManager.value,toTask:1},
							currentTaskVar:{opinion:'请修改'}
						},
						showInfo:{destination:'退回起草人',approver:investmentManager.name}
					}];
				}else{
					$.alert("评审分配人不存在，请先设置评审分配人！");
				}
			}
		})
	});
	$scope.wfInfo = {processDefinitionId:params[1],processInstanceId:params[2]};

	$scope.getProjectPreReviewByID=function(id){
		var  url = 'projectPreReview/ProjectPreReview/getProjectPreReviewByID';
		$scope.httpData(url,id).success(function(data){
			var pthNameArr=[],ptNameArr=[],pmNameArr=[],fgNameArr=[],rlNameArr=[];
			$scope.pre  = data.result_data;
			/*var pth=$scope.pre.apply.companyHeader;
			if(null!=pth && pth.length>0){
				for(var i=0;i<pth.length;i++){
					pthNameArr.push(pth[i].name);
				}
				$scope.pre.apply.companyHeaderName=pthNameArr.join(",");
			}*/
			var pt1NameArr=[];
			var pt1=$scope.pre.apply.serviceType;
			if(null!=pt1 && pt1.length>0){
				for(var i=0;i<pt1.length;i++){
					pt1NameArr.push(pt1[i].VALUE);
				}
				$scope.pre.apply.serviceType=pt1NameArr.join(",");
			}
			var pt=$scope.pre.apply.projectType;
			if(null!=pt && pt.length>0){
				for(var i=0;i<pt.length;i++){
					ptNameArr.push(pt[i].VALUE);
				}
				$scope.pre.apply.projectType=ptNameArr.join(",");
			}
			var pm=$scope.pre.apply.projectModel;
			if(null!=pm && pm.length>0){
				for(var j=0;j<pm.length;j++){
					pmNameArr.push(pm[j].VALUE);
				}
				$scope.pre.apply.projectModel=pmNameArr.join(",");
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
	$scope.getProjectPreReviewByID(params[0]);
}]);

ctmApp.register.controller('ProjectPreReviewReadOnly', ['$timeout','$http','$scope','$location', function ($timeout,$http,$scope,$location) {
	$scope.orderby='desc';
	//获取当前年数组
	$scope.yearArrFunc = function(){
		
		$http({
			method:'post',  
		    url: srvUrl + "deptwork/getYearArr.do"
		}).success(function(result){
			if(result.success){
				$scope.yearArr = result.result_data.yearArr;
			}
		});
	}
	$scope.yearArrFunc();
	$scope.queryForPie = function (){
		var wf_state = "";
		var stage = "";
		$("span[name='stage'].label-danger").each(function(){
			wf_state += ","+$(this).context.attributes.data.value;
			stage += ","+$(this).context.attributes.value.value;
		});
		wf_state = wf_state.substring(1);
		stage = stage.substring(1);
		
		var areaId = "";
		$("span[name='area'].label-danger").each(function(){
			areaId += ","+$(this).context.attributes.value.value;
		});
		areaId = areaId.substring(1);
		
		var serviceTypeId = "";
		$("span[name='serviceType'].label-danger").each(function(){
			serviceTypeId += ","+$(this).context.attributes.value.value;
		});
		serviceTypeId = serviceTypeId.substring(1);
		
		$http({
			method:'post',  
		    url: srvUrl + "deptwork/queryPreCount.do",
		    data:$.param({"wf_state":wf_state,
		    	"stage":stage,
		    	"pertainAreaId":areaId,
		    	"serviceTypeId":serviceTypeId,
		    	"year":$scope.year
		    	})
		}).success(function(result){
			var FormalcountforPie = result.result_data.list;
			$scope.year = result.result_data.year+"";
		
			//准备数据
			var preData1 = new Array();
			var total1 = 0;
			for(var i in FormalcountforPie){
				var item = {};
				var dt = FormalcountforPie[i];
				if(dt.NAME !='' && dt.NAME != undefined){
					item.name=dt.NAME;
				}else{
					item.name="未知";
				}
				total1 += dt.VALUE;
				item.value = dt.VALUE;
				preData1.push(item);
			}
			$scope.total1 = total1;
			//模板
			$scope.pk1 = {
			    tooltip : {
			        trigger: 'item',
			        formatter: "{a} <br/>{b} : {c} ({d}%)"
			    },
			    series : [
	              {
	            	  name:'数量',
	                  type: 'pie',
	                  radius : '60%',
	                  center: ['50%', '50%'],
	                  data:preData1,
	                  itemStyle: {
	                      emphasis: {
	                          shadowBlur: 10,
	                          shadowOffsetX: 0,
	                          shadowColor: 'rgba(0, 0, 0, 0.5)'
	                      },
	                      normal:{ 
		                      label:{ 
		                         show: true, 
		                         formatter: '{b} : {c} (个)' 
		                      }, 
		                      labelLine :{show:true}
		                  } 
	                  }
	              }
	          ]
	        };
			var myChart = document.getElementById('review1');
			var myChart = echarts.init(myChart);
			myChart.setOption($scope.pk1);
		});
	}
	
	$scope.selectAllArea = function(event){
		if($(event.target)[0].checked){
			//全选
			$("span[name='area']").addClass("label-danger");
		}else{
			//全不选
			$("span[name='area']").removeClass("label-danger");
		}
	}
	$scope.selectAllStage = function(event){
		if($(event.target)[0].checked){
			//全选
			$("span[name='stage']").addClass("label-danger");
		}else{
			//全不选
			$("span[name='stage']").removeClass("label-danger");
		}
	}
	
	$scope.selectAllServiceType = function(event){
		if($(event.target)[0].checked){
			//全选
			$("span[name='serviceType']").addClass("label-danger");
		}else{
			//全不选
			$("span[name='serviceType']").removeClass("label-danger");
		}
	}
	
	$scope.aaa = function(event){
		if($(event.target).hasClass("label-danger")){
			$(event.target).removeClass("label-danger");  
		}else{
			$(event.target).addClass("label-danger");  
		}
	}
	
	//查询业务类型
	$scope.getServiceType = function(){
		$http({
			method:'post',  
		    url: srvUrl + "dict/queryDictItemByDictTypeCode.do",
		    params:{"code":"14"}
		}).success(function(result){
			$scope.serviceTypeList = result.result_data;
		});
	}
	$scope.getServiceType();
	$scope.serviceTypeFinfish = function(){
	}
	$scope.areaFinfish = function(){
		
		$timeout(function () {
			$scope.queryForPie();
		}, 100);
	}
	
	//查询大区
	$scope.getPertainArea = function(){
		$http({
			method:'post',  
		    url: srvUrl + "deptwork/queryPrePertainArea.do"
		}).success(function(result){
			$scope.pertainAreaList = result.result_data;
		});
	}
	$scope.getPertainArea();
	//查义所有的操作
	$scope.initData=function(){
		
		$http({
			method:'post',  
		    url: srvUrl + "deptwork/queryAllPreInfoByPage.do",
		    data: $.param({"page":JSON.stringify($scope.paginationConf)})
		}).success(function(result){
			$scope.projectPreReview = result.result_data.list;
			$scope.paginationConf.totalItems = result.result_data.totalItems;
		});
		
	};
	$scope.order=function(o,v){
		if(o=="time"){
			$scope.orderby=v;
			$scope.orderbystate=null;
			if(v=="asc"){
				$("#orderasc").addClass("cur");
				$("#orderdesc").removeClass("cur");
			}else{
				$("#orderdesc").addClass("cur");
				$("#orderasc").removeClass("cur");
			}
		}else{
			$scope.orderbystate=v;
			$scope.orderby=null;
			if(v=="asc"){
				$("#orderascstate").addClass("cur");
				$("#orderdescstate").removeClass("cur");
			}else{
				$("#orderdescstate").addClass("cur");
				$("#orderascstate").removeClass("cur");
			}
		}
		$scope.initData();
	}
	
	// 通过$watch currentPage和itemperPage 当他们一变化的时候，重新获取数据条目
	$scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage',  $scope.initData);
	//flag="5"代表是从部门工作台预评审申请列表进入工单详情
	$scope.flag="5";
	
	//初始化未过会表格
	$scope.initTableWgh = function(){
		$http({
			method: 'post',
			url: srvUrl + "deptwork/initTablePreWghByServietypeAndAreaWithNum.do"
		}).success(function(result){
			$scope.drawTableWgh(result.result_data);
		});
	};
    
	$scope.drawTableWgh = function(data){
		var serviceTypeList = data.serviceTypeList; //左
    	var areaList = data.areaList;//上
    	var dataList = data.dataList;//数据
    	var totalList =data.totalList;//统计
    	var html = '<tr >' ;
    	var trHtml = '<th>业务类型/区域</th>';
    	var oldUrl = window.btoa(encodeURIComponent(escape("#/ProjectPreReviewReadOnly")));
    	//表头
    	for (var i = 0; i < areaList.length; i++) {
			var name = areaList[i].name;
			trHtml += "<th><a href='#/preDeptWorkList/\"\"/"+areaList[i].value+"/3,3.5,3.7,3.9,4,5/"+oldUrl+"'>" + name + "</a></th>";
		}
    	trHtml +=  '<th> 总计 </th>' + '</tr >';
    	html += trHtml;
    	//列头  以及数据
    	for (var i = 0; i < serviceTypeList.length; i++) {
    		var tDHtml ='' ;
			var serviceType = serviceTypeList[i].name;
			tDHtml += "<tr> <td><a href='#/preDeptWorkList/"+serviceTypeList[i].value+"/\"\"/3,3.5,3.7,3.9,4,5/"+oldUrl+"'>" + serviceType + "</a></td>";
			//数据
			var dataHtml ='';
			var dataArr = dataList[i].data;
			var dataObject = dataList[i].data;
			var tatol = 0;
			for(var k = 0; k < areaList.length; k++) {
				var data = dataObject[areaList[k].value];
				if(data == undefined || data == null){
					data = 0;
				}
				tatol += data;
				dataHtml += ' <td>' + data + '</td>';
			}
			dataHtml += ' <td>' + tatol + '</td>';
			tDHtml +=dataHtml;
			tDHtml += '</tr>';
			html += tDHtml;
		}
    	
    	//统计数据
    	var totalHtml ='<tr> <td>总计</td>';
    	var numTotal = 0;
    	for (var i = 0; i < areaList.length; i++) {
			var area = areaList[i].name;
			//数据
			for (var j = 0; j < totalList.length; j++) {
				var ttt = totalList[j];
				if(area == ttt.NAME){
					totalHtml += ' <td>' + ttt.VALUE + '</td>';
					numTotal += ttt.VALUE;
				}
			}
			
		}
    	totalHtml += '<td>'+ numTotal +'</td></tr>';
    	html += totalHtml;
    	
    	$('#wghTable').append(html);
    };
    
    
    //初始化已过会表格
	$scope.initTableYgh = function(){
		$http({
			method: 'post',
			url: srvUrl + "deptwork/initTablePreYghByServietypeAndAreaWithNum.do"
		}).success(function(result){
			$scope.drawTableYgh(result.result_data);
		});
	};
    
	$scope.drawTableYgh = function(data){
		var serviceTypeList = data.serviceTypeList; //左
    	var areaList = data.areaList;//上
    	var dataList = data.dataList;//数据
    	var totalList =data.totalList;//统计
    	var html = '<tr >' ;
    	var trHtml = '<th>业务类型/区域</th>';
    	var oldUrl = window.btoa(encodeURIComponent(escape("#/ProjectPreReviewReadOnly")));
    	//表头
    	for (var i = 0; i < areaList.length; i++) {
    		var name = areaList[i].name;
    		trHtml += "<th><a href='#/preDeptWorkList/\"\"/"+areaList[i].value+"/6,7,9/"+oldUrl+"'>" + name + "</a></th>";
    	}
    	trHtml +=  '<th> 总计 </th>' + '</tr >';
    	html += trHtml;
    	//列头  以及数据
    	for (var i = 0; i < serviceTypeList.length; i++) {
    		var tDHtml ='' ;
			var serviceType = serviceTypeList[i].name;
			tDHtml += "<tr> <td><a href='#/preDeptWorkList/"+serviceTypeList[i].value+"/\"\"/6,7,9/"+oldUrl+"'>" + serviceType + "</a></td>";
			//数据
			var dataHtml ='';
			var dataArr = dataList[i].data;
			var dataObject = dataList[i].data;
			var tatol = 0;
			for(var k = 0; k < areaList.length; k++) {
				var data = dataObject[areaList[k].value];
				if(data == undefined || data == null){
					data = 0;
				}
				tatol += data;
				dataHtml += ' <td>' + data + '</td>';
			}
			dataHtml += ' <td>' + tatol + '</td>';
			tDHtml +=dataHtml;
			tDHtml += '</tr>';
			html += tDHtml;
		}
    	
    	//统计数据
    	var totalHtml ='<tr> <td>总计</td>';
    	var numTotal = 0;
    	for (var i = 0; i < areaList.length; i++) {
			var area = areaList[i].name;
			//数据
			for (var j = 0; j < totalList.length; j++) {
				var ttt = totalList[j];
				if(area == ttt.NAME){
					totalHtml += ' <td>' + ttt.VALUE + '</td>';
					numTotal += ttt.VALUE;
				}
			}
			
		}
    	totalHtml += '<td>'+ numTotal +'</td></tr>';
    	html += totalHtml;
    	
    	
    	$('#yghTable').append(html);
    };
    
    
    //类型统计图
    //跟进中
    $scope.initEChartsPieLX = function(){
		$('a[data-toggle="tab"]').on('shown.lx.tab', function (e) {
			// 获取已激活的标签页的名称
			var activeTab = $(e.target)[0].hash;
			if(activeTab=="#lx-tabdrop-tab1") {
//				myChart = echarts.init(document.getElementById('reviewLX1'),'shine');
				myChart.setOption($scope.WghLX1);
			}
		});
		$scope.initEchartsPieLXData1();
	};
	
	$scope.initEchartsPieLXData1 = function(){
	};
	

    
    
    //已过会
    $scope.initEChartsPieLXYgh = function(){
		$('a[data-toggle="tab"]').on('shown.yghlx.tab', function (e) {
			// 获取已激活的标签页的名称
			var activeTab = $(e.target)[0].hash;
			if(activeTab=="#yghlx-tabdrop-tab1") {
				myChart.setOption($scope.YghLX1);
			}
		});
		$scope.initEchartsPieLXYghData1();
	};
	
	$scope.initEchartsPieLXYghData1 = function(){
	};
	

    
    
    //导出投标评审项目列表
    $scope.exportFormaReportInfo = function(){
    	$http({
    		method:'post',
    		url:srvUrl+'reviewStatisticsform/exportFormaReportInfo.do'
    	}).success(function(data){
    		if(data.success){
    			var files = data.result_data.filePath;
    			var index = files.lastIndexOf(".");
                var str = files.substring(index + 1, files.length);
                var fileName = data.result_data.fileName;
                var result = data.result_data.result;
    			window.location.href = srvUrl+"file/downloadFile.do?filepaths="+encodeURI(files)+"&filenames="+encodeURI(encodeURI("正式评审-"+""+"报告."))+str;
    		}else{
    			$.alert("文档生成失败!");
    		}
    	}).error(function(data,status,headers,config){
			$.alert(status);
		});
    }
    
    
    $scope.qiehuanNoCompleted = function(){
    	if($("#noCompleted").is(':hidden')){  
            $("#completed").hide(); 
            $("#noCompleted").show(); 
        }
    }
    $scope.qiehuanCompleted = function(){
    	if($("#completed").is(':hidden')){  
    		$("#noCompleted").hide(); 
            $("#completed").show(); 
        }
    }
    
    $scope.qiehuannoCompleted1 = function(){
    	if($("#noCompleted1").is(':hidden')){  
            $("#completed1").hide(); 
            $("#noCompleted1").show(); 
        }
    }
    $scope.qiehuanCompleted1 = function(){
    	if($("#completed1").is(':hidden')){  
    		$("#noCompleted1").hide(); 
            $("#completed1").show(); 
        }
    }
    $scope.qiehuanOne = function(){
    	if($("#one").is(':hidden')){  
    		$("#one").hide(); 
        }
    }
    $scope.hideDiv = function(){
    	 $("#completed").hide(); 
    	 $("#completed1").hide(); 
    }
   
    
    //初始化未过会表格
    $scope.initTableWgh();
    $scope.initTableYgh();
    $scope.initEChartsPieLX();
    $scope.initEChartsPieLXYgh();
    $scope.hideDiv();
    
	$scope.rounding = function keepTwoDecimalFull(num) {
		  var result = parseFloat(num);
		  if (isNaN(result)) {
		    alert('传递参数错误，请检查！');
		    return false;
		  }
		  result = Math.round(num * 10000) / 10000;
		  var s_x = result.toString();
		  var pos_decimal = s_x.indexOf('.');
		  if (pos_decimal < 0) {
		    pos_decimal = s_x.length;
		    s_x += '.';
		  }
		  while (s_x.length <= pos_decimal + 2) {
		    s_x += '0';
		  }
		  return s_x;
		}

	//导出未过会投标评审项目列表
    $scope.exportTenderReportInfoWgh = function(){
    	$http({
    		method:'post',
    		url:srvUrl+'reviewStatisticsform/exportTenderReportInfoWgh.do'
    	}).success(function(data){
    		if(data.success){
    			var files = data.result_data.filePath;
    			var index = files.lastIndexOf(".");
                var str = files.substring(index + 1, files.length);
                var fileName = data.result_data.fileName;
                var result = data.result_data.result;
    			window.location.href = srvUrl+"file/downloadFile.do?filepaths="+encodeURI(files)+"&filenames="+encodeURI(encodeURI("投标评审-"+""+"报告."))+str;
    		}else{
    			$.alert("文档生成失败!");
    		}
    	}).error(function(data,status,headers,config){
			$.alert(status);
		});
    }
	
	//导出已过会投标评审项目列表
    $scope.exportTenderReportInfo = function(){
    	$http({
    		method:'post',
    		url:srvUrl+'reviewStatisticsform/exportTenderReportInfo.do'
    	}).success(function(data){
    		if(data.success){
    			var files = data.result_data.filePath;
    			var index = files.lastIndexOf(".");
                var str = files.substring(index + 1, files.length);
                var fileName = data.result_data.fileName;
                var result = data.result_data.result;
    			window.location.href = srvUrl+"file/downloadFile.do?filepaths="+encodeURI(files)+"&filenames="+encodeURI(encodeURI("投标评审-"+""+"报告."))+str;
    		}else{
    			$.alert("文档生成失败!");
    		}
    	}).error(function(data,status,headers,config){
			$.alert(status);
		});
    }
	
}]);
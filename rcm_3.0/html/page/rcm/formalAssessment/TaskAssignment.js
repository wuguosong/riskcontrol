ctmApp.register.controller('TaskAssignment', ['$http','$scope','$location','$routeParams', function ($http,$scope,$location,$routeParams) {

    $scope.dic=[];
    $scope.pfr={};
    $scope.pfr.apply = {};
    $scope.pfr.taskallocation={};
    $scope.reviewLeaderModel = '{"NAME":"","VALUE":""}';
    $scope.legalLeaderModel = '{"NAME":"","VALUE":""}';
    $scope.pfr = {taskallocation:{reviewLeader:{},legalReviewLeader:{}}};
    function FormatDate() {
        var date = new Date();
        var paddNum = function(num){
            num += "";
            return num.replace(/^(\d)$/,"0$1");
        }
        return date.getFullYear()+""+paddNum(date.getMonth()+1)+""+paddNum(date.getDate());
    }
    function FormatDateto() {
        var date = new Date();
        var paddNum = function(num){
            num += "";
            return num.replace(/^(\d)$/,"0$1");
        }
        return date.getFullYear()+"-"+paddNum(date.getMonth()+1)+"-"+paddNum(date.getDate());
    }
    //$scope.taskal=[{name:'小组1',value:[{value:1,name:'高'},{value:2,name:'李'},{value:3,name:'郑'}]},{name:'小组2',value:[{value:4,name:'王'},{value:5,name:'杨'},{value:6,name:'余'}]} ];
    $scope.saveReviewLeader=function(openPopWin){
    	$scope.reviewLeaderModel = $("#reviewLeadername option:selected").val();
    	$scope.legalLeaderModel = $("#legalReviewLeader option:selected").val();
   	 	var jsonLeader = eval("("+$scope.reviewLeaderModel+")");
   	 	$scope.pfr.taskallocation.reviewLeader=jsonLeader;
   	 	var jsonLegalLeader = eval("("+$scope.legalLeaderModel+")");
   	 	$scope.pfr.taskallocation.legalReviewLeader=jsonLegalLeader;
   	 	/*
   	 	 固定小组成员可以不选
        var fixedGroup = $scope.pfr.taskallocation.fixedGroup;
        if( $scope.pfr.taskallocation.reviewLeader!=null && $scope.pfr.taskallocation.reviewLeader.VALUE!=""
        	&&(fixedGroup==null||fixedGroup=="")){
	        	$.alert("请选择评审小组固定成员!");
	        	return false;
        }
        */
        var  url = 'formalAssessment/ProjectFormalReview/saveProjectFormalReviewByID';
//        $scope.pfr.taskallocation.dataCompleteTime=FormatDateto();
        $scope.httpData(url,$scope.pfr).success(function(data){
            if(data.result_code === 'S'){
                openPopWin();
            }else{
            	$.alert("保存任务分配人失败");
            }
        });
    }
    $scope.getCommonRoleuser=function(){
        var  url = 'common/commonMethod/getRoleuser';
        var id="65fc76c0-829a-4670-bffa-bbcee8b70c53";
        $scope.httpData(url,id).success(function(data){

            $scope.userRole  = data.result_data;
        });
    }
    $scope.getProjectFormalReviewByID=function(id){
        var  url = 'formalAssessment/ProjectFormalReview/getProjectFormalReviewByID';
        $scope.httpData(url,id).success(function(data){
            var ptNameArr=[],pthNameArr=[],pmNameArr=[],pmValueArr=[],fgNameArr=[],fgValueArr=[];
            $scope.pfr  = data.result_data;
            if($scope.pfr.taskallocation == null){
            	$scope.pfr.taskallocation = {};
            }else {
            	if($scope.pfr.taskallocation.reviewLeader!=null && $scope.pfr.taskallocation.reviewLeader.NAME!=null){
            		$scope.reviewLeaderModel=JSON.stringify($scope.pfr.taskallocation.reviewLeader);
            	}
            	if($scope.pfr.taskallocation.legalReviewLeader!=null && $scope.pfr.taskallocation.legalReviewLeader.NAME!=null){
            		$scope.legalLeaderModel=JSON.stringify($scope.pfr.taskallocation.legalReviewLeader);
            	}
            }
            var pt1NameArr=[];
            var pt1=$scope.pfr.apply.serviceType;
            if(null!=pt1 && pt1.length>0){
                for(var i=0;i<pt1.length;i++){
                    pt1NameArr.push(pt1[i].VALUE);
                }
                $scope.pfr.apply.serviceType=pt1NameArr.join(",");
            }
            var reviewLeader=$scope.pfr.taskallocation;
            if(reviewLeader!=null){
                $scope.pfr.taskallocation.reviewLeader=JSON.stringify($scope.pfr.taskallocation.reviewLeader);
                $scope.pfr.taskallocation.legalReviewLeader=JSON.stringify($scope.pfr.taskallocation.legalReviewLeader);
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
            var fg=$scope.pfr.taskallocation.fixedGroup;

            if(null!=fg && fg.length>0){
                for(var k=0;k<fg.length;k++){
                    fgNameArr.push(fg[k].NAME);
                    fgValueArr.push(fg[k].UUID);
                }
                commonModelValue2(fgValueArr,fgNameArr);
            }
            $scope.getNoticeOfDecstionByProjectFormalID($scope.pfr.apply.projectNo);
            $scope.getProjectPreReviewYJDWByID(params[0]);
        });
    }
    $scope.getSelectTypeByCode=function(typeCode){
        var  url = 'common/commonMethod/getRoleuser';
        $scope.httpData(url,typeCode).success(function(data){
            if(data.result_code === 'S'){
                $scope.dic=[];
                $scope.dic.userRoleListall=data.result_data.userRoleList;
            }else{
                alert(data.result_name);
            }
        });
    }
    $scope.getCommonteam=function(){
        var  url = 'common/commonMethod/getTeamList';
        $scope.httpData(url,{}).success(function(data){
            $scope.taskal= data.result_data;
        });
    }
    $scope.getCommonteamflps=function(){
        var  url = 'common/commonMethod/getTeamListFLPS';
        $scope.httpData(url,{}).success(function(data){
            $scope.taskalflps= data.result_data;
        });
    }

    //初始化
    var complexId = $routeParams.id;
    var params = complexId.split("@");
    if(null!=params[4] && ""!=params[4]){
        $scope.flag=params[4];
    }
    $scope.getProjectFormalReviewByID(params[0]);
    $scope.getCommonteam();
    $scope.getCommonteamflps();
    $scope.getCommonRoleuser();
    var commonModelValue2=function(arrID,arrName){
        var leftstr2="<li class=\"select2-search-choice\"><div>";
        var centerstr2="</div><a class=\"select2-search-choice-close\" tabindex=\"-1\" onclick=\"delSelectmember(this,'";
        var addID2="');\"  ></a><div class=\"full-drop\"><input type=\"hidden\" id=\"\"  value=\"";
        var rightstr2="\"></div></li>";
        for(var i=0;i<arrName.length;i++){
            $("#headerfixed-member-box").find(".select2-search-field").before(leftstr2+arrName[i]+centerstr2+arrName[i]+"','"+arrID[i]+addID2+arrID[i]+rightstr2);
        }
    }
    angular.element(document).ready(function() {
        $scope.getSelectTypeByCode("65fc76c0-829a-4670-bffa-bbcee8b70c53");
    });
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
                    if(null!=$scope.pfr.tzProtocolOpinion && ''!=$scope.pfr.tzProtocolOpinion){
                        $scope.yjdw = $scope.yjdw+" ；<br/>项目投资协议意见：" + $scope.pfr.tzProtocolOpinion;
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

    //提交流程
    $scope.showSubmitModal=function(){
        $scope.saveReviewLeader(function(){
        	var currentUserName = $scope.credentials.userName;
        	var approveUser = $scope.evalJsonStr($scope.reviewLeaderModel);
        	var legalReviewLeader = $scope.evalJsonStr($scope.legalLeaderModel);
            var investmentManager = $scope.pfr.apply.investmentManager;//投资经理
            //查询评审组长
            $scope.httpData("rcm/Pteam/getTeamLeaderByMemberId",{teamMemberId:approveUser.VALUE,type:'1'}).success(function(data){
                var readerArray = [];
                if(data.result_code=='S'){
                    readerArray.push(data.result_data);
                    //查询法律组长
                    $scope.httpData("rcm/Pteam/getTeamLeaderByMemberId",{teamMemberId:legalReviewLeader.VALUE,type:'2'}).success(function(data){
                        if(data.result_code=='S'){
                            readerArray.push(data.result_data);
                            $scope.approve = {"isAllocateTask":true};
                            //判断是否为新流程
                            var task = null;
                      	  	var url=srvUrl+"bpmn/queryTaskById.do";
                      		var reqparams = {
                      			taskId: params[3]
                      		};
                      		$.ajax({
                      		  url: url,
                      	    	type: "POST",
                      	    	data: reqparams,
                      	    	dataType: "json",
                      	    	async: false,
                      	    	success: function(data){
                      	    		if(data.success){
                      	    			task = data.result_data;
                      	            }else{ 
                      	                $.alert(data.result_name);
                      	            }
                      	    	}
                      	    });
                        	if(task != null && task.description == "isnew"){
                        		//新流程
//                        		$scope.isNewBpmn = false;
                        		$scope.approve.approve=[];
                        		for(var m = 0; m < task.outSequences.length; m++){
                        			var sequenceFlow = new Object();
                        			var flow = task.outSequences[m];
                        			var document = flow.documentation;
                        			var infos = document.split(",");
                        			var destination = infos[0];
                        			sequenceFlow.submitInfo = {
                        				taskId: params[3],
                        				businessId:params[0]
                        			};
                        			sequenceFlow.submitInfo.runtimeVar = {};
                        			if(infos.length == 1){
                        				sequenceFlow.toNodeType = "end";
                        				sequenceFlow.submitInfo.runtimeVar= {"sequenceFlow":flow.id};
                        			}else{
                        				var users = null;
                        				var auditUsers = infos[1].split(";");
                        				var psfzr = "formalAudit.queryPsFuzeren:inputUser";
                        				var flfzr = "formalAudit.queryLeagueFuzeren:legalReviewLeader";
                        				var tzjl = "formalAudit.queryTzManager:inputUser";
                        				var showUserNameList = "";
                        				for(var h = 0; h < auditUsers.length; h++){
                        					if(psfzr == auditUsers[h]){
                        						sequenceFlow.submitInfo.runtimeVar.inputUser=approveUser.VALUE;
                        					}else if(flfzr == auditUsers[h]){
                        						sequenceFlow.submitInfo.runtimeVar.legalReviewLeader=legalReviewLeader.VALUE;
                        					}else if(tzjl == auditUsers[h]){
                        						sequenceFlow.submitInfo.runtimeVar.inputUser=investmentManager.value;
                        					}else{
                        						$.alert("出错了！");
                        						return;
                        					}
                        				}
                        				if(auditUsers.length==1){
                        					showUserNameList = investmentManager.name;
                        				}else if(auditUsers.length==2){
                        					showUserNameList = approveUser.NAME+'/'+legalReviewLeader.NAME;
                        					sequenceFlow.submitInfo.noticeInfo = {
                                                infoSubject:$scope.pfr.apply.projectName+'正式评审任务分配给了'+legalReviewLeader.NAME+'和'+approveUser.NAME+'，请悉知！',
                                                businessId:params[0],
                                                remark:'',
                                                formKey:'/ProjectFormalReviewDetailView/View'+params[0]+'@'+params[1]+'@'+params[2],
                                                createBy:$scope.credentials.UUID,
                                                reader:removeDuplicate(readerArray),
                                                type:'1',
                                                custText01:'正式评审'
                                            }
                        				}
                        				sequenceFlow.showInfo={
                        					"destination":destination,
                        					"approver": showUserNameList
                        				};
                        				sequenceFlow.submitInfo.currentTaskVar={opinion:'请审批'};
                        				sequenceFlow.submitInfo.runtimeVar.sequenceFlow=flow.id;
                        			}
                        			$scope.approve.approve.push(sequenceFlow);
                        		}
                        	}else{
                        		$scope.approve.approve = [{
                                    submitInfo:{
                                        taskId:params[3],
                                        businessId:params[0],
                                        runtimeVar:{inputUser:approveUser.VALUE,legalReviewLeader:legalReviewLeader.VALUE,toTask:'1'},
                                        currentTaskVar:{opinion:'请审批'},
                                        noticeInfo:{
                                            infoSubject:$scope.pfr.apply.projectName+'正式评审任务分配给了'+legalReviewLeader.NAME+'和'+approveUser.NAME+'，请悉知！',
                                            businessId:params[0],
                                            remark:'',
                                            formKey:'/ProjectFormalReviewDetailView/View'+params[0]+'@'+params[1]+'@'+params[2],
                                            createBy:$scope.credentials.UUID,
                                            reader:removeDuplicate(readerArray),
                                            type:'1',
                                            custText01:'正式评审'
                                        }
                                    },
                                    showInfo:{destination:'评审负责人及法律负责人审批',approver:approveUser.NAME+'/'+legalReviewLeader.NAME}
                                },{
                                    submitInfo:{
                                        taskId:params[3],
                                        businessId:params[0],
                                        runtimeVar:{inputUser:investmentManager.value,toTask:"2"},
                                        currentTaskVar:{opinion:'请修改'}
                                    },
                                    showInfo:{destination:'退回起草人',approver:investmentManager.name}
                                },{
                                    submitInfo:{
                                        taskId:params[3],
                                        businessId:params[0],
                                        runtimeVar:{toTask:'0'}
                                    },
                                    toNodeType:'end'
                                }];
                        	}
                            $('#submitModal').modal('show');

                        }else{
                            $.alert('查询法律评审组组长失败');
                        }
                    });
                }else{
                    $.alert('查询评审组组长失败');
                }
            });
        });
    }
    $scope.wfInfo = {processDefinitionId:params[1],processInstanceId:params[2]};
}]);
function delSelectmember(o,nameval,id){
    $(o).parent().remove();
    accessScope("#fixedGroupName", function(scope){
        var names=scope.pfr.taskallocation.fixedGroup;
        names.splice(id,1);
        if(names.length>0){
            scope.pfr.taskallocation.fixedGroup=names;
        }else{
            scope.pfr.taskallocation.fixedGroup=null;
        }
    });
}

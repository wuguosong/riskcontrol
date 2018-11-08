ctmApp.register.controller('MeetingApply', ['$http','$scope','$location','$routeParams', function ($http,$scope,$location,$routeParams) {
    //初始化
	var rootParamsIds = $routeParams.id.split("_");
	var formalIds = null, tbsxIds = null;
	//正式评审
	formalIds = rootParamsIds[0];
	//通报事项
	tbsxIds = rootParamsIds[1];
	
	var userDomId = null;
	
	$scope.initData = function(){
		if(formalIds != null && formalIds != ""){
			$scope.initFormalData();
		}
		if(tbsxIds != null && tbsxIds != ""){
			$scope.initTbsxData();
		}
	};
	
	$scope.initFormalData = function(){
		 $scope.getMeetingByID(formalIds);
		 $scope.getMeetingByIDMeetingtime(formalIds);
	};
	
	$scope.initTbsxData = function(){
		$http({
			method:'post',  
		    url:srvUrl+"meeting/queryTbsxSelected.do",
		    data: $.param({"tbsxIds": tbsxIds})
		}).success(function(result){
			$scope.tbsxs = result.result_data;
		});
	};
	//通报事项的会议信息
	$scope.tbsxMeetings = [];
	
    var objId =  formalIds;
    $scope.id=objId;
    $scope.columnName="";
    $scope.columnNum="";
    $scope.columnIndex="";
    $scope.dic2=[];
    $scope.nameValue=[{name:'A级（困难评审)',value:1},{name:'B级（中级评审)',value:2},{name:'C级（简单评审)',value:3}];
    $scope.setDirectiveParam=function(columnName,columnNum,columnIndex){
        $scope.columnName=columnName;
        $scope.columnNum=columnNum;
        $scope.columnIndex=columnIndex;
    }
    
    $scope.showSelectUser = function(domId){
    	$("#company-modal").modal('show');
    	$scope.setDirectiveParam(null, null, null);
    	userDomId = domId;
    };
    
    function FormatDate() {
        var date = new Date();
        var paddNum = function(num){
            num += "";
            return num.replace(/^(\d)$/,"0$1");
        }
        return date.getFullYear()+""+paddNum(date.getMonth()+1)+""+paddNum(date.getDate())+"-"+paddNum(date.getHours())+""+paddNum(date.getMinutes())+""+paddNum(date.getSeconds());
    }
    $scope.ArrOnemeeting={};
    $scope.ArrOnemeeting.children=[];
    $scope.getMeetingByID=function(id){
        var  url = 'projectPreReview/Meeting/getMeetingByID';
        $scope.httpData(url,id).success(function(data){
            $scope.ArrOnemeeting.children = data.result_data;
            var projectNumber="T-"+FormatDate();
            $scope.ArrOnemeeting.noticeId=projectNumber;
            $scope.ArrOnemeeting._id=id;
        }).error(function (data, status, headers, config) {
            alert(status);
        });
    };
    $scope.getMeetingByIDMeetingtime=function(id){
        var  url = 'projectPreReview/Meeting/getMeetingByIDMeetingtime';
        $scope.httpData(url,id).success(function(data){
            $scope.ArrmeetingTime = data.result_data;
        }).error(function (data, status, headers, config) {
            alert(status);
        });
    };
    $scope.setDirectiveUserList=function(arrID,arrName,arrUserNamesValue){
    	if($scope.columnName != null){
    		var paramsVal=$scope.columnName;
            var paramsNum= $scope.columnNum;
            if(paramsVal=="investment"){
                $scope.ArrOnemeeting.children[paramsNum].investment =arrUserNamesValue;  //赋值保存到数据库

            }else if(paramsVal=="division"){
                $scope.ArrOnemeeting.children[paramsNum].division =arrUserNamesValue;  //赋值保存到数据库
            }
            commonModelValue(paramsVal,arrID,arrName,$scope.columnNum);
    	}else{
    		var index = userDomId.split("_")[1];
    		if($scope.tbsxMeetings[index].meeting != null && Array.isArray($scope.tbsxMeetings[index].meeting.otherPerson)){
    			for(var i = 0; i < arrUserNamesValue.length; i++){
    				if(arrayContains($scope.tbsxMeetings[index].meeting.otherPerson,{"NAME":arrUserNamesValue[i].name,"VALUE":arrUserNamesValue[i].value},"VALUE")){
    					continue;
    				}
    				$scope.tbsxMeetings[index].meeting.otherPerson.push({"NAME":arrUserNamesValue[i].name, "VALUE":arrUserNamesValue[i].value});
        		}
    		}else{
    			$scope.tbsxMeetings[index].meeting = {otherPerson: []};
    			for(var i = 0; i < arrUserNamesValue.length; i++){
    				$scope.tbsxMeetings[index].meeting.otherPerson.push({"NAME":arrUserNamesValue[i].name, "VALUE":arrUserNamesValue[i].value});
        		}
    		}
    	}
    }
    $scope.delSelectedUser = function($event, outIndex, userId){
    	$event.stopPropagation();
    	var uid = userId;
    	var index = outIndex;
//    	var appElement = document.querySelector('[ng-controller=SysControl]');
//    	var $scope = angular.element(appElement).scope(); 
//    	var $scope = $(event.target).scope();
    	for(var i = 0; i < $scope.tbsxMeetings[index].meeting.otherPerson.length; i++){
    		if($scope.tbsxMeetings[index].meeting.otherPerson[i].VALUE == uid){
    			$scope.tbsxMeetings[index].meeting.otherPerson.splice(i, 1);
    		    break;
    		}
    	}
    }
    $scope.setDirectiveRadioUserList=function(arrID,arrName) {
        var paramsVal=$scope.columnName;
        var paramsNum=$scope.columnNum;
        var paramsIndex=$scope.columnIndex;
        if(paramsVal=="contacts") {
            $scope.ArrOnemeeting.children[paramsNum].contacts = {name: arrName, value: arrID};
        }else if(paramsVal=="agendaName"){
            $scope.ArrOnemeeting.children[paramsNum].agenda[paramsIndex].agendaName={name:arrName,value:arrID};
        }
    }
    var commonModelValue=function(paramsVal,arrID,arrName,k){
        $("#header"+paramsVal+"Name"+k).find(".select2-choices").html("<li class=\"select2-search-field\"><input id=\"s2id_autogen2\" class=\"select2-input\" type=\"text\" spellcheck=\"false\" autocapitalize=\"off\" autocorrect=\"off\" autocomplete=\"off\" style=\"width: 16px;\"> </li>");
        var leftstr="<li class=\"select2-search-choice\"><div>";
        var centerstr="</div><a class=\"select2-search-choice-close\" tabindex=\"-1\" onclick=\"delSelect(this,'";
        var addID="');\"  ></a><div class=\"full-drop\"><input type=\"hidden\" id=\"\"  value=\"";
        var rightstr="\"></div></li>";
        for(var i=0;i<arrName.length;i++){
            $("#header"+paramsVal+"Name"+k).find(".select2-search-field").before(leftstr+arrName[i]+centerstr+paramsVal+"','"+arrID[i]+"','"+k+addID+arrID[i]+rightstr);
        }
    }
    $scope.$on('ngRepeatFinished', function () {
        var options = {
            minuteStep: 5,
            orientation: $('body').hasClass('right-to-left') ? { x: 'right', y: 'auto'} : { x: 'auto', y: 'auto'}
        }
        $('.starttime').timepicker(options);
        $('.endTime').timepicker(options);
        if(null!= $scope.ArrOnemeeting.children){
            var arrList=$scope.ArrOnemeeting.children;
            for(var k=0;k<arrList.length;k++) {
                var arrName=[],arrID=[];
                var haderNameArr=[],haderValueArr=[],dNameArr=[],dValueArr=[];
                if(null!=$scope.ArrOnemeeting.children[k].investment){
                    var header=$scope.ArrOnemeeting.children[k].investment;
                    if(null!=header && header.length>0){
                        for(var i=0;i<header.length;i++){
                            haderNameArr.push(header[i].name);
                            haderValueArr.push(header[i].value);
                        }
                        commonModelValue("investment",haderValueArr,haderNameArr,k);
                    }
                }
                if(null!=$scope.ArrOnemeeting.children[k].division){
                    var header2=$scope.ArrOnemeeting.children[k].division;
                    if(null!=header2 && header2.length>0){
                        for(var i=0;i<header2.length;i++){
                            dNameArr.push(header2[i].name);
                            dValueArr.push(header2[i].value);
                        }
                        commonModelValue("division",dValueArr,dNameArr,k);
                    }
                }
                if (null != $scope.ArrOnemeeting.children[k].decisionMakingCommitteeStaff) {
                    var paramsVal = "decisionMakingCommitteeStaff";
                    var pm=$scope.ArrOnemeeting.children[k].decisionMakingCommitteeStaff;
                    if(null!=pm && pm.length>0){
                        for(var j=0;j<pm.length;j++){
                            arrName.push(pm[j].NAME);
                            arrID.push(pm[j].VALUE);
                        }
                        var leftstr2="<li class=\"select2-search-choice\"><div>";
                        var centerstr2="</div><a class=\"select2-search-choice-close\" tabindex=\"-1\" onclick=\"delSelect(this,'";
                        var addID2="');\"  ></a><div class=\"full-drop\"><input type=\"hidden\" id=\"\"  value=\"";
                        var rightstr2="\"></div></li>";
                        for(var i=0;i<arrName.length;i++){
                            $(".header"+paramsVal+k).find(".select2-search-field").before(leftstr2+arrName[i]+centerstr2+paramsVal+"','"+arrID[i]+"','"+k+addID2+arrID[i]+rightstr2);
                        }
                    }
                }
            }
        }
        $("#fixed-member-box select").select2();
    });
    function newDate() {
        var date = new Date();
        var paddNum = function(num){
            num += "";
            return num.replace(/^(\d)$/,"0$1");
        }
        return date.getFullYear()+"-"+paddNum(date.getMonth()+1)+"-"+paddNum(date.getDate());
    }
    //获取最大会议期次编号
    $scope.getMaxMeetingIssue = function () {
        var postObj = $scope.httpData('meetingIssue/getMaxMeetingIssue.do');
        postObj.success(function (data) {
        	if (data.success) {
        		$scope.ArrOnemeeting.meetingIssueNumber = data.result_data.meetingIssueNumber;
                $("#meetingIssueText").html(data.result_data.meetingIssue);
        		$scope.ArrOnemeeting.meetingIssueFormat = data.result_data.meetingIssueFormat;
            }else{
            	alert(data.result_name);
            }
        });
    }
    $scope.changeMeetingIssue = function(){
        var meetingIssue = $scope.ArrOnemeeting.meetingIssueFormat.replace("%s",$scope.ArrOnemeeting.meetingIssueNumber);
        $("#meetingIssueText").html(meetingIssue);
    }
    $scope.save= function () {
    	var meetingTime = $scope.ArrOnemeeting.meetingTime;
		if(null==meetingTime || ""==meetingTime){
			$.alert("上会时间必填！");
			return false;
		}
		if($scope.ArrOnemeeting.decisionMakingCommitteeStaffNum == null || $scope.ArrOnemeeting.decisionMakingCommitteeStaffNum == ""){
    		$.alert("会议类型必填!");
    		return false;
    	}
    	if($scope.ArrOnemeeting.decisionMakingCommitteeStaff == null || $scope.ArrOnemeeting.decisionMakingCommitteeStaff.length == 0){
    		$.alert("决策委员会委员必填!");
    		return false;
    	}
    	if($scope.ArrOnemeeting.meetingIssueNumber == null || $scope.ArrOnemeeting.meetingIssueNumber == ""){
    		$.alert("会议期次必填!");
    		return false;
    	}
    	var meetingIssueNumber = parseInt($scope.ArrOnemeeting.meetingIssueNumber);
    	if (isNaN(meetingIssueNumber))
    	{
    		$.alert("会议期次必填或只能填写数字!");
    		return false;
    	}
    	$scope.ArrOnemeeting.meetingIssueNumber = meetingIssueNumber;
    	var meetingTime = $scope.ArrOnemeeting.meetingTime;
		if(null==meetingTime || ""==meetingTime){
			$.alert("上会时间必填！");
			return false;
		}
		if($scope.ArrOnemeeting.decisionMakingCommitteeStaffNum == null || $scope.ArrOnemeeting.decisionMakingCommitteeStaffNum == ""){
    		$.alert("会议类型必填!");
    		return false;
    	}
    	if($scope.ArrOnemeeting.decisionMakingCommitteeStaff == null || $scope.ArrOnemeeting.decisionMakingCommitteeStaff.length == 0){
    		$.alert("决策委员会委员必填!");
    		return false;
    	}
    	var jueCeHuiYiZhuXi = $("select[name='jueCeHuiYiZhuXi']");
    	if(jueCeHuiYiZhuXi == null || jueCeHuiYiZhuXi.length == 0 || jueCeHuiYiZhuXi.val() == ""){
    		$.alert("决策会议主席必填!");
    		return false;
    	}
    	if($scope.ArrOnemeeting.huiYiQiCi == null || $scope.ArrOnemeeting.huiYiQiCi == ""){
    		$.alert("会议期次必填!");
    		return false;
    	}
        if ($("#Meeting_Apply").valid()) {
        	//------------------------------------------------------
        	//验证委员中是否有决策会议主席(20170621) start
        	//------------------------------------------------------
        	var url = 'meetingIssue/isIncludeChairman.do';
            postObj = $scope.httpData(url,$.param({"meetingLeaders": JSON.stringify($scope.ArrOnemeeting.decisionMakingCommitteeStaff)}));
            postObj.success(function (data) {
            	if (data.success) {
            		$scope.addMeeting();
                }else{
                	alert(data.result_name);
                }
            });
        	//------------------------------------------------------
        	//验证委员中是否有决策会议主席(20170621) end
        	//------------------------------------------------------
        	$scope.ArrOnemeeting.jueCeHuiYiZhuXiId = jueCeHuiYiZhuXi.val();
        	for(var i = 0; i < $scope.tbsxMeetings.length; i++){
        		$scope.tbsxMeetings[i].meeting = {
    				meetingLeadersNum: $scope.ArrOnemeeting.decisionMakingCommitteeStaffNum,
        			jueCeHuiYiZhuXiId: $scope.ArrOnemeeting.jueCeHuiYiZhuXiId,
        			huiYiQiCi: $scope.ArrOnemeeting.huiYiQiCi,
        			meetingLeaders: $scope.ArrOnemeeting.decisionMakingCommitteeStaff,
         			meetingTime: $scope.ArrOnemeeting.meetingTime,
        			startTime: $scope.tbsxs[i].startTime,
        			endTime: $scope.tbsxs[i].endTime,
        			openMeetingPerson:{NAME:$scope.credentials.userName, VALUE:$scope.credentials.UUID},
        			otherPerson: $scope.tbsxMeetings[i].meeting==null||
        				$scope.tbsxMeetings[i].meeting.otherPerson==null?[]
        				:$scope.tbsxMeetings[i].meeting.otherPerson
        		};
        	}
        	if($scope.tbsxMeetings.length > 0){
        		//如果有通报事项，则保存
        		$http({
        			method:'post',  
        		    url:srvUrl+"meeting/saveTbsxMeeting.do",
        		    data: $.param({"tbsxMeetings": JSON.stringify($scope.tbsxMeetings)})
        		}).success(function(result){
        			//------------------------------------------------------
                	//添加会议决策(20170527) start
                	//------------------------------------------------------
                    var url = 'decision/add.do';
                    postObj = $scope.httpData(url,$.param({"tbsxMeetings": JSON.stringify($scope.tbsxMeetings),"arrOnemeeting": JSON.stringify($scope.ArrOnemeeting)}));
                    postObj.success(function (data) {
                        if (!data.success) {
                            alert(data.result_name);return;
                        }
                    });
                	//------------------------------------------------------
                	//添加会议决策(20170527) end
                	//------------------------------------------------------
                    
        			if($scope.ArrOnemeeting._id == null || $scope.ArrOnemeeting.children.length==0){
        				$.alert("保存成功");
        				$location.path("/MeetingInfoList");
        				return;
        			}
        			var postObj;
                    var url = 'projectPreReview/Meeting/saveMeetingByID';
                    $scope.ArrOnemeeting.submitDate = newDate();
                    $scope.ArrOnemeeting.openMeetingPerson = {name:$scope.credentials.userName,value:$scope.credentials.UUID};
                    postObj = $scope.httpData(url, $scope.ArrOnemeeting);
                    postObj.success(function (data) {
                    	if (data.result_code === 'S') {
                            $.alert("保存成功");
                            $location.path("/MeetingInfoList");
                        } else {
                            alert(data.result_name);
                        }
                    });
        		});
        	}else{
        		var postObj;
                var url = 'projectPreReview/Meeting/saveMeetingByID';
                $scope.ArrOnemeeting.submitDate = newDate();
                $scope.ArrOnemeeting.openMeetingPerson = {name:$scope.credentials.userName,value:$scope.credentials.UUID};
                postObj = $scope.httpData(url, $scope.ArrOnemeeting);
                postObj.success(function (data) {
                    if (data.result_code === 'S') {
                    	//------------------------------------------------------
                    	//添加会议决策(20170527) start
                    	//------------------------------------------------------
                        var url = 'decision/add.do';
                        postObj = $scope.httpData(url,$.param({"arrOnemeeting": JSON.stringify($scope.ArrOnemeeting)}));
                        postObj.success(function (data) {
                        	if (data.success) {
                                $.alert("保存成功");
                                $location.path("/MeetingInfoList");
                            } else {
                                alert(data.result_name);
                            }
                        });
                    	//------------------------------------------------------
                    	//添加会议决策(20170527) end
                    	//------------------------------------------------------
                    } else {
                        alert(data.result_name);
                    }
                });
        	}
        }
    }
    $scope.addMeeting = function(){
    	$scope.ArrOnemeeting.jueCeHuiYiZhuXiId = "";
    	for(var i = 0; i < $scope.tbsxMeetings.length; i++){
    		$scope.tbsxMeetings[i].meeting = {
				meetingLeadersNum: $scope.ArrOnemeeting.decisionMakingCommitteeStaffNum,
    			jueCeHuiYiZhuXiId: $scope.ArrOnemeeting.jueCeHuiYiZhuXiId,
    			huiYiQiCi: $scope.ArrOnemeeting.meetingIssueNumber,
    			meetingIssue: $scope.ArrOnemeeting.meetingIssue,
    			meetingLeaders: $scope.ArrOnemeeting.decisionMakingCommitteeStaff,
     			meetingTime: $scope.ArrOnemeeting.meetingTime,
    			startTime: $scope.tbsxs[i].startTime,
    			endTime: $scope.tbsxs[i].endTime,
    			openMeetingPerson:{NAME:$scope.credentials.userName, VALUE:$scope.credentials.UUID},
    			otherPerson: $scope.tbsxMeetings[i].meeting==null||
    				$scope.tbsxMeetings[i].meeting.otherPerson==null?[]
    				:$scope.tbsxMeetings[i].meeting.otherPerson
    		};
    	}
    	if($scope.tbsxMeetings.length > 0){
    		//如果有通报事项，则保存
    		$http({
    			method:'post',  
    		    url:srvUrl+"meeting/saveTbsxMeeting.do",
    		    data: $.param({"tbsxMeetings": JSON.stringify($scope.tbsxMeetings)})
    		}).success(function(result){
    			//------------------------------------------------------
            	//添加会议决策(20170527) start
            	//------------------------------------------------------
                var url = 'decision/add.do';
                postObj = $scope.httpData(url,$.param({"tbsxMeetings": JSON.stringify($scope.tbsxMeetings),"arrOnemeeting": JSON.stringify($scope.ArrOnemeeting)}));
                postObj.success(function (data) {
                    if (!data.success) {
                        alert(data.result_name);return;
                    }
                });
            	//------------------------------------------------------
            	//添加会议决策(20170527) end
            	//------------------------------------------------------
                
    			if($scope.ArrOnemeeting._id == null || $scope.ArrOnemeeting.children.length==0){
    				$.alert("保存成功");
    				$location.path("/MeetingInfoList");
    				return;
    			}
    			var postObj;
                var url = 'projectPreReview/Meeting/saveMeetingByID';
                $scope.ArrOnemeeting.submitDate = newDate();
                $scope.ArrOnemeeting.openMeetingPerson = {name:$scope.credentials.userName,value:$scope.credentials.UUID};
                postObj = $scope.httpData(url, $scope.ArrOnemeeting);
                postObj.success(function (data) {
                	if (data.result_code === 'S') {
                        $.alert("保存成功");
                        $location.path("/MeetingInfoList");
                    } else {
                        alert(data.result_name);
                    }
                });
    		});
    	}else{
    		var postObj;
            var url = 'projectPreReview/Meeting/saveMeetingByID';
            $scope.ArrOnemeeting.submitDate = newDate();
            $scope.ArrOnemeeting.openMeetingPerson = {name:$scope.credentials.userName,value:$scope.credentials.UUID};
            postObj = $scope.httpData(url, $scope.ArrOnemeeting);
            postObj.success(function (data) {
                if (data.result_code === 'S') {
                	//------------------------------------------------------
                	//添加会议决策(20170527) start
                	//------------------------------------------------------
                    var url = 'decision/add.do';
                    postObj = $scope.httpData(url,$.param({"arrOnemeeting": JSON.stringify($scope.ArrOnemeeting)}));
                    postObj.success(function (data) {
                    	if (data.success) {
                            $.alert("保存成功");
                            $location.path("/MeetingInfoList");
                        } else {
                            alert(data.result_name);
                        }
                    });
                	//------------------------------------------------------
                	//添加会议决策(20170527) end
                	//------------------------------------------------------
                } else {
                    alert(data.result_name);
                }
            });
    	}
    }
    
    //给第二部分添加行
    $scope.addProfit = function(id){
        function addBlankRow(array){
            var blankRow = {
                agendaOption:'',
                agendaName:'',
                agendaRule:'',
            }
            array.push(blankRow);
        }
        if(undefined==$scope.ArrOnemeeting.children[id].agenda){
            $scope.ArrOnemeeting.children[id].agenda=[];
        }
        addBlankRow($scope.ArrOnemeeting.children[id].agenda);
    }
    //移除第二部分对应数据
    $scope.deleteProfit = function(id){
        var commentsObj = $scope.ArrOnemeeting.children[id].agenda;
        if(commentsObj!=null){
            for(var i=0;i<commentsObj.length;i++){
                if(commentsObj[i].selected){
                    commentsObj.splice(i,1);
                    i--;
                }
            }
        }
    }
    $scope.getSelectTypeByCode=function(typeCode){
        var  url = 'common/commonMethod/getRoleuser';
        $scope.httpData(url,typeCode).success(function(data){
            if(data.result_code === 'S'){
                $scope.userRoleListall=data.result_data.userRoleList;
            }else{
                alert(data.result_name);
            }
        });
    }
    angular.element(document).ready(function() {
        $scope.getSelectTypeByCode("d59c074a-9117-44b2-b7e7-3f97fe23a975");
    });

    $scope.changDate=function(i,o){
        var startTime= $scope.ArrOnemeeting.children[i].startTime;
        var endTime=$scope.ArrOnemeeting.children[i].endTime;
        if(null!=startTime && ""!=startTime){
         var AP=  startTime.substring(startTime.length-2, startTime.length);
           startTime=  startTime.substring(0,startTime.length-3);
           if(AP=="PM"){
               var strs= new Array(); //定义一数组
               strs=startTime.split(":");
              var s= strs[0]*1+12;
               startTime=s+":"+strs[1];
           }
        }
        if(null!=endTime && ""!=endTime){
            var APd=  endTime.substring(endTime.length-2, endTime.length);
            endTime=  endTime.substring(0,endTime.length-3);
            if(APd=="PM"){
                var strsd= new Array(); //定义一数组
                strsd=endTime.split(":");
                var sd= strsd[0]*1+12;
                endTime=sd+":"+strsd[1];
            }
        }
        if(undefined!=startTime && undefined!=endTime){
            s=startTime.split(":");
            e=endTime.split(":");
            var daya = new Date();
            var dayb = new Date();

            daya.setHours(s[0]);
            dayb.setHours(e[0]);
            daya.setMinutes(s[1]);
            dayb.setMinutes(e[1]);
            var m=(dayb-daya)/1000/60;
            if(m<0){
                alert("时间不能为负数,请修改");
                if(o=="0"){
                    $scope.ArrOnemeeting.children[i].startTime=$scope.ArrOnemeeting.children[i].endTime;
                }else{
                    $scope.ArrOnemeeting.children[i].endTime=$scope.ArrOnemeeting.children[i].startTime;
                }
                $scope.ArrOnemeeting.children[i].minute=0;
                return false;
            }
            $scope.ArrOnemeeting.children[i].minute=m;
        }else{
            $scope.ArrOnemeeting.children[i].minute=0;
        }
    }    
    $scope.initData();
    $scope.getMaxMeetingIssue();
}]);
function delSelect(o,paramsVal,value,k){
    $(o).parent().remove();
    accessScope("#"+paramsVal+"Name"+k, function(scope){
        if(paramsVal=="decisionMakingCommitteeStaff"){
            var names=scope.ArrOnemeeting.children[k].decisionMakingCommitteeStaff;
            var retArray = [];
            for(var i=0;i<names.length;i++){
                if(value !== names[i].VALUE){
                    retArray.push(names[i]);
                }
            }
            if(retArray.length>0){
                scope.ArrOnemeeting.children[k].decisionMakingCommitteeStaff=retArray;
            }else{
                scope.ArrOnemeeting.children[k].decisionMakingCommitteeStaff=null;
            }
        }else if(paramsVal=="investment"){
            var names=scope.ArrOnemeeting.children[k].investment;
            var retArray = [];
            for(var i=0;i<names.length;i++){
                if(value !== names[i].value){
                    retArray.push(names[i]);
                }
            }
            if(retArray.length>0){
                scope.ArrOnemeeting.children[k].investment=retArray;
            }else{
                scope.ArrOnemeeting.children[k].investment=null;
            }
        }else if(paramsVal=="division"){
            var names=scope.ArrOnemeeting.children[k].division;
            var retArray = [];
            for(var i=0;i<names.length;i++){
                if(value !== names[i].value){
                    retArray.push(names[i]);
                }
            }
            if(retArray.length>0){
                scope.ArrOnemeeting.children[k].division=retArray;
            }else{
                scope.ArrOnemeeting.children[k].division=null;
            }
        }

    });
}

//阻止事件冒泡函数
function stopBubble(e)
{
    if (e && e.stopPropagation)
        e.stopPropagation()
    else
        window.event.cancelBubble=true
}

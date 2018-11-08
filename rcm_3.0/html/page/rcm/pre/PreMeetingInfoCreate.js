ctmApp.register.controller('PreMeetingInfoCreate', ['$http','$scope','$location','$routeParams', 
 function ($http,$scope,$location,$routeParams) {
	//初始化数据
	var objId = $routeParams.id;
	$scope.oldUrl = $routeParams.url;
	$scope.initData = function(){
		$scope.getFormalAssessmentByID(objId);
		$scope.pfrAgenda={"nameField":"name","valueField":"value"};
		$scope.pfrInvestment={"nameField":"name","valueField":"value"};
		$scope.pfrDivision={"nameField":"name","valueField":"value"};
		$scope.pfrContacts={"nameField":"name","valueField":"value"};
	}
	 $scope.columnName="";
	    $scope.setDirectiveParam=function(columnName){
	        $scope.columnName=columnName;
	    }
	$scope.dic2=[];
    $scope.dic2.nameValue=[{name:'A级（困难评审)',value:1},{name:'B级（中级评审)',value:2},{name:'C级（简单评审)',value:3}];
    $scope.setDirectiveParamTwo=function(columnName,num){
        $scope.columnsName=columnName;
        $scope.columnsNum=num;
    }
	//根据businessid查询投标评审项目信息
	$scope.getFormalAssessmentByID=function(id){
		var  url = 'preInfo/getPreByID.do';
		$http({
			method:'post',  
		    url:srvUrl+url, 
		    data: $.param({"businessId":id})
		}).success(function(data){
			$scope.pfr  = data.result_data.mongo;
			$scope.pfr.isUrgent = "0";
			$scope.pfr.participantMode = "1";
			var pt1NameArr=[];
			var pt1=$scope.pfr.apply.serviceType;
			if(null!=pt1 && pt1.length>0){
				for(var i=0;i<pt1.length;i++){
					pt1NameArr.push(pt1[i].VALUE);
				}
				$scope.pfr.apply.serviceType=pt1NameArr.join(",");
			}
			if(null == $scope.pfr.division){
				$scope.pfr.division = [];
			}
			if(null == $scope.pfr.investment){
				$scope.pfr.investment = [];
			}
			if(null == $scope.pfr.agenda){
				$scope.pfr.agenda= {};
			}
			if(null == $scope.pfr.contacts){
				$scope.pfr.contacts = {};
			}
		});
	}
	//投标评审参会信息详情
	$scope.getMeetingInfoByID=function(id){
		var  url = 'preMeetingInfo/queryMeetingInfoById.do';
		$http({
			method:'post',  
		    url:srvUrl+url, 
		    data: $.param({"id":id})
		}).success(function(data){
			$scope.meeting= data.result_data;
		});
	}
	//新增会议信息到mongo
	$scope.save = function(){
		if($("#myFormPPR").valid()) {
			if($scope.pfr.division==null || $scope.pfr.division==""){
				$.alert("大区参会人员必填");
				return false;
			}
			if($scope.pfr.agenda==null || $scope.pfr.agenda.value==null||$scope.pfr.agenda.value==""){
				$.alert("汇报人必填")
				return false;
			}
			if($scope.pfr.contacts==null || $scope.pfr.contacts.value==null||$scope.pfr.contacts.value==""){
				$.alert("联系人必填");
				return false;
			}
			show_Mask();
			$scope.pfr._id = objId;
			var  url = 'preMeetingInfo/addMeetingInfo.do';
			var mypfr = angular.copy($scope.pfr);
			$http({
	 			method:'post',  
	 			url:srvUrl+url, 
	 		    data: $.param({"information":JSON.stringify(mypfr)})
	 		}).success(function(result){
	 			hide_Mask();
	 			 if (result.success) {
//	                  $scope.pfr._id = result.result_data;
	                  $.alert(result.result_name);
	                  var oldUrl=window.btoa(encodeURIComponent(escape("#/PreMeetingInfoList/1")))
	 				  $location.path("PreMeetingInfoDetailView/"+objId+"/"+oldUrl);
	                  $scope.getMeetingInfoByID();
	              } else {
	                  $.alert(result.result_name);
	              }
	 		});
		}
	}
    $scope.setDirectiveRadioUserList=function(arrID,arrName) {
        var paramsVal=$scope.columnsName;
        var paramsNum=$scope.columnsNum;
        if(paramsVal=="contacts") {
            $scope.pfr.contacts = {name: arrName, value: arrID};//赋值保存到数据库
            $("#contactsName").val(name);
            $("label[for='contactsName']").remove();
            commonModelOneValue(paramsVal, arrID, arrName);
        }else if(paramsVal=="agendaName"){
            $scope.pfr.agenda[paramsNum].agendaName={name:arrName,value:arrID};
        }
    }
	//给第二部分添加行
    $scope.addProfit = function(){
        function addBlankRow(array){
            var blankRow = {
                agendaOption:'',
                agendaName:'',
                agendaRule:'',
            }
            array.push(blankRow);
        }
        if(undefined==$scope.pfr.agenda){
            $scope.pfr.agenda=[];
        }
        addBlankRow($scope.pfr.agenda);

    }
    //移除第二部分对应数据
    $scope.deleteProfit = function(){
        var commentsObj = $scope.pfr.agenda;
        if(commentsObj!=null){
            for(var i=0;i<commentsObj.length;i++){
                if(commentsObj[i].selected){
                    commentsObj.splice(i,1);
                    i--;
                }
            }
        }
    }
    $scope.setDirectiveRadioUserList=function(arrID,arrName) {
        var paramsVal=$scope.columnsName;
        var paramsNum=$scope.columnsNum;
        if(paramsVal=="contacts") {
            $scope.pfr.contacts = {name: arrName, value: arrID};//赋值保存到数据库
            $("#contactsName").val(name);
            $("label[for='contactsName']").remove();
            commonModelOneValue(paramsVal, arrID, arrName);
        }else if(paramsVal=="agendaName"){
            $scope.pfr.agenda[paramsNum].agendaName={name:arrName,value:arrID};
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
    $scope.setDirectiveUserList=function(arrID,arrName,arrUserNamesValue){
        var paramsVal=$scope.columnName;
        if(paramsVal=="investment"){
            $scope.pfr.investment =arrUserNamesValue;  //赋值保存到数据库
            $("#investmentName").val(arrUserNamesValue);
            $("label[for='investmentName']").remove();
        }else if(paramsVal=="division"){
            $scope.pfr.division =arrUserNamesValue;  //赋值保存到数据库
            $("#divisionName").val(arrUserNamesValue);
            $("label[for='divisionName']").remove();
        }
        commonModelValue(paramsVal,arrID,arrName);
    }
    var commonModelValue=function(paramsVal,arrID,arrName){
        $("#header"+paramsVal+"Name").find(".select2-choices").html("<li class=\"select2-search-field\"><input id=\"s2id_autogen2\" class=\"select2-input\" type=\"text\" spellcheck=\"false\" autocapitalize=\"off\" autocorrect=\"off\" autocomplete=\"off\" style=\"width: 16px;\"> </li>");
        var leftstr="<li class=\"select2-search-choice\"><div>";
        var centerstr="</div><a class=\"select2-search-choice-close\" tabindex=\"-1\"  onclick=\"delObj(this,'";
        var addID="');\"  ></a><div class=\"full-drop\"><input type=\"hidden\" id=\"\"  value=\"";
        var rightstr="\"></div></li>";
        for(var i=0;i<arrName.length;i++){
            $("#header"+paramsVal+"Name").find(".select2-search-field").before(leftstr+arrName[i]+centerstr+paramsVal+"','"+arrID[i]+addID+arrID[i]+rightstr);
        }
    }
	$scope.initData();
}]);
function delObj(o,paramsVal,value,name){
    $(o).parent().remove();
    accessScope("#"+paramsVal+"Name", function(scope){
        if(paramsVal=="contacts"){
          var  names=scope.pfr.contacts;
          var  ids= scope.pfr.contacts_id;
            names.splice(value,1);
            ids.splice(value,1);
            scope.pfr.contacts=names;
            scope.pfr.contacts_id=ids;
        }else if(paramsVal=="investment"){
            var names=scope.pfr.investment;
            var retArray = [];
            for(var i=0;i<names.length;i++){
                if(value !== names[i].value){
                    retArray.push(names[i]);
                }
            }
            if(retArray.length>0){
                scope.pfr.investment=retArray;
            }else{
                scope.pfr.investment=null;
                $("#investmentName").val("");
            }
        }else if(paramsVal=="division"){
            var names=scope.pfr.division;
            var retArray = [];
            for(var i=0;i<names.length;i++){
                if(value !== names[i].value){
                    retArray.push(names[i]);
                }
            }
            if(retArray.length>0){
                scope.pfr.division=retArray;
            }else{
                scope.pfr.division=null;
                $("#divisionName").val("");
            }
        }
    });
}

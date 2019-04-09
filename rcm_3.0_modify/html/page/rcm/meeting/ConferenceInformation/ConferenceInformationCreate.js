ctmApp.register.controller('ConferenceInformationCreate', ['$http','$scope','$location','$routeParams',
    function ($http,$scope,$location,$routeParams) {
        //初始化数据
        var objId = $routeParams.id;
        $scope.oldUrl = $routeParams.url;
        $scope.flag = $routeParams.flag;
        $scope.initData = function(){
            $scope.getProjectByID(objId);
            $scope.getConferenceInfoByID(objId);
            $scope.projectAgenda={"nameField":"name","valueField":"value"};
            $scope.projectInvestment={"nameField":"name","valueField":"value"};
            $scope.projectDivision={"nameField":"name","valueField":"value"};
            $scope.projectContacts={"nameField":"name","valueField":"value"};
        };
        //$scope.project = {"agenda":{},"contacts":{}};
        $scope.columnName="";
        $scope.setDirectiveParam=function(columnName){
            $scope.columnName=columnName;
        };
        $scope.setDirectiveParamTwo=function(columnName,num){
            $scope.columnsName=columnName;
            $scope.columnsNum=num;
        };
        //根据businessid查询正式评审信息
        $scope.getProjectByID = function(id){
            var  url = '';
            if($scope.flag == 1){
                url = 'formalAssessmentInfoCreate/getProjectByID.do';
            } else {
                url = 'preInfoCreate/getProjectByID.do';
            }

            $http({
                method:'post',
                url:srvUrl+url,
                data: $.param({"id":id})
            }).success(function(data){
                $scope.project  = data.result_data.mongoData;
                $scope.project.isUrgent = "0";
                var pt1NameArr=[];
                var pt1=$scope.project.apply.serviceType;
                if(null!=pt1 && pt1.length>0){
                    for(var i=0;i<pt1.length;i++){
                        pt1NameArr.push(pt1[i].VALUE);
                    }
                    $scope.project.apply.serviceType=pt1NameArr.join(",");
                }
            });
        };
        // 参会信息详情
        $scope.getConferenceInfoByID=function(id){
            var  url = '';
            if($scope.flag == 1){
                url = 'meeting/queryConferenceInfomationById.do';
            } else {
                url = 'preMeetingInfo/queryMeetingInfoById.do';
            }
            $http({
                method:'post',
                url:srvUrl+url,
                data: $.param({"formalId":id})
            }).success(function(data){
                $scope.meeting= data.result_data;
                if(null == $scope.meeting.participantMode){
                    $scope.meeting.participantMode = "1";
                }
                if(null == $scope.meeting.division){
                    $scope.meeting.division = [];
                }
                if(null == $scope.meeting.investment){
                    $scope.meeting.investment = [];
                }
                if(null == $scope.meeting.agenda){
                    $scope.meeting.agenda= {};
                }
                if(null == $scope.meeting.contacts){
                    $scope.meeting.contacts = {};
                }
            });
        };
        // 暂存会议信息到mongo
        $scope.save = function(){
            if($("#myFormPPR").valid()) {
                if($scope.meeting.division==null || $scope.meeting.division==""){
                    $.alert("大区参会人员必填");
                    return false;
                }
                if($scope.meeting.agenda==null || $scope.meeting.agenda.VALUE==null||$scope.meeting.agenda.VALUE==""){
                    $.alert("汇报人必填");
                    return false;
                }
                if($scope.meeting.contacts==null || $scope.meeting.contacts.VALUE==null||$scope.meeting.contacts.VALUE==""){
                    $.alert("联系人必填");
                    return false;
                }
                $scope.addInfoToMeeting("save");
            }
        };
        // 提交会议信息到mongo
        $scope.submit = function(){
            if($("#myFormPPR").valid()) {
                if($scope.meeting.division==null || $scope.meeting.division==""){
                    $.alert("大区参会人员必填");
                    return false;
                }
                if($scope.meeting.agenda==null || $scope.meeting.agenda.VALUE==null||$scope.meeting.agenda.VALUE==""){
                    $.alert("汇报人必填");
                    return false;
                }
                if($scope.meeting.contacts==null || $scope.meeting.contacts.VALUE==null||$scope.meeting.contacts.VALUE==""){
                    $.alert("联系人必填");
                    return false;
                }

                $scope.addInfoToMeeting("submit");
            }
        };
        $scope.addInfoToMeeting = function (flag){
            show_Mask();
            $scope.project.formalId = objId;
            var  url = '';
            if($scope.flag == 1){
                url = 'formalAssessmentInfoCreate/addConferenceInformation.do';
            } else {
                url = 'preInfoCreate/addConferenceInformation.do';
            }
            $scope.organizeData();
            var mypfr = angular.copy($scope.meeting);
            $http({
                method:'post',
                url:srvUrl+url,
                data: $.param({"information":JSON.stringify(mypfr), "method": flag})
            }).success(function(result){
                hide_Mask();
                $.alert(result.result_name);
                if (flag == 'submit'){
                    var oldUrl=window.btoa(encodeURIComponent(escape("#/IndividualTable")))
                    $location.path("ConferenceInformationDetailView/"+objId+"/" + oldUrl + "/" + $scope.flag);
                }

            });
        };

        $scope.organizeData = function (){
            $scope.meeting.formalId = $scope.project._id;
            $scope.meeting.projectName = $scope.project.projectName;
            $scope.meeting.projectName = $scope.project.projectName;
            $scope.meeting.serviceType = $scope.project.apply.serviceType;
        };

        $scope.setDirectiveRadioUserList=function(arrID,arrName) {
            var paramsVal=$scope.columnsName;
            var paramsNum=$scope.columnsNum;
            if(paramsVal=="contacts") {
                $scope.project.contacts = {name: arrName, value: arrID};//赋值保存到数据库
                $("#contactsName").val(name);
                $("label[for='contactsName']").remove();
                commonModelOneValue(paramsVal, arrID, arrName);
            }else if(paramsVal=="agendaName"){
                $scope.project.agenda[paramsNum].agendaName={name:arrName,value:arrID};
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
            if(undefined==$scope.project.agenda){
                $scope.project.agenda=[];
            }
            addBlankRow($scope.project.agenda);

        }
        //移除第二部分对应数据
        $scope.deleteProfit = function(){
            var commentsObj = $scope.project.agenda;
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
                $scope.project.contacts = {name: arrName, value: arrID};//赋值保存到数据库
                $("#contactsName").val(name);
                $("label[for='contactsName']").remove();
                commonModelOneValue(paramsVal, arrID, arrName);
            }else if(paramsVal=="agendaName"){
                $scope.project.agenda[paramsNum].agendaName={name:arrName,value:arrID};
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
                $scope.project.investment =arrUserNamesValue;  //赋值保存到数据库
                $("#investmentName").val(arrUserNamesValue);
                $("label[for='investmentName']").remove();
            }else if(paramsVal=="division"){
                $scope.project.division =arrUserNamesValue;  //赋值保存到数据库
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

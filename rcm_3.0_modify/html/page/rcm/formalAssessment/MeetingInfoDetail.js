ctmApp.register.controller('MeetingInfoDetail', ['$http','$scope','$location','$routeParams', function ($http,$scope,$location,$routeParams) {
    var action =$routeParams.action;
    $scope.actionPam =$routeParams.action;
    $scope.arf={projectName:{},projectNo:{},create_date:{},projectSize:{}};
    $scope.Onemeeting={};
    var complexId = $routeParams.id;
    var params = complexId.split("@");
    if(null!=params[1] && ""!=params[1]){
        $scope.flag=params[1];
    }
    var objId = params[0];
    $scope.id=objId;
    $scope.team = {};
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

    //getMeetingByID与getMeetingByReportID 查询结果为一个  后者根据报表id查询
    $scope.getMeetingByID=function(id){
        var  url = 'projectPreReview/Meeting/listOne';
        $scope.httpData(url,id).success(function(data){
            $scope.Onemeeting = data.result_data;
            var haderNameArr=[],haderValueArr=[],dNameArr=[],dValueArr=[];
            if(null!=$scope.Onemeeting.investment){
                var header=$scope.Onemeeting.investment;
                if(null!=header && header.length>0){
                    for(var i=0;i<header.length;i++){
                        haderNameArr.push(header[i].name);
                        haderValueArr.push(header[i].value);
                    }
                    commonModelValue("investment",haderValueArr,haderNameArr);
                }
            }
            if(null!=$scope.Onemeeting.division){
                var header2=$scope.Onemeeting.division;
                if(null!=header2 && header2.length>0){
                    for(var i=0;i<header2.length;i++){
                        dNameArr.push(header2[i].name);
                        dValueArr.push(header2[i].value);
                    }
                    commonModelValue("division",dValueArr,dNameArr);
                }
            }
            if($scope.actionPam=="View"){
                $("a").attr("onclick","");
                $("#model1").attr("data-toggle","");
                $("#model2").attr("data-toggle","");
            }

        }).error(function (data, status, headers, config) {
            alert(status);
        });
    };
    $scope.getMeetingByReportID=function(id){
        var  url = 'projectPreReview/Meeting/findFormalPrassesmentByFormalId';
        $scope.httpData(url,id).success(function(data){
            $scope.Onemeeting = data.result_data;
            var haderNameArr=[],haderValueArr=[],dNameArr=[],dValueArr=[];
            if(null!=$scope.Onemeeting.investment){
                var header=$scope.Onemeeting.investment;
                if(null!=header && header.length>0){
                    for(var i=0;i<header.length;i++){
                        haderNameArr.push(header[i].name);
                        haderValueArr.push(header[i].value);
                    }
                    commonModelValue("investment",haderValueArr,haderNameArr);
                }
            }
            if(null!=$scope.Onemeeting.division){
                var header2=$scope.Onemeeting.division;
                if(null!=header2 && header2.length>0){
                    for(var i=0;i<header2.length;i++){
                        dNameArr.push(header2[i].name);
                        dValueArr.push(header2[i].value);
                    }
                    commonModelValue("division",dValueArr,dNameArr);
                }
            }
        }).error(function (data, status, headers, config) {
            alert(status);
        });
    };
    $scope.save= function (flag) {
        var postObj;
        var url="";
        if(typeof ($scope.Onemeeting._id) !="undefined"){
            url ='projectPreReview/Meeting/updateMeeting';
        }else{
            url = 'projectPreReview/Meeting/create';
        }
        if($("#myFormPPR").valid()){
        postObj=$scope.httpData(url,$scope.Onemeeting);
        postObj.success(function(data){
            if(data.result_code === 'S'){
            	$.alert("保存成功");
                if(action=="Create"){
                    if(flag=='1'){
                        $location.path("/FormalBiddingInfo/"+objId+"@1");
                    }
                    if(flag=='3'){
                        $location.path("/FormalBiddingInfo/"+objId+"@3");
                    }
                }

            }else{
                alert(data.result_name);
            }
        });
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
        if(undefined==$scope.Onemeeting.agenda){
            $scope.Onemeeting.agenda=[];
        }
        addBlankRow($scope.Onemeeting.agenda);

    }
    //移除第二部分对应数据
    $scope.deleteProfit = function(){
        var commentsObj = $scope.Onemeeting.agenda;
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
            $scope.Onemeeting.contacts = {name: arrName, value: arrID};//赋值保存到数据库
            $("#contactsName").val(name);
            $("label[for='contactsName']").remove();
            commonModelOneValue(paramsVal, arrID, arrName);
        }else if(paramsVal=="agendaName"){
            $scope.Onemeeting.agenda[paramsNum].agendaName={name:arrName,value:arrID};
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
            $scope.Onemeeting.investment =arrUserNamesValue;  //赋值保存到数据库
            $("#investmentName").val(arrUserNamesValue);
            $("label[for='investmentName']").remove();
        }else if(paramsVal=="division"){
            $scope.Onemeeting.division =arrUserNamesValue;  //赋值保存到数据库
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
    $scope.getFormalReportByID=function(id){
        var  url = 'formalAssessment/FormalReport/getByID';
        $scope.httpData(url,id).success(function(data){
            $scope.formalReport  = data.result_data;
            $scope.Onemeeting.projectName=$scope.formalReport.projectName;
            $scope.Onemeeting.projectForm=$scope.formalReport.projectTypeName;
            $scope.Onemeeting.serviceType=$scope.formalReport.serviceType;
            var ptNameArr=[],pmNameArr=[];
            var st=$scope.formalReport.serviceType;
            if(null!=st && st.length>0){
                for(var i=0;i<st.length;i++){
                    ptNameArr.push(st[i].VALUE);
                }
                $scope.Onemeeting.serviceType=ptNameArr.join(",");
            }
            $scope.Onemeeting.formalId=$scope.formalReport.projectFormalId;
            $scope.Onemeeting.reportlId=id;
            if(undefined==$scope.Onemeeting.agenda){
                $scope.addProfit();
            }
            $scope.Onemeeting.user_id=$scope.credentials.UUID;
            $scope.Onemeeting.isUrgent=0;
        });
    }
    //定义窗口action

    if (action == 'Update') {
        $scope.getMeetingByID(objId);
    } else if (action == 'View') {
        $scope.getMeetingByID(objId);
        $('input').attr('disabled',true);
        $('textarea').attr('disabled',true);
        $('select').attr('disabled',true);
        $('button').attr('disabled',true);
        $("#savebtn").hide();

    } else if (action == 'Create') {
        var  url = 'projectPreReview/Meeting/getMeetingByIDReportID';
        $scope.httpData(url,objId).success(function(data){
           var boolean=data.result_data;
            if(boolean){
                $scope.getMeetingByReportID(objId);
            }else{
                $scope.getFormalReportByID(objId);
            }
        });
    }

}]);
function delObj(o,paramsVal,value,name){
    $(o).parent().remove();
    accessScope("#"+paramsVal+"Name", function(scope){
        if(paramsVal=="contacts"){
          var  names=scope.Onemeeting.contacts;
          var  ids= scope.Onemeeting.contacts_id;
            names.splice(value,1);
            ids.splice(value,1);
            scope.Onemeeting.contacts=names;
            scope.Onemeeting.contacts_id=ids;
        }else if(paramsVal=="investment"){
            var names=scope.Onemeeting.investment;
            var retArray = [];
            for(var i=0;i<names.length;i++){
                if(value !== names[i].value){
                    retArray.push(names[i]);
                }
            }
            if(retArray.length>0){
                scope.Onemeeting.investment=retArray;
            }else{
                scope.Onemeeting.investment=null;
                $("#investmentName").val("");
            }
        }else if(paramsVal=="division"){
            var names=scope.Onemeeting.division;
            var retArray = [];
            for(var i=0;i<names.length;i++){
                if(value !== names[i].value){
                    retArray.push(names[i]);
                }
            }
            if(retArray.length>0){
                scope.Onemeeting.division=retArray;
            }else{
                scope.Onemeeting.division=null;
                $("#divisionName").val("");
            }
        }
    });
}
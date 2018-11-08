ctmApp.register.controller('MeetingApplyNotice', ['$http','$scope','$location','$routeParams', function ($http,$scope,$location,$routeParams) {
    //初始化

    var objId=  $routeParams.id;
    $scope.id=objId;
    $scope.columnName="";
    $scope.columnNum="";
    $scope.setDirectiveParam=function(columnName,columnNum){
        $scope.columnName=columnName;
        $scope.columnNum=columnNum;
    }

    function FormatDate() {
        var date = new Date();
        var paddNum = function(num){
            num += "";
            return num.replace(/^(\d)$/,"0$1");
        }
        return date.getFullYear()+""+paddNum(date.getMonth()+1)+""+paddNum(date.getDate())+"-"+paddNum(date.getHours())+""+paddNum(date.getMinutes())+""+paddNum(date.getSeconds());
    }
    $scope.getMeetingByID=function(id){
        var  url = 'projectPreReview/Meeting/getMeetingByIDNotice';
        $scope.httpData(url,id).success(function(data){
                $scope.ArrOnemeeting = data.result_data;
            var fgNameArr=[],fgValueArr=[],haderValueArr=[],haderNameArr=[],dValueArr=[],dNameArr=[];
            var fg=$scope.ArrOnemeeting.decisionMakingCommitteeStaff;

            if(null!=fg && fg.length>0){
                for(var k=0;k<fg.length;k++){
                    fgNameArr.push(fg[k].NAME);
                    fgValueArr.push(fg[k].VALUE);
                }
                commonModelValue2(fgValueArr,fgNameArr);
            }
                var fg1=$scope.ArrOnemeeting.investment;
                if(null!=fg1 && fg1.length>0){
                    for(var k=0;k<fg1.length;k++){
                        haderNameArr.push(fg1[k].name);
                        haderValueArr.push(fg1[k].value);
                    }
                    commonModelValue("investment",haderValueArr,haderNameArr);
                }
                var fg2=$scope.ArrOnemeeting.division;
                if(null!=fg2 && fg2.length>0){
                    for(var k=0;k<fg2.length;k++){
                        dNameArr.push(fg2[k].name);
                        dValueArr.push(fg2[k].value);
                    }
                    commonModelValue("division",dValueArr,dNameArr);
                }
        }
        ).error(function (data, status, headers, config) {
            alert(status);
        });
    };
    var commonModelValue2=function(arrID,arrName){
        var leftstr2="<li class=\"select2-search-choice\"><div>";
        var centerstr2="</div><a class=\"select2-search-choice-close\" tabindex=\"-1\" onclick=\"delObjMember2(this,'";
        var addID2="');\"  ></a><div class=\"full-drop\"><input type=\"hidden\" id=\"\"  value=\"";
        var rightstr2="\"></div></li>";
        for(var i=0;i<arrName.length;i++){
            $("#fixed-member-box").find(".select2-search-field").before(leftstr2+arrName[i]+centerstr2+arrID[i]+addID2+arrID[i]+rightstr2);
        }
    }
    var commonModelValue=function(paramsVal,arrID,arrName){
        $("#header"+paramsVal+"Name").find(".select2-choices").html("<li class=\"select2-search-field\"><input id=\"s2id_autogen2\" class=\"select2-input\" type=\"text\" spellcheck=\"false\" autocapitalize=\"off\" autocorrect=\"off\" autocomplete=\"off\" style=\"width: 16px;\"> </li>");
        var leftstr="<li class=\"select2-search-choice\"><div>";
        var centerstr="</div><div class=\"full-drop\"><input type=\"hidden\" id=\"\" disabled value=\"";
        var rightstr="\"></div></li>";
        for(var i=0;i<arrName.length;i++){
            $("#header"+paramsVal+"Name").find(".select2-search-field").before(leftstr+arrName[i]+centerstr+rightstr);
        }
    }
    $scope.save= function () {
        var postObj;
        var url ='projectPreReview/Meeting/saveOneMeeting';
        var perple=$scope.ArrOnemeeting.decisionMakingCommitteeStaff;
        var perpleNum=$scope.ArrOnemeeting.decisionMakingCommitteeStaffNum;
        if(null!=perple){
            var perpleLength=perple.length;
            if(perpleNum=="7" && perpleLength<7){
            	$.alert("参会人员小于7的只能选择简单会议！");
                return false;
            }
        }
        if(null==perple || perple==""){
        	$.alert("请选择参会人员！");
            return false;

        }
        if(perpleNum==null || perpleNum==""){
        	$.alert("请选择会议！");
            return false;
        }
        postObj=$scope.httpData(url,$scope.ArrOnemeeting);
        postObj.success(function(data){
            if(data.result_code === 'S'){
            	$.alert("保存成功");
                $.alert("保存成功");
                $location.path("/MeetingInfoList");
            }else{
                alert(data.result_name);
            }
        });
    }
    //给第二部分添加行
   /* $scope.addProfit = function(){
        function addBlankRow(array){
            var blankRow = {
                agendaOption:'',
                agendaName:'',
                agendaRule:'',
            }
            array.push(blankRow);
        }
        if(undefined==$scope.ArrOnemeeting.agenda){
            $scope.ArrOnemeeting.agenda=[];
        }
        addBlankRow($scope.ArrOnemeeting.agenda);

    }
    //移除第二部分对应数据
    $scope.deleteProfit = function(){
        var commentsObj = $scope.ArrOnemeeting.agenda;
        if(commentsObj!=null){
            var num = 0;
            for (var parent in commentsObj) {
                for (var child in commentsObj[parent]) {
                    if (commentsObj[parent][child] == true) {
                        delete commentsObj[num];
                    }
                }
                num++;
            }
            var newComments = [];
            var size = 0;
            for (var obj in commentsObj) {
                newComments[size] = commentsObj[obj];
                size++;
            }
        }
        $scope.ArrOnemeeting.agenda= newComments;
    }*/
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
    angular.element(document).ready(function() {
        $scope.getSelectTypeByCode("2df64ade-c20d-4d74-bad6-61b93fd7d88f");
    });

    $scope.getMeetingByID(objId);
}]);
function delObjMember2(o,value){
    $(o).parent().remove();
    accessScope("#fixed-member", function(scope){
        var names=scope.ArrOnemeeting.decisionMakingCommitteeStaff;
        var retArray = [];
        for(var i=0;i<names.length;i++){
            if(value !== names[i].value){
                retArray.push(names[i]);
            }
        }
        scope.ArrOnemeeting.decisionMakingCommitteeStaff=retArray;

    });
}
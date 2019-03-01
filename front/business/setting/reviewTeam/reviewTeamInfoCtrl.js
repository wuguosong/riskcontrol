define(['app', 'Service'], function (app) {
    app
        .register.controller('reviewTeamInfoCtrl', ['$http', '$scope', '$location', '$stateParams', 'BEWG_URL', 'Window',
        function ($http, $scope, $location, $stateParams, BEWG_URL, Window) {
            console.log('reviewTeamInfo');

            $scope.teamLeaderMapped={"nameField":"TEAM_LEADER","valueField":"TEAM_LEADER_ID"};
            $scope.teamMemberMapped={"nameField":"TEAM_MEMBER_NAME","valueField":"TEAM_MEMBER_ID"};
            var uid=0;
            $scope.team = {};
            $scope.columnName="";
            $scope.setDirectiveParam=function(columnName){
                $scope.columnName=columnName;
            }

            //保存操作
            $scope.save = function () {
                if($scope.team.ORDERNUM=="" || $scope.team.ORDERNUM == null || $scope.team.UUID=="undefined"){
                    Window.alert("小组编号必须填写,且要与名称中的组号严格匹配！");
                    return false;
                }

                var postObj;
                var url ='';
                if(typeof $scope.team.UUID!=null  && $scope.team.UUID!=''  && $scope.team.UUID!="undefined"){
                    url = BEWG_URL.UpdateReviewTeam;
                }else{
                    url = BEWG_URL.SaveReviewTeam;
                }

                //处理数据
                var arrID=[],arrName=[];
                $scope.teamMemberArr
                for(var i in $scope.teamMemberArr){
                    arrID.push($scope.teamMemberArr[i].TEAM_MEMBER_ID);
                    arrName.push($scope.teamMemberArr[i].TEAM_MEMBER_NAME);
                }
                $scope.team.TEAM_MEMBER_NAME=arrName.join(",");
                $scope.team.TEAM_MEMBER_ID=arrID.join(",");

                $scope.httpData(url,$scope.team).success(function(data){
                    if(data.result_code === 'S'){
                        $location.path("/index/ReviewTeamList");
                    }else{
                        Window.alert("评审小组不能重复或必填项不能为空!");
                    }
                });
            };

            $scope.teamLeaderCallBack = function(){
                if($scope.team != null &&  $scope.team.leader != null){
                    $scope.team.TEAM_LEADER = $scope.team.leader.TEAM_LEADER;
                    $scope.team.TEAM_LEADER_ID = $scope.team.leader.TEAM_LEADER_ID;
                }
            }
            //查询一个用户
            $scope.getTeamByID = function (uuid) {
                $scope.httpData(BEWG_URL.SelectReviewTeamById,uuid).success(
                    function (data, status, headers, config) {
                        $scope.team = data.result_data.team;
                        $scope.team.leader={};
                        $scope.team.leader.TEAM_LEADER = data.result_data.team.TEAM_LEADER;
                        $scope.team.leader.TEAM_LEADER_ID = data.result_data.team.TEAM_LEADER_ID;
                        var arrlead=data.result_data.team;
                        var arrIdname=data.result_data.teamItem;
                        $scope.teamMemberArr=data.result_data.teamItem;
                        var arrID=[],arrName=[];
                        for(var i=0;i<arrIdname.length;i++){
                            arrID.push(arrIdname[i].TEAM_MEMBER_ID);
                            arrName.push(arrIdname[i].TEAM_MEMBER_NAME);
                        }
                        $scope.team.TEAM_MEMBER_NAME=arrName.join(",");
                        $scope.team.TEAM_MEMBER_ID=arrID.join(",");
                        if(null!=$scope.team.TEAM_MEMBER_NAME){
                            commonModelValue("team_member_name",arrID,arrName);
                        }
                        if(null!=$scope.team.TEAM_LEADER){
                            commonModelOneValue("team_leader",arrlead.TEAM_LEADER_ID,arrlead.TEAM_LEADER);
                        }
                    }
                ).error(function (data, status, headers, config) {
                    alert(status);
                });
            };

            //取消操作
            $scope.cancel = function () {
                $location.path("/index/ReviewTeamList");
            };
            var commonModelOneValue=function(paramsVal,arrID,arrName){
                $("#header"+paramsVal+"Name").find(".select2-choices").html("<li class=\"select2-search-field\"><input id=\"s2id_autogen2\" class=\"select2-input\" type=\"text\" spellcheck=\"false\" autocapitalize=\"off\" autocorrect=\"off\" autocomplete=\"off\" style=\"width: 16px;\"> </li>");
                var leftstr="<li class=\"select2-search-choice\"><div>";
                var centerstr="</div><a class=\"select2-search-choice-close\" tabindex=\"-1\" onclick=\"delObj(this,'";
                var addID="');\"  ></a><div class=\"full-drop\"><input type=\"hidden\" id=\"\"  value=\"";
                var rightstr="\"></div></li>";
                $("#header"+paramsVal+"Name").find(".select2-search-field").before(leftstr+arrName+centerstr+paramsVal+"','"+arrID+"','"+arrName+addID+arrID+rightstr);
            }
            $scope.setDirectiveParamTwo=function(columnName,num){
                $scope.columnsName=columnName;
                $scope.columnsNum=num;
            }

            $scope.setDirectiveRadioUserList=function(value,name){
                var paramsVal=$scope.columnsName;
                if(paramsVal=="team_leader"){
                    $scope.team.TEAM_LEADER=name;
                    $scope.team.TEAM_LEADER_ID=value;
                    /*  $("#investmentManagerName").val(name);
                      $("label[for='investmentManagerName']").remove();*/
                    commonModelOneValue(paramsVal,value,name);
                }
            }
            //初始化
            var uuid = $stateParams .uuid;//getUrlParam("userCode");
            //定义窗口action
            var action =$stateParams.action; //getUrlParam('action');
            if (action == 'Update') {
                //初始化状态f
                $scope.getTeamByID(uuid);
                //编辑状态则初始化下拉列表内容
            }else if (action == 'View') {
                $scope.getTeamByID(uuid);
                $('#savebtn').hide();
                $('#cancelbtn').hide();
                $('#viewlbtn').show();
                //设置控件只读
            }else if (action == 'Create') {
                //取默认值
                $scope.team.UUID = '';
                $scope.team.TEAM_NAME = '';
                $scope.team.TYPE='';
                $scope.team.TEAM_LEADER = '';
                $scope.team.TEAM_LEADER_ID = '';
                $scope.team.TEAM_MEMBER_NAME = '';
                $scope.team.TEAM_MEMBER_ID ='';
            };

            //取组长和成员值
            $scope.setDirectiveUserList=function(arrID,arrName) {
                var paramsVal = $scope.columnName;
                if(paramsVal == "team_member_name"){
                    $scope.team.TEAM_MEMBER_NAME = arrName.join(",");
                    $scope.team.TEAM_MEMBER_ID = arrID.join(",");
                    commonModelValue(paramsVal,arrID,arrName);
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
        }]);
    Array.prototype.remove = function(val) {
        var index = this.indexOf(val);
        if (index > -1) {
            this.splice(index, 1);
        }
    };
    function delObj(o,paramsVal,id,name){
        $(o).parent().remove();
        accessScope("#"+paramsVal, function(scope){
            if(paramsVal=="team_member_name"){
                var names=scope.team.TEAM_MEMBER_NAME;
                var values=scope.team.TEAM_MEMBER_ID;
                var arrID=[],arrName=[];
                var strid= new Array(); //定义一数组
                var strname= new Array(); //定义一数组
                strid=values.split(","); //字符分割
                for (var i=0;i<strid.length ;i++ )
                {
                    arrID.push(strid[i]); //分割后的字符输出
                }
                strname=names.split(","); //字符分割
                for (var j=0;j<strname.length ;j++ )
                {
                    arrName.push(strname[j]); //分割后的字符输出
                }
                arrName.remove(name);
                arrID.remove(id,1);
                if(arrName.length>0){
                    scope.team.TEAM_MEMBER_NAME=arrName.join(",");
                    scope.team.TEAM_MEMBER_ID=arrID.join(",");
                }else{
                    scope.team.TEAM_MEMBER_NAME='';
                    scope.team.TEAM_MEMBER_ID='';
                }
            }else if(paramsVal=="team_leader"){
                scope.team.TEAM_LEADER = null;  //赋值保存到数据库
                scope.team.TEAM_LEADER_ID = null;
            }
        });
    }
});

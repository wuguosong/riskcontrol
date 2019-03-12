/**
 * Created by Administrator on 2016/8/11.
 */
/**
 * Created by gl on 2016/8/4.
 */

ctmApp.register.controller('ReviewTeamEdit', ['$http','$scope','$location','$routeParams',


    function ($http,$scope,$location,$routeParams) {
        var uid = 0;
        $scope.team = {};

        $scope.columnName="";
        $scope.team_byName="";
        $scope.setDirectiveParam=function(columnName,team_byName){
            $scope.columnName=columnName;
            $scope.team_byName=team_byName;
        }

        //初始化
        var team_name = $routeParams.TEAM_NAME;
        var team_id=$routeParams.TEAM_ID;
        //var team_people= $scope.team.TEAM_PEOPLE;
        //var team_man= $scope.team.TEAM_MAN;

        $scope.team.TEAM_NAME=team_name;
        $scope.team.TEAM_ID=team_id;

        $scope.insertAll = function (){
            var team_people= $scope.team.TEAM_PEOPLE;
            var team_leadername= $scope.team.TEAM_LEADERNAME;

            var aMethed = "rcm/Pteam/insertAll";
                statevar = {"TEAM_NAME":team_name,'TEAM_ID':team_id,'TEAM_PEOPLE':team_people,'TEAM_MAN':team_man,'TEAM_LEADERNAME':team_leadername};
                $scope.httpData(aMethed, statevar).success(
                    function (data, status, headers, config) {
                        $location.path("/ReviewTeamList/");
                    }
                ).error(function (data, status, headers, config) {
                    alert(status);
                });
            }

        //查询一个用户
        $scope.getTeamByID = function (uuid) {
            var aMethed = 'rcm/Pteam/getTeamByID';
            $scope.httpData(aMethed, uuid).success(
                function (data, status, headers, config) {
                    $scope.item = data.result_data;
                }
            ).error(function (data, status, headers, config) {
                alert(status);
            });
        };

        //定义窗口action
        var action = $routeParams.action; //getUrlParam('action');
        if (action == 'Update') {
            //初始化状态f
            $scope.getTeamByID(uuid);
            //编辑状态则初始化下拉列表内容
        } else if (action == 'View') {
            $scope.getTeamByID(uuid);
            $('#savebtn').hide();
            $('#cancelbtn').hide();
            $('#viewlbtn').show();
            //设置控件只读
        } else if (action == 'Create') {

            uid = uuid;
            //取默认值
            $scope.team.TEAM_NAME = '';
            $scope.team.TEAM_LEADER = '';
            $scope.team.TEAM_BY = '';
            $scope.team.UUID = '';
        };
        //取组长和成员值
        $scope.setDirectiveUserList=function(arrID,arrName) {
            var paramsVal = $scope.columnName;
            if (paramsVal == "teamLeader") {
                $scope.team.TEAM_LEADERNAME = arrName.join(",");  //赋值保存到数据库
                $scope.team.TEAM_LEADERVALUE = arrID.join(",");
            } else if(paramsVal == "team_by"){
                $scope.team.TEAM_PEOPLE = arrName.join(",");
            $scope.team.TEAM_MAN = arrID.join(",");
        }
        }
    }]);

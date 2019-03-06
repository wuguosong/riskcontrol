define(['app', 'Service'], function (app) {
    app
        .register.controller('formalAssessmentInfoListCtrl', ['$http', '$scope', '$location', '$stateParams', 'BEWG_URL', 'Window',
        function ($http, $scope, $location, $stateParams, BEWG_URL, Window) {
            console.log('formalAssessmentInfoList');

            var tabIndex = $stateParams.tabIndex;


            //控制具体显示那个tab标签页  012
            $('#myTab li:eq('+tabIndex+') a').tab('show');
            $scope.tabIndex = tabIndex;

            //按钮控制器
            /*$scope.buttonControl=srvUrl=="http://riskcontrol.bewg.net.cn/rcm-rest/";*/
            $scope.initData = function(){
                $scope.getForamlAssessmentList();
                $scope.getForamlAssessmentSubmitedList();
            }
            //查询正式评审基本信息列表--起草中
            $scope.getForamlAssessmentList = function(){
                if($scope.paginationConf.queryObj == null || $scope.paginationConf.queryObj == ''){
                    $scope.paginationConf.queryObj = {};
                }
                $scope.paginationConf.queryObj.needCreateBy = '1';
                $http({
                    method:'post',
                    url: BEWG_URL.SelectAllPfr,
                    data: $.param({"page":JSON.stringify($scope.paginationConf)})
                }).success(function(result){
                    $scope.formalAssessmentList = result.result_data.list;
                    $scope.paginationConf.totalItems = result.result_data.totalItems;
                });
            };
            //查询正式评审基本信息列表--已提交
            $scope.getForamlAssessmentSubmitedList = function(){
                /*show_Mask();*/
                $http({
                    method:'post',
                    url: BEWG_URL.SelectAllSubmitPfr,
                    data: $.param({"page":JSON.stringify($scope.paginationConfes)})
                }).success(function(result){
                    $scope.formalAssessmentSubmitedList = result.result_data.list;
                    $scope.paginationConfes.totalItems = result.result_data.totalItems;
                    /*hide_Mask();*/
                }).error(function(data, status, headers, config){
                   /* hide_Mask();*/
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
                $scope.getForamlAssessmentList();
            }

            // 新增操作
            $scope.createProject=function(){
                $location.path("/index/FormalAssessmentInfo/0/1");
            };

            // 修改操作
            $scope.updateProject=function(){
                var chkObjs = $("input[type=checkbox][name=uncommittedReportCheckbox]:checked");
                if(chkObjs.length == 0){
                    Window.alert("请选择要修改的数据！");
                    return false;
                }

                if(chkObjs.length > 1){
                    Window.alert("请只选择一条数据进行修改!");
                    return false;
                }
                var idsStr = "";
                for(var i = 0; i < chkObjs.length; i++){
                    idsStr = idsStr + chkObjs[i].value + ",";
                }
                idsStr = idsStr.substring(0, idsStr.length - 1);
                $location.path("/index/FormalAssessmentInfo/" + idsStr + "/2");
            };

            // 删除项目
            $scope.deleteProject = function () {
                var chkObjs = $("input[type=checkbox][name=uncommittedReportCheckbox]:checked");
                if(chkObjs.length == 0){
                    Window.alert("请选择要删除的数据！");
                    return false;
                }
                Window.confirm('注意', "删除后不可恢复，确认删除吗？").result.then(function (btn) {
                    var idsStr = "";
                    for(var i = 0; i < chkObjs.length; i++){
                        if(i == chkObjs.length-1){
                            idsStr = idsStr + chkObjs[i].value;
                        } else {
                            idsStr = idsStr + chkObjs[i].value + ",";
                        }
                    }
                    $http({
                        method:'post',
                        url: BEWG_URL.DeletePfr,
                        data: $.param({"ids":JSON.stringify(idsStr)})
                    }).success(function(data){
                        if(data.success){
                            $scope.initData();
                            Window.alert("执行成功");
                        }else{
                            Window.alert(data);
                        }
                    });
                });
            }
           /* $scope.delete=function(){
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
                    Window.alert("请选择其中一条或多条数据删除！");
                    return false;
                }
                var obj={"_id": uid};
                $scope.httpData("formalAssessment/ProjectFormalReview/delete", obj).success(
                    function (data, status, headers, config) {
                        if (data.result_code == "R") {
                            Window.alert('报告未删除！删除申请单之前请先删除该项目下的报告单！');
                            return false;
                        }else{
                            Window.confirm('注意', "删除后不可恢复，确认删除吗？").result.then(function (btn) {
                                $scope.httpData(aMethed, obj).success(
                                    function (data, status, headers, config) {
                                        $scope.getForamlAssessmentList();
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
            };*/

            // 跳转详情页面
            $scope.toPfrInfo = function (id, tabIndex) {
                $location.path("/index/FormalAssessmentInfoView/" + id + "/" + tabIndex);
            };

            // 配置分页基本参数
            $scope.paginationConf = {
                currentPage: 1,
                itemsPerPage: 10,
                perPageOptions: [10, 20, 30, 40, 50]
            };
            $scope.paginationConfes = {
                currentPage: 1,
                itemsPerPage: 10,
                perPageOptions: [10, 20, 30, 40, 50]
            };
            // 通过$watch currentPage和itemperPage 当他们一变化的时候，重新获取数据条目
            $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.getForamlAssessmentList);
            $scope.$watch('paginationConfes.currentPage + paginationConfes.itemsPerPage', $scope.getForamlAssessmentSubmitedList);
        }]);
});
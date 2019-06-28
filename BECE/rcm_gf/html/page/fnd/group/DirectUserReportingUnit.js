/**
 * Created by gl on 2016/12/23.
 */
ctmApp.register.controller('DirectUserReportingUnitList', ['$http','$scope','$location','$routeParams',
    function ($http,$scope,$location,$routeParams) {
        //查看操作
        $scope.View=function(uuid){
            $location.path("/DirectUserReportingUnit/View/"+uuid);
        };
        //新建操作
        $scope.Create=function(id){
            $location.path("/DirectUserReportingUnit/Create/"+id);
        };
        $scope.updateGroup=function (){
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
                $.alert("未选中编辑数据！");
            }
            else if(num>1){
                $.alert("只能选择其中一条数据进行编辑！");
                return false;
            }else{
                $location.path("/DirectUserReportingUnit/Update/"+uid);
            }
        }
        $scope.deleteGroup=function(){
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
                $.alert("未选中删除的数据！");
                return false;
            }else{
                var aMethed = 'fnd/Group/delectDirectUserReportingUnitByID';
                $scope.httpData(aMethed, uid).success(
                    function (data, status, headers, config) {
                        $scope.ListAll();
                    }
                ).error(function (data, status, headers, config) {
                    alert(status);
                });

            }
        }
        $scope.ListAll=function(){
            var  url = 'fnd/Group/getDirectUserReportingUnitAll';
            $scope.paginationConf.queryObj = $scope.queryObj;
            $scope.httpData(url,$scope.paginationConf).success(function(data){
                // 变更分页的总数
                $scope.group  = data.result_data.list;
                $scope.paginationConf.totalItems = data.result_data.totalItems;
            });
        };
        $scope.getSyncbusinessmodel=function(keys){
            //初始化一级业务类型下拉列表数据
            var url="common/commonMethod/selectsyncbusinessmodel";
            $scope.httpData(url,keys).success(function(data){
                if(data.result_code === 'S'){
                    $scope.Syncbusinessmodel=data.result_data;
                }else{
                    alert(data.result_name);
                }
            });
        }
        angular.element(document).ready(function() {
            $scope.getSyncbusinessmodel('0');
        });
        // 通过$watch currentPage和itemperPage 当他们一变化的时候，重新获取数据条目
        $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.ListAll);
    }]);

ctmApp.register.controller('DirectUserReportingUnit', ['$http','$scope','$location','$routeParams',
    function ($http,$scope,$location,$routeParams) {
        $scope.columnsName=null;
        $scope.user={};
        $scope.investmentPersonMapped={"nameField":"INVESTMENTPERSON_NAME","valueField":"INVESTMENTPERSON_ID"};
        $scope.directPersonMapped={"nameField":"DIRECTPERSON_NAME","valueField":"DIRECTPERSON_ID"};
        //保存操作
        $scope.save = function () {
            var aMethed=null,postObj=null;
            if($scope.user.ID!=null  && $scope.user.ID!=''  && $scope.user.ID!="undefined"){
                aMethed = 'fnd/Group/UpdateDirectUserReportingUnit';
                postObj=$scope.httpData(aMethed,$scope.user);
            }else{
                aMethed = 'fnd/Group/CreateDirectUserReportingUnit';
                postObj=$scope.httpData(aMethed,$scope.user);
            }
            postObj.success(function(data){
                    if(data.result_code === 'S'){
                        $location.path("/DirectUserReportingUnitList");
                    }else{
                        $.alert("申报单位不能重复!");
                    }
                }
            )
        };
        //取消操作
        $scope.cancel = function () {
            $location.path("/DirectUserReportingUnitList");
        };
        //查询一个用户
        $scope.GetGroupUserReportingUnit = function (id) {
            var aMethed = 'fnd/Group/getDirectUserReportingUnitByID';
            $scope.httpData(aMethed,id).success(
                function (data, status, headers, config) {
                    $scope.user = data.result_data;
                }
            ).error(function (data, status, headers, config) {
                $.alert(status);
            });
        };
        $scope.getSyncbusinessmodel=function(keys){
            //初始化一级业务类型下拉列表数据
            var url="common/commonMethod/selectsyncbusinessmodel";
            $scope.httpData(url,keys).success(function(data){
                if(data.result_code === 'S'){
                    $scope.Syncbusinessmodel=data.result_data;
                }else{
                    alert(data.result_name);
                }
            });
        }
        $scope.setDirectiveParam=function(columnName){
            $scope.columnsName=columnName;
        }
        $scope.setDirectiveRadioUserList=function(id,name){
            var paramsVal=$scope.columnsName;
            if(paramsVal=="investmentPerson"){
                $scope.user.INVESTMENTPERSON_NAME=name;//赋值保存到数据库
                $scope.user.INVESTMENTPERSON_ID=id;
                $("#investmentPersonName").val(name);
                $("label[for='investmentPersonName']").remove();
            }else if(paramsVal=="directPerson"){
                $scope.user.DIRECTPERSON_NAME=name;//赋值保存到数据库
                $scope.user.DIRECTPERSON_ID=id;
                $("#directPersonName").val(name);
                $("label[for='directPersonName']").remove();
            }
        }
        var id =$routeParams.id;
        //定义窗口action
        var action =$routeParams.action; //getUrlParam('action');
        if (action == 'Update') {
            $scope.GetGroupUserReportingUnit(id);
        } else if (action == 'View') {
            $scope.GetGroupUserReportingUnit(id);
            $("#content-wrapper input").attr("disabled",true);
            $("select").attr("disabled",true);
            $("#savebtn").hide();
            //设置控件只读
        } else if (action == 'Create') {

        }
        var ztree, setting = {
            callback:{
                onClick:function(event, treeId, treeNode){
                    accessScope("#reportingUnitName", function(scope){
                        scope.user.REPORTINGUNIT_NAME=treeNode.name;
                        scope.user.REPORTINGUNIT_ID=treeNode.id;
                        $("#reportingUnitName").val(name);
                        $("label[for='reportingUnitName']").remove();
                    });
                },
                beforeExpand:function(treeId, treeNode){
                    if(typeof(treeNode.children)=='undefined'){
                        $scope.addTreeNode(treeNode);
                    }
                }
            }
        };
        $scope.addTreeNode = function (parentNode){
            var pid = '';
            if(parentNode && parentNode.id) pid = parentNode.id;
            $scope.httpData('fnd/Group/getOrg', {parentId:pid}).success(function(data){
                if (!data || data.result_code != 'S') return null;
                var nodeArray = data.result_data;
                if(nodeArray<1) return null;
                for(var i=0;i<nodeArray.length;i++){
                    curNode = nodeArray[i];
                    var iconUrl = 'assets/javascripts/zTree/css/zTreeStyle/img/department.png';
                    if(curNode.cat && curNode.cat=='Org'){
                        iconUrl = 'assets/javascripts/zTree/css/zTreeStyle/img/org.png';
                    }
                    curNode.icon = iconUrl;
                }
                if(pid == ''){//当前加载的是根节点
                    ztree.addNodes(null, nodeArray);
                    var rootNode = ztree.getNodes()[0];
                    $scope.addTreeNode(rootNode);
                    rootNode.open = true;
                    ztree.refresh();
                }else{
                    ztree.addNodes(parentNode, nodeArray, true);
                }
            });
        }
        angular.element(document).ready(function() {
            ztree = $.fn.zTree.init($("#treeID1"), setting);
            $scope.addTreeNode('');
            $scope.getSyncbusinessmodel('0');
        });
    }]);

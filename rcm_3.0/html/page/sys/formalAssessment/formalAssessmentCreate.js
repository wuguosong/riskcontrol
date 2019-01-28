ctmApp.register.controller('formalAssessmentCreate', ['$http','$scope','$location','$routeParams', function ($http,$scope,$location,$routeParams) {
//数据绑定初始化
    $scope.global_projectTypes=new Array();
    $scope.pfr={};
    $scope.pfr.apply={};
    $scope.pfr.taskallocation={};
    $scope.pfr.createby={};
    $scope.columnName="";
    $scope.columnsName="";
    $scope.columnsNum="";
    $scope.isFinished=false;
    $scope.dicSyn=[];
    $scope.pfr.taskallocation.fixedGroup=null;
    $scope.pfr.taskallocation.reviewLeader=null;
    $scope.investmentPerson=null;
    $scope.directPerson=null;

    //初始化数据
    $scope.initData = function(){
        $scope.pfr.createby.VALUE=$scope.credentials.UUID;
        $scope.pfr.createby.NAME=$scope.credentials.userName;
        $scope.pfr.apply.investmentManager = {NAME:$scope.credentials.userName,VALUE:$scope.credentials.UUID};
    }

    // 给申报单位变量赋值
    var ztree1, setting1 = {
        callback:{
            onClick:function(event, treeId, treeNode){
                accessScope("#reportingUnitName", function(scope){
                    scope.pfr.apply.reportingUnit = {"VALUE":treeNode.name,KEY:treeNode.id};
                    $("#reportingUnitName").val(name);
                    $("label[for='reportingUnitName']").remove();
                });
            },
            beforeExpand:function(treeId, treeNode){
                if(typeof(treeNode.children)=='undefined'){
                    $scope.addTreeNode1(treeNode);
                }
            }
        }
    };
    // 给所属大区变量赋值
    var ztree2, setting2 = {
        callback:{
            onClick:function(event, treeId, treeNode){
                accessScope("#pertainAreaName", function(scope){
                    scope.pfr.apply.pertainArea = {"VALUE":treeNode.name,KEY:treeNode.id};
                    $("#pertainAreaName").val(name);
                    $("label[for='pertainAreaName']").remove();
                });
            },
            beforeExpand:function(treeId, treeNode){
                if(typeof(treeNode.children)=='undefined'){
                    $scope.addTreeNode2(treeNode);
                }
            }
        }
    };
    $scope.addTreeNode1 = function (parentNode){
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
                ztree1.addNodes(null, nodeArray);
                var rootNode = ztree1.getNodes()[0];
                $scope.addTreeNode1(rootNode);
                rootNode.open = true;
                ztree1.refresh();
            }else{
                ztree1.addNodes(parentNode, nodeArray, true);
            }
        });
    }
    $scope.addTreeNode2 = function (parentNode){
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
                ztree2.addNodes(null, nodeArray);
                var rootNode = ztree2.getNodes()[0];
                $scope.addTreeNode2(rootNode);
                rootNode.open = true;
                ztree2.refresh();
            }else{
                ztree2.addNodes(parentNode, nodeArray, true);
            }
        });
    }

    // 选择人员后给变量赋值
    $scope.setDirectiveRadioUserList=function(value,name){
        var paramsVal=$scope.columnsName;
        var paramsNum=$scope.columnsNum;
        if(paramsVal=="companyHeader"){
            $scope.pfr.apply.companyHeader ={NAME:name,VALUE:value};//赋值保存到数据库
            $("#companyHeaderName").val(name);
            $("label[for='companyHeaderName']").remove();
            commonModelOneValue(paramsVal,value,name);
        }else if(paramsVal=="grassrootsLegalStaff"){
            $scope.pfr.apply.grassrootsLegalStaff ={NAME:name,VALUE:value};//赋值保存到数据库
            $("#grassrootsLegalStaffName").val(name);
            $("label[for='grassrootsLegalStaffName']").remove();
            commonModelOneValue(paramsVal,value,name);
        }else if(paramsVal=="investmentPerson"){
            $scope.pfr.apply.investmentPerson ={NAME:name,VALUE:value};//赋值保存到数据库
            $("#investmentPersonName").val(name);
            $("label[for='investmentPersonName']").remove();
            commonModelOneValue(paramsVal,value,name);
        }else if(paramsVal=="directPerson"){
            $scope.pfr.apply.directPerson ={NAME:name,VALUE:value};//赋值保存到数据库
            $("#directPersonName").val(name);
            $("label[for='directPersonName']").remove();
            commonModelOneValue(paramsVal,value,name);
        }
    }

    // 选择人员后显示人员的值
    var commonModelOneValue=function(paramsVal,arrID,arrName){
        $("#header"+paramsVal+"Name").find(".select2-choices").html("<li class=\"select2-search-field\"><input id=\"s2id_autogen2\" class=\"select2-input\" type=\"text\" spellcheck=\"false\" autocapitalize=\"off\" autocorrect=\"off\" autocomplete=\"off\" style=\"width: 16px;\"> </li>");
        var leftstr="<li class=\"select2-search-choice\"><div>";
        var centerstr="</div><a class=\"select2-search-choice-close\" tabindex=\"-1\" onclick=\"delObj(this,'";
        var addID="');\"  ></a><div class=\"full-drop\"><input type=\"hidden\" id=\"\"  value=\"";
        var rightstr="\"></div></li>";
        $("#header"+paramsVal+"Name").find(".select2-search-field").before(leftstr+arrName+centerstr+paramsVal+"','"+arrID+"','"+arrName+addID+arrID+rightstr);
    }

    // 人员赋值
    $scope.setDirectiveParam=function(columnName,num){
        $scope.columnsName=columnName;
        $scope.columnsNum=num;
    }

    // 选择项目后，写入项目名称
    $scope.setDirectiveCompanyList=function(code,name){
        $scope.pfr.apply.projectNo=code;
        $scope.pfr.apply.projectName=name;
        $("#projectName").val(name);
        $("label[for='projectName']").remove();
    }

    // 获取项目模式
    $scope.getSyncbusinessmodel=function(keys){
        var url="businessDict/queryBusinessType.do";
        $scope.httpData(url,keys).success(function(data){
            if(data.success){
                $scope.dicSyn.Syncbusinessmodel=data.result_data;
            }else{
                alert(data.result_name);
            }
        });

        // 加载所有的二级业务类型数据，存入全局变量
        var url=srvUrl+"common/commonMethod/queryAllProjectTypes";
        $.ajax({
            url: url,
            type: "GET",
            dataType: "json",
            async: false,
            success: function(data){
                if(data.result_code === 'S'){
                    $scope.global_projectTypes=data.result_data;
                }else{
                    alert(data.result_name);
                }
            }
        });
    };

    // 获取项目模式
    $scope.getprojectmodel=function(keys){
        var url= "common/commonMethod/selectsyncbusinessmodel";
        $scope.httpData(url,keys).success(function(data){
            if(data.result_code === 'S'){
                $scope.dicSyn.projectModelValue=data.result_data;
            }else{
                alert(data.result_name);
            }
        });
    };

    //保存
    $scope.save = function(){
        $http({
            method:'post',
            url: srvUrl + 'formalAssessmentInfo/create.do',
            data: $.param({"formalAssessment":JSON.stringify($scope.pfr)})
        }).success(function(result){
            if (result.result_code === 'S') {
                $scope.nod._id = result.result_data;
                if(typeof(showPopWin)=='function'){
                    showPopWin();
                }else{
                    $.alert('保存成功');
                }
            } else {
                $.alert(result.result_name);
            }
        });
    }

    $scope.initData();

    //初始化申报单位组织树
    angular.element(document).ready(function() {
        ztree1 = $.fn.zTree.init($("#treeID1"), setting1);
        ztree2 = $.fn.zTree.init($("#treeID2"), setting2);
        $scope.addTreeNode1('');
        $scope.addTreeNode2('');
    });

    angular.element(document).ready(function() {
        // 初始化业务类型下拉框
        $scope.getSyncbusinessmodel('0');
        // 初始化项目模式的值
        $scope.getprojectmodel('2');
    });
}]);
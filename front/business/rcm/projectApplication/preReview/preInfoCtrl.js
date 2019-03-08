define(['app', 'Service'], function (app) {
    app
        .register.controller('preInfoCtrl', ['$http', '$scope', '$location', '$stateParams', 'BEWG_URL', 'Window', 'CommonService',
        function ($http, $scope, $location, $stateParams, BEWG_URL, Window, CommonService) {
            console.log('preInfo');

            // 获取参数
            $scope.id = $stateParams.id;
            $scope.flag = $stateParams.flag;

            //数据绑定初始化
            $scope.global_projectTypes=new Array();
            $scope.pre={};
            $scope.pre.apply={};
            $scope.pre.taskallocation={};
            $scope.pre.createby={};
            $scope.columnName="";
            $scope.columnsName="";
            $scope.columnsNum="";
            $scope.isFinished=false;
            $scope.dicSyn=[];
            $scope.pre.taskallocation.fixedGroup=null;
            $scope.pre.taskallocation.reviewLeader=null;
            $scope.investmentPerson=null;
            $scope.directPerson=null;
            $scope.investmentModel=false;
            $scope.title = "";

            // 获取系统当前时间
            $scope.getDate = function () {
                var myDate = new Date();
                //获取当前年
                var year = myDate.getFullYear();
                //获取当前月
                var month = myDate.getMonth() + 1;
                if (month < 10){
                    month = "0" + month;
                }
                //获取当前日
                var date = myDate.getDate();
                var h = myDate.getHours(); //获取当前小时数(0-23)
                var m = myDate.getMinutes(); //获取当前分钟数(0-59)
                var s = myDate.getSeconds();
                var now = year + '-' + month + "-" + date;
                return now;
            }

            //初始化数据
            $scope.initData = function(){
                if($scope.flag == 1){
                    $scope.title = "项目投标评审新增";
                    $scope.initCreate();
                } else {
                    if ($scope.flag == 2){
                        $scope.title = "项目投标评审修改";
                    } else {
                        $scope.title = "项目投标评审查看";
                    }
                    $scope.initUpdate();
                }
            }

            // 初始化新增数据
            $scope.initCreate = function () {
                $scope.pre.create_date = $scope.getDate();
                $scope.pre.apply.createby = $scope.credentials.UUID;
                $scope.pre.apply.investmentManager = {NAME:$scope.credentials.userName,VALUE:$scope.credentials.UUID};
                $scope.pre.apply.pertainArea = {KEY: $scope.credentials.deptId, VALUE: $scope.credentials.deptName};
            }

            // 初始化修改、查看数据
            $scope.initUpdate = function () {
                $http({
                    method:'post',
                    url: BEWG_URL.SelectPre1ById,
                    data: $.param({"id":$scope.id})
                }).success(function(data){
                    console.log(data.result_data.mongoData._id);
                    $scope.pre  = data.result_data.mongoData;
                    $scope.preOracle  = data.result_data.oracleDate;

                    // 回显数据-人员信息
                    let paramsVal = "";
                    if($scope.pre.apply.companyHeader != undefined && $scope.pre.apply.companyHeader != null && $scope.pre.apply.companyHeader != ""){
                        paramsVal = "companyHeader"
                        $("label[for='companyHeaderName']").remove();
                        commonModelOneValue(paramsVal,$scope.pre.apply.companyHeader.VALUE,$scope.pre.apply.companyHeader.NAME);
                    }
                    if($scope.pre.apply.grassrootsLegalStaff != undefined && $scope.pre.apply.grassrootsLegalStaff != null && $scope.pre.apply.grassrootsLegalStaff != ""){
                        paramsVal = "grassrootsLegalStaff"
                        $("label[for='grassrootsLegalStaffName']").remove();
                        commonModelOneValue(paramsVal,$scope.pre.apply.grassrootsLegalStaff.VALUE,$scope.pre.apply.grassrootsLegalStaff.NAME);
                    }
                    if($scope.pre.apply.investmentPerson != undefined && $scope.pre.apply.investmentPerson != null && $scope.pre.apply.investmentPerson != ""){
                        paramsVal = "investmentPerson"
                        $("label[for='investmentPersonName']").remove();
                        commonModelOneValue(paramsVal,$scope.pre.apply.investmentPerson.VALUE,$scope.pre.apply.investmentPerson.NAME);
                    }
                    if($scope.pre.apply.directPerson != undefined && $scope.pre.apply.directPerson != null && $scope.pre.apply.directPerson != ""){
                        paramsVal = "directPerson"
                        $("label[for='directPersonName']").remove();
                        commonModelOneValue(paramsVal,$scope.pre.apply.directPerson.VALUE,$scope.pre.apply.directPerson.NAME);
                    }

                    // 回显数据-业务类型
                    if($scope.pre.apply.serviceType != undefined && $scope.pre.apply.serviceType != null && $scope.pre.apply.serviceType != {} ){
                        commonModelValue2('oneservicetypebox',$scope.pre.apply.serviceType);
                    }

                    // 回显数据-投资模式
                    if($scope.pre.apply.investmentModel = '1'){
                        $scope.investmentModel = true;
                        $scope.getprojectmodel('1');
                    }
                    if($scope.pre.apply.projectModel != undefined && $scope.pre.apply.projectModel != null && $scope.pre.apply.projectModel != {} ){
                        commonModelValue2('projectmodebox',$scope.pre.apply.projectModel);
                    }
                });
            }

            // 给申报单位变量赋值
            var ztree1, setting1 = {
                callback:{
                    onClick:function(event, treeId, treeNode){
                        CommonService.accessScope("#pertainArea", function(scope){
                            scope.pre.apply.pertainArea = {"VALUE":treeNode.name,KEY:treeNode.id};
                            // 保存reportingUnit，提交流程时会验证
                            scope.pre.apply.reportingUnit = {"VALUE":treeNode.name,KEY:treeNode.id};
                            $("#pertainArea").val(name);
                            $("label[for='pertainArea']").remove();
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
                        CommonService.accessScope("#pertainAreaName", function(scope){
                            scope.pre.apply.pertainArea = {"VALUE":treeNode.name,KEY:treeNode.id};
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
                $scope.httpData(BEWG_URL.SelectGroupOrg, {parentId:pid}).success(function(data){
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
                $scope.httpData(BEWG_URL.SelectGroupOrg, {parentId:pid}).success(function(data){
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
                    $scope.pre.apply.companyHeader ={NAME:name,VALUE:value};//赋值保存到数据库
                    $("#companyHeaderName").val(name);
                    $("label[for='companyHeaderName']").remove();
                    commonModelOneValue(paramsVal,value,name);
                }else if(paramsVal=="grassrootsLegalStaff"){
                    $scope.pre.apply.grassrootsLegalStaff ={NAME:name,VALUE:value};//赋值保存到数据库
                    $("#grassrootsLegalStaffName").val(name);
                    $("label[for='grassrootsLegalStaffName']").remove();
                    commonModelOneValue(paramsVal,value,name);
                }else if(paramsVal=="investmentPerson"){
                    $scope.pre.apply.investmentPerson ={NAME:name,VALUE:value};//赋值保存到数据库
                    $("#investmentPersonName").val(name);
                    $("label[for='investmentPersonName']").remove();
                    commonModelOneValue(paramsVal,value,name);
                }else if(paramsVal=="directPerson"){
                    $scope.pre.apply.directPerson ={NAME:name,VALUE:value};//赋值保存到数据库
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

            // 回显select2下拉框的值
            var commonModelValue2=function(paramsVal,arr){
                var leftstr2="<li class=\"select2-search-choice\"><div>";
                var centerstr2="</div><a class=\"select2-search-choice-close\" tabindex=\"-1\" onclick=\"delSelect(this,'";
                var rightstr2="');\"  ></a></li>";
                for(var i=0;i<arr.length;i++){
                    console.log(leftstr2+arr[i].VALUE + centerstr2+paramsVal+"','"+arr[i].VALUE+"','"+arr[i].KEY+rightstr2);
                    $("#header"+paramsVal).find(".select2-search-field").before(leftstr2+arr[i].VALUE + centerstr2+paramsVal+"','"+arr[i].VALUE+"','"+arr[i].KEY+rightstr2);
                }
            }

            // 人员赋值
            $scope.setDirectiveParam=function(columnName,num){
                $scope.columnsName=columnName;
                $scope.columnsNum=num;
            }

            // 选择项目后，写入项目名称
            $scope.setDirectiveCompanyList=function(code,name){
                $scope.pre.apply.projectNo=code;
                $scope.pre.apply.projectName=name;
                $("#projectName").val(name);
                $("label[for='projectName']").remove();
            }

            // 获取项目模式
            $scope.getSyncbusinessmodel=function(keys){
                $scope.httpData(BEWG_URL.SelectBusinessType,keys).success(function(data){
                    if(data.success){
                        $scope.dicSyn.Syncbusinessmodel=data.result_data;
                    }else{
                        alert(data.result_name);
                    }
                });

                // 加载所有的二级业务类型数据，存入全局变量
                $.ajax({
                    url: BEWG_URL.SelectProjectType,
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

            // 投资模式修改时候触发 （1：ppp；0：非ppp）
            $scope.changeInvestmentModel=function(){
                var pid="";
                if($("#investmentModel").is(':checked')){
                    pid="1";
                    $scope.pre.apply.investmentModel = '1';
                }else{
                    pid="2";
                    $scope.pre.apply.investmentModel = '0';
                }
                $("#s2id_projectmodeboxName").find(".select2-choices .select2-search-choice").remove();
                $scope.pre.apply.projectModel=null;
                $scope.getprojectmodel(pid);
            }

            // 获取项目模式
            $scope.getprojectmodel=function(keys){
                $scope.httpData(BEWG_URL.SelectSyncBusinessModel,keys).success(function(data){
                    if(data.result_code === 'S'){
                        $scope.dicSyn.projectModelValue=data.result_data;
                    }else{
                        alert(data.result_name);
                    }
                });
            };

            //保存
            $scope.save = function(){
                $scope.pre.create_date = $scope.getDate();
                $http({
                    method:'post',
                    url: BEWG_URL.SavePre,
                    data: $.param({"projectInfo":JSON.stringify($scope.pre)})
                }).success(function(result){
                    if (result.result_code === 'S') {
                        $scope.nod._id = result.result_data;
                        if(typeof(showPopWin)=='function'){
                            showPopWin();
                        }else{
                            Window.alert('保存成功');
                        }
                        $location.path("/preCreateList");
                    } else {
                        Window.alert(result.result_name);
                    }
                });
            }

            // 修改
            $scope.update = function(){
                $scope.pre.last_update_date = $scope.getDate();
                console.log($scope.pre);
                $http({
                    method:'post',
                    url: BEWG_URL.UpdatePre,
                    data: $.param({"projectInfo":JSON.stringify($scope.pre)})
                }).success(function(result){
                    if (result.result_code === 'S') {
                        $scope.nod._id = result.result_data;
                        if(typeof(showPopWin)=='function'){
                            showPopWin();
                        }else{
                            Window.alert('保存成功');
                        }
                        $location.path("/preCreateList");
                    } else {
                        Window.alert(result.result_name);
                    }
                });
            };

            // 返回列表
            $scope.cancel = function (){
                $location.path("/index/PreInfoList/0");
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
    function delObj(o,paramsVal,value,name){
        $(o).parent().remove();
        CommonService.accessScope("#"+paramsVal+"Name", function(scope){
            if(paramsVal=="companyHeader"){
                scope.pre.apply.companyHeader={NAME:'',VALUE:''};
                $("#companyHeaderName").val("");
            }else if(paramsVal=="investmentManager"){
                scope.pre.apply.investmentManager ={NAME:'',VALUE:''};
                $("#investmentManagerName").val("");
            }else if(paramsVal=="grassrootsLegalStaff"){
                scope.pre.apply.grassrootsLegalStaff ={NAME:'',VALUE:''};//赋值保存到数据库
                $("#grassrootsLegalStaffName").val("");
            }else if(paramsVal=="investmentPerson"){
                scope.pre.apply.investmentPerson ={NAME:'',VALUE:''};//赋值保存到数据库
                $("#investmentPersonName").val("");
            }else if(paramsVal=="directPerson"){
                scope.pre.apply.directPerson ={NAME:'',VALUE:''};//赋值保存到数据库
                $("#directPersonName").val("");
            }
        });
    }
    function delSelect(o,paramsVal,name,id){
        $(o).parent().remove();
        CommonService.accessScope("#"+paramsVal+"Name", function(scope){
            if(paramsVal=="oneservicetypebox"){
                var names=scope.pre.apply.serviceType;
                var newNames = $(names).map(function(){
                    if(this.KEY!=id){
                        return this;
                    }
                }).get();
                if(newNames.length>0){
                    scope.pre.apply.serviceType=newNames;
                }else{
                    scope.pre.apply.serviceType=null;
                }

            }else if(paramsVal=="projecttypebox"){
                var names=scope.pre.apply.projectType;
                var newNames = $(names).map(function(){
                    if(this.KEY!=id){
                        return this;
                    }
                }).get();
                if(newNames.length>0){
                    scope.pre.apply.projectType=newNames;
                }else{
                    scope.pre.apply.projectType=null;
                }
            }else if(paramsVal=="projectmodebox"){
                var names=scope.pre.apply.projectModel;
                var newNames = $(names).map(function(){
                    if(this.KEY!=id){
                        return this;
                    }
                }).get();
                if(newNames.length>0){
                    scope.pre.apply.projectModel=newNames;
                }else{
                    scope.pre.apply.projectModel=null;
                }
            }
        });
    }
});
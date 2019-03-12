ctmApp.register.controller('FormalReportList', ['$http','$scope','$location', function ($http,$scope,$location) {
    $scope.r={};
    $scope.ListAll=function(){
        $scope.conf={currentPage:$scope.paginationConf.currentPage,pageSize:$scope.paginationConf.itemsPerPage,
            queryObj:{'user_id':$scope.credentials.UUID,'PROJECT_NAME':$scope.PROJECT_NAME,'WF_STATE':$scope.WF_STATE,
            'PROJECT_MODEL_NAMES':$scope.PROJECT_MODEL_NAMES,'ASCDESC':$scope.orderby,'WFSTATEASCDESC':$scope.orderbystate}};
        var url =  'formalAssessment/FormalReport/getAllFormalReport';
        $scope.httpData(url,$scope.conf).success(function(data){
            // 变更分页的总数
        	console.log(data.result_data.list);
            $scope.report = data.result_data.list;
            $scope.paginationConf.totalItems = data.result_data.totalItems;
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
        $scope.ListAll();
    }
    $scope.formalBiddingInfo=function(){
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
        	$.alert("请选择其中一条数据！");
            return false;
        }
        if(num>1){
        	$.alert("只能选择其中一条数据！");
            return false;
        }else{
            var ind=uid.lastIndexOf("/");
            uid= uid.substring(ind+1,uid.length);
            var aMethod = 'rcm/ProjectInfo/selectPrjReviewView';
            $scope.httpData(aMethod, {reportId:uid}).success(
                function (data, status, headers, config) {
                    var result = data.result_data;
                    if(result && result.TASK_NAME=='提交报告及材料'){
                        //判断是否上会
                        if(typeof result.NEED_MEETING=='undefined'){
                            //选择是否需要上会
                            $scope.pageFlag="3";
                            $scope.formalReport={_id:result.REPORT_ID,projectFormalId:result.BUSINESS_ID};
                            $("#passModal").modal('show');
                        }else{
                            if(result.NEED_MEETING=='1'){//不需要上会
                                $location.path("/FormalBiddingInfo/"+uid+"@3");
                            }else{//需要上会且已经有上会信息
                                $location.path("/MeetingInfoDetail/Create/"+uid+"@3");
                            }
                        }
                    }else{
                        if(""!=result.TASK_NAME && null!=result.TASK_NAME){
                        	$.alert("当前节点为：("+result.TASK_NAME+"),不能提交决策委员会!");
                            $location.path("/ProjectFormalReviewView/View/"+result.BUSINESS_ID);
                        }else{
                        	$.alert("还没有进行审批,不能提交决策委员会!");
                            $location.path("/ProjectFormalReviewDetailView/Update/"+result.BUSINESS_ID);
                        }


                    }
                }
            ).error(function (data, status, headers, config) {
                alert(status);
            });
        }


    }

    $scope.formalSpecialApprovalMeeting=function(){
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
        	$.alert("请选择其中一条数据！");
            return false;
        }
        if(num>1){
        	$.alert("只能选择其中一条数据！");
            return false;
        }else{
            var ind=uid.lastIndexOf("/");
            uid= uid.substring(ind+1,uid.length);
            var aMethod = 'projectPreReview/Meeting/getMeetingByIDReportID';
            $scope.httpData(aMethod, uid).success(
                function (data, status, headers, config) {
                    if(data.result_data){
                        $location.path("/SpecialApprovalMeeting/"+uid);
                    }else{
                    	$.alert("正式评审报告还没有审批结束！");
                    }
                }
            ).error(function (data, status, headers, config) {
                alert(status);
            });
        }
    }
    // 删除操作
    $scope.Delete = function () {

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
        	$.alert("请选择其中一条或多条数据删除！");
            return false;
        }else {
            var ind = uid.lastIndexOf("/");
            console.log(ind);
            uid = uid.substring(ind + 1, uid.length);
            console.log(uid);
            var obj = {"_id": uid};
            console.log(obj);
            $.confirm("确定要删除？", function () {
                var aMethed = 'formalAssessment/FormalReport/delete';
                $scope.httpData(aMethed, obj).success(
                    function (data, status, headers, config) {
                        $scope.ListAll();
                        $.alert("删除成功！");
                    }
                ).error(function (data, status, headers, config) {
                    alert(status);
                });
            });
        };
    };
    $scope.update = function () {
        var chk_list = document.getElementsByName("checkbox");
        var uid = "", num = 0;
        for (var i = 0; i < chk_list.length; i++) {
            if (chk_list[i].checked) {
                num++;
                uid = uid + ',' + chk_list[i].value;
            }
        }
        if (uid != '') {
            uid = uid.substring(1, uid.length);
        }
        if (num == 0) {
        	$.alert("请选择其中一条或多条数据编辑！");
            return false;
        }
        if (num > 1) {
        	$.alert("只能选择其中一条数据进行编辑！");
            return false;
        } else {
            $location.path("/" + uid+"@2");
        }
    }
    //重新获取数据条目
    var reGetProducts = function () {
        // 发送给后台的请求数据
        var postData = {
            currentPage: $scope.paginationConf.currentPage,
            itemsPerPage: $scope.paginationConf.itemsPerPage
        };
        //当初始化时需要加载一次， 当查询设置为null时并且返回结果与上次总条数不一致时加载
        $scope.ListAll();
    };
    // 配置分页基本参数
    $scope.paginationConf = {
        currentPage: 1,
        itemsPerPage: 10,
        perPageOptions: [10, 20, 30, 40, 50]
    };
    // 通过$watch currentPage和itemperPage 当他们一变化的时候，重新获取数据条目
    $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', reGetProducts);

    $scope.import=function(){
        var url = 'formalAssessment/FormalReport/importForAssmessentReport';
        $scope.httpData(url,$scope.credentials.UUID).success(function (data) {
            if (data.result_code=="S") {
                var files=data.result_data.filePath;
                var fileName=data.result_data.fileName;
                window.location.href = srvUrl+"common/RcmFile/downLoad?filePath="+encodeURI(files)+"&fileName="+encodeURI(fileName);;

            } else {
            	$.alert("生成失败");
            }
        });
    }

    $scope.r={};
    $scope.listProjectFormalReviewList = function () {
        var aMethed = 'common/commonMethod/getProjectPreReviewNoReportList';
        $scope.params={type:'pfr',user_id:$scope.credentials.UUID};
        $scope.httpData(aMethed,$scope.params).success(
            function (data, status, headers, config) {
                $scope.pprs = data.result_data;
            }
        ).error(function (data, status, headers, config) {
            alert(status);
        });
       /* if($scope.$parent.pfr && $scope.$parent.pfr._id){//如果已经明确知道正式评审项目
            $scope.pprs = [{BUSINESS_ID:$scope.$parent.pfr._id,PROJECT_NAME:$scope.$parent.pfr.apply.projectName}];
            $scope.r.UUID = $scope.$parent.pfr._id;
        }else{
            $scope.conf={queryObj:{'user_id':$scope.$parent.credentials.UUID,'CONTROLLER_VAL':"1"}};
            var url =  'formalAssessment/ProjectFormalReview/getAll';
        $scope.httpData(url,$scope.conf).success(
                function (data, status, headers, config) {
                    $scope.pprs = data.result_data.list;
                }
            ).error(function (data, status, headers, config) {
                alert(status);
            });
        }*/
        $scope.r.pmodel="FormalReviewReport/Create";
    };
    $scope.forReport=function(model,uuid){
        var ind=model.lastIndexOf("/");
        var model2= model.substring(ind+1,model.length);
        if(model2=='Update'){
            $.alert("请选择项目模式!");
            return false;
        }else if(uuid==null || uuid==""){
            $.alert("请选择项目!");
            return false;
        }else{
            var model3= model.substring(0,ind);
            var aMethod = 'formalAssessment/FormalReport/checkRoprt';
            var datajson={"model":model3,"projectID":uuid};
            $scope.$parent.httpData(aMethod, datajson).success(
                function (data, status, headers, config) {
                    if(data.result_data=="true"){
                        $("#addModal").modal('hide');
                        $location.path("/"+model3+"/Create/"+uuid+"@2");
                    }else{
                        $.alert("已经生成，不能再次生成该报告！");
                    }
                }
            ).error(function (data, status, headers, config) {
                alert(status);
            });
        }
    }

}]);
ctmApp.register.controller('FormalReviewReport',['$http','$scope','$location','$routeParams','Upload','$timeout', function ($http,$scope,$location,$routeParams,Upload,$timeout) {
    //初始化
    var complexId = $routeParams.id;
    $scope.paramId = complexId;
    var params = complexId.split("@");
    if(null!=params[1] && ""!=params[1]){
        $scope.flag=params[1];
    }
    if(null!=params[2] && ""!=params[2] && null!=params[3] && ""!=params[3] && null!=params[4] && ""!=params[4] && null!=params[5] && ""!=params[5] && null!=params[6] && ""!=params[6]){
        $scope.reportReturnId=params[2]+"@"+params[3]+"@"+params[4]+"@"+params[5]+"@"+params[6];
    }
    var objId = params[0];
    var taskID='wwxosnsosjdsnsjnddID';
    $scope.taskID=taskID;
    $scope.formalReport={};
    $scope.formalReport.BTPPP=[];
    $scope.formalReport.WATERSUPPLYBOT=[];
    $scope.formalReport.WATERSUPPLYTOT=[];
    $scope.formalReport.HW=[];
    $scope.formalReport.WTYY=[];
    $scope.formalReport.WSBOT=[];
    $scope.formalReport.WSTOT=[];
    var params=$scope.columnName;
    //$scope.formalReport.leadershipDecision=[];
    //当选中时添加数组
    $scope.changePr=function(paramVal){
        function addBlankRow(array) {
            array[0] = {};
        }
        if(paramVal=="BT") {
            if ($("#projectTypeBT").is(':checked')) {
                $scope.formalReport.BTnum="1";
                if (undefined == $scope.formalReport.BTPPP) {
                    $scope.formalReport.BTPPP = [];
                }
                addBlankRow($scope.formalReport.BTPPP);
                $scope.myObjone = {"display": "block"}
            } else {
                $scope.formalReport.BTnum=null;
                $scope.myObjone = {"display": "none"}
                $scope.formalReport.BTPPP = [];
            }
        }else if(paramVal=="GSBOT") {
            if ($("#projectTypeGSBOT").is(':checked')) {
                $scope.formalReport.GSBOTnum="1";
                if (undefined == $scope.formalReport.WATERSUPPLYBOT) {
                    $scope.formalReport.WATERSUPPLYBOT = [];
                }
                addBlankRow($scope.formalReport.WATERSUPPLYBOT);
                $scope.myObjtwo = {"display": "block"}
            } else {
                $scope.formalReport.GSBOTnum=null;
                $scope.myObjtwo = {"display": "none"}
                $scope.formalReport.WATERSUPPLYBOT = [];
            }
        }else if(paramVal=="GSTOT") {
            if ($("#projectTypeGSTOT").is(':checked')) {
                $scope.formalReport.GSTOTnum="1";
                if (undefined == $scope.formalReport.WATERSUPPLYTOT) {
                    $scope.formalReport.WATERSUPPLYTOT = [];
                }
                addBlankRow($scope.formalReport.WATERSUPPLYTOT);
                $scope.myObjthree = {"display": "block"}
            } else {
                $scope.formalReport.GSTOTnum=null;
                $scope.myObjthree = {"display": "none"}
                $scope.formalReport.WATERSUPPLYTOT = [];
            }
        } else if (paramVal == "Sanitation") {
            if ($("#projectTypeSanitation").is(':checked')) {
                $scope.formalReport.Sanitationnum="1";
                if (undefined == $scope.formalReport.HW) {
                    $scope.formalReport.HW = [];
                }
                addBlankRow($scope.formalReport.HW);
                $scope.myObjfour = {"display": "block"}
            } else {
                $scope.formalReport.Sanitationnum=null;
                $scope.myObjfour = {"display": "none"}
                $scope.formalReport.HW = [];
            }
        } else if (paramVal == "CommissionedOperation") {
            if ($("#projectTypeCommissionedOperation").is(':checked')) {
                $scope.formalReport.CommissionedOperationnum="1";
                if (undefined == $scope.formalReport.WTYY) {
                    $scope.formalReport.WTYY = [];
                }
                addBlankRow($scope.formalReport.WTYY);
                $scope.myObjfive = {"display": "block"}
            } else {
                $scope.formalReport.CommissionedOperationnum=null;
                $scope.myObjfive = {"display": "none"}
                $scope.formalReport.WTYY = [];
            }
        } else if (paramVal == "WSBOT") {
            if ($("#projectTypeWSBOT").is(':checked')) {
                $scope.formalReport.WSBOTnum="1";
                if (undefined == $scope.formalReport.WSBOT) {
                    $scope.formalReport.WSBOT = [];
                }
                addBlankRow($scope.formalReport.WSBOT);
                $scope.myObjsix = {"display": "block"}
            } else {
                $scope.formalReport.WSBOTnum=null;
                $scope.myObjsix = {"display": "none"}
                $scope.formalReport.WSBOT = [];
            }
        } else if (paramVal == "WSTOT") {
            if ($("#projectTypeWSTOT").is(':checked')) {
                $scope.formalReport.WSTOTnum="1";
                if (undefined == $scope.formalReport.WSTOT) {
                    $scope.formalReport.WSTOT = [];
                }
                addBlankRow($scope.formalReport.WSTOT);
                $scope.myObjseven = {"display": "block"}
            } else {
                $scope.formalReport.WSTOTnum=null;
                $scope.myObjseven = {"display": "none"}
                $scope.formalReport.WSTOT = [];
            }
        }
    }
    //当num个数改变时动态修改数据值
    $scope.oneNumChage=function(paramVal) {
        if (paramVal == "BT") {
            var num = $("#BTnum").val();
            var bt = $scope.formalReport.BTPPP;
            var i = bt.length;
            if (num != "" && num != "0") {
                if (num > i) {
                    $scope.formalReport.projectTypeBT = true;
                    for (var i; i < num; i++) {
                        function addBlankRow(array) {
                            array[i] = {};
                        }
                        addBlankRow($scope.formalReport.BTPPP);
                    }
                } else if (num < i) {
                    var len = i - num;
                    bt.splice(num, len);
                }
            } else if (num != "" && num == 0) {
                $scope.formalReport.projectTypeBT = false;
                $scope.formalReport.BTPPP = [];
            } else {
                $scope.formalReport.BTnum = i;
            }
        } else if (paramVal == "GSBOT") {
            var num = $("#GSBOTnum").val();
            var bt = $scope.formalReport.WATERSUPPLYBOT;
            var i = bt.length;
            if (num != "" && num != "0") {
                if (num > i) {
                    $scope.formalReport.projectTypeGSBOT = true;
                    for (var i; i < num; i++) {
                        function addBlankRow(array) {
                            array[i] = {};
                        }
                        addBlankRow($scope.formalReport.WATERSUPPLYBOT);
                    }
                } else if (num < i) {
                    var len = i - num;
                    bt.splice(num, len);
                }
            } else if (num != "" && num == 0) {
                $scope.formalReport.projectTypeGSBOT = false;
                $scope.formalReport.WATERSUPPLYBOT = [];
            } else {
                $scope.formalReport.GSBOTnum = i;
            }
        } else if (paramVal == "GSTOT") {
            var num = $("#GSTOTnum").val();
            var bt = $scope.formalReport.WATERSUPPLYTOT;
            var i = bt.length;
            if (num != "" && num != "0") {
                if (num > i) {
                    $scope.formalReport.projectTypeGSTOT = true;
                    for (var i; i < num; i++) {
                        function addBlankRow(array) {
                            array[i] = {};
                        }
                        addBlankRow($scope.formalReport.WATERSUPPLYTOT);
                    }
                } else if (num < i) {
                    var len = i - num;
                    bt.splice(num, len);
                }
            } else if (num != "" && num == 0) {
                $scope.formalReport.projectTypeGSTOT = false;
                $scope.formalReport.WATERSUPPLYTOT = [];
            } else {
                $scope.formalReport.GSTOTnum = i;
            }
        } else if (paramVal == "Sanitation") {
            var num = $("#Sanitationnum").val();
            var bt = $scope.formalReport.HW;
            var i = bt.length;
            if (num != "" && num != "0") {
                if (num > i) {
                    $scope.formalReport.projectTypeSanitation = true;
                    for (var i; i < num; i++) {
                        function addBlankRow(array) {
                            array[i] = {};
                        }
                        addBlankRow($scope.formalReport.HW);
                    }
                } else if (num < i) {
                    var len = i - num;
                    bt.splice(num, len);
                }
            } else if (num != "" && num == 0) {
                $scope.formalReport.projectTypeSanitation = false;
                $scope.formalReport.HW = [];
            } else {
                $scope.formalReport.Sanitationnum = i;
            }
        } else if (paramVal == "CommissionedOperation") {
            var num = $("#CommissionedOperationnum").val();
            var bt = $scope.formalReport.WTYY;
            var i = bt.length;
            if (num != "" && num != "0") {
                if (num > i) {
                    $scope.formalReport.projectTypeCommissionedOperation = true;
                    for (var i; i < num; i++) {
                        function addBlankRow(array) {
                            array[i] = {};
                        }
                        addBlankRow($scope.formalReport.WTYY);
                    }
                } else if (num < i) {
                    var len = i - num;
                    bt.splice(num, len);
                }
            } else if (num != "" && num == 0) {
                $scope.formalReport.projectTypeCommissionedOperation = false;
                $scope.formalReport.WTYY = [];
            } else {
                $scope.formalReport.CommissionedOperationnum = i;
            }
        } else if (paramVal == "WSBOT") {
            var num = $("#WSBOTnum").val();
            var bt = $scope.formalReport.WSBOT;
            var i = bt.length;
            if (num != "" && num != "0") {
                if (num > i) {
                    $scope.formalReport.projectTypeWSBOT = true;
                    for (var i; i < num; i++) {
                        function addBlankRow(array) {
                            array[i] = {};
                        }
                        addBlankRow($scope.formalReport.WSBOT);
                    }
                } else if (num < i) {
                    var len = i - num;
                    bt.splice(num, len);
                }
            } else if (num != "" && num == 0) {
                $scope.formalReport.projectTypeWSBOT = false;
                $scope.formalReport.WSBOT = [];
            } else {
                $scope.formalReport.WSBOTnum = i;
            }
        } else if (paramVal == "WSTOT") {
            var num = $("#WSTOTnum").val();
            var bt = $scope.formalReport.WSTOT;
            var i = bt.length;
            if (num != "" && num != "0") {
                if (num > i) {
                    $scope.formalReport.projectTypeWSTOT = true;
                    for (var i; i < num; i++) {
                        function addBlankRow(array) {
                            array[i] = {};
                        }
                        addBlankRow($scope.formalReport.WSTOT);
                    }
                } else if (num < i) {
                    var len = i - num;
                    bt.splice(num, len);
                }
            } else if (num != "" && num == 0) {
                $scope.formalReport.projectTypeWSTOT = false;
                $scope.formalReport.WSTOT = [];
            } else {
                $scope.formalReport.WSTOTnum = i;
            }
        }
    }

    //给第三部分添加行
    $scope.addtableRow=function(n){
        function addcellRow(array){
            var blankRow = {
                riskPoint:'',
                riskContent:'',
                pointsAndProblems:'',
                opinionAndCommitment:''
            }
            array.push(blankRow);
        }
        if(undefined==$scope.formalReport){
            $scope.formalReport={projectConcernsIssues:{}};
        }
        if(n==1){
            if(undefined==$scope.formalReport.projectConcernsIssues){
                $scope.formalReport.projectConcernsIssues={leadershipDecision:[]};
            }
            if(undefined==$scope.formalReport.projectConcernsIssues.leadershipDecision){
                $scope.formalReport.projectConcernsIssues.leadershipDecision=[];
            }
            addcellRow($scope.formalReport.projectConcernsIssues.leadershipDecision);
        }else if(n==2){
            if(undefined==$scope.formalReport.projectConcernsIssues){
                $scope.formalReport.projectConcernsIssues={investmentConditions:[]};
            }
            if(undefined==$scope.formalReport.projectConcernsIssues.investmentConditions){
                $scope.formalReport.projectConcernsIssues.investmentConditions=[];
            }
            addcellRow($scope.formalReport.projectConcernsIssues.investmentConditions);
        }else if(n==3){
            if(undefined==$scope.formalReport.projectConcernsIssues){
                $scope.formalReport.projectConcernsIssues={implementationRequirements:[]};
            }
            if(undefined==$scope.formalReport.projectConcernsIssues.implementationRequirements){
                $scope.formalReport.projectConcernsIssues.implementationRequirements=[];
            }
            addcellRow($scope.formalReport.projectConcernsIssues.implementationRequirements);
        }else if(n==4){
            if(undefined==$scope.formalReport.projectConcernsIssues){
                $scope.formalReport.projectConcernsIssues={prompt:[]};
            }
            if(undefined==$scope.formalReport.projectConcernsIssues.prompt){
                $scope.formalReport.projectConcernsIssues.prompt=[];
            }
            addcellRow($scope.formalReport.projectConcernsIssues.prompt);
        }
    }
    //第三分部删除行
    $scope.delrow=function(name,num){
        if(num==1) {
            var names = $scope.formalReport.projectConcernsIssues.leadershipDecision;
            names.splice(name, 1);
            if (names.length > 0) {
                $scope.formalReport.projectConcernsIssues.leadershipDecision = names;
            } else {
                $scope.formalReport.projectConcernsIssues.leadershipDecision = [];
            }
        }else if(num==2) {
            var names = $scope.formalReport.projectConcernsIssues.investmentConditions;
            names.splice(name, 1);
            if (names.length > 0) {
                $scope.formalReport.projectConcernsIssues.investmentConditions = names;
            } else {
                $scope.formalReport.projectConcernsIssues.investmentConditions = [];
            }
        }else if(num==3) {
            var names = $scope.formalReport.projectConcernsIssues.implementationRequirements;
            names.splice(name, 1);
            if (names.length > 0) {
                $scope.formalReport.projectConcernsIssues.implementationRequirements = names;
            } else {
                $scope.formalReport.projectConcernsIssues.implementationRequirements = [];
            }
        }else if(num==4) {
            var names = $scope.formalReport.projectConcernsIssues.prompt;
            names.splice(name, 1);
            if (names.length > 0) {
                $scope.formalReport.projectConcernsIssues.prompt = names;
            } else {
                $scope.formalReport.projectConcernsIssues.prompt = [];
            }
        }
    }
    $scope.addOption = function(){
        function addBlankRow(array){
            var blankRow = {
                option_content:'',
                option_execute:'',
                taskId:taskID
            }
            var size=0;
            for(attr in array){
                size++;
            }
            array[size]=blankRow;
        }

        if(undefined==$scope.formalReport){
            $scope.formalReport={optionList:[]};

        }
        if(undefined==$scope.formalReport.optionList){
            $scope.formalReport.optionList=[];
        }
        addBlankRow($scope.formalReport.optionList);
    }
    $scope.addRequire = function(){
        function addBlankRow(array){
            var blankRow = {
                require_content:'',
                require_execute:'',
                taskId:taskID
            }
            var size=0;
            for(attr in array){
                size++;
            }
            array[size]=blankRow;
        }

        if(undefined==$scope.formalReport){
            $scope.formalReport={requireList:[]};

        }
        if(undefined==$scope.formalReport.requireList){
            $scope.formalReport.requireList=[];
        }
        addBlankRow($scope.formalReport.requireList);
    }
    $scope.addLaw = function(){
        function addBlankRow(array){
            var blankRow = {
                lawOpinion:'',
                pendingConfirmation:false,
                canNotBeModified:false,
                taskId:taskID
            }
            var size=0;
            for(attr in array){
                size++;
            }
            array[size]=blankRow;
        }

        if(undefined==$scope.formalReport){
            $scope.formalReport={lawList:[]};

        }
        if(undefined==$scope.formalReport.lawList){
            $scope.formalReport.lawList=[];
        }
        addBlankRow($scope.formalReport.lawList);
    }
    $scope.addTechnology = function(){
        function addBlankRow(array){
            var blankRow = {
                technology_opinion:'',
                taskId:taskID
            }
            var size=0;
            for(attr in array){
                size++;
            }
            array[size]=blankRow;
        }

        if(undefined==$scope.formalReport){
            $scope.formalReport={technologyList:[]};

        }
        if(undefined==$scope.formalReport.technologyList){
            $scope.formalReport.technologyList=[];
        }
        addBlankRow($scope.formalReport.technologyList);
    }
    $scope.addFinance = function(){
        function addBlankRow(array){
            var blankRow = {
                finance_opinion:'',
                taskId:taskID
            }
            var size=0;
            for(attr in array){
                size++;
            }
            array[size]=blankRow;
        }

        if(undefined==$scope.formalReport){
            $scope.formalReport={financeList:[]};

        }
        if(undefined==$scope.formalReport.financeList){
            $scope.formalReport.financeList=[];
        }
        addBlankRow($scope.formalReport.financeList);
    }
    $scope.addhrResource = function(){
        function addBlankRow(array){
            var blankRow = {
                hrResource_opinion:'',
                taskId:taskID
            }
            var size=0;
            for(attr in array){
                size++;
            }
            array[size]=blankRow;
        }

        if(undefined==$scope.formalReport){
            $scope.formalReport={hrResourceList:[]};

        }
        if(undefined==$scope.formalReport.hrResourceList){
            $scope.formalReport.hrResourceList=[];
        }
        addBlankRow($scope.formalReport.hrResourceList);
    }
    $scope.commonDdelete = function(n){
        var commentsObj=null;
        if(n==0) {
            commentsObj = $scope.formalReport.optionList;
        }else if(n==1){
            commentsObj = $scope.formalReport.requireList;
        }else if(n==2) {
            commentsObj = $scope.formalReport.lawList;
        }else if(n==3){
            commentsObj = $scope.formalReport.technologyList;
        }else if(n==4) {
            commentsObj = $scope.formalReport.financeList;
        }else if(n==5){
            commentsObj = $scope.formalReport.hrResourceList;
        }
        if(commentsObj!=null){
            for(var i=0;i<commentsObj.length;i++){
                if(commentsObj[i].selected){
                    commentsObj.splice(i,1);
                    i--;
                }
            }
        }

    }
    $scope.dic=[];
    $scope.saveReport=function(callBack){
        if(typeof callBack == 'function') {
            if ($("#FormalReviewReport").valid()) {
                var postObj;
                var url;
                if (typeof ($scope.formalReport._id) != "undefined") {
                    url = 'formalAssessment/FormalReport/update';
                } else {
                    url = 'formalAssessment/FormalReport/create';
                }
                postObj = $scope.httpData(url, $scope.formalReport);
                postObj.success(function (data) {
                    if (data.result_code === 'S') {
                        $scope.formalReport._id = data.result_data;
                        if (typeof callBack == 'function') {
                            callBack();
                        } else {
                            $.alert("保存成功");
                        }
                        $("#wordbtn").show();
                    } else {
                        alert(data.result_name);
                    }
                })
            }
        }else{
            var postObj;
            var url;
            if (typeof ($scope.formalReport._id) != "undefined") {
                url = 'formalAssessment/FormalReport/update';
            } else {
                url = 'formalAssessment/FormalReport/create';
            }
            postObj = $scope.httpData(url, $scope.formalReport);
            postObj.success(function (data) {
                if (data.result_code === 'S') {
                    $scope.formalReport._id = data.result_data;
                    if (typeof callBack == 'function') {
                        callBack();
                    } else {
                        $.alert("保存成功");
                    }
                    $("#wordbtn").show();
                } else {
                    alert(data.result_name);
                }
            })
        }
    }
    //弹出是否上会的提示
    $scope.showSubmitModal = function() {
        $scope.saveReport(function(){
            var url = 'formalAssessment/FormalReport/SubmitAndupdate';
            var postObj = $scope.httpData(url, $scope.formalReport._id);
            var istrue=createwords();
            if(istrue){
               $scope.needMeetingRouter($scope.formalReport._id,"1");
            }

        });
    }
    //点击提交时先生成word，生成成功后方可上会
    function  createwords(){
        var boolean=true;
        var url =  'formalAssessment/FormalReport/getPfrAssessmentWord';
        $scope.httpData(url,$scope.formalReport._id).success(function (data) {
            if (data.result_code=="S") {
               // var filesPath=data.result_data.filePath;
               // var filesName=data.result_data.fileName;
               // window.location.href = srvUrl+"common/RcmFile/downLoad?filePath="+encodeURI(filesPath)+"&fileName="+encodeURI("正式评审-"+filesName+"报告-混合模式.docx");;
            } else {
                $.alert("提交前系统报表生成失败，请查看必填项是否填写完毕；如果全部正常填写请联系管理员！");
                boolean= false;
            }
        });
        return boolean;
    }
    $scope.getProjectFormalReviewByID=function(id){
        var  url = 'formalAssessment/ProjectFormalReview/getProjectFormalReviewByID';
        $scope.httpData(url,id).success(function(data){
            $scope.pfr  = data.result_data;
            $scope.formalReport.projectFormalId=$scope.pfr._id;
            $scope.formalReport.projectName=$scope.pfr.apply.projectName;
            $scope.formalReport.projectNo=$scope.pfr.apply.projectNo;
            if(null!=$scope.pfr.apply.reportingUnit){
                $scope.formalReport.reportingUnit=$scope.pfr.apply.reportingUnit.name;
            }
            var ptNameArr=[];
            var pt=$scope.pfr.apply.projectType;
            if(null!=pt && pt.length>0){
                for(var i=0;i<pt.length;i++){
                    ptNameArr.push(pt[i].VALUE);
                }
                $scope.formalReport.projectTypeName=ptNameArr.join(",");
            }
            $scope.formalReport.controllerVal="FormalReviewReport";
            $scope.formalReport.attachment=[{'file':'','fileName':''}];

            $scope.addtableRow(1);
            $scope.addtableRow(2);
            $scope.addtableRow(3);
            $scope.addtableRow(4);
            if(undefined==$scope.formalReport.optionList){
                $scope.addOption();
            }
            if(undefined==$scope.formalReport.requireList){
                $scope.addRequire();
            } if(undefined==$scope.formalReport.lawList){
                $scope.addLaw();
            }
            if(undefined==$scope.formalReport.technologyList){
                $scope.addTechnology();
            }
            if(undefined==$scope.formalReport.financeList){
                $scope.addFinance();
            }
            if(undefined==$scope.formalReport.hrResourceList){
                $scope.addhrResource();
            }
            if(null==$scope.formalReport.projectOverview){
                $scope.formalReport.projectOverview="项目由XX事业部通过XX途径获得，我方接手后由XX事业部负责运营。本项目的盈利模式为XX。";
            }
        });
    }
    $scope.getByID=function(id){
        var  url = 'formalAssessment/FormalReport/getByID';
        $scope.httpData(url,id).success(function(data){
            $scope.formalReport  = data.result_data;
            $(".mixed-template").css({"display":"block"});
            $scope.myObjone = {"display" : "block"};
            $scope.myObjtwo = {"display" : "block"};
            $scope.myObjthree = {"display" : "block"};
            $scope.myObjfour = {"display" : "block"};
            $scope.myObjfive = {"display" : "block"};
            $scope.myObjsix = {"display" : "block"};
            $scope.myObjseven = {"display" : "block"};
            var files=$scope.formalReport.attachment;
            if(files==""){
                $scope.formalReport.attachment=[{'file':'','fileName':''}];
            }
            if(action=="View"){
                /*$('input').attr("disabled","disabled");
                $('textarea').attr("disabled","disabled");
                $('button').attr("disabled","disabled");*/
                $('input').attr("readonly","readonly");
                $('textarea').attr("readonly","readonly");
                $('select').attr("disabled","disabled");
                $('#savebtnreport').hide();
                $("#submitbnt").attr("disabled",false);
                $('#wordbtn').attr("disabled",false);
            }

        });
    }
    var action = $routeParams.action;
    $scope.actionparm=$routeParams.action;
    if(action=="Create"){
        $scope.getProjectFormalReviewByID(objId);
        $scope.formalReport.create_by=$scope.credentials.UUID;
        $scope.formalReport.create_name=$scope.credentials.userName;
        $("#wordbtn").hide();
    }else if(action=="Update"){
        $scope.getByID(objId);
    }else if(action=="View"){
        $scope.getByID(objId);
        $("#submitbnt").hide();
    }
    $scope.setDirectiveParam=function(columnName,type,index){
        $scope.columnName=columnName;
        $scope.columnType=type;
        $scope.columnIndex=index;
    }
    $scope.setDirectiveOrgList=function(id,name){
        var params=$scope.columnName;
        if(params=="invertmentUnit"){
            $scope.formalReport.invertmentUnit = {"name":name,value:id};
            $("#invertmentUnitName").val(name);
            $("label[for='invertmentUnitName']").remove();
        }
        if(params=="operationsUnit"){
            $scope.formalReport.operationsUnit= {"name":name,value:id};
            $("#operationsUnitName").val(name);
            $("label[for='operationsUnitName']").remove();
        }
        if(params=="tenderCompany"){
            var i=$scope.columnIndex;
            var type=$scope.columnType;
            if(type=="btpp"){
                $scope.formalReport.BTPPP[i].tenderCompany= {"name":name,value:id};
            }else if(type=="gsbot"){
                $scope.formalReport.WATERSUPPLYBOT[i].tenderCompany= {"name":name,value:id};
            }else if(type=="gstot"){
                $scope.formalReport.WATERSUPPLYTOT[i].tenderCompany= {"name":name,value:id};
            }else if(type=="gshw"){
                $scope.formalReport.HW[i].tenderCompany= {"name":name,value:id};
            }else if(type=="wsbot"){
                $scope.formalReport.WSBOT[i].tenderCompany= {"name":name,value:id};
            }else if(type=="wstot"){
                $scope.formalReport.WSTOT[i].tenderCompany= {"name":name,value:id};
            }
        }
    }
    $scope.createWord=function(id){
        startLoading();
        var url = 'formalAssessment/FormalReport/getPfrAssessmentWord';
        $scope.httpData(url,id).success(function (data) {
            if (data.result_code=="S") {
                var filesPath=data.result_data.filePath;
                var filesName=data.result_data.fileName;
                window.location.href = srvUrl+"common/RcmFile/downLoad?filePath="+encodeURI(filesPath)+"&fileName="+encodeURI("正式评审-"+filesName+"报告-混合模式.docx");

            } else {
                $.alert("报表生成失败，请查看必填项是否填写完毕；如果全部正常填写请联系管理员！");

            }
        });
        endLoading();
    }
    function FormatDateto() {
        var date = new Date();
        var paddNum = function(num){
            num += "";
            return num.replace(/^(\d)$/,"0$1");
        }
        return date.getFullYear()+""+paddNum(date.getMonth()+1);
    }
    $scope.errorAttach=[];
    $scope.uploadfformal = function (file,errorFile, idx) {
        if(errorFile && errorFile.length>0){
        	var errorMsg = fileErrorMsg(errorFile);
            $scope.errorAttach[idx]={msg:errorMsg};
        }else if(file){
            var fileFolder = "formalReport/";
            if($routeParams.action=='Create') {
                if(undefined==$scope.formalReport.projectNo){
                    $.alert("请先选择项目然后上传附件");
                    return false;
                }
                var no=$scope.formalReport.projectNo;
                var myDate = new Date();
                var paddNum = function(num){
                    num += "";
                    return num.replace(/^(\d)$/,"0$1");
                }
                fileFolder=fileFolder+FormatDateto()+"/"+no;
            }else{
                var dates=$scope.formalReport.create_date;
                var no=$scope.formalReport.projectNo;
                var strs= new Array(); //定义一数组
                strs=dates.split("-"); //字符分割
                dates=strs[0]+strs[1]; //分割后的字符输出
                fileFolder=fileFolder+dates+"/"+no;
            }
            $scope.errorAttach[idx]={msg:''};
            Upload.upload({
                url:srvUrl+'common/RcmFile/upload',
                data: {file: file, folder:fileFolder}
            }).then(function (resp) {
                var retData = resp.data.result_data[0];
                retData.version = "0";
                $scope.formalReport.attachment=[retData];
            }, function (resp) {
                console.log('Error status: ' + resp.status);
            }, function (evt) {
                var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                $scope["progress"+idx]=progressPercentage == 100 ? "":progressPercentage+"%";
            });
        }
    };
    $scope.downLoadFile = function(idx){
        var filePath = $scope.formalReport.attachment[0].filePath, fileName = $scope.formalReport.attachment[0].fileName;
        window.location.href = srvUrl+"common/RcmFile/downLoad?filePath="+filePath+"&fileName="+encodeURI(fileName);
    }
    $scope.getSelectTypeByCode=function(typeCode){
        var  url = 'common/commonMethod/selectDataDictionByCode';
        $scope.httpData(url,typeCode).success(function(data){
            if(data.result_code === 'S'){
                $scope.payTypeList=data.result_data;
            }else{
                alert(data.result_name);
            }
        });
    }
    $scope.getSelectTypeByCode2=function(typeCode){
        var  url = 'common/commonMethod/selectDataDictionByCode';
        $scope.httpData(url,typeCode).success(function(data){
            if(data.result_code === 'S'){
                $scope.csTypeList=data.result_data;
            }else{
                alert(data.result_name);
            }
        });
    }

    $scope.$on('ngRepeatFinished', function () {
        var optionsDate = {
            todayBtn: "linked",
            orientation: $('body').hasClass('right-to-left') ? "auto right" : 'auto auto'
        }
        $('.loadtime').datepicker(optionsDate);
    });

    angular.element(document).ready(function() {
        $scope.getSelectTypeByCode("10");
        $scope.getSelectTypeByCode2("11");
    });
}]);

ctmApp.register.controller('SpecialApprovalMeeting',['$http','$scope','$location','$routeParams', function ($http,$scope,$location,$routeParams,Upload,$timeout) {
    var objId = $routeParams.id;
    $scope.saveReport=function(){
        var postObj;
        var url;
        if(typeof ($scope.formalReport._id) !="undefined"){
            url ='formalAssessment/FormalReport/update';
        }else{
            url = 'formalAssessment/FormalReport/create';
        }
        postObj=$scope.httpData(url,$scope.formalReport);
        postObj.success(function(data){
            if(data.result_code === 'S'){
                $location.path("/FormalReportList");
            }else{
                alert(data.result_name);
            }
        })
    }
    $scope.getByID=function(id){
        var  url = 'projectPreReview/Meeting/findFormalAndReport';
        $scope.httpData(url,id).success(function(data){
            $scope.formalReport  = data.result_data.Report;
            $scope.pfr  = data.result_data.Formal;
        });
    }
    $scope.getByID(objId);
    $scope.downLoadFile = function(idx){
        var filePath = idx.filePath, fileName = idx.fileName;
        window.location.href = srvUrl+"common/RcmFile/downLoad?filePath="+filePath+"&fileName="+encodeURI(fileName);
    }


}]);
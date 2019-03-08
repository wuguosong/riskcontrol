define(['app', 'Service', 'select2'], function (app) {
    app
        .register.controller('hazardousWasteReportCtrl', ['$http', '$scope', '$location', '$stateParams', 'BEWG_URL', 'Window',
        function ($http, $scope, $location, $stateParams, BEWG_URL, Window, Upload) {
            console.log('hazardousWasteReport');


            //初始化
            $scope.tabIndex =  $stateParams.tabIndex;
            $scope.controller_val = $location.$$url.split("/")[1];
            var complexId = $stateParams.id;
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
            $scope.dic=[];
            
            var action = $stateParams.action;
            $scope.actionparm=$stateParams.action;
            $scope.initData = function(){
                if(action=="Create"){
                    $scope.title = "正式评审报告-新增";
                    $scope.getProjectFormalReviewByID(objId);
                    $scope.formalReport.create_by=$scope.credentials.UUID;
                    $scope.formalReport.create_name=$scope.credentials.userName;
                    $("#wordbtn").hide();
                }else if(action=="Update"){
                    $scope.title = "正式评审报告-修改";
                    $scope.getByID(objId);
                }else if(action=="View"){
                    $scope.title = "正式评审报告-查看";
                    $scope.getByID(objId);
                    $("#savebtn").hide();
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

            $scope.addRawMaterials = function(){
                function addBlankRow(array){
                    var blankRow = {
                        rawMaterialName:'',
                        marketContent:'',
                        marketDataResource:'',
                        areaContent:'',
                        areaDataResource:'',
                        bidCompanyContent:'',
                        bidCompanyDataResource:'',
                        taskId:taskID
                    }
                    var size=0;
                    for(attr in array){
                        size++;
                    }
                    array[size]=blankRow;
                }

                if(undefined==$scope.formalReport){
                    $scope.formalReport={rawMaterial:[]};

                }
                if(undefined==$scope.formalReport.rawMaterial){
                    $scope.formalReport.rawMaterial=[];
                }
                addBlankRow($scope.formalReport.rawMaterial);
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
                }else if(n==6) {
                    commentsObj = $scope.formalReport.rawMaterial;
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

            $scope.getProjectFormalReviewByID=function(id){
                $http({
                    method:'post',
                    url: BEWG_URL.SelectReportPfrView,
                    data: $.param({"id":id})
                }).success(function(data){

                    $scope.pfr  = data.result_data;
                    $scope.formalReport.projectFormalId=$scope.pfr.id;
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
                    $scope.formalReport.controllerVal = $scope.controller_val;
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
                    if(undefined==$scope.formalReport.rawMaterial){
                        $scope.addRawMaterials();
                    }
                    if(null==$scope.formalReport.projectOverview) {
                        $scope.formalReport.projectOverview="项目由XX事业部通过XX途径获得，我方接手后由XX事业部负责运营。 本项目的盈利模式为XX。";
                    }
                    if(null==$scope.formalReport.marketCapacityAnalysis) {
                        $scope.formalReport.marketCapacityAnalysis="本项目原材料以XX、XX、XX等，具体原材料市场容量及具体来源如下：";
                    }
                    if(null==$scope.formalReport.risksProblems) {
                        $scope.formalReport.risksProblems="根据法律专业评审意见（XX条）以及技术专业评审意见（具体详见附件1），现就本项目提出XX条风险及问题，其中第1条至第XX条是需要决策机构重点关注，其余为通报列示供决策参考。";
                    }
                    if(null==$scope.formalReport.lawContent) {
                        $scope.formalReport.lawContent="法律共提出XX条意见，其中认为可以修订但未与对方确认的XX条，对方明确表示不同意修改的XX条。";
                    }
                    if(null==$scope.formalReport.technicalReviewOpinions) {
                        $scope.formalReport.technicalReviewOpinions="技术中心共提出XX条意见，主要围绕XX问题，具体如下： ";
                    }
                    if(null==$scope.formalReport.financialProfessionalReviewOpinion) {
                        $scope.formalReport.financialProfessionalReviewOpinion="财金资源中心共提出XX条意见，主要围绕XX问题，具体如下：";
                    }
                    if(null==$scope.formalReport.costCostReview) {
                        $scope.formalReport.costCostReview="基于技术中心给定的成本费用意见，本报告测算所用成本费用如下：";
                    }
                    $scope.addbusinessModel();

                }).error(function(data,status,headers,config){
                    Window.alert(status);
                });
            }

            $scope.saveReport4 = function(callBack){
                var bankLoan = $("#bankLoan").val();
                var privateCapital= $("#privateCapital").val();
                var sum = parseInt(bankLoan)+parseInt(privateCapital);
                var total = parseInt(100);
                if(typeof callBack == 'function') {
                    if ($("#HazardousWasteReport").valid() && sum == total) {
                        var url_post;
                        if (typeof ($scope.formalReport._id) != "undefined") {
                            url_post = BEWG_URL.UpdateReportPfr;
                        }else{
                            var boolean = $scope.isReportExist();
                            if(boolean){
                                Window.alert("请勿重复保存数据!");
                                return false;
                            }
                            url_post = BEWG_URL.SaveReportPfr;
                        }
                        $http({
                            method:'post',
                            url: url_post,
                            data: $.param({"json":JSON.stringify($scope.formalReport)})
                        }).success(function(data){
                            if(data.success){
                                $scope.formalReport._id = data.result_data;
                                if(typeof callBack == 'function'){
                                    callBack();
                                }else{
                                    Window.alert("保存成功!");
                                }
                                $("#wordbtn").show();
                            }else{
                                Window.alert(data.result_name);
                            }
                        }).error(function(data,status,headers,config){
                            Window.alert(status);
                        });
                    } else {
                        Window.alert("必填项未填或银行贷款和自有资金之合须等于100%！");
                    }
                }else{
                    if (sum == total) {
                        var url_post;
                        if (typeof ($scope.formalReport._id) != "undefined") {
                            url_post = BEWG_URL.UpdateReportPfr;
                        }else{
                            var boolean = $scope.isReportExist();
                            if(boolean){
                                Window.alert("请勿重复保存数据!");
                                return false;
                            }
                            url_post = BEWG_URL.SaveReportPfr;
                        }
                        $http({
                            method:'post',
                            url: url_post,
                            data: $.param({"json":JSON.stringify($scope.formalReport)})
                        }).success(function(data){
                            if(data.success){
                                $scope.formalReport._id = data.result_data;
                                if(typeof callBack == 'function'){
                                    callBack();
                                }else{
                                    Window.alert("保存成功!");
                                }
                                $("#wordbtn").show();
                            }else{
                                Window.alert(data.result_name);
                            }
                        }).error(function(data,status,headers,config){
                            Window.alert(status);
                        });
                    } else {
                        Window.alert("银行贷款和自有资金之合须等于100%！");
                    }
                }
            }

            $scope.isReportExist = function(){
                var boolean = false;
                $.ajax({
                    url: BEWG_URL.IsExistReportPfr,
                    type: "POST",
                    dataType: "json",
                    data: {"businessid": objId},
                    async: false,
                    success: function(data){
                        if(data.result_data > 0){
                            boolean = true;
                        }
                    }
                });
                return boolean;
            }

            //检查是否可提交
            $scope.isPossible2Submit = function(){
                var boolean = false;
                $.ajax({
                    url: BEWG_URL.IsSubmitReportPfr,
                    type: "POST",
                    dataType: "json",
                    data: {"projectFormalId":$scope.formalReport.projectFormalId},
                    async: false,
                    success: function(data){
                        if(data.result_data){
                            boolean = true;
                        }
                    }
                });
                return boolean;
            }

            //提交报告并更改状态
            $scope.showSubmitModal = function() {
                show_Mask();
                var flag = $scope.isPossible2Submit();
                if(flag){
                    $scope.saveReport4(function(){
                        $http({
                            method:'post',
                            url: BEWG_URL.SubmitReportPfr,
                            data: $.param({"id":$scope.formalReport._id,"projectFormalId":$scope.formalReport.projectFormalId})
                        }).success(function(data){
                            if(data.success){
                                var istrue = $scope.createwords();
                                if(istrue){
                                    hide_Mask();
                                    Window.alert("提交成功!");
                                    $('input').attr("readonly","readonly");
                                    $('textarea').attr("readonly","readonly");
                                    $('button').attr("disabled","disabled");
                                    $("#savebtn").hide();
                                    $("#submitbnt").hide();
                                    $('#wordbtn').attr("disabled",false);
                                    $(".modal-footer button").attr({"disabled":false});
                                }else{
                                    hide_Mask();
                                    Window.alert("提交失败，请检查是否填写完整后重新提交或联系管理员!");
                                    return false;
                                }
                            }
                        }).error(function(data,status,headers, config){
                            hide_Mask();
                            Window.alert(status);
                            return false;
                        });
                    });
                }else{
                    hide_Mask();
                    Window.alert("请确保流程已结束!");
                    return false;
                }
            }
            //点击提交时先生成word，生成成功后方可上会
            $scope.createwords = function(){
                var boolean = true;
                $.ajax({
                    url: BEWG_URL.CreateReportPfrWord,
                    type: "POST",
                    dataType: "json",
                    data: {"id":$scope.formalReport._id},
                    async: false,
                    success: function(data){
                        if(!data.success){
                            boolean = false;
                            Window.alert("提交前系统报表生成失败，请查看必填项是否填写完毕；如果全部正常填写请联系管理员!");
                        }
                    }
                });
                return boolean;
            }

            $scope.setDirectiveParam=function(columnName){
                $scope.columnName=columnName;
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
                    $scope.formalReport.tenderCompany = {"name":name,value:id};
                    $("#tenderCompanyName").val(name);
                    $("label[for='tenderCompanyName']").remove();
                }
            }
            $scope.getByID=function(id){
                $http({
                    method:'post',
                    url: BEWG_URL.SelectReportPrfrById,
                    data: $.param({"id":id})
                }).success(function(data){
                    $scope.formalReport  = data.result_data;
                    if(action=="View"){
                        $('input').attr("readonly","readonly");
                        $('textarea').attr("readonly","readonly");
                        $('button').attr("disabled","disabled");
                        $("#submitbnt").attr("disabled",false);
                        $('#wordbtn').attr("disabled",false);
                    }

                }).error(function(data,status,headers,config){
                    Window.alert(status);
                });
            }

            $scope.createWord=function(id){
                startLoading();
                $http({
                    method:'post',
                    url: BEWG_URL.CreateReportPfrWord,
                    data: $.param({"id":id})
                }).success(function(data){
                    if (data.success) {
                        var filesPath=data.result_data.filePath;
                        var index = filesPath.lastIndexOf(".");
                        var str = filesPath.substring(index + 1, filesPath.length);
                        var filesName=data.result_data.fileName;

                        window.location.href = srvUrl+"file/downloadFile.do?filepaths="+encodeURI(filesPath)+"&filenames="+encodeURI(encodeURI("正式评审-"+filesName+"报告-单体模式.")) + str;

                    } else {
                        Window.alert("报表生成失败，请查看必填项是否填写完毕；如果全部正常填写请联系管理员！");
                    }
                }).error(function(data,status,headers, config){
                    Window.alert(status);
                });
                endLoading();
            }
            $scope.addbusinessModel = function(){
                var boolean=true;
                function addBlankRow(array){
                    var blankRow = {
                        value:null
                    }
                    var size=0;
                    for(attr in array){
                        size++;
                        /*if(size==3){
                            boolean=false;
                            break;
                        }*/
                    }
                    /*if(size==3){
                        alert("已经超出限制！");
                        return false;
                    }*/
                    array[size]=blankRow;
                }
                if(undefined==$scope.formalReport){
                    $scope.formalReport={productionScales:[],mainIncomes:[],mainCosts:[],
                        materialCosts:[],manufacturingCostss:[],AmortizationOfDepreciations:[],
                        subsidiesIncomes:[],annualNetProfits:[],
                        workingCapitalNeedss:[],loanDemands:[],
                        totalInvestments:[],ROIs:[],recentProfitMarginss:[]
                    };
                }
                /*  if(undefined==$scope.formalReport.fProjectNames){
                      $scope.formalReport.fProjectNames=[];
                  }
                  addBlankRow($scope.formalReport.fProjectNames);*/

                if (undefined == $scope.formalReport.productionScales) {
                    $scope.formalReport.productionScales = [];
                }
                addBlankRow($scope.formalReport.productionScales);
                if(boolean) {
                    if (undefined == $scope.formalReport.mainIncomes) {
                        $scope.formalReport.mainIncomes = [];
                    }
                    addBlankRow($scope.formalReport.mainIncomes);
                    if (undefined == $scope.formalReport.mainCosts) {
                        $scope.formalReport.mainCosts = [];
                    }
                    addBlankRow($scope.formalReport.mainCosts);
                    if (undefined == $scope.formalReport.materialCosts) {
                        $scope.formalReport.materialCosts = [];
                    }
                    addBlankRow($scope.formalReport.materialCosts);
                    if (undefined == $scope.formalReport.manufacturingCostss) {
                        $scope.formalReport.manufacturingCostss = [];
                    }
                    addBlankRow($scope.formalReport.manufacturingCostss);

                    if (undefined == $scope.formalReport.AmortizationOfDepreciations) {
                        $scope.formalReport.AmortizationOfDepreciations = [];
                    }
                    addBlankRow($scope.formalReport.AmortizationOfDepreciations);

                    if (undefined == $scope.formalReport.subsidiesIncomes) {
                        $scope.formalReport.subsidiesIncomes = [];
                    }
                    addBlankRow($scope.formalReport.subsidiesIncomes);

                    if (undefined == $scope.formalReport.annualNetProfits) {
                        $scope.formalReport.annualNetProfits = [];
                    }
                    addBlankRow($scope.formalReport.annualNetProfits);

                    if (undefined == $scope.formalReport.workingCapitalNeedss) {
                        $scope.formalReport.workingCapitalNeedss = [];
                    }
                    addBlankRow($scope.formalReport.workingCapitalNeedss);
                    if (undefined == $scope.formalReport.loanDemands) {
                        $scope.formalReport.loanDemands = [];
                    }
                    addBlankRow($scope.formalReport.loanDemands);
                    if (undefined == $scope.formalReport.totalInvestments) {
                        $scope.formalReport.totalInvestments = [];
                    }
                    addBlankRow($scope.formalReport.totalInvestments);
                    if (undefined == $scope.formalReport.ROIs) {
                        $scope.formalReport.ROIs = [];
                    }
                    addBlankRow($scope.formalReport.ROIs);
                    if (undefined == $scope.formalReport.recentProfitMarginss) {
                        $scope.formalReport.recentProfitMarginss = [];
                    }
                    addBlankRow($scope.formalReport.recentProfitMarginss);
                }
            }
            $scope.commonbusinessModelDdelete = function(n){
                /* var fProjectNames= $scope.formalReport.fProjectNames;
                 if(null!= fProjectNames){
                     delete fProjectNames.splice(n,1);
                     var newComments = [],size = 0;
                     for (var obj in fProjectNames) {
                         newComments[size] =fProjectNames[obj];
                         size++;
                     }
                     $scope.formalReport.fProjectNames=newComments;
                 }*/
                var productionScales=  $scope.formalReport.productionScales;
                if(null!=productionScales){
                    productionScales.splice(n,1);
                }
                var mainIncomes = $scope.formalReport.mainIncomes;
                if(null!=mainIncomes){
                    mainIncomes.splice(n,1);
                }
                var mainCosts = $scope.formalReport.mainCosts;
                if(null!=mainCosts){
                    mainCosts.splice(n,1);
                }
                var materialCosts = $scope.formalReport.materialCosts;
                if(null!=materialCosts){
                    materialCosts.splice(n,1);
                }

                var manufacturingCostss =$scope.formalReport.manufacturingCostss;
                if(null!=manufacturingCostss){
                    delete manufacturingCostss.splice(n,1);
                }
                var AmortizationOfDepreciations=$scope.formalReport.AmortizationOfDepreciations;
                if(null!=AmortizationOfDepreciations){
                    AmortizationOfDepreciations.splice(n,1);
                }
                var subsidiesIncomes=$scope.formalReport.subsidiesIncomes;
                if(null!=subsidiesIncomes){
                    subsidiesIncomes.splice(n,1);
                }
                var annualNetProfits=$scope.formalReport.annualNetProfits;
                if(null!=annualNetProfits){
                    annualNetProfits.splice(n,1);
                }
                var workingCapitalNeedss=$scope.formalReport.workingCapitalNeedss;
                if(null!=workingCapitalNeedss){
                    workingCapitalNeedss.splice(n,1);
                }
                var loanDemands=$scope.formalReport.loanDemands;
                if(null!=loanDemands){
                    loanDemands.splice(n,1);
                }
                var totalInvestments=$scope.formalReport.totalInvestments;
                if(null!=totalInvestments){
                    totalInvestments.splice(n,1);
                }
                var ROIs=$scope.formalReport.ROIs;
                if(null!=ROIs){
                    ROIs.splice(n,1);
                }
                var recentProfitMarginss=$scope.formalReport.recentProfitMarginss;
                if(null!=recentProfitMarginss){
                    recentProfitMarginss.splice(n,1);
                }
            }
            // 返回列表
            $scope.cancel = function (){
                $location.path("/index/FormalReportList_new/" + $scope.tabindex);
            };

            $scope.initData();

        }]);
});
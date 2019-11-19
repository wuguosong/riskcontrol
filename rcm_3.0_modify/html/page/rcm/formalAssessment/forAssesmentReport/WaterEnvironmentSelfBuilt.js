/********
 * Created by dsl on 17/6/29.
 * 正式评审项目
 *********/
ctmApp.register.controller('WaterEnvironmentSelfBuilt', ['$http', '$scope', '$location', '$routeParams', 'Upload',
    function ($http, $scope, $location, $routeParams, Upload) {

        //初始化
        $scope.oldUrl = $routeParams.url;
        $scope.tabIndex = $routeParams.tabIndex;
        $scope.controller_val = $location.$$url.split("/")[1];
        var complexId = $routeParams.id;
        $scope.paramId = complexId;
        var params = complexId.split("@");
        if (null != params[1] && "" != params[1]) {
            $scope.flag = params[1];
        }
        if (null != params[2] && "" != params[2] && null != params[3] && "" != params[3] && null != params[4] && "" != params[4] && null != params[5] && "" != params[5] && null != params[6] && "" != params[6]) {
            $scope.reportReturnId = params[2] + "@" + params[3] + "@" + params[4] + "@" + params[5] + "@" + params[6];
        }
        var objId = params[0];
        var taskID = 'wwxosnsosjdsnsjnddID';
        $scope.taskID = taskID;
        $scope.formalReport = {};
        var params = $scope.columnName;
        $scope.formalReport.leadershipDecision = [];

        // 获取项目模式
        $scope.getprojectmodel = function (key) {
            var url = "common/commonMethod/selectsyncbusinessmodel";
            $scope.httpData(url, key).success(function (data) {
                if (data.result_code === 'S') {
                    $scope.projectModelValue = data.result_data;
                } else {
                    alert(data.result_name);
                }
            });
        };

        var action = $routeParams.action;
        $scope.actionparm = $routeParams.action;
        $scope.initData = function () {
            if (action == "Create") {
                $scope.boundaryView1 = {};
                $scope.earningsView2 = {};
                $scope.risksView3 = {};
                $scope.interspaceView4 = {};
                $scope.conclusionView5 = {};
                $scope.executeView6 = {};
                $scope.attchView6 = {};
                $scope.attchView7 = {};
                $scope.attchView8 = {};
                $scope.attchView9 = [];
                $scope.title = "正式评审报告-新增";
                $scope.getProjectFormalReviewByID(objId);
                $scope.formalReport.create_by = $scope.credentials.UUID;
                $scope.formalReport.create_name = $scope.credentials.userName;
                $scope.boundaryView1.governmentProcurement = false;
                $scope.boundaryView1.projectProcedure = false;
                $scope.boundaryView1.projectWarehouse = false;
                $scope.boundaryView1.signSubject = false;
                $scope.boundaryView1.whetherConstructionIsCompleted = false;
                $scope.boundaryView1.whetherWeNeedToBuild = false;
                $scope.boundaryView1.isContainsConstruction = false;
                $scope.boundaryView1.whetherToObtainLoanTermSheet = false;
                $scope.boundaryView1.whetherNeedAddLetter = false;
                $scope.boundaryView1.wetherCommissioningCharge = false;
                $scope.boundaryView1.wetherCommissioningChargeTwo = false;
                // if ($scope.formalReport.boundaryView1.projectModel != undefined && $scope.formalReport.boundaryView1.projectModel != null && $scope.formalReport.boundaryView1.projectModel != {}) {
                //     $("#projectmodeboxName").select2("val", " ");
                //     commonModelValue2('projectmodebox', $scope.pfr.apply.projectModel);
                // }
                $("#wordbtn").hide();
            } else if (action == "Update") {
                $scope.title = "正式评审报告-修改";
                $scope.getByID(objId);
            } else if (action == "View") {
                $scope.title = "正式评审报告-查看";
                $scope.getByID(objId);
                $("#savebtn").hide();
                $("#submitbnt").hide();
            }
        }


        //当选中时添加数组
        $scope.changePr = function (paramVal) {
            function addBlankRow(array) {
                array[0] = {};
            }

            if (paramVal == "BT") {
                if ($("#projectTypeBT").is(':checked')) {
                    $scope.formalReport.BTnum = "1";
                    if (undefined == $scope.formalReport.BTPPP) {
                        $scope.formalReport.BTPPP = [];
                    }
                    addBlankRow($scope.formalReport.BTPPP);
                    $scope.myObjone = {"display": "block"}
                } else {
                    $scope.formalReport.BTnum = null;
                    $scope.myObjone = {"display": "none"}
                    $scope.formalReport.BTPPP = [];
                }
            } else if (paramVal == "GSBOT") {
                if ($("#projectTypeGSBOT").is(':checked')) {
                    $scope.formalReport.GSBOTnum = "1";
                    if (undefined == $scope.formalReport.WATERSUPPLYBOT) {
                        $scope.formalReport.WATERSUPPLYBOT = [];
                    }
                    addBlankRow($scope.formalReport.WATERSUPPLYBOT);
                    $scope.myObjtwo = {"display": "block"}
                } else {
                    $scope.formalReport.GSBOTnum = null;
                    $scope.myObjtwo = {"display": "none"}
                    $scope.formalReport.WATERSUPPLYBOT = [];
                }
            } else if (paramVal == "GSTOT") {
                if ($("#projectTypeGSTOT").is(':checked')) {
                    $scope.formalReport.GSTOTnum = "1";
                    if (undefined == $scope.formalReport.WATERSUPPLYTOT) {
                        $scope.formalReport.WATERSUPPLYTOT = [];
                    }
                    addBlankRow($scope.formalReport.WATERSUPPLYTOT);
                    $scope.myObjthree = {"display": "block"}
                } else {
                    $scope.formalReport.GSTOTnum = null;
                    $scope.myObjthree = {"display": "none"}
                    $scope.formalReport.WATERSUPPLYTOT = [];
                }
            } else if (paramVal == "Sanitation") {
                if ($("#projectTypeSanitation").is(':checked')) {
                    $scope.formalReport.Sanitationnum = "1";
                    if (undefined == $scope.formalReport.HW) {
                        $scope.formalReport.HW = [];
                    }
                    addBlankRow($scope.formalReport.HW);
                    $scope.myObjfour = {"display": "block"}
                } else {
                    $scope.formalReport.Sanitationnum = null;
                    $scope.myObjfour = {"display": "none"}
                    $scope.formalReport.HW = [];
                }
            } else if (paramVal == "CommissionedOperation") {
                if ($("#projectTypeCommissionedOperation").is(':checked')) {
                    $scope.formalReport.CommissionedOperationnum = "1";
                    if (undefined == $scope.formalReport.WTYY) {
                        $scope.formalReport.WTYY = [];
                    }
                    addBlankRow($scope.formalReport.WTYY);
                    $scope.myObjfive = {"display": "block"}
                } else {
                    $scope.formalReport.CommissionedOperationnum = null;
                    $scope.myObjfive = {"display": "none"}
                    $scope.formalReport.WTYY = [];
                }
            } else if (paramVal == "WSBOT") {
                if ($("#projectTypeWSBOT").is(':checked')) {
                    $scope.formalReport.WSBOTnum = "1";
                    if (undefined == $scope.formalReport.WSBOT) {
                        $scope.formalReport.WSBOT = [];
                    }
                    addBlankRow($scope.formalReport.WSBOT);
                    $scope.myObjsix = {"display": "block"}
                } else {
                    $scope.formalReport.WSBOTnum = null;
                    $scope.myObjsix = {"display": "none"}
                    $scope.formalReport.WSBOT = [];
                }
            } else if (paramVal == "WSTOT") {
                if ($("#projectTypeWSTOT").is(':checked')) {
                    $scope.formalReport.WSTOTnum = "1";
                    if (undefined == $scope.formalReport.WSTOT) {
                        $scope.formalReport.WSTOT = [];
                    }
                    addBlankRow($scope.formalReport.WSTOT);
                    $scope.myObjseven = {"display": "block"}
                } else {
                    $scope.formalReport.WSTOTnum = null;
                    $scope.myObjseven = {"display": "none"}
                    $scope.formalReport.WSTOT = [];
                }
            }
        }
        //当num个数改变时动态修改数据值
        $scope.oneNumChage = function (paramVal) {
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
        $scope.addtableRow = function (n) {
            console.log(n);

            function addcellRow(array, type) {
                console.log(array);
                var blankRow = {};
                if (type == 'projectConcernsIssues') {
                    blankRow = {
                        riskPoint: '',
                        riskContent: '',
                        pointsAndProblems: '',
                        opinionAndCommitment: ''
                    }
                } else if (type == 'fundUsetable') {
                    blankRow = {
                        fundsUseTime: '',
                        capitalRequirement: '',
                        fundUse: '',
                        paymentTerms: '',
                        sourceOfFunds: ''
                    }
                } else if (type == 'profitContributionTable') {
                    blankRow = {
                        year: '',
                        expectedCompletionRatio: '',
                        expectedOutputValue: '',
                        grossProfit: ''
                    }
                } else if (type == 'drug') {
                    blankRow = {
                        reagentCostName: '',
                        reagentCostYear: '',
                        reagentCostTonsWater: '',
                        reagentCostRemarks: '',
                        reagentCostExplain: ''
                    }
                }
                array.push(blankRow);
            }

            if (undefined == $scope.formalReport) {
                $scope.formalReport = {projectConcernsIssues: {}};
            }
            if (n == 1) {
                if (undefined == $scope.risksView3.projectConcernsIssues) {
                    $scope.risksView3.projectConcernsIssues = {leadershipDecision: []};
                }
                if (undefined == $scope.risksView3.projectConcernsIssues.leadershipDecision) {
                    $scope.risksView3.projectConcernsIssues.leadershipDecision = [];
                }
                addcellRow($scope.risksView3.projectConcernsIssues.leadershipDecision, 'projectConcernsIssues');
            } else if (n == 2) {
                if (undefined == $scope.risksView3.projectConcernsIssues) {
                    $scope.risksView3.projectConcernsIssues = {investmentConditions: []};
                }
                if (undefined == $scope.risksView3.projectConcernsIssues.investmentConditions) {
                    $scope.risksView3.projectConcernsIssues.investmentConditions = [];
                }
                addcellRow($scope.risksView3.projectConcernsIssues.investmentConditions, 'projectConcernsIssues');
            } else if (n == 3) {
                if (undefined == $scope.formalReport.projectConcernsIssues) {
                    $scope.formalReport.projectConcernsIssues = {implementationRequirements: []};
                }
                if (undefined == $scope.formalReport.projectConcernsIssues.implementationRequirements) {
                    $scope.formalReport.projectConcernsIssues.implementationRequirements = [];
                }
                addcellRow($scope.formalReport.projectConcernsIssues.implementationRequirements, 'projectConcernsIssues');
            } else if (n == 4) {
                if (undefined == $scope.risksView3.projectConcernsIssues) {
                    $scope.risksView3.projectConcernsIssues = {prompt: []};
                }
                if (undefined == $scope.risksView3.projectConcernsIssues.prompt) {
                    $scope.risksView3.projectConcernsIssues.prompt = [];
                }
                addcellRow($scope.risksView3.projectConcernsIssues.prompt, 'projectConcernsIssues');
            } else if (n == 5) {
                if (undefined == $scope.boundaryView1.projectConcernsIssues) {
                    $scope.boundaryView1.projectConcernsIssues = {fundUsetableOneConditions: []};
                }
                if (undefined == $scope.boundaryView1.projectConcernsIssues.fundUsetableOneConditions) {
                    $scope.boundaryView1.projectConcernsIssues.fundUsetableOneConditions = [];
                }
                addcellRow($scope.boundaryView1.projectConcernsIssues.fundUsetableOneConditions, 'fundUsetable');
            } else if (n == 6) {
                if (undefined == $scope.boundaryView1.projectConcernsIssues) {
                    $scope.boundaryView1.projectConcernsIssues = {fundUsetableTwoConditions: []};
                }
                if (undefined == $scope.boundaryView1.projectConcernsIssues.fundUsetableTwoConditions) {
                    $scope.boundaryView1.projectConcernsIssues.fundUsetableTwoConditions = [];
                }
                addcellRow($scope.boundaryView1.projectConcernsIssues.fundUsetableTwoConditions, 'fundUsetable');
            } else if (n == 7) {
                if (undefined == $scope.boundaryView1.projectConcernsIssues) {
                    $scope.boundaryView1.projectConcernsIssues = {profitContributionTableConditions: []};
                }
                if (undefined == $scope.boundaryView1.projectConcernsIssues.profitContributionTableConditions) {
                    $scope.boundaryView1.projectConcernsIssues.profitContributionTableConditions = [];
                }
                addcellRow($scope.boundaryView1.projectConcernsIssues.profitContributionTableConditions, 'profitContributionTable');
            } else if (n == 8) {
                if (undefined == $scope.attchView8.costEstimate) {
                    $scope.attchView8.costEstimate = {drugList: []};
                }
                if (undefined == $scope.attchView8.costEstimate.drugList) {
                    $scope.attchView8.costEstimate.drugList = [];
                }
                addcellRow($scope.attchView8.costEstimate.drugList, 'drug');
            }

        }
        //第三分部删除行
        $scope.delrow = function (name, num) {
            if (num == 1) {
                var names = $scope.formalReport.projectConcernsIssues.leadershipDecision;
                names.splice(name, 1);
                if (names.length > 0) {
                    $scope.formalReport.projectConcernsIssues.leadershipDecision = names;
                } else {
                    $scope.formalReport.projectConcernsIssues.leadershipDecision = [];
                }
            } else if (num == 2) {
                var names = $scope.formalReport.projectConcernsIssues.investmentConditions;
                names.splice(name, 1);
                if (names.length > 0) {
                    $scope.formalReport.projectConcernsIssues.investmentConditions = names;
                } else {
                    $scope.formalReport.projectConcernsIssues.investmentConditions = [];
                }
            } else if (num == 3) {
                var names = $scope.formalReport.projectConcernsIssues.implementationRequirements;
                names.splice(name, 1);
                if (names.length > 0) {
                    $scope.formalReport.projectConcernsIssues.implementationRequirements = names;
                } else {
                    $scope.formalReport.projectConcernsIssues.implementationRequirements = [];
                }
            } else if (num == 4) {
                var names = $scope.formalReport.projectConcernsIssues.prompt;
                names.splice(name, 1);
                if (names.length > 0) {
                    $scope.formalReport.projectConcernsIssues.prompt = names;
                } else {
                    $scope.formalReport.projectConcernsIssues.prompt = [];
                }
            } else if (num == 5) {
                var names = $scope.boundaryView1.projectConcernsIssues.fundUsetableOneConditions;
                names.splice(name, 1);
                if (names.length > 0) {
                    $scope.boundaryView1.projectConcernsIssues.fundUsetableOneConditions = names;
                } else {
                    $scope.boundaryView1.projectConcernsIssues.fundUsetableOneConditions = [];
                }
            } else if (num == 6) {
                var names = $scope.boundaryView1.projectConcernsIssues.fundUsetableTwoConditions;
                names.splice(name, 1);
                if (names.length > 0) {
                    $scope.boundaryView1.projectConcernsIssues.fundUsetableTwoConditions = names;
                } else {
                    $scope.boundaryView1.projectConcernsIssues.fundUsetableTwoConditions = [];
                }
            } else if (num == 7) {
                var names = $scope.formalReport.projectConcernsIssues.profitContributionTableConditions;
                names.splice(name, 1);
                if (names.length > 0) {
                    $scope.formalReport.projectConcernsIssues.profitContributionTableConditions = names;
                } else {
                    $scope.formalReport.projectConcernsIssues.profitContributionTableConditions = [];
                }
            } else if (num == 8) {
                var names = $scope.attchView8.costEstimate.drugList;
                names.splice(name, 1);
                if (names.length > 0) {
                    $scope.attchView8.costEstimate.drugList = names;
                } else {
                    $scope.attchView8.costEstimate.drugList = [];
                }
            }
        }
        $scope.addYearList = function () {
            function addBlankRow(array) {
                var blankRow = {
                    option_content: '',
                    option_execute: '',
                    taskId: taskID
                }
                var size = 0;
                for (attr in array) {
                    size++;
                }
                array[size] = blankRow;
            }

            if (undefined == $scope.earningsView2) {
                $scope.earningsView2 = {yearList: []};

            }
            if (undefined == $scope.earningsView2.yearList) {
                $scope.earningsView2.yearList = [];
            }
            addBlankRow($scope.earningsView2.yearList);
        }

        $scope.addOption = function () {
            function addBlankRow(array) {
                var blankRow = {
                    option_content: '',
                    option_execute: '',
                    taskId: taskID
                }
                var size = 0;
                for (attr in array) {
                    size++;
                }
                array[size] = blankRow;
            }

            if (undefined == $scope.conclusionView5) {
                $scope.conclusionView5 = {optionList: []};

            }
            if (undefined == $scope.conclusionView5.optionList) {
                $scope.conclusionView5.optionList = [];
            }
            addBlankRow($scope.conclusionView5.optionList);
        }
        $scope.addRequire = function () {
            function addBlankRow(array) {
                var blankRow = {
                    require_content: '',
                    require_execute: '',
                    taskId: taskID
                }
                var size = 0;
                for (attr in array) {
                    size++;
                }
                array[size] = blankRow;
            }

            if (undefined == $scope.executeView6) {
                $scope.executeView6 = {requireList: []};

            }
            if (undefined == $scope.executeView6.requireList) {
                $scope.executeView6.requireList = [];
            }
            addBlankRow($scope.executeView6.requireList);
        }
        $scope.addLaw = function () {
            function addBlankRow(array) {
                var blankRow = {
                    lawOpinion: '',
                    pendingConfirmation: false,
                    canNotBeModified: false,
                    taskId: taskID
                }
                var size = 0;
                for (attr in array) {
                    size++;
                }
                array[size] = blankRow;
            }

            if (undefined == $scope.attchView7) {
                $scope.attchView7 = {lawList: []};

            }
            if (undefined == $scope.attchView7.lawList) {
                $scope.attchView7.lawList = [];
            }
            addBlankRow($scope.attchView7.lawList);
        }
        $scope.addTechnology = function () {
            function addBlankRow(array) {
                var blankRow = {
                    technology_opinion: '',
                    taskId: taskID
                }
                var size = 0;
                for (attr in array) {
                    size++;
                }
                array[size] = blankRow;
            }

            if (undefined == $scope.attchView7) {
                $scope.attchView7 = {technologyList: []};

            }
            if (undefined == $scope.attchView7.technologyList) {
                $scope.attchView7.technologyList = [];
            }
            addBlankRow($scope.attchView7.technologyList);
        }
        $scope.addFinance = function () {
            function addBlankRow(array) {
                var blankRow = {
                    finance_opinion: '',
                    taskId: taskID
                }
                var size = 0;
                for (attr in array) {
                    size++;
                }
                array[size] = blankRow;
            }

            if (undefined == $scope.attchView7) {
                $scope.attchView7 = {financeList: []};

            }
            if (undefined == $scope.attchView7.financeList) {
                $scope.attchView7.financeList = [];
            }
            addBlankRow($scope.attchView7.financeList);
        }
        $scope.addDrainage = function () {
            function addBlankRow(array) {
                var blankRow = {
                    drainage_opinion: '',
                    taskId: taskID
                }
                var size = 0;
                for (attr in array) {
                    size++;
                }
                array[size] = blankRow;
            }

            if (undefined == $scope.attchView7) {
                $scope.attchView7 = {drainageList: []};

            }
            if (undefined == $scope.attchView7.drainageList) {
                $scope.attchView7.drainageList = [];
            }
            addBlankRow($scope.attchView7.drainageList);
        }
        $scope.commonDdelete = function (n) {
            var commentsObj = null;
            if (n == 0) {
                commentsObj = $scope.formalReport.optionList;
            } else if (n == 1) {
                commentsObj = $scope.formalReport.requireList;
            } else if (n == 2) {
                commentsObj = $scope.attchView7.lawList;
            } else if (n == 3) {
                commentsObj = $scope.attchView7.technologyList;
            } else if (n == 4) {
                commentsObj = $scope.attchView7.financeList;
            } else if (n == 5) {
                commentsObj = $scope.attchView7.drainageList;
            } else if (n == 7) {
                commentsObj = $scope.earningsView2.yearList;
            }

            if (commentsObj != null) {
                for (var i = 0; i < commentsObj.length; i++) {
                    if (commentsObj[i].selected) {
                        commentsObj.splice(i, 1);
                        i--;
                    }
                }
            }
        }

        $scope.dic = [];
        //保存评审报告(草稿)
        $scope.saveReport = function (callBack) {
            if (typeof callBack == 'function') {
            } else {
                var url_post;
                if(typeof ($scope.formalReport._id) != "undefined"){
                    url_post = 'formalReport/updateReport.do';
                }else{
                    var boolean = $scope.isReportExist();
                    if(boolean){
                        $.alert("请勿重复保存数据!");
                        return false;
                    }
                    url_post = 'formalReport/createNewReport.do';
                }
                $scope.formalReportCopy = $scope.serialize($scope.formalReport);

                $http({
                    method: 'post',
                    url: srvUrl + url_post,
                    data: $.param({"json": JSON.stringify($scope.formalReportCopy)})
                }).success(function (data) {
                    if (data.success) {
                        $scope.formalReport._id = data.result_data;
                        $.alert("保存成功!");
                        $("#wordbtn").show();
                        console.log($scope.boundaryView1)
                    } else {
                        $.alert(data.result_name);
                    }
                }).error(function (data, status, headers, config) {
                    $.alert(status);
                });
            }
        }

        $scope.isReportExist = function () {
            var boolean = false;
            $.ajax({
                url: srvUrl + 'formalReport/isReportExist.do',
                type: "POST",
                dataType: "json",
                data: {"businessid": objId},
                async: false,
                success: function (data) {
                    if (data.result_data > 0) {
                        boolean = true;
                    }
                }
            });
            return boolean;
        }

        //检查是否可提交
        $scope.isPossible2Submit = function () {
            var boolean = false;
            $.ajax({
                url: srvUrl + 'formalReport/isPossible2Submit.do',
                type: "POST",
                dataType: "json",
                data: {"projectFormalId": $scope.formalReport.projectFormalId},
                async: false,
                success: function (data) {
                    if (data.result_data) {
                        boolean = true;
                    }
                }
            });
            return boolean;
        }

        //提交报告并更改状态
        $scope.showSubmitModal = function () {
            show_Mask();
            // var flag = $scope.isPossible2Submit();
            // if(flag){
            $scope.saveReport(function () {
                $http({
                    method: 'post',
                    url: srvUrl + 'formalReport/submitAndupdate.do',
                    data: $.param({
                        "id": $scope.formalReport._id,
                        "projectFormalId": $scope.formalReport.projectFormalId
                    })
                }).success(function (data) {
                    if (data.success) {
                        var istrue = $scope.createwords();
                        if (istrue) {
                            hide_Mask();
                            $.alert("提交成功!");
                            $('input').attr("readonly", "readonly");
                            $('textarea').attr("readonly", "readonly");
                            $('button').attr("disabled", "disabled");
                            $("#savebtn").hide();
                            $("#submitbnt").hide();
                            $('#wordbtn').attr("disabled", false);
                            $(".modal-footer button").attr({"disabled": false});
                        } else {
                            hide_Mask();
                            $.alert("提交失败，请检查是否填写完整后重新提交或联系管理员!");
                            return false;
                        }
                    }
                }).error(function (data, status, headers, config) {
                    hide_Mask();
                    $.alert(status);
                    return false;
                });
            });
            // }else{
            // 	hide_Mask();
            // 	$.alert("请确保流程已结束!");
            // 	return false;
            // }
        }


        //点击提交时先生成word，生成成功后方可上会
        $scope.createwords = function () {
            var boolean = true;
            $.ajax({
                url: srvUrl + 'formalReport/getPfrAssessmentWord.do',
                type: "POST",
                dataType: "json",
                data: {"id": $scope.formalReport._id},
                async: false,
                success: function (data) {
                    if (!data.success) {
                        boolean = false;
                        $.alert("提交前系统报表生成失败，请查看必填项是否填写完毕；如果全部正常填写请联系管理员!");
                    }
                }
            });
            return boolean;
        }

        $scope.getProjectFormalReviewByID = function (id) {
            $http({
                method: 'post',
                url: srvUrl + "formalReport/getProjectFormalReviewByID.do",
                data: $.param({"id": id})
            }).success(function (data) {

                $scope.pfr = data.result_data;
                $scope.formalReport.projectFormalId = $scope.pfr.id;
                $scope.formalReport.projectName = $scope.pfr.apply.projectName;
                $scope.formalReport.projectNo = $scope.pfr.apply.projectNo;
                if (null != $scope.pfr.apply.reportingUnit) {
                    $scope.formalReport.reportingUnit = $scope.pfr.apply.reportingUnit.name;
                }
                var ptNameArr = [];
                var pt = $scope.pfr.apply.projectType;
                if (null != pt && pt.length > 0) {
                    for (var i = 0; i < pt.length; i++) {
                        ptNameArr.push(pt[i].VALUE);
                    }
                    $scope.formalReport.projectTypeName = ptNameArr.join(",");
                }
                $scope.formalReport.controllerVal = $scope.controller_val;
                // $scope.formalReport.attachment = [{'file': '', 'fileName': ''}];

                $scope.addtableRow(1);
                $scope.addtableRow(2);
                $scope.addtableRow(3);
                $scope.addtableRow(4);
                $scope.addtableRow(8);
                if (undefined == $scope.conclusionView5.optionList) {
                    $scope.addOption();
                }
                if (undefined == $scope.executeView6.requireList) {
                    $scope.addRequire();
                }
                if (undefined == $scope.attchView7.lawList) {
                    $scope.addLaw();
                }
                if (undefined == $scope.attchView7.technologyList) {
                    $scope.addTechnology();
                }
                if (undefined == $scope.attchView7.financeList) {
                    $scope.addFinance();
                }
                if (undefined == $scope.attchView7.hrResourceList) {
                    $scope.addDrainage();
                }
                if (undefined == $scope.earningsView2.yearList) {
                    $scope.addYearList();
                }
                if (undefined == $scope.earningsView2.yearList) {
                    $scope.addYearList();
                }
                if (null == $scope.formalReport.projectOverview) {
                    //$scope.formalReport.projectOverview="项目由XX事业部通过XX途径获得，我方接手后由XX事业部负责运营，本项目的盈利模式为XX。";
                    $scope.formalReport.projectOverview = "";
                }
                //设置checkbox初始值

            }).error(function (data, status, headers, config) {
                $.alert(status);
            });
        }

        //修改时查询基本信息
        $scope.getByID = function (id) {
            $http({
                method: 'post',
                url: srvUrl + 'formalReport/getByID.do',
                data: $.param({"id": id})
            }).success(function (data) {
                $scope.formalReport = data.result_data;
                $(".mixed-template").css({"display": "block"});
                $scope.myObjone = {"display": "block"};
                $scope.myObjtwo = {"display": "block"};
                $scope.myObjthree = {"display": "block"};
                $scope.myObjfour = {"display": "block"};
                $scope.myObjfive = {"display": "block"};
                $scope.myObjsix = {"display": "block"};
                $scope.myObjseven = {"display": "block"};
                $scope.getViewData();
                if (action == "View") {
                    /*$('input').attr("disabled","disabled");
                    $('textarea').attr("disabled","disabled");
                    $('button').attr("disabled","disabled");*/
                    $('input').attr("readonly", "readonly");
                    $('textarea').attr("readonly", "readonly");
                    $('select').attr("disabled", "disabled");
                    $('button').attr("disabled", "disabled");
                    $("#submitbnt").attr("disabled", false);
                    $('#wordbtn').attr("disabled", false);
                }
            }).error(function (data, status, headers, config) {
                $.alert(status);
            });
        }

        $scope.setDirectiveParam = function (columnName, type, index) {
            $scope.columnName = columnName;
        }
        $scope.setDirectiveOrgList = function (id, name) {
            var params = $scope.columnName;
            if (params == "tenderCompany") {
                $scope.boundaryView1.tenderCompany = {"name": name, value: id};
                $("#tenderCompanyName").val(name);
                $("label[for='tenderCompanyName']").remove();
            }
        }

        $scope.createWord = function (id) {
            startLoading();
            $http({
                method: 'post',
                url: srvUrl + 'formalReport/getPfrAssessmentWord.do',
                data: $.param({"id": id})
            }).success(function (data) {
                if (data.success) {
                    var filesPath = data.result_data.filePath;
                    var index = filesPath.lastIndexOf(".");
                    var str = filesPath.substring(index + 1, filesPath.length);
                    var filesName = data.result_data.fileName;

                    window.location.href = srvUrl + "file/downloadFile.do?filepaths=" + encodeURI(filesPath) + "&filenames=" + encodeURI(encodeURI("正式评审-" + filesName + "报告-混合模式.")) + str;

                } else {
                    $.alert("报表生成失败，请查看必填项是否填写完毕；如果全部正常填写请联系管理员！");
                }
            }).error(function (data, status, headers, config) {
                $.alert(status);
            });
            endLoading();
        }

        function FormatDateto() {
            var date = new Date();
            var paddNum = function (num) {
                num += "";
                return num.replace(/^(\d)$/, "0$1");
            }
            return date.getFullYear() + "" + paddNum(date.getMonth() + 1);
        }

        $scope.errorAttach = [];
        $scope.uploadfformal = function (file, errorFile, idx) {
            if (errorFile && errorFile.length > 0) {
                var errorMsg = fileErrorMsg(errorFile);
                $scope.errorAttach[idx] = {msg: errorMsg};
            } else if (file) {
                var fileFolder = "formalReport/";
                if ($routeParams.action == 'Create') {
                    if (undefined == $scope.formalReport.projectNo) {
                        $.alert("请先选择项目然后上传附件");
                        return false;
                    }
                    var no = $scope.formalReport.projectNo;
                    fileFolder = fileFolder + FormatDateto() + "/" + no;
                } else {
                    var dates = $scope.formalReport.create_date;
                    var no = $scope.formalReport.projectNo;
                    var strs = new Array(); //定义一数组
                    strs = dates.split("-"); //字符分割
                    dates = strs[0] + strs[1]; //分割后的字符输出
                    fileFolder = fileFolder + dates + "/" + no;
                }

                $scope.errorAttach[idx] = {msg: ''};
                Upload.upload({
                    url: srvUrl + 'file/uploadFile.do',
                    data: {file: file, folder: fileFolder}
                }).then(function (resp) {
                    var retData = resp.data.result_data[0];
                    retData.version = "0";
                    // $scope.formalReport.attachment = [retData];
                }, function (resp) {
                    $.alert(resp.status);
                }, function (evt) {
                    var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                    $scope["progress" + idx] = progressPercentage == 100 ? "" : progressPercentage + "%";
                });
            }
        };
        // $scope.downLoadFile = function (idx) {
        //     var filePath = $scope.formalReport.attachment[0].filePath,
        //         fileName = $scope.formalReport.attachment[0].fileName;
        //     window.location.href = srvUrl + "file/downloadFile.do?filepaths=" + encodeURI(filePath) + "&filenames=" + encodeURI(encodeURI(fileName));
        // }

        $scope.getSelectTypeByCode = function (typeCode) {
            var url = 'common/commonMethod/selectDataDictionByCode';
            $scope.httpData(url, typeCode).success(function (data) {
                if (data.result_code === 'S') {
                    $scope.payTypeList = data.result_data;
                } else {
                    alert(data.result_name);
                }
            });
        }
        $scope.getSelectTypeByCode2 = function (typeCode) {
            var url = 'common/commonMethod/selectDataDictionByCode';
            $scope.httpData(url, typeCode).success(function (data) {
                if (data.result_code === 'S') {
                    $scope.csTypeList = data.result_data;
                } else {
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

        angular.element(document).ready(function () {
            $scope.getSelectTypeByCode("10");
            $scope.getSelectTypeByCode2("11");
            // 初始化项目模式的值
            $scope.getprojectmodel('0');
        });

        $scope.initData();

        // 回显select2下拉框的值
        var commonModelValue2 = function (paramsVal, arr) {
            var leftstr2 = "<li class=\"select2-search-choice\"><div>";
            var centerstr2 = "</div><a class=\"select2-search-choice-close\" tabindex=\"-1\" onchange=\"changeServiceType()\" onclick=\"delSelect(this,'"
            var rightstr2 = "');\"  ></a></li>";
            for (var i = 0; i < arr.length; i++) {
                console.log(leftstr2 + arr[i].VALUE + centerstr2 + paramsVal + "','" + arr[i].VALUE + "','" + arr[i].KEY + rightstr2);
                $("#header" + paramsVal).find(".select2-search-field").before(leftstr2 + arr[i].VALUE + centerstr2 + paramsVal + "','" + arr[i].VALUE + "','" + arr[i].KEY + rightstr2);
            }
        }

        $scope.serialize = function (model) {
            $scope.boundary1 = {};
            $scope.boundary1 = angular.copy($scope.boundaryView1);
            if ($scope.boundaryView1.governmentProcurement) {
                $scope.boundary1.governmentProcurement = '1';
                $scope.boundary1.unFillReasonOne = null;
            } else {
                $scope.boundary1.governmentProcurement = '0';
                $scope.boundary1.unFillReasonOne = $scope.boundaryView1.unFillReasonOne;
            }
            if ($scope.boundaryView1.projectProcedure) {
                $scope.boundary1.projectProcedure = '1';
                $scope.boundary1.unFillReasonTwo = null;
            } else {
                $scope.boundary1.projectProcedure = '0';
                $scope.boundary1.unFillReasonTwo = $scope.boundaryView1.unFillReasonTwo;
            }
            if ($scope.boundaryView1.projectWarehouse) {
                $scope.boundary1.projectWarehouse = '1';
                $scope.boundary1.unFillReasonThree = null;
            } else {
                $scope.boundary1.projectWarehouse = '0';
                $scope.boundary1.unFillReasonThree = $scope.boundaryView1.unFillReasonThree;
            }
            if ($scope.boundaryView1.signSubject) {
                $scope.boundary1.signSubject = '1';
                $scope.boundary1.unFillReasonFour = null;
            } else {
                $scope.boundary1.signSubject = '0';
                $scope.boundary1.unFillReasonFour = $scope.boundaryView1.unFillReasonFour;
            }
            if ($scope.boundaryView1.whetherConstructionIsCompleted) {
                $scope.boundary1.whetherConstructionIsCompleted = '1';
            } else {
                $scope.boundary1.whetherConstructionIsCompleted = '0';
            }
            if ($scope.boundaryView1.whetherWeNeedToBuild) {
                $scope.boundary1.whetherWeNeedToBuild = '1';
            } else {
                $scope.boundary1.whetherWeNeedToBuild = '0';
            }
            if ($scope.boundaryView1.isContainsConstruction) {
                $scope.boundary1.isContainsConstruction = '1';
            } else {
                $scope.boundary1.isContainsConstruction = '0';
            }
            if ($scope.boundaryView1.whetherToObtainLoanTermSheet) {
                $scope.boundary1.whetherToObtainLoanTermSheet = '1';
            } else {
                $scope.boundary1.whetherToObtainLoanTermSheet = '0';
            }
            if ($scope.boundaryView1.whetherNeedAddLetter) {
                $scope.boundary1.whetherNeedAddLetter = '1';
            } else {
                $scope.boundary1.whetherNeedAddLetter = '0';
            }
            if ($scope.boundaryView1.wetherCommissioningChargeTwo) {
                $scope.boundary1.wetherCommissioningChargeTwo = '1';
            } else {
                $scope.boundary1.wetherCommissioningChargeTwo = '0';
            }
            $scope.earnings2 = {};
            $scope.earnings2 = $scope.earningsView2;
            $scope.risks3 = {};
            $scope.risks3 = $scope.risksView3;
            $scope.interspace4 = {};
            $scope.interspace4 = $scope.interspaceView4;
            $scope.conclusion5 = {};
            $scope.conclusion5 = $scope.conclusionView5;
            $scope.execute6 = {};
            $scope.execute6 = $scope.executeView6;
            $scope.attch7 = {};
            $scope.attch7 = $scope.attchView7;
            $scope.attch8 = {};
            $scope.attch8 = $scope.attchView8;
            $scope.attch9 = [];
            $scope.attch9 = $scope.attchView9;
            model.boundary1 = $scope.boundary1;
            model.earnings2 = $scope.earnings2;
            model.risks3 = $scope.risks3;
            model.interspace4 = $scope.interspace4;
            model.interspace4 = $scope.interspace4;
            model.conclusion5 = $scope.conclusion5;
            model.execute6 = $scope.execute6;
            model.attch7 = $scope.attch7;
            model.attch8 = $scope.attch8;
            model.attch9 = $scope.attch9;
            console.log(model);
            return model;
        }

        $scope.getViewData = function () {
            console.log($scope.formalReport);
            // 数据回显
            $scope.boundaryView1 = $scope.formalReport.boundary1;
            if($scope.boundaryView1.governmentProcurement == 1) {
                $scope.boundaryView1.governmentProcurement = true;
            } else {
                $scope.boundaryView1.governmentProcurement = false;
            }
            if($scope.boundaryView1.projectProcedure == 1) {
                $scope.boundaryView1.projectProcedure = true;
            } else {
                $scope.boundaryView1.projectProcedure = false;
            }
            if($scope.boundaryView1.projectWarehouse == 1) {
                $scope.boundaryView1.projectWarehouse = true;
            } else {
                $scope.boundaryView1.projectWarehouse = false;
            }
            if($scope.boundaryView1.signSubject == 1) {
                $scope.boundaryView1.signSubject = true;
            } else {
                $scope.boundaryView1.signSubject = false;
            }
            if($scope.boundaryView1.whetherConstructionIsCompleted == 1) {
                $scope.boundaryView1.whetherConstructionIsCompleted = true;
            } else {
                $scope.boundaryView1.whetherConstructionIsCompleted = false;
            }
            if($scope.boundaryView1.whetherWeNeedToBuild == 1) {
                $scope.boundaryView1.whetherWeNeedToBuild = true;
            } else {
                $scope.boundaryView1.whetherWeNeedToBuild = false;
            }
            if($scope.boundaryView1.isContainsConstruction == 1) {
                $scope.boundaryView1.isContainsConstruction = true;
            } else {
                $scope.boundaryView1.isContainsConstruction = false;
            }
            if($scope.boundaryView1.whetherToObtainLoanTermSheet == 1) {
                $scope.boundaryView1.whetherToObtainLoanTermSheet = true;
            } else {
                $scope.boundaryView1.whetherToObtainLoanTermSheet = false;
            }
            if($scope.boundaryView1.whetherNeedAddLetter == 1) {
                $scope.boundaryView1.whetherNeedAddLetter = true;
            } else {
                $scope.boundaryView1.whetherNeedAddLetter = false;
            }
            if($scope.boundaryView1.wetherCommissioningCharge == 1) {
                $scope.boundaryView1.wetherCommissioningCharge = true;
            } else {
                $scope.boundaryView1.wetherCommissioningCharge = false;
            }
            if($scope.boundaryView1.wetherCommissioningChargeTwo == 1) {
                $scope.boundaryView1.wetherCommissioningChargeTwo = true;
            } else {
                $scope.boundaryView1.wetherCommissioningChargeTwo = false;
            }

            if($scope.boundaryView1.projectModel != undefined && $scope.boundaryView1.projectModel != null && $scope.boundaryView1.projectModel != {} ){
                $("#projectmodeboxName").select2("val", " ");
                commonModelValue2('projectmodebox',$scope.boundaryView1.projectModel);
            }

            $scope.earningsView2 = $scope.formalReport.earnings2;
            $scope.risksView3 = $scope.formalReport.risks3;
            $scope.interspaceView4 = $scope.formalReport.interspace4;
            $scope.conclusionView5 = $scope.formalReport.conclusion5;
            $scope.executeView6 = $scope.formalReport.execute6;
            $scope.attchView7 = $scope.formalReport.attch7;
            $scope.attchView8 = $scope.formalReport.attch8;
            $scope.attchView9 = $scope.formalReport.attch9;
        }


        $scope.addAttch = function () {
            $scope.attchNew = {};
            $scope.attchNew.title1 = "";
            $scope.attchNew.title2 = "";
            $scope.attchNew.contents = [];
            $scope.content = {};
            $scope.content.content1 = "";
            $scope.content.content1 = "";
            $scope.attchNew.contents.push($scope.content);
            console.log($scope.attchNew);
            if(!$scope.attchView9) {
                $scope.attchView9  = [];
            }
            $scope.attchView9.push($scope.attchNew);
        }

        $scope.addContent = function (attchNew) {
            $scope.content = {};
            $scope.content.content1 = "";
            $scope.content.content2 = "";
            attchNew.contents.push($scope.content);
        }





    }]);
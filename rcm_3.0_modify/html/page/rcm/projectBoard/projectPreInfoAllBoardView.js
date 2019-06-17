ctmApp.register.controller('ProjectPreInfoAllBoardView',
    ['$http','$scope','$location','$routeParams','Upload','$timeout', '$filter',
        function ($http,$scope,$location,$routeParams,Upload,$timeout,$filter) {
            //初始化
            var objId = $routeParams.id;
            $scope.businessId = $routeParams.id;
            $scope.oldUrl = $routeParams.url;

            //判断新旧流程
            if(objId.indexOf("@")>0){
                var params = $routeParams.id.split("@");
                objId = params[0];
                $scope.isOldFlow = true;//新旧流程标识
            }

            //流程相关
            $scope.initPage = function(){
                if($scope.isOldFlow ){
                    //旧流程
                    if(typeof (params[1])=='string' && typeof (params[2])=='string' && params[1]!='' && params[2]!=''){//已经启动流程
                        $scope.wfInfo = {processDefinitionId:params[1],processInstanceId:params[2],processKey:'preAssessment',businessId:objId};
                    }else{//未启动流程
                        $scope.wfInfo = {processKey:'preAssessment',businessId:objId};
                    }
                }else{
                    //新流程
                    $scope.wfInfo = {processKey:'preReview'};
                    $scope.wfInfo.businessId = objId;
                    $scope.refreshImg = Math.random()+1;
                }
            };

            $scope.initMarkPie = function(){
                $scope.markPieOption = {
                    title : {
                        text: '各项得分比例图',
                        subtext: '能力评价',
                        x:'center'
                    },
                    tooltip : {
                        trigger: 'item',
                        formatter: "{a} <br/>{b} : {c} ({d}%)"
                    },
                    legend: {
                        orient : 'vertical',
                        x : 'left',
                        data:['评审合规能力','评审资料质量','投资核心能力']
                    },
                    calculable : true,
                    series : [
                        {
                            name:'能力评价',
                            type:'pie',
                            radius : '55%',
                            center: ['50%', '60%'],
                            data:[
                                {value:$scope.mark.HEGUITOTALMARK, name:'评审合规能力'},
                                {value:$scope.mark.FILETOTALMARK, name:'评审资料质量'},
                                {value:$scope.mark.HEXINTOTALMARK, name:'投资核心能力'}
                            ],
                            itemStyle: {
                                emphasis: {
                                    shadowBlur: 10,
                                    shadowOffsetX: 0,
                                    shadowColor: 'rgba(0, 0, 0, 0.5)'
                                },
                                normal:{
                                    label:{
                                        show: true,
                                        formatter: '{b} : {c}分'
                                    },
                                    labelLine :{show:true}
                                }
                            }
                        }
                    ]
                };
                myChart = echarts.init(document.getElementById('markPieChart'),'shine');
                myChart.setOption($scope.markPieOption);
            }
            $scope.initMarkBar = function(){
                $scope.markBarOption =  {
                    title : {
                        text: '各项得分比例图',
                        subtext: '能力评价',
                        x:'center'
                    },
                    tooltip : {
                        trigger: 'axis',
                        axisPointer : {            // 坐标轴指示器，坐标轴触发有效
                            type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
                        }
                    },
                    grid: {
                        left: '3%',
                        right: '30%',
                        bottom: '3%',
                        containLabel: true
                    },
                    legend: {
                        data:['流程熟悉度',
                            '资料备案',
                            '及时性',
                            '完整性',
                            '准确性',
                            '风险识别及规避能力',
                            '财务测算能力',
                            '方案设计能力',
                            '协议谈判能力',
                            '总分'
                        ],
                        x:'right',
                        y:'center',
                        orient: 'vertical'
                    },
                    calculable : true,
                    xAxis : [
                        {type : 'category', data : ['评审合规能力','评审资料质量','投资核心能力']}
                    ],
                    yAxis : [
                        {type : 'value'}
                    ],
                    series : [
                        {
                            name:'流程熟悉度',
                            type:'bar',
                            stack: '各项得分',
                            data:[$scope.mark.FLOWMARK,null,null],
                            itemStyle : { normal: {label : {show: true, position: 'insideTop'}}}
                        },
                        {
                            name:'资料备案',
                            type:'bar',
                            stack: '各项得分',
                            data:[$scope.mark.FILECOPY, null, null],
                            itemStyle : { normal: {label : {show: true, position: 'insideTop'}}}
                        },
                        {
                            name:'及时性',
                            type:'bar',
                            stack: '各项得分',
                            data:[null, $scope.mark.FILETIME, null],
                            itemStyle : { normal: {label : {show: true, position: 'insideTop'}}}
                        },
                        {
                            name:'完整性',
                            type:'bar',
                            stack: '各项得分',
                            data:[null, $scope.mark.FILECONTENT, null],
                            itemStyle : { normal: {label : {show: true, position: 'insideTop'}}}
                        },
                        {
                            name:'准确性',
                            type:'bar',
                            stack: '各项得分',
                            data:[null, $scope.mark.REVIEWFILEACCURACY+$scope.mark.LEGALFILEACCURACY, null],
                            itemStyle : { normal: {label : {show: true, position: 'insideTop'}}}
                        },
                        {
                            name:'风险识别及规避能力',
                            type:'bar',
                            stack: '各项得分',
                            data:[null, null, $scope.mark.RISKCONTROL],
                            itemStyle : { normal: {label : {show: true, position: 'insideTop'}}}
                        },
                        {
                            name:'财务测算能力',
                            type:'bar',
                            stack: '各项得分',
                            data:[null, null, $scope.mark.MONEYCALCULATE],
                            itemStyle : { normal: {label : {show: true, position: 'insideTop'}}}
                        },
                        {
                            name:'方案设计能力',
                            type:'bar',
                            stack: '各项得分',
                            data:[null, null, $scope.mark.PLANDESIGN],
                            itemStyle : { normal: {label : {show: true, position: 'insideTop'}}}
                        },
                        {
                            name:'协议谈判能力',
                            type:'bar',
                            stack: '各项得分',
                            data:[null, null, $scope.mark.TALKS],
                            itemStyle : { normal: {label : {show: true, position: 'insideTop'}}}
                        },
                        {
                            name:'总分',
                            type:'bar',
                            data:[30, 30, 40],
                            itemStyle : { normal: {label : {show: true, position: 'insideTop'}}}
                        }
                    ]
                };
                myChart = echarts.init(document.getElementById('markBarChart'),'shine');
                myChart.setOption($scope.markBarOption);
            }

            //获取审核日志
            $scope.queryAuditLogsByBusinessId = function (businessId){
                var  url = 'preAudit/queryAuditedLogsById.do';
                $http({
                    method:'post',
                    url: srvUrl + url,
                    data: $.param({"businessId":businessId})
                }).success(function(result){
                    $scope.auditLogs = result.result_data;
                });
            };

            // 获取项目评分
            $scope.getMarkById = function(businessId){
                var  url = 'preMark/queryMarks.do';
                $http({
                    method:'post',
                    url: srvUrl + url,
                    data: $.param({"businessId":businessId})
                }).success(function(result){
                    if(result.success){
                        $scope.mark = result.result_data;
                        if($scope.mark!=null && $scope.mark !="" ){
                            $scope.initMarkPie();
                            $scope.initMarkBar();
                        }
                    }else{
                        $.alert(result.result_name);
                    }
                });
            };

            // 获取投标反馈
            $scope.getFeedbackByID=function(id){
                var  url = 'projectPreReview/Feedback/getFeedbackByID';
                $scope.httpData(url,id).success(function(data){
                        $scope.arf = data.result_data;
                    }
                ).error(function (data, status, headers, config) {
                    alert(status);
                });
            };

            // 获取提交决策会数据
            $scope.getById = function (businessId) {
                $http({
                    method:'post',
                    url:srvUrl+"preBidding/getByBusinessId.do",
                    data: $.param({"businessId":businessId})
                }).success(function(data){
                    $scope.pfr  = data.result_data.preMongo;
                    $scope.preBidding  = $scope.pfr;
                    $scope.preBidding.id = $scope.pfr.id;
                    $scope.applyDate = data.result_data.applyDate;
                    $scope.stage = data.result_data.stage;
                    $scope.reportOracle = data.result_data.reportOracle;
                    $scope.isReadOnly = 'true';
                }).error(function(data,status,header,config){
                    $.alert(status);
                });
            };

            //处理附件列表
            $scope.reduceAttachment = function(attachment, id){
                $scope.newAttachment = attach_list("preReview", id, "preInfo").result_data;
                for(var i in attachment){
                    var file = attachment[i];
                    console.log(file);
                    for (var j in $scope.newAttachment){
                        if (file.fileId == $scope.newAttachment[j].fileid){
                            $scope.newAttachment[j].fileName = file.fileName;
                            $scope.newAttachment[j].type = file.type;
                            $scope.newAttachment[j].itemType = file.itemType;
                            $scope.newAttachment[j].programmed = file.programmed;
                            $scope.newAttachment[j].approved = file.approved;
                            $scope.newAttachment[j].lastUpdateBy = file.lastUpdateBy;
                            $scope.newAttachment[j].lastUpdateData = file.lastUpdateData;
                            $scope.newAttachment[j].isMettingAttachment = file.isMettingAttachment;
                            break;
                        }
                    }

                }
            };

            $scope.initUpdate = function(objId){
                $scope.getMarkById(objId);
                $scope.getFeedbackByID(objId);
                $scope.queryAuditLogsByBusinessId(objId);
                $scope.initPage();
                $http({
                    method:'post',
                    url:srvUrl+"deptwork/queryPreAllViewById.do",
                    data: $.param({"businessId": objId})
                }).success(function(data){
                    if(data.success){
                        //1、查正式评审基本信息
                        $scope.pre = data.result_data.projectInfo;
                        $scope.pre.apply.serviceType = $filter("keyValueNames")($scope.pre.apply.serviceType, "VALUE");
                        $scope.pre.apply.projectType = $filter("keyValueNames")($scope.pre.apply.projectType, "VALUE");
                        $scope.pre.apply.projectModel = $filter("keyValueNames")($scope.pre.apply.projectModel, "VALUE");
                        //处理附件
                        $scope.reduceAttachment(data.result_data.projectInfo.attachmentList, objId);
                        //2、一级业务单位意见
                        $scope.firstLevelOpinion = data.result_data.firstLevelOpinion;
                        //3、风控意见
                        $scope.fengkongOpinion = data.result_data.fengkongOpinion;
                        $scope.fileName=[];
                        var filenames=$scope.pre.attachment;
                        for(var i=0;i<filenames.length;i++){
                            var arr={UUID:filenames[i].UUID,ITEM_NAME:filenames[i].ITEM_NAME};
                            $scope.fileName.push(arr);
                        }
                        //4、投资评审报告
                        var file = attach_list('preReportInfo', $scope.businessId, 'preReport').result_data;
                        if (!isEmpty(file) && !isEmpty(data.result_data.report)){
                            $scope.report = data.result_data.report;
                        } else {
                            $scope.report = null;
                        }
                        //5、投资决策通知书
                        $scope.nod = data.result_data.noticeOfDecisionInfo;
                        //7、项目经验总结
                        $scope.arf = data.result_data.projectExperience;

                        $scope.oracleData = data.result_data.oracleData;
                        // 提交决策会材料
                        if ($scope.oracleData.STAGE >= 3.9){
                            $scope.getById(objId);
                        }

                        $timeout(function(){
                            angular.element(document).ready(function() {
                                $('ul.bs-tabdrop').tabdrop();
                            });
                        },5);

                    }else{
                        $.alert(data.result_name);
                    }
                });
            };
            /*//处理附件列表
            $scope.reduceAttachment = function(attachment){
                $scope.newAttachment = [];
                for(var i in attachment){
                    var files = attachment[i].files;
                    if(files!=null && undefined!=files){
                        var item_name = attachment[i].ITEM_NAME;
                        var uuid = attachment[i].UUID;
                        for(var j in files){
                            files[j].ITEM_NAME=item_name;
                            files[j].UUID=uuid;
                            $scope.newAttachment.push(files[j]);
                        }
                    }

                }
            }*/
            //在后台调用公共方法，把他们两个都放入一个map中  在前台取一下。
            $scope.getSelectTypeByCode=function(typeCode){
                var  url = 'common/commonMethod/selectDataDictionByCode';
                $scope.httpData(url,typeCode).success(function(data){
                    if(data.result_code === 'S'){
                        $scope.optionTypeList=data.result_data;
                    }else{
                        alert(data.result_name);
                    }
                });
            }
            $scope.getSelectTypeByCodeL=function(typeCode){
                var  url = 'common/commonMethod/selectDataDictionByCode';
                $scope.httpData(url,typeCode).success(function(data){
                    if(data.result_code === 'S'){

                        $scope.optionTypeListL=data.result_data;
                    }else{
                        alert(data.result_name);
                    }
                });
            }
            angular.element(document).ready(function() {
                $scope.getSelectTypeByCodeL("09");
                $scope.getSelectTypeByCode("06");
            });

            $scope.initUpdate(objId);
        }]);
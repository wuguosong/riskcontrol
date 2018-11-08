
ctmApp.register.controller('PreReviewReport', ['$http','$scope','$location','$routeParams','Upload','$timeout', function ($http,$scope,$location,$routeParams,Upload,$timeout) {
    $scope.pre={};
    $scope.pre.apply = {};
    $scope.pre.taskallocation={};
    $scope.pre.approveAttachment = {};
    $scope.pre.reviewReport={};
    $scope.pre.reviewReport.essentialInformation=[];
    $scope.pre.reviewReport.incomeEvaluation=[];
    $scope.dic=[];
    $scope.setDirectiveParam=function(columnName){
        $scope.columnName=columnName;
    }

    //初始化
    var complexId = $routeParams.id;
    $scope.paramId = complexId;
    var params = complexId.split("@");
    var objId = params[0];
    if(null!=params[2] && ""!=params[2]){
        $scope.flag=params[2];
    }
    if(null!=params[3] && ""!=params[3] && null!=params[4] && ""!=params[4] && null!=params[5] && ""!=params[5] && null!=params[6] && ""!=params[6] && null!=params[7] && ""!=params[7]){
        $scope.reportReturnId=params[3]+"@"+params[4]+"@"+params[5]+"@"+params[6]+"@"+params[7];
    }
    //查义所有的操作
    $scope.listProjectNameVyId=function(objId){
        var aMethed = 'projectPreReview/ProjectPreReview/getProjectPreReviewByID';
        $scope.httpData(aMethed,objId).success(
            function (data, status, headers, config) {
                var pthNameArr=[];
                $scope.pre = data.result_data;
                $scope.pre.apply.companyHeaderName=$scope.pre.apply.companyHeader.name;
                var paddNum = function(num){
                    num += "";
                    return num.replace(/^(\d)$/,"0$1");
                }
                var myDate = new Date();
                var dates= myDate.getFullYear() + "-" + paddNum(myDate.getMonth() + 1) + "-" + myDate.getDate() + " " + myDate.getHours() + ":" + myDate.getMinutes() + ":" + myDate.getSeconds();
                if(undefined==$scope.pre.reviewReport){
                        var models=params[1];
                        $scope.pre.reviewReport={};
                        $scope.pre.reviewReport.create_by = $scope.credentials.UUID;
                        $scope.pre.reviewReport.create_name = $scope.credentials.userName;
                        $scope.pre.reviewReport.models = models;
                        $scope.pre.reviewReport.create_date =dates;
                       if(models=='normal') {
                           $scope.pre.reviewReport.essentialInformation= {};
                           $scope.pre.reviewReport.projectConcernsIssues= {};


                           if (isEmpty($scope.pre.reviewReport.projectConcernsIssues)) {
                               $scope.pre.reviewReport.projectConcernsIssues = {projectSummary: null};
                               if (null == $scope.pre.reviewReport.projectConcernsIssues.projectSummary) {
                                   $scope.pre.reviewReport.projectConcernsIssues.projectSummary = "根据XX事业部现阶段提供的招标文件及XX部门提供的XX文件，现阶段的项目关注要点及问题总结如下：";
                               }
                               $scope.addtableRow(1);
                               $scope.addtableRow(2);
                               $scope.addtableRow(3);
                               $scope.addtableRow(4);
                           }
                           $scope.pre.reviewReport.essentialInformation.paymentTime=$scope.pre.apply.paymentTime;
                           $scope.pre.reviewReport.essentialInformation.tenderTime=$scope.pre.apply.tenderTime;
                           var wayValue='1';
                           if(undefined!=$scope.pre.reviewReport.essentialInformation.inputWay){
                               wayValue= $scope.pre.reviewReport.essentialInformation.inputWay;
                           }else{
                               $scope.pre.reviewReport.essentialInformation.inputWay='1';
                           }

                           if(wayValue==1){
                               $("input[name='preAssessmentScale']").css({"display":"block"});
                               $("#unit").css({"display":"block"});
                               $("textarea[name='preAssessmentScale']").css({"display":"none"});
                           }else{
                               $("#unit").parent().removeClass("has-feedback");
                               $("input[name='preAssessmentScale']").css({"display":"none"});
                               $("#unit").css({"display":"none"});
                               $("textarea[name='preAssessmentScale']").css({"display":"block"});
                           }

                           if(undefined==$scope.pre.reviewReport.incomeEvaluation || null==$scope.pre.reviewReport.incomeEvaluation){
                               $scope.addProfit();
                           }
                       }
                    $("#wordbtn").hide();
                }else{
                    $scope.pre.reviewReport.update_date=dates;
                    if($scope.pre.reviewReport.models=='normal') {
                        if (isEmpty($scope.pre.reviewReport.projectConcernsIssues)) {
                            $scope.pre.reviewReport.projectConcernsIssues = {projectSummary: null};
                            if (null == $scope.pre.reviewReport.projectConcernsIssues.projectSummary) {
                                $scope.pre.reviewReport.projectConcernsIssues.projectSummary = "根据XX事业部现阶段提供的招标文件及XX部门提供的XX文件，现阶段的项目关注要点及问题总结如下：";
                            }
                            $scope.addtableRow(1);
                            $scope.addtableRow(2);
                            $scope.addtableRow(3);
                            $scope.addtableRow(4);
                        }
                        $scope.pre.reviewReport.essentialInformation.paymentTime = $scope.pre.apply.paymentTime;
                        $scope.pre.reviewReport.essentialInformation.tenderTime = $scope.pre.apply.tenderTime;
                        var wayValue = '1';
                        if (undefined != $scope.pre.reviewReport.essentialInformation.inputWay) {
                            wayValue = $scope.pre.reviewReport.essentialInformation.inputWay;
                        } else {
                            $scope.pre.reviewReport.essentialInformation.inputWay = '1';
                        }

                        if (wayValue == 1) {
                            $("input[name='preAssessmentScale']").css({"display": "block"});
                            $("#unit").css({"display": "block"});
                            $("textarea[name='preAssessmentScale']").css({"display": "none"});
                        } else {
                            $("#unit").parent().removeClass("has-feedback");
                            $("input[name='preAssessmentScale']").css({"display": "none"});
                            $("#unit").css({"display": "none"});
                            $("textarea[name='preAssessmentScale']").css({"display": "block"});
                        }

                        if (undefined == $scope.pre.reviewReport.incomeEvaluation || null == $scope.pre.reviewReport.incomeEvaluation) {
                            $scope.addProfit();
                        }
                    }
                }

            }
        ).error(function (data, status, headers, config) {
            alert(status);
        });
    };
    function isEmpty(obj)
    {
        for (var name
            in obj)
        {
            return false;
        }
        return true;
    };
    //给第三部分添加行
    $scope.addtableRow=function(n){
        function addcellRow(array){
            var blankRow = {
                selectType:'',
                pointsAndProblems:'',
                opinionAndCommitment:''
            }
            array.push(blankRow);
        }
        if(undefined==$scope.pre.reviewReport){
            $scope.pre.reviewReport={projectConcernsIssues:{}};
        }
        if(n==1){
            if(undefined==$scope.pre.reviewReport.projectConcernsIssues){
                $scope.pre.reviewReport.projectConcernsIssues={influenceBidding:[]};
            }
            if(undefined==$scope.pre.reviewReport.projectConcernsIssues.influenceBidding){
                $scope.pre.reviewReport.projectConcernsIssues.influenceBidding=[];
            }
            addcellRow($scope.pre.reviewReport.projectConcernsIssues.influenceBidding);
        }else if(n==2){
            if(undefined==$scope.pre.reviewReport.projectConcernsIssues){
                $scope.pre.reviewReport.projectConcernsIssues={focusOnMatter:[]};
            }
            if(undefined==$scope.pre.reviewReport.projectConcernsIssues.focusOnMatter){
                $scope.pre.reviewReport.projectConcernsIssues.focusOnMatter=[];
            }
            addcellRow($scope.pre.reviewReport.projectConcernsIssues.focusOnMatter);
        }else if(n==3){
            if(undefined==$scope.pre.reviewReport.projectConcernsIssues){
                $scope.pre.reviewReport.projectConcernsIssues={agreementPoints:[]};
            }
            if(undefined==$scope.pre.reviewReport.projectConcernsIssues.agreementPoints){
                $scope.pre.reviewReport.projectConcernsIssues.agreementPoints=[];
            }
            addcellRow($scope.pre.reviewReport.projectConcernsIssues.agreementPoints);
        }
    }
  /*  function addBlankRow(array){
        var blankRow = {
            profitSubject:'',
            profitInvestment:'',
            profitPrivateCapital:'',
            profitCashFlowInfo:''
        }
        array.push(blankRow);
    }
   if($scope.pre.reviewReport.incomeEvaluation!=null){
        $scope.pre.reviewReport.incomeEvaluation = [];
        addBlankRow($scope.pre.reviewReport.incomeEvaluation);
    }*/
    //给第二部分添加行
    $scope.addProfit = function(){
        function addBlankRow(array){
            var blankRow = {
                profitSubject:'',
                profitInvestment:'',
                profitPrivateCapital:'',
                profitCashFlowInfo:''
            }
            array.push(blankRow);
        }
        if(undefined==$scope.pre.reviewReport.incomeEvaluation){
            $scope.pre.reviewReport.incomeEvaluation=[];
        }
        addBlankRow($scope.pre.reviewReport.incomeEvaluation);

    }
    //移除第二部分对应数据
    $scope.deleteProfit = function(){
            var commentsObj = $scope.pre.reviewReport.incomeEvaluation;
            if(commentsObj!=null){
                for(var i=0;i<commentsObj.length;i++){
                    if(commentsObj[i].selected){
                        commentsObj.splice(i,1);
                        i--;
                    }
                }
            }
    }
    $scope.createWordes=function(){
        var url =  'projectPreReview/ProjectPreReview/getPreAssessmentReport';
        $scope.httpData(url,$scope.pre._id).success(function (data) {
            if (data.result_code=="S") {
               // var filesPath=data.result_data.filePath;
                //var filesName=data.result_data.fileName;
               // window.location.href = srvUrl+"common/RcmFile/downLoad?filePath="+encodeURI(filesPath)+"&fileName="+encodeURI("预评审-"+filesName+"报告.docx");;

            } else {
                $.alert("提交系统报表生成失败，请查看必填项是否填写完毕；如果全部正常填写请联系管理员！");
            }
        });
    }
    $scope.createWord=function(id){
        startLoading();
        var url = 'projectPreReview/ProjectPreReview/getPreAssessmentReport';
        $scope.httpData(url,id).success(function (data) {
            if (data.result_code=="S") {
                var filesPath=data.result_data.filePath;
                var filesName=data.result_data.fileName;
                window.location.href = srvUrl+"common/RcmFile/downLoad?filePath="+encodeURI(filesPath)+"&fileName="+encodeURI("预评审-"+filesName+"报告.docx");;

            } else {
                $.alert("报表生成失败，请查看必填项是否填写完毕；如果全部正常填写请联系管理员！");
            }
        });
        endLoading();
    }

    $scope.saveReviewReportById=function(){
       if($scope.pre.reviewReport.models=='normal') {
           var bankLoan = $("#bankLoan").val();
           var privateCapital = $("#privateCapital").val();
           var sum = parseFloat(bankLoan) + parseFloat(privateCapital);
           var total = parseFloat(100);
           if (sum == total) {
               var url = 'projectPreReview/ProjectPreReview/saveReviewReportById';
               $scope.httpData(url, $scope.pre).success(function (data) {
                   if (data.result_code == 'S') {
                       $.alert('保存成功');
                       $("#wordbtn").show();
                   } else {
                       $.alert("保存失败");
                   }
               });
           }
           else {
               $.alert("必填项未填或银行贷款和自有资金之合须等于100%！");
           }
       }else{
           var filep=$scope.pre.reviewReport.filePath;
           if(null==filep || ""==filep){
               $.alert("请上传附件");
               return false;
           }
           var url = 'projectPreReview/ProjectPreReview/saveReviewReportById';
           $scope.httpData(url, $scope.pre).success(function (data) {
               if (data.result_code == 'S') {
                   $.alert('保存成功');
                   $("#wordbtn").show();
               } else {
                   $.alert("保存失败");
               }
           });
       }
    }
    $scope.submitReviewReport=function(){
        if($scope.pre.reviewReport.models=='normal') {
            var bankLoan = $("#bankLoan").val();
            var privateCapital = $("#privateCapital").val();
            var sum = parseInt(bankLoan) + parseInt(privateCapital);
            var total = parseInt(100);
            if ($("#myFormPfR").valid() && sum == total) {
                var url = 'projectPreReview/ProjectPreReview/saveReviewReportById';
                $scope.httpData(url, $scope.pre).success(function (data) {
                    if (data.result_code == 'S') {
                        $scope.createWordes($scope.pre._id);
                        $location.path("BiddingInfo/" + $routeParams.id);
                    } else {
                        $.alert("保存失败");
                    }
                });
            }
            else {
                $.alert("必填项未填或银行贷款和自有资金之合须等于100%！");
            }
        }else{
            var filep=$scope.pre.reviewReport.filePath;
            if(null==filep || ""==filep){
                $.alert("请上传附件");
                return false;
            }
            var url = 'projectPreReview/ProjectPreReview/saveReviewReportById';
            $scope.httpData(url, $scope.pre).success(function (data) {
                if (data.result_code == 'S') {
                    $location.path("BiddingInfo/" + $routeParams.id);
                } else {
                    $.alert("保存失败");
                }
            });
        }
    }
    //定义窗口action
    var action =$routeParams.action;
    $scope.actionparm=$routeParams.action;
    $scope.btnfiles=false;
    if (action == 'Update') {
        $scope.listProjectNameVyId(objId);
        $scope.titleName = "投资预评审报告修改";
    } else if (action == 'View') {
       $scope.listProjectNameVyId(objId);
        $('#savebtnreport').hide();
        $('#submitbnt').hide();
        $('#wordbtn').attr("disabled",false);
        /*$scope.btnfiles=true;*/
        $scope.titleName = "投资预评审报告查看";
    } else if (action == 'Create') {
        $scope.listProjectNameVyId(objId);
        $scope.pre.reviewReport.models = params[1];
        $scope.titleName = "投资预评审报告新增";
    }
    //第三分部删除行
    $scope.delrow=function(name,num){
            if(num==1) {
                var names = $scope.pre.reviewReport.projectConcernsIssues.influenceBidding;
                names.splice(name, 1);
                if (names.length > 0) {
                    $scope.pre.reviewReport.projectConcernsIssues.influenceBidding = names;
                } else {
                    $scope.pre.reviewReport.projectConcernsIssues.influenceBidding = [];
                }
            }else if(num==2) {
                var names = $scope.pre.reviewReport.projectConcernsIssues.focusOnMatter;
                names.splice(name, 1);
                if (names.length > 0) {
                    $scope.pre.reviewReport.projectConcernsIssues.focusOnMatter = names;
                } else {
                    $scope.pre.reviewReport.projectConcernsIssues.focusOnMatter = [];
                }
            }else if(num==3) {
                var names = $scope.pre.reviewReport.projectConcernsIssues.agreementPoints;
                names.splice(name, 1);
                if (names.length > 0) {
                    $scope.pre.reviewReport.projectConcernsIssues.agreementPoints = names;
                } else {
                    $scope.pre.reviewReport.projectConcernsIssues.agreementPoints = [];
                }
            }
    }
    $scope.setDirectiveOrgList=function(id,name){
        var params=$scope.columnName;
        if(params=="invertmentUnit"){
                $scope.pre.reviewReport.essentialInformation.invertmentUnit = {"name":name,value:id};
            $("#invertmentUnitValue").val(name);
            $("label[for='invertmentUnitName']").remove();
        }
        if(params=="operationsUnit"){
                $scope.pre.reviewReport.essentialInformation.operationsUnit = {"name":name,value:id};
            $("#operationsUnitValue").val(name);
            $("label[for='operationsUnitName']").remove();
        }
        if(params=="tenderCompany"){
            $scope.pre.reviewReport.essentialInformation.tenderCompany = {"name":name,value:id};
            $("#tenderCompanyName").val(name);
            $("label[for='tenderCompanyName']").remove();
        }
    }
    $scope.getselectDataDictionByCode=function(typeCode){
        var url="common/commonMethod/selectDataDictionByCode";
        $scope.httpData(url,typeCode).success(function(data){
            if(data.result_code === 'S'){
                $scope.fjjg=data.result_data;

            }else{
                alert(data.result_name);
            }
        });
    }
    angular.element(document).ready(function() {
        $scope.getselectDataDictionByCode('08');

    });
    function FormatDate() {
        var date = new Date();
        var paddNum = function(num){
            num += "";
            return num.replace(/^(\d)$/,"0$1");
        }
        return date.getFullYear()+""+paddNum(date.getMonth()+1);
    }
    $scope.errorAttach=[];
    $scope.uploadfformal2 = function (file,errorFile, idx) {
       if(null!=file) {
           var str = file.name;
           var index = str.lastIndexOf(".");
           str = str.substring(index + 1, str.length);
           if (str == "doc" || str == "DOC" || str == "docx" || str == "DOCX") {
           }else{
               $.alert("请上传word文件！");
               return false;
           }
       }
        if(errorFile && errorFile.length>0){
        	var errorMsg = fileErrorMsg(errorFile);
        	$scope.errorAttach[idx]={msg:errorMsg};
        }else if(file){
            var fileFolder = "preReport/";
            if($routeParams.action=='Create') {
                if(undefined==$scope.pre.apply.projectNo){
                    $.alert("请先选择项目然后上传附件");
                    return false;
                }
                var no=$scope.pre.apply.projectNo;
                fileFolder=fileFolder+FormatDate()+"/"+no;
            }else{
                var strs= new Array(); //定义一数组
                var dates=$scope.pre.reviewReport.create_date;
                if(undefined==dates || null==dates ){
                    dates=FormatDate();
                }else{
                    strs=dates.split("-"); //字符分割
                    dates=strs[0]+strs[1]; //分割后的字符输出
                }

                var no=$scope.pre.apply.projectNo;
                fileFolder=fileFolder+dates+"/"+no;
            }
            $scope.errorAttach[idx]={msg:''};
            Upload.upload({
                url:srvUrl+'common/RcmFile/upload',
                data: {file: file, folder:fileFolder}
            }).then(function (resp) {
                var retData = resp.data.result_data[0];
                $scope.pre.reviewReport.filePath=retData.filePath;
                $scope.pre.reviewReport.fileName=retData.fileName;
            }, function (resp) {
                console.log('Error status: ' + resp.status);
            }, function (evt) {
                var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                $scope["progress"+idx]=progressPercentage == 100 ? "":progressPercentage+"%";
            });
        }
    };

    $scope.downLoadFile = function(path,name){
        var filePath = path;
        var fileName=name;
        window.location.href = srvUrl+"common/RcmFile/downLoad?filePath="+filePath+"&fileName="+encodeURI(fileName);
    }
}]);
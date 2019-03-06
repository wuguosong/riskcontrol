define(['app', 'Service'], function (app) {
    app
        .register.controller('formalAssessmentInfoViewCtrl', ['$http', '$scope', '$location', '$stateParams', 'BEWG_URL', 'Window',
        function ($http, $scope, $location, $stateParams, BEWG_URL, Window) {
            console.log('formalAssessmentInfoView');

            $scope.businessId = $stateParams.id;
            var tabIndex = $stateParams.tabIndex

            //初始化
            var objId = $stateParams.id;
            $scope.pfr={};
            $scope.pfr.apply = {};
            $scope.pfr.taskallocation={};
            $scope.pfr.approveAttachment = {};
            $scope.pfr.pfrBusinessUnitCommit=[];
            $scope.pfr._id=objId;
            $scope.dic=[];
            $scope.myTaskallocation = {};

            //专业评审控制器
            $scope.approveShow = true;
            //法律评审控制器
            $scope.approveLegalShow = true;
            //初始化数据
            $scope.initData = function(){
                $scope.getFormalAssessmentByID(objId);
                $scope.getProjectPreReviewGDCYByID(objId);
                $scope.initPage();
                $scope.queryAuditLogsByBusinessId(objId);
            }

            //获取审核日志
            $scope.queryAuditLogsByBusinessId = function (businessId){
                $http({
                    method:'post',
                    url: BEWG_URL.SelectPfrAuditedLogsById,
                    data: $.param({"businessId":businessId})
                }).success(function(result){
                    $scope.auditLogs = result.result_data;
                });
            }

            //流程图相关
            $scope.initPage = function(){
                $scope.wfInfo.businessId = objId;
                $scope.refreshImg = Math.random()+1;
            }
            $scope.wfInfo = {processKey:'formalReview'};

            //处理附件列表
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
            }


            $scope.getFormalAssessmentByID=function(id){

                $http({
                    method:'post',
                    url: BEWG_URL.SelectPfrById,
                    data: $.param({"id":id})
                }).success(function(data){
                    $scope.pfr  = data.result_data.formalAssessmentMongo;
                    $scope.pfrOracle  = data.result_data.formalAssessmentOracle;
                    $scope.pfr.oracle  = data.result_data.formalAssessmentOracle;
                    //处理任务人
                    //数据回显
                    if($scope.pfr.taskallocation !=null ){
                        if($scope.pfr.taskallocation.reviewLeader!=null){
                            $scope.myTaskallocation.reviewLeader = $scope.pfr.taskallocation.reviewLeader;
                        }
                        if($scope.pfr.taskallocation.legalReviewLeader!=null){
                            $scope.myTaskallocation.legalReviewLeader = $scope.pfr.taskallocation.legalReviewLeader;
                        }
                        if($scope.pfr.taskallocation.fixedGroup!=null){
                            $scope.myTaskallocation.fixedGroup = $scope.pfr.taskallocation.fixedGroup;
                        }
                        if($scope.pfr.taskallocation.professionalReviewers!=null){
                            $scope.myTaskallocation.professionalReviewers = $scope.pfr.taskallocation.professionalReviewers;
                            $scope.currentUserId = data.result_data.currentUserId;
                        }
                    }

                    $scope.attach = data.result_data.attach;
                    //处理附件
                    $scope.reduceAttachment(data.result_data.formalAssessmentMongo.attachment);

                    var ptNameArr=[],pmNameArr=[],pthNameArr=[],fgNameArr=[];
                    var pt1NameArr=[];
                    var pt1=$scope.pfr.apply.serviceType;
                    $scope.serviceTypes = $scope.pfr.apply.serviceType;
                    if(null!=pt1 && pt1.length>0){
                        for(var i=0;i<pt1.length;i++){
                            pt1NameArr.push(pt1[i].VALUE);
                        }
                        $scope.pfr.apply.serviceType=pt1NameArr.join(",");
                    }
                    var pt=$scope.pfr.apply.projectType;
                    if(null!=pt && pt.length>0){
                        for(var i=0;i<pt.length;i++){
                            ptNameArr.push(pt[i].VALUE);
                        }
                        $scope.pfr.apply.projectType=ptNameArr.join(",");
                    }
                    var pm=$scope.pfr.apply.projectModel;
                    if(null!=pm && pm.length>0){
                        for(var j=0;j<pm.length;j++){
                            pmNameArr.push(pm[j].VALUE);
                        }
                        $scope.pfr.apply.projectModel=pmNameArr.join(",");
                    }
                    if(undefined!=$scope.pfr.taskallocation){
                        if(undefined!=$scope.pfr.taskallocation.fixedGroup){
                            var fg=$scope.pfr.taskallocation.fixedGroup;
                            if(null!=fg && fg.length>0){
                                for(var k=0;k<fg.length;k++){
                                    fgNameArr.push(fg[k].NAME);
                                }
                                $scope.pfr.taskallocation.fixedGroup=fgNameArr.join(",");
                            }
                        }
                    }
                    if(undefined==$scope.pfr.approveLegalAttachment){
                        $scope.approveLegalShow = false;
                        $scope.pfr.approveLegalAttachment = {};
//                $scope.addFormalLegalComment();
                    }
                    if(undefined==$scope.pfr.approveAttachment){
                        $scope.approveShow = false;
                        $scope.pfr.approveAttachment = {};
                    }
                    //显示专业评审
                    if(undefined==$scope.pfr.approveMajorCommonts){
                        $scope.approveMajorShow = false;
                    }

                    $scope.fileName=[];
                    var filenames=$scope.pfr.attachment;
                    for(var i=0;i<filenames.length;i++){
                        var arr={UUID:filenames[i].UUID,ITEM_NAME:filenames[i].ITEM_NAME};
                        $scope.fileName.push(arr);
                    }
                    if (null != $scope.pfr.apply.expectedContractDate) {
                        $scope.changDate($scope.pfr.apply.expectedContractDate);
                    }
                });
            }
            $scope.changDate=function(values){
                var date = new Date();
                var paddNum = function(num){
                    num += "";
                    return num.replace(/^(\d)$/,"0$1");
                }
                var nowDate=date.getFullYear()+"-"+paddNum(date.getMonth()+1)+"-"+paddNum(date.getDate());
                var d=DateDiff(values,nowDate);
                $("#shouwdate").text("距离签约时间还差："+d+"天");
            }

            $scope.getSelectTypeByCode=function(typeCode){
                /*var  url = 'common/commonMethod/selectDataDictionByCode';*/
                $scope.httpData(BEWG_URL.SelecttDataDictionByCode,typeCode).success(function(data){
                    if(data.result_code === 'S'){
                        $scope.optionTypeList=data.result_data;
                    }else{
                        alert(data.result_name);
                    }
                });
            }
            $scope.getSelectTypeByCode2=function(typeCode){
                $scope.httpData(BEWG_URL.SelecttDataDictionByCode,typeCode).success(function(data){
                    if(data.result_code === 'S'){
                        $scope.optionTypeList2=data.result_data;
                    }else{
                        alert(data.result_name);
                    }
                });
            }
            /*查询固定成员审批列表*/
            $scope.getProjectPreReviewGDCYByID=function(id){
                $scope.panam={taskDefKey:'usertask4' ,businessId:id};
                $scope.httpData(BEWG_URL.SelectPfrTaskOpinion,$scope.panam).success(function(data){
                    $scope.gdcy  = data.result_data;
                });
            }
            /*查询一级审批列表*/
            $scope.getProjectPreReviewYJDWByID=function(id){
                $scope.panam={taskDefKey:'usertask16' ,businessId:id};
                $scope.httpData(BEWG_URL.SelectPfrTaskOpinionTwo,$scope.panam).success(function(data){
                    if(data.result_code === 'S') {
                        var yijd = data.result_data;
                        if (null != yijd) {
                            $scope.yjdw = yijd.DEPTNAME+" _ "+yijd.USERNAME+"："+yijd.OPINION;
                            if(null!=$scope.pfr.cesuanFileOpinion && ''!=$scope.pfr.cesuanFileOpinion){
                                $scope.yjdw = $scope.yjdw+" ；<br/>项目投资测算意见："+$scope.pfr.cesuanFileOpinion;
                            }
                            $scope.yjdw=Utils.encodeTextAreaString($scope.yjdw);
                        }else{
                            $scope.yjdw=null;
                        }

                        var tzcs = data.result_data;
                        if (null != tzcs) {
                            if(null!=$scope.pfr.tzProtocolOpinion && ''!=$scope.pfr.tzProtocolOpinion){
                                $scope.xyyj ="项目投资协议意见：" + $scope.pfr.tzProtocolOpinion;
                            }
                            $scope.xyyj=Utils.encodeTextAreaString($scope.xyyj);
                        }else{
                            $scope.xyyj=null;
                        }

                    }
                });
            }
            var Utils = {};
            // textArea换行符转<br/>
            Utils.encodeTextAreaString= function(str) {
                var reg = new RegExp("<br/>", "g");
                str = str.replace(reg, "\n");
                return str;
            }
            //根据id查询决策通知书决策意见
//	$scope.getNoticeOfDecstionByProjectFormalID=function(pid){
//		var url="formalAssessment/NoticeOfDecision/getNoticeOfDecstionByProjectFormalID";
//		$scope.httpData(url,pid).success(function(data){
//			if(data.result_code === 'S'){
//				if(undefined!=data.result_data) {
//					$scope.noticofDec=data.result_data;
//					var c = $scope.noticofDec.consentToInvestment;
//					if (c == "1") {
//						$scope.consentToInvestment = "同意投资";
//					} else if (c == "2") {
//						$scope.consentToInvestment = "不同意投资";
//					} else if (c == 3) {
//						$scope.consentToInvestment = "同意有条件投资";
//					}
//					$scope.implementationMatters = $scope.noticofDec.implementationMatters;
//					$scope.dateOfMeeting = $scope.noticofDec.dateOfMeeting;
//				}else{
//					$scope.consentToInvestment=null;
//					$scope.implementationMatters=null;
//				}
//			}else{
//				alert(data.result_name);
//			}
//		});
//	}
            //模态框
            $scope.showSubmitModal = function(){
                $scope.approve = {
                    operateType: "submit",
                    processKey: "formalAssessment",
                    businessId: objId,
                    callbackSuccess: function(result){
                        Window.alert(result.result_name);
                        $('#submitModal').modal('hide');
                        $("#submibtnn").hide();
                        $scope.initData();
                    },
                    callbackFail: function(result){
                        Window.alert(result.result_name);
                    }
                };
                $('#submitModal').modal('show');
            };


            // 返回列表
            $scope.cancel = function (){
                $location.path("/index/FormalAssessmentInfoList/"+tabIndex);
            }
            $scope.initData();

            angular.element(document).ready(function() {
                $scope.getSelectTypeByCode("06");
                $scope.getSelectTypeByCode2("09");
            });

        }]);
});
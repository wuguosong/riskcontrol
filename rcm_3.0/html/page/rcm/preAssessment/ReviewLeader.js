
ctmApp.register.controller('ReviewLeader', ['$http','$scope','$location','$routeParams', function ($http,$scope,$location,$routeParams) {
    $scope.dic=[];
    $scope.pre={};
    $scope.pre.taskallocation={};
    $scope.pre.apply = {};
    $scope.reviewLeaderModel = '{"NAME":"","VALUE":""}';
     $('#banckBtn').hide();
     $('#overbtn').hide();
     $('#submitbtn').hide();
     $scope.saveReviewLeader=function(openPopWin){
    	 $scope.reviewLeaderModel = $("#reviewLeadername option:selected").val();
         var  url = 'projectPreReview/ProjectPreReview/saveProReviewCommentsByID';
    	 var jsonLeader = eval("("+$scope.reviewLeaderModel+")");
    	 $scope.pre.taskallocation.reviewLeader=jsonLeader;
         var fixedGroup = $scope.pre.taskallocation.fixedGroup;
         /*if( $scope.pre.taskallocation.reviewLeader!=null && $scope.pre.taskallocation.reviewLeader.VALUE!=""
        	 &&(fixedGroup==null||fixedGroup=="")){
         	$.alert("请选择评审小组固定成员!");
         	return false;
         }*/
         $scope.httpData(url,$scope.pre).success(function(data){
             if(data.result_code === 'S'){
            	 $scope.reviewLeaderModel = $("#reviewLeadername option:selected").val();
                 openPopWin();
             }else{
            	 $.alert("保存任务分配人失败");
             }
         });
     }
    $scope.getSelectTypeByCode=function(typeCode){
        var  url = 'common/commonMethod/getRoleuser';
        $scope.httpData(url,typeCode).success(function(data){
            if(data.result_code === 'S'){
                $scope.dic=[];
                $scope.dic.userRoleListall=data.result_data.userRoleList;
            }else{
                alert(data.result_name);
            }
        });
    }
    $scope.getCommonteam=function(){
         var  url = 'common/commonMethod/getTeamList';
        $scope.httpData(url,{}).success(function(data){
             $scope.taskal= data.result_data;
         });
      }
    	$scope.getProjectPreReviewByID=function(id){
         var  url = 'projectPreReview/ProjectPreReview/getProjectPreReviewByID';
            $scope.httpData(url,id).success(function(data){
	             var pthNameArr=[],ptNameArr=[],ccNameArr=[],pmNameArr=[],pmValueArr=[],fgNameArr=[],fgValueArr=[];
	            
	             $scope.pre  = data.result_data;
	             if($scope.pre.taskallocation == null){
	            	 $scope.pre.taskallocation = {};
	             }else if($scope.pre.taskallocation.reviewLeader!=null && $scope.pre.taskallocation.reviewLeader.NAME!=null){
	            	 $scope.reviewLeaderModel=JSON.stringify($scope.pre.taskallocation.reviewLeader);
	             }
	          /*  var pth=$scope.pre.apply.companyHeader;
	            if(null!=pth && pth.length>0){
	                for(var i=0;i<pth.length;i++){
	                    pthNameArr.push(pth[i].name);
	                }
	                $scope.pre.apply.companyHeaderName=pthNameArr.join(",");
	            }*/
	                var pt1NameArr=[];
	                var pt1=$scope.pre.apply.serviceType;
	                if(null!=pt1 && pt1.length>0){
	                    for(var i=0;i<pt1.length;i++){
	                        pt1NameArr.push(pt1[i].VALUE);
	                    }
	                    $scope.pre.apply.serviceType=pt1NameArr.join(",");
	                }
	             var pt=$scope.pre.apply.projectType;
	             if(null!=pt && pt.length>0){
	                 for(var i=0;i<pt.length;i++){
	                     ptNameArr.push(pt[i].VALUE);
	                 }
	                 $scope.pre.apply.projectType=ptNameArr.join(",");
	             }
	             var pm=$scope.pre.apply.projectModel;
	             if(null!=pm && pm.length>0){
	                 for(var j=0;j<pm.length;j++){
	                  pmNameArr.push(pm[j].VALUE);
	                 }
	                 $scope.pre.apply.projectModel=pmNameArr.join(",");
	             }
	             var fg=$scope.pre.taskallocation.fixedGroup;
	
	             if(null!=fg && fg.length>0){
	                 for(var k=0;k<fg.length;k++){
	                     fgNameArr.push(fg[k].NAME);
	                     fgValueArr.push(fg[k].VALUE);
	                 }
	                 commonModelValue2(fgValueArr,fgNameArr);
	             }
                $scope.getProjectPreReviewYJDWByID(objId);
             if(reviewLeader!=""){
                 $('#banckBtn').show();
                 $('#overbtn').show();
                 $('#submitbtn').show();
             }
                if(null!=$scope.pre.apply.tenderTime) {
                    $scope.changDate($scope.pre.apply.tenderTime);
                }
            });
        }
    $scope.changDate=function(values) {
        var nowDate=null;
        nowDate=$scope.pre.create_date;
        if(null==nowDate || ""==nowDate){
            var date = new Date();
            var paddNum = function (num) {
                num += "";
                return num.replace(/^(\d)$/, "0$1");
            }
            nowDate = date.getFullYear() + "-" + paddNum(date.getMonth() + 1) + "-" + paddNum(date.getDate());
        }else{
            nowDate=nowDate.substr(0,10);
        }
        var d=DateDiff(values,nowDate);
        if(d<=5){
            $("#showdate").text("申请距离投标时间："+d+"天").attr("style","color:#ff0000;font-weight:bold");
        }else{
            $("#showdate").text("申请距离投标时间："+d+"天").removeAttr("style","color:#ff0000");
        }
    }
     var commonModelValue2=function(arrID,arrName){
         var leftstr2="<li class=\"select2-search-choice\"><div>";
         var centerstr2="</div><a class=\"select2-search-choice-close\" tabindex=\"-1\" onclick=\"delSelectmember(this,'";
         var addID2="');\"  ></a><div class=\"full-drop\"><input type=\"hidden\" id=\"\"  value=\"";
         var rightstr2="\"></div></li>";
         for(var i=0;i<arrName.length;i++){
              $("#headerfixed-member-box").find(".select2-search-field").before(leftstr2+arrName[i]+centerstr2+arrName[i]+"','"+arrID[i]+addID2+arrID[i]+rightstr2);
         }
     }
     //初始化
    var complexId = $routeParams.id;
    var params = complexId.split("@");
    var objId = params[0];

    if(null!=params[4] && ""!=params[4]){
        $scope.flag=params[4];
    }
    
    $scope.getProjectPreReviewByID(objId);
    $scope.getCommonteam();
    angular.element(document).ready(function() {
        $scope.getSelectTypeByCode("65fc76c0-829a-4670-bffa-bbcee8b70c53");
    });
    /*查询一级审批列表*/
    $scope.getProjectPreReviewYJDWByID=function(id){
        var  url = 'rcm/ProjectInfo/getTaskOpinionTwo';
        $scope.panam={taskDefKey:'usertask16' ,businessId:id};
        $scope.httpData(url,$scope.panam).success(function(data){
            if(data.result_code === 'S') {
                var yijd = data.result_data;
                if (null != yijd) {
                    $scope.yjdw = yijd.DEPTNAME+" _ "+yijd.USERNAME+"："+yijd.OPINION;
                    if(null!=$scope.pre.cesuanFileOpinion && ''!=$scope.pre.cesuanFileOpinion){
                        $scope.yjdw = $scope.yjdw+" ；<br/>项目投资测算意见："+$scope.pre.cesuanFileOpinion;
                    }
                    if(null!=$scope.pre.tzProtocolOpinion && ''!=$scope.pre.tzProtocolOpinion){
                        $scope.yjdw = $scope.yjdw+" ；<br/>项目投资协议意见：" + $scope.pre.tzProtocolOpinion;
                    }
                    $scope.yjdw=Utils.encodeTextAreaString($scope.yjdw);
                }else{
                    $scope.yjdw=null;
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

    //提交流程
    $scope.showSubmitModal=function(){
        $scope.saveReviewLeader(function(){
        	var currentUserName = $scope.credentials.userName;
            var approveUser = $scope.evalJsonStr($scope.reviewLeaderModel);
            var investmentManager = $scope.pre.apply.investmentManager;
            //查询评审组长
            $scope.httpData("rcm/Pteam/getTeamLeaderByMemberId",{teamMemberId:approveUser.VALUE,type:'1'}).success(function(data){
                if(data.result_code=='S'){
                	$scope.approve = {"isAllocateTask":true};
                    $scope.approve.approve = [{
                        submitInfo:{
                            taskId:params[3],
                            businessId:params[0],
                            runtimeVar:{inputUser:approveUser.VALUE,toTask:0},
                            currentTaskVar:{opinion:'请审批'},
                            noticeInfo:{
                                infoSubject:$scope.pre.apply.projectName+'预评审任务分配给了'+approveUser.NAME+'，请悉知！',
                                businessId:params[0],
                                remark:'',
                                formKey:'/ProjectPreReviewView/'+params[0]+'@'+params[1]+'@'+params[2],
                                createBy:$scope.credentials.UUID,
                                reader:[data.result_data],
                                type:'1',
                                custText01:'预评审'
                            }
                        },
                        showInfo:{destination:'评审负责人审批',approver:approveUser.NAME}
                    },{
                        submitInfo:{
                            taskId:params[3],
                            businessId:params[0],
                            runtimeVar:{inputUser:investmentManager.value,toTask:2},
                            currentTaskVar:{opinion:'请修改'}
                        },
                        showInfo:{destination:'退回起草人',approver:investmentManager.name}
                    },{
                        submitInfo:{
                        	businessId:params[0],
                            taskId:params[3],
                            runtimeVar:{toTask:1}
                        },
                        toNodeType:'end'
                    }];
                    $('#submitModal').modal('show');
                }else{
                    $.alert('查询评审组长失败');
                }
            });
        });
    }
    $scope.wfInfo = {processDefinitionId:params[1],processInstanceId:params[2]};
}]);
function delSelectmember(o,nameval,id){
    $(o).parent().remove();
    accessScope("#fixedGroupName", function(scope){
        var names=scope.pre.taskallocation.fixedGroup;
        names.splice(id,1);
        if(names.length>0){
            scope.pre.taskallocation.fixedGroup=names;
        }else{
            scope.pre.taskallocation.fixedGroup=null;
        }
    });
}

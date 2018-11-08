ctmApp.register.controller('AuctionResultFeedback', ['$http','$scope','$location','$routeParams','Upload','$timeout', function ($http,$scope,$location,$routeParams,Upload,$timeout) {
    $scope.arf={projectName:{},projectNo:{}};
    $scope.oldUrl = $routeParams.url;
    var objId=  $routeParams.id;
    $scope.id=objId;
    function FormatDate() {
        var date = new Date();
        var paddNum = function(num){
            num += "";
            return num.replace(/^(\d)$/,"0$1");
        };
        return date.getFullYear()+""+paddNum(date.getMonth()+1);
    }
    $scope.getFeedbackByID=function(id){
        var  url = 'projectPreReview/Feedback/getFeedbackByID';
        $scope.httpData(url,id).success(function(data){
                $scope.arf = data.result_data;
            }
        ).error(function (data, status, headers, config) {
            alert(status);
        });
    };

    $scope.addOption = function(){
        function addBlankRow(array){
            var blankRow = {
                name:'',
                price:''
            };
            var size=0;
            for(attr in array){
                size++;
            }
            array[size]=blankRow;
        }

        if(undefined==$scope.arf){
            $scope.arf={projectOtherprice:[]};
        }
        if(undefined==$scope.arf.projectOtherprice){
            $scope.arf.projectOtherprice=[];
        }
        addBlankRow($scope.arf.projectOtherprice);
    };
    $scope.delOption = function(){
    	var otherPriceList = $scope.arf.projectOtherprice;
    	if(otherPriceList!=null){
            for(var i=0;i<otherPriceList.length;i++){
                if(otherPriceList[i].selected){
                	otherPriceList.splice(i,1);
                    i--;
                }
            }
        }
    };
    //查义所有的操作
    $scope.listFormaltName=function(objId){
        var aMethed = 'projectPreReview/Feedback/selectName';
        $scope.httpData(aMethed,objId).success(
            function (data, status, headers, config) {
                $scope.pprs = data.result_data;
                var pname=$scope.pprs.apply.projectName;
                var pno=$scope.pprs.apply.projectNo;
                $scope.arf.projectName=$scope.pprs.apply.projectName;
                $scope.arf.projectNo=$scope.pprs.apply.projectNo;
            }
        ).error(function (data, status, headers, config) {
            alert(status);
        });
    };

    $scope.save= function () {
        var postObj;
        var url="";
        if(typeof ($scope.arf._id) !="undefined"){
            url ='projectPreReview/Feedback/updateFeedback';
        }else {
            url = 'projectPreReview/Feedback/create';
        }
        postObj=$scope.httpData(url,$scope.arf);
        postObj.success(function(data){
            if(data.result_code === 'S'){
            	$.alert("保存成功");
                $location.path("/FeedbackList");
            }else{
                alert(data.result_name);
            }
        });
    };
    var action =$routeParams.action;
    $scope.actionpam =$routeParams.action;
    if (action == 'Update') {
        $scope.getFeedbackByID(objId);
        $scope.titleName = "投标结果反馈修改";
    } else if (action == 'View') {
        $scope.getFeedbackByID(objId);
        $("#arfbtn").hide();
        $('#content-wrapper input').attr("disabled","disabled");
        $('textarea').attr("disabled","disabled");
        $('button').attr("disabled","disabled");
        $scope.titleName = "投标结果反馈查看";
    } else if (action == 'Create') {
       // $scope.listFormaltName(objId);
        $scope.arf.projectName=null;
        $scope.arf.create_by=$scope.credentials.UUID;
        $scope.arf.create_name=$scope.credentials.userName;
        $scope.titleName = "投标结果反馈新增";
    }
    $scope.setDirectiveCompanyList=function(code,name){
        $scope.arf.projectNo=code;
        $scope.arf.projectName=name;
        $("#projectName").val(name);
        $("label[for='projectName']").remove();
    };
    $scope.hideCls=function(){
    	if (action == 'View') {
	    	$("#arfbtn").hide();
	        $('#content-wrapper input').attr("disabled","disabled");
	        $('textarea').attr("disabled","disabled");
	        $('button').attr("disabled","disabled");
    	}
    };
   
    //附件上传
    $scope.errorAttach=[];
    $scope.upload = function (file,errorFile, idx) {
        if(errorFile && errorFile.length>0){
        	var errorMsg = fileErrorMsg(errorFile);
        	$scope.errorAttach[idx]={msg:errorMsg};
        }else if(file){
            var fileFolder = "preAssessmentFeedbackInfo/";
            if($routeParams.action=='Create') {
                if(undefined==$scope.arf.projectNo){
                    $.alert("请先选择项目然后上传附件");
                    return false;
                }
                var no=$scope.arf.projectNo;
                fileFolder=fileFolder+FormatDate()+"/"+no;
            }else{
                var dates=$scope.arf.create_date;
                var no=$scope.arf.projectNo;
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
                retData.version = "1";
                var myDate = new Date();
                retData.upload_date=myDate.getFullYear()+"-"+myDate.getMonth()+"-"+ myDate.getDate()+" "+myDate.getHours()+":"+myDate.getMinutes()+":"+myDate.getSeconds();
                $scope.arf.projectNoticefile=retData;
            }, function (resp) {
                console.log('Error status: ' + resp.status);
            }, function (evt) {
                var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                $scope["progress"+idx]=progressPercentage == 100 ? "":progressPercentage+"%";
            });
        }
    };
    $scope.downLoadFile = function(df){
        window.location.href = srvUrl+"common/RcmFile/downLoad?filePath="+df.filePath+"&fileName="+encodeURI(df.fileName);
    }
}]);
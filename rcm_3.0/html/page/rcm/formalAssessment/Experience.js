ctmApp.register.controller('Experience', ['$http','$scope','$location','$routeParams','Upload','$timeout', 
function ($http,$scope,$location,$routeParams,Upload,$timeout) {
	$scope.oldUrl = $routeParams.url;
	
	$scope.arf={projectName:{},projectNo:{},create_date:{},projectSize:{}};
    var objId=  $routeParams.id;
    var pname = {};
    $scope.id=objId;

    $scope.getExperienceByID=function(id){
        var  url = 'projectPreReview/Feedback/getExperienceByID';
        $scope.httpData(url,id).success(function(data){
                $scope.arf = data.result_data;
            }
        ).error(function (data, status, headers, config) {
            alert(status);
        });
    };
    $scope.addFileList = function(){
        function addBlankRow(array){
            var blankRow = {
                file_content:''
            }
            var size=0;
            for(attr in array){
                size++;
            }
            array[size]=blankRow;
        }

        if(undefined==$scope.arf){
            $scope.arf={fileList:[]};

        }
        if(undefined==$scope.arf.fileList){
            $scope.arf.fileList=[];
        }
        addBlankRow($scope.arf.fileList);
    }
    $scope.commonDdelete = function(){
        var commentsObj = $scope.arf.fileList;
        if(commentsObj!=null){
            for(var i=0;i<commentsObj.length;i++){
                if(commentsObj[i].selected){
                    commentsObj.splice(i,1);
                    i--;
                }
            }
        }
    }
    //查义所有的操作
    $scope.listProjectName=function(objId){
        var aMethed = 'formalAssessment/FormalReport/getByID';
        $scope.httpData(aMethed,objId).success(
            function (data, status, headers, config) {
                $scope.pprs = data.result_data;
                $scope.arf.reportId=$scope.pprs._id;
                $scope.arf.projectName=$scope.pprs.projectName;
                $scope.arf.projectFormalId=$scope.pprs.projectFormalId;
                $scope.arf.projectNo=$scope.pprs.projectNo;
                $scope.arf.create_date=$scope.pprs.create_date;
                $scope.arf.create_id=$scope.credentials.UUID;
                $scope.arf.create_Name=$scope.credentials.userName;
            }
        ).error(function (data, status, headers, config) {
            alert(status);
        });
    };

    $scope.save= function () {
        if ($("#Experience").valid()) {
            var postObj;
            var url = "";
            if (typeof ($scope.arf._id) != "undefined") {
                url =  'projectPreReview/Feedback/updateExperience';
            } else {
                url =  'projectPreReview/Feedback/createExperience';
            }
            postObj = $scope.httpData(url, $scope.arf);
            postObj.success(function (data) {
                if (data.result_code === 'S') {
                	$.alert("保存成功！");
                    $location.path("/ExperienceList");
                } else {
                    alert(data.result_name);
                }
            });
        }
    }
    $scope.submitbtn= function () {
        if ($("#Experience").valid()) {
            $.confirm("确认要提交吗？提交后不可以再修改信息！", function(s) {
                var postObj;
                var url = "";
                if (typeof ($scope.arf._id) != "undefined") {
                    url =  'projectPreReview/Feedback/updateExperience';
                } else {
                    url =  'projectPreReview/Feedback/createExperience';
                }
                $scope.arf.state = "2";
                postObj = $scope.httpData(url, $scope.arf);
                postObj.success(function (data) {
                    if (data.result_code === 'S') {
                        $.alert("提交成功！");
                        $location.path("/ExperienceList");
                    } else {
                        alert(data.result_name);
                    }
                });
            });
        }
    }
    var action =$routeParams.action;
    $scope.actions=$routeParams.action;
    if (action == 'Update') {
        $scope.getExperienceByID(objId);
        $scope.titleName = "项目经验总结修改";
    } else if (action == 'View') {
        $scope.getExperienceByID(objId);
        $('#content-wrapper input').attr("disabled","disabled");
        $('textarea').attr("readonly","readonly");
        $('select').attr("disabled","disabled");
        $('checkbox').attr("disabled","disabled");
        $("#savebtn").hide();
        $("#submitbtn").hide();
        $("button").attr("disabled","disabled");
        $scope.titleName = "项目经验总结查看";
    } else if (action == 'Create') {
        $scope.listProjectName(objId);
        $scope.titleName = "项目经验总结新增";
        $scope.addFileList();
       // $("#submitbtn").hide();
    }
    function FormatDate() {
        var date = new Date();
        var paddNum = function(num){
            num += "";
            return num.replace(/^(\d)$/,"0$1");
        }
        return date.getFullYear()+""+paddNum(date.getMonth()+1);
    }

    $scope.errorAttach=[];
    $scope.upload = function (file,errorFile, idx) {
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
            var fileFolder = "formalReport/";
            if($routeParams.action=='Create') {
                if(undefined==$scope.arf.projectNo){
                    $.alert("请先选择项目然后上传附件！");
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
                $scope.arf.fileList[idx].files=retData;
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
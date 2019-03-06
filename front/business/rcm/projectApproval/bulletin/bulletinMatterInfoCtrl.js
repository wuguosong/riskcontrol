define(['app', 'Service'], function (app) {
    app
        .register.controller('bulletinMatterInfoCtrl', ['$http', '$scope', '$location', '$stateParams', '$filter', 'BEWG_URL', 'Window',
        function ($http, $scope, $location, $stateParams, $filter, BEWG_URL, Window) {
            console.log('bulletinMatterInfo');

            $scope.mappedKeyValue = {
                "nameField" : "ORGNAME",
                "valueField" : "ORGID"
            };
            $scope.columns = [{
                "fieldName" : "单位名称" ,
                "fieldValue" : "ORGNAME"
            },{
                "fieldName" : "负责人" ,
                "fieldValue" : "FZRNAME"
            }]
            $scope.otherFields = ["FZRNAME","FZRID"];
            $scope.selectOrgCallback = function(checkedOrg){
                $scope.checkedOrg = checkedOrg;
                $scope.setUnitPerson($scope.checkedOrg.FZRID, $scope.checkedOrg.FZRNAME);
            };

            $scope.checkedOrg={
            };

            $scope.changeFzrModel = function(){
                if($scope.bulletin == null){
                    $scope.bulletin = {};
                }
                if($scope.bulletin.unitPerson == null){
                    $scope.bulletin.unitPerson = {};
                }
                if($scope.bulletin.applyUnit == null){
                    $scope.bulletin.applyUnit = {};
                }
                if($scope.checkedOrg.FZRID != null && $scope.checkedOrg.FZRID != ''){
                    $scope.bulletin.unitPerson.NAME=$scope.checkedOrg.FZRNAME ;
                    $scope.bulletin.unitPerson.VALUE=$scope.checkedOrg.FZRID ;
                    $scope.bulletin.applyUnit.NAME = $scope.checkedOrg.ORGNAME;
                    $scope.bulletin.applyUnit.VALUE = $scope.checkedOrg.ORGID;

                }else{
                    $scope.bulletin.unitPerson.NAME='' ;
                    $scope.bulletin.unitPerson.VALUE='' ;
                    $scope.bulletin.applyUnit.NAME = '';
                    $scope.bulletin.applyUnit.VALUE = '';
                }

            }
            $scope.$watch('checkedOrg',  $scope.changeFzrModel, true);
            var queryParamId = $stateParams.id;
            $scope.bulletin = {
                createTime: $filter("date")(new Date(), "yyyy-MM-dd HH:mm:ss"),
                applyUser: {VALUE: $scope.credentials.UUID,NAME: $scope.credentials.userName},
                businessPerson: {}
            };
            $scope.initDefaultData = function(){
                $scope.wfInfo = {processKey:'bulletin'};
                $scope.refreshImg = Math.random()+1;
                if("0" == queryParamId){
                    $scope.initCreate();
                }else{
                    $scope.initUpdate();
                }
            };

            //保存当前人申报单位
            $scope.APPLYINGUNIT_NAME;
            $scope.APPLYINGUNIT_ID;
            //保存当前人申报单位负责人
            $scope.APPLYHEADER_NAME;
            $scope.APPLYHEADER_ID;


            //保存原来申报单位
            $scope.APPLYINGUNIT_NAME_OLD;
            $scope.APPLYINGUNIT_ID_OLD;
            //保存原来申报单位负责人
            $scope.APPLYHEADER_NAME_OLD;
            $scope.APPLYHEADER_ID_OLD;
            $scope.initCreate = function(){
                $http({
                    method:'post',
                    url: BEWG_URL.SelectById,
                }).success(function(result){
                    if(!result.success){
                        Window.alert(result.result_name);
                    }else{
                        var data = result.result_data;
                        $scope.APPLYINGUNIT_NAME = data.REPORTINGUNIT_NAME,
                            $scope.APPLYINGUNIT_ID =data.REPORTINGUNIT_ID,

                            $scope.APPLYHEADER_NAME = data.COMPANYHEADER_NAME,
                            $scope.APPLYHEADER_ID = data.COMPANYHEADER_ID,
                            $scope.bulletin.applyUnit = {
                                NAME: data.REPORTINGUNIT_NAME,
                                VALUE: data.REPORTINGUNIT_ID
                            };
                        $scope.bulletin.unitPerson = {
                            NAME: data.COMPANYHEADER_NAME,
                            VALUE: data.COMPANYHEADER_ID
                        };
                        $scope.bulletin.applyUser = {
                            NAME: data.APPLYUSER_NAME,
                            VALUE: data.APPLYUSER_ID
                        };
                        $scope.tbsxType = data.tbsxType;
                        $scope.businessUsers = data.businessUsers;
                        $scope.isNotCityService = true;
                        $scope.isNotbusinessService = true;
                    }
                });
            };
            $scope.initUpdate = function(){
                $http({
                    method:'post',
                    url: BEWG_URL.SelectBulletin1ById,
                    data: $.param({"businessId": queryParamId})
                }).success(function(result){
                    var data = result.result_data;
                    $scope.bulletin = data.bulletinMongo;
                    $scope.checkedOrg = {
                        "ORGNAME" : $scope.bulletin.applyUnit.NAME ,
                        "ORGID" : $scope.bulletin.applyUnit.VALUE ,
                        "FZRID" :$scope.bulletin.unitPerson.VALUE,
                        "FZRNAME" :$scope.bulletin.unitPerson.NAME
                    }
                    $scope.tbsxType = data.tbsxType;
                    $scope.businessUsers = data.businessUsers;
                    //保存原来申报单位
                    //$scope.APPLYINGUNIT_NAME_OLD;
                    //$scope.APPLYINGUNIT_ID_OLD;
                    //保存原来申报单位负责人
                    //$scope.APPLYHEADER_NAME_OLD;
                    //$scope.APPLYHEADER_ID_OLD;
                    //申请人单位名称
                    $scope.APPLYINGUNIT_NAME_OLD = data.REPORTINGUNIT_NAME;
                    //申请人单位id
                    $scope.APPLYINGUNIT_ID_OLD = data.REPORTINGUNIT_ID;
                    //申请人单位负责人名称
                    $scope.APPLYHEADER_NAME_OLD = data.COMPANYHEADER_NAME;
                    $scope.APPLYHEADER_ID_OLD = data.APPLYUSER_ID;

                    $scope.APPLYHEADER_NAME = $scope.bulletin.unitPerson.NAME;
                    $scope.APPLYHEADER_ID = $scope.bulletin.unitPerson.VALUE;
                });
            };
            $scope.initDefaultData();
            // 修改事项类型事件
            $scope.changeTbsxType = function(){
                var tbsxTypeModel = $scope.tbsxTypeModel;
                if(tbsxTypeModel == null){
                    return;
                }
                if("0" == queryParamId){
                    //新增
                    if(tbsxTypeModel == ""){
                        $scope.bulletin.bulletinType = "";

                        $scope.bulletin.businessPerson.NAME = "";
                        $scope.bulletin.businessPerson.VALUE = "";
                    }else{
                        var jsonObj = JSON.parse(tbsxTypeModel);
                        $scope.bulletin.bulletinType = {NAME:jsonObj.ITEM_NAME, VALUE:jsonObj.UUID};
                        $scope.bulletin.businessPerson = {};

                        //如果是借款审批事项  申报单位变为可选，单位负责人先置空
                        if(jsonObj.ITEM_CODE == 'TBSX_BUSINESS_BORROWMONEY'){
                            $scope.isNotbusinessService = false;
                            $scope.isNotCityService = true;
                            for(var i = 0; i < $scope.businessUsers.length; i++){
                                var user = $scope.businessUsers[i];
                                if(user.CODE == jsonObj.ITEM_CODE){
                                    $scope.bulletin.businessPerson.NAME = user.USERNAME;
                                    $scope.bulletin.businessPerson.VALUE = user.USER_ID;
                                    break;
                                }
                            }
                            $scope.bulletin.applyUnit.NAME = '';
                            $scope.bulletin.applyUnit.VALUE = '';
                            $scope.bulletin.unitPerson.NAME = $scope.APPLYHEADER_NAME;
                            $scope.bulletin.unitPerson.VALUE = $scope.APPLYHEADER_ID;
                            return;

                        }else{
                            // 将当前人的所属单位回显到页面
                            $scope.bulletin.applyUnit.NAME = $scope.APPLYINGUNIT_NAME;
                            $scope.bulletin.applyUnit.VALUE = $scope.APPLYINGUNIT_ID;
                            // 将当前人的单位负责人回显到页面
                            $scope.bulletin.unitPerson.NAME =$scope.APPLYHEADER_NAME;
                            $scope.bulletin.unitPerson.VALUE =$scope.APPLYHEADER_ID;
                        }
                        if(jsonObj.ITEM_CODE == 'TBSX_BUSINESS_SUBCOMPANYTZ'){
                            //如果是北控城服的，就不需要设置业务负责人了
                            $scope.isNotCityService = false;
                            $scope.isNotbusinessService = true;
                            return;
                        }

                        $scope.isNotbusinessService = true;
                        $scope.isNotCityService = true;
                        for(var i = 0; i < $scope.businessUsers.length; i++){
                            var user = $scope.businessUsers[i];
                            if(user.CODE == jsonObj.ITEM_CODE){
                                $scope.bulletin.businessPerson.NAME = user.USERNAME;
                                $scope.bulletin.businessPerson.VALUE = user.USER_ID;
                                break;
                            }
                        }
                    }
                }else{
                    //修改
                    if(tbsxTypeModel == ""){
                        $scope.bulletin.bulletinType = "";
                        $scope.bulletin.businessPerson.NAME = "";
                        $scope.bulletin.businessPerson.VALUE = "";
                    }else{
                        var jsonObj = JSON.parse(tbsxTypeModel);
                        $scope.bulletin.bulletinType = {NAME:jsonObj.ITEM_NAME, VALUE:jsonObj.UUID};
                        $scope.bulletin.businessPerson = {};
                        //如果是借款审批事项  申报单位变为可选，单位负责人先置空
                        if(jsonObj.ITEM_CODE == 'TBSX_BUSINESS_BORROWMONEY'){
                            $scope.isNotbusinessService = false;
                            $scope.isNotCityService = true;
                            $scope.bulletin.unitPerson.NAME = $scope.APPLYHEADER_NAME;
                            $scope.bulletin.unitPerson.VALUE = $scope.APPLYHEADER_ID;
                            for(var i = 0; i < $scope.businessUsers.length; i++){
                                var user = $scope.businessUsers[i];
                                if(user.CODE == jsonObj.ITEM_CODE){
                                    $scope.bulletin.businessPerson.NAME = user.USERNAME;
                                    $scope.bulletin.businessPerson.VALUE = user.USER_ID;
                                    break;
                                }
                            }
                            return;
                        }
                        $scope.bulletin.applyUnit.NAME =$scope.APPLYINGUNIT_NAME_OLD;
                        $scope.bulletin.applyUnit.VALUE =$scope.APPLYINGUNIT_ID_OLD;
                        $scope.bulletin.unitPerson.NAME = $scope.APPLYHEADER_NAME_OLD;
                        $scope.bulletin.unitPerson.VALUE = $scope.APPLYHEADER_ID_OLD;

                        if(jsonObj.ITEM_CODE == 'TBSX_BUSINESS_SUBCOMPANYTZ'){
                            //如果是北控城服的，就不需要设置业务负责人了
                            $scope.isNotCityService = false;
                            $scope.isNotbusinessService = true;
                            for(var i = 0; i < $scope.businessUsers.length; i++){
                                var user = $scope.businessUsers[i];
                                if(user.CODE == jsonObj.ITEM_CODE){
                                    $scope.bulletin.businessPerson.NAME = user.USERNAME;
                                    $scope.bulletin.businessPerson.VALUE = user.USER_ID;
                                    break;
                                }
                            }
                            return;
                        }

                        $scope.isNotbusinessService = true;
                        $scope.isNotCityService = true;
                        for(var i = 0; i < $scope.businessUsers.length; i++){
                            var user = $scope.businessUsers[i];
                            if(user.CODE == jsonObj.ITEM_CODE){
                                $scope.bulletin.businessPerson.NAME = user.USERNAME;
                                $scope.bulletin.businessPerson.VALUE = user.USER_ID;
                                break;
                            }
                        }
                    }
                }


            }
            //保存
            $scope.save = function(callBack){
                if(!$("#myForm").valid()) {
                    Window.alert("有必填项未填！");
                    return false;
                }
                $http({
                    method:'post',
                    url: BEWG_URL.SaveOrUpdateBulletin,
                    data: $.param({"json": JSON.stringify($scope.bulletin)})
                }).success(function (data) {
                    if (data.success) {
                        $scope.bulletin._id = data.result_data;
                        if (callBack && typeof callBack == 'function') {
                            callBack();
                        }
                    }
                    if(callBack == null){
                        Window.alert(data.result_name);
                    }
                });
            };


            //提交
            $scope.showSubmitModal = function(){

                $scope.save(function(){
                    $scope.approve = {
                        operateType: "submit",
                        processKey: "bulletin",
                        businessId: $scope.bulletin._id,
                        callbackSuccess: function(result){
                            Window.alert(result.result_name);
                            $('#submitModal').modal('hide');
                            $("#subBtn").hide();
                            var oldurl = window.btoa(encodeURIComponent(escape("#/BulletinMatters/1")))
                            $location.path("/BulletinMattersAuditView/"+$scope.bulletin._id+"/"+oldurl);
                            //#/BulletinMattersAuditView/9b8e9d76984a415dbe4a31dd3c5b1ad2/JTI1MjMlMkZCdWxsZXRpbk1hdHRlcnMlMkYx
                        },
                        callbackFail: function(result){
                            Window.alert(result.result_name);
                        }
                    };
                    $('#submitModal').modal('show');
                });
            };
            //新增附件
            $scope.addFileList = function(){
                function addBlankRow(array){
                    var blankRow = {
                        file_content:'',
                        selected: false
                    };
                    var size=array.length;
                    array[size]=blankRow;
                }
                if(undefined==$scope.bulletin){
                    $scope.bulletin={fileList:[]};

                }
                if(undefined==$scope.bulletin.fileList){
                    $scope.bulletin.fileList=[];
                }
                addBlankRow($scope.bulletin.fileList);
            };
            $scope.commonDdelete = function(){
                var commentsObj = $scope.bulletin.fileList;
                if(commentsObj!=null){
                    for(var i=0;i<commentsObj.length;i++){
                        if(commentsObj[i].selected){
                            commentsObj.splice(i,1);
                            i--;
                        }
                    }
                }
            };
            $scope.errorAttach=[];
            $scope.upload = function (file,errorFile, idx) {
                if(errorFile && errorFile.length>0){
                    var errorMsg = fileErrorMsg(errorFile);
                    $scope.errorAttach[idx]={msg:errorMsg};
                }else if(file){

                    if(file.name){
                        //检查压缩文件
                        var index = file.name.lastIndexOf('.');
                        var suffix  = file.name.substring(index+1);
                        if("rar" == suffix || "zip" == suffix || "7z" == suffix){
                            Window.alert("附件不能是压缩文件！");
                            return false;
                        }
                    }

                    var fileFolder = "bulletin/attachments/"+$filter("date")(new Date(), "yyyyMM");
                    $scope.errorAttach[idx]={msg:''};
                    Upload.upload({
                        url:srvUrl+'common/RcmFile/upload',
                        data: {file: file, folder:fileFolder}
                    }).then(function (resp) {
                        var retData = resp.data.result_data[0];
                        $scope.bulletin.fileList[idx].files=retData;
                    }, function (resp) {
                        console.log('Error status: ' + resp.status);
                    }, function (evt) {
                        var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                        $scope["progress"+idx]=progressPercentage == 100 ? "":progressPercentage+"%";
                    });
                }
            };

            // 选择项目后，写入项目名称
            $scope.setDirectiveCompanyList=function(code,name){
                $scope.bulletin.projectNo=code;
                $scope.bulletin.projectName=name;
                $("#projectName").val(name);
                $("label[for='projectName']").remove();
            }

            // 修改大区后，修改单位负责人的值
            $scope.setUnitPerson = function(id, name){
                debugger
                $scope.bulletin.unitPerson.VALUE = id;
                $scope.bulletin.unitPerson.NAME = name;
            };

            // 返回列表
            $scope.cancel = function (){
                $location.path("/index/BulletinMatterList/0");
            };
        }]);
});
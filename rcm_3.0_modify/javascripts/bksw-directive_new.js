/*******************************************************公共指令开始***************************************************/
ctmApp.directive('directiveReturnBtn', function() {
    return {
        restrict: 'E',
        //templateUrl: 'page/sys/directive/projectFormal/DirectiveProjectFormalReview.html',
        template: '<a class="btn btn-info" ng-href="{{url|decodeURI}}" ng-click="callback()"><i class="fa fa-reply"></i>返回</a>',
        /*template: '<button class="btn btn-primary" ng-href="{{url|decodeURI}}" ng-click="callback()">返回</button>',*/
        replace: true,
        scope:{url:'@',callback:"&"},
        link:function(scope,element,attr){
        },
        controller:function($scope,$http,$element){
        }
    };
});
// 人员多选
ctmApp.directive('directUserMultiSelect', function() {
    return {
        restrict: 'E',
        templateUrl: 'page/sys/directive/directUserMultiSelect.html',
        replace: true,
        scope:{
            //必填,该指令所在modal的id，在当前页面唯一
            id: "@",
            //对话框的标题，如果没设置，默认为“人员选择”
            title: "@",
            //查询参数
            queryParams: "=",
            isEditable:"=?bind",
            //默认选中的用户,数组类型，[{NAME:'张三',VALUE:'user.uuid'},{NAME:'李四',VALUE:'user.uuid'}]
            checkedUsers: "=",
            //映射的key，value，{nameField:'username',valueField:'uuid'}，
            //默认为{nameField:'NAME',valueField:'VALUE'}
            mappedKeyValue: "=",
            callback: "="
        },
        controller:function($scope,$http,$element){
            if($scope.mappedKeyValue == null){
                $scope.mappedKeyValue = {nameField:'NAME',valueField:'VALUE'};
            }
            if($scope.checkedUsers == null){
                $scope.checkedUsers = [];
            }
            $scope.initDefaultData = function(){
                if($scope.title==null){
                    $scope.title = "人员选择";
                }
                if($scope.isEditable==null|| ($scope.isEditable!="true" && $scope.isEditable!="false")){
                    $scope.isEditable = "true";
                }
            };
            $scope.initDefaultData();
            $scope.removeSelectedUser = function(user){
                for(var i = 0; i < $scope.checkedUsers.length; i++){
                    if(user[$scope.mappedKeyValue.valueField] == $scope.checkedUsers[i][$scope.mappedKeyValue.valueField]){
                        $scope.checkedUsers.splice(i, 1);
                        break;
                    }
                }
            };
        }
    };
});
ctmApp.directive('directUserMultiDialog', function () {
    return {
        restrict: 'E',
        templateUrl: 'page/sys/directive/directUserMultiDialog.html',
        replace: true,
        scope: {
            //必填,该指令所在modal的id，在当前页面唯一
            id: "@",
            //对话框的标题，如果没设置，默认为“人员选择”
            title: "@",
            url: "@",
            //查询参数
            queryParams: "=",
            //默认选中的用户,数组类型，[{NAME:'张三',VALUE:'user.uuid'},{NAME:'李四',VALUE:'user.uuid'}]
            checkedUsers: "=",
            //映射的key，value，{nameField:'username',valueField:'uuid'}，
            //默认为{nameField:'NAME',valueField:'VALUE'}
            mappedKeyValue: "=",
            callback: "=",
            //移除选中的人员，调用父scope中的同名方法
//        	removeSelectedUser: "&"
            parentSaveSelected:"&"
        },
        controller: function ($scope, $http, $element) {
            ;
            if ($scope.url == null || '' == $scope.url) {
                $scope.url = "user/queryUserForSelected.do";
            }
            $scope.paginationConf = {
                lastCurrentTimeStamp: '',
                currentPage: 1,
                totalItems: 0,
                itemsPerPage: 10,
                pagesLength: 10,
                queryObj: {},
                perPageOptions: [10, 20, 30, 40, 50],
                onChange: function () {
                }
            };
            if (null != $scope.queryParams) {
                $scope.paginationConf.queryObj = $scope.queryParams;
            }
            $scope.queryUser = function () {
                $http({
                    method: 'post',
                    url: srvUrl + $scope.url,
                    data: $.param({"page": JSON.stringify($scope.paginationConf)})
                }).success(function (data) {
                    if (data.success) {
                        $scope.users = data.result_data.list;
                        $scope.paginationConf.totalItems = data.result_data.totalItems;
                    } else {
                        $.alert(data.result_name);
                    }
                });
            }
            $scope.removeSelectedUser = function (user) {
                for (var i = 0; i < $scope.tempCheckedUsers.length; i++) {
                    if (user.VALUE == $scope.tempCheckedUsers[i].VALUE) {
                        $scope.tempCheckedUsers.splice(i, 1);
                        break;
                    }
                }
            };
            $scope.isChecked = function (user) {
                for (var i = 0; i < $scope.tempCheckedUsers.length; i++) {
                    if (user.UUID == $scope.tempCheckedUsers[i].VALUE) {
                        return true;
                    }
                }
                return false;
            };
            $scope.toggleChecked = function (user) {
                //是否选中
                var isChecked = $("#chk_" + $scope.id + "_" + user.UUID).prop("checked");
                //是否已经存在
                var flag = false;
                for (var i = 0; i < $scope.tempCheckedUsers.length; i++) {
                    if (user.UUID == $scope.tempCheckedUsers[i].VALUE) {
                        flag = true;
                        if (!isChecked) {
                            $scope.tempCheckedUsers.splice(i, 1);
                            break;
                        }
                    }
                }
                if (isChecked && !flag) {
                    //如果已经选中，但是不存在，添加
                    $scope.tempCheckedUsers.push({"VALUE": user.UUID, "NAME": user.NAME});
                }
            };

            $scope.cancelSelected = function () {
                $scope.initData();
            }
            $scope.saveSelected = function () {
                var cus = $scope.tempCheckedUsers;
                $scope.checkedUsers.splice(0, $scope.checkedUsers.length);
                for (var i = 0; i < cus.length; i++) {
                    var user = {};
                    user[$scope.mappedKeyValue.nameField] = cus[i].NAME;
                    user[$scope.mappedKeyValue.valueField] = cus[i].VALUE;

                    $scope.checkedUsers.push(user);
                    delete user.$$hashKey;
                }
                if ($scope.callback != null) {
                    $scope.callback();
                }
                /***父页面进行添加 Add By LiPan****/
                if(!isEmpty($scope.parentSaveSelected) && (typeof $scope.parentSaveSelected === 'function')){
                    var executeEval = $scope.parentSaveSelected();
                    if(!isEmpty(executeEval)){
                        try{
                            eval(executeEval);
                        }catch(e){
                            console.log(e);
                        }
                    }
                }
                /***父页面进行添加 Add By LiPan****/
            }
            $scope.initData = function () {
                var cus = $.parseJSON(JSON.stringify($scope.checkedUsers));
                $scope.tempCheckedUsers = [];
                for (var i = 0; i < cus.length; i++) {
                    var user = {};
                    user.NAME = cus[i][$scope.mappedKeyValue.nameField];
                    user.VALUE = cus[i][$scope.mappedKeyValue.valueField];
                    $scope.tempCheckedUsers.push(user);
                }
                $scope.paginationConf.queryObj.username = '';
                $scope.queryUser();
            };
            $scope.$watch('checkedUsers', $scope.initData, true);
            $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.queryUser);
        }
    };
});
// 人员单选
ctmApp.directive('directUserSingleSelect', function() {
    return {
        restrict: 'E',
        templateUrl: 'page/sys/directive/directUserSingleSelect.html',
        replace: true,
        scope:{
            //必填,该指令所在modal的id，在当前页面唯一
            id: "@",
            //对话框的标题，如果没设置，默认为“人员选择”
            title: "@",
            //查询参数
            queryParams: "=",
            //是否可编辑
            isEditable:"=?bind",
            //默认选中的用户,数组类型，{NAME:'张三',VALUE:'user.uuid'}
            checkedUser: "=",
            //映射的key，value，{nameField:'username',valueField:'uuid'}，
            //默认为{nameField:'NAME',valueField:'VALUE'}
            mappedKeyValue: "=?bind",
            callback: "="
        },
        controller:function($scope,$http,$element){
            if($scope.mappedKeyValue == null){
                $scope.mappedKeyValue = {nameField:'NAME',valueField:'VALUE'};
            }
            if($scope.checkedUser == null){
                $scope.checkedUser = {};
            }
            $scope.initDefaultData = function(){
                if($scope.title==null){
                    $scope.title = "人员选择";
                }
                if($scope.isEditable==null|| ($scope.isEditable!="true" && $scope.isEditable!="false")){
                    $scope.isEditable = "true";
                }
            };
            $scope.removeSelectedUser = function(){
                $scope.checkedUser = {};
            };
            $scope.initDefaultData();
        }
    };
});
ctmApp.directive('directUserSingleDialog', function() {
    return {
        restrict: 'E',
        templateUrl: 'page/sys/directive/directUserSingleDialog.html',
        replace: true,
        scope:{
            //必填,该指令所在modal的id，在当前页面唯一
            id: "@",
            //对话框的标题，如果没设置，默认为“人员选择”
            title: "@",
            //查询参数
            queryParams: "=",
            //默认选中的用户,数组类型，{NAME:'张三',VALUE:'user.uuid'}
            checkedUser: "=",
            //映射的key，value，{nameField:'username',valueField:'uuid'}，
            //默认为{nameField:'NAME',valueField:'VALUE'}
            mappedKeyValue: "=?bind",
            callback: "=",
            //移除选中的人员，调用父scope中的同名方法
//        	removeSelectedUser: "&"
            parentSaveSelected:"&"
        },
        controller:function($scope,$http,$element){
            if($scope.mappedKeyValue == null){
                $scope.mappedKeyValue = {nameField:'NAME',valueField:'VALUE'};
            }
            $scope.initData = function(){
                var cus = $.parseJSON(JSON.stringify($scope.checkedUser));
                $scope.tempCheckedUser = {};
                $scope.tempCheckedUser.NAME = cus[$scope.mappedKeyValue.nameField];
                $scope.tempCheckedUser.VALUE = cus[$scope.mappedKeyValue.valueField];
            }
            $scope.paginationConf = {
                lastCurrentTimeStamp:'',
                currentPage: 1,
                totalItems: 0,
                itemsPerPage: 10,
                pagesLength: 10,
                queryObj:{},
                perPageOptions: [10, 20, 30, 40, 50],
                onChange: function(){
                }
            };
            $scope.queryUser = function(){
                $http({
                    method:'post',
                    url:srvUrl+"user/queryUserForSelected.do",
                    data: $.param({"page":JSON.stringify($scope.paginationConf)})
                }).success(function(data){
                    hide_Mask();
                    if(data.success){
                        $scope.users = data.result_data.list;
                        $scope.paginationConf.totalItems = data.result_data.totalItems;
                    }else{
                        $.alert(data.result_name);
                    }
                });
            }
            $scope.removeSelectedUser = function(){
                $scope.tempCheckedUser = {};
            };
            $scope.isChecked = function(user){
                if(user.UUID == $scope.tempCheckedUser.VALUE){
                    return true;
                }
                return false;
            };
            $scope.toggleChecked = function(user){
                //是否选中
                var isChecked = $("#chk_"+$scope.id+"_"+user.UUID).prop("checked");
                //是否已经存在
                var flag = false;
                if(user.UUID == $scope.tempCheckedUser.VALUE){
                    flag = true;
                    if(!isChecked){
                        $scope.tempCheckedUser = {};
                    }
                }
                if(isChecked && !flag){
                    //如果已经选中，但是不存在，添加
                    $scope.tempCheckedUser = {"VALUE":user.UUID,"NAME":user.NAME};
                }
            };

            $scope.cancelSelected = function(){
                $scope.initData();
            }
            $scope.saveSelected = function(){
                var cus = $scope.tempCheckedUser;
                if(cus.VALUE==null||cus.VALUE==""){
                    delete $scope.checkedUser[$scope.mappedKeyValue.nameField];
                    delete $scope.checkedUser[$scope.mappedKeyValue.valueField];
                }else{
                    $scope.checkedUser[$scope.mappedKeyValue.nameField] = cus.NAME;
                    $scope.checkedUser[$scope.mappedKeyValue.valueField] = cus.VALUE;
                }
                if($scope.callback != null){
                    $scope.callback();
                }
                /***父页面进行添加 Add By LiPan****/
                if(!isEmpty($scope.parentSaveSelected) && (typeof $scope.parentSaveSelected === 'function')){
                    var executeEval = $scope.parentSaveSelected();
                    if(!isEmpty(executeEval)){
                        try{
                            eval(executeEval);
                        }catch(e){
                            console.log(e);
                        }
                    }
                }
                /***父页面进行添加 Add By LiPan****/
            }
            $scope.$watch('checkedUser', $scope.initData);
            $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.queryUser);
        }
    };
});
// 项目名称选择
ctmApp.directive('directReportOrgSelect', function() {
    return {
        restrict: 'E',
        templateUrl: 'page/sys/directive/directReportOrgSelect.html',
        replace: true,
        scope:{
            //必填,该指令所在modal的id，在当前页面唯一
            id: "@",
            //对话框的标题，如果没设置，默认为“单位选择”
            title: "@",
            //必填，查询的url
            url: "@",
            //是否可编辑，默认为true
            isEditable:"=",
            //是否分页，默认为false
            isPage:"@",
            //查询参数，非必填
            queryParams: "=",
            //默认选中的单位，必填,必须有键和值,可以附带其它字段,例：{"NAME":"北控中国","VALUE":"单位uuid","其它字段1":"v1",...}
            checkedOrg: "=",
            //映射的key，value，如果checkedOrg的键不是默认的，该字段需给出，{nameField:'orgname',valueField:'uuid'}，
            //默认键应为{nameField:'NAME',valueField:'VALUE'}
            mappedKeyValue: "=",
            //其它附加字段的key组成的数组，非必填，例：["orgFzr","orgParent","orgPertainArea"]
            otherFields:"=",
            //必填，表格的列
            columns:"=",
            //确定按钮的回调方法，非必填
            callback: "="
        },
        controller:function($scope,$http,$element){
            if($scope.mappedKeyValue == null){
                $scope.mappedKeyValue = {nameField:'NAME',valueField:'VALUE'};
            }
            if($scope.isPage == null){
                $scope.isPage = "false";
            }
            if($scope.checkedOrg == null){
                $scope.checkedOrg = {};
            }
            $scope.initDefaultData = function(){
                if($scope.title==null){
                    $scope.title = "单位选择";
                }
                if($scope.isEditable==null|| ($scope.isEditable!="true" && $scope.isEditable!="false")){
                    $scope.isEditable = "true";
                }
            };
            $scope.removeSelectedOrg = function(){
//        		delete $scope.checkedOrg[$scope.mappedKeyValue.nameField];
//    			delete $scope.checkedOrg[$scope.mappedKeyValue.valueField];
//    			for(var i = 0; $scope.otherFields!=null && i < $scope.otherFields.length; i++){
//    				delete $scope.checkedOrg[$scope.otherFields[i]];
//        		}
                $scope.checkedOrg = {};
            };
            $scope.initDefaultData();
        }
    };
});
ctmApp.directive('directReportOrgDialog', function() {
    return {
        restrict: 'E',
        templateUrl: 'page/sys/directive/directReportOrgDialog.html',
        replace: true,
        scope:{
            //必填,该指令所在modal的id，在当前页面唯一
            id: "@",
            //对话框的标题，如果没设置，默认为“人员选择”
            title: "@",
            url: "@",
            //查询参数
            queryParams: "=",
            isPage:"=",
            //默认选中的单位，必填,必须有键和值,可以附带其它字段,例：{"NAME":"北控中国","VALUE":"单位uuid","其它字段1":"v1",...}
            checkedOrg: "=",
            //映射的key，value，如果checkedOrg的键不是默认的，该字段需给出，{nameField:'orgname',valueField:'uuid'}，
            //默认键应为{nameField:'NAME',valueField:'VALUE'}
            mappedKeyValue: "=",
            //其它附加字段的key组成的数组，非必填，例：["orgFzr","orgParent","orgPertainArea"]
            otherFields:"=",
            //必填，表格的列
            columns:"=",
            callback: "="
        },
        controller:function($scope,$http,$element){
            $scope.initData = function(){
                var cus = $.parseJSON(JSON.stringify($scope.checkedOrg));
                $scope.tempCheckedOrg = {};
                $scope.tempCheckedOrg.NAME = cus[$scope.mappedKeyValue.nameField];
                $scope.tempCheckedOrg.VALUE = cus[$scope.mappedKeyValue.valueField];
                for(var i = 0; $scope.otherFields!=null && i < $scope.otherFields.length; i++){
                    $scope.tempCheckedOrg[$scope.otherFields[i]] = cus[$scope.otherFields[i]];
                }
                $scope.queryOrg();
            }
            $scope.paginationConf = {
                lastCurrentTimeStamp:'',
                currentPage: 1,
                totalItems: 0,
                itemsPerPage: 10,
                pagesLength: 10,
                queryObj:{},
                perPageOptions: [10, 20, 30, 40, 50],
                onChange: function(){
                }
            };
            /*$scope.queryOrg = function(){
                $http({
                    method:'post',
                    url:srvUrl+"user/queryUserForSelected.do",
                    data: $.param({"page":JSON.stringify($scope.paginationConf)})
                }).success(function(data){
                    if(data.success){
                        $scope.users = data.result_data.list;
                        $scope.paginationConf.totalItems = data.result_data.totalItems;
                    }else{
                        $.alert(data.result_name);
                    }
                });
            }*/
            $scope.queryOrg = function(){
                var config = {
                    method:'post',
                    url:srvUrl + $scope.url
                };
                if("true" == $scope.isPage){
                    //分页
                    if($scope.queryParams != null){
                        $scope.paginationConf.queryObj = $scope.queryParams;
                    }
                    config.data = $.param({"page":JSON.stringify($scope.paginationConf)})
                }else{
                    //不分页
                    if($scope.queryParams != null){
                        config.data = $.param($scope.queryParams)
                    }
                }
                $http(config).success(function(data){
                    if(data.success){
                        if("true" == $scope.isPage){
                            $scope.orgs = data.result_data.list;
                            $scope.paginationConf.totalItems = data.result_data.totalItems;
                        }else{
                            $scope.orgs = data.result_data;
                        }
                    }else{
                        $.alert(data.result_name);
                    }
                });
            }
            $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage',  $scope.queryOrg);
            $scope.removeSelectedOrg = function(){
                $scope.tempCheckedOrg = {};
            };
            $scope.isChecked = function(org){
                if($scope.tempCheckedOrg != null && $scope.tempCheckedOrg.VALUE != null
                    && org[$scope.mappedKeyValue.valueField] == $scope.tempCheckedOrg.VALUE){
                    return true;
                }
                return false;
            };
            $scope.toggleChecked = function(org){
                //是否选中
                var isChecked = $("#chk_"+$scope.id+"_"+org[$scope.mappedKeyValue.valueField]).prop("checked");
                //是否已经存在
                var flag = false;
                if($scope.tempCheckedOrg != null && $scope.tempCheckedOrg.VALUE != null &&
                    org[$scope.mappedKeyValue.valueField] == $scope.tempCheckedOrg.VALUE){
                    flag = true;
                    if(!isChecked){
                        $scope.tempCheckedOrg = {};
                    }
                }
                if(isChecked && !flag){
                    //如果已经选中，但是不存在，添加
                    $scope.tempCheckedOrg = {"VALUE":org[$scope.mappedKeyValue.valueField],"NAME":org[$scope.mappedKeyValue.nameField]};
                    for(var i = 0; $scope.otherFields!=null && i < $scope.otherFields.length; i++){
                        $scope.tempCheckedOrg[$scope.otherFields[i]] = org[$scope.otherFields[i]];
                    }
                }
            };

            $scope.cancelSelected = function(){
                $scope.initData();
            }
            $scope.saveSelected = function(){
                var cus = $scope.tempCheckedOrg;
                if(cus.VALUE==null||cus.VALUE==""){
                    delete $scope.checkedOrg[$scope.mappedKeyValue.nameField];
                    delete $scope.checkedOrg[$scope.mappedKeyValue.valueField];
                    for(var i = 0; $scope.otherFields!=null && i < $scope.otherFields.length; i++){
                        delete $scope.checkedOrg[$scope.otherFields[i]];
                    }
                }else{
                    $scope.checkedOrg[$scope.mappedKeyValue.nameField] = cus.NAME;
                    $scope.checkedOrg[$scope.mappedKeyValue.valueField] = cus.VALUE;
                    for(var i = 0; $scope.otherFields!=null && i < $scope.otherFields.length; i++){
                        $scope.checkedOrg[$scope.otherFields[i]] = cus[$scope.otherFields[i]];
                    }
                }
                if($scope.callback != null){
                    $scope.callback($scope.checkedOrg);
                }
            }
            $scope.$watch('checkedOrg', $scope.initData);
        }
    };
});
// 评审负责人单选框
ctmApp.directive('directFzrSingleSelect', function() {
    return {
        restrict: 'E',
        templateUrl: 'page/sys/directive/directFzrSingleSelect.html',
        replace: true,
        scope:{
            //必填,该指令所在modal的id，在当前页面唯一
            id: "@",
            //对话框的标题，如果没设置，默认为“人员选择”
            title: "@",
            //必填，查询用户的url
            url: "@",
            //是否可编辑
            isEditable:"=?bind",
            //是否分组
            isGroup:"@",
            //是否可以多选，'true':可以多选，'false':不可以多选，默认为'false'
            isMultiSelect:"@",
            //查询参数
            queryParams: "=",
            //默认选中的用户,数组类型，{NAME:'张三',VALUE:'user.uuid'}
            checkedUser: "=",
            //映射的key，value，{nameField:'username',valueField:'uuid'}，
            //默认为{nameField:'NAME',valueField:'VALUE'}
            mappedKeyValue: "=?bind",
            callback: "=",
            //字符串，'true','false',是否默认选中全部，默认为'false'
            isCheckedAll: "@"
        },
        controller:function($scope,$http,$element){
            if($scope.mappedKeyValue == null){
                $scope.mappedKeyValue = {nameField:'NAME',valueField:'VALUE'};
            }
            $scope.initDefaultData = function(){
                if($scope.title==null){
                    $scope.title = "人员选择";
                }
                if($scope.isGroup==null){
                    $scope.isGroup = "false";
                }
                if($scope.isEditable==null|| ($scope.isEditable!="true" && $scope.isEditable!="false")){
                    $scope.isEditable = "true";
                }
                if($scope.isMultiSelect==null|| ($scope.isMultiSelect!="true" && $scope.isMultiSelect!="false")){
                    $scope.isMultiSelect = "false";
                }
                if($scope.checkedUser == null && $scope.isMultiSelect == "false"){
                    $scope.checkedUser = {};
                }else if($scope.checkedUser == null && $scope.isMultiSelect == "true"){
                    $scope.checkedUser = [];
                }else if($scope.checkedUser != null && $.isArray($scope.checkedUser)){
                    //$scope.checkedUser = [];
                    $scope.isMultiSelect == "true";
                }else if($scope.checkedUser != null && !$.isArray($scope.checkedUser)){
                    $scope.checkedUser = {};
                    $scope.isMultiSelect == "false";
                }
                if($scope.isCheckedAll==null|| ($scope.isCheckedAll!="true" && $scope.isCheckedAll!="false")){
                    $scope.isCheckedAll = "false";
                }
            };
            $scope.removeSelectedUser = function(user){
                if($scope.isMultiSelect == "false"){
                    $scope.checkedUser = {};
                }else if($scope.isMultiSelect == "true"){
                    for(var i = 0; i < $scope.checkedUser.length; i++){
                        if(user[$scope.mappedKeyValue.valueField] == $scope.checkedUser[i][$scope.mappedKeyValue.valueField]){
                            $scope.checkedUser.splice(i, 1);
                            break;
                        }
                    }
                }
            };
            $scope.initDefaultData();
        }
    };
});
ctmApp.directive('directFzrSingleDialog', function() {
    return {
        restrict: 'E',
        templateUrl: 'page/sys/directive/directFzrSingleDialog.html',
        replace: true,
        scope:{
            //必填,该指令所在modal的id，在当前页面唯一
            id: "@",
            //对话框的标题，如果没设置，默认为“人员选择”
            title: "@",
            url: "@",
            //查询参数
            queryParams: "=",
            //默认选中的用户,数组类型，{NAME:'张三',VALUE:'user.uuid'}
            checkedUser: "=",
            //映射的key，value，{nameField:'username',valueField:'uuid'}，
            //默认为{nameField:'NAME',valueField:'VALUE'}
            mappedKeyValue: "=",
            callback: "=",
            //是否分组
            isGroup:"@",
            //是否可以多选，'true':可以多选，'false':不可以多选，默认为'false'
            isMultiSelect:"@",
            //字符串，'true','false',是否默认选中全部，默认为'false'
            isCheckedAll: "@"
        },
        controller:function($scope,$http,$element){
            $scope.initData = function(){
                if(null == $scope.checkedUser){
                    return;
                }
                var cus = $.parseJSON(JSON.stringify($scope.checkedUser));
                if($scope.isMultiSelect == "false"){
                    $scope.tempCheckedUser = {};
                    $scope.tempCheckedUser.NAME = cus[$scope.mappedKeyValue.nameField];
                    $scope.tempCheckedUser.VALUE = cus[$scope.mappedKeyValue.valueField];
                }else if($scope.isMultiSelect == "true"){
                    $scope.tempCheckedUser = [];
                    for(var i = 0; i < cus.length; i++){
                        var tmpUser = {};
                        tmpUser.NAME = cus[i][$scope.mappedKeyValue.nameField];
                        tmpUser.VALUE = cus[i][$scope.mappedKeyValue.valueField];
                        $scope.tempCheckedUser.push(tmpUser);
                    }
                }
                $scope.queryUser();
            }
            $scope.queryUser = function(){
                var config = {
                    method:'post',
                    url:srvUrl + $scope.url
                };
                if($scope.queryParams != null){
                    config.data = $.param($scope.queryParams)
                }
                $http(config).success(function(data){
                    if(data.success){
                        $scope.users = data.result_data;
                        if($scope.isCheckedAll == "true" && $scope.users != null){
                            for(var k = 0; k < $scope.users.length; k++){
                                //是否已经存在
                                var flag = false;
                                for(var i = 0; i < $scope.tempCheckedUser.length; i++){
                                    if($scope.users[k][$scope.mappedKeyValue.valueField] == $scope.tempCheckedUser[i].VALUE){
                                        flag = true;
                                    }
                                }
                                if(!flag){
                                    //如果已经选中，但是不存在，添加
                                    $scope.tempCheckedUser.push({"VALUE":$scope.users[k][$scope.mappedKeyValue.valueField],"NAME":$scope.users[k][$scope.mappedKeyValue.nameField]});
                                }
                            }
                        }
                        $scope._setDefaultUser();
                    }else{
                        $.alert(data.result_name);
                    }
                });
            }
            // 设定默认选中的人相关方法开始 add by LiPan 2019-04-29
            $scope._jsonIsEmpty = function(_json){
                var _length = 0;
                for (var i in _json){
                    _length ++;
                }
                return _length == 0;
            };
            $scope._setDefaultUser = function(){
                var _users = [];
                for(var _i = 0; _i < $scope.users.length; _i ++){
                    var _user = $scope.users[_i];
                    if(_user.ISGROUP == 0){
                        _users.push(_user);
                    }
                }
                _users.sort(function(o1_, o2_){
                    return o1_.ORDERNUM - o2_.ORDERNUM;
                });
                _users.sort(function(o1_, o2_){
                    return o1_.CPN_ - o2_.CPN_;
                });
                if($scope.isMultiSelect == "false"){
                    if($scope._jsonIsEmpty($scope.tempCheckedUser) || $scope._jsonIsEmpty($scope.checkedUser)){
                        $scope.tempCheckedUser = {'VALUE':_users[0].VALUE,'NAME':_users[0].NAME};
                    }
                }else if($scope.isMultiSelect == "true"){
                    if($scope.tempCheckedUser.length < 0 || $scope.checkedUser.length < 0){
                        $scope.tempCheckedUser.push({'VALUE':_users[0].VALUE,'NAME':_users[0].NAME});
                    }
                }
            };
            // 设定默认选中的人相关方法结束 add by LiPan 2019-04-29
            $scope.removeSelectedUser = function(user){
                if($scope.isMultiSelect == "false"){
                    $scope.tempCheckedUser = {};
                }else if($scope.isMultiSelect == "true"){
                    for(var i = 0; i < $scope.tempCheckedUser.length; i++){
                        if(user[$scope.mappedKeyValue.valueField] == $scope.tempCheckedUser[i].VALUE){
                            $scope.tempCheckedUser.splice(i, 1);
                            break;
                        }
                    }
                }
            };
            $scope.isChecked = function(user){
                if($scope.isMultiSelect == "false"){
                    if($scope.tempCheckedUser != null && $scope.tempCheckedUser.VALUE != null && user[$scope.mappedKeyValue.valueField] == $scope.tempCheckedUser.VALUE){
                        return true;
                    }
                    return false;
                }else if($scope.isMultiSelect == "true"){
                    for(var i = 0; i < $scope.tempCheckedUser.length; i++){
                        if(user[$scope.mappedKeyValue.valueField] == $scope.tempCheckedUser[i].VALUE){
                            return true;
                        }
                    }
                    return false;
                }
            };
            $scope.toggleChecked = function(user){
                //是否选中
                var isChecked = $("#chk_"+$scope.id+"_"+user[$scope.mappedKeyValue.valueField]).prop("checked");
                //是否已经存在
                var flag = false;
                if($scope.isMultiSelect == "false"){
                    if($scope.tempCheckedUser != null && $scope.tempCheckedUser.VALUE != null && user[$scope.mappedKeyValue.valueField] == $scope.tempCheckedUser.VALUE){
                        flag = true;
                        if(!isChecked){
                            $scope.tempCheckedUser = {};
                        }
                    }
                    if(isChecked && !flag){
                        //如果已经选中，但是不存在，添加
                        $scope.tempCheckedUser = {"VALUE":user[$scope.mappedKeyValue.valueField],"NAME":user[$scope.mappedKeyValue.nameField]};
                    }
                }else if($scope.isMultiSelect == "true"){
                    for(var i = 0; i < $scope.tempCheckedUser.length; i++){
                        if(user[$scope.mappedKeyValue.valueField] == $scope.tempCheckedUser[i].VALUE){
                            flag = true;
                            if(!isChecked){
                                $scope.tempCheckedUser.splice(i, 1);
                                break;
                            }
                        }
                    }
                    if(isChecked && !flag){
                        //如果已经选中，但是不存在，添加
                        $scope.tempCheckedUser.push({"VALUE":user[$scope.mappedKeyValue.valueField],"NAME":user[$scope.mappedKeyValue.nameField]});
                    }
                }
            };

            $scope.cancelSelected = function(){
                $scope.initData();
            }
            $scope.saveSelected = function(){
                var cus = $scope.tempCheckedUser;
                if($scope.isMultiSelect == "false"){
                    if(cus.VALUE==null||cus.VALUE==""){
                        delete $scope.checkedUser[$scope.mappedKeyValue.nameField];
                        delete $scope.checkedUser[$scope.mappedKeyValue.valueField];
                    }else{
                        $scope.checkedUser[$scope.mappedKeyValue.nameField] = cus.NAME;
                        $scope.checkedUser[$scope.mappedKeyValue.valueField] = cus.VALUE;
                    }
                }else if($scope.isMultiSelect == "true"){
                    var cus = $scope.tempCheckedUser;
                    $scope.checkedUser.splice(0,$scope.checkedUser.length)
                    for(var i = 0; i < cus.length; i++){
                        var user = {};
                        user[$scope.mappedKeyValue.nameField] = cus[i].NAME;
                        user[$scope.mappedKeyValue.valueField] = cus[i].VALUE;
                        $scope.checkedUser.push(user);
                    }
                }
                if($scope.callback != null){
                    $scope.callback($scope.checkedUser);
                }
            }
            $scope.$watch('checkedUser + queryParams', $scope.initData, true);
        }
    };
});
// 带组织的人员单选框
ctmApp.directive('directiveUserList', function() {
    return {
        restrict: 'E',
        templateUrl: 'page/sys/directive/directiveUserList.html',
        replace: true,
        scope:{},
        controller:function($scope,$http,$element){
            //获取父作用域
            var carouselScope = $element.parent().scope();
            $scope.selected = [];
            $scope.selectedTags = [];
            $scope.selectedNameValue = [];
            $("#arrUserName").val("");
            var updateSelected = function(action,id,name){
                if(action == 'add' && $scope.selected.indexOf(id) == -1){
                    var objs={name:name,value:id};    /*封装成Object对象*/
                    $scope.selected.push(id);
                    $scope.selectedTags.push(name);
                    $scope.selectedNameValue.push(objs);
                }
                if(action == 'remove' && $scope.selected.indexOf(id)!=-1){
                    var objs={name:name,value:id};
                    var idx = $scope.selected.indexOf(id);
                    $scope.selected.splice(idx,1);
                    $scope.selectedTags.splice(idx,1);
                    $scope.selectedNameValue.splice(objs,1);
                }
                var  arrName=$scope.selectedTags;
                var arrNameToString=arrName.join(",");                 //将数组 [] 转为 String
                $("#arrUserName").val(arrNameToString);
                var arrIDD= $scope.selected;
                arrIDD=arrIDD.join(",");

                $("#selectedName").find(".select2-choices").html("<li class=\"select2-search-field\"><input id=\"s2id_autogen2\" class=\"select2-input\" type=\"text\" spellcheck=\"false\" autocapitalize=\"off\" autocorrect=\"off\" autocomplete=\"off\" style=\"width: 16px;\"> </li>");
                var selectedName = [];
                var selectedId=[];
                selectedName =arrNameToString.split(",");
                selectedId = arrIDD.split(",");
                var leftstr="<li class=\"select2-search-choice\"><div>";
                var centerstr="</div><a class=\"select2-search-choice-close\" tabindex=\"-1\" onclick=\"delDom(this)\" href=\"javascript:delCommonname('";
                var addID="');\"></a><input type=\"hidden\" id=\"\"  value=\"";
                var rightstr="\"></li>";
                for(var i=0;i<selectedName.length;i++){
                    $("#selectedName").find(".select2-search-field").before(leftstr+selectedName[i]+centerstr+selectedId[i]+addID+selectedId[i]+rightstr);
                }
            }

            $scope.updateSelection = function($event, id){
                var checkbox = $event.target;
                var action = (checkbox.checked?'add':'remove');
                updateSelected(action,id,checkbox.name);
            }

            $scope.isSelected = function(id){
                return $scope.selected.indexOf(id)>=0;
            }
            $scope.paginationConf = {
                currentPage: 1,
                itemsPerPage: 10,
                queryObj:{},
                perPageOptions: [10]
            };
            $scope.queryUserList = function(){
                var cp = $scope.paginationConf.currentPage;
                if(cp == 1){
                    $scope.queryUser();
                }else{
                    $scope.paginationConf.currentPage = 1;
                }
            }
            $scope.queryUser=function(){
                $scope.paginationConf.queryObj = $scope.queryObj;
                var  url = 'fnd/user/getAll';
                $scope.$parent.httpData(url,$scope.paginationConf).success(function(data){
                    // 变更分页的总数
                    if(data.result_code == "S") {
                        $scope.sysUserList = data.result_data.list;
                        $scope.paginationConf.totalItems = data.result_data.totalItems;
                    }
                });
            };
            $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage + queryObj.ORGID', $scope.queryUser);
            //获取组织结构角色
            var ztree, setting = {
                callback:{
                    onClick:function(event, treeId, treeNode){
                        accessScope("#ORGID",function(scope){
                            scope.queryObj = {};
                            scope.queryObj.ORGID = treeNode.id;
                            scope.queryObj.categoryCode = treeNode.cat;
                        });
                    },
                    beforeExpand:function(treeId, treeNode){
                        if(typeof(treeNode.children)=='undefined'){
                            $scope.addTreeNode(treeNode);
                        }
                    }
                }
            };
            $scope.addTreeNode = function (parentNode){
                var pid = '';
                if(parentNode && parentNode.id) pid = parentNode.id;
                $scope.$parent.httpData('fnd/Group/getOrg', {parentId:pid}).success(function(data){
                    if (!data || data.result_code != 'S') return null;
                    var nodeArray = data.result_data;
                    if(nodeArray<1) return null;
                    for(var i=0;i<nodeArray.length;i++){
                        curNode = nodeArray[i];
                        var iconUrl = 'assets/javascripts/zTree/css/zTreeStyle/img/department.png';
                        if(curNode.cat && curNode.cat=='Org'){
                            iconUrl = 'assets/javascripts/zTree/css/zTreeStyle/img/org.png';
                        }
                        curNode.icon = iconUrl;
                    }
                    if(pid == ''){//当前加载的是根节点
                        ztree.addNodes(null, nodeArray);
                        var rootNode = ztree.getNodes()[0];
                        $scope.addTreeNode(rootNode);
                        rootNode.open = true;
                        ztree.refresh();
                    }else{
                        ztree.addNodes(parentNode, nodeArray, true);
                    }
                });
            }

            $scope.resetUserList=function(){
                $scope.selected = [];
                $scope.selectedTags = [];
                $scope.selectedNameValue = [];
                $("#arrUserName").val("");
                var d=" <div class=\"select2-success\">";
                d+="<div id=\"s2id_projectModel\" class=\"select2-container select2-container-multi form-control ng-untouched ng-valid ng-dirty ng-valid-parse\">";
                d+="<ul class=\"select2-choices\"> <li class=\"select2-search-field\">";
                d+="<input id=\"s2id_autogen2\" class=\"select2-input\" type=\"text\" spellcheck=\"false\" autocapitalize=\"off\" autocorrect=\"off\" autocomplete=\"off\" style=\"width: 16px;\">";
                d+=" </li></ul></div></div>";
                $("#selectedName").html(d);
                $scope.queryObj = {};
                $scope.queryObj.ORGID = null;
                $scope.queryObj.categoryCode = null;
            }
            $scope.saveUserListforDiretive=function(){
                var  arrUserIDs=$scope.selected;
                var arrUserNames= $scope.selectedTags;
                var arrUserNamesValue= $scope.selectedNameValue;
                carouselScope.setDirectiveUserList(arrUserIDs,arrUserNames,arrUserNamesValue);
                $scope.selected = [];
                $scope.selectedTags = [];
                $scope.selectedNameValue = [];
                $("#arrUserName").val("");
                var d=" <div class=\"select2-success\">";
                d+="<div id=\"s2id_projectModel\" class=\"select2-container select2-container-multi form-control ng-untouched ng-valid ng-dirty ng-valid-parse\">";
                d+="<ul class=\"select2-choices\"> <li class=\"select2-search-field\">";
                d+="<input id=\"s2id_autogen2\" class=\"select2-input\" type=\"text\" spellcheck=\"false\" autocapitalize=\"off\" autocorrect=\"off\" autocomplete=\"off\" style=\"width: 16px;\">";
                d+=" </li></ul></div></div>";
                $("#selectedName").html(d);
                $scope.queryObj = {};
                $scope.queryObj.ORGID = null;
                $scope.queryObj.categoryCode = null;
            }

            angular.element(document).ready(function() {
                ztree = $.fn.zTree.init($("#treeID5"), setting);
                $scope.addTreeNode('');

                $scope.selected = [];
                $scope.selectedTags = [];
                $scope.selectedNameValue = [];
                $("#arrUserName").val("");
                $scope.queryObj = {};
                $scope.queryObj.ORGID = null;
                $scope.queryObj.categoryCode = null;
            });
        }
    };
});
function  delCommonname(id){
    accessScope("#ORGID", function(scope){
        if(scope.selected.indexOf(id)!=-1){
            var idx = scope.selected.indexOf(id);
            scope.selected.splice(idx,1);
            scope.selectedTags.splice(idx,1);
            scope.selectedNameValue.splice(idx,1);
        }
    });
}
function delDom(dom){
    $(dom).parent("li").remove();
}
ctmApp.directive('directiveUserRadioList', function() {
    return {
        restrict: 'E',
        templateUrl: 'page/sys/directive/DirectiveUserRadioList.html',
        replace: true,
        scope:{},
        controller:function($scope,$http,$element){
            //获取父作用域
            var carouselUserScope = $element.parent().scope();
            $scope.selectUserCode =null;
            $scope.selectUserName = null;
            $scope.setSelection = function(code,name){
                $scope.selectUserCode=code;
                $scope.selectUserName=name;
            }
            $scope.paginationConfes = {
                currentPage: 1,
                queryObj:{},
                itemsPerPage: 10,
                perPageOptions: [10]
            };
            $scope.queryuserradioList = function(){
                var cp = $scope.paginationConfes.currentPage;
                if(cp == 1){
                    $scope.queryuserradio();
                }else{
                    $scope.paginationConfes.currentPage = 1;
                }
            }
            $scope.queryuserradio=function(){
                $scope.paginationConfes.queryObj = $scope.queryObj;

                $http({
                    method:'post',
                    url: srvUrl + "user/getDirectiveUserAll.do",
                    data:$.param({"page":JSON.stringify($scope.paginationConfes)})
                }).success(function(data){
                    // 变更分页的总数
                    if(data.success) {
                        $scope.sysUserradio = data.result_data.list;
                        $scope.paginationConfes.totalItems = data.result_data.totalItems;
                    }else{
                        $.alert(data.result_name);
                    }
                });
            };

            $scope.$watch('paginationConfes.currentPage + paginationConfes.itemsPerPage + queryObj.ORGIDRADIO', $scope.queryuserradio);
            //获取组织结构角色
            var ztree3, setting3 = {
                callback:{
                    onClick:function(event, treeId, treeNode){
                        accessScope("#ORGIDRADIO",function(scope){
                            scope.queryObj = {};
                            scope.queryObj.ORGIDRADIO = treeNode.id;
                            scope.queryObj.categoryCode = treeNode.cat;
                        });
                    },
                    beforeExpand:function(treeId, treeNode){
                        if(typeof(treeNode.children)=='undefined'){
                            $scope.addTreeNode3(treeNode);
                        }
                    }
                }
            };
            $scope.addTreeNode3 = function (parentNode){
                var pid = '';
                if(parentNode && parentNode.id) pid = parentNode.id;
                $scope.$parent.httpData('fnd/Group/getOrg', {parentId:pid}).success(function(data){
                    if (!data || data.result_code != 'S') return null;
                    var nodeArray = data.result_data;
                    if(nodeArray<1) return null;
                    for(var i=0;i<nodeArray.length;i++){
                        curNode = nodeArray[i];
                        var iconUrl = 'assets/javascripts/zTree/css/zTreeStyle/img/department.png';
                        if(curNode.cat && curNode.cat=='Org'){
                            iconUrl = 'assets/javascripts/zTree/css/zTreeStyle/img/org.png';
                        }
                        curNode.icon = iconUrl;
                    }
                    if(pid == ''){//当前加载的是根节点
                        ztree3.addNodes(null, nodeArray);
                        var rootNode = ztree3.getNodes()[0];
                        $scope.addTreeNode3(rootNode);
                        rootNode.open = true;
                        ztree3.refresh();
                    }else{
                        ztree3.addNodes(parentNode, nodeArray, true);
                    }
                });
            }

            $scope.resetRadioUserList=function(){
                $scope.selectUserCode =null;
                $scope.selectUserName = null
                $("input[name='RaidoNAME']").removeAttr("checked");

                $scope.queryObj = {};
                $scope.queryObj.ORGIDRADIO = null;
                $scope.queryObj.categoryCode = null;

            }
            $scope.saveUserRadioListforDiretive=function(){
                carouselUserScope.setDirectiveRadioUserList($scope.selectUserCode,$scope.selectUserName);
                $scope.selectUserCode =null;
                $scope.selectUserName = null;
                $("input[name='RaidoNAME']").removeAttr("checked");
                $scope.queryObj = {};
                $scope.queryObj.ORGIDRADIO = null;
                $scope.queryObj.categoryCode = null;
            }
            angular.element(document).ready(function() {
                ztree3 = $.fn.zTree.init($("#treeIDuser5"), setting3);
                $scope.addTreeNode3('');
                $scope.selectUserCode =null;
                $scope.selectUserName = null;
                $scope.queryObj = {};
                $scope.queryObj.ORGIDRADIO = null;
                $scope.queryObj.categoryCode = null;
            });
        }
    };
});
// 项目列表弹出框
ctmApp.directive('directiveCompanyList', function() {
    return {
        restrict: 'E',
        templateUrl: 'page/sys/directive/DirectiveCompanyList.html',
        replace: true,
        scope:{},
        controller:function($scope,$http,$element){
            //获取父作用域
            var carouselScope = $element.parent().scope();
            $scope.selectProjejct =null;
            $scope.getSelection = function(selectProjejct){
                $scope.selectProjejct = angular.copy(selectProjejct);
            }
            $scope.paginationConf = {
                currentPage: 1,
                itemsPerPage: 10,
                queryObj:{},
                perPageOptions: [10]
            };
            $scope.queryCompanyList = function(){
                var cp = $scope.paginationConf.currentPage;
                if(cp == 1){
                    $scope.queryCompany();
                }else{
                    $scope.paginationConf.currentPage = 1;
                }
            }
            $scope.queryCompany=function(){
                if (!isEmpty($scope.queryObj)){
                    $scope.paginationConf.queryObj = $scope.queryObj;
                }
                $scope.paginationConf.queryObj.USERID = $scope.$parent.credentials.UUID;
                var  url = 'common/commonMethod/getDirectiveCompanyList';
                $scope.$parent.httpData(url,$scope.paginationConf).success(function(data){
                    // 变更分页的总数
                    if(data.result_code == "S") {
                        $scope.sysCompany = data.result_data.list;
                        $scope.paginationConf.totalItems = data.result_data.totalItems;
                    }
                });
            };

            $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.queryCompany);
            $scope.resetCompanyList=function(){
                $scope.selectProjejct =null;
            }
            $scope.saveCompanyListforDiretive=function(){
                var ORGCODE = $scope.selectProjejct.ORGCODE;
                var  url = 'common/commonMethod/gePertainArea';
                $scope.$parent.httpData(url, ORGCODE).success(function(data){
                    // 变更分页的总数
                    if(data.result_code == "S") {
                        if(!isEmpty(data.result_data)){
                            var org = data.result_data[0];
                            $scope.selectProjejct.ORGCODE = org.ORGPKVALUE;
                            $scope.selectProjejct.ORGNAME = org.NAME;
                            $scope.selectProjejct.ORGHEADERNAME = org.ORGHEADERNAME;
                            $scope.selectProjejct.ORGHEADERID = org.ORGHEADERID;
                        }
                    }
                    carouselScope.setDirectiveCompanyList($scope.selectProjejct);
                    $scope.selectProjejct =null;
                });
            }
            angular.element(document).ready(function() {
                $scope.selectProjejct =null;
            });
        }
    };
});

// 会议纪要新增弹出框
ctmApp.directive('mettingSummaryBpmnPopWin', function(){
    return {
        restrict:'AE',
        templateUrl:'page/sys/directive/mettingSummaryBpmnPopWin.html',
        replace:'true',
        scope:{businessId:'='},
        controller:function($scope,$location,$http){
            $scope.mettingSummary = "";
            $scope.submit = function(){
                if($scope.mettingSummary==null || $scope.mettingSummary==""){
                    $.alert("会议纪要不得为空！");
                    return false;
                }
                //show_Mask();
                //保存附件到mongo
                $http({
                    method:'post',
                    url: srvUrl + "bulletinInfo/saveMettingSummary.do",
                    data: $.param({
                        "businessId": $scope.businessId,
                        "mettingSummaryInfo": $scope.mettingSummary
                    })
                }).success(function(result){
                    $('#submitModal').modal('hide');
                    $.alert(result.result_name);
                    $scope.$parent.initDefaultData();
                    $scope.mettingSummary = "";
                });
            };
            $scope.cancel = function(){
                $scope.mettingSummary="";
            }
        }
    }
});

// 上传列表
ctmApp.directive('commonAttachments', function () {
    return {
        restrict: 'AE',
        templateUrl: 'page/sys/directive/commonAttachments.html',
        replace: true,
        scope: {
            id: "@",// 组件ID,确保唯一性
            docType: "@",// 业务类型
            docCode: "=",// 业务单据编号或者UUID
            pageLocation: "@",// 组件在页面的位置,保证唯一性,可以与组件ID保持及一致
            showUpload:"@",// 是否展示浏览按钮
            showReview:"@",// 是否展示预览按钮
            showDownload:"@",// 是否展示下载按钮
            showDelete:"@"// 是否展示删除按钮
        },
        link: function (scope, element, attr) {
        },
        controller: function ($scope, Upload) {
            // 初始化
            $scope._init = function () {
                console.log($scope.docCode);
                $scope._files = attach_list($scope.docType, $scope.docCode, $scope.pageLocation).result_data;
            };
            $scope._init();
            // 新增
            $scope._addAttachment = function () {
                function _addBlankRow(_array) {
                    var blankRow = {
                        _file_content: ''
                    };
                    var size = 0;
                    for (var idx in _array) {
                        console.log(idx);
                        size++;
                    }
                    _array[size] = blankRow;
                }

                if (undefined == $scope._files) {
                    $scope._files = [];
                }
                _addBlankRow($scope._files);
            };

            // 移除
            $scope._removeAttachment = function () {
                var _all_files = $scope._files;
                if (_all_files != null) {
                    for (var i = 0; i < _all_files.length; i++) {
                        if (_all_files[i].selected) {
                            if (_all_files[i].fileid) {
                                attach_delete(_all_files[i].fileid);
                            }
                            _all_files.splice(i, 1);
                            i--;
                        }
                    }
                    $scope._init();
                }
            };

            // 上传
            $scope._uploadThat = function (_file, _idx) {
                Upload.upload({
                    url: srvUrl + 'cloud/upload.do',
                    data: {
                        file: _file,
                        "docType": $scope.docType,
                        'docCode': $scope.docCode,
                        'pageLocation': $scope.pageLocation
                    }
                }).then(function (resp) {
                    var retData = resp.data.result_data[0];
                    $scope._files[_idx] = retData;
                }, function (resp) {
                    $.alert(resp.status);
                }, function (evt) {
                    var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                    $scope["_progress_" + _idx] = progressPercentage == 100 ? "" : progressPercentage + "%";
                });
                $scope._init();
            };

            // 预览
            $scope._review = function (uri) {
                window.open(uri, '_blank', 'menubar=no,toolbar=no, status=no,scrollbars=yes');
            };

            // 下载
            $scope._download = function (uri) {
                window.open(uri, '_blank', 'menubar=no,toolbar=no, status=no,scrollbars=yes');
            };

            $scope._delete = function(file_id){
                attach_delete(file_id);
                $scope._init();
            }
        }
    };
});

// BBS 谈话框
ctmApp.directive('bbsChat', function() {
    return {
        restrict: 'E',
        templateUrl: 'page/sys/directive/DirectiveBbsPage.html',
        replace: true,
        link:function(scope,element,attr){
        },
        controller:function($scope,$http,$element){

            $scope.conf = [];

            $scope.queryMessage = function (procInstId, parentId) {
                $http({
                    method: 'post',
                    url: '/rcm-rest/message/tree.do',
                    data: $.param({
                        'procInstId': procInstId,
                        'parentId': parentId
                    }),
                    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
                }).success(function (data) {
                    $scope.messages = data.result_data;
                });
            }

            // 初始化留言表单
            $scope.message = {};
            $scope.queryMessage(1008611, 0);
            $scope.message.originalId = 0;
            $scope.message.parentId = 0;
            $scope.message.procInstId = 1008611;
            $scope.message.repliedBy = '';
            $scope.message.repliedName = '';
            // 展示留言表单
            $scope.showMessageForm = function (originalId, parentId, repliedBy, repliedName) {
                $scope.message.originalId = originalId;
                $scope.message.parentId = parentId;
                $scope.message.procInstId = 1008611;
                $scope.message.repliedBy = repliedBy;
                $scope.message.repliedName = repliedName;
            }
            // 更新已阅
            $scope.updateRead = function () {
                $http({
                    method: 'post',
                    url: '/rcm-rest/message/read.do',
                    data: $.param({'messageId': 10109}),
                    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
                }).success(function (data) {
                    document.write(data.result_data);
                });
            }
            // 获取叶子留言
            $scope.getChildren = function () {
                $http({
                    method: 'post',
                    url: '/rcm-rest/message/leaves.do',
                    data: $.param({'parentId': 10109}),
                    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
                }).success(function (data) {
                    document.write(JSON.stringify(data.result_data));
                });
            }

            // 提交留言表单
            $scope.submitMessage = function () {
                console.log($scope.message);
                if ($scope.message.messageContent == null || $scope.message.messageContent == '') {
                    alert('留言内容不能为空!');
                    return;
                }
                $http({
                    method: 'post',
                    url: '/rcm-rest/message/add.do',
                    data: $.param($scope.message),
                    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
                }).success(function (data) {
                    console.log(data);
                    window.location.reload(true);
                });
            }

            $scope.recursionHtml = function (messageId, list) {
                // 先清空之前加载的
                $('#leaves_li_' + messageId + '>ul').each(function (i, e) {
                    $(e).remove();
                });
                var li = $('#leaves_li_' + messageId);
                var appendStr = '<ul>';
                for (var i = 0; i < list.length; i++) {
                    var o = list[i];
                    console.log(o);
                    appendStr +=
                        '<li class="aaa" id="leaves_li_' + o.messageId + '">'
                        + '<b>|</b>&nbsp;<span class="msg">'
                        + o.createdName
                        + '</span>&nbsp;'
                        + '<span class="blue">'
                        + '回复'
                        + '</span>&nbsp;'
                        + '<span class="msg">'
                        + o.repliedName
                        + '</span>&nbsp;'
                        + '<span class="blue">'
                        + '发表于&nbsp;'
                        + o.messageDate
                        + '</span>'
                        + '&nbsp;'
                        + '<a href="javascript:void(0);" onclick="getChildrenListOuter(' + o.originalId + ',' + o.messageId + ', \'' + o.createdBy + '\',\'' + o.createdName + '\', ' + o.messageId + ')">&nbsp;'
                        + '<span class="content">(' + o.children.length + ')</span></a>'
                        + '<br>'
                        + '<span class="content">'
                        + o.messageContent
                        + '</span>'
                        + '</li>';
                }
                li.append(appendStr + '</<ul>');
            }

            $scope.executeLeavesQuery = function (messageId) {
                $http({
                    method: 'post',
                    url: '/rcm-rest/message/tree.do',
                    data: $.param({
                        'procInstId': 1008611,
                        'parentId': messageId
                    }),
                    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
                }).success(function (data) {
                    $scope.recursionHtml(messageId, data.result_data);
                });
            }

            $scope.getChildrenList = function (originalId, parentId, repliedBy, repliedName, messageId) {
                $scope.message.originalId = originalId;
                $scope.message.parentId = parentId;
                $scope.message.procInstId = 1008611;
                $scope.message.repliedBy = repliedBy;
                $scope.message.repliedName = repliedName;
                $scope.executeLeavesQuery(messageId);
            }

            $scope.deleteMessage = function (messageId) {
                if (confirm('确认删除?')) {
                    $http({
                        method: 'post',
                        url: '/rcm-rest/message/delete.do',
                        data: $.param({
                            'messageId': messageId
                        }),
                        headers: {'Content-Type': 'application/x-www-form-urlencoded'}
                    }).success(function (data) {
                        console.log(data);
                        window.location.reload(true);
                    });
                }
            }

            $scope.showMore = function () {
                alert('more');
            }

            function getChildrenListOuter(originalId, parentId, repliedBy, repliedName, messageId) {
                // 重点!!!!!!!
                var appElement = document.querySelector('[ng-controller=myCtrl]');
                var $scope = angular.element(appElement).scope();
                $scope.message.originalId = originalId;
                $scope.message.parentId = parentId;
                $scope.message.procInstId = 1008611;
                $scope.message.repliedBy = repliedBy;
                $scope.message.repliedName = repliedName;
                console.log(originalId + '=' + parentId + '=' + repliedBy + '=' + repliedName + '=' + messageId)
                $.ajax({
                    url: "/message/tree.do",
                    type: "post",
                    data: {
                        'procInstId': 1008611,
                        'parentId': messageId
                    },
                    async: false,
                    success: function (result) {
                        var list = result.result_data;
                        console.log(messageId);
                        console.log($('#leaves_li_' + messageId + '>ul').html());
                        $('#leaves_li_' + messageId + '>ul').each(function (i, e) {
                            $(e).remove();
                        });
                        var li = $('#leaves_li_' + messageId);
                        var appendStr = '<ul>';
                        for (var i = 0; i < list.length; i++) {
                            var o = list[i];
                            console.log(o);
                            appendStr +=
                                '<li id="leaves_li_' + o.messageId + '">'
                                + '<b>|</b>&nbsp;<span class="msg">'
                                + o.createdName
                                + '</span>&nbsp;'
                                + '<span class="blue">'
                                + '回复'
                                + '</span>&nbsp;'
                                + '<span class="msg">'
                                + o.repliedName
                                + '</span>&nbsp;'
                                + '<span class="blue">'
                                + '发表于&nbsp;'
                                + o.messageDate
                                + '</span>'
                                + '&nbsp;'
                                + '<a href="javascript:void(0);" onclick="getChildrenListOuter(' + o.originalId + ',' + o.messageId + ',\'' + o.createdBy + '\',\'' + o.createdName + '\', ' + o.messageId + ')">&nbsp;'
                                + '<span class="content">(' + o.children.length + ')</span></a>'
                                + '<br>'
                                + '<span class="content">'
                                + o.messageContent
                                + '</span>'
                                + '</li>';
                        }
                        li.append(appendStr + '</<ul>');
                        console.log(appendStr);
                    },
                    error: function () {
                    }
                });
            }

            $scope.replayQuestion = function (msg) {
                console.log(msg);
                $scope.msg = {};
                $scope.msg.messageType = msg.messageType;
                $scope.msg.procInstId = msg.procInstId;
                $scope.msg.parentId = msg.messageId;
                $scope.msg.originalId = msg.originalId;
                $scope.msg.repliedBy = msg.createdBy;
                $scope.msg.repliedName = msg.createdName;
                $scope.msg.messageContent = msg.replay;
                $http({
                    method: 'post',
                    url: '/rcm-rest/message/add.do',
                    data: $.param($scope.msg),
                    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
                }).success(function (data) {
                    console.log(data);
                    window.location.reload(true);
                });
                console.log($scope.msg);
            }
        }
    };
});
ctmApp.directive('bbsChatNew', function() {
    return {
        restrict: 'E',
        templateUrl: 'page/sys/directive/DirectiveBbsPageNew.html',
        replace: true,
        scope:{
            id: "@",// 组件ID
            viaUserQueryUrl:"@",// 查询访问的url
            messageType:"@",// 消息类型
            businessId:"@",// 业务ID
            initMessagesArray:"=",// 初始化
            initUuid:"=",// 当前登录用户
            isPagination:'@',//是否开启分页，默认为false
            isAlertUser:'@',//是否弹出用户信息，默认为false
            isShowShare:'@',//是否显示分享，默认为false
            isShowDt:'@',// 显示分享到钉钉，默认为false
            isShowWx:'@',// 显示分享到微信，默认为false
            isShowEmail:'@',//显示分享到邮箱，默认为false
            isShowSms:'@',// 显示分享到短信，默认为false
            isShowPublishBtn:'@',// 是否显示发表留言按钮，默认为true
            isShowReplyBtn:'@',// 是否显示回复按钮，默认为true
            isShowPriority:'@',// 是否展示优先级设置，默认false
            priorityType:'@',// 优先级展示方式：select或者radio方式，默认为radio
            priorityDescription:'@',// 优先级别文字描述,如：特急,一般,紧急
        },
        link:function(scope, element, attr){
        },
        controller:function($scope, $http, Upload, $window){
            // 定义查询参数
            var _query_params_ = {};
            if(isEmpty($scope.isShowPriority)){
                $scope._is_show_priority_ = false;
            }else{
                $scope._is_show_priority_ = $scope.isShowPriority == 'true';
            }
            if(isEmpty($scope.priorityType)){
                $scope._priority_type_ = 'radio';
            }else{
                if($scope.priorityType == 'select'){
                    $scope._priority_type_ = 'select';
                }else{
                    $scope._priority_type_ = 'radio';
                }
            }
            // 初始化留言优先级
            var _priorities_desc_ = [];
            if(isEmpty($scope.priorityDescription)){
                _priorities_desc_ = ['一般','紧急','特急'];
            }else{
                _priorities_desc_ = $scope.priorityDescription.split(",");
            }
            $scope._message_priorities_ = [];
            for(var _ix = 0; _ix < _priorities_desc_.length; _ix ++){
                var _js = {};
                _js.key = _ix;
                _js.value = _priorities_desc_[_ix];
                $scope._message_priorities_.push(_js);
            }
            $scope._message_type_ = $scope.messageType;
            $scope._message_business_id_ = $scope.businessId;
            if(isEmpty($scope.viaUserQueryUrl)){
                $scope._via_user_query_url_ = 'message/queryViaUsers.do?message_business_id=' + $scope.businessId + '&message_type=' + $scope.messageType;
            }
            // 初始化是否分页、是否弹出用户设置、是否显示分享按钮等
            if(isEmpty($scope.isPagination)){
                $scope._is_pagination_ = false;
            }else{
                $scope._is_pagination_ = $scope.isPagination == 'true';
            }
            if(isEmpty($scope.isAlertUser)){
                $scope._is_alert_user_ = false;
            }else{
                $scope._is_alert_user_ = $scope.isAlertUser == 'true';
            }
            if(isEmpty($scope.isShowPublishBtn)){
                $scope._is_show_publish_btn_ = true;
            }else{
                $scope._is_show_publish_btn_ = $scope.isShowPublishBtn == 'true';
            }
            if(isEmpty($scope.isShowReplyBtn)){
                $scope._is_show_reply_btn_ = true;
            }else{
                $scope._is_show_reply_btn_ = $scope.isShowReplyBtn == 'true';
            }
            if(isEmpty($scope.isShowShare)){
                $scope._is_show_share_ = false;
            }else{
                $scope._is_show_share_ = $scope.isShowShare == 'true';
            }
            if(isEmpty($scope.isShowDt)){
                $scope._is_show_dt_ = false;
            }else{
                $scope._is_show_dt_ = $scope.isShowDt == 'true';
            }
            if(isEmpty($scope.isShowWx)){
                $scope._is_show_wx_ = false;
            }else{
                $scope._is_show_wx_ = $scope.isShowWx == 'true';
            }
            if(isEmpty($scope.isShowSms)){
                $scope._is_show_sms_ = false;
            }else{
                $scope._is_show_sms_ = $scope.isShowSms == 'true';
            }
            if(isEmpty($scope.isShowEmail)){
                $scope._is_show_email_ = false;
            }else{
                $scope._is_show_email_ = $scope.isShowEmail == 'true';
            }
            // 初始化留言表单内容
            $scope._message = {};
            $scope._message.originalId = 0;
            $scope._message.messageTitle = '';
            $scope._message.parentId = 0;
            $scope._message.procInstId = $scope.businessId;
            $scope._message.repliedBy = '';
            $scope._message.repliedName = '';
            $scope._message_first = {};
            $scope._message_first.originalId = 0;
            $scope._message_first.messageTitle = '';
            $scope._message_first.parentId = 0;
            $scope._message_first.messagePriority = 0;
            $scope._message_first.procInstId = $scope.businessId;
            $scope._message_first.repliedBy = '';
            $scope._message_first.repliedName = '';
            // 查询留言信息
            $scope._query_messages_list_ = function (_parent_id_) {
                $http({
                    method: 'post',
                    url: srvUrl + 'message/queryMessagesList.do',
                    data: $.param({
                        'procInstId': $scope.businessId,
                        'parentId': _parent_id_
                    }),
                    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
                }).success(function (data) {
                    $scope._messages_array_ = data;
                });
            };
            // 清空留言表单
            $scope._clear_message_from = function(){
                $scope._message.originalId = '';
                $scope._message.parentId = '';
                $scope._message.repliedBy = '';
                $scope._message.repliedName = '';
                $scope._message.messageContent = '';
                $scope._message.messageTitle = '';
                $scope._message_first.originalId = '';
                $scope._message_first.parentId = '';
                $scope._message_first.repliedBy = '';
                $scope._message_first.repliedName = '';
                $scope._message_first.messageContent = '';
                $scope._message_first.messageTitle = '';
                $scope._message_first.messagePriority = 0;
                $scope._clear_via_users_();
                $scope._clear_share_users_();
            };
            // 保存留言表单信息
            $scope._submit_message_form_ = function (_is_first_,_original_id_, _parent_id_, _replied_by_, _replied_name_, _idx_) {
                var formData = null;
                debugger;
                if(_is_first_ == 'Y'){
                    formData = $scope._message_first;
                    formData.originalId = 0;
                    formData.parentId = 0;
                    formData.repliedBy = '';
                    formData.repliedName = 0;
                    formData.messageContent = $('#_message_first_0').text();
                    formData.messageTitle = $('#_message_first_title_0').text();
                    formData.viaUsers = notify_mergeTempCheckedUsers($scope._via_users_TempCheckedUsers);
                }else{
                    formData = $scope._message;
                    formData.originalId = _original_id_;
                    formData.parentId = _parent_id_;
                    formData.repliedBy = _replied_by_;
                    formData.repliedName = _replied_name_;
                    formData.messageContent = $('#_message_textarea_bottom_' + _idx_).text();
                }
                if (isEmpty(formData.messageTitle)) {
                    if(_is_first_ == 'Y'){
                        $.alert('留言主题不能为空!');
                        return;
                    }
                }
                if (isEmpty(formData.messageContent)) {
                    if(_is_first_ == 'Y'){
                        $.alert('留言内容不能为空!');
                    }else{
                        $.alert('回复内容不能为空!');
                    }
                    return;
                }
                if(_is_first_ == 'Y'){
                    if(_common_get_string_byte_length(formData.messageTitle) > 128){
                        $.alert('标题不能超过128个字符!');
                        return;
                    }
                }
                if(_common_get_string_byte_length(formData.messageContent) > 2500){
                    $.alert('内容不能超过2500个字符!');
                    return;
                }
                formData.messageType = $scope.messageType;
                $http({
                    method: 'post',
                    url: srvUrl + 'message/add.do',
                    data: $.param(formData),
                    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
                }).success(function (data) {
                    if($scope._is_pagination_){
                        $scope._query_messages_list_page_();
                    }else{
                        $scope._query_messages_list_(0);
                    }
                    $scope._clear_message_from();
                    if(_is_first_ == 'Y'){
                        $('#_message_first_0').text('');
                        $('#_message_first_title_0').text('');
                        $.alert('发表留言成功!');
                    }else{
                        $.alert('回复留言成功!');
                    }
                });
            };
            // 删除留言信息
            $scope._delete_message_ = function (_message_id_, _message_) {
                var _message_date_ = _message_['messageDate'];
                if(_common_get_ttl(new Date(_message_date_), 3) < 0){
                    $.alert('只能撤回3分钟以内的留言！');
                    return;
                }
                $.confirm('确定撤回该留言吗?', function(){
                    $http({
                        method: 'post',
                        url: srvUrl + 'message/delete.do',
                        data: $.param({
                            'messageId': _message_id_
                        }),
                        headers: {'Content-Type': 'application/x-www-form-urlencoded'}
                    }).success(function (data) {
                        if($scope._is_pagination_){
                            $scope._query_messages_list_page_();
                        }else{
                            $scope._query_messages_list_(0);
                        }
                        if(isEmpty(data)){
                            $.alert('服务器错误！撤回留言失败!');
                        }else{
                            if(data.success){
                                $.alert('撤回留言成功!');
                            }else{
                                $.alert("撤回留言失败!" + data.result_name);
                            }
                        }
                    });
                });
            };
            // 用户的其他操作
            $scope._message_user = {};
            // 弹出用户信息
            $scope._user_about_click_ = function(_source_, _user_id_, _user_name_){
                console.log("查看用户：" + _source_ + " " + _user_id_ + " " + _user_name_);
                if(!$scope._is_alert_user_){
                    return;
                }
                $http({
                    method: 'post',
                    url: srvUrl + 'message/getUserInfo.do',
                    data: $.param({
                        'uuid': _user_id_
                    }),
                    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
                }).success(function (data) {
                    if(!isEmpty(data)){
                        $scope._message_user = data;
                        $('#_message_user_dialog').modal('show');
                    }
                });
            };
            // 展示留言弹窗
            $scope._show_submit_message_form_ = function(_is_first_,_original_id_, _parent_id_, _replied_by_, _replied_name_, _idx_){
                $('#_submit_message_dialog').modal('show');
                $('#_message_textarea_bottom_' + _idx_).text('');
                $scope._message.originalId = _original_id_;
                $scope._message.parentId = _parent_id_;
                $scope._message.repliedBy = _replied_by_;
                $scope._message.repliedName = _replied_name_;
            };
            // 保存留言弹窗内容
            $scope._save_message_dialog_form_ = function(){
                if(isEmpty($('#_message_textarea_bottom_0').text())){
                    $.alert('回复内容不能为空!');
                    return;
                }
                $scope._submit_message_form_('N', $scope._message.originalId, $scope._message.parentId, $scope._message.repliedBy, $scope._message.repliedName, 0);
                $('#_submit_message_dialog').modal('hide');
            };
            // 内容太多时，只展示前10个字。
            $scope._check_collapse_event_ = function(_obj_, _idx_){
                var _class = $('#_message_panel_body_' + _idx_).attr("class");
                var _span = '';
                if("panel-collapse collapse in" == _class){
                    _span += _obj_['createdName'] + '&nbsp;';
                    _span += _obj_['messageDate'] + '&nbsp;';
                    if(!isEmpty(_obj_.messageContent)){
                        if(_obj_.messageContent.length >= 10){
                            _span += _obj_.messageContent.substr(0, 11) + '......';
                        }else{
                            _span += _obj_.messageContent;
                        }
                    }
                }
                $('#_span_hide_content_' + _idx_).html(_span);
            };
            // @功能用户多选初始化
            $scope._via_users_MappedKeyValue = {"nameField": "NAME", "valueField": "VALUE"};
            $scope._via_users_CheckedUsers = [];
            $scope._via_users_TempCheckedUsers = [];
            // 当用户被选择时调用
            $scope._via_users_ParentSaveSelected = function(){
                var _via_users_ExecuteEval = '';
                _via_users_ExecuteEval += '$scope.$parent._via_users_CheckedUsers = $scope.checkedUsers;';
                _via_users_ExecuteEval += '$scope.$parent._via_users_TempCheckedUsers = $scope.tempCheckedUsers;';
                return _via_users_ExecuteEval;
            };
            // 移除选中的用户
            $scope._via_users_removeUsers = function (_user) {
                for (var _i = 0; _i < $scope._via_users_TempCheckedUsers.length; _i++) {
                    if (_user.VALUE == $scope._via_users_TempCheckedUsers[_i].VALUE) {
                        $scope._via_users_TempCheckedUsers.splice(_i, 1);
                        break;
                    }
                }
                $scope._via_users_CheckedUsers = $scope._via_users_TempCheckedUsers;
            };
            // 清空@人
            $scope._clear_via_users_ = function(){
                $scope._via_users_CheckedUsers = [];
                $scope._via_users_TempCheckedUsers = [];
            };
            // 分享功能参数初始化
            $scope._share_message_id_ = '';
            $scope._share_type_ = 'DT';
            // 展示分享弹窗
            $scope._show_share_message_form_ = function(_message_id_){
                $('#_share_message_type_span_').text('');
                $scope._share_type_ = 'DT';
                $('#_share_message_dialog').modal('show');
                $scope._share_message_id_ = _message_id_;
            };
            // 测试方法
            $scope._share_message_result_test_ = function(_url_){
                window.open(_url_, '_blank', 'menubar=no,toolbar=no, status=no,scrollbars=yes');
            };
            // 执行分享操作
            $scope._share_message_dialog_form_ = function(){
                // 获取分享用户
                var _share_users_ = notify_mergeTempCheckedUsers($scope._share_users_TempCheckedUsers);
                if(isEmpty(_share_users_)){
                    $.alert('请选择要分享的用户！');
                    return;
                }
                $http({
                    method: 'post',
                    url: srvUrl + 'message/share.do',
                    data: $.param({
                        'messageId': $scope._share_message_id_,
                        'shareUsers': _share_users_,
                        'type':$scope._share_type_
                    }),
                    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
                }).success(function (data) {
                    if(isEmpty(data)){
                        $.alert('分享留言失败!');
                    }else{
                        if(data['result_code'] == 'S'){
                            $scope._share_message_id_ = '';
                            $scope._clear_share_users_();
                            $('#_share_message_dialog').modal('hide');
                            $.alert(data['result_name']);
                            // $scope._share_message_result_test_(data['result_data']);
                        }else{
                            $.alert(data['result_name']);
                        }
                    }
                });
            };
            // share多选
            $scope._share_users_MappedKeyValue = {"nameField": "NAME", "valueField": "VALUE"};
            $scope._share_users_CheckedUsers = [];
            $scope._share_users_TempCheckedUsers = [];
            $scope._share_users_ParentSaveSelected = function(){
                var _share_users_ExecuteEval = '';
                _share_users_ExecuteEval += '$scope.$parent._share_users_CheckedUsers = $scope.checkedUsers;';
                _share_users_ExecuteEval += '$scope.$parent._share_users_TempCheckedUsers = $scope.tempCheckedUsers;';
                return _share_users_ExecuteEval;
            };
            $scope._share_users_removeUsers = function (_user) {
                for (var _i = 0; _i < $scope._share_users_TempCheckedUsers.length; _i++) {
                    if (_user.VALUE == $scope._share_users_TempCheckedUsers[_i].VALUE) {
                        $scope._share_users_TempCheckedUsers.splice(_i, 1);
                        break;
                    }
                }
                $scope._share_users_CheckedUsers = $scope._share_users_TempCheckedUsers;
            };
            // 清空分享人
            $scope._clear_share_users_ = function(){
                $scope._share_users_CheckedUsers = [];
                $scope._share_users_TempCheckedUsers = [];
            };
            // 页面锚点定位
            $scope._jump_page_to_ = function(_target_ele_id_){
                $("html, body").animate({
                    scrollTop: $("#" + _target_ele_id_).offset().top }, {duration: 500,easing: "swing"});
                return false;
            };
            // 分页查询
            $scope._query_messages_list_page_ = function(){
                $scope._message_pagination_configuration_.queryObj.procInstId = $scope.businessId;
                $scope._message_pagination_configuration_.queryObj.parentId = 0;
                $http({
                    method: 'post',
                    url: srvUrl + "message/queryMessagesListPage.do",
                    data: $.param({"page": JSON.stringify($scope._message_pagination_configuration_)})
                }).success(function(data){
                    $scope._message_pagination_configuration_.totalItems = data['result_data'].totalItems;
                    $scope._messages_array_ = data['result_data'].list;
                    console.log($scope._messages_array_);
                });
            };
            // 分页参数初始化
            $scope._message_pagination_configuration_ = {};
            $scope._message_pagination_configuration_.queryObj = {};
            $scope._message_pagination_configuration_.itemsPerPage = 5;
            $scope._message_pagination_configuration_.perPageOptions = [5, 10, 15, 20, 25, 30];
            $scope.$watch('_message_pagination_configuration_.currentPage + _message_pagination_configuration_.itemsPerPage', $scope._query_messages_list_page_);
            // 查询初始化
            if(!$scope._is_pagination_){
                $scope._query_messages_list_(0);
            }else{
                $scope._query_messages_list_page_();
            }
            if(!isEmpty($scope.initUuid)){
                $scope._init_uuid_global_ = $scope.initUuid;
            }
            if(isEmpty($scope._messages_array_)){
                if(!isEmpty($scope.initMessagesArray)){
                    $scope._messages_array_ = $scope.initMessagesArray;
                }
            }
            $scope._choice_share_type_ = function(_share_type_){
                if(_share_type_ == 'DT'){
                    $('#_share_message_type_span_').text('钉钉');
                }
                if(_share_type_ == 'WX'){
                    $('#_share_message_type_span_').text('微信');
                }
                if(_share_type_ == 'SMS'){
                    $('#_share_message_type_span_').text('短信');
                }
                if(_share_type_ == 'EMAIL'){
                    $('#_share_message_type_span_').text('邮箱');
                }
                if(!isEmpty(_share_type_)){
                    $scope._share_type_ = _share_type_;
                }
            };
            // 留言中的过程附件
            $scope._message_upload_file_ = function(_file_, _message_){
                Upload.upload({
                    url: srvUrl + 'cloud/upload.do',
                    data: {
                        file: _file_,
                        'docType':'sys_message_' + _message_.messageType,
                        'docCode':_message_.procInstId,
                        'pageLocation':_message_.messageId
                    }
                }).then(function (_resp_) {
                    var _result = _resp_.data;
                    if(!isEmpty(_result)){
                        if(_result.success){
                            var _cloud_file_dto_ = _result['result_data'];
                            if(!isEmpty(_cloud_file_dto_)){
                                _message_.messageFile = _cloud_file_dto_.fileid;
                                $scope._message_update_(_message_);
                            }
                        }
                    }
                }, function (_resp_) {
                }, function (_evt_) {
                });
            };
            $scope._message_delete_file_ = function(_message_){
                attach_delete(_message_.messageFile);
                _message_.messageFile = null;
                $scope._message_update_(_message_);
                if($scope._is_pagination_){
                    $scope._query_messages_list_page_();
                }else{
                    $scope._query_messages_list_(0);
                }
            };
            $scope._message_update_ = function(_message_){
                $http({
                    method: 'post',
                    url: srvUrl + 'message/update.do',
                    data: $.param(_message_),
                    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
                }).success(function (data) {
                    if($scope._is_pagination_){
                        $scope._query_messages_list_page_();
                    }else{
                        $scope._query_messages_list_(0);
                    }
                });
            };
            $scope._message_preview_file_ = function(_url_){
                if(!isEmpty(_url_)){
                    $window.open(_url_, '_blank', 'menubar=no,toolbar=no, status=no,scrollbars=yes');
                }
            };
        }
    };
});
ctmApp.directive('uploadFile', function () {
    return {
        restrict: 'E',
        templateUrl: 'page/sys/directive/uploadFile.html',
        replace: true,
        scope: {
            //必填,该指令所在modal的id，在当前页面唯一
            id: "@",
            uploader: "@",
            picker: "@",
            fileList: "@",
            startOrStopBtn: "@",
            choiceForm: "@",
            fileBp: "@",
            progressLine: "@"
        },
        controller: function ($scope, $http, $element, $timeout) {
            jQuery(function () {
                var beforeSendFile = "beforeSendFile" + $scope.id;
                /***************************************************** 监听分块上传过程中的三个时间点 start ***********************************************************/
                WebUploader.Uploader.register({
                        "before-send-file": "beforeSendFile",//整个文件上传前
                        "before-send": "beforeSend",  //每个分片上传前
                        "after-send-file": "afterSendFile",  //分片上传完毕
                    },
                    {
                        //时间点1：所有分块进行上传之前调用此函数
                        beforeSendFile: function (file) {
                            console.log("所有分块上传之前调用..." + file);
                            var deferred = WebUploader.Deferred();
                            //1、计算文件的唯一标记fileMd5，用于断点续传  如果.md5File(file)方法里只写一个file参数则计算MD5值会很慢 所以加了后面的参数：10*1024*1024
                            (new WebUploader.Uploader()).md5File(file, 0, 10 * 1024 * 1024).progress(function (percentage) {
                                // $('#' + file.id).find('p.state').text('读取MD5信息...' + percentage * 100 + "%");
                            }).then(function (val) {
                                // $('#' + file.id).find("p.state").text("成功获取文件信息...");
                                fileMd5 = val;
                                //获取文件信息后进入下一步
                                deferred.resolve();
                            });
                            fileName = file.name; //为自定义参数文件名赋值
                            return deferred.promise();
                        },
                        //时间点2：如果有分块上传，则每个分块上传之前调用此函数
                        beforeSend: function (block) {
                            console.log("每个分块上传之前调用..." + block);
                            var deferred = WebUploader.Deferred();
                            $.ajax({
                                type: "POST",
                                url: "http://localhost:8080/rcm-rest/v2/breakpoint/check.do",  //ajax验证每一个分片
                                data: {
                                    fileName: fileName,
                                    progressLine: $("#progressLine").val(),
                                    fileMd5: fileMd5,  //文件唯一标记
                                    chunk: block.chunk,  //当前分块下标
                                    chunkSize: block.end - block.start//当前分块大小
                                },
                                cache: false,
                                async: false,  // 与js同步
                                timeout: 10000, // todo 超时的话，只能认为该分片未上传过
                                dataType: "json",
                                success: function (data) {
                                    if (data.exist) {
                                        //分块存在，跳过
                                        deferred.reject();
                                    } else {
                                        //分块不存在或不完整，重新发送该分块内容
                                        deferred.resolve();
                                    }
                                }
                            });
                            this.owner.options.formData.fileMd5 = fileMd5;
                            deferred.resolve();
                            return deferred.promise();
                        },
                        //时间点3：所有分块上传成功后调用此函数
                        afterSendFile: function (file) {
                            console.log("所有分块上传成功后调用..." + file);
                            //如果分块上传成功，则通知后台合并分块
                            $.ajax({
                                type: "POST",
                                url: "http://localhost:8080/rcm-rest/v2/breakpoint/merge.do",  //ajax将所有片段合并成整体
                                data: {
                                    fileName: fileName,
                                    fileMd5: fileMd5
                                },
                                success: function (data) {
                                    count++; //每上传完成一个文件 count+1
                                    if (count <= filesArr.length - 1) {
                                        uploader.upload(filesArr[count].id);//上传文件列表中的下一个文件
                                    }
                                }
                            });
                        }
                    });
                /***************************************************** 监听分块上传过程中的三个时间点 end **************************************************************/

                var $ = jQuery,
                    ratio = window.devicePixelRatio || 1,
                    thumbnailWidth = 100 * ratio,
                    thumbnailHeight = 100 * ratio;
                /*******************初始化参数*********************************/
                var $btn = $('#startOrStopBtn'),//开始上传按钮
                    state = 'pending',//初始按钮状态
                    uploader; //uploader对象
                var fileMd5;  //文件唯一标识

                /******************下面的参数是自定义的*************************/
                var fileName;//文件名称
                var beforeProgress;//如果该文件之前上传过 已经上传的进度是多少
                var count = 0;//当前正在上传的文件在数组中的下标，一次上传多个文件时使用
                var filesArr = new Array();//文件数组：每当有文件被添加进队列的时候 就push到数组中
                var map = {};//key存储文件id，value存储该文件上传过的进度

                uploader = WebUploader.create({
                    auto: true,//选择文件后是否自动上传
                    chunked: true,//开启分片上传
                    chunkSize: 10 * 1024 * 1024,// 如果要分片，分多大一片？默认大小为5M
                    chunkRetry: 3,//如果某个分片由于网络问题出错，允许自动重传多少次
                    threads: 3,//上传并发数。允许同时最大上传进程数[默认值：3]
                    duplicate: true,//是否重复上传（同时选择多个一样的文件），true可以重复上传
                    prepareNextFile: true,//上传当前分片时预处理下一分片
                    fileNumLimit: 10,
                    auto: true,
                    swf: '/html/assets/webuploader-0.1.5/Uploader.swf',// swf文件路径
                    server: 'http://localhost:8080/rcm-rest/v2/breakpoint/save.do',// 文件接收服务端
                    pick: '.filePicker',
                    accept: {
                        //允许上传的文件后缀，不带点，多个用逗号分割
                        extensions: "txt,jpg,jpeg,bmp,png,zip,rar,war,pdf,cebx,doc,docx,ppt,pptx,xls,xlsx",
                        mimeTypes: '.txt,.jpg,.jpeg,.bmp,.png,.zip,.rar,.war,.pdf,.cebx,.doc,.docx,.ppt,.pptx,.xls,.xlsx',
                    }
                });
                //当有文件被添加进队列的时候（点击上传文件按钮，弹出文件选择框，选择完文件点击确定后触发的事件）
                uploader.on('fileQueued', function (file) {
                    console.log("当有文件添加进队列..." + file);
                    //限制单个文件的大小 超出了提示
                    if (file.size > 3 * 1024 * 1024 * 1024) {
                        alert("单个文件大小不能超过3G");
                        return false;
                    }
                    /*************如果一次只能选择一个文件，再次选择替换前一个，就增加如下代码*******************************/
                    //清空文件队列
                    // $list.html("");
                    //清空文件数组
                    // filesArr = [];
                    /*************如果一次只能选择一个文件，再次选择替换前一个，就增加以上代码*******************************/
                    //将选择的文件添加进文件数组
                    filesArr.push(file);
                    $.ajax({
                        type: "POST",
                        url: "http://localhost:8080/rcm-rest/v2/breakpoint/progress.do",  //先检查该文件是否上传过，如果上传过，上传进度是多少
                        data: {
                            fileName: file.name
                        },
                        cache: false,
                        async: false,  // 同步
                        dataType: "json",
                        success: function (data) {
                            //上传过

                        }
                    });
                    uploader.stop(true);
                    //删除队列中的文件
                });
            });
            $scope.addWebuploadCurrent = function (id) {
                console.log(id);
                /*angular.element(".webuploader-container").removeClass('webuploader-container');
                angular.element("#"+id).addClass('webuploader-container');*/
                /*angular.element(".webUpload_current").removeClass('webUpload_current');
                angular.element("#"+id).addClass('webUpload_current');*/
                $(".webupload_current").removeClass("webupload_current");
                $("#" + id).addClass("webupload_current");
            }
        }
    };
});

// 投资部门文件
ctmApp.directive('directUploadFileTouzi', function() {
    return {
        restrict: 'E',
        templateUrl: 'page/sys/directive/directUploadFileTouzi.html',
        replace: true,
        scope:{
            //必填,该指令所在modal的id，在当前页面唯一
            id: "@",
            //对话框的标题，如果没设置，默认为“人员选择”
            title: "@",
            //查询参数
            queryParams: "=",
            //是否可编辑
            isEditable:"=",
            //默认选中的用户,数组类型，{NAME:'张三',VALUE:'user.uuid'}
            checkedUser: "=",
            //映射的key，value，{nameField:'username',valueField:'uuid'}，
            //默认为{nameField:'NAME',valueField:'VALUE'}
            mappedKeyValue: "=",
            callback: "=",
            attach: "=",
            fileList: "=",
            select: "=",
            formalReport: "=",
            // 确定按钮是否可用
            isUse: "=?bind"
        },
        controller: function($scope, $http, $element, Upload){
            $scope.item = {};// 选项
            $scope.item.newItem = null;
            $scope.isUse = true;// 确定按钮是否禁用
            $scope.isUpload = true;// 上传按钮是否隐藏
            $scope.latestAttachmentS = []; // 文件列表
            $scope.title = '附件上传';
            // 获得时间
            $scope.getDate = function () {
                var myDate = new Date();
                //获取当前年
                var year = myDate.getFullYear();
                //获取当前月
                var month = myDate.getMonth() + 1;
                //获取当前日
                var date = myDate.getDate();
                var h = myDate.getHours(); //获取当前小时数(0-23)
                var m = myDate.getMinutes(); //获取当前分钟数(0-59)
                var s = myDate.getSeconds();
                var now = year + '-' + month + "-" + date + " " + h + ':' + m + ":" + s;
                return now;
            }

            $scope.initFileType = function() {
                $scope.item = {};
                $scope.item.newItem = null;
                $scope.isUse = true;
                $scope.isUpload = true;
                $scope.latestAttachmentS = [];
            }

            $scope.changeAttach = function (name) {
                console.log(name);
                if(name.newItem == null) {
                    $scope.isUpload = true;
                } else {
                    $scope.isUpload = false;
                }
                $scope.item = name;
            };

            $scope.submit = function() {
                console.log($scope.latestAttachmentS);
                if($scope.fileList == undefined || $scope.fileList == null){
                    $scope.fileList = [];
                }
                angular.forEach($scope.latestAttachmentS, function (data, index) {
                    $scope.file = {};
                    $scope.file.files = {};
                    $scope.file.files.fileName = data.fileName;
                    $scope.file.files.filePath = data.filePath;
                    $scope.file.files.upload_date = data.upload_date;
                    $scope.file.files.type = data.type;
                    $scope.file.files.typeValue = data.typeValue;
                    $scope.file.files.ITEM_NAME = data.ITEM_NAME;
                    $scope.file.files.UUID = data.UUID;
                    $scope.fileList.push($scope.file);
                });
            }

            $scope.upload = function (file, errorFile, idx) {
                console.log(file);
                if(file == null) {
                    return;
                }
                $scope.status = false;
                angular.forEach($scope.fileList, function (data, index) {
                    if (data.files.fileName == file.name) {
                        $scope.status = true;
                    }
                });
                angular.forEach($scope.latestAttachmentS, function (data, index) {
                    if (data.fileName == file.name) {
                        $scope.status = true;
                    }
                });
                if($scope.status) {
                    alert("您上传的文件已存在，请重新选择！");
                    return;
                }

                var fileSuffixArr = file.name.split('.');
                var fileSuffix = fileSuffixArr[fileSuffixArr.length-1];
                if (fileSuffix != "docx" && fileSuffix != "xlsx" && fileSuffix != "pptx" && fileSuffix != "pdf" &&
                    fileSuffix != "jpg" && fileSuffix != "png" && fileSuffix != "gif" && fileSuffix != "tif" &&
                    fileSuffix != "psd" && fileSuffix != "ppts"){
                    alert("您上传的文档格式不正确，请重新选择！");
                    return;
                }
                $scope.newAttachment = {};
                var fileFolder = "formalReport/";
                var dates = $scope.formalReport.create_date;
                var no = $scope.formalReport.projectNo;

                var strs = new Array(); //定义一数组
                var dates = $scope.getDate();
                strs = dates.split("-"); //字符分割
                dates = strs[0] + strs[1]; //分割后的字符输出
                fileFolder = fileFolder + dates + "/" + no;
                console.log(fileFolder);

                Upload.upload({
                    url: srvUrl + 'file/uploadFile.do',
                    data: {file: file, folder: fileFolder}
                }).then(function (resp) {
                    var retData = resp.data.result_data[0];
                    $scope.newAttachment = {};
                    $scope.newAttachment.fileName = retData.fileName;
                    $scope.newAttachment.filePath = retData.filePath;
                    $scope.newAttachment.upload_date = retData.upload_date;
                    $scope.newAttachment.upload_date.replace(/-/g,"/");
                    $scope.newAttachment.type = "invest";
                    $scope.newAttachment.typeValue = "投资部门提供";
                    $scope.newAttachment.ITEM_NAME = $scope.item.newItem.ITEM_NAME;
                    $scope.newAttachment.UUID = $scope.item.newItem.UUID;
                    $scope.latestAttachmentS.push($scope.newAttachment);
                    $scope.isUse = false;
                }, function (resp) {
                    $.alert(resp.status);
                }, function (evt) {
                    var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                    $scope["progress" + idx] = progressPercentage == 100 ? "" : progressPercentage + "%";
                });
            };

            $scope.deleteFile = function(index) {
                $scope.latestAttachmentS.splice(index, 1);
                console.log($scope.latestAttachmentS.length);
                if($scope.latestAttachmentS.length == 0) {
                    $scope.isUse = true;
                    console.log($scope.isUse);
                }
            }

        }
    };
});
// 项目多选
ctmApp.directive('directProjectMultiDialog', function() {
    return {
        restrict: 'E',
        templateUrl: 'page/sys/directive/directProjectMultiDialog.html',
        replace: true,
        scope:{
            //必填,该指令所在modal的id，在当前页面唯一
            id: "@",
            //对话框的标题，如果没设置，默认为“人员选择”
            title: "@",
            url: "@",
            //查询参数
            queryParams: "=",
            //默认选中的用户,数组类型，[{NAME:'张三',VALUE:'user.uuid'},{NAME:'李四',VALUE:'user.uuid'}]
            checkedProjects: "=",
            //映射的key，value，{nameField:'username',valueField:'uuid'}，
            //默认为{nameField:'NAME',valueField:'VALUE'}
            mappedKeyValue: "=",
            callback: "="
            //移除选中的人员，调用父scope中的同名方法
//        	removeSelectedUser: "&"
        },
        controller:function($scope,$http,$element){
           /* if($scope.url == null || '' == $scope.url){
                $scope.url = "user/queryProjectForSelected.do";
            }*/
            $scope.paginationConf = {
                lastCurrentTimeStamp:'',
                currentPage: 1,
                totalItems: 0,
                itemsPerPage: 10,
                pagesLength: 10,
                queryObj:{},
                perPageOptions: [10, 20, 30, 40, 50],
                onChange: function(){
                }
            };
            if(null != $scope.queryParams){
                $scope.paginationConf.queryObj = $scope.queryParams;
            }
            $scope.queryProject = function(){
                $http({
                    method:'post',
                    url:srvUrl+$scope.url,
                    data: $.param({"page":JSON.stringify($scope.paginationConf)})
                }).success(function(data){
                    if(data.success){
                        $scope.projects = data.result_data.list;
                        $scope.paginationConf.totalItems = data.result_data.totalItems;
                    }else{
                        $.alert(data.result_name);
                    }
                });
            }
            $scope.removeSelectedProject = function(project){
                for(var i = 0; i < $scope.tempCheckedProjects.length; i++){
                    if(project.VALUE == $scope.tempCheckedProjects[i].VALUE){
                        $scope.tempCheckedProjects.splice(i, 1);
                        break;
                    }
                }
            };
            $scope.isChecked = function(project){
                for(var i = 0; i < $scope.tempCheckedProjects.length; i++){
                    if(project.ID    == $scope.tempCheckedProjects[i].VALUE){
                        return true;
                    }
                }
                return false;
            };
            $scope.toggleChecked = function(project){
                //是否选中
                var isChecked = $("#chk_"+$scope.id+"_"+project.ID).prop("checked");
                //是否已经存在
                var flag = false;
                for(var i = 0; i < $scope.tempCheckedProjects.length; i++){
                    if(project.ID == $scope.tempCheckedProjects[i].VALUE){
                        flag = true;
                        if(!isChecked){
                            $scope.tempCheckedProjects.splice(i, 1);
                            break;
                        }
                    }
                }
                if(isChecked && !flag){
                    //如果已经选中，但是不存在，添加
                    $scope.tempCheckedProjects.push({"VALUE":project.BUSINESSID,"NAME":project.PROJECTNAME,"PROJECT_TYPE":project.PROJECT_TYPE});
                }
            };

            $scope.cancelSelected = function(){
                $scope.initData();
            }
            $scope.saveSelected = function(){
                var cus = $scope.tempCheckedProjects;
                $scope.checkedProjects.splice(0,$scope.checkedProjects.length)
                for(var i = 0; i < cus.length; i++){
                    var project = {};
                    project[$scope.mappedKeyValue.nameField] = cus[i].NAME;
                    project[$scope.mappedKeyValue.valueField] = cus[i].VALUE;
                    project[$scope.mappedKeyValue.typeField] = cus[i].PROJECT_TYPE;

                    $scope.checkedProjects.push(project);
                    delete project.$$hashKey;
                }
                if($scope.callback != null){
                    $scope.callback();
                }
            }
            $scope.initData = function(){
                var cus = $.parseJSON(JSON.stringify($scope.checkedProjects));
                $scope.tempCheckedProjects = [];
                for(var i = 0; i < cus.length; i++){
                    var project = {};
                    project.NAME = cus[i][$scope.mappedKeyValue.nameField];
                    project.VALUE = cus[i][$scope.mappedKeyValue.valueField];
                    project.PROJECT_TYPE = cus[i][$scope.mappedKeyValue.typeField];
                    $scope.tempCheckedProjects.push(project);
                }
                $scope.paginationConf.queryObj.name = '';
                $scope.queryProject();
            }
            $scope.$watch('checkedProjects', $scope.initData, true);
            $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.queryProject);
        }
    };
});

// 组织多选
ctmApp.directive('directOrgMultiDialog', function() {
    return {
        restrict: 'E',
        templateUrl: 'page/sys/directive/directOrgMultiDialog.html',
        replace: true,
        scope:{
            //必填,该指令所在modal的id，在当前页面唯一
            id: "@",
            //对话框的标题，如果没设置，默认为“人员选择”
            title: "@",
            url: "@",
            //查询参数
            queryParams: "=",
            //默认选中的用户,数组类型，[{NAME:'张三',VALUE:'user.uuid'},{NAME:'李四',VALUE:'user.uuid'}]
            checkedOrgs: "=",
            //映射的key，value，{nameField:'username',valueField:'uuid'}，
            //默认为{nameField:'NAME',valueField:'VALUE'}
            mappedKeyValue: "=",
            callback: "="
            //移除选中的人员，调用父scope中的同名方法
//        	removeSelectedUser: "&"
        },
        controller:function($scope,$http,$element){
            /* if($scope.url == null || '' == $scope.url){
                 $scope.url = "user/queryProjectForSelected.do";
             }*/
            $scope.paginationConf = {
                lastCurrentTimeStamp:'',
                currentPage: 1,
                totalItems: 0,
                itemsPerPage: 10,
                pagesLength: 10,
                queryObj:{},
                perPageOptions: [10, 20, 30, 40, 50],
                onChange: function(){
                }
            };
            if(null != $scope.queryParams){
                $scope.paginationConf.queryObj = $scope.queryParams;
            }
            $scope.queryOrg = function(){
                $http({
                    method:'post',
                    url:srvUrl+$scope.url,
                    data: $.param({"page":JSON.stringify($scope.paginationConf)})
                }).success(function(data){
                    if(data.success){
                        $scope.orgs = data.result_data.list;
                        $scope.paginationConf.totalItems = data.result_data.totalItems;
                    }else{
                        $.alert(data.result_name);
                    }
                });
            }
            $scope.removeSelectedOrg = function(org){
                for(var i = 0; i < $scope.tempCheckedOrgs.length; i++){
                    if(org.VALUE == $scope.tempCheckedOrgs[i].VALUE){
                        $scope.tempCheckedOrgs.splice(i, 1);
                        break;
                    }
                }
            };
            $scope.isChecked = function(org){
                for(var i = 0; i < $scope.tempCheckedOrgs.length; i++){
                    if(org.ID    == $scope.tempCheckedOrgs[i].VALUE){
                        return true;
                    }
                }
                return false;
            };
            $scope.toggleChecked = function(org){
                //是否选中
                var isChecked = $("#chk_"+$scope.id+"_"+org.ID).prop("checked");
                //是否已经存在
                var flag = false;
                for(var i = 0; i < $scope.tempCheckedOrgs.length; i++){
                    if(org.ID == $scope.tempCheckedOrgs[i].VALUE){
                        flag = true;
                        if(!isChecked){
                            $scope.tempCheckedOrgs.splice(i, 1);
                            break;
                        }
                    }
                }
                if(isChecked && !flag){
                    //如果已经选中，但是不存在，添加
                    $scope.tempCheckedOrgs.push({"VALUE":org.ORGPKVALUE,"NAME":org.NAME});
                }
            };

            $scope.cancelSelected = function(){
                $scope.initData();
            }
            $scope.saveSelected = function(){
                var cus = $scope.tempCheckedOrgs;
                $scope.checkedOrgs.splice(0,$scope.checkedOrgs.length)
                for(var i = 0; i < cus.length; i++){
                    var org = {};
                    org[$scope.mappedKeyValue.nameField] = cus[i].NAME;
                    org[$scope.mappedKeyValue.valueField] = cus[i].VALUE;

                    $scope.checkedOrgs.push(org);
                    delete org.$$hashKey;
                }
                if($scope.callback != null){
                    $scope.callback();
                }
            }
            $scope.initData = function(){
                var cus = $.parseJSON(JSON.stringify($scope.checkedOrgs));
                $scope.tempCheckedOrgs = [];
                for(var i = 0; i < cus.length; i++){
                    var org = {};
                    org.NAME = cus[i][$scope.mappedKeyValue.nameField];
                    org.VALUE = cus[i][$scope.mappedKeyValue.valueField];
                    $scope.tempCheckedOrgs.push(org);
                }
                $scope.paginationConf.queryObj.name = '';
                $scope.queryOrg();
            }
            $scope.$watch('checkedOrgs', $scope.initData, true);
            $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.queryOrg);
        }
    };
});

// 组织单选 ZTree
ctmApp.directive('directiveOrgList', function() {
    return {
        restrict: 'E',
        templateUrl: 'page/sys/directive/DirectiveOrgList.html',
        replace: true,
        scope:{},
        controller:function($scope,$http,$element){
            //获取父作用域
            var carouselScope = $element.parent().scope();
            var paramId=null;
            var categoryCode=null;
            //获取组织结构角色
            var ztree, setting = {
                callback:{
                    onClick:function(event, treeId, treeNode){
                        paramId = treeNode.id;
                        categoryCode = treeNode.name;
                    },
                    beforeExpand:function(treeId, treeNode){
                        if(typeof(treeNode.children)=='undefined'){
                            $scope.addTreeNode(treeNode);
                        }
                    }
                }
            };
            $scope.addTreeNode = function (parentNode){
                var pid = '';
                if(parentNode && parentNode.id) pid = parentNode.id;
                $scope.$parent.httpData('fnd/Group/getCommonOrg', {parentId:pid}).success(function(data){
                    if (!data || data.result_code != 'S') return null;
                    var nodeArray = data.result_data;
                    if(nodeArray<1) return null;
                    for(var i=0;i<nodeArray.length;i++){
                        curNode = nodeArray[i];
                        var iconUrl = 'assets/javascripts/zTree/css/zTreeStyle/img/department.png';
                        if(curNode.cat && curNode.cat=='Org'){
                            iconUrl = 'assets/javascripts/zTree/css/zTreeStyle/img/org.png';
                        }
                        curNode.icon = iconUrl;
                    }
                    if(pid == ''){//当前加载的是根节点
                        ztree.addNodes(null, nodeArray);
                        var rootNode = ztree.getNodes()[0];
                        $scope.addTreeNode(rootNode);
                        rootNode.open = true;
                        ztree.refresh();
                    }else{
                        ztree.addNodes(parentNode, nodeArray, true);
                    }
                });
            }
            $scope.cancelBtn=function(){
                paramId=null;
                categoryCode=null;
            }
            $scope.saveOrgListforDiretive=function(){
                carouselScope.setDirectiveOrgList(paramId,categoryCode);
                paramId=null;
                categoryCode=null;
            }

            angular.element(document).ready(function() {
                ztree = $.fn.zTree.init($("#treeIDpor1"), setting);
                $scope.addTreeNode('');
            });
        }
    };
});

// 相关资源（新）
ctmApp.directive('directiveAccachmentNew', function() {
    return {
        restrict: 'E',
        templateUrl: 'page/sys/directive/directiveAccachmentNew.html',
        replace: true,
        scope: {
            // 唯一标识
            id: "@",
            // 业务类型
            businessType: "@",
            pageLocation: "@",
            // 项目Oracle数据
            businessId: "=",
            wfState: "=",
            lastUpdateBy: "=",
            // mongo数据同步
            serviceType: "=",
            projectModel: "=",
            // 替换发送相关信息
            projectName: "=",
            toSend: "=",
            // 附件列表
            fileList: "=",
            // 设置属性
            isEdite: "@",
            isChoose: "@",
            isShowMeetingAttachment: "@",
            // 调用父组件操作
            initUpdate: "&initUpdate"
        },
        link:function(scope,element,attr){
        },
        controller:function($scope,$http,$element,Upload,$window){
            $scope.getDate = function () {
                var myDate = new Date();
                //获取当前年
                var year = myDate.getFullYear();
                //获取当前月
                var month = myDate.getMonth() + 1;
                //获取当前日
                var date = myDate.getDate();
                var h = myDate.getHours(); //获取当前小时数(0-23)
                var m = myDate.getMinutes(); //获取当前分钟数(0-59)
                var s = myDate.getSeconds();
                var now = year + '-' + month + "-" + date + " " + h + ':' + m + ":" + s;
                return now;
            };

            // 初始化数据
            $scope._initData = function () {
                if($scope.isEdite == "true"){
                    $scope.isEdite = true;
                } else {
                    $scope.isEdite = false;
                }
                if($scope.isChoose == "true"){
                    $scope.isChoose = true;
                } else {
                    $scope.isChoose = false;
                }
                if($scope.isShowMeetingAttachment == "true"){
                    $scope.isShowMeetingAttachment = true;
                } else {
                    $scope.isShowMeetingAttachment = false;
                }

                $scope.getAttachmentType();
                $scope.isShow = false;
            };
            $scope._selectItemType = function (docCode) {
                return selectDocItem(docCode);
            };
            // 获取附件类型
            $scope.getAttachmentType = function(){
                if (isEmpty($scope.serviceType)){
                    return false;
                }
                var serviceCode = $scope.serviceType[0].KEY;
                var projectModelName = '';
                if(isEmpty($scope.projectModel[0])) {
                    projectModelName = $scope.projectModel.VALUE;
                } else {
                    projectModelName = $scope.projectModel[0].VALUE;
                }
                var functionType = '';
                if ($scope.businessType == 'formalReview'){
                    functionType = '正式评审';
                } else {
                    functionType = '预评审';
                }

                var url= "common/commonMethod/getAttachmentType";
                $scope.$parent.httpData(url,{"serviceCode":serviceCode, "projectModelName": projectModelName, "functionType": functionType}).success(function(data){
                    if(data.result_code === 'S'){
                        $scope.attachmentType=data.result_data;
                    }else{
                        alert(data.result_name);
                    }
                });
            };
            $scope._initData();
            /*// 全选
            $scope._selectAll = function(){
                if($("#all").attr("checked")){
                    $(":checkbox[name='choose']").attr("checked",1);
                }else{
                    $(":checkbox[name='choose']").attr("checked",false);
                }
            };*/

            // 新增文件
            $scope._addOneNewFile = function () {
                if (isEmpty($scope.attachmentType)){
                    $scope.getAttachmentType();
                }
                function _addBlankRow(_array) {
                    var blankRow = {
                        newFile: true,
                        _file_content: ''
                    };
                    var size = 0;
                    for (var idx in _array) {
                        console.log(idx);
                        size++;
                    }
                    _array[size] = blankRow;
                }

                if (undefined == $scope.fileList) {
                    $scope.fileList = [];
                }
                _addBlankRow($scope.fileList);
            };

            // 删除文件
            $scope._deleteFile = function(){
                var chkObjs = $("input[type=checkbox][name=fileChoose]:checked");
                if(chkObjs.length == 0){
                    $.alert("请选择要删除的数据！");
                    return false;
                }
                if(chkObjs.length > 1){
                    $.alert("请只选择一条数据进行删除!");
                    return false;
                }
                var idsStr = "";
                for(var i = 0; i < chkObjs.length; i++){
                    idsStr = idsStr + chkObjs[i].value + ",";
                }
                idsStr = idsStr.substring(0, idsStr.length - 1);
                attach_delete(idsStr);
                var url = '';
                // 判断文件路径
                if ($scope.businessType == 'preReview') {
                    url = "preInfoCreate/deleteAttachmengInfoInMongo.do";
                } else if ($scope.businessType == 'formalReview') {
                    url = "formalAssessmentInfoCreate/deleteAttachmengInfoInMongo.do";
                } else {
                    url = "bulletinInfo/deleteAttachmengInfoInMongo.do";
                }
                $http({
                    method:'post',
                    url:srvUrl + url,
                    data: $.param({"json":JSON.stringify({"businessId":$scope.businessId, "fileId":idsStr})})
                }).success(function(data){
                    if(data.success){
                        $.alert(data.result_name);
                        $scope.initUpdate({'id': $scope.businessId});
                    }else{
                        $.alert(data.result_name);
                    }
                });
                var _all_files = $scope.fileList;
                if (_all_files != null) {
                    for (var i = 0; i < _all_files.length; i++) {
                        if (_all_files[i].selected) {
                            if (_all_files[i].fileid) {
                                attach_delete(_all_files[i].fileid);
                            }
                            _all_files.splice(i, 1);
                            i--;
                        }
                    }
                    $scope.initUpdate({'id': $scope.businessId});
                }
            };

            // 上传
            $scope._uploadThat = function (_file, _idx, _item) {
                Upload.upload({
                    url: srvUrl + 'cloud/upload.do',
                    data: {
                        file: _file,
                        "docType": $scope.businessType,
                        'docCode': $scope.businessId,
                        'pageLocation': $scope.pageLocation
                    }
                }).then(function (resp) {
                    var _fileList = attach_list($scope.businessType, $scope.businessId, $scope.pageLocation).result_data;

                    var url = '';
                    // 判断文件路径
                    if ($scope.businessType == 'preReview') {
                        url = "preInfoCreate/addAttachmengInfoToMongo.do";
                    } else if ($scope.businessType == 'formalReview') {
                        url = "formalAssessmentInfoCreate/addAttachmengInfoToMongo.do";
                    } else {
                        url = "bulletinInfo/addAttachmengInfoToMongo.do";
                    }
                    _item.fileId = _fileList[_fileList.length-1].fileid + "";
                    _item.lastUpdateBy = $scope.lastUpdateBy;
                    _item.lastUpdateData = $scope.getDate();

                    $http({
                        method:'post',
                        url:srvUrl + url,
                        data: $.param({"json":JSON.stringify({"businessId":$scope.businessId, "item":_item, "oldFileName": _file.name})})
                    }).success(function(data){
                        if(data.success){
                            $scope.initUpdate({'id': $scope.businessId});
                            $.alert(data.result_name);
                        }else{
                            $.alert(data.result_name);
                        }
                    });
                }, function (resp) {
                    $.alert(resp.status);
                }, function (evt) {
                    var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                    $scope["_progress_" + _idx] = progressPercentage == 100 ? "" : progressPercentage + "%";
                });
            };

            // 替换
            $scope._uploadReplace = function (_file, _idx, _item) {
                $scope.cancel();
                Upload.upload({
                    url: srvUrl + 'cloud/replace.do',
                    data: {
                        file: _file,
                        "docType": $scope.businessType,
                        'docCode': $scope.businessId,
                        'pageLocation': $scope.pageLocation,
                        "oldFileId": _item.fileid,
                        "reason": _item.reason,
                        "oldFileName": _item.fileName
                    }
                }).then(function (resp) {

                    var _fileList = attach_list($scope.businessType, $scope.businessId, $scope.pageLocation).result_data;

                    var _fileList = attach_list($scope.businessType, $scope.businessId, $scope.pageLocation).result_data;

                    var addUrl = '';
                    var delUrl = '';
                    // 判断文件路径
                    if ($scope.businessType == 'preReview') {
                        addUrl = "preInfoCreate/addAttachmengInfoToMongo.do";
                        delUrl = "preInfoCreate/deleteAttachmengInfoInMongo.do";
                    } else if ($scope.businessType == 'formalReview') {
                        addUrl = "formalAssessmentInfoCreate/addAttachmengInfoToMongo.do";
                        delUrl = "formalAssessmentInfoCreate/deleteAttachmengInfoInMongo.do";
                    } else {
                        addUrl = "bulletinInfo/addAttachmengInfoToMongo.do";
                        delUrl = "bulletinInfo/deleteAttachmengInfoInMongo.do";
                    }
                    $http({
                        method:'post',
                        url:srvUrl + delUrl,
                        data: $.param({"json":JSON.stringify({"businessId":$scope.businessId, "fileId":_item.fileid})})
                    }).success(function(data){
                        _item.fileId = _fileList[_fileList.length-1].fileid + "";
                        _item.lastUpdateBy = $scope.lastUpdateBy;
                        _item.lastUpdateData = $scope.getDate();
                        if (_item.fileNamSuffix != null && _item.fileNamSuffix != undefined) {
                            _item.fileName = _item.fileName + "_" + _item.fileNamSuffix;
                        }
                        $http({
                            method:'post',
                            url:srvUrl + addUrl,
                            data: $.param({"json":JSON.stringify({"businessId":$scope.businessId, "item":_item, "oldFileName": _file.name})})
                        }).success(function(data){
                            alert(data.result_name);
                            if(data.success){
                                if (!isEmpty($scope.toSend)){
                                    var message = $scope.projectName + "中类型为" + _item.itemType.ITEM_NAME + "的附件,由于" + _item.reason + "原因被替换了，请查看！";
                                    $http({
                                        method: 'post',
                                        url: srvUrl + 'cloud/remind.do',
                                        data: $.param({
                                            'message': message,
                                            'shareUsers': $scope.toSend.VALUE,
                                            'type':'DT'
                                        }),
                                        headers: {'Content-Type': 'application/x-www-form-urlencoded'}
                                    }).success(function (data) {
                                        if(isEmpty(data)){
                                            $.alert('推送消息失败!');
                                        }else{
                                            if(data['result_code'] == 'S'){
                                                $scope._share_message_id_ = '';
                                                $scope._clear_share_users_();
                                                $('#_share_message_dialog').modal('hide');
                                                $.alert(data['result_name']);
                                                // $scope._share_message_result_test_(data['result_data']);
                                            }else{
                                                $.alert(data['result_name']);
                                            }
                                        }
                                    });
                                }
                                $scope.initUpdate({'id': $scope.businessId});

                            }else{
                                $.alert(data.result_name);
                            }
                        });
                    });

                    /*var url = '';
                    // 判断文件路径
                    if ($scope.businessType == 'preReview') {
                        url = "preInfoCreate/addAttachmengInfoToMongo.do";
                    } else if ($scope.businessType == 'formalReview') {
                        url = "formalAssessmentInfoCreate/addAttachmengInfoToMongo.do";
                    } else {
                        url = "bulletinInfo/addAttachmengInfoToMongo.do";
                    }
                    _item.fileId = _fileList[_fileList.length-1].fileid + "";
                    _item.lastUpdateBy = $scope.lastUpdateBy;
                    _item.lastUpdateData = $scope.getDate();
                    if (_item.fileNamSuffix != null && _item.fileNamSuffix != undefined) {
                        _item.fileName = _item.fileName + "_" + _item.fileNamSuffix;
                    }
                    $http({
                        method:'post',
                        url:srvUrl + url,
                        data: $.param({"json":JSON.stringify({"businessId":$scope.businessId, "item":_item, "oldFileName": _file.name})})
                    }).success(function(data){
                        alert(data.result_name);
                        if(data.success){
                            if (!isEmpty($scope.toSend)){
                                var message = $scope.projectName + "中类型为" + _item.itemType.ITEM_NAME + "的附件,由于" + _item.reason + "原因被替换了，请查看！";
                                $http({
                                    method: 'post',
                                    url: srvUrl + 'cloud/remind.do',
                                    data: $.param({
                                        'message': message,
                                        'shareUsers': $scope.toSend.VALUE,
                                        'type':'DT'
                                    }),
                                    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
                                }).success(function (data) {
                                    if(isEmpty(data)){
                                        $.alert('推送消息失败!');
                                    }else{
                                        if(data['result_code'] == 'S'){
                                            $scope._share_message_id_ = '';
                                            $scope._clear_share_users_();
                                            $('#_share_message_dialog').modal('hide');
                                            $.alert(data['result_name']);
                                            // $scope._share_message_result_test_(data['result_data']);
                                        }else{
                                            $.alert(data['result_name']);
                                        }
                                    }
                                });
                            }
                            $scope.initUpdate({'id': $scope.businessId});

                        }else{
                            $.alert(data.result_name);
                        }
                    });*/
                }, function (resp) {

                }, function (evt) {
                    var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                    $scope["_progress_" + _idx] = progressPercentage == 100 ? "" : progressPercentage + "%";
                });
            };

            $scope.showModel = function () {
                $scope.isShow = true;
            };

            $scope.cancel = function () {
                $scope.isShow = false;
                $('.modal-backdrop').remove();
                $('#main-body').removeClass("modal-open");
            };

            // 预览
            $scope._review = function (uri) {
                $window.open(uri, '_blank', 'menubar=no,toolbar=no, status=no,scrollbars=yes');
            };

            // 下载
            $scope._download = function (uri) {
                window.open(uri, '_blank', 'menubar=no,toolbar=no, status=no,scrollbars=yes');
            };

            // 删除
            $scope._delete = function(file_id){
                attach_delete(file_id);
                var url = '';
                // 判断文件路径
                if ($scope.businessType == 'preReview') {
                    url = "preInfoCreate/deleteAttachmengInfoInMongo.do";
                } else if ($scope.businessType == 'formalReview') {
                    url = "formalAssessmentInfoCreate/deleteAttachmengInfoInMongo.do";
                } else {
                    url = "bulletinInfo/deleteAttachmengInfoInMongo.do";
                }
                $http({
                    method:'post',
                    url:srvUrl + url,
                    data: $.param({"json":JSON.stringify({"businessId":$scope.businessId, "fileId":file_id})})
                }).success(function(data){
                    if(data.success){
                        $.alert(data.result_name);
                        $scope.initUpdate({'id': $scope.businessId});
                    }else{
                        $.alert(data.result_name);
                    }
                });
            };

            // 切换资源类型时修改附件名称逻辑
            $scope._changeItemType = function (item) {
                item.fileName = item.type.ITEM_NAME;
            };

            // 选择上会文件
            $scope._changeChoose = function (index, fileId) {
                console.log(index);
                console.log(fileId);
                if ($scope.businessType == 'preReview') {
                    url = "preInfoCreate/changeMeetingAttach.do";
                } else if ($scope.businessType == 'formalReview') {
                    url = "formalAssessmentInfoCreate/changeMeetingAttach.do";
                } else {
                    url = "bulletinInfo/changeMeetingAttach.do";
                }
                $http({
                    method:'post',
                    url:srvUrl + url,
                    data: $.param({"json":JSON.stringify({"businessId":$scope.businessId, "fileId":fileId})})
                }).success(function(data){
                    if(data.success){
                        if (data.result_data == '0'){
                            $.alert("删除上会附件成功");
                        } else {
                            $.alert("添加上会附件成功");
                        }
                    }else{
                        $.alert(data.result_name);
                    }
                });
            }
        }
    };
});
/*******************************************************公共指令结束***************************************************/


/*******************************************************业务指令开始***************************************************/
/************************************投标评审开始*****************************/
// 项目详情
ctmApp.directive('directiveProjectPreReviewView', function() {
    return {
        restrict: 'E',
        templateUrl: 'page/sys/directive/ProjectPre/DirectiveProjectPreReviewView.html',
        replace: true,
        link:function(scope,element,attr){
        },
        controller:function($scope,$http,$element){
        }
    };
});
// 相关资源
ctmApp.directive('directiveFileList', function() {
    return {
        restrict: 'E',
        templateUrl: 'page/sys/directive/ProjectPre/DirectiveFileList.html',
        replace: true,
        link:function(scope,element,attr){
        },
        controller:function($scope,$http,$element,Upload){
            $scope.selectAll = function(){
                if($("#all").attr("checked")){
                    $(":checkbox[name='choose']").attr("checked",1);
                }else{
                    $(":checkbox[name='choose']").attr("checked",false);
                }
            }
            $scope.batchDownload = function(){
                var filenames = "";
                var filepaths = "";
                $("input[type=checkbox][name=choose]:checked").each(function(){
                    filenames+=$(this).attr("filename")+",";
                    filepaths+=$(this).attr("filepath")+",";
                });
                if(filenames.length == 0 || filepaths.length == 0){
                    $.alert("请选择要打包下载的文件！");
                    return false;
                }
                filenames = filenames.substring(0, filenames.length - 1);
                filepaths = filepaths.substring(0, filepaths.length - 1);
                downloadBatch(filenames, filepaths);
            }

            //删除数组
            $scope.deleteFile = function(item){
                $.confirm("您确认要删除该文件吗？",function(){
                    //根据UUID和版本号定位删除
                    $http({
                        method:'post',
                        url:srvUrl+"preInfo/deleteAttachment.do",
                        data: $.param({"json":JSON.stringify({"UUID":item.UUID,"version":item.version,"businessId":$scope.pre.id})})
                    }).success(function(data){
                        if(data.success){
                            $scope.newAttachment.splice(jQuery.inArray(item,$scope.newAttachment),1);
                            $scope.getPreById($scope.pre.id);
                            $.alert("文件删除成功！");
                        }else{
                            $.alert(data.result_name);
                        }
                    });
                })
            }

            //新增数组
            $scope.addOneNewFile = function(){
                function addBlankRow1(array){
                    var blankRow = {
                        newFile:true
                    }
                    var size = array.length;
                    array[size]=blankRow;
                }
                if(undefined==$scope.newAttachment){
                    $scope.newAttachment={files:[]};
                }
                addBlankRow1($scope.newAttachment);
            }

            $scope.upload = function (file,errorFile, outId,item) {
                if(errorFile && errorFile.length>0){
                    if(errorFile[0].$error=='maxSize'){
                        var errorMsg = fileErrorMsg(errorFile);
                        $.alert(errorMsg);
                    }else{
                        $.alert("文件错误！");
                    }
                }else if(file){

                    if(item.approved == null || item.approved == "" || item.approved.NAME == null|| item.approved.NAME == ""){
                        $.alert("请选择审核人！");
                        return;
                    }
                    if(item.programmed == null || item.programmed == "" || item.programmed.NAME == null || item.programmed.NAME == ""){
                        $.alert("请选择编制人！");
                        return;
                    }

                    $.confirm("是否确认替换？",function(){
                        Upload.upload({
                            url:srvUrl+'common/RcmFile/upload',
                            data: {file: file, folder:'',typeKey:'preAssessmentPath'}
                        }).then(function (resp) {
                            var retData = resp.data.result_data[0];

                            //根据UUID和版本号定位修改
                            $http({
                                method:'post',
                                url:srvUrl+"preInfo/updateAttachment.do",
                                data: $.param({"json":JSON.stringify({
                                        "UUID":item.UUID,
                                        "version":item.version,
                                        "businessId":$scope.pre.id,
                                        "fileName":retData.fileName,
                                        "filePath":retData.filePath,
                                        "programmed":item.programmed,
                                        "approved":item.approved
                                    })
                                })
                            }).success(function(data){
                                if(data.success){
                                    $scope.getPreById($scope.pre.id);
                                    $.alert("文件替换成功！");
                                }else{
                                    $.alert(data.result_name);
                                }
                            });

                            $scope.newAttachment[outId].fileName=retData.fileName;
                            $scope.newAttachment[outId].filePath=retData.filePath;
                        }, function (resp) {
                            console.log('Error status: ' + resp.status);
                        }, function (evt) {
                            //            			var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                        });
                    });
                }
            };

            $scope.uploadNew = function (file,errorFile, outId,item) {
                if(errorFile && errorFile.length>0){
                    if(errorFile[0].$error=='maxSize'){
                        var errorMsg = fileErrorMsg(errorFile);
                        $.alert(errorMsg);
                    }else{
                        $.alert("文件错误！");
                    }
                }else if(file){
                    if(item.newItem == null || item.newItem.UUID == null || item.newItem.UUID == ""){
                        $.alert("请选择资源类型！");
                        return;
                    }
                    if(item.approved == null || item.approved == "" || item.approved.NAME == null|| item.approved.NAME == ""){
                        $.alert("请选择审核人！");
                        return;
                    }
                    if(item.programmed == null || item.programmed == "" || item.programmed.NAME == null || item.programmed.NAME == ""){
                        $.alert("请选择编制人！");
                        return;
                    }
                    Upload.upload({
                        url:srvUrl+'common/RcmFile/upload',
                        data: {file: file, folder:'',typeKey:'preAssessmentPath'}
                    }).then(function (resp) {
                        var retData = resp.data.result_data[0];

                        //根据UUID处理版本号
                        var v = 0;
                        for(var i in $scope.newAttachment){

                            if($scope.newAttachment[i].newFile){
                                $scope.newAttachment[i] = $scope.newAttachment[i].newItem;
                            }
                            if($scope.newAttachment[i].UUID == item.newItem.UUID){
                                if($scope.newAttachment[i].version != undefined && $scope.newAttachment[i].version != null && $scope.newAttachment[i].version !=""){
                                    if($scope.newAttachment[i].version > v){
                                        v = $scope.newAttachment[i].version;
                                    }
                                }

                            }
                        }
                        v++ ;
                        item.fileName = retData.fileName;
                        item.filePath = retData.filePath;
//            				item.programmed={"NAME":$scope.credentials.userName,"VALUE":$scope.credentials.UUID}
//            				item.approved={"NAME":$scope.credentials.userName,"VALUE":$scope.credentials.UUID}

                        //根据UUID判断文件所属类别
                        $http({
                            method:'post',
                            url:srvUrl+"preInfo/addNewAttachment.do",
                            data: $.param({"json":JSON.stringify({"UUID":item.newItem.UUID,"version":v,"businessId":$scope.pre.id,"item":item})})
                        }).success(function(data){
                            if(data.success){
                                $scope.getPreById($scope.pre.id);
                            }else{
                                $.alert(data.result_name);
                            }
                        });
                        $scope.newAttachment[outId].fileName=retData.fileName;
                        $scope.newAttachment[outId].filePath=retData.filePath;
                    }, function (resp) {
                        console.log('Error status: ' + resp.status);
                    }, function (evt) {
                        //            			var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                    });
                }
            };
        }
    };
});
// 投标评审决策委员会第一部分
ctmApp.directive('directivePreJcwyh', function() {
    return {
        restrict: 'E',
        templateUrl: 'page/sys/directive/projectPre/DirectivePreJcwyh.html',
        replace: true,
        link:function(scope,element,attr){
        }

    };
});
// 投标评审决策委员会第一部分（新）
ctmApp.directive('directivePreJcwyhNew', function() {
    return {
        restrict: 'E',
        templateUrl: 'page/sys/directive/projectPre/DirectivePreJcwyhNew.html',
        replace: true,
        link:function(scope,element,attr){
        }

    };
});

//起草预评审报告弹窗
ctmApp.directive('directivePreCreateReport', ['$location','$filter', function($location,$filter) {
    return {
        restrict: 'E',
        templateUrl: 'page/sys/directive/ProjectPre/DirectivePreCreateReport.html',
        replace: true,
        scope:{btnText:"@btnText",textValue:"@textValue"},
        link:function(scope,element,attr){
        },
        controller:function($scope,$http,$element){
            $scope.x={};
            $scope.listProjectName = function () {
                if($scope.$parent.pre && $scope.$parent.pre._id){//如果已经明确知道预评审项目
                    $scope.pprs = [{BUSINESS_ID:$scope.$parent.pre._id,PROJECT_NAME:$scope.$parent.pre.apply.projectName}];
                    $scope.x.UUID = $scope.$parent.pre._id;
                }else{
                    $http({
                        method:'post',
                        url:srvUrl+'preAuditReport/queryNotNewlyPreAuditProject.do'
                    }).success(function(data){
                        if(data.success){
                            $scope.pprs = data.result_data;
                        }
                    }).error(function(data,status,headers, config){
                        $.alert(status);
                    });
                }
                $scope.x.pmodel = "normal";
            };
            $scope.forReport=function(model,uuid,comId){
                if(model==null || model==""){
                    $.alert("请选择项目模式!");
                    return false;
                }else if(uuid==null || uuid=="") {
                    $.alert("请选择项目!");
                    return false;
                }else{
                    $("#addModal").modal('hide');
                    var routePath = "";
                    if(model == "normal"){
                        routePath = "PreNormalReport";
                    }

                    if(model == "other"){
                        routePath = "PreOtherReport";
                    }
                    $location.path("/"+routePath+"/"+model+"/Create/"+uuid+"/"+$filter('encodeURI')('#/PreAuditReportList/0'));
                }
            }
        }
    };
}]);

/************************************投标评审结束*****************************/

/************************************正式评审开始*****************************/
// 项目详情
ctmApp.directive('directiveProjectFormalAssessmentInfo', function() {
    return {
        restrict: 'E',
        templateUrl: 'page/sys/directive/projectFormal/DirectiveProjectFormalAssessmentInfo.html',
        replace: true,
        link:function(scope,element,attr){
        },
        controller:function($scope,$http,$element){
        }
    };
});
// 相关资源
ctmApp.directive('directiveProjectFormalFileList', function() {
    return {
        restrict: 'E',
        templateUrl: 'page/sys/directive/projectFormal/DirectiveProjectFormalFileList.html',
        replace: true,
        link:function(scope,element,attr){
        },
        controller:function($scope,$http,$element,Upload){
            $scope.batchDownload = function(){
                var filenames = "";
                var filepaths = "";
                $("input[type=checkbox][name=choose]:checked").each(function(){
                    filenames+=$(this).attr("filename")+",";
                    filepaths+=$(this).attr("filepath")+",";
                });
                if(filenames.length == 0 || filepaths.length == 0){
                    $.alert("请选择要打包下载的文件！");
                    return false;
                }
                filenames = filenames.substring(0, filenames.length - 1);
                filepaths = filepaths.substring(0, filepaths.length - 1);
                downloadBatch(filenames, filepaths);
            }

            $scope.selectAll = function(){
                if($("#all").attr("checked")){
                    $(":checkbox[name='choose']").attr("checked",1);
                }else{
                    $(":checkbox[name='choose']").attr("checked",false);
                }
            }

            //删除数组
            $scope.deleteFile = function(item){
                $.confirm("您确认要删除该文件吗？",function(){
                    //根据UUID和版本号定位删除
                    $http({
                        method:'post',
                        url:srvUrl+"formalAssessmentAudit/deleteAttachment.do",
                        data: $.param({"json":JSON.stringify({"UUID":item.UUID,"version":item.version,"businessId":$scope.pfr.id})})
                    }).success(function(data){
                        if(data.success){
                            $scope.newAttachment.splice(jQuery.inArray(item,$scope.newAttachment),1);
                            $scope.getFormalAssessmentByID($scope.pfr.id);
                            $.alert("文件删除成功！");
                        }else{
                            $.alert(data.result_name);
                        }
                    });
                })
            }

            //新增数组
            $scope.addOneNewFile = function(){
                function addBlankRow1(array){
                    var blankRow = {
                        newFile:true
                    }
                    var size = array.length;
                    array[size]=blankRow;
                }
                if(undefined==$scope.newAttachment){
                    $scope.newAttachment={files:[]};
                }
                addBlankRow1($scope.newAttachment);
            }

            $scope.upload = function (file,errorFile, outId,item) {
                if(errorFile && errorFile.length>0){
                    if(errorFile[0].$error=='maxSize'){
                        var errorMsg = fileErrorMsg(errorFile);
                        $.alert(errorMsg);
                    }else{
                        $.alert("文件错误！");
                    }
                }else if(file){

                    if(item.approved == null || item.approved == "" || item.approved.NAME == null|| item.approved.NAME == ""){
                        $.alert("请选择审核人！");
                        return;
                    }
                    if(item.programmed == null || item.programmed == "" || item.programmed.NAME == null || item.programmed.NAME == ""){
                        $.alert("请选择编制人！");
                        return;
                    }

                    $.confirm("是否确认替换？",function(){
                        Upload.upload({
                            url:srvUrl+'common/RcmFile/upload',
                            data: {file: file,typeKey:'formalAssessmentPath'}
                        }).then(function (resp) {
                            var retData = resp.data.result_data[0];

                            //根据UUID和版本号定位修改
                            $http({
                                method:'post',
                                url:srvUrl+"formalAssessmentAudit/updateAttachment.do",
                                data: $.param({"json":JSON.stringify({
                                        "UUID":item.UUID,
                                        "version":item.version,
                                        "businessId":$scope.pfr.id,
                                        "fileName":retData.fileName,
                                        "filePath":retData.filePath,
                                        "programmed":item.programmed,
                                        "approved":item.approved
                                    })
                                })
                            }).success(function(data){
                                if(data.success){
                                    $scope.getFormalAssessmentByID($scope.pfr.id);
                                    $.alert("文件替换成功！");
                                }else{
                                    $.alert(data.result_name);
                                }
                            });

                            $scope.newAttachment[outId].fileName=retData.fileName;
                            $scope.newAttachment[outId].filePath=retData.filePath;
                        }, function (resp) {
                            console.log('Error status: ' + resp.status);
                        }, function (evt) {
                            //            			var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                        });
                    });
                }
            };

            $scope.uploadNew = function (file,errorFile, outId,item) {
                if(errorFile && errorFile.length>0){
                    if(errorFile[0].$error=='maxSize'){
                        var errorMsg = fileErrorMsg(errorFile);
                        $.alert(errorMsg);
                    }else{
                        $.alert("文件错误！");
                    }
                }else if(file){
                    if(item.newItem == null || item.newItem.UUID == null || item.newItem.UUID == ""){
                        $.alert("请选择资源类型！");
                        return;
                    }
                    if(item.approved == null || item.approved == "" || item.approved.NAME == null|| item.approved.NAME == ""){
                        $.alert("请选择审核人！");
                        return;
                    }
                    if(item.programmed == null || item.programmed == "" || item.programmed.NAME == null || item.programmed.NAME == ""){
                        $.alert("请选择编制人！");
                        return;
                    }
                    Upload.upload({
                        url:srvUrl+'common/RcmFile/upload',
                        data: {file: file, folder:'',typeKey:'formalAssessmentPath'}
                    }).then(function (resp) {
                        var retData = resp.data.result_data[0];

                        //根据UUID处理版本号
                        var v = 0;
                        for(var i in $scope.newAttachment){

                            if($scope.newAttachment[i].newFile){
                                $scope.newAttachment[i] = $scope.newAttachment[i].newItem;
                            }
                            if($scope.newAttachment[i].UUID == item.newItem.UUID){
                                if($scope.newAttachment[i].version != undefined && $scope.newAttachment[i].version != null && $scope.newAttachment[i].version !=""){
                                    if($scope.newAttachment[i].version > v){
                                        v = $scope.newAttachment[i].version;
                                    }
                                }

                            }
                        }
                        v++ ;
                        item.fileName = retData.fileName;
                        item.filePath = retData.filePath;
                        //根据UUID判断文件所属类别
                        $http({
                            method:'post',
                            url:srvUrl+"formalAssessmentAudit/addNewAttachment.do",
                            data: $.param({"json":JSON.stringify({"UUID":item.newItem.UUID,"version":v,"businessId":$scope.pfr.id,"item":item})})
                        }).success(function(data){
                            if(data.success){
                                $scope.getFormalAssessmentByID($scope.pfr.id);
                            }else{
                                $.alert(data.result_name);
                            }
                        });
                        $scope.newAttachment[outId].fileName=retData.fileName;
                        $scope.newAttachment[outId].filePath=retData.filePath;
                    }, function (resp) {
                        console.log('Error status: ' + resp.status);
                    }, function (evt) {
                        //            			var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                    });
                }
            };

        }
    };
});
//测算意见
ctmApp.directive('directiveFormalCesuanOpinion', function() {
    return {
        restrict: 'E',
        templateUrl: 'page/sys/directive/projectFormal/DirectiveFormalCesuanOpinion.html',
        replace: true,
        controller:function($scope,$http,$element,$location,$routeParams) {

        }
    };
});
//协议意见
ctmApp.directive('directiveFormalProtocolOpinion', function() {
    return {
        restrict: 'E',
        templateUrl: 'page/sys/directive/projectFormal/DirectiveFormalProtocolOpinion.html',
        replace: true,
        controller:function($scope,$http,$element,$location,$routeParams) {

        }
    };
});

// 意见（NEW）
ctmApp.directive('directiveFormalOpinionNew', function() {
    return {
        restrict: 'E',
        templateUrl: 'page/sys/directive/projectFormal/DirectiveFormalOpinionNew.html',
        replace: true,
        controller:function($scope,$http,$element,$location,$routeParams) {

        }
    };
});

// 评审用成本及费用
ctmApp.directive('directiveFormalReportCbfy', function() {
    return {
        restrict: 'E',
        templateUrl: 'page/sys/directive/projectFormal/DirectiveFormalReportCbfy.html',
        replace: true,
        link:function(scope,element,attr){
        },
        controller:function($scope,$http,$element){
        }
    };
});
// 风险及问题总结
ctmApp.directive('directiveFormalReportFxjwtzj', function() {
    return {
        restrict: 'E',
        templateUrl: 'page/sys/directive/projectFormal/DirectiveFormalReportFxjwtzj.html',
        replace: true,
        link:function(scope,element,attr){
        },
        controller:function($scope,$http,$element){
        }
    };
});
// 结论与建议
ctmApp.directive('directiveFormalReportJlyjy', function() {
    return {
        restrict: 'E',
        templateUrl: 'page/sys/directive/projectFormal/DirectiveFormalReportJlyjy.html',
        replace: true,
        link:function(scope,element,attr){
        },
        controller:function($scope,$http,$element){
        }
    };
});
// 评审用成本及费用(新)
ctmApp.directive('directiveFormalReportCbfyNew', function() {
    return {
        restrict: 'E',
        templateUrl: 'page/sys/directive/projectFormal/DirectiveFormalReportCbfyNew.html',
        replace: true,
        link:function(scope,element,attr){
        },
        controller:function($scope,$http,$element){
        }
    };
});
// 风险及问题总结(新)
ctmApp.directive('directiveFormalReportFxjwtzjNew', function() {
    return {
        restrict: 'E',
        templateUrl: 'page/sys/directive/projectFormal/DirectiveFormalReportFxjwtzjNew.html',
        replace: true,
        link:function(scope,element,attr){
        },
        controller:function($scope,$http,$element){
        }
    };
});
// 结论与建议(新)
ctmApp.directive('directiveFormalReportJlyjyNew', function() {
    return {
        restrict: 'E',
        templateUrl: 'page/sys/directive/projectFormal/DirectiveFormalReportJlyjyNew.html',
        replace: true,
        link:function(scope,element,attr){
        },
        controller:function($scope,$http,$element){
        }
    };
});
// 后续执行要求(新)
ctmApp.directive('directiveFormalReportHxzxyqNew', function() {
    return {
        restrict: 'E',
        templateUrl: 'page/sys/directive/projectFormal/DirectiveFormalReportHxzxyqNew.html',
        replace: true,
        link:function(scope,element,attr){
        },
        controller:function($scope,$http,$element){
        }
    };
});
// 专业评审意见(新)
ctmApp.directive('directiveFormalReportZypsyjNew', function() {
    return {
        restrict: 'E',
        templateUrl: 'page/sys/directive/projectFormal/DirectiveFormalReportZypsyjNew.html',
        replace: true,
        link:function(scope,element,attr){
        },
        controller:function($scope,$http,$element){
        }
    };
});
// 后续执行要求
ctmApp.directive('directiveFormalReportHxzxyq', function() {
    return {
        restrict: 'E',
        templateUrl: 'page/sys/directive/projectFormal/DirectiveFormalReportHxzxyq.html',
        replace: true,
        link:function(scope,element,attr){
        },
        controller:function($scope,$http,$element){
        }
    };
});
// 专业评审意见
ctmApp.directive('directiveFormalReportZypsyj', function() {
    return {
        restrict: 'E',
        templateUrl: 'page/sys/directive/projectFormal/DirectiveFormalReportZypsyj.html',
        replace: true,
        link:function(scope,element,attr){
        },
        controller:function($scope,$http,$element){
        }
    };
});
// 是否需要上会
ctmApp.directive('directiveNeedMeeting', function() {
    return {
        restrict: 'E',
        templateUrl: 'page/sys/directive/projectFormal/DirectiveNeedMeeting.html',
        replace: true,
        link:function(scope,element,attr){
        },
        controller:function($scope,$http,$element){
            $scope.saveNeetMeetingState = function(state,businessId){
                $scope.confirmMeetingUrl = $scope.confirmMeetingUrl.replace("{{formalReport._id}}", $scope.formalReport._id);
                $scope.confirmMeetingUrl = $scope.confirmMeetingUrl.replace("{{pageFlag}}", $scope.pageFlag);
                $scope.$parent.httpData("rcm/ProjectInfo/updateProjectInfo",{needMeeting:state,businessId:businessId}).success(function(data){
                    window.location.href = $scope.confirmMeetingUrl;
                })
            }
            $scope.confirmMeeting = function(url){
                $.confirm("确认后将不可更改，确认不上会？", function(){
                    $scope.confirmMeetingUrl = url;
                    $scope.saveNeetMeetingState(1, $scope.formalReport.projectFormalId)
                });
            }
        }
    };
});
// 正式评审决策委员会第一部分(旧)
ctmApp.directive('directiveFormalJcwyh', function() {
    return {
        restrict: 'E',
        templateUrl: 'page/sys/directive/projectFormal/DirectiveFormalJcwyh.html',
        replace: true,
        link:function(scope,element,attr){
        }

    };
});
// 正式评审决策委员会第一部分(新)
ctmApp.directive('directiveFormalJcwyhNew', function() {
    return {
        restrict: 'E',
        templateUrl: 'page/sys/directive/projectFormal/DirectiveFormalJcwyhNew.html',
        replace: true,
        link:function(scope,element,attr){
        }

    };
});
// 切换模板提示
ctmApp.directive('directPromptBoxFormal', function() {
    return {
        restrict: 'E',
        templateUrl: 'page/sys/directive/projectFormal/directPromptBoxFormal.html',
        replace: true,
        scope:{
            //必填,该指令所在modal的id，在当前页面唯一
            id: "@",
            //对话框的标题，如果没设置，默认为“人员选择”
            title: "@",
            //是否显示
            isShow:"=?bind",
            // select框数据
            summaryTemplate: "=",
            // select框留存初始化数据
            templateBak: "=",
            // select框初始化数据
            template: "=",
            // 调用的父页面的方法
            summaryTemplateChange: "&summaryTemplateChange"
        },
        link:function($scope,$element,$attrs){
            $scope.change = function(){
                if($scope.template.ITEM_CODE != $scope.templateBak.ITEM_CODE){
                    $scope.isShow = true;
                }
            };
            $scope.submit = function () {
                $scope.templateBak = angular.copy($scope.template);
                $scope.summaryTemplateChange({type:$scope.template});
                $scope.isShow = false;
                $('.modal-backdrop').remove();
                $('#main-body').removeClass("modal-open");
            }
        },
        controller: function($scope, $http, $element, Upload){
            $scope.initDefaultData = function(){
                if($scope.title==null){
                    $scope.title = "提示信息";
                }
                if($scope.isShow==null|| ($scope.isShow!="true" && $scope.isShow!="false")){
                    $scope.isShow = false;
                }

            };
            $scope.initDefaultData();
            $scope.cancel = function () {
                $scope.template = angular.copy($scope.templateBak);
                $scope.isShow = false;
                $('.modal-backdrop').remove();
                $('#main-body').removeClass("modal-open");
            }
        }
    };
});
// 提交决策会材料返回
ctmApp.directive('directiveFormalReturnBtn', function() {
    return {
        restrict: 'E',
        //templateUrl: 'page/sys/directive/projectFormal/DirectiveProjectFormalReview.html',
        // '<a class="btn btn-info" ng-href="{{url|decodeURI}}" ng-click="callback()"><i class="fa fa-reply"></i>返回</a>'
        template: '<a ng-href="{{url|decodeURI}}" ng-click="callback()">返回</a>',
        replace: true,
        scope:{url:'@',callback:"&"},
        link:function(scope,element,attr){
        },
        controller:function($scope,$http,$element){
        }
    };
});
// 正式评审评审申请单详情
ctmApp.directive('directiveProjectFormalReview', function() {
    return {
        restrict: 'E',
        templateUrl: 'page/sys/directive/projectFormal/DirectiveProjectFormalReview.html',
        replace: true,
        link:function(scope,element,attr){
        },
        controller:function($scope,$http,$element){
        }
    };
});
//
ctmApp.directive('directiveProjectFormalBusinessUnitCommit', function() {
    return {
        restrict: 'E',
        templateUrl: 'page/sys/directive/projectFormal/DirectiveProjectFormalBusinessUnitCommit.html',
        replace: true,
        link:function(scope,element,attr){
        },
        controller:function($scope,$http,$element){
        }
    };
});
// 提交决策会材料模板内容查看
ctmApp.directive('directiveProjectFormalTempData', function() {
    return {
        restrict: 'E',
        templateUrl: 'page/sys/directive/projectFormal/directiveProjectFormalTempData.html',
        replace: true,
        link:function(scope,element,attr){
        },
        controller:function($scope,$http,$element){
        }
    };
});
/************************************正式评审结束*****************************/

/************************************其他评审开始*****************************/
/************************************其他评审结束*****************************/
/*******************************************************业务指令结束***************************************************/


/*******************************************************流程指令开始***************************************************/
// 旧流程图
ctmApp.directive('directiveProcessList', function() {
    return {
        restrict: 'E',
        templateUrl: 'page/sys/directive/DirectiveProcessList.html',
        replace: true,
        link:function(scope,element,attr){
        },
        controller:function($scope,$http,$element){
            $scope.wf = {};

            $scope.$watch("pre + pfr", function(){
                if($scope.pre == null){
                    $scope.proj = $scope.pfr;
                }else{
                    $scope.proj = $scope.pre;
                }
            });
            if($scope.wfInfo.processInstanceId == null || $scope.wfInfo.processInstanceId == ''){
                $.ajax({
                    type:'post',
                    url: srvUrl + "bpmn/getProcessInstanceId.do",
                    data: $.param({"businessId":$scope.wfInfo.businessId}),
                    dataType: "json",
                    async: false,
                    success:function(result){
                        if(result.success && result.result_data != null &&result.result_data != ''){
                            $scope.wfInfo.processInstanceId = result.result_data.PROC_INST_ID_;
                            $scope.wfInfo.processDefinitionId = result.result_data.PROC_DEF_ID_;
                        }else{
                            return false;
                        }
                    }
                });
            }
            //获取审批历史
            if($scope.wfInfo && $scope.wfInfo.processInstanceId){
                //如果有流程实例ID
                //获取审批历史
                $scope.$parent.httpData('bpm/WorkFlow/getProcessInstanceApproveHistory',$scope.wfInfo).success(
                    function(data){
                        if(data.result_code == 'S'){
                            $scope.wf.historyList = data.result_data;;
                        }
                    }
                );

                //获取流程图
                $scope.wf.processDefinitionId = $scope.wfInfo.processDefinitionId;
                //获取当前节点位置
                $scope.$parent.httpData('bpm/WorkFlow/getActiveActivityIds',$scope.wfInfo).success(function(data){
                    $scope.wf.currentNodes = data.result_data;
                });
            }else{
                //起草阶段
                $scope.$parent.httpData('bpm/WorkFlow/getProcessDefinitionId',$scope.wfInfo).success(function(data){
                    $scope.wf.processDefinitionId = data.result_data.processDefinitionId;
                });
            }
        }
    };
});
// 新流程图
ctmApp.directive('directiveProcessPage', function() {
    return {
        restrict: 'E',
        templateUrl: 'page/sys/directive/DirectiveProcessPage.html',
        replace: true,
        link:function(scope,element,attr){
        },
        controller:function($scope,$http,$element){
            $scope.wf = {};
            $scope.processType = $scope.$parent.progressType || $scope.progressType;
            console.log($scope);
            //获取流程图
            $scope.$watch("refreshImg", function(){
                if($scope.wfInfo!=null && $scope.wfInfo.businessId != null){
                    //如果businessId不为空，说明流程已经提交，要获取当前节点位置
                    $scope.$parent.httpData('bpm/WorkFlow/getActiveActivityIds',$scope.wfInfo).success(function(data){
                        $scope.wf.currentNodes = data.result_data;
                    });
                }
                $scope.$parent.httpData('bpm/WorkFlow/getProcessDefinitionId',$scope.wfInfo).success(function(data){
                    $scope.wf.processDefinitionId = data.result_data.processDefinitionId;
                });
            });
        }
    };
});
// 旧流程提交框
ctmApp.directive('approvePopWin', function(){
    return {
        restrict:'AE',
        templateUrl:'page/sys/directive/approvePopWin.html',
        replace:'true',
        scope:{approve:'=',approvearr:'='},
        controller:function($scope,$location){
//        	$scope.approve = {};
            $scope.changeData = function(idx){
                $scope.submitInfo = $scope.approve[idx].submitInfo;
                $scope.toNodeType = $scope.approve[idx].toNodeType;
                $scope.redirectUrl = $scope.approve[idx].redirectUrl;
            }

            $scope.$watch("approvearr+approve", function(){
                var str = JSON.stringify($scope.approvearr);
                if(str!=null){
                    $scope.approve=JSON.parse(str);
                }

                if($scope.approve!=null && !Array.isArray($scope.approve)){
                    $scope.isAllocateTask = false;
                    $scope.approve = $scope.approve.approve;
                    $scope.emergencyLevel = "一般";
                }
                if(typeof ($scope.approve)!='undefined'){
                    for(var i=0;i<$scope.approve.length;i++){//添加默认的newTaskVar属性
                        var si = $scope.approve[i].submitInfo;
                        if(typeof (si.newTaskVar)=='undefined'){
                            $scope.approve[i].submitInfo.newTaskVar = {submitBy:$scope.$parent.credentials.userName};
                        }else{
                            if(typeof (si.newTaskVar.submitBy)=='undefined'){
                                $scope.approve[i].submitInfo.newTaskVar.submitBy = $scope.$parent.credentials.userName;
                            }
                        }
                    }
                    $scope.toNodeIndex = 0;
                    $scope.changeData($scope.toNodeIndex);
                }
            },true);

            $scope.changeToNodeIndex = function(){
                var idx = $scope.toNodeIndex;
                if($scope.approve[idx].submitInfo.currentTaskVar == null || $scope.approve[idx].submitInfo.currentTaskVar.cesuanFileOpinion==null){
                    $("#cesuanFileOpinionDiv").hide();
                    $("#tzProtocolOpinionDiv").hide();
                }else{
                    $("#cesuanFileOpinionDiv").show();
                    $("#tzProtocolOpinionDiv").show();
                }
                $scope.changeData(idx);
            }
            $scope.submitNext = function(){
                if(typeof($scope.submitInfo)=='undefined'){
                    $.alert("请选择下一环节！");
                    return;
                }
                if($scope.submitInfo.runtimeVar!=null && $scope.submitInfo.runtimeVar.inputUser==""){
                    $('#submitModal').modal('hide');
                    $.alert("请先分配任务！");
                    return;
                }
                if($scope.submitInfo.runtimeVar!=null && $scope.submitInfo.runtimeVar.legalReviewLeader==""){
                    $('#submitModal').modal('hide');
                    $.alert("请先分配任务！");
                    return;
                }
                if($scope.$parent.$parent.showController!=null && $scope.$parent.$parent.showController.isTask!=null){
                    //保存任务分配信息
                    $scope.$parent.$parent.myTaskallocation.businessId = $scope.submitInfo.businessId;
                    if($scope.$parent.$parent.wfInfo.processKey == 'preAssessment'){
                        $.ajax({
                            type:'post',
                            url: srvUrl + "preInfo/saveTaskPerson.do",
                            data: $.param({"task":JSON.stringify($scope.$parent.$parent.myTaskallocation)}),
                            dataType: "json",
                            async: false,
                            success:function(result){
                                if(!result.success){
                                    alert(result.result_name);
                                    return false;
                                }
                            }
                        });
                    }else if($scope.$parent.$parent.wfInfo.processKey == 'formalAssessment'){
                        $.ajax({
                            type:'post',
                            url: srvUrl + "formalAssessmentAudit/saveTaskToMongo.do",
                            data: $.param({"task":JSON.stringify($scope.$parent.$parent.myTaskallocation)}),
                            dataType: "json",
                            async: false,
                            success:function(result){
                                if(!result.success){
                                    alert(result.result_name);
                                    return false;
                                }
                            }
                        });
                    }
                }
                var taskId = $scope.submitInfo.taskId;
                var url = "bpm/WorkFlow/startProcess";
                if(taskId && taskId!=""){
                    url = "bpm/WorkFlow/approve";
                }
                var cesuan = null;
                var tz = null;
                if($scope.submitInfo.currentTaskVar!=null && $scope.submitInfo.currentTaskVar.cesuanFileOpinion!=null ){
                    cesuan = $scope.submitInfo.currentTaskVar.cesuanFileOpinion;
                    tz = $scope.submitInfo.currentTaskVar.tzProtocolOpinion;
                }
                if(cesuan!=null && (cesuan==""||tz=="")){
                    $.alert("测算文件和投资协议的意见必须填写！");
                    return;
                }
                if($scope.emergencyLevel!=null){
                    $scope.submitInfo.emergencyLevel = $scope.emergencyLevel;
                }
                var auditUrl = $location.absUrl();
                var preUrl = "";

                if(auditUrl.indexOf("preAssessment") > 0){
                    preUrl = "ProjectPreReviewView"
                }else if(auditUrl.indexOf("formalAssessment") > 0){
                    preUrl = "ProjectFormalReviewDetailView/View";
                }else if(auditUrl.indexOf("NoticeOfDecision") > 0){
                    preUrl = "NoticeOfDecision/view";
                }
                var redirectUrl = null;
                if(preUrl!=""){
                    redirectUrl = preUrl+auditUrl.substring(auditUrl.lastIndexOf("/"));
                    redirectUrl = redirectUrl.replace(taskId, "");
                }
                $scope.redirectUrl = null;
                if($scope.submitInfo.currentTaskVar!=null && $scope.submitInfo.currentTaskVar.opinion!=null){
                    $scope.submitInfo.runtimeVar.opinion = $scope.submitInfo.currentTaskVar.opinion;
                }
                show_Mask();
                $scope.$parent.httpData(url, $scope.submitInfo).success(function(data){
                    hide_Mask();
                    if(data.success){
                        $scope.submitAllReady = true;
                        $('#submitModal').modal('hide');
                        $('#submitModalOld').modal('hide');
                        $('#oldSubmitModal').modal('hide');
//                        if(auditUrl.indexOf("formalAssessment") > 0){
////                        	$scope.$parent.$parent.initData();
//                        	$location.path($location.url());
//                        }

                        if(typeof $scope.redirectUrl == 'string'){//如果配置的有跳转链接
                            $location.path($scope.redirectUrl);
                        }else{
                            $.alert(data.result_name);
                            if(redirectUrl!=null){
                                $location.path(redirectUrl);
                            }
                        }
                    }else{
                        $.alert(data.result_name);
                    }
                });

            }
            //设置常用意见
            $scope.setOpinion = function(opinion){
                $scope.submitInfo.currentTaskVar.opinion = opinion;
            }
        }
    }
});
// 投标评审流程提交弹出框
ctmApp.directive('preReviewBpmnPopWin', function () {
    return {
        restrict: 'AE',
        templateUrl: 'page/sys/directive/preReviewBpmnPopWin.html',
        replace: 'true',
        scope: {
            approve: '=',
            // 调用父组件操作
            beforeSubmit: "&beforeSubmit"
        },
        controller: function ($scope, $location, $http, Upload) {
            $scope.changeTypeSelected = "";
            $scope.changeTypes = [{key: 'before', value: '前加签'}, {key: 'after', value: '后加签'}];
            $scope.pre = {};
            //默认不上会
            $scope.pre.needMeeting = '0';
            //默认不出报告
            $scope.pre.needReport = '1';
            $scope.pre.decisionOpinion = false;
            //验证任务人员
            $scope.callfunction = function (functionName) {
                var func = eval(functionName);
                //创建函数对象，并调用
                return new func(arguments[1]);
            }
            var validServiceType = function () {
                var result = {success: true, result_name: ""};
                if ($scope.$parent.pre.oracle.SERVICETYPE_ID != "1401" && $scope.$parent.pre.oracle.SERVICETYPE_ID != "1402") {
                    result.success = false;
                    result.result_name = "此项目非传统水务、水环境项目！无法选择此选项！";
                }
                return result;
            };
            //验证任务人员信息(分配任务节点)
            var validCheckedFzr = function () {
                var result = {success: true, result_name: ""};
                if ($scope.$parent.myTaskallocation == null || $scope.$parent.myTaskallocation == "") {
                    result.success = false;
                    result.result_name = "请分配负责人！";
                }
                if ($scope.$parent.myTaskallocation.reviewLeader.NAME == null || $scope.$parent.myTaskallocation.reviewLeader.NAME == "") {
                    result.success = false;
                    result.result_name = "请选择评审负责人！";
                }
                return result;
            };
            /**加签参数初始化add by LiPan 2019-03-08**/
            $scope.showSelectPerson = function () {
                $("#submitModal").modal('hide');
                $("#userSinDialog").modal('show');
            };
            $scope.changeWork = function () {
                console.log($scope.approve);
                //人员验证
                if ($scope.$parent.checkedUser.NAME == null || $scope.$parent.checkedUser.NAME == '') {
                    $.alert("请选择目标人员！");
                    return;
                }
                if ($scope.$parent.checkedUser.VALUE == $scope.$parent.$parent.credentials.UUID) {
                    $.alert("不能转办给自己！");
                    return;
                }
                if ($scope.$parent.checkedUser.VALUE == $scope.$parent.curLog.AUDITUSERID) {
                    $.alert("不能转办给最初人员！");
                    return;
                }
                if ($scope.flowVariables == null || $scope.flowVariables.opinion == null || $scope.flowVariables.opinion == "") {
                    $.alert("审批意见不能为空！");
                    return;
                }
                if ($scope.flowVariables.opinion.length > 650) {
                    $.alert("审批意见不能超过650字！");
                    return;
                }
                if ($scope.changeTypeSelected == 'after') {
                    var validate = wf_validateSign('preReview', $scope.approve.businessId);
                    if (!isEmpty(validate.code)) {
                        $.alert(validate.comment);
                        return;
                    }
                }
                //执行转办操作
                show_Mask();
                $http({
                    method: 'post',
                    url: srvUrl + "sign/doSign.do",
                    data: $.param({
                        'type': $scope.changeTypeSelected,
                        'business_module': 'preReview',
                        "business_id": $scope.approve.businessId,
                        "user_json": JSON.stringify($scope.$parent.checkedUser),
                        "task_id": $scope.$parent.curLog.TASKID,
                        "option": $scope.flowVariables.opinion
                    })
                }).success(function (result) {
                    hide_Mask();
                    if ($scope.approve.callbackSuccess != null && result.success) {
                        $scope.approve.callbackSuccess(result);
                    } else if ($scope.approve.callbackFail != null && !result.success) {
                        $scope.approve.callbackFail(result);
                    } else {
                        $.alert(result.result_name);
                    }
                });
            };
            $scope.workOver = function () {
                //人员验证
                if ($scope.flowVariables == null || $scope.flowVariables.opinion == null || $scope.flowVariables.opinion == "") {
                    $.alert("审批意见不能为空！");
                    return;
                }
                if ($scope.flowVariables.opinion.length > 650) {
                    $.alert("审批意见不能超过650字！");
                    return;
                }
                //执行办结操作
                show_Mask();
                $http({
                    method: 'post',
                    url: srvUrl + "sign/endSign.do",
                    data: $.param({
                        "business_module": "preReview",
                        "business_id": $scope.approve.businessId,
                        "task_id": $scope.$parent.curLog.TASKID,
                        "option": $scope.flowVariables.opinion
                    })
                }).success(function (result) {
                    hide_Mask();
                    if ($scope.approve.callbackSuccess != null && result.success) {
                        $scope.approve.callbackSuccess(result);
                    } else if ($scope.approve.callbackFail != null && !result.success) {
                        $scope.approve.callbackFail(result);
                    } else {
                        $.alert(result.result_name);
                    }
                });
            };
            /**加签参数初始化add by LiPan 2019-03-08**/
                //验证专业评审人员(评审负责人节点)
//			var validCheckedMajorMember= function(){
//				var result = {success:true,result_name:""};
//				if($scope.$parent.myTaskallocation == null  || $scope.$parent.myTaskallocation == ""){
//					result.success = false;
//					result.result_name = "请分配专业评审人员！";
//				}
//				if($scope.$parent.myTaskallocation.majorMember == null ||$scope.$parent.myTaskallocation.majorMember == [] || $scope.$parent.myTaskallocation.majorMember == ""){
//					result.success = false;
//					result.result_name = "请分配专业评审人员！";
//				}
//				return result;
//			};
                //验证意见（投资中心/水环境）
            var validOpinions = function () {
                    var result = {success: true, result_name: ""};
                    if ($scope.submitInfo.currentTaskVar == null || $scope.submitInfo.currentTaskVar.cesuanFileOpinion == null || $scope.submitInfo.currentTaskVar.cesuanFileOpinion == "") {
                        result.success = false;
                        result.result_name = "请填写测算文件意见！";
                    }
                    if ($scope.submitInfo.currentTaskVar == null || $scope.submitInfo.currentTaskVar.tzProtocolOpinion == null || $scope.submitInfo.currentTaskVar.tzProtocolOpinion == "") {
                        result.success = false;
                        result.result_name = "请填写投资协议意见！";
                    }
                    return result;
                }

            //判断是否显示toConfirm的打分项
            $scope.showMarkMethod = function (documentation) {

                if (documentation != null && documentation != "") {
                    var docObj = JSON.parse(documentation);
                    if (docObj.mark == "reviewPassMark") {
                        $scope.showReviewToConfirm = true;
                        $scope.showReviewConfirmToEnd = false;
                        $scope.showMark = true;
                    }
                    if (docObj.mark == "toEnd") {
                        $scope.showMark = false;
                        $scope.showReviewToConfirm = false;
                        $scope.showReviewConfirmToEnd = true;
                    }
                } else {
                    $scope.showReviewToConfirm = false;
                    $scope.showReviewConfirmToEnd = false;
                }
            }
            $scope.checkMark = function () {
                if ($scope.approve == null) {
                    return;
                }
                var processOptions = $scope.approve.processOptions;

                if (processOptions[0].documentation != null && processOptions[0].documentation != '') {
                    var docObj = JSON.parse(processOptions[0].documentation);

                    if (docObj.mark == "reviewPassMark") {
                        $scope.showReviewToConfirm = true;
                    }
                    if (docObj.mark == "legalPassMark") {
                        $scope.showLegalToConfirm = true;
                    }
                }

                //流程选项
                for (var i in processOptions) {
                    var documentation = processOptions[i].documentation;
                    if (documentation != null && documentation != "") {
                        var docObj = JSON.parse(documentation);
                        if (docObj.mark == "toEnd") {
                            $scope.newEndOption = true;
                        }
                        if (docObj.mark == "reviewPassMark") {
//							$scope.showMark = true;
                            if (i == 0) {
                                $scope.showMark = true;
                            }
                            //查询后台的评价记录
//							$.ajax({
//								type:'post',
//								url: srvUrl + "formalMark/queryMarks.do",
//								data: $.param({"businessId":$scope.approve.businessId}),
//								dataType: "json",
//					        	async: false,
//								success:function(result){
//									if(result.success){
//										if(result.result_data == null){
//											if(docObj.mark == "reviewPassMark"){
//												$scope.showReviewFirstMark = true;
//											}
//										}else{
//											if(docObj.mark == "reviewPassMark"){
//												if(result.result_data.REVIEWFILEACCURACY == null && result.result_data.REVIEWFILEACCURACY ==""){
//													$scope.showReviewFirstMark = true;
//												}
//											}
//										}
//									}else{
//										alert(result.result_name);
//										return;
//									}
//								}
//							});
                        }
                    }
                }
            }


            $scope.submitInfo = {};
            $scope.submitInfo.currentTaskVar = {};
            $scope.submitNext = function () {
                if(!isEmpty($scope.beforeSubmit)){
                    if(typeof $scope.beforeSubmit == 'function'){
                        if(!$scope.beforeSubmit()){
                            $('#submitModal').modal('hide');
                            $("#submibtnn").hide();
                            return;
                        }
                    }
                }
                /********Add By LiPan
                 * 此处发现选择了"加签"单选以后,
                 * $scope.showReviewToConfirm的值依然是true
                 * 加签操作不起作用,所以加上了该段代码
                 * ********/
                if ($("input[name='bpmnProcessOption']:checked").val() == 'CHANGE') {
                    $scope.showReviewToConfirm = false;
                }
                if ($("input[name='bpmnProcessOption']:checked").val() == 'WORKOVER') {
                    $scope.showReviewToConfirm = false;
                }
                /********Add By LiPan********/
                if ("submit" == $scope.approve.operateType) {
                    $scope.submit();
                } else if ("audit" == $scope.approve.operateType) {
                    if ($scope.showReviewConfirmToEnd) {
                        $.confirm("您选择了终止项目，意味着您将废弃此项目！是否确认？", function () {
                            $scope.auditSingle();
                        });
                    } else if ($scope.showReviewToConfirm) {
                        $.confirm("您选择了评审负责人确认选项，意味着您已经和投资经理沟通完毕，流程将进入下一环节！是否确认？", function () {
                            $scope.auditSingle();
                        });
                    } else if ($("input[name='bpmnProcessOption']:checked").val() == 'CHANGE') {
                        $scope.changeWork();
                    } else if ($("input[name='bpmnProcessOption']:checked").val() == 'WORKOVER') {
                        $scope.workOver();
                    } else {
                        $scope.auditSingle();
                    }
                } else if ("change" == $scope.approve.operateType) {
                    if ($("input[name='bpmnProcessOption']:checked").val() == 'CHANGE') {
                        $scope.changeWork();
                    } else if ($("input[name='bpmnProcessOption']:checked").val() == 'WORKOVER') {
                        $scope.workOver();
                    }
                } else {
                    $.alert("操作状态不明确！");
                }
            };
            $scope.checkReport = function () {
                $scope.pre.needReport = "1";
                $scope.pre.decisionOpinion = null;
            }
            $scope.submit = function () {
                var url = srvUrl + "preAudit/startSingleFlow.do";
                show_Mask();
                $http({
                    method: 'post',
                    url: url,
                    data: $.param({
                        "processKey": $scope.approve.processKey,
                        "businessId": $scope.approve.businessId
                    })
                }).success(function (result) {
                    hide_Mask();
                    if ($scope.approve.callbackSuccess != null && result.success) {
                        $scope.approve.callbackSuccess(result);
                        $location.path("/PreInfoList/1");
                    } else if ($scope.approve.callbackFail != null && !result.success) {
                        $scope.approve.callbackFail(result);
                    } else {
                        $.alert(result.result_name);
                    }
                });
            };

            //jquery判断是否对象非空
            function isEmptyObject(e) {
                var t;
                for (t in e)
                    return !1;
                return !0
            }

            $scope.auditSingle = function () {

                //验证确认节点是否选择上会，与报告
                if ($scope.approve.showController.isReviewLeaderConfirm) {
                    if ($scope.pre == null || $scope.pre.needMeeting == null) {
                        $.alert("请选择是否需要上会！");
                        return;
                    }
//					if($scope.pre.needMeeting == '0' || $scope.pre.needMeeting == 0  ){
//						$.alert("请选择决策意见！");
//						return;
//					}
                    if ($scope.pre.needReport == null) {
                        $.alert("请选择是否需要出评审报告！");
                        return;
                    }

                    if ($scope.pre.needReport == 0) {
                        if ($scope.pre.noReportReason == null || $scope.pre.noReportReason == '') {
                            $.alert("请填写不出报告原因！");
                            return;
                        }
                        if ($scope.pre.noReportReason.length > 200) {
                            $.alert("不出报告原因不能大于200字！");
                            return;
                        }
                    }
                    if ($scope.pre.decisionOpinion) {
                        $scope.pre.decisionOpinion = '6';
                    } else {
                        $scope.pre.decisionOpinion = '5';
                    }
                    $.ajax({
                        type: 'post',
                        url: srvUrl + "preInfo/saveNeedMeetingAndNeedReport.do",
                        data: $.param({
                            "pre": JSON.stringify($scope.pre),
                            'businessId': $scope.approve.businessId
                        }),
                        dataType: "json",
                        async: false,
                        success: function (result) {
                            if (!result.success) {
                                alert(result.result_name);
                                return;
                            }
                        }
                    });
                }

                if ($scope.flowVariables == null || $scope.flowVariables == 'undefined' || $scope.flowVariables.opinion == undefined || $scope.flowVariables.opinion == null || $scope.flowVariables.opinion == "") {
                    $.alert("审批意见不能为空！");
                    return;
                }
                if ($scope.flowVariables.opinion.length > 650) {
                    $.alert("审批意见不能超过650字！");
                    return;
                }
                if ($scope.approve.showController.isGroupMember) {
                    //保存小组成员意见到mongo

                    var json = {
                        "opinion": $scope.flowVariables.opinion,
                        "businessId": $scope.approve.businessId,
                        "user": $scope.$parent.$parent.credentials
                    };

                    $http({
                        method: 'post',
                        url: srvUrl + "preInfo/saveFixGroupOption.do",
                        data: $.param({"json": JSON.stringify(json)})
                    }).success(function (result) {
                    });
                }
                var url = srvUrl + "preAudit/auditSingle.do";
                var documentation = $("input[name='bpmnProcessOption']:checked").attr("aaa");

                if (documentation != null && documentation != "") {
                    var docObj = JSON.parse(documentation);
                    if (docObj.preAction) {
                        var preActionArr = docObj.preAction;
                        for (var i in preActionArr) {
                            //validServiceType
                            if (preActionArr[i].callback == 'validServiceType') {
                                var result = $scope.callfunction(preActionArr[i].callback);
                                if (!result.success) {
                                    $.alert(result.result_name);
                                    return;
                                }
                            } else if (preActionArr[i].callback == 'validCheckedFzr') {
                                var result = $scope.callfunction(preActionArr[i].callback);
                                if (!result.success) {
                                    $.alert(result.result_name);
                                    return;
                                } else {
                                    //保存任务人员信息
                                    $scope.$parent.myTaskallocation.businessId = $scope.approve.businessId;
                                    $.ajax({
                                        type: 'post',
                                        url: srvUrl + "preInfo/saveTaskPerson.do",
                                        data: $.param({
                                            "task": JSON.stringify(angular.copy($scope.$parent.myTaskallocation)),
                                        }),
                                        dataType: "json",
                                        async: false,
                                        success: function (result) {
                                            if (!result.success) {
                                                alert(result.result_name);
                                                return;
                                            }
                                        }
                                    });
                                }
                            } else if (preActionArr[i].callback == 'validOpinions') {
                                var result = $scope.callfunction(preActionArr[i].callback);
                                if (!result.success) {
                                    $.alert(result.result_name);
                                    return;
                                } else {
                                    //保存意见信息
                                    $.ajax({
                                        type: 'post',
                                        url: srvUrl + "preInfo/saveServiceTypeOpinion.do",
                                        data: $.param({
                                            "serviceTypeOpinion": JSON.stringify($scope.submitInfo.currentTaskVar),
                                            "businessId": $scope.approve.businessId
                                        }),
                                        dataType: "json",
                                        async: false,
                                        success: function (result) {
                                            if (!result.success) {
                                                alert(result.result_name);
                                                return;
                                            }
                                        }
                                    });
                                }
                            } else if (preActionArr[i].callback == 'validCheckedMajorMember') {
//								var result = $scope.callfunction(preActionArr[i].callback);
//								if(!result.success){
//									$.alert(result.result_name);
//									return;
//								}else{
//									//保存专业评审人员信息
//									$scope.$parent.myTaskallocation.businessId = $scope.approve.businessId;
////									$.ajax({
////										type:'post',
////										url: srvUrl + "preInfo/saveTaskPerson.do",
////										data: $.param({
////											"task":JSON.stringify(angular.copy($scope.$parent.myTaskallocation)),
////										}),
////										dataType: "json",
////							        	async: false,
////										success:function(result){
////											if(!result.success){
////												alert(result.result_name);
////												return;
////											}
////										}
////									});
//								}
                            }
                        }
                    }
                }

                show_Mask();
                $http({
                    method: 'post',
                    url: url,
                    data: $.param({
                        "processKey": $scope.approve.processKey,
                        "businessId": $scope.approve.businessId,
                        "opinion": $scope.flowVariables.opinion,
                        "processOption": $("input[name='bpmnProcessOption']:checked").val()
                    })
                }).success(function (result) {
                    hide_Mask();
                    if ($scope.approve.callbackSuccess != null && result.success) {
                        $scope.approve.callbackSuccess(result);
                    } else if ($scope.approve.callbackFail != null && !result.success) {
                        $scope.approve.callbackFail(result);
                    } else {
                        $.alert(result.result_name);
                    }
                });
            };
            $scope.$watch("approve", $scope.checkMark);
        }
    }
});
// 正式评审流程提交弹出框
ctmApp.directive('formalAssessmentBpmnPopWin', function () {
    return {
        restrict: 'AE',
        templateUrl: 'page/sys/directive/formalAssessmentBpmnPopWin.html',
        replace: 'true',
        scope: {
            approve: '=',
            // 调用父组件操作
            beforeSubmit: "&beforeSubmit"
        },
        controller: function ($scope, $location, $http, Upload) {
            $scope.changeTypeSelected = "";
            $scope.changeTypes = [{key: 'before', value: '前加签'}, {key: 'after', value: '后加签'}];
            //初始化提示信息框
            $("[data-toggle='tooltip']").tooltip();

            //验证任务人员
            $scope.callfunction = function (functionName) {
                var func = eval(functionName);
                //创建函数对象，并调用
                return new func(arguments[1]);
            }
            /**加签参数初始化add by LiPan 2019-03-08**/
            $scope.showSelectPerson = function () {
                $("#submitModal").modal('hide');
                $("#userSinDialog").modal('show');
            };
            $scope.changeWork = function () {
                console.log($scope.approve);
                //人员验证
                if ($scope.$parent.checkedUser.NAME == null || $scope.$parent.checkedUser.NAME == '') {
                    $.alert("请选择目标人员！");
                    return;
                }
                if ($scope.$parent.checkedUser.VALUE == $scope.$parent.$parent.credentials.UUID) {
                    $.alert("不能转办给自己！");
                    return;
                }
                if ($scope.$parent.checkedUser.VALUE == $scope.$parent.curLog.AUDITUSERID) {
                    $.alert("不能转办给最初人员！");
                    return;
                }
                if ($scope.flowVariables == null || $scope.flowVariables.opinion == null || $scope.flowVariables.opinion == "") {
                    $.alert("审批意见不能为空！");
                    return;
                }
                if ($scope.flowVariables.opinion.length > 650) {
                    $.alert("审批意见不能超过650字！");
                    return;
                }
                if ($scope.changeTypeSelected == 'after') {
                    var validate = wf_validateSign('formalReview', $scope.approve.businessId);
                    if (!isEmpty(validate.code)) {
                        $.alert(validate.comment);
                        return;
                    }
                }
                //执行转办操作
                show_Mask();
                $http({
                    method: 'post',
                    url: srvUrl + "sign/doSign.do",
                    data: $.param({
                        'type': $scope.changeTypeSelected,
                        'business_module': 'formalReview',
                        "business_id": $scope.approve.businessId,
                        "user_json": JSON.stringify($scope.$parent.checkedUser),
                        "task_id": $scope.$parent.curLog.TASKID,
                        "option": $scope.flowVariables.opinion
                    })
                }).success(function (result) {
                    hide_Mask();
                    if ($scope.approve.callbackSuccess != null && result.success) {
                        $scope.approve.callbackSuccess(result);
                    } else if ($scope.approve.callbackFail != null && !result.success) {
                        $scope.approve.callbackFail(result);
                    } else {
                        $.alert(result.result_name);
                    }
                });
            };
            $scope.workOver = function () {
                //人员验证
                if ($scope.flowVariables == null || $scope.flowVariables.opinion == null || $scope.flowVariables.opinion == "") {
                    $.alert("审批意见不能为空！");
                    return;
                }
                if ($scope.flowVariables.opinion.length > 650) {
                    $.alert("审批意见不能超过650字！");
                    return;
                }
                //执行办结操作
                show_Mask();
                $http({
                    method: 'post',
                    url: srvUrl + "sign/endSign.do",
                    data: $.param({
                        "business_module": "formalReview",
                        "business_id": $scope.approve.businessId,
                        "task_id": $scope.$parent.curLog.TASKID,
                        "option": $scope.flowVariables.opinion
                    })
                }).success(function (result) {
                    hide_Mask();
                    if ($scope.approve.callbackSuccess != null && result.success) {
                        $scope.approve.callbackSuccess(result);
                    } else if ($scope.approve.callbackFail != null && !result.success) {
                        $scope.approve.callbackFail(result);
                    } else {
                        $.alert(result.result_name);
                    }
                });
            };
            /**加签参数初始化add by LiPan 2019-03-08**/

            //判断是否显示toConfirm的打分项
            $scope.showMarkMethod = function (documentation) {

                if (documentation != null && documentation != "") {
                    var docObj = JSON.parse(documentation);
                    if (docObj.mark == "reviewPassMark") {
                        $scope.showReviewToConfirm = true;
                        $scope.showReviewConfirmToEnd = false;
                        $scope.showMark = true;
                    }
                    if (docObj.mark == "legalPassMark") {
                        $scope.showMark = true;
                        $scope.showLegalToConfirm = true;

                    }
                    if (docObj.mark == "toEnd") {
                        $scope.showMark = false;
                        $scope.showLegalToConfirm = false;
                        $scope.showReviewToConfirm = false;
                        $scope.showReviewConfirmToEnd = true;
                    }
                } else {
//					$scope.showMark = false;
                    $scope.showLegalToConfirm = false;
                    $scope.showReviewToConfirm = false;
                    $scope.showReviewConfirmToEnd = false;
                }
            }

            $scope.checkMark = function () {
                if ($scope.approve == null) {
                    return;
                }
                var processOptions = $scope.approve.processOptions;

                if (processOptions[0].documentation != null && processOptions[0].documentation != '') {
                    var docObj = JSON.parse(processOptions[0].documentation);

                    if (docObj.mark == "reviewPassMark") {
                        $scope.showReviewToConfirm = true;
                    }
                    if (docObj.mark == "legalPassMark") {
                        $scope.showLegalToConfirm = true;
                    }
                }

                //流程选项
                for (var i in processOptions) {
                    var documentation = processOptions[i].documentation;
                    if (documentation != null && documentation != "") {
                        var docObj = JSON.parse(documentation);
                        if (docObj.mark == "toEnd") {
                            $scope.newEndOption = true;
                        }
                        if (docObj.mark == "reviewPassMark" || docObj.mark == "legalPassMark") {
//							$scope.showMark = true;
                            if (i == 0) {
                                $scope.showMark = true;
                            }
                            //查询后台的评价记录
                            $.ajax({
                                type: 'post',
                                url: srvUrl + "formalMark/queryMarks.do",
                                data: $.param({"businessId": $scope.approve.businessId}),
                                dataType: "json",
                                async: false,
                                success: function (result) {
                                    if (result.success) {
                                        if (result.result_data == null) {
                                            if (docObj.mark == "reviewPassMark") {
                                                $scope.showReviewFirstMark = true;
                                                $scope.showMark = true;
                                            }
                                            if (docObj.mark == "legalPassMark") {
                                                $scope.showLegalFirstMark = true;
                                                $scope.showMark = true;
                                            }
                                        } else {
                                            if (docObj.mark == "reviewPassMark") {
                                                if (result.result_data.REVIEWFILEACCURACY == null || result.result_data.REVIEWFILEACCURACY == "" || result.result_data.FLOWMARK == null || result.result_data.FLOWMARK == '') {
                                                    $scope.showReviewFirstMark = true;
                                                }
                                            }
                                            if (docObj.mark == "legalPassMark") {
                                                if (result.result_data.LEGALFILEACCURACY == null || result.result_data.LEGALFILEACCURACY == "") {
                                                    $scope.showLegalFirstMark = true;
                                                }
                                            }
                                        }
                                    } else {
                                        alert(result.result_name);
                                        return;
                                    }
                                }
                            });
                        }
                    }
                }
            }
            var validServiceType = function () {
                var result = {success: true, result_name: ""};

                if ($scope.$parent.$parent.pfrOracle.SERVICETYPE_ID != '1401' && $scope.$parent.$parent.pfrOracle.SERVICETYPE_ID != '1402') {
                    result.success = false;
                    result.result_name = "此项目非传统水务、水环境项目！无法选择该选项	！";
                }
                return result;
            };
            var validCheckedFzr = function () {
                var result = {success: true, result_name: ""};

                if ($scope.$parent.$parent.myTaskallocation == null || $scope.$parent.$parent.myTaskallocation == "") {
                    result.success = false;
                    result.result_name = "请分配负责人！";
                }
                if ($scope.approve.showController.isSignLegal == null || $scope.approve.showController.isSignLegal == '') {
                    if ($scope.$parent.$parent.myTaskallocation.legalReviewLeader.NAME == null || $scope.$parent.$parent.myTaskallocation.legalReviewLeader.NAME == "") {
                        result.success = false;
                        result.result_name = "请选择法律评审负责人！";
                    }
                }
                if ($scope.$parent.$parent.myTaskallocation.reviewLeader.NAME == null || $scope.$parent.$parent.myTaskallocation.reviewLeader.NAME == "") {
                    result.success = false;
                    result.result_name = "请选择评审负责人！";
                }
                return result;
            };
            var validCheckedFLFzr = function () {
                var result = {success: true, result_name: ""};

                if ($scope.$parent.$parent.myTaskallocation == null || $scope.$parent.$parent.myTaskallocation == "") {
                    result.success = false;
                    result.result_name = "请分配负责人！";
                }
                if ($scope.$parent.$parent.myTaskallocation.legalReviewLeader.NAME == null || $scope.$parent.$parent.myTaskallocation.legalReviewLeader.NAME == "") {
                    result.success = false;
                    result.result_name = "请选择法律评审负责人！";
                }
                return result;
            };

            var validCheckedMajor = function () {
                var result = {success: true, result_name: ""};

                if ($scope.$parent.$parent.myTaskallocation.professionalReviewers == null || $scope.$parent.$parent.myTaskallocation.professionalReviewers == "") {
                    result.success = false;
                    result.result_name = "请选择专业评审人员！";
                }
                return result;
            };
            $scope.submitInfo = {};
            $scope.submitInfo.currentTaskVar = {};
            $scope.submitNext = function () {
                if(!isEmpty($scope.beforeSubmit)){
                    if(typeof $scope.beforeSubmit == 'function'){
                        if(!$scope.beforeSubmit()){
                            $('#submitModal').modal('hide');
                            $("#submibtnn").hide();
                            return;
                        }
                    }
                }
                /********Add By LiPan
                 * 此处发现选择了"加签"单选以后,
                 * $scope.showReviewToConfirm的值依然是true
                 * 加签操作不起作用,所以加上了该段代码
                 * ********/
                if ($("input[name='bpmnProcessOption']:checked").val() == 'CHANGE') {
                    $scope.showReviewToConfirm = false;
                    $scope.showLegalToConfirm = false;
                }
                if ($("input[name='bpmnProcessOption']:checked").val() == 'WORKOVER') {
                    $scope.showReviewToConfirm = false;
                    $scope.showLegalToConfirm = false;
                }
                
                if ("submit" == $scope.approve.operateType) {
                    $scope.submit();
                } else if ("audit" == $scope.approve.operateType) {
                    if ($scope.showReviewConfirmToEnd) {
                        $.confirm("您选择了终止项目，意味着您将废弃此项目！是否确认？", function () {
                            $scope.auditSingle();
                        });
                    } else if ($scope.showLegalToConfirm) {
                        $.confirm("您选择了评审负责人确认，意味着您已经和基层法务沟通完毕，流程将进入下一环节！是否确认？", function () {
                            $scope.auditSingle();
                        });
                    } else if ($scope.showReviewToConfirm) {
                        $.confirm("您选择了评审负责人确认选项，意味着您已经和投资经理沟通完毕，流程将进入下一环节！是否确认？", function () {
                            $scope.auditSingle();
                        });
                    } else if ($("input[name='bpmnProcessOption']:checked").val() == 'CHANGE') {
                        $scope.changeWork();
                    } else if ($("input[name='bpmnProcessOption']:checked").val() == 'WORKOVER') {
                        $scope.workOver();
                    } else {
                        $scope.auditSingle();
                    }
                } else if ("change" == $scope.approve.operateType) {
                    if ($("input[name='bpmnProcessOption']:checked").val() == 'CHANGE') {
                        $scope.changeWork();
                    } else if ($("input[name='bpmnProcessOption']:checked").val() == 'WORKOVER') {
                        $scope.workOver();
                    }
                } else {
                    $.alert("操作状态不明确！");
                }
            };
            $scope.submit = function () {
                var url = srvUrl + "formalAssessmentAudit/startSingleFlow.do";
                show_Mask();
                $http({
                    method: 'post',
                    url: url,
                    data: $.param({
                        "processKey": $scope.approve.processKey,
                        "businessId": $scope.approve.businessId
                    })
                }).success(function (result) {
                    hide_Mask();
                    if ($scope.approve.callbackSuccess != null && result.success) {
                        $scope.approve.callbackSuccess(result);
                        $location.path("/FormalAssessmentInfoList/1");
                    } else if ($scope.approve.callbackFail != null && !result.success) {
                        $scope.approve.callbackFail(result);
                    } else {
                        $.alert(result.result_name);

                    }
                });
            };

            //jquery判断是否对象非空
            function isEmptyObject(e) {
                var t;
                for (t in e)
                    return !1;
                return !0
            }

            $(".mark").keyup(function () {
                if (this.value.length == 1) {
                    this.value = this.value.replace(/[^0-9]/g, '');
                } else {
                    this.value = this.value.replace(/\D/g, '')
                }
                if (this.value > this.attributes.max.value * 1) {
                    this.value = null;
                }
            });

            $scope.auditSingle = function () {
                //打分项验证-start
                if ($scope.showMark && ($scope.showReviewFirstMark || $scope.showReviewToConfirm || $scope.showLegalToConfirm)) {
                    if (!$scope.mark) {
                        $.alert("请评分！");
                        return;
                    }
                    if ($scope.showReviewFirstMark) {
                        if ($scope.mark.flowMark == null) {
                            $.alert("请对审批流程熟悉度评分！");
                            return;
                        }
                        if ($scope.mark.moneyCalculate == null) {
                            $.alert("请对核心财务测算能力评分！");
                            return;
                        }
                        if ($scope.mark.reviewFileAccuracy == null) {
                            $.alert("请对资料的准确性评分！");
                            return;
                        }
                        if ($scope.mark.planDesign == null) {
                            $.alert("请对核心的方案设计能力评分！");
                            return;
                        }
                    }
                    if ($scope.showLegalFirstMark) {
                    }
                    if ($scope.showReviewToConfirm) {
                        if ($scope.mark.fileContent == null) {
                            $.alert("请对资料的完整性评分！");
                            return;
                        }
                        if ($scope.mark.fileTime == null) {
                            $.alert("请对资料的及时性评分！");
                            return;
                        }
                        if ($scope.mark.riskControl == null) {
                            $.alert("请对核心风险识别及规避能力评分！");
                            return;
                        }

                    }
                    if ($scope.showLegalToConfirm) {
                        if ($scope.mark.legalFileAccuracy == null) {
                            $.alert("请对资料的准确性评分！");
                            return;
                        }
                        if ($scope.mark.talks == null) {
                            $.alert("请对核心的协议谈判能力评分！");
                            return;
                        }
                    }

                    //存分
                    $.ajax({
                        type: 'post',
                        url: srvUrl + "formalMark/saveOrUpdate.do",
                        data: $.param({
                            "json": JSON.stringify($scope.mark),
                            "businessId": $scope.approve.businessId
                        }),
                        dataType: "json",
                        async: false,
                        success: function (result) {
                            if (!result.success) {
                                alert(result.result_name);
                                return;
                            }
                        }
                    });

                }
                //打分项验证-end

                if ($scope.approve.showController.isServiewType) {
                    if ($scope.submitInfo.currentTaskVar == null || $scope.submitInfo.currentTaskVar.cesuanFileOpinion == null || $scope.submitInfo.currentTaskVar.cesuanFileOpinion == "") {
                        $.alert("测算文件意见不能为空！");
                        return;
                    }
                    if ($scope.submitInfo.currentTaskVar == null || $scope.submitInfo.currentTaskVar.tzProtocolOpinion == null || $scope.submitInfo.currentTaskVar.tzProtocolOpinion == "") {
                        $.alert("投资协议意见不能为空！");
                        return;
                    }
                    else {
                        //保存意见到mongo
                        $http({
                            method: 'post',
                            url: srvUrl + "formalAssessmentInfo/updateServiceTypeOpinion.do",
                            data: $.param({
                                "serviceTypeOpinion": JSON.stringify($scope.submitInfo.currentTaskVar),
                                "businessId": $scope.approve.businessId
                            })
                        }).success(function (result) {

                        });
                    }
                }
                if ($scope.flowVariables == null || $scope.flowVariables == 'undefined' || $scope.flowVariables.opinion == undefined || $scope.flowVariables.opinion == null || $scope.flowVariables.opinion == "") {
                    $.alert("审批意见不能为空！");
                    return;
                }
                if ($scope.flowVariables.opinion.length > 650) {
                    $.alert("审批意见不能超过650字！");
                    return;
                }
                if ($scope.approve.showController.isGroupMember) {
                    //保存小组成员意见到mongo

                    var json = {
                        "opinion": $scope.flowVariables.opinion,
                        "businessId": $scope.approve.businessId,
                        "user": $scope.$parent.$parent.credentials
                    };

                    $http({
                        method: 'post',
                        url: srvUrl + "formalAssessmentInfo/saveFixGroupOption.do",
                        data: $.param({"json": JSON.stringify(json)})
                    }).success(function (result) {
                    });
                }
                var url = srvUrl + "formalAssessmentAudit/auditSingle.do";
                var documentation = $("input[name='bpmnProcessOption']:checked").attr("aaa");

                if (documentation != null && documentation != "") {
                    var docObj = JSON.parse(documentation);
                    if (docObj.preAction) {
                        var preActionArr = docObj.preAction;
                        for (var i in preActionArr) {
                            if (preActionArr[i].callback == 'validServiceType') {
                                var result = $scope.callfunction(preActionArr[i].callback);
                                if (!result.success) {
                                    $.alert(result.result_name);
                                    return;
                                }
                            } else if (preActionArr[i].callback == 'validCheckedFzr') {
                                var result = $scope.callfunction(preActionArr[i].callback);
                                if (!result.success) {
                                    $.alert(result.result_name);
                                    return;
                                } else {
                                    //保存任务人员信息
                                    $scope.$parent.$parent.myTaskallocation.businessId = $scope.approve.businessId;
                                    $.ajax({
                                        type: 'post',
                                        url: srvUrl + "formalAssessmentAudit/saveTaskToMongo.do",
                                        data: $.param({"task": JSON.stringify($scope.$parent.$parent.myTaskallocation)}),
                                        dataType: "json",
                                        async: false,
                                        success: function (result) {
                                            if (!result.success) {
                                                alert(result.result_name);
                                                return;
                                            }
                                        }
                                    });
                                }
                            } else if (preActionArr[i].callback == 'validCheckedMajor') {

                                var result = $scope.callfunction(preActionArr[i].callback);
                                if (!result.success) {
                                    $.alert(result.result_name);
                                    return;
                                } else {
                                    //保存专业评审人员信息
                                    if ($scope.approve.showController.isTask && $scope.$parent.$parent.professionalReviewers.NAME == null || $scope.$parent.$parent.myTaskallocation.professionalReviewers.NAME == "") {
                                        $.alert("请选择专业评审人员！");
                                        return;
                                    }
                                    $scope.$parent.$parent.myTaskallocation.businessId = $scope.approve.businessId;
                                    $.ajax({
                                        type: 'post',
                                        url: srvUrl + "formalAssessmentAudit/saveTaskToMongo.do",
                                        data: $.param({"task": JSON.stringify($scope.$parent.$parent.myTaskallocation)}),
                                        dataType: "json",
                                        async: false,
                                        success: function (result) {
                                            if (!result.success) {
                                                alert(result.result_name);
                                                return;
                                            }
                                        }
                                    });
                                }
                            } else if (preActionArr[i].callback == 'validCheckedFLFzr') {

                                var result = $scope.callfunction(preActionArr[i].callback);
                                if (!result.success) {
                                    $.alert(result.result_name);
                                    return;
                                } else {
                                    //保存专业评审人员信息
                                    $scope.$parent.$parent.myTaskallocation.businessId = $scope.approve.businessId;
                                    $.ajax({
                                        type: 'post',
                                        url: srvUrl + "formalAssessmentAudit/saveTaskToMongo.do",
                                        data: $.param({"task": JSON.stringify($scope.$parent.$parent.myTaskallocation)}),
                                        dataType: "json",
                                        async: false,
                                        success: function (result) {
                                            if (!result.success) {
                                                alert(result.result_name);
                                                return;
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    }
                }

                show_Mask();
                $http({
                    method: 'post',
                    url: url,
                    data: $.param({
                        "processKey": $scope.approve.processKey,
                        "businessId": $scope.approve.businessId,
                        "opinion": $scope.flowVariables.opinion,
                        "processOption": $("input[name='bpmnProcessOption']:checked").val()
                    })
                }).success(function (result) {
                    hide_Mask();
                    if ($scope.approve.callbackSuccess != null && result.success) {
                        $scope.approve.callbackSuccess(result);
                    } else if ($scope.approve.callbackFail != null && !result.success) {
                        $scope.approve.callbackFail(result);
                    } else {
                        $.alert(result.result_name);
                    }
                });
            };
            $scope.$watch("approve", $scope.checkMark);

        }
    }
});
// 其他评审流程弹出框
ctmApp.directive('bpmnPopWin', function () {
    return {
        restrict: 'AE',
        templateUrl: 'page/sys/directive/bpmnPopWin.html',
        replace: 'true',
        scope: {approve: '='},
        controller: function ($scope, $location, $http) {
            $scope.changeTypeSelected = "";
            $scope.changeTypes = [{key: 'before', value: '前加签'}, {key: 'after', value: '后加签'}];
            $scope.callfunction = function (functionName) {
                var func = eval(functionName);
                //创建函数对象，并调用
                return new func(arguments[1]);
            }

            var validCheckedFzr = function () {
                var result = {success: true, result_name: ""};

                if ($scope.$parent.myTaskallocation == null || $scope.$parent.myTaskallocation == "") {
                    result.success = false;
                    result.result_name = "请分配负责人！";
                }
                if ($scope.$parent.myTaskallocation.reviewLeader.NAME == null || $scope.$parent.myTaskallocation.reviewLeader.NAME == "") {
                    result.success = false;
                    result.result_name = "请选择评审负责人！";
                }
                return result;
            };

            var validCheckedRiskFzr = function () {

                var result = {success: true, result_name: ""};

                if ($scope.$parent.myTaskallocation == null || $scope.$parent.myTaskallocation == "") {
                    result.success = false;
                    result.result_name = "请分配负责人！";
                }
                if ($scope.$parent.myTaskallocation.riskLeader.NAME == null || $scope.$parent.myTaskallocation.riskLeader.NAME == "") {
                    result.success = false;
                    result.result_name = "请选择风控负责人！";
                }
                return result;
            };
            $scope.showSelectPerson = function () {
                $("#submitModal").modal('hide');
                $("#userSinDialog").modal('show');
            }
            $scope.submitNext = function () {
                /*if ($("#workOver").attr("checked")) {*/
                if ($("input[name='bpmnProcessOption']:checked").val() == 'WORKOVER') {
                    $scope.workOver();
                } /*else if ($("input[name='bpmnProcessOption']#change:checked").length > 0) {*/
                else if ($("input[name='bpmnProcessOption']:checked").val() == 'CHANGE') {
                    var changeTypeSelected = $scope.changeTypeSelected;
                    if (changeTypeSelected == null || changeTypeSelected == '' || changeTypeSelected == "") {
                        $.alert("请选择加签类型！");
                    } else {
                        $scope.changeWork();
                    }
                } else if ("submit" == $scope.approve.operateType) {
                    $scope.submit();
                } else if ("audit" == $scope.approve.operateType) {
                    $scope.auditSingle();
                } else {
                    $.alert("操作状态不明确！");
                }
            };

            $scope.workOver = function () {
                //人员验证
                if ($scope.flowVariables == null || $scope.flowVariables.opinion == null || $scope.flowVariables.opinion == "") {
                    $.alert("审批意见不能为空！");
                    return;
                }
                if ($scope.flowVariables.opinion.length > 650) {
                    $.alert("审批意见不能超过650字！");
                    return;
                }

                //执行办结操作
                show_Mask();
                $http({
                    method: 'post',
                    url: srvUrl + "sign/endSign.do",
                    data: $.param({
                        "business_module": "bulletin",
                        "business_id": $scope.approve.businessId,
                        "task_id": $scope.$parent.curLog.TASKID,
                        "option": $scope.flowVariables.opinion
                    })
                }).success(function (result) {
                    hide_Mask();
                    if ($scope.approve.callbackSuccess != null && result.success) {
                        $scope.approve.callbackSuccess(result);
                    } else if ($scope.approve.callbackFail != null && !result.success) {
                        $scope.approve.callbackFail(result);
                    } else {
                        $.alert(result.result_name);
                    }
                });
            }

            $scope.changeWork = function () {
                //人员验证
                if ($scope.$parent.checkedUser.NAME == null || $scope.$parent.checkedUser.NAME == '') {
                    $.alert("请选择目标人员！");
                    return;
                }
                if ($scope.$parent.checkedUser.VALUE == $scope.$parent.$parent.credentials.UUID) {
                    $.alert("不能转办给自己！");
                    return;
                }
                if ($scope.$parent.checkedUser.VALUE == $scope.$parent.curLog.AUDITUSERID) {
                    $.alert("不能转办给最初人员！");
                    return;
                }
                if ($scope.flowVariables == null || $scope.flowVariables.opinion == null || $scope.flowVariables.opinion == "") {
                    $.alert("审批意见不能为空！");
                    return;
                }
                if ($scope.flowVariables.opinion.length > 650) {
                    $.alert("审批意见不能超过650字！");
                    return;
                }
                if ($scope.changeTypeSelected == 'after') {
                    var validate = wf_validateSign('bulletin', $scope.approve.businessId);
                    if (!isEmpty(validate.code)) {
                        $.alert(validate.comment);
                        return;
                    }
                }
                //执行转办操作
                show_Mask();
                $http({
                    method: 'post',
                    url: srvUrl + "sign/doSign.do",
                    data: $.param({
                        'type': $scope.changeTypeSelected,
                        'business_module': 'bulletin',
                        "business_id": $scope.approve.businessId,
                        "user_json": JSON.stringify($scope.$parent.checkedUser),
                        "task_id": $scope.$parent.curLog.TASKID,
                        "option": $scope.flowVariables.opinion
                    })
                }).success(function (result) {
                    hide_Mask();
                    if ($scope.approve.callbackSuccess != null && result.success) {
                        $scope.approve.callbackSuccess(result);
                    } else if ($scope.approve.callbackFail != null && !result.success) {
                        $scope.approve.callbackFail(result);
                    } else {
                        $.alert(result.result_name);
                    }
                });


            }
            $scope.submit = function () {
                var url = srvUrl + "bulletinAudit/startSingleFlow.do";
                show_Mask();
                $http({
                    method: 'post',
                    url: url,
                    data: $.param({
                        "processKey": $scope.approve.processKey,
                        "businessId": $scope.approve.businessId
                    })
                }).success(function (result) {
                    hide_Mask();
                    if ($scope.approve.callbackSuccess != null && result.success) {
                        $scope.approve.callbackSuccess(result);
                    } else if ($scope.approve.callbackFail != null && !result.success) {
                        $scope.approve.callbackFail(result);
                    } else {
                        $.alert(result.result_name);
                    }
                });
            };
            $scope.auditSingle = function () {
                if ($scope.flowVariables == null || $scope.flowVariables.opinion == null || $scope.flowVariables.opinion == "") {
                    $.alert("审批意见不能为空！");
                    return;
                }
                if ($scope.flowVariables.opinion.length > 650) {
                    $.alert("审批意见不能超过650字！");
                    return;
                }

                var url = srvUrl + "bulletinAudit/auditSingle.do";
                var documentation = $("input[name='bpmnProcessOption']:checked").attr("aaa");

                if (documentation != null && documentation != "") {
                    var docObj = JSON.parse(documentation);
                    if (docObj.preAction) {
                        var preActionArr = docObj.preAction;
                        for (var i in preActionArr) {
                            if (preActionArr[i].callback == 'validCheckedFzr') {
                                var result = $scope.callfunction(preActionArr[i].callback);
                                if (!result.success) {
                                    $.alert(result.result_name);
                                    return;
                                } else {
                                    //保存任务人员信息
                                    $.ajax({
                                        type: 'post',
                                        url: srvUrl + "bulletinInfo/saveTaskPerson.do",
                                        data: $.param({
                                            "json": JSON.stringify(angular.copy($scope.$parent.myTaskallocation)),
                                            "businessId": $scope.approve.businessId
                                        }),
                                        dataType: "json",
                                        async: false,
                                        success: function (result) {
                                            if (!result.success) {
                                                alert(result.result_name);
                                                return;
                                            }
                                        }
                                    });
                                }
                            } else if (preActionArr[i].callback == 'validCheckedRiskFzr') {
                                var result = $scope.callfunction(preActionArr[i].callback);
                                if (!result.success) {
                                    $.alert(result.result_name);
                                    return;
                                } else {
                                    $.ajax({
                                        type: 'post',
                                        url: srvUrl + "bulletinInfo/saveTaskPerson.do",
                                        data: $.param({
                                            "json": JSON.stringify($scope.$parent.myTaskallocation),
                                            "businessId": $scope.approve.businessId
                                        }),
                                        dataType: "json",
                                        async: false,
                                        success: function (result) {
                                            if (!result.success) {
                                                alert(result.result_name);
                                                return;
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    }
                }

                show_Mask();
                $http({
                    method: 'post',
                    url: url,
                    data: $.param({
                        "processKey": $scope.approve.processKey,
                        "businessId": $scope.approve.businessId,
                        "opinion": $scope.flowVariables.opinion,
                        "processOption": $("input[name='bpmnProcessOption']:checked").val()
                    })
                }).success(function (result) {
                    hide_Mask();
                    if ($scope.approve.callbackSuccess != null && result.success) {
                        $scope.approve.callbackSuccess(result);
                    } else if ($scope.approve.callbackFail != null && !result.success) {
                        $scope.approve.callbackFail(result);
                    } else {
                        $.alert(result.result_name);
                    }
                });
            };
        }
    }
});
/*******************************************************流程指令结束***************************************************/

// 其他评审流程弹出框
ctmApp.directive('fillMaterial', ['$filter', function ($filter) {
    return {
        restrict: 'AE',
        templateUrl: 'page/sys/directive/directFillMaterialPage.html',
        replace: 'true',
        controller: function ($scope, $location, $http) {

            // $scope.paginationConf

            $scope.initData = function () {
                if($scope.paginationConf.queryObj == null || $scope.paginationConf.queryObj == ''){
                    $scope.paginationConf.queryObj = {};
                }
                $scope.paginationConf.queryObj.userId = $scope.credentials.UUID;
                $scope.paginationConf.itemsPerPage = 5;
                $http({
                    method:'post',
                    url: srvUrl + "fillMaterials/queryNoSubmitList.do",
                    data: $.param({
                        "page":JSON.stringify($scope.paginationConf),
                        "json":JSON.stringify($scope.paginationConf.queryObj)
                    })
                }).success(function(result){
                    $scope.noSubmitList = result.result_data.list;
                });
            };
            $scope.initData();
            $scope.r={};
            //新建评审报告
            $scope.openRFIReport = function (noSubmit) {
                $scope.toCreateReport = noSubmit;
                $scope.r.pmodel="FormalReviewReport/Create";
            };
            $scope.y = {};
            $scope.openRFIBiddingInfo = function (noSubmit) {
                $scope.toCreateBiddingInfo = noSubmit;
                $scope.y.pmodel="normal";
            };

            //编辑评审报告
            $scope.createRFIReport = function(model,uuid){
                var ind = model.lastIndexOf("/");
                var modelAction = model.substring(ind + 1,model.length);
                if(modelAction == 'Update'){
                    $.alert("请选择项目模式!");
                    return false;
                }else if(uuid == null || uuid == ""){
                    $.alert("请选择一个项目!");
                    return false;
                }else{
                    var routePath = model.substring(0,ind);
                    $('#addModal').modal('hide');
                    $location.path("/"+routePath+"/0/Create/"+uuid+"@2/"+$filter('encodeURI')('#/IndividualTable'));
                }
            }

            //编辑正是评审提交决策会材料
            $scope.createRFIBiddingInfo = function(model,uuid){
                console.log(model);
                console.log(uuid);
                var ind = model.lastIndexOf("/");
                var modelAction = model.substring(ind + 1,model.length);
                var routePath = model.substring(0,ind);
                $('#addModal4').modal('hide');
                if (model == 'normal') {
                    $location.path("/FormalBiddingInfo/"+uuid+"@2/"+$filter('encodeURI')('#/IndividualTable') + '/0');
                } else {
                    $location.path("/"+model+""+uuid+"@2/"+$filter('encodeURI')('#/IndividualTable'));
                }
            }

            /**
             * 查询项目部分信息，查看是新项目还是老项目，决定跳转路径
             * */
            $scope.getInfo = function (id) {
                $http({
                    method: 'post',
                    url: srvUrl + "formalReport/findFormalAndReport.do",
                    data: $.param({"projectFormalId": id})
                }).success(function (data) {
                    $scope.projectSummary = data.result_data.summary;
                    $scope.stage = data.result_data.stage;

                    console.log($scope.stage)
                    var path = $filter('encodeURI')('#/IndividualTable');
                    /*if ($scope.projectSummary == null || $scope.projectSummary == undefined){
                        $location.path("/FormalBiddingInfo_view/"+ id + "/" + path);
                    } else {
                        $location.path("/FormalBiddingInfoPreview/"+ id + "/" + path + "/3");
                    }*/
                    // #/FormalBiddingInfo/5afcc2e6ddd03412cebef6e5@2/JTI1MjMlMkZGb3JtYWxCaWRkaW5nSW5mb0xpc3QlMkYw/0
                    if ($scope.projectSummary == null || $scope.projectSummary == undefined){
                        $location.path("/FormalBiddingInfo_view/"+ id + "/" + path);
                    } else if ($scope.stage == 7) {
                        $location.path("/FormalBiddingInfoPreview/"+ id + "/" + path + "/3");
                    } else {
                        $location.path("/FormalBiddingInfo/"+ id + "/" + path + "/3");
                    }
                }).error(function (data, status, header, config) {
                    $.alert(status);
                });
            }

            $scope.x = {};
            $scope.openPREReport = function (noSubmit) {
                $scope.preProjectName = noSubmit;
                $scope.x.pmodel="normal";
            };

            //新建投资评审报告
            $scope.forPreReport = function (model, uuid, comId) {
                if (model == null || model == "") {
                    $.alert("请选择项目模式!");
                    return false;
                } else if (uuid == null || uuid == "") {
                    $.alert("请选择项目!");
                    return false;
                } else {
                    $("#addModal2").modal('hide');
                    var routePath = "";
                    if (model == "normal") {
                        routePath = "PreNormalReport";
                    }

                    if (model == "other") {
                        routePath = "PreOtherReport";
                    }
                    $location.path("/" + routePath + "/" + model + "/Create/" + uuid + "/" + $filter('encodeURI')('#/IndividualTable'));
                }
            }

            $scope.openRBIMeeting = function (noSubmit) {
                $scope.businessId = noSubmit.BUSINESSID;
            }
            $scope.mettingSummary = "";
            $scope.mettingSubmit = function () {
                if ($scope.mettingSummary == null || $scope.mettingSummary == "") {
                    $.alert("会议纪要不得为空！");
                    return false;
                }
                //show_Mask();
                //保存附件到mongo
                $http({
                    method: 'post',
                    url: srvUrl + "bulletinInfo/saveMettingSummary.do",
                    data: $.param({
                        "businessId": $scope.businessId,
                        "mettingSummaryInfo": $scope.mettingSummary
                    })
                }).success(function (result) {
                    $('#submitModal').modal('hide');
                    $.alert(result.result_name);
                    $scope.$parent.initDefaultData();
                    $scope.mettingSummary = "";
                });
            };
            $scope.cancel = function () {
                $scope.mettingSummary = "";
            }
        }
    }
}]);
/*云文件指令*/
/*云文件指令*/
ctmApp.directive('cloudFile', function () {
    return {
        restrict: 'AE',
        templateUrl: 'page/sys/common/cloud-file.html',
        replace: 'true',
        scope:{
            fileId: "@",// 组件ID
            fileType:"@",// 文件类别
            fileCode:"@",// 文件Code
            fileLocation:"@",// 文件位置
            showUpload:"@",// 是否展示上传按钮，默认为true
            showPreview:'@',// 是否展示预览按钮，默认为false
            showReplace:'@',// 是否展示替换按钮，默认为false
            showDownload:'@',// 是否展示下载按钮，默认为true
            showDelete:'@',// 是否展示删除按钮，默认true
            uploadText:'@',// 上传按钮本本，默认：选择
            downloadText:'@',//下载按钮文本，默认：下载
            previewText:'@',// 预览按钮文本，默认：预览
            deleteText:'@',// 删除按钮文本，默认：删除
            replaceText:'@',// 替换按钮文本，默认：替换
            fileCheck:'@',// 是否进行文件名校验，默认false
            areaCode:'@', // 附件操作区域选择
            textBefore:'@',// 是否将文件名置于前面，其它操作按钮置于后面，默认true。false：操作按钮在前，文件名在后
            btnClass:'@',// 按钮类
            btnStyle:'@',// 按钮样式
            textStyle:'@',// 文本类
            textClass:'@',// 文本样式
            btnAreaStyle:'@',// 按钮域类
            btnAreaClass:'@'// 按钮域样式
        },
        controller: function ($scope, $location, $http, Upload, $window) {
            // 样式与类初始化
            if(isEmpty($scope.btnClass)){
                $scope._cloud_btn_class_ = 'btn-add btn-scale';
            }else{
                $scope._cloud_btn_class_ = $scope.btnClass;
            }
            if(isEmpty($scope.btnStyle)){
                $scope._cloud_btn_style_ = ';';
            }else{
                $scope._cloud_btn_style_ = $scope.btnStyle;
            }
            if(isEmpty($scope.textStyle)){
                $scope._cloud_text_style_ = 'color: blue;';
            }else{
                $scope._cloud_text_style_ = $scope.textStyle;
            }
            if(isEmpty($scope.textClass)){
                $scope._cloud_text_class_ = '';
            }else{
                $scope._cloud_text_class_ = $scope.textClass;
            }
            if(isEmpty($scope.btnAreaStyle)){
                $scope._cloud_btn_area_style_ = '';
            }else{
                $scope._cloud_btn_area_style_ = $scope.btnAreaStyle;
            }
            if(isEmpty($scope.btnAreaClass)){
                $scope._cloud_btn_area_class_ = '';
            }else{
                $scope._cloud_btn_area_class_ = $scope.btnAreaClass;
            }

            // 其他初始化
            if(isEmpty($scope.textBefore)){
                $scope._cloud_text_before_ = true;
            }else{
                $scope._cloud_text_before_ = $scope.textBefore == 'true';
            }
            if(isEmpty($scope.areaCode)){
                $scope._cloud_area_code_ = '1';
            }else{
                $scope._cloud_area_code_ = $scope.areaCode;
            }
            // 文件校验初始化
            if(isEmpty($scope.fileCheck)){
                $scope._let_file_check_ = false;
            }else{
                $scope._let_file_check_ = $scope.fileCheck == 'true';
            }
            // 按钮名称初始化
            if(isEmpty($scope.uploadText)){
                $scope._cloud_upload_text_ = '选择';
            }else{
                $scope._cloud_upload_text_ = $scope.uploadText;
            }
            if(isEmpty($scope.downloadText)){
                $scope._cloud_download_text_ = '下载';
            }else{
                $scope._cloud_download_text_ = $scope.downloadText;
            }
            if(isEmpty($scope.deleteText)){
                $scope._cloud_delete_text_ = '删除';
            }else{
                $scope._cloud_delete_text_ = $scope.deleteText;
            }
            if(isEmpty($scope.previewText)){
                $scope._cloud_preview_text_ = '预览';
            }else{
                $scope._cloud_preview_text_ = $scope.previewText;
            }
            if(isEmpty($scope.replaceText)){
                $scope._cloud_replace_text_ = '替换';
            }else{
                $scope._cloud_replace_text_ = $scope.replaceText;
            }
            // 按钮显示与否初始化
            if(isEmpty($scope.showUpload)){
                $scope._cloud_show_upload_ = true;
            }else{
                $scope._cloud_show_upload_ = $scope.showUpload == 'true';
            }
            if(isEmpty($scope.showDownload)){
                $scope._cloud_show_download_ = true;
            }else{
                $scope._cloud_show_download_ = $scope.showDownload == 'true';
            }
            if(isEmpty($scope.showPreview)){
                $scope._cloud_show_preview_ = false;
            }else{
                $scope._cloud_show_preview_ = $scope.showPreview == 'true';
            }
            if(isEmpty($scope.showReplace)){
                $scope._cloud_show_replace_ = false;
            }else{
                $scope._cloud_show_replace_ = $scope.showReplace == 'true';
            }
            if(isEmpty($scope.showDelete)){
                $scope._cloud_show_delete_ = true;
            }else{
                $scope._cloud_show_delete_ = $scope.showDelete == 'true';
            }
            // 云组件root初始化
            $scope._cloud_ = $scope.fileId + '_' + $scope.fileLocation + '_' + $scope._cloud_area_code_;
            // 显示初始化
            $scope._cloud_init_ = function(){
                console.log("组件初始化:" + $scope.fileType + "," + $scope.fileCode + "," + $scope.fileLocation);
                $http({
                    method: 'post',
                    url: srvUrl + 'cloud/list.do',
                    data: $.param({
                        "docType":$scope.fileType,
                        "docCode":$scope.fileCode,
                        "pageLocation":$scope.fileLocation
                    }),
                    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
                }).success(function (_result) {
                    if(!isEmpty(_result)){
                        if(_result.success){
                            var _list_ = _result['result_data'];
                            if(!isEmpty(_list_) && _list_.length > 0){
                                var _cloud_file_dto_ = _list_[0];
                                $('#_cloud_file_ipt_' + $scope._cloud_area_code_ + '_' + $scope._cloud_).val(JSON.stringify(_cloud_file_dto_));
                                $('#_cloud_file_a_' + $scope._cloud_area_code_+ '_' + $scope._cloud_).text(_cloud_file_dto_.filename);
                                console.log(_cloud_file_dto_);
                            }
                        }
                    }
                });
            };
            // 将input中的附件数据转换为json，传递进来一个附件综合ID，页面唯一
            $scope._cloud_ipt_to_json_ = function(_cloud_){
                console.log($scope._cloud_area_code_ );
                var _ipt_val_ = $('#_cloud_file_ipt_'+ $scope._cloud_area_code_ + '_' + _cloud_).val();
                if(isEmpty(_ipt_val_)){
                    return null;
                }else{
                    return JSON.parse(_ipt_val_);
                }
            };
            // 上传和替换共同调用的方法
            $scope._private_cloud_backend_call = function(_file_, _opt_, _data_,_cloud_){
                if (_file_) {
                    // 是否进行文件校验，默认不开启
                    if($scope._let_file_check_){
                        var _file_suffix_arr_ = _file_.name.split('.');
                        var _file_suffix_ = _file_suffix_arr_[_file_suffix_arr_.length - 1];
                        if (_file_suffix_ != "docx" && _file_suffix_ != "xlsx" && _file_suffix_ != "pptx" && _file_suffix_ != "pdf" &&
                            _file_suffix_ != "jpg" && _file_suffix_ != "png" && _file_suffix_ != "gif" && _file_suffix_ != "tif" &&
                            _file_suffix_ != "psd" && _file_suffix_ != "ppts"){
                            $.alert("您上传的文档格式不正确，请重新选择！");
                            return;
                        }
                    }
                    Upload.upload({
                        url: srvUrl + 'cloud/' + _opt_ + '.do',
                        data: _data_
                    }).then(function (_resp_) {
                        var _result = _resp_.data;
                        if(!isEmpty(_result)){
                            if(_result.success){
                                var _cloud_file_dto_ = _result['result_data'];
                                $('#_cloud_file_ipt_' + $scope._cloud_area_code_ + '_' + _cloud_).val(JSON.stringify(_cloud_file_dto_));
                                $('#_cloud_file_a_' + $scope._cloud_area_code_ + '_'  + _cloud_).text(_cloud_file_dto_.filename);
                            }
                        }
                    }, function (_resp_) {
                    }, function (_evt_) {
                        console.log(_evt_);
                        // 进行上传进度的获取和计算
                        var _percent_ = parseInt(100.0 * _evt_.loaded / _evt_.total);
                    });
                }
            };
            // 上传
            $scope._cloud_upload_ = function(_file_,_cloud_){
                var _cloud_file_dto_ =  $scope._cloud_ipt_to_json_(_cloud_);
                if(!isEmpty(_cloud_file_dto_)){
                    return;
                }
                var _data = {file: _file_,"docType":$scope.fileType, "docCode":$scope.fileCode,"pageLocation":$scope.fileLocation};
                $scope._private_cloud_backend_call(_file_,'upload',_data,_cloud_);
            };
            // 下载
            $scope._cloud_download_ = function(_cloud_){
                var _cloud_file_dto_ =  $scope._cloud_ipt_to_json_(_cloud_);
                if(isEmpty(_cloud_file_dto_) || isEmpty(_cloud_file_dto_.download3d)){
                    return;
                }
                $window.open(_cloud_file_dto_.download3d, '_blank', 'menubar=no,toolbar=no, status=no,scrollbars=yes');
            };
            // 删除
            $scope._cloud_delete_ = function(_cloud_){
                var _cloud_file_dto_ =  $scope._cloud_ipt_to_json_(_cloud_);
                if(isEmpty(_cloud_file_dto_) || isEmpty(_cloud_file_dto_.fileid)){
                    return;
                }
                $.confirm('删除该文件吗?', function(){
                    attach_delete(_cloud_file_dto_.fileid);
                    $('#_cloud_file_a_' + _cloud_).text('');
                    $('#_cloud_file_ipt_' + _cloud_).val('');
                });
            };
            // 替换
            $scope._cloud_replace_ = function(_file_, _cloud_){

                var _cloud_file_dto_ =  $scope._cloud_ipt_to_json_(_cloud_);
                if(isEmpty(_cloud_file_dto_) || isEmpty(_cloud_file_dto_.fileid)){
                    return;
                }
                var _data = {file: _file_,"docType":$scope.fileType, "docCode":$scope.fileCode,"pageLocation":$scope.fileLocation,"oldFileId":_cloud_file_dto_.fileid,"reason":"replace"};
                $scope._private_cloud_backend_call(_file_,'replace',_data,_cloud_);
            };
            // 预览
            $scope._cloud_preview_ = function(_cloud_){
                var _cloud_file_dto_ =  $scope._cloud_ipt_to_json_(_cloud_);
                if(isEmpty(_cloud_file_dto_) || isEmpty(_cloud_file_dto_.preview3d)){
                    return;
                }
                $window.open(_cloud_file_dto_.preview3d);
            };
            $scope._cloud_init_();
        }
    }
});

ctmApp.directive('directLeaderDialog', function() {
    return {
        restrict: 'E',
        templateUrl: 'page/sys/directive/directLeaderDialog.html',
        replace: true,
        scope:{
            //必填,该指令所在modal的id，在当前页面唯一
            id: "@",
            //对话框的标题，如果没设置，默认为“人员选择”
            title: "@",
            url: "@",
            //查询参数
            queryParams: "=",
            //默认选中的用户,数组类型，{NAME:'张三',VALUE:'user.uuid'}
            //checkedUser: "=",
            //映射的key，value，{nameField:'username',valueField:'uuid'}，
            //默认为{nameField:'NAME',valueField:'VALUE'}
            mappedKeyValue: "=",
            callback: "="
        },
        controller:function($scope,$http,$element){
            $scope.initData = function(){
                if($scope.queryParams == null){
                    return;
                }
                $scope.queryUser();
            }
            $scope.queryUser = function(){
                var config = {
                    method:'post',
                    url:srvUrl + $scope.url,
                    data:$.param($scope.queryParams)
                };
                $http(config).success(function(data){
                    if(data.success){
                        var users = data.result_data;
                        $scope.users = [];
                        for(var k = 0; k < users.length; k++){
                            $scope.users.push({"VALUE":users[k][$scope.mappedKeyValue.valueField],"NAME":users[k][$scope.mappedKeyValue.nameField]});
                        }
                        //加载候选委员
                        $scope.queryLeaderUser();
                    }else{
                        $.alert(data.result_name);
                    }
                });
            }
            $scope.queryLeaderUser = function(){
                var config = {
                    method:'post',
                    url:srvUrl + 'role/queryRoleuserByCode.do',
                    data:$.param({"code":"DECISION_LEADERS"})
                };
                $http(config).success(function(data){
                    if(data.success){
                        var leaders = data.result_data;
                        for(var k = 0; k < leaders.length; k++){
                            //是否已经存在
                            var flag = false;
                            for(var i = 0; i < $scope.users.length; i++){
                                if(leaders[k]['VALUE'] == $scope.users[i].VALUE){
                                    flag = true;break;
                                }
                            }
                            if(flag){
                                leaders.splice(k, 1);
                                k--;
                            }
                        }
                        $scope.leaders = leaders;
                    }else{
                        $.alert(data.result_name);
                    }
                });
            }
            $scope.cancelSelected = function(){
                $scope.initData();
            }
            $scope.saveSelected = function(){
                var checkboxs = $(".checkbox-inline :checkbox");
                var checkedUser = [];
                $(checkboxs).each(function(i,box){
                    if(box.checked){
                        var user = {};
                        user[$scope.mappedKeyValue.nameField] = box.name;
                        user[$scope.mappedKeyValue.valueField] = box.value;
                        checkedUser.push(user);
                    }
                });
                if($scope.callback != null){
                    $scope.callback(checkedUser);
                }
            }
            $scope.$watch('queryParams', $scope.initData, true);
        }
    };
});


//决策通知书提交弹出框
ctmApp.directive('directCommonUpload', function(){
    return {
        restrrict:'AE',
        templateUrl:'page/sys/directive/directCommonUpload.html',
        replace:'true',
        scope:{
            //必填,该指令所在modal的id，在当前页面唯一
            //id: "@",
            //对话框的标题，如果没设置，默认为“人员选择”
            title: "@",
            attachment: "@",
            callback: "="
        },
        controller:function($scope,$location,$http,Upload){
            $scope.errorAttach=[];
            $scope.upload = function (file,errorFile, idx) {
                if(errorFile && errorFile.length>0){
                    var errorMsg = fileErrorMsg(errorFile);
                    $scope.errorAttach[idx]={msg:errorMsg};
                }else if(file){
                    $scope.errorAttach[idx]={msg:''};
                    Upload.upload({
                        url:srvUrl+'common/RcmFile/upload',
                        data: {file: file, typeKey:"noticeDecisionFinalPath"}
                    }).then(function (resp) {
                        var retData = resp.data.result_data[0];
                        $scope.attachment=retData;
                    }, function (resp) {
                    }, function (evt) {
                        var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                        $scope["progress"+idx]=progressPercentage == 100 ? "":progressPercentage+"%";
                    });
                }
            };
            $scope.submit = function(){
                if(isEmptyObject($scope.attachment)){
                    $.alert("附件不能为空！");
                    return false;
                }
                if($scope.callback!=null){
                    $scope.callback($scope.attachment);
                }
            };
            $scope.cancel = function(){
                $scope.attachment={};
            }
            //jquery判断是否对象非空
            function isEmptyObject(e) {
                var t;
                for (t in e)
                    return !1;
                return !0
            }
        }
    }
});
// 新流程图
ctmApp.directive('directiveProcessPageNew', function() {
    return {
        restrict: 'E',
        templateUrl: 'page/sys/directive/DirectiveProcessPageNew.html',
        replace: true,
        link:function(scope,element,attr){
        },
        scope:{
            processKey:'@'// 流程Key
        },
        controller:function($scope, $routeParams){
            $scope._process_key_ = $scope.processKey;
            $scope._process_id_ = $routeParams.id;
            // 监听流程变化
            $scope.$parent.$watch("refreshImg", function(){
                // 获取流程审批记录
                $scope._process_logs_ = wf_listTaskLog($scope._process_key_, $scope._process_id_);
                // 获取流程审批进度
                $scope._approvalNode_ = wf_getProcessImageStep($scope._process_key_, $scope._process_id_);
                console.log($scope._approvalNode_);
            });
        }
    };
});
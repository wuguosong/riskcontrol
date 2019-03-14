/*******************************************************公共指令开始***************************************************/
ctmApp.directive('directiveReturnBtn', function() {
    return {
        restrict: 'E',
        //templateUrl: 'page/sys/directive/projectFormal/DirectiveProjectFormalReview.html',
        template: '<a class="btn btn-info" ng-href="{{url|decodeURI}}" ng-click="callback()"><i class="fa fa-reply"></i>返回</a>',
        replace: true,
        scope:{url:'@',callback:"&"},
        link:function(scope,element,attr){
        },
        controller:function($scope,$http,$element){
        }
    };
});
// 人员多选
ctmApp.directive('directUserMultiDialog', function() {
    return {
        restrict: 'E',
        templateUrl: 'page/sys/directive/directUserMultiDialog.html',
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
            checkedUsers: "=",
            //映射的key，value，{nameField:'username',valueField:'uuid'}，
            //默认为{nameField:'NAME',valueField:'VALUE'}
            mappedKeyValue: "=",
            callback: "="
            //移除选中的人员，调用父scope中的同名方法
//        	removeSelectedUser: "&"
        },
        controller:function($scope,$http,$element){
            if($scope.url == null || '' == $scope.url){
                $scope.url = "user/queryUserForSelected.do";
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
            if(null != $scope.queryParams){
                $scope.paginationConf.queryObj = $scope.queryParams;
            }
            $scope.queryUser = function(){
                $http({
                    method:'post',
                    url:srvUrl+$scope.url,
                    data: $.param({"page":JSON.stringify($scope.paginationConf)})
                }).success(function(data){
                    if(data.success){
                        $scope.users = data.result_data.list;
                        $scope.paginationConf.totalItems = data.result_data.totalItems;
                    }else{
                        $.alert(data.result_name);
                    }
                });
            }
            $scope.removeSelectedUser = function(user){
                for(var i = 0; i < $scope.tempCheckedUsers.length; i++){
                    if(user.VALUE == $scope.tempCheckedUsers[i].VALUE){
                        $scope.tempCheckedUsers.splice(i, 1);
                        break;
                    }
                }
            };
            $scope.isChecked = function(user){
                for(var i = 0; i < $scope.tempCheckedUsers.length; i++){
                    if(user.UUID == $scope.tempCheckedUsers[i].VALUE){
                        return true;
                    }
                }
                return false;
            };
            $scope.toggleChecked = function(user){
                //是否选中
                var isChecked = $("#chk_"+$scope.id+"_"+user.UUID).prop("checked");
                //是否已经存在
                var flag = false;
                for(var i = 0; i < $scope.tempCheckedUsers.length; i++){
                    if(user.UUID == $scope.tempCheckedUsers[i].VALUE){
                        flag = true;
                        if(!isChecked){
                            $scope.tempCheckedUsers.splice(i, 1);
                            break;
                        }
                    }
                }
                if(isChecked && !flag){
                    //如果已经选中，但是不存在，添加
                    $scope.tempCheckedUsers.push({"VALUE":user.UUID,"NAME":user.NAME});
                }
            };

            $scope.cancelSelected = function(){
                $scope.initData();
            }
            $scope.saveSelected = function(){
                var cus = $scope.tempCheckedUsers;
                $scope.checkedUsers.splice(0,$scope.checkedUsers.length)
                for(var i = 0; i < cus.length; i++){
                    var user = {};
                    user[$scope.mappedKeyValue.nameField] = cus[i].NAME;
                    user[$scope.mappedKeyValue.valueField] = cus[i].VALUE;

                    $scope.checkedUsers.push(user);
                    delete user.$$hashKey;
                }
                if($scope.callback != null){
                    $scope.callback();
                }
            }
            $scope.initData = function(){
                var cus = $.parseJSON(JSON.stringify($scope.checkedUsers));
                $scope.tempCheckedUsers = [];
                for(var i = 0; i < cus.length; i++){
                    var user = {};
                    user.NAME = cus[i][$scope.mappedKeyValue.nameField];
                    user.VALUE = cus[i][$scope.mappedKeyValue.valueField];
                    $scope.tempCheckedUsers.push(user);
                }
                $scope.paginationConf.queryObj.username = '';
                $scope.queryUser();
            }
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
            isEditable:"=",
            //默认选中的用户,数组类型，{NAME:'张三',VALUE:'user.uuid'}
            checkedUser: "=",
            //映射的key，value，{nameField:'username',valueField:'uuid'}，
            //默认为{nameField:'NAME',valueField:'VALUE'}
            mappedKeyValue: "=",
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
            mappedKeyValue: "=",
            callback: "="
            //移除选中的人员，调用父scope中的同名方法
//        	removeSelectedUser: "&"
        },
        controller:function($scope,$http,$element){
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
            isPage:"=",
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
                    $scope.callback(cus);
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
            isEditable:"=",
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
            mappedKeyValue: "=",
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
                    }else{
                        $.alert(data.result_name);
                    }
                });
            }
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
// 会议纪要新增弹出框
ctmApp.directive('mettingSummaryBpmnPopWin', function(){
    return {
        restrrict:'AE',
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
            isUse: "="
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
                // $scope.latestAttachmentS = [];

                // angular.forEach($scope.attach, function (data, index) {
                //     if (data.ITEM_NAME == name.newItem.ITEM_NAME) {
                //         if ($scope.latestAttachment) {
                //             if ($scope.latestAttachment.upload_date < data.upload_date) {
                //                 $scope.latestAttachment = data;
                //             }
                //         } else {
                //             $scope.latestAttachment = data;
                //         }
                //     }
                // });
                // angular.forEach($scope.fileList, function (data, index) {
                //     if (data.files.ITEM_NAME == name.newItem.ITEM_NAME) {
                //         data.files.upload_date.replace(/-/g,"/");
                //         if ($scope.latestAttachment) {
                //             if ($scope.latestAttachment.upload_date < data.files.upload_date) {
                //                 $scope.latestAttachment = data.files;
                //             }
                //         } else {
                //             $scope.latestAttachment = data.files;
                //         }
                //     }
                // });
                // if($scope.latestAttachment == null || $scope.latestAttachment.fileName == undefined || $scope.latestAttachment.fileName == ''
                //         || $scope.latestAttachment.fileName == null){
                //         $scope.isUse = true;
                //     } else {
                //     $scope.isUse = false;
                // }
                // if ($scope.latestAttachment.type == "invest") {
                //     $scope.latestAttachment.typeValue = "投资部门提供";
                // } else {
                //     $scope.latestAttachment.typeValue = "业务部门提供";
                // }
            };

            // $scope.close = function() {
            //    $scope.latestAttachmentS = [];
            //    if ($scope.item != null) {
            //        if($scope.item.newItem != undefined) {
            //            $scope.item = {};
            //            $scope.item.newItem = null;
            //        }
            //    }
            // }

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
            isShow:"=",
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
        restrrict:'AE',
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
ctmApp.directive('preReviewBpmnPopWin', function(){
    return {
        restrrict:'AE',
        templateUrl:'page/sys/directive/preReviewBpmnPopWin.html',
        replace:'true',
        scope:{approve:'='},
        controller:function($scope,$location,$http,Upload){

            $scope.pre = {};
            //默认不上会
            $scope.pre.needMeeting = '0';
            //默认不出报告
            $scope.pre.needReport = '1';
            $scope.pre.decisionOpinion = false;
            //验证任务人员
            $scope.callfunction = function(functionName){
                var func = eval(functionName);
                //创建函数对象，并调用
                return new func(arguments[1]);
            }
            var validServiceType= function(){
                var result = {success:true,result_name:""};
                if($scope.$parent.pre.oracle.SERVICETYPE_ID != "1401"  && $scope.$parent.pre.oracle.SERVICETYPE_ID != "1402"){
                    result.success = false;
                    result.result_name = "此项目非传统水务、水环境项目！无法选择此选项！";
                }
                return result;
            };
            //验证任务人员信息(分配任务节点)
            var validCheckedFzr= function(){
                var result = {success:true,result_name:""};
                if($scope.$parent.myTaskallocation == null  || $scope.$parent.myTaskallocation == ""){
                    result.success = false;
                    result.result_name = "请分配负责人！";
                }
                if($scope.$parent.myTaskallocation.reviewLeader.NAME == null || $scope.$parent.myTaskallocation.reviewLeader.NAME == ""){
                    result.success = false;
                    result.result_name = "请选择评审负责人！";
                }
                return result;
            };
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
            var validOpinions = function(){
                var result = {success:true,result_name:""};
                if($scope.submitInfo.currentTaskVar == null ||$scope.submitInfo.currentTaskVar.cesuanFileOpinion == null || $scope.submitInfo.currentTaskVar.cesuanFileOpinion==""){
                    result.success = false;
                    result.result_name = "请填写测算文件意见！";
                }
                if($scope.submitInfo.currentTaskVar == null ||$scope.submitInfo.currentTaskVar.tzProtocolOpinion == null || $scope.submitInfo.currentTaskVar.tzProtocolOpinion==""){
                    result.success = false;
                    result.result_name = "请填写投资协议意见！";
                }
                return result;
            }

            //判断是否显示toConfirm的打分项
            $scope.showMarkMethod = function(documentation){

                if(documentation != null && documentation !=""){
                    var docObj = JSON.parse(documentation);
                    if(docObj.mark == "reviewPassMark"){
                        $scope.showReviewToConfirm = true;
                        $scope.showReviewConfirmToEnd = false;
                        $scope.showMark = true;
                    }
                    if(docObj.mark == "toEnd"){
                        $scope.showMark = false;
                        $scope.showReviewToConfirm = false;
                        $scope.showReviewConfirmToEnd = true;
                    }
                }else{
                    $scope.showReviewToConfirm = false;
                    $scope.showReviewConfirmToEnd = false;
                }
            }
            $scope.checkMark = function(){
                if($scope.approve==null){
                    return;
                }
                var processOptions = $scope.approve.processOptions;

                if(processOptions[0].documentation != null && processOptions[0].documentation != ''){
                    var docObj = JSON.parse(processOptions[0].documentation);

                    if(docObj.mark == "reviewPassMark"){
                        $scope.showReviewToConfirm = true;
                    }
                    if(docObj.mark == "legalPassMark"){
                        $scope.showLegalToConfirm = true;
                    }
                }

                //流程选项
                for(var i in processOptions){
                    var documentation = processOptions[i].documentation;
                    if(documentation !=null && documentation !=""){
                        var docObj = JSON.parse(documentation);
                        if(docObj.mark == "toEnd"){
                            $scope.newEndOption = true;
                        }
                        if(docObj.mark == "reviewPassMark"){
//							$scope.showMark = true;
                            if(i == 0){
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



            $scope.submitInfo={};
            $scope.submitInfo.currentTaskVar={};
            $scope.submitNext = function(){
                if("submit" == $scope.approve.operateType){
                    $scope.submit();
                }else if("audit" == $scope.approve.operateType){
                    if($scope.showReviewConfirmToEnd){
                        $.confirm("您选择了终止项目，意味着您将废弃此项目！是否确认？",function(){
                            $scope.auditSingle();
                        });
                    }else if($scope.showReviewToConfirm){
                        $.confirm("您选择了评审负责人确认选项，意味着您已经和投资经理沟通完毕，流程将进入下一环节！是否确认？",function(){
                            $scope.auditSingle();
                        });
                    }else{
                        $scope.auditSingle();
                    }
                }else{
                    $.alert("操作状态不明确！");
                }
            };
            $scope.checkReport = function(){
                $scope.pre.needReport = "1";
                $scope.pre.decisionOpinion = null;
            }
            $scope.submit = function(){
                var url = srvUrl + "preAudit/startSingleFlow.do";
                show_Mask();
                $http({
                    method:'post',
                    url: url,
                    data: $.param({
                        "processKey": $scope.approve.processKey,
                        "businessId": $scope.approve.businessId
                    })
                }).success(function(result){
                    hide_Mask();
                    if($scope.approve.callbackSuccess != null && result.success){
                        $scope.approve.callbackSuccess(result);
                    }else if($scope.approve.callbackFail!=null && !result.success){
                        $scope.approve.callbackFail(result);
                    }else{
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
            $scope.auditSingle = function(){

                //验证确认节点是否选择上会，与报告
                if($scope.approve.showController.isReviewLeaderConfirm){
                    if($scope.pre == null||$scope.pre.needMeeting == null){
                        $.alert("请选择是否需要上会！");
                        return;
                    }
//					if($scope.pre.needMeeting == '0' || $scope.pre.needMeeting == 0  ){
//						$.alert("请选择决策意见！");
//						return;
//					}
                    if($scope.pre.needReport == null){
                        $.alert("请选择是否需要出评审报告！");
                        return;
                    }

                    if($scope.pre.needReport == 0){
                        if($scope.pre.noReportReason == null || $scope.pre.noReportReason == ''){
                            $.alert("请填写不出报告原因！");
                            return;
                        }
                        if($scope.pre.noReportReason.length >200){
                            $.alert("不出报告原因不能大于200字！");
                            return;
                        }
                    }
                    if($scope.pre.decisionOpinion){
                        $scope.pre.decisionOpinion = '6';
                    }else{
                        $scope.pre.decisionOpinion = '5';
                    }
                    $.ajax({
                        type:'post',
                        url: srvUrl + "preInfo/saveNeedMeetingAndNeedReport.do",
                        data: $.param({
                            "pre":JSON.stringify($scope.pre),
                            'businessId':$scope.approve.businessId
                        }),
                        dataType: "json",
                        async: false,
                        success:function(result){
                            if(!result.success){
                                alert(result.result_name);
                                return;
                            }
                        }
                    });
                }

                if($scope.flowVariables == null ||$scope.flowVariables == 'undefined' ||$scope.flowVariables.opinion == undefined ||$scope.flowVariables.opinion == null || $scope.flowVariables.opinion==""){
                    $.alert("审批意见不能为空！");
                    return;
                }
                if($scope.flowVariables.opinion.length>650){
                    $.alert("审批意见不能超过650字！");
                    return;
                }
                if($scope.approve.showController.isGroupMember){
                    //保存小组成员意见到mongo

                    var json = {"opinion":$scope.flowVariables.opinion,
                        "businessId":$scope.approve.businessId,
                        "user":$scope.$parent.$parent.credentials
                    };

                    $http({
                        method:'post',
                        url: srvUrl+"preInfo/saveFixGroupOption.do",
                        data: $.param({"json":JSON.stringify(json)})
                    }).success(function(result){
                    });
                }
                var url = srvUrl + "preAudit/auditSingle.do";
                var  documentation = $("input[name='bpmnProcessOption']:checked").attr("aaa");

                if(documentation !=null && documentation !=""){
                    var docObj = JSON.parse(documentation);
                    if(docObj.preAction){
                        var preActionArr = docObj.preAction;
                        for(var i in preActionArr){
                            //validServiceType
                            if(preActionArr[i].callback == 'validServiceType'){
                                var result = $scope.callfunction(preActionArr[i].callback);
                                if(!result.success){
                                    $.alert(result.result_name);
                                    return;
                                }
                            }else if(preActionArr[i].callback == 'validCheckedFzr'){
                                var result = $scope.callfunction(preActionArr[i].callback);
                                if(!result.success){
                                    $.alert(result.result_name);
                                    return;
                                }else{
                                    //保存任务人员信息
                                    $scope.$parent.myTaskallocation.businessId = $scope.approve.businessId;
                                    $.ajax({
                                        type:'post',
                                        url: srvUrl + "preInfo/saveTaskPerson.do",
                                        data: $.param({
                                            "task":JSON.stringify(angular.copy($scope.$parent.myTaskallocation)),
                                        }),
                                        dataType: "json",
                                        async: false,
                                        success:function(result){
                                            if(!result.success){
                                                alert(result.result_name);
                                                return;
                                            }
                                        }
                                    });
                                }
                            }else if(preActionArr[i].callback == 'validOpinions'){
                                var result = $scope.callfunction(preActionArr[i].callback);
                                if(!result.success){
                                    $.alert(result.result_name);
                                    return;
                                }else{
                                    //保存意见信息
                                    $.ajax({
                                        type:'post',
                                        url: srvUrl + "preInfo/saveServiceTypeOpinion.do",
                                        data: $.param({"serviceTypeOpinion":JSON.stringify($scope.submitInfo.currentTaskVar),
                                            "businessId":$scope.approve.businessId
                                        }),
                                        dataType: "json",
                                        async: false,
                                        success:function(result){
                                            if(!result.success){
                                                alert(result.result_name);
                                                return;
                                            }
                                        }
                                    });
                                }
                            }else if(preActionArr[i].callback == 'validCheckedMajorMember'){
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
                    method:'post',
                    url: url,
                    data: $.param({
                        "processKey": $scope.approve.processKey,
                        "businessId": $scope.approve.businessId,
                        "opinion":$scope.flowVariables.opinion,
                        "processOption":$("input[name='bpmnProcessOption']:checked").val()
                    })
                }).success(function(result){
                    hide_Mask();
                    if($scope.approve.callbackSuccess != null && result.success){
                        $scope.approve.callbackSuccess(result);
                    }else if($scope.approve.callbackFail!=null && !result.success){
                        $scope.approve.callbackFail(result);
                    }else{
                        $.alert(result.result_name);
                    }
                });
            };
            $scope.$watch("approve",$scope.checkMark);
        }
    }
});
// 正式评审流程提交弹出框
ctmApp.directive('formalAssessmentBpmnPopWin', function(){
    return {
        restrrict:'AE',
        templateUrl:'page/sys/directive/formalAssessmentBpmnPopWin.html',
        replace:'true',
        scope:{approve:'='},
        controller:function($scope,$location,$http,Upload){
            //初始化提示信息框
            $("[data-toggle='tooltip']").tooltip();

            //验证任务人员
            $scope.callfunction = function(functionName){
                var func = eval(functionName);
                //创建函数对象，并调用
                return new func(arguments[1]);
            }

            //判断是否显示toConfirm的打分项
            $scope.showMarkMethod = function(documentation){

                if(documentation != null && documentation !=""){
                    var docObj = JSON.parse(documentation);
                    if(docObj.mark == "reviewPassMark"){
                        $scope.showReviewToConfirm = true;
                        $scope.showReviewConfirmToEnd = false;
                        $scope.showMark = true;
                    }
                    if(docObj.mark == "legalPassMark"){
                        $scope.showMark = true;
                        $scope.showLegalToConfirm = true;

                    }
                    if(docObj.mark == "toEnd"){
                        $scope.showMark = false;
                        $scope.showLegalToConfirm = false;
                        $scope.showReviewToConfirm = false;
                        $scope.showReviewConfirmToEnd = true;
                    }
                }else{
//					$scope.showMark = false;
                    $scope.showLegalToConfirm = false;
                    $scope.showReviewToConfirm = false;
                    $scope.showReviewConfirmToEnd = false;
                }
            }

            $scope.checkMark = function(){
                if($scope.approve==null){
                    return;
                }
                var processOptions = $scope.approve.processOptions;

                if(processOptions[0].documentation != null && processOptions[0].documentation != ''){
                    var docObj = JSON.parse(processOptions[0].documentation);

                    if(docObj.mark == "reviewPassMark"){
                        $scope.showReviewToConfirm = true;
                    }
                    if(docObj.mark == "legalPassMark"){
                        $scope.showLegalToConfirm = true;
                    }
                }

                //流程选项
                for(var i in processOptions){
                    var documentation = processOptions[i].documentation;
                    if(documentation !=null && documentation !=""){
                        var docObj = JSON.parse(documentation);
                        if(docObj.mark == "toEnd"){
                            $scope.newEndOption = true;
                        }
                        if(docObj.mark == "reviewPassMark" || docObj.mark == "legalPassMark" ){
//							$scope.showMark = true;
                            if(i == 0){
                                $scope.showMark = true;
                            }
                            //查询后台的评价记录
                            $.ajax({
                                type:'post',
                                url: srvUrl + "formalMark/queryMarks.do",
                                data: $.param({"businessId":$scope.approve.businessId}),
                                dataType: "json",
                                async: false,
                                success:function(result){
                                    if(result.success){
                                        if(result.result_data == null){
                                            if(docObj.mark == "reviewPassMark"){
                                                $scope.showReviewFirstMark = true;
                                                $scope.showMark = true;
                                            }
                                            if(docObj.mark == "legalPassMark"){
                                                $scope.showLegalFirstMark = true;
                                                $scope.showMark = true;
                                            }
                                        }else{
                                            if(docObj.mark == "reviewPassMark"){
                                                if(result.result_data.REVIEWFILEACCURACY == null || result.result_data.REVIEWFILEACCURACY =="" || result.result_data.FLOWMARK == null || result.result_data.FLOWMARK == ''){
                                                    $scope.showReviewFirstMark = true;
                                                }
                                            }
                                            if(docObj.mark == "legalPassMark"){
                                                if(result.result_data.LEGALFILEACCURACY == null || result.result_data.LEGALFILEACCURACY =="" ){
                                                    $scope.showLegalFirstMark = true;
                                                }
                                            }
                                        }
                                    }else{
                                        alert(result.result_name);
                                        return;
                                    }
                                }
                            });
                        }
                    }
                }
            }
            var validServiceType = function(){
                var result = {success:true,result_name:""};

                if($scope.$parent.$parent.pfrOracle.SERVICETYPE_ID != '1401'  &&  $scope.$parent.$parent.pfrOracle.SERVICETYPE_ID != '1402'){
                    result.success = false;
                    result.result_name = "此项目非传统水务、水环境项目！无法选择该选项	！";
                }
                return result;
            };
            var validCheckedFzr= function(){
                var result = {success:true,result_name:""};

                if($scope.$parent.$parent.myTaskallocation == null  || $scope.$parent.$parent.myTaskallocation == ""){
                    result.success = false;
                    result.result_name = "请分配负责人！";
                }
                if($scope.approve.showController.isSignLegal == null || $scope.approve.showController.isSignLegal == ''){
                    if($scope.$parent.$parent.myTaskallocation.legalReviewLeader.NAME == null || $scope.$parent.$parent.myTaskallocation.legalReviewLeader.NAME == ""){
                        result.success = false;
                        result.result_name = "请选择法律评审负责人！";
                    }
                }
                if($scope.$parent.$parent.myTaskallocation.reviewLeader.NAME == null || $scope.$parent.$parent.myTaskallocation.reviewLeader.NAME == ""){
                    result.success = false;
                    result.result_name = "请选择评审负责人！";
                }
                return result;
            };
            var validCheckedFLFzr= function(){
                var result = {success:true,result_name:""};

                if($scope.$parent.$parent.myTaskallocation == null  || $scope.$parent.$parent.myTaskallocation == ""){
                    result.success = false;
                    result.result_name = "请分配负责人！";
                }
                if($scope.$parent.$parent.myTaskallocation.legalReviewLeader.NAME == null || $scope.$parent.$parent.myTaskallocation.legalReviewLeader.NAME == ""){
                    result.success = false;
                    result.result_name = "请选择法律评审负责人！";
                }
                return result;
            };

            var validCheckedMajor= function(){
                var result = {success:true,result_name:""};

                if($scope.$parent.$parent.myTaskallocation.professionalReviewers == null || $scope.$parent.$parent.myTaskallocation.professionalReviewers == ""){
                    result.success = false;
                    result.result_name = "请选择专业评审人员！";
                }
                return result;
            };
            $scope.submitInfo={};
            $scope.submitInfo.currentTaskVar={};
            $scope.submitNext = function(){
                if("submit" == $scope.approve.operateType){
                    $scope.submit();
                }else if("audit" == $scope.approve.operateType){
                    if($scope.showReviewConfirmToEnd){
                        $.confirm("您选择了终止项目，意味着您将废弃此项目！是否确认？",function(){
                            $scope.auditSingle();
                        });
                    }else if($scope.showLegalToConfirm){
                        $.confirm("您选择了评审负责人确认，意味着您已经和基层法务沟通完毕，流程将进入下一环节！是否确认？",function(){
                            $scope.auditSingle();
                        });
                    }else if($scope.showReviewToConfirm){
                        $.confirm("您选择了评审负责人确认选项，意味着您已经和投资经理沟通完毕，流程将进入下一环节！是否确认？",function(){
                            $scope.auditSingle();
                        });
                    }else{
                        $scope.auditSingle();
                    }
                }else{
                    $.alert("操作状态不明确！");
                }
            };
            $scope.submit = function(){
                var url = srvUrl + "formalAssessmentAudit/startSingleFlow.do";
                show_Mask();
                $http({
                    method:'post',
                    url: url,
                    data: $.param({
                        "processKey": $scope.approve.processKey,
                        "businessId": $scope.approve.businessId
                    })
                }).success(function(result){
                    hide_Mask();
                    if($scope.approve.callbackSuccess != null && result.success){
                        $scope.approve.callbackSuccess(result);
                    }else if($scope.approve.callbackFail!=null && !result.success){
                        $scope.approve.callbackFail(result);
                    }else{
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
            $(".mark").keyup( function() {
                if(this.value.length==1){
                    this.value=this.value.replace(/[^0-9]/g,'');
                }else{
                    this.value=this.value.replace(/\D/g,'')
                }
                if(this.value>this.attributes.max.value*1){
                    this.value=null;
                }
            });

            $scope.auditSingle = function(){
                //打分项验证-start
                if($scope.showMark && ($scope.showReviewFirstMark || $scope.showReviewToConfirm || $scope.showLegalToConfirm)){
                    if(!$scope.mark){
                        $.alert("请评分！");
                        return;
                    }
                    if($scope.showReviewFirstMark){
                        if($scope.mark.flowMark == null){
                            $.alert("请对审批流程熟悉度评分！");
                            return;
                        }
                        if($scope.mark.moneyCalculate == null){
                            $.alert("请对核心财务测算能力评分！");
                            return;
                        }
                        if($scope.mark.reviewFileAccuracy == null){
                            $.alert("请对资料的准确性评分！");
                            return;
                        }
                        if($scope.mark.planDesign == null){
                            $.alert("请对核心的方案设计能力评分！");
                            return;
                        }
                    }
                    if($scope.showLegalFirstMark){
                    }
                    if($scope.showReviewToConfirm){
                        if($scope.mark.fileContent == null){
                            $.alert("请对资料的完整性评分！");
                            return;
                        }
                        if($scope.mark.fileTime == null){
                            $.alert("请对资料的及时性评分！");
                            return;
                        }
                        if($scope.mark.riskControl == null){
                            $.alert("请对核心风险识别及规避能力评分！");
                            return;
                        }

                    }
                    if($scope.showLegalToConfirm){
                        if($scope.mark.legalFileAccuracy == null){
                            $.alert("请对资料的准确性评分！");
                            return;
                        }
                        if($scope.mark.talks == null){
                            $.alert("请对核心的协议谈判能力评分！");
                            return;
                        }
                    }

                    //存分
                    $.ajax({
                        type:'post',
                        url: srvUrl + "formalMark/saveOrUpdate.do",
                        data: $.param({"json":JSON.stringify($scope.mark),
                            "businessId":$scope.approve.businessId
                        }),
                        dataType: "json",
                        async: false,
                        success:function(result){
                            if(!result.success){
                                alert(result.result_name);
                                return;
                            }
                        }
                    });

                }
                //打分项验证-end

                if($scope.approve.showController.isServiewType){
                    if($scope.submitInfo.currentTaskVar == null ||$scope.submitInfo.currentTaskVar.cesuanFileOpinion == null || $scope.submitInfo.currentTaskVar.cesuanFileOpinion==""){
                        $.alert("测算文件意见不能为空！");
                        return;
                    }
                    if($scope.submitInfo.currentTaskVar == null ||$scope.submitInfo.currentTaskVar.tzProtocolOpinion == null || $scope.submitInfo.currentTaskVar.tzProtocolOpinion==""){
                        $.alert("投资协议意见不能为空！");
                        return;
                    }
                    else{
                        //保存意见到mongo
                        $http({
                            method:'post',
                            url: srvUrl+"formalAssessmentInfo/updateServiceTypeOpinion.do",
                            data: $.param({"serviceTypeOpinion":JSON.stringify($scope.submitInfo.currentTaskVar),
                                "businessId":$scope.approve.businessId
                            })
                        }).success(function(result){

                        });
                    }
                }
                if($scope.flowVariables == null ||$scope.flowVariables == 'undefined' ||$scope.flowVariables.opinion == undefined ||$scope.flowVariables.opinion == null || $scope.flowVariables.opinion==""){
                    $.alert("审批意见不能为空！");
                    return;
                }
                if($scope.flowVariables.opinion.length>650){
                    $.alert("审批意见不能超过650字！");
                    return;
                }
                if($scope.approve.showController.isGroupMember){
                    //保存小组成员意见到mongo

                    var json = {"opinion":$scope.flowVariables.opinion,
                        "businessId":$scope.approve.businessId,
                        "user":$scope.$parent.$parent.credentials
                    };

                    $http({
                        method:'post',
                        url: srvUrl+"formalAssessmentInfo/saveFixGroupOption.do",
                        data: $.param({"json":JSON.stringify(json)})
                    }).success(function(result){
                    });
                }
                var url = srvUrl + "formalAssessmentAudit/auditSingle.do";
                var  documentation = $("input[name='bpmnProcessOption']:checked").attr("aaa");

                if(documentation !=null && documentation !=""){
                    var docObj = JSON.parse(documentation);
                    if(docObj.preAction){
                        var preActionArr = docObj.preAction;
                        for(var i in preActionArr){
                            if(preActionArr[i].callback == 'validServiceType'){
                                var result = $scope.callfunction(preActionArr[i].callback);
                                if(!result.success){
                                    $.alert(result.result_name);
                                    return;
                                }
                            }else if(preActionArr[i].callback == 'validCheckedFzr'){
                                var result = $scope.callfunction(preActionArr[i].callback);
                                if(!result.success){
                                    $.alert(result.result_name);
                                    return;
                                }else{
                                    //保存任务人员信息
                                    $scope.$parent.$parent.myTaskallocation.businessId = $scope.approve.businessId;
                                    $.ajax({
                                        type:'post',
                                        url: srvUrl + "formalAssessmentAudit/saveTaskToMongo.do",
                                        data: $.param({"task":JSON.stringify($scope.$parent.$parent.myTaskallocation)}),
                                        dataType: "json",
                                        async: false,
                                        success:function(result){
                                            if(!result.success){
                                                alert(result.result_name);
                                                return;
                                            }
                                        }
                                    });
                                }
                            }else if (preActionArr[i].callback == 'validCheckedMajor') {

                                var result = $scope.callfunction(preActionArr[i].callback);
                                if(!result.success){
                                    $.alert(result.result_name);
                                    return;
                                }else{
                                    //保存专业评审人员信息
                                    if($scope.approve.showController.isTask && $scope.$parent.$parent.professionalReviewers.NAME == null || $scope.$parent.$parent.myTaskallocation.professionalReviewers.NAME == ""){
                                        $.alert("请选择专业评审人员！");
                                        return;
                                    }
                                    $scope.$parent.$parent.myTaskallocation.businessId = $scope.approve.businessId;
                                    $.ajax({
                                        type:'post',
                                        url: srvUrl + "formalAssessmentAudit/saveTaskToMongo.do",
                                        data: $.param({"task":JSON.stringify($scope.$parent.$parent.myTaskallocation)}),
                                        dataType: "json",
                                        async: false,
                                        success:function(result){
                                            if(!result.success){
                                                alert(result.result_name);
                                                return;
                                            }
                                        }
                                    });
                                }
                            }else if (preActionArr[i].callback == 'validCheckedFLFzr') {

                                var result = $scope.callfunction(preActionArr[i].callback);
                                if(!result.success){
                                    $.alert(result.result_name);
                                    return;
                                }else{
                                    //保存专业评审人员信息
                                    $scope.$parent.$parent.myTaskallocation.businessId = $scope.approve.businessId;
                                    $.ajax({
                                        type:'post',
                                        url: srvUrl + "formalAssessmentAudit/saveTaskToMongo.do",
                                        data: $.param({"task":JSON.stringify($scope.$parent.$parent.myTaskallocation)}),
                                        dataType: "json",
                                        async: false,
                                        success:function(result){
                                            if(!result.success){
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
                    method:'post',
                    url: url,
                    data: $.param({
                        "processKey": $scope.approve.processKey,
                        "businessId": $scope.approve.businessId,
                        "opinion":$scope.flowVariables.opinion,
                        "processOption":$("input[name='bpmnProcessOption']:checked").val()
                    })
                }).success(function(result){
                    hide_Mask();
                    if($scope.approve.callbackSuccess != null && result.success){
                        $scope.approve.callbackSuccess(result);
                    }else if($scope.approve.callbackFail!=null && !result.success){
                        $scope.approve.callbackFail(result);
                    }else{
                        $.alert(result.result_name);
                    }
                });
            };
            $scope.$watch("approve",$scope.checkMark);

        }
    }
});
// 其他评审流程弹出框
ctmApp.directive('bpmnPopWin', function(){
    return {
        restrrict:'AE',
        templateUrl:'page/sys/directive/bpmnPopWin.html',
        replace:'true',
        scope:{approve:'='},
        controller:function($scope,$location,$http){

            $scope.callfunction = function(functionName){
                var func = eval(functionName);
                //创建函数对象，并调用
                return new func(arguments[1]);
            }

            var validCheckedFzr= function(){
                var result = {success:true,result_name:""};

                if($scope.$parent.myTaskallocation == null  || $scope.$parent.myTaskallocation == ""){
                    result.success = false;
                    result.result_name = "请分配负责人！";
                }
                if($scope.$parent.myTaskallocation.reviewLeader.NAME == null || $scope.$parent.myTaskallocation.reviewLeader.NAME == ""){
                    result.success = false;
                    result.result_name = "请选择评审负责人！";
                }
                return result;
            };

            var validCheckedRiskFzr= function(){

                var result = {success:true,result_name:""};

                if($scope.$parent.myTaskallocation == null  || $scope.$parent.myTaskallocation == ""){
                    result.success = false;
                    result.result_name = "请分配负责人！";
                }
                if($scope.$parent.myTaskallocation.riskLeader.NAME == null || $scope.$parent.myTaskallocation.riskLeader.NAME == ""){
                    result.success = false;
                    result.result_name = "请选择风控负责人！";
                }
                return result;
            };
            $scope.showSelectPerson = function(){
                $("#submitModal").modal('hide');
                $("#userSinDialog").modal('show');
            }
            $scope.submitNext = function(){
                if($("#workOver").attr("checked")){
                    $scope.workOver();
                }else if($("input[name='bpmnProcessOption']#change:checked").length>0){
                    $scope.changeWork();
                }else if("submit" == $scope.approve.operateType){
                    $scope.submit();
                }else if("audit" == $scope.approve.operateType){
                    $scope.auditSingle();
                }else{
                    $.alert("操作状态不明确！");
                }
            };

            $scope.workOver = function(){
                //人员验证
                if($scope.flowVariables == null ||$scope.flowVariables.opinion == null || $scope.flowVariables.opinion==""){
                    $.alert("审批意见不能为空！");
                    return;
                }
                if($scope.flowVariables.opinion.length>650){
                    $.alert("审批意见不能超过650字！");
                    return;
                }

                //执行办结操作
                show_Mask();
                $http({
                    method:'post',
                    url: srvUrl + "bulletinAudit/workOver.do",
                    data: $.param({
                        "businessId": $scope.approve.businessId,
                        "oldUser":$scope.$parent.curLog.OLDUSERID,
                        "taskId":$scope.$parent.curLog.TASKID,
                        "option":$scope.flowVariables.opinion
                    })
                }).success(function(result){
                    hide_Mask();
                    if($scope.approve.callbackSuccess != null && result.success){
                        $scope.approve.callbackSuccess(result);
                    }else if($scope.approve.callbackFail!=null && !result.success){
                        $scope.approve.callbackFail(result);
                    }else{
                        $.alert(result.result_name);
                    }
                });
            }

            $scope.changeWork = function(){
                //人员验证
                if($scope.$parent.checkedUser.NAME == null || $scope.$parent.checkedUser.NAME == ''){
                    $.alert("请选择目标人员！");
                    return;
                }
                if($scope.$parent.checkedUser.VALUE == $scope.$parent.$parent.credentials.UUID){
                    $.alert("不能转办给自己！");
                    return;
                }
                if($scope.$parent.checkedUser.VALUE == $scope.$parent.curLog.AUDITUSERID){
                    $.alert("不能转办给最初人员！");
                    return;
                }
                if($scope.flowVariables == null ||$scope.flowVariables.opinion == null || $scope.flowVariables.opinion==""){
                    $.alert("审批意见不能为空！");
                    return;
                }
                if($scope.flowVariables.opinion.length>650){
                    $.alert("审批意见不能超过650字！");
                    return;
                }
                //执行转办操作
                show_Mask();
                $http({
                    method:'post',
                    url: srvUrl + "bulletinAudit/changeWork.do",
                    data: $.param({
                        "businessId": $scope.approve.businessId,
                        "user": JSON.stringify($scope.$parent.checkedUser),
                        "taskId":$scope.$parent.curLog.TASKID,
                        "option":$scope.flowVariables.opinion
                    })
                }).success(function(result){
                    hide_Mask();
                    if($scope.approve.callbackSuccess != null && result.success){
                        $scope.approve.callbackSuccess(result);
                    }else if($scope.approve.callbackFail!=null && !result.success){
                        $scope.approve.callbackFail(result);
                    }else{
                        $.alert(result.result_name);
                    }
                });


            }
            $scope.submit = function(){
                var url = srvUrl + "bulletinAudit/startSingleFlow.do";
                show_Mask();
                $http({
                    method:'post',
                    url: url,
                    data: $.param({
                        "processKey": $scope.approve.processKey,
                        "businessId": $scope.approve.businessId
                    })
                }).success(function(result){
                    hide_Mask();
                    if($scope.approve.callbackSuccess != null && result.success){
                        $scope.approve.callbackSuccess(result);
                    }else if($scope.approve.callbackFail!=null && !result.success){
                        $scope.approve.callbackFail(result);
                    }else{
                        $.alert(result.result_name);
                    }
                });
            };
            $scope.auditSingle = function(){
                if($scope.flowVariables == null ||$scope.flowVariables.opinion == null || $scope.flowVariables.opinion==""){
                    $.alert("审批意见不能为空！");
                    return;
                }
                if($scope.flowVariables.opinion.length>650){
                    $.alert("审批意见不能超过650字！");
                    return;
                }

                var url = srvUrl + "bulletinAudit/auditSingle.do";
                var documentation = $("input[name='bpmnProcessOption']:checked").attr("aaa");

                if(documentation !=null && documentation !=""){
                    var docObj = JSON.parse(documentation);
                    if(docObj.preAction){
                        var preActionArr = docObj.preAction;
                        for(var i in preActionArr){
                            if(preActionArr[i].callback == 'validCheckedFzr'){
                                var result = $scope.callfunction(preActionArr[i].callback);
                                if(!result.success){
                                    $.alert(result.result_name);
                                    return;
                                }else{
                                    //保存任务人员信息
                                    $.ajax({
                                        type:'post',
                                        url: srvUrl + "bulletinInfo/saveTaskPerson.do",
                                        data: $.param({
                                            "json":JSON.stringify(angular.copy($scope.$parent.myTaskallocation)),
                                            "businessId":$scope.approve.businessId
                                        }),
                                        dataType: "json",
                                        async: false,
                                        success:function(result){
                                            if(!result.success){
                                                alert(result.result_name);
                                                return;
                                            }
                                        }
                                    });
                                }
                            }else if(preActionArr[i].callback == 'validCheckedRiskFzr'){
                                var result = $scope.callfunction(preActionArr[i].callback);
                                if(!result.success){
                                    $.alert(result.result_name);
                                    return;
                                }else{
                                    $.ajax({
                                        type:'post',
                                        url: srvUrl + "bulletinInfo/saveTaskPerson.do",
                                        data: $.param({
                                            "json":JSON.stringify($scope.$parent.myTaskallocation),
                                            "businessId":$scope.approve.businessId
                                        }),
                                        dataType: "json",
                                        async: false,
                                        success:function(result){
                                            if(!result.success){
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
                    method:'post',
                    url: url,
                    data: $.param({
                        "processKey": $scope.approve.processKey,
                        "businessId": $scope.approve.businessId,
                        "opinion":$scope.flowVariables.opinion,
                        "processOption": $("input[name='bpmnProcessOption']:checked").val()
                    })
                }).success(function(result){
                    hide_Mask();
                    if($scope.approve.callbackSuccess != null && result.success){
                        $scope.approve.callbackSuccess(result);
                    }else if($scope.approve.callbackFail!=null && !result.success){
                        $scope.approve.callbackFail(result);
                    }else{
                        $.alert(result.result_name);
                    }
                });
            };
        }
    }
});
/*******************************************************流程指令结束***************************************************/
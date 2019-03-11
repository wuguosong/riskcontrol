define(['app'], function (app) {
    //Service比较特殊，加载后还需要手动注入控制器
    app
        .service('Window', ['$uibModal', '$rootScope',
            function ($uibModal, $rootScope) {
                return {
                    login: function (password) {
                        console.log("bbbbb" + password);
                    },
                    alert: function (warn) {
                        //if ((document.getElementById("windowAlert")) != null) return;
                        return $uibModal.open({
                            template: '<div class="modal-header dialog-header-confirm"><div class="modal-title">' + warn + '</div></div><div class="modal-body" ng-bind-html="msg"></div><div class="modal-footer"><button type="button" class="btn btn-visa btn-visa-md" ng-click="confirm()">确定</button></div>',
                            // controller: 'alert',
                            backdrop: false,
                            size: 'sm',
                            controller: function ($scope, $uibModalInstance) {
                                $scope.confirm = function () {
                                    $uibModalInstance.dismiss("0");
                                };
                            }
                        });
                    },
                    confirm: function (title, msg) {
                        $rootScope.msg = msg;
                        // if ((document.getElementById("windowAlert")) != null) return;
                        return $uibModal.open({
                            template: '<div class="modal-header dialog-header-confirm"><button type="button" class="close" ng-click="close()">&times;</button><div class="modal-title">' + title + '</div></div><div class="modal-body" ng-bind-html="msg"></div><div class="modal-footer"><button type="button" class="btn btn-visa btn-visa-md" ng-click="confirm()">确定</button><button type="button" class="btn btn-visa btn-visa-md" ng-click="close()">取消</button></div>',
                            controller: function ($scope, $uibModalInstance) {
                                $scope.confirm = function () {
                                    $uibModalInstance.close();
                                };
                                $scope.close = function () {
                                    $uibModalInstance.dismiss("0");
                                };
                            },
                            backdrop: false,
                            size: 'sm'
                        });
                    },
                }
            }]);
    app
        .service('UserSelectDialog', ['$uibModal', '$rootScope', '$http',
            function ($uibModal, $rootScope, $http) {
                return {
                    multi: function (title, msg, checkedUsers, url, mappedKeyValue) {
                        $rootScope.msg = msg;
                        // if ((document.getElementById("windowAlert")) != null) return;
                        return $uibModal.open({
                            templateUrl: BUSINESS_PATH + 'directive/common/directUserMultiDialog.html',
                            controller: function ($scope, $uibModalInstance) {
                                $scope.checkedUsers = checkedUsers;
                                $scope.mappedKeyValue = mappedKeyValue;
                                $scope.url = url;
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
                                        url: SRV_URL + $scope.url,
                                        data: $.param({"page": JSON.stringify($scope.paginationConf)})
                                    }).success(function (data) {
                                        if (data.success) {
                                            $scope.users = data.result_data.list;
                                            $scope.paginationConf.totalItems = data.result_data.totalItems;
                                        } else {
                                            Window.alert(data.result_name);
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
                                    var isChecked = $("#chk_" + user.UUID).prop("checked");
                                    console.log(isChecked);
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
                                    console.log($scope.tempCheckedUsers);
                                };

                                $scope.cancelSelected = function () {
                                    $scope.initData();
                                }
                                $scope.saveSelected = function () {
                                    var cus = $scope.tempCheckedUsers;
                                    $scope.checkedUsers.splice(0, $scope.checkedUsers.length)
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
                                }
                                $scope.$watch('checkedUsers', $scope.initData, true);
                                $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.queryUser);


                                $scope.initData();
                                $scope.saveSelected = function () {
                                    var cus = $scope.tempCheckedUsers;
                                    console.log(cus);
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
                                    $uibModalInstance.close($scope.checkedUsers);
                                };
                                $scope.cancelSelected = function () {
                                    $uibModalInstance.dismiss("0");
                                };
                            },
                            backdrop: false,
                            size: 'lg'
                        });
                    },
                }
            }]);
    /**公共Service[Add by LiPan 2019-03-04]**/
    app.service('CommonService', [
        function () {
            return {
                /**显示遮罩层**/
                showMask: function () {
                    $("#mask_").css("height", $(document).height());
                    $("#mask_").css("line-height", $(document).height() + "px");
                    $("#mask_").css("width", $(document).width());
                    $("#mask_").show();
                },
                /**关闭遮罩层**/
                hideMask: function () {
                    $("#mask_").hide();
                },
                /**调用该方法可访问scope对象**/
                accessScope: function (node, func) {
                    var scope = angular.element(document.querySelector(node)).scope();
                    scope.$apply(func);
                },
                /**移除数组特定元素**/
                removeObjByValue: function (array, value) {
                    var retArray = [];
                    for (var i = 0; i < array.length; i++) {
                        if (value !== array[i].VALUE) {
                            retArray.push(array[i]);
                        }
                    }
                    return retArray;
                },
                /**根据value判断数组中的两个对象是否相同，然后去重**/
                removeDuplicate: function (array) {
                    if (!array || array.length < 2) return array;
                    array.sort(function compare(a, b) {
                        return (a.VALUE === b.VALUE) ? 0 : 1;
                    });
                    var re = [array[0]];
                    for (var i = 1; i < array.length; i++) {
                        if (array[i].VALUE !== re[re.length - 1].VALUE) {
                            re.push(array[i]);
                        }
                    }
                    return re;
                },
                /**文件上传错误提示**/
                fileErrorMsg: function (errorFile) {
                    var key = errorFile[0].$error;
                    var param = errorFile[0].$errorParam;
                    var errorMap = {"maxSize": "附件超过" + param + "限制！"};
                    return errorMap[key];
                },
                /**获取申请单所有的相关人**/
                findRelationUser: function (businessId, relationType) {
                    var url = 'rcm/ProjectRelation/findRelationUserByBusinessId';
                    var queryObj = {businessId: businessId, exclude: [$scope.credentials.UUID]};
                    if (typeof relationType != 'undefined') {
                        queryObj.relationType = relationType;
                    }
                    $scope.httpData(url, queryObj).success(function (data) {
                        if (data.result_code == 'S') {
                            $scope.relationUsers = data.result_data;
                        }
                    });
                },
                /**批量下载**/
                downloadBatch: function (filenames, filepaths) {
                    var isExists = this.validFileExists(filepaths);
                    if (!isExists) {
                        $.alert("要下载的文件已经不存在了！");
                        return;
                    }
                    var url = SRV_URL + "file/downloadBatch.do";
                    var form = $("<form>");//定义一个form表单
                    form.attr("style", "display:none");
                    form.attr("target", "");
                    form.attr("method", "post");
                    form.attr("action", url);
                    var input1 = $("<input>");
                    input1.attr("type", "hidden");
                    input1.attr("name", "filenames");
                    input1.attr("value", filenames);
                    var input2 = $("<input>");
                    input2.attr("type", "hidden");
                    input2.attr("name", "filepaths");
                    input2.attr("value", filepaths);
                    $("body").append(form);//将表单放置在web中
                    form.append(input1);
                    form.append(input2);
                    form.submit();//表单提交
                },
                /**校验文件是否存在**/
                validFileExists: function (filepaths) {
                    var result = true;
                    var url = SRV_URL + "file/validFileExists.do";
                    $.ajax({
                        url: url,
                        type: "POST",
                        dataType: "json",
                        data: {filepaths: filepaths},
                        async: false,
                        success: function (data) {
                            result = data.success;
                        }
                    });
                    return result;
                },
                DateDiff: function (values, nowDate) {    //sDate1和sDate2是2006-12-18格式
                    values = new Date(values.replace(/-/g, "/"));
                    nowDate = new Date(nowDate.replace(/-/g, "/"));
                    var days = values.getTime() - nowDate.getTime();
                    var iDays = parseInt(days / (1000 * 60 * 60 * 24));
                    return iDays
                },
                /**查询审批日志记录列表**/
                listLogs:function(business_module, business_id){
                    var url = SRV_URL + "sign/listLogs.do";
                    var logs = null;
                    $.ajax({
                        url: url,
                        type: "POST",
                        dataType: "json",
                        data: {"business_module": business_module,'business_id':business_id},
                        async: false,
                        success: function (data) {
                            logs = data;
                        }
                    });
                    console.log(logs);
                    return logs;
                },
                /**初始化当前审批日志记录**/
                getCurLog:function(business_module, business_id, curUUID){
                    console.log(curUUID);
                    var logs = this.listLogs(business_module, business_id);
                    var log = null;
                    for (var i in logs) {
                        if (logs[i].ISWAITING == '1') {
                            if (logs[i].AUDITUSERID == curUUID) {
                                log = logs[i];
                                break;
                            }
                        }
                    }
                    console.log(log);
                    return log;
                }
            }
        }]);
});
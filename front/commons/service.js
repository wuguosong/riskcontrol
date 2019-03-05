define(['app', 'ComCtrl'], function (app) {
    //Service比较特殊，加载后还需要手动注入控制器
    app.register
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
    /**公共Service[Add by LiPan 2019-03-04]**/
    app.register.service('CommonService', [
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
                }
            }
        }]);
});
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
                            controller:  function ($scope, $uibModalInstance) {
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
            }])
});
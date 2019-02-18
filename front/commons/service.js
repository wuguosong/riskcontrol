define(['app', 'Controller'], function (app) {
    //Service比较特殊，加载后还需要手动注入控制器
    app.register
        .service('booksService', ['$uibModal',
            function ($uibModal) {
                return {
                    login: function (password) {
                        console.log("bbbbb" + password);
                    },
                    alert: function (warn) {
                        //if ((document.getElementById("windowAlert")) != null) return;
                        return $uibModal.open({
                            template: '<div class="modal-header dialog-header-confirm"><div class="modal-title">' + warn + '</div></div><div class="modal-body" ng-bind-html="msg"></div><div class="modal-footer"><button type="button" class="btn btn-visa btn-visa-md" ng-click="confirm()"></button></div>',
                            controller: 'alert',
                            backdrop: false,
                            size: 'sm'
                        });
                    }
                }
            }])
});
define(['app'], function (app) {
    var srvUrl = "/rcm-rest";
    app
        .constant('BEWG_URL', {
            LoginUrl: srvUrl + "login/login",
            SaveOrUpdateDict : srvUrl + "dict/saveOrUpdateDictType.do",


        });
});
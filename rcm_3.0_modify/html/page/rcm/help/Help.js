ctmApp.register.controller('Help', ['$http','$routeParams','$scope','$location', function ($http,$routeParams,$scope,$location) {
    $("#main-menu").css({display:"none"});
    $("#main-menu-bg").css({display:"none"});
    $("#main-wrapper").css({paddingLeft:0});
    $("html").css({overflowY:"auto"});
}]);
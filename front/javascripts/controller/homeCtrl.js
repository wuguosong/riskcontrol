define(['app','booksService'],function(app){
	app.register.controller('homeCtrl', function($scope,booksService, ssssssssssss){
		$scope.str = 'home page';
        $scope.sex = 0;
        $scope.books = booksService.books;
        $scope.books = ssssssssssss.books;
        console.log($scope.books)
	})
    //《AngularJS过滤器filter入门》http://www.cnblogs.com/wangmeijian/p/4979452.html
    app.register.filter('sexFilter', function(){
        return function(sex){
            return ['男','女'][sex];
        }
    })    
    //《AngularJs指令配置参数scope详解》http://www.cnblogs.com/wangmeijian/p/4944030.html
    app.register.directive('myDirective', function(){
        return {
            restrict: "E",
            replace: true,
            template: "<div><span>我是由自定义指令渲染出来的</span></div>"
        }
    })
    app.register.service('ssssssssssss', function(){
        console.log("aaaaaaaaa");
        this.books = [
            {
                id: 0,
                name: 'javascript权威指南'
            },
            {
                id: 1,
                name: 'javascript高级程序设计'
            }
        ];
    })
})
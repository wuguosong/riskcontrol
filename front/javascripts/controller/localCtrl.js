define(['app', 'booksService', 'service/aaaaaaaaaaa'],function(app){
	app.register
	.controller('localCtrl',function($scope, booksService, aaaaaaaaaaa){
		$scope.str = 'local page';
        $scope.books = booksService.books;
	})
})
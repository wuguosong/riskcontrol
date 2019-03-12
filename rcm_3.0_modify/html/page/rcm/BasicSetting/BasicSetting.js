/********
 * Created by wangjian on 16/4/11.
 * 用户管理控制器
 *********/

ctmApp.register.controller('DataDictionaryList', ['$http','$scope','$location', function ($http,$scope,$location) {
	//查义所有的操作
	$scope.ListAll=function(){
		var aMethed = 'projectPreReview/ProjectPreReview/getAll';
		$scope.httpData(aMethed,$scope.conf).success(
			function (data, status, headers, config) {
				$scope.ppr = data.result_data;
				$scope.paginationConf.totalItems = data.result_data.length;
			}
		).error(function (data, status, headers, config) {
			alert(status);
		});
	};
	// 重新获取数据条目
	var reGetProducts = function(){
		// 发送给后台的请求数据
		var postData = {
			currentPage:5 ,//$scope.paginationConf.currentPage,
			itemsPerPage:10 //$scope.paginationConf.itemsPerPage
		};
		//当初始化时需要加载一次， 当查询设置为null时并且返回结果与上次总条数不一致时加载
		$scope.ListAll();
	};
	// 配置分页基本参数
	$scope.paginationConf = {
		currentPage: 1,
		itemsPerPage: 10,
		//totalItems: 50,
		perPageOptions: [10, 20, 30, 40, 50]
	};
	// 通过$watch currentPage和itemperPage 当他们一变化的时候，重新获取数据条目
	$scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', reGetProducts);
}]);

ctmApp.register.controller('DataDictionaryEdit', ['$http','$scope','$location', function ($http,$scope,$location) {
	//查义所有的操作
	$scope.ListAll=function(){
		var aMethed = 'projectPreReview/ProjectPreReview/getAll';
		$scope.httpData(aMethed,$scope.conf).success(
			function (data, status, headers, config) {
				$scope.ppr = data.result_data;
				$scope.paginationConf.totalItems = data.result_data.length;
			}
		).error(function (data, status, headers, config) {
			alert(status);
		});
	};
	// 重新获取数据条目
	var reGetProducts = function(){
		// 发送给后台的请求数据
		var postData = {
			currentPage:5 ,//$scope.paginationConf.currentPage,
			itemsPerPage:10 //$scope.paginationConf.itemsPerPage
		};
		//当初始化时需要加载一次， 当查询设置为null时并且返回结果与上次总条数不一致时加载
		$scope.ListAll();
	};
	// 配置分页基本参数
	$scope.paginationConf = {
		currentPage: 1,
		itemsPerPage: 10,
		//totalItems: 50,
		perPageOptions: [10, 20, 30, 40, 50]
	};
	// 通过$watch currentPage和itemperPage 当他们一变化的时候，重新获取数据条目
	$scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', reGetProducts);
}]);

ctmApp.register.controller('DataOptionList', ['$http','$scope','$location', function ($http,$scope,$location) {
	//查义所有的操作
	$scope.ListAll=function(){
		var aMethed = 'projectPreReview/ProjectPreReview/getAll';
		$scope.httpData(aMethed,$scope.conf).success(
			function (data, status, headers, config) {
				$scope.ppr = data.result_data;
				$scope.paginationConf.totalItems = data.result_data.length;
			}
		).error(function (data, status, headers, config) {
			alert(status);
		});
	};
	// 重新获取数据条目
	var reGetProducts = function(){
		// 发送给后台的请求数据
		var postData = {
			currentPage:5 ,//$scope.paginationConf.currentPage,
			itemsPerPage:10 //$scope.paginationConf.itemsPerPage
		};
		//当初始化时需要加载一次， 当查询设置为null时并且返回结果与上次总条数不一致时加载
		$scope.ListAll();
	};
	// 配置分页基本参数
	$scope.paginationConf = {
		currentPage: 1,
		itemsPerPage: 10,
		//totalItems: 50,
		perPageOptions: [10, 20, 30, 40, 50]
	};
	// 通过$watch currentPage和itemperPage 当他们一变化的时候，重新获取数据条目
	$scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', reGetProducts);
}]);

ctmApp.register.controller('DataOptionEdit', ['$http','$scope','$location', function ($http,$scope,$location) {
	//查义所有的操作
	$scope.ListAll=function(){
		var aMethed = 'projectPreReview/ProjectPreReview/getAll';
		$scope.httpData(aMethed,$scope.conf).success(
			function (data, status, headers, config) {
				$scope.ppr = data.result_data;
				$scope.paginationConf.totalItems = data.result_data.length;
			}
		).error(function (data, status, headers, config) {
			alert(status);
		});
	};
	// 重新获取数据条目
	var reGetProducts = function(){
		// 发送给后台的请求数据
		var postData = {
			currentPage:5 ,//$scope.paginationConf.currentPage,
			itemsPerPage:10 //$scope.paginationConf.itemsPerPage
		};
		//当初始化时需要加载一次， 当查询设置为null时并且返回结果与上次总条数不一致时加载
		$scope.ListAll();
	};
	// 配置分页基本参数
	$scope.paginationConf = {
		currentPage: 1,
		itemsPerPage: 10,
		//totalItems: 50,
		perPageOptions: [10, 20, 30, 40, 50]
	};
	// 通过$watch currentPage和itemperPage 当他们一变化的时候，重新获取数据条目
	$scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', reGetProducts);
}]);

ctmApp.register.controller('PreWarningTimeList', ['$http','$scope','$location', function ($http,$scope,$location) {
	//查义所有的操作
	$scope.ListAll=function(){
		var aMethed = 'projectPreReview/ProjectPreReview/getAll';
		$scope.httpData(aMethed,$scope.conf).success(
			function (data, status, headers, config) {
				$scope.ppr = data.result_data;
				$scope.paginationConf.totalItems = data.result_data.length;
			}
		).error(function (data, status, headers, config) {
			alert(status);
		});
	};
	// 重新获取数据条目
	var reGetProducts = function(){
		// 发送给后台的请求数据
		var postData = {
			currentPage:5 ,//$scope.paginationConf.currentPage,
			itemsPerPage:10 //$scope.paginationConf.itemsPerPage
		};
		//当初始化时需要加载一次， 当查询设置为null时并且返回结果与上次总条数不一致时加载
		$scope.ListAll();
	};
	// 配置分页基本参数
	$scope.paginationConf = {
		currentPage: 1,
		itemsPerPage: 10,
		//totalItems: 50,
		perPageOptions: [10, 20, 30, 40, 50]
	};
	// 通过$watch currentPage和itemperPage 当他们一变化的时候，重新获取数据条目
	$scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', reGetProducts);
}]);

ctmApp.register.controller('PreWarningTimeEdit', ['$http','$scope','$location', function ($http,$scope,$location) {
	//查义所有的操作
	$scope.ListAll=function(){
		var aMethed = 'projectPreReview/ProjectPreReview/getAll';
		$scope.httpData(aMethed,$scope.conf).success(
			function (data, status, headers, config) {
				$scope.ppr = data.result_data;
				$scope.paginationConf.totalItems = data.result_data.length;
			}
		).error(function (data, status, headers, config) {
			alert(status);
		});
	};
	// 重新获取数据条目
	var reGetProducts = function(){
		// 发送给后台的请求数据
		var postData = {
			currentPage:5 ,//$scope.paginationConf.currentPage,
			itemsPerPage:10 //$scope.paginationConf.itemsPerPage
		};
		//当初始化时需要加载一次， 当查询设置为null时并且返回结果与上次总条数不一致时加载
		$scope.ListAll();
	};
	// 配置分页基本参数
	$scope.paginationConf = {
		currentPage: 1,
		itemsPerPage: 10,
		//totalItems: 50,
		perPageOptions: [10, 20, 30, 40, 50]
	};
	// 通过$watch currentPage和itemperPage 当他们一变化的时候，重新获取数据条目
	$scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', reGetProducts);
}]);

ctmApp.register.controller('FormalWarningTimeList', ['$http','$scope','$location', function ($http,$scope,$location) {
	//查义所有的操作
	$scope.ListAll=function(){
		var aMethed = 'projectPreReview/ProjectPreReview/getAll';
		$scope.httpData(aMethed,$scope.conf).success(
			function (data, status, headers, config) {
				$scope.ppr = data.result_data;
				$scope.paginationConf.totalItems = data.result_data.length;
			}
		).error(function (data, status, headers, config) {
			alert(status);
		});
	};
	// 重新获取数据条目
	var reGetProducts = function(){
		// 发送给后台的请求数据
		var postData = {
			currentPage:5 ,//$scope.paginationConf.currentPage,
			itemsPerPage:10 //$scope.paginationConf.itemsPerPage
		};
		//当初始化时需要加载一次， 当查询设置为null时并且返回结果与上次总条数不一致时加载
		$scope.ListAll();
	};
	// 配置分页基本参数
	$scope.paginationConf = {
		currentPage: 1,
		itemsPerPage: 10,
		//totalItems: 50,
		perPageOptions: [10, 20, 30, 40, 50]
	};
	// 通过$watch currentPage和itemperPage 当他们一变化的时候，重新获取数据条目
	$scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', reGetProducts);
}]);

ctmApp.register.controller('FormalWarningTimeEdit', ['$http','$scope','$location', function ($http,$scope,$location) {
	//查义所有的操作
	$scope.ListAll=function(){
		var aMethed = 'projectPreReview/ProjectPreReview/getAll';
		$scope.httpData(aMethed,$scope.conf).success(
			function (data, status, headers, config) {
				$scope.ppr = data.result_data;
				$scope.paginationConf.totalItems = data.result_data.length;
			}
		).error(function (data, status, headers, config) {
			alert(status);
		});
	};
	// 重新获取数据条目
	var reGetProducts = function(){
		// 发送给后台的请求数据
		var postData = {
			currentPage:5 ,//$scope.paginationConf.currentPage,
			itemsPerPage:10 //$scope.paginationConf.itemsPerPage
		};
		//当初始化时需要加载一次， 当查询设置为null时并且返回结果与上次总条数不一致时加载
		$scope.ListAll();
	};
	// 配置分页基本参数
	$scope.paginationConf = {
		currentPage: 1,
		itemsPerPage: 10,
		//totalItems: 50,
		perPageOptions: [10, 20, 30, 40, 50]
	};
	// 通过$watch currentPage和itemperPage 当他们一变化的时候，重新获取数据条目
	$scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', reGetProducts);
}]);

ctmApp.register.controller('LawWarningTimeList', ['$http','$scope','$location', function ($http,$scope,$location) {
	//查义所有的操作
	$scope.ListAll=function(){
		var aMethed = 'projectPreReview/ProjectPreReview/getAll';
		$scope.httpData(aMethed,$scope.conf).success(
			function (data, status, headers, config) {
				$scope.ppr = data.result_data;
				$scope.paginationConf.totalItems = data.result_data.length;
			}
		).error(function (data, status, headers, config) {
			alert(status);
		});
	};
	// 重新获取数据条目
	var reGetProducts = function(){
		// 发送给后台的请求数据
		var postData = {
			currentPage:5 ,//$scope.paginationConf.currentPage,
			itemsPerPage:10 //$scope.paginationConf.itemsPerPage
		};
		//当初始化时需要加载一次， 当查询设置为null时并且返回结果与上次总条数不一致时加载
		$scope.ListAll();
	};
	// 配置分页基本参数
	$scope.paginationConf = {
		currentPage: 1,
		itemsPerPage: 10,
		//totalItems: 50,
		perPageOptions: [10, 20, 30, 40, 50]
	};
	// 通过$watch currentPage和itemperPage 当他们一变化的时候，重新获取数据条目
	$scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', reGetProducts);
}]);

ctmApp.register.controller('LawWarningTimeEdit', ['$http','$scope','$location', function ($http,$scope,$location) {
	//查义所有的操作
	$scope.ListAll=function(){
		var aMethed = 'projectPreReview/ProjectPreReview/getAll';
		$scope.httpData(aMethed,$scope.conf).success(
			function (data, status, headers, config) {
				$scope.ppr = data.result_data;
				$scope.paginationConf.totalItems = data.result_data.length;
			}
		).error(function (data, status, headers, config) {
			alert(status);
		});
	};
	// 重新获取数据条目
	var reGetProducts = function(){
		// 发送给后台的请求数据
		var postData = {
			currentPage:5 ,//$scope.paginationConf.currentPage,
			itemsPerPage:10 //$scope.paginationConf.itemsPerPage
		};
		//当初始化时需要加载一次， 当查询设置为null时并且返回结果与上次总条数不一致时加载
		$scope.ListAll();
	};
	// 配置分页基本参数
	$scope.paginationConf = {
		currentPage: 1,
		itemsPerPage: 10,
		//totalItems: 50,
		perPageOptions: [10, 20, 30, 40, 50]
	};
	// 通过$watch currentPage和itemperPage 当他们一变化的时候，重新获取数据条目
	$scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', reGetProducts);
}]);

ctmApp.register.controller('ReviewTeamList', ['$http','$scope','$location', function ($http,$scope,$location) {
	//查义所有的操作
	$scope.ListAll=function(){
		var aMethed = 'projectPreReview/ProjectPreReview/getAll';
		$scope.httpData(aMethed,$scope.conf).success(
			function (data, status, headers, config) {
				$scope.ppr = data.result_data;
				$scope.paginationConf.totalItems = data.result_data.length;
			}
		).error(function (data, status, headers, config) {
			alert(status);
		});
	};
	// 重新获取数据条目
	var reGetProducts = function(){
		// 发送给后台的请求数据
		var postData = {
			currentPage:5 ,//$scope.paginationConf.currentPage,
			itemsPerPage:10 //$scope.paginationConf.itemsPerPage
		};
		//当初始化时需要加载一次， 当查询设置为null时并且返回结果与上次总条数不一致时加载
		$scope.ListAll();
	};
	// 配置分页基本参数
	$scope.paginationConf = {
		currentPage: 1,
		itemsPerPage: 10,
		//totalItems: 50,
		perPageOptions: [10, 20, 30, 40, 50]
	};
	// 通过$watch currentPage和itemperPage 当他们一变化的时候，重新获取数据条目
	$scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', reGetProducts);
}]);

ctmApp.register.controller('ReviewTeamEdit', ['$http','$scope','$location', function ($http,$scope,$location) {
	//查义所有的操作
	$scope.ListAll=function(){
		var aMethed = 'projectPreReview/ProjectPreReview/getAll';
		$scope.httpData(aMethed,$scope.conf).success(
			function (data, status, headers, config) {
				$scope.ppr = data.result_data;
				$scope.paginationConf.totalItems = data.result_data.length;
			}
		).error(function (data, status, headers, config) {
			alert(status);
		});
	};
	// 重新获取数据条目
	var reGetProducts = function(){
		// 发送给后台的请求数据
		var postData = {
			currentPage:5 ,//$scope.paginationConf.currentPage,
			itemsPerPage:10 //$scope.paginationConf.itemsPerPage
		};
		//当初始化时需要加载一次， 当查询设置为null时并且返回结果与上次总条数不一致时加载
		$scope.ListAll();
	};
	// 配置分页基本参数
	$scope.paginationConf = {
		currentPage: 1,
		itemsPerPage: 10,
		//totalItems: 50,
		perPageOptions: [10, 20, 30, 40, 50]
	};
	// 通过$watch currentPage和itemperPage 当他们一变化的时候，重新获取数据条目
	$scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', reGetProducts);
}]);
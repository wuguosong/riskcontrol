ctmApp.register.controller('PersonnelWork', ['$http','$scope','$location','$routeParams','$filter', function ($http,$scope,$location,$routeParams,$filter) {
	//保存传递的参数
	$scope.type = $routeParams.type;
	$scope.id = $routeParams.id;
	$scope.tabIndex = $routeParams.tabIndex;
	$scope.lx = $routeParams.lx;
	$scope.flag = $routeParams.flag;
	$scope.paginationConf.queryObj={};
	$scope.paginationConf1 = {
        lastCurrentTimeStamp:'',
        currentPage: 1,
        totalItems: 0,
        itemsPerPage: 10,
        pagesLength: 10,
        perPageOptions: [10, 20, 30, 40, 50],
        onChange: function(){
        },
		queryObj:{}
    };
	$scope.paginationConf2 = {
		lastCurrentTimeStamp:'',
		currentPage: 1,
		totalItems: 0,
		itemsPerPage: 10,
		pagesLength: 10,
		perPageOptions: [10, 20, 30, 40, 50],
		onChange: function(){
		},
		queryObj:{}
	};
	$scope.paginationConf3 = {
		lastCurrentTimeStamp:'',
		currentPage: 1,
		totalItems: 0,
		itemsPerPage: 10,
		pagesLength: 10,
		perPageOptions: [10, 20, 30, 40, 50],
		onChange: function(){
		},
		queryObj:{}
	};
	$scope.paginationConf4 = {
		lastCurrentTimeStamp:'',
		currentPage: 1,
		totalItems: 0,
		itemsPerPage: 10,
		pagesLength: 10,
		perPageOptions: [10, 20, 30, 40, 50],
		onChange: function(){
		},
		queryObj:{}
	};
	$scope.paginationConf5 = {
		lastCurrentTimeStamp:'',
		currentPage: 1,
		totalItems: 0,
		itemsPerPage: 10,
		pagesLength: 10,
		perPageOptions: [10, 20, 30, 40, 50],
		onChange: function(){
		},
		queryObj:{}
	};
	
	//正式评审跟进中
	$scope.getFormalGoing= function(){
		$scope.paginationConf.queryObj.projectName = $scope.projectName;
		$scope.paginationConf.queryObj.type = $scope.type;
		$scope.paginationConf.queryObj.id = $scope.id;
		$scope.paginationConf.queryObj.stage = '3,3.5,3.7,3.9,4,5';
		$scope.paginationConf.queryObj.pType = 'formal';
		$scope.paginationConf.queryObj.lx = $scope.lx;
		$http({
			method:'post',  
		    url:srvUrl+"deptwork/queryProjects.do",
		    data: $.param({"page":JSON.stringify($scope.paginationConf)})
		}).success(function(result){
			if(result.success){
				  $scope.projectReport = result.result_data.list;
		          $scope.paginationConf.totalItems = result.result_data.totalItems;
			}else{
				$.alert(result.result_name);
			}
		});
	};
	//正式评审已上会
	$scope.getFormalNoticed= function(){
		$scope.paginationConf1.queryObj.projectName = $scope.projectName;
		$scope.paginationConf1.queryObj.type = $scope.type;
		$scope.paginationConf1.queryObj.id = $scope.id;
		$scope.paginationConf1.queryObj.stage = '6,7,9';
		$scope.paginationConf1.queryObj.pType = 'formal';
		$scope.paginationConf1.queryObj.lx = $scope.lx;
		$http({
			method:'post',  
		    url:srvUrl+"deptwork/queryProjects.do",
		    data: $.param({"page":JSON.stringify($scope.paginationConf1)})
		}).success(function(result){
			if(result.success){
				  $scope.projectReport1 = result.result_data.list;
		          $scope.paginationConf1.totalItems = result.result_data.totalItems;
			}else{
				$.alert(result.result_name);
			}
		});
	};
	//投标评审跟进中
	$scope.getPreGoing= function(){
		$scope.paginationConf2.queryObj.projectName = $scope.projectName;
		$scope.paginationConf2.queryObj.type = $scope.type;
		$scope.paginationConf2.queryObj.id = $scope.id;
		$scope.paginationConf2.queryObj.stage = '3,3.5,3.7,3.9,4,5';
		$scope.paginationConf2.queryObj.pType = 'pre';
		$scope.paginationConf2.queryObj.lx = $scope.lx;
		$http({
			method:'post',  
		    url:srvUrl+"deptwork/queryProjects.do",
		    data: $.param({"page":JSON.stringify($scope.paginationConf2)})
		}).success(function(result){
			if(result.success){
				  $scope.projectReport2 = result.result_data.list;
		          $scope.paginationConf2.totalItems = result.result_data.totalItems;
			}else{
				$.alert(result.result_name);
			}
		});
	};
	//投标评审已决策
	$scope.getPreNoticed= function(){
		$scope.paginationConf3.queryObj.projectName = $scope.projectName;
		$scope.paginationConf3.queryObj.type = $scope.type;
		$scope.paginationConf3.queryObj.id = $scope.id;
		$scope.paginationConf3.queryObj.stage = '6,7,9';
		$scope.paginationConf3.queryObj.pType = 'pre';
		$scope.paginationConf3.queryObj.lx = $scope.lx;
		$http({
			method:'post',  
		    url:srvUrl+"deptwork/queryProjects.do",
		    data: $.param({"page":JSON.stringify($scope.paginationConf3)})
		}).success(function(result){
			if(result.success){
				  $scope.projectReport3 = result.result_data.list;
		          $scope.paginationConf3.totalItems = result.result_data.totalItems;
			}else{
				$.alert(result.result_name);
			}
		});
	};
	//其他评审跟进中
	$scope.getBulletinGoing= function(){
		$scope.paginationConf4.queryObj.projectName = $scope.projectName;
		$scope.paginationConf4.queryObj.type = $scope.type;
		$scope.paginationConf4.queryObj.id = $scope.id;
		$scope.paginationConf4.queryObj.stage = '1.5,2,3';
		$scope.paginationConf4.queryObj.pType = 'bulletin';
		$scope.paginationConf4.queryObj.lx = $scope.lx;
		$http({
			method:'post',  
		    url:srvUrl+"deptwork/queryProjects.do",
		    data: $.param({"page":JSON.stringify($scope.paginationConf4)})
		}).success(function(result){
			if(result.success){
				  $scope.projectReport4 = result.result_data.list;
		          $scope.paginationConf4.totalItems = result.result_data.totalItems;
			}else{
				$.alert(result.result_name);
			}
		});
	};
	//其他评审已决策
	$scope.getBulletinNoticed= function(){
		$scope.paginationConf5.queryObj={};
		$scope.paginationConf5.queryObj.projectName = $scope.projectName;
		$scope.paginationConf5.queryObj.type = $scope.type;
		$scope.paginationConf5.queryObj.id = $scope.id;
		$scope.paginationConf5.queryObj.stage = '4,5';
		$scope.paginationConf5.queryObj.pType = 'bulletin';
		$scope.paginationConf5.queryObj.lx = $scope.lx;
		$http({
			method:'post',  
		    url:srvUrl+"deptwork/queryProjects.do",
		    data: $.param({"page":JSON.stringify($scope.paginationConf5)})
		}).success(function(result){
			if(result.success){
				  $scope.projectReport5 = result.result_data.list;
		          $scope.paginationConf5.totalItems = result.result_data.totalItems;
			}else{
				$.alert(result.result_name);
			}
		});
	};
	
	$scope.selectByCondition = function(){
		var activeLi = $(".bs-tabdrop-example li[class=active]");
		var index = activeLi.index();
		if(0==index){
			$scope.getFormalGoing();
		}else if(1==index){
			$scope.getFormalNoticed();
		}else if(2==index){
			$scope.getPreGoing();
		}else if(3==index){
			$scope.getPreNoticed();
		}else if(4==index){
			$scope.getBulletinGoing();
		}else if(5==index){
			$scope.getBulletinNoticed();
		}
	}
	
    $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.getFormalGoing);
    $scope.$watch('paginationConf1.currentPage + paginationConf1.itemsPerPage', $scope.getFormalNoticed);
    $scope.$watch('paginationConf2.currentPage + paginationConf2.itemsPerPage', $scope.getPreGoing);
    $scope.$watch('paginationConf3.currentPage + paginationConf3.itemsPerPage', $scope.getPreNoticed);
    $scope.$watch('paginationConf4.currentPage + paginationConf4.itemsPerPage', $scope.getBulletinGoing);
    $scope.$watch('paginationConf5.currentPage + paginationConf5.itemsPerPage', $scope.getBulletinNoticed);
    
}]);

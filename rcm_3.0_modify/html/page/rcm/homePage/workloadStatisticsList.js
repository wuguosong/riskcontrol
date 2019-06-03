/********
 * 工作量统计
 *********/
ctmApp.register.controller('workloadStatisticsList', ['$http','$scope','$location', function ($http,$scope,$location) {
	$scope.ishow1 ='false';
	$scope.ishow2 ='false';
	$scope.ishow3 ='false';
	$scope.ishow4 ='false';
	$scope.ishow5 ='false';
	$scope.ishow6 ='false';
    var uuid=$scope.credentials.UUID;
    $scope.import = function(){
        var url = 'formalAssessment/ProjectFormalReview/importFormalAssessmentReport';
        $scope.httpData(url,$scope.credentials.UUID).success(function (data) {
            if (data.result_code=="S") {
                var files=data.result_data.filePath;
                var fileName=data.result_data.fileName;
                window.location.href = srvUrl+"common/RcmFile/downLoad?filePath="+encodeURI(files)+"&fileName="+encodeURI(fileName);;

            } else {
            	$.alert("生成失败");
            }
        });
    }
    //查义所有的操作
    $scope.ListAll=function(){
        $scope.conf={currentPage:$scope.paginationConf.currentPage,pageSize:$scope.paginationConf.itemsPerPage,queryObj:{reader:$scope.credentials.UUID}}
        var aMethed = 'projectPreReview/ListNotice/listUnreadNotice';
        $scope.httpData(aMethed,$scope.conf).success(
            function (data, status, headers, config) {
                if(data.result_code == "S") {
                    $scope.unreadnotice = data.result_data.list;
                    $scope.unreadnoticeCount = data.result_data.totalItems;
                }
            }
        ).error(function (data, status, headers, config) {
            alert(status);
        });
    };
    
    $scope.initializeCount=function(){
    	 $http({
 			method:'post',  
 		    url: srvUrl + "deptwork/initializeCount.do"
 		}).success(function(result){
 			$scope.formalApplyingCount = result.result_data.formalApplyingCount;
 			$scope.preApplyingCount = result.result_data.preApplyingCount;
 			$scope.bulletinApplyingCount = result.result_data.bulletinApplyingCount;
 			$scope.formalEndCount = result.result_data.formalEndCount;
 			$scope.preEndCount = result.result_data.preEndCount;
 			$scope.bulletinEndCount = result.result_data.bulletinEndCount;
 			$scope.ypsCount = result.result_data.ypsCount;
 			$scope.zspsCount = result.result_data.zspsCount;
 			$scope.qtjcCount = result.result_data.qtjcCount;
 			 
 			$scope.ypswghCount = result.result_data.ypswghCount;
			$scope.ypsyghCount = result.result_data.ypsyghCount;
			 
			$scope.zsswghCount = result.result_data.zsswghCount;
			$scope.zssyghCount = result.result_data.zssyghCount;
			 
 			$scope.qtjcwghCount = result.result_data.qtjcwghCount;
 			$scope.qtjcyghCount = result.result_data.qtjcyghCount;
 			 
 			$scope.tzjcCount = result.result_data.tzjcCount;
			$scope.tzjcWxshCount = result.result_data.tzjcWxshCount;
			$scope.tzjcXshCount = result.result_data.tzjcXshCount;
 		});
    	 
    };
    $scope.getProjectReport=function(){
        $http({
    		method:'post',  
    	    url: srvUrl + "deptwork/getProjectReport.do"
    	}).success(function(result){
    		 $scope.projectReport0922ByYw = result.result_data.getProjectReport0922ByYw;
    		 $scope.projectReport0922ByFL = result.result_data.getProjectReport0922ByFL;
    		 $scope.queryPsfzrUsers = result.result_data.queryPsfzrUsers;
    		 $scope.queryFlfzrUsers = result.result_data.queryFlfzrUsers;
    		 
    	});
    };
    
    $scope.listUnreadWarning=function(){
        $scope.conf={currentPage:$scope.paginationConf.currentPage,pageSize:$scope.paginationConf.itemsPerPage,queryObj:{reader:$scope.credentials.UUID}}
        var aMethed = 'projectPreReview/ListNotice/listUnreadWarning';
        $scope.httpData(aMethed,$scope.conf).success(
            function (data, status, headers, config) {
                if(data.result_code == "S") {
                    $scope.unreadwarning = data.result_data.list;
                    $scope.unreadwarningCount = data.result_data.totalItems;
                }
            }
        ).error(function (data, status, headers, config) {
            alert(status);
        });
    };
    $scope.listAllMyTask = function(){
        $scope.paginationConf.queryObj = {assignee:$scope.credentials.UUID};
        $scope.httpData('bpm/WorkFlow/getMyTask',$scope.paginationConf).success(function(data){
            if(data.result_code == "S"){
                $scope.tasks = data.result_data.list;
                $scope.MyTask = data.result_data.totalItems;
                $scope.paginationConf.totalItems = data.result_data.totalItems;
            }
        });
    }
    // 配置分页基本参数
    $scope.paginationConf = {
        currentPage: 1,
		// 修改已办代办默认每页展示数
        itemsPerPage: 10,
        perPageOptions: [5, 10]
    };
    //初始化
    angular.element(document).ready(function() {
    	$scope.initializeCount();
        $scope.getProjectReport();
    });

    
    $scope.queryFZR = function($event,uuid) {
    	$http({
			method: 'post',
			url: srvUrl + "deptwork/showFzr.do",
			data: $.param({"uuid":uuid})
		}).success(function(result){
			var data = result.result_data.data;
			var html  ;
			var inderHtml ;
			//张三</a></td><td colspan="2" align="left"><span >113</span></td><td colspan="2" align="left"><span>313</span></td><td colspan="2" align="left"><span>333</span></td></tr><tr id="'+uuid+'ls"><td  align="left"><a href="">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;栗色</a></td><td colspan="2" align="left"><span >113</span></td><td colspan="2" align="left"><span >22</span></td><td colspan="2" align="left"><span>313</span></td></tr>';
			for (var i = 0; i < data.length; i++) {
				var name = data[i].TEAM_MEMBER_NAME;
				var wgh = data[i].FORMAL_GOING + data[i].PRE_GOING + data[i].BULLETIN_GOING;
				var ywc = data[i].FORMAL_DEALED + data[i].PRE_DEALED+ data[i].BULLETIN_DEALED;
				var total = data[i].TOTAL_NUM;
				inderHtml = '<tr name="'+uuid+'zs">' + '<td align="left">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#/PersonnelWork/user/'+data[i].USERID+'/YW/0/1">' + name + '</a></td>'
						+ '<td colspan="2" align="left"><span >'+ wgh +'</span></td>'
						+ '<td colspan="2" align="left"><span>' + ywc + '</span></td>' 
						+ '<td colspan="2" align="left"><span>' + total + '</span></td></tr>' ;
				html = html + inderHtml;
			}
			$('#'+uuid).after(html);
	    	$($event.target).hide();
	    	$('#'+uuid+'sq').show();
		});
    	
    	
    	
    };
    
    $scope.queryFlFZR = function($event,uuid) {
    	$http({
			method: 'post',
			url: srvUrl + "deptwork/showFlFzr.do",
			data: $.param({"uuid":uuid})
		}).success(function(result){
			var data = result.result_data.data;
			var html  ;
			var inderHtml ;
			//张三</a></td><td colspan="2" align="left"><span >113</span></td><td colspan="2" align="left"><span>313</span></td><td colspan="2" align="left"><span>333</span></td></tr><tr id="'+uuid+'ls"><td  align="left"><a href="">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;栗色</a></td><td colspan="2" align="left"><span >113</span></td><td colspan="2" align="left"><span >22</span></td><td colspan="2" align="left"><span>313</span></td></tr>';
			for (var i = 0; i < data.length; i++) {
				var name = data[i].TEAM_MEMBER_NAME;
				var wgh = data[i].FORMAL_GOING + data[i].BULLETIN_GOING;
				var ywc = data[i].FORMAL_DEALED + data[i].BULLETIN_DEALED;
				var total = data[i].TOTAL_NUM;
				inderHtml = '<tr name="'+uuid+'zs">' + '<td align="left"><a href="#/PersonnelWork/user/'+data[i].USERID+'/FL/0/1">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;' + name + '</a></td>'
						+ '<td colspan="2" align="left"><span >'+ wgh +'</span></td>'
						+ '<td colspan="2" align="left"><span>' + ywc + '</span></td>' 
						+ '<td colspan="2" align="left"><span>' + total + '</span></td></tr>' ;
				html = html + inderHtml;
			}
			$('#'+uuid).after(html);
	    	$($event.target).hide();
	    	$('#'+uuid+'sq').show();
		});
    	
    	
    	
    };
    
    $scope.sqFZR = function($event,uuid) {
    	//$('#'+uuid+'zs').remove();
    	$("tr[name='"+uuid+"zs']").remove();
    	//$('#'+uuid+'ls').remove();
    	$($event.target).hide();
    	$('#'+uuid+'zk').show();
    };
    
	$scope.rounding = function keepTwoDecimalFull(num) {
		  var result = parseFloat(num);
		  if (isNaN(result)) {
		    alert('传递参数错误，请检查！');
		    return false;
		  }
		  result = Math.round(num * 100) / 100;
		  var s_x = result.toString();
		  var pos_decimal = s_x.indexOf('.');
		  if (pos_decimal < 0) {
		    pos_decimal = s_x.length;
		    s_x += '.';
		  }
		  while (s_x.length <= pos_decimal + 2) {
		    s_x += '0';
		  }
		  return s_x;
		}


    $scope.yearArrFunc = function(){
		$http({
			method:'post',  
		    url: srvUrl + "deptwork/getYearArr.do"
		}).success(function(result){
			if(result.success){
				$scope.yearArr = result.result_data.yearArr;
			}
			
		});
	}
	$scope.yearArrFunc();
}]);
ctmApp.register.controller('ExperienceList', ['$http','$scope','$location', 
function ($http,$scope,$location) {
	if($scope.paginationConf.queryObj==null || $scope.paginationConf.queryObj == ''){
		$scope.paginationConf.queryObj = {};
	}
	if($scope.paginationConfes.queryObj==null || $scope.paginationConfes.queryObj == ''){
		$scope.paginationConfes.queryObj = {};
	}
    $scope.ppr={};
    $scope.pprAll={};
    $scope.orderby=-1;
    //查义所有的操作
    $scope.listExperience = function () {
        var aMethed = 'common/commonMethod/getProjectFormalReviewList';
        $scope.conf={user_id:$scope.credentials.UUID,'pfrExperience':"true"};
        $scope.httpData(aMethed,  $scope.conf).success(
            function (data, status, headers, config) {
                $scope.pprs = data.result_data;
                //console.log( data.result_data);
            }
        ).error(function (data, status, headers, config) {
            alert(status);
        });
    };

    $scope.ListAll = function () {
        $scope.conf={currentPage:$scope.paginationConf.currentPage,itemsPerPage:$scope.paginationConf.itemsPerPage,
            'ORDERVAL':$scope.orderby,
            'projectName':$scope.paginationConf.queryObj.projectName,
            'createName':$scope.paginationConf.queryObj.createName,
            'create_date':$scope.paginationConf.queryObj.create_date,
            'createId':$scope.credentials.UUID,
            'state':$scope.paginationConf.queryObj.state
        };
        var aMethod= 'projectPreReview/Feedback/listFormaltName';
        $scope.httpData(aMethod, $scope.conf).success(
            function (data, status, headers, config) {
                $scope.ppr = data.result_data.list;
                $scope.paginationConf.totalItems = data.result_data.totalItem;
            }
        ).error(function (data, status, headers, config) {
            alert(status);
        });
    };
    $scope.ListAll2 = function () {
        $scope.conf={currentPage:$scope.paginationConfes.currentPage,itemsPerPage:$scope.paginationConfes.itemsPerPage,
            'ORDERVAL':$scope.orderby,
            'projectName':$scope.paginationConfes.queryObj.projectName,
            'createName':$scope.paginationConfes.queryObj.createName,
            'create_date':$scope.paginationConfes.queryObj.create_date,
            'state':'2'
        };
        var aMethod= 'projectPreReview/Feedback/listFormaltName';
        $scope.httpData(aMethod, $scope.conf).success(
            function (data, status, headers, config) {
                $scope.pprAll = data.result_data.list;
                $scope.paginationConfes.totalItems = data.result_data.totalItem;
            }
        ).error(function (data, status, headers, config) {
            alert(status);
        });
    };

    // 删除操作
    $scope.Delete = function () {

        var chk_list = document.getElementsByName("checkbox");
        var uid = "", num = 0;
        for (var i = 0; i < chk_list.length; i++) {
            if (chk_list[i].checked) {
                num++;
                uid = uid + ',' + chk_list[i].value;
            }
        }
        if (uid != '') {
            uid = uid.substring(1, uid.length);
        }
        if (num == 0) {
        	$.alert("请选择其中一条或多条数据删除！");
            return false;
        }
        var obj = {"_id": uid};
        var aMethed = 'projectPreReview/Feedback/deleteExperience';
        $scope.httpData(aMethed, obj).success(
            function (data, status, headers, config) {
                $scope.ListAll();
                $.alert("删除成功！");
            }
        ).error(function (data, status, headers, config) {
            alert(status);
        });
    };
    $scope.update = function (state) {
        var chk_list = document.getElementsByName("checkbox");
        var uid = "", num = 0;
        for (var i = 0; i < chk_list.length; i++) {
            if (chk_list[i].checked) {
                num++;
                uid = uid + ',' + chk_list[i].value;
            }
        }
        if (uid != '') {
            uid = uid.substring(1, uid.length);
        }
        if (num == 0) {
        	$.alert("请选择其中一条或多条数据编辑！");
            return false;
        }
        if (num > 1) {
        	$.alert("只能选择其中一条数据进行编辑！");
            return false;
        } else {
        	var str = window.btoa(encodeURIComponent(escape("#/ExperienceList")))
            $location.path("/Experience/Update/" + uid+"/"+str);
        }	
    }
    $scope.order=function(v){
        $scope.orderby=v;
        if(v==1){
            $("#orderasc").addClass("cur");
            $("#orderdesc").removeClass("cur");
        }else{
            $("#orderdesc").addClass("cur");
            $("#orderasc").removeClass("cur");
        }
        $scope.ListAll();
    }
    $scope.import=function(){
    	var obj = {
                'ORDERVAL':$scope.orderby,
                'projectName':$scope.paginationConfes.queryObj.projectName,
                'create_date':$scope.paginationConfes.queryObj.create_date
            };
        var aMethed =  'projectPreReview/Feedback/feekbackListReport';
        $scope.httpData(aMethed,obj).success(
            function (data) {
                if(data.result_code=="S"){
                    var file=data.result_data.filePath;
                    var fileName=data.result_data.fileName;
                    window.location.href = srvUrl+"common/RcmFile/downLoad?filePath="+encodeURI(file)+"&fileName="+encodeURI(fileName);
                }
            }

        ).error(function (data, status, headers, config) {
            alert(status);
        });
    }
    $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.ListAll);
    $scope.$watch('paginationConfes.currentPage + paginationConfes.itemsPerPage', $scope.ListAll2);

    //$scope.ListAll();
}]);
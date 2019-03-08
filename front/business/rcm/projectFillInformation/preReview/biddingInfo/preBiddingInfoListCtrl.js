define(['app', 'Service', 'Filter'], function (app) {
    app
        .register.controller('preBiddingInfoListCtrl', ['$http', '$scope', '$location', '$stateParams', 'BEWG_URL', 'Window',
        function ($http, $scope, $location, $stateParams, BEWG_URL, Window) {
            console.log('preBiddingInfoList');

            $scope.tabIndex = $stateParams.tabIndex;
//	$scope.queryObject = {};
//	$scope.hi_queryObject = {};
           /* if($scope.paginationConf.queryObj == null || $scope.paginationConf.queryObj == ''){
                $scope.paginationConf.queryObj = {};
            }
            if($scope.paginationConfes.queryObj == null || $scope.paginationConfes.queryObj == ''){
                $scope.paginationConfes.queryObj = {};
            }*/

            //初始化页面所需数据
            $scope.initData = function(){
                $scope.queryUncommittedByPage();
                $scope.querySubmittedByPage();
            }

            //获取待提交评审报告的项目
            $scope.queryUncommittedByPage = function(){
//		$scope.dataJson = {
//				projectName:$scope.queryObject.PROJECT_NAME,
//				createBy:$scope.queryObject.CREATEBY,
//				pertainareaname:$scope.queryObject.PERTAINAREANAME
//		}
                $http({
                    method:'post',
                    url: BEWG_URL.SelectAllBiddlingPre,
                    data: $.param({
                        "page":JSON.stringify($scope.paginationConf),
                        "json":JSON.stringify($scope.paginationConf.queryObj)
                    })
                }).success(function(data){
                    if(data.success){
                        $scope.uncommittedReport = data.result_data.list;
                        $scope.paginationConf.totalItems = data.result_data.totalItems;
                    }else{
                        Window.alert(data.result_name);
                    }
                }).error(function(data,status,headers,config){
                    Window.alert(status);
                });
            };

            //获取已提交评审报告的项目
            $scope.querySubmittedByPage = function(){
//    	$scope.dataJson = {
//    			projectName:$scope.queryObject.HI_PROJECT_NAME,
//    			createBy:$scope.hi_queryObject.HI_CREATEBY,
//    			pertainareaname:$scope.hi_queryObject.HI_PERTAINAREANAME
//    	}
                $http({
                    method:'post',
                    url: BEWG_URL.SelectAllSubmitBiddlingPre,
                    data: $.param({
                        "page":JSON.stringify($scope.paginationConfes),
                        "json":JSON.stringify($scope.paginationConfes.queryObj)
                    })
                }).success(function(data){
                    if(data.success){
                        $scope.submittedReport = data.result_data.list;
                        $scope.paginationConfes.totalItems = data.result_data.totalItems;
                    }else{
                        Window.alert(data.result_name);
                    }
                }).error(function(data,status,headers,config){
                    Window.alert(status);
                });
            };

            $scope.toFormalBiddingInfo = function(){
                var chkObjs = $("input[type=checkbox][name=uncommittedDecisionMaterialCheckbox]:checked");
                if(chkObjs.length == 0){
                    Window.alert("请选择要提交的数据！");
                    return false;
                }
                if(chkObjs.length > 1){
                    Window.alert("请只选择一条数据进行提交!");
                    return false;
                }
                var idsStr = "";
                for(var i = 0; i < chkObjs.length; i++){
                    var chkValue = chkObjs[i].value.split("/") ;
                    var chkValue_len = chkValue.length;
                    idsStr = idsStr + chkValue[chkValue_len - 1];
                }
                $location.path("/index/PreBiddingInfo/"+idsStr+"@0/"+$filter('encodeURI')('#/PreBiddingInfoList/0'));
            }

            // 跳转报告详情页面
            $scope.toBiddingInfo = function (id, flag) {
                /*href="#/PreBiddingInfo/{{p.BUSINESSID}}@2/{{'#/PreBiddingInfoList/0'|encodeURI}}"
                * href="#/PreBiddingInfoView/{{p.BUSINESSID}}@view/{{'#/PreBiddingInfoList/1'|encodeURI}}"*/
                if (flag == 0) {
                    $location.path("/index/PreBiddingInfo/" + id + "@2");
                } else {
                    $location.path("/index/PreBiddingInfoView/" + id + "@view");
                }
            };

            // 配置分页基本参数
            $scope.paginationConf = {
                currentPage: 1,
                itemsPerPage: 10,
                perPageOptions: [10, 20, 30, 40, 50],
                queryObj: {}
            };
            $scope.paginationConfes = {
                currentPage: 1,
                itemsPerPage: 10,
                perPageOptions: [10, 20, 30, 40, 50],
                queryObj: {}
            };
            // 通过$watch currentPage和itemperPage 当他们一变化的时候，重新获取数据条目
            $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage',  $scope.queryUncommittedByPage);
            $scope.$watch('paginationConfes.currentPage + paginationConfes.itemsPerPage',  $scope.querySubmittedByPage);

            $scope.initData();
        }]);
});
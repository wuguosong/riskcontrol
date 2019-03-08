define(['app', 'Service'], function (app) {
    app
        .register.controller('formalBiddingInfoListCtrl', ['$http', '$scope', '$location', '$stateParams', 'BEWG_URL', 'Window',
        function ($http, $scope, $location, $stateParams, BEWG_URL, Window) {
            console.log('formalBiddingInfoList');

            $scope.tabIndex = $stateParams.tabIndex;
            /*if($scope.paginationConf.queryObj == null || $scope.paginationConf.queryObj == ''){
                $scope.paginationConf.queryObj = {};
            }
            if($scope.paginationConfes.queryObj == null || $scope.paginationConfes.queryObj == ''){
                $scope.paginationConfes.queryObj = {};
            }*/

            //初始化页面所需数据
            $scope.initData = function(){
                $scope.initUncommittedDecisionMaterial();
                $scope.initSubmittedDecisionMaterial();
            }

            //获取待提交评审报告的项目
            $scope.initUncommittedDecisionMaterial = function(){
//		$scope.dataJson = {
//				projectName:$scope.queryObject.PROJECT_NAME,
//				createBy:$scope.queryObject.CREATEBY,
//				pertainareaname:$scope.queryObject.PERTAINAREANAME
//		}
                $http({
                    method:'post',
                    url: BEWG_URL.SelectAllBiddlingPfr,
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
            $scope.initSubmittedDecisionMaterial = function(){
//    	$scope.dataJson = {
//    			projectName:$scope.queryObject.HI_PROJECT_NAME,
//    			createBy:$scope.hi_queryObject.HI_CREATEBY,
//    			pertainareaname:$scope.hi_queryObject.HI_PERTAINAREANAME
//    	}
                /*show_Mask();*/
                $http({
                    method:'post',
                    url: BEWG_URL.SelectAllSubmitBiddlingPfr,
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
                    /*hide_Mask();*/
                }).error(function(data, status, headers, config){
                    /*hide_Mask();*/
                });
            };

            $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage',  $scope.initUncommittedDecisionMaterial);
            $scope.$watch('paginationConfes.currentPage + paginationConfes.itemsPerPage',  $scope.initSubmittedDecisionMaterial);

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

                $location.path("/FormalBiddingInfo/"+idsStr+"@0/0");
            };

            /**
             * 查询项目部分信息，查看是新项目还是老项目，决定跳转路径
             * */
            $scope.getInfo = function (id) {
                $http({
                    method: 'post',
                    url: BEWG_URL.SelectFormalAndReport,
                    data: $.param({"projectFormalId": id})
                }).success(function (data) {
                    $scope.projectSummary = data.result_data.summary;
                    $scope.stage = data.result_data.stage;

                    console.log($scope.stage)
                    /*var path = $filter('encodeURI')('#/FormalBiddingInfoList/1');*/
                    /*if ($scope.projectSummary == null || $scope.projectSummary == undefined){
                        $location.path("/FormalBiddingInfo_view/"+ id + "/" + path);
                    } else {
                        $location.path("/FormalBiddingInfoPreview/"+ id + "/" + path + "/3");
                    }*/
                    // #/FormalBiddingInfo/5afcc2e6ddd03412cebef6e5@2/JTI1MjMlMkZGb3JtYWxCaWRkaW5nSW5mb0xpc3QlMkYw/0
                    if ($scope.projectSummary == null || $scope.projectSummary == undefined){
                        $location.path("/index/FormalBiddingInfo_view/"+ id );
                    } else if ($scope.stage == 7) {
                        $location.path("/index/FormalBiddingInfoPreview/"+ id + "/3");
                    } else {
                        $location.path("/index/FormalBiddingInfo/"+ id + "/3");
                    }
                }).error(function (data, status, header, config) {
                    Window.alert(status);
                });
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
            $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage',  $scope.initUncommittedDecisionMaterial);
            $scope.$watch('paginationConfes.currentPage + paginationConfes.itemsPerPage',  $scope.initSubmittedDecisionMaterial);

            $scope.initData();
        }]);
});
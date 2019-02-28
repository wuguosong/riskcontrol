define(['app', 'Service'], function (app) {
    app
        .register.controller('manageAttachmentListCtrl', ['$http', '$scope', '$location', '$stateParams', 'BEWG_URL', 'Window',
        function ($http, $scope, $location, $stateParams, BEWG_URL, Window) {
            console.log('manageAttachmentList');

            $scope.conf={};

            var Buuid = $stateParams.yuuid;
            var Bbusiness_name = $stateParams.ybusiness_name;
            var business_type = $stateParams.business_type;
            var bitem_type = $stateParams.bitem_type;

            //初始化
            $scope.conf.UUID=Buuid;
            $scope.conf.BUSINESS_NAME=Bbusiness_name;
            $scope.conf.BUSINESS_TYPE=business_type;
            $scope.conf.ITEM_TYPE=bitem_type;


            //查义所有的操作
            $scope.queryList=function(){

                if($scope.paginationConf.currentPage === 1){
                    //如果当前页为第一页，则此时请求$scope.ListAll();不会触发两次操作
                    $scope.ListAll();
                }else{
                    //如果当前页不是第一页，则可以通过修改currentPage来触发$scope.ListAll();
                    $scope.paginationConf.currentPage = 1;
                }
            };



            $scope.selectType = function(code){
                $scope.httpData(BEWG_URL.SelectAllBusinessType,code).success(function(data){
                    if(code=='04'){
                        $scope.allname  = data.result_data;
                    }
                });
            }

            $scope.ListAll=function(uuid){
                $("#yuuid").val(Buuid);
                $("#ybusiness_name").val(Bbusiness_name);
                $("#business_type").val(business_type);
                $("#bitem_type").val(bitem_type);


                //var item_type=$("ITEM_TYPE").val();

                $scope.conf={currentPage:$scope.paginationConf.currentPage,pageSize:$scope.paginationConf.itemsPerPage,queryObj:{'ITEM_CODE':$scope.ITEM_CODE,'ITEM_NAME':$scope.ITEM_NAME}};
                $scope.httpData(BEWG_URL.SelectAllManageAttachment,$scope.conf).success(function(data){
                    $("#yuuid").val(Buuid);
                    $("#ybusiness_name").val(Bbusiness_name);
                    $("#business_type").val(business_type);
                    $("#bitem_type").val(bitem_type);
                    // 变更分页的总数
                    $scope.item  = data.result_data.list;
                    $scope.paginationConf.totalItems = data.result_data.totalItems;
                });
                //$scope.selectType('01');
                $scope.selectType('04');
            };

            //添加用户到角色
            $scope.addAttachment = function (){
                var Buuid = $("#yuuid").val();
                var Bbusiness_name = $("#ybusiness_name").val();
                var business_type = $("#business_type").val();
                var item_type = $scope.conf.ITEM_TYPE;
                var chk_list=document.getElementsByName("checkbox");
                var uid = "",num=0;
                var postObj;
                for(var i=0;i<chk_list.length;i++)
                {
                    if(chk_list[i].checked)
                    {
                        num++;
                        uid = uid+','+chk_list[i].value;
                    }
                }
                if(uid!=''){
                    uid=uid.substring(1,uid.length);
                }
                if(num==0){
                   Window.alert("请选择其中一条或多条数据！");
                    return false;
                }else {
                    statevar = {"UUID": uid, BUUID: Buuid,BBUSINESS_NAME:Bbusiness_name,"BUSINESS_TYPE":item_type,"DIC_TYPE":business_type};
                    $scope.httpData(BEWG_URL.AddBusinessAttachment, statevar).success(
                        function (data) {
                            if(data.result_code=="S"){
                                $location.path("/index/ListBusiness/"+Buuid+"/"+Bbusiness_name+"/"+business_type);
                            }else{
                                alert(data.result_name);
                            }

                        }
                    );
                }
            };

            // 返回列表
            $scope.cancel = function (){
                $location.path("/index/BusinessModelList");
            }

            // 配置分页基本参数
            $scope.paginationConf = {
                currentPage: 1,
                itemsPerPage: 10,
                perPageOptions: [10, 20, 30, 40, 50]
            };
            $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.ListAll);
        }]);
});
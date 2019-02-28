define(['app', 'Service'], function (app) {
    app
        .register.controller('listBusinessCtrl', ['$http', '$scope', '$location', '$stateParams', 'BEWG_URL', 'Window',
        function ($http, $scope, $location, $stateParams, BEWG_URL, Window) {
            console.log('listBusiness');

            var UUID = $stateParams.UUID;
            var BUSINESS_NAME=$stateParams.BUSINESS_NAME;
            var BUSINESS_TYPE=$stateParams.BUSINESS_TYPE;
            //赋值
            $scope.LUUID=UUID;
            $scope.BUSINESS_NAME=BUSINESS_NAME;
            $scope.BUSINESS_TYPE=BUSINESS_TYPE;

            //给后一个页面赋值
            $scope.conf={};
            $scope.conf.UUID=UUID;
            $scope.conf.BUSINESS_NAME=BUSINESS_NAME;
            $scope.conf.BUSINESS_TYPE=BUSINESS_TYPE;
            ////初始化
            //var Yuuid = $routeParams.uuid;
            //var Yitem_name = $routeParams.item_name;

            //$scope.ListAll();
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

            $scope.listBusiness=function(){
                $scope.conf={currentPage:$scope.paginationConf.currentPage,pageSize:$scope.paginationConf.itemsPerPage};
                $scope.httpData(BEWG_URL.SelectAllBusiness,$scope.conf).success(function(data){
                    $scope.item  = data.result_data.list;
                    $scope.paginationConf.totalItems = data.result_data.totalItems;
                });
            };

            $scope.selectType = function(){
                $scope.conf={currentPage:$scope.paginationConf.currentPage,pageSize:$scope.paginationConf.itemsPerPage};
                $scope.httpData(BEWG_URL.SelectAllBusinessType,$scope.conf).success(function(data){
                    $scope.businessList  = data.result_data.list;
                    $scope.paginationConf.totalItems = data.result_data.totalItems;
                });
            }
            $scope.ListAll=function(uuid){
                //var business_name=$("#BUSINESS_NAME").val();
                var attachment_name=$("#ATTACHMENT_NAME").val();
                $scope.conf={currentPage:$scope.paginationConf.currentPage,pageSize:$scope.paginationConf.itemsPerPage,queryObj:{'uuid':UUID,'business_type':BUSINESS_TYPE,'attachment_name':$scope.ATTACHMENT_NAME,'business_name':BUSINESS_NAME}};
                $scope.httpData(BEWG_URL.SelectAllBusinessAndAttachment,$scope.conf).success(function(data){
                    // 变更分页的总数
                    $scope.businessList  = data.result_data.list;
                    $scope.paginationConf.totalItems = data.result_data.totalItems;
                });
            };
            $scope.delete=function(){
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
                    Window.alert("未选择删除的数据！");
                    return false;
                }else {
                    Window.confirm('注意', "删除后不可恢复，确认删除吗？").result.then(function (btn) {
                        $scope.httpData(BEWG_URL.DeleteBusiness, uid).success(
                            function (data, status, headers, config) {
                                $scope.queryList();
                            }
                        ).error(function (data, status, headers, config) {
                            alert(status);
                        });
                    });
                }
            }

            $scope.addAttachment = function (){
                var yuuid = $("#yuuid").val();
                var yitem_name = $("#yitem_name").val();
                var chk_list=document.getElementsByName("checkbox");
                var uid = "",num=0;
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
                    statevar = {"UUID": uid, "YUUID": yuuid,"YITEM_NAME":yitem_name};
                    $scope.httpData(BEWG_URL.AddBusinessAttachment, statevar).success(
                        function (data, status, headers, config) {
                            $scope.listBusiness();
                        }
                    ).error(function (data, status, headers, config) {
                        alert(status);
                    });
                }
            };

            // 添加附件
            $scope.addAttachment = function (id, name, type) {
                $location.path("/index/ManageAttachmentList/"+id+"/"+name+"/"+type);
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
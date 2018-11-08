/**
 * Created by gl on 2016/8/4.
 */

ctmApp.register.controller('ManageAttachmentList', ['$http','$scope','$location','$routeParams',function ($http,$scope,$location,$routeParams)
{

    $scope.conf={};

    var Buuid = $routeParams.yuuid;
    var Bbusiness_name = $routeParams.ybusiness_name;
    var business_type = $routeParams.business_type;
    var bitem_type = $routeParams.bitem_type;

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
        var  url = 'rcm/ManageAttachment/SelectType';
        $scope.httpData(url,code).success(function(data){
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
        var  url = 'rcm/ManageAttachment/getAll';
        $scope.httpData(url,$scope.conf).success(function(data){
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
        	$.alert("请选择其中一条或多条数据！");
            return false;
        }else {
            var aMethed = "rcm/ManageAttachment/addAttachment";
            statevar = {"UUID": uid, BUUID: Buuid,BBUSINESS_NAME:Bbusiness_name,"BUSINESS_TYPE":item_type,"DIC_TYPE":business_type};
            $scope.httpData(aMethed, statevar).success(
                function (data) {
                    if(data.result_code=="S"){
                        $location.path("/ListBusiness/"+Buuid+"/"+Bbusiness_name+"/"+business_type);
                    }else{
                        alert(data.result_name);
                    }

                }
            );
        }
    }

    // 配置分页基本参数
    $scope.paginationConf = {
        currentPage: 1,
        itemsPerPage: 10,
        perPageOptions: [10, 20, 30, 40, 50]
    };
    $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.ListAll);


}]);
function callbackV(code,name){

}

ctmApp.register.controller('WebserviceLog', ['$http','$scope','$location','$routeParams',function ($http,$scope,$location,$routeParams) {

    $scope.listAll = function(){
        $scope.httpData('rcm/WebServiceLog/logList',$scope.paginationConf).success(function(data){
            if(data.result_code == "S"){
                $scope.logs = data.result_data.list;
                $scope.paginationConf.totalItems = data.result_data.totalItems;
            }
        });
    }
    $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage',  $scope.listAll);

    /*$scope.contractInit = function(){
        getResendCount();
        getResendResults();
    }
    $scope.contractInit();
    $scope.constractSys = {};
    $scope.constractSys.sending = false;
    $scope.constractSys.resendCount = 0;
    $scope.constractSys.resendResults = {};

    function getResendCount(){
        var url = 'ws.client.contract/SendProjectThread/getResendCount';
        $scope.httpData(url).success(function (data) {
            $scope.constractSys.resendCount = data.result_data;
        });
    }

    function getResendResults(){
        var url = 'rcm/ContractSysResendRecord/getRecords';
        $scope.httpData(url).success(function (data) {
            $scope.constractSys.resendResults = data.result_data;
        });
    }

    $scope.resend = function(){
        $scope.constractSys.sending = true;
        var newRecordURL = 'ws.client.contract/SendProjectThread/newRecord';
        var date = new Date();
        var createDate = date.format("yyyyMMdd hh:mm:ss");

        $scope.httpData(newRecordURL, createDate).success(function (record) {
            if(record.result_data == "success"){
                $scope.constractSys.resendCount = '...sending...';
                $scope.constractSys.resendResults = getResendResults();
                var resendURL = 'ws.client.contract/SendProjectThread/batchResend';
                $scope.httpData(resendURL, createDate).success(function (data) {
                    $scope.constractSys.resendCount = getResendCount();
                    $scope.constractSys.resendResults = getResendResults();
                });
            }else{
                $.alert("所有数据都已成功传输, 无需重传");
            }
        });
    }*/

    Date.prototype.format = function(format){
        var o = {
            "M+" : this.getMonth()+1, //month
            "d+" : this.getDate(), //day
            "h+" : this.getHours(), //hour
            "m+" : this.getMinutes(), //minute
            "s+" : this.getSeconds(), //second
            "q+" : Math.floor((this.getMonth()+3)/3), //quarter
            "S" : this.getMilliseconds() //millisecond
        }

        if(/(y+)/.test(format)) {
            format = format.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length));
        }

        for(var k in o) {
            if(new RegExp("("+ k +")").test(format)) {
                format = format.replace(RegExp.$1, RegExp.$1.length==1 ? o[k] : ("00"+ o[k]).substr((""+ o[k]).length));
            }
        }
        return format;
    }
}]);


ctmApp.register.controller('wsLogDetail', ['$http','$scope','$location','$routeParams',function ($http,$scope,$location,$routeParams) {
    $scope.findById = function(id){
        $scope.httpData('rcm/WebServiceLog/findLogById',{id:id}).success(function(data){
            if(data.result_code == "S"){
                $scope.l = data.result_data;
            }
        });
    }
    $scope.findById($routeParams.id);
}]);
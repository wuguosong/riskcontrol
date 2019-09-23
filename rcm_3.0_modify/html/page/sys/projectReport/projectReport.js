ctmApp.register.controller('projectReport', ['$http', '$routeParams', '$scope', '$location', '$routeParams', '$window',
    function ($http, $routeParams, $scope, $location, $routeParams, $window) {
    $scope.queryProjectReportListByPage = function () {
        console.log(JSON.stringify($scope.paginationConf))
        show_Mask();
        $http({
            method: 'post',
            url: srvUrl + "projectReport/queryProjectReportListByPage.do",
            data: $.param({"page": JSON.stringify($scope.paginationConf)})
        }).success(function (result) {
            if (result.success) {
                $scope.projectReportList = result.result_data.list;
                $scope.paginationConf.totalItems = result.result_data.totalItems;
            } else {
                $.alert(result.result_name);
            }
            hide_Mask();
        }).error(function (data, status, headers, config) {
            hide_Mask();
        });
    };
    if (null != $scope.paginationConf && null == $scope.paginationConf.queryObj) {
        $scope.paginationConf.queryObj = {}
    }
    $scope.executeQueryProjectReportListByPage = function () {
        $scope.paginationConf.currentPage = 1;
        $scope.queryProjectReportListByPage();
    };
    $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.queryProjectReportListByPage);

    $scope.downLoadFormalBiddingInfoFile = function (filePath, filename, flag) {
        console.log(filePath, filename, flag)
        var isExists = validFileExists(filePath);
        if (!isExists) {
            $.alert("要下载的文件已经不存在了！");
            return false;
        }
        /*if(filename!=null && filename.length>12){
            filename = filename.substring(0, 12)+"...";
        }else{
            filename = filename.substring(0,filename.lastIndexOf("."));
        }*/

        if (undefined != filePath && null != filePath) {
            var index = filePath.lastIndexOf(".");
            var str = filePath.substring(index + 1, filePath.length);
            if (flag == 1) {
                var url = srvUrl + "file/downloadFile.do?filepaths=" + encodeURI(filePath) + "&filenames=" + encodeURI(encodeURI(filename + ".")) + str;
            } else {
                var url = srvUrl + "file/downloadFile.do?filepaths=" + encodeURI(filePath) + "&filenames=" + encodeURI(encodeURI(filename + "-正式评审报告.")) + str;
            }

            var a = document.createElement('a');
            a.id = 'tagOpenWin';
            a.target = '_blank';
            a.href = url;
            document.body.appendChild(a);

            var e = document.createEvent('MouseEvent');
            e.initEvent('click', false, false);
            document.getElementById("tagOpenWin").dispatchEvent(e);
            $(a).remove();
        } else {
            $.alert("附件未找到！");
            return false;
        }
    }
    $scope.report = function (businessId, type) {
        if ('pfr' == type) {
            $scope.newAttachment = attach_list("FormalReportInfo", businessId, "pfrReport").result_data;
        } else {
            $scope.newAttachment = attach_list("preReportInfo", businessId, "preReport").result_data;
        }
        console.log($scope.newAttachment);
        if ($scope.newAttachment.length != 0) {

            $.ajax({
                url: srvUrl + 'cloud/getUrl.do',
                type: "POST",
                dataType: "json",
                data: {"type": 'preview', 'path': $scope.newAttachment[0].fullpath},
                async: true,
                success: function (data) {
                    if (!isEmpty(data)) {
                        $window.open(data);
                    }
                }
            });
        } else {
            $.alert("该项目不存在评审报告！");
        }
    }


}]);

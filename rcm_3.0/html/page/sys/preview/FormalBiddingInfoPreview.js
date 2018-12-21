ctmApp.register.controller('FormalBiddingInfoPreview', ['$http','$scope','$location','$routeParams','Upload','$filter',
    function ($http,$scope,$location,$routeParams,Upload,$filter) {
        // 预览传来的参数
        $scope.formalPreview = $routeParams.formalPreview;
        $scope.changeValue = 1;   // 标识切换的页面
        $scope.hideFlagR = false; // 正式评审报告隐藏标识
        $scope.hideFlagS = false; // 项目整体评分隐藏标识


        // 文件下载
        $scope.downLoadAttachment = function (projectOverview) {
            var isExists = validFileExists(projectOverview.attachmentFile);
            if (!isExists) {
                $.alert("要下载的文件已经不存在了！");
                return;
            }
            var filePath = projectOverview.attachmentFile, fileName = projectOverview.attachmentValue;
            if (fileName != null && fileName.length > 22) {
                var extSuffix = fileName.substring(fileName.lastIndexOf("."));
                fileName = fileName.substring(0, 22);
                fileName = fileName + extSuffix;
            }
            var url = srvUrl + "file/downloadFile.do?filepaths=" + encodeURI(filePath) + "&filenames=" + encodeURI(encodeURI(fileName));
            var a = document.createElement('a');
            a.id = 'tagOpenWin';
            a.target = '_blank';
            a.href = url;
            document.body.appendChild(a);

            var e = document.createEvent('MouseEvent');
            e.initEvent('click', false, false);
            document.getElementById("tagOpenWin").dispatchEvent(e);
            $(a).remove();
        }

        $scope.downLoadFormalBiddingInfoFile = function (filePath, filename) {
            var isExists = validFileExists(filePath);
            if (!isExists) {
                $.alert("要下载的文件已经不存在了！");
                return false;
            }
            if (filename != null && filename.length > 12) {
                filename = filename.substring(0, 12) + "...";
            } else {
                filename = filename.substring(0, filename.lastIndexOf("."));
            }

            if (undefined != filePath && null != filePath) {
                var index = filePath.lastIndexOf(".");
                var str = filePath.substring(index + 1, filePath.length);
                var url = srvUrl + "file/downloadFile.do?filepaths=" + encodeURI(filePath) + "&filenames=" + encodeURI(encodeURI(filename + "-正式评审报告.")) + str;

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

        $scope.downLoadBiddingFile = function (idx) {
            var isExists = validFileExists(idx.filePath);
            if (!isExists) {
                $.alert("要下载的文件已经不存在了！");
                return;
            }
            var filePath = idx.filePath, fileName = idx.fileName;
            if (fileName != null && fileName.length > 22) {
                var extSuffix = fileName.substring(fileName.lastIndexOf("."));
                fileName = fileName.substring(0, 22);
                fileName = fileName + extSuffix;
            }

            var url = srvUrl + "file/downloadFile.do?filepaths=" + encodeURI(filePath) + "&filenames=" + encodeURI(encodeURI(fileName));
            var a = document.createElement('a');
            a.id = 'tagOpenWin';
            a.target = '_blank';
            a.href = url;
            document.body.appendChild(a);

            var e = document.createEvent('MouseEvent');
            e.initEvent('click', false, false);
            document.getElementById("tagOpenWin").dispatchEvent(e);
            $(a).remove();
        }

        $scope.validateVoidFile = function () {
            for (var i in $scope.newAttachment) {
                if ($scope.newAttachment[i].fileName == null || $scope.newAttachment[i] == '') {
                    return false;
                }
            }
            return true;
        }

        // 滑动切换时，上面的过程跟着切换
        $scope.changeStyle = function (num) {
            var tabId = ['processReview', 'template', 'formalReport', 'score'];
            angular.forEach(tabId, function (data, index, array) {
                if (index != num) {
                    angular.element("#"+data).removeClass('chose');
                } else {
                    angular.element("#"+data).addClass('chose');
                }
            });
        }

        // 展开展示信息
        $scope.expandMore = function (parentId, val) {
            angular.element("#"+parentId).addClass('hideOpen');
            $scope[val] = true;
        }

    }]
);
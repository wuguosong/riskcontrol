ctmApp.register.controller('FormalBiddingInfoPreview', ['$http','$scope','$location','$routeParams','Upload','$filter',
    function ($http,$scope,$location,$routeParams,Upload,$filter) {
        // 预览传来的参数
        $scope.formalPreview = $routeParams.formalPreview;
        $scope.changeValue = 1;   // 标识切换的页面
        $scope.templateChangeValue = 1; // 标识切换页面
        $scope.templateChangeValue1 = 0; // 滑动页面标识
        $scope.templateChangeValue2 = 1; // 滑动页面标识
        $scope.templateChangeValue3 = 2; // 滑动页面标识
        $scope.tempFlag1 = true; // 没有前一个页面，隐藏左阴影区
        $scope.tempFlag3 = false; // 没有后一个页面，隐藏右阴影区
        $scope.hideFlagR = false; // 正式评审报告隐藏标识
        $scope.hideFlagS = false; // 项目整体评分隐藏标识

        $scope.getDate = function(){
            var myDate = new Date();
            //获取当前年
            var year = myDate.getFullYear();
            //获取当前月
            var month = myDate.getMonth() + 1;
            //获取当前日
            var date = myDate.getDate();
            var h = myDate.getHours(); //获取当前小时数(0-23)
            var m = myDate.getMinutes(); //获取当前分钟数(0-59)
            var s = myDate.getSeconds();
            var now = year + '-' + month + "-" + date + " " + h + ':' + m + ":" + s;
            return now;
        }

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



        // 点击最上边标题切换内容 以及使用样式
        $scope.changeTitle = function (num, id) {
            var tabId = ['processReview', 'template', 'formalReport', 'score'];
            angular.forEach(tabId, function (data, index, array) {
                if (data != id) {
                    angular.element("#"+data).removeClass('chose');
                }
            });
            angular.element("#"+id).addClass('chose');
            $scope.changeValue = num;
        }

        // 展开展示信息
        $scope.expandMore = function (parentId, val) {
            debugger
            angular.element("#"+parentId).addClass('hideOpen');
            $scope[val] = true;
        }

        /*// 点击左右按钮切换
        $scope.changeTab = function (flag) {
            debugger
            var $scope.templateChangeValue2 = $scope.changeValue;
            if(flag == 0){
                if ($scope.templateChangeValue2 > 1){
                    $scope.changeValue = $scope.templateChangeValue2 - 1;
                }
            } else {
                $scope.changeValue = $scope.templateChangeValue2 + 1;
            }
        }*/

        // 点击左右按钮切换 风险评控意见汇总 里面的内容
        $scope.changeTemplateTab = function (flag) {
            console.log($scope.formalPreview.summaryType)
            debugger
            // 模板包含列表数
            var number = 0;
            if($scope.formalPreview.summaryType == '1000' || $scope.formalPreview.summaryType == '4000' ||
                $scope.formalPreview.summaryType == '5000'){
                number = 5;
            } else {
                number = 4;
            }
            if(flag == 0){
                if ($scope.templateChangeValue2 > 1){
                    if($scope.templateChangeValue2 == number){
                        $scope.templateChangeValue3 = number;
                    } else {
                        $scope.templateChangeValue3 = $scope.templateChangeValue3 - 1;
                    }
                    $scope.templateChangeValue1 = $scope.templateChangeValue1 - 1;
                    $scope.templateChangeValue2 = $scope.templateChangeValue2 - 1;
                } else if ($scope.templateChangeValue2 == 1) {
                    /*$scope.changeValue = 1;*/
                }
            } else {
                if ($scope.templateChangeValue2 != number) {
                    $scope.templateChangeValue1 = $scope.templateChangeValue1 + 1;
                    $scope.templateChangeValue2 = $scope.templateChangeValue2 + 1;
                    if($scope.templateChangeValue3 == number){
                        $scope.templateChangeValue3 = 10;
                    } else {
                        $scope.templateChangeValue3 = $scope.templateChangeValue3 + 1;
                    }
                } else {
                    /*$scope.changeValue = 3;*/
                }
            }
            // 没有呈现内容，阴影区域不显示
            if ($scope.templateChangeValue1 < 1) {
                $scope.tempFlag1 = true;
            } else {
                $scope.tempFlag1 = false;
            }
            if ($scope.templateChangeValue3 > 5) {
                $scope.tempFlag3 = true;
            } else {
                $scope.tempFlag3 = false;
            }
        }

    }]
);
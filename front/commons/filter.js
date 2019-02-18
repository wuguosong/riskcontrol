/**
 * Created by zhangsl on 2016/06/06.
 */
define(['app'], function (app) {
    app.register
        .filter('jsonStrToDate', function () {
            return function (date, arr) {
                angular.forEach(arr, function (item) {
                    /*    console.log("================================");
                        console.log(date); console.log(item);
                        console.log("+++++++++++++++++++++++++++++++");*/
                    if (!moment.isMoment(item.start)) {
                        item.start = moment(date + ' ' + item.start);
                    }
                    if (!moment.isMoment(item.end)) {
                        item.end = moment(date + ' ' + item.end);
                    }
                    /*  console.log(item);
                      console.log("================================");*/
                });
            }
        })
        .filter('jsonStrToNum', function () {
            return function (arr) {
                angular.forEach(arr, function (item) {
                    if (item.count) {
                        item.count = parseInt(item.count);
                    }
                    item.applyCount = parseInt(item.applyCount);
                });
            }
        })
});


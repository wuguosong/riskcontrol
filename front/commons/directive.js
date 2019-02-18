/**
 * Created by gaohe on 2016/06/06.
 */
define(['app'], function (app) {
    app.register
    /*使输入的数字自动加上千位符*/
        .directive('toChange', ['$parse',
            function ($parse) {
                return {
                    link: function (scope, element, attrs, ctrl) {
                        //控制输入框只能输入数字和小数点
                        function limit() {
                            var limitV = element[0].value;
                            limitV = limitV.replace(/[^0-9.]/g, "");
                            element[0].value = limitV;
                            $parse(attrs['ngModel']).assign(scope, limitV);
                            format();
                        }

                        //对输入数字的整数部分插入千位分隔符
                        function format() {
                            var formatV = element[0].value;
                            var array = new Array();
                            array = formatV.split(".");
                            var re = /(-?\d+)(\d{3})/;
                            while (re.test(array[0])) {
                                array[0] = array[0].replace(re, "$1,$2")
                            }
                            var returnV = array[0];
                            for (var i = 1; i < array.length; i++) {
                                returnV += "." + array[i];
                            }
                            element[0].value = returnV;
                            $parse(attrs['ngModel']).assign(scope, formatV);
                        }

                        scope.$watch(attrs.ngModel, function () {
                            limit();
                        })
                    }
                };
            }])
});

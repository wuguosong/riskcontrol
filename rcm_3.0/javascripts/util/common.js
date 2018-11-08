var ctmUtil = {
    //获取url中的参数
    getUrlParam: function (name) {
        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); //构造一个含有目标参数的正则表达式对象
        var r = window.location.search.substr(1).match(reg);  //匹配目标参数
        if (r != null) return unescape(r[2]);
        return null; //返回参数值
    },

    //传递给后台的json
    getConditionObj: function(tb, paramObj) {
        var obj = {};
        if (!obj[tb]) obj[tb] = {};
        if (paramObj) {
            obj[tb] = paramObj;
        }
        return JSON.stringify(obj);
    },

    //构造url集合
    url: function(urlObj) {
            var baseUrl = "http://127.0.0.1:8080/ctm-rest/";
            for(key in urlObj){
                urlObj[key] = baseUrl + urlObj[key];
            }
            return urlObj;
    }
};

//将对象数组中的某个字段值封装成字符串数组
function getKeyArray(array, key){
    var ar = [];
    if(array && array.length>0){
        for(var i=0;i<array.length;i++){
            var kv = array[i][key];
            ar.push(kv);
        }
    }
    return ar;
}
//更新新数组中对象，根据key判断对象是否相同，如果相同把老数组中的元素放到新数组中
function mergeArray(oldArray, newArray, key){
    if(oldArray == null || oldArray.length==0){
        oldArray = newArray;
        return oldArray;
    }
    if(newArray == null || newArray.length==0) return [];
    var newIds = getKeyArray(newArray, key), oldIds = getKeyArray(oldArray, key);
    //如果老数组中的元素在新数组中也有， 那么删除新数组中的元素并把老数组中的元素添加到这个位置
    for(var i=0;i<oldIds.length;i++){
        var index = newIds.indexOf(oldIds[i]);
        if(index!=-1){
            newArray.splice(index,1,oldArray[i]);
        }
    }
    return newArray;
}
function del(p){
    $(p).parent().remove();
}

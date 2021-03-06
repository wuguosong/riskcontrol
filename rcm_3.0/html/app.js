'use strict';
var ctmApp = angular.module('ctmApp', ['ngRoute','ngCookies','treeGrid','pagination','paginationes','paginationhome','ngFileUpload']);
ctmApp.config(function($httpProvider) {
	var user = $.parseJSON($.cookie('credentials'));
	if(user!=null && user.UUID != null){
		$httpProvider.defaults.headers.common = {'authorization' : user.UUID};
		$httpProvider.defaults.headers.post = {"Content-Type":"application/x-www-form-urlencoded"};
	}
});
var srvUrl = "/rcm-rest";
//var srvUrl = "http://10.10.20.38/rcm-rest/";
//var srvUrl = "http://riskcontrol.bewg.net.cn/rcm-rest/";

//决策定时器刷新(毫秒)
var meetDeciInteTime = 2500;
//决策定时器 变量
var meetDeciInte = null;
ctmApp.controller('SysControl',['$scope','$cookies','$http','$location','$interval','$rootScope','$filter', function ($scope,$cookies,$http,$location,$interval,$rootScope,$filter) {
	////到cookie中读取用户名及密码
    //服务端地址信息
    $scope.srvInfo = {
        srvUrl: srvUrl
    };
    //登录信息
    $scope.credentials=$.parseJSON($cookies.get('credentials'));
    $rootScope.credentials = $.parseJSON($cookies.get('credentials'));
    //-------------------------------------------------//
    //页面及参数相关
    //-------------------------------------------------//
    //工作区页面
    $scope.content="page/ctm/contract/contractEdit.html";
    //当前工作区页面及参数
    //$scope.currentPage={url:"",param:{}};
    //工作区页面跳转
    $scope.setContent= function (aUrl) {
//        alert(aUrl);
        //设置参全局变量
        //$scope.currentPage=pCurrentPage
        //页面跳转
        //页面加载完成后加载JS文件
        //$.getScript(currentPage.url+".js");
        //设置当前页面
        $scope.content=aUrl;
        //load(pCurrentPage)
    };
    //获得当前页面的参数
    $scope.getParam= function (Key) {
        return $scope.currentPage.param.key;
    };
    //-------------------------------------------------//
    //服务端通讯相关
    //-------------------------------------------------//
    //获取服务端数据
    $scope.HttpBpm = function (pLoadInfo) {
        var aLoadInfo = pLoadInfo;
        //增加头部授权调用
        //var headers = {authorization: "Basic " + btoa($scope.credentials.userID + ":" + $scope.credentials.password)};
        //aLoadInfo.headers = headers;
        //增加服端的url信息
        aLoadInfo.url = srvInfo.bpmUrl + pLoadInfo.url;
        //调用angularjs服务端方法
        return $http(aLoadInfo);
    };
    //获取服务端数据
    $scope.GetData = function (pLoadInfo) {
        var aLoadInfo = pLoadInfo;
        //增加头部授权调用
        //var headers = {authorization: "Basic " + btoa($scope.credentials.userID + ":" + $scope.credentials.password)};
        //aLoadInfo.headers = headers;
        //增加服端的url信息
        aLoadInfo.url = srvInfo.srvUrl + pLoadInfo.url;
        //增加调用方法
        aLoadInfo.method = 'get';
        //调用angularjs服务端方法
        return $http(aLoadInfo);
    };
    //获取服务端的数据
    $scope.httpData=function(pMethod,pData){
    	if(pData == null){
    		pData = "";
    	}
        var aUrl=$scope.srvInfo.srvUrl+pMethod;
        //加入授权信息
       // var aheaders = {authorization: "Basic " + btoa($scope.credentials.userID + ":" + $scope.credentials.password)};
        //请求参数
        var req = {
            method: "post",
            url: aUrl,
            data:pData,
            withCredentials:true
        };
        return $http(req);
    }
    $scope.downLoadFile = function(idx){
    	var isExists = validFileExists(idx.filePath);
    	if(!isExists){
    		$.alert("要下载的文件已经不存在了！");
    		return;
    	}
		var filePath = idx.filePath, fileName = idx.fileName;
		if(fileName!=null && fileName.length>22){
			var extSuffix = fileName.substring(fileName.lastIndexOf("."));
			fileName = fileName.substring(0, 22);
			fileName = fileName + extSuffix;
    	}
		var url = srvUrl+"file/downloadFile.do?filepaths="+encodeURI(encodeURI(filePath))+"&filenames="+encodeURI(encodeURI(fileName));
		var a = document.createElement('a');
	    a.id = 'tagOpenWin';
	    a.target = '_blank';
	    a.href = url;
	    document.body.appendChild(a);

	    var e = document.createEvent('MouseEvent');     
	    e.initEvent('click', false, false);     
	    document.getElementById("tagOpenWin").dispatchEvent(e);
		$(a).remove();
		
		//window.location.href = srvUrl+"common/RcmFile/downLoad?filePath="+filePath+"&fileName="+encodeURI(fileName);
	}
    $scope.downLoadFileReport = function(filePath,filename){
    	var isExists = validFileExists(filePath);
    	if(!isExists){
    		$.alert("要下载的文件已经不存在了！");
    		return;
    	}
    	if(filename!=null && filename.length>12){
    		filename = filename.substring(0, 12)+"...";
    	}
        if(undefined!=filePath && null!=filePath){
            var index = filePath.lastIndexOf(".");
            var str = filePath.substring(index + 1, filePath.length);
            var url = srvUrl+"file/downloadFile.do?filepaths="+encodeURI(encodeURI(filePath))+"&filenames="+encodeURI(encodeURI(filename+"-正式评审报告."+str));
            
            var a = document.createElement('a');
    	    a.id = 'tagOpenWin';
    	    a.target = '_blank';
    	    a.href = url;
    	    document.body.appendChild(a);

    	    var e = document.createEvent('MouseEvent');     
    	    e.initEvent('click', false, false);     
    	    document.getElementById("tagOpenWin").dispatchEvent(e);
    		$(a).remove();
//            window.location.href = 
        }else{
            $.alert("附件未找到！");
            return false;
        }
    }
    
    //上传服务端数据
    $scope.PostData = function (LoadInfo) {
        var aLoadInfo = pLoadInfo;
        //增加头部授权调用
        //var headers = {authorization: "Basic " + btoa($scope.credentials.userID + ":" + $scope.credentials.password)};
        //aLoadInfo.headers = headers;
        //增加服端的url信息
        aLoadInfo.url = srvInfo.srvUrl + pLoadInfo.url;
        //增加调用方法
        aLoadInfo.method = 'post';
        //调用angularjs服务端方法
        return $http(aLoadInfo);
    };

    //显示信息,错误\提示\警告
    $scope.PopInfo=function(){
    };

    //初始化用户名及密码,来自url或cookies
    //$scope.srvInfo.userCode="kermit";
    //$scope.srvInfo.pwd="111111";
    //登录信息失效,则跳到登录页面或提醒
    //if(login){
    //}
    //此处为rest微服务的地址,写死或本机,端口默认8080
    var host=$location.host();
    var port=$location.port();
    //$scope.srvInfo.srvUrl='http://127.0.0.1:8080/ctm-rest/';
    // 配置分页基本参数
    $scope.paginationConf = {
        lastCurrentTimeStamp:'',
        currentPage: 1,
        totalItems: 0,
        itemsPerPage: 10,
        pagesLength: 10,
        perPageOptions: [10, 20, 30, 40, 50],
        queryObj:{},
        onChange: function(){
        }
    };
    $scope.paginationConfhome = {
        currentPage: 1,
        totalItems: 0,
        itemsPerPage: 5,
        pagesLength: 5,
        perPageOptions: [5, 10],
        queryObj:{},
        onChange: function(){
        }
    };
    $scope.paginationConfes = {
        currentPage: 1,
        totalItems: 0,
        itemsPerPage: 10,
        pagesLength: 10,
        perPageOptions: [10, 20, 30, 40, 50],
        queryObj:{},
        onChange: function(){
        }
    };
    //判断当前用户是否为指定角色
    $rootScope.hasRole = function(roleCodes) {
    	if(null == roleCodes || roleCodes.length == 0) 
    		return false;
    	if(!(roleCodes instanceof Array)){
    		if(roleCodes.indexOf(",") != -1){
        		roleCodes = roleCodes.split(',');
        	}else{
	    		var array = new Array();
	    		array.push(roleCodes);
	    		roleCodes = array;
        	}
    	}
		var roles = $scope.credentials.roles;
    	for(var x = 0; x < roles.length; x++)
		{
    		for(var y = 0; y < roleCodes.length; y++)
    		{
				if(roles[x].CODE == roleCodes[y]){
					return true;
				}
    		}
		}
    	return false;
    }
    $scope.isFuncTF=false;
    $scope.sysFuncListing=function(){
        var  url = 'common/commonMethod/getSysFuncList';
        $scope.httpData(url,$scope.credentials.UUID).success(function(data){
            if(data.result_code == "S") {
                $scope.func = data.result_data;
                var funclist = $scope.func;
                for (var i = 0; i < funclist.length; i++) {
                    var subFuncList = funclist[i].subFunc;
                    for (var k = 0; k < subFuncList.length; k++) {
                        if ($scope.globalURLID == subFuncList[k].URL) {
                            subFuncList[k].isFuncTF = true;
                        } else {
                            subFuncList[k].isFuncTF = false;
                        }
                        
                        //三层菜单
                        if(subFuncList[k].subFunc != null){
                        	var subsubFuncList = subFuncList[k].subFunc;
                        	for (var j = 0; j < subsubFuncList.length; j++) {
                                if ($scope.globalURLID == subsubFuncList[j].URL) {
                                	subsubFuncList[j].isFuncTF = true;
                                } else {
                                	subsubFuncList[j].isFuncTF = false;
                                }
                            }
                        }
                        
                       
                    }
                }
            }
        }
        ).error(function (data, status, headers, config) {
            $.alert("登录已失效，请重新登录");
            if(srvUrl == 'http://riskcontrol.bewg.net.cn/rcm-rest/'){
            	window.location.href="http://sso.bewg.net.cn";
            }else{
            	window.location.href="signin.html";
            }
            
        });
    }
    $scope.sysFuncListing();

    $scope.isDFuncTF=false;
    $scope.sysDFuncListing=function(){
        var  url = 'common/commonMethod/getSysFuncList';
        $scope.httpData(url,$scope.credentials.UUID).success(function(data){
                if(data.result_code == "S") {
                    $scope.func = data.result_data;
                    var funclist = $scope.func;
                    for (var i = 0; i < funclist.length; i++) {
                        if ($scope.globalURLID == funclist[i].URL) {
                            funclist[i].isDFuncTF = true;
                        } else {
                            funclist[i].isDFuncTF = false;
                        }
                    }
                }
            }
        ).error(function (data, status, headers, config) {
            $.alert("登录已失效，请重新登录");
            if(srvUrl == 'http://riskcontrol.bewg.net.cn/rcm-rest/'){
            	window.location.href="http://sso.bewg.net.cn";
            }else{
            	window.location.href="signin.html";
            }
        });
    }
    $scope.sysDFuncListing();
    
    $scope.mapEntry = function(mapArr){
    	var names = [], values = [];
    	if(null!=mapArr && mapArr.length>0){
            for(var k=0;k<mapArr.length;k++){
            	if(mapArr[k] != null){
            		names.push(mapArr[k].NAME==null?mapArr[k].name:mapArr[k].NAME);
            		values.push(mapArr[k].VALUE==null?mapArr[k].value:mapArr[k].VALUE);
            	}
            }
        }
    	var obj = {names: names, values: values};
    	return obj;
    }
    
    //删除数组中的特定元素
    $scope.removeObjByValue = function(array, value){
        var retArray = [];
        for(var i=0;i<array.length;i++){
            if(value !== array[i].VALUE){
                retArray.push(array[i]);
            }
        }
        return retArray;
    }
    //获取申请单所有的相关人
     $scope.findRelationUser = function(businessId, relationType){
         var url = 'rcm/ProjectRelation/findRelationUserByBusinessId';
         var queryObj = {businessId:businessId, exclude:[$scope.credentials.UUID]};
         if(typeof relationType !='undefined'){
             queryObj.relationType = relationType;
         }
         $scope.httpData(url,queryObj).success(function(data){
             if(data.result_code == 'S'){
                 $scope.relationUsers = data.result_data;
             }
         });
     }
    $scope.DirectMenuCur=function(event){
        $(event.currentTarget).addClass("cur");
        $(event.currentTarget).find(".fa-caret-left").css({display:"block"});
        $(event.currentTarget).siblings(".mm-direct").removeClass("cur");
        $(event.currentTarget).siblings(".mm-direct").find(".fa-caret-left").css({display:"none"});
        $(event.currentTarget).siblings(".mm-dropdown").find("li").removeClass("cur");
        $(event.currentTarget).siblings(".mm-dropdown").find("li .fa-caret-left").css({display:"none"});
        $(event.currentTarget).siblings(".mm-dropdown").removeClass("open");
        $(event.currentTarget).parents(".slimScrollDiv").siblings(".mmc-dropdown-open-ul").find("li .fa-caret-left").css({display:"none"});
        $(event.currentTarget).parents(".slimScrollDiv").siblings(".mmc-dropdown-open-ul").find("li").removeClass("cur");
        $(event.currentTarget).parents(".slimScrollDiv").siblings(".mmc-dropdown-open-ul").css({display:"none"});
    }
    $scope.jsonStringify = function(jsonObj){
    	if(jsonObj == null){
    		return "";
    	}
    	return JSON.stringify(jsonObj);
    };
    $scope.evalJsonStr = function(jsonStr){
    	return eval("("+jsonStr+")");
    };
    $scope.indexOf = function(originalStr, subStr){
    	if(originalStr == null || subStr == null){
    		return -1;
    	}
    	return originalStr.indexOf(subStr);
    };
    $scope.deleteJsonAttr = function(jsonObj, attr){
    	var newObj = jsonObj;
    	delete newObj[attr];
    	return newObj;
    }
    $scope.$alert = function(msg){
    	alert(msg);
    }
    $scope.GoPage = function (event) {
        $(event.currentTarget).addClass("cur");
        $(event.currentTarget).find(".fa-caret-left").css({display:"block"});
        $(event.currentTarget).siblings().removeClass("cur");
        $(event.currentTarget).siblings().find(".fa-caret-left").css({display:"none"});
        $(event.currentTarget).parents(".mm-dropdown").siblings(".mm-dropdown").find("li").removeClass("cur");
        $(event.currentTarget).parents(".mm-dropdown").siblings(".mm-dropdown").find("li .fa-caret-left").css({display:"none"});
        $(event.currentTarget).parents(".mm-dropdown").siblings(".mm-direct").removeClass("cur");
        $(event.currentTarget).parents(".mm-dropdown").siblings(".mm-direct").find(".fa-caret-left").css({display:"none"});
        $(event.currentTarget).parents(".mmc-dropdown-open-ul").siblings(".slimScrollDiv").find(".mm-dropdown li").removeClass("cur");
        $(event.currentTarget).parents(".mmc-dropdown-open-ul").siblings(".slimScrollDiv").find(".mm-dropdown li .fa-caret-left").css({display:"none"});
        $(event.currentTarget).parents(".mmc-dropdown-open-ul").siblings(".slimScrollDiv").find(".mm-direct").removeClass("cur");
        $(event.currentTarget).parents(".mmc-dropdown-open-ul").siblings(".slimScrollDiv").find(".mm-direct .fa-caret-left").css({display:"none"});
        //$(event.currentTarget).parents(".mmc-dropdown-open-ul").siblings(".slimScrollDiv").find(".mmc-dropdown-open").addClass("open");
        //$(event.currentTarget).parents(".mmc-dropdown-open-ul").siblings(".slimScrollDiv").find(".mm-dropdown").removeClass("open");
    }
    //正式评审报告中判断上会及跳转的公共方法
    $scope.needMeetingRouter = function(reportId,pageFlag){
        var aMethod = 'rcm/ProjectInfo/selectPrjReviewView';
        $scope.httpData(aMethod, {reportId:reportId}).success(function (data) {
            var result = data.result_data;
            //判断是否上会
            if(typeof result.NEED_MEETING=='undefined'){
                //选择是否需要上会
                $scope.formalReport={_id:result.REPORT_ID,projectFormalId:result.BUSINESS_ID};
                $scope.pageFlag=pageFlag;
                $("#passModal").modal('show');
            }else{
                if(result.NEED_MEETING=='1'){//不需要上会
                    $location.path("/FormalBiddingInfo/"+result.REPORT_ID+"@1");
                }else{//需要上会且已经有上会信息
                    $location.path("/MeetingInfoDetail/Create/"+result.REPORT_ID+"@1");
                }
            }
        })
    }
    //批量下载方法
    $scope.batchDownload = function(){
		var filenames = "";
		var filepaths = "";
		$("input[type=checkbox][name=choose]:checked").each(function(){
			if($(this).attr("filename")==null || $(this).attr("filename")==""){
				return true;
			}
			filenames+=$(this).attr("filename")+",";
			filepaths+=$(this).attr("filepath")+",";
		});
		if(filenames.length == 0 || filepaths.length == 0){
			$.alert("请选择要打包下载的文件！");
			return false;
		}
		filenames = filenames.substring(0, filenames.length - 1);
		filepaths = filepaths.substring(0, filepaths.length - 1);
		downloadBatch(filenames, filepaths);
	}
    /**
     * 判断list集合中的元素中是否有某列（field）的值跟myvalue一样，
     */
    $scope.isValueExist = function(list, field, myvalue){
    	if(list == null || !$.isArray(list) || field == null){
    		return false;
    	}
    	var fs = field.split(".");
    	for(var i = 0; i < list.length; i++){
    		var tmp = list[i];
    		for(var j = 0; j < fs.length; j++){
    			tmp = tmp[fs[j]];
    		}
    		if(tmp == myvalue){
    			return true;
    		}
    	}
    	return false;
    }
    
	//---------------------------------------------------------------------------
	//	是否跳转到表决页面,条件如下:
    //	1：如果有开会,则启动定时器
    //	2：如果已在表决页面,则关闭定时器
    //	3：如果时间(代码判断)不在上会时间,则关闭定时器
    //	4：当前用户未表决,定时器处于开启状态
	//	2017-06-12,2018-02-27
	//---------------------------------------------------------------------------
    $rootScope.meetingMonitor = function() {
		//如果已在表决页面,则取消全局定时器
    	var path = $location.path();
		if(path == "/MeetingVote" || 0 == path.indexOf("/MeetingVote/") || path == "/MeetingVoteWait" || 0 == path.indexOf("/MeetingVoteWait/")){
			return;
		}
    	$http({
			method:'post',  
		    url:srvUrl+"decision/isUserDecision.do"
		}).success(function(data){
			if(data.result_data.isTodayDecision){
				if(data.result_data.isUserDecision){
					path = $filter('encodeURI')("#/FormalReviewListToday/JTI1MjMlMkZEZWNpc2lvbk92ZXJ2aWV3","VALUE");
					$location.path("/MeetingVote/"+path);
				}
			}else{
				try{
					$interval.cancel(meetDeciInte);
				}catch(e){
				}finally{
					//定时器取消后就把标识改为 可 以创建定时器
					meetDeciInte = null;
				}
			}
		});
    };
    $http({
		method:'post',  
	    url:srvUrl+"meetingIssue/isShowPublicSearch.do"
	}).success(function(data){
		if(data.success){
			$("#publicProjectName,#publicSearchButton").show();
		} 
	});
    $scope.publicKeyupSearchProject = function ($event) {
    	 if($event.keyCode==13){
    		 $scope.publicSearchProject();
    	 }
    }
    $scope.publicSearchProject = function () {
    	var returnUrl = $filter("encodeURI")("#/");
      	var publicProjectName = $scope.publicProjectName;
      	if(null == publicProjectName || publicProjectName == ""){
      		publicProjectName = "undefined";
      	}
      	publicProjectName = $filter("encodeURI")(publicProjectName);
	    $location.path("/projectReviewList/" + publicProjectName+"/"+returnUrl);
    }
}]);
ctmApp.filter('dictItemFilter', function(){
	return function(item){
		if(item == null){
			return;
		}
		var newItem = {};
		newItem.NAME = item.ITEM_NAME;
		newItem.VALUE = item.UUID;
		return newItem;
	};
});
ctmApp.filter('jsonStrFilter', function(){
	return function(jsonObj){
		return JSON.stringify(jsonObj);
	};
});
ctmApp.filter('keyValueNames', function(){
	return function(array, name){
		var str = "";
		for(var i = 0; array != null && Array.isArray(array) && i < array.length; i++){
			str = str + array[i][name] + ",";
		}
		if(str.length > 0){
			str = str.substring(0, str.length-1);
		}
		return str;
	};
});
ctmApp.filter('encodeURI', function() {
    return function (str) {
        return window.btoa(encodeURIComponent(escape(str)));
    }
});
ctmApp.filter('decodeURI', function() {
    return function(str) {
        return unescape(decodeURIComponent(window.atob(str)));
    }
});
ctmApp.filter('textToHtml', function($sce) {
    return function(text) {
        return $sce.trustAsHtml(text);
    }
});
//调用该方法可访问scope对象
function accessScope(node, func) {
    var scope = angular.element(document.querySelector(node)).scope();
    scope.$apply(func);
}
//创建一个js命名空间
function namespace(objPath, scope){
    var object = scope || window, tokens = objPath.split("."), token;
    while(tokens.length>0){
        token = tokens.shift();
        object = object[token] = object[token] || {};
    }
    return object;
}

function getStringInArray(array, attName){
    var str = "";
    for(var i=0;i<array.length;i++){
        str+=array[i][attName]+',';
    }
    if(str!=''){
        str = str.substring(0,str.length-1);
    }
    return str;
}
//根据value判断数组中的两个对象是否相同，然后去重
function removeDuplicate(array)
{
    if(!array || array.length<2) return array;
    array.sort(function compare(a, b){
        return (a.VALUE === b.VALUE) ? 0: 1;
    });
    var re=[array[0]];
    for(var i = 1; i < array.length; i++)
    {
        if( array[i].VALUE !== re[re.length-1].VALUE)
        {
            re.push(array[i]);
        }
    }
    return re;
}

function startLoading(){
    var _PageHeight = document.documentElement.clientHeight,
        _PageWidth = document.documentElement.clientWidth;
    var _LoadingHeight = 40, _LoadingWidth = 40;
    var _LoadingTop = _PageHeight > _LoadingHeight ? (_PageHeight-_LoadingHeight)/2 : 0,
        _LoadingLeft = _PageWidth > _LoadingWidth ? (_PageWidth-_LoadingWidth)/2 : 0;

    var _LoadingDiv = document.createElement("div");
    _LoadingDiv.setAttribute("id","loadingDiv");
    _LoadingDiv.style.top = _LoadingTop+'px';
    _LoadingDiv.style.left = _LoadingLeft+'px';
    _LoadingDiv.className = "spinner";
    _LoadingDiv.innerHTML = "<div class=\"spinner-container container1\"> <div class=\"circle1\"></div> <div class=\"circle2\"></div> <div class=\"circle3\"></div> <div class=\"circle4\"></div> </div> <div class=\"spinner-container container2\"> <div class=\"circle1\"></div> <div class=\"circle2\"></div> <div class=\"circle3\"></div> <div class=\"circle4\"></div> </div> <div class=\"spinner-container container3\"> <div class=\"circle1\"></div> <div class=\"circle2\"></div> <div class=\"circle3\"></div> <div class=\"circle4\"></div> </div>"
    document.body.appendChild(_LoadingDiv);
}

function endLoading(){
    var loadingMask = document.getElementById('loadingDiv');
    loadingMask.parentNode.removeChild(loadingMask);
}

function  DateDiff(values,  nowDate){    //sDate1和sDate2是2006-12-18格式
  /*  var  aDate,  oDate1,  oDate2,  iDays
    aDate  =  sDate1.split("-")
    oDate1  =  new  Date(aDate[1]  +  '-'  +  aDate[2]  +  '-'  +  aDate[0]) //转换为12-18-2006格式
    aDate  =  sDate2.split("-")
    oDate2  =  new  Date(aDate[1]  +  '-'  +  aDate[2]  +  '-'  +  aDate[0])
    iDays  =  parseInt(Math.abs(oDate1  -  oDate2)  /  1000  /  60  /  60  /24) //把相差的毫秒数转换为天数*/
    values = new Date(values.replace(/-/g, "/"));
    nowDate = new Date(nowDate.replace(/-/g, "/"));
    var days = values.getTime() - nowDate.getTime();
    var iDays = parseInt(days / (1000 * 60 * 60 * 24));
    return  iDays
}
/**
 * 批量下载
 * filenames:逗号隔开的文件名
 * filepaths:逗号隔开的文件路径
 */
function downloadBatch(filenames, filepaths){
	var isExists = validFileExists(filepaths);
	if(!isExists){
		$.alert("要下载的文件已经不存在了！");
		return;
	}
	var url=srvUrl+"file/downloadBatch.do";
	var form=$("<form>");//定义一个form表单
	form.attr("style","display:none");
	form.attr("target","");
	form.attr("method","post");
	form.attr("action", url);
	var input1=$("<input>");
	input1.attr("type","hidden");
	input1.attr("name","filenames");
	input1.attr("value", filenames);
	var input2=$("<input>");
	input2.attr("type","hidden");
	input2.attr("name","filepaths");
	input2.attr("value", filepaths);
	$("body").append(form);//将表单放置在web中
	form.append(input1);
	form.append(input2);
	form.submit();//表单提交
}
function validFileExists(filepaths){
	var result = true;
	var url=srvUrl+"file/validFileExists.do";
	$.ajax({
		url: url,
		type: "POST",
		dataType: "json",
		data: {filepaths: filepaths},
		async: false,
		success: function(data){
			result = data.success;
		}
	});
	return result;
}
//显示遮罩层    
function show_Mask(){    
    $("#mask_").css("height",$(document).height());  
    $("#mask_").css("line-height",$(document).height()+"px");  
    $("#mask_").css("width",$(document).width());     
    $("#mask_").show();
}  
//隐藏遮罩层  
function hide_Mask(){     
    $("#mask_").hide();     
}  
function arrayContains(array, obj, equalField) {
    var i = array.length;
    while (i--) {
    	if(equalField == null){
    		if (array[i] === obj) {
                return true;
            }
    	}else{
    		if (array[i][equalField] === obj[equalField]) {
                return true;
            }
    	}
        
    }
    return false;
}
function guid() {
    return 'xxxxxxxxxxxx4xxxyxxxxxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
        var r = Math.random()*16|0, v = c == 'x' ? r : (r&0x3|0x8);
        return v.toString(16);
    });
}
//处理附件的$$hashkey
function reduceAttachment(oldAttachment){
	var newAttachment=[];
	for(var i in oldAttachment){
		var attachmentObj = {};
		attachmentObj.ITEM_NAME=oldAttachment[i].ITEM_NAME;
		attachmentObj.UUID=oldAttachment[i].UUID;
		if(undefined!=oldAttachment[i].files){
			var newFiles=[];
			for(var j in oldAttachment[i].files){
				var fileObj={};
				fileObj.approved = oldAttachment[i].files[j].approved;
				fileObj.fileName = oldAttachment[i].files[j].fileName;
				fileObj.filePath = oldAttachment[i].files[j].filePath;
				fileObj.programmed = oldAttachment[i].files[j].programmed;
				fileObj.upload_date = oldAttachment[i].files[j].upload_date;
				fileObj.version = oldAttachment[i].files[j].version;
				newFiles.push(fileObj);
			}
			attachmentObj.files = newFiles;
		}
		newAttachment.push(attachmentObj);
	}
	return newAttachment;
	
}

function deleteJsonAttr(jsonObj, attr){
	var newObj = jsonObj;
	delete newObj[attr];
	return newObj;
}

function fileErrorMsg(errorFile){
	var key = errorFile[0].$error;
	var param = errorFile[0].$errorParam;
	var errorMap = {"maxSize":"附件超过"+param+"限制！"};
	
	return errorMap[key];
}

/**流程相关的一些公共方法开始**/
/**
 * 根据流程Key和业务Id获取任务日志列表
 * @param business_module 业务单元,这里以流程Key进行区分
 * @param business_id 业务Id
 * @returns {*}
 * @public
 */
function wf_listTaskLog(business_module, business_id){
    var url = srvUrl + "sign/listLogs.do";
    var logs = null;
    $.ajax({
        url: url,
        type: "POST",
        dataType: "json",
        data: {"business_module": business_module,'business_id':business_id},
        async: false,
        success: function (data) {
            logs = data;
        }
    });
    return logs;
}

/**
 * 获取当前任务日志
 * @param business_module 业务单元,这里以流程Key进行区分
 * @param business_id 业务Id
 * @param uuid 当前登录用户ID
 * @returns {*}
 * @public
 */
function wf_getTaskLog(business_module, business_id, uuid){
    var logs = wf_listTaskLog(business_module, business_id);
    var log = null;
    for (var i in logs) {
        if (logs[i].ISWAITING == '1') {
            if (logs[i].AUDITUSERID == uuid) {
                log = logs[i];
                break;
            }
        }
    }
    return log;
}

/**
 * 校验加签可行性
 * @param business_module
 * @param business_id
 * @returns {*}
 */
function wf_validateSign(business_module, business_id){
    var url = srvUrl + "sign/validateSign.do";
    var validate = null;
    $.ajax({
        url: url,
        type: "POST",
        dataType: "json",
        data: {"business_module": business_module,'business_id':business_id},
        async: false,
        success: function (data) {
            validate = data;
        }
    });
    return validate;
}

/**流程相关的一些公共方法结束**/
/**附件相关的一些公共方法开始**/
/**
 * 上传附件
 * @param docType
 * @param docCode
 * @param pageLocation
 * @returns {*}
 */
function attach_upload(docType, docCode, pageLocation){
    var url = srvUrl + "cloud/upload.do";
    var result = null;
    $.ajax({
        url: url,
        type: "POST",
        dataType: "json",
        data: {"docType": docType,'docCode':docCode,'pageLocation':pageLocation},
        async: false,
        success: function (data) {
            result = data;
        }
    });
    return result;
}
/**
 * 删除附件
 * @param fileId
 * @returns {*}
 */
function attach_delete(fileId){
    var url = srvUrl + "cloud/delete.do";
    var result = null;
    $.ajax({
        url: url,
        type: "POST",
        dataType: "json",
        data: {"fileId": fileId},
        async: false,
        success: function (data) {
            result = data;
        }
    });
    return result;
}
/**
 * 查询附件
 * @param docType
 * @param docCode
 * @param pageLocation
 * @returns {*}
 */
function attach_list(docType, docCode, pageLocation){
    var url = srvUrl + "cloud/list.do";
    var result = null;
    $.ajax({
        url: url,
        type: "POST",
        dataType: "json",
        data: {"docType": docType,'docCode':docCode,'pageLocation':pageLocation},
        async: false,
        success: function (data) {
            result = data;
        }
    });
    return result;
}
/**附件相关的一些公共方法结束**/
function isEmpty(s) {
    if (typeof s != "undefined" && s != null && s != '' && s != "") {
        return false;
    }
    return true;
}
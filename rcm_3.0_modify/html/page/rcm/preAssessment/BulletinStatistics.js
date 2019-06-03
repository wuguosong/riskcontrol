ctmApp.register.controller('BulletinStatistics', ['$timeout','$http','$scope','$location', function ($timeout,$http,$scope,$location) {
	$scope.orderby='desc';
	//获取当前年数组
	$scope.yearArrFunc = function(){
		$http({
			method:'post',  
		    url: srvUrl + "deptwork/getYearArr.do"
		}).success(function(result){
			if(result.success){
				$scope.yearArr = result.result_data.yearArr;
			}
		});
        
	}
	$scope.yearArrFunc();
	$scope.queryForPie = function (){
		var wf_state = "";
		var stage = "";
		$("span[name='stage'].label-danger").each(function(){
			wf_state += ","+$(this).context.attributes.data.value;
			if("" != $(this).context.attributes.value.value){
				stage += ","+$(this).context.attributes.value.value;
			}
		});
		wf_state = wf_state.substring(1);
		stage = stage.substring(1);
		
		var areaId = "";
		$("span[name='area'].label-danger").each(function(){
			areaId += ","+$(this).context.attributes.value.value;
		});
		areaId = areaId.substring(1);
		
		var serviceTypeId = "";
		$("span[name='serviceType'].label-danger").each(function(){
			serviceTypeId += ","+$(this).context.attributes.value.value;
		});
		serviceTypeId = serviceTypeId.substring(1);
		
		$http({
			method:'post',  
		    url: srvUrl + "deptwork/queryBulletinCount.do",
		    data:$.param({"wf_state":wf_state,
		    	"stage":stage,
		    	"pertainAreaId":areaId,
		    	"serviceTypeId":serviceTypeId,
		    	"year":$scope.year
		    	})
		}).success(function(result){
			var FormalcountforPie = result.result_data.list;
			$scope.year = result.result_data.year+"";
		
			//准备数据
			var preData1 = new Array();
			var total1 = 0;
			for(var i in FormalcountforPie){
				var item = {};
				var dt = FormalcountforPie[i];
				if(dt.NAME !='' && dt.NAME != undefined){
					item.name=dt.NAME;
				}else{
					item.name="未知";
				}
				total1 += dt.VALUE;
				item.value = dt.VALUE;
				preData1.push(item);
			}
			$scope.total1 = total1;
			//模板
			$scope.pk1 = {
			    tooltip : {
			        trigger: 'item',
			        formatter: "{a} <br/>{b} : {c} ({d}%)"
			    },
			    series : [
	              {
	            	  name:'数量',
	                  type: 'pie',
	                  radius : '60%',
	                  center: ['50%', '50%'],
	                  data:preData1,
	                  itemStyle: {
	                      emphasis: {
	                          shadowBlur: 10,
	                          shadowOffsetX: 0,
	                          shadowColor: 'rgba(0, 0, 0, 0.5)'
	                      },
	                      normal:{ 
		                      label:{ 
		                         show: true, 
		                         formatter: '{b} : {c} (个)' 
		                      }, 
		                      labelLine :{show:true}
		                  } 
	                  }
	              }
	          ]
	        };
			
			myChart = echarts.init(document.getElementById('review1'),'shine');
			myChart.setOption($scope.pk1);
		});
	}
	
	$scope.selectAllArea = function(event){
		if($(event.target)[0].checked){
			//全选
			$("span[name='area']").addClass("label-danger");
		}else{
			//全不选
			$("span[name='area']").removeClass("label-danger");
		}
	}
	$scope.selectAllStage = function(event){
		if($(event.target)[0].checked){
			//全选
			$("span[name='stage']").addClass("label-danger");
		}else{
			//全不选
			$("span[name='stage']").removeClass("label-danger");
		}
	}
	
	$scope.selectAllServiceType = function(event){
		if($(event.target)[0].checked){
			//全选
			$("span[name='serviceType']").addClass("label-danger");
		}else{
			//全不选
			$("span[name='serviceType']").removeClass("label-danger");
		}
	}
	
	$scope.aaa = function(event){
		if($(event.target).hasClass("label-danger")){
			$(event.target).removeClass("label-danger");  
		}else{
			$(event.target).addClass("label-danger");  
		}
	}
	
	//查询业务类型
	$scope.getServiceType = function(){
		$http({
			method:'post',  
		    url: srvUrl + "dict/queryDictItemByDictTypeCode.do",
		    params:{"code":"TBSX_BUSINESS"}
		}).success(function(result){
			$scope.serviceTypeList = result.result_data;
		});
	}
	$scope.getServiceType();
	$scope.serviceTypeFinfish = function(){
	}
	$scope.areaFinfish = function(){
		$timeout(function () {
			$scope.queryForPie();
		}, 100);
	}
	
	//查询大区
	$scope.getPertainArea = function(){
		$http({
			method:'post',  
		    url: srvUrl + "deptwork/queryBulletinPertainArea.do"
		}).success(function(result){
			$scope.pertainAreaList = result.result_data;
		});
	}
	$scope.getPertainArea();
	//查义所有的操作
	$scope.initData=function(){
		
		$http({
			method:'post',  
		    url: srvUrl + "deptwork/queryAllBulletinInfoByPage.do",
		    data: $.param({"page":JSON.stringify($scope.paginationConf)})
		}).success(function(result){
			$scope.projectPreReview = result.result_data.list;
			$scope.paginationConf.totalItems = result.result_data.totalItems;
		});
	};
	$scope.order=function(o,v){
		if(o=="time"){
			$scope.orderby=v;
			$scope.orderbystate=null;
			if(v=="asc"){
				$("#orderasc").addClass("cur");
				$("#orderdesc").removeClass("cur");
			}else{
				$("#orderdesc").addClass("cur");
				$("#orderasc").removeClass("cur");
			}
		}else{
			$scope.orderbystate=v;
			$scope.orderby=null;
			if(v=="asc"){
				$("#orderascstate").addClass("cur");
				$("#orderdescstate").removeClass("cur");
			}else{
				$("#orderdescstate").addClass("cur");
				$("#orderascstate").removeClass("cur");
			}
		}
		$scope.initData();
	}
	
	// 通过$watch currentPage和itemperPage 当他们一变化的时候，重新获取数据条目
	$scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage',  $scope.initData);
	//flag="5"代表是从部门工作台预评审申请列表进入工单详情
	$scope.flag="5";
	
	
    
	//初始化未过会表格
	$scope.initTableWgh = function(){
		$http({
			method: 'post',
			url: srvUrl + "deptwork/initTableBulletinWghByServietypeAndAreaWithNum.do"
		}).success(function(result){
			$scope.drawTableWgh(result.result_data);
		});
	};
    
	$scope.drawTableWgh = function(data){
		var serviceTypeList = data.serviceTypeList; //左
    	var areaList = data.areaList;//上
    	var dataList = data.dataList;//数据
    	var totalList =data.totalList;//统计
    	var html = '<tr >' ;
    	var trHtml = '<th>业务类型/区域</th>';
    	var oldUrl = window.btoa(encodeURIComponent(escape("#/bulletinStatistics")));
    	//表头
    	for (var i = 0; i < areaList.length; i++) {
			var name = areaList[i].name;
			trHtml += "<th><a href='#/bulletinDeptWorkList/\"\"/"+areaList[i].value+"/1.5,2,3/"+oldUrl+"'>" + name + "</a></th>";
		}
    	trHtml +=  '<th> 总计 </th>' + '</tr >';
    	html += trHtml;
    	//列头  以及数据
    	for (var i = 0; i < serviceTypeList.length; i++) {
    		var tDHtml ='' ;
			var serviceType = serviceTypeList[i].name;
			tDHtml += "<tr> <td><a href='#/bulletinDeptWorkList/"+serviceTypeList[i].value+"/\"\"/1.5,2,3/"+oldUrl+"'>" + serviceType + "</a></td>";
			//数据
			var dataHtml ='';
			var dataArr = dataList[i].data;
			var dataObject = dataList[i].data;
			var tatol = 0;
			for(var k = 0; k < areaList.length; k++) {
				var data = dataObject[areaList[k].value];
				if(data == undefined || data == null){
					data = 0;
				}
				tatol += data;
				dataHtml += ' <td>' + data + '</td>';
			}
			dataHtml += ' <td>' + tatol + '</td>';
			tDHtml +=dataHtml;
			tDHtml += '</tr>';
			html += tDHtml;
		}
    	
    	//统计数据
    	var totalHtml ='<tr> <td>总计</td>';
    	var numTotal = 0;
    	for (var i = 0; i < areaList.length; i++) {
			var area = areaList[i].name;
			//数据
			for (var j = 0; j < totalList.length; j++) {
				var ttt = totalList[j];
				if(area == ttt.NAME){
					totalHtml += ' <td>' + ttt.VALUE + '</td>';
					numTotal += ttt.VALUE;
				}
			}
			
		}
    	totalHtml += '<td>'+ numTotal +'</td></tr>';
    	html += totalHtml;
    	
    	$('#wghTable').append(html);
    };
    
    
    //初始化已过会表格
	$scope.initTableYgh = function(){
		$http({
			method: 'post',
			url: srvUrl + "deptwork/initTableBulletinYghByServietypeAndAreaWithNum.do"
		}).success(function(result){
			$scope.drawTableYgh(result.result_data);
		});
	};
    
	$scope.drawTableYgh = function(data){
		var serviceTypeList = data.serviceTypeList; //左
    	var areaList = data.areaList;//上
    	var dataList = data.dataList;//数据
    	var totalList =data.totalList;//统计
    	var html = '<tr >' ;
    	var trHtml = '<th>业务类型/区域</th>';
    	var oldUrl = window.btoa(encodeURIComponent(escape("#/bulletinStatistics")));
    	//表头
    	for (var i = 0; i < areaList.length; i++) {
    		var name = areaList[i].name;
    		trHtml += "<th><a href='#/bulletinDeptWorkList/\"\"/"+areaList[i].value+"/4,5/"+oldUrl+"'>" + name + "</a></th>";
    	}
    	trHtml +=  '<th> 总计 </th>' + '</tr >';
    	html += trHtml;
    	//列头  以及数据
    	for (var i = 0; i < serviceTypeList.length; i++) {
    		var tDHtml ='' ;
			var serviceType = serviceTypeList[i].name;
			tDHtml += "<tr> <td><a href='#/bulletinDeptWorkList/"+serviceTypeList[i].value+"/\"\"/4,5/"+oldUrl+"'>" + serviceType + "</a></td>";
			//数据
			var dataHtml ='';
			var dataArr = dataList[i].data;
			var dataObject = dataList[i].data;
			var tatol = 0;
			for(var k = 0; k < areaList.length; k++) {
				var data = dataObject[areaList[k].value];
				if(data == undefined || data == null){
					data = 0;
				}
				tatol += data;
				dataHtml += ' <td>' + data + '</td>';
			}
			dataHtml += ' <td>' + tatol + '</td>';
			tDHtml +=dataHtml;
			tDHtml += '</tr>';
			html += tDHtml;
		}
    	
    	//统计数据
    	var totalHtml ='<tr> <td>总计</td>';
    	var numTotal = 0;
    	for (var i = 0; i < areaList.length; i++) {
			var area = areaList[i].name;
			//数据
			for (var j = 0; j < totalList.length; j++) {
				var ttt = totalList[j];
				if(area == ttt.NAME){
					totalHtml += ' <td>' + ttt.VALUE + '</td>';
					numTotal += ttt.VALUE;
				}
			}
			
		}
    	totalHtml += '<td>'+ numTotal +'</td></tr>';
    	html += totalHtml;
    	
    	
    	$('#yghTable').append(html);
    };
    
    
    //类型统计图
    //跟进中
    $scope.initEChartsPieLX = function(){
		$('a[data-toggle="tab"]').on('shown.lx.tab', function (e) {
			// 获取已激活的标签页的名称
			var activeTab = $(e.target)[0].hash;
			if(activeTab=="#lx-tabdrop-tab1") {
				myChart = echarts.init(document.getElementById('reviewLX1'),'shine');
				myChart.setOption($scope.WghLX1);
			}
		});
		$scope.initEchartsPieLXData1();
	};
	
	$scope.initEchartsPieLXData1 = function(){
		$http({
			method: 'post',
			url: srvUrl + "deptwork/getBulletinFromWghByServietypeAndAreaWithNum.do"
		}).success(function(result){
			$scope.drawLXData1(result.result_data);
		});
	};
	

    $scope.drawLXData1 = function(data){
    	$scope.ReportLX1 = echarts.init(document.getElementById('reviewLX1'),'shine');
		var serviceTypeList = data.serviceTypeList;
    	var areaList = data.areaList;
    	var dataList = data.dataList;
    	$scope.WghLX1 = {
    		    tooltip : {
    		        trigger: 'axis',
    		        axisPointer : {            // 坐标轴指示器，坐标轴触发有效
    		            type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
    		        }
    		    },
    		    legend: {
    		        data:serviceTypeList
    		    },
    		   
    		    xAxis : [
    		        {
    		            type : 'category',
    		            data :areaList
    		        }
    		    ],
    		    yAxis : [
    		        {
    		            type : 'value'
    		        }
    		    ],
    		    series : dataList
		};
        $scope.ReportLX1.setOption($scope.WghLX1);
        
                   
    };
    
    
    //已过会
    $scope.initEChartsPieLXYgh = function(){
		$('a[data-toggle="tab"]').on('shown.yghlx.tab', function (e) {
			// 获取已激活的标签页的名称
			var activeTab = $(e.target)[0].hash;
			if(activeTab=="#yghlx-tabdrop-tab1") {
				myChart = echarts.init(document.getElementById('reviewYghLX1'),'shine');
				myChart.setOption($scope.YghLX1);
			}
		});
		$scope.initEchartsPieLXYghData1();
	};
	
	$scope.initEchartsPieLXYghData1 = function(){
		$http({
			method: 'post',			
			url: srvUrl + "deptwork/getBulletinFromYghByServietypeAndAreaWithNum.do"
		}).success(function(result){
			$scope.drawLXYghData1(result.result_data);
		});
	};
	

    $scope.drawLXYghData1 = function(data){
    	$scope.ReportyghLX1 = echarts.init(document.getElementById('reviewYghLX1'),'shine');
    	var serviceTypeList = data.serviceTypeList;
    	var areaList = data.areaList;
    	var dataList = data.dataList;
    	$scope.YghLX1 = {
    		    tooltip : {
    		        trigger: 'axis',
    		        axisPointer : {            // 坐标轴指示器，坐标轴触发有效
    		            type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
    		        }
    		    },
    		    legend: {
    		        data:serviceTypeList
    		    },
    		   
    		    xAxis : [
    		        {
    		            type : 'category',
    		            data :areaList
    		        }
    		    ],
    		    yAxis : [
    		        {
    		            type : 'value'
    		        }
    		    ],
    		    series : dataList
    		};
        $scope.ReportyghLX1.setOption($scope.YghLX1);
        
                   
    };
    
    
    //导出投标评审项目列表
    $scope.exportFormaReportInfo = function(){
    	$http({
    		method:'post',
    		url:srvUrl+'reviewStatisticsform/exportFormaReportInfo.do'
    	}).success(function(data){
    		if(data.success){
    			var files = data.result_data.filePath;
    			var index = files.lastIndexOf(".");
                var str = files.substring(index + 1, files.length);
                var fileName = data.result_data.fileName;
                var result = data.result_data.result;
    			window.location.href = srvUrl+"file/downloadFile.do?filepaths="+encodeURI(files)+"&filenames="+encodeURI(encodeURI("正式评审-"+""+"报告."))+str;
    		}else{
    			$.alert("文档生成失败!");
    		}
    	}).error(function(data,status,headers,config){
			$.alert(status);
		});
    }
    
    
    $scope.qiehuanNoCompleted = function(){
    	if($("#noCompleted").is(':hidden')){  
            $("#completed").hide(); 
            $("#noCompleted").show(); 
        }
    }
    $scope.qiehuanCompleted = function(){
    	if($("#completed").is(':hidden')){  
    		$("#noCompleted").hide(); 
            $("#completed").show(); 
        }
    }
    
    $scope.qiehuannoCompleted1 = function(){
    	if($("#noCompleted1").is(':hidden')){  
            $("#completed1").hide(); 
            $("#noCompleted1").show(); 
        }
    }
    $scope.qiehuanCompleted1 = function(){
    	if($("#completed1").is(':hidden')){  
    		$("#noCompleted1").hide(); 
            $("#completed1").show(); 
        }
    }
    $scope.qiehuanOne = function(){
    	if($("#one").is(':hidden')){  
    		$("#one").hide(); 
        }
    }
    $scope.hideDiv = function(){
    	 $("#completed").hide(); 
    	 $("#completed1").hide(); 
    }
   
    
    //初始化未过会表格
    $scope.initTableWgh();
    $scope.initTableYgh();
    $scope.hideDiv();
    
	$scope.rounding = function keepTwoDecimalFull(num) {
		  var result = parseFloat(num);
		  if (isNaN(result)) {
		    alert('传递参数错误，请检查！');
		    return false;
		  }
		  result = Math.round(num * 10000) / 10000;
		  var s_x = result.toString();
		  var pos_decimal = s_x.indexOf('.');
		  if (pos_decimal < 0) {
		    pos_decimal = s_x.length;
		    s_x += '.';
		  }
		  while (s_x.length <= pos_decimal + 2) {
		    s_x += '0';
		  }
		  return s_x;
		}

	//导出未过会投标评审项目列表
    $scope.exportTenderReportInfoWgh = function(){
    	$http({
    		method:'post',
    		url:srvUrl+'reviewStatisticsform/exportTenderReportInfoWgh.do'
    	}).success(function(data){
    		if(data.success){
    			var files = data.result_data.filePath;
    			var index = files.lastIndexOf(".");
                var str = files.substring(index + 1, files.length);
                var fileName = data.result_data.fileName;
                var result = data.result_data.result;
    			window.location.href = srvUrl+"file/downloadFile.do?filepaths="+encodeURI(files)+"&filenames="+encodeURI(encodeURI("投标评审-"+""+"报告."))+str;
    		}else{
    			$.alert("文档生成失败!");
    		}
    	}).error(function(data,status,headers,config){
			$.alert(status);
		});
    }
	
	
	//导出已过会投标评审项目列表
    $scope.exportTenderReportInfo = function(){
    	$http({
    		method:'post',
    		url:srvUrl+'reviewStatisticsform/exportTenderReportInfo.do'
    	}).success(function(data){
    		if(data.success){
    			var files = data.result_data.filePath;
    			var index = files.lastIndexOf(".");
                var str = files.substring(index + 1, files.length);
                var fileName = data.result_data.fileName;
                var result = data.result_data.result;
    			window.location.href = srvUrl+"file/downloadFile.do?filepaths="+encodeURI(files)+"&filenames="+encodeURI(encodeURI("投标评审-"+""+"报告."))+str;
    		}else{
    			$.alert("文档生成失败!");
    		}
    	}).error(function(data,status,headers,config){
			$.alert(status);
		});
    }
}]);
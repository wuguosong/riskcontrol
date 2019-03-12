ctmApp.register.controller('NoticeReviewListReadOnly', ['$timeout','$http','$scope','$location', function ($timeout,$http,$scope,$location) {
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
	$("span[name='result']").addClass("label-danger");
	$("input[name='stageAll']").attr("checked",true);
	$scope.queryForPie = function (){
		var wf_state = "";
		var result = "";
		$("span[name='result'].label-danger").each(function(){
//			wf_state += ","+$(this).context.attributes.data.value;
			result += ","+$(this).context.attributes.value.value;
		});
		wf_state = wf_state.substring(1);
		result = result.substring(1);
		
		var areaId = "";
		$("span[name='area'].label-danger").each(function(){
			areaId += ","+$(this).context.attributes.value.value;
		});
		areaId = areaId.substring(1);
		
		//验证大区
		if(areaId == null || areaId == ''){
			$.alert("请选择大区！");
			return;
		}
		
		var serviceTypeId = "";
		$("span[name='serviceType'].label-danger").each(function(){
			serviceTypeId += ","+$(this).context.attributes.value.value;
		});
		serviceTypeId = serviceTypeId.substring(1);
		
		$http({
			method:'post',  
		    url: srvUrl + "deptwork/queryNoticeCount.do",
		    data:$.param({"wf_state":wf_state,
		    	"result":result,
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
	
	//初始化同意表格
	$scope.initTableWgh = function(){
		$http({
			method: 'post',
			url: srvUrl + "deptwork/getNoticeTYCount.do"
		}).success(function(result){
			$scope.drawTableWgh(result.result_data);
		});
	};
	$scope.initTableWgh();
	
	$scope.drawTableWgh = function(data){
		var serviceTypeList = data.serviceTypeList; //左
    	var areaList = data.areaList;//上
    	var dataList = data.dataList;//数据
    	var totalList =data.totalList;//统计
    	var html = '<tr >' ;
    	var trHtml = '<th>项目类型/区域</th>';
    	//表头
    	for (var i = 0; i < areaList.length; i++) {
			var name = areaList[i];
			trHtml += '<th>' + name + '</th>';
		}
    	trHtml +=  '<th> 总计 </th>' + '</tr >';
    	html += trHtml;
    	//列头  以及数据
    	for (var i = 0; i < serviceTypeList.length; i++) {
    		var tDHtml ='' ;
			var serviceType = serviceTypeList[i];
			tDHtml += '<tr> <td>' + serviceType + '</td>';
			//数据
			var dataHtml ='';
			var dataObject = dataList[i].data;
			var tatol = 0;
			for(var k = 0; k < areaList.length; k++) {
				var data = dataObject[areaList[k]];
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
			var area = areaList[i];
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
    	
    	$('#TYTable').append(html);
    };
	
  //初始化不同意表格
	$scope.initTableYgh = function(){
		$http({
			method: 'post',
			url: srvUrl + "deptwork/getNoticeBTYCount.do"
		}).success(function(result){
			$scope.drawTableYgh(result.result_data);
		});
	};
	$scope.initTableYgh();
	$scope.drawTableYgh = function(data){
		var serviceTypeList = data.serviceTypeList; //左
    	var areaList = data.areaList;//上
    	var dataList = data.dataList;//数据
    	var totalList =data.totalList;//统计
    	var html = '<tr >' ;
    	var trHtml = '<th>项目类型/区域</th>';
    	//表头
    	for (var i = 0; i < areaList.length; i++) {
			var name = areaList[i];
			trHtml += '<th>' + name + '</th>';
		}
    	trHtml +=  '<th> 总计 </th>' + '</tr >';
    	html += trHtml;
    	//列头  以及数据
    	for (var i = 0; i < serviceTypeList.length; i++) {
    		var tDHtml ='' ;
			var serviceType = serviceTypeList[i];
			tDHtml += '<tr> <td>' + serviceType + '</td>';
			//数据
			var dataHtml ='';
			var dataObject = dataList[i].data;
			var tatol = 0;
			for(var k = 0; k < areaList.length; k++) {
				var data = dataObject[areaList[k]];
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
			var area = areaList[i];
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
    	
    	
    	$('#BTYTable').append(html);
    };
    
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
			$("span[name='result']").addClass("label-danger");
		}else{
			//全不选
			$("span[name='result']").removeClass("label-danger");
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
		    url: srvUrl + "deptwork/queryNoticedType.do"
		}).success(function(result){
			$scope.serviceTypeList = result.result_data;
		});
	}
	$scope.getServiceType();
	$scope.serviceTypeFinfish = function(){
		$("input[name='serviceTypeAll']").attr("checked",true);
		$("span[name='serviceType']").addClass("label-danger");
	}
	$scope.areaFinfish = function(){
		$("input[name='areaAll']").attr("checked",true);
		$("span[name='area']").addClass("label-danger");
		
		$timeout(function () {
			$scope.queryForPie();
		}, 100);
	}
	
	//查询大区
	$scope.getPertainArea = function(){
		$http({
			method:'post',  
		    url: srvUrl + "deptwork/queryNoticedPertainArea.do"
		}).success(function(result){
			$scope.pertainAreaList = result.result_data;
		});
	}

	//查询正式评审基本信息列表--起草中
	$scope.paginationConf.queryObj = {};
	$scope.queryAllFormalAssessmentInfoByPage = function(){
		$http({
			method:'post',  
		    url: srvUrl + "deptwork/queryAllFormalAssessmentInfoByPage.do",
		    data: $.param({"page":JSON.stringify($scope.paginationConf)})
		}).success(function(result){
			$scope.formalAssessmentList = result.result_data.list;
			$scope.paginationConf.totalItems = result.result_data.totalItems;
		});
	}
    $scope.order=function(o,v){
        if(o=="time"){
        	$scope.paginationConf.queryObj.orderby_apptime=v;
        	$scope.paginationConf.queryObj.orderby_state=null;
            if(v=="asc"){
                $("#orderasc").addClass("cur");
                $("#orderdesc").removeClass("cur");
            }else{
                $("#orderdesc").addClass("cur");
                $("#orderasc").removeClass("cur");
            }
        }else{
        	$scope.paginationConf.queryObj.orderby_apptime=null;
        	$scope.paginationConf.queryObj.orderby_state=v;
            if(v=="asc"){
                $("#orderascstate").addClass("cur");
                $("#orderdescstate").removeClass("cur");
            }else{
                $("#orderdescstate").addClass("cur");
                $("#orderascstate").removeClass("cur");
            }
        }
        $scope.queryAllFormalAssessmentInfoByPage();
    }
    // 通过$watch currentPage 和 itemperPage 当他们一变化的时候，重新获取数据条目
    $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.queryAllFormalAssessmentInfoByPage);
    
    
    
    //业务类型统计
    $scope.initEChartsPieTJ = function(){
	};
	
	//战区pk   规模
	$scope.initEchartsPieYGHData2 = function(){
		$http({
			method: 'post',
			url: srvUrl + "deptwork/getFromYghGroupAreaWithGm.do"
		}).success(function(result){
			var data = result.result_data;
		});
	};
    
	//规模
	$scope.initEchartsPieYGHDataTj2 = function(){
		$http({
			method: 'post',
			url: srvUrl + "deptwork/getFromYghGroupServiceWithGm.do"
		}).success(function(result){
			var data = result.result_data;
			$scope.drawYGHDataTj2(data);
		});
	};
    $scope.drawYGHDataTj2 = function(data){
    	//规模
        var fromYghGroupServiceWithGm = data.fromYghGroupServiceWithGm;
		var preData1 = new Array();
		var total8 = 0 ;
		for(var i = 0; i < fromYghGroupServiceWithGm.length; i++){
			var item = {};
			var dt = fromYghGroupServiceWithGm[i];
			if(dt.NAME !='' && dt.NAME != undefined){
				item.name=dt.NAME;
			}else{
				item.name="未知";
			}
			if(dt.VALUE !='' && dt.VALUE != undefined){
				item.value=dt.VALUE;
			}else{
				item.value=0;
			}
			total8 += item.value;
			
			preData1.push(item);
		}
		total8 = $scope.rounding(total8);
		$scope.yghTJ2 = {
			title : {
		        subtext: '总数:'+ total8 +'万吨',
		        x:'left'
		    },
    		tooltip : {
		        trigger: 'item',
		        formatter: "{a} <br/>{b} : {c} ({d}%)"
		    },
		    series : [
		              {
		            	  name:'规模',
		                  type: 'pie',
		                  radius : '50%',
   		                  center: ['50%', '55%'],
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
			                         formatter: '{b} : {c} (万吨)' 
			                      }, 
			                      labelLine :{show:true}
			                  } 
		                  }
		              }
		          ]
        };
    };
	
    //导出正式评审项目列表
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
   
    $scope.hideDiv();
    $scope.getPertainArea();
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
}]);

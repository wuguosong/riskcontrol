ctmApp.register.controller('ProjectFormalReviewListReadOnly', ['$timeout','$http','$scope','$location', function ($timeout,$http,$scope,$location) {
	var oldUrl = window.btoa(encodeURIComponent(escape("#/ProjectFormalReviewListReadOnly")));
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
		$scope.wf_state = "";
		$scope.stage = "";
		$scope.result = "";
		$("span[name='stage'].label-danger").each(function(){
			$scope.wf_state += ","+$(this).context.attributes.data.value;
			$scope.stage += ","+$(this).context.attributes.value.value;
			if($(this).context.attributes.resultData != null && $(this).context.attributes.resultData != ""){
				$scope.result += ","+$(this).context.attributes.resultData.value;
			}
		});
		$scope.wf_state = $scope.wf_state.substring(1);
		$scope.stage = $scope.stage.substring(1);
		$scope.result = $scope.result.substring(1);
		
		$scope.areaId = "";
		$("span[name='area'].label-danger").each(function(){
			$scope.areaId += ","+$(this).context.attributes.value.value;
		});
		$scope.areaId = $scope.areaId.substring(1);
		
		
		$scope.serviceTypeId = "";
		$("span[name='serviceType'].label-danger").each(function(){
			$scope.serviceTypeId += ","+$(this).context.attributes.value.value;
		});
		$scope.serviceTypeId = $scope.serviceTypeId.substring(1);
		
		$http({
			method:'post',  
		    url: srvUrl + "deptwork/queryFormalCount.do",
		    data:$.param({"wf_state":$scope.wf_state,
		    	"stage":$scope.stage,
		    	"result":$scope.result,
		    	"pertainAreaId":$scope.areaId,
		    	"serviceTypeId":$scope.serviceTypeId,
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
					item.areaId = dt.AREAID;
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
			myChart.on('click', function(params) {
			    var newServiceType = $scope.serviceTypeId.split(",");
			    var sType = "";
			    for(var i in newServiceType){
			    	sType+="','"+newServiceType[i];
			    }
			    sType = sType.substring(3);
			    var url = "#/formalDeptWorkList/"+sType+"/"+params.data.areaId+"/"+$scope.stage;
			})
			myChart.setOption($scope.pk1);
		});
	}
	
	//初始化未过会表格
	$scope.initTableWgh = function(){
		$http({
			method: 'post',
			url: srvUrl + "deptwork/initTableFormalWghByServietypeAndAreaWithNum.do"
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
    	var trHtml = '<th>业务类型/区域</th>';
    	//表头
    	for (var i = 0; i < areaList.length; i++) {
			var name = areaList[i].name;
			trHtml += "<th><a href='#/formalDeptWorkList/\"\"/"+areaList[i].value+"/3,3.5,3.7,3.9,4,5/"+oldUrl+"'>" + name + "</a></th>";
		}
    	trHtml +=  '<th> 总计 </th>' + '</tr >';
    	html += trHtml;
    	//列头  以及数据
    	for (var i = 0; i < serviceTypeList.length; i++) {
    		var tDHtml ='' ;
			var serviceType = serviceTypeList[i].name;
			tDHtml += "<tr> <td><a href='#/formalDeptWorkList/"+serviceTypeList[i].value+"/\"\"/3,3.5,3.7,3.9,4,5/"+oldUrl+"'>" + serviceType + "</a></td>";
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
			url: srvUrl + "deptwork/initTableFormalYghByServietypeAndAreaWithNum.do"
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
    	var trHtml = '<th>业务类型/区域</th>';
    	//表头
    	for (var i = 0; i < areaList.length; i++) {
			var name = areaList[i].name;
			trHtml += "<th><a href='#/formalDeptWorkList/\"\"/"+areaList[i].value+"/6,7,9/"+oldUrl+"'>" + name + "</a></th>";
		}
    	trHtml +=  '<th> 总计 </th>' + '</tr >';
    	html += trHtml;
    	//列头  以及数据
    	for (var i = 0; i < serviceTypeList.length; i++) {
    		var tDHtml ='' ;
			var serviceType = serviceTypeList[i].name;
			tDHtml += "<tr> <td><a href='#/formalDeptWorkList/"+serviceTypeList[i].value+"/\"\"/6,7,9/"+oldUrl+"'>" + serviceType + "</a></td>";
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
		    params:{"code":"14"}
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
		    url: srvUrl + "deptwork/queryFormalPertainArea.do"
		}).success(function(result){
			$scope.pertainAreaList = result.result_data;
		});
	}

    
    //导出正式评审项目列表
    $scope.exportFormaWghReportInfo = function(){
    	$http({
    		method:'post',
    		url:srvUrl+'reviewStatisticsform/exportFormaWghReportInfo.do'
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

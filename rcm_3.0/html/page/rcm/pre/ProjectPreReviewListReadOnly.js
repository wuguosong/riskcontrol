ctmApp.register.controller('ProjectPreReviewListReadOnly', ['$http','$scope','$location', function ($http,$scope,$location) {
	//查询正式评审基本信息列表--起草中
	$scope.paginationConf.queryObj = {};
	$scope.queryAllPreInfoByPage = function(){
		$http({
			method:'post',  
		    url: srvUrl + "deptwork/queryAllPreInfoByPage.do",
		    data: $.param({"page":JSON.stringify($scope.paginationConf)})
		}).success(function(result){
			$scope.preList = result.result_data.list;
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
        $scope.queryAllPreInfoByPage();
    }
    // 通过$watch currentPage 和 itemperPage 当他们一变化的时候，重新获取数据条目
    $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage', $scope.queryAllPreInfoByPage);
    
    //new
    //战区pk
    //跟进中
    $scope.initEChartsPiePK = function(){
		$('a[data-toggle="tab"]').on('shown.ff.tab', function (e) {
			// 获取已激活的标签页的名称
			var activeTab = $(e.target)[0].hash;
			if(activeTab=="#ff-tabdrop-tab1") {
				myChart = echarts.init(document.getElementById('review1'),'shine');
				myChart.setOption($scope.pk1);
			}
			if(activeTab=="#ff-tabdrop-tab2"){
				myChart2 = echarts.init(document.getElementById('review2'),'shine');
				myChart2.setOption($scope.pk2);
			}
		});
		$scope.initEchartsPieWGHData1();
		$scope.initEchartsPieWGHData2();
	};
	//项目数量
	$scope.initEchartsPieWGHData1 = function(){
		$http({
			method: 'post',
			url: srvUrl + "deptwork/getPreFromWghGroupArea.do"
		}).success(function(result){
			var data = result.result_data;
			$scope.drawWGHData1(data);
		});
	};
	
    $scope.drawWGHData1 = function(data){
    	//跟进中
    	//数量饼图
    	$scope.ReportDistribution1 = echarts.init(document.getElementById('review1'),'shine');
		var FormalaAllGroupAreaReports = data;
		var preData1 = new Array();
		var total1 = 0;
		for(var i = 0; i < FormalaAllGroupAreaReports.length; i++){
			var item = {};
			var dt = FormalaAllGroupAreaReports[i];
			if(dt.NAME !='' && dt.NAME != undefined){
				item.name=dt.NAME;
			}else{
				item.name="未知";
			}
			total1 += dt.VALUE;
			item.value = dt.VALUE;
			preData1.push(item);
		}
		$scope.pk1 = {
			title : {
		        subtext: '总数:'+ total1 +'个',
		        x:'left'
		    },
		    tooltip : {
		        trigger: 'item',
		        formatter: "{a} <br/>{b} : {c} ({d}%)"
		    },
		    series : [
		              {
		            	  name:'数量',
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
			                         formatter: '{b} : {c} (个)' 
			                      }, 
			                      labelLine :{show:true}
			                  } 
		                  }
		              }
		          ]
        };
        $scope.ReportDistribution1.setOption($scope.pk1);
        
                   
    };
    
	//规模
	$scope.initEchartsPieWGHData2 = function(){
		$http({
			method: 'post',
			url: srvUrl + "deptwork/getPreFromWghGroupAreaWithGm.do"
		}).success(function(result){
			var data = result.result_data;
			$scope.drawWGHData2(data);
		});
	};
    $scope.drawWGHData2 = function(data){
    	//规模
    	$scope.fromWghGroupAreaReports = echarts.init(document.getElementById('review2'),'shine');
        var preaWghReportsByAreaWithGm = data;
		var preData1 = new Array();
		var total2 = 0 ;
		for(var i = 0; i < preaWghReportsByAreaWithGm.length; i++){
			var item = {};
			var dt = preaWghReportsByAreaWithGm[i];
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
			total2 += item.value;
			
			preData1.push(item);
		}
		total2 = $scope.rounding(total2);
		$scope.pk2 = {
			title : {
		        subtext: '总数:'+ total2 +'万吨',
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
		$scope.fromWghGroupAreaReports.setOption($scope.pk2);
    };
    
    //业务类型统计
    $scope.initEChartsPieTJ = function(){
		$('a[data-toggle="tab"]').on('shown.tj.tab', function (e) {
			// 获取已激活的标签页的名称
			var activeTab = $(e.target)[0].hash;
			if(activeTab=="#tj-tabdrop-tab1") {
				myChart = echarts.init(document.getElementById('TJ1'),'shine');
				myChart.setOption($scope.tj1);
			}
			if(activeTab=="#tj-tabdrop-tab2"){
				myChart2 = echarts.init(document.getElementById('TJ2'),'shine');
				myChart2.setOption($scope.tjc2);
			}
		});
		$scope.initEchartsPieTJData1();
		$scope.initEchartsPieTJData2();
	};
	
	$scope.initEchartsPieTJData1 = function(){
		$http({
			method: 'post',
			url: srvUrl + "deptwork/getPreFromWghByServietype.do"
		}).success(function(result){
			$scope.drawTJData1(result.result_data);
		});
	};
	

    $scope.drawTJData1 = function(data){
    	//跟进中 业务类型 数量
    	$scope.ReportTJ1 = echarts.init(document.getElementById('TJ1'),'shine');
        var formalaWghReportsByServiceType = data;
		var preData1 = new Array();
		var total3 = 0 ;
		for(var i = 0; i < formalaWghReportsByServiceType.length; i++){
			var item = {};
			var dt = formalaWghReportsByServiceType[i];
			if(dt.NAME !='' && dt.NAME != undefined){
				item.name=dt.NAME;
			}else{
				item.name="未知";
			}
			item.value = dt.VALUE;
			total3 += dt.VALUE;
			preData1.push(item);
		}
    	$scope.tj1 =  {
    		    title : {
    		        subtext: '总数:' + total3 +'个',
    		        x:'left'
    		    },
    		    tooltip : {
    		        trigger: 'item',
    		        formatter: "{a} <br/>{b} : {c} ({d}%)"
    		    },
    		    series : [
    		        {
    		            name: '数量',
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
			                         formatter: '{b} : {c} (个)' 
			                      }, 
			                      labelLine :{show:true}
			                  } 
    		            }
    		        }
    		    ]
    		};
        $scope.ReportTJ1.setOption($scope.tj1);
        
                   
    };
    
	$scope.initEchartsPieTJData2 = function(){
		$http({
			method: 'post',
			url: srvUrl + "deptwork/getPreFromWghByServietypeWithGm.do"
		}).success(function(result){
			$scope.drawTJData2(result.result_data);
		});
	};
    $scope.drawTJData2 = function(data){
    	//跟进中   业务类型  规模
    	$scope.ReportTJ2 = echarts.init(document.getElementById('TJ2'),'shine');
        var formalaWghReportsByServiceWithGm = data;
		var preData1 = new Array();
		var total4 = 0 ;
		for(var i = 0; i < formalaWghReportsByServiceWithGm.length; i++){
			var item = {};
			var dt = formalaWghReportsByServiceWithGm[i];
			if(dt !='' && dt != undefined){
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
				total4 += item.value;
				preData1.push(item);
			}
		}
		total4 = $scope.rounding(total4);
		$scope.tj2 = {
			title : {
		        subtext: '总数:'+ total4 +'万吨',
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
		$scope.ReportTJ2.setOption($scope.tj2);
    };
    
    //已完成
    //1
    $scope.initEChartsPieYgh = function(){
		$('a[data-toggle="tab"]').on('shown.yghPK.tab', function (e) {
			// 获取已激活的标签页的名称
			var activeTab = $(e.target)[0].hash;
			if(activeTab=="#yghPK-tabdrop-tab1") {
				myChart = echarts.init(document.getElementById('reviewYghPK1'),'shine');
				myChart.setOption($scope.yghPK1);
			}
			if(activeTab=="#yghPK-tabdrop-tab2"){
				myChart2 = echarts.init(document.getElementById('reviewYghPK2'),'shine');
				myChart2.setOption($scope.yghPK2);
			}
		});
		$scope.initEchartsPieYGHData1();
		$scope.initEchartsPieYGHData2();
	};
	//项目数量
	$scope.initEchartsPieYGHData1 = function(){
		$http({
			method: 'post',
			url: srvUrl + "deptwork/getPreFromYghGroupArea.do"
		}).success(function(result){
			var data = result.result_data;
			$scope.drawYGHData1(data);
		});
	};
	
    $scope.drawYGHData1 = function(data){
    	//已完成
    	//数量饼图
    	$scope.ReportDistributionyghPK1 = echarts.init(document.getElementById('reviewYghPK1'),'shine');
		var FormalaAllGroupAreaReports = data;
		var preData1 = new Array();
		var total5 = 0;
		for(var i = 0; i < FormalaAllGroupAreaReports.length; i++){
			var item = {};
			var dt = FormalaAllGroupAreaReports[i];
			if(dt.NAME !='' && dt.NAME != undefined){
				item.name=dt.NAME;
			}else{
				item.name="未知";
			}
			total5 += dt.VALUE;
			item.value = dt.VALUE;
			preData1.push(item);
		}
		$scope.yghPK1 ={
				title : {
    		        subtext: '总数:'+ total5 +'个',
    		        x:'left'
    		    },
		    tooltip : {
		        trigger: 'item',
		        formatter: "{b} : {c} ({d}%)"
		    },
		    
		    series : [
		              {
		            	  name:'数量',
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
			                         formatter: '{b} : {c} (个)' 
			                      }, 
			                      labelLine :{show:true}
			                  } 
		                  }
		              }
		          ]
        };
        $scope.ReportDistributionyghPK1.setOption($scope.yghPK1);
        
                   
    };
    
	//战区pk   规模
	$scope.initEchartsPieYGHData2 = function(){
		$http({
			method: 'post',
			url: srvUrl + "deptwork/getPreFromYghGroupAreaWithGm.do"
		}).success(function(result){
			var data = result.result_data;
			$scope.drawYGHData2(data);
		});
	};
    $scope.drawYGHData2 = function(data){
    	//规模
    	$scope.fromYghGroupAreaReports = echarts.init(document.getElementById('reviewYghPK2'),'shine');
        var formalaYghReportsByAreaWithGm = data;
		var preData1 = new Array();
		var total6 = 0 ;
		for(var i = 0; i < formalaYghReportsByAreaWithGm.length; i++){
			var item = {};
			var dt = formalaYghReportsByAreaWithGm[i];
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
			total6 += item.value;
			
			preData1.push(item);
		}
		total6 = $scope.rounding(total6);
		$scope.yghPK2 = {
			title : {
		        subtext: '总数:'+ total6 +'万吨',
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
		$scope.fromYghGroupAreaReports.setOption($scope.yghPK2);
    };
    
    
    
    //业务类型统计
    $scope.initEChartsPieYghService = function(){
		$('a[data-toggle="tab"]').on('shown.yghtj.tab', function (e) {
			// 获取已激活的标签页的名称
			var activeTab = $(e.target)[0].hash;
			if(activeTab=="#yghtj-tabdrop-tab1") {
				myChart = echarts.init(document.getElementById('TJYghLx1'),'shine');
				myChart.setOption($scope.yghTJ1);
			}
			if(activeTab=="#yghtj-tabdrop-tab2"){
				myChart2 = echarts.init(document.getElementById('TJYghLx2'),'shine');
				myChart2.setOption($scope.yghTJ2);
			}
		});
		$scope.initEchartsPieYGHDataTj1();
		$scope.initEchartsPieYGHDataTj2();
	};
	//项目数量
	$scope.initEchartsPieYGHDataTj1 = function(){
		$http({
			method: 'post',
			url: srvUrl + "deptwork/getPreFromYghGroupServiceWithNum.do"
		}).success(function(result){
			var data = result.result_data;
			$scope.drawYGHDataTj1(data);
		});
	};
	
    $scope.drawYGHDataTj1 = function(data){
    	//已完成
    	//数量饼图
    	$scope.ReportDistributionyghTj1 = echarts.init(document.getElementById('TJYghLx1'),'shine');
		var fromYghGroupServiceWithNum = data;
		var preData1 = new Array();
		var total7 = 0;
		for(var i = 0; i < fromYghGroupServiceWithNum.length; i++){
			var item = {};
			var dt = fromYghGroupServiceWithNum[i];
			if(dt.NAME !='' && dt.NAME != undefined){
				item.name=dt.NAME;
			}else{
				item.name="未知";
			}
			total7 += dt.VALUE;
			item.value = dt.VALUE;
			preData1.push(item);
		}
		$scope.yghTj1 ={
				title : {
    		        subtext: '总数:'+ total7 +'个',
    		        x:'left'
    		    },
		    tooltip : {
		        trigger: 'item',
		        formatter: "{b} : {c} ({d}%)"
		    },
		    
		    series : [
		              {
		            	  name:'数量',
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
			                         formatter: '{b} : {c} (个)' 
			                      }, 
			                      labelLine :{show:true}
			                  } 
		                  }
		              }
		          ]
        };
        $scope.ReportDistributionyghTj1.setOption($scope.yghTj1);
        
                   
    };
    
	//规模
	$scope.initEchartsPieYGHDataTj2 = function(){
		$http({
			method: 'post',
			url: srvUrl + "deptwork/getPreFromYghGroupServiceWithGm.do"
		}).success(function(result){
			var data = result.result_data;
			$scope.drawYGHDataTj2(data);
		});
	};
    $scope.drawYGHDataTj2 = function(data){
    	//规模
    	$scope.fromYghGroupAreaReportsTj2 = echarts.init(document.getElementById('TJYghLx2'),'shine');
        var fromYghGroupServiceWithGm = data;
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
		$scope.fromYghGroupAreaReportsTj2.setOption($scope.yghTJ2);
    };
    
    //完成情况
    /*$scope.initEChartsPieWC = function(){
		$('a[data-toggle="tab"]').on('shown.wc.tab', function (e) {
			// 获取已激活的标签页的名称
			var activeTab = $(e.target)[0].hash;
			if(activeTab=="#wc-tabdrop-tab1") {
				myChart = echarts.init(document.getElementById('reviewWC1'),'shine');
				myChart.setOption($scope.wc1);
			}
			if(activeTab=="#wc-tabdrop-tab2"){
				myChart2 = echarts.init(document.getElementById('reviewWC2'),'shine');
				myChart2.setOption($scope.wc2);
			}
		});
		$scope.initEchartsPieWCData1();
		$scope.initEchartsPieWCData2();
	};
	
	//月
	$scope.initEchartsPieWCData1 = function(){
		$http({
			method: 'post',
			url: srvUrl + "deptwork/getMonthlyReports.do"
		}).success(function(result){
			$scope.drawWCData1(result);
		});
	};
	
	//季度
	$scope.initEchartsPieWCData2 = function(){
		$http({
			method: 'post',
			url: srvUrl + "deptwork/getFromWghGroupAreaReports.do"
		}).success(function(result){
			var data = result.result_data;
			$scope.drawWCData2(data);
		});
	};
	

    $scope.drawWCData1 = function(data){
    	//月
    	$scope.ReportDistribution1 = echarts.init(document.getElementById('reviewWC1'),'shine');
    	var formalAssessmentMonthlyReports = data.formalAssessment;
        var wc1 = {
    		title : {
		        subtext: '总数230',
		        x:'left'
		    },
            tooltip : {
                trigger: 'axis'
            },
            calculable : true,
            xAxis : [
                {
                	
                    type : 'category',
                    data : ['1','2','3','4','5','6','7','8','9','10','11','12'],
                    axisLabel :{  
                        interval:0   
                    }  
                }
            ],
            yAxis : [
                {
                    type : 'value'
                }
            ],
            series : [
                {
                    name:'正式评审',
                    type:'bar',
                    data:formalAssessmentMonthlyReports,
                    barWidth : 10,
                    itemStyle:{
                        normal:{
                            color:'#77B7C5',
                        }
                    }
                }
            ]
        };
        $scope.ReportDistribution1.setOption(wc1);
        
                   
    };
    $scope.drawWCData2 = function(data){
    	//季度
    	$scope.fromWghGroupAreaReports = echarts.init(document.getElementById('reviewWC2'),'shine');
        var data1401 = data.data1401;
		var preData1 = new Array();
		for(var i = 0; i < data1401.length; i++){
			var item = {};
			var dt = data1401[i];
			if(dt.NAME !='' && dt.NAME != undefined){
				item.name=dt.NAME;
			}else{
				item.name="未知";
			}
			item.value = dt.VALUE;
			preData1.push(item);
		}
		$scope.wc2 = {
    		 title : {
    			 text: '',
    			 x:'center'
    		},
    		tooltip : {
		        trigger: 'item',
		        formatter: "{a} <br/>{b} : {c} ({d}%)"
		    },
		    series : [
		              {
		                  type: 'pie',
		                  radius : '50%',
		                  center: ['30%', '60%'],
		                  data:preData1,
		                  itemStyle: {
		                      emphasis: {
		                          shadowBlur: 10,
		                          shadowOffsetX: 0,
		                          shadowColor: 'rgba(0, 0, 0, 0.5)'
		                      }
		                  }
		              }
		          ]
        };
		$scope.fromWghGroupAreaReports.setOption($scope.wc2);
    };
    */
    
    
    
    
    
    
    
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
			if(activeTab=="#lx-tabdrop-tab2") {
				myChart = echarts.init(document.getElementById('reviewLX2'),'shine');
				myChart.setOption($scope.WghLX2);
			}
		});
		$scope.initEchartsPieLXData1();
		$scope.initEchartsPieLXData2();
	};
	
	$scope.initEchartsPieLXData1 = function(){
		$http({
			method: 'post',
			url: srvUrl + "deptwork/getPreFromWghByServietypeAndAreaWithNum.do"
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
    
    //规模
    $scope.initEchartsPieLXData2 = function(){
		$http({
			method: 'post',
			url: srvUrl + "deptwork/getPreFromWghByServietypeAndAreaWithGm.do"
		}).success(function(result){
			$scope.drawLXData2(result.result_data);
		});
	};
	

    $scope.drawLXData2 = function(data){
    	$scope.ReportLX2 = echarts.init(document.getElementById('reviewLX2'),'shine');
    	var serviceTypeList = data.serviceTypeList;
    	var areaList = data.areaList;
    	var dataList = data.dataList;
    	$scope.WghLX2 = {
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
        $scope.ReportLX2.setOption($scope.WghLX2);
        
                   
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
			if(activeTab=="#yghlx-tabdrop-tab2") {
				myChart = echarts.init(document.getElementById('reviewYghLX2'),'shine');
				myChart.setOption($scope.YghLX2);
			}
		});
		$scope.initEchartsPieLXYghData1();
		$scope.initEchartsPieLXYghData2();
	};
	
	$scope.initEchartsPieLXYghData1 = function(){
		$http({
			method: 'post',
			url: srvUrl + "deptwork/getPreFromYghByServietypeAndAreaWithNum.do"
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
    
    
    $scope.initEchartsPieLXYghData2 = function(){
		$http({
			method: 'post',
			url: srvUrl + "deptwork/getPreFromYghByServietypeAndAreaWithGm.do"
		}).success(function(result){
			$scope.drawLXYghData2(result.result_data);
		});
	};
	

    $scope.drawLXYghData2 = function(data){
    	$scope.ReportyghLX2 = echarts.init(document.getElementById('reviewYghLX2'),'shine');
    	var serviceTypeList = data.serviceTypeList;
    	var areaList = data.areaList;
    	var dataList = data.dataList;
    	$scope.YghLX2 = {
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
        $scope.ReportyghLX2.setOption($scope.YghLX2);
        
                   
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
//    	else{  
//            $("#noCompleted").hide(); 
//            $("#completed").show(); 
//            //$("#liCompleted").attr("disabled", true); 
//        } 
    }
    $scope.qiehuanCompleted = function(){
    	if($("#completed").is(':hidden')){  
    		$("#noCompleted").hide(); 
            $("#completed").show(); 
        }
//    	else{  
//            $("#completed").hide(); 
//            $("#noCompleted").show(); 
//        } 
    }
    
    $scope.qiehuannoCompleted1 = function(){
    	if($("#noCompleted1").is(':hidden')){  
            $("#completed1").hide(); 
            $("#noCompleted1").show(); 
        }
//    	else{  
//            $("#noCompleted1").hide(); 
//            $("#completed1").show(); 
//        } 
    }
    $scope.qiehuanCompleted1 = function(){
    	if($("#completed1").is(':hidden')){  
    		$("#noCompleted1").hide(); 
            $("#completed1").show(); 
        }
//    	else{  
//            $("#completed1").hide(); 
//            $("#noCompleted1").show(); 
//        } 
    }
    $scope.qiehuanOne = function(){
    	if($("#one").is(':hidden')){  
    		$("#one").hide(); 
        }
//    	else{  
//            $("#one").show(); 
//        } 
    }
    $scope.hideDiv = function(){
    	 $("#completed").hide(); 
    	 $("#completed1").hide(); 
    }
   
    $scope.initEChartsPiePK();
    $scope.initEChartsPieYgh();
    
    $scope.initEChartsPieYghService();
    /*$scope.initEChartsPieWC();*/
    $scope.initEChartsPieTJ();
    $scope.initEChartsPieLX();
    $scope.initEChartsPieLXYgh();
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
}]);

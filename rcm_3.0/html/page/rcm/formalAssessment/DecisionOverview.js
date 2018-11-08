ctmApp.register.controller('DecisionOverview', ['$http','$scope','$location','$routeParams', function ($http,$scope,$location,$routeParams) {
	
	$scope.queryPertainAreaAchievement = function(){
		$http({
			method:'post',  
		    url:srvUrl+"reportInfo/queryPertainAreaAchievement.do"
		}).success(function(data){
			
			for(var i in data.result_data){
				if(data.result_data[i].PERTAINAREAID == "0001N61000000005ZBVA"){
					data.result_data[i].AREANAME = "城市服务";
				}
			}
			
			$scope.pertainAreaAchievementList = data.result_data;
		});
	}
	
	$scope.rounding = function keepTwoDecimalFull(num) {
		  var result = parseFloat(num);
		  if (isNaN(result)) {
		    alert('传递参数错误，请检查！');
		    return false;
		  }
		  result = Math.round(num * 100) / 100;
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
	
	$scope.initData = function(){
		$scope.queryPertainAreaAchievement();
		$scope.initEChartsPie();
		$scope.countTodayLaterDecisionReview();
		$scope.countHistoryDecision();
	}
	$scope.initEChartsPie = function(){
		
		$('a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
			// 获取已激活的标签页的名称
			var activeTab = $(e.target)[0].hash;
			if(activeTab=="#bs-tabdrop-tab1") {
				myChart = echarts.init(document.getElementById('review'),'shine');
				myChart.setOption($scope.pieAmountOption);
			}
			if(activeTab=="#bs-tabdrop-tab2"){
				myChart2 = echarts.init(document.getElementById('review2'),'shine');
				myChart2.setOption($scope.pieProjectSizeOption);
			}
		});
		
		$scope.initEchartsPieData();
		
	};

	//获取投资金额及投资规模的数据
	$scope.initEchartsPieData = function(){
		$http({
			method: 'post',
			url: srvUrl + "decisionedReport/queryInvestmentAmountAndScale.do"
		}).success(function(result){
			var data = result.result_data;
			$scope.drawPieAmount(data);
			$scope.drawPieProjectSize(data);
		});
	};
	
	//绘制投资金额饼图
	$scope.drawPieAmount = function(data){
		$scope.pieAmount =  echarts.init(document.getElementById('review'),'shine');
		
		var pieAmountData = new Array();
		for(var i = 0; i < data.length; i++){
			var item = {};
			var dt = data[i];
			if(dt.AMOUNT == 0){
				continue;
			}
			if(dt.PERTAINAREAID =='0001N61000000005ZBVA'){
				item.name="城市服务";
			}else{
				item.name=dt.AREANAME;
			}
			item.value = $scope.rounding(dt.AMOUNT/10000);
			pieAmountData.push(item);
		}
		
		$scope.pieAmountOption = {
    		    title : {
    		        text: '投资金额',
    		        subtext: '',
    		        x:'center'
    		    },
    		    tooltip : {
    		        trigger: 'item',
    		        formatter: "{b} : {d}%"
    		    },
    		    series : [
    		        {
    		            type: 'pie',
    		            radius : '64%',
    		            center: ['50%', '60%'],
    		            data:pieAmountData,
    		            itemStyle: {
    		                emphasis: {
    		                    shadowBlur: 10,
    		                    shadowOffsetX: 0,
    		                    shadowColor: 'rgba(0, 0, 0, 0.5)'
    		                },
    		                normal:{
    		                    label:{
    		                      show: true,
    		                      formatter: '{b} : {c}亿元'
    		                    },
    		                    labelLine :{show:true}
    		                 }
    		            }
    		        }
    		    ]
    		};
    	$scope.pieAmount.setOption($scope.pieAmountOption);
	};
	
	//绘制投资规模饼图
	$scope.drawPieProjectSize = function(data){
		$scope.pieProjectSize =  echarts.init(document.getElementById('review2'),'shine');
		
		var projectSizeData = new Array();
		for(var i = 0; i < data.length; i++){
			var item = {};
			var dt = data[i];
			if(dt.PROJECTSIZE == 0){
				continue;
			}
			if(dt.PERTAINAREAID =='0001N61000000005ZBVA'){
				item.name="城市服务";
			}else{
				item.name=dt.AREANAME;
			}
			item.value = $scope.rounding(dt.PROJECTSIZE);
			projectSizeData.push(item);
		}
		
		$scope.pieProjectSizeOption = {
    		    title : {
    		        text: '投资规模',
    		        subtext: '',
    		        x:'center'
    		    },
    		    tooltip : {
    		        trigger: 'item',
    		        formatter: "{b} : {d}%"
    		    },
    		    series : [
    		        {
    		            type: 'pie',
    		            radius : '62%',
    		            center: ['50%', '60%'],
    		            data:projectSizeData,
    		            itemStyle: {
    		                emphasis: {
    		                    shadowBlur: 10,
    		                    shadowOffsetX: 0,
    		                    shadowColor: 'rgba(0, 0, 0, 0.5)'
    		                },
    		                normal:{
    		                    label:{
    		                      show: true,
    		                      formatter: '{b} : {c}万吨'
    		                    },
    		                    labelLine :{show:true}
    		                 }
    		            }
    		        }
    		    ]
    		};
    	$scope.pieProjectSize.setOption($scope.pieProjectSizeOption);
	};
	
	$scope.countTodayLaterDecisionReview = function(){
		$http({
			method:'post',  
		    url:srvUrl+"decisionReview/countTodayLater.do"
		}).success(function(data){
			$scope.todayLaterDecisionReviewNumber = data.result_data;
		});
	}
	
	$scope.countHistoryDecision = function(){
		$http({
			method:'post',  
		    url:srvUrl+"decisionReview/countHistoryDecision.do"
		}).success(function(data){
			$scope.historyDecisionNumber = data.result_data;
		});
	}
	$scope.initData();
}]);
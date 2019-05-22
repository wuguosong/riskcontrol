/********
 * Created by wangjian on 16/4/11.
 * 用户管理控制器
 *********/
ctmApp.register.controller('homePage', ['$http','$scope','$location', function ($http,$scope,$location) {
	var pieContain = document.getElementById('pieContain');
	$scope.pieWidth = pieContain.clientWidth;
	$("#review1").css("width",$scope.pieWidth);
	$("#review2").css("width",$scope.pieWidth);
	$("#review3").css("width",$scope.pieWidth);
	$scope.ishow1 ='false';
	$scope.ishow2 ='false';
	$scope.ishow3 ='false';
	$scope.ishow4 ='false';
	$scope.ishow5 ='false';
	$scope.ishow6 ='false';
	$("span[name='preServiceType']").addClass("label-danger");
	$("span[name='formalServiceType']").addClass("label-danger");
    var uuid=$scope.credentials.UUID;
    $scope.import = function(){
        var url = 'formalAssessment/ProjectFormalReview/importFormalAssessmentReport';
        $scope.httpData(url,$scope.credentials.UUID).success(function (data) {
            if (data.result_code=="S") {
                var files=data.result_data.filePath;
                var fileName=data.result_data.fileName;
                window.location.href = srvUrl+"common/RcmFile/downLoad?filePath="+encodeURI(files)+"&fileName="+encodeURI(fileName);;

            } else {
            	$.alert("生成失败");
            }
        });
    }
    //查义所有的操作
    $scope.ListAll=function(){
        $scope.conf={currentPage:$scope.paginationConf.currentPage,pageSize:$scope.paginationConf.itemsPerPage,queryObj:{reader:$scope.credentials.UUID}}
        var aMethed = 'projectPreReview/ListNotice/listUnreadNotice';
        $scope.httpData(aMethed,$scope.conf).success(
            function (data, status, headers, config) {
                if(data.result_code == "S") {
                    $scope.unreadnotice = data.result_data.list;
                    $scope.unreadnoticeCount = data.result_data.totalItems;
                }
            }
        ).error(function (data, status, headers, config) {
            alert(status);
        });
    };
    
    $scope.initializeCount=function(){
    	 $http({
 			method:'post',  
 		    url: srvUrl + "deptwork/initializeCount.do"
 		}).success(function(result){
 			$scope.formalApplyingCount = result.result_data.formalApplyingCount;
 			$scope.preApplyingCount = result.result_data.preApplyingCount;
 			$scope.bulletinApplyingCount = result.result_data.bulletinApplyingCount;
 			$scope.formalEndCount = result.result_data.formalEndCount;
 			$scope.preEndCount = result.result_data.preEndCount;
 			$scope.bulletinEndCount = result.result_data.bulletinEndCount;
 			$scope.ypsCount = result.result_data.ypsCount;
 			$scope.zspsCount = result.result_data.zspsCount;
 			$scope.qtjcCount = result.result_data.qtjcCount;
 			 
 			$scope.ypswghCount = result.result_data.ypswghCount;
			$scope.ypsyghCount = result.result_data.ypsyghCount;
			 
			$scope.zsswghCount = result.result_data.zsswghCount;
			$scope.zssyghCount = result.result_data.zssyghCount;
			 
 			$scope.qtjcwghCount = result.result_data.qtjcwghCount;
 			$scope.qtjcyghCount = result.result_data.qtjcyghCount;
 			 
 			$scope.tzjcCount = result.result_data.tzjcCount;
			$scope.tzjcWxshCount = result.result_data.tzjcWxshCount;
			$scope.tzjcXshCount = result.result_data.tzjcXshCount;
 		});
    	 
    };
    $scope.getProjectReport=function(){
        $http({
    		method:'post',  
    	    url: srvUrl + "deptwork/getProjectReport.do"
    	}).success(function(result){
    		 $scope.projectReport0922ByYw = result.result_data.getProjectReport0922ByYw;
    		 $scope.projectReport0922ByFL = result.result_data.getProjectReport0922ByFL;
    		 $scope.queryPsfzrUsers = result.result_data.queryPsfzrUsers;
    		 $scope.queryFlfzrUsers = result.result_data.queryFlfzrUsers;
    		 
    	});
    };
    
    $scope.listUnreadWarning=function(){
        $scope.conf={currentPage:$scope.paginationConf.currentPage,pageSize:$scope.paginationConf.itemsPerPage,queryObj:{reader:$scope.credentials.UUID}}
        var aMethed = 'projectPreReview/ListNotice/listUnreadWarning';
        $scope.httpData(aMethed,$scope.conf).success(
            function (data, status, headers, config) {
                if(data.result_code == "S") {
                    $scope.unreadwarning = data.result_data.list;
                    $scope.unreadwarningCount = data.result_data.totalItems;
                }
            }
        ).error(function (data, status, headers, config) {
            alert(status);
        });
    };
    $scope.listAllMyTask = function(){
        $scope.paginationConf.queryObj = {assignee:$scope.credentials.UUID};
        $scope.httpData('bpm/WorkFlow/getMyTask',$scope.paginationConf).success(function(data){
            if(data.result_code == "S"){
                $scope.tasks = data.result_data.list;
                $scope.MyTask = data.result_data.totalItems;
                $scope.paginationConf.totalItems = data.result_data.totalItems;
            }
        });
    }
    // 配置分页基本参数
    $scope.paginationConf = {
        currentPage: 1,
		// 修改已办代办默认每页展示数
        itemsPerPage: 10,
        perPageOptions: [5, 10]
    };
    //初始化
    angular.element(document).ready(function() {
    	$scope.initializeCount();
        $scope.getProjectReport();
    });

    var url_contractTable = 'ws.client.contract/ReadSysUtil/getMonthlyStatistics';
//   
    $scope.paginationConfes.itemsPerPage = 6;
    $scope.showOthers = function(){
    	$scope.paginationConfes.totalItems = $scope.others1.length;
    	$scope.others = angular.copy($scope.others1);
    	if($scope.others.length > 6){
    		$scope.others = $scope.others.splice(($scope.paginationConfes.currentPage-1)*6, $scope.paginationConfes.itemsPerPage)
    	}
    }
    $scope.httpData(url_contractTable).success(function (data) {
        if (data.result_code=="S") {
            $scope.investment = data.result_data.investment;
            $scope.others = data.result_data.others;
			$scope.$watch('paginationConfes.currentPage + paginationConfes.itemsPerPage', $scope.showOthers);
        }
    });
	$scope.initEChartsPie = function(){
		$('a[data-toggle="tab"]').on('shown.ff.tab', function (e) {
			// 获取已激活的标签页的名称
			var activeTab = $(e.target)[0].hash;
			if(activeTab=="#ff-tabdrop-tab1") {
			}
			if(activeTab=="#ff-tabdrop-tab1"){
			}
			if(activeTab=="#ff-tabdrop-tab3"){
			}
		});
		$scope.initEchartsPieWGHData3();
		
	};
	
	//获取跟进中其他评审数据
	$scope.initEchartsPieWGHData3 = function(){
		$http({
			method: 'post',
			url: srvUrl + "deptwork/getBulletinTypeReport.do",
		    data:$.param({"year":$scope.review3Year})
		}).success(function(result){
			var data = result.result_data.list;
			$scope.review3Year = ''+result.result_data.year;
			$scope.total3 = 0;
	    	//正式评审饼图
	    	//跟进中
	        var data1401 = data;
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
				$scope.total3 +=dt.VALUE;
				preData1.push(item);
			}
			$scope.fromWghGroupArea = {
	    		 title : {
	    			 text: '业务类型',
	    			 x:'center'
	    		},
	    		tooltip : {
			        trigger: 'item',
			        formatter: "{a} <br/>{b} : {c} ({d}%)"
			    },
			    series : [
			              {
			                  name: '业务类型(数量)',
			                  type: 'pie',
			                  radius : '60%',
//			                  center: ['50%', '50%'],
			                  data:preData1,
			                  itemStyle: {
			                      emphasis: {
			                          shadowBlur: 10,
			                          shadowOffsetX: 0,
			                          shadowColor: 'rgba(0, 0, 0, 0.5)'
			                      },normal:{ 
				                      label:{ 
					                         show: true, 
					                         formatter: '{b} : {c}(个)' 
					                      }, 
					                      labelLine :{show:true}
					              } 
			                  }
			              }
			          ]
	        };
			$scope.bulletinTypePie = echarts.init(document.getElementById('review3'),'shine');
			
			$scope.bulletinTypePie.setOption($scope.fromWghGroupArea);
		});
	};

    
    //已决策的投资图
    $scope.initEChartsPieTZ = function(){
		$('a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
			// 获取已激活的标签页的名称
			var activeTab = $(e.target)[0].hash;
			if(activeTab=="#tz-tabdrop-tab1") {
				myChart = echarts.init(document.getElementById('reviewTZJE'),'shine');
				myChart.setOption($scope.pieAmountOption);
			}
			if(activeTab=="#tz-tabdrop-tab2"){
				myChart2 = echarts.init(document.getElementById('reviewTZGM'),'shine');
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
		$scope.pieAmount =  echarts.init(document.getElementById('reviewTZJE'),'shine');
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
		$scope.pieProjectSize =  echarts.init(document.getElementById('reviewTZGM'),'shine');
		
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
    
    $scope.queryFZR = function($event,uuid) {
    	$http({
			method: 'post',
			url: srvUrl + "deptwork/showFzr.do",
			data: $.param({"uuid":uuid})
		}).success(function(result){
			var data = result.result_data.data;
			var html  ;
			var inderHtml ;
			//张三</a></td><td colspan="2" align="left"><span >113</span></td><td colspan="2" align="left"><span>313</span></td><td colspan="2" align="left"><span>333</span></td></tr><tr id="'+uuid+'ls"><td  align="left"><a href="">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;栗色</a></td><td colspan="2" align="left"><span >113</span></td><td colspan="2" align="left"><span >22</span></td><td colspan="2" align="left"><span>313</span></td></tr>';
			for (var i = 0; i < data.length; i++) {
				var name = data[i].TEAM_MEMBER_NAME;
				var wgh = data[i].FORMAL_GOING + data[i].PRE_GOING + data[i].BULLETIN_GOING;
				var ywc = data[i].FORMAL_DEALED + data[i].PRE_DEALED+ data[i].BULLETIN_DEALED;
				var total = data[i].TOTAL_NUM;
				inderHtml = '<tr name="'+uuid+'zs">' + '<td align="left">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#/PersonnelWork/user/'+data[i].USERID+'/YW/0">' + name + '</a></td>'
						+ '<td colspan="2" align="left"><span >'+ wgh +'</span></td>'
						+ '<td colspan="2" align="left"><span>' + ywc + '</span></td>' 
						+ '<td colspan="2" align="left"><span>' + total + '</span></td></tr>' ;
				html = html + inderHtml;
			}
			$('#'+uuid).after(html);
	    	$($event.target).hide();
	    	$('#'+uuid+'sq').show();
		});
    	
    	
    	
    };
    
    $scope.queryFlFZR = function($event,uuid) {
    	$http({
			method: 'post',
			url: srvUrl + "deptwork/showFlFzr.do",
			data: $.param({"uuid":uuid})
		}).success(function(result){
			var data = result.result_data.data;
			var html  ;
			var inderHtml ;
			//张三</a></td><td colspan="2" align="left"><span >113</span></td><td colspan="2" align="left"><span>313</span></td><td colspan="2" align="left"><span>333</span></td></tr><tr id="'+uuid+'ls"><td  align="left"><a href="">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;栗色</a></td><td colspan="2" align="left"><span >113</span></td><td colspan="2" align="left"><span >22</span></td><td colspan="2" align="left"><span>313</span></td></tr>';
			for (var i = 0; i < data.length; i++) {
				var name = data[i].TEAM_MEMBER_NAME;
				var wgh = data[i].FORMAL_GOING + data[i].BULLETIN_GOING;
				var ywc = data[i].FORMAL_DEALED + data[i].BULLETIN_DEALED;
				var total = data[i].TOTAL_NUM;
				inderHtml = '<tr name="'+uuid+'zs">' + '<td align="left"><a href="#/PersonnelWork/user/'+data[i].USERID+'/FL/0">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;' + name + '</a></td>'
						+ '<td colspan="2" align="left"><span >'+ wgh +'</span></td>'
						+ '<td colspan="2" align="left"><span>' + ywc + '</span></td>' 
						+ '<td colspan="2" align="left"><span>' + total + '</span></td></tr>' ;
				html = html + inderHtml;
			}
			$('#'+uuid).after(html);
	    	$($event.target).hide();
	    	$('#'+uuid+'sq').show();
		});
    	
    	
    	
    };
    
    $scope.sqFZR = function($event,uuid) {
    	//$('#'+uuid+'zs').remove();
    	$("tr[name='"+uuid+"zs']").remove();
    	//$('#'+uuid+'ls').remove();
    	$($event.target).hide();
    	$('#'+uuid+'zk').show();
    };
    
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
	$scope.queryPreForPie = function (){
		var wf_state = "1,2";
		var stage = "3,3.5,3.7,3.9,4,5";
		var serviceTypeId = "";
		$("span[name='preServiceType'].label-danger").each(function(){
			serviceTypeId += ","+$(this).context.attributes.value.value;
		});
		serviceTypeId = serviceTypeId.substring(1);
		if(serviceTypeId == null || serviceTypeId == ''){
			$.alert("请选择一个业务类型！");
			return;
		}
		$http({
			method:'post',  
		    url: srvUrl + "deptwork/queryPreCount.do",
		    data:$.param({"wf_state":wf_state,
		    	"stage":stage,
		    	"serviceTypeId":serviceTypeId,
		    	"year":$scope.review1Year
		    	})
		}).success(function(result){
			var FormalcountforPie = result.result_data.list;
			$scope.review1Year  = ''+result.result_data.year;
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
			var myChart = document.getElementById('review1');
			var myChart = echarts.init(myChart);
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
//	                  center: ['40%', '50%'],
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
			// 使用刚指定的配置项和数据显示图表。
			myChart.setOption($scope.pk1);
//			//浏览器大小改变时重置大小
			 window.onresize = function () {
				    myChart.resize();
			};
		});
	}
	$scope.queryFormalForPie = function (){
		var wf_state = "1,2";
		var stage = "3,3.5,3.7,3.9,4,5";
		var serviceTypeId = "";
		$("span[name='formalServiceType'].label-danger").each(function(){
			serviceTypeId += ","+$(this).context.attributes.value.value;
		});
		serviceTypeId = serviceTypeId.substring(1);
		//验证大区
		if(serviceTypeId == null || serviceTypeId == ''){
			$.alert("请选择一个业务类型！");
			return;
		}
		$http({
			method:'post',  
		    url: srvUrl + "deptwork/queryFormalCount.do",
		    data:$.param({"wf_state":wf_state,
		    	"stage":stage,
		    	"serviceTypeId":serviceTypeId,
		    	"year":$scope.review2Year
		    	})
		}).success(function(result){
			var FormalcountforPie = result.result_data.list;
			$scope.review2Year = result.result_data.year+"";
		
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
			$scope.total2 = total1;
			var myChart = document.getElementById('review2');
			var myChart = echarts.init(myChart);
			
			//模板
			$scope.pk2 = {
			    tooltip : {
			        trigger: 'item',
			        formatter: "{a} <br/>{b} : {c} ({d}%)"
			    },
			    series : [
	              {
	            	  name:'数量',
	                  type: 'pie',
	                  radius : '60%',
//	                  center: ['40%', '50%'],
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
			// 使用刚指定的配置项和数据显示图表。
			myChart.setOption($scope.pk2);
			// 使用刚指定的配置项和数据显示图表。
			 window.onresize = function () {
				    myChart.resize();
			};
		});
	}
	$scope.formalServiceTypeClick = function(event){
		if($(event.target).hasClass("label-danger")){
			$(event.target).removeClass("label-danger");  
		}else{
			$(event.target).addClass("label-danger");  
		}
		$scope.queryFormalForPie();
	}
	$scope.preServiceTypeClick = function(event){
		if($(event.target).hasClass("label-danger")){
			$(event.target).removeClass("label-danger");  
		}else{
			$(event.target).addClass("label-danger");  
		}
		$scope.queryPreForPie();
	}
	$scope.queryPreForPie();
	$scope.queryFormalForPie();
    $scope.initEChartsPie();
    $scope.initEChartsPieTZ();
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
}]);
ctmApp.register.controller('toReadRedirect', ['$http','$scope','$location','$routeParams', function ($http,$scope,$location,$routeParams) {
    var nid = $routeParams.nid, redirectUrl = $routeParams.redirectUrl.replace(/!/g,'/');
    //将通知的状态改为已读
    $scope.httpData('rcm/NoticeInfo/update',{id:nid,state:'2'});
    $location.path(redirectUrl);
}]);
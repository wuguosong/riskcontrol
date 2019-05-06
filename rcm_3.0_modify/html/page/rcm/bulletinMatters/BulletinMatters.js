/********
 * Created by wufucan on 17/3/11.
 * 通报项目
 *********/
ctmApp.register.controller('BulletinMatters', ['$http','$scope','$location','$routeParams',
function ($http,$scope,$location,$routeParams) {
	$scope.tabIndex = $routeParams.tabIndex;
	$scope.initDefaultData = function(){
		var url = srvUrl + "bulletinInfo/queryListDefaultInfo.do";
		$http({
			method:'post',  
		    url: url
		}).success(function(result){
			var data = result.result_data;
			$scope.tbsxType = data.tbsxType;
		});
	};
	$scope.initDefaultData();
	//查询起草状态列表
	$scope.queryApplyList=function(){
		$http({
			method:'post',  
		    url:srvUrl+"bulletinInfo/queryApplyList.do", 
		    data: $.param({"page":JSON.stringify($scope.paginationConf)})
		}).success(function(data){
			if(data.success){
				$scope.bulletins = data.result_data.list;
                $scope.paginationConf.totalItems = data.result_data.totalItems;
			}else{
				$.alert(data.result_name);
			}
		});
    };
    //查询已提交列表
    $scope.queryApplyedList=function(){
    	show_Mask();
    	$http({
			method:'post',  
		    url:srvUrl+"bulletinInfo/queryApplyedList.do", 
		    data: $.param({"page":JSON.stringify($scope.paginationConfes)})
		}).success(function(data){
			if(data.success){
				$scope.applyedBulletins = data.result_data.list;
                $scope.paginationConfes.totalItems = data.result_data.totalItems;
			}else{
				$.alert(data.result_name);
			}
			hide_Mask();
		}).error(function(data, status, headers, config){
			hide_Mask();
		});
    };
    $scope.$watch('paginationConf.currentPage + paginationConf.itemsPerPage',  $scope.queryApplyList);
    $scope.$watch('paginationConfes.currentPage + paginationConfes.itemsPerPage',  $scope.queryApplyedList);
    
    $scope.update = function(){
    	var chkObjs = $("input[type=checkbox][name=applyChk]:checked");
    	if(chkObjs.length == 0 || chkObjs.length > 1){
    		$.alert("请选择一条数据编辑！");
    		return false;
    	}
    	var businessId = $(chkObjs[0]).val();
        $location.path("/BulletinMattersDetail/"+businessId);
    };
    
    $scope.deleteBatch = function(){
    	var chkObjs = $("input[type=checkbox][name=applyChk]:checked");
    	if(chkObjs.length == 0){
    		$.alert("请选择要删除的数据！");
    		return false;
    	}
    	$.confirm("删除后不可恢复，确认删除吗？", function() {
    		var idsStr = "";
        	for(var i = 0; i < chkObjs.length; i++){
        		idsStr = idsStr + chkObjs[i].value + ",";
        	}
        	idsStr = idsStr.substring(0, idsStr.length - 1);
        	$http({
    			method:'post',  
    		    url:srvUrl+"bulletinInfo/deleteByIds.do", 
    		    data: $.param({"ids": idsStr})
    		}).success(function(data){
    			if(data.success){
    				$scope.queryApplyList();
    			}
    			$.alert(data.result_name);
    		});
    	});
    }
    
    $scope.startBatchFlow = function(){
    	var chkObjs = $("input[type=checkbox][name=applyChk]:checked");
    	if(chkObjs.length == 0){
    		$.alert("请选择要提交的数据！");
    		return false;
    	}
    	var idsStr = "";
    	for(var i = 0; i < chkObjs.length; i++){
    		idsStr = idsStr + chkObjs[i].value + ",";
    	}
    	idsStr = idsStr.substring(0, idsStr.length - 1);
    	show_Mask();
    	$http({
			method:'post',  
		    url:srvUrl+"bulletinAudit/startBatchFlow.do", 
		    data: $.param({"ids": idsStr})
		}).success(function(data){
			hide_Mask();
			if(data.success){
				$scope.queryApplyList();
				$scope.queryApplyedList();
			}
			$.alert(data.result_name);
		});
    }
	
}]);

ctmApp.register.controller('BulletinMattersDetail', ['$http','$scope','$location', '$routeParams', '$filter','Upload',
 function ($http,$scope,$location, $routeParams, $filter, Upload) {
	
	$scope.mappedKeyValue = {
			"nameField" : "ORGNAME",
			"valueField" : "ORGID"
	};
	$scope.columns = [{
		"fieldName" : "单位名称" ,
		"fieldValue" : "ORGNAME"
	},{
		"fieldName" : "负责人" ,
		"fieldValue" : "FZRNAME"
	}]
	$scope.otherFields = ["FZRNAME","FZRID"];
	$scope.selectOrgCallback = function(checkedOrg){
		$scope.checkedOrg = checkedOrg;
        $scope.setUnitPerson($scope.checkedOrg.FZRID, $scope.checkedOrg.FZRNAME);
	};
	
	$scope.checkedOrg={
	};
	
	$scope.changeFzrModel = function(){
		if($scope.bulletin == null){
			$scope.bulletin = {};
		}
		if($scope.bulletin.unitPerson == null){
			$scope.bulletin.unitPerson = {};
		}
		if($scope.bulletin.applyUnit == null){
			$scope.bulletin.applyUnit = {};
		}
		if($scope.checkedOrg.FZRID != null && $scope.checkedOrg.FZRID != ''){
			$scope.bulletin.unitPerson.NAME=$scope.checkedOrg.FZRNAME ;
			$scope.bulletin.unitPerson.VALUE=$scope.checkedOrg.FZRID ;
			$scope.bulletin.applyUnit.NAME = $scope.checkedOrg.ORGNAME;
			$scope.bulletin.applyUnit.VALUE = $scope.checkedOrg.ORGID;
			
		}else{
			$scope.bulletin.unitPerson.NAME='' ;
			$scope.bulletin.unitPerson.VALUE='' ;
			$scope.bulletin.applyUnit.NAME = '';
			$scope.bulletin.applyUnit.VALUE = '';
		}
		
	}
	$scope.$watch('checkedOrg',  $scope.changeFzrModel, true);
	$scope.queryParamId = $routeParams.id;
	$scope.bulletin = {
		createTime: $filter("date")(new Date(), "yyyy-MM-dd HH:mm:ss"), 
		applyUser: {VALUE: $scope.credentials.UUID,NAME: $scope.credentials.userName},
		businessPerson: {},
        WF_STATE: "0"
	};
	$scope.initDefaultData = function(){
		$scope.wfInfo = {processKey:'bulletin'};
		$scope.refreshImg = Math.random()+1;
		if("0" == $scope.queryParamId){
			$scope.initCreate();
		}else{
			$scope.initUpdate($scope.queryParamId);
		}
	};
	
	//保存当前人申报单位
	$scope.APPLYINGUNIT_NAME;
	$scope.APPLYINGUNIT_ID;
	//保存当前人申报单位负责人
	$scope.APPLYHEADER_NAME;
	$scope.APPLYHEADER_ID;
	
	
	//保存原来申报单位
	$scope.APPLYINGUNIT_NAME_OLD;
	$scope.APPLYINGUNIT_ID_OLD;
	//保存原来申报单位负责人
	$scope.APPLYHEADER_NAME_OLD;
	$scope.APPLYHEADER_ID_OLD;
	$scope.initCreate = function(){
		var url = srvUrl + "bulletinInfo/queryCreateDefaultInfo.do";
		$http({
			method:'post',  
		    url: url
		}).success(function(result){
			if(!result.success){
				$.alert(result.result_name);
			}else{
				var data = result.result_data;
				$scope.APPLYINGUNIT_NAME = data.REPORTINGUNIT_NAME,
				$scope.APPLYINGUNIT_ID =data.REPORTINGUNIT_ID,
				
				$scope.APPLYHEADER_NAME = data.COMPANYHEADER_NAME,
				$scope.APPLYHEADER_ID = data.COMPANYHEADER_ID,
				$scope.bulletin.applyUnit = {
					NAME: data.REPORTINGUNIT_NAME,
					VALUE: data.REPORTINGUNIT_ID
				};
				$scope.bulletin.unitPerson = {
					NAME: data.COMPANYHEADER_NAME,
					VALUE: data.COMPANYHEADER_ID
				};
				$scope.bulletin.applyUser = {
					NAME: data.APPLYUSER_NAME,
					VALUE: data.APPLYUSER_ID
				};
				$scope.tbsxType = data.tbsxType;
				$scope.businessUsers = data.businessUsers;
				console.log($scope.businessUsers);
				$scope.isNotCityService = true;
				$scope.isNotbusinessService = true;
			}
		});
	};

     //处理附件列表
     $scope.reduceAttachment = function(attachment, id){
         $scope.newAttachment = attach_list("bulletin", id, "BulletinMattersDetail").result_data;
         for(var i in attachment){
             var file = attachment[i];
             console.log(file);
             for (var j in $scope.newAttachment){
                 if (file.fileId == $scope.newAttachment[j].fileid){
                     $scope.newAttachment[j].fileName = file.oldFileName;
                     $scope.newAttachment[j].lastUpdateBy = file.lastUpdateBy;
                     $scope.newAttachment[j].lastUpdateData = file.lastUpdateData;
                     break;
                 }
             }

         }
     };

	$scope.initUpdate = function(id){
		var url = srvUrl + "bulletinInfo/queryUpdateDefaultInfo.do";
		$http({
			method:'post',  
		    url: url,
		    data: $.param({"businessId": id})
		}).success(function(result){
			var data = result.result_data;
			$scope.bulletin = data.bulletinMongo;
			   //匹配ng-model
           	for(var i =0 ; i<data.tbsxType.length;i++){
           		if($scope.bulletin.bulletinType.VALUE == data.tbsxType[i].UUID){
           			//控制右边和能否选地区
           			if(data.tbsxType[i].ITEM_CODE =='TBSX_BUSINESS_BORROWMONEY') {
                        $scope.isNotbusinessService = false;
                        $scope.isNotCityService = true;
                    }else if(data.tbsxType[i].ITEM_CODE =='TBSX_BUSINESS_SUBCOMPANYTZ'){
                        $scope.isNotCityService = false;
                        $scope.isNotbusinessService = true;
                    }else{
                        $scope.isNotCityService = true;
                        $scope.isNotbusinessService = true;
					}
           			$scope.tbsxTypeModel = JSON.stringify(data.tbsxType[i]);
           			break;
                }
			}
            // 处理附件
            $scope.reduceAttachment(data.bulletinMongo.attachmentList, id);
			$scope.checkedOrg = {
					"ORGNAME" : $scope.bulletin.applyUnit.NAME ,
					"ORGID" : $scope.bulletin.applyUnit.VALUE ,
					"FZRID" :$scope.bulletin.unitPerson.VALUE,
					"FZRNAME" :$scope.bulletin.unitPerson.NAME
			}
			$scope.tbsxType = data.tbsxType;
			$scope.businessUsers = data.businessUsers;
			//保存原来申报单位
			//$scope.APPLYINGUNIT_NAME_OLD;
			//$scope.APPLYINGUNIT_ID_OLD;
			//保存原来申报单位负责人
			//$scope.APPLYHEADER_NAME_OLD;
			//$scope.APPLYHEADER_ID_OLD;
			//申请人单位名称
			$scope.APPLYINGUNIT_NAME_OLD = data.REPORTINGUNIT_NAME;
			//申请人单位id
			$scope.APPLYINGUNIT_ID_OLD = data.REPORTINGUNIT_ID;
			//申请人单位负责人名称
			$scope.APPLYHEADER_NAME_OLD = data.COMPANYHEADER_NAME;
			$scope.APPLYHEADER_ID_OLD = data.APPLYUSER_ID;
			
			$scope.APPLYHEADER_NAME = $scope.bulletin.unitPerson.NAME;
			$scope.APPLYHEADER_ID = $scope.bulletin.unitPerson.VALUE;
		});
	};
	$scope.initDefaultData();
	// 修改事项类型事件
	$scope.changeTbsxType = function(){

        /*$scope.bulletinNameBuild();*/
		console.log($scope.tbsxTypeModel);
		var tbsxTypeModel = $scope.tbsxTypeModel;
		if(tbsxTypeModel == null){
			return;
		}
		if("0" == $scope.queryParamId){
			//新增
			if(tbsxTypeModel == ""){
				$scope.bulletin.bulletinType = "";
				
				$scope.bulletin.businessPerson.NAME = "";
				$scope.bulletin.businessPerson.VALUE = "";
			}else{
				var jsonObj = JSON.parse(tbsxTypeModel);
				$scope.bulletin.bulletinType = {NAME:jsonObj.ITEM_NAME, VALUE:jsonObj.UUID};
				$scope.bulletin.businessPerson = {};
				
				//如果是借款审批事项  申报单位变为可选，单位负责人先置空
				if(jsonObj.ITEM_CODE == 'TBSX_BUSINESS_BORROWMONEY'){
						$scope.isNotbusinessService = false;
						$scope.isNotCityService = true;
						for(var i = 0; i < $scope.businessUsers.length; i++){
							var user = $scope.businessUsers[i];
							if(user.CODE == jsonObj.ITEM_CODE){
								$scope.bulletin.businessPerson.NAME = user.USERNAME;
								$scope.bulletin.businessPerson.VALUE = user.USER_ID;
								break;
							}
						}
						$scope.bulletin.applyUnit.NAME = '';
						$scope.bulletin.applyUnit.VALUE = '';
						$scope.bulletin.unitPerson.NAME = $scope.APPLYHEADER_NAME;
						$scope.bulletin.unitPerson.VALUE = $scope.APPLYHEADER_ID;
						return;
					
				}else{
					// 将当前人的所属单位回显到页面
					$scope.bulletin.applyUnit.NAME = $scope.APPLYINGUNIT_NAME;
					$scope.bulletin.applyUnit.VALUE = $scope.APPLYINGUNIT_ID;
					// 将当前人的单位负责人回显到页面
					$scope.bulletin.unitPerson.NAME =$scope.APPLYHEADER_NAME;
					$scope.bulletin.unitPerson.VALUE =$scope.APPLYHEADER_ID;
				}
				if(jsonObj.ITEM_CODE == 'TBSX_BUSINESS_SUBCOMPANYTZ'){
					//如果是北控城服的，就不需要设置业务负责人了
					$scope.isNotCityService = false;
					$scope.isNotbusinessService = true;
					return;
				}
				
				$scope.isNotbusinessService = true;
				$scope.isNotCityService = true;
				for(var i = 0; i < $scope.businessUsers.length; i++){
					var user = $scope.businessUsers[i];
					if(user.CODE == jsonObj.ITEM_CODE){
						$scope.bulletin.businessPerson.NAME = user.USERNAME;
						$scope.bulletin.businessPerson.VALUE = user.USER_ID;
						break;
					}
				}
			}
		}else{
			//修改
			if(tbsxTypeModel == ""){
				$scope.bulletin.bulletinType = "";
				$scope.bulletin.businessPerson.NAME = "";
				$scope.bulletin.businessPerson.VALUE = "";
			}else{
				var jsonObj = JSON.parse(tbsxTypeModel);
				$scope.bulletin.bulletinType = {NAME:jsonObj.ITEM_NAME, VALUE:jsonObj.UUID};
				$scope.bulletin.businessPerson = {};
				//如果是借款审批事项  申报单位变为可选，单位负责人先置空
				if(jsonObj.ITEM_CODE == 'TBSX_BUSINESS_BORROWMONEY'){
					$scope.isNotbusinessService = false;
					$scope.isNotCityService = true;
					$scope.bulletin.unitPerson.NAME = $scope.APPLYHEADER_NAME;
					$scope.bulletin.unitPerson.VALUE = $scope.APPLYHEADER_ID;
					for(var i = 0; i < $scope.businessUsers.length; i++){
						var user = $scope.businessUsers[i];
						if(user.CODE == jsonObj.ITEM_CODE){
							$scope.bulletin.businessPerson.NAME = user.USERNAME;
							$scope.bulletin.businessPerson.VALUE = user.USER_ID;
							break;
						}
					}
					return;
				}
				$scope.bulletin.applyUnit.NAME =$scope.APPLYINGUNIT_NAME_OLD;
				$scope.bulletin.applyUnit.VALUE =$scope.APPLYINGUNIT_ID_OLD;
				$scope.bulletin.unitPerson.NAME = $scope.APPLYHEADER_NAME_OLD;
				$scope.bulletin.unitPerson.VALUE = $scope.APPLYHEADER_ID_OLD;
				
				if(jsonObj.ITEM_CODE == 'TBSX_BUSINESS_SUBCOMPANYTZ'){
					//如果是北控城服的，就不需要设置业务负责人了
					$scope.isNotCityService = false;
					$scope.isNotbusinessService = true;
					for(var i = 0; i < $scope.businessUsers.length; i++){
						var user = $scope.businessUsers[i];
						if(user.CODE == jsonObj.ITEM_CODE){
							$scope.bulletin.businessPerson.NAME = user.USERNAME;
							$scope.bulletin.businessPerson.VALUE = user.USER_ID;
							break;
						}
					}
					return;
				}
				
				$scope.isNotbusinessService = true;
				$scope.isNotCityService = true;
				for(var i = 0; i < $scope.businessUsers.length; i++){
					var user = $scope.businessUsers[i];
					if(user.CODE == jsonObj.ITEM_CODE){
						$scope.bulletin.businessPerson.NAME = user.USERNAME;
						$scope.bulletin.businessPerson.VALUE = user.USER_ID;
						break;
					}
				}
			}
		}
	};
	// 标准项目名称构建
	 $scope.bulletinNameBuild = function () {
         $scope.qtType = angular.copy(JSON.parse($scope.tbsxTypeModel));
	 	if ($scope.qtType != ''){
            $scope.couldStr = $scope.bulletin.bulletinName.indexOf("项目");
            if ($scope.qtType.ITEM_CODE == 'TBSX_BUSINESS_BORROWMONEY') {
                $scope.setDirectiveCompanyList($scope.bakProject);
            	if ($scope.couldStr > 0) {
                    $scope.bulletin.bulletinName = $scope.bulletin.bulletinName + '公司针对' + $scope.bulletin.bulletinName + '的借款事项';
				} else {
                    $scope.bulletin.bulletinName = $scope.bulletin.bulletinName + '项目公司针对' + $scope.bulletin.bulletinName + '项目的借款事项';
				}
            } else if ($scope.qtType.ITEM_CODE == 'TBSX_BUSINESS_NEWCOMPANY') {
                $scope.setDirectiveCompanyList($scope.bakProject);
            	$scope.bulletin.bulletinName = '新设' + $scope.bulletin.bulletinName + '公司申请';
            } else if ($scope.qtType.ITEM_CODE == 'TBSX_BUSINESS_OTHER'){
                $scope.setDirectiveCompanyList($scope.bakProject);
                $scope.bulletin.bulletinName = '关于' + $scope.bulletin.bulletinName + '事项的汇报/申请';
			}
        } else {
	 		$scope.setDirectiveCompanyList($scope.bakProject);
		}
     };


	//保存
	$scope.save = function(callBack){
		if(!$("#myForm").valid()) {
			$.alert("有必填项未填！");
			return false;
		}
		var url = srvUrl + "bulletinInfo/saveOrUpdate.do";
		$http({
			method:'post', 
			url: url,
			data: $.param({"json": JSON.stringify($scope.bulletin)})
		}).success(function (result) {
            if (result.success){
                $scope.bulletin._id = result.result_data;
                if (callBack && typeof callBack == 'function') {
                    callBack();
                }
                if(callBack == null){
                    $.alert(result.result_name);
                }
                $location.path("/BulletinMattersDetail/" + result.result_data);
            } else {
                $.alert(result.result_name);
            }
			/*if (data.success) {
				$scope.bulletin._id = data.result_data;
				if (callBack && typeof callBack == 'function') {
					callBack();
				}
			}
			if(callBack == null){
				$.alert(data.result_name);
			}*/
		});
	};
	
	
	//提交
	$scope.showSubmitModal = function(){
		
		$scope.save(function(){
			$scope.approve = {
				operateType: "submit",
				processKey: "bulletin",
				businessId: $scope.bulletin._id,
				callbackSuccess: function(result){
					$.alert(result.result_name);
					$('#submitModal').modal('hide');
					$("#subBtn").hide();
					var oldurl = window.btoa(encodeURIComponent(escape("#/BulletinMatters/1")))
					$location.path("/BulletinMattersAuditView/"+$scope.bulletin._id+"/"+oldurl);
					//#/BulletinMattersAuditView/9b8e9d76984a415dbe4a31dd3c5b1ad2/JTI1MjMlMkZCdWxsZXRpbk1hdHRlcnMlMkYx
				},
				callbackFail: function(result){
					$.alert(result.result_name);
				}
			};
			$('#submitModal').modal('show');
		});
	};
	/*//新增附件
	$scope.addFileList = function(){
        function addBlankRow(array){
            var blankRow = {
                file_content:'',
                selected: false
            };
            var size=array.length;
            array[size]=blankRow;
        }
        if(undefined==$scope.bulletin){
            $scope.bulletin={fileList:[]};

        }
        if(undefined==$scope.bulletin.fileList){
            $scope.bulletin.fileList=[];
        }
        addBlankRow($scope.bulletin.fileList);
    };
    $scope.commonDdelete = function(){
        var commentsObj = $scope.bulletin.fileList;
        if(commentsObj!=null){
            for(var i=0;i<commentsObj.length;i++){
                if(commentsObj[i].selected){
                    commentsObj.splice(i,1);
                    i--;
                }
            }
        }
    };*/
    /*$scope.errorAttach=[];
    $scope.upload = function (file,errorFile, idx) {
        if(errorFile && errorFile.length>0){
        	var errorMsg = fileErrorMsg(errorFile);
            $scope.errorAttach[idx]={msg:errorMsg};
        }else if(file){
        	
        	if(file.name){
    		 //检查压缩文件
    	    	var index = file.name.lastIndexOf('.');
    	    	var suffix  = file.name.substring(index+1);
    	    	if("rar" == suffix || "zip" == suffix || "7z" == suffix){
    	    	    $.alert("附件不能是压缩文件！");
    	    		return false;
    	    	}
        	}
        	
            var fileFolder = "bulletin/attachments/"+$filter("date")(new Date(), "yyyyMM");
            $scope.errorAttach[idx]={msg:''};
            Upload.upload({
                url:srvUrl+'common/RcmFile/upload',
                data: {file: file, folder:fileFolder}
            }).then(function (resp) {
                var retData = resp.data.result_data[0];
                $scope.bulletin.fileList[idx].files=retData;
            }, function (resp) {
                console.log('Error status: ' + resp.status);
            }, function (evt) {
                var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                $scope["progress"+idx]=progressPercentage == 100 ? "":progressPercentage+"%";
            });
        }
    };*/

     // 选择项目后，写入项目名称
     $scope.setDirectiveCompanyList=function(project){
     	debugger
         $scope.bulletin.projectNo = project.PROJECTCODE;  // 存储用编码
         $scope.bulletin.projectNoNew = project.PROJECTCODENEW; // 显示用编码
         $scope.bulletin.projectName = project.PROJECTNAME; // 项目名称
         /*$scope.bulletin.applyUnit = {KEY: project.ORGCODE, VALUE: project.ORGNAME};
         if(!isEmpty(project.ORGHEADERNAME) && !isEmpty(project.ORGHEADERID)){
             $scope.bulletin.unitPerson = {NAME:project.ORGHEADERNAME,VALUE:project.ORGHEADERID};
         }*/

         $("#projectName").val(name);
         $("label[for='projectName']").remove();
     }

     // 修改大区后，修改单位负责人的值
     $scope.setUnitPerson = function(id, name){
         $scope.bulletin.unitPerson.VALUE = id;
         $scope.bulletin.unitPerson.NAME = name;
     };
}]);

ctmApp.register.controller('BulletinMattersDetailView', ['$http','$scope','$location', '$routeParams', '$filter',
 function ($http,$scope,$location, $routeParams, $filter) {
	$scope.queryParamId = $routeParams.id;
	$scope.oldUrl = $routeParams.url;
	$scope.initDefaultData = function(){
		$scope.wfInfo = {processKey:'bulletin', "businessId":$scope.queryParamId};
		$scope.initUpdate($scope.queryParamId);
	};

     //处理附件列表
     $scope.reduceAttachment = function(attachment, id){
         $scope.newAttachment = attach_list("bulletin", id, "BulletinMattersDetail").result_data;
         for(var i in attachment){
             var file = attachment[i];
             console.log(file);
             for (var j in $scope.newAttachment){
                 if (file.fileId == $scope.newAttachment[j].fileid){
                     $scope.newAttachment[j].fileName = file.oldFileName;
                     $scope.newAttachment[j].lastUpdateBy = file.lastUpdateBy;
                     $scope.newAttachment[j].lastUpdateData = file.lastUpdateData;
                     break;
                 }
             }

         }
     };
	$scope.initUpdate = function(id){
		var url = srvUrl + "bulletinInfo/queryViewDefaultInfo.do";
		$http({
			method:'post',  
		    url: url,
		    data: $.param({"businessId":id})
		}).success(function(result){
			console.log('BulletinMattersDetailView')
			var data = result.result_data;
			$scope.bulletinOracle = data.bulletinOracle;
			$scope.bulletin = data.bulletinMongo;
			$scope.auditLogs = data.logs;

            // 处理附件
            $scope.reduceAttachment(data.bulletinMongo.attachmentList, id);

			$scope.initPage();
		});
	};
	$scope.initDefaultData();
	$scope.initPage = function(){
		if($scope.bulletinOracle.AUDITSTATUS=="1" || $scope.bulletinOracle.AUDITSTATUS=="2"){
			//流程已启动
			$("#submitBtn").hide();
			$scope.wfInfo.businessId = $scope.queryParamId;
			$scope.refreshImg = Math.random()+1;
		}else{
			//未启动流程
			$("#submitBtn").show();
		}
	}
	//提交
	$scope.showSubmitModal = function(){
		$scope.approve = {
			operateType: "submit",
			processKey: "bulletin",
			businessId: $scope.queryParamId,
			callbackSuccess: function(result){
				$.alert(result.result_name);
				$('#submitModal').modal('hide');
				$("#submitBtn").hide();
				$scope.initData();
			},
			callbackFail: function(result){
				$.alert(result.result_name);
			}
		};
		$('#submitModal').modal('show');
	};
	$scope.selectAll = function(){
		if($("#all").attr("checked")){
			$(":checkbox[name='choose']").attr("checked",1);
		}else{
			$(":checkbox[name='choose']").attr("checked",false);
		}
	}
}]);
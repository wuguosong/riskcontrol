<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()+ path + "/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<base href="<%=basePath%>">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>流程部署</title>
<script type="text/javascript" src="<%=basePath %>js/jquery-1.8.3.min.js"></script>
<script type="text/javascript">
	var urlPrefix = "<%=basePath%>";
	function deploy() {
		var url = urlPrefix+"bpmn/deploy.do";
		$.ajax({
			url: url,
			data: {"bpmnType": $("#processKey").val()},
			type: "post",
			dataType: "json",
			success: function(data){
				if(data.success == true){
					alert("流程发布成功！");
				}else{
					alert(data.result_name);
				}
			}
		});
	}
	function stopProcess() {
		var url = urlPrefix+"bpmn/stopProcess.do";
		$.ajax({
			url: url,
			data: {
				"bpmnType": $("#bpmnType").val(),
				"businessKey": $("#businessKey").val()
			},
			type: "post",
			dataType: "json",
			success: function(data){
				if(data.success == true){
					alert("执行成功！");
				}else{
					alert(data.result_name);
				}
			}
		});
	}
</script>
</head>
<body>
	<div style="width: 800px; margin:20 auto;padding:20px;border:1px solid gray;">
		<h2>发布流程：</h2>
		流程文件名称：<input type="text" id="processKey" name="processKey"
			style="width: 280px;"><br> <input type="button"
			value="发布" onclick="deploy()">
	</div>
	<div style="width: 800px; margin:20 auto;padding:20px;border:1px solid gray;">
		<h2>结束流程：</h2>
		流程类型：<input type="text" id="bpmnType" name="bpmnType"
			style="width: 280px;"><br>
		工单号：<input type="text" id="businessKey" name="businessKey"
			style="width: 280px;"><br>
			
			 <input type="button" value="结束流程" onclick="stopProcess()">
	</div>
</body>
</html>
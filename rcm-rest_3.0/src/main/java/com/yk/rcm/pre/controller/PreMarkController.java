package com.yk.rcm.pre.controller;

import com.yk.log.annotation.SysLog;
import com.yk.log.constant.LogConstant;
import com.yk.rcm.pre.service.IPreMarkService;
import common.Result;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@RequestMapping("/preMark")
public class PreMarkController {
	
	@Resource
	private IPreMarkService preMarkService;
	
	@RequestMapping("/queryMarks")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_PRE_AUDIT, operation = LogConstant.QUERY, description = "查询标记")
	public Result queryMarks(HttpServletRequest request){
		Result result = new Result();
		String businessId = request.getParameter("businessId");
		Map<String, Object> marks  = preMarkService.queryMarks(businessId);
		return result.setResult_data(marks);
	}
	
	@RequestMapping("/saveOrUpdate")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_PRE_AUDIT, operation = LogConstant.CREATE, description = "保存或者更新标记")
	public Result saveOrUpdate(HttpServletRequest request){
		String json = request.getParameter("json");
		String businessId = request.getParameter("businessId");
		preMarkService.saveOrUpdate(businessId,json);
		Result result = new Result();
		return result;
	}
	
}

package com.yk.rcm.formalAssessment.controller;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.yk.log.annotation.SysLog;
import com.yk.log.constant.LogConstant;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yk.rcm.formalAssessment.service.IFormalMarkService;
import common.Result;

@Controller
@RequestMapping("/formalMark")
public class FormalMarkController {
	
	@Resource
	private IFormalMarkService formalMarkService;
	
	@RequestMapping("/queryMarks")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_FORMAL_ASSESSMENT, operation = LogConstant.QUERY, description = "查询标记")
	public Result queryMarks(HttpServletRequest request){
		Result result = new Result();
		String businessId = request.getParameter("businessId");
		Map<String, Object> marks  = formalMarkService.queryMarks(businessId);
		return result.setResult_data(marks);
	}
	
	@RequestMapping("/saveOrUpdate")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_FORMAL_ASSESSMENT, operation = LogConstant.CREATE, description = "保存或者更新标记")
	public Result saveOrUpdate(HttpServletRequest request){
		Result result = new Result();
		String json = request.getParameter("json");
		String businessId = request.getParameter("businessId");
		formalMarkService.saveOrUpdate(businessId,json);
		return result;
	}
	
}

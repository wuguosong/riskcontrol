package com.yk.reportData.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import util.ThreadLocalUtil;

import com.yk.log.annotation.SysLog;
import com.yk.log.constant.LogConstant;
import com.yk.rcm.newFormalAssessment.service.IFormalAssessmentInfoCreateService;
import com.yk.reportData.service.IReportDataService;

import common.Constants;
import common.PageAssistant;
import common.Result;

/**
 * 同步报表数据
 * 
 * @author Sunny Qi
 */
@Controller
@RequestMapping("/reportData")
public class ReportDataController {

	@Resource
	private IReportDataService reportDataService;
	

	@RequestMapping("/saveOrUpdate")
	@ResponseBody
	public Result saveOrUpdate(HttpServletRequest request){
		Result result = new Result();
		String json = request.getParameter("json");
		try {
			this.reportDataService.saveOrUpdateReportData(json);
			result.setResult_code("S");
		} catch (Exception e) {
			result.setResult_name(e.getMessage());
			e.printStackTrace();
		}
		
		return result;
	}
}

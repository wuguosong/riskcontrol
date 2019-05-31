package com.yk.reportData.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yk.reportData.service.IReportDataService;

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
	public Result saveOrUpdate(HttpServletRequest request) {
		Result result = new Result();
		String json = request.getParameter("json");
		try {
			this.reportDataService.saveOrUpdateReportData(json);
			result.setResult_code("S");
		} catch (Exception e) {
			result.setResult_code("R");
			result.setResult_name("同步数据报表失败，请联系管理员！" + e.getMessage());
			e.printStackTrace();
		}

		return result;
	}
}

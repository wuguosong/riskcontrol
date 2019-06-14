package com.yk.historyData.controller;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yk.historyData.service.IHistoryDataService;
import com.yk.reportData.service.IReportDataService;

import common.PageAssistant;
import common.Result;
import util.ThreadLocalUtil;

/**
 * 整理历史数据
 * 
 * @author Sunny Qi
 */
@Controller
@RequestMapping("/historyData")
public class HistoryDataController {

	@Resource
	private IHistoryDataService historyDataService;

	@RequestMapping("/saveOrUpdate")
	@ResponseBody
	public Result saveOrUpdate(HttpServletRequest request) {
		Result result = new Result();
		String json = request.getParameter("json");
		try {
			this.historyDataService.saveOrUpdateHistoryData(json);
			result.setResult_code("S");
		} catch (Exception e) {
			result.setResult_code("R");
			result.setResult_name("保存失败，请联系管理员！" + e.getMessage());
			e.printStackTrace();
		}

		return result;
	}
	
	@RequestMapping("/getHistoryList")
	@ResponseBody
	public Result getHistoryList(HttpServletRequest request) {
		Result result = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		try {
			this.historyDataService.getHistoryList(page);
			result.setResult_data(page);
			result.setResult_code("S");
		} catch (Exception e) {
			result.setResult_code("R");
			result.setResult_name("获取历史数据失败，请联系管理员！" + e.getMessage());
			e.printStackTrace();
		}

		return result;
	}
	
	@RequestMapping("/getHistoryById")
	@ResponseBody
	public Result getHistoryById(HttpServletRequest request){
		Result result = new Result();
		String id = request.getParameter("id");
		try {
			Map<String, Object> historyInfo = this.historyDataService.getHistoryById(id);
			result.setResult_data(historyInfo);
			result.setResult_code("S");
		} catch (Exception e) {
			result.setResult_code("R");
			result.setResult_name("获取历史数据失败，请联系管理员！" + e.getMessage());
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 新增附件信息到Mongo
	 * @param request
	 * @return
	 */
	@RequestMapping("/addAttachmengInfoToMongo")
	@ResponseBody
	public Result addAttachmengInfoToMongo(HttpServletRequest request){
		Result result = new Result();
		String json = request.getParameter("json");
		this.historyDataService.addNewAttachment(json);
		
		return result;
	}
	
	/**
	 * 删除Mongo附件信息
	 * @param request
	 * @return
	 */
	@RequestMapping("/deleteAttachmengInfoInMongo")
	@ResponseBody
	public Result deleteAttachmengInfoInMongo(HttpServletRequest request){
		Result result = new Result();
		String json = request.getParameter("json");
		this.historyDataService.deleteAttachment(json);
		
		return result;
	}
}

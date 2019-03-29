package com.yk.rcm.meeting.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import util.Util;

import com.yk.rcm.meeting.service.IPreliminaryNoticeService;

import common.PageAssistant;
import common.Result;

/**
 * 拟上会通知
 * @author hubiao
 */
@Controller
@RequestMapping("/preliminaryNotice")
public class PreliminaryNoticeController {
	
	@Resource
	private IPreliminaryNoticeService preliminaryNoticeService;

	/**
	 * 拟上会通知列表
	 */
	@RequestMapping("/queryByPage")
	@ResponseBody
	public Result queryByPage(HttpServletRequest request){
		Result result = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		preliminaryNoticeService.queryByPage(page);
		result.setResult_data(page);
		return result;
	}
	
	/**
	 * 拟上会通知详情
	 * @param id
	 * @return
	 */
	@RequestMapping("/queryById")
	@ResponseBody
	public Result queryById(String id){
		Result result = new Result();
		Map<String, Object> resultData = preliminaryNoticeService.queryById(id);
		result.setResult_data(resultData);
		return result;
	}
	
	/**
	 * 查询   前几条 拟上会通知
	 * @return
	 */
	@RequestMapping("/queryTop")
	@ResponseBody
	public Result queryTop(){
		Result result = new Result();
		List<Map<String, Object>> resultData = preliminaryNoticeService.queryTop();
		result.setResult_data(resultData);
		return result;
	}
	
	/**
	 * 拟上会通知审阅列表
	 */
	@RequestMapping("/queryReviewByPage")
	@ResponseBody
	public Result queryReviewByPage(HttpServletRequest request){
		Result result = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		preliminaryNoticeService.queryReviewByPage(page);
		result.setResult_data(page);
		return result;
	}
	
	/**
	 * 拟上会通知详情
	 * @param id
	 * @return
	 */
	@RequestMapping("/queryReviewById")
	@ResponseBody
	public Result queryReviewById(String id){
		Result result = new Result();
		Map<String, Object> resultData = preliminaryNoticeService.queryReviewById(id);
		result.setResult_data(resultData);
		return result;
	}
	
	/**
	 * 更新通知用户审阅状态
	 * @param reviewStatus
	 */
	@RequestMapping("/updateUsreReviewStatus")
	@ResponseBody
	public Result updateUsreReviewStatus(String noticeId){
		Result result = new Result();
		preliminaryNoticeService.updateUsreReviewStatus(noticeId);
		return result;
	}
	
	/**
	 * 新增拟上会通知
	 */
	@RequestMapping("/create")
	@ResponseBody
	public Result create(String json, HttpServletRequest request) {
		Result result = new Result();
		Map<String, Object> map = Util.parseJson2Map(json);
		map.put("ID", Util.getUUID());
		String id = preliminaryNoticeService.save(map);
		result.setResult_data(id);
		return result;
	}

	/**
	 * 更新拟上会通知
	 */
	@RequestMapping("/update")
	@ResponseBody
	public Result update(String json, HttpServletRequest request) {
		Result result = new Result();
		Map<String, Object> map = Util.parseJson2Map(json);
		String id = preliminaryNoticeService.update(map);
		result.setResult_data(id);
		return result;
	}
	
	/**
	 * 更新拟上会通知
	 */
	@RequestMapping("/submit")
	@ResponseBody
	public Result submit(String id, HttpServletRequest request) {
		Result result = new Result();
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);
		//状态(0:草稿,1:正常,2:过期)
		map.put("status","1");

		preliminaryNoticeService.updateStatusById(map);
		return result;
	}
	
	/**
	 * 删除拟上会通知
	 */
	@RequestMapping("/deleteById")
	@ResponseBody
	public Result deleteById(String id, HttpServletRequest request) {
		Result result = new Result();
		preliminaryNoticeService.deleteById(id);
		return result;
	}
}

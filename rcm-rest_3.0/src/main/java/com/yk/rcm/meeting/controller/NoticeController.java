package com.yk.rcm.meeting.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import util.Util;

import com.yk.rcm.meeting.service.INoticeService;
import com.yk.rcm.meeting.service.INoticeUserService;

import common.PageAssistant;
import common.Result;

/**
 * 通知
 * @author hubiao
 */
@Controller
@RequestMapping("/notice")
public class NoticeController {
	
	@Resource
	private INoticeService noticeService;
	
	@Resource
	private INoticeUserService noticeUserService;

	/**
	 * 分页查询数据
	 * @param notice
	 * @param limit
	 */
	@RequestMapping("/queryListByPage")
	@ResponseBody
	public Result queryListByPage(HttpServletRequest request){
		Result result = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		noticeService.queryListByPage(page);
		result.setResult_data(page);
		return result;
	}
	
	/**
	 * 根据条件查询   前几条数据
	 * @param notice
	 * 		notice.type  通知类型(必填)
	 * 		notice.limit 表示前几条,默认为10条(可填)
	 * @return
	 */
	@RequestMapping("/queryListTopLimit")
	@ResponseBody
	public Result queryListTopLimit(@RequestParam Map<String,Object> notice){
		Result result = new Result();
		Object limitObject = notice.get("limit");
		//默认前10条
		if(Util.isEmpty(limitObject)){
			notice.put("limit", "10");
		}
		List<Map<String, Object>> resultData = noticeService.queryListTopLimit(notice);
		result.setResult_data(resultData);
		return result;
	}
	
	/**
	 * 根据条件查询详情
	 * @param notice
	 * 	notice.id 通知ID(必填)
	 * @return
	 */
	@RequestMapping("/queryInfo")
	@ResponseBody
	public Result queryInfo(@RequestParam Map<String,Object> notice){
		Result result = new Result();
		Map<String, Object> resultData = noticeService.queryInfo(notice);
		result.setResult_data(resultData);
		return result;
	}
	
	/**
	 * 更新审阅状态(1:待阅,2:已阅)
	 * @param reviewStatus
	 */
	@RequestMapping("/updateReviewStatus")
	@ResponseBody
	public Result updateReviewStatus(String noticeId){
		Result result = new Result();
		noticeUserService.updateReviewStatus(noticeId,"2");
		return result;
	}
	
	/**
	 * 根据关联ID查询第一个通知详情信息
	 * @param relationId
	 * @return
	 */
	@RequestMapping("/getByRelationId")
	@ResponseBody
	public Result getByRelationId(String relationId){
		Result result = new Result();
		Map<String, Object> resultData = noticeService.getByRelationId(relationId);
		result.setResult_data(resultData);
		return result;
	}
}

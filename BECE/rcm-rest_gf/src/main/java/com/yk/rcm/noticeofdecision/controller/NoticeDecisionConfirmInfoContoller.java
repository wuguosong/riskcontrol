/**
 * 
 */
package com.yk.rcm.noticeofdecision.controller;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.bson.Document;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yk.rcm.noticeofdecision.service.INoticeDecisionConfirmInfoService;
import common.PageAssistant;
import common.Result;

/**
 * @author shaosimin
 *
 */
@Controller
@RequestMapping("/noticeDecisionConfirmInfo")
public class NoticeDecisionConfirmInfoContoller {
	
	@Resource
	private INoticeDecisionConfirmInfoService noticeDecisionConfirmInfoService;
	/**
	 * 查询待确认数据
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryWaitConfirm")
	@ResponseBody
	public Result queryWaitConfirm(HttpServletRequest request){
		Result result = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		this.noticeDecisionConfirmInfoService.queryWaitConfirm(page);
		result.setResult_data(page);
		return result;
	}
	/**
	 * 分页查询已确认数据
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryConfirmed")
	@ResponseBody
	public Result queryConfirmed(HttpServletRequest request){
		Result result = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		this.noticeDecisionConfirmInfoService.queryConfirmed(page);
		result.setResult_data(page);
		return result;
	}

	/**
	 * 根据正式评审项目id,决策通知书确认
	 * @param request
	 * @return
	 */
	@RequestMapping("/comfirm")
	@ResponseBody
	public Result comfirm(HttpServletRequest request){
		Result result = new Result();
		String formalId = request.getParameter("formalId");
		String attachmentStr = request.getParameter("attachment");
		String noticeInfo = request.getParameter("nod");
		Document attachment = Document.parse(attachmentStr);
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("formalId", formalId);
		data.put("attachment", attachment);
		data.put("noticeInfo", noticeInfo);
		this.noticeDecisionConfirmInfoService.confirm(data);
		return result;
	} 
	
}

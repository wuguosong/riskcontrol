/**
 * 
 */
package com.yk.rcm.noticeofdecision.controller;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yk.rcm.noticeofdecision.service.INoticeOfDecisionService;

import common.Result;

/**
 * @author 80845530
 *
 */
@Controller
@RequestMapping("/noticeOfDecision")
public class NoticeOfDecisionContoller {
	@Resource
	private INoticeOfDecisionService noticeOfDecisionService;
	
	/**
	 * 根据formalId查询决策通知书
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryNoticeDecisionByFormalId")
	@ResponseBody
	public Result queryNoticeDecisionByFormalId(HttpServletRequest request){
		Result result = new Result();
		String businessId = request.getParameter("businessId");
		Map<String, Object> data = this.noticeOfDecisionService.queryMongoByFormalId(businessId);
		result.setResult_data(data);
		return result;
	}
}

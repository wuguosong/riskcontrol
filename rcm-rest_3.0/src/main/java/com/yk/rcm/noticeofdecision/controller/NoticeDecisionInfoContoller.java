/**
 * 
 */
package com.yk.rcm.noticeofdecision.controller;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.bson.Document;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yk.rcm.noticeofdecision.service.INoticeDecisionInfoService;
import common.PageAssistant;
import common.Result;

/**
 * @author yaphet
 *
 */
@Controller
@RequestMapping("/noticeDecisionInfo")
public class NoticeDecisionInfoContoller {
	
	@Resource
	private INoticeDecisionInfoService noticeDecisionService;
	
	/**
	 * 初始化列表数据
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryListDefaultInfo")
	@ResponseBody
	public Result queryListDefaultInfo(HttpServletRequest request){
		Result result = new Result();
		PageAssistant assistant = new PageAssistant(request.getParameter("page"));
		PageAssistant page = this.noticeDecisionService.queryStartByPage(assistant);
		result.setResult_data(page);
		return result;
	}
	/**
	 * 分页查询待提交数据
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryApplyList")
	@ResponseBody
	public Result queryApplyList(HttpServletRequest request){
		Result result = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		this.noticeDecisionService.queryStartByPage(page);
		result.setResult_data(page);
		return result;
	}
	/**
	 * 分页查询已提交数据
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryApplyedList")
	@ResponseBody
	public Result queryApplyedList(HttpServletRequest request){
		Result result = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		this.noticeDecisionService.queryOverByPage(page);
		result.setResult_data(page);
		return result;
	}

	
	/**
	 * 根据ID查询
	 * 查询新增页面初始数据
	 * @param request
	 * @return
	 */
	@RequestMapping("/querySaveDefaultInfo")
	@ResponseBody
	public Result querySaveDefaultInfo(HttpServletRequest request){
		Result result = new Result();
		String userId = request.getParameter("userId");
		Document queryUpdateDefaultInfo = this.noticeDecisionService.querySaveDefaultInfo(userId);
		result.setResult_data(queryUpdateDefaultInfo);
		return result;
	}
	
	@RequestMapping("/queryUpdateInitInfo")
	@ResponseBody
	public Result queryUpdateInitInfo(HttpServletRequest request){
		Result result = new Result();
		String projectId = request.getParameter("projectId");
		System.out.println("projectId:"+projectId);
		System.out.println(projectId);
		Document doc = this.noticeDecisionService.queryUpdateInitInfo(projectId);
		result.setResult_data(doc);
		return result;
	}
	
	

	/**
	 * update by id 
	 * 
	 * @param request
	 * @return Result
	 */
	@RequestMapping("/update")
	@ResponseBody
	public Result update(HttpServletRequest request){
		Result result = new Result();
		String json = request.getParameter("nod");
		System.out.println(json);
		this.noticeDecisionService.update(json);
		result.setResult_code("5");
		return result;
	}
	/**
	 * delete by id 
	 * 
	 * @param request
	 * @return Result
	 */
	@RequestMapping("/delete")
	@ResponseBody
	public Result delete(HttpServletRequest request){
		Result result = new Result();
		String projectId = request.getParameter("projectid");
		System.out.println(projectId);
		this.noticeDecisionService.delete(projectId);
		result.setResult_code("5");
		return result;
	}
	
	/**
	 * save 
	 * 
	 * @param request
	 * @return Result
	 */
	@RequestMapping("/create")
	@ResponseBody
	public Result create(HttpServletRequest request){
		Result result = new Result();
		String json = request.getParameter("nod");
		this.noticeDecisionService.create(json);
		result.setResult_code("5");		
		return result;
	}
	/**
	 * save 
	 * 
	 * @param request
	 * @return Result
	 */
	@RequestMapping("/getNoticeDecstionByID")
	@ResponseBody
	public Result getNoticeDecstionByID(HttpServletRequest request){
		Result result = new Result();
		String id = request.getParameter("id");
		Map<String, Object> doc = this.noticeDecisionService.getNoticeDecstionByID(id);
		result.setResult_data(doc);
		return result;
	}
	
	/**
	 * get rcm_noticeDecision_info mongoDB  by BusinessId
	 * @param request
	 * @return
	 */
	@RequestMapping("/getNoticeDecstionByBusinessId")
	@ResponseBody
	public Result getNoticeDecstionByBusinessId(String businessId){
		Result result = new Result();
		Map<String, Object> doc = this.noticeDecisionService.getNoticeDecstionByBusinessId(businessId);
		result.setResult_data(doc);
		return result;
	}
}

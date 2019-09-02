package com.yk.rcm.newFormalAssessment.controller;

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

import common.Constants;
import common.PageAssistant;
import common.Result;

/**
 * 正式评审新增
 * 
 * @author Sunny Qi
 */
@Controller
@RequestMapping("/formalAssessmentInfoCreate")
public class FormalAssessmentInfoCreateController {

	@Resource
	private IFormalAssessmentInfoCreateService formalAssessmentInfoCreateService;
	

	@RequestMapping("/createProject")
	@ResponseBody
	public Result create(HttpServletRequest request){
		Result result = new Result();
		String json = request.getParameter("projectInfo");
		try {
			String businessId = this.formalAssessmentInfoCreateService.createProject(json);
			result.setResult_data(businessId);
			result.setResult_code("S");
		} catch (Exception e) {
			result.setResult_name(e.getMessage());
		}
		
		return result;
	}
	
	@RequestMapping("/updateProject")
	@ResponseBody
	public Result update(HttpServletRequest request){
		Result result = new Result();
		String json = request.getParameter("projectInfo");
		try {
			this.formalAssessmentInfoCreateService.updateProject(json);
			result.setResult_code("S");
		} catch (Exception e) {
			result.setResult_name(e.getMessage());
		}
		
		return result;
	}
	
	@RequestMapping("/deleteProject")
	@ResponseBody
	public Result delete(HttpServletRequest request){
		Result result = new Result();
		String json = request.getParameter("ids");
		this.formalAssessmentInfoCreateService.deleteProject(json);
		return result;
	}
	
	@RequestMapping("/getNewProjectList")
	@ResponseBody
	public Result getNewProjectList(HttpServletRequest request){
		Result result = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		this.formalAssessmentInfoCreateService.getNewProjectList(page);
		page.setParamMap(null);
		result.setResult_data(page);
		return result;
	}
	@RequestMapping("/getProjectByID")
	@ResponseBody
	public Result getFormalAssessmentByID(HttpServletRequest request){
		Result result = new Result();
		String id = request.getParameter("id");
		Map<String, Object> projectInfo = this.formalAssessmentInfoCreateService.getProjectByID(id);
		String userId = ThreadLocalUtil.getUserId();
		projectInfo.put("currentUserId", userId);
		result.setResult_data(projectInfo);
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
		this.formalAssessmentInfoCreateService.addNewAttachment(json);
		
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
		this.formalAssessmentInfoCreateService.deleteAttachment(json);
		
		return result;
	}
	
	@RequestMapping("/changeMeetingAttach")
	@ResponseBody
	public Result changeMeetingAttach(HttpServletRequest request){
		Result result = new Result();
		String json = request.getParameter("json");
	    String flag = this.formalAssessmentInfoCreateService.changeMeetingAttach(json);
	    result.setResult_data(flag);
		
		return result;
	}
	
	@RequestMapping("/checkAttachment")
	@ResponseBody
	public Result checkAttachment(HttpServletRequest request){
		Result result = new Result();
		String Json = request.getParameter("json");
		List<Map> list = this.formalAssessmentInfoCreateService.checkAttachment(Json);
	    result.setResult_data(list);
		
		return result;
	}
	
	/**
	 * 新增会议信息(保存到mongo)
	 * @param request
	 * @return
	 */
	@RequestMapping("/addConferenceInformation")
	@ResponseBody
	public Result addConferenceInformation(HttpServletRequest request){
		Result result = new Result();
		String json = request.getParameter("information");
		String method = request.getParameter("method");
		this.formalAssessmentInfoCreateService.addConferenceInformation(json, method);
		return result;
	}
	
	/**
	 * 保存是否上会字段
	 * */
	@RequestMapping("/saveNeedMeeting")
	@ResponseBody
	public Result saveNeedMeeting(HttpServletRequest request){
		Result result = new Result();
		String id = request.getParameter("businessId");
		String needMeeting = request.getParameter("needMeeting");
		this.formalAssessmentInfoCreateService.saveNeedMeeting(id, needMeeting);
		return result;
	}
	
	
	/**
	 * 获取某类型历史附件
	 * @param request
	 * @return
	 */
	@RequestMapping("/getHistoryList")
	@ResponseBody
	public Result getHistoryList(HttpServletRequest request){
		Result result = new Result();
		String json = request.getParameter("json");
		try {
			List<Map<String, Object>> list = this.formalAssessmentInfoCreateService.getHistoryList(json);
			result.setResult_code(Constants.S);
			result.setSuccess(true);
			result.setResult_data(list);
		} catch (Exception e) {
			result.setResult_code(Constants.R);
			result.setSuccess(false);
			result.setResult_data(e);
			e.printStackTrace();
			result.setResult_name("获取历史附件失败!" + e.getMessage());
		}
		return result;
	}
	
	/**
	 * 保存附件信息(并修改stage状态为"5")
	 * 
	 * @param Request
	 * @return
	 */
	@RequestMapping("/saveEnvirMettingSummary")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_BULLETIN, operation = LogConstant.CREATE, description = "保存附件信息")
	public Result saveEnvirMettingSummary(HttpServletRequest request) {
		Result result = new Result();
		String businessId = request.getParameter("businessId");
		String projectName = request.getParameter("projectName");
		String mettingSummaryInfo = request.getParameter("mettingSummaryInfo");
		String pmodel = request.getParameter("pmodel");
		this.formalAssessmentInfoCreateService.saveEnvirMettingSummary(businessId, mettingSummaryInfo, projectName, pmodel);
		return result;
	}
	
	/**
	 * 查询会议纪要
	 * 
	 * @param Request
	 * @return
	 */
	@RequestMapping("/queryEnvirMettingSummarys")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_BULLETIN, operation = LogConstant.QUERY, description = "查询会议纪要")
	public Result queryEnvirMettingSummarys(HttpServletRequest request) {
		String businessId = request.getParameter("businessId");
		Result result = this.formalAssessmentInfoCreateService.queryEnvirMettingSummarys(businessId);
		return result;
	}
}

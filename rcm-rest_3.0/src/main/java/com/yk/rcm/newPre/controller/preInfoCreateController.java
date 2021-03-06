package com.yk.rcm.newPre.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yk.rcm.newPre.service.IPreInfoCreateService;

import util.ThreadLocalUtil;
import common.Constants;
import common.PageAssistant;
import common.Result;

/**
 * 正式评审新增
 * 
 * @author Sunny Qi
 */
@Controller
@RequestMapping("/preInfoCreate")
public class preInfoCreateController {

	@Resource
	private IPreInfoCreateService preInfoCreateService;
	

	@RequestMapping("/createProject")
	@ResponseBody
	public Result create(HttpServletRequest request){
		Result result = new Result();
		String json = request.getParameter("projectInfo");
		String businessId = this.preInfoCreateService.createProject(json);
		result.setResult_data(businessId);
		return result;
	}
	
	@RequestMapping("/updateProject")
	@ResponseBody
	public Result update(HttpServletRequest request){
		Result result = new Result();
		String json = request.getParameter("projectInfo");
		this.preInfoCreateService.updateProject(json);
		return result;
	}
	
	@RequestMapping("/deleteProject")
	@ResponseBody
	public Result delete(HttpServletRequest request){
		Result result = new Result();
		String json = request.getParameter("ids");
		this.preInfoCreateService.deleteProject(json);
		return result;
	}
	
	@RequestMapping("/getNewProjectList")
	@ResponseBody
	public Result getNewProjectList(HttpServletRequest request){
		Result result = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		this.preInfoCreateService.getNewProjectList(page);
		page.setParamMap(null);
		result.setResult_data(page);
		return result;
	}
	@RequestMapping("/getProjectByID")
	@ResponseBody
	public Result getFormalAssessmentByID(HttpServletRequest request){
		Result result = new Result();
		String id = request.getParameter("id");
		Map<String, Object> projectInfo = this.preInfoCreateService.getProjectByID(id);
		String userId = ThreadLocalUtil.getUserId();
		projectInfo.put("currentUserId", userId);
		result.setResult_data(projectInfo);
		return result;
	}
	@RequestMapping("/addAttachmengInfoToMongo")
	@ResponseBody
	public Result addAttachmengInfoToMongo(HttpServletRequest request){
		Result result = new Result();
		String json = request.getParameter("json");
		this.preInfoCreateService.addNewAttachment(json);
		
		return result;
	}
	@RequestMapping("/deleteAttachmengInfoInMongo")
	@ResponseBody
	public Result deleteAttachmengInfoInMongo(HttpServletRequest request){
		Result result = new Result();
		String json = request.getParameter("json");
		this.preInfoCreateService.deleteAttachment(json);
		
		return result;
	}
	@RequestMapping("/changeMeetingAttach")
	@ResponseBody
	public Result changeMeetingAttach(HttpServletRequest request){
		Result result = new Result();
		String json = request.getParameter("json");
	    String flag = this.preInfoCreateService.changeMeetingAttach(json);
	    result.setResult_data(flag);
		
		return result;
	}
	
	@RequestMapping("/checkAttachment")
	@ResponseBody
	public Result checkAttachment(HttpServletRequest request){
		Result result = new Result();
		String Json = request.getParameter("json");
		List<Map> list = this.preInfoCreateService.checkAttachment(Json);
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
		this.preInfoCreateService.addConferenceInformation(json, method);
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
			List<Map<String, Object>> list = this.preInfoCreateService.getHistoryList(json);
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
}

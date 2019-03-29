/**
 * 
 */
package com.yk.bpmn.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yk.bpmn.service.IBpmnService;
import com.yk.rcm.project.service.IFormalAssesmentService;
import com.yk.rcm.project.service.IPreAssementService;

import common.Constants;
import common.PageAssistant;
import common.Result;

/**
 * @author 80845530
 *
 */
@Controller
@RequestMapping("/bpmn")
public class BpmnController {
	@Resource
	private IBpmnService bpmnService;
	@Resource
	private IPreAssementService preAssementService;
	@Resource
	private IFormalAssesmentService formalAssesmentService;
			
	/**
	 * 发布流程
	 * @param bpmnType
	 * @return
	 */
	@RequestMapping("/deploy")
	@ResponseBody
	public Result deploy(String bpmnType){
//		Result result = new Result();
//		result.setResult_data(this.preAssementService.queryById(processKey));;
		return this.bpmnService.deploy(bpmnType);
//		return result;
	}
	/**
	 * stopProcess
	 * @param bpmnType, businessKey
	 * @return
	 */
	@RequestMapping("/stopProcess")
	@ResponseBody
	public Result stopProcess(String bpmnType, String businessKey){
		
//		result.setResult_data(this.preAssementService.queryById(processKey));;
		if(bpmnType == null || "".equals(bpmnType) || businessKey == null || "".equals(businessKey)){
			Result result = new Result();
			return result.setSuccess(false)
						.setResult_name("流程类型和工单号不能为空！");
			
		}
		return this.bpmnService.stopProcess(bpmnType, businessKey);
	}
	
	/**
	 * stopProcess
	 * @param bpmnType, businessKey
	 * @return
	 */
	@RequestMapping("/clear")
	@ResponseBody
	public Result clear(String bpmnType, String businessKey){
		if(bpmnType == null || "".equals(bpmnType) || businessKey == null || "".equals(businessKey)){
			Result result = new Result();
			return result.setSuccess(false)
						.setResult_name("流程类型和工单号不能为空！");
			
		}
		if(Constants.PRE_ASSESSMENT.equals(bpmnType)){
			this.preAssementService.deleteByIdSyncTz(businessKey);
		}else if(Constants.FORMAL_ASSESSMENT.equals(bpmnType)){
			this.formalAssesmentService.deleteByIdSyncTz(businessKey);
		}else{
			Result result = new Result();
			return result.setSuccess(false)
					.setResult_name("流程类型不存在！");
		}
		return new Result().setResult_name("执行成功！");
	}
	/**
	 * queryTaskById
	 * @param taskId
	 * @return
	 */
	@RequestMapping("/queryTaskById")
	@ResponseBody
	public Result queryTaskById(String taskId){
		Result result = new Result();
		if(taskId == null || "".equals(taskId.trim())){
			return result.setSuccess(false).setResult_name("任务不能为空！");
		}
		return this.bpmnService.queryTaskInfoById(taskId);
	}
	
	
	@RequestMapping("/queryProjectList")
	@ResponseBody
	public Result queryProjectList(HttpServletRequest request){
		Result result = new Result();
		Map<String, Object> params = new HashMap<String, Object>();
		String projectName = request.getParameter("PROJECT_NAME");
		params.put("projectName", projectName);
		
		PageAssistant page =new PageAssistant( (String)request.getParameter("page"));
		List<Map<String, Object>> list = this.bpmnService.queryProjectListByPage(page);
		page.setList(list);
		result.setResult_data(page);
		return result;
	}
	
	@RequestMapping("/queryProjectListByPage")
	@ResponseBody
	public Result queryProjectListByPage(HttpServletRequest request){
		Result result = new Result();
		Map<String, Object> params = new HashMap<String, Object>();
		String page = request.getParameter("page");
		PageAssistant pageAssistant = new PageAssistant(page);
		List<Map<String, Object>> list = this.bpmnService.queryProjectListByPage(pageAssistant);
		pageAssistant.setList(list);
		result.setResult_data(pageAssistant);
		return result;
	}
	
	@RequestMapping("/changeAuditUser")
	@ResponseBody
	public Result changeAuditUser(HttpServletRequest request){
		String flow = request.getParameter("flow");
		Result result = this.bpmnService.queryProjectList(flow);
		return result;
	}
	@RequestMapping("/getTaskPerson")
	@ResponseBody
	public Result getTaskPerson(HttpServletRequest request){
		String flow = request.getParameter("flow");
		Result result = this.bpmnService.getTaskPerson(flow);
		return result;
	}
	@RequestMapping("/endFlow")
	@ResponseBody
	public Result endFlow(HttpServletRequest request){
		String type = request.getParameter("type");
		String businessId = request.getParameter("businessId");
		String reason = request.getParameter("reason");
		Result result = new Result();
		this.bpmnService.endFlow(type,businessId,reason);
		return result;
	}
	@RequestMapping("/getProcessInstanceId")
	@ResponseBody
	public Result getProcessInstanceId(HttpServletRequest request){
		String businessId = request.getParameter("businessId");
		Result result = new Result();
		Map<String, Object> map = this.bpmnService.getProcessInstanceId(businessId);
		result.setResult_data(map);
		return result;
	}
	
}

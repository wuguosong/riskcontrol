package com.yk.rcm.formalAssessment.controller;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.yk.log.annotation.SysLog;
import com.yk.log.constant.LogConstant;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import util.ThreadLocalUtil;

import com.yk.rcm.formalAssessment.service.IFormalAssessmentInfoService;
import common.PageAssistant;
import common.Result;

@Controller
@RequestMapping("/formalAssessmentInfo")
public class FormalAssessmentInfoController {
	
	@Resource
	private IFormalAssessmentInfoService formalAssessmentInfoService;
	
	@RequestMapping("/saveFixGroupOption")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_FORMAL_ASSESSMENT, operation = LogConstant.CREATE, description = "保存固定组选项")
	public Result saveFixGroupOption(HttpServletRequest request){
		Result result = new Result();
		String json = request.getParameter("json");
		formalAssessmentInfoService.saveFixGroupOption(json);
		return result;
	}
	
	@RequestMapping("/saveMajorMemberOption")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_FORMAL_ASSESSMENT, operation = LogConstant.CREATE, description = "保存主要成员选项")
	public Result saveMajorMemberOption(HttpServletRequest request){
		Result result = new Result();
		String json = request.getParameter("json");
		String businessId = request.getParameter("businessId");
		formalAssessmentInfoService.saveMajorMemberOption(json,businessId);
		return result;
	}
	
	@RequestMapping("/saveLegalReviewInfo")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_FORMAL_ASSESSMENT, operation = LogConstant.CREATE, description = "保存法律审查信息")
	public Result saveLegalReviewInfo(HttpServletRequest request){
		Result result = new Result();
		String json = request.getParameter("json");
		String businessId = request.getParameter("businessId");
		formalAssessmentInfoService.saveLegalReviewInfo(json,businessId);
		return result;
	}
	@RequestMapping("/saveReviewInfo")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_FORMAL_ASSESSMENT, operation = LogConstant.CREATE, description = "保存审阅信息")
	public Result saveReviewInfo(HttpServletRequest request){
		Result result = new Result();
		String json = request.getParameter("json");
		String businessId = request.getParameter("businessId");
		
		formalAssessmentInfoService.saveReviewInfo(json,businessId);
		String professionalReviewersJson = request.getParameter("professionalReviewersJson");
		formalAssessmentInfoService.saveMajorMemberOption(professionalReviewersJson,businessId);
		return result;
	}
	
	
	@RequestMapping("/saveMajMemberInfo")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_FORMAL_ASSESSMENT, operation = LogConstant.CREATE, description = "保存主要成员信息")
	public Result saveMajMemberInfo(HttpServletRequest request){
		//保存
		Result result = new Result();
		String json = request.getParameter("json");
		String businessId = request.getParameter("businessId");
		Map<String, Object> user = ThreadLocalUtil.getUser();
		formalAssessmentInfoService.saveMajMemberInfo(json,businessId,user);
		return result;
	}
	
	/**
	 * 初始化列表数据
	 * 各种下拉
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryListDefaultInfo")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_FORMAL_ASSESSMENT, operation = LogConstant.QUERY, description = "初始化列表数据")
	public Result queryListDefaultInfo(HttpServletRequest request){
		Result result = new Result();
		return result;
	}
	/**
	 * 查询正式评审列表
	 */
	@RequestMapping("/queryFormalAssessmentList")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_FORMAL_ASSESSMENT, operation = LogConstant.QUERY, description = "查询正式评审列表")
	public Result queryFormalAssessmentList(HttpServletRequest request){
		Result result = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		this.formalAssessmentInfoService.queryByPage(page);
		page.setParamMap(null);
		result.setResult_data(page);
		return result;
	}
	/**
	 * 查询正式评审台账列表
	 */
	@RequestMapping("/queryPageForExport")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_FORMAL_ASSESSMENT, operation = LogConstant.QUERY, description = "查询正式评审台账列表")
	public Result queryPageForExport(HttpServletRequest request){
		Result result = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		this.formalAssessmentInfoService.queryPageForExport(page);
		result.setResult_data(page);
		return result;
	}
	/**
	 * 查询正式评审列表--已提交
	 */
	@RequestMapping("/queryFormalAssessmentSubmitedList")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_FORMAL_ASSESSMENT, operation = LogConstant.QUERY, description = "查询已提交正式评审列表")
	public Result queryFormalAssessmentSubmitedList(HttpServletRequest request){
		Result result = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		this.formalAssessmentInfoService.querySubmitedByPage(page);
		result.setResult_data(page);
		return result;
	}
	/**
	 * @param request
	 * @return Result
	 */
	@RequestMapping("/getFormalAssessmentByID")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_FORMAL_ASSESSMENT, operation = LogConstant.QUERY, description = "获取正式评审")
	public Result getFormalAssessmentByID(HttpServletRequest request){
		Result result = new Result();
		String id = request.getParameter("id");
		Map<String, Object> formalAssessment = this.formalAssessmentInfoService.getFormalAssessmentByID(id);
		String userId = ThreadLocalUtil.getUserId();
		formalAssessment.put("currentUserId", userId);
		result.setResult_data(formalAssessment);
		return result;
	}
	
	@RequestMapping("/create")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_FORMAL_ASSESSMENT, operation = LogConstant.CREATE, description = "保存正式评审")
	public Result create(HttpServletRequest request){
		Result result = new Result();
		String json = request.getParameter("formalAssessment");
		this.formalAssessmentInfoService.create(json);
		return result;
	}
	/**
	 * 根据id修改mongo中的测算意见、投资协议意见
	 * @param request
	 * @return
	 */
	@RequestMapping("/updateServiceTypeOpinion")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_FORMAL_ASSESSMENT, operation = LogConstant.UPDATE, description = "修改测算意见、投资协议意见")
	public Result updateServiceTypeOpinion(HttpServletRequest request){
		Result result = new Result();
		String json = request.getParameter("serviceTypeOpinion");
		String businessId = request.getParameter("businessId");
		this.formalAssessmentInfoService.updateServiceTypeOpinionByBusinessId(json,businessId);
		return result;
	}

	@RequestMapping("/updateAttachment")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_FORMAL_ASSESSMENT, operation = LogConstant.CREATE, description = "附件上传")
	public Result updateAttachment(HttpServletRequest request){
		Result result = new Result();
		String json = request.getParameter("json");
		this.formalAssessmentInfoService.updateAttachment(json);
		return result;
	}
	
	@RequestMapping("/changeAttachment")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_FORMAL_ASSESSMENT, operation = LogConstant.UPDATE, description = "修改附件")
	public Result changeAttachment(HttpServletRequest request){
		Result result = new Result();
		String json = request.getParameter("json");
		this.formalAssessmentInfoService.changeAttachment(json);
		return result;
	}
	@RequestMapping("/updateRiskAttachment")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_FORMAL_ASSESSMENT, operation = LogConstant.UPDATE, description = "修改风险附件")
	public Result updateRiskAttachment(HttpServletRequest request){
		String json = request.getParameter("json");
		Result result = this.formalAssessmentInfoService.updateRiskAttachment(json);
		return result;
	}
	
	@RequestMapping("/deleteAttachment")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_FORMAL_ASSESSMENT, operation = LogConstant.DELETE, description = "删除附件")
	public Result deleteAttachment(HttpServletRequest request){
		String json = request.getParameter("json");
		Result result = this.formalAssessmentInfoService.deleteAttachment(json);
		return result;
	}
	
	@RequestMapping("/deleteRiskAttachment")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_FORMAL_ASSESSMENT, operation = LogConstant.DELETE, description = "删除风险附件")
	public Result deleteRiskAttachment(HttpServletRequest request){
		String json = request.getParameter("json");
		Result result = this.formalAssessmentInfoService.deleteRiskAttachment(json);
		return result;
	}
	
	@RequestMapping("/deleteSign")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_FORMAL_ASSESSMENT, operation = LogConstant.DELETE, description = "删除签名")
	public Result deleteSign(HttpServletRequest request){
		Result result = new Result();
		String json = request.getParameter("json");
		this.formalAssessmentInfoService.deleteSign(json);
		return result;
	}
	
	@RequestMapping("/saveMeetingFiles")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_FORMAL_ASSESSMENT, operation = LogConstant.CREATE, description = "保存会议文件")
	public Result saveMeetingFiles(HttpServletRequest request){
		Result result = new Result();
		String json = request.getParameter("json");
		this.formalAssessmentInfoService.saveMeetingFiles(json);
		return result;
	}
	
	@RequestMapping("/signRead")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_FORMAL_ASSESSMENT, operation = LogConstant.UPDATE, description = "签名已阅")
	public Result signRead(HttpServletRequest request){
		Result result = new Result();
		String json = request.getParameter("json");
		this.formalAssessmentInfoService.signRead(json);
		return result;
	}
	
	
}

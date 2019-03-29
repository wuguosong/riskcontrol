package com.yk.rcm.formalAssessment.controller;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

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
	public Result saveFixGroupOption(HttpServletRequest request){
		Result result = new Result();
		String json = request.getParameter("json");
		formalAssessmentInfoService.saveFixGroupOption(json);
		return result;
	}
	
	@RequestMapping("/saveMajorMemberOption")
	@ResponseBody
	public Result saveMajorMemberOption(HttpServletRequest request){
		Result result = new Result();
		String json = request.getParameter("json");
		String businessId = request.getParameter("businessId");
		formalAssessmentInfoService.saveMajorMemberOption(json,businessId);
		return result;
	}
	
	@RequestMapping("/saveLegalReviewInfo")
	@ResponseBody
	public Result saveLegalReviewInfo(HttpServletRequest request){
		Result result = new Result();
		String json = request.getParameter("json");
		String businessId = request.getParameter("businessId");
		formalAssessmentInfoService.saveLegalReviewInfo(json,businessId);
		return result;
	}
	@RequestMapping("/saveReviewInfo")
	@ResponseBody
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
	public Result queryListDefaultInfo(HttpServletRequest request){
		Result result = new Result();
		return result;
	}
	/**
	 * 查询正式评审列表
	 */
	@RequestMapping("/queryFormalAssessmentList")
	@ResponseBody
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
	public Result updateServiceTypeOpinion(HttpServletRequest request){
		Result result = new Result();
		String json = request.getParameter("serviceTypeOpinion");
		String businessId = request.getParameter("businessId");
		this.formalAssessmentInfoService.updateServiceTypeOpinionByBusinessId(json,businessId);
		return result;
	}

	@RequestMapping("/updateAttachment")
	@ResponseBody
	public Result updateAttachment(HttpServletRequest request){
		Result result = new Result();
		String json = request.getParameter("json");
		this.formalAssessmentInfoService.updateAttachment(json);
		return result;
	}
	
	@RequestMapping("/changeAttachment")
	@ResponseBody
	public Result changeAttachment(HttpServletRequest request){
		Result result = new Result();
		String json = request.getParameter("json");
		this.formalAssessmentInfoService.changeAttachment(json);
		return result;
	}
	@RequestMapping("/updateRiskAttachment")
	@ResponseBody
	public Result updateRiskAttachment(HttpServletRequest request){
		String json = request.getParameter("json");
		Result result = this.formalAssessmentInfoService.updateRiskAttachment(json);
		return result;
	}
	
	@RequestMapping("/deleteAttachment")
	@ResponseBody
	public Result deleteAttachment(HttpServletRequest request){
		String json = request.getParameter("json");
		Result result = this.formalAssessmentInfoService.deleteAttachment(json);
		return result;
	}
	
	@RequestMapping("/deleteRiskAttachment")
	@ResponseBody
	public Result deleteRiskAttachment(HttpServletRequest request){
		String json = request.getParameter("json");
		Result result = this.formalAssessmentInfoService.deleteRiskAttachment(json);
		return result;
	}
	
	@RequestMapping("/deleteSign")
	@ResponseBody
	public Result deleteSign(HttpServletRequest request){
		Result result = new Result();
		String json = request.getParameter("json");
		this.formalAssessmentInfoService.deleteSign(json);
		return result;
	}
	
	@RequestMapping("/saveMeetingFiles")
	@ResponseBody
	public Result saveMeetingFiles(HttpServletRequest request){
		Result result = new Result();
		String json = request.getParameter("json");
		this.formalAssessmentInfoService.saveMeetingFiles(json);
		return result;
	}
	
	@RequestMapping("/signRead")
	@ResponseBody
	public Result signRead(HttpServletRequest request){
		Result result = new Result();
		String json = request.getParameter("json");
		this.formalAssessmentInfoService.signRead(json);
		return result;
	}
	
	
}

package com.yk.rcm.formalAssessment.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.bson.Document;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yk.rcm.formalAssessment.service.IFormalReportService;

import common.PageAssistant;
import common.Result;

@Controller
@RequestMapping("/formalReport")
public class FormalReportController {

	@Resource
	private IFormalReportService formalReportService;

	@RequestMapping("/queryUncommittedReportByPage")
	@ResponseBody
	public Result queryUncommittedReportByPage(String page, String json, HttpServletRequest request) {
		Result result = new Result();
		PageAssistant pageAssistant = new PageAssistant(page);
		this.formalReportService.queryUncommittedReportByPage(pageAssistant, json);
		result.setResult_data(pageAssistant);

		return result;
	}

	@RequestMapping("/querySubmittedReportByPage")
	@ResponseBody
	public Result querySubmittedReportByPage(String page, String json, HttpServletRequest request) {
		Result result = new Result();
		PageAssistant pageAssistant = new PageAssistant(page);
		this.formalReportService.querySubmittedReportByPage(pageAssistant, json);
		result.setResult_data(pageAssistant);

		return result;
	}

	@RequestMapping("/batchDeleteUncommittedReport")
	@ResponseBody
	public Result batchDeleteUncommittedReport(String ids, HttpServletRequest request) {
		Result result = new Result();
		String[] businessids = ids.split(",");
		this.formalReportService.batchDeleteFormalReport(businessids);

		return result;
	}

	@RequestMapping("/exportReportInfo")
	@ResponseBody
	public Result exportReportFile(HttpServletRequest request) {
		Result result = new Result();
		Map<String, String> map = null;
		try {
			map = this.formalReportService.exportReportInfo();
		} catch (IOException e) {
			e.printStackTrace();
		}
		result.setResult_data(map);

		return result;
	}

	@RequestMapping("/queryNotNewlyBuiltProject")
	@ResponseBody
	public Result queryNotNewlyBuiltProject(HttpServletRequest request) {
		Result result = new Result();
		List<Map<String, Object>> map = this.formalReportService.queryNotNewlyBuiltProject();
		result.setResult_data(map);

		return result;
	}

	@RequestMapping("/getProjectFormalReviewByID")
	@ResponseBody
	public Result getProjectFormalReviewByID(String id, HttpServletRequest request) {
		Result result = new Result();
		Document doc = this.formalReportService.getProjectFormalReviewByID(id);
		result.setResult_data(doc);

		return result;
	}

	@RequestMapping("/isReportExist")
	@ResponseBody
	public Result isReportExist(String businessid, HttpServletRequest request) {
		Result result = new Result();
		int i = this.formalReportService.isReportExist(businessid);
		result.setResult_data(i);

		return result;
	}

	@RequestMapping("/createNewReport")
	@ResponseBody
	public Result createNewReport(String json, HttpServletRequest request) {
		Result result = new Result();
		String id = this.formalReportService.createNewReport(json);
		result.setResult_data(id);

		return result;
	}

	@RequestMapping("/updateReport")
	@ResponseBody
	public Result updateReport(String json, HttpServletRequest request) {
		Result result = new Result();
		String id = this.formalReportService.updateReport(json);
		result.setResult_data(id);

		return result;
	}

	@RequestMapping("/submitAndupdate")
	@ResponseBody
	public Result submitAndupdate(String id, String projectFormalId, HttpServletRequest request) {
		Result result = new Result();
		this.formalReportService.submitAndupdate(id, projectFormalId);

		return result;
	}

	@RequestMapping("/isPossible2Submit")
	@ResponseBody
	public Result isPossible2Submit(String projectFormalId, HttpServletRequest request) {
		Result result = new Result();
		boolean flag = this.formalReportService.isPossible2Submit(projectFormalId);
		result.setResult_data(flag);

		return result;
	}

	@RequestMapping("/getPfrAssessmentWord")
	@ResponseBody
	public Result getPfrAssessmentWord(String id, HttpServletRequest request) {
		Result result = new Result();
		Map<String, String> map = this.formalReportService.getPfrAssessmentWord(id);
		result.setResult_data(map);

		return result;
	}

	@RequestMapping("/getByID")
	@ResponseBody
	public Result getByID(String id, HttpServletRequest request) {
		Result result = new Result();
		Document document = this.formalReportService.getByID(id);
		result.setResult_data(document);

		return result;
	}

	@RequestMapping("/selectPrjReviewView")
	@ResponseBody
	public Result selectPrjReviewView(String businessId, HttpServletRequest request) {
		Result result = new Result();
		Map<String, Object> map = this.formalReportService.selectPrjReviewView(businessId);
		result.setResult_data(map);

		return result;
	}

	@RequestMapping("/queryUncommittedDecisionMaterialByPage")
	@ResponseBody
	public Result queryUncommittedDecisionMaterialByPage(String page, String json, HttpServletRequest request) {
		Result result = new Result();
		PageAssistant pageAssistant = new PageAssistant(page);
		this.formalReportService.queryUncommittedDecisionMaterialByPage(pageAssistant, json);
		result.setResult_data(pageAssistant);

		return result;
	}

	@RequestMapping("/querySubmittedDecisionMaterialByPage")
	@ResponseBody
	public Result querySubmittedDecisionMaterialByPage(String page, String json, HttpServletRequest request) {
		Result result = new Result();
		PageAssistant pageAssistant = new PageAssistant(page);
		this.formalReportService.querySubmittedDecisionMaterialByPage(pageAssistant, json);
		result.setResult_data(pageAssistant);

		return result;
	}

	@RequestMapping("/findFormalAndReport")
	@ResponseBody
	public Result findFormalAndReport(String projectFormalId, HttpServletRequest request) {
		Result result = new Result();
		Map<String, Object> map = this.formalReportService.findFormalAndReport(projectFormalId);
		result.setResult_data(map);

		return result;
	}

	@RequestMapping("/addPolicyDecision")
	@ResponseBody
	public Result addPolicyDecision(String json, String method, HttpServletRequest request) {
		// 提交的判断项目参会信息是否已填写，如没填写禁止提交，并提示先填写参会信息才能够提交
//		Result result = new Result();
		Result result = this.formalReportService.addPolicyDecision(json, method);
//		result.setSuccess(flag);

		return result;
	}
	@RequestMapping("/updatePolicyDecision")
	@ResponseBody
	public Result updatePolicyDecision(String json, String method, HttpServletRequest request) {
		Result result = new Result();
		boolean flag = this.formalReportService.updatePolicyDecision(json, method);
		result.setResult_data(flag);
		return result;
	}
	
	@RequestMapping("/getByBusinessId")
	@ResponseBody
	public Result getByBusinessId(String businessId, HttpServletRequest request) {
		Result result = new Result();
		Map<String, Object> map = this.formalReportService.getOracleByBusinessId(businessId);
		result.setResult_data(map);
		return result;
	}
	@RequestMapping("/queryPfrNoticeFileList")
	@ResponseBody
	public Result queryPfrNoticeFileList(HttpServletRequest request) {
		Result result = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		List<Map<String, Object>> list = this.formalReportService.queryPfrNoticeFileList(page);
		page.setList(list);
		result.setResult_data(page);
		return result;
	}
	
	@RequestMapping("/saveReportFile")
	@ResponseBody
	public Result saveReportFile(HttpServletRequest request) {
		Result result = new Result();
		String json = request.getParameter("json");
		this.formalReportService.saveReportFile(json);
		return result;
	}
	
	/**
	 * author: Sam Gao
	 * date: 2018-12-05
	 * 决策会材料提交暂存功能
	 * 参数：
	 * json: 保存的数据对象
	 * method: 
	 * 
	 */
	@RequestMapping("/stagingFormalProjectSummary")
	@ResponseBody
	public Result stagingFormalProjectSummary(String json, HttpServletRequest request) {
		Result result = new Result();
		Document bjson = Document.parse(json);
		boolean flag = this.formalReportService.saveOrUpdateFormalProjectSummary(bjson);
		result.setResult_data("" + flag);
		return result;
	}
	
	@RequestMapping("/findFormalProjectSummary")
	@ResponseBody
	public Result findFormalProjectSummary(String json, String method, HttpServletRequest request) {
		Result result = new Result();
		Map<String, Object> map = this.formalReportService.findFormalProjectSummary();
		result.setResult_data(map);
		return result;
	}
}

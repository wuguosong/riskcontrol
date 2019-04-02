package com.yk.rcm.formalAssessment.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.yk.log.annotation.SysLog;
import com.yk.log.constant.LogConstant;
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
	@SysLog(module = LogConstant.MODULE_FORMAL_ASSESSMENT, operation = LogConstant.QUERY, description = "逐页查询未提交的报表")
	public Result queryUncommittedReportByPage(String page, String json, HttpServletRequest request) {
		Result result = new Result();
		PageAssistant pageAssistant = new PageAssistant(page);
		this.formalReportService.queryUncommittedReportByPage(pageAssistant, json);
		result.setResult_data(pageAssistant);

		return result;
	}

	@RequestMapping("/querySubmittedReportByPage")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_FORMAL_ASSESSMENT, operation = LogConstant.QUERY, description = "逐页查询已提交的报表")
	public Result querySubmittedReportByPage(String page, String json, HttpServletRequest request) {
		Result result = new Result();
		PageAssistant pageAssistant = new PageAssistant(page);
		this.formalReportService.querySubmittedReportByPage(pageAssistant, json);
		result.setResult_data(pageAssistant);

		return result;
	}

	@RequestMapping("/batchDeleteUncommittedReport")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_FORMAL_ASSESSMENT, operation = LogConstant.DELETE, description = "批量删除未提交的报告")
	public Result batchDeleteUncommittedReport(String ids, HttpServletRequest request) {
		Result result = new Result();
		String[] businessids = ids.split(",");
		this.formalReportService.batchDeleteFormalReport(businessids);

		return result;
	}

	@RequestMapping("/exportReportInfo")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_FORMAL_ASSESSMENT, operation = LogConstant.EXPORT, description = "导出报表信息")
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
	@SysLog(module = LogConstant.MODULE_FORMAL_ASSESSMENT, operation = LogConstant.QUERY, description = "查询非新建项目")
	public Result queryNotNewlyBuiltProject(HttpServletRequest request) {
		Result result = new Result();
		List<Map<String, Object>> map = this.formalReportService.queryNotNewlyBuiltProject();
		result.setResult_data(map);

		return result;
	}

	@RequestMapping("/getProjectFormalReviewByID")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_FORMAL_ASSESSMENT, operation = LogConstant.QUERY, description = "获取项目正式审核")
	public Result getProjectFormalReviewByID(String id, HttpServletRequest request) {
		Result result = new Result();
		Document doc = this.formalReportService.getProjectFormalReviewByID(id);
		result.setResult_data(doc);

		return result;
	}

	@RequestMapping("/isReportExist")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_FORMAL_ASSESSMENT, operation = LogConstant.QUERY, description = "项目是否存在")
	public Result isReportExist(String businessid, HttpServletRequest request) {
		Result result = new Result();
		int i = this.formalReportService.isReportExist(businessid);
		result.setResult_data(i);

		return result;
	}

	@RequestMapping("/createNewReport")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_FORMAL_ASSESSMENT, operation = LogConstant.CREATE, description = "新建项目")
	public Result createNewReport(String json, HttpServletRequest request) {
		Result result = new Result();
		String id = this.formalReportService.createNewReport(json);
		result.setResult_data(id);

		return result;
	}

	@RequestMapping("/updateReport")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_FORMAL_ASSESSMENT, operation = LogConstant.UPDATE, description = "更新项目")
	public Result updateReport(String json, HttpServletRequest request) {
		Result result = new Result();
		String id = this.formalReportService.updateReport(json);
		result.setResult_data(id);

		return result;
	}

	@RequestMapping("/submitAndupdate")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_FORMAL_ASSESSMENT, operation = LogConstant.UPDATE, description = "提交更新")
	public Result submitAndupdate(String id, String projectFormalId, HttpServletRequest request) {
		Result result = new Result();
		this.formalReportService.submitAndupdate(id, projectFormalId);

		return result;
	}

	@RequestMapping("/isPossible2Submit")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_FORMAL_ASSESSMENT, operation = LogConstant.QUERY, description = "是否提交")
	public Result isPossible2Submit(String projectFormalId, HttpServletRequest request) {
		Result result = new Result();
		boolean flag = this.formalReportService.isPossible2Submit(projectFormalId);
		result.setResult_data(flag);

		return result;
	}

	@RequestMapping("/getPfrAssessmentWord")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_FORMAL_ASSESSMENT, operation = LogConstant.QUERY, description = "获取正式评审评估词")
	public Result getPfrAssessmentWord(String id, HttpServletRequest request) {
		Result result = new Result();
		Map<String, String> map = this.formalReportService.getPfrAssessmentWord(id);
		result.setResult_data(map);

		return result;
	}

	@RequestMapping("/getByID")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_FORMAL_ASSESSMENT, operation = LogConstant.QUERY, description = "获取评审报告")
	public Result getByID(String id, HttpServletRequest request) {
		Result result = new Result();
		Document document = this.formalReportService.getByID(id);
		result.setResult_data(document);

		return result;
	}

	@RequestMapping("/selectPrjReviewView")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_FORMAL_ASSESSMENT, operation = LogConstant.QUERY, description = "提交决策委员会材料")
	public Result selectPrjReviewView(String businessId, HttpServletRequest request) {
		Result result = new Result();
		Map<String, Object> map = this.formalReportService.selectPrjReviewView(businessId);
		result.setResult_data(map);

		return result;
	}

	@RequestMapping("/queryUncommittedDecisionMaterialByPage")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_FORMAL_ASSESSMENT, operation = LogConstant.QUERY, description = "查询未提交的决策材料类型")
	public Result queryUncommittedDecisionMaterialByPage(String page, String json, HttpServletRequest request) {
		Result result = new Result();
		PageAssistant pageAssistant = new PageAssistant(page);
		this.formalReportService.queryUncommittedDecisionMaterialByPage(pageAssistant, json);
		result.setResult_data(pageAssistant);

		return result;
	}

	@RequestMapping("/querySubmittedDecisionMaterialByPage")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_FORMAL_ASSESSMENT, operation = LogConstant.QUERY, description = "查询提交的决策材料类型")
	public Result querySubmittedDecisionMaterialByPage(String page, String json, HttpServletRequest request) {
		Result result = new Result();
		PageAssistant pageAssistant = new PageAssistant(page);
		this.formalReportService.querySubmittedDecisionMaterialByPage(pageAssistant, json);
		result.setResult_data(pageAssistant);

		return result;
	}

	@RequestMapping("/findFormalAndReport")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_FORMAL_ASSESSMENT, operation = LogConstant.QUERY, description = "查询正式报告")
	public Result findFormalAndReport(String projectFormalId, HttpServletRequest request) {
		Result result = new Result();
		Map<String, Object> map = this.formalReportService.findFormalAndReport(projectFormalId);
		result.setResult_data(map);

		return result;
	}

	@RequestMapping("/addPolicyDecision")
	@ResponseBody
	/*@SysLog(module = LogConstant.MODULE_FORMAL_ASSESSMENT, operation = LogConstant.CREATE, description = "添加策略决策")*/
	public Result addPolicyDecision(String json, String method, HttpServletRequest request) {
		// 提交的判断项目参会信息是否已填写，如没填写禁止提交，并提示先填写参会信息才能够提交
//		Result result = new Result();
		Result result = this.formalReportService.addPolicyDecision(json, method);
//		result.setSuccess(flag);

		return result;
	}
	@RequestMapping("/updatePolicyDecision")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_FORMAL_ASSESSMENT, operation = LogConstant.UPDATE, description = "更新政策决策")
	public Result updatePolicyDecision(String json, String method, HttpServletRequest request) {
		Result result = new Result();
		boolean flag = this.formalReportService.updatePolicyDecision(json, method);
		result.setResult_data(flag);
		return result;
	}
	
	@RequestMapping("/getByBusinessId")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_FORMAL_ASSESSMENT, operation = LogConstant.QUERY, description = "获取政策决策")
	public Result getByBusinessId(String businessId, HttpServletRequest request) {
		Result result = new Result();
		Map<String, Object> map = this.formalReportService.getOracleByBusinessId(businessId);
		result.setResult_data(map);
		return result;
	}
	@RequestMapping("/queryPfrNoticeFileList")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_FORMAL_ASSESSMENT, operation = LogConstant.QUERY, description = "查询可替换的决策会信息")
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
	@SysLog(module = LogConstant.MODULE_FORMAL_ASSESSMENT, operation = LogConstant.CREATE, description = "保存决策会附件")
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
	@SysLog(module = LogConstant.MODULE_FORMAL_ASSESSMENT, operation = LogConstant.CREATE, description = "决策会材料提交暂存")
	public Result stagingFormalProjectSummary(String json, HttpServletRequest request) {
		Result result = new Result();
		Document bjson = Document.parse(json);
		boolean flag = this.formalReportService.saveOrUpdateFormalProjectSummary(bjson);
		result.setResult_data("" + flag);
		return result;
	}
	
	@RequestMapping("/findFormalProjectSummary")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_FORMAL_ASSESSMENT, operation = LogConstant.CREATE, description = "查找正式项目摘要")
	public Result findFormalProjectSummary(String json, String method, HttpServletRequest request) {
		Result result = new Result();
		Map<String, Object> map = this.formalReportService.findFormalProjectSummary();
		result.setResult_data(map);
		return result;
	}
}

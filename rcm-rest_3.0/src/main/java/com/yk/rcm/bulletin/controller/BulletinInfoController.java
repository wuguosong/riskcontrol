/**
 * 
 */
package com.yk.rcm.bulletin.controller;

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

import util.ThreadLocalUtil;

import com.yk.rcm.bulletin.service.IBulletinInfoService;

import common.Constants;
import common.PageAssistant;
import common.Result;

/**
 * @author wufucan 通报基本信息模块controller
 */
@Controller
@RequestMapping("/bulletinInfo")
public class BulletinInfoController {
	@Resource
	private IBulletinInfoService bulletinInfoService;

	/**
	 * 攒讯列表（分页）
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryListByPage")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_BULLETIN, operation = LogConstant.QUERY, description = "查询列表")
	public Result queryListByPage(HttpServletRequest request) {
		Result result = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		this.bulletinInfoService.queryListByPage(page);
		result.setResult_data(page);
		return result;
	}

	/**
	 * 查询待提交（草稿，还有未通过终止的，可以重复提交）
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryApplyList")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_BULLETIN, operation = LogConstant.QUERY, description = "查询代提交列表")
	public Result queryApplyList(HttpServletRequest request) {
		Result result = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		this.bulletinInfoService.queryApplyByPage(page);
		page.setParamMap(null);
		result.setResult_data(page);
		return result;
	}

	/**
	 * 查询已提交（审核中的）
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryApplyedList")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_BULLETIN, operation = LogConstant.QUERY, description = "查询已提交列表")
	public Result queryApplyedList(HttpServletRequest request) {
		Result result = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		this.bulletinInfoService.queryApplyedByPage(page);
		result.setResult_data(page);
		return result;
	}

	/**
	 * 新增通报页面数据初始化
	 * 
	 * @return
	 */
	@RequestMapping("/queryCreateDefaultInfo")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_BULLETIN, operation = LogConstant.QUERY, description = "新增页面数据初始化")
	public Result queryCreateDefaultInfo() {
		String userId = ThreadLocalUtil.getUserId();
		return this.bulletinInfoService.queryCreateDefaultInfo(userId);
	}

	/**
	 * 修改通报页面数据初始化
	 * 
	 * @param businessId
	 * @return
	 */
	@RequestMapping("/queryUpdateDefaultInfo")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_BULLETIN, operation = LogConstant.QUERY, description = "修改页面数据初始化")
	public Result queryUpdateDefaultInfo(String businessId) {
		Result result = new Result();
		Map<String, Object> data = this.bulletinInfoService.queryUpdateDefaultInfo(businessId);
		result.setResult_data(data);
		return result;
	}

	/**
	 * 新增保存和修改保存方法，根据有没有_id来判断是新增还是修改
	 * 
	 * @param json
	 * @return
	 */
	@RequestMapping("/saveOrUpdate")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_BULLETIN, operation = LogConstant.CREATE, description = "新增或者修改")
	public Result saveOrUpdate(String json) {
		Result result = new Result();
		Document doc = Document.parse(json);
		if (doc.get("_id") == null) {
			// 新增
			String id = this.bulletinInfoService.save(doc);
			result.setResult_data(id);
		} else {
			// 修改
			this.bulletinInfoService.updateByBusinessId(doc);
			result.setResult_data(doc.get("_id"));
		}

		return result;
	}

	/**
	 * 批量删除
	 * 
	 * @param ids 删除多个时，逗号隔开id
	 * @return
	 */
	@RequestMapping("/deleteByIds")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_BULLETIN, operation = LogConstant.DELETE, description = "批量删除")
	public Result deleteByIds(String ids) {
		Result result = new Result();
		String[] idsArr = ids.split(",");
		this.bulletinInfoService.deleteByIds(idsArr);
		return result;
	}

	/**
	 * 列表页面数据初始化
	 * 
	 * @return
	 */
	@RequestMapping("/queryListDefaultInfo")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_BULLETIN, operation = LogConstant.QUERY, description = "列表页面初始化")
	public Result queryListDefaultInfo() {
		Result result = new Result();
		Map<String, Object> map = this.bulletinInfoService.queryListDefaultInfo();
		result.setResult_data(map);
		return result;
	}

	/**
	 * 详情查看页面数据初始化
	 * 
	 * @return
	 */
	@RequestMapping("/queryViewDefaultInfo")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_BULLETIN, operation = LogConstant.QUERY, description = "查看页面初始化")
	public Result queryViewDefaultInfo(String businessId) {
		Result result = new Result();
		Map<String, Object> map = this.bulletinInfoService.queryViewDefaultInfo(businessId);
		result.setResult_data(map);
		return result;
	}

	@RequestMapping("/addAttachmengInfoToMongo")
	@ResponseBody
	public Result addAttachmengInfoToMongo(HttpServletRequest request) {
		Result result = new Result();
		String json = request.getParameter("json");
		this.bulletinInfoService.addNewAttachment(json);

		return result;
	}

	@RequestMapping("/deleteAttachmengInfoInMongo")
	@ResponseBody
	public Result deleteAttachmengInfoInMongo(HttpServletRequest request) {
		Result result = new Result();
		String json = request.getParameter("json");
		this.bulletinInfoService.deleteAttachment(json);

		return result;
	}

	/**
	 * 获取某类型历史附件
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/getHistoryList")
	@ResponseBody
	public Result getHistoryList(HttpServletRequest request) {
		Result result = new Result();
		String json = request.getParameter("json");
		try {
			List<Map<String, Object>> list = this.bulletinInfoService.getHistoryList(json);
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

//=============================================会议纪要==============================================================
	/**
	 * 查询未出会议纪要（流程状态为stage：4）
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryMettingSummary")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_BULLETIN, operation = LogConstant.QUERY, description = "查询未出会议纪要")
	public Result queryMettingSummary(HttpServletRequest request) {
		Result result = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		this.bulletinInfoService.quaryMettingSummary(page);
		result.setResult_data(page);
		return result;
	}

	/**
	 * 查询已出会议纪要（流程状态为stage：5）
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryMettingSummaryed")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_BULLETIN, operation = LogConstant.QUERY, description = "查询已出会议纪要")
	public Result queryMettingSummaryed(HttpServletRequest request) {
		Result result = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		this.bulletinInfoService.queryMettingSummaryed(page);
		result.setResult_data(page);
		return result;
	}

	/**
	 * 保存附件信息(并修改stage状态为"5")
	 * 
	 * @param Request
	 * @return
	 */
	@RequestMapping("/saveMettingSummary")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_BULLETIN, operation = LogConstant.CREATE, description = "保存附件信息")
	public Result saveMettingSummary(HttpServletRequest request) {
		Result result = new Result();
		String businessId = request.getParameter("businessId");
		String pmodel = request.getParameter("pmodel");
		String mettingSummaryInfo = request.getParameter("mettingSummaryInfo");
		this.bulletinInfoService.saveMettingSummary(businessId, mettingSummaryInfo, pmodel);
		return result;
	}

	/**
	 * 查询会议纪要
	 * 
	 * @param Request
	 * @return
	 */
	@RequestMapping("/queryRBIMettingSummarys")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_BULLETIN, operation = LogConstant.QUERY, description = "查询会议纪要")
	public Result queryRBIMettingSummarys(HttpServletRequest request) {
		String businessId = request.getParameter("businessId");
		Result result = this.bulletinInfoService.queryRBIMettingSummarys(businessId);
		return result;
	}

	/**
	 * 保存法律评审提交信息
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/saveLegalLeaderAttachment")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_BULLETIN, operation = LogConstant.CREATE, description = "保存法律评审提交信息")
	public Result saveLegalLeaderAttachment(HttpServletRequest request) {
		Result result = new Result();
		String businessId = request.getParameter("businessId");
		String legalLeaderAttachment = request.getParameter("attachment");
		String opinion = request.getParameter("opinion");
		this.bulletinInfoService.saveLegalLeaderAttachment(businessId, legalLeaderAttachment, opinion);
		return result;
	}

	/**
	 * 保存业务评审提交信息
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/saveReviewLeaderAttachment")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_BULLETIN, operation = LogConstant.CREATE, description = "保存业务评审提交信息")
	public Result saveReviewLeaderAttachment(HttpServletRequest request) {
		Result result = new Result();
		String businessId = request.getParameter("businessId");
		String reviewLeaderAttachment = request.getParameter("attachment");
		String opinion = request.getParameter("opinion");
		this.bulletinInfoService.saveReviewLeaderAttachment(businessId, reviewLeaderAttachment, opinion);
		return result;
	}

	/**
	 * 保存业务评审提交信息
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/saveRiskLeaderAttachment")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_BULLETIN, operation = LogConstant.CREATE, description = "保存业务评审提交信息")
	public Result saveRiskLeaderAttachment(HttpServletRequest request) {
		Result result = new Result();
		String businessId = request.getParameter("businessId");
		String riskLeaderAttachment = request.getParameter("attachment");
		String opinion = request.getParameter("opinion");
		this.bulletinInfoService.saveRiskLeaderAttachment(businessId, riskLeaderAttachment, opinion);
		return result;
	}

	/**
	 * 保存任务分配信息
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/saveTaskPerson")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_BULLETIN, operation = LogConstant.CREATE, description = "保存任务分配信息")
	public Result saveTaskPerson(HttpServletRequest request) {
		Result result = new Result();
		String businessId = request.getParameter("businessId");
		String json = request.getParameter("json");
		this.bulletinInfoService.saveTaskPerson(businessId, json);
		return result;
	}

	/**
	 * 修改基础附件
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/updateBaseFile")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_BULLETIN, operation = LogConstant.UPDATE, description = "修改基础附件")
	public Result updateBaseFile(HttpServletRequest request) {
		Result result = new Result();
		String businessId = request.getParameter("businessId");
		String attachment = request.getParameter("attachment");
		this.bulletinInfoService.updateBaseFile(businessId, attachment);
		return result;
	}
}

package com.yk.rcm.pre.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.bson.Document;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import util.ThreadLocalUtil;
import util.Util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yk.common.IBaseMongo;
import com.yk.rcm.fillMaterials.service.IFillMaterialsService;
import com.yk.rcm.pre.dao.IPreBiddingMapper;
import com.yk.rcm.pre.service.IPreAuditReportService;
import com.yk.rcm.pre.service.IPreBiddingService;
import com.yk.rcm.pre.service.IPreInfoService;

import common.Constants;
import common.PageAssistant;

@Service
@Transactional
public class PreBiddingServiceImpl implements IPreBiddingService {

	@Resource
	private IPreBiddingMapper preBiddingMapper;

	@Resource
	private IBaseMongo baseMongo;

	@Resource
	private IPreInfoService preInfoService;
	
	@Resource
	private IPreAuditReportService preAuditReportService;
	
	@Resource
	private IFillMaterialsService fillMaterialsService;

	@SuppressWarnings("unchecked")
	@Override
	public void queryUncommittedByPage(PageAssistant page, String json) {
		Map<String, Object> params = new HashMap<String, Object>();
		Map<String, Object> retMap = new Gson().fromJson(json, new TypeToken<Map<String, Object>>() {
		}.getType());
		params.put("page", page);
		params.put("projectName", retMap.get("projectName"));
		params.put("createBy", retMap.get("createBy"));
		if (!ThreadLocalUtil.getIsAdmin()) {
			params.put("psfzr", ThreadLocalUtil.getUserId());
		}
		params.put("pertainareaname", retMap.get("pertainareaname"));
		String orderBy = page.getOrderBy();
		if (orderBy == null) {
			orderBy = " create_date desc ";
		}
		params.put("orderBy", orderBy);

		List<Map<String, Object>> list = preBiddingMapper.queryUncommittedByPage(params);
		page.setList(list);
	}

	@Override
	public void querySubmittedByPage(PageAssistant page, String json) {
		Map<String, Object> params = new HashMap<String, Object>();
		Map<String, Object> retMap = new Gson().fromJson(json, new TypeToken<Map<String, Object>>() {
		}.getType());
		params.put("page", page);
		params.put("projectName", retMap.get("projectName"));
		params.put("createBy", retMap.get("createBy"));
		if (!ThreadLocalUtil.getIsAdmin()) {
			params.put("psfzr", ThreadLocalUtil.getUserId());
		}
		params.put("pertainareaname", retMap.get("pertainareaname"));
		String orderBy = page.getOrderBy();
		if (orderBy == null) {
			orderBy = " decision_commit_time desc ";
		}
		params.put("orderBy", orderBy);

		List<Map<String, Object>> list = preBiddingMapper.querySubmittedByPage(params);
		page.setList(list);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getByBusinessId(String businessId) {
		String applyDate = preBiddingMapper.queryApplDate(businessId);

		Map<String, Object> preOracle = preInfoService.getOracleByBusinessId(businessId);
		Map<String, Object> preMongo = baseMongo.queryById(businessId,Constants.RCM_PRE_INFO);
		Map<String, Object> reportOracle = preAuditReportService.getOracleByBusinessId(businessId);
		List<Document> attachment = (List<Document>) preMongo.get("attachment");// 获取附件类型
		List<Document> attach = new ArrayList<Document>();
		if (null != attachment) {
			for (Document document : attachment) {
				Document doc4 = new Document();
				String item_name = document.getString("ITEM_NAME");
				String uuid = document.getString("UUID");
				doc4.put("ITEM_NAME", item_name);
				doc4.put("UUID", uuid);
				attach.add(doc4);
			}
		}
		
		
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("preMongo", preMongo);
		param.put("attach", attach);
		param.put("applyDate", applyDate);
		param.put("stage", preOracle.get("STAGE"));
		param.put("reportOracle", reportOracle);
		
		return param;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean addPolicyDecision(Document bjson, String method) {
		boolean flag = true;
		String businessId = bjson.getString("id");
		
		
		Map<String, Object> statusMap = new HashMap<String, Object>();
		statusMap.put("table", "RCM_PRE_INFO");
		statusMap.put("filed", "IS_SUBMIT_BIDDING");
		statusMap.put("status", "0");
		statusMap.put("BUSINESSID", businessId);
		this.fillMaterialsService.updateProjectStaus(statusMap);

		if ("ss".equals(method)) {
			flag = this.isHaveMeetingInfo(businessId);
			if (flag) {
				Map<String, Object> map = new HashMap<String, Object>();
//				map.put("stage", "4");
				map.put("decision_commit_time", Util.now());
				map.put("businessid", businessId);
				
				Map<String, Object> statusMap1 = new HashMap<String, Object>();
				statusMap1.put("table", "RCM_PRE_INFO");
				statusMap1.put("filed", "IS_SUBMIT_BIDDING");
				statusMap1.put("status", "1");
				statusMap1.put("BUSINESSID", businessId);
				this.fillMaterialsService.updateProjectStaus(statusMap);
				
				Map<String, Object> Object = this.fillMaterialsService.getRPIStatus(businessId);
				if(Util.isNotEmpty(Object)) {
					if (Util.isNotEmpty(Object.get("IS_SUBMIT_REPORT"))) {
						if (Object.get("IS_SUBMIT_REPORT").equals("1") && Object.get("IS_SUBMIT_BIDDING").equals("1")) {
							map.put("stage", "4");
						}
					}
				}
				
				preBiddingMapper.changeState(map);
			} else {
				return false;
			}
		}

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("businessId", businessId);
		// 获取被选中的投资部门提供的附件、项目重要边界条件的信息以及附件列表的附件信息
		Document policyDecision = (Document) bjson.get("policyDecision");
		if (Util.isNotEmpty(policyDecision)) {
			Document projectType = (Document) policyDecision.get("projectType");// 项目类型

			if (Util.isNotEmpty(projectType)) {
				params.put("serviceType", projectType.getString("ITEM_CODE"));// 项目类型
				params.put("projectSize", policyDecision.get("projectSize"));// 项目规模
				params.put("investmentAmount", policyDecision.get("investmentAmount"));// 投资金额
				params.put("rateOfReturn", policyDecision.get("rateOfReturn"));// 投资收益率
			}
		}

		// 更新项目规模、投资金额、收益率、业务类型
		preAuditReportService.updateReportByBusinessId(params);

		// 获取投资部门提供的所有附件(包含新增的附件)
		List<Document> attachments = (List<Document>) bjson.get("ac_attachment");
		bjson.remove("ac_attachment");

		Document doc = (Document) this.baseMongo.queryById(businessId, Constants.RCM_PRE_INFO);
		doc.put("fkPsResult", bjson.getString("fkPsResult"));// 风控中心评审结论
		doc.put("fkRiskTip", bjson.getString("fkRiskTip"));// 风控重点风险提示

		// 获取投资部门提供的附件中被选中的附件
		List<Document> decisionMakingCommitteeStaffFiles = (List<Document>) policyDecision
				.get("decisionMakingCommitteeStaffFiles");

		for (Document item : decisionMakingCommitteeStaffFiles) {

			String version = item.getString("version");
			String uuid = item.getString("UUID");
			if ("".equals(uuid) || null == uuid) {
				item.put("version", "");
			} else if ("".equals(version) || null == version) {

				int tmp = 1;
				for (Document attachment : attachments) {
					if (attachment.getString("UUID").equals(uuid)) {
						List<Document> filesList = (List<Document>) attachment.get("files");
						tmp = filesList.size();
					}
				}
				item.put("version", String.valueOf(tmp));
			}
		}
		doc.put("policyDecision", policyDecision);
		this.baseMongo.updateSetByObjectId(businessId, doc, Constants.RCM_PRE_INFO);

		// 处理附件
		if (null != attachments) {
			for (Document document : attachments) {
				List<Document> files = (List<Document>) document.get("files");
				if (Util.isNotEmpty(files)) {
					for (Document document_ : files) {
						document_.remove("newFile");
						document_.remove("newItem");
						document_.remove("ITEM_NAME");
						document_.remove("UUID");
					}
				}
			}
			// 更新附件
			Document doc_rpi = (Document) this.baseMongo.queryById(businessId, Constants.RCM_PRE_INFO);
			doc_rpi.put("attachment", attachments);
			this.baseMongo.updateSetByObjectId(businessId, doc_rpi, Constants.RCM_PRE_INFO);
		}
			Map<String,Object> map = new HashMap<String,Object>();
			Map<String,Object> reviewReport = (Map<String, Object>) bjson.get("reviewReport");
			map.put("reviewReport", reviewReport);
			// 更新参会部分信息
			map.put("meetingInfo", (Map<String, Object>) bjson.get("meetingInfo"));
			this.baseMongo.updateSetByObjectId(businessId, map, Constants.RCM_PRE_INFO);
		return flag;
	}

	@Override
	public boolean isHaveMeetingInfo(String businessId) {
		boolean flag = false;
		Map<String, Object> queryById = this.baseMongo.queryById(businessId, Constants.RCM_PRE_INFO);
		Map<String, Object> meetingInfo = (Map<String, Object>) queryById.get("meetingInfo");
		if(Util.isNotEmpty(meetingInfo)){
			flag = true;
		}
		return flag;
	}

}

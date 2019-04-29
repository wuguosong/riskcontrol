package com.yk.rcm.formalAssessment.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.annotation.Resource;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mongodb.BasicDBObject;
import com.yk.common.IBaseMongo;
import com.yk.flow.util.JsonUtil;
import com.yk.rcm.fillMaterials.dao.IFillMaterialsMapper;
import com.yk.rcm.fillMaterials.service.IFillMaterialsService;
import com.yk.rcm.formalAssessment.dao.IFormalAssessmentInfoMapper;
import com.yk.rcm.formalAssessment.dao.IFormalReportMapper;
import com.yk.rcm.formalAssessment.service.IFormalAssessmentInfoService;
import com.yk.rcm.formalAssessment.service.IFormalReportService;
import com.yk.rcm.report.service.IReportInfoService;

import common.Constants;
import common.ExportExcel;
import common.PageAssistant;
import common.Result;
import report.FormalAssessmentReportUtil;
import util.FileUtil;
import util.PropertiesUtil;
import util.ThreadLocalUtil;
import util.Util;
import ws.client.TzClient;

@Service
@Transactional
public class FormalReportServiceImpl implements IFormalReportService {

	@Resource
	private IFormalReportMapper formalReportMapper;

	@Resource
	private IBaseMongo baseMongo;

	@Resource
	private IReportInfoService reportInfoService;

	@Resource
	private IFormalAssessmentInfoService formalAssessmentInfoService;
	
	@Resource
	private IFillMaterialsService fillMaterialsService;
	
	@Resource
	private IFillMaterialsMapper fillMaterialsMapper;
	
	@Resource
	private IFormalAssessmentInfoMapper formalAssessmentInfoMapper;
	
	private Logger logger = Logger.getLogger("FormalReportServiceImpl");

	@SuppressWarnings("unchecked")
	@Override
	public void queryUncommittedReportByPage(PageAssistant page, String json) {
		Map<String, Object> params = new HashMap<String, Object>();
		Map<String, Object> retMap = JsonUtil.fromJson(json, Map.class);
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

		List<Map<String, Object>> list = this.formalReportMapper.queryUncommittedReportByPage(params);
		page.setList(list);
	}

	@Override
	public void querySubmittedReportByPage(PageAssistant page, String json) {

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

		List<Map<String, Object>> list = this.formalReportMapper.querySubmittedReportByPage(params);
		page.setList(list);
	}

	@Override
	public void batchDeleteFormalReport(String[] businessids) {

		this.formalReportMapper.batchDeleteFormalReport(businessids);

		BasicDBObject queryAndWhere = new BasicDBObject();
		BasicDBObject queryAndWhere2 = new BasicDBObject();

		for (String string : businessids) {
			queryAndWhere.put("projectFormalId", string);
			List<Map<String, Object>> listMap = this.baseMongo.queryByCondition(queryAndWhere,
					Constants.RCM_FORMALREPORT_INFO);

			for (Map<String, Object> map : listMap) {
				String str = map.get("_id").toString();

				queryAndWhere2.put("reportlId", str);
				List<Map<String, Object>> list = this.baseMongo.queryByCondition(queryAndWhere2,
						Constants.FORMAL_MEETING_INFO);

				if (!list.isEmpty()) {
					for (Map<String, Object> map2 : list) {
						this.baseMongo.deleteById(map2.get("_ids").toString(), Constants.FORMAL_MEETING_INFO);
					}
				}
				this.baseMongo.deleteById(str, Constants.RCM_FORMALREPORT_INFO);
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Map<String, String> exportReportInfo() throws IOException {

		List list = this.formalReportMapper.queryUncommittedReportByPage(null);
		String[] arr = new String[] { "项目名称", "投资经理", "评审负责人", "项目模式", "申报单位", "报告状态" };
		String[] arrColumn = new String[] { "PROJECTNAME", "CREATEBY", "REVIEW_LEADER_NAME", "SERVICETYPE_ID",
				"PERTAINAREANAME", "ISWAITCREATE" };

		return ExportExcel.exportExcel("正式评审报告列表", "forAssessmentReport", arr, arrColumn, list);
	}

	@Override
	public List<Map<String, Object>> queryNotNewlyBuiltProject() {

		return this.formalReportMapper.queryNotNewlyBuiltProject(ThreadLocalUtil.getUserId());
	}

	@Override
	public Document getProjectFormalReviewByID(String id) {
		Map<String, Object> map = this.baseMongo.queryById(id, Constants.RCM_FORMALASSESSMENT_INFO);

		return (Document) map;
	}

	@Override
	public int isReportExist(String businessid) {

		return this.formalReportMapper.isReportExist(businessid);
	}

	@Override
	public String createNewReport(String json) {
		Document doc = Document.parse(json);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("businessId", doc.getString("projectFormalId"));
		params.put("controller_val", doc.getString("controllerVal"));
		this.reportInfoService.saveReport(params);
		
		Map<String, Object> statusMap = new HashMap<String, Object>();
		statusMap.put("table", "RCM_FORMALASSESSMENT_INFO");
		statusMap.put("filed", "IS_SUBMIT_REPORT");
		statusMap.put("status", "0");
		statusMap.put("BUSINESSID", doc.getString("projectFormalId"));
		this.fillMaterialsService.updateProjectStaus(statusMap);

		// 设置添加时间
		Date now = Util.now();
		doc.append("create_date", Util.format(now));
		doc.append("currentTimeStamp", Util.format(now, "yyyyMMddHHmmssSSS"));
		this.baseMongo.save(doc, Constants.RCM_FORMALREPORT_INFO);
		String id = doc.get("_id").toString();
		doc.put("id", id);
		return id;
	}

	@Override
	public String updateReport(String json) {
		BasicDBObject queryAndWhere = new BasicDBObject();
		Document bson = Document.parse(json);

		String id = bson.get("_id").toString();

		bson.append("update_date", Util.format(Util.now(), "yyyy-MM-dd HH:mm:ss"));
		queryAndWhere.put("_id", new ObjectId(id));
		bson.put("_id", new ObjectId(id));

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("businessId", bson.getString("projectFormalId"));
		this.reportInfoService.updateReportByBusinessId(params);

		this.baseMongo.updateSetByObjectId(id, bson, Constants.RCM_FORMALREPORT_INFO);

		return id;
	}

	@Override
	public void submitAndupdate(String id, String projectFormalId) {
		Map<String, Object> map = new HashMap<String, Object>();
		
		Map<String, Object> statusMap = new HashMap<String, Object>();
		statusMap.put("table", "RCM_FORMALASSESSMENT_INFO");
		statusMap.put("filed", "IS_SUBMIT_REPORT");
		statusMap.put("status", "1");
		statusMap.put("BUSINESSID", projectFormalId);
		this.fillMaterialsMapper.updateProjectStaus(statusMap);
		
		Map<String, Object> Object = this.fillMaterialsService.getRFIStatus(projectFormalId);
		if(Util.isNotEmpty(Object)) {
			if (Util.isNotEmpty(Object.get("IS_SUBMIT_BIDDING")) && Util.isNotEmpty(Object.get("IS_SUBMIT_DECISION_NOTICE"))) {
				if (Object.get("IS_SUBMIT_BIDDING").equals("1") && Object.get("IS_SUBMIT_DECISION_NOTICE").equals("1")) {
					map.put("stage", "4");
				}
			}
		}
		
//		map.put("stage", "3.7");
		map.put("decision_commit_time", null);
		map.put("businessid", projectFormalId);
		// 合并 正式评审参会信息 时，将正式品汇参会信息中的修改项合并到正式评审报告中
		map.put("need_meeting", "1");
		map.put("metting_commit_time", Util.getTime());
		
		this.formalReportMapper.changeState(map);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("submit_date", Util.format(Util.now(), "yyyy-MM-dd"));
		this.baseMongo.updateSetByObjectId(id, params, Constants.RCM_FORMALREPORT_INFO);

		Document doc = (Document) this.baseMongo.queryById(id, Constants.RCM_FORMALREPORT_INFO);

		String filepath = "", ext = "";
		if (doc != null) {
			filepath = doc.getString("filePath");
			if (filepath != null) {
				ext = filepath.substring(filepath.lastIndexOf("."));
			}
		}

		String serverPath = PropertiesUtil.getProperty("domain.allow") + PropertiesUtil.getProperty("contextPath");
		serverPath = serverPath + "/common/RcmFile/downLoad?filePath=" + filepath + "&fileName=formalReport" + ext;

		String formalId = (String) doc.get("projectFormalId");

		TzClient client = new TzClient();
		client.setBusinessId(formalId);
		client.setStatus("2");
		client.setLocation(serverPath);
		Thread t = new Thread(client);
		t.start();

	}

	@Override
	public Map<String, String> getPfrAssessmentWord(String id) {

		String path = FormalAssessmentReportUtil.generateReport(id);

		BasicDBObject queryAndWhere = new BasicDBObject();
		queryAndWhere.put("_id", new ObjectId(id));
		Document doc = (Document) this.baseMongo.queryById(id, Constants.RCM_FORMALREPORT_INFO);

		// 判断之前的报告是不是存在，如果存在，删除
		String oldpath = doc.getString("filePath");
		try {
			if (oldpath != null && oldpath.length() > 0) {
				FileUtil.removeFile(oldpath);
			}
		} catch (Exception e) {
		}

		// 修改数据前将_id转为ObjectId类型，同时 设置$set,表示要更新
		doc.put("_id", new ObjectId(id));
		doc.append("filePath", path);
		doc.append("submit_date", Util.format(Util.now(), "yyyy-MM-dd"));
		this.baseMongo.updateSetByObjectId(id, doc, Constants.RCM_FORMALREPORT_INFO);

		Map<String, String> map = new HashMap<String, String>();
		map.put("filePath", path);
		map.put("fileName", doc.getString("projectName"));

		return map;
	}

	@Override
	public Document getByID(String id) {
		BasicDBObject queryAndWhere = new BasicDBObject();
		Document doc = new Document();

		queryAndWhere.put("projectFormalId", id);
		List<Map<String, Object>> list = this.baseMongo.queryByCondition(queryAndWhere,
				Constants.RCM_FORMALREPORT_INFO);
		int listSize = list.size();

		if (listSize > 0) {
			doc = (Document) list.get(0);
		}

		if (null != doc && Util.isNotEmpty(doc)) {
			doc.put("_id", doc.get("_id").toString());

			Map<String, Object> queryById = this.baseMongo.queryById(id, Constants.RCM_FORMALASSESSMENT_INFO);
			@SuppressWarnings("unchecked")
			Map<String, Object> apply = (Map<String, Object>) queryById.get("apply");
			doc.put("serviceType", apply.get("serviceType"));
		}
		return doc;
	}

	@Override
	public boolean isPossible2Submit(String projectFormalId) {
		boolean flag = false;

		Map<String, Object> map = this.formalReportMapper.isPossible2Submit(projectFormalId);

		// if ("2".equals(map.get("WF_STATE")) &&
		// "3.5".equals(map.get("STAGE"))) {
		if ("2".equals(map.get("WF_STATE"))) {
			flag = true;
		}

		return flag;
	}

	@Override
	public Map<String, Object> selectPrjReviewView(String businessId) {

		return this.formalReportMapper.selectPrjReviewView(businessId);
	}

	@Override
	public void queryUncommittedDecisionMaterialByPage(PageAssistant page, String json) {
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

		List<Map<String, Object>> list = this.formalReportMapper.queryUncommittedDecisionMaterialByPage(params);
		page.setList(list);
	}

	@Override
	public void querySubmittedDecisionMaterialByPage(PageAssistant page, String json) {
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

		List<Map<String, Object>> list = this.formalReportMapper.querySubmittedDecisionMaterialByPage(params);
		page.setList(list);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> findFormalAndReport(String projectFormalId) {

		String applyDate = this.formalReportMapper.queryApplDate(projectFormalId);

		BasicDBObject queryAndWhere = new BasicDBObject();
		queryAndWhere.put("projectFormalId", projectFormalId);
		List<Map<String, Object>> listDoc_report = this.baseMongo.queryByCondition(queryAndWhere,
				Constants.RCM_FORMALREPORT_INFO);
		Document doc_Report = null;
		if (listDoc_report.size() > 0) {
			for (Map<String, Object> map1 : listDoc_report) {
				doc_Report = (Document) map1;
				doc_Report.put("_id", doc_Report.get("_id").toString());
			}
		}

		BasicDBObject basicDBObject = new BasicDBObject();
		basicDBObject.put("formalId", projectFormalId);
		List<Map<String, Object>> listDoc_meetingInfo = this.baseMongo.queryByCondition(basicDBObject,
				Constants.FORMAL_MEETING_INFO);
		Document doc_meetingInfo = null;
		if (listDoc_meetingInfo.size() > 0) {
			for (Map<String, Object> map : listDoc_meetingInfo) {
				doc_meetingInfo = (Document) map;
			}
		}

		Document doc_Formal = (Document) this.baseMongo.queryById(projectFormalId, Constants.RCM_FORMALASSESSMENT_INFO);
		doc_Formal.put("_id", doc_Formal.get("_id").toString());

		List<Document> attachment = (List<Document>) doc_Formal.get("attachment");// 获取附件类型
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

		Map<String, Object> map = this.formalAssessmentInfoService.getFormalAssessmentByID(projectFormalId);
		Map<String, Object> oracle = (Map<String, Object>) map.get("formalAssessmentOracle");

		Map<String, Object> param = new HashMap<String, Object>();

		// ------------------------Sam Gao 2018-12-06 Start------------------------------
		// 获得风控评审意见汇总
		BasicDBObject summaryDBObject = new BasicDBObject();
		summaryDBObject.put("projectFormalId", projectFormalId);
		List<Map<String, Object>> listDoc_summaryInfo = this.baseMongo.queryByCondition(summaryDBObject,
				Constants.RCM_FORMAL_SUMMARY);
		Document doc_summmaryInfo = null;
		if (listDoc_meetingInfo.size() > 0) {
			for (Map<String, Object> mapSummary : listDoc_summaryInfo) {
				doc_summmaryInfo = (Document) mapSummary;
			}
			param.put("summary", doc_summmaryInfo);
		}
		// ------------------------Sam Gao 2018-12-06 End------------------------------

		param.put("Report", doc_Report);
		param.put("MeetInfo", doc_meetingInfo);
		param.put("Formal", doc_Formal);
		param.put("attach", attach);
		param.put("applyDate", applyDate);
		param.put("stage", oracle.get("STAGE"));
		return param;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Result addPolicyDecision(String json, String method) {
		System.out.println("查看是提交还是暂存  method="+method);
		Result result = new Result();
		result.setResult_data(true);
		Document bjson = Document.parse(json);
		String businessId = bjson.getString("projectFormalId");
		
		Map<String, Object> statusMap = new HashMap<String, Object>();
		statusMap.put("table", "RCM_FORMALASSESSMENT_INFO");
		statusMap.put("filed", "IS_SUBMIT_BIDDING");
		statusMap.put("status", "0");
		statusMap.put("BUSINESSID", businessId);
		statusMap.put("BIDDING_TYPE", "NORMAL");
		this.fillMaterialsService.updateProjectBiddingStaus(statusMap);

		// 验证边界条件
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("businessId", businessId);
		// 获取被选中的投资部门提供的附件、项目重要边界条件的信息以及附件列表的附件信息
		Document policyDecision = (Document) bjson.get("policyDecision");
		if (Util.isNotEmpty(policyDecision)) {
			Document projectType = (Document) policyDecision.get("projectType");// 项目类型

			if (Util.isNotEmpty(projectType)) {
				Double projectSize = null;
				Double investmentAmount = null;
				Double rateOfReturn = null;

				try {
					projectSize = Double.parseDouble(policyDecision.get("projectSize").toString());
					investmentAmount = Double.parseDouble(policyDecision.get("investmentAmount").toString());
					rateOfReturn = Double.parseDouble(policyDecision.get("rateOfReturn").toString());
				} catch (Exception e) {
					result.setSuccess(false).setResult_name("投资金额、项目规模、投资收益率必须为数字！");
					return result;
				}
				params.put("serviceType", projectType.getString("ITEM_CODE"));// 项目类型
				params.put("projectSize", projectSize);// 项目规模
				params.put("investmentAmount", investmentAmount);// 投资金额
				params.put("rateOfReturn", rateOfReturn);// 投资收益率
			}
		}

		// 1、更新项目规模、投资金额、收益率、业务类型
		this.reportInfoService.updateReportByBusinessId(params);

		// 获取投资部门提供的所有附件(包含新增的附件)
		List<Document> attachments = (List<Document>) bjson.get("ac_attachment");
		bjson.remove("ac_attachment");

		// 2、添加提交时间,修改阶段
		if ("ss".equals(method)) {
			if (this.isHaveMeetingInfo(businessId)) {
				Map<String, Object> statusMap1 = new HashMap<String, Object>();
				statusMap1.put("table", "RCM_FORMALASSESSMENT_INFO");
				statusMap1.put("filed", "IS_SUBMIT_BIDDING");
				statusMap1.put("status", "1");
				statusMap1.put("BUSINESSID", businessId);
				this.fillMaterialsService.updateProjectStaus(statusMap1);
				
				Map<String, Object> map = new HashMap<String, Object>();
				Map<String, Object> Object = this.fillMaterialsService.getRFIStatus(businessId);
				if(Util.isNotEmpty(Object)) {
					if (Util.isNotEmpty(Object.get("IS_SUBMIT_REPORT")) && Util.isNotEmpty(Object.get("IS_SUBMIT_DECISION_NOTICE"))) {
						if (Object.get("IS_SUBMIT_REPORT").equals("1") && Object.get("IS_SUBMIT_BIDDING").equals("1") && Object.get("IS_SUBMIT_DECISION_NOTICE").equals("1")) {
							map.put("stage", "4");
						}
					}
				}
//				map.put("stage", "4");
				map.put("decision_commit_time", Util.format(Util.now()));
				map.put("businessid", businessId);
				this.formalReportMapper.changeState(map);
			} else {
				return result.setResult_data(false);
			}
		}
		// 3、修改mongo数据
		Document doc = (Document) this.baseMongo.queryById(bjson.getString("_id"), Constants.RCM_FORMALREPORT_INFO);
		doc.put("_id", new ObjectId(bjson.getString("_id")));
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
		doc.put("filePath", bjson.getString("filePath"));
		this.baseMongo.updateSetByObjectId(bjson.getString("_id"), doc, Constants.RCM_FORMALREPORT_INFO);

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
			Document doc_rpi = (Document) this.baseMongo.queryById(businessId, Constants.RCM_FORMALASSESSMENT_INFO);
			doc_rpi.put("attachment", attachments);
			this.baseMongo.updateSetByObjectId(businessId, doc_rpi, Constants.RCM_FORMALASSESSMENT_INFO);
		}
		
		// 修改评审意见汇总-------------Start-------------------------
	
		Document projectSummary = (Document) bjson.get("projectSummary");
		
		saveOrUpdateFormalProjectSummary(projectSummary);

		// 修改评审意见汇总-------------End-------------------------
		
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean updatePolicyDecision(String json, String method) {

		boolean flag = true;

		Document bjson = Document.parse(json);
		String businessId = bjson.getString("projectFormalId");

		if ("ss".equals(method)) {
			flag = this.isHaveMeetingInfo(businessId);
			if (flag) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("stage", "4");
				map.put("decision_commit_time", Util.now());
				map.put("businessid", businessId);
				this.formalReportMapper.changeState(map);
			} else {
				return false;
			}
		}

		// 获取被选中的投资部门提供的附件、项目重要边界条件的信息以及附件列表的附件信息
		Document policyDecision = (Document) bjson.get("policyDecision");

		// 获取投资部门提供的所有附件(包含新增的附件)
		List<Document> attachments = (List<Document>) bjson.get("ac_attachment");
		bjson.remove("ac_attachment");

		Document doc = (Document) this.baseMongo.queryById(bjson.getString("_id"), Constants.RCM_FORMALREPORT_INFO);
		doc.put("_id", new ObjectId(bjson.getString("_id")));

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
		doc.put("filePath", bjson.getString("filePath"));
		this.baseMongo.updateSetByObjectId(bjson.getString("_id"), doc, Constants.RCM_FORMALREPORT_INFO);

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
			Document doc_rpi = (Document) this.baseMongo.queryById(businessId, Constants.RCM_FORMALASSESSMENT_INFO);
			doc_rpi.put("attachment", attachments);
			this.baseMongo.updateSetByObjectId(businessId, doc_rpi, Constants.RCM_FORMALASSESSMENT_INFO);
		}

		return flag;
	}

	@Override
	public boolean isHaveMeetingInfo(String formalId) {
		boolean flag = false;

		BasicDBObject basicDBObject = new BasicDBObject("formalId", formalId);
		List<Map<String, Object>> document = this.baseMongo.queryByCondition(basicDBObject,
				Constants.FORMAL_MEETING_INFO);

		if (document.size() > 0) {
			flag = true;
		}

		return flag;
	}

	@Override
	public Map<String, Object> getOracleByBusinessId(String businessid) {
		return formalReportMapper.getByBusinessId(businessid);
	}

	@Override
	public List<Map<String, Object>> queryPfrNoticeFileList(PageAssistant page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		if (page.getParamMap() != null) {
			params.putAll(page.getParamMap());
		}
		if (Util.isNotEmpty(params.get("stage"))) {
			String stage = (String) params.get("stage");
			String bulletinStage = "";
			String pfrStage = "";
			if ("1".equals(stage)) {
				bulletinStage = "'3'";
				pfrStage = "'5'";
			} else if ("2".equals(stage)) {
				bulletinStage = "'2'";
				pfrStage = "'4'";
			} else if ("3".equals(stage)) {
				bulletinStage = "'4','5'";
				pfrStage = "'6','7','9'";
			} else if ("4".equals(stage)) {
				bulletinStage = "'1','1.5'";
				pfrStage = "'1','2','3','3.5','3.7','3.9'";
			}
			params.put("bulletinStage", bulletinStage);

			params.put("pfrStage", pfrStage);
			params.put("now", Util.format(Util.now(), "yyyy-MM-dd"));
		}

		return formalReportMapper.queryPfrNoticeFileList(params);
	}

	@Override
	public void saveReportFile(String json) {
		Document doc = Document.parse(json);
		String id = doc.getString("_id");
		String filePath = doc.getString("filePath");
		String fileName = doc.getString("fileName");

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("fileName", fileName);
		data.put("filePath", filePath);
		this.baseMongo.updateSetByObjectId(id, data, Constants.RCM_FORMALREPORT_INFO);
	}

	@Override
	public boolean saveOrUpdateFormalProjectSummary(Document summaryDoc) {
//		Document summaryDoc = Document.parse(json);
		summaryDoc.put("_id", new ObjectId(summaryDoc.getString("projectFormalId")));
		summaryDoc.put("create_by", ThreadLocalUtil.getUserId());
		summaryDoc.put("create_name", ThreadLocalUtil.getUser().get("NAME"));
		Map<String, Object> doc = this.baseMongo.queryById(summaryDoc.getString("projectFormalId"),
				Constants.RCM_FORMAL_SUMMARY);
		if (Util.isNotEmpty(doc)) {
			try {
				this.baseMongo.updateSetByObjectId(summaryDoc.getString("projectFormalId"), summaryDoc,
						Constants.RCM_FORMAL_SUMMARY);
				logger.info("更新风控评审意见汇总成功");
				return true;
			} catch (Exception e) {
				logger.info("更新风控评审意见汇总出错");
				e.printStackTrace();
				return false;
			}
		} else {
			try {
				this.baseMongo.save(summaryDoc, Constants.RCM_FORMAL_SUMMARY);
				logger.info("保存风控评审意见汇总成功");
				return true;
			} catch (Exception e) {
				logger.info("保存风控评审意见汇总出错");
				e.printStackTrace();
				return false;
			}
		}
	}

	@Override
	public Map<String, Object> findFormalProjectSummary() {
		BasicDBObject queryAndWhere = new BasicDBObject();
		queryAndWhere.put("projectFormalId", "5afcc2e6ddd03412cebef6e5");
		List<Map<String, Object>> queryById = this.baseMongo.queryByCondition(queryAndWhere,
				Constants.RCM_FORMAL_SUMMARY);
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("stage", queryById.get(1));
		return param;
	}

	@Override
	public Result addPptecision(String json, String method) {
		System.out.println("查看是提交还是暂存  method="+method);
		Result result = new Result();
		result.setResult_data(true);
		Document bjson = Document.parse(json);
		String businessId = bjson.getString("projectFormalId");
		
		Map<String, Object> statusMap = new HashMap<String, Object>();
		statusMap.put("table", "RCM_FORMALASSESSMENT_INFO");
		statusMap.put("filed", "IS_SUBMIT_BIDDING");
		statusMap.put("status", "0");
		statusMap.put("BUSINESSID", businessId);
		statusMap.put("BIDDING_TYPE", "PPT");
		this.fillMaterialsService.updateProjectBiddingStaus(statusMap);

		// 2、添加提交时间,修改阶段
		if ("ss".equals(method)) {
			if (this.isHaveMeetingInfo(businessId)) {
				Map<String, Object> statusMap1 = new HashMap<String, Object>();
				statusMap1.put("table", "RCM_FORMALASSESSMENT_INFO");
				statusMap1.put("filed", "IS_SUBMIT_BIDDING");
				statusMap1.put("status", "1");
				statusMap1.put("BUSINESSID", businessId);
				this.fillMaterialsService.updateProjectStaus(statusMap1);
				
				Map<String, Object> map = new HashMap<String, Object>();
				Map<String, Object> Object = this.fillMaterialsService.getRFIStatus(businessId);
				if(Util.isNotEmpty(Object)) {
					if (Util.isNotEmpty(Object.get("IS_SUBMIT_REPORT")) && Util.isNotEmpty(Object.get("IS_SUBMIT_DECISION_NOTICE"))) {
						if (Object.get("IS_SUBMIT_REPORT").equals("1") && Object.get("IS_SUBMIT_BIDDING").equals("1") && Object.get("IS_SUBMIT_DECISION_NOTICE").equals("1")) {
							map.put("stage", "4");
						}
					}
				}
//				map.put("stage", "4");
				map.put("decision_commit_time", Util.format(Util.now()));
				map.put("businessid", businessId);
				this.formalReportMapper.changeState(map);
			} else {
				return result.setResult_data(false);
			}
		}
		
		// 修改评审意见汇总-------------Start-------------------------
		Document projectSummary = (Document) bjson.get("projectSummary");
		saveOrUpdateFormalProjectSummary(projectSummary);
		// 修改评审意见汇总-------------End-------------------------
		
		return result;
	}

	@Override
	public Map<String, Object> findFormalPptSummary(String businessId, String type) {
		Map<String, Object> map = new HashMap<String, Object>();
		if(type.equals("pfr")) {
			Map<String, Object> project = this.formalAssessmentInfoMapper.getFormalAssessmentById(businessId);
			map.put("project", project);
		}
		Map<String, Object> summary = this.baseMongo.queryById(businessId, Constants.RCM_FORMAL_SUMMARY);
		map.put("summary", summary);
		return map;
	}

}

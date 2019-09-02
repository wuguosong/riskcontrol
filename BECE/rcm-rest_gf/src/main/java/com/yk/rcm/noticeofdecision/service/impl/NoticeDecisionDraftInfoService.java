/**
 * 
 */
package com.yk.rcm.noticeofdecision.service.impl;

import java.io.File;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.mongodb.BasicDBObject;
import com.yk.common.DocxReportUtil;
import com.yk.common.IBaseMongo;
import com.yk.exception.rcm.DocReportException;
import com.yk.power.service.IOrgService;
import com.yk.rcm.fillMaterials.service.IFillMaterialsService;
import com.yk.rcm.formalAssessment.dao.IFormalAssessmentInfoMapper;
import com.yk.rcm.formalAssessment.service.IFormalAssessmentAuditService;
import com.yk.rcm.formalAssessment.service.IFormalReportService;
import com.yk.rcm.noticeofdecision.dao.INoticeDecisionDraftInfoMapper;
import com.yk.rcm.noticeofdecision.dao.INoticeDecisionRoleMapper;
import com.yk.rcm.noticeofdecision.service.INoticeDecisionDraftInfoService;
import com.yk.reportData.service.IReportDataService;

import common.Constants;
import common.PageAssistant;
import report.Path;
import util.ThreadLocalUtil;
import util.Util;

/**
 * @author 80845530
 *
 */
@Service
@Transactional
public class NoticeDecisionDraftInfoService implements INoticeDecisionDraftInfoService {
	@Resource
	private IBaseMongo baseMongo;
	@Resource
	private INoticeDecisionRoleMapper roleMapper;// need update
	@Resource
	private INoticeDecisionDraftInfoMapper noticeDecisionDraftInfoMapper;
	@Resource
	private IFormalAssessmentInfoMapper formalAssessmentInfoMapper;
	@Resource
	private IFormalAssessmentAuditService formalAssessmentAuditService;
	@Resource
	private IFormalReportService formalReportService;
	@Resource
	private IOrgService orgService;
	@Resource
	private IFillMaterialsService fillMaterialsService;
	@Resource
	private IReportDataService reportDataService;

	private Logger logger = LoggerFactory.getLogger(NoticeDecisionDraftInfoService.class);

	@Override
	public PageAssistant queryStartByPage(PageAssistant page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		if (!ThreadLocalUtil.getIsAdmin()) {
			// 管理员能看所有的
			params.put("createBy", ThreadLocalUtil.getUserId());
		}
		if (page.getParamMap() != null) {
			params.putAll(page.getParamMap());
		}
		String orderBy = page.getOrderBy();
		if (orderBy == null) {
			orderBy = " notice_create_time desc ";
		}
		params.put("orderBy", orderBy);
		List<Map<String, Object>> list = this.noticeDecisionDraftInfoMapper.queryStartByPage(params);
		page.setList(list);
		return page;
	}

	@Override
	public PageAssistant queryOverByPage(PageAssistant page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		if (!ThreadLocalUtil.getIsAdmin()) {
			// 管理员能看所有的
			params.put("createBy", ThreadLocalUtil.getUserId());
		}
		if (page.getParamMap() != null) {
			params.putAll(page.getParamMap());
		}
		/*
		 * String orderBy = page.getOrderBy(); if(orderBy == null){ orderBy =
		 * " notice_create_time desc "; } params.put("orderBy", orderBy);
		 */
		List<Map<String, Object>> list = this.noticeDecisionDraftInfoMapper.queryOverByPage(params);
		page.setList(list);
		return page;
	}

	@Override
	public String update(String json) {
		Document doc = Document.parse(json);
		// 打包oracle数据,修改oracle
		Map<String, Object> paramsForOracle = packageNoticeDecisionInfoForOracle(doc);
		this.noticeDecisionDraftInfoMapper.update(paramsForOracle);
		// 修改mongoDB
		String id = doc.getString("_id");
		if (Util.isEmpty(id)) {
			doc.put("_id", new ObjectId((String) paramsForOracle.get("businessId")));
		} else {
			doc.put("_id", new ObjectId(id));
		}

		// 拼接三个要求
		StringBuffer implRequ = new StringBuffer();
		if (Util.isNotEmpty(doc.get("require1"))) {
			implRequ.append("\t").append("纳入收益考核要求").append("\n");
			implRequ.append(doc.get("require1")).append("\n");
		}
		if (Util.isNotEmpty(doc.get("require2"))) {
			implRequ.append("\t").append("单独考核要求").append("\n");
			implRequ.append(doc.get("require2")).append("\n");
		}
		if (Util.isNotEmpty(doc.get("require3"))) {
			implRequ.append("\t").append("建议及挖潜").append("\n");
			implRequ.append(doc.get("require3"));
		}
		doc.put("implementationRequirements", implRequ.toString());

		if (Util.isEmpty(id)) {
			id = paramsForOracle.get("businessId").toString();
			baseMongo.save(doc, Constants.RCM_NOTICEDECISION_INFO);
		} else {
			baseMongo.updateSetByObjectId(id, doc, Constants.RCM_NOTICEDECISION_INFO);
		}
		return id;
	}

	@Override
	public void deleteByFormalIds(String idsStr) {
		String[] ids = idsStr.split(",");
		// 删oracle
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("formalIds", ids);
		this.noticeDecisionDraftInfoMapper.deleteByFormalIds(params);
		// 删除mongo
		BasicDBObject filter = new BasicDBObject();
		BasicDBObject condition = new BasicDBObject();
		condition.put("$in", Arrays.asList(ids));
		filter.put("projectFormalId", condition);
		this.baseMongo.deleteByCondition(filter, Constants.RCM_NOTICEDECISION_INFO);

	}

	@Override
	public String create(String json) {
		// 添加信息
		Document doc = Document.parse(json);
		Map<String, Object> paramsForOracle = this.packageNoticeDecisionInfoForOracle(doc);
		// 处理mongo的_id
		ObjectId objectId = new ObjectId((String) paramsForOracle.get("businessId"));
		doc.put("_id", objectId);
		// 梳理时间戳
		// Date object = (Date) paramsForOracle.get("create_date");
		Format format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		doc.append("currentTimeStamp", format.format(new Date()));
		// 处理创建时间
		doc.put("create_date", paramsForOracle.get("create_date"));

		// 将前台传过来的数据添加到oracle rcm_noticeDecision_info
		this.noticeDecisionDraftInfoMapper.save(paramsForOracle);
//		Map<String, Object> statusMap = new HashMap<String, Object>();
//		statusMap.put("table", "RCM_FORMALASSESSMENT_INFO");
//		statusMap.put("filed", "IS_SUBMIT_DECISION_NOTICE");
//		statusMap.put("status", "0");
//		statusMap.put("BUSINESSID", paramsForOracle.get("projectFormalid").toString());
//		this.fillMaterialsService.updateProjectStaus(statusMap);

		/*
		 * //修改oracle的stage状态 Map<String, Object> map = new HashMap<String,
		 * Object>(); map.put("stage", "3.9"); map.put("businessId",
		 * doc.getString("projectFormalId").toString());
		 * this.noticeDecisionDraftInfoMapper.updateFormalAssessmentStage(map);
		 */

		// 添加mongo数据库rcm_noticeDecision_info
		doc.remove("oracle");

		// 拼接三个要求
		StringBuffer implRequ = new StringBuffer();
		if (Util.isNotEmpty(doc.get("require1"))) {
			implRequ.append("\t").append("纳入收益考核要求").append("\n");
			implRequ.append(doc.get("require1")).append("\n");
		}
		if (Util.isNotEmpty(doc.get("require2"))) {
			implRequ.append("\t").append("单独考核要求").append("\n");
			implRequ.append(doc.get("require2")).append("\n");
		}
		if (Util.isNotEmpty(doc.get("require3"))) {
			implRequ.append("\t").append("建议及挖潜").append("\n");
			implRequ.append(doc.get("require3"));
		}
		doc.put("implementationRequirements", implRequ.toString());

		baseMongo.save(doc, Constants.RCM_NOTICEDECISION_INFO);
		return objectId.toString();
	}

	/**
	 * 将doc数据封装到map中
	 * 
	 * @param doc
	 * @return map
	 */
	public Map<String, Object> packageNoticeDecisionInfoForOracle(Document doc) {
		String now = Util.getTime();
		Map<String, Object> paramsForOracle = new HashMap<String, Object>();

		if (null == doc.getString("_id") || "".equals(doc.getString("_id"))) {
			ObjectId _id = new ObjectId();
			// 业务ID
			paramsForOracle.put("businessId", _id.toString());
		} else {
			paramsForOracle.put("businessId", doc.getString("_id"));
		}

		// 合同规模
		String contractScale = doc.getString("contractScale");
		paramsForOracle.put("contractScale", contractScale);
		// 评审规模
		String evaluationScale = doc.getString("evaluationScale");
		paramsForOracle.put("evaluationScale", evaluationScale);
		// 评审总投资
		String reviewOfTotalInvestment = doc.getString("reviewOfTotalInvestment");
		paramsForOracle.put("reviewOfTotalInvestment", reviewOfTotalInvestment);
		/*
		 * //投资决策会议期次 String decisionStage = doc.getString("decisionStage");
		 * paramsForOracle.put("decisionStage", decisionStage);
		 */
		// 责任单位 ID
		Document responsibilityUnit = doc.get("responsibilityUnit", Document.class);
		if (responsibilityUnit != null) {
			String responsibilityUnitValue = responsibilityUnit.getString("value");
			paramsForOracle.put("responsibilityUnitValue", responsibilityUnitValue);
		}
		// 创建人id
		Document createBy = doc.get("createBy", Document.class);
		String createById = new String();
		if (Util.isEmpty(createBy)) {
			createById = ThreadLocalUtil.getUserId();
		} else {
			createById = createBy.getString("value");
		}

		paramsForOracle.put("createBy", createById);

		// 通知书状态
		String wf_State = "0";
		paramsForOracle.put("wf_State", wf_State);
		// 申请时间
		String appyly_date = now;
		paramsForOracle.put("appyly_date", appyly_date);
		// 创建时间
		String create_date = now;
		paramsForOracle.put("create_date", create_date);
		// 最近修改时间
		String last_update_date = now;
		paramsForOracle.put("last_update_date", last_update_date);
		// 项目id
		String projectFormalid = doc.getString("projectFormalId");
		paramsForOracle.put("projectFormalid", projectFormalid);
		// 申报项目ID
		Document reportingUnit = doc.get("reportingUnit", Document.class);
		if (Util.isEmpty(reportingUnit)) {
			// createById = ThreadLocalUtil.getUserId();
		} else {
			String reportingUnitId = reportingUnit.getString("value");
			paramsForOracle.put("reportingUnitId", reportingUnitId);
		}
		// 会议时间
		String dateOfMeeting = doc.getString("dateOfMeeting");
		paramsForOracle.put("dateOfMeeting", dateOfMeeting);
		// 项目投资决策意见
		String consentToInvestment = doc.getString("consentToInvestment");
		paramsForOracle.put("consentToInvestment", consentToInvestment);

		return paramsForOracle;
	}

	@Override
	public Map<String, Object> queryNoticeDecstionByFormalId(String formalId) {
		/*
		 * BasicDBObject queryAndWhere =new BasicDBObject();
		 * queryAndWhere.put("_id", new ObjectId(id)); Map<String, Object> doc =
		 * baseMongo.queryById(id, DocumentName); Map<String,Object> oracle =
		 * noticeDecisionDraftInfoMapper.queryById(id); doc.put("_id",
		 * doc.get("_id").toString()); doc.put("oracle", oracle);
		 */
		BasicDBObject queryAndWhere = new BasicDBObject();
		queryAndWhere.put("projectFormalId", formalId);
		List<Map<String, Object>> queryByCondition = baseMongo.queryByCondition(queryAndWhere,
				Constants.RCM_NOTICEDECISION_INFO);
		if (queryByCondition.size() > 0) {
			Map<String, Object> doc = queryByCondition.get(0);
			Map<String, Object> oracle = noticeDecisionDraftInfoMapper.queryById(doc.get("_id").toString());
			doc.put("_id", doc.get("_id").toString());
			doc.put("oracle", oracle);
			return doc;
		}
		return null;
	}

	@Override
	public List<Map<String, Object>> queryFormalForCreate() {
		Map<String, Object> params = new HashMap<String, Object>();
		if (!ThreadLocalUtil.getIsAdmin()) {
			// 管理员能看所有的
			params.put("userId", ThreadLocalUtil.getUserId());
		}
		return this.noticeDecisionDraftInfoMapper.queryFormalForCreate(params);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, String> getNoticeOfDecisionWord(String businessId) {
		BasicDBObject queryAndWhere = new BasicDBObject();
		queryAndWhere.put("projectFormalId", businessId);
		Map<String, Object> data = baseMongo.queryByCondition(queryAndWhere, Constants.RCM_NOTICEDECISION_INFO).get(0);
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("decisionNo", String.format("JCTZ－%tY-%s", new Date(),
				Util.isEmpty(data.get("decisionNo")) ? "" : data.get("decisionNo")));
		param.put("decisionDate", Util.format(new Date(), "yyyy-MM-dd"));
		param.put("projectName", data.get("projectName"));

		// 评审规模(传统水务或水环境任意输入,反之即数字+万吨/日)
		/*
		 * Map<String, Object> formalReportParam =
		 * formalReportService.getOracleByBusinessId(businessId);
		 */
		// 由于调整录入顺序，因此此处报告可能没有值会造成空指针错误，因此此处取正式评审主数据中的业务类型值
		Map<String, Object> formalReportParam = formalAssessmentInfoMapper.getFormalAssessmentById(businessId);
		String serviceType = (String) formalReportParam.get("SERVICETYPE");
		if ("1401".equals(serviceType) || "1402".equals(serviceType)) {
			param.put("contScale", data.get("contractScale"));
			param.put("evalScale", data.get("evaluationScale"));
		} else {
			param.put("contScale", data.get("contractScale").toString().concat("万吨/日"));
			param.put("evalScale", data.get("evaluationScale").toString().concat("万吨/日"));
		}

		// 评审总投资
		param.put("reviTotaInve", data.get("reviewOfTotalInvestment"));
		// 评审总投资-类型
		boolean reviOfTotaInveTypeIsNull = null == data.get("reviOfTotaInveType")
				|| "".equals(data.get("reviOfTotaInveType"));
		param.put("reviOfTotaInveType",
				reviOfTotaInveTypeIsNull ? "" : "(".concat(data.get("reviOfTotaInveType").toString()).concat(")"));

		// 申报单位
		String reportingUnit = null;
		Document reportingUnitMap = (Document) data.get("pertainArea");
		/*
		 * if(Util.isNotEmpty(reportingUnitMap) &&
		 * Util.isNotEmpty(reportingUnitMap.get("NAME"))){ reportingUnit =
		 * reportingUnitMap.get("NAME").toString();
		 */
		// 根据MongoDB存值修改取值逻辑
		if (Util.isNotEmpty(reportingUnitMap) && Util.isNotEmpty(reportingUnitMap.get("VALUE"))) {
			reportingUnit = reportingUnitMap.get("VALUE").toString();
		} else {
			// 旧数据没有大区,则使用原来的申报单位
			reportingUnitMap = (Document) data.get("reportingUnit");
			reportingUnit = reportingUnitMap.get("VALUE").toString();
		}
		// 根据申报单位查大区
		Map<String, Object> queryPertainArea = orgService.queryPertainArea(reportingUnit);
		if (Util.isNotEmpty(queryPertainArea)) {
			reportingUnit = (String) queryPertainArea.get("NAME");
		}
		param.put("reportingUnit", reportingUnit);

		// 原有项目再投资或已投资项目补充评审
		String additionalReview = "";
		if ("1".equals(data.get("additionalReview"))) {
			additionalReview = "是";
		} else if ("2".equals(data.get("additionalReview"))) {
			additionalReview = "否";
		}
		param.put("additionalReview", additionalReview);

		// 投资决策会议期次
		param.put("decisionStage", data.get("decisionStage"));

		// 会议日期
		Object dateOfMeeting = data.get("dateOfMeeting");
		if (Util.isNotEmpty(dateOfMeeting)) {
			try {
				dateOfMeeting = Util.format(Util.parse(dateOfMeeting.toString(), "yyyy-MM-dd"), "yyyy年M月d日");
			} catch (Exception e) {
				throw new DocReportException("会议日期转换失败，详情请查看日志！", e);
			}
		}
		param.put("dateOfMeeting", dateOfMeeting);

		// 项目投资决策意见
		String consentToInvestment = "";
		if ("1".equals(data.get("consentToInvestment"))) {
			consentToInvestment = "同意投资";
		} else if ("2".equals(data.get("consentToInvestment"))) {
			consentToInvestment = "不同意投资";
		} else if ("3".equals(data.get("consentToInvestment"))) {
			consentToInvestment = "同意有条件投资";
		} else if ("4".equals(data.get("consentToInvestment"))) {
			consentToInvestment = "择期决议";
		}
		param.put("consentToInvestment", consentToInvestment);

		// 项目投资前须落实事项
		Object implementationMatters = data.get("implementationMatters");
		String implMattStyle = "left";
		String implMatt = "";
		if (null == implementationMatters || StringUtils.isEmpty(implementationMatters.toString())
				|| "无".equals(implementationMatters.toString().trim())) {
			implMattStyle = "center";
			implMatt = "无";
		} else {
			implMatt = implementationMatters.toString();
		}
		param.put("implMatt", implMatt);
		param.put("implMattStyle", implMattStyle);

		// 项目实施主体
		Document subjectOfImplementation = (Document) data.get("subjectOfImplementation");
		if (null != subjectOfImplementation) {
			param.put("subjOfImpl", subjectOfImplementation.get("name"));
		} else {
			param.put("subjOfImpl", null);
		}

		// 北控水务股权比例
		param.put("equityRatio", data.get("equityRatio"));

		// 是否新注册项目公司
		String register = "";
		if ("1".equals(data.get("register"))) {
			register = "是";
		} else if ("2".equals(data.get("register"))) {
			register = "否";
		}
		param.put("register", register);

		// （新增）注册资本金
		param.put("regiCapi", data.get("registeredCapital"));

		// 责任单位,然后查询责任单位的所属大区
		Document responsibilityUnit = (Document) data.get("responsibilityUnit");
		if (Util.isNotEmpty(responsibilityUnit)) {
			String respUnit = (String) responsibilityUnit.get("value");
			queryPertainArea = orgService.queryPertainArea(respUnit);
			if (Util.isNotEmpty(queryPertainArea)) {
				param.put("respUnit", queryPertainArea.get("NAME"));
			} else {
				param.put("respUnit", responsibilityUnit.get("name"));
			}
		} else {
			param.put("respUnit", null);
		}

		// 责任人
		if (Util.isNotEmpty(data.get("personLiable"))) {
			List<Document> personLiable = (List<Document>) data.get("personLiable");
			StringBuffer perNames = new StringBuffer();
			for (int i = 0; i < personLiable.size(); i++) {
				Document person = personLiable.get(i);
				if (person != null && person.get("name") != null && !"".equals(person.get("name"))) {
					perNames.append(person.get("name")).append(",");
				}
			}
			String perNameStr = "";
			if (perNames.length() > 0) {
				perNameStr = perNames.substring(0, perNames.length() - 1);
			}
			param.put("personLiable", perNameStr);
		} else {
			param.put("personLiable", null);
		}
		// 主管领导签发
		param.put("leadershipIssue", data.get("leadershipIssue"));

		String outpath = Path.noticeOfDecisionReportPath();
		String projectName = (String) data.get("projectName");
		if (projectName != null && projectName.length() > 15) {
			projectName = projectName.substring(0, 14);
		}
		String filename = projectName + "-决策通知书申请单.docx";
		outpath = outpath + filename;

		BasicDBObject aa = new BasicDBObject();
		aa.put("projectFormalId", businessId);
		Map<String, Object> data2 = baseMongo.queryByCondition(queryAndWhere, Constants.RCM_NOTICEDECISION_INFO).get(0);
		String oldPath = (String) data2.get("reportFilePath");

		if (oldPath != null && !"".equals(oldPath)) {
			File file = new File((String) oldPath);
			if (file.exists()) {
				file.delete();
			}
		}
		if (Util.isNotEmpty(data.get("require2"))) {
			param.put("require2", data.get("require2"));
		}
		if (Util.isNotEmpty(data.get("require3"))) {
			param.put("require3", data.get("require3"));
		}
		if (Util.isNotEmpty(data.get("require1"))) {
			param.put("require1Name", "纳入收益考核要求");
			param.put("require1", data.get("require1"));
		} else {
			// 因为格式原因，任务1如果没有，就用后面的接上。
			if (Util.isNotEmpty(data.get("require2"))) {
				param.put("require1Name", "单独考核要求");
				param.put("require1", data.get("require2"));
				param.remove("require2");
			} else if (Util.isNotEmpty(data.get("require3"))) {
				param.put("require1Name", "建议及挖潜");
				param.put("require1", data.get("require3"));
				param.remove("require3");
			} else {
				// 如果没有要求,则显示 居中显示无!
				param.put("require0", "无");
			}
		}

		DocxReportUtil.generateWord(param, Path.NoticeOfDecision_XML, Path.NoticeOfDecision_DOCX, outpath);

		// 修改数据前将_id转为ObjectId类型，同时 设置$set,表示要更新
		BasicDBObject newDoc = new BasicDBObject();
		newDoc.put("reportFilePath", outpath);
		baseMongo.updateSetByFilter(queryAndWhere, newDoc, Constants.RCM_NOTICEDECISION_INFO);
		Map<String, String> map = new HashMap<String, String>();
		map.put("filePath", outpath);
		map.put("fileName", filename);
		return map;
	}

	@Override
	public Map<String, Object> querySaveDefaultInfo(String businessId) {
		Map<String, Object> doc = baseMongo.queryById(businessId, Constants.RCM_FORMALASSESSMENT_INFO);
		doc.put("_id", doc.get("_id").toString());
		return doc;
	}

	@Override
	public Map<String, Object> queryNoticeDecstionById(String id) {
		Map<String, Object> doc = baseMongo.queryById(id, Constants.RCM_NOTICEDECISION_INFO);
		Map<String, Object> oracle = noticeDecisionDraftInfoMapper.queryById(doc.get("id").toString());
		doc.put("_id", doc.get("id"));
		doc.put("oracle", oracle);
		return doc;
	}

	@Override
	public void updateStageByBusinessId(String businessId, String stage) {
		Map<String, Object> map = new HashMap<String, Object>();
		// map.put("stage", "3.9");
		map.put("stage", "3");
		map.put("notice_draft_time", Util.getTime());
		map.put("businessId", businessId);
		// 更改提交状态
		Map<String, Object> statusMap = new HashMap<String, Object>();
		statusMap.put("table", "RCM_FORMALASSESSMENT_INFO");
		statusMap.put("filed", "IS_SUBMIT_DECISION_NOTICE");
		statusMap.put("status", "1");
		statusMap.put("BUSINESSID", businessId);
		this.fillMaterialsService.updateProjectStaus(statusMap);
		this.noticeDecisionDraftInfoMapper.updateStageByBusinessId(map);
	}

	@Override
	public PageAssistant notMeetingList(PageAssistant page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		if (!ThreadLocalUtil.getIsAdmin()) {
			// 管理员能看所有的
			params.put("createBy", ThreadLocalUtil.getUserId());
		}
		if (page.getParamMap() != null) {
			params.putAll(page.getParamMap());
		}
		String orderBy = page.getOrderBy();
		if (orderBy == null) {
			orderBy = " notice_create_time desc ";
		}
		params.put("orderBy", orderBy);
		List<Map<String, Object>> list = this.noticeDecisionDraftInfoMapper.notMeetingList(params);
		page.setList(list);
		return page;
	}

	@Override
	public void saveDecisionFile(String json) {
		Document doc = Document.parse(json);
		String id = doc.getString("_id");
		String fileName = doc.getString("fileName");
		String filePath = doc.getString("filePath");
		List<Map<String, Object>> attachment = new ArrayList<Map<String, Object>>();

		Map<String, Object> file = new HashMap<String, Object>();
		file.put("fileName", fileName);
		file.put("filePath", filePath);

		attachment.add(file);

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("attachment", attachment);
		this.baseMongo.updateSetByObjectId(id, data, Constants.RCM_NOTICEDECISION_INFO);
	}

}

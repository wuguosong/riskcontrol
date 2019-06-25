package com.yk.rcm.newFormalAssessment.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.mongodb.BasicDBObject;
import com.yk.common.IBaseMongo;
import com.yk.power.dao.IRoleMapper;
import com.yk.rcm.file.service.IFileService;
import com.yk.rcm.fillMaterials.service.IFillMaterialsService;
import com.yk.rcm.formalAssessment.dao.IFormalReportMapper;
import com.yk.rcm.newFormalAssessment.dao.IFormalAssessmentInfoCreateMapper;
import com.yk.rcm.newFormalAssessment.service.IFormalAssessmentInfoCreateService;
import com.yk.rcm.noticeofdecision.dao.INoticeDecisionDraftInfoMapper;
import com.yk.reportData.service.IReportDataService;

import common.Constants;
import common.PageAssistant;
import common.Result;
import common.commonMethod;
import util.ThreadLocalUtil;
import util.Util;

@Service
@Transactional
public class FormalAssessmentInfoCreateServiceImpl implements IFormalAssessmentInfoCreateService {

	@Resource
	private IFormalAssessmentInfoCreateMapper formalAssessmentInfoCreateMapper;
	@Resource
	private IRoleMapper roleMapper;
	@Resource
	private IBaseMongo baseMongo;
	@Resource
	private IFileService fileService;
	@Resource
	private IFillMaterialsService fillMaterialsService;
	@Resource
	private IFormalReportMapper formalReportMapper;
	@Resource
	private INoticeDecisionDraftInfoMapper noticeDecisionDraftInfoMapper;
	@Resource
	private IReportDataService reportDataService;

	private Logger logger = LoggerFactory.getLogger(FormalAssessmentInfoCreateServiceImpl.class);

	@Override
	public String createProject(String json) {
		Document doc = Document.parse(json);
		ObjectId businessid = new ObjectId();
		doc.put("_id", businessid);
		Map<String, Object> dataForOracle = this.packageDataForOracle(doc);
		this.formalAssessmentInfoCreateMapper.insert(dataForOracle);
		this.baseMongo.save(doc, Constants.RCM_FORMALASSESSMENT_INFO);
		this.saveDefaultProRole(doc);
		return (String) dataForOracle.get("businessid");
	}

	/**
	 * 新建项目时创建默认项目角色 author Sunny Qi 2019-03-14
	 */
	private void saveDefaultProRole(Document doc) {
		HashMap<String, Object> params = new HashMap<String, Object>();

		HashMap<String, Object> params1 = new HashMap<String, Object>();
		Document apply = (Document) doc.get("apply");
		Document pertainArea = (Document) apply.get("pertainArea");
		String pertainAreaId = pertainArea.getString("KEY");
		params1.put("orgId", pertainAreaId);
		List<Map<String, Object>> roleIds = roleMapper.queryRoleIdByOrgId(params1);

		for (int i = 0; i < roleIds.size(); i++) {
			params.put("id", Util.getUUID());
			params.put("businessId", doc.get("_id").toString());
			params.put("roleId", roleIds.get(i).get("ROLE_ID"));
			Document createby = (Document) apply.get("investmentManager");
			params.put("createBy", createby.getString("VALUE"));
			params.put("create_date", doc.get("create_date"));
			params.put("projectType", "pfr");

			this.roleMapper.insertProRole(params);
		}
	}

	@Override
	public void updateProject(String json) {
		Document doc = Document.parse(json);
		Map<String, Object> dataForOracle = this.packageDataForOracle(doc);
		this.formalAssessmentInfoCreateMapper.update(dataForOracle);
		Document data = new Document();
		data.put("apply", doc.get("apply"));
		data.put("last_update_date", doc.get("last_update_date"));
		baseMongo.updateSetByObjectId(doc.get("_id").toString(), data, Constants.RCM_FORMALASSESSMENT_INFO);
	}

	@Override
	public void deleteProject(String ids) {
		String[] id = ids.split(",");
		for (int i = 0; i < id.length; i++) {
			String objId = id[i].replace("\"", "");
			baseMongo.deleteById(objId, Constants.RCM_FORMALASSESSMENT_INFO);
			this.formalAssessmentInfoCreateMapper.delete(objId);
		}
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> packageDataForOracle(Document doc) {
		HashMap<String, Object> params = new HashMap<String, Object>();

		params.put("businessid", doc.get("_id").toString());

		Document apply = (Document) doc.get("apply");
		params.put("projectName", apply.getString("projectName"));
		params.put("projectNum", apply.getString("projectNo"));

		System.out.println(apply.get("serviceType"));
		List<Document> serviceType = (List<Document>) apply.get("serviceType");
		if (Util.isNotEmpty(serviceType) && serviceType.size() > 0) {
			String serviceType_id = "";
			for (Document st : serviceType) {
				serviceType_id += "," + st.getString("KEY");
			}
			serviceType_id = serviceType_id.substring(1);
			params.put("serviceType_id", serviceType_id);
		}
		System.out.println(apply.get("investmentModel").toString());
		params.put("investment_model", apply.get("investmentModel").toString());

		List<Document> projectModel = (List<Document>) apply.get("projectModel");

		if (Util.isNotEmpty(projectModel) && projectModel.size() > 0) {
			String projectModel_ids = "";
			for (Document pm : projectModel) {
				projectModel_ids += "," + pm.getString("KEY");
			}
			projectModel_ids = projectModel_ids.substring(1);
			params.put("project_model_ids", projectModel_ids);
		}

		params.put("need_meeting", null);

		params.put("meeting_date", null);

		params.put("is_supplement_review", doc.get("is_supplement_review"));

		params.put("emergencyLevel", null);

		params.put("isUrgent", null);

		Document reportingUnit = (Document) apply.get("reportingUnit");
		params.put("reportingUnitId", reportingUnit.getString("KEY"));

		Document pertainArea = (Document) apply.get("pertainArea");
		params.put("pertainAreaId", pertainArea.getString("KEY"));

		params.put("isTZ", "0");

		params.put("wf_state", "0");

		params.put("apply_date", null);

		params.put("complete_date", null);

		params.put("report_create_date", null);

		params.put("projectSize", apply.getString("projectSize"));

		params.put("investMoney", apply.getString("investMoney"));

		params.put("rateOfReturn", null);

		params.put("auditStage", "1");

		params.put("oldData", "0");

		// 基层法务评审人ID
		Document grassrootsLegalStaff = (Document) apply.get("grassrootsLegalStaff");
		if (grassrootsLegalStaff != null) {
			params.put("grassrootslegalpersonId", grassrootsLegalStaff.getString("VALUE"));
		}
		// 大区负责人ID
		Map<String, Object> companyHeader = (Map<String, Object>) apply.get("companyHeader");
		String companyHeaderValue = (String) companyHeader.get("VALUE");
		params.put("largeAreaPersonId", companyHeaderValue);

		// 区域负责人——投资中心/水环境
		Map<String, Object> investmentPerson = (Map<String, Object>) apply.get("investmentPerson");
		if (investmentPerson != null) {
			String serviceTypeValue = (String) investmentPerson.get("VALUE");
			params.put("serviceTypePersonId", serviceTypeValue);
		}

		String createby = apply.get("createby").toString();
		params.put("createBy", createby);

		// 创建时间与mongDB 一致
		params.put("create_date", doc.get("create_date"));
		// 修改时间与mongDB 一致
		params.put("last_update_date", doc.get("last_update_date"));
		return params;
	}

	@Override
	public PageAssistant getNewProjectList(PageAssistant page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);

		if (page.getParamMap() != null) {
			params.putAll(page.getParamMap());
		}

		List<Map<String, Object>> list = this.formalAssessmentInfoCreateMapper.getNewProjectList(params);
		// 循环分页的list，取map的每一个对象，用id从mongo中查询数据，放到分页的list中
		for (Map<String, Object> map : list) {
			String id = (String) map.get("BUSINESSID");
			Map<String, Object> mongoDate = baseMongo.queryById(id, Constants.RCM_FORMALASSESSMENT_INFO);
			map.put("mongoDate", mongoDate);
		}
		page.setList(list);
		return page;
	}

	@Override
	public Map<String, Object> getProjectByID(String id) {
		HashMap<String, Object> result = new HashMap<String, Object>();
		Document mongoData = null;
		Map<String, Object> queryMongoById = this.baseMongo.queryById(id, Constants.RCM_FORMALASSESSMENT_INFO);
		mongoData = (Document) queryMongoById;
		mongoData.put("_id", mongoData.get("_id").toString());
		result.put("mongoData", mongoData);

		Map<String, Object> queryOracleById = this.formalAssessmentInfoCreateMapper.getProjectByID(id);
		result.put("oracleDate", queryOracleById);

		return result;
	}

	@SuppressWarnings({ "unchecked", "null" })
	@Override
	public void addNewAttachment(String json) {

		Document doc = Document.parse(json);

		String businessId = (String) doc.get("businessId");
		String oldFileName = (String) doc.get("oldFileName");
		Document item = (Document) doc.get("item");
		String fileId = (String) item.get("fileId");
		Document type = (Document) item.get("type");
		String fileName = (String) item.get("fileName");
		// 申请人信息
		Document programmed = (Document) item.get("programmed");
		// 审核人信息
		Document approved = (Document) item.get("approved");
		// 最後提交人信息
		Document lastUpdateBy = (Document) item.get("lastUpdateBy");
		String lastUpdateData = (String) item.get("lastUpdateData");

		Map<String, Object> queryById = baseMongo.queryById(businessId, Constants.RCM_FORMALASSESSMENT_INFO);

		List<Map<String, Object>> attachmentList = (List<Map<String, Object>>) queryById.get("attachmentList");

		if (attachmentList == null) {
			attachmentList = new ArrayList<Map<String, Object>>();
			Map<String, Object> file = new HashMap<String, Object>();
			file.put("fileId", fileId);
			file.put("fileName", fileName);
			file.put("oldFileName", oldFileName);
			file.put("type", type);
			file.put("programmed", programmed);
			file.put("approved", approved);
			file.put("lastUpdateBy", lastUpdateBy);
			file.put("lastUpdateData", lastUpdateData);
			file.put("isMettingAttachment", "0");
			attachmentList.add(file);
		} else {
			Map<String, Object> file = new HashMap<String, Object>();
			file.put("fileId", fileId);
			file.put("fileName", fileName);
			file.put("oldFileName", oldFileName);
			file.put("type", type);
			file.put("programmed", programmed);
			file.put("approved", approved);
			file.put("lastUpdateBy", lastUpdateBy);
			file.put("lastUpdateData", lastUpdateData);
			file.put("isMettingAttachment", "0");
			attachmentList.add(file);
		}

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("attachmentList", attachmentList);
		baseMongo.updateSetByObjectId(businessId, data, Constants.RCM_FORMALASSESSMENT_INFO);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void deleteAttachment(String json) {
		Document doc = Document.parse(json);
		String businessId = (String) doc.get("businessId");
		String fileId = doc.get("fileId") + "";
		Map<String, Object> queryById = baseMongo.queryById(businessId, Constants.RCM_FORMALASSESSMENT_INFO);

		List<Map<String, Object>> attachmentList = (List<Map<String, Object>>) queryById.get("attachmentList");
		System.out.println("fileId ====" + fileId);
		for (int i = 0; i < attachmentList.size(); i++) {
			Document attachment = (Document) attachmentList.get(i);
			if (attachment.get("fileId").equals(fileId)) {
				attachmentList.remove(i);
				break;
			}
		}
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("attachmentList", attachmentList);
		baseMongo.updateSetByObjectId(businessId, data, Constants.RCM_FORMALASSESSMENT_INFO);
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public String changeMeetingAttach(String json) {

		Document doc = Document.parse(json);

		String flag = "";

		String businessId = (String) doc.get("businessId");
		String fileId = doc.get("fileId").toString();

		Map<String, Object> queryById = baseMongo.queryById(businessId, Constants.RCM_FORMALASSESSMENT_INFO);

		List<Map<String, Object>> attachmentList = (List<Map<String, Object>>) queryById.get("attachmentList");

		for (int i = 0; i < attachmentList.size(); i++) {
			Document attachment = (Document) attachmentList.get(i);
			if (attachment.get("fileId").toString().equals(fileId)) {
				if (attachment.get("isMettingAttachment").toString().equals("1")) {
					attachment.put("isMettingAttachment", "0");
					attachmentList.set(i, attachment);
					flag = "0";
				} else {
					attachment.put("isMettingAttachment", "1");
					attachmentList.set(i, attachment);
					flag = "1";
				}
				break;
			}
		}

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("attachmentList", attachmentList);
		baseMongo.updateSetByObjectId(businessId, data, Constants.RCM_FORMALASSESSMENT_INFO);
		return flag;
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public List<Map> checkAttachment(String json) {

		Document doc = Document.parse(json);

		List<Map> list = new ArrayList<Map>();

		String businessId = (String) doc.get("businessId");

		Map<String, Object> queryById = baseMongo.queryById(businessId, Constants.RCM_FORMALASSESSMENT_INFO);

		List<Map<String, Object>> attachmentList = (List<Map<String, Object>>) queryById.get("attachmentList");
		commonMethod common = new commonMethod();
		List<Map> attachmentTypeList = common.getAttachmentType(json);

		if (Util.isNotEmpty(attachmentTypeList)) {
			if (Util.isNotEmpty(attachmentList)) {
				for (int i = 0; i < attachmentTypeList.size(); i++) {
					// 其他文件不是必填项，去掉
					if (!Constants.FILE_CODE_OTHER.equals(attachmentTypeList.get(i).get("ITEM_CODE"))) {
						int count = 0;
						for (int j = 0; j < attachmentList.size(); j++) {
							Document type = (Document) attachmentList.get(j).get("type");
							if (attachmentTypeList.get(i).get("ITEM_CODE").equals(type.get("ITEM_CODE"))) {
								count++;
								break;
							}

						}
						if (count == 0) {
							list.add(attachmentTypeList.get(i));
						}
					}
				}
			} else {
				list = attachmentTypeList;
			}
		} else {
			Map<String, String> msg = new HashMap<String, String>();
			msg.put("code", "500");
			list.add(msg);
		}

		return list;
	}

	/*
	 * @SuppressWarnings("unchecked")
	 * 
	 * @Override public void updateAttachment(String json) { Document doc =
	 * Document.parse(json); String uuid = (String) doc.get("UUID"); String version
	 * = doc.get("version").toString(); String businessId = (String)
	 * doc.get("businessId"); String fileName = (String) doc.get("fileName"); String
	 * filePath = (String) doc.get("filePath");
	 * 
	 * Document programmed = (Document) doc.get("programmed");
	 * 
	 * Document approved = (Document) doc.get("approved");
	 * 
	 * Map<String, Object> queryById = baseMongo.queryById(businessId,
	 * Constants.RCM_FORMALASSESSMENT_INFO);
	 * 
	 * List<Map<String, Object>> attachmentList = (List<Map<String, Object>>)
	 * queryById.get("attachment");
	 * 
	 * for (Map<String, Object> attachment : attachmentList) {
	 * if(attachment.get("UUID").toString().equals(uuid)){ List<Map<String, Object>>
	 * filesList = (List<Map<String, Object>>) attachment.get("files");
	 * if(Util.isNotEmpty(filesList)){ for (Map<String, Object> files : filesList) {
	 * if(files.get("version").toString().equals(version)){ files.put("fileName",
	 * fileName); files.put("filePath", filePath); files.put("approved", approved);
	 * files.put("programmed", programmed); } } } } }
	 * 
	 * Map<String, Object> data = new HashMap<String,Object>();
	 * data.put("attachment", attachmentList);
	 * baseMongo.updateSetByObjectId(businessId, data,
	 * Constants.RCM_FORMALASSESSMENT_INFO); }
	 */

	@Override
	public void addConferenceInformation(String json, String method) {
		Document meetingInfo = Document.parse(json);
		String businessId = meetingInfo.getString("formalId");
		BasicDBObject query = new BasicDBObject();
		query.put("formalId", businessId);
		List<Map<String, Object>> queryByConditions = baseMongo.queryByCondition(query, Constants.FORMAL_MEETING_INFO);
		Map<String, Object> queryByCondition = new HashMap<String, Object>();
		if (Util.isNotEmpty(queryByConditions)) {
			queryByCondition = queryByConditions.get(0);
		}

		if (method.equals("submit")) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("need_meeting", "1");
			map.put("businessId", businessId);
			map.put("metting_commit_time", Util.getTime());
			map.put("is_investmentmanager_submit", "1");
			this.formalAssessmentInfoCreateMapper.updateManagerSubmitState(map);
		}

		// 其他会议信息保存到metting表中
		if (Util.isEmpty(queryByCondition)) {
			String id = meetingInfo.get("formalId").toString();
			meetingInfo.put("_id", new ObjectId(id));
			meetingInfo.put("user_id", ThreadLocalUtil.getUserId());
			meetingInfo.put("create_date", Util.getTime());
			meetingInfo.put("state", "1");
			this.baseMongo.save(meetingInfo, Constants.FORMAL_MEETING_INFO);
		} else {
			String id = queryByCondition.get("_id").toString();
			meetingInfo.remove("_id");
			this.baseMongo.updateSetByObjectId(id, meetingInfo, Constants.FORMAL_MEETING_INFO);
		}
	}

	@Override
	public void saveNeedMeeting(String id, String needMeeting) {
		Map<String, Object> params = new HashMap<String, Object>();

		params.put("need_meeting", needMeeting);
		params.put("businessId", id);
		if (needMeeting.equals("0")) {
			params.put("stage", "9");
		} else {
			params.put("stage", "3");
		}
		this.formalAssessmentInfoCreateMapper.updateNeedMeeting(params);
	}

	@Override
	public List<Map<String, Object>> getHistoryList(String Json) throws Exception {
		HashMap<String, Object> jsonMap = JSON.parseObject(Json, HashMap.class);
		String id = jsonMap.get("id").toString();
		String businessType = jsonMap.get("businessType").toString();
		String pageLocation = jsonMap.get("pageLocation").toString();

		// 获取mongo存储的附件信息
		Map<String, Object> queryById = baseMongo.queryById(id, Constants.RCM_FORMALASSESSMENT_INFO);
		List<Map<String, Object>> attachmentList = (List<Map<String, Object>>) queryById.get("attachmentList");

		// 获取云库存储的附件
		List<Map<String, Object>> couldAttchmentList = fileService.getAttachHistoryList(id, businessType, pageLocation);

		// 初始化返回List
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		String param = "";
		if (Util.isNotEmpty(jsonMap.get("fileType"))) {
			param = jsonMap.get("fileType").toString();
		} else {
			param = jsonMap.get("fileName").toString();
		}

		// 筛选符合条件的返回数据
		for (int i = 0; i < couldAttchmentList.size(); i++) {
			for (int j = 0; j < attachmentList.size(); j++) {
				Map<String, Object> attach = new HashMap<String, Object>();
				Map<String, Object> type = (Map<String, Object>) attachmentList.get(j).get("type");
				if ((param.equals(type.get("ITEM_CODE")) || param.equals(attachmentList.get(j).get("fileName")))
						&& couldAttchmentList.get(i).get("FILEID").toString()
								.equals(attachmentList.get(j).get("fileId").toString())) {
					attach = couldAttchmentList.get(i);
					attach.put("fileName", attachmentList.get(j).get("fileName"));
					list.add(attach);
					break;
				}
			}
		}

		return list;
	}

	@Override
	public void saveEnvirMettingSummary(String businessId, String mettingSummaryInfo, String projectName,
			String pmodel) {
		// 1.保存会议纪要
		// 环卫、危废项目保存会议纪要，如果全部提交后，应修改状态
		if ("normal".equals(pmodel)) {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("summaryType", "normal");
			data.put("mettingSummary", mettingSummaryInfo);
			this.baseMongo.updateSetByObjectId(businessId, data, Constants.RCM_FORMALASSESSMENT_INFO);
		} else {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("summaryType", "file");
			this.baseMongo.updateSetById(businessId, data, Constants.RCM_BULLETIN_INFO);
		}

		// 在oracle增加决策通知书数据
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("businessId", businessId);
		params.put("projectFormalid", businessId);
		this.noticeDecisionDraftInfoMapper.save(params);

		// 在monogo增加决策通知书数据
		Map<String, Object> queryById = baseMongo.queryById(businessId, Constants.RCM_FORMALASSESSMENT_INFO);
		Document notice = new Document();
		ObjectId objectId = new ObjectId((String) businessId);
		// notice.put("_id", objectId);
		notice.put("projectFormalId", businessId);
		notice.put("projectName", projectName);
		notice.put("pertainArea", ((Document) queryById.get("apply")).get("pertainArea"));
		baseMongo.save(notice, Constants.RCM_NOTICEDECISION_INFO);

		Map<String, Object> statusMap = new HashMap<String, Object>();
		statusMap.put("table", "RCM_FORMALASSESSMENT_INFO");
		statusMap.put("filed", "IS_SUBMIT_DECISION_NOTICE");
		statusMap.put("status", "1");
		statusMap.put("BUSINESSID", businessId);
		this.fillMaterialsService.updateProjectStaus(statusMap);

		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> Object = this.fillMaterialsService.getRFIStatus(businessId);
		if (Util.isNotEmpty(Object)) {
			if (Util.isNotEmpty(Object.get("IS_SUBMIT_REPORT")) && Util.isNotEmpty(Object.get("IS_SUBMIT_BIDDING"))) {
				if (Object.get("IS_SUBMIT_REPORT").equals("1") && Object.get("IS_SUBMIT_BIDDING").equals("1")) {
					map.put("stage", "4");
					map.put("businessid", businessId);
					this.formalReportMapper.changeState(map);

					// 调用报表同步报表数据方法
					Map<String, Object> params1 = new HashMap<String, Object>();
					params1.put("projectType", "pfr");
					params1.put("businessId", businessId);
					String Json = JSON.toJSONString(params1);
					try {
						reportDataService.saveOrUpdateReportData(Json);
						logger.info("同步报表数据成功：[" + params1.get("projectType") + "," + businessId + "]");
					} catch (Exception e) {
						logger.info("同步报表数据出错：[" + params1.get("projectType") + "," + businessId + "],错误详情："
								+ e.getMessage());
					}
				}
			}
		}

	}

	@Override
	public Result queryEnvirMettingSummarys(String businessId) {
		Result result = new Result();
		Document mettingSummary = (Document) this.baseMongo.queryById(businessId, Constants.RCM_FORMALASSESSMENT_INFO);
		result.setResult_data(mettingSummary);
		return result;
	}
}

package com.yk.rcm.pre.service.impl;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import util.PropertiesUtil;
import util.ThreadLocalUtil;
import util.Util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yk.common.IBaseMongo;
import com.yk.flow.service.IBpmnAuditService;
import com.yk.flow.util.JsonUtil;
import com.yk.power.service.IOrgService;
import com.yk.rcm.bulletin.service.IBulletinInfoService;
import com.yk.rcm.pre.dao.IPreInfoMapper;
import com.yk.rcm.pre.service.IPreInfoService;

import common.Constants;
import common.PageAssistant;
import common.Result;
/**
 * 预评审基本信息service
 * @author yaphet
 */
@Service
@Transactional
public class PreInfoService implements IPreInfoService {					
	
	@Resource
	private IBaseMongo baseMongo;
	
	@Resource
	private IPreInfoMapper preInfoMapper;
	
	@Resource
	private IOrgService orgService;
	
	@Resource
	private IBpmnAuditService bpmnAuditService;
	
	@Override
	public void updateAuditStatusByBusinessId(String businessId, String wf_state) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("businessId", businessId);
		data.put("wf_state", wf_state);
		this.preInfoMapper.updateAuditStatusByBusinessId(data);
		this.updateLastUpdateDate(businessId);
	}

	@Override
	public void updateAuditStageByBusinessId(String businessId, String stage) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("businessId", businessId);
		data.put("stage", stage);
		this.preInfoMapper.updateAuditStageByBusinessId(data);
		this.updateLastUpdateDate(businessId);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getPreInfoByID(String businessId) {
		// oracle
		Map<String, Object> oracle = this.preInfoMapper.queryPreInfoById(businessId);
		// mongo
		Map<String, Object> mongo = this.baseMongo.queryById(businessId,Constants.RCM_PRE_INFO);
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("oracle", oracle);
		data.put("mongo", mongo);
		
		if(Util.isNotEmpty(mongo)){
			//获取附件类型
			List<Map<String, Object>> attachmentList = (List<Map<String, Object>>) mongo.get("attachment");
			List<Map<String, Object>> attach = new ArrayList<Map<String, Object>>();
			if(Util.isNotEmpty(attachmentList)){
				for (Map<String, Object> attachment : attachmentList) {
					if(Util.isNotEmpty(attachment)){
						Map<String, Object> document = new HashMap<String, Object>();
						document.put("ITEM_NAME", attachment.get("ITEM_NAME").toString());
						document.put("UUID", attachment.get("UUID").toString());
						attach.add(document);
					}
				}
			}
			data.put("attach", attach);
		}
		
		return data;
	}

	@Override
	public void queryPreListByPage(PageAssistant page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		if(Util.isNotEmpty(page.getParamMap())){
			String needCreateBy = (String) page.getParamMap().get("needCreateBy");
			if(!ThreadLocalUtil.getIsAdmin()){
				//管理员能看所有的
				if(!"0".equals(needCreateBy)){
					params.put("createBy", ThreadLocalUtil.getUserId());
				}
			}
		}
		if(page.getParamMap() != null){
			params.putAll(page.getParamMap());
		}
		List<Map<String,Object>> list = this.preInfoMapper.queryPreListByPage(params);
		//循环分页的list，取map的每一个对象，用id从mongo中查询数据，放到分页的list中
		for (Map<String, Object> map : list) {
			String id = (String)map.get("BUSINESSID");
			Map<String, Object> mongoDate = baseMongo.queryById(id, Constants.RCM_PRE_INFO);
			map.put("mongoDate", mongoDate);
		}
		page.setList(list);
	}

	@Override
	public void queryPreSubmitedList(PageAssistant page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		if(!ThreadLocalUtil.getIsAdmin()){
			//管理员能看所有的
			params.put("createBy", ThreadLocalUtil.getUserId());
		}
		if(page.getParamMap() != null){
			params.putAll(page.getParamMap());
		}
		List<Map<String,Object>> list = this.preInfoMapper.queryPreSubmitedList(params);
		//循环分页的list，取map的每一个对象，用id从mongo中查询数据，放到分页的list中
		for (Map<String, Object> map : list) {
			String id = (String)map.get("BUSINESSID");
			Map<String, Object> mongoDate = baseMongo.queryById(id, Constants.RCM_PRE_INFO);
			map.put("mongoDate", mongoDate);
		}
		page.setList(list);
	}
	public void updateLastUpdateDate(String businessId){
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("businessId", businessId);
		data.put("last_update_date", Util.now());
		this.preInfoMapper.updateLastUpdateDate(data);
	}

	@Override
	public void updateApplyDate(String businessId) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("businessId", businessId);
		data.put("apply_date", Util.now());
		this.preInfoMapper.updateApplyDate(data);
		this.updateLastUpdateDate(businessId);
	}

	@Override
	public void saveServiceTypeOpinionByBusinessId(String json, String businessId) {
		Document currentTaskVar = Document.parse(json);
		Document data = new Document();
		data.put("tzProtocolOpinion", currentTaskVar.getString("tzProtocolOpinion"));
		data.put("cesuanFileOpinion", currentTaskVar.getString("cesuanFileOpinion"));
		this.baseMongo.updateSetByObjectId(businessId, data, Constants.RCM_PRE_INFO);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void saveTaskPerson(String json) {
		Document doc = Document.parse(json);
		String businessId = (String) doc.get("businessId");
		Document reviewLeader = (Document) doc.get("reviewLeader");
		List<Document> fixedGroup = (List<Document>) doc.get("fixedGroup");
		
		Map<String, Object> taskallocation = new HashMap<String,Object>();
		taskallocation.put("reviewLeader", reviewLeader);
		if(Util.isNotEmpty(fixedGroup)){
			taskallocation.put("fixedGroup", fixedGroup);
		}
		//查mongo根据id查pre
		Map<String, Object> preMongo = baseMongo.queryById(businessId, Constants.RCM_PRE_INFO);
		preMongo.put("taskallocation", taskallocation);
		baseMongo.updateSetByObjectId(businessId, preMongo, Constants.RCM_PRE_INFO);
		
		Map<String, Object> params = new HashMap<String,Object>();
		
		
		String fixedGroupIds = "";
		if(Util.isNotEmpty(fixedGroup)){
			for (Document person : fixedGroup) {
				fixedGroupIds += ","+person.get("VALUE");
			}
			fixedGroupIds = fixedGroupIds.substring(1);
			params.put("fixedGroupIds", fixedGroupIds);
		}
		
		if(Util.isNotEmpty(reviewLeader)){
			params.put("businessId", businessId);
			params.put("reviewLeader", (String) reviewLeader.get("VALUE"));
			this.updatePersonById(params);
		}
		this.updateFixGroupIds(businessId,fixedGroupIds);
		
		this.updateLastUpdateDate(businessId);
	}

	private void updateFixGroupIds(String businessId, String fixedGroupIds) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("businessId", businessId);
		params.put("fixedGroupIds", fixedGroupIds);
		this.preInfoMapper.updateFixGroupIds(params);
	}
	@Override
	public void updatePersonById(Map<String, Object> params) {
		this.preInfoMapper.updatePersonById(params);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void saveReviewInfo(String json,String businessId) {
		
		if(Util.isEmpty(json)) return;
		Document doc = Document.parse(json);
		List<Document> commentsList = (List<Document>) doc.get("commentsList");
		//获取需要反馈时间
		String feedbackTime = (String)doc.get("feedbackTime");
		if(Util.isNotEmpty(feedbackTime)){
			//处理反馈时间
			if(Util.isNotEmpty(commentsList)){
				for (Document comments : commentsList) {
					if("1".equals((String)comments.get("isReviewLeaderEdit"))){
						comments.put("commentDate", feedbackTime);
					}
				}
			}
		}
		
		//查询mongo
		Map<String, Object> docc = this.baseMongo.queryById(businessId, Constants.RCM_PRE_INFO);
		//获取所有文件信息
		
		List<Document> attachmentOldList =(List<Document>) docc.get("attachment");
		
		//获取初步评审意见上传的文件
		List<Document> attachmentNewList = (List<Document>) doc.get("attachmentNew");
		if(Util.isNotEmpty(attachmentNewList)){
			for (Document attachmentNew : attachmentNewList) {
				Document attachmentUList = (Document) attachmentNew.get("attachmentUList");
				Object attachment_new1 = attachmentNew.get("attachment_new");
				if(Util.isNotEmpty(attachmentUList) && Util.isNotEmpty(attachment_new1)){
					Document attachment_new = (Document)attachment_new1;
					//新文件类型的UUID
					String newUuid = (String) attachmentUList.get("UUID");
					String newVersion = (String) attachment_new.get("version");
					//遍历旧文件，匹配旧文件类型与版本号
					
					for (Document attachmentOld : attachmentOldList) {
						if(Util.isNotEmpty(attachmentOld) && attachmentOld.get("UUID").equals(newUuid)){
							
							//旧附件中是否存在同类型的新的附件，根据版本号
							boolean hasFile = false;
							
							List<Document> files = (List<Document>) attachmentOld.get("files");
							if(Util.isNotEmpty(files)){
								for (int i =0 ;i<files.size() ; i++){
									String oldVersion = (String) files.get(i).get("version");
									if(oldVersion.equals(newVersion)){
										files.set(i, attachment_new);
										hasFile = true;
									}
								}
							}
							//不存在新的附件
							if(!hasFile){
								files.add(attachment_new);
							}
						}
					}
				}
			}
		}
		
		//更新附件与评审意见
		Document newDocument = new Document();
		newDocument.put("approveAttachment", doc);
		String json2 = JsonUtil.toJson(attachmentOldList);
		List<Document> attachmentOldList2 = JsonUtil.fromJson(json2, List.class);
		newDocument.put("attachment", attachmentOldList2);
		this.baseMongo.updateSetByObjectId(businessId, newDocument, Constants.RCM_PRE_INFO);
	}

	@Override
	public List<Map<String, Object>> getAllOldPre() {
		return this.preInfoMapper.getAllOldPre();
	}

	@Override
	public List<Map<String, Object>> getOldProjectRelationByBusinessId(String businessId) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("businessId", businessId);
		return this.preInfoMapper.getOldProjectRelationByBusinessId(map);
	}

	@Override
	public void deleteByBusinessId(String businessId) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("businessId", businessId);
		this.preInfoMapper.deleteByBusinessId(map);
	}

	@Override
	public void save(Map<String, Object> param) {
		this.preInfoMapper.save(param);
	}
	
	@Override
	public Map<String, Object> queryWaitingByConditions(String userId, String businessId) {
		String sql = this.bpmnAuditService.queryWaitdealSql(Constants.PRE_ASSESSMENT, userId).getData();
		Map<String,Object> conditions = new HashMap<String,Object>();
		conditions.put("userId", userId);
		conditions.put("businessId", businessId);
		conditions.put("waitSql", sql);
		return this.preInfoMapper.queryWaitingByConditions(conditions);
	}
	
	@Override
	public Map<String, Object> queryAuditedByConditions(String userId,
			String businessId) {
		Map<String,Object> conditions = new HashMap<String,Object>();
		conditions.put("userId", userId);
		conditions.put("businessId", businessId);
		return this.preInfoMapper.queryAuditedByConditions(conditions);
	}
	
	@Override
	public Map<String, Object> queryRelationByTypeId (String businessId,String relationTypeCode){
		Map<String, Object> data = new HashMap<String,Object>();
		data.put("businessId", businessId);
		data.put("relationTypeId", relationTypeCode);
		return this.preInfoMapper.queryRelationByTypeId(data);
	}

	@Override
	public List<Map<String, Object>> queryFixGroupOption(String businessId) {
		Map<String, Object> params = new HashMap<String,Object>();
		params.put("businessId", businessId);
		return this.preInfoMapper.queryFixGroupOption(params);
	}

	@Override
	public void updateCompleteDate(String businessId, Date now) {
		Map<String, Object> params = new HashMap<String,Object>();
		params.put("businessId", businessId);
		params.put("completeDate", now);
		this.preInfoMapper.updateCompleteDate(params);
		this.updateLastUpdateDate(businessId);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void deleteAttachment(String json) {
		Document doc = Document.parse(json);
		String uuid = (String) doc.get("UUID");
		String version = (String) doc.get("version");
		String businessId = (String) doc.get("businessId");
		Map<String, Object> queryById = baseMongo.queryById(businessId, Constants.RCM_PRE_INFO);
		
		List<Map<String, Object>> attachmentList = (List<Map<String, Object>>) queryById.get("attachment");
		
		for (Map<String, Object> attachment : attachmentList) {
			if(attachment.get("UUID").toString().equals(uuid)){
				List<Map<String, Object>> filesList = (List<Map<String, Object>>) attachment.get("files");
				if(Util.isNotEmpty(filesList)){
					for(int i = 0 ; i < filesList.size() ; i++){
						if(filesList.get(i).get("version").toString().equals(version)){
							filesList.remove(i);
						}
					}
				}
			}
		}
		Map<String, Object> data = new HashMap<String,Object>();
		data.put("attachment", attachmentList);
		baseMongo.updateSetByObjectId(businessId, data, Constants.RCM_PRE_INFO);
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addNewAttachment(String json) {
		
		Document doc = Document.parse(json);
		
		String uuid = doc.get("UUID").toString();
		
		String version = doc.get("version").toString();
		String businessId = (String) doc.get("businessId");
		Document item = (Document) doc.get("item");
		String fileName = (String) item.get("fileName");
		String filePath = (String) item.get("filePath");
		//申请人信息
		Document programmed = (Document) item.get("programmed");
		//审核人信息
		Document approved = (Document) item.get("approved");
		
		Map<String, Object> queryById = baseMongo.queryById(businessId, Constants.RCM_PRE_INFO);
		
		List<Map<String, Object>> attachmentList = (List<Map<String, Object>>) queryById.get("attachment");
		
		for (Map<String, Object> attachment : attachmentList) {
			if(attachment.get("UUID").toString().equals(uuid)){
				List<Map<String, Object>> filesList = (List<Map<String, Object>>) attachment.get("files");
				if(Util.isNotEmpty(filesList)){
					Map<String, Object> file = new HashMap<String,Object>();
					file.put("fileName", fileName);
					file.put("filePath", filePath);
					file.put("programmed", programmed);
					file.put("version", version);
					file.put("approved", approved);
					file.put("upload_date", approved);
					file.put("upload_date", Util.format(Util.now()));
					filesList.add(file);
				}else{
					
					Map<String, Object> file = new HashMap<String,Object>();
					file.put("fileName", fileName);
					file.put("filePath", filePath);
					file.put("programmed", programmed);
					file.put("approved", approved);
					file.put("version", version);
					file.put("upload_date", Util.format(Util.now()));
					List<Map<String, Object>> files = new ArrayList<Map<String, Object>>();
					files.add(file);
					attachment.put("files", files);
				}
			}
		}
		Map<String, Object> data = new HashMap<String,Object>();
		data.put("attachment", attachmentList);
		baseMongo.updateSetByObjectId(businessId, data, Constants.RCM_PRE_INFO);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void updateAttachment(String json) {
		Document doc = Document.parse(json);
		String uuid = (String) doc.get("UUID");
		String version = doc.get("version").toString();
		String businessId = (String) doc.get("businessId");
		String fileName = (String) doc.get("fileName");
		String filePath = (String) doc.get("filePath");
		
		Document programmed = (Document) doc.get("programmed");
		
		Document approved = (Document) doc.get("approved");
		
		Map<String, Object> queryById = baseMongo.queryById(businessId, Constants.RCM_PRE_INFO);
		
		List<Map<String, Object>> attachmentList = (List<Map<String, Object>>) queryById.get("attachment");
		
		for (Map<String, Object> attachment : attachmentList) {
			if(attachment.get("UUID").toString().equals(uuid)){
				List<Map<String, Object>> filesList = (List<Map<String, Object>>) attachment.get("files");
				if(Util.isNotEmpty(filesList)){
					for (Map<String, Object> files : filesList) {
						if(files.get("version").toString().equals(version)){
							files.put("fileName", fileName);
							files.put("filePath", filePath);
							files.put("approved", approved);
							files.put("programmed", programmed);
						}
					}
				}
			}
		}
		
		Map<String, Object> data = new HashMap<String,Object>();
		data.put("attachment", attachmentList);
		baseMongo.updateSetByObjectId(businessId, data, Constants.RCM_PRE_INFO);
	}
	

	@Override
	public Map<String, Object> getOracleByBusinessId(String businessId) {
		return preInfoMapper.queryPreInfoById(businessId);
	}

	@Override
	public void saveOrUpdateForTz(Document doc, Result result) {
		String businessid = null;
		Map<String, Object> map = new HashMap<String, Object>();
		doc.append("create_date", Util.format(Util.now()));
		doc.append("currentTimeStamp", Util.format(Util.now(), "yyyyMMddHHmmssSSS"));
		doc.append("state", "1");
		//判断新增或修改
		if(!doc.containsKey("businessid") || Util.isEmpty(doc.get("businessid"))){
			//组织数据
			doc.put("_id", new ObjectId());
			//插入oracle
			Map<String, Object> dataForOracle = packageDataForOracle(doc);
			preInfoMapper.createForTz(dataForOracle);
			//插入mongdb
			baseMongo.save(doc, Constants.RCM_PRE_INFO);
			businessid = doc.get("_id").toString();
		}else{
			//删除oracle
			businessid = doc.getString("businessid");
			deleteByBusinessId(businessid);
			//组织数据
			doc.append("update_date", Util.format(Util.now()));
			doc.put("_id", businessid);
			//插入oracle
			Map<String, Object> dataForOracle = packageDataForOracle(doc);
			preInfoMapper.createForTz(dataForOracle);
			//修改mongo
			doc.remove("_id");
			baseMongo.updateSetByObjectId(businessid, doc, Constants.RCM_PRE_INFO);
		}
		this.updateAuditStageByBusinessId(businessid, "1");
		map.put("result_wf_state", "0");
		map.put("result_status", "true");
		map.put("id", businessid);
		// 返回地址为:http://localhost/html/index.html#/PreInfoList/0
		String prefix = PropertiesUtil.getProperty("domain.allow");
		map.put("URL", prefix+"/html/index.html#/PreDetailView/"+businessid+"/JTI1MjMlMkZQcmVJbmZvTGlzdCUyRjA=");
		Gson gson = new GsonBuilder().disableHtmlEscaping().create(); 
		String json = gson.toJson(map);
		result.setResult_data(json);
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> packageDataForOracle(Document doc){
		HashMap<String, Object> params = new HashMap<String,Object>();
		
		params.put("businessId", doc.get("_id").toString());
		Document apply = (Document) doc.get("apply");
		params.put("projectName", apply.getString("projectName"));
		params.put("projectNum", apply.getString("projectNo"));
		
		Document reportingUnit = (Document) apply.get("reportingUnit");
		params.put("reportingUnit_id", reportingUnit.getString("KEY"));
		
		List<Document> serviceType = (List<Document>) apply.get("serviceType");
		if(Util.isNotEmpty(serviceType) && serviceType.size()>0){
			String serviceType_id="";
			for (Document st : serviceType) {
				serviceType_id+=","+st.getString("KEY");
			}
			serviceType_id = serviceType_id.substring(1);
			params.put("serviceType_id", serviceType_id);
		}
		
		List<Document> projectModel = (List<Document>) apply.get("projectModel");
		if(Util.isNotEmpty(projectModel) && projectModel.size()>0){
			String projectModel_ids="";
			for (Document pm : projectModel) {
				projectModel_ids+=","+pm.getString("KEY");
			}
			if(projectModel_ids.length() > 0){
				projectModel_ids = projectModel_ids.substring(1);
			}
			params.put("project_model_ids", projectModel_ids);
		}
		params.put("need_meeting", null);
		
		params.put("meeting_date", null);
		
		params.put("is_supplement_review", doc.getString("is_supplement_review"));
		
		params.put("emergencyLevel", null);
		
		params.put("isUrgent", null);
		
		Document pertainArea = (Document) apply.get("pertainArea");
		if(Util.isNotEmpty(pertainArea)){
			params.put("pertainAreaId", pertainArea.getString("KEY"));
		}
		
		params.put("isTZ", doc.getString("istz"));
		
		params.put("wf_state", "0");
		
		params.put("apply_date", null);
		
		params.put("complete_date", null);
		
		params.put("report_create_date", null);
		
		params.put("projectSize", apply.getString("projectSize"));
		
		params.put("investMoney", apply.getString("investMoney"));
		
		params.put("rateOfReturn", null);
		
		params.put("stage", doc.getString("stage"));
		
		params.put("oldData", "0");
		
		params.put("investmentModel", apply.getString("investmentModel"));
		
		//大区ID
		Document grassrootsLegalStaff = (Document) apply.get("companyHeader");
		if(Util.isNotEmpty(grassrootsLegalStaff)){
			params.put("largeAreaPersonId", grassrootsLegalStaff.getString("VALUE"));
		}
		
		//投资中心
		Document investmentPerson = (Document) apply.get("investmentPerson");
		if(investmentPerson !=null && investmentPerson.getString("VALUE")!=null){
			params.put("serviceTypePersonId", investmentPerson.getString("VALUE"));
		}
		//创建人(投资经理)
		Document createby = (Document) apply.get("createBy");
		params.put("createBy", createby.getString("VALUE"));
		
		//创建时间与mongDB 一致
		params.put("create_date", doc.get("create_date"));
		return params;
	}

	@Override
	public void deleteOracleAndMongdbByBusinessId(String businessId) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("businessId", businessId);
		preInfoMapper.deleteByBusinessId(map);
		baseMongo.deleteById(businessId, Constants.RCM_PRE_INFO);
	}

	@Override
	public void saveMajorMemberInfo(String businessId , String json) {
		Document majorMemberInfo = Document.parse(json);
		
		Map<String,Object> data = new HashMap<String,Object>();
		//TODO
		data.put("", majorMemberInfo);
		this.baseMongo.updateSetByObjectId(businessId, data, Constants.RCM_PRE_INFO);
	}

	@Override
	public int countAll() {
		return this.preInfoMapper.countAll();
	}

	@Override
	public void getProjectReportDetails0706(PageAssistant page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		if(page.getParamMap() != null){
			params.putAll(page.getParamMap());
		}
		List<Map<String,Object>> list = preInfoMapper.getProjectReportDetails0706(params);
		page.setList(list);
	}

	@Override
	public void queryAllInfoByPage(PageAssistant page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		if(page.getParamMap() != null){
			params.putAll(page.getParamMap());
		}
		List<Map<String,Object>> list = preInfoMapper.queryAllInfoByPage(params);
		page.setList(list);
	}
	
	@Override
	public List<Map<String, Object>> queryPertainAreaIsNull() {
		return preInfoMapper.queryPertainAreaIsNull();
	}

	@Override
	public void updatePertainAreaId(String id, String pertainAreaId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		params.put("pertainAreaId", pertainAreaId);
		preInfoMapper.updatePertainAreaId(params);
	}

	@Override
	public List<Map<String, Object>> queryByStageAndstate(String stage,String state) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("stage", stage);
		map.put("wf_state", state);
		return this.preInfoMapper.queryByStageAndstate(map);
	}
	
	@Override
	public List<Map<String, Object>> queryPreCount(String wf_state,String stage, String pertainAreaId, String serviceTypeId,String year) {
		Map<String, Object> map = new HashMap<String,Object>();
		
		if(Util.isNotEmpty(wf_state)){
			map.put("wf_state", wf_state.split(","));
		}
		if(Util.isNotEmpty(stage)){
			map.put("stage", stage.split(","));
		}
		if(Util.isNotEmpty(pertainAreaId)){
			//处理大区id
			map.put("pertainAreaId", pertainAreaId.split(","));
		}
		if(Util.isNotEmpty(serviceTypeId)){
			//处理serviceTypeId
			map.put("serviceTypeId", serviceTypeId.split(","));
		}
		
		if(Util.isEmpty(year)){
			int y = Calendar.getInstance().get(Calendar.YEAR);
			map.put("year", y);
		}else{
			map.put("year", year);
		}
		return this.preInfoMapper.queryPreCount(map);
	}
	@Override
	public void startPigeonholeByBusinessId(String businessId,Date pigeonholeTime) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("businessId", businessId);
		params.put("pigeonholeStatus", "1");
		params.put("pigeonholeTime", pigeonholeTime);
		preInfoMapper.startPigeonholeByBusinessId(params);
	}
	
	@Override
	public void updatePigeStatByBusiId(String businessId,String status) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("businessId", businessId);
		params.put("pigeonholeStatus", status);
		preInfoMapper.updatePigeStatByBusiId(params);
	}
	
	@Override
	public void cancelPigeonholeByBusinessId(String businessId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("businessId", businessId);
		params.put("pigeonholeStatus", "");
		params.put("pigeonholeTime",null);
		preInfoMapper.cancelPigeonholeByBusinessId(params);
	}

	@Override
	public List<Map<String, Object>> queryPrePertainArea() {
		List<Map<String, Object>> list = this.preInfoMapper.queryAllPre();
		List<String> areaList = new ArrayList<String>();
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> map : list) {
			String pertainAreaId = (String) map.get("PERTAINAREAID");
			if(!areaList.contains(pertainAreaId)){
				areaList.add(pertainAreaId);
			}
		}
		for (String pertainAreaId : areaList) {
			Map<String, Object> org = this.orgService.queryByPkvalue(pertainAreaId);
			result.add(org);
		}
		return result;
	}

	@Override
	public void queryPageForExport(PageAssistant page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		if(page.getParamMap() != null){
			params.putAll(page.getParamMap());
			Map<String, Object> date = page.getParamMap();
			if(Util.isEmpty(date.get("year"))){
				Date now = Util.now();
		        //获取当前年
				int year=now.getYear() +1900;
				params.put("year", year+"");
			}
		}
		List<Map<String,Object>> list = this.preInfoMapper.queryPreListForExport(params);
		//循环分页的list，取map的每一个对象，用id从mongo中查询数据，放到分页的list中
		for (Map<String, Object> map : list) {
			String id = (String)map.get("BUSINESSID");
			Map<String, Object> mongoDate = baseMongo.queryById(id, Constants.RCM_PRE_INFO);
			map.put("mongoDate", mongoDate);
		}
		page.setList(list);
	}

	@Override
	public void saveNeedMeetingAndNeedReport(String businessId, String pre) {
		Document preDoc = Document.parse(pre);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("needMeeting", (String) preDoc.get("needMeeting"));
		params.put("needReport", (String) preDoc.get("needReport"));
		params.put("decisionOpinion", (String) preDoc.get("decisionOpinion"));
		params.put("businessId", businessId);
		String noReportReason = (String) preDoc.get("noReportReason");
		
		if(Util.isNotEmpty(noReportReason)){
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("noReportReason", noReportReason);
			this.baseMongo.updateSetByObjectId(businessId, map, Constants.RCM_PRE_INFO);
		}
		
		this.preInfoMapper.saveNeedMeetingAndNeedReport(params);
		
		if("0".equals((String) preDoc.get("needMeeting")) && "0".equals((String) preDoc.get("needReport"))){
			this.updateAuditStageByBusinessId(businessId, "9");
		}else{
			this.updateAuditStageByBusinessId(businessId, "3");
		}
		
		if("0".equals((String) preDoc.get("needMeeting"))){
			this.updateMeetingCommitTime(businessId, Util.now());
		}
	}
	
	public void updateMeetingCommitTime(String businessId,Date date){
		Map<String, Object> hashMap = new HashMap<String,Object>();
		hashMap.put("businessId", businessId);
		hashMap.put("date", date);
		this.preInfoMapper.updateMeetingCommitTime(hashMap);
	}
	
	@Override
	public List<Map<String, Object>> queryAllByDaxt() {
		return preInfoMapper.queryAllByDaxt();
	}
}

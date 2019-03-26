package com.yk.rcm.newPre.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import util.Util;

import com.yk.common.IBaseMongo;
import com.yk.power.dao.IRoleMapper;
import com.yk.power.service.IOrgService;
import com.yk.rcm.newPre.dao.IPreInfoCreateMapper;
import com.yk.rcm.newPre.service.IPreInfoCreateService;

import common.Constants;
import common.PageAssistant;


@Service
@Transactional
public class preInfoCreateServiceImpl implements IPreInfoCreateService {

	
	@Resource
	private IPreInfoCreateMapper preInfoCreateMapper;
	@Resource
	private IRoleMapper roleMapper;
	@Resource
	private IBaseMongo baseMongo;
	@Resource
	private IOrgService orgService;
	
	@Override
	public String createProject(String json) {
		Document doc = Document.parse(json);
		ObjectId businessid = new ObjectId();
		doc.put("_id", businessid);
		Map<String, Object> dataForOracle = this.packageDataForOracle(doc);
		this.preInfoCreateMapper.insert(dataForOracle);
		this.baseMongo.save(doc, Constants.RCM_PRE_INFO);
		this.saveDefaultProRole(doc);
		return (String) dataForOracle.get("businessId");
	}
	
	/**
	 * 新建项目时创建默认项目角色
	 * author Sunny Qi
	 * 2019-03-14
	 * */
	private void saveDefaultProRole(Document doc){
		HashMap<String, Object> params = new HashMap<String,Object>();
		
		HashMap<String, Object> params1 = new HashMap<String,Object>();
		Document apply = (Document) doc.get("apply");
		Document pertainArea = (Document) apply.get("pertainArea");
		String pertainAreaId = pertainArea.getString("KEY");
		params1.put("orgId", pertainAreaId);
		List<Map<String, Object>> roleIds = roleMapper.queryRoleIdByOrgId(params1);
		
		for(int i=0; i < roleIds.size(); i++){
			params.put("id", Util.getUUID());
			params.put("businessId", doc.get("_id").toString());
			params.put("roleId", roleIds.get(i).get("ROLE_ID"));
			Document createby = (Document) apply.get("investmentManager");
			params.put("createBy", createby.getString("VALUE"));
			params.put("create_date", doc.get("create_date"));
			params.put("projectType", "pre");
			
			this.roleMapper.insertProRole(params);
		}
	}
	
	@Override
	public void updateProject(String json) {
		Document doc = Document.parse(json);
		Map<String, Object> dataForOracle = this.packageDataForOracle(doc);
		this.preInfoCreateMapper.update(dataForOracle);
		Document data = new Document();
		data.put("apply", doc.get("apply"));
		data.put("last_update_date", doc.get("last_update_date"));
		baseMongo.updateSetByObjectId(doc.get("_id").toString(), data, Constants.RCM_PRE_INFO);
	}
	
	@Override
	public void deleteProject(String ids) {
		String[] id = ids.split(",");
		for(int i=0; i<id.length; i++){
			String objId = id[i].replace("\"", "");
			baseMongo.deleteById(objId, Constants.RCM_PRE_INFO);
			this.preInfoCreateMapper.delete(objId);
		}
	}
	
	@SuppressWarnings("unchecked")
	private Map<String, Object> packageDataForOracle(Document doc){
        HashMap<String, Object> params = new HashMap<String,Object>();
		
		params.put("businessId", doc.get("_id").toString());
		Document apply = (Document) doc.get("apply");
		params.put("projectName", apply.getString("projectName"));
		params.put("projectNum", apply.getString("projectNo"));
		
		Document reportingUnit = (Document) apply.get("pertainArea");
		params.put("reportingUnit_id", reportingUnit.getString("KEY"));
		
		//根据申报单位初始化大区ID
		Map<String, Object> pertainAreaDocument = orgService.queryPertainArea(reportingUnit.get("KEY").toString());
		params.put("pertainAreaId", pertainAreaDocument.get("ORGPKVALUE"));
		
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
		
		params.put("isTZ", "0");
		
		params.put("wf_state", "0");
		
		params.put("apply_date", null);
		
		params.put("complete_date", null);
		
		params.put("report_create_date", null);
		
		params.put("projectSize", apply.getString("projectSize"));
		
		params.put("investMoney", apply.getString("investMoney"));
		
		params.put("rateOfReturn", null);
		
		params.put("stage", "1");
		
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
		Document createby = (Document) apply.get("investmentManager");
		params.put("createBy", createby.getString("VALUE"));
		
		//创建时间与mongDB 一致
		params.put("create_date", doc.get("create_date"));
		return params;
	}

	@Override
	public PageAssistant getNewProjectList(PageAssistant page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		
		if(page.getParamMap() != null){
			params.putAll(page.getParamMap());
		}
		
		List<Map<String,Object>> list = this.preInfoCreateMapper.getNewProjectList(params);
		//循环分页的list，取map的每一个对象，用id从mongo中查询数据，放到分页的list中
		for (Map<String, Object> map : list) {
			String id = (String)map.get("BUSINESSID");
			Map<String, Object> mongoDate = baseMongo.queryById(id, Constants.RCM_PRE_INFO);
			map.put("mongoDate", mongoDate);
		}
		page.setList(list);
		return page;
	}

	@Override
	public Map<String, Object> getProjectByID(String id) {
		HashMap<String, Object> result = new HashMap<String,Object>();
		Document mongoData = null;
		Map<String, Object> queryMongoById = this.baseMongo.queryById(id, Constants.RCM_PRE_INFO);
		mongoData = (Document)queryMongoById;
		mongoData.put("_id", mongoData.get("_id").toString());
		result.put("mongoData", mongoData);
		
		Map<String, Object> queryOracleById = this.preInfoCreateMapper.getProjectByID(id);
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
		Document itemType = (Document) item.get("itemType");
		String fileName = (String) item.get("fileName");
		//申请人信息
		Document programmed = (Document) item.get("programmed");
		//审核人信息
		Document approved = (Document) item.get("approved");
		//最後提交人信息
		Document lastUpdateBy = (Document) item.get("lastUpdateBy");
		String lastUpdateData = (String) item.get("lastUpdateData"); 
		
		Map<String, Object> queryById = baseMongo.queryById(businessId, Constants.RCM_PRE_INFO);
		
		List<Map<String, Object>> attachmentList = (List<Map<String, Object>>) queryById.get("attachmentList");
		
		if (attachmentList == null) {
			attachmentList = new ArrayList<Map<String, Object>>();
			Map<String, Object> file = new HashMap<String,Object>();
			file.put("fileId", fileId);
			file.put("fileName", fileName);
			file.put("oldFileName", oldFileName);
			file.put("type", type);
			file.put("itemType", itemType);
			file.put("programmed", programmed);
			file.put("approved", approved);
			file.put("lastUpdateBy", lastUpdateBy);
			file.put("lastUpdateData", lastUpdateData);
			attachmentList.add(file);
		} else {
			Map<String, Object> file = new HashMap<String,Object>();
			file.put("fileId", fileId);
			file.put("fileName", fileName);
			file.put("oldFileName", oldFileName);
			file.put("type", type);
			file.put("itemType", itemType);
			file.put("programmed", programmed);
			file.put("approved", approved);
			file.put("lastUpdateBy", lastUpdateBy);
			file.put("lastUpdateData", lastUpdateData);
			attachmentList.add(file);
		}

		
		Map<String, Object> data = new HashMap<String,Object>();
		data.put("attachmentList", attachmentList);
		baseMongo.updateSetByObjectId(businessId, data, Constants.RCM_PRE_INFO);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void deleteAttachment(String json) {
		Document doc = Document.parse(json);
		String businessId = (String) doc.get("businessId");
		String fileId = doc.get("fileId") + "";
		Map<String, Object> queryById = baseMongo.queryById(businessId, Constants.RCM_PRE_INFO);
		
		List<Map<String, Object>> attachmentList = (List<Map<String, Object>>) queryById.get("attachmentList");
		System.out.println("fileId ====" + fileId);
		for (int i = 0; i < attachmentList.size(); i++) {
			Document attachment = (Document) attachmentList.get(i);
			System.out.println("attachment=           " + attachment);
			System.out.println(attachment.get("fileId"));
			System.out.println(attachment.get("fileId") .equals(fileId));
			if (attachment.get("fileId") .equals(fileId)) {
				attachmentList.remove(i);
				break;
			}
		}
		Map<String, Object> data = new HashMap<String,Object>();
		data.put("attachmentList", attachmentList);
		baseMongo.updateSetByObjectId(businessId, data, Constants.RCM_PRE_INFO);
	}

	/*@SuppressWarnings("unchecked")
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
	}*/

}

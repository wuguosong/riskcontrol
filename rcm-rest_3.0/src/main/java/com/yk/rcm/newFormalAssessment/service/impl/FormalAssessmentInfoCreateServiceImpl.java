package com.yk.rcm.newFormalAssessment.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import util.ThreadLocalUtil;
import util.Util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yk.common.IBaseMongo;
import com.yk.power.dao.IRoleMapper;
import com.yk.rcm.formalAssessment.dao.IFormalAssessmentInfoMapper;
import com.yk.rcm.newFormalAssessment.dao.IFormalAssessmentInfoCreateMapper;
import com.yk.rcm.newFormalAssessment.service.IFormalAssessmentInfoCreateService;

import common.Constants;
import common.PageAssistant;


@Service
@Transactional
public class FormalAssessmentInfoCreateServiceImpl implements IFormalAssessmentInfoCreateService {

	
	@Resource
	private IFormalAssessmentInfoCreateMapper formalAssessmentInfoCreateMapper;
	@Resource
	private IRoleMapper roleMapper;
	@Resource
	private IBaseMongo baseMongo;
	
	@Override
	public void createProject(String json) {
		Document doc = Document.parse(json);
		ObjectId businessid = new ObjectId();
		doc.put("_id", businessid);
		Map<String, Object> dataForOracle = this.packageDataForOracle(doc);
		this.formalAssessmentInfoCreateMapper.insert(dataForOracle);
		this.baseMongo.save(doc, Constants.RCM_FORMALASSESSMENT_INFO);
		this.saveDefaultProRole(doc);
	}
	
	/**
	 * 新建项目时创建默认项目角色
	 * author Sunny Qi
	 * 2019-03-14
	 * */
	private void saveDefaultProRole(Document doc){
		HashMap<String, Object> params = new HashMap<String,Object>();
		
		ArrayList<String> roleIds = new ArrayList<String>();
		roleIds.add("11911d2638f141c3b2e3f75805e58c75");
		roleIds.add("54c42dd0585140dd9e0770eb88da4b34");
		
		for(int i=0; i < roleIds.size(); i++){
			params.put("id", Util.getUUID());
			params.put("businessId", doc.get("_id").toString());
			
			Document apply = (Document) doc.get("apply");
			Document pertainArea = (Document) apply.get("pertainArea");
			String pertainAreaName = pertainArea.getString("VALUE");
			/*if (pertainAreaName == "东部大区") {
				
			}*/
			params.put("roleId", roleIds.get(i));
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
		for(int i=0; i<id.length; i++){
			String objId = id[i].replace("\"", "");
			baseMongo.deleteById(objId, Constants.RCM_FORMALASSESSMENT_INFO);
			this.formalAssessmentInfoCreateMapper.delete(objId);
		}
	}
	
	@SuppressWarnings("unchecked")
	private Map<String, Object> packageDataForOracle(Document doc){
		HashMap<String, Object> params = new HashMap<String,Object>();
		
		params.put("businessid", doc.get("_id").toString());
			
		Document apply = (Document) doc.get("apply");
		params.put("projectName", apply.getString("projectName"));
		params.put("projectNum", apply.getString("projectNo"));
		
		System.out.println(apply.get("serviceType"));
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
		
		params.put("investment_model", apply.get("investmentModel").toString());
		
		List<Document> projectModel = (List<Document>) apply.get("projectModel");
		
		if(Util.isNotEmpty(projectModel) && projectModel.size()>0){
			String projectModel_ids="";
			for (Document pm : projectModel) {
				projectModel_ids+=","+pm.getString("KEY");
			}
			projectModel_ids = projectModel_ids.substring(1);
			params.put("project_model_ids", projectModel_ids);
		}
		
		params.put("need_meeting", null);
		
		params.put("meeting_date", null);
		
		params.put("is_supplement_review", doc.get("is_supplement_review"));
		
		params.put("emergencyLevel", null);
		
		params.put("isUrgent", null);
		
		Document pertainArea = (Document) apply.get("pertainArea");
		if(pertainArea != null){
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
		
		params.put("auditStage", "1");
		
		params.put("oldData", "0");
		
		//基层法务评审人ID
		Document grassrootsLegalStaff = (Document) apply.get("grassrootsLegalStaff");
		if(grassrootsLegalStaff != null){
			params.put("grassrootslegalpersonId", grassrootsLegalStaff.getString("VALUE"));
		}
		//大区负责人ID
		Map<String, Object> companyHeader = (Map<String, Object>) apply.get("companyHeader");
		String companyHeaderValue = (String)companyHeader.get("VALUE");
		params.put("largeAreaPersonId", companyHeaderValue);
		
		//区域负责人——投资中心/水环境
		Map<String, Object> investmentPerson = (Map<String, Object>) apply.get("investmentPerson");
		if(investmentPerson != null){
			String serviceTypeValue = (String) investmentPerson.get("VALUE");
			params.put("serviceTypePersonId",serviceTypeValue);
		}
		
		Document createby = (Document) apply.get("investmentManager");
		params.put("createBy", createby.getString("VALUE"));
		
		//创建时间与mongDB 一致
		params.put("create_date", doc.get("create_date"));
		//修改时间与mongDB 一致
        params.put("last_update_date", doc.get("last_update_date"));
		return params;
	}

	@Override
	public PageAssistant getNewProjectList(PageAssistant page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		
		if(page.getParamMap() != null){
			params.putAll(page.getParamMap());
		}
		
		List<Map<String,Object>> list = this.formalAssessmentInfoCreateMapper.getNewProjectList(params);
		//循环分页的list，取map的每一个对象，用id从mongo中查询数据，放到分页的list中
		for (Map<String, Object> map : list) {
			String id = (String)map.get("BUSINESSID");
			Map<String, Object> mongoDate = baseMongo.queryById(id, Constants.RCM_FORMALASSESSMENT_INFO);
			map.put("mongoDate", mongoDate);
		}
		page.setList(list);
		return page;
	}

	@Override
	public Map<String, Object> getProjectByID(String id) {
		HashMap<String, Object> result = new HashMap<String,Object>();
		Document mongoData = null;
		Map<String, Object> queryMongoById = this.baseMongo.queryById(id, Constants.RCM_FORMALASSESSMENT_INFO);
		mongoData = (Document)queryMongoById;
		mongoData.put("_id", mongoData.get("_id").toString());
		result.put("mongoData", mongoData);
		
		Map<String, Object> queryOracleById = this.formalAssessmentInfoCreateMapper.getProjectByID(id);
		result.put("oracleDate", queryOracleById);
		
		return result;
	}

}

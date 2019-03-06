package com.yk.rcm.newPre.service.impl;

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
	private IBaseMongo baseMongo;
	
	@Override
	public void createProject(String json) {
		Document doc = Document.parse(json);
		ObjectId businessid = new ObjectId();
		doc.put("_id", businessid);
		Map<String, Object> dataForOracle = this.packageDataForOracle(doc);
		this.preInfoCreateMapper.insert(dataForOracle);
		this.baseMongo.save(doc, Constants.RCM_PRE_INFO);
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

}
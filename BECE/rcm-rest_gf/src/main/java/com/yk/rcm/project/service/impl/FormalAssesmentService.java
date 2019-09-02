/**
 * 
 */
package com.yk.rcm.project.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import util.Util;
import ws.client.TzClient;

import com.mongodb.BasicDBObject;
import com.yk.bpmn.service.IBpmnService;
import com.yk.common.IBaseMongo;
import com.yk.power.service.IOrgService;
import com.yk.rcm.newFormalAssessment.dao.IFormalAssessmentInfoCreateMapper;
import com.yk.rcm.project.dao.IProjectMapper;
import com.yk.rcm.project.service.IFormalAssesmentService;
import com.yk.rcm.project.service.IPreAssementService;
import com.yk.rcm.project.service.IProjectRelationService;
import common.Constants;

/**
 * @author 80845530
 *
 */
@Service
@Transactional
public class FormalAssesmentService implements IFormalAssesmentService {
	@Resource
	private IBaseMongo baseMongo;
	@Resource
	private IProjectMapper projectMapper;
	@Resource
	private IProjectRelationService projectRelationService;
	@Resource
	private IBpmnService bpmnService;
	@Resource
	private IPreAssementService preAssementService;
	@Resource
	private IOrgService orgService;
	@Resource
	private TzClient tzClient;
	@Resource
	private IFormalAssessmentInfoCreateMapper formalAssessmentInfoCreateMapper;
	
	@Override
	public void deleteById(String businessId) {
//		//删除mongo数据
//		this.baseMongo.deleteById(businessId, Constants.RCM_PROJECTFORMALREVIEW_INFO);
//		//删除oracle数据
//		this.projectMapper.deleteByBusinessId(businessId);
//		//删除人员关系
//		this.projectRelationService.deleteByBusinessId(businessId);
//		//删除流程数据
//		this.bpmnService.stopProcess(Constants.FORMAL_ASSESSMENT, businessId);
		baseMongo.deleteById(businessId, Constants.RCM_FORMALASSESSMENT_INFO);
		this.formalAssessmentInfoCreateMapper.delete(businessId);
	}
	
	@Override
	public void deleteByIdSyncTz(String businessId) {
		//删除系统数据
		this.deleteById(businessId);
		//调用投资接口
		tzClient.setBusinessId(businessId);
		tzClient.setStatus("5");
		tzClient.setLocation("");
		Thread t = new Thread(tzClient);
		t.start();
	}

	@Override
	public Map<String, Object> queryMongoById(String businessId) {
		return this.baseMongo.queryById(businessId, Constants.RCM_FORMALASSESSMENT_INFO);
	}

	@Override
	public Map<String, Object> queryOracleById(String businessId) {
		return this.projectMapper.queryById(businessId);
	}

	@Override
	public Map<String, Object> queryReportById(String businessId) {
		BasicDBObject queryWhere = new BasicDBObject();
		queryWhere.put("projectFormalId", businessId);
		List<Map<String, Object>> list = this.baseMongo.queryByCondition(queryWhere, Constants.RCM_FORMALREPORT_INFO);
		if(list != null && list.size() > 0){
			return list.get(0);
		}else{
			return null;
		}
	}
	@Override
	public Map<String, Object> queryProjectExperienceById(String businessId) {
		BasicDBObject queryWhere = new BasicDBObject();
		queryWhere.put("projectFormalId", businessId);
		List<Map<String, Object>> list = this.baseMongo.queryByCondition(queryWhere, Constants.RCM_PROJECTEXPERIENCE_INFO);
		if(list != null && list.size() > 0){
			return list.get(0);
		}else{
			return null;
		}
	}
	@Override
	public void updateOracleById(String businessId, Map<String, Object> params) {
		params.put("businessId", businessId);
		this.projectMapper.updateOracleById(params);
	}
	
	@SuppressWarnings("unchecked")
	public void saveReviewBaseInfo2Oracle(String objectId, String processType,Document doc){
		Document apply = doc.get("apply", Document.class);
		//业务ID
		String businessId = objectId;
		//申报单位
		Document reportingUnit = apply.get("reportingUnit", Document.class);
		String reportingUnitName = reportingUnit.getString("name");
		String reportingUnitId = reportingUnit.getString("value");
		//项目名称
		String projectName = apply.getString("projectName");
		//项目编码
		String projectNo = apply.getString("projectNo");
		//业务类型
		List<Document> serviceType = apply.get("serviceType", ArrayList.class);
		String serviceTypeId = getStringFromListDoc(serviceType, "KEY");
		String serviceTypeName = getStringFromListDoc(serviceType, "VALUE");
		//二级业务类型
		List<Document> projectTypeList = apply.get("projectType", ArrayList.class);
		String projectTypeIds = getStringFromListDoc(projectTypeList, "KEY");
		String projectTypNames = getStringFromListDoc(projectTypeList, "VALUE");
		//投资模式
		Boolean investmentModel = apply.getBoolean("investmentModel");//PPP和非PPP
		List<Document> projectModelList = apply.get("projectModel", ArrayList.class);
		String projectModelIds = getStringFromListDoc(projectModelList, "KEY");
		String projectModelNames = getStringFromListDoc(projectModelList, "VALUE");
		String is_supplement_review ="0";
		if(null!=doc.get("is_supplement_review") && !"".equals(doc.get("is_supplement_review"))){
			is_supplement_review=doc.get("is_supplement_review").toString();
		}
		String istz ="1";
		if(null!=doc.get("istz") && !"".equals(doc.get("istz"))){
			istz=doc.get("istz").toString();
		}
		//执行保存操作
		Map<String, Object> insertMap = new HashMap<String, Object>();
		insertMap.put("id", Util.getUUID());
		insertMap.put("businessId", businessId);
		insertMap.put("projectName", projectName);
		insertMap.put("projectNo", projectNo);
		insertMap.put("reportingUnitName", reportingUnitName);
		insertMap.put("reportingUnitId", reportingUnitId);
		insertMap.put("serviceTypeId", serviceTypeId);
		insertMap.put("serviceTypeName", serviceTypeName);
		insertMap.put("projectTypeIds", projectTypeIds);
		insertMap.put("projectTypNames", projectTypNames);
		insertMap.put("investmentModel", investmentModel==null ? Boolean.FALSE:investmentModel);
		insertMap.put("projectModelIds", projectModelIds);
		insertMap.put("projectModelNames", projectModelNames);
		insertMap.put("approveState", "");
		insertMap.put("type", processType);
		insertMap.put("wfState", "0");
		insertMap.put("is_supplement_review", is_supplement_review);
		insertMap.put("istz", istz);
		Document pertainArea = (Document) apply.get("pertainArea");
		if(pertainArea!=null && pertainArea.get("KEY")!=null){
			insertMap.put("pertainareaId", pertainArea.get("KEY"));
		}
		Map<String, Object> oldPrj = this.projectMapper.queryById(businessId);
		if(Util.isNotEmpty(oldPrj)){
			insertMap.put("wfState", oldPrj.get("WF_STATE"));
		}
		this.projectMapper.deleteByBusinessId(businessId);
		this.projectMapper.insert(insertMap);
	}

	//获取List<Document>中的
	private String getStringFromListDoc(List<Document> listDoc, String key){
		String str = "";
		if(Util.isNotEmpty(listDoc)){
			for(Document doc : listDoc){
				str += doc.getString(key)+",";
			}
		}
		str = StringUtils.removeEnd(str, ",");
		return str;
	}
	
	@Override
	public Map<String, Object> querySummaryById(String businessId) {
		return this.baseMongo.queryById(businessId, Constants.RCM_FORMAL_SUMMARY);
	}

}

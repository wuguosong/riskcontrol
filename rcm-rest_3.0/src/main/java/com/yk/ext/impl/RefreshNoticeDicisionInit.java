package com.yk.ext.impl;

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

import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.yk.common.IBaseMongo;
import com.yk.common.SpringUtil;
import com.yk.ext.Initable;
import com.yk.rcm.noticeofdecision.service.INoticeDecisionInfoService;
import com.yk.rcm.project.dao.IWsCallMapper;
import common.Constants;

@Service("refreshNoticeDicisionInit")
@Transactional
public class RefreshNoticeDicisionInit implements Initable{

	@Resource
	private IBaseMongo baseMongo;
	@Resource
	private IWsCallMapper wsCallMapper;
	
	@Override
	public void execute() {
		MongoCollection<Document> noticeCell = this.baseMongo.getCollection(Constants.RCM_NOTICEOFDECISION_INFO);
		FindIterable<Document> noticeDicisionCell = noticeCell.find();
		noticeDicisionCell.forEach(new Block<Document>(){
			
			@Override
			public void apply(Document t) {
				Document returnNoticeDicision = selectMongoData(t);
				String id =  returnNoticeDicision.get("_id").toString();
				Map<String, Object> row = baseMongo.queryById(id, Constants.RCM_NOTICEDECISION_INFO);
				if(!Util.isEmpty(row)){
					baseMongo.updateSetByObjectId(id, returnNoticeDicision, Constants.RCM_NOTICEDECISION_INFO);
				}else{
					baseMongo.save(returnNoticeDicision, Constants.RCM_NOTICEDECISION_INFO);
				}
			}
		});
		//查oracle数据
		List<Map<String, Object>> noticeDicisionList = this.wsCallMapper.queryNotice();
			for (int i = 0; i < noticeDicisionList.size(); i++) {
					Map<String,Object> noticeDicision = noticeDicisionList.get(i);
					String businessid = (String) noticeDicision.get("BUSINESSID");
					String reviewoftotalinvestment = (String) noticeDicision.get("REVIEWOFTOTALINVESTMENT");
					String decisionstage = (String) noticeDicision.get("DECISIONSTAGE");
					String dateofmeeting = (String) noticeDicision.get("DATEOFMEETING");
					String consenttoinvestment = (String) noticeDicision.get("CONSENTTOINVESTMENT");
					String responsibilityunitvalue = (String) noticeDicision.get("RESPONSIBILITYUNITVALUE");
					String wf_state = (String) noticeDicision.get("WF_STATE");
					String apply_date = (String) noticeDicision.get("APPLY_DATES");
					String create_date = (String) noticeDicision.get("CREATE_DATES");
					String last_update_date = (String) noticeDicision.get("LAST_UPDATE_DATES");
					String projectformalid = (String) noticeDicision.get("PROJECTFORMALID");
					String reportingunitid= wsCallMapper.getUnitIdById(businessid);
					String createby = wsCallMapper.getGreateBy(businessid,"0706");
					
					Map<String,Object> params = new HashMap<String,Object>();
					params.put("businessId", businessid);
//					params.put("reportingunit", reportingunit);
					//params.put("contractScale", null);
					//params.put("evaluationScale", null);
					params.put("reviewOfTotalInvestment", reviewoftotalinvestment);
					params.put("decisionStage", decisionstage);
					params.put("dateOfMeeting", dateofmeeting);
					params.put("consentToInvestment", consenttoinvestment);
					params.put("responsibilityUnitValue", responsibilityunitvalue);
					params.put("wf_State", wf_state);
					params.put("apply_date", apply_date);
					params.put("create_date", create_date);
					params.put("last_update_date", last_update_date);
					params.put("projectFormalid", projectformalid);
					params.put("reportingUnitId", reportingunitid);
					params.put("createBy", createby);
					params.put("oldData", "0");
					
					if(wf_state.equals("1") || wf_state.equals("2")){
						params.put("oldData", "1");
					}
					String id = (String) params.get("businessid");
					List<Map<String, Object>> not = wsCallMapper.queryOracleById(businessid);
					INoticeDecisionInfoService noticeDecisionInfoService = (INoticeDecisionInfoService)SpringUtil.getBean("noticeDecisionInfoService");
					if(!Util.isEmpty(not)){
						noticeDecisionInfoService.deleteOracle(businessid);
						noticeDecisionInfoService.insert(params);
					}else{
						noticeDecisionInfoService.insert(params);
					}
				}
	}
	//查询mongodb数据
	private Document selectMongoData(Document t){
			String id = t.get("_id").toString();
			Document createBy = (Document) t.get("createBy");
			String projectFormalId = (String) t.get("projectFormalId");
			String projectName = (String) t.get("projectName");
			String value= wsCallMapper.getUnitIdById(id);
			String name = wsCallMapper.getorgpkvalueIdById(value);
			String contractScale = (String) t.get("contractScale");
			String evaluationScale = (String) t.get("evaluationScale");
			String reviewOfTotalInvestment = (String) t.get("reviewOfTotalInvestment");
			String additionalReview = (String) t.get("additionalReview");
			String dateOfMeeting = (String) t.get("dateOfMeeting");
			String decisionStage = (String) t.get("decisionStage");
			String consentToInvestment = (String) t.get("consentToInvestment");
			String implementationMatters = (String) t.get("implementationMatters");
			Document subjectOfImplementation = (Document) t.get("subjectOfImplementation");
			String equityRatio = (String) t.get("equityRatio");
			String registeredCapital = (String) t.get("registeredCapital");
			Document responsibilityUnit = (Document) t.get("responsibilityUnit");
			
			Object object = t.get("personLiable");
			String person = object.toString();
			person = person.substring(person.length()-1);
			List<Document> list = new ArrayList<Document>();
			if(person.equals("]")){
				List<Document> personLiableList = (List<Document>) t.get("personLiable");
				for(int i=0; i<personLiableList.size(); i++){
					Document map = personLiableList.get(i);
					String names = (String) map.get("name");
					String values = (String) map.get("value");
					Document personLia = new Document();
					personLia.put("name", names);
					personLia.put("value", values);
					list.add(personLia);
				}
			}else{
				Document personLiable = (Document) t.get("personLiable");
				list.add(personLiable);
			}
			
			String implementationRequirements = (String) t.get("implementationRequirements");
			String create_date = (String) t.get("create_date");
			String currentTimeStamp = (String) t.get("currentTimeStamp");
			
			Document noticeDicision = new Document();
			noticeDicision.put("_id", new ObjectId(id));
			noticeDicision.put("createBy", createBy);
			noticeDicision.put("projectFormalId", projectFormalId);
			noticeDicision.put("projectName", projectName);
			Document reportingUnitNew = new Document();
			reportingUnitNew.put("value", value);
			reportingUnitNew.put("name", name);
			noticeDicision.put("reportingUnit", reportingUnitNew);
			noticeDicision.put("contractScale", contractScale);
			noticeDicision.put("evaluationScale", evaluationScale);
			noticeDicision.put("reviewOfTotalInvestment", reviewOfTotalInvestment);
			noticeDicision.put("additionalReview", additionalReview);
			noticeDicision.put("dateOfMeeting", dateOfMeeting);
			noticeDicision.put("decisionStage", decisionStage);
			noticeDicision.put("consentToInvestment", consentToInvestment);
			noticeDicision.put("implementationMatters", implementationMatters);
			noticeDicision.put("subjectOfImplementation", subjectOfImplementation);
			noticeDicision.put("equityRatio", equityRatio);
			noticeDicision.put("registeredCapital", registeredCapital);
			noticeDicision.put("responsibilityUnit", responsibilityUnit);
			noticeDicision.put("personLiable", list);
			noticeDicision.put("implementationRequirements", implementationRequirements);
			noticeDicision.put("create_date", create_date);
			noticeDicision.put("currentTimeStamp", currentTimeStamp);
			noticeDicision.put("oldData", "1");
			
			return noticeDicision;
	}
}

package com.yk.ext.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import util.Util;

import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.yk.common.IBaseMongo;
import com.yk.ext.Initable;
import com.yk.flow.util.JsonUtil;
import com.yk.rcm.decision.serevice.IDecisionService;
import com.yk.rcm.formalAssessment.service.IFormalAssessmentInfoService;

import common.Constants;
/**
 * 
 * @author yaphet
 * 本类用于同步新旧正式评审数据，新版正式评审的审核阶段
 * 在正式评审上线时候要最先执行
 * 
 * 

审核阶段, 1:分配任务之前；2：分配任务节点；3：风控审批阶段；

if(如果以businessId为条件匹配 mongoDB.formal_meeting_info 中 formalId 字段,信息存在){
	3.5:已填写参会信息 ;
}

if(如果以businessId为条件匹配 mongoDB.rcm_formalReport_info 中 projectFormalId 字段 且 policyDecision.decisionMakingCommitteeStaffFiles 字段长度 小于 0){
	3.7:已提交报告但未提交决策会材料 ; 
}

if(oraclewf_state == 2 && 如果以businessId为条件匹配 mongoDB.formal_meeting_info 中 formalId 字段 且 decisionOpinionList 字段长度等于0){
	4：提交报告材料后，安排上会前；
}

if(如果以businessId为条件匹配 oracle.rcm_decision_resolution 中 FORMAL_ID 字段 且 VOTE_STATUS字段值不为2){
	5：已安排上会，但还未过会；
}

if(如果以businessId为条件匹配 oracle.rcm_decision_resolution 中 FORMAL_ID 字段 且 VOTE_STATUS字段值为2){
	6：已过会；
}

if(如果以businessId为条件匹配 mongoDB.rcm_noticeDecision_info 中 projectFormalId 字段,信息存在){
	7：新增完决策通知书；
}

9:无需上会 ;
 */
@Service("refreshFormalAssessment")
@Transactional
public class RefreshFormalAssessment implements Initable{

	@Resource
	private IBaseMongo baseMongo;
	
	@Resource
	private IDecisionService decisionService;
	
	@Resource
	private IFormalAssessmentInfoService formalAssessmentInfoService;
	
	@Override
	public void execute() {
		//刷oracle数据
		initOracle();
		//刷mongo数据
		initMongo();
	}
	
	//刷oracle数据
	private void initOracle(){
		
		//rcm_project_info中的数据
		List<Map<String, Object>> allOldFormal = formalAssessmentInfoService.getAllOldFormal();
		
		for (Map<String, Object> oldFormal : allOldFormal) {
			
			//业务id
			String businessId = (String) oldFormal.get("BUSINESS_ID");
			
			List<Map<String, Object>>  projectRelation = formalAssessmentInfoService.getOldProjectRelationByBusinessId(businessId);
			
			Map<String,Object> param = new HashMap<String,Object>();
			param.put("businessid", businessId);
			param.put("projectName", (String) oldFormal.get("PROJECT_NAME"));
			param.put("projectNum", (String) oldFormal.get("PROJECT_NO"));
			param.put("reportingUnit_id", (String) oldFormal.get("REPORTING_UNIT_ID"));
			String serviceTypeString = oldFormal.get("SERVICE_TYPE_ID").toString();
			String[] serviceTypeArray = serviceTypeString.split(",");
			StringBuffer buff = new StringBuffer();
			for (int i = 0; i < serviceTypeArray.length; i++) {
				String serviceType = serviceTypeArray[i];
				if("1".equals(serviceType) || "4".equals(serviceType)){
					buff.append("1401");
				}else if("2".equals(serviceType)){
					buff.append("1402");
				}else if("5".equals(serviceType)){
					buff.append("1404");
				}else if("6".equals(serviceType)){
					buff.append("1403");
				}else if("7".equals(serviceType)){
					buff.append("1405");
				}else{
					buff.append(serviceType);
				}
				if(i != serviceTypeArray.length -1){
					buff.append(",");
				}
			}
			param.put("serviceType_id", buff.toString());
			param.put("investment_model", (String) oldFormal.get("INVESTMENT_MODEL"));
			param.put("project_model_ids", (String) oldFormal.get("PROJECT_MODEL_IDS"));
			
			String need_meeting = (String) oldFormal.get("NEED_MEETING");
			
			if(Util.isNotEmpty(need_meeting)){
				if(need_meeting.equals("0")){
					need_meeting = "1";
				}else if(need_meeting.equals("1")){
					need_meeting = "0";
				}
			}
			
			param.put("need_meeting", need_meeting);
			
			param.put("meeting_date", (String) oldFormal.get("MEETING_DATEA"));
			param.put("is_supplement_review", (String) oldFormal.get("IS_SUPPLEMENT_REVIEW"));
			param.put("emergencyLevel", (String) oldFormal.get("EMERGENCYLEVEL"));
			param.put("isUrgent", (String) oldFormal.get("ISURGENT"));
			param.put("pertainAreaId", (String) oldFormal.get("PERTAINAREAID"));
			param.put("isTZ", (String) oldFormal.get("ISTZ"));
			param.put("wf_state", (String) oldFormal.get("WF_STATE"));
			param.put("apply_date",  (String) oldFormal.get("APPLY_DATEA"));
			param.put("complete_date", (String) oldFormal.get("COMPLETE_DATEA"));
			String auditStage = initAuditStage(oldFormal);
			//审核阶段
			param.put("auditStage", auditStage);
			if(oldFormal.get("WF_STATE").toString().equals("0")){
				param.put("oldData", "0");
			}else{
				param.put("oldData", "1");
			}
			
			//投资经理 0701
			String createBy = "";
			//大区审批人ID 0702
			String largeAreaPersonId = "";
			//投资中心/水环境审批人ID 0704
			String serviceTypePersonId = "";
			//分配任务审批人ID 0712
			String taskPersonId = "";
			//法律评审负责人ID 0710
			String legalReviewPersonId = "";
			//专业评审负责人ID 0706
			String reviewPersonId = "";
			//基层法务评审人ID 0703
			String grassRootsLegalPersonId = "";
			//一级法务评审人ID 
			String firstLevelLawyerPersonId = "";
			//固定小组成员IDs 0707
			String fixedGroupPersonIds = "";
			
			for (Map<String, Object> relation : projectRelation) {
				if(relation.get("RELATION_TYPE").toString().equals("0701")){
					if(Util.isNotEmpty(relation.get("USER_ID"))){
						createBy = relation.get("USER_ID").toString();
					}
				}
				if(relation.get("RELATION_TYPE").toString().equals("0702")){
					if(Util.isNotEmpty(relation.get("USER_ID"))){
						largeAreaPersonId = relation.get("USER_ID").toString();
					}
				}
				if(relation.get("RELATION_TYPE").toString().equals("0704")){
					if(Util.isNotEmpty(relation.get("USER_ID"))){
						serviceTypePersonId = relation.get("USER_ID").toString();
					}
				}
				if(relation.get("RELATION_TYPE").toString().equals("0712")){
					if(Util.isNotEmpty(relation.get("USER_ID"))){
						taskPersonId = relation.get("USER_ID").toString();
					}
				}
				if(relation.get("RELATION_TYPE").toString().equals("0710")){
					if(Util.isNotEmpty(relation.get("USER_ID"))){
						legalReviewPersonId = relation.get("USER_ID").toString();
					}
				}
				if(relation.get("RELATION_TYPE").toString().equals("0706")){
					if(Util.isNotEmpty(relation.get("USER_ID"))){
						reviewPersonId = relation.get("USER_ID").toString();
					}
				}
				if(relation.get("RELATION_TYPE").toString().equals("0703")){
					if(Util.isNotEmpty(relation.get("USER_ID"))){
						grassRootsLegalPersonId = relation.get("USER_ID").toString();
					}
				}
				if(relation.get("RELATION_TYPE").toString().equals("0701")){
					if(Util.isNotEmpty(relation.get("USER_ID"))){
						createBy = relation.get("USER_ID").toString();
					}
				}
				if(relation.get("RELATION_TYPE").toString().equals("0707")){
					if(Util.isNotEmpty(relation.get("USER_ID"))){
						fixedGroupPersonIds = relation.get("USER_ID").toString();
					}
				}
			}
			param.put("largeAreaPersonId", largeAreaPersonId);
			param.put("serviceTypePersonId", serviceTypePersonId);
			param.put("taskPersonId", taskPersonId);
			param.put("legalReviewPersonId", legalReviewPersonId);
			param.put("reviewPersonId", reviewPersonId);
			param.put("grassRootsLegalPersonId", grassRootsLegalPersonId);
			param.put("firstLevelLawyerPersonId", firstLevelLawyerPersonId);
			param.put("fixedGroupPersonIds", fixedGroupPersonIds);
			param.put("createBy", createBy);
			
			param.put("create_date", oldFormal.get("CREATE_DATEA"));
			param.put("last_update_date", (String) oldFormal.get("LAST_UPDATE_DATEA"));
			
			//删除
			formalAssessmentInfoService.deleteByBusinessId(businessId);
			//新增
			formalAssessmentInfoService.save(param);
		}
		
	}
	
	private String initAuditStage(Map<String, Object> oldFormal){
		//业务id
		String businessId = (String) oldFormal.get("BUSINESS_ID");
		//审核状态
		String wf_state = oldFormal.get("WF_STATE").toString();
		
		if(wf_state.equals("0") || wf_state.equals("3")){
			return "1";
		}
		
		if(wf_state.equals("1")){
			//孙敏uuid = 0001N61000000000198L
			//查询孙敏有无当前项目的待办 参数当前人id 项目id
			String userId = "0001N61000000000198L";
			
			Map<String, Object> waiting = formalAssessmentInfoService.queryWaitingByConditions(userId,businessId);
			
			if(Util.isNotEmpty(waiting)){
				//有待办
				return "2";
			}else{
				//无待办
				//查询relation表0706,0710是否有数据、有数据是否有人的ID 评审负责人
				Map<String, Object> queryRelationByTypeId = formalAssessmentInfoService.queryRelationByTypeId(businessId, "0706");
				if(Util.isNotEmpty(queryRelationByTypeId)){
					
					if(Util.isNotEmpty(queryRelationByTypeId.get("USER_ID"))){
						//有法律/评审负责人，需要查询待办
						String reviewLeaderId = queryRelationByTypeId.get("USER_ID").toString();
						
						Map<String, Object> waitingOfRreviewLeader = formalAssessmentInfoService.queryWaitingByConditions(reviewLeaderId,businessId);
						if(Util.isNotEmpty(waitingOfRreviewLeader)){
							//有待办，3阶段
							return "3";
						}else{
							//没有代办查询已办
							Map<String, Object> auditedOfRreviewLeader= formalAssessmentInfoService.queryAuditedByConditions(reviewLeaderId,businessId);
							if(Util.isNotEmpty(auditedOfRreviewLeader)){
								//有已办，返回3
								return "3";
							}else{
								//法律评审、专业评审没已办返回1
								return "1";
							}
						}
					}else{
						return "1";
					}
				}else{
					//没有法律评审负责人
					return "1";
				}
			}
		}
		
		//如果旧数据为  不上会，则把该新流程设置为无需上会
		Object need_meeting = oldFormal.get("NEED_MEETING");
		if(Util.isNotEmpty(need_meeting) && "1".equals(need_meeting.toString())){
			return "9";
		}
		
		BasicDBObject queryWhere = new BasicDBObject();
		queryWhere.put("projectFormalId", businessId);
		List<Map<String, Object>> noticeDecision = baseMongo.queryByCondition(queryWhere, Constants.RCM_NOTICEDECISION_INFO);
		if(Util.isNotEmpty(noticeDecision)){
			//如果以businessId为条件匹配 mongoDB.rcm_noticeDecision_info 中 projectFormalId 字段,信息存在。
			return "7";
		}
		
		Map<String, Object> decisionInfo = decisionService.queryByBusinessId(businessId);
		if(Util.isNotEmpty(decisionInfo)){
			Object voteStatus = decisionInfo.get("VOTE_STATUS");
			if("2".equals(voteStatus.toString())){
				//如果以businessId为条件匹配 oracle.rcm_decision_resolution 中 FORMAL_ID 字段 且 VOTE_STATUS字段值为2,信息存在。
				return "6";
			}
			else{
				//如果以businessId为条件匹配 oracle.rcm_decision_resolution 中 FORMAL_ID 字段 且 VOTE_STATUS字段值不为2,信息存在。
				return "5";
			}
		}
		//审核阶段, 1:分配任务之前；2：分配任务节点；3：风控审批阶段；3.5:已填写参会信息 ; 3.7:已提交报告但未提交决策会材料 ; 
		//4：提交报告材料后，安排上会前；5：已安排上会，但还未过会；6：已过会；7：新增完决策通知书；9:无需上会 ;
		queryWhere.clear();
		queryWhere.put("formalId", businessId);
		List<Map<String, Object>> formalMeetingInfo = baseMongo.queryByCondition(queryWhere, Constants.FORMAL_MEETING_INFO);
		if(Util.isNotEmpty(formalMeetingInfo)){
			Object decisionOpinionList = formalMeetingInfo.get(0).get("decisionOpinionList");
			if(wf_state.equals("2") && Util.isEmpty(decisionOpinionList)){
				//oraclewf_state == 2 && 如果以businessId为条件匹配 mongoDB.formal_meeting_info 中 formalId 字段 且 decisionOpinionList 字段长度等于0
				return "4";
			}
		}

		List<Map<String, Object>> rcmFormalreportInfo = baseMongo.queryByCondition(queryWhere, Constants.RCM_FORMALREPORT_INFO);
		if(Util.isNotEmpty(rcmFormalreportInfo)){
			Object policyDecisionObject = rcmFormalreportInfo.get(0).get("policyDecision");
			boolean isPolicyDecisionObjectEmpty = Util.isEmpty(policyDecisionObject);
			Map<String, Object> policyDecisionMap = (Map<String, Object>) (isPolicyDecisionObjectEmpty ? policyDecisionObject : null);
			if(isPolicyDecisionObjectEmpty || Util.isEmpty(policyDecisionMap.get("decisionMakingCommitteeStaffFiles"))){
				//如果以businessId为条件匹配 mongoDB.rcm_formalReport_info 中 projectFormalId 字段 且 policyDecision.decisionMakingCommitteeStaffFiles 字段长度 小于 1
				return "3.7";
			}
		}
		
		if(Util.isNotEmpty(formalMeetingInfo)){
			//如果以businessId为条件匹配 mongoDB.formal_meeting_info 中 formalId 字段,信息存在
			return "3.5";
		}
		return null;
	}
	
	//刷mongoDB数据
	private void initMongo(){
		
		MongoCollection<Document> projectFormalReviewCollection = this.baseMongo.getCollection(Constants.RCM_PROJECTFORMALREVIEW_INFO);
		
		FindIterable<Document> projectFormalReview = projectFormalReviewCollection.find();
		
		projectFormalReview.forEach(new Block<Document>(){
			@Override
			public void apply(Document olddoc) {
				String json = JsonUtil.toJson(olddoc);
				Document doc = Document.parse(json);
				String businessId = olddoc.get("_id").toString();
				
				//先删除,防止出错不同步
				baseMongo.deleteById(businessId, Constants.RCM_FORMALASSESSMENT_INFO);
				//验证此数据是否在oracle中存在 
				Map<String, Object> oracleByBusinessId = formalAssessmentInfoService.getOracleByBusinessId(businessId);
				if(Util.isEmpty(oracleByBusinessId)){
					return;
				}
				
				//name:value--KEY:VALUE
				doc = formatMongoData(doc);
				
				//刷固定小组成员意见
				List<Map<String,Object>> optionList = formalAssessmentInfoService.queryFixGroupOption(businessId);
				
				for (Map<String, Object> option : optionList) {
					option.remove("create_time");
				}
				doc.put("fixGroupOption", optionList);
				doc.put("_id", new ObjectId(businessId));
				baseMongo.save(doc, Constants.RCM_FORMALASSESSMENT_INFO);
			}
		});
	}
	
	private Document formatMongoData(Document doc){
		Document apply = (Document) doc.get("apply");
		
		Document investmentManager = (Document) apply.get("investmentManager");
		if(Util.isNotEmpty(investmentManager)){
			investmentManager.put("NAME", investmentManager.get("name").toString());
			investmentManager.put("VALUE", investmentManager.get("value").toString());
			investmentManager.remove("name");
			investmentManager.remove("value");
			apply.put("investmentManager", investmentManager);
		}
		
		
		Document investmentPerson = (Document) apply.get("investmentPerson");
		if(Util.isNotEmpty(investmentPerson)){
			investmentPerson.put("NAME", investmentPerson.get("name").toString());
			investmentPerson.put("VALUE", investmentPerson.get("value").toString());
			investmentPerson.remove("name");
			investmentPerson.remove("value");
			apply.put("investmentPerson", investmentPerson);
		}
		Document directPerson = (Document) apply.get("directPerson");
		if(Util.isNotEmpty(directPerson)){
			directPerson.put("NAME", directPerson.get("name").toString());
			directPerson.put("VALUE", directPerson.get("value").toString());
			directPerson.remove("name");
			directPerson.remove("value");
			apply.put("directPerson", directPerson);
		}
		Document reportingUnit = (Document) apply.get("reportingUnit");
		if(Util.isNotEmpty(reportingUnit)){
			reportingUnit.put("VALUE", reportingUnit.get("name").toString());
			reportingUnit.put("KEY", reportingUnit.get("value").toString());
			reportingUnit.remove("name");
			reportingUnit.remove("value");
			apply.put("directPerson", directPerson);
		}
		Document companyHeader = (Document) apply.get("companyHeader");
		if(Util.isNotEmpty(companyHeader)){
			companyHeader.put("NAME", companyHeader.get("name").toString());
			companyHeader.put("VALUE", companyHeader.get("value").toString());
			companyHeader.remove("name");
			companyHeader.remove("value");
			apply.put("directPerson", directPerson);
		}
		Document grassrootsLegalStaff = (Document) apply.get("grassrootsLegalStaff");
		if(Util.isNotEmpty(grassrootsLegalStaff)){
			grassrootsLegalStaff.put("NAME", grassrootsLegalStaff.get("name").toString());
			grassrootsLegalStaff.put("VALUE", grassrootsLegalStaff.get("value").toString());
			grassrootsLegalStaff.remove("name");
			grassrootsLegalStaff.remove("value");
			apply.put("directPerson", directPerson);
		}
		
		
		String create_by  = "";
		String create_name  = "";
		if(Util.isNotEmpty(investmentManager)){
			create_by = investmentManager.get("VALUE").toString();
			create_name = investmentManager.get("NAME").toString();
		}
		//createby
		Document createby = new Document();
		createby.put("VALUE", create_by);
		createby.put("NAME", create_name);
		doc.put("createby", createby);
		doc.remove("create_by");
		doc.remove("create_name");
		
		//两个附件
		return doc;
	}
}

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

import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.yk.common.IBaseMongo;
import com.yk.ext.Initable;
import com.yk.flow.util.JsonUtil;
import com.yk.rcm.pre.service.IPreInfoService;

import common.Constants;

@Service("refreshPreInfoData")
@Transactional
public class RefreshPreInfoData implements Initable{

	@Resource
	private IBaseMongo baseMongo;
	
	@Resource IPreInfoService preInfoService;
	
	@Override
	public void execute() {
		
		//刷oracle数据	先
		initOracle();
		//刷mongo数据		后
		initMongo();
		
	}
	private void initOracle(){
		//rcm_project_info中的数据
		List<Map<String, Object>> allOldPre = preInfoService.getAllOldPre();
		
		for (Map<String, Object> oldPre : allOldPre) {
			
			//业务id
			String businessId = (String) oldPre.get("BUSINESS_ID");
			
			List<Map<String, Object>>  projectRelation = preInfoService.getOldProjectRelationByBusinessId(businessId);
			
			Map<String,Object> param = new HashMap<String,Object>();
			param.put("businessId", businessId);
			param.put("projectName", (String) oldPre.get("PROJECT_NAME"));
			param.put("projectNum", (String) oldPre.get("PROJECT_NO"));
			param.put("reportingUnit_id", (String) oldPre.get("REPORTING_UNIT_ID"));
			String serviceTypeString = oldPre.get("SERVICE_TYPE_ID").toString();
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
			param.put("investmentModel", (String) oldPre.get("INVESTMENT_MODEL"));
			param.put("project_model_ids", (String) oldPre.get("PROJECT_TYPE_IDS"));
			
			String need_meeting = (String) oldPre.get("NEED_MEETING");
			
			if(Util.isNotEmpty(need_meeting)){
				if(need_meeting.equals("0")){
					need_meeting = "1";
				}else if(need_meeting.equals("1")){
					need_meeting = "0";
				}
			}
			
			param.put("need_meeting", need_meeting);
			
			param.put("meeting_date", (String) oldPre.get("MEETING_DATEA"));
			param.put("is_supplement_review", (String) oldPre.get("IS_SUPPLEMENT_REVIEW"));
			param.put("emergencyLevel", (String) oldPre.get("EMERGENCYLEVEL"));
			param.put("isUrgent", (String) oldPre.get("ISURGENT"));
			param.put("pertainAreaId", (String) oldPre.get("PERTAINAREAID"));
			param.put("istz", (String) oldPre.get("ISTZ"));
			param.put("wf_state", (String) oldPre.get("WF_STATE"));
			param.put("apply_date",  (String) oldPre.get("APPLY_DATEA"));
			param.put("complete_date", (String) oldPre.get("COMPLETE_DATEA"));
			String auditStage = initAuditStage(oldPre);
			//审核阶段
			param.put("stage", auditStage);
			if(oldPre.get("WF_STATE").toString().equals("0")){
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
			//专业评审负责人ID 0706
			String reviewPersonId = "";
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
				if(relation.get("RELATION_TYPE").toString().equals("0706")){
					if(Util.isNotEmpty(relation.get("USER_ID"))){
						reviewPersonId = relation.get("USER_ID").toString();
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
			param.put("reviewPersonId", reviewPersonId);
			param.put("fixedGroupPersonIds", fixedGroupPersonIds);
			param.put("createBy", createBy);
			
			param.put("create_date", oldPre.get("CREATE_DATEA"));
			param.put("last_update_date", (String) oldPre.get("LAST_UPDATE_DATEA"));
			
			//删除
			preInfoService.deleteByBusinessId(businessId);
			//新增
			preInfoService.save(param);
		}
	}
	
	//刷mongoDB数据
	private void initMongo(){
		
		MongoCollection<Document> preAssessmentCollection = this.baseMongo.getCollection(Constants.PREASSESSMENT);
		
		FindIterable<Document> preAssessment = preAssessmentCollection.find();
		
		preAssessment.forEach(new Block<Document>(){
			@SuppressWarnings("unchecked")
			@Override
			public void apply(Document olddoc) {
				String json = JsonUtil.toJson(olddoc);
				Document doc = Document.parse(json);
				String businessId = olddoc.get("_id").toString();
				
				//先删除,防止出错不同步
				baseMongo.deleteById(businessId, Constants.RCM_PRE_INFO);
				//验证此数据是否在oracle中存在 
				Map<String, Object> oracleByBusinessId = (Map<String, Object>) preInfoService.getPreInfoByID(businessId).get("oracle");
				if(Util.isEmpty(oracleByBusinessId)){
					return;
				}
				
				//name:value--KEY:VALUE
				doc = formatMongoData(doc);
				
				//刷固定小组成员意见
				List<Map<String,Object>> optionList = preInfoService.queryFixGroupOption(businessId);
				
				for (Map<String, Object> option : optionList) {
					option.remove("create_time");
				}
				doc.put("fixGroupOption", optionList);
				doc.put("_id", new ObjectId(businessId));
				baseMongo.save(doc, Constants.RCM_PRE_INFO);
			}
		});
	}
	
	//初始化阶段
	@SuppressWarnings("unchecked")
	private String initAuditStage(Map<String, Object> oldPre){
		//业务id
		String businessId = (String) oldPre.get("BUSINESS_ID");
		//审核状态
		String wf_state = oldPre.get("WF_STATE").toString();
		
		if(wf_state.equals("0") || wf_state.equals("3")){
			
			//查询报告
			Map<String, Object> preInfo = this.baseMongo.queryById(businessId, Constants.PREASSESSMENT);
			Map<String, Object> report = (Map<String, Object>) preInfo.get("reviewReport");
			if(Util.isNotEmpty(report)){
				return "3.7";
			}else{
				return "1";
			}
			
		}else if(wf_state.equals("1")){
			//孙敏uuid = 0001N61000000000198L
			//查询孙敏有无当前项目的待办 参数当前人id 项目id
			String userId = "0001N61000000000198L";
			
			Map<String, Object> waiting = preInfoService.queryWaitingByConditions(userId,businessId);
			
			if(Util.isNotEmpty(waiting)){
				//有待办
				return "2";
			}else{
				//无待办
				//查询relation表0706是否有数据、有数据是否有人的ID 评审负责人
				Map<String, Object> queryRelationByTypeId = preInfoService.queryRelationByTypeId(businessId, "0706");
				if(Util.isNotEmpty(queryRelationByTypeId)){
					
					if(Util.isNotEmpty(queryRelationByTypeId.get("USER_ID"))){
						//有评审负责人，需要查询待办
						String reviewLeaderId = queryRelationByTypeId.get("USER_ID").toString();
						
						Map<String, Object> waitingOfRreviewLeader = preInfoService.queryWaitingByConditions(reviewLeaderId,businessId);
						if(Util.isNotEmpty(waitingOfRreviewLeader)){
							//有待办，3阶段
							return "3";
						}else{
							//没有代办查询已办
							Map<String, Object> auditedOfRreviewLeader= preInfoService.queryAuditedByConditions(reviewLeaderId,businessId);
							if(Util.isNotEmpty(auditedOfRreviewLeader)){
								//有已办，返回3
								return "3";
							}else{
								//专业评审没已办返回1
								return "1";
							}
						}
					}else{
						return "1";
					}
				}else{
					//没有评审负责人
					return "1";
				}
			}
			
		}else if(wf_state.equals("2")){
			//查询报告
			Map<String, Object> preInfo = this.baseMongo.queryById(businessId, Constants.PREASSESSMENT);
			Map<String, Object> report = (Map<String, Object>) preInfo.get("reviewReport");
			
			if(Util.isNotEmpty(report)){
				return "3.7";
			}else{
				return "3";
			}
		}
		
		return null;
	}

	@SuppressWarnings("unchecked")
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
			apply.put("reportingUnit", reportingUnit);
		}
		Document companyHeader = (Document) apply.get("companyHeader");
		if(Util.isNotEmpty(companyHeader)){
			companyHeader.put("NAME", companyHeader.get("name").toString());
			companyHeader.put("VALUE", companyHeader.get("value").toString());
			companyHeader.remove("name");
			companyHeader.remove("value");
			apply.put("companyHeader", companyHeader);
		}
		Document grassrootsLegalStaff = (Document) apply.get("grassrootsLegalStaff");
		if(Util.isNotEmpty(grassrootsLegalStaff)){
			grassrootsLegalStaff.put("NAME", grassrootsLegalStaff.get("name").toString());
			grassrootsLegalStaff.put("VALUE", grassrootsLegalStaff.get("value").toString());
			grassrootsLegalStaff.remove("name");
			grassrootsLegalStaff.remove("value");
			apply.put("grassrootsLegalStaff", grassrootsLegalStaff);
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
		apply.put("createby", createby);
		apply.remove("create_by");
		apply.remove("create_name");
		//approveAttachment-attachmentNew-attachment_new-programmed/approved
		Document approveAttachment = (Document) doc.get("approveAttachment");
		if(Util.isNotEmpty(approveAttachment)){
			List<Document> attachmentNews = (List<Document>) approveAttachment.get("attachmentNew");
			if(Util.isNotEmpty(attachmentNews)){
				for (Document attachmentNew : attachmentNews) {
					if(Util.isNotEmpty(attachmentNew)){
						if(Util.isNotEmpty(attachmentNew.get("attachment_new"))){
							Document attachment_new = (Document) attachmentNew.get("attachment_new");
							Document approved = (Document) attachment_new.get("approved");
							Document programmed = (Document) attachment_new.get("programmed");
							
							if(Util.isNotEmpty(approved)){
								approved.put("NAME", approved.get("name").toString());
								approved.put("VALUE", approved.get("value").toString());
								approved.remove("name");
								approved.remove("value");
								attachment_new.put("approved", approved);
							}
							if(Util.isNotEmpty(programmed)){
								programmed.put("NAME", programmed.get("name").toString());
								programmed.put("VALUE", programmed.get("value").toString());
								programmed.remove("name");
								programmed.remove("value");
								attachment_new.put("programmed", programmed);
							}
						}
					}
				}
				
			}
		}
		//attachment-approved/programmed
		List<Document> attachmentList = (List<Document>) doc.get("attachment");
		if(Util.isNotEmpty(attachmentList)){
			for (Document attachment : attachmentList) {
				List<Document> files = (List<Document>) attachment.get("files");
				if(Util.isNotEmpty(files)){
					for (Document file : files) {
						Document approved = (Document) file.get("approved");
						Document programmed = (Document) file.get("programmed");
						if(Util.isNotEmpty(approved)){
							approved.put("NAME", approved.get("name").toString());
							approved.put("VALUE", approved.get("value").toString());
							approved.remove("name");
							approved.remove("value");
							file.put("approved", approved);
						}
						if(Util.isNotEmpty(programmed)){
							programmed.put("NAME", programmed.get("name").toString());
							programmed.put("VALUE", programmed.get("value").toString());
							programmed.remove("name");
							programmed.remove("value");
							file.put("programmed", programmed);
						}
						
					}
				}	
			}
		}
		return doc;
	}
}

package com.yk.rcm.formalAssessment.listener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.bson.Document;
import org.springframework.stereotype.Component;

import util.Util;

import com.mongodb.client.MongoCollection;
import com.yk.common.IBaseMongo;
import com.yk.power.service.IRoleService;
import com.yk.rcm.formalAssessment.dao.IFormalAssessmentInfoMapper;
import com.yk.rcm.formalAssessment.service.IFormalAssessmentAuditLogService;

import common.Constants;
@Component
public class QueryTaskMajorReviewListener implements ExecutionListener{

	private static final long serialVersionUID = 1L;
	@Resource 
	private IBaseMongo baseMongo;
	@Resource 
	private IRoleService roleService;
	@Resource
	private IFormalAssessmentAuditLogService formalAssessmentAuditLogService;
	
	@Resource
	private IFormalAssessmentInfoMapper formalAssessmentInfoMapper;
	
	private  static final String RCM_FORMALASSESSMENT_INFO = Constants.RCM_FORMALASSESSMENT_INFO; 

	@SuppressWarnings("unchecked")
	@Override
	public void notify(DelegateExecution execution) throws Exception {
		
		String businessKey = execution.getProcessBusinessKey();
	
		Map<String, Object> fromalAssessmentMongo = baseMongo.queryById(businessKey, RCM_FORMALASSESSMENT_INFO);
		Map<String, Object> taskallocation = (Map<String, Object>) fromalAssessmentMongo.get("taskallocation");
		
		List<Document> professionalReviewers = (List<Document>) taskallocation.get("professionalReviewers");
//		Document professionalReviewers = (Document) taskallocation.get("professionalReviewers");
		Map<String, Object> data = new HashMap<String,Object>();
		if(Util.isNotEmpty(professionalReviewers)){
			List<String> majorMembersList = new ArrayList<String>();
			for (Document ll : professionalReviewers) {
				String id = ll.getString("VALUE");
				majorMembersList.add(id);
			}
//			提交到专家评审时
//			根据选择的专家，在数据库里新建出对应的tab页，每个专家只能修改自己的tab页的数据
//			通过对应的专家id来确定，并且要定位到自己的tab页
			
			//如果流程回到评审负责人 再次选择了专家评审人，那么保留原来的数据，添加新的数据
			Object object = fromalAssessmentMongo.get("approveMajorCommonts");
			if(Util.isEmpty(object)){
				ArrayList<Document> documents = new ArrayList<Document>();
				for (String majorMemberId : majorMembersList) {
					ArrayList<Document> commentsList = new ArrayList<Document>();
					Map<String, Object> professionByUserid = formalAssessmentInfoMapper.getProfessionByUserid(majorMemberId);
					String reviewTypeValue = (String)professionByUserid.get("REVIEW_TYPE");
					String reviewTypeId = (String)professionByUserid.get("REVIEW_TEAM_ID");
					String userID = (String)professionByUserid.get("REVIEW_TEAM_MEMBERID");
					String userName = (String)professionByUserid.get("REVIEW_TEAM_MEMBERNAME");
					Document majorCommonts = new Document();
					//专家类别的document
					Document reviewTypeDocument = new Document();
					reviewTypeDocument.put("KEY", reviewTypeId);
					reviewTypeDocument.put("VALUE", reviewTypeValue);
					majorCommonts.put("reviewType", reviewTypeDocument);
					////专家用户的document
					Document majorApplyDocument = new Document();
					majorApplyDocument.put("NAME", userName);
					majorApplyDocument.put("VALUE", userID);
					majorCommonts.put("majorApply", majorApplyDocument);
					majorCommonts.put("commentsList", commentsList);
					documents.add(majorCommonts);
				}
				data.put("approveMajorCommonts", documents);
			}else{
				ArrayList<Document> approveMajorCommontList =  (ArrayList<Document>) object;
				List<String> majorApplyIdList = new ArrayList<String>();
				for (Document document : approveMajorCommontList) {
					Document majorApplyDocument = (Document)document.get("majorApply");
					String majorApplyId = majorApplyDocument.get("VALUE").toString();
					majorApplyIdList.add(majorApplyId);
					
				}
				//查出已有的专业评审和新选择的专业评审不同的人，再循环判断是否是已有的
				ArrayList<String> diffent =  this.getDiffent(majorApplyIdList, majorMembersList);
				if(Util.isNotEmpty(diffent)){
					for (String newUserId : diffent) {
						ArrayList<Document> commentsList = new ArrayList<Document>();
						Map<String, Object> professionByUserid = formalAssessmentInfoMapper.getProfessionByUserid(newUserId);
						String reviewTypeValue = (String)professionByUserid.get("REVIEW_TYPE");
						String reviewTypeId = (String)professionByUserid.get("REVIEW_TEAM_ID");
						String userID = (String)professionByUserid.get("REVIEW_TEAM_MEMBERID");
						String userName = (String)professionByUserid.get("REVIEW_TEAM_MEMBERNAME");
						Document majorCommonts = new Document();
						//专家类别的document
						Document reviewTypeDocument = new Document();
						reviewTypeDocument.put("KEY", reviewTypeId);
						reviewTypeDocument.put("VALUE", reviewTypeValue);
						majorCommonts.put("reviewType", reviewTypeDocument);
						////专家用户的document
						Document majorApplyDocument = new Document();
						majorApplyDocument.put("NAME", userName);
						majorApplyDocument.put("VALUE", userID);
						majorCommonts.put("majorApply", majorApplyDocument);
						majorCommonts.put("commentsList", commentsList);
						approveMajorCommontList.add(majorCommonts);
						data.put("approveMajorCommonts", approveMajorCommontList);
					}
				}
				
			}
			
			
			if(Util.isNotEmpty(data)){
				this.baseMongo.updateSetByObjectId(businessKey, data, Constants.RCM_FORMALASSESSMENT_INFO);
			}
		}
		

		Map<String, Object> variables = new HashMap<String,Object>();
		if(Util.isNotEmpty(professionalReviewers)){
			List<String> majorMembersList = new ArrayList<String>();
			for (Document ll : professionalReviewers) {
				String id = ll.getString("VALUE");
				majorMembersList.add(id);
			}
			variables.put("majorMembers", majorMembersList);
		}
		execution.setVariables(variables);
	
	}
	
	/**
      *  获取两个集合的不同元素
      * @param majorApplyIdList
      * @param majorMembersList
      * @return
      */
     public  ArrayList<String> getDiffent(List<String> majorApplyIdList,List<String> majorMembersList){
         //使用LinkeList防止差异过大时,元素拷贝
    	 ArrayList<String> csReturn = new ArrayList<String>();
		for (String min : majorMembersList) {
			if(!majorApplyIdList.contains(min)){
				csReturn.add(min);
			}
		}
         return csReturn;
     }

}

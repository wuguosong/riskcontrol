package com.yk.rcm.formalAssessment.listener;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.bson.Document;
import org.springframework.stereotype.Component;

import util.Util;

import com.yk.common.IBaseMongo;
import com.yk.flow.util.JsonUtil;

import common.Constants;
@Component
public class FormalAssessmentLegalReviewLeaderDiseditListener implements TaskListener{
	
	@Resource
	private IBaseMongo baseMongo;
	
	private static final long serialVersionUID = 1L;
	
	private static final String RCM_FORMALASSESSMENT_INFO  = Constants.RCM_FORMALASSESSMENT_INFO;

	@SuppressWarnings("unchecked")
	@Override
	public void notify(DelegateTask delegateTask) {
		
		String businessId = delegateTask.getExecution().getProcessBusinessKey();
		
		Map<String, Object> formalAssessmentold = baseMongo.queryById(businessId, RCM_FORMALASSESSMENT_INFO);
		
		String json = JsonUtil.toJson(formalAssessmentold);
		
		Document formalAssessment = Document.parse(json);
		
		Map<String, Object> approveLegalAttachment = (Map<String, Object>) formalAssessment.get("approveLegalAttachment");
		
		if(Util.isNotEmpty(approveLegalAttachment)){
		
			List<Map<String, Object>> commentsList = (List<Map<String, Object>>) approveLegalAttachment.get("commentsList");
			//修改反馈信息
			if(Util.isNotEmpty(commentsList)){
			
				for (Map<String, Object> comments : commentsList) {
				
					comments.put("isLegalEdit", "0");
				}
			
			}
			
			List<Map<String, Object>> attachmentNewList = (List<Map<String, Object>>) approveLegalAttachment.get("attachmentNew");
			//修改更新附件
			if(Util.isNotEmpty(attachmentNewList)){
				
				for (Map<String, Object> attachmentNew : attachmentNewList) {
					
					attachmentNew.put("isLegalEdit", "0");
					
				}
				
			}
		
		}
		
		
		formalAssessment.remove("_id");
		
		baseMongo.updateSetByObjectId(businessId, formalAssessment, RCM_FORMALASSESSMENT_INFO);
	}
}

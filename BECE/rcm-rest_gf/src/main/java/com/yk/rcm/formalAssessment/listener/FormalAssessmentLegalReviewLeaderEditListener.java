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
public class FormalAssessmentLegalReviewLeaderEditListener implements TaskListener{
	
	@Resource
	private IBaseMongo baseMongo;
	
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	@Override
	public void notify(DelegateTask delegateTask) {
		String businessId = delegateTask.getExecution().getProcessBusinessKey();
		
		Map<String, Object> formalAssessmentOld = baseMongo.queryById(businessId, Constants.RCM_FORMALASSESSMENT_INFO);
		String json = JsonUtil.toJson(formalAssessmentOld);
		Document formalAssessment = Document.parse(json);
		Document approveLegalAttachment = (Document) formalAssessment.get("approveLegalAttachment");
		
		if(Util.isNotEmpty(approveLegalAttachment)){
			List<Document> commentsList = (List<Document>) approveLegalAttachment.get("commentsList");
			if(Util.isNotEmpty(commentsList)){
				for (Document comments : commentsList) {
					comments.put("isLegalEdit", "0");
					comments.put("isGrassEdit", "0");
				}
			}
			
			List<Document> attachmentNewList = (List<Document>) approveLegalAttachment.get("attachmentNew");
			if(Util.isNotEmpty(attachmentNewList)){
				for (Document comments : attachmentNewList) {
					comments.put("isLegalEdit", "0");
					comments.put("isGrassEdit", "0");
				}
			}
		
		}
		
		formalAssessment.remove("_id");
		baseMongo.updateSetByObjectId(businessId, formalAssessment, Constants.RCM_FORMALASSESSMENT_INFO);
	}

}

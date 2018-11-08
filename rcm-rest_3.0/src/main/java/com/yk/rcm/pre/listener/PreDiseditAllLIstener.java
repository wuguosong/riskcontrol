package com.yk.rcm.pre.listener;

import java.util.HashMap;
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


/**
 * 将评审文件和意见变为只读
 * @author yaphet
 */
@Component
public class PreDiseditAllLIstener implements TaskListener{
	
	@Resource
	private IBaseMongo baseMongo;
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	@Override
	public void notify(DelegateTask delegateTask) {
		String businessId = delegateTask.getExecution().getProcessBusinessKey();
		
		Map<String, Object> preOld = baseMongo.queryById(businessId, Constants.RCM_PRE_INFO);
		String json = JsonUtil.toJson(preOld);
		Document pre = Document.parse(json);
		Document approveAttachment = (Document) pre.get("approveAttachment");
		
		if(Util.isNotEmpty(approveAttachment)){
			List<Document> commentsList = (List<Document>) approveAttachment.get("commentsList");
			if(Util.isNotEmpty(commentsList)){
				for (Document comments : commentsList) {
					comments.put("isReviewLeaderEdit", "0");
					comments.put("isInvestmentManagerBackEdit", "0");
				}
			}
			
			List<Document> attachmentNewList = (List<Document>) approveAttachment.get("attachmentNew");
			if(Util.isNotEmpty(attachmentNewList)){
				for (Document comments : attachmentNewList) {
					comments.put("isReviewLeaderEdit", "0");
					comments.put("isInvestmentManagerBackEdit", "0");
				}
			}
			
			Map<String,Object> data = new HashMap<String,Object>();
			data.put("approveAttachment", approveAttachment);
			baseMongo.updateSetByObjectId(businessId, data, Constants.RCM_PRE_INFO);
		}
		
	}
}

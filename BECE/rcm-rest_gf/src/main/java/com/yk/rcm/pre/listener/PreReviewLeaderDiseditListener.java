package com.yk.rcm.pre.listener;

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
 * 改评审意见状态
 * 投资经理可编辑
 * 评审负责人不可编辑
 * @author yaphet
 */

@Component
public class PreReviewLeaderDiseditListener implements TaskListener{
	@Resource
	private IBaseMongo baseMongo;
	
	private static final long serialVersionUID = 1L;
	
	@SuppressWarnings("unchecked")
	@Override
	public void notify(DelegateTask delegateTask) {
		
		String businessId = delegateTask.getExecution().getProcessBusinessKey();
		
		Map<String, Object> preOld = baseMongo.queryById(businessId, Constants.RCM_PRE_INFO);
		
		String json = JsonUtil.toJson(preOld);
		
		Document preInfo = Document.parse(json);
		
		Map<String, Object> approveAttachment = (Map<String, Object>) preInfo.get("approveAttachment");
		if(Util.isNotEmpty(approveAttachment)){
			List<Map<String, Object>> commentsList = (List<Map<String, Object>>) approveAttachment.get("commentsList");
			//修改反馈信息
			if(Util.isNotEmpty(commentsList)){
				for (Map<String, Object> comments : commentsList) {
					comments.put("isReviewLeaderEdit", "0");
				}
			}
			List<Map<String, Object>> attachmentNewList = (List<Map<String, Object>>) approveAttachment.get("attachmentNew");
			//修改更新附件
			if(Util.isNotEmpty(attachmentNewList)){
				for (Map<String, Object> attachmentNew : attachmentNewList) {
					attachmentNew.put("isReviewLeaderEdit", "0");
				}
			}
		}
		
		baseMongo.updateSetByObjectId(businessId, approveAttachment, Constants.RCM_PRE_INFO);
	}
}

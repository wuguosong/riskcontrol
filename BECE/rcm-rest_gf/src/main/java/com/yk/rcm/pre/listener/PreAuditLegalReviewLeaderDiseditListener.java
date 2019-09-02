package com.yk.rcm.pre.listener;

import com.yk.common.IBaseMongo;
import com.yk.flow.util.JsonUtil;
import common.Constants;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.bson.Document;
import org.springframework.stereotype.Component;
import util.Util;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Component
public class PreAuditLegalReviewLeaderDiseditListener implements TaskListener {

    @Resource
    private IBaseMongo baseMongo;

    private static final long serialVersionUID = 1L;


    @SuppressWarnings("unchecked")
    @Override
    public void notify(DelegateTask delegateTask) {

        String businessId = delegateTask.getExecution().getProcessBusinessKey();

        Map<String, Object> preOld = baseMongo.queryById(businessId, Constants.PRE_ASSESSMENT);
        if (preOld != null) {
            String json = JsonUtil.toJson(preOld);

            Document formalAssessment = Document.parse(json);

            Map<String, Object> approveLegalAttachment = (Map<String, Object>) formalAssessment.get("approveLegalAttachment");

            if (Util.isNotEmpty(approveLegalAttachment)) {
                List<Map<String, Object>> commentsList = (List<Map<String, Object>>) approveLegalAttachment.get("commentsList");
                // 修改反馈信息
                if (Util.isNotEmpty(commentsList)) {
                    for (Map<String, Object> comments : commentsList) {
                        comments.put("isLegalEdit", "0");// hello，world
                    }
                }
                List<Map<String, Object>> attachmentNewList = (List<Map<String, Object>>) approveLegalAttachment.get("attachmentNew");
                // 修改更新附件
                if (Util.isNotEmpty(attachmentNewList)) {
                    for (Map<String, Object> attachmentNew : attachmentNewList) {
                        attachmentNew.put("isLegalEdit", "0");
                    }
                }
            }
            formalAssessment.remove("_id");
            baseMongo.updateSetByObjectId(businessId, formalAssessment, Constants.PRE_ASSESSMENT);
        }
    }
}

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
public class PreAuditLegalReviewLeaderEditListener implements TaskListener {

    @Resource
    private IBaseMongo baseMongo;

    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unchecked")
    @Override
    public void notify(DelegateTask delegateTask) {
        String businessId = delegateTask.getExecution().getProcessBusinessKey();

        Map<String, Object> preInfoOld = baseMongo.queryById(businessId, Constants.RCM_PRE_INFO);
        String json = JsonUtil.toJson(preInfoOld);
        Document preInfo = Document.parse(json);
        Document approveLegalAttachment = (Document) preInfo.get("approveLegalAttachment");
        if (Util.isNotEmpty(approveLegalAttachment)) {
            List<Document> comments = (List<Document>) approveLegalAttachment.get("commentsList");
            if (Util.isNotEmpty(comments)) {
                for (Document comment : comments) {
                    comment.put("isLegalEdit", "0");
                    comment.put("isGrassEdit", "0");
                }
            }
            List<Document> attachmentsNew = (List<Document>) approveLegalAttachment.get("attachmentNew");
            if (Util.isNotEmpty(attachmentsNew)) {
                for (Document attachment : attachmentsNew) {
                    attachment.put("isLegalEdit", "0");
                    attachment.put("isGrassEdit", "0");
                }
            }

        }
        preInfo.remove("_id");
        baseMongo.updateSetByObjectId(businessId, preInfo, Constants.RCM_PRE_INFO);
    }
}

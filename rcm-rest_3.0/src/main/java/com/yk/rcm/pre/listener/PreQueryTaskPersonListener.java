package com.yk.rcm.pre.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.bson.Document;
import org.springframework.stereotype.Component;

import util.Util;

import com.yk.common.IBaseMongo;

import common.Constants;
import ws.client.HpgClient;

/**
 * @author yaphet
 *         查询任务分配人，添加到流程变量中
 */
@Component
public class PreQueryTaskPersonListener implements ExecutionListener {

    private static final long serialVersionUID = 1L;

    @Resource
    private IBaseMongo baseMongo;

    //@Override
    //@SuppressWarnings("unchecked")
    //public void notify(DelegateExecution execution) throws Exception {
    //String businessKey = execution.getProcessBusinessKey();

    //Map<String, Object> preMongo = baseMongo.queryById(businessKey, Constants.RCM_PRE_INFO);
    //Map<String, Object> taskallocation = (Map<String, Object>) preMongo.get("taskallocation");

//		List<Document> fixedGroup = (List<Document>) taskallocation.get("fixedGroup");
    //Document reviewLeader = (Document) taskallocation.get("reviewLeader");

    // reviewLeaderId = reviewLeader.getString("VALUE");

    //Map<String, Object> variables = new HashMap<String,Object>();
    //variables.put("reviewLeader", reviewLeaderId);

    //		if(Util.isNotEmpty(fixedGroup)){
//			List<String> groupMembersList = new ArrayList<String>();
//			for (Document ll : fixedGroup) {
//				String id = ll.getString("VALUE");
//				groupMembersList.add(id);
//			}
//			variables.put("groupMembers", groupMembersList);
//		}
    //execution.setVariables(variables);
    //}
    @SuppressWarnings("unchecked")
    @Override
    public void notify(DelegateExecution execution) throws Exception {
        String businessKey = execution.getProcessBusinessKey();
        Map<String, Object> preMongo = baseMongo.queryById(businessKey, Constants.RCM_PRE_INFO);
        Map<String, Object> taskallocation = (Map<String, Object>) preMongo.get("taskallocation");
        List<Document> fixedGroup = (List<Document>) taskallocation.get("fixedGroup");
        Document legalReviewLeader = (Document) taskallocation.get("legalReviewLeader");
        Document reviewLeader = (Document) taskallocation.get("reviewLeader");
        Map<String, Object> variables = new HashMap<String, Object>();
        if (Util.isNotEmpty(legalReviewLeader)) {
            String legalReviewLeaderId = legalReviewLeader.getString("VALUE");
            variables.put("legalReviewLeader", legalReviewLeaderId);
        }
        if (Util.isNotEmpty(reviewLeader)) {
            String reviewLeaderId = reviewLeader.getString("VALUE");
            variables.put("reviewLeader", reviewLeaderId);
        }
        /*=====基层法务人员=====*/
        JSONObject preMongoJs = JSON.parseObject(JSON.toJSONString(preMongo), JSONObject.class);
        if(preMongoJs != null){
            JSONObject preApplyJs = preMongoJs.getJSONObject("apply");
            if(preApplyJs != null){
                JSONObject grassrootsLegalStaff = preApplyJs.getJSONObject("grassrootsLegalStaff");
                if(grassrootsLegalStaff != null){
                    String grassrootsLegalStaffId = grassrootsLegalStaff.getString("VALUE");
                    variables.put("grassrootsLegalStaff", grassrootsLegalStaffId);
                }
            }
        }
		/*=====基层法务人员=====*/
        if (Util.isNotEmpty(fixedGroup)) {
            List<String> groupMembersList = new ArrayList<String>();
            for (Document ll : fixedGroup) {
                String id = ll.getString("VALUE");
                groupMembersList.add(id);
            }
            variables.put("groupMembers", groupMembersList);
        }
        execution.setVariables(variables);
        // hello world
    }
}

package com.yk.ext.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import util.Util;
import ws.client.HpgClient;
import ws.service.WebServiceForTZ;

import com.yk.common.IBaseMongo;
import com.yk.ext.InitWithJson;
import com.yk.ext.Initable;
import com.yk.rcm.formalAssessment.service.IFormalAssessmentInfoService;

import common.Constants;
import common.Result;

/**
 * 推送所有评审人员信息
 * 
 * @author yaphet
 */
@Service("sendAllReviewPerson2HPG")
@Transactional
public class SendAllReviewPerson2HPG implements Initable {

	@Resource
	private IFormalAssessmentInfoService formalAssessmentInfoService; 
	@Resource
	private IBaseMongo baseMongo;
	@Override
	public void execute() {
		
		List<Map<String, Object>> queryByStageAndstate = formalAssessmentInfoService.queryByStageAndstate("3,3.1,3.5,3.7,3.9,4,5,6,7,9", "1,2");
	
		for (Map<String, Object> ff : queryByStageAndstate) {
			String businessId = (String) ff.get("BUSINESSID");
			Map<String, Object> fomral = this.baseMongo.queryById(businessId, Constants.RCM_FORMALASSESSMENT_INFO);
			Map<String, Object> taskallocation = (Map<String, Object>) fomral.get("taskallocation");
			if(Util.isEmpty(taskallocation)){
				continue; 
			}
			
			Map<String, Object> reviewLeader = (Map<String, Object>) taskallocation.get("reviewLeader");
			if(Util.isEmpty(reviewLeader)){
				continue; 
			}
			
			String reviewLeaderId = (String) reviewLeader.get("VALUE");
			
			if(Util.isEmpty(reviewLeaderId)){
				continue; 
			}
			
			HpgClient client = new HpgClient(businessId, reviewLeaderId);
			Thread thread = new Thread(client);
			thread.start();
		}
	}
}

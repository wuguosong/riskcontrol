package com.yk.ext.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ws.client.HpgInfoClient;

import com.yk.ext.Initable;
import com.yk.rcm.formalAssessment.service.IFormalAssessmentInfoService;

/**
 * 推送所有评审项目信息
 * @author yaphet
 */
@Service("sendReviewAllInfo2HPG")
@Transactional
public class SendReviewAllInfo2HPG implements Initable {
	
	@Resource
	private IFormalAssessmentInfoService formalAssessmentInfoService; 
	
	@Override
	public void execute() {
		List<Map<String, Object>> queryByStageAndstate = formalAssessmentInfoService.queryByStageAndstate("7", "2");
		for (Map<String, Object> oracle : queryByStageAndstate) {
			String businessId = (String) oracle.get("BUSINESSID");
			HpgInfoClient client = new HpgInfoClient(businessId);
			Thread thread = new Thread(client);
			thread.start();
		}
	}
}

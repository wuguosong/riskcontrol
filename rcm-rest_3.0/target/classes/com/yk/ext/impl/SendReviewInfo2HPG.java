	package com.yk.ext.impl;

import java.util.Map;

import javax.annotation.Resource;

import org.bson.Document;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import util.Util;
import ws.client.HpgClient;
import ws.client.HpgInfoClient;
import ws.service.WebServiceForTZ;

import com.yk.common.IBaseMongo;
import com.yk.ext.InitWithJson;
import com.yk.rcm.formalAssessment.service.IFormalAssessmentInfoService;

import common.Constants;
import common.Result;

/**
 * 单个推送评审项目信息
 * @author yaphet
 */
@Service("sendReviewInfo2HPG")
@Transactional
public class SendReviewInfo2HPG implements InitWithJson {
	
	@Resource
	private IFormalAssessmentInfoService formalAssessmentInfoService; 
	
	@Override
	public Result execute(String json) {
		Result result = new Result();
		Document doc = Document.parse(json);
		String businessId = (String) doc.get("businessId");
		Map<String, Object> oracle = this.formalAssessmentInfoService.getOracleByBusinessId(businessId);
		
		String stage = (String) oracle.get("STAGE");
		if("7".equals(stage)){
			HpgInfoClient client = new HpgInfoClient(businessId);
			Thread thread = new Thread(client);
			thread.start();
		}else{
			result.setSuccess(false).setResult_name("项目没有确认决策通知书");
		}
		
		return result;
	}
}

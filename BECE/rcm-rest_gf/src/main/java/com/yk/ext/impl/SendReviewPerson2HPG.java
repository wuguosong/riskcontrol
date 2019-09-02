	package com.yk.ext.impl;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import util.Util;
import ws.client.HpgClient;
import ws.service.WebServiceForTZ;

import com.yk.common.IBaseMongo;
import com.yk.ext.InitWithJson;

import common.Constants;
import common.Result;

/**
 * 单个推送评审人员信息
 * @author yaphet
 */
@Service("sendReviewPerson2HPG")
@Transactional
public class SendReviewPerson2HPG implements InitWithJson {
	
	@Resource
	private IBaseMongo baseMongo;
	
	@Override
	public Result execute(String businessId) {
		Result result = new Result();
		Map<String, Object> fomral = this.baseMongo.queryById(businessId, Constants.RCM_FORMALASSESSMENT_INFO);
		Map<String, Object> taskallocation = (Map<String, Object>) fomral.get("taskallocation");
		if(Util.isEmpty(taskallocation)){
			result.setSuccess(false).setResult_name("此项目没有分配完任务！没有人员信息");
			return result;
		}
		
		Map<String, Object> reviewLeader = (Map<String, Object>) taskallocation.get("reviewLeader");
		if(Util.isEmpty(reviewLeader)){
			result.setSuccess(false).setResult_name("人员信息不完整");
			return result;
		}
		
		String reviewLeaderId = (String) reviewLeader.get("VALUE");
		
		if(Util.isEmpty(reviewLeaderId)){
			result.setSuccess(false).setResult_name("人员信息不完整");
			return result;
		}
		
		HpgClient client = new HpgClient(businessId, reviewLeaderId);
		Thread thread = new Thread(client);
		thread.start();
		return result;
	}
}

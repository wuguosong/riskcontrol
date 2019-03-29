package com.yk.ext.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import util.Util;

import com.yk.common.IBaseMongo;
import com.yk.ext.Initable;
import com.yk.rcm.bulletin.service.IBulletinInfoService;

import common.Constants;
@Service("refreshBulletinPerson")
@Transactional
public class RefreshBulletinPerson implements Initable {

	@Resource
	private IBaseMongo baseMongo;
	@Resource
	private IBulletinInfoService bulletinInfoService;
	
	@Override
	@SuppressWarnings("unchecked")
	public void execute() {
		
		List<Map<String, Object>> bulletinList = this.bulletinInfoService.queryBulletin();
		
		for (Map<String, Object> bulletin : bulletinList) {
			
			String businessId = (String) bulletin.get("BUSINESSID");
			
			Map<String, Object> mongo = this.baseMongo.queryById(businessId, Constants.RCM_BULLETIN_INFO);
			if(Util.isEmpty(mongo)){
				continue;
			}
			Map<String, Object> taskallocation = (Map<String, Object>) mongo.get("taskallocation");
			
			if(Util.isEmpty(taskallocation)){
				continue;
			}
			Map<String, Object> legalLeader = (Map<String, Object>) taskallocation.get("legalLeader");
			
			Map<String, Object> reviewLeader = (Map<String, Object>) taskallocation.get("reviewLeader");
			
			Map<String, Object> riskLeader = (Map<String, Object>) taskallocation.get("riskLeader");
			
			HashMap<String, Object> data = new HashMap<String,Object>();
			
			data.put("businessId", businessId);
			
			if(Util.isNotEmpty(legalLeader)){
				String legalLeaderId = (String) legalLeader.get("VALUE");
				data.put("legalLeaderId", legalLeaderId);
			}
			
			if(Util.isNotEmpty(reviewLeader)){
				String reviewLeaderId = (String) reviewLeader.get("VALUE");
				data.put("reviewLeaderId", reviewLeaderId);
			}
			
			if(Util.isNotEmpty(riskLeader)){
				String reviewLeaderId = (String) riskLeader.get("VALUE");
				data.put("reviewLeaderId", reviewLeaderId);
			}
			
			this.bulletinInfoService.updatePerson(data);
			
		}
		
	}

}

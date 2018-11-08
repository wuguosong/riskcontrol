package com.yk.ext.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import util.Util;

import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.yk.common.IBaseMongo;
import com.yk.ext.Initable;
import com.yk.power.service.IOrgService;
import com.yk.rcm.formalAssessment.service.IFormalAssessmentInfoService;

import common.Constants;

/**
 * 处理正式评审大区，双投人员为空问题
 * @author yaphet
 *
 */
@Service("refreshPrfPerson")
@Transactional
public class RefreshPrfPerson implements Initable {
	
	@Resource
	private IFormalAssessmentInfoService formalAssessmentInfoService;
	
	@Resource
	private IBaseMongo baseMongo;
	
	@Override
	public void execute() {
		List<Map<String,Object>> list = formalAssessmentInfoService.queryAllLargePersonIsNull();
		for (Map<String, Object> map : list) {
			String businessId = (String) map.get("BUSINESSID");
			
			Map<String, Object> params = new HashMap<String,Object>();
			Map<String, Object> formalMongo = this.baseMongo.queryById(businessId, Constants.RCM_FORMALASSESSMENT_INFO);
			Map<String, Object> apply = (Map<String, Object>) formalMongo.get("apply");
			//区域负责人——投资中心/水环境
			Map<String, Object> investmentPerson = (Map<String, Object>) apply.get("investmentPerson");
			if(investmentPerson != null){
				String serviceTypeValue = (String) investmentPerson.get("VALUE");
				params.put("serviceTypePersonId", serviceTypeValue);
			}
			
			//大区companyHeader
			Map<String, Object> companyHeader = (Map<String, Object>) apply.get("companyHeader");
			String companyHeaderValue = (String)companyHeader.get("VALUE");
			params.put("largeAreaPersonId", companyHeaderValue);
			
			params.put("businessId", businessId);
			formalAssessmentInfoService.updatePersonById(params);
			
		}
		
	}


}

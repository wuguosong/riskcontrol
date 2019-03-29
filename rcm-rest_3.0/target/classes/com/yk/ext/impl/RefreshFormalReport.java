package com.yk.ext.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.bson.Document;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import util.Util;

import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.yk.common.IBaseMongo;
import com.yk.ext.Initable;
import com.yk.rcm.project.dao.IWsCallMapper;

import common.Constants;

@Service
@Transactional
public class RefreshFormalReport implements Initable{

	@Resource
	private IBaseMongo baseMongo;
	@Resource
	private IWsCallMapper wsCallMapper;
	
	@Override
	public void execute() {

		MongoCollection<Document> reportCollection = this.baseMongo.getCollection(Constants.RCM_FORMALREPORT_INFO);
		
		FindIterable<Document> reportList = reportCollection.find();
		reportList.forEach(new Block<Document>() {
			@Override
			public void apply(Document doc) {
				Map<String,Object> params = new HashMap<String,Object>();
				String businessId = doc.getString("projectFormalId");
				String projectSize = "";
				String investmentAmount = "";
				String rateOfReturn = "";
				String serviceType = "";
				Document policyDecision = (Document) doc.get("policyDecision");
				if(Util.isNotEmpty(policyDecision)){
					Document projectType = (Document) policyDecision.get("projectType");
					if(Util.isNotEmpty(projectType)){
						serviceType = projectType.getString("ITEM_CODE");
					}
					if(Util.isNotEmpty(policyDecision.get("investmentAmount"))){
						investmentAmount = policyDecision.get("investmentAmount").toString();
					}
					if(Util.isNotEmpty(policyDecision.get("projectSize"))){
						projectSize = policyDecision.get("projectSize").toString();
					}
					if(Util.isNotEmpty(policyDecision.get("rateOfReturn"))){
						rateOfReturn = policyDecision.get("rateOfReturn").toString();
					}
				}
				params.put("businessId",businessId);
				params.put("projectSize", projectSize);
				params.put("investmentAmount", investmentAmount);
				params.put("rateOfReturn", rateOfReturn);
				params.put("serviceType", serviceType);
				
				wsCallMapper.deleteFormalAssessmentReportByBusinessId(businessId);
				
				wsCallMapper.saveFormalAssessmentReport(params);
			}
		});
	}
}

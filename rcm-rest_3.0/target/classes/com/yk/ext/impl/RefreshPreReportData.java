package com.yk.ext.impl;

import java.util.ArrayList;
import java.util.Date;
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
import com.yk.flow.util.JsonUtil;
import com.yk.rcm.pre.dao.IPreAuditReportMapper;
import com.yk.rcm.pre.service.IPreAuditReportService;
import com.yk.rcm.pre.service.IPreInfoService;

import common.Constants;

@Service("refreshPreReportData")
@Transactional
public class RefreshPreReportData implements Initable{

	protected static final Object Document = null;

	@Resource
	private IBaseMongo baseMongo;
	
	@Resource 
	IPreAuditReportService preAuditReportService;
	@Resource 
	IPreAuditReportMapper preAuditReportMapper;
	@Resource 
	IPreInfoService preInfoService;
	
	@Override
	public void execute() {
		preAuditReportMapper.delete();
		this.initMongo();
		
	}
	
	//刷mongoDB数据
	private void initMongo(){
		
		MongoCollection<Document> preAssessmentCollection = this.baseMongo.getCollection(Constants.RCM_PRE_INFO);
		
		FindIterable<Document> preAssessment = preAssessmentCollection.find();
		
		preAssessment.forEach(new Block<Document>(){

			@Override
			public void apply(Document t) {
				
				Object object = t.get("reviewReport");
				if(Util.isNotEmpty(object)){
					Document reviewReport= (Document)object;
					String id = t.get("_id").toString();
					
					Map<String, Object> pre = preInfoService.getPreInfoByID(id);
					Map<String, Object> oracle = (Map<String, Object>)pre.get("oracle");
					
					String create_date = reviewReport.getString("create_date");
					Document params = new Document();
					params.put("businessId", id);
					params.put("controller_val", "PreOtherReport");
					
					Date parse = Util.parse(create_date, "yyyy-MM-dd hh:mm:ss");
					params.put("create_date", parse);
					
					params.put("submit_date", parse);
					
					
					preAuditReportMapper.savePreReport(params);
				}
			}
			
		});
	}
	
}

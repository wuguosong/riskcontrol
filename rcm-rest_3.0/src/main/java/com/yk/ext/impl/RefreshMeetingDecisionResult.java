package com.yk.ext.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.bson.Document;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import util.Util;

import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.yk.common.IBaseMongo;
import com.yk.ext.Initable;
import com.yk.rcm.decision.dao.IDecisionMapper;
import com.yk.rcm.noticeofdecision.dao.INoticeDecisionInfoMapper;

import common.Constants;

@Service("refreshMeetingDecisionResult")
@Transactional
public class RefreshMeetingDecisionResult implements Initable{

	@Resource
	public IBaseMongo baseMongo;
	
	@Resource
	public INoticeDecisionInfoMapper noticeDecisionInfoMapper;
	
	@Resource 
	public IDecisionMapper decisionMapper;
	
	@Override
	public void execute() {
		initMongo();
	}
	private void initMongo(){
		MongoCollection<Document> noticeDecisionCollection = this.baseMongo.getCollection(Constants.RCM_NOTICEDECISION_INFO);
		FindIterable<Document> preAssessment = noticeDecisionCollection.find();
		preAssessment.forEach(new Block<Document>(){
			@Override
			public void apply(Document oldDoc) {
				String businessId = oldDoc.get("projectFormalId").toString();

				BasicDBObject filter = new BasicDBObject();
				filter.put("projectFormalId", businessId);
				
				Map<String,Object> updateToMongDb = new HashMap<String, Object>();
				updateToMongDb.put("consentToInvestment",null);
				
				Map<String, Object> decisionInfo = decisionMapper.queryByBusinessId(businessId);
				if(Util.isNotEmpty(decisionInfo)){
					String vote_status = decisionInfo.get("VOTE_STATUS").toString();
					//已表决,已出表决结果
					String decision_result = decisionInfo.get("DECISION_RESULT").toString();
					if("2".equals(vote_status) && !"0".equals(decision_result)){
						updateToMongDb.put("consentToInvestment",decision_result);
					}
					//已表决,未出表决结果，则为旧数据!
					else if("2".equals(vote_status) && "0".equals(decision_result)){
						//查询决策通知  oracle数据
						Map<String, Object> noticeDecisionInfo = noticeDecisionInfoMapper.queryByProjectformalId(businessId);
						if(Util.isNotEmpty(noticeDecisionInfo)){
							String consentToinvestment = noticeDecisionInfo.get("CONSENTTOINVESTMENT").toString();
							updateToMongDb.put("consentToInvestment",consentToinvestment);
						}
					}
				}
				baseMongo.updateSetByFilter(filter, updateToMongDb, Constants.RCM_NOTICEDECISION_INFO);
			}
		});
	}
	
}

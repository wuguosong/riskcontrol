package com.yk.ext.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

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
import com.yk.power.service.IRoleService;
import com.yk.rcm.decision.serevice.IMeetingIssueService;

import common.Constants;

/**
 * 
 * 将会议委员中会议主席刷到专门的  按照顺序  存储到会议主席字段中
 * @author hubiao
 *
 */
@Service("refreshMeetingIssueChairmans")
@Transactional
public class RefreshMeetingIssueChairmans implements Initable {

	@Resource
	private IBaseMongo baseMongo;

	@Resource
	private IMeetingIssueService meetingIssueService;
	
	@Resource
	private IRoleService roleService;
	
	@Override
	public void execute() {
		//刷
		List<Map<String, Object>> meetIssuList = meetingIssueService.queryAll();
		List<Map<String, Object>> roleMeetChaiList = roleService.queryMeetingChairman();
		
		for (Map<String, Object> meetIssu : meetIssuList) {
			if(null == meetIssu.get("MEETING_LEADERS")){
				continue;
			}
			
			StringBuffer chairmansBuffer = new StringBuffer();
			TreeMap<Integer,String> matchingMap = new TreeMap<Integer,String>();
			String[] meetLeadArray = meetIssu.get("MEETING_LEADERS").toString().split(",");
			
			for (String meetLead : meetLeadArray) {
				for (Map<String, Object> roleMeetChai : roleMeetChaiList) {
					if(meetLead.equals(roleMeetChai.get("USER_ID"))){
						matchingMap.put(Integer.parseInt(roleMeetChai.get("ORDER_BY").toString()), meetLead);
						break;
					}
				}
			}
			for (Entry<Integer, String> matching : matchingMap.entrySet()) {
				chairmansBuffer.append(matching.getValue()).append(",");
			}
			if(chairmansBuffer.length() > 0){
				chairmansBuffer.delete(chairmansBuffer.length()-1, chairmansBuffer.length());
				meetIssu.put("MEETING_CHAIRMANS", chairmansBuffer.toString());
				meetingIssueService.updateMeetChai(meetIssu);
			}
		}
		
		//-----------------------------------------------------
		//刷旧数据会议主席
		//-----------------------------------------------------
		final List<Map<String, Object>> meetChaiList = roleService.queryMeetingChairman();
		MongoCollection<Document> meetingInfoCollection = baseMongo.getCollection(Constants.FORMAL_MEETING_INFO);
		FindIterable<Document> meetingInfoIterable = meetingInfoCollection.find();
		meetingInfoIterable.forEach(new Block<Document>(){
			@Override
			public void apply(Document document) {
				Object jueCeHuiYiZhuXiIdObj = document.get("jueCeHuiYiZhuXiId");
				if(null != jueCeHuiYiZhuXiIdObj){
					return;
				}
				//如果没有表决则不计算会议主席
				Object decisionOpinionList = document.get("decisionOpinionList");
				if(Util.isEmpty(decisionOpinionList)){
					return;
				}
				List<Document> deciMakiCommStaf = (List<Document>) document.get("decisionMakingCommitteeStaff");
				if(Util.isEmpty(deciMakiCommStaf)){
					return;
				}
				for (Map<String, Object> meetChai : meetChaiList) {
					for (Document deciMakiComm : deciMakiCommStaf) {
						if(deciMakiComm.getString("VALUE").equals(meetChai.get("USER_ID").toString())){
							jueCeHuiYiZhuXiIdObj = deciMakiComm.getString("VALUE");break;
						}
					}
					if(null != jueCeHuiYiZhuXiIdObj){
						break;
					}
				}
				
				BasicDBObject filter = new BasicDBObject();
				String formalId = document.getString("formalId");
				filter.put("formalId", formalId);
				
				Map<String, Object> updateToMongDb = new HashMap<String, Object>();
				updateToMongDb.put("jueCeHuiYiZhuXiId", jueCeHuiYiZhuXiIdObj);
				baseMongo.updateSetByFilter(filter, updateToMongDb, Constants.FORMAL_MEETING_INFO);
			}
		});
	}
}

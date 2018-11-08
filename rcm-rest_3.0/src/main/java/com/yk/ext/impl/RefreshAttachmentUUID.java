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

import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.yk.common.IBaseMongo;
import com.yk.ext.Initable;
import com.yk.rcm.formalAssessment.service.IFormalAssessmentInfoService;

import common.Constants;

/**
 * 
 * 给附件增加uuid
 * @author yaphet
 *
 */
@Service("refreshAttachmentUUID")
@Transactional
public class RefreshAttachmentUUID implements Initable {
	
	@Resource
	private IBaseMongo baseMongo;
	
	@Resource
	private IFormalAssessmentInfoService formalAssessmentInfoService;
	
	@SuppressWarnings("unchecked")
	@Override
	public void execute() {
		
		//正式评审基础附件
		MongoCollection<Document> fomralCollection = this.baseMongo.getCollection(Constants.RCM_FORMALASSESSMENT_INFO);
		FindIterable<Document> formalIterable = fomralCollection.find();
		formalIterable.forEach(new Block<Document>() {
			@Override
			public void apply(Document doc) {
				String businessId = doc.get("_id").toString();
				List<Document> attachment = (List<Document> ) doc.get("attachment");
				if(Util.isNotEmpty(attachment)){
					for (Document atta : attachment) {
						List<Document> files = (List<Document>) atta.get("files");
						if(Util.isNotEmpty(files)){
							for (Document file : files) {
								if(Util.isNotEmpty(file)){
									String filePath = (String) file.get("filePath");
									Document owner =  (Document) file.get("owner");
									
									if(Util.isEmpty(owner)){
										owner =(Document) file.get("programmed");
										file.put("owner", owner);
									}
									
									ObjectId uuid = new ObjectId();
									file.put("UUID",uuid.toHexString());
									updateFormalReoprtFiles(businessId,uuid.toHexString(),filePath);
								}
							}
						}
					}
					Map<String,Object> map = new HashMap<String,Object>();
					map.put("attachment", attachment);
					baseMongo.updateSetByObjectId(businessId, map, Constants.RCM_FORMALASSESSMENT_INFO);
				}
			}
		});
		
		//投标评审基础附件
		MongoCollection<Document> preCollection = this.baseMongo.getCollection(Constants.RCM_PRE_INFO);
		FindIterable<Document> preIterable = preCollection.find();
		preIterable.forEach(new Block<Document>() {
			@Override
			public void apply(Document doc) {
				String businessId = doc.get("_id").toString();
				List<Document> attachment = (List<Document> ) doc.get("attachment");
				if(Util.isNotEmpty(attachment)){
					for (Document atta : attachment) {
						List<Document> files = (List<Document>) atta.get("files");
						if(Util.isNotEmpty(files)){
							for (Document file : files) {
								if(Util.isNotEmpty(file)){
									String filePath = (String) file.get("filePath");
									Document owner =  (Document) file.get("owner");
									
									if(Util.isEmpty(owner)){
										owner =(Document) file.get("programmed");
										file.put("owner", owner);
									}
									
									ObjectId uuid = new ObjectId();
									file.put("UUID",uuid.toHexString());
									//上会附件
									updatePreReoprtFiles(businessId, uuid.toHexString(), filePath,doc);
								}
							}
						}
					}
					Map<String,Object> map = new HashMap<String,Object>();
					map.put("attachment", attachment);
					baseMongo.updateSetByObjectId(businessId, map, Constants.RCM_PRE_INFO);
				}
				//风控附件
				Document policyDecision =  (Document) doc.get("policyDecision");
				if(Util.isNotEmpty(policyDecision)){
					List<Document> fileList =  (List<Document>) policyDecision.get("fileList");
					if(Util.isNotEmpty(fileList)){
						for (Document file : fileList) {
							Document files = (Document) file.get("files");
							if(Util.isNotEmpty(files)){
								String filePath = files.getString("filePath");
								ObjectId uuid = new ObjectId();
								files.put("UUID",uuid.toHexString());
								updatePreReoprtFiles(businessId,uuid.toHexString(),filePath,doc);
							}
						}
						Map<String,Object> date = new HashMap<String,Object>();
						date.put("policyDecision", policyDecision);
						baseMongo.updateSetByObjectId(businessId, date, Constants.RCM_PRE_INFO);
					}
				}
			}
		});
		//正式评审风控附件
		MongoCollection<Document> noticeCollection = this.baseMongo.getCollection(Constants.RCM_NOTICEDECISION_INFO);
		FindIterable<Document> noticeIterable = noticeCollection.find();
		noticeIterable.forEach(new Block<Document>() {
			@Override
			public void apply(Document doc) {
				String _id = doc.get("_id").toString();
				String businessId = doc.getString("projectFormalId");
				List<Document> attachment = (List<Document> ) doc.get("attachment");
				if(Util.isNotEmpty(attachment)){
					for (Document atta : attachment) {
						if(Util.isNotEmpty(atta)){
							String filePath = atta.getString("filePath");
							ObjectId uuid = new ObjectId();
							
							updateFormalReoprtFiles(businessId,uuid.toHexString(),filePath);
							updatePreReoprtFiles(businessId,uuid.toHexString(),filePath,doc);
							
							atta.put("UUID",uuid.toHexString());
						}
					}
					Map<String,Object> map = new HashMap<String,Object>();
					map.put("attachment", attachment);
					baseMongo.updateSetByObjectId(_id, map, Constants.RCM_NOTICEDECISION_INFO);
				}
			}
		});
		
	}
	
	@SuppressWarnings("unchecked")
	private void updateFormalReoprtFiles(String businessId,String uuid,String filePath){
		
		BasicDBObject queryWhere = new BasicDBObject();
		queryWhere.put("projectFormalId", businessId);
		List<Map<String, Object>> queryByCondition = baseMongo.queryByCondition(queryWhere , Constants.RCM_FORMALREPORT_INFO);
		if(Util.isEmpty(queryByCondition)){
			return;
		}
		Map<String, Object> report = queryByCondition.get(0);
		if(Util.isEmpty(report)){
			return;
		}
		
		Map<String, Object> policyDecision = (Map<String, Object>) report.get("policyDecision");
		if(Util.isEmpty(policyDecision)){
			return;
		}
		
		List<Map<String, Object>> meetingFiles = (List<Map<String, Object>>) policyDecision.get("decisionMakingCommitteeStaffFiles");
		if(Util.isEmpty(meetingFiles)){
			return;
		}
		
		for (Map<String, Object> file : meetingFiles) {
			String meetingFilePath = (String) file.get("filePath");
			if(meetingFilePath.equals(filePath)){
				file.put("ITEM_UUID", (String)file.get("UUID"));
				file.put("UUID", uuid);
			}
		}
		Map<String, Object> data = new HashMap<String,Object>();
		data.put("policyDecision", policyDecision);
		this.baseMongo.updateSetByFilter(queryWhere, data, Constants.RCM_FORMALREPORT_INFO);
	}
	
	@SuppressWarnings("unchecked")
	private void updatePreReoprtFiles(String businessId,String uuid,String filePath,Map<String, Object> preMongo){
		
		if(Util.isEmpty(preMongo)){
			return ;
		}
		Map<String, Object> policyDecision = (Map<String, Object>) preMongo.get("policyDecision");
		if(Util.isEmpty(policyDecision)){
			return ;
		}
		
		List<Map<String, Object>> meetingFiles = (List<Map<String, Object>>) policyDecision.get("decisionMakingCommitteeStaffFiles");
		if(Util.isEmpty(meetingFiles)){
			return ;
		}
		
		for (Map<String, Object> file : meetingFiles) {
			String meetingFilePath = (String) file.get("filePath");
			if(meetingFilePath.equals(filePath)){
				file.put("ITEM_UUID", (String)file.get("UUID"));
				file.put("UUID", uuid);
			}
		}
	}
}

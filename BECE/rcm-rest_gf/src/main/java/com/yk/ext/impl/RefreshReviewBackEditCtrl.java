package com.yk.ext.impl;

import java.util.List;
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
import com.yk.rcm.formalAssessment.service.IFormalAssessmentInfoService;

import common.Constants;

/**
 * tips:刷旧版的正式评审法律评审、专业评审需反馈的附件的编辑控制器
 * @author yaphet
 *
 */
@Service
@Transactional
public class RefreshReviewBackEditCtrl implements Initable{

	@Resource
	private IBaseMongo baseMongo;
	
	@Resource
	private IFormalAssessmentInfoService formalAssessmentInfoService;
	
	@Override
	public void execute() {
		MongoCollection<Document> formalAssessmentCollection = this.baseMongo.getCollection(Constants.RCM_FORMALASSESSMENT_INFO);
		
		FindIterable<Document> formalAssessmentList = formalAssessmentCollection.find();
		
		formalAssessmentList.forEach(new Block<Document>(){

			@SuppressWarnings("unchecked")
			@Override
			public void apply(Document oldDoc) {
				Document apply = (Document) oldDoc.get("apply");
				
				//判断这个项目的审核状态和阶段
				//阶段为3且审核状体为1 的修改mongo的控制器
				String businessid = oldDoc.get("_id").toString();
				//获取businessid
				Map<String, Object> formalAssessmentOracle = formalAssessmentInfoService.getFormalAssessmentByID(businessid);
				//formalAssessment.get
				Map<String, Object> formalAssessment = (Map<String, Object>) formalAssessmentOracle.get("formalAssessmentOracle");
				if(null == formalAssessment){
					return;
				}
				
				//chaxun
				String wf_state = formalAssessment.get("WF_STATE").toString();
				
				String stage = formalAssessment.get("STAGE").toString();
				
				if(wf_state.equals("1") && stage.equals("3") ){
					//全部可编辑
					if(Util.isNotEmpty(apply)){
						Document approveAttachment = (Document) apply.get("approveAttachment");
						Document approveLegalAttachment = (Document) apply.get("approveLegalAttachment");
						if(Util.isNotEmpty(approveAttachment)){
							List<Document> commentsList = (List<Document>) approveAttachment.get("commentsList");
							if(Util.isNotEmpty(commentsList)){
								if(commentsList.size()>0){
									for (Document comments : commentsList) {
										comments.put("isReviewLeaderEdit", "1");
										comments.put("isInvestmentManagerBackEdit", "1");
									}
								}
							}
							List<Document> attachmentNew = (List<Document>) approveAttachment.get("attachmentNew");
							if(Util.isNotEmpty(attachmentNew)){
								if(attachmentNew.size()>0){
									for (Document attachment : attachmentNew) {
										attachment.put("isReviewLeaderEdit", "1");
										attachment.put("isInvestmentManagerBackEdit", "1");
									}
								}
							}
						}
						if(Util.isNotEmpty(approveLegalAttachment)){
							List<Document> commentsList = (List<Document>) approveAttachment.get("commentsList");
							if(Util.isNotEmpty(commentsList)){
								if(commentsList.size()>0){
									for (Document comments : commentsList) {
										comments.put("isLegalEdit", "1");
										comments.put("isGrassEdit", "1");
									}
								}
							}
							List<Document> attachmentNew = (List<Document>) approveAttachment.get("attachmentNew");
							if(Util.isNotEmpty(attachmentNew)){
								if(attachmentNew.size()>0){
									for (Document attachment : attachmentNew) {
										attachment.put("isLegalEdit", "1");
										attachment.put("isGrassEdit", "1");
									}
								}
							}
						}
					}
				}
			}
		});
	}
}

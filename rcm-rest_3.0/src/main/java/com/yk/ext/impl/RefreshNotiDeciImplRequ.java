package com.yk.ext.impl;

import javax.annotation.Resource;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.yk.common.IBaseMongo;
import com.yk.ext.Initable;

import common.Constants;

/**
 * 刷决策通知书
 *   将 旧数据的执行要求，赋值给 require1(纳入收益考核要求)!
 *   
 * @author hubiao
 *
 */
@Service("refreshNotiDeciImplRequ")
@Transactional
public class RefreshNotiDeciImplRequ implements Initable{

	@Resource
	private IBaseMongo baseMongo;
	
	@Override
	public void execute() {
		MongoCollection<Document> noticeDecisionCollection = baseMongo.getCollection(Constants.RCM_NOTICEDECISION_INFO);
		FindIterable<Document> documents = noticeDecisionCollection.find();
		documents.forEach(new Block<Document>(){
			@Override
			public void apply(Document document) {
				ObjectId objectId = document.getObjectId("_id");
				//如果没有执行要求，或已有  纳入收益考核要求  则不刷数据
				Object implRequObject = document.get("implementationRequirements");
				if(document.containsKey("require1") || null == implRequObject){
					return;
				}
				
				String implRequ = implRequObject.toString();
				document.put("require1", implRequ);
				
				document.put("implementationRequirements", "\t纳入收益考核要求\n".concat(implRequ));
				
				baseMongo.updateSetByObjectId(objectId.toString(), document, Constants.RCM_NOTICEDECISION_INFO);
			}
		});
	}
}

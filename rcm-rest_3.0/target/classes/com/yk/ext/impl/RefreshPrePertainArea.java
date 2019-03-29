package com.yk.ext.impl;

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
import com.yk.rcm.pre.service.IPreInfoService;
import common.Constants;

/**
 * 刷  投标评审的  大区ID
 * @author hubiao
 *
 */
@Service("refreshPrePertainArea")
@Transactional
public class RefreshPrePertainArea implements Initable {
	
	@Resource
	private IPreInfoService preInfoService;
	
	@Resource
	private IOrgService orgService;
	
	@Resource
	private IBaseMongo baseMongo;
	
	@Override
	public void execute() {
		oracle();
		mongdb();
	}

	private void mongdb() {
		MongoCollection<Document> formalAssessmentCollection = baseMongo.getCollection(Constants.RCM_PRE_INFO);
		FindIterable<Document> documents = formalAssessmentCollection.find();
		documents.forEach(new Block<Document>(){
			@Override
			public void apply(Document document) {
				Document apply = (Document) document.get("apply");
				if(null == apply){
					return;
				}
				//过滤大区不是空的
				Document pertainArea = (Document) apply.get("pertainArea");
				if(Util.isNotEmpty(pertainArea) && Util.isNotEmpty(pertainArea.get("KEY"))){
					return;
				}
				ObjectId objectId = document.getObjectId("_id");

				//根据申报单位 获取大区ID
				Document reportingUnit = (Document) apply.get("reportingUnit");
				Map<String, Object> queryPertainArea = orgService.queryPertainArea(reportingUnit.getString("KEY"));
				
				pertainArea = new Document();
				pertainArea.put("KEY", queryPertainArea.get("ORGPKVALUE"));
				pertainArea.put("VALUE", queryPertainArea.get("NAME"));
			
				apply.put("pertainArea", pertainArea);
				Document updateDocument = new Document();
				updateDocument.put("apply", apply);
				baseMongo.updateSetByObjectId(objectId.toString(), updateDocument, Constants.RCM_PRE_INFO);
			}
		});
	}

	private void oracle() {
		List<Map<String,Object>> pfrs = preInfoService.queryPertainAreaIsNull();
		for (Map<String, Object> pfr : pfrs) {
			Map<String, Object> queryPertainArea = orgService.queryPertainArea(pfr.get("REPORTINGUNIT_ID").toString());
			String pertainAreaId = queryPertainArea.get("ORGPKVALUE").toString();
			preInfoService.updatePertainAreaId(pfr.get("ID").toString(), pertainAreaId);
		}		
	}
}

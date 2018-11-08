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
import com.yk.power.service.IOrgService;
import com.yk.rcm.bulletin.service.IBulletinInfoService;
import common.Constants;

/**
 * 刷  其他决策事项的  大区ID
 * @author hubiao
 *
 */
@Service("refreshBulletinPertainArea")
@Transactional
public class RefreshBulletinPertainArea implements Initable {
	
	@Resource
	private IBulletinInfoService bulletinInfoService;
	
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
		MongoCollection<Document> formalAssessmentCollection = baseMongo.getCollection(Constants.RCM_BULLETIN_INFO);
		FindIterable<Document> documents = formalAssessmentCollection.find();
		documents.forEach(new Block<Document>(){
			@Override
			public void apply(Document document) {
				Document pertainArea = (Document) document.get("pertainArea");
				if(Util.isNotEmpty(pertainArea) && Util.isNotEmpty(pertainArea.get("KEY"))){
					return;
				}
				String objectId = document.getString("_id");

				//根据申报单位 获取大区ID
				Document applyUnit = (Document) document.get("applyUnit");
				Map<String, Object> queryPertainArea = orgService.queryPertainArea(applyUnit.getString("VALUE"));
				
				if(Util.isNotEmpty(queryPertainArea)){
					pertainArea = new Document();
					pertainArea.put("KEY", queryPertainArea.get("ORGPKVALUE"));
					pertainArea.put("VALUE", queryPertainArea.get("NAME"));
					document.put("pertainArea", pertainArea);
//				}else{
//					System.out.println("找不到:"+applyUnit+"===="+objectId);
				}
				baseMongo.updateSetById(objectId, document, Constants.RCM_BULLETIN_INFO);
			}
		});		
	}

	private void oracle() {
		 List<Map<String, Object>> bulletins = bulletinInfoService.queryBulletin();
		for (Map<String, Object> bulletin : bulletins) {
			Map<String, Object> queryPertainArea = orgService.queryPertainArea(bulletin.get("APPLYUNITID").toString());
			if(Util.isNotEmpty(queryPertainArea)){
				String pertainAreaId = queryPertainArea.get("ORGPKVALUE").toString();
				bulletinInfoService.updatePertainAreaId(bulletin.get("ID").toString(), pertainAreaId);
//			}else{
//				System.out.println("找不到:"+bulletin);
			}
		}		
	}
}

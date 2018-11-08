package com.yk.ext.impl;

import java.util.List;

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

import common.Constants;

/**
 * 刷正式评审的附件
 * 1:版本号全部转为String类型的数字值	
 * 2:审核人的name和value全部转为大写
 * @author hubiao
 *
 */
@Service("refreshPrfAttachment")
@Transactional
public class RefreshPrfAttachment implements Initable{

	@Resource
	private IBaseMongo baseMongo;
	
	@Override
	public void execute() {
		MongoCollection<Document> formalAssessmentCollection = baseMongo.getCollection(Constants.RCM_FORMALASSESSMENT_INFO);
		FindIterable<Document> documents = formalAssessmentCollection.find();
		documents.forEach(new Block<Document>(){
			@Override
			public void apply(Document document) {
				ObjectId objectId = document.getObjectId("_id");
				Document apply = (Document) document.get("apply");
				if(null == apply){
					return;
				}
				Document investmentManager = (Document) apply.get("investmentManager");
				String investmentManagerName = investmentManager.getString("NAME");
				String investmentManagerVALUE = investmentManager.getString("VALUE");

				List<Document> attachments = (List<Document>) document.get("attachment");
				for (Document attachment : attachments) {
					if(!attachment.containsKey("files")){
						continue;
					}
					List<Document> files = (List<Document>) attachment.get("files");
					if(Util.isEmpty(files)){
						continue;
					}
					//处理附件审核人
					for (Document file : files) {
						Object versionObject = file.get("version");
						if(null == versionObject){
							versionObject = "1";
						}
						String version = versionObject.toString();
						if(version.indexOf(".") != -1){
							version = version.substring(0,version.indexOf("."));
						}
						file.put("version",version);
						
						//审核人为空，使用投资经理
						Document programmed = (Document) file.get("programmed");
						if(Util.isEmpty(programmed)){
							file.put("programmed", new Document());
							programmed = (Document) file.get("programmed");
						}
						if(Util.isEmpty(programmed.getString("VALUE"))){
							if(Util.isEmpty(programmed.getString("value"))){
								programmed.remove("value");
								programmed.put("VALUE", investmentManagerVALUE);
							}else{
								programmed.put("VALUE", programmed.getString("value"));
								programmed.remove("value");
							}
						}
						if(Util.isEmpty(programmed.getString("NAME"))){
							if(Util.isEmpty(programmed.getString("name"))){
								programmed.remove("name");
								programmed.put("NAME", investmentManagerName);
							}else{
								programmed.put("NAME", programmed.getString("name"));
								programmed.remove("name");
							}
						}
						
						//审核人为空，使用投资经理
						Document approved = (Document) file.get("approved");
						if(Util.isEmpty(approved)){
							file.put("approved", new Document());
							approved = (Document) file.get("approved");
						}
						if(Util.isEmpty(approved.getString("VALUE"))){
							if(Util.isEmpty(approved.getString("value"))){
								approved.remove("value");
								approved.put("VALUE", investmentManagerVALUE);
							}else{
								approved.put("VALUE", approved.getString("value"));
								approved.remove("value");
							}
						}
						
						if(Util.isEmpty(approved.getString("NAME"))){
							if(Util.isEmpty(approved.getString("name"))){
								approved.remove("name");
								approved.put("NAME", investmentManagerName);
							}else{
								approved.put("NAME", approved.getString("name"));
								approved.remove("name");
							}
						}
					}
				}
				Document attachmentUpdate = new Document();
				attachmentUpdate.put("attachment", attachments);
				baseMongo.updateSetByObjectId(objectId.toString(), attachmentUpdate, Constants.RCM_FORMALASSESSMENT_INFO);
			}
		});
	}
}

/**
 * 
 */
package com.yk.rcm.project.service.impl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.bson.Document;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import util.Util;

import com.yk.common.IBaseMongo;
import com.yk.common.SpringUtil;
import com.yk.rcm.project.component.FormalAuditUserComponent;
import com.yk.rcm.project.service.IFormalAuditService;

import common.Constants;

/**
 * @author 80845530
 *
 */
@Service
@Transactional
public class FormalAuditService implements IFormalAuditService {
	@Resource
	private IBaseMongo baseMongo;
	
	private static Logger logger = LoggerFactory.getLogger(FormalAuditService.class);

	/* (non-Javadoc)
	 * @see com.yk.rcm.service.IFormalAuditService#queryAuditUsers(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> queryAuditUsers(String sign, String businessId) {
		String[] info = sign.split("\\.");
		FormalAuditUserComponent formalAuditUser = (FormalAuditUserComponent) SpringUtil.getBean(info[0]);
		List<Map<String, Object>> users = null;
		try {
			Method method = FormalAuditUserComponent.class.getMethod(info[1], String.class);
			users = (List<Map<String, Object>>) method.invoke(formalAuditUser, businessId);
		} catch (Exception e) {
			logger.error(Util.parseException(e));
		}
		return users;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void deleteAttachment(String json) {
		Document doc = Document.parse(json);
		String uuid = (String) doc.get("UUID");
		String version = (String) doc.get("version");
		String businessId = (String) doc.get("businessId");
		Map<String, Object> queryById = baseMongo.queryById(businessId, Constants.RCM_PROJECTFORMALREVIEW_INFO);
		
		List<Map<String, Object>> attachmentList = (List<Map<String, Object>>) queryById.get("attachment");
		
		for (Map<String, Object> attachment : attachmentList) {
			if(attachment.get("UUID").toString().equals(uuid)){
				List<Map<String, Object>> filesList = (List<Map<String, Object>>) attachment.get("files");
				if(Util.isNotEmpty(filesList)){
					for(int i = 0 ; i < filesList.size() ; i++){
						if(filesList.get(i).get("version").toString().equals(version)){
							filesList.remove(i);
						}
					}
				}
			}
		}
		Map<String, Object> data = new HashMap<String,Object>();
		data.put("attachment", attachmentList);
		baseMongo.updateSetByObjectId(businessId, data, Constants.RCM_PROJECTFORMALREVIEW_INFO);
		
	}

	@Override
	public void updateAttachment(String json) {
		Document doc = Document.parse(json);
		String uuid = (String) doc.get("UUID");
		String version = doc.get("version").toString();
		String businessId = (String) doc.get("businessId");
		String fileName = (String) doc.get("fileName");
		String filePath = (String) doc.get("filePath");
		
		Map<String, Object> queryById = baseMongo.queryById(businessId, Constants.RCM_PROJECTFORMALREVIEW_INFO);
		
		List<Map<String, Object>> attachmentList = (List<Map<String, Object>>) queryById.get("attachment");
		
		for (Map<String, Object> attachment : attachmentList) {
			if(attachment.get("UUID").toString().equals(uuid)){
				List<Map<String, Object>> filesList = (List<Map<String, Object>>) attachment.get("files");
				if(Util.isNotEmpty(filesList)){
					for (Map<String, Object> files : filesList) {
						if(files.get("version").toString().equals(version)){
							files.put("fileName", fileName);
							files.put("filePath", filePath);
						}
					}
				}
			}
		}
		Map<String, Object> data = new HashMap<String,Object>();
		data.put("attachment", attachmentList);
		baseMongo.updateSetByObjectId(businessId, data, Constants.RCM_PROJECTFORMALREVIEW_INFO);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addNewAttachment(String json) {
		
		Document doc = Document.parse(json);
		
		String uuid = doc.get("UUID").toString();
		
		String version = doc.get("version").toString();
		String businessId = (String) doc.get("businessId");
		Document item = (Document) doc.get("item");
		String fileName = (String) item.get("fileName");
		String filePath = (String) item.get("filePath");
		//申请人信息
		Document programmed = (Document) item.get("programmed");
		//审核人信息
		Document approved = (Document) item.get("approved");
		
		Map<String, Object> queryById = baseMongo.queryById(businessId, Constants.RCM_PROJECTFORMALREVIEW_INFO);
		
		List<Map<String, Object>> attachmentList = (List<Map<String, Object>>) queryById.get("attachment");
		
		for (Map<String, Object> attachment : attachmentList) {
			if(attachment.get("UUID").toString().equals(uuid)){
				List<Map<String, Object>> filesList = (List<Map<String, Object>>) attachment.get("files");
				if(Util.isNotEmpty(filesList)){
					Map<String, Object> file = new HashMap<String,Object>();
					file.put("fileName", fileName);
					file.put("filePath", filePath);
					file.put("programmed", programmed);
					file.put("version", version);
					file.put("approved", approved);
					file.put("upload_date", approved);
					file.put("upload_date", Util.format(Util.now()));
					filesList.add(file);
				}else{
					
					Map<String, Object> file = new HashMap<String,Object>();
					file.put("fileName", fileName);
					file.put("filePath", filePath);
					file.put("programmed", programmed);
					file.put("approved", approved);
					file.put("version", version);
					file.put("upload_date", Util.format(Util.now()));
					List<Map<String, Object>> files = new ArrayList<Map<String, Object>>();
					files.add(file);
					attachment.put("files", files);
				}
			}
		}
		Map<String, Object> data = new HashMap<String,Object>();
		data.put("attachment", attachmentList);
		baseMongo.updateSetByObjectId(businessId, data, Constants.RCM_PROJECTFORMALREVIEW_INFO);
	}

	
	
}

/**
 * 
 */
package com.yk.rcm.project.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import util.ThreadLocalUtil;
import util.Util;

import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.yk.common.IBaseMongo;
import com.yk.rcm.project.dao.IUpdateProjectNameMapper;
import com.yk.rcm.project.service.IUpdateProjectNameService;

import common.Constants;
import common.PageAssistant;

/**
 * @author shaosimin
 *
 */
@Service
@Transactional
public class UpdateProjectNameService implements IUpdateProjectNameService {

	@Resource
	private IUpdateProjectNameMapper updateProjectNameMapper;
	@Resource
	private IBaseMongo baseMongo;
	
	@Override
	public void queryAllProject(PageAssistant page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		if(!ThreadLocalUtil.getIsAdmin()){
			//管理员能看所有的
			params.put("userId", ThreadLocalUtil.getUserId());
		}
		if(page.getParamMap() != null){
			params.putAll(page.getParamMap());
		}
		List<Map<String, Object>> list = this.updateProjectNameMapper.queryAllProject(params);
		page.setList(list);
	}

	@Override
	public void updateProject(String projectName, String businessId, String type) {
		if(("bulletin").equals(type)){
			//其他需决策事项
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("businessId", businessId);
			map.put("bulletinName", projectName);
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("bulletinName", projectName);
			//oracle
			this.updateProjectNameMapper.updateBulletin(map);
			//mongo
			this.baseMongo.updateSetById(businessId, params, Constants.RCM_BULLETIN_INFO);
		}else if(("formal").equals(type)){
			//正式评审
			Map<String, Object> formal = new HashMap<String, Object>();
			formal.put("businessId", businessId);
			formal.put("projectName", projectName);
			//oracle
			this.updateProjectNameMapper.updateFormal(formal);
			//mongo
			Map<String, Object> formalass = this.baseMongo.queryById(businessId,Constants.RCM_FORMALASSESSMENT_INFO);
			Document apply = (Document) formalass.get("apply");
			apply.put("projectName",projectName);
			Map<String, Object> formalMongo = new HashMap<String, Object>();
			formalMongo.put("apply", apply);
			this.baseMongo.updateSetByObjectId(businessId, formalMongo, Constants.RCM_FORMALASSESSMENT_INFO);
			//决策通知书
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("projectName", projectName);
			BasicDBObject filter =new BasicDBObject();
			filter.put("projectFormalId", businessId);
			this.baseMongo.updateSetByFilter(filter, params, Constants.RCM_NOTICEDECISION_INFO);
			//评审报告
			this.baseMongo.updateSetByFilter(filter, params, Constants.RCM_FORMALREPORT_INFO);
			//会议
			Map<String, Object> paramss = new HashMap<String, Object>();
			paramss.put("projectName", projectName);
			BasicDBObject filters =new BasicDBObject();
			filters.put("formalId", businessId);
			this.baseMongo.updateSetByFilter(filters, paramss, Constants.FORMAL_MEETING_INFO);
		}else if(("pre").equals(type)){
			//投标评审
			Map<String, Object> pre = new HashMap<String, Object>();
			pre.put("businessId", businessId);
			pre.put("projectName", projectName);
			//oracle
			this.updateProjectNameMapper.updatePre(pre);
			//mongo
			Map<String, Object> pro = this.baseMongo.queryById(businessId,Constants.RCM_PRE_INFO);
			Document apply = (Document) pro.get("apply");
			apply.put("projectName",projectName);
			Map<String, Object> preMongo = new HashMap<String, Object>();
			preMongo.put("apply", apply);
			this.baseMongo.updateSetByObjectId(businessId, preMongo, Constants.RCM_PRE_INFO);
		}
	}


	

	
}

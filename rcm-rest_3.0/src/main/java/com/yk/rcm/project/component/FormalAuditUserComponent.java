/**
 * 
 */
package com.yk.rcm.project.component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import util.Util;

import com.yk.common.IBaseMongo;
import common.Constants;

/**
 * @author 80845530
 *
 */
@Component("formalAudit")
public class FormalAuditUserComponent {
	@Resource IBaseMongo baseMongo;
	
	/**
	 * 查询投资经理
	 * @param businessId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> queryTzManager(String businessId){
		Map<String, Object> formalassessment = baseMongo.queryById(businessId, Constants.RCM_FORMALASSESSMENT_INFO);
		
		Map<String, Object> createby = (Map<String, Object>) formalassessment.get("createby");
		List<Map<String, Object>> users = new ArrayList<Map<String,Object>>();
		if(Util.isNotEmpty(createby)){
			users.add(createby);
		}
//		ProjectRelation projectRelation = (ProjectRelation) SpringUtil.getBean("rcm.ProjectRelation");
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("businessId", businessId);
//		map.put("relationType", new String[]{"0701"});
//		ObjectMapper objectMapper = new ObjectMapper();
//		String json = null;
//		try {
//			json = objectMapper.writeValueAsString(map);
//		} catch (JsonProcessingException e) {
//			json = "{\"businessId\":"+businessId+",\"relationType\":[\"0701\"]}";
//		}
//		List<Map<String, Object>> users = projectRelation.findRelationUserByBusinessId(json);
		return users;
	}
	/**
	 * 查询固定小组成员
	 * @param businessId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> queryTeamMember(String businessId){
		
		Map<String, Object> formalassessment = baseMongo.queryById(businessId, Constants.RCM_FORMALASSESSMENT_INFO);
		
		Map<String, Object> taskallocation = (Map<String, Object>) formalassessment.get("taskallocation");
		
		List<Map<String, Object>> users = new ArrayList<Map<String,Object>>();
		
		if(Util.isNotEmpty(taskallocation)){
			List<Map<String, Object>> fixedGroup = (List<Map<String, Object>>) taskallocation.get("fixedGroup");
			
			if(Util.isNotEmpty(fixedGroup)){
				return fixedGroup;
			}
		}
		
//		ProjectRelation projectRelation = (ProjectRelation) SpringUtil.getBean("rcm.ProjectRelation");
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("businessId", businessId);
//		map.put("relationType", new String[]{"0707"});
//		ObjectMapper objectMapper = new ObjectMapper();
//		String json = null;
//		try {
//			json = objectMapper.writeValueAsString(map);
//		} catch (JsonProcessingException e) {
//			json = "{\"businessId\":"+businessId+",\"relationType\":[\"0707\"]}";
//		}
//		List<Map<String, Object>> users = projectRelation.findRelationUserByBusinessId(json);
		return users;
	}
	
	
	
	/**
	 * 查询评审负责人
	 * @param businessId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> queryPsFuzeren(String businessId){

		Map<String, Object> formalassessment = baseMongo.queryById(businessId, Constants.RCM_FORMALASSESSMENT_INFO);
		
		Map<String, Object> taskallocation = (Map<String, Object>) formalassessment.get("taskallocation");
		
		List<Map<String, Object>> users = new ArrayList<Map<String,Object>>();
		
		if(Util.isNotEmpty(taskallocation)){
			Map<String, Object> reviewLeader = (Map<String, Object>) taskallocation.get("reviewLeader");
			
			if(Util.isNotEmpty(reviewLeader)){
				users.add(reviewLeader);
			}
		}
		
//		ProjectRelation projectRelation = (ProjectRelation) SpringUtil.getBean("rcm.ProjectRelation");
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("businessId", businessId);
//		map.put("relationType", new String[]{"0706"});
//		ObjectMapper objectMapper = new ObjectMapper();
//		String json = null;
//		try {
//			json = objectMapper.writeValueAsString(map);
//		} catch (JsonProcessingException e) {
//			json = "{\"businessId\":"+businessId+",\"relationType\":[\"0706\"]}";
//		}
//		List<Map<String, Object>> users = projectRelation.findRelationUserByBusinessId(json);
		return users;
	}
	
}

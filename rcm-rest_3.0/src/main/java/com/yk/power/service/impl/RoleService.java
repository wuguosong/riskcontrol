/**
 * 
 */
package com.yk.power.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import util.ThreadLocalUtil;
import util.Util;

import com.yk.power.dao.IRoleMapper;
import com.yk.power.service.IDictService;
import com.yk.power.service.IRoleService;

import common.Constants;
import common.PageAssistant;

/**
 * @author wufucan
 *
 */
@Service
@Transactional
public class RoleService implements IRoleService {
	@Resource
	private IRoleMapper roleMapper;
	
	@Resource
	private IDictService dictService;
	
	
	/* (non-Javadoc)
	 * @see com.yk.power.service.IRoleService#queryById(java.lang.String)
	 */
	@Override
	public Map<String, Object> queryById(String id) {
		return this.roleMapper.queryById(id);
	}

	/* (non-Javadoc)
	 * @see com.yk.power.service.IRoleService#queryUserById(java.lang.String)
	 */
	@Override
	public List<Map<String, Object>> queryUserById(String id) {
		return this.roleMapper.queryUserById(id);
	}

	@Override
	public List<Map<String, Object>> queryRoleuserByCode(String code) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("code", code);
		List<Map<String,Object>> retList = this.roleMapper.selectUserByRoleCode(paramMap);
		return retList;
	}

	@Override
	public List<Map<String, Object>> queryReviewUsers() {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String[] psfzrTypes = {"1"};
		paramMap.put("psfzrTypes", psfzrTypes);
		return this.roleMapper.queryPsfzrUsers(paramMap);
	}

	@Override
	public List<Map<String, Object>> queryLegalUsers() {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String[] psfzrTypes = {"2"};
		paramMap.put("psfzrTypes", psfzrTypes);
		return this.roleMapper.queryPsfzrUsers(paramMap);
	}

	@Override
	public List<Map<String, Object>> queryPsfzr() {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String[] psfzrTypes = {"1","2"};
		paramMap.put("psfzrTypes", psfzrTypes);
		return this.roleMapper.queryPsfzrUsers(paramMap);
	}

	@Override
	public boolean ifRoleContainUser(String code) {
		String userId = ThreadLocalUtil.getUserId();
		int count = roleMapper.ifRoleContainUser(code,userId);
		return count > 0;
	}
	
	@Override
	public boolean ifRolesContainUser(String... codes) {
		String userId = ThreadLocalUtil.getUserId();
		int count = roleMapper.ifRolesContainUser(codes,userId);
		return count > 0;
	}

	@Override
	public void queryMeetingLeaderByPage(PageAssistant page) {
		Map<String, Object> params = new HashMap<String, Object>();
		if (page.getParamMap() != null) {
			params.putAll(page.getParamMap());
		}
		params.put("page", page);
		//委员角色code
		params.put("role_code", Constants.ROLE_CODE_DECISION_LEADERS);
		//会议主席 数据字典类型code
		params.put("dictionary_code",Constants.DICT_MEETING_LEADER);
		params.put("orgCodeRoot", Constants.SYS_ORG_CODE_ROOT);
		List<Map<String, Object>> list = roleMapper.queryMeetingLeaderByPage(params);
		page.setList(list);		
	}
	
	@Override
	public Map<String, Object> queryMeetingLeaderById(String user_role_id) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("user_role_id", user_role_id);
		//委员角色code
		params.put("role_code",Constants.ROLE_CODE_DECISION_LEADERS);
		//会议主席 数据字典类型code
		params.put("dictionary_code",Constants.DICT_MEETING_LEADER);
		return roleMapper.queryMeetingLeaderById(params);
	}
	
	@Override
	public int queryRoleUserLastIndexByCode(String code) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("code", code);
		return roleMapper.queryRoleUserLastIndexByCode(paramMap);
	}

	@Override
	public void createMeetingLeader(Map<String, Object> data) {
		String MEETING_LEADER_STATUS = data.get("MEETING_LEADER_STATUS").toString();
		if("1".equals(MEETING_LEADER_STATUS)){
			//添加或更新会议主席
			Map<String, Object> dictInfo = dictService.getDictByCode(Constants.DICT_MEETING_LEADER);
			
			Map<String, Object> dictItem = new  HashMap<String, Object>(10);
			dictItem.put("uuid",Util.getUUID());
			dictItem.put("fk_dictionary_uuid", dictInfo.get("UUID"));
			dictItem.put("item_name", data.get("NAME"));
			dictItem.put("item_code", data.get("UUID"));
			dictItem.put("is_enabled", "1");
			dictItem.put("business_type", Constants.DICT_MEETING_LEADER);
			dictService.addDictItemMeetLeader(dictItem);
		}
		Map<String, Object> roleInfo = roleMapper.queryByCode(Constants.ROLE_CODE_DECISION_LEADERS);
		data.put("USER_ROLE_ID",Util.getUUID());
		data.put("USER_ID",data.get("UUID"));
		data.put("ROLE_ID",roleInfo.get("ROLE_ID"));
		data.put("CREATE_DATE",new Date());
		data.put("STATE","1");
		data.put("CODE",Constants.ROLE_CODE_DECISION_LEADERS);
		data.put("ORDER_BY",data.get("ORDER_BY"));
		int edit = roleMapper.createMeetingLeader(data);
		System.out.println(edit);
	}

	@Override
	public void updateMeetingLeader(Map<String, Object> data) {
		//页面第一次保存后，是没有MEETING_LEADER_UUID 
		Map<String, Object> meetingLeaderOld = queryMeetingLeaderById(data.get("USER_ROLE_ID").toString());
		
		String MEETING_LEADER_STATUS = data.get("MEETING_LEADER_STATUS").toString();
		Object meetingLeaderUuid = meetingLeaderOld.get("MEETING_LEADER_UUID");
		if("1".equals(MEETING_LEADER_STATUS)){
			//添加或更新会议主席
			if(null == meetingLeaderUuid){
				Map<String, Object> dictInfo = dictService.getDictByCode(Constants.DICT_MEETING_LEADER);
				Map<String, Object> dictItem = new  HashMap<String, Object>(10);
				dictItem.put("uuid",Util.getUUID());
				dictItem.put("fk_dictionary_uuid", dictInfo.get("UUID"));
				dictItem.put("item_name", data.get("NAME"));
				dictItem.put("item_code", data.get("UUID"));
				dictItem.put("is_enabled", "1");
				dictItem.put("business_type", Constants.DICT_MEETING_LEADER);
				dictService.addDictItemMeetLeader(dictItem);
			}else{
				Map<String, Object> dictItem = dictService.getDictItemById(meetingLeaderUuid.toString());
				dictItem.put("uuid", dictItem.get("UUID"));
				dictItem.put("item_name", data.get("NAME"));
				dictItem.put("item_code", data.get("UUID"));
				dictItem.put("is_enabled", dictItem.get("IS_ENABLED"));
				dictItem.put("business_type", dictItem.get("BUSINESS_TYPE"));
				dictService.updateDictItemMeetLeader(dictItem);
			}
		}else if(meetingLeaderUuid != null){
			dictService.deleteDictItemById(meetingLeaderUuid.toString());
		}
		data.put("CODE",Constants.ROLE_CODE_DECISION_LEADERS);
		data.put("USER_ID",data.get("UUID"));
		data.put("CREATE_DATE",new Date());
		data.put("STATE","1");
		data.put("ORDER_BY",data.get("ORDER_BY"));
		roleMapper.updateMeetingLeader(data);
	}

	@Override
	public void deleteMeetingLeaderById(String user_role_id) {
		Map<String, Object> meetingLeaderInfo = queryMeetingLeaderById(user_role_id);
		
		String MEETING_LEADER_STATUS = meetingLeaderInfo.get("MEETING_LEADER_STATUS").toString();
		if("1".equals(MEETING_LEADER_STATUS)){
			String uuid = meetingLeaderInfo.get("MEETING_LEADER_UUID").toString();
			dictService.deleteDictItemById(uuid);
		}
		
		roleMapper.deleteById(user_role_id);
	}

	@Override
	public Map<String, Object> queryMeetingLeaderByUserId(String user_id) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("user_id", user_id);
		params.put("role_code",Constants.ROLE_CODE_DECISION_LEADERS);
		return roleMapper.queryMeetingLeaderByUserId(params);
	}
	
	@Override
	public void updateUserIdByRoleCode(String userId,String roleCode) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userId", userId);
		map.put("code", roleCode);
		roleMapper.updateUserIdByRoleCode(map);
	}

	@Override
	public void updateOneUserByRoleId(String roleId, String newUserId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("roleId", roleId);
		map.put("newUserId", newUserId);
		roleMapper.updateOneUserByRoleId(map);
	}
	
	@Override
	public void queryRoleListByPage(PageAssistant page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		if (page.getParamMap() != null) {
			params.putAll(page.getParamMap());
		}
		List<Map<String, Object>> list = roleMapper
				.queryRoleListByPage(params);
		page.setList(list);
	}
	
	@Override
	public int countRoleUserFunc(String[] ids) {
		return roleMapper.countRoleUserFunc(ids);
	}
	
	@Override
	public int countServiceRole(String[] ids) {
		return roleMapper.countServiceRole(ids);
	}
	
	@Override
	public void deleteRoleById(String[] ids) {
		roleMapper.deleteRoleById(ids);
	}
	
	@Override
	public void createRole(Map<String, Object> role) {
		roleMapper.createRole(role);
	}
	
	@Override
	public void updateRole(Map<String, Object> role) {
		roleMapper.updateRole(role);
	}
	
	@Override
	public void queryRoleUserListByPage(PageAssistant page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		if (page.getParamMap() != null) {
			params.putAll(page.getParamMap());
		}
		params.put("orgCodeRoot", Constants.SYS_ORG_CODE_ROOT);
		List<Map<String, Object>> list = roleMapper
				.queryRoleUserListByPage(params);
		page.setList(list);
	}
	
	@Override
	public void queryRoleAddUserByPage(PageAssistant page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		if (page.getParamMap() != null) {
			params.putAll(page.getParamMap());
		}
		params.put("orgCodeRoot", Constants.SYS_ORG_CODE_ROOT);
		List<Map<String, Object>> list = roleMapper
				.queryRoleAddUserByPage(params);
		page.setList(list);
	}

	@Override
	public void addRoleUser(List<Map<String, Object>> roleUsers) {
		for (Map<String, Object> map : roleUsers) {
			Map<String, Object> resutlData = roleMapper.queryByRoleUser(map);
			if(Util.isEmpty(resutlData)){
				roleMapper.addRoleUser(map);
			}
		}
	}
	
	@Override
	public void deleteRoleUserById(String[] ids) {
		roleMapper.deleteRoleUserById(ids);
	}
	
	@Override
	public List<Map<String, Object>> queryFunc() {
		return roleMapper.queryFunc();
	}
	
	@Override
	public List<Map<String, Object>> getRoleAndFunc(String roleId) {
		return roleMapper.getRoleAndFunc(roleId);
	}
	
	@Override
	public void deleteRoleAndFunc(Map<String, Object> param) {
		roleMapper.deleteRoleAndFunc(param);
	}
	
	@Override
	public void insertRoleAndFunc(Map<String, Object> param) {
		roleMapper.insertRoleAndFunc(param);
	}
	
	@Override
	public List<Map<String, Object>> queryMeetingChairman() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ROLE_DECISION_LEADERS", Constants.ROLE_CODE_DECISION_LEADERS);
		params.put("DICT_MEETING_LEADER", Constants.DICT_MEETING_LEADER);
		return roleMapper.queryMeetingChairman(params);
	}

	@Override
	public Map<String, Object> queryRoleByCode(String code) {
		return roleMapper.queryByCode(code);
	}

	@Override
	public Map<String, Object> queryRoleUserByRoleId(String roleId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("roleId",roleId);
		return roleMapper.queryRoleUserByRoleId(params);
	}
	
	
}

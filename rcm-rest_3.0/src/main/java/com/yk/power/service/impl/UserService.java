/**
 * 
 */
package com.yk.power.service.impl;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.bson.Document;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import util.DbUtil;
import util.Util;

import com.yk.flow.util.JsonUtil;
import com.yk.power.dao.IUserMapper;
import com.yk.power.service.IUserService;

import common.BusinessException;
import common.Constants;
import common.PageAssistant;

/**
 * @author wufucan
 *
 */
@Service
@Transactional
public class UserService implements IUserService {
	@Resource
	private IUserMapper userMapper;
	/* (non-Javadoc)
	 * @see com.yk.power.service.IUserService#queryById(java.lang.String)
	 */
	@Override
	public Map<String, Object> queryById(String userId) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("rootCode", Constants.SYS_ORG_CODE_ROOT);
		paramMap.put("userId", userId);
		return this.userMapper.queryById(paramMap);
	}
	@Override
	public void queryUserForSelected(PageAssistant page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		if(page.getParamMap() != null){
			params.putAll(page.getParamMap());
		}
		String orderBy = page.getOrderBy();
		if(orderBy == null){
			orderBy = " account ";
		}
		params.put("orderBy", orderBy);
		params.put("orgCodeRoot", Constants.SYS_ORG_CODE_ROOT);
		List<Map<String, Object>> list = this.userMapper.queryByCondition(params);
		page.setList(list);
	}
	
	@Override
	public void getAll(PageAssistant page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		if(page.getParamMap() != null){
			params.putAll(page.getParamMap());
		}
		String orderBy = page.getOrderBy();
		if(orderBy == null){
			orderBy = " account ";
		}
		params.put("orderBy", orderBy);
		params.put("BEWGCode", Constants.SYS_ORG_CODE_ROOT);
		List<Map<String, Object>> list = this.userMapper.selectUser(params);
		page.setList(list);
	}
	
	@Override
	public void getDirectiveUserAll(PageAssistant page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		if(page.getParamMap() != null){
			params.putAll(page.getParamMap());
		}
		String orderBy = page.getOrderBy();
		if(orderBy == null){
			orderBy = " account ";
		}
		params.put("orderBy", orderBy);
		List<Map<String, Object>> list = this.userMapper.directiveUser(params);
		page.setList(list);
	}
	
	@Override
	public Map<String, Object> getSysUserByID(String id) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("uuid", id);
		paramMap.put("rootCode", Constants.SYS_ORG_CODE_ROOT);
		Map user = this.userMapper.selectOneUser(paramMap);
		
		paramMap.put("jobcode", (String) user.get("CODE"));
		List<Map> userPosition = this.userMapper.selectUserPosition(paramMap);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userDate", user);
		map.put("postition", userPosition);
		return map;
	}
	
	@Override
	public boolean isAdmin(String userId){
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("userId", userId);
		int num = this.userMapper.selectIsAdmin(paramMap);
		return num > 0;
	}
	@Override
	public Map<String, Object> getAUser(String json){
		Map<String, Object> params = Util.parseJson2Map(json);
		Map<String, Object> map = this.userMapper.selectAUser(params);
		if(map == null || map.size()==0) throw new RuntimeException("未找到此用户");
		Map<String, Object> retMap = new HashMap<String, Object>();
		String uuid = (String)map.get("UUID");
		String account = (String)map.get("ACCOUNT");
		String userName = (String)map.get("NAME");
		retMap.put("UUID", uuid);
		retMap.put("userID", account);
		retMap.put("userName", userName);
		/**
		 * Add By Sunny Qi On 2019-02-18 Start
		 */
		// 返回登录人所在大区数据
		String orgId = (String)map.get("ORGPKVALUE");
		String orgName = (String)map.get("ORGNAME");
		String deptId = (String)map.get("DEPTPKVALUE");
		String deptName = (String)map.get("DEPTNAME");
		retMap.put("orgId", orgId);
		retMap.put("orgName", orgName);
		retMap.put("deptId", deptId);
		retMap.put("deptName", deptName);
		/**
		 * Add By Sunny Qi On 2019-02-18 End
		 */
		
		retMap.put("isAdmin", this.isAdmin(uuid));
		
		params = new HashMap<String, Object>();
		params.put("userId", uuid);
		List<Map<String, Object>> roles = this.userMapper.queryRoleByUser(params);
		retMap.put("roles", roles);
		return retMap;
	}
	@Override
	public List<Map<String, Object>> getAllRole() {
		return this.userMapper.getAllRole();
	}
	@Override
	public List<Map<String, Object>> getRoleByUserId(String userId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userId", userId);
		return this.userMapper.getRoleByUserId(map);
	}
	
	@Override
	public void saveUserRole(String userId, String roleArr) {
		this.userMapper.deleteUserRoleByUserId(userId);
		ArrayList<Map<String,Object>> roleDocArr = JsonUtil.fromJson(roleArr, ArrayList.class);
		if(Util.isNotEmpty(roleDocArr)){
			for (Map<String,Object> role : roleDocArr) {
				HashMap<String, Object> map = new HashMap<String,Object>();
				map.put("user_role_id", Util.getUUID());
				map.put("user_id", userId);
				map.put("role_id", role.get("ROLE_ID"));
				map.put("create_date", role.get("CREATE_DATE"));
				map.put("state", role.get("STATE"));
				map.put("code", role.get("CODE"));
				this.userMapper.saveUserRole(map);
			}
		}
	}
}

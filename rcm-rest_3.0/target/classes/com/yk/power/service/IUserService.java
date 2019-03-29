/**
 * 
 */
package com.yk.power.service;

import java.util.List;
import java.util.Map;

import common.PageAssistant;

/**
 * 用户模块
 * @author wufucan
 *
 */
public interface IUserService {
	/**
	 * 根据uuid查询用户信息
	 * @param userId
	 * @return
	 */
	public Map<String, Object> queryById(String userId);
	/**
	 * 用户选择框数据查询
	 * @param page
	 */
	public void queryUserForSelected(PageAssistant page);
	
	public void getAll(PageAssistant page);
	
	public void getDirectiveUserAll(PageAssistant page);
	
	public Map<String, Object> getSysUserByID(String id);
	
	boolean isAdmin(String userId);
	
	Map<String, Object> getAUser(String json);
	
	/**
	 * 查询所有角色
	 * @return
	 */
	public List<Map<String, Object>> getAllRole();
	
	public List<Map<String, Object>> getRoleByUserId(String userId);
	
	public void saveUserRole(String userId, String roleArr);
	
}

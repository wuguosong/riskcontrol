/**
 * 
 */
package com.yk.power.service;

import java.util.List;
import java.util.Map;

import common.PageAssistant;

/**
 * 角色模块
 * @author wufucan
 *
 */
public interface IRoleService {
	
	/**
	 * 根据id查询角色
	 * @param id
	 * @return
	 */
	public Map<String, Object> queryById(String id);
	/**
	 * 根据角色id查询用户
	 * @param id
	 * @return
	 */
	public List<Map<String, Object>> queryUserById(String id);
	/**
	 * 根据角色code查找角色用户
	 * @param id
	 * @return
	 */
	public List<Map<String, Object>> queryRoleuserByCode(String code);
	/**
	 * 查询业务评审负责人
	 * @return
	 */
	public List<Map<String, Object>> queryReviewUsers();
	/**
	 * 查询法律评审负责人
	 * @return
	 */
	public List<Map<String, Object>> queryLegalUsers();
	/**
	 * 查询所有的评审负责人
	 * @return
	 */
	public List<Map<String, Object>> queryPsfzr();
	
	/**
	 * 判断当前登录用户是否为指定角色
	 * @return
	 * 	true:是
	 *  false:不是
	 */
	public boolean ifRoleContainUser(String code);
	
	/**
	 * 判断当前登录用户是否为指定角色范围
	 * @return
	 * 	true:是
	 *  false:不是
	 */
	public boolean ifRolesContainUser(String... codes);
	
	/**
	 * 分页查询委员角色用户
	 * @param params
	 * @return
	 */
	public void queryMeetingLeaderByPage(PageAssistant page);
	
	/**
	 * 获取角色用户详情信息
	 * @param params
	 * @return
	 */
	public Map<String, Object> queryMeetingLeaderById(String id);
	
	/**
	 * 根据  角色下的用户 code 最后序号
	 */
	public int queryRoleUserLastIndexByCode(String code);
	
	/**
	 * 插入委员
	 * @param data
	 * @return
	 */
	public void createMeetingLeader(Map<String, Object> data);
	
	/**
	 * 更新委员
	 * @param data
	 * @return
	 */
	public void updateMeetingLeader(Map<String, Object> data);
	
	/**
	 * 删除委员
	 * @param data
	 */
	public void deleteMeetingLeaderById(String id);
	
	public Map<String, Object> queryMeetingLeaderByUserId(String uuid);
	/**
	 * 根据roleCode修改橘色的人员关系
	 * @param userId
	 * @param roleCode
	 */
	void updateUserIdByRoleCode(String userId, String roleCode);
	/**
	 * 修改角色人员关系（角色对应一个人）
	 * @param groupId
	 * @param newUserId
	 */
	public void updateOneUserByRoleId(String roleId, String newUserId);
	
	/**
	 * 分页查询所有角色
	 * @param page
	 */
	public void queryRoleListByPage(PageAssistant page);
	
	/**
	 * 统计角色下的菜单和用户
	 * @param id
	 * @return
	 */
	public int countRoleUserFunc(String[] ids);
	
	/**
	 * 统计指定角色ID中的功能角色
	 * @param id
	 * @return
	 */
	public int countServiceRole(String[] ids);
	
	/**
	 * 根据ID删除角色
	 * @param id
	 * @return
	 */
	public void deleteRoleById(String[] ids);
	
	/**
	 * 创建角色
	 * @param data
	 * @return
	 */
	public void createRole(Map<String, Object> role);
	
	/**
	 * 更新角色
	 * @param data
	 * @return
	 */
	public void updateRole(Map<String, Object> role);
	
	/**
	 * 分页查询角色用户
	 * @param page
	 */
	public void queryRoleUserListByPage(PageAssistant page);
	
	/**
	 * 查询角色可以添加用户
	 * @param page
	 */
	public void queryRoleAddUserByPage(PageAssistant page);
	
	/**
	 * 添加角色用户
	 * @param roleUsers
	 */
	public void addRoleUser(List<Map<String, Object>> roleUsers);
	
	/**
	 * 根据ID删除角色用户
	 * @param ids
	 */
	public void deleteRoleUserById(String[] ids);
	
	/**
	 * 查询所有菜单
	 */
	public List<Map<String, Object>> queryFunc();
	
	/**
	 * 查询角色菜单ID
	 * @param roleId
	 * @return
	 */
	public List<Map<String, Object>> getRoleAndFunc(String roleId);
	
	/**
	 * 删除角色菜单
	 * @param roleId
	 * @return
	 */
	public void deleteRoleAndFunc(Map<String, Object> paramMap);
	
	/**
	 * 添加角色菜单
	 * @param roleId
	 * @return
	 */
	public void insertRoleAndFunc(Map<String, Object> paramMap);
	
	/**
	 * 获取会议主席
	 * @return
	 */
	public List<Map<String, Object>> queryMeetingChairman();
	
	/**
	 * 按code查询角色
	 * @param code
	 * @return
	 */
	public Map<String, Object> queryRoleByCode(String code);
	/**
	 * 按角色id查询用户角色表
	 * @param roleId
	 * @return
	 */
	public Map<String, Object> queryRoleUserByRoleId(String roleId);
}

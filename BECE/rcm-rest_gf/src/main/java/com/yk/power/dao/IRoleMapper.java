/**
 * 
 */
package com.yk.power.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.yk.common.BaseMapper;
import org.springframework.stereotype.Repository;

/**
 * @author wufucan
 *
 */
@Repository
public interface IRoleMapper extends BaseMapper {

	/**
	 * 根据id查询
	 * @param userId
	 * @return
	 */
	public Map<String, Object> queryById(@Param("roleId")String roleId);
	/**
	 * 根据角色id查询用户
	 * @param id
	 * @return
	 */
	public List<Map<String, Object>> queryUserById(@Param("roleId")String roleId);
	/**
	 * 根据角色code查找角色用户
	 * @param 
	 * @return
	 */
	public List<Map<String, Object>> selectUserByRoleCode(Map<String, Object> paramMap);
	/**
	 * 根据角色codes查找角色用户
	 * @param 
	 * @return
	 */
	public List<Map<String, Object>> queryUsersByRoleCodes(Map<String, Object> paramMap);
	/**
	 * 根据角色codes查找角色用户
	 * @param 
	 * @return
	 */
	public List<Map<String, Object>> queryPsfzrUsers(Map<String, Object> params);
	
	/**
	 * 判断  指定 角色  是否  包含当前登录用户
	 * @param code
	 * @param userId
	 * @return 
	 *   >0  表示包含
	 *   <1表示不包含
	 */
	public int ifRoleContainUser(@Param("code")String code, @Param("userId")String userId);
	
	/**
	 * 分页查询委员角色用户
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> queryMeetingLeaderByPage(
			Map<String, Object> params);
	
	/**
	 * 获取决策委员用户详情信息
	 * @param params
	 * @return
	 */
	public Map<String, Object> queryMeetingLeaderById(Map<String, Object> params);
	
	/**
	 * 根据  角色下的用户 code 最后一个序号
	 */
	public int queryRoleUserLastIndexByCode(Map<String, Object> paramMap);
	
	public int createMeetingLeader(Map<String, Object> data);
	
	/**
	 * 根据id查询
	 * @param userId
	 * @return
	 */
	public Map<String, Object> queryByCode(@Param("code")String code);
	
	/**
	 * 更新委员角色ID
	 * @param id
	 */
	public int updateMeetingLeader(Map<String, Object> data);
	
	/**
	 * 删除角色用户ID
	 * @param id
	 */
	public void deleteById(@Param("user_role_id")String user_role_id);
	
	/**
	 * 根据用户ID和类型查询 用户角色表
	 * @param params
	 * @return
	 */
	public Map<String, Object> queryMeetingLeaderByUserId(Map<String, Object> params);
	/**
	 * 根据roleCode修改橘色的人员关系
	 * @param map
	 */
	public void updateUserIdByRoleCode(Map<String, Object> map);
	/**
	 * 修改用户角色（角色仅对应一个人员）
	 * @param map
	 */
	public void updateOneUserByRoleId(Map<String, Object> map);
	
	/**
	 * 判断  指定 角色  是否  包含当前登录用户
	 * @param code
	 * @param userId
	 * @return 
	 *   >0  表示包含
	 *   <1表示不包含
	 */
	public int ifRolesContainUser(@Param("codes")String[] codes,@Param("userId") String userId);
	
	/**
	 * 分页查询所有角色
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> queryRoleListByPage(
			Map<String, Object> params);
	
	/**
	 * 统计角色下的菜单和用户
	 * @param id
	 * @return
	 */
	public int countRoleUserFunc(@Param("ids")String[] ids);
	
	/**
	 * 统计指定ID中功能角色
	 * @param id
	 * @return
	 */
	public int countServiceRole(@Param("ids")String[] ids);
	
	/**
	 * 根据ID删除角色
	 * @param id
	 * @return
	 */
	public void deleteRoleById(@Param("ids")String[] ids);
	
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
	 * @param request
	 * @return
	 */
	public List<Map<String, Object>> queryRoleUserListByPage(
			Map<String, Object> params);
	
	/**
	 * 分页查询角色可以添加的用户
	 * @param request
	 * @return
	 */
	public List<Map<String, Object>> queryRoleAddUserByPage(
			Map<String, Object> params);
	
	/**
	 * 添加角色用户
	 * @param roleUsers
	 */
	public void addRoleUser(Map<String, Object> user);
	
	/**
	 * 根据ID删除角色用户
	 * @param ids
	 */
	public void deleteRoleUserById(@Param("ids")String[] ids);
	
	/**
	 * 根据角色类型和用户ID，查询角色用户信息(添加用户角色之前调用)
	 * @param map
	 * @return
	 */
	public Map<String, Object> queryByRoleUser(Map<String, Object> map);
	
	/**
	 * 查询所有菜单
	 */
	public List<Map<String, Object>> queryFunc();
	
	/**
	 * 查询角色菜单ID
	 * @param roleId
	 * @return
	 */
	public List<Map<String, Object>> getRoleAndFunc(@Param("roleId")String roleId);
	
	/**
	 * 删除角色菜单
	 * @param roleId
	 * @return
	 */
	public void deleteRoleAndFunc(Map<String, Object> param);
	
	/**
	 * 添加角色菜单
	 * @param roleId
	 * @return
	 */
	public void insertRoleAndFunc(Map<String, Object> param);
	
	/**
	 * 获取会议主席
	 * @return
	 */
	public List<Map<String, Object>> queryMeetingChairman(
			Map<String, Object> params);
	/**
	 * 按角色id查询用户角色表
	 * @param params
	 * @return
	 */
	public Map<String, Object> queryRoleUserByRoleId(Map<String, Object> params);
	
	/**
	 * 插入项目角色数据
	 * @param dataForProRole
	 * @return
	 */
	public void insertProRole(Map<String, Object> dataForProRole);
	
	
	
	
	
	
	/**
	 * 根据角色类型和项目ID，查询角色项目信息(添加用户角色之前调用)
	 * @param map
	 * @return
	 */
	public Map<String, Object> queryByRoleProject(Map<String, Object> map);
	/**
	 * 分页查询角色项目
	 * @param request
	 * @return
	 */
	public List<Map<String, Object>> queryRoleProjectListByPage(
			Map<String, Object> params);
	
	/**
	 * 分页查询角色可以添加的项目
	 * @param request
	 * @return
	 */
	public List<Map<String, Object>> queryRoleAddProjectByPage(
			Map<String, Object> params);
	
	/**
	 * 添加角色项目
	 * @param roleUsers
	 */
	public void addRoleProject(Map<String, Object> user);
	
	/**
	 * 根据ID删除角色项目
	 * @param ids
	 */
	public void deleteRoleProjectById(@Param("ids")String[] ids);
	
	
	/**
	 * 根据角色类型和组织ID，查询角色组织信息(添加用户角色之前调用)
	 * @param map
	 * @return
	 */
	public Map<String, Object> queryByRoleOrg(Map<String, Object> map);
	/**
	 * 分页查询角色组织
	 * @param request
	 * @return
	 */
	public List<Map<String, Object>> queryRoleOrgListByPage(
			Map<String, Object> params);
	
	/**
	 * 分页查询角色可以添加的组织
	 * @param request
	 * @return
	 */
	public List<Map<String, Object>> queryRoleAddOrgByPage(
			Map<String, Object> params);
	
	/**
	 * 添加角色组织
	 * @param roleUsers
	 */
	public void addRoleOrg(Map<String, Object> user);
	
	/**
	 * 根据ID删除角色项目
	 * @param ids
	 */
	public void deleteRoleOrgById(@Param("ids")String[] ids);
	
	/**
	 * 插入组织角色数据
	 * @param dataForOrgRole
	 * @return
	 */
	public void insertOrgRole(Map<String, Object> dataForProRole);
	
	
	/**
	 * 通过组织查组织对应的角色ID
	 * @param request
	 * @return
	 */
	public List<Map<String, Object>> queryRoleIdByOrgId(
			Map<String, Object> params);
	
	/**
	 * 查询所有组织
	 *//*
	public List<Map<String, Object>> queryOrg();
	
	*//**
	 * 查询角色组织ID
	 * @param roleId
	 * @return
	 *//*
	public List<Map<String, Object>> getRoleAndOrg(@Param("roleId")String roleId);
	
	*//**
	 * 删除角色组织
	 * @param roleId
	 * @return
	 *//*
	public void deleteRoleAndOrg(Map<String, Object> param);
	
	*//**
	 * 添加角色组织
	 * @param roleId
	 * @return
	 *//*
	public void insertRoleAndOrg(Map<String, Object> param);*/
	
}

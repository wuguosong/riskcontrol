package com.yk.power.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import util.ThreadLocalUtil;
import util.Util;

import com.yk.flow.util.JsonUtil;
import com.yk.power.service.IDictService;
import com.yk.power.service.IRoleService;
import common.PageAssistant;
import common.Result;
/**
 * 角色模块
 * @author Administrator
 *
 */
@Controller
@RequestMapping("/role")
public class RoleController {
	@Resource
	private IRoleService roleService;
	@Resource
	private IDictService dictService;
	/**
	 * 根据角色id查询人
	 * @param id
	 * @return
	 */
	@RequestMapping("/queryUserById")
	@ResponseBody
	public Result queryUserById(String id){
		Result result = new Result();
		List<Map<String, Object>> dataMap = roleService.queryUserById(id);
		result.setResult_data(dataMap);
		return result;
	}
	/**
	 * 根据角色查人
	 * @param code
	 * @return
	 */
	@RequestMapping("queryRoleuserByCode")
	@ResponseBody
	public Result queryRoleuserByCode(String code){
		Result result = new Result();
		List<Map<String, Object>> paramMap = roleService.queryRoleuserByCode(code);
		result.setResult_data(paramMap);
		return result;
	}
	/**
	 * 查询业务评审负责人
	 * @return
	 */
	@RequestMapping("/queryReviewUsers")
	@ResponseBody
	public Result queryReviewUsers(){
		Result result = new Result();
		List<Map<String, Object>> paramMap = roleService.queryReviewUsers();
		result.setResult_data(paramMap);
		return result;
	}
	
	/**
	 * 查询法律评审负责人
	 * @return
	 */
	@RequestMapping("/queryLegalUsers")
	@ResponseBody
	public Result queryLegalUsers(){
		Result result = new Result();
		List<Map<String, Object>> paramMap = roleService.queryLegalUsers();
		result.setResult_data(paramMap);
		return result;
	}
	
	/**
	 * 查询所有的评审负责人
	 * @return
	 */
	@RequestMapping("/queryPsfzr")
	@ResponseBody
	public Result queryPsfzr(){
		Result result = new Result();
		List<Map<String, Object>> paramMap = roleService.queryPsfzr();
		result.setResult_data(paramMap);
		return result;
	}
	
	/**
	 * 根据角色查人
	 * @param code
	 * @return
	 */
	@RequestMapping("queryMeetingLeaderByPage")
	@ResponseBody
	public Result queryMeetingLeaderByPage(HttpServletRequest request){
		Result result = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		roleService.queryMeetingLeaderByPage(page);
		result.setResult_data(page);
		return result;
	}
	
	/**
	 * 根据角色查人
	 * @param code
	 * @return
	 */
	@RequestMapping("queryMeetingLeaderById")
	@ResponseBody
	public Result queryMeetingLeaderById(String user_role_id){
		Result result = new Result();
		Map<String, Object> data = roleService.queryMeetingLeaderById(user_role_id);
		result.setResult_data(data);
		return result;
	}
	
	/**
	 * 根据角色查人
	 * @param code
	 * @return
	 */
	@RequestMapping("queryRoleUserLastIndexByCode")
	@ResponseBody
	public Result queryRoleUserLastIndexByCode(String code){
		Result result = new Result();
		int edit = roleService.queryRoleUserLastIndexByCode(code);
		result.setResult_data(edit);
		return result;
	}
	
	/**
	 * 创建委员
	 * @param json
	 * @param request
	 * @return
	 */
	@RequestMapping("createMeetingLeader")
	@ResponseBody
	public Result createMeetingLeader(String json, HttpServletRequest request){
		Result result = new Result();
		Map<String, Object> data = Util.parseJson2Map(json);
		
		//判断委员是否存在
		Map<String, Object> data2 = roleService.queryMeetingLeaderByUserId(data.get("UUID").toString());
		if(null != data2 && data2.size() > 0){
			return result.setSuccess(false).setResult_name("委员不能重复添加!");
		}
		
		roleService.createMeetingLeader(data);
		result.setResult_data(data.get("USER_ROLE_ID"));
		return result;
	}
	
	/**
	 * 更新委员
	 * @param json
	 * @param request
	 * @return
	 */
	@RequestMapping("updateMeetingLeader")
	@ResponseBody
	public Result updateMeetingLeader(String json, HttpServletRequest request){
		Result result = new Result();
		Map<String, Object> data = Util.parseJson2Map(json);
		
		//委员是否有变动?
		String user_role_id = data.get("USER_ROLE_ID").toString();
		String user_id = data.get("UUID").toString();
		Map<String, Object> oldUserRoleInfo = roleService.queryMeetingLeaderById(user_role_id);
		if(!oldUserRoleInfo.get("UUID").toString().equals(user_id)){
			//判断委员是否存在
			Map<String, Object> data2 = roleService.queryMeetingLeaderByUserId(user_id);
			if(null != data2 && data2.size() > 0){
				return result.setSuccess(false).setResult_name("委员不能重复添加!");
			}
		}
		
		roleService.updateMeetingLeader(data);
		result.setResult_data(user_role_id);
		return result;
	}
	
	/**
	 * 删除委员
	 * @param id
	 * @param request
	 * @return
	 */
	@RequestMapping("deleteMeetingLeaderById")
	@ResponseBody
	public Result deleteMeetingLeaderById(String id, HttpServletRequest request){
		Result result = new Result();
		roleService.deleteMeetingLeaderById(id);
		return result;
	}
	
	/**
	 * 分页查询所有角色
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryRoleListByPage")
	@ResponseBody
	public Result queryRoleListByPage(HttpServletRequest request) {
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		Result result = new Result();
		roleService.queryRoleListByPage(page);
		result.setResult_data(page);
		return result;
	}
	
	/**
	 * 删除角色之前验证
	 * @param request
	 * @return
	 */
	@RequestMapping("/deleteRoleVali")
	@ResponseBody
	public Result deleteRoleVali(String id) {
		Result result = new Result();
		String[] idArray = id.split(",");
		int count = roleService.countRoleUserFunc(idArray);
		if(count > 0){
			return result.setSuccess(false).setResult_name("角色下的用户或菜单未删除！");
		}
		count = roleService.countServiceRole(idArray);
		if(count > 0){
			return result.setSuccess(false).setResult_name("要删除的角色中含有业务角色，不允许删除!");
		}
		return result;
	}
	
	/**
	 * 根据ID删除角色
	 * @param id
	 * @param request
	 * @return
	 */
	@RequestMapping("deleteRoleById")
	@ResponseBody
	public Result deleteRoleById(String id){
		Result result = new Result();
		roleService.deleteRoleById(id.split(","));
		return result;
	}
	
	/**
	 * 创建角色
	 * @param json
	 * @param request
	 * @return
	 */
	@RequestMapping("createRole")
	@ResponseBody
	public Result createRole(String json, HttpServletRequest request){
		Result result = new Result();
		Map<String, Object> roleInfo = Util.parseJson2Map(json);
		String roleId = Util.getUUID();
		roleInfo.put("ROLE_ID", roleId);
		roleInfo.put("ROLE_PID","0");
		//类型默认功能角色
		roleInfo.put("TYPE","0");
		roleInfo.put("CREATE_DATE",new Date());
		//角色状态默认启用
		roleInfo.put("STATE","1");
		
		roleService.createRole(roleInfo);
		
		result.setResult_data(roleId);
		return result;
	}
	
	/**
	 * 更新角色
	 * @param json
	 * @param request
	 * @return
	 */
	@RequestMapping("updateRole")
	@ResponseBody
	public Result updateRole(String json, HttpServletRequest request){
		Result result = new Result();
		Map<String, Object> roleInfo = Util.parseJson2Map(json);
		roleInfo.put("LAST_UPDATE_DATE",new Date());
		
		roleService.updateRole(roleInfo);
		
		result.setResult_data(roleInfo.get("ROLE_ID"));
		return result;
	}
	
	/**
	 * 根据id查询角色
	 * @param id
	 * @return
	 */
	@RequestMapping("/queryById")
	@ResponseBody
	public Result queryById(String id){
		Result result = new Result();
		Map<String, Object> role = roleService.queryById(id);
		result.setResult_data(role);
		return result;
	}
	
	/**
	 * 分页查询角色用户
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryRoleUserListByPage")
	@ResponseBody
	public Result queryRoleUserListByPage(HttpServletRequest request) {
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		Result result = new Result();
		roleService.queryRoleUserListByPage(page);
		result.setResult_data(page);
		return result;
	}
	
	/**
	 * 分页查询角色可以添加的用户
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryRoleAddUserByPage")
	@ResponseBody
	public Result queryRoleAddUserByPage(HttpServletRequest request) {
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		Result result = new Result();
		roleService.queryRoleAddUserByPage(page);
		result.setResult_data(page);
		return result;
	}
	
	/**
	 * 添加角色用户
	 * @param roleId
	 * @param json
	 * @param request
	 * @return
	 */
	@RequestMapping("/addRoleUser")
	@ResponseBody
	public Result addRoleUser(String roleId,String json,HttpServletRequest request) {
		List<Map<String,Object>> roleUsers = JsonUtil.fromJson(json, List.class);
		Result result = new Result();
		Map<String, Object> roleInfo = roleService.queryById(roleId);
		for (Map<String, Object> map : roleUsers) {
			Object userId = map.get("UUID");
			map.put("USER_ROLE_ID", Util.getUUID());
			map.put("USER_ID", userId);
			map.put("ROLE_ID", roleId);
			map.put("CREATE_DATE", new Date());
			map.put("STATE","1");
			map.put("CODE", roleInfo.get("CODE"));
			map.put("ORDER_BY", null);
		}
		roleService.addRoleUser(roleUsers);
		return result;
	}
	
	/**
	 * 根据ID删除角色用户
	 * @param id
	 * @param request
	 * @return
	 */
	@RequestMapping("deleteRoleUserById")
	@ResponseBody
	public Result deleteRoleUserById(String id){
		Result result = new Result();
		roleService.deleteRoleUserById(id.split(","));
		return result;
	}
	
	@RequestMapping("queryFunc")
	@ResponseBody
	public Result queryFunc(){
		Result result = new Result();
		List<Map<String, Object>> list = roleService.queryFunc();
		result.setResult_data(list);
		return result;
	}
	
	@RequestMapping("getRoleAndFunc")
	@ResponseBody
	public Result getRoleAndFunc(String roleId){
		Result result = new Result();
		List<Map<String, Object>> list = roleService.getRoleAndFunc(roleId);
		result.setResult_data(list);
		return result;
	}
	
	@RequestMapping("createOrUpdateRoleAndFunc")
	@ResponseBody
	public Result createOrUpdateRoleAndFunc(String UUID,String ROLE_ID,String ROLE_CODE){
		Result result = new Result();
		//先删除已经保存的菜单id
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("role_id", ROLE_ID);
		roleService.deleteRoleAndFunc(paramMap);
		//重新保存菜单id
		String arrId= UUID;
		String[] array={};
		if(null!=arrId && !"".equals(arrId)){
			array=arrId.split(",");
			for(String id :array){
				Map<String, Object> paramMap2 = new HashMap<String, Object>();
				paramMap2.put("role_func_id", Util.getUUID());
				paramMap2.put("func_id", id);
				paramMap2.put("role_id", ROLE_ID);
				paramMap2.put("create_by", "");
				paramMap2.put("code", ROLE_CODE);
				paramMap2.put("create_date", new Date());
				roleService.insertRoleAndFunc(paramMap2);
			}
		}
		return result;
	}
	
	/**
	 * 分页查询角色项目
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryRoleProjectListByPage")
	@ResponseBody
	public Result queryRoleProjectListByPage(HttpServletRequest request) {
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		Result result = new Result();
		roleService.queryRoleProjectListByPage(page);
		result.setResult_data(page);
		return result;
	}
	
	/**
	 * 分页查询角色可以添加的项目
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryRoleAddProjectByPage")
	@ResponseBody
	public Result queryRoleAddProjectByPage(HttpServletRequest request) {
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		Result result = new Result();
		roleService.queryRoleAddProjectByPage(page);
		result.setResult_data(page);
		return result;
	}
	
	/**
	 * 添加角色项目
	 * @param roleId
	 * @param json
	 * @param request
	 * @return
	 */
	@RequestMapping("/addRoleProject")
	@ResponseBody
	public Result addRoleProject(String roleId, String create_date,String json,HttpServletRequest request) {
		List<Map<String,Object>> roleProjects = JsonUtil.fromJson(json, List.class);
		Result result = new Result();
		Map<String, Object> roleInfo = roleService.queryById(roleId);
		for (Map<String, Object> map : roleProjects) {
			Object projectId = map.get("BUSINESSID");
			map.put("id", Util.getUUID());
			map.put("businessId", projectId);
			map.put("roleId", roleId);
			map.put("createBy", ThreadLocalUtil.getUser().get("NAME"));
			map.put("create_date", create_date);
			map.put("projectType", map.get("PROJECT_TYPE"));
		}
		roleService.addRoleProject(roleProjects);
		return result;
	}
}

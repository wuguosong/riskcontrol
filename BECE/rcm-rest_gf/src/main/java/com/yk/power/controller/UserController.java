package com.yk.power.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.alibaba.fastjson.JSON;
import fnd.UserDto;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yk.power.service.IUserService;
import common.PageAssistant;
import common.Result;
import util.ThreadLocalUtil;

@Controller
@RequestMapping("/user")
public class UserController {
	@Resource
	private IUserService userService;
	
	/**
	 * 用户选择框，分页查询用户
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryUserForSelected")
	@ResponseBody
	public Result queryUserForSelected(HttpServletRequest request){
		Result result = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		this.userService.queryUserForSelected(page);
		result.setResult_data(page);
		return result;
	}
	
	@RequestMapping("/getAll")
	@ResponseBody
	public Result getAll(HttpServletRequest request){
		Result result = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		this.userService.getAll(page);
		result.setResult_data(page);
		return result;
	}
	
	@RequestMapping("/getDirectiveUserAll")
	@ResponseBody
	public Result getDirectiveUserAll(HttpServletRequest request){
		Result result = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		this.userService.getDirectiveUserAll(page);
		result.setResult_data(page);
		return result;
	}
	
	@RequestMapping("/getSysUserByID")
	@ResponseBody
	public Result getSysUserByID(HttpServletRequest request){
		Result result = new Result();
		String id = request.getParameter("id");
		Map<String, Object> map = this.userService.getSysUserByID(id);
		result.setResult_data(map);
		return result;
	}
	
	@RequestMapping("/getAUser")
	@ResponseBody
	public Result getAUser(HttpServletRequest request){
		Result result = new Result();
		String json = request.getParameter("json");
		try {
			Map<String,Object> map = this.userService.getAUser(json);
			result.setResult_data(map);
			/**
			 * Add By LiPan On 2019-01-12 Start
			 */
			// Session
			/*HttpSession session = request.getSession();
			UserDto userDto = JSON.parseObject(JSON.toJSONString(map), UserDto.class);
			Map<String,Object> mapInfo = userService.queryById(userDto.getUuid());
			userDto = JSON.parseObject(JSON.toJSONString(mapInfo), UserDto.class);
			session.setAttribute("userId", userDto.getUuid());
			session.setAttribute("userInfo", userDto);
			session.setAttribute("isAdmin", map.get("isAdmin"));*/
			// Thread
			ThreadLocalUtil.setIsAdmin((Boolean)map.get("isAdmin"));
			ThreadLocalUtil.setUser(map);
			ThreadLocalUtil.setUserId(map.get("UUID").toString());
			/**
			 * Add By LiPan On 2019-01-12 End
			 */
		} catch (Exception e) {
			result.setResult_name("未找到此用户");
			result.setSuccess(false);
		}
		return result;
	}
	@RequestMapping("/getAllRole")
	@ResponseBody
	public Result getAllRole(HttpServletRequest request){
		Result result = new Result();
		List<Map<String,Object>> roleList = this.userService.getAllRole();
		result.setResult_data(roleList);
		return result;
	}
	@RequestMapping("/getRoleByUserId")
	@ResponseBody
	public Result getRoleByUserId(HttpServletRequest request){
		Result result = new Result();
		String userId = request.getParameter("userId");
		List<Map<String,Object>> roleList = this.userService.getRoleByUserId(userId);
		result.setResult_data(roleList);
		return result;
	}
	@RequestMapping("/saveUserRole")
	@ResponseBody
	public Result saveUserRole(HttpServletRequest request,String [] roleArr3){
		Result result = new Result();
		String userId = request.getParameter("userId");
		String roleArr = request.getParameter("roleArr");
		this.userService.saveUserRole(userId,roleArr);
		return result;
	}
	
	/**
	 * 按角色获取人员列表
	 * @param request
	 * @return
	 */
	@RequestMapping("/getDirectiveRoleUserList")
	@ResponseBody
	public Result getDirectiveRoleUserList(HttpServletRequest request){
		Result result = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		this.userService.getDirectiveRoleUserList(page);
		System.out.println(page.getTotalItems());
		result.setResult_data(page);
		return result;
	}
}
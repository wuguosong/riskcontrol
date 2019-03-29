package com.yk.rcm.base.notification.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yk.rcm.base.notification.service.INotificationFlatformService;

import common.PageAssistant;
import common.Result;

/**
 * 
 * @author dsl <br/>
 *         平台公告
 *
 */

@Controller
@RequestMapping("/notificationFlatform")
public class NotificationFlatformController {

	@Resource
	private INotificationFlatformService notificationFlatformService;

	/**
	 * 获取公告平台列表
	 * 
	 * @param request
	 *            request
	 * @return {@link Result}
	 */
	@RequestMapping("/queryNotifications")
	@ResponseBody
	public Result queryNotificationList(HttpServletRequest request) {
		Result result = new Result();

		PageAssistant page = new PageAssistant(request.getParameter("page"));
		this.notificationFlatformService.queryNotificationByPage(page);
		result.setResult_data(page);

		return result;
	}

	/**
	 * 新增平台公告
	 * 
	 * @param json
	 *            String
	 * @param request
	 *            request
	 * @return {@link Result}
	 */
	@RequestMapping("/addNotification")
	@ResponseBody
	public Result addNotification(String json, HttpServletRequest request) {
		Result result = new Result();
		String id = this.notificationFlatformService.addNotification(json);
		result.setResult_data(id);

		return result;
	}

	/**
	 * 修改公告平台
	 * 
	 * @param json
	 *            String
	 * @param request
	 *            request
	 * @return {@link Result}
	 */
	@RequestMapping("/modifyNotification")
	@ResponseBody
	public Result modifyNotification(String json, HttpServletRequest request) {
		Result result = new Result();

		String id = this.notificationFlatformService.modifyNotification(json);
		result.setResult_data(id);

		return result;
	}

	/**
	 * 提交公告，提交之后公告不允许修改
	 * 
	 * @param id
	 *            String
	 * @param request
	 *            request
	 * @return {@link Result}
	 */
	@RequestMapping("/submitNotification")
	@ResponseBody
	public Result submitNotification(String id, HttpServletRequest request) {
		Result result = new Result();

		this.notificationFlatformService.submitNotification(id);

		return result;
	}

	/**
	 * 删除平台公告
	 * 
	 * @param ids
	 *            String
	 * @param request
	 *            request
	 * @return {@link Result}
	 */
	@RequestMapping("/deleteNotifications")
	@ResponseBody
	public Result deleteNotifications(String ids, HttpServletRequest request) {
		Result result = new Result();
		String[] idsArr = ids.split(",");
		this.notificationFlatformService.deleteNotification(idsArr);

		return result;
	}

	/**
	 * 查询公告详情信息(为公告修改页面提供数据)
	 * 
	 * @param id
	 *            String
	 * @param request
	 *            request
	 * @return {@link Result}
	 */
	@RequestMapping("/queryNotificationInfo")
	@ResponseBody
	public Result queryNotificationInfo(String id, HttpServletRequest request) {
		Result result = new Result();
		Map<String, Object> map = this.notificationFlatformService.queryNotificationInfo(id);
		result.setResult_data(map);

		return result;
	}

	/**
	 * 查询公告详情信息(查看详情页面)
	 * 
	 * @param id
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryNotificationInfoForView")
	@ResponseBody
	public Result queryNotificationInfoForView(String id, HttpServletRequest request) {
		Result result = new Result();
		Map<String, Object> map = this.notificationFlatformService.queryNotificationInfoForView(id);
		result.setResult_data(map);

		return result;
	}

	/**
	 * 获取个人待办-->更多--公告列表
	 * 
	 * @param request
	 *            request
	 * @return {@link Result}
	 */
	@RequestMapping("/queryNotifByPage")
	@ResponseBody
	public Result queryNotifByPage(HttpServletRequest request) {
		Result result = new Result();

		PageAssistant page = new PageAssistant(request.getParameter("page"));
		this.notificationFlatformService.queryNotifByPage(page);
		result.setResult_data(page);

		return result;
	}

	/**
	 * 为个人待办--平台公告板块提供信息
	 * 
	 * @param request
	 *            request
	 * @return {@link Result}
	 */
	@RequestMapping("/queryNotifTop")
	@ResponseBody
	public Result queryNotifTop(HttpServletRequest request) {
		Result result = new Result();
		List<Map<String, Object>> map = notificationFlatformService.queryNotifTop();
		result.setResult_data(map);
		return result;
	}
}

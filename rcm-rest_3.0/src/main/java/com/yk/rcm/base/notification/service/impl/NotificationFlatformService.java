package com.yk.rcm.base.notification.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import util.ThreadLocalUtil;
import util.Util;

import com.yk.rcm.base.notification.dao.INotificationFlatformMapper;
import com.yk.rcm.base.notification.service.INotificationFlatformService;
import common.PageAssistant;

/**
 * 
 * @author dsl<br/>
 *         平台公告
 *
 */

@Service
@Transactional
public class NotificationFlatformService implements INotificationFlatformService {

	@Resource
	private INotificationFlatformMapper notificationFlatformMapper;

	@Override
	public PageAssistant queryNotificationByPage(PageAssistant page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);

		if (page.getParamMap() != null) {
			params.putAll(page.getParamMap());
		}
		List<Map<String, Object>> list = this.notificationFlatformMapper.queryNotificationByPage(params);
		page.setList(list);

		return page;
	}

	@Override
	public String addNotification(String json) {

		String id = Util.getUUID();
		Map<String, Object> map = Util.parseJson2Map(json);
		map.put("ID", id);
		map.put("STATUS", "1");
		map.put("STAGE", "1");
		map.put("MODIFY_DATE", Util.now());
		if(map.get("EXPIRE_DATE") != null){
			map.put("EXPIRE_DATE", Util.parse((String) map.get("EXPIRE_DATE"), "yyyy-MM-dd"));
		}

		this.notificationFlatformMapper.addNotification(map);

		return id;
	}

	@Override
	public String modifyNotification(String json) {

		Map<String, Object> map = Util.parseJson2Map(json);
		map.put("MODIFIER", ThreadLocalUtil.getUserId());
		map.put("MODIFY_DATE", new Date());
		if(map.get("EXPIRE_DATE") != null){
			map.put("EXPIRE_DATE", Util.parse((String) map.get("EXPIRE_DATE"), "yyyy-MM-dd"));
		}
		this.notificationFlatformMapper.modifyNotification(map);

		return (String) map.get("ID");
	}

	@Override
	public void deleteNotification(String[] ids) {

		this.notificationFlatformMapper.deleteNotification(ids);

	}

	@Override
	public Map<String, Object> queryNotificationInfo(String id) {

		return this.notificationFlatformMapper.queryNotificationInfo(id);
	}
	
	@Override
	public Map<String, Object> queryNotificationInfoForView(String id) {
		
		return this.notificationFlatformMapper.queryNotificationInfoForView(id);
	}

	@Override
	public void submitNotification(String id) {

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ID", id);
		map.put("STAGE", "2");
		map.put("PUBDATE", new Date());
		map.put("PUBLISHER", ThreadLocalUtil.getUserId());
		this.notificationFlatformMapper.submitNotification(map);

	}
	
	@Override
	public PageAssistant queryNotifByPage(PageAssistant page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		if (page.getParamMap() != null) {
			params.putAll(page.getParamMap());
		}
		params.put("todayDate", new Date());
		List<Map<String, Object>> list = this.notificationFlatformMapper.queryNotifByPage(params);
		page.setList(list);

		return page;
	}

	@Override
	public List<Map<String, Object>> queryNotifTop() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("todayDate", new Date());
		return this.notificationFlatformMapper.queryNotifTop(params);
	}

}

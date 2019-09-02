package com.yk.rcm.meeting.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface IPigeonholeService {

	/**
	 * 获取所有可以归档的数据
	 * @return
	 */
	public List<Map<String, Object>> queryProject(Date pigeonholeTime);
	
	public List<Map<String, Object>> queryPfrContract(Date contractTime);
}

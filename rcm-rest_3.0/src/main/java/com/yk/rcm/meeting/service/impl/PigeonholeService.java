package com.yk.rcm.meeting.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import util.PropertiesUtil;

import com.yk.rcm.meeting.dao.IPigeonholeMapper;
import com.yk.rcm.meeting.service.IPigeonholeService;

@Service
@Transactional
public class PigeonholeService implements IPigeonholeService {

	@Resource
	private IPigeonholeMapper pigeonholeMapper;
	
	@Override
	public List<Map<String, Object>> queryProject(Date pigeonholeTime) {
		Map<String, Object> params = new HashMap<String, Object>(1);
		params.put("pigeonholeTime", pigeonholeTime);
		return pigeonholeMapper.queryProject(params);
	}

	@Override
	public List<Map<String, Object>> queryPfrContract(Date contractTime) {
		Map<String, Object> params = new HashMap<String, Object>(1);
		
		//归档时间大于今天的!防止出现重复更新
        Calendar calendar = Calendar.getInstance();  
	    calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
	    Date todayTime = calendar.getTime(); 
		params.put("todayTime", todayTime);
		params.put("contractTime", contractTime);
		return pigeonholeMapper.queryPfrContract(params);
	}
}

/**
 * 
 */
package com.yk.power.service.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yk.power.dao.IServiceTypeMapper;
import com.yk.power.service.IServiceTypeService;

/**
 * @author yaphet
 * 
 */
@Service
@Transactional
public class ServiceTypeService implements IServiceTypeService {
	@Resource
	private IServiceTypeMapper serviceTypeMapper;

	@Override
	public Map<String, Object> getRcmServiceTypeByTzServiceType(String tzServiceType) {
		HashMap<String, Object> data = new HashMap<String,Object>();
		data.put("tzServiceType", tzServiceType);
		return this.serviceTypeMapper.getRcmServiceTypeByTzServiceType(data);
	}
	
	@Override
	public Map<String, Object> getDictServiceTypeByCode(String code) {
		HashMap<String, Object> data = new HashMap<String,Object>();
		data.put("code", code);
		return this.serviceTypeMapper.getDictServiceTypeByCode(data);
	}
}

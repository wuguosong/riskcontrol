package com.yk.power.service;

import java.util.List;
import java.util.Map;

import common.PageAssistant;
import common.Result;


/**
 * 业务类型银蛇
 * @author wufucan
 *
 */
public interface IServiceTypeService {
	
	public Map<String,Object> getRcmServiceTypeByTzServiceType(String tzServiceType);

	Map<String, Object> getDictServiceTypeByCode(String code);
	
}

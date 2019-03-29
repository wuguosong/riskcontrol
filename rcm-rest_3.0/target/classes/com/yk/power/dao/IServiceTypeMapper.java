package com.yk.power.dao;

import java.util.HashMap;
import java.util.Map;

import com.yk.common.BaseMapper;

public interface IServiceTypeMapper extends BaseMapper {

	Map<String, Object> getRcmServiceTypeByTzServiceType(HashMap<String, Object> data);

	Map<String, Object> getDictServiceTypeByCode(HashMap<String, Object> data);

}

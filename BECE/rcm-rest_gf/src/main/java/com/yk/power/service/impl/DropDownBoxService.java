package com.yk.power.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yk.power.dao.IDropDownBoxMapper;
import com.yk.power.service.IDropDownBoxService;

/**
 * @author yaphet
 *
 */
@Service
@Transactional
public class DropDownBoxService implements IDropDownBoxService{

	@Resource
	private IDropDownBoxMapper dropDownBoxMapper;
	
	@Override
	public Map<String, Object> queryByCode(String code) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> list =  this.dropDownBoxMapper.queryByCode(code);
		map.put("typeList", list);
		return map ; 
	}
	
	

	
	
}

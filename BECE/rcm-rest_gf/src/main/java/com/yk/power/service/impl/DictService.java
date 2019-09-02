/**
 * 
 */
package com.yk.power.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import util.Util;

import com.yk.power.dao.IDictMapper;
import com.yk.power.service.IDictService;

import common.PageAssistant;
import common.Result;

/**
 * @author wufucan
 * 
 */
@Service
@Transactional
public class DictService implements IDictService {
	@Resource
	private IDictMapper dictMapper;

	@Override
	public Result addDictType(Map<String, Object> dictType) {
		Result result = new Result();
		String pk = dictMapper.selectPkDictType(dictType);
		if(StringUtils.isEmpty(pk) || "0".equals(pk))
		{
			dictType.put("uuid", Util.getUUID());
			dictMapper.addDictType(dictType);
	    }else{
	    	result.setSuccess(false).setResult_name("编码不能重复!");
	    }
		return result;
	}
	
	@Override
	public Result updateDictType(Map<String, Object> dictType) {
		Result result = new Result();
		String pk = dictMapper.selectPkDictType(dictType);
		if(StringUtils.isEmpty(pk) || "0".equals(pk))
		{
			dictMapper.updateDictType(dictType);
	    }else{
	    	result.setSuccess(false).setResult_name("编码不能重复!");
	    }
		return result;
	}

	@Override
	public Result addDictItem(Map<String, Object> dictItem) {
		Result result = new Result();
		String pk = dictMapper.selectPkDictItem(dictItem);
		if(StringUtils.isEmpty(pk) || "0".equals(pk))
		{
			dictItem.put("uuid", Util.getUUID());
			dictMapper.addDictItem(dictItem);
	    }else{
	    	result.setSuccess(false).setResult_name("编码不能重复!");
	    }
		return result;
	}
	
	@Override
	public Result updateDictItem(Map<String, Object> dictItem) {
		Result result = new Result();
		String pk = dictMapper.selectPkDictItem(dictItem);
		if(StringUtils.isEmpty(pk) || "0".equals(pk))
		{
			dictMapper.updateDictItem(dictItem);
	    }else{
	    	result.setSuccess(false).setResult_name("编码不能重复!");
	    }
		return result;
	}


	@Override
	public void queryDictTypeByPage(PageAssistant page) {
		Map<String, Object> params = new HashMap<String, Object>();
		if (page.getParamMap() != null) {
			params.putAll(page.getParamMap());
		}
		params.put("page", page);
		List<Map<String, Object>> list = dictMapper.queryDictTypeByPage(params);
		page.setList(list);
	}

	@Override
	public void queryDictItemByDictTypeAndPage(PageAssistant page) {
		Map<String, Object> params = new HashMap<String, Object>();
		if (page.getParamMap() != null) {
			params.putAll(page.getParamMap());
		}
		params.put("page", page);
		List<Map<String, Object>> list = dictMapper.queryDictItemByDictTypeAndPage(params);
		page.setList(list);
	}

	@Override
	public Map<String, Object> getDictTypeById(String uuid) {
		return dictMapper.getDictTypeById(uuid);
	}
	
	@Override
	public Map<String, Object> getDictItemById(String uuid) {
		return dictMapper.getDictItemById(uuid);
	}


	@Override
	public void deleteDictTypeByIds(String[] uuidArray) {
		dictMapper.deleteDictTypeByIds(uuidArray);
	}

	@Override
	public void deleteDictItemByIds(String[] uuidArray) {
		dictMapper.deleteDictItemByIds(uuidArray);
	}

	@Override
	public String getDictItemLastIndexByDictType(String FK_UUID) {
		return dictMapper.getDictItemLastIndexByDictType(FK_UUID);
	}

	@Override
	public List<Map<String, Object>> queryDictItemByDictTypeCode(String code) {
		return dictMapper.queryDictItemByDictTypeCode(code);
	}

	@Override
	public Map<String, Object> getDictItemByItemCode(String itemCode) {
		return dictMapper.getDictItemByItemCode(itemCode);
	}

	@Override
	public Map<String, Object> getDictByCode(String code) {
		return dictMapper.getDictByCode(code);
	}
	
	@Override
	public void deleteDictItemById(String uuid) {
		dictMapper.deleteDictItemById(uuid);
	}

	@Override
	public String getDictItemLastIndexByDictCode(String code) {
		return dictMapper.getDictItemLastIndexByDictCode(code);
	}

	@Override
	public void addDictItemMeetLeader(Map<String, Object> dictItem) {
		dictMapper.addDictItemMeetLeader(dictItem);
	}

	@Override
	public void updateDictItemMeetLeader(Map<String, Object> dictItem) {
		dictMapper.updateDictItemMeetLeader(dictItem);
	}
	
	@Override
	public Map<String, Object> queryItemByFuCoZiName(String fuCode,
			String ziCode) {
		return dictMapper.queryItemByFuCoZiName(fuCode,ziCode);
	}
}

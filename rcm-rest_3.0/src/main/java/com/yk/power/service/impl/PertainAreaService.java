/**
 * 
 */
package com.yk.power.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.bson.Document;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import util.ThreadLocalUtil;
import util.Util;

import com.yk.power.dao.IOrgMapper;
import com.yk.power.dao.IPertainAreaMapper;
import com.yk.power.service.IOrgService;
import com.yk.power.service.IPertainAreaService;

import common.PageAssistant;
import common.Result;

/**
 * @author yaphet
 */
@Service
@Transactional
public class PertainAreaService implements IPertainAreaService {
	
	@Resource
	private IPertainAreaMapper pertainAreaMapper;
	@Resource
	private IOrgService orgService;
	@Override
	public List<Map<String, Object>> queryPertainAreaByOrgPkValue( String queryByPkvalue) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("orgPkValue", queryByPkvalue);
		return this.pertainAreaMapper.queryPertainAreaByOrgPkValue(map);
	}

	@Override
	public void updatePersonByPertainAreaId(String pid, String newUserId,String type) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("pid", pid);
		map.put("newUserId", newUserId);
		map.put("type", type);
		this.pertainAreaMapper.updatePersonByPertainAreaId(map);
	}
	@Override
	public void updateById(String id, String newUserId) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("id", id);
		map.put("newUserId", newUserId);
		this.pertainAreaMapper.updatePersonById(map);
	}
	@Override
	public Result updateByUserId(String json) {
		Result result = new Result();
		
		Document doc = Document.parse(json);
		HashMap<String, Object> hashMap = new HashMap<String,Object>();
		hashMap.put("userId", doc.getString("oldUserId"));
		this.pertainAreaMapper.deleteByUserId(hashMap);
		if("1".equals(doc.getString("type"))){
			String serviceType = doc.getString("serviceType");
			String[] idsArr = serviceType.split(",");
			for (String string : idsArr) {
				Map<String, Object> map = new HashMap<String,Object>();
				map.put("orgId", doc.getString("orgId"));
				map.put("userId", doc.getString("userId"));
				map.put("type", doc.getString("type"));
				map.put("serviceType", string);
				this.pertainAreaMapper.save(map);
			}
		}else{
			this.pertainAreaMapper.save(doc);
		}
		
		return result;
	}

	@Override
	public List<Map<String, Object>> queryList(PageAssistant page) {
		
		Map<String, Object> paramMap = page.getParamMap();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		if(Util.isNotEmpty(paramMap)){
			params.putAll(paramMap);
		}
		
		return this.pertainAreaMapper.queryList(params);
	}

	@Override
	public Map<String, Object> getByUserId(String userId) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("userId", userId);
		return this.pertainAreaMapper.getByUserId(map);
	}

	@Override
	public Result deleteByUserId(String userId) {
		Result result = new Result();
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("userId", userId);
		this.pertainAreaMapper.deleteByUserId(map);
		return result;
	}

	public List<Map<String, Object>> queryAll() {
		
		return this.pertainAreaMapper.queryAll();
	}

	@Override
	public Result save(String json) {
		Document doc = Document.parse(json);
		Result result = new Result();
		
		this.pertainAreaMapper.deleteByUserId(doc);
		if("1".equals(doc.getString("type"))){
			String serviceType = doc.getString("serviceType");
			String[] idsArr = serviceType.split(",");
			for (String string : idsArr) {
				Map<String, Object> map = new HashMap<String,Object>();
				map.put("orgId", doc.getString("orgId"));
				map.put("userId", doc.getString("userId"));
				map.put("type", doc.getString("type"));
				map.put("serviceType", string);
				this.pertainAreaMapper.save(map);
			}
		}else{
			this.pertainAreaMapper.save(doc);
		}
		
		return result;
	}
}

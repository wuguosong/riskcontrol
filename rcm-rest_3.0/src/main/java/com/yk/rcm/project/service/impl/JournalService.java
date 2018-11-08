/**
 * 
 */
package com.yk.rcm.project.service.impl;

import java.sql.Clob;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import util.Util;

import com.yk.rcm.project.dao.IJournalMapper;
import com.yk.rcm.project.service.IJournalService;

import common.PageAssistant;

/**
 * @author 80845530
 *
 */
@Service
@Transactional(propagation=Propagation.REQUIRES_NEW)
public class JournalService implements IJournalService {
	private static Logger logger = LoggerFactory.getLogger(JournalService.class);
	@Resource
	private IJournalMapper journalMapper;
	
	/* (non-Javadoc)
	 * @see com.yk.rcm.service.IJournalService#save(java.util.Map)
	 */
	@Override
	public void save(Map<String, Object> data) {
		data.put("id", Util.getUUID());
		this.journalMapper.save(data);
	}

	@Override
	public void queryByPage(PageAssistant page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		if(page.getParamMap() != null){
			params.putAll(page.getParamMap());
		}
		List<Map<String, Object>> list = this.journalMapper.queryByPage(params);
		for(int i = 0; i < list.size(); i++){
			Map<String, Object> one = list.get(i); 
			Clob content = (Clob) one.get("CONTENT");
			String contentStr = null;
			try {
				contentStr = content.getSubString(1, new Long(content.length()).intValue());
			} catch (SQLException e) {
				logger.error(Util.parseException(e));
			}
			
			one.put("CONTENT", contentStr);
		}
		page.setList(list);
	}

	@Override
	public Map<String, Object> queryById(String id) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		Map<String, Object> queryById = this.journalMapper.queryById(params);
		Clob content = (Clob) queryById.get("CONTENT");
		String contentStr = null;
		try {
			contentStr = content.getSubString(1, new Long(content.length()).intValue());
		} catch (SQLException e) {
			logger.error(Util.parseException(e));
		}
		
		queryById.put("CONTENT", contentStr);
		
		
		return queryById;
	}

}

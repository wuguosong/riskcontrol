/**
 * 
 */
package com.yk.rcm.project.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yk.rcm.project.dao.IFirstlevelLawyerMapper;
import com.yk.rcm.project.service.IFirstlevelLawyerService;

/**
 * @author 80845530
 *
 */
@Service
@Transactional
public class FirstlevelLawyerService implements IFirstlevelLawyerService {
	@Resource
	private IFirstlevelLawyerMapper firstlevelLawyerMapper;
	
	/* (non-Javadoc)
	 * @see com.yk.rcm.service.IFirstlevelLawyerService#queryByServiceTypes(java.util.List)
	 */
	@Override
	public List<Map<String, Object>> queryByServiceTypes(
			List<String> serviceTypes) {
		Map<String, Object> query = new HashMap<String, Object>();
		query.put("serviceTypes", serviceTypes);
		return this.firstlevelLawyerMapper.queryByServiceTypes(query);
	}

	/* (non-Javadoc)
	 * @see com.yk.rcm.service.IFirstlevelLawyerService#updateById(java.util.Map)
	 */
	@Override
	public void updateById(Map<String, Object> data) {
		this.firstlevelLawyerMapper.updateById(data);
	}

}

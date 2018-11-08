/**
 * 
 */
package com.yk.rcm.project.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yk.rcm.project.dao.IProjectRelationMapper;
import com.yk.rcm.project.service.IProjectRelationService;

/**
 * @author 80845530
 *
 */
@Service
@Transactional
public class ProjectRelationService implements IProjectRelationService {
	@Resource
	private IProjectRelationMapper projectRelationMapper;
	
	/* (non-Javadoc)
	 * @see com.yk.rcm.service.IProjectRelationService#deleteByBusinessId(java.lang.String)
	 */
	@Override
	public void deleteByBusinessId(String businessId) {
		this.projectRelationMapper.deleteByBusinessId(businessId);
	}

}

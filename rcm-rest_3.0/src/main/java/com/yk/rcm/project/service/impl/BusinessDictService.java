/**
 * 
 */
package com.yk.rcm.project.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yk.rcm.project.dao.IBusinessDictMapper;
import com.yk.rcm.project.service.IBusinessDictService;
import common.Constants;

/**
 * @author wufucan
 *
 */
@Service
@Transactional(readOnly=true)
public class BusinessDictService implements IBusinessDictService {
	@Resource
	private IBusinessDictMapper businessDictMapper;

	/* (non-Javadoc)
	 * @see com.yk.rcm.project.service.IDictService#queryBusinessType()
	 */
	@Override
	public List<Map<String, Object>> queryBusinessType() {
		return this.businessDictMapper.queryByParentCode(Constants.DICT_CODE_BUSINESSTYPE);
	}

}

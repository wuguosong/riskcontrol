package com.yk.ext.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.daxt.service.IDaxtService;
import com.yk.ext.InitWithJson;
import common.Result;

/**
 * 档案系统 
 * @author hubiao
 *
 */
@Service("daxtPrfInit")
@Transactional
public class DaxtPrfInit implements InitWithJson {

	@Resource
	private IDaxtService daxtService;
	
	@Override
	public Result execute(String businessId) {
		daxtService.prfStart(businessId);
		return new Result();
	}
}

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
@Service("daxtPreInit")
@Transactional
public class DaxtPreInit implements InitWithJson {

	@Resource
	private IDaxtService daxtService;
	
	@Override
	public Result execute(String businessId) {
		daxtService.preStart(businessId);
		return new Result();
	}
}

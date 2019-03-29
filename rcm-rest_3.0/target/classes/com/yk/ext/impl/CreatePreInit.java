package com.yk.ext.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ws.service.WebServiceForTZ;

import com.yk.ext.InitWithJson;

import common.Result;

/**
 * 新建预评审接口测试
 * @author hubiao
 */
@Service("createPreInit")
@Transactional
public class CreatePreInit implements InitWithJson {

	@Override
	public Result execute(String json) {
		WebServiceForTZ tz = new WebServiceForTZ();
		String createPre = tz.createPre(json);
		Result result = new Result();
		result.setResult_data(createPre);
		result.setResult_name(createPre);
		return result;
	}
}

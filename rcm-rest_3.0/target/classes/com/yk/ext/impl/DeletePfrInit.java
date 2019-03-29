package com.yk.ext.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ws.service.WebServiceForTZ;

import com.yk.ext.InitWithJson;

import common.Result;

/**
 * 新建正式评审接口测试
 * @author hubiao
 */
@Service("deletePfrInit")
@Transactional
public class DeletePfrInit implements InitWithJson {

	@Override
	public Result execute(String json) {
		WebServiceForTZ tz = new WebServiceForTZ();
		String createPfr = tz.deletePfr(json);
		Result result = new Result();
		result.setResult_data(createPfr);
		result.setResult_name(createPfr);
		return result;
	}
}

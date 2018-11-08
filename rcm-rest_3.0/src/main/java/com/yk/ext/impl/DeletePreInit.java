package com.yk.ext.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ws.service.WebServiceForTZ;

import com.yk.ext.InitWithJson;

import common.Result;

/**
 * 删除预评审接口测试
 * @author hubiao
 */
@Service("deletePreInit")
@Transactional
public class DeletePreInit implements InitWithJson {

	@Override
	public Result execute(String json) {
		WebServiceForTZ tz = new WebServiceForTZ();
		String deletePre = tz.deletePre(json);
		Result result = new Result();
		result.setResult_data(deletePre);
		result.setResult_name(deletePre);
		return result;
	}
}

package com.yk.rcm.project.filter;

import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.Util;

import com.yk.ext.filter.IProjectTzFilter;
import com.yk.ext.filter.ProjectTzFilterChain;
import com.yk.flow.util.JsonUtil;
import com.yk.rcm.project.service.IWsCallService;

import common.Result;

/**
 * 接口日志记录
 * @author hubiao
 */
public class PfrTzLogFilter implements IProjectTzFilter {
	private static final Logger log = LoggerFactory.getLogger(PfrTzLogFilter.class);
	@Resource
	private IWsCallService wsCallService;
	
	@Override
	public void doFilter(Map<String, Object> data, Result result,
			ProjectTzFilterChain chain) {
		String content = JsonUtil.toJson(data);
		String resultText = null;
		try {
			chain.doFilter(data, result, chain);
			resultText = result.getResult_data().toString();
			wsCallService.saveWsServer("TZPFR", content, resultText, result.isSuccess());
		} catch (Exception e) {
			result.setSuccess(false);
			resultText = Util.parseException(e).replace(" ", "");
			wsCallService.saveWsServer("TZPFR", content, resultText, result.isSuccess());
			throw new RuntimeException(e);
		}
	}
}

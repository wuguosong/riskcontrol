package com.yk.rcm.project.filter;

import java.util.Map;

import javax.annotation.Resource;

import org.bson.Document;

import com.yk.ext.filter.IProjectTzFilter;
import com.yk.ext.filter.ProjectTzFilterChain;
import com.yk.rcm.pre.service.IPreInfoService;
import common.Result;

/**
 * 执行保存或更新动作
 * @author wufucan
 *
 */
public class PreTzSaveOrUpdateFilter implements IProjectTzFilter {
	@Resource
	private IPreInfoService preInfoService;
	
	@Override
	public void doFilter(Map<String, Object> data, Result result,
			ProjectTzFilterChain chain) {
		Document doc = (Document)data;
		preInfoService.saveOrUpdateForTz(doc,result);
		return;
	}
}

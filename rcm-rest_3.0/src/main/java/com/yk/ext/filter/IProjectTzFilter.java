package com.yk.ext.filter;

import java.util.Map;

import common.Result;

/**
 * 投资系统推送评审项目的过滤器
 * @author wufucan
 *
 */
public interface IProjectTzFilter {

	public void doFilter(Map<String, Object> data, Result result, ProjectTzFilterChain chain);
}

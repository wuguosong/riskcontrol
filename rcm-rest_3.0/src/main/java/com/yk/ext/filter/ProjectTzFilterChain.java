/**
 * 
 */
package com.yk.ext.filter;

import java.util.List;
import java.util.Map;

import common.Result;

/**
 * 投资系统推送评审项目的责任链
 * @author wufucan
 *
 */
public class ProjectTzFilterChain implements IProjectTzFilter {

	private List<IProjectTzFilter> filters;
	private ThreadLocal<Integer> index = new ThreadLocal<Integer>();
	
	/*
	 * (non-Javadoc)
	 * @see com.yk.ext.filter.IProjectTzFilter#doFilter(java.util.Map, com.yk.ext.filter.ProjectTzFilterChain)
	 */
	@Override
	public void doFilter(Map<String, Object> data, Result result, ProjectTzFilterChain chain) {
		if(index.get() == null){
			index.set(0);
		}
		if(filters == null || filters.size() <= index.get()){
			return;
		}
		IProjectTzFilter filter = filters.get(index.get());
		index.set(index.get()+1);
		filter.doFilter(data, result, chain);
	}
	@Override
	public String toString() {
		return "ProjectTzFilterChain [filters=" + filters + "]";
	}
	public List<IProjectTzFilter> getFilters() {
		return filters;
	}
	public void setFilters(List<IProjectTzFilter> filters) {
		this.filters = filters;
	}
	
	/**
	 * 执行之前请调用此方法，不然  当某个链抛出异常，不再重新执行
	 */
	public void reset()
	{
		index.set(0);
	}
}

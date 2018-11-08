package com.yk.rcm.base.user.service;

import java.util.Map;

import common.PageAssistant;

public interface IRepoUnitUserService {

	/**
	 * 分页查询单位负责人
	 * @param page
	 * @return
	 */
	public PageAssistant queryByPage(PageAssistant page);

	/**
	 * 创建单位负责人
	 * @param map
	 */
	public void create(Map<String, Object> map);

	/**
	 * 根据ID 更新单位负责人
	 * @param map
	 */
	public void update(Map<String, Object> map);

	/**
	 * 根据ID 删除单位负责人
	 * @param map
	 */
	public void deleteById(String id);

	/**
	 * 根据ID 查询单位负责人信息
	 * @param map
	 */
	public Map<String, Object> queryById(String id);
	
	/**
	 * 根据 单位负责人ID查询信息
	 * @param map
	 */
	public Map<String, Object> queryByRepoUnitId(String id);
}

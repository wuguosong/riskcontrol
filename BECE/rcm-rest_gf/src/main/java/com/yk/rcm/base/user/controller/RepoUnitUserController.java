package com.yk.rcm.base.user.controller;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import util.Util;

import com.yk.rcm.base.user.service.IRepoUnitUserService;

import common.PageAssistant;
import common.Result;

/**
 * 单位、基层法务负责人管理
 * @author hubiao
 *
 */
@Controller
@RequestMapping("/repoUnitUser")
public class RepoUnitUserController {

	@Resource
	private IRepoUnitUserService repoUnitUserService;
	
	/**
	 * 分页获取列表
	 */
	@RequestMapping("/queryByPage")
	@ResponseBody
	public Result queryByPage(String page) {
		Result result = new Result();

		PageAssistant pageAssistant = new PageAssistant(page);
		repoUnitUserService.queryByPage(pageAssistant);
		result.setResult_data(pageAssistant);

		return result;
	}
	
	/**
	 * 新增
	 */
	@RequestMapping("/create")
	@ResponseBody
	public Result create(String json, HttpServletRequest request) {
		Result result = new Result();
		Map<String, Object> map = Util.parseJson2Map(json);
		String id = Util.getUUID();
		map.put("ID", id);
		String repoUnitId = map.get("REPORTINGUNIT_ID").toString();
		Map<String, Object> repoUnitMap = repoUnitUserService.queryByRepoUnitId(repoUnitId);
		if(Util.isNotEmpty(repoUnitMap)){
			return result.setSuccess(false).setResult_name("单位不能重复添加!");
		}
		repoUnitUserService.create(map);
		result.setResult_data(id);
		return result;
	}

	/**
	 * 更新
	 */
	@RequestMapping("/update")
	@ResponseBody
	public Result update(String json, HttpServletRequest request) {
		Result result = new Result();
		Map<String, Object> map = Util.parseJson2Map(json);
		String id = map.get("ID").toString();
		String repoUnitId = map.get("REPORTINGUNIT_ID").toString();
		
		Map<String, Object> repoUnitMap = repoUnitUserService.queryByRepoUnitId(repoUnitId);
		if(Util.isNotEmpty(repoUnitMap) && !id.equals(repoUnitMap.get("ID"))){
			return result.setSuccess(false).setResult_name("单位不能重复添加!");
		}
		
		repoUnitUserService.update(map);
		result.setResult_data(map.get("ID"));
		return result;
	}
	
	/**
	 * 删除
	 */
	@RequestMapping("/deleteById")
	@ResponseBody
	public Result deleteById(String id, HttpServletRequest request) {
		Result result = new Result();
		repoUnitUserService.deleteById(id);
		return result;
	}
	
	/**
	 * 根据ID获取信息
	 */
	@RequestMapping("/queryById")
	@ResponseBody
	public Result queryById(String id, HttpServletRequest request) {
		Result result = new Result();
		Map<String, Object> map =repoUnitUserService.queryById(id);
		result.setResult_data(map);
		return result;
	}
}

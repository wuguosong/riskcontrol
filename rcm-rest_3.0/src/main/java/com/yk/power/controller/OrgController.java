package com.yk.power.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import util.Util;

import com.yk.power.service.IOrgService;

import common.PageAssistant;
import common.Result;

/**
 * 组织机构
 * @author wufucan
 *
 */
@Controller
@RequestMapping("/org")
public class OrgController {
	@Resource
	private IOrgService orgService;
	
	/**
	 * 查询组织部门
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryByPkvalue")
	@ResponseBody
	public Result queryByPkvalue(HttpServletRequest request){
		Result result = new Result();
		String orgPKValue = request.getParameter("orgPKValue");
		Map<String, Object> queryByPkvalue = this.orgService.queryByPkvalue(orgPKValue);
		return result.setResult_data(queryByPkvalue);
	}
	/**
	 * 查询单位和单位负责人表信息，不分页
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryReportOrg")
	@ResponseBody
	public Result queryReportOrg(HttpServletRequest request){
		Result result = new Result();
		Map<String, Object> params = new HashMap<String, Object>();
		String orgname = request.getParameter("ORGNAME");
		params.put("orgname", orgname);
		List<Map<String, Object>> list = this.orgService.queryReportOrg(params);
		result.setResult_data(list);
		return result;
	}
	
	@RequestMapping("/queryAllOrg")
	@ResponseBody
	public Result queryAllOrg(HttpServletRequest request){
		Result result = new Result();
		Map<String, Object> params = new HashMap<String, Object>();
		String orgname = request.getParameter("name");
		PageAssistant page =new PageAssistant( (String)request.getParameter("page"));
		List<Map<String, Object>> list = this.orgService.queryAllOrg(page);
		result.setResult_data(page);
		return result;
	}
	
	/**
	 * 根据parentId为空则查询顶级org,反之查询父节点为parentId的org
	 * @param params
	 * @return
	 */
	@RequestMapping("/queryCommonOrg")
	@ResponseBody
	public Result queryCommonOrg(String parentId){
		Result result = new Result();
		
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("parentId", parentId);
		List<Map<String, Object>> queryCommonOrg = orgService.queryCommonOrg(param);
		
		List<Map> retList = new ArrayList<Map>();
	    if(Util.isNotEmpty(queryCommonOrg)){
	    	for(Map<String, Object> map : queryCommonOrg){
	    		String id = (String)map.get("ID");
	    		String pid = (String)map.get("PID");
	    		String name = (String)map.get("NAME");
	    		Boolean isParent = Boolean.valueOf((String)map.get("ISPARENT"));
	    		Map<String, Object> retMap = new HashMap<String, Object>();
	    		retMap.put("id", id);
	    		retMap.put("pid", pid);
	    		retMap.put("name", name);
	    		retMap.put("isParent", isParent);
	    		retMap.put("cat", map.get("CATEGORYCODE"));
	    		retList.add(retMap);
	    	}
	    }
		
	    result.setResult_data(retList);
		return result;
	}
	
}

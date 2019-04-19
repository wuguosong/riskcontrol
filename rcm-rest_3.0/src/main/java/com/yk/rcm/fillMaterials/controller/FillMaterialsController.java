/**
 * 
 */
package com.yk.rcm.fillMaterials.controller;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;
import com.yk.log.annotation.SysLog;
import com.yk.log.constant.LogConstant;
import com.yk.rcm.fillMaterials.service.IFillMaterialsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import common.PageAssistant;
import common.Result;
import util.ThreadLocalUtil;

/**
 * @author gaohe
 * 资料填写查询controller
 */
@Controller
@RequestMapping("/fillMaterials")
public class FillMaterialsController {
	@Resource
	private IFillMaterialsService fillMaterialsService;
	
	/**
	 *  查询所有的资料填写项目列表
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryAllList")
	@ResponseBody
	// @SysLog(module = LogConstant.MODULE_FILL_MATERIALS, operation = LogConstant.QUERY, description = "查询所有的资料填写项目列表")
	public Result queryAllList(){
		Result result = new Result();
		Map<String,Object> data = new HashMap<String,Object>();
		//获取当前登录用户
		String userId = ThreadLocalUtil.getUserId();
		//获取未提交前5条数据
		PageAssistant page = new PageAssistant();
		page.getParamMap().put("userId", userId);
		page.setPageSize(10);
		page.setTotalItems(0);
		page = fillMaterialsService.queryNoSubmitList(page);
		data.put("noSubmitList", page.getList());
		result.setResult_data(page);
		//获取已提交前5条数据
		page.setList(null);
		page.setTotalItems(0);
		page = fillMaterialsService.querySubmitList(page);
		data.put("submitList", page.getList());
		result.setResult_data(data);
		return result;
	}

	/**
	 *  查询未提交的资料填写项目列表
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryNoSubmitList")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_FILL_MATERIALS, operation = LogConstant.QUERY, description = "查询未提交的资料填写项目列表")
	public Result queryInformationList(String page){
		Result result = new Result();
		PageAssistant pageAssistant = new PageAssistant(page);
		Map<String, Object> paramMap = pageAssistant.getParamMap();
		if(null == paramMap){
			pageAssistant.setParamMap(new HashMap<String, Object>());
		}
		pageAssistant.getParamMap().put("userId", ThreadLocalUtil.getUserId());
		fillMaterialsService.querySubmitList(pageAssistant);
		result.setResult_data(pageAssistant);
		return result;
	}
	/**
	 * 查询会议信息列表(已处理)
	 * @param request
	 * @return
	 */
	@RequestMapping("/querySubmitList")
	@ResponseBody
	@SysLog(module = LogConstant.MODULE_FORMAL_ASSESSMENT, operation = LogConstant.QUERY, description = "查询已提交的资料填写项目列表")
	public Result queryInformationListed(String page){
		Result result = new Result();
		PageAssistant pageAssistant = new PageAssistant(page);
		Map<String, Object> paramMap = pageAssistant.getParamMap();
		if(null == paramMap){
			pageAssistant.setParamMap(new HashMap<String, Object>());
		}
		pageAssistant.getParamMap().put("userId", ThreadLocalUtil.getUserId());
		fillMaterialsService.querySubmitList(pageAssistant);
		result.setResult_data(pageAssistant);
		return result;
	}

}

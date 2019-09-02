/**
 * 
 */
package com.yk.rcm.fillMaterials.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.yk.log.annotation.SysLog;
import com.yk.log.constant.LogConstant;
import com.yk.rcm.fillMaterials.service.IFillMaterialsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import common.PageAssistant;
import common.Result;

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
	 *  查询未提交的资料填写项目列表
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryNoSubmitList")
	@ResponseBody
	// @SysLog(module = LogConstant.MODULE_FILL_MATERIALS, operation = LogConstant.QUERY, description = "查询所有的资料填写项目列表")
	public Result queryNoSubmitList(HttpServletRequest request){
		Result result = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		String json = request.getParameter("json");
		this.fillMaterialsService.queryNoSubmitList(page, json);
		page.setParamMap(null);
		result.setResult_data(page);
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
	public Result querySubmitList(HttpServletRequest request){
		Result result = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		String json = request.getParameter("json");
		this.fillMaterialsService.querySubmitList(page, json);
		page.setParamMap(null);
		result.setResult_data(page);
		return result;
	}

}

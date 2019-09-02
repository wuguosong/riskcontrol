package com.yk.rcm.templateFile.controller;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yk.rcm.templateFile.service.ITemplateFileformService;

import common.PageAssistant;
import common.Result;

/**
 * 
 * @author lyc
 *         模板文件
 *
 */

@Controller
@RequestMapping("/templateFileFrom")
public class TemplateFileformController {

	@Resource
	private ITemplateFileformService templateFileformService;

	/**
	 * 获取模板文件列表
	 * 
	 * @param request
	 *            request
	 * @return {@link Result}
	 */
	@RequestMapping("/queryTemplateFiles")
	@ResponseBody
	public Result queryTemplateFiles(HttpServletRequest request) {
		Result result = new Result();

		PageAssistant page = new PageAssistant(request.getParameter("page"));
		this.templateFileformService.queryTemplateFilesByPage(page);
		result.setResult_data(page);

		return result;
	}
	
	/**
	 * 获取已提交模板文件列表
	 * 
	 * @param request
	 *            request
	 * @return {@link Result}
	 */
	@RequestMapping("/queryRiskGuidelinesForSubmit")
	@ResponseBody
	public Result queryRiskGuidelinesForSubmit(HttpServletRequest request) {
		Result result = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		this.templateFileformService.queryRiskGuidelinesForSubmit(page);
		result.setResult_data(page);

		return result;
	}
	/**
	 * 新增模板文件
	 * 
	 * @param json
	 *            String
	 * @param request
	 *            request
	 * @return {@link Result}
	 */
	@RequestMapping("/addTemplateFile")
	@ResponseBody
	public Result addTemplateFile(String json, HttpServletRequest request) {
		Result result = new Result();
		String id = this.templateFileformService.addTemplateFile(json);
		result.setResult_data(id);

		return result;
	}

	/**
	 * 修改模板文件
	 * 
	 * @param json
	 *            String
	 * @param request
	 *            request
	 * @return {@link Result}
	 */
	@RequestMapping("/modifyTemplateFile")
	@ResponseBody
	public Result modifyTemplateFile(String json, HttpServletRequest request) {
		Result result = new Result();

		String id = this.templateFileformService.modifyTemplateFile(json);
		result.setResult_data(id);

		return result;
	}

	/**
	 * 提交模板文件，提交之后不允许修改
	 * 
	 * @param id
	 *            String
	 * @param request
	 *            request
	 * @return {@link Result}
	 */
	@RequestMapping("/submitTemalateFile")
	@ResponseBody
	public Result submitTemalateFile(String id, HttpServletRequest request) {
		Result result = new Result();
		this.templateFileformService.submitTemalateFile(id);
		return result;
	}

	/**
	 * 删除模板文件
	 * 
	 * @param ids
	 *            String
	 * @param request
	 *            request
	 * @return {@link Result}
	 */
	@RequestMapping("/deleteTemplateFile")
	@ResponseBody
	public Result deleteTemplateFile(String ids, HttpServletRequest request) {
		Result result = new Result();
		String[] idsArr = ids.split(",");
		this.templateFileformService.deleteTemplateFile(idsArr);

		return result;
	}

	/**
	 * 查询模板文件(为模板文件修改页面提供数据)
	 * 
	 * @param id
	 *            String
	 * @param request
	 *            request
	 * @return {@link Result}
	 */
	@RequestMapping("/queryTemalateFileInfo")
	@ResponseBody
	public Result queryTemalateFileInfo(String id, HttpServletRequest request) {
		Result result = new Result();
		Map<String, Object> map = this.templateFileformService.queryTemalateFileInfo(id);
		result.setResult_data(map);
		return result;
	}

	/**
	 * 查询模板文件详情信息(查看详情页面)
	 * 
	 * @param id
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryTemalateFileInfoForView")
	@ResponseBody
	public Result queryTemalateFileInfoForView(String id, HttpServletRequest request) {
		Result result = new Result();
		Map<String, Object> map = this.templateFileformService.queryRideGuidelineInfoForView(id);
		result.setResult_data(map);

		return result;
	}

}

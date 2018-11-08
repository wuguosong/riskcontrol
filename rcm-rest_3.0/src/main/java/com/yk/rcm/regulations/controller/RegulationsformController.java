package com.yk.rcm.regulations.controller;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yk.rcm.regulations.service.IRegulationsformService;

import common.PageAssistant;
import common.Result;

/**
 * 
 * @author lyc
 *         规章制度
 *
 */

@Controller
@RequestMapping("/regulationsFrom")
public class RegulationsformController {

	@Resource
	private IRegulationsformService regulationsformService;

	/**
	 * 获取规章制度列表
	 * 
	 * @param request
	 *            request
	 * @return {@link Result}
	 */
	@RequestMapping("/queryRegulationsList")
	@ResponseBody
	public Result queryRegulationsList(HttpServletRequest request) {
		Result result = new Result();

		PageAssistant page = new PageAssistant(request.getParameter("page"));
		this.regulationsformService.queryRegulationsByPage(page);
		result.setResult_data(page);

		return result;
	}
	
	/**
	 * 获取已提交规章制度列表
	 * 
	 * @param request
	 *            request
	 * @return {@link Result}
	 */
	@RequestMapping("/queryRegulationsForSubmit")
	@ResponseBody
	public Result queryRegulationsForSubmit(HttpServletRequest request) {
		Result result = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		this.regulationsformService.queryRegulationsForSubmit(page);
		result.setResult_data(page);

		return result;
	}
	/**
	 * 新增规章制度
	 * 
	 * @param json
	 *            String
	 * @param request
	 *            request
	 * @return {@link Result}
	 */
	@RequestMapping("/addRegulations")
	@ResponseBody
	public Result addRegulations(String json, HttpServletRequest request) {
		Result result = new Result();
		String id = this.regulationsformService.addRegulations(json);
		result.setResult_data(id);

		return result;
	}

	/**
	 * 修改规章制度
	 * 
	 * @param json
	 *            String
	 * @param request
	 *            request
	 * @return {@link Result}
	 */
	@RequestMapping("/modifyRegulations")
	@ResponseBody
	public Result modifyRegulations(String json, HttpServletRequest request) {
		Result result = new Result();
		String id = this.regulationsformService.modifyRegulations(json);
		result.setResult_data(id);

		return result;
	}

	/**
	 * 提交规章制度，提交之后不允许修改
	 * 
	 * @param id
	 *            String
	 * @param request
	 *            request
	 * @return {@link Result}
	 */
	@RequestMapping("/submitRegulations")
	@ResponseBody
	public Result submitRegulations(String id, HttpServletRequest request) {
		Result result = new Result();
		this.regulationsformService.submitRegulations(id);
		return result;
	}

	/**
	 * 删除规章制度
	 * 
	 * @param ids
	 *            String
	 * @param request
	 *            request
	 * @return {@link Result}
	 */
	@RequestMapping("/deleteRegulations")
	@ResponseBody
	public Result deleteRegulations(String ids, HttpServletRequest request) {
		Result result = new Result();
		String[] idsArr = ids.split(",");
		this.regulationsformService.deleteRegulations(idsArr);

		return result;
	}

	/**
	 * 查询规章制度(为模板文件修改页面提供数据)
	 * 
	 * @param id
	 *            String
	 * @param request
	 *            request
	 * @return {@link Result}
	 */
	@RequestMapping("/queryRegulationsInfo")
	@ResponseBody
	public Result queryRegulationsInfo(String id, HttpServletRequest request) {
		Result result = new Result();
		Map<String, Object> map = this.regulationsformService.queryRegulationsInfo(id);
		result.setResult_data(map);
		return result;
	}

	/**
	 * 查询规章制度详情信息(查看详情页面)
	 * 
	 * @param id
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryRegulationsInfoForView")
	@ResponseBody
	public Result queryRegulationsInfoForView(String id, HttpServletRequest request) {
		Result result = new Result();
		Map<String, Object> map = this.regulationsformService.queryRegulationsInfoForView(id);
		result.setResult_data(map);

		return result;
	}

}

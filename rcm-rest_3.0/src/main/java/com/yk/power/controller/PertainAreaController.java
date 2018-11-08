package com.yk.power.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yk.power.service.IPertainAreaService;

import common.PageAssistant;
import common.Result;

/**
 * 业务区分管领导
 * @author yaphet
 *
 */
@Controller
@RequestMapping("/pertainArea")
public class PertainAreaController {
	@Resource
	private IPertainAreaService pertainAreaService;
	
	@RequestMapping("/queryList")
	@ResponseBody
	public Result queryList(HttpServletRequest request){
		Result result = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		List<Map<String, Object>> list = this.pertainAreaService.queryList(page);
		page.setList(list);
		result.setResult_data(page);
		return result;
	}
	
	@RequestMapping("/save")
	@ResponseBody
	public Result save(HttpServletRequest request){
		String json = request.getParameter("json");
		Result result = this.pertainAreaService.save(json);
		return result;
	}
	
	@RequestMapping("/getByUserId")
	@ResponseBody
	public Result getById(HttpServletRequest request){
		Result result = new Result();
		String userId = request.getParameter("userId");
		Map<String, Object> obj = this.pertainAreaService.getByUserId(userId);
		result.setResult_data(obj);
		return result;
	}
	
	@RequestMapping("/updateByUserId")
	@ResponseBody
	public Result updateByUserId(HttpServletRequest request){
		String json = request.getParameter("json");
		Result result = this.pertainAreaService.updateByUserId(json);
		return result;
	}
	
	@RequestMapping("/deleteByUserId")
	@ResponseBody
	public Result deleteByUserId(HttpServletRequest request){
		String userId = request.getParameter("userId");
		Result result = this.pertainAreaService.deleteByUserId(userId);
		return result;
	}
	
}

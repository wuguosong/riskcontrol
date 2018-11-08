/**
 * 
 */
package com.yk.rcm.project.controller;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yk.rcm.project.service.IJournalService;

import common.PageAssistant;
import common.Result;

/**
 * @author 80845530
 *
 */
@Controller
@RequestMapping("/journal")
public class JournalController {
	@Resource
	private IJournalService journalService;
	
	@RequestMapping("/queryByPage")
	@ResponseBody
	public Result queryByPage(HttpServletRequest request){
		Result result = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		this.journalService.queryByPage(page);
		result.setResult_data(page);
		return result;
	}
	@RequestMapping("/queryById")
	@ResponseBody
	public Result queryById(HttpServletRequest request){
		Result result = new Result();
		String id = request.getParameter("id");
		Map<String,Object> log = this.journalService.queryById(id);
		result.setResult_data(log);
		return result;
	}
}

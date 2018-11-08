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

import com.yk.rcm.project.service.IWsCallService;

import common.PageAssistant;
import common.Result;

/**
 * @author 80845530
 *
 */
@Controller
@RequestMapping("/wscall")
public class WsCallController {
	@Resource
	private IWsCallService wscallService;
	
	@RequestMapping("/sendTask")
	@ResponseBody
	public Result sendTask(HttpServletRequest request){
		String json = request.getParameter("json");
		Result result = this.wscallService.sendTask(json);
		return result;
	}
	
	@RequestMapping("/queryByPage")
	@ResponseBody
	public Result queryByPage(HttpServletRequest request){
		Result result = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		this.wscallService.queryByPage(page);
//		Object o = ((java.util.Map<String, Object>)(page.getList().get(0))).get("CREATETIME").getClass();
		result.setResult_data(page);
		return result;
	}
	/**
	 * 重新调接口，批量
	 * @param ids ，wscall的id串
	 * @return
	 */
	@RequestMapping("/repeatCallBatch")
	@ResponseBody
	public Result repeatCallBatch(String ids){
		if(ids == null || "".equals(ids.trim())){
			return new Result().setSuccess(false)
						.setResult_name("请选择要重新调用的记录！");
		}
		String[] wscallIds = ids.split(",");
		return this.wscallService.repeatCallBatch(wscallIds);
	}
	/**
	 * 重新调接口，单个
	 * @param ids ，wscall的id
	 * @return
	 */
	@RequestMapping("/repeatCallOne")
	@ResponseBody
	public Result repeatCallOne(String id){
		if(id == null || "".equals(id.trim())){
			return new Result().setSuccess(false)
						.setResult_name("请选择要重新调用的记录！");
		}
		return this.wscallService.repeatCallOne(id);
	}
	/**
	 * 根据决策通知书id,重新调接口，单个
	 * @param ids ，wscall的id
	 * @return
	 */
	@RequestMapping("/repeatCallByNoticeId")
	@ResponseBody
	public Result repeatCallByNoticeId(String id){
		if(id == null || "".equals(id.trim())){
			return new Result().setSuccess(false)
						.setResult_name("请输入决策通知书ID！");
		}
		return this.wscallService.repeatCallByNoticeId(id);
	}
	/**
	 * 初始化(无参)
	 * @return
	 */
	@RequestMapping("/initReportStatus")
	@ResponseBody
	public Result initReportStatus(String params){
		return this.wscallService.initReportStatus(params);
	}
	/**
	 * 初始化(有参)
	 * @return
	 */
	@RequestMapping("/initWithJson")
	@ResponseBody
	public Result initWithJson(String beanName, String json){
		return this.wscallService.initWithJson(beanName, json);
	}
	
	/**
	 * 初始化(有参)
	 * @return
	 */
	@RequestMapping("/queryById")
	@ResponseBody
	public Result queryById(String id){
		Result result = new Result();
		Map<String,Object> map = this.wscallService.queryById(id);
		return result.setResult_data(map);
	}
}

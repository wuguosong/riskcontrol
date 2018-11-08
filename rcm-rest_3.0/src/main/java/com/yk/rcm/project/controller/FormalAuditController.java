/**
 * 
 */
package com.yk.rcm.project.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yk.rcm.project.service.IFormalAuditService;

import common.Result;

/**
 * @author 80845530
 *
 */
@Controller
@RequestMapping("/formalAudit")
public class FormalAuditController {
	@Resource
	private IFormalAuditService formalAuditService;
	
	/**
	 * 查询下一步的审核人
	 * @param sign
	 * @param businessId
	 * @return
	 */
	@RequestMapping("/queryAuditUsers")
	@ResponseBody
	public Result queryAuditUsers(String sign, String businessId){
		Result result = new Result();
		if(sign == null || "".equals(sign) || businessId == null || "".equals(businessId)){
			return result.setSuccess(false).setResult_name("工单信息不对，请联系管理员！");
		}
		return result.setResult_data(this.formalAuditService.queryAuditUsers(sign, businessId));
	}
	
	/**
	 * 删除附件
	 * @param sign
	 * @param businessId
	 * @return
	 */
	@RequestMapping("/deleteAttachment")
	@ResponseBody
	public Result deleteAttachment(HttpServletRequest request){
		Result result = new Result();
		String json = request.getParameter("json");
		
		this.formalAuditService.deleteAttachment(json);
		
		return result;
	}
	/**
	 * 替换附件
	 * @param sign
	 * @param businessId
	 * @return
	 */
	@RequestMapping("/updateAttachment")
	@ResponseBody
	public Result updateAttachment(HttpServletRequest request){
		Result result = new Result();
		String json = request.getParameter("json");
		
		this.formalAuditService.updateAttachment(json);
		
		return result;
	}
	/**
	 * 新增附件
	 * @param sign
	 * @param businessId
	 * @return
	 */
	@RequestMapping("/addNewAttachment")
	@ResponseBody
	public Result addNewAttachment(HttpServletRequest request){
		Result result = new Result();
		String json = request.getParameter("json");
		
		this.formalAuditService.addNewAttachment(json);
		
		return result;
	}
	
	
	
}

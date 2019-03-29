/**
 * 
 */
package com.yk.rcm.project.controller;

import java.util.Arrays;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yk.rcm.project.service.IFirstlevelLawyerService;
import common.Result;

/**
 * 一级法务人员处理controller
 * @author 80845530
 *
 */
@Controller
@RequestMapping("/firstlevelLawyer")
public class FirstlevelLawyerController {
	@Resource
	private IFirstlevelLawyerService firstlevelLawyerService;
	/**
	 * 查询业务类型对应的一级法务人员
	 * @param serviceTypeKeys
	 * @return
	 */
	@RequestMapping("/queryByServiceTypes")
	@ResponseBody
	public Result queryByServiceTypes(String[] serviceTypeKeys){
		Result result = new Result();
		if(serviceTypeKeys == null || serviceTypeKeys.length == 0){
			return result.setSuccess(false)
				.setResult_name("请提供业务类型！");
		}
		return result.setResult_name("执行成功！")
			.setResult_data(this.firstlevelLawyerService.queryByServiceTypes(Arrays.asList(serviceTypeKeys)));
	}
	
}

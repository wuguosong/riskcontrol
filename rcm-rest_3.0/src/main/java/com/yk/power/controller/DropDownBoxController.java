package com.yk.power.controller;


import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yk.power.service.IDropDownBoxService;

import common.Result;

@Controller
@RequestMapping("/dropDownBox")
public class DropDownBoxController {

	@Resource
	private IDropDownBoxService dropDownBoxService;
	
	
	
	@RequestMapping("/queryDropDownBoxByCode")
	@ResponseBody
	public Result queryDropDownBoxByCode(String code){
		Result result = new Result();
		Map<String, Object> queryByCode = this.dropDownBoxService.queryByCode(code);
		result.setResult_data(queryByCode);
		return result;
	}
	
}

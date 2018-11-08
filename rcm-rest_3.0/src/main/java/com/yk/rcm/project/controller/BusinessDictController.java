/**
 * 
 */
package com.yk.rcm.project.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yk.rcm.project.service.IBusinessDictService;

import common.Result;

/**
 * @author 80845530
 *
 */
@Controller
@RequestMapping("/businessDict")
public class BusinessDictController {
	@Resource
	private IBusinessDictService businessDictService;
	
	/**
	 * 查询决策项目类型代码表
	 * @return
	 */
	@RequestMapping("/queryBusinessType")
	@ResponseBody
	public Result queryBusinessType(){
		Result result = new Result();
		List<Map<String, Object>> list = this.businessDictService.queryBusinessType();
		List<Map<String, Object>> newList = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> map : list) {
			Map<String, Object> newMap = new HashMap<String, Object>();
			newMap.put("KEY",map.get("ITEM_CODE"));
			newMap.put("VALUE", map.get("ITEM_NAME"));
			newList.add(newMap);
		}
		return result.setResult_data(newList);
	}
	
}

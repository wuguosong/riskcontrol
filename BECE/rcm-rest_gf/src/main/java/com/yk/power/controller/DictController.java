package com.yk.power.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yk.power.service.IDictService;

import common.PageAssistant;
import common.Result;

/**
 * 字典模块controller
 * 
 * @author wufucan
 * 
 */
@Controller
@RequestMapping("/dict")
public class DictController {

	@Resource
	private IDictService dictService;

	/**
	 * 分页查询字典类型
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryDictTypeByPage")
	@ResponseBody
	public Result queryDictTypeByPage(HttpServletRequest request) {
		Result result = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		this.dictService.queryDictTypeByPage(page);
		result.setResult_data(page);
		return result;
	}

	/**
	 * 接口请求地址
	 * 	http://localhost:8080/rcm-rest_1.0/dict/queryDictItemByDictTypeCode.do?code=10
	 * 
	 * 根据 字典类型   获取所有 字典值  
	 * @param code 字典类型code
	 * @return
	 */
	@RequestMapping("/queryDictItemByDictTypeCode")
	@ResponseBody
	public Result queryDictItemByDictTypeCode(String code){
		Result result = new Result();
		if(StringUtils.isNoneEmpty(code)){
			List<Map<String,Object>> resultData = dictService.queryDictItemByDictTypeCode(code);
			result.setResult_data(resultData);
		}else{
			result.setResult_name("code 不能为空");
		}
		return result;
	}
	
	/**
	 * 分页查询字典值
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryDictItemByDictTypeAndPage")
	@ResponseBody
	public Result queryDictItemByDictTypeAndPage(HttpServletRequest request) {
		Result result = new Result();
		PageAssistant page = new PageAssistant(request.getParameter("page"));
		dictService.queryDictItemByDictTypeAndPage(page);
		result.setResult_data(page);
		return result;
	}

	/**
	 * 新增或更新字典类型
	 * @param json
	 * @return
	 */
	@RequestMapping("/saveOrUpdateDictType")
	@ResponseBody
	public Result saveOrUpdateDictType(String json) {
		Document doc = Document.parse(json);
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("uuid", doc.get("UUID"));
		paramMap.put("dictionary_name", doc.get("DICTIONARY_NAME"));
		paramMap.put("dictionary_code", doc.get("DICTIONARY_CODE"));
		paramMap.put("dictionary_desc", doc.get("DICTIONARY_DESC")==null?"":doc.get("DICTIONARY_DESC"));
		Result result = null;
		if(null == paramMap.get("uuid") || StringUtils.isEmpty(paramMap.get("uuid").toString())){
			result = dictService.addDictType(paramMap);
		}else{
			result = dictService.updateDictType(paramMap);
		}
		result.setResult_data(paramMap.get("uuid"));
		return result;
	}
	
	/**
	 *  新增或更新字典值
	 * @param json
	 * @return
	 */
	@RequestMapping("/saveOrUpdateDictItem")
	@ResponseBody
	public Result saveOrUpdateDictItem(String json) {
		Document doc = Document.parse(json);
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("uuid", doc.get("UUID"));
		paramMap.put("fk_dictionary_uuid", doc.get("FK_DICTIONARY_UUID"));
		paramMap.put("item_name", doc.get("ITEM_NAME"));
		paramMap.put("item_code", doc.get("ITEM_CODE"));
		paramMap.put("is_enabled", doc.get("IS_ENABLED"));
		paramMap.put("business_type", doc.get("BUSINESS_TYPE") == null ? "" : doc.get("BUSINESS_TYPE"));
		paramMap.put("cust_number01", doc.get("CUST_NUMBER01"));
		Result result = null;
		if(null == paramMap.get("uuid") || StringUtils.isEmpty(paramMap.get("uuid").toString())){
			result = dictService.addDictItem(paramMap);
		}else{
			result = dictService.updateDictItem(paramMap);
		}
		result.setResult_data(paramMap.get("uuid"));
		return result;
	}
	
	/**
	 * 查询字典类型详情信息
	 * @param id
	 * @return
	 */
	@RequestMapping("/getDictTypeById")
	@ResponseBody
	public Result getDictTypeById(String uuid){
		Result result = new Result();
		Map<String, Object> dataMap =dictService.getDictTypeById(uuid);
		result.setResult_data(dataMap);
		return result;
	}
	
	/**
	 * 查询字典类型详情信息
	 * @param id
	 * @return
	 */
	@RequestMapping("/getDictItemById")
	@ResponseBody
	public Result getDictItemById(String uuid){
		Result result = new Result();
		Map<String, Object> dataMap =dictService.getDictItemById(uuid);
		result.setResult_data(dataMap);
		return result;
	}
	
	
	/**
	 * 删除一个或多个字典类型
	 * @param uuids
	 * @return
	 */
	@RequestMapping("/deleteDictTypeByIds")
	@ResponseBody
	public Result deleteDictTypeByIds(String uuids){
		Result result = new Result();
		String[] array = uuids.split(",");
		dictService.deleteDictTypeByIds(array);
		return result;
	}
	
	/**
	 *  用在删除之前，查询被删除的字典类型，是否含有字典值。
	 * @param uuids
	 * @return
	 */
	@RequestMapping("/queryDictItemByDictTypeIds")
	@ResponseBody
	public Result queryDictItemByDictTypeIds(String uuids) {
		Result result = new Result();
		String[] uuidArray = uuids.split(",");
		for(String uuid :uuidArray){
			PageAssistant page = new PageAssistant();
			page.setPageSize(1);
			page.getParamMap().put("FK_UUID", uuid);
			dictService.queryDictItemByDictTypeAndPage(page);
			if (page.getTotalItems() > 0) {
				result.setSuccess(false);
				result.setResult_name("选项中含有未删除的字典项，请先删除对应字典项信息再删除字典！");
				break;
			} 
		}
		return result;
	}
	
	/**
	 * 删除一个或多个字典值
	 * @param uuids
	 * @return
	 */
	@RequestMapping("/deleteDictItemByIds")
	@ResponseBody
	public Result deleteDictItemByIds(String uuids){
		Result result = new Result();
		String[] array = uuids.split(",");
		dictService.deleteDictItemByIds(array);
		return result;
	}
	
	/**
	 * 获取字典项 下字典值的最大序号+1
	 * @param FK_UUID
	 * @return
	 */
	@RequestMapping("/getDictItemLastIndexByDictType")
	@ResponseBody
	public Result getDictItemLastIndexByDictType(String FK_UUID){
		Result result = new Result();
		String maxIndex = dictService.getDictItemLastIndexByDictType(FK_UUID);
		result.setResult_data(maxIndex);
		return result;
	}
	
	/**
	 * 根据ItemCode获取项
	 * @return
	 */
	@RequestMapping("/getDictItemByItemCode")
	@ResponseBody
	public Result getDictItemByItemCode(HttpServletRequest request){
		Result result = new Result();
		String itemCode = request.getParameter("itemCode");
		Map<String,Object> item = dictService.getDictItemByItemCode(itemCode);
		result.setResult_data(item);
		return result;
	}
	
	/**
	 * 获取字典项 下字典值的最大序号+1
	 * @param FK_UUID
	 * @return
	 */
	@RequestMapping("/getDictItemLastIndexByDictCode")
	@ResponseBody
	public Result getDictItemLastIndexByDictCode(String code){
		Result result = new Result();
		String maxIndex = dictService.getDictItemLastIndexByDictCode(code);
		result.setResult_data(maxIndex);
		return result;
	}
}

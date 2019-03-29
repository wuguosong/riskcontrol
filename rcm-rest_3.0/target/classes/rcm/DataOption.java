package rcm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;

import util.DbUtil;
import util.Util;

import common.BaseService;
import common.BusinessException;
import common.PageAssistant;

public class DataOption extends BaseService {
	public PageAssistant getAll(String json) {
		PageAssistant assistant = new PageAssistant(json);
		this.selectByPage(assistant, "item.selectOption");
		return assistant;
	}

	public String createOption(String json) {
		Document bjson = Document.parse(json);
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("uuid", Util.getUUID());
		paramMap.put("fk_dictionary_uuid", bjson.get("FK_DICTIONARY_UUID"));
		paramMap.put("item_name", bjson.get("ITEM_NAME"));
		paramMap.put("item_code", bjson.get("ITEM_CODE"));
		paramMap.put("is_enabled", bjson.get("IS_ENABLED"));
		paramMap.put("business_type", bjson.get("BUSINESS_TYPE"));
		paramMap.put("cust_number01", bjson.get("CUST_NUMBER01"));
		// Map<String, Object> codeMap = new HashMap<String, Object>();
		// codeMap.get(uuid);
		String pId = DbUtil.openSession().selectOne("item.selectPkOption",
				paramMap);
		DbUtil.close();
		if ("0".equals(pId)) {
			Integer o = DbUtil.openSession().insert("item.insertOption",
					paramMap);
			DbUtil.close();
			return o.toString();
		} else {
			throw new BusinessException("code 不能为空");
		}
	}

	public String updateOption(String json) {
		Document bjson = Document.parse(json);
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("uuid", bjson.get("UUID"));
		paramMap.put("item_name", bjson.get("ITEM_NAME"));
		paramMap.put("item_code", bjson.get("ITEM_CODE"));
		paramMap.put("is_enabled", bjson.get("IS_ENABLED"));
		paramMap.put("business_type", bjson.get("BUSINESS_TYPE"));
		paramMap.put("cust_number01", bjson.get("CUST_NUMBER01"));
		String Oid = DbUtil.openSession().selectOne("item.selectPkOption",
				paramMap);
		DbUtil.close();
		if ("0".equals(Oid)) {
			Integer o = DbUtil.openSession().update("item.updateOption",
					paramMap);
			DbUtil.close();
			return o.toString();
		} else {
			throw new BusinessException("Code已存在");
		}
	}

	public Map getOptionByID(String id) {
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("uuid", id);
		Map doc = DbUtil.openSession().selectOne("item.selectOneOption",
				paramMap);
		DbUtil.close();
		return doc;
	}

	public String deleteOptionByID(String id) {
		String[] array = {};
		array = id.split(",");
		for (String ids : array) {
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("uuid", ids);
			DbUtil.openSession().delete("item.deleteOption", paramMap);
			DbUtil.close();
		}
		return "";
	}

	public boolean SelectCode(String arrId) {
		String[] array = {};
		array = arrId.split(",");
		boolean flag = true;
		for (String id : array) {
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("uuid", id);
			String did = DbUtil.openSession().selectOne("item.selectDidCode",paramMap);
			DbUtil.close();
			if (!"0".equals(did)) {
				flag = false;
				break;
			}
		}
		return flag;
	}
	
	/**
	 * 根据字典类别code获取字典项
	 */
	public Map<String, String> getDicItemByCode(String code){
		Map paramMap = new HashMap();
		paramMap.put("code", code);
		List<Map<String, String>> list = DbUtil.openSession().selectList("item.selectByCode",paramMap);
		Map<String, String> retMap = new HashMap<String, String>();
		if(Util.isNotEmpty(list)){
			for(Map<String, String> map : list){
				String itemCode = map.get("ITEM_CODE");
				String columnName = map.get("BUSINESS_TYPE");
				retMap.put(itemCode, columnName);
			}
		}
		return retMap;
	}
	/**
	 * 根据父code查子列表
	 * @param pcode
	 * @return
	 */
	public List<Map<String, Object>> queryItemsByPcode(String pcode){
		List<Map<String, Object>> list = DbUtil.openSession().selectList("item.queryItemsByPcode", pcode);
		return list;
	}
}

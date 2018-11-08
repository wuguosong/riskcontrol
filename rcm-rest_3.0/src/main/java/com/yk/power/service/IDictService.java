package com.yk.power.service;

import java.util.List;
import java.util.Map;

import common.PageAssistant;
import common.Result;


/**
 * 字典模块
 * @author wufucan
 *
 */
public interface IDictService {
	
	/**
	 * 新增字典类型
	 * @param dictType
	 * @return 
	 */
	public Result addDictType(Map<String, Object> dictType);
	
	/**
	 * 更新字典类型
	 * @param dictType
	 * @return 
	 */
	public Result updateDictType(Map<String, Object> dictType);
	
	/**
	 * 新增字典值
	 * @param dictOption
	 */
	public Result addDictItem(Map<String, Object> dictItem);

	/**
	 * 更新字典值
	 * @param dictType
	 * @return 
	 */
	public Result updateDictItem(Map<String, Object> dictItem);
	
	/**
	 * 查询字典类型
	 * @param page
	 */
	public void queryDictTypeByPage(PageAssistant page);
	
	/**
	 * 查询字典值
	 * @param page
	 */
	public void queryDictItemByDictTypeAndPage(PageAssistant page);
	
	/**
	 * 查询字典类型详情信息
	 * @param id
	 * @return
	 */
	public Map<String, Object> getDictTypeById(String id);

	/**
	 * 删除一个或多个字典类型
	 * @param array
	 */
	public void deleteDictTypeByIds(String[] uuidArray);

	/**
	 * 删除一个或多个字典值
	 * @param array
	 */
	public void deleteDictItemByIds(String[] uuidArray);

	/**
	 * 查询字典值详情信息
	 * @param id
	 * @return
	 */
	public Map<String, Object> getDictItemById(String uuid);

	/**
	 * 获取字典项 下字典值的最大序号+1
	 * @param FK_DICTIONARY_UUID
	 * @return
	 */
	public String getDictItemLastIndexByDictType(String FK_UUID);

	/**
	 * 根据 字典类型   获取所有 字典值  
	 * @param code 字典类型code
	 * @return
	 */
	public List<Map<String, Object>> queryDictItemByDictTypeCode(String code);
	
	/**
	 * 根据sys_dictionary.DICTIONARY_CODE and  sys_dictionary_item.ITEM_NAME 匹配唯一值
	 * @param fuCode
	 * @param ziCode
	 * @return
	 */
	public Map<String, Object> queryItemByFuCoZiName(String fuCode,String ziCode);
	
	/**
	 * 根据itemCode查询字典信息
	 * @param itemCode
	 * @return
	 */
	public Map<String, Object> getDictItemByItemCode(String itemCode);
		
	/**
	 * 根据code查询  类型
	 * @param code
	 * @return
	 */
	public Map<String, Object> getDictByCode(String code);
	
	/**
	 * 根据UUID，删除字典值
	 * @param uuid
	 * @return
	 */
	public void deleteDictItemById(String uuid);

	/**
	 * 获取字典项 下字典值的最大序号+1
	 * @param code
	 * @return
	 */
	public String getDictItemLastIndexByDictCode(String code);

	public void addDictItemMeetLeader(Map<String, Object> dictItem);

	public void updateDictItemMeetLeader(Map<String, Object> dictItem);
}

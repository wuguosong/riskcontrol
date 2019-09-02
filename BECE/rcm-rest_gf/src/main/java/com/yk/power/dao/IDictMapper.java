/**
 * 
 */
package com.yk.power.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.yk.common.BaseMapper;

/**
 * @author wufucan
 *
 */
public interface IDictMapper extends BaseMapper {

	/**
	 * 查询字典类型
	 * @param params
	 */
	public List<Map<String, Object>> queryDictTypeByPage(
			Map<String, Object> params);

	/**
	 * 查询字典值
	 * @param params
	 */
	public List<Map<String, Object>> queryDictItemByDictTypeAndPage(
			Map<String, Object> params);

	/**
	 * 根据 字典类型 和字典code 查询(统计)  字典类型信息
	 * @param dictItem
	 * @return
	 */
	public String selectPkDictType(Map<String, Object> dictType);

	/**
	 * 新增字典类型
	 * @param dictType
	 * @return 
	 */
	public void addDictType(Map<String, Object> dictType);
	
	/**
	 * 更新字典类型
	 * @param dictOption
	 */
	public void updateDictType(Map<String, Object> dictType);
	
	/**
	 * 根据字典类型和字典code 查询(统计)  字典值信息
	 * @param dictItem
	 * @return
	 */
	public String selectPkDictItem(Map<String, Object> dictItem);
	
	/**
	 * 新增字典值
	 * @param dictItem
	 */
	public void addDictItem(Map<String, Object> dictItem);
	
	/**
	 * 更新字典值
	 * @param dictItem
	 */
	public void updateDictItem(Map<String, Object> dictItem);
	
	public Map<String, Object> getDictTypeById(@Param("uuid") String uuid);

	public void deleteDictTypeByIds(@Param("uuidArray") String[] uuidArray);

	public void deleteDictItemByIds(@Param("uuidArray") String[] uuidArray);

	public Map<String, Object> getDictItemById(@Param("uuid") String uuid);

	public String getDictItemLastIndexByDictType(@Param("FK_UUID") String FK_UUID);

	public List<Map<String, Object>> queryDictItemByDictTypeCode(@Param("code") String code);

	public Map<String, Object> getDictItemByItemCode(@Param("itemCode") String itemCode);
	
	public Map<String, Object> getDictByCode(@Param("code") String code);

	public void deleteDictItemById(@Param("uuid") String uuid);
	
	public String getDictItemLastIndexByDictCode(@Param("code") String code);

	public void addDictItemMeetLeader(Map<String, Object> dictItem);

	public void updateDictItemMeetLeader(Map<String, Object> dictItem);

	public Map<String, Object> queryItemByFuCoZiName(@Param("dictionary_code") String dictionary_code,
			@Param("item_name") String item_name);
}

/**
 * 
 */
package com.yk.rcm.project.service;

import java.util.List;
import java.util.Map;

/**
 * 一级法务人员service
 * @author 80845530
 *
 */
public interface IFirstlevelLawyerService {
	
	/**
	 * 根据一级业务类型查询
	 * @param serviceTypes
	 * @return
	 */
	public List<Map<String, Object>> queryByServiceTypes(List<String> serviceTypes);
	/**
	 * 根据id更新
	 * @param data
	 */
	public void updateById(Map<String, Object> data);
}

/**
 * 
 */
package com.yk.rcm.project.service;

import java.util.List;
import java.util.Map;

/**
 * @author 80845530
 *
 */
public interface IFormalAuditService {
	/**
	 * 查询sequenceFlow审核人信息
	 * @param sign
	 * @return
	 */
	public List<Map<String, Object>> queryAuditUsers(String sign, String businessId);
	/**
	 * 删除附件
	 * @param json
	 */
	public void deleteAttachment(String json);
	
	/**
	 * 修改附件
	 * @param json
	 */
	public void updateAttachment(String json);
	
	/**
	 * 新增附件
	 * @param json
	 */
	public void addNewAttachment(String json);

}

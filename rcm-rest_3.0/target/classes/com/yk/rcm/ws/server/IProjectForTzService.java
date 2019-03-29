/**
 * 
 */
package com.yk.rcm.ws.server;



/**
 * 投资系统接口
 * @author wufucan
 *
 */
public interface IProjectForTzService {
	/**
	 * 推送预评审数据
	 * @param json
	 * @return
	 */
	public String createPre(String json);
	/**
	 * 推送正式评审数据
	 * @param json
	 * @return
	 */
	public String createPfr(String json);
	/**
	 * 删除预评审
	 * @param json
	 * 根据业务id来判断是否删除
	 * @return
	 */
	public String deletePre(String json);
	/**
	 * 删除正式评审
	 * @param json
	 * 根据业务id来判断是否删除
	 * @return
	 */
	public String deletePfr(String json);
	
	
	/**
	 * 预评审投标结果反馈
	 * createPreFeedBack
	 * @return
	 * */
	public String createPreFeedBack(String json);
	
}

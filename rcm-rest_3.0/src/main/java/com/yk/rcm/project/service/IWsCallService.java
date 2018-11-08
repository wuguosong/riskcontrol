/**
 * 
 */
package com.yk.rcm.project.service;

import java.util.Map;

import common.PageAssistant;
import common.Result;

/**
 * @author 80845530
 *
 */
public interface IWsCallService {
	/**
	 * 保存接口调用记录
	 * @param data
	 */
	public void save(Map<String, Object> data);

	public void queryByPage(PageAssistant page);
	/**
	 * 重新调用接口，批量
	 * @param wscallIds
	 * @return
	 */
	public Result repeatCallBatch(String[] wscallIds);
	/**
	 * 重新调用接口，单个
	 * @param wscallIds
	 * @return
	 */
	public Result repeatCallOne(String id);
	/**
	 * 根据决策通知书id,重新调接口，单个
	 * @param wscallIds
	 * @return
	 */
	public Result repeatCallByNoticeId(String id);
	/**
	 * 初始化
	 * @param params 
	 * @return
	 */
	public Result initReportStatus(String params);
	/**
	 * 初始化带参数的命令
	 * @param beanName
	 * @param json
	 * @return
	 */
	public Result initWithJson(String beanName, String json);
	/**
	 * 发送代办已办
	 * @param json
	 * @return
	 */
	public Result sendTask(String json);
	/**
	 *   保存请求调用接口的信息
	 * @param type 接口类型
	 * @param content 请求参数
	 * @param result 响应结果
	 * @param success 是否成功
	 * @return
	 */
	public void saveWsServer(String type, String content, String result,boolean success);

	public Map<String, Object> queryById(String id);
}

package com.yk.rcm.newFormalAssessment.service;

import java.util.List;
import java.util.Map;

import common.PageAssistant;


/**
 * @author Sunny Qi
 *
 */
public interface IFormalAssessmentInfoCreateService {
	
	/**
	 * 新增项目
	 * */
	public String createProject(String json);
	
	/**
	 * 修改项目
	 * */
	public void updateProject(String json);
	
	/**
	 * 删除项目
	 * */
	public void deleteProject(String ids);
	
	/**
	 * 查询新增项目
	 * */
	public PageAssistant getNewProjectList(PageAssistant page);
	
	/**
	 * 查询新增项目详情
	 * */
	public Map<String, Object> getProjectByID(String id);
	
	/**
	 * 新增附件
	 * @param json
	 */
	void addNewAttachment(String json);
	/**
	 * 删除附件
	 * @param json
	 */
	void deleteAttachment(String json);
	/**
	 * 修改附件是否上会标识
	 * @param json
	 */
	String changeMeetingAttach(String json);
	/**
	 * 提交检查附件
	 * @param Json
	 */
	List<Map> checkAttachment(String Json);
	/**
	 * 替换附件
	 * @param json
	 */
	/*void updateAttachment(String json);*/
	/**
	 * 新增会议信息(保存到mongo)
	 * @param json
	 * @param method
	 */
	void addConferenceInformation(String json, String method);
	
	/**
	 * 保存是否上会信息
	 * @param id
	 * @param needMeeting
	 * */
	void saveNeedMeeting(String id, String needMeeting);
}

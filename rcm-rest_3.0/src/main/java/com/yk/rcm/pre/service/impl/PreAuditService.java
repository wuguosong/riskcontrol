package com.yk.rcm.pre.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.yk.rcm.pre.dao.IPreAuditLogMapper;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import util.ThreadLocalUtil;
import util.Util;

import com.yk.bpmn.entity.TaskInfo;
import com.yk.common.IBaseMongo;
import com.yk.flow.dto.SingleProcessOption;
import com.yk.flow.service.IBpmnAuditService;
import com.yk.flow.util.ProcessResult;
import com.yk.rcm.pre.dao.IPreAuditMapper;
import com.yk.rcm.pre.service.IPreAuditService;
import com.yk.rcm.pre.service.IPreInfoService;

import common.Constants;
import common.PageAssistant;
import common.Result;

@Service
@Transactional
public class PreAuditService implements IPreAuditService{
	
	@Resource
	private IBaseMongo baseMongo;
	
	@Resource
	private IBpmnAuditService bpmnAuditService;
	
	@Resource
	private IPreAuditLogMapper preAuditLogMapper;

	@Resource
	private IPreAuditMapper preAuditMapper;
	
	@Resource
	private IPreInfoService preInfoService;
	
	@Override
	public void queryAuditedList(PageAssistant page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		String assignUserId = ThreadLocalUtil.getUserId();
		if(ThreadLocalUtil.getIsAdmin()){
			//管理员能看所有的
			assignUserId = null;
		}
		if(page.getParamMap() != null){
			params.putAll(page.getParamMap());
		}
		String orderBy = page.getOrderBy();
		params.put("assignUserId", assignUserId);
		params.put("orderBy", orderBy);
		List<Map<String, Object>> list = this.preAuditMapper.queryAuditedList(params);
		for (Map<String, Object> map : list) {
			String id = (String)map.get("BUSINESSID");
			Map<String, Object> mongoDate = baseMongo.queryById(id,Constants.RCM_PRE_INFO);
			map.put("mongoDate", mongoDate);
		}
		page.setList(list);
	}
	@Override
	public void queryWaitList(PageAssistant page) {
		//查询待办的sql
		String newWaitingsql = this.bpmnAuditService.queryWaitdealSql(Constants.PROCESS_KEY_PREREVIEW, ThreadLocalUtil.getUserId()).getData();
		String oldWaitingsql = this.bpmnAuditService.queryWaitdealSql(Constants.PRE_ASSESSMENT, ThreadLocalUtil.getUserId()).getData();

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		if(page.getParamMap() != null){
			params.putAll(page.getParamMap());
		}
		params.put("assignSql1", newWaitingsql);
		params.put("assignSql2", oldWaitingsql);
		params.put("userId", ThreadLocalUtil.getUserId());
		List<Map<String, Object>> list = this.preAuditMapper.queryWaitList(params);
		for (Map<String, Object> map : list) {
			String id = (String)map.get("BUSINESSID");
			Map<String, Object> mongoDate = baseMongo.queryById(id,Constants.RCM_PRE_INFO);
			map.put("mongoDate", mongoDate);
		}
		page.setList(list);
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Result querySingleProcessOptions(String businessId) {
		Result result = new Result();
		ProcessResult<List<SingleProcessOption>> pr = this.bpmnAuditService.querySingleSequenceFlow(Constants.PROCESS_KEY_PREREVIEW, businessId, ThreadLocalUtil.getUserId());
		
		String flowId = "flow18";
		List<SingleProcessOption> data = pr.getData();
		
		for(int i = 0 ;i<data.size();i++){
			if(data.get(i).getFlowId().equals(flowId)){
				//查询固定小组成员
				Map<String, Object> pre = this.preInfoService.getPreInfoByID(businessId);
				Map<String, Object> preOracle = (Map<String, Object>) pre.get("oracle");
				//TODO 等正式评审的专业评审做好之后参考正式评审设计投标评审的专业评审部分
				String fixedGroupPersonIds = (String) preOracle.get("FIXEDGROUPPERSONIDS");
				if(Util.isEmpty(fixedGroupPersonIds)){
					data.remove(i);
				}
			}
		}
		
		result.setResult_data(data);
		return result;
	}
	
	@Override
	public Result startSingleFlow(String businessId) {
		Result result = new Result();
		//启动流程
		ProcessResult<ProcessInstance> pr = this.bpmnAuditService.startFlow(Constants.PROCESS_KEY_PREREVIEW, businessId, null);
		if(!pr.isSuccess()){
			result.setResult_name(pr.getMessage());
		}
		return result;
	}

	@Override
	public Result auditSingle(String businessId, String processOption, Map<String, Object> variables) {
		Result result = new Result();
		ProcessResult<String> pr = this.bpmnAuditService.auditSingle(processOption, variables);
		if(!pr.isSuccess()){
			result.setSuccess(false);
			result.setResult_name(pr.getMessage());
		}
		return result;
	}
	
	@Override
	public TaskInfo queryTaskInfoByBusinessId(String businessId,String processKey) {
		String userId = ThreadLocalUtil.getUserId();
		List<Task> queryTaskInfo = bpmnAuditService.queryTaskInfo(processKey, businessId, userId);
		/**Add By LiPan**/
		List<Map<String, Object>> logs = preAuditLogMapper.queryAuditedLogsById(businessId);
		if(CollectionUtils.isEmpty(queryTaskInfo)){
			/**
			 * 1.依靠当前登录用已经不能从流程相关表中查询到任务记录
			 * 2.因为当前登录用户可能是转发用户(即被指定的加签用户)
			 * 3.此时需要使用加签用户的原始用户记录取查询任务信息
			 * 4.从每个业务日志表中获取当前用户的原始用户信息
			 * */
			if (CollectionUtils.isNotEmpty(logs)){
				for(Map<String, Object> log : logs){
					String audituserid = String.valueOf(log.get("AUDITUSERID"));
					String iswaiting = String.valueOf(log.get("ISWAITING"));
					if("1".equals(iswaiting) && userId.equals(audituserid)){
						userId = String.valueOf(log.get("OLDUSERID"));
						break;
					}
				}
				queryTaskInfo = bpmnAuditService.queryTaskInfo(processKey, businessId, userId);
			}
		}else{
			/**
			 * 1.即使当前用户在流程表中可以查到,也要预防另一种情况发生
			 * 2.当前用户为原始用户,但是当前用户进行了转办操作,已经转办给另一个用户,且操作完成
			 * 3.此时不因该被查到任务信息
			 * 4.对于页面来说,流程已经提交成功,就不能再进行提交操作了
			 */
			if (CollectionUtils.isNotEmpty(logs)){
				for(Map<String, Object> log : logs){
					String audituserid = String.valueOf(log.get("AUDITUSERID"));
					String iswaiting = String.valueOf(log.get("ISWAITING"));
					if("1".equals(iswaiting) && !userId.equals(audituserid)){
						userId = String.valueOf(log.get("AUDITUSERID"));
						break;
					}
				}
				queryTaskInfo = bpmnAuditService.queryTaskInfo(processKey, businessId, userId);
			}
		}
		/**Add By LiPan**/
		TaskInfo taskInfo = new TaskInfo();
		if(queryTaskInfo.size()>0){
			taskInfo.setDescription(queryTaskInfo.get(0).getDescription());
			taskInfo.setAssignee(queryTaskInfo.get(0).getAssignee());
			taskInfo.setTaskId(queryTaskInfo.get(0).getId());
			taskInfo.setTaskKey(queryTaskInfo.get(0).getTaskDefinitionKey());
		}
		return taskInfo;
	}
}

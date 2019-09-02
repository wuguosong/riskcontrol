package com.yk.rcm.noticeofdecision.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.activiti.engine.runtime.ProcessInstance;
import org.bson.Document;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import util.ThreadLocalUtil;

import com.mongodb.BasicDBObject;
import com.yk.common.BaseMongo;
import com.yk.flow.dto.SingleProcessOption;
import com.yk.flow.service.IBpmnAuditService;
import com.yk.flow.util.ProcessResult;
import com.yk.power.service.IRoleService;
import com.yk.rcm.noticeofdecision.dao.INoticeDecisionAuditMapper;
import com.yk.rcm.noticeofdecision.service.INoticeDecisionAuditService;
import com.yk.rcm.noticeofdecision.service.INoticeDecisionInfoService;

import common.Constants;
import common.PageAssistant;
import common.Result;

/**
 * @author yaphet
 *
 */
@Service
@Transactional
public class NoticeDecisionAuditService implements INoticeDecisionAuditService {
	
	@Resource
	private INoticeDecisionAuditMapper noticeDecisionAuditMapper; 
	@Resource
	private INoticeDecisionInfoService noticeDecisionInfoService; 
	@Resource
	private IBpmnAuditService bpmnAuditService;
	@Resource
	private IRoleService roleService;
	@Resource
	private BaseMongo baseMongo;
	
	private final static String RCM_NOTICEDECISION_INFO = Constants.RCM_NOTICEDECISION_INFO;
	private final static String FORMAL_MEETING_INFO = Constants.FORMAL_MEETING_INFO;
	
	
	
	@Override
	public void queryAuditedList(PageAssistant page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		String assignUserId = ThreadLocalUtil.getUserId();
		/*if(ThreadLocalUtil.getIsAdmin()){
			//管理员能看所有的
			assignUserId = null;
		}*/
		if(page.getParamMap() != null){
			params.putAll(page.getParamMap());
		}
		String orderBy = page.getOrderBy();
		if(orderBy == null){
			orderBy = " apply_date desc ";
		}
		params.put("assignUserId", assignUserId);
		params.put("orderBy", orderBy);
		List<Map<String, Object>> list = this.noticeDecisionAuditMapper.queryAuditedList(params);
		page.setList(list);
	}
	@Override
	public void queryWaitList(PageAssistant page) {
		//查询待办的sql
		String sql = this.bpmnAuditService.queryWaitdealSql(Constants.PROCESS_KEY_NOTICEDECISION, ThreadLocalUtil.getUserId()).getData();
		String sql2 = this.bpmnAuditService.queryWaitdealSql(Constants.FORMAL_NOTICEOFDECSTION, ThreadLocalUtil.getUserId()).getData();
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		/*if(ThreadLocalUtil.getIsAdmin()){
			//管理员能看所有的
			sql = null;
		}*/
		if(page.getParamMap() != null){
			params.putAll(page.getParamMap());
		}
		String orderBy = page.getOrderBy();
		if(orderBy == null){
			orderBy = " apply_date desc ";
		}
		params.put("assignSql", sql);
		params.put("assignSql2", sql2);
		params.put("orderBy", orderBy);
		List<Map<String, Object>> list = this.noticeDecisionAuditMapper.queryWaitList(params);
		page.setList(list);
		
	}
	
	@Override
	public Result querySingleProcessOptions(String businessId) {
		Result result = new Result();
		ProcessResult<List<SingleProcessOption>> pr = this.bpmnAuditService.querySingleSequenceFlow(Constants.PROCESS_KEY_NOTICEDECISION, businessId, ThreadLocalUtil.getUserId());
		result.setResult_data(pr.getData());
		return result;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Result startSingleFlow(String businessId) {
		Result result = new Result();
		
		//查mongo根据id查niticeDecision
		Map<String, Object> noticeDecisionById = baseMongo.queryById(businessId, RCM_NOTICEDECISION_INFO);
		String projectFormalId = (String) noticeDecisionById.get("projectFormalId");
		BasicDBObject queryWhere = new BasicDBObject();
		queryWhere.put("formalId", projectFormalId);
		Map<String, Object> formalMeetingInfo = baseMongo.queryByCondition(queryWhere, FORMAL_MEETING_INFO).get(0);
		//查找上会人、在启动流程时添加到流程中
		
		Map<String,Object> openMeetingPerson = (Map<String,Object>) formalMeetingInfo.get("openMeetingPerson");
		
		Map<String, Object> variables = new HashMap<String, Object>();
		
		variables.put("openMeetingPerson", (String)openMeetingPerson.get("value"));
		
		ProcessResult<ProcessInstance> pr = this.bpmnAuditService.startFlow(Constants.PROCESS_KEY_NOTICEDECISION, businessId, variables);
		
		if(pr.isSuccess()){
			//启动流程成功，修改niticeDecision状态
			this.noticeDecisionInfoService.updateAfterStartflow(businessId);
		}else{
			result.setResult_name(pr.getMessage());
		}
		return result;
	}



	@Override
	public Result auditSingle(String businessId, String processOption,
			Map<String, Object> variables) {
		
		Result result = new Result();
		ProcessResult<String> pr = this.bpmnAuditService.auditSingle(processOption, variables);
		if(!pr.isSuccess()){
			result.setSuccess(false)
				.setResult_name(pr.getMessage());
		}
//		Integer.parseInt("aa");
		return result;
	}
	@Override
	public List<Map<String, Object>> queryAuditedLogsById(String businessId) {
		
		return this.noticeDecisionAuditMapper.queryAuditedLogsById(businessId);
	}
	
	@Override
	public void saveAttachmentById(String businessId, Document attachment) {
		Map<String, Object> data = new HashMap<String, Object>();
		ArrayList<Document> list = new ArrayList<Document>();
		list.add(attachment);
		data.put("attachment", list);
		this.baseMongo.updateSetByObjectId(businessId, data , RCM_NOTICEDECISION_INFO);
	}
}

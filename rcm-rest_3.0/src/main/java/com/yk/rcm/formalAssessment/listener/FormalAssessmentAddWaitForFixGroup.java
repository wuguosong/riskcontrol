package com.yk.rcm.formalAssessment.listener;
import javax.annotation.Resource;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.springframework.stereotype.Component;
import com.yk.rcm.formalAssessment.service.IFormalAssessmentAuditLogService;
import com.yk.rcm.formalAssessment.service.IFormalAssessmentAuditService;
/**
 * 
 * @author yaphet
 * 
 * 2017年6月6日
 *
 */
@Component
public class FormalAssessmentAddWaitForFixGroup implements ExecutionListener {

	@Resource
	private IFormalAssessmentAuditService formalAssessmentAuditService;
	@Resource 
	private IFormalAssessmentAuditLogService formalAssessmentAuditLogService;
	
	private static final long serialVersionUID = 1L;

	@Override
	public void notify(DelegateExecution execution) throws Exception {
//		String businessId = execution.getProcessBusinessKey();
//		//删除评审负责人待办
//		Map<String,Object> log = new HashMap<String,Object>();
//		//获取评审负责人id
//		String reviewLeaderId = (String) execution.getVariable("reviewLeader");
//		log.put("businessId", businessId);
//		log.put("currentUserId", reviewLeaderId);
//		log.put("isWaiting", "1");
//		formalAssessmentAuditLogService.deleteByBusinessIdAndAuditUserId(log);
//		//添加固定小组成员待办
//		List<String> groupMembers = (List<String>) execution.getVariable("groupMembers");
//		for (String groupMemberId : groupMembers) {
//			Map<String,Object> data = new HashMap<String,Object>();
//			data.put("businessId", businessId);
//			data.put("auditUserId", groupMemberId);
//			data.put("auditTime", Util.now());
//			data.put("opinion", null);
//			data.put("auditStatus", "9");
//			data.put("orderBy", this.formalAssessmentAuditLogService.queryNextOrderNum(businessId));
//			data.put("isWaiting", "1");
//			data.put("taskdesc", "固定小组成员审批");
//			data.put("taskId", null);
//			data.put("executionId", null);
//			formalAssessmentAuditLogService.save(data);
//		}
	}

}

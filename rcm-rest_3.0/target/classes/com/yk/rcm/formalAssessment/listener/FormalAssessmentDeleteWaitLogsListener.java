package com.yk.rcm.formalAssessment.listener;
import javax.annotation.Resource;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.delegate.Expression;
import org.springframework.stereotype.Component;
import com.yk.rcm.formalAssessment.service.IFormalAssessmentAuditLogService;
/**
 * 
 * @author yaphet
 * 
 * 2017年6月7日
 *
 */
@Component
public class FormalAssessmentDeleteWaitLogsListener implements ExecutionListener{

	@Resource
	private IFormalAssessmentAuditLogService formalAssessmentAuditLogService;
	
	private static final long serialVersionUID = 1L;
	
	public Expression assignId;
	@Override
	public void notify(DelegateExecution execution) throws Exception {
	}

}

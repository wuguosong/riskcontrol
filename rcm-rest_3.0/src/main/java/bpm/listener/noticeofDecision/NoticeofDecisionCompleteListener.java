/**
 * 
 */
package bpm.listener.noticeofDecision;

import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.apache.log4j.Logger;

import rcm.NoticeOfDecisionInfo;
import ws.client.contract.SendProjectThread;

/**
 * @Description: 
 * @Author zhangkewei
 * @Date 2016年10月17日 下午4:20:56  
 */
public class NoticeofDecisionCompleteListener implements TaskListener{
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(NoticeofDecisionCompleteListener.class);
	
	@Override
	public void notify(DelegateTask delegateTask) {
		try {
			String businessId = delegateTask.getExecution().getProcessBusinessKey();
			String processInstanceId = delegateTask.getProcessInstanceId();
			String processDefId = delegateTask.getProcessDefinitionId();
			logger.debug("businessId:"+businessId+" processInstanceId:"+processInstanceId+" processDefId:"+processDefId);
			NoticeOfDecisionInfo nd = new NoticeOfDecisionInfo();
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("wfState", "2");
			nd.updateNoticeDecisionInfo(businessId, params);
			
			SendProjectThread sendProject = new SendProjectThread(businessId);
			new Thread(sendProject).start();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}

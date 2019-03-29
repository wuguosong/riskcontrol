/**
 * 
 */
package bpm.listener.noticeofDecision;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.apache.log4j.Logger;

import rcm.NoticeOfDecisionInfo;

/**
 * @Description: 
 * 更新决策通知单状态为审批中状态wfState=1, applyDate=new Date()
 * @Author zhangkewei
 * @Date 2016年10月17日 下午4:20:56  
 */
public class NoticeofDecisionStartListener implements TaskListener{
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(NoticeofDecisionStartListener.class);

	@Override
	public void notify(DelegateTask delegateTask) {
		try {
			String businessId = delegateTask.getExecution().getProcessBusinessKey();
			NoticeOfDecisionInfo nd = new NoticeOfDecisionInfo();
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("wfState", "1");
			params.put("applyDate", new Date());
			nd.updateNoticeDecisionInfo(businessId, params);
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
		
	}

}

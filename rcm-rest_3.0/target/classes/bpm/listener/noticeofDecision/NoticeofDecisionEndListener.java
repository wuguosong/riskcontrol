/**
 * 
 */
package bpm.listener.noticeofDecision;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.log4j.Logger;

import rcm.NoticeOfDecisionInfo;

/**
 * @Description: 
 * 更新申请单状态,如果状态为审批通过(wfState=2)，那么此处不做处理，如果状态为
 * 审批中(wfState=1)，则说明未经过出具评审报告环节，直接从其它环节跳转到结束的，此时
 * 需要把状态设置为项目终止状态(wfState=3)
 * @Author zhangkewei
 * @Date 2016年10月17日 下午4:20:56  
 */
public class NoticeofDecisionEndListener implements JavaDelegate{
	private static Logger logger = Logger.getLogger(NoticeofDecisionEndListener.class);

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		try {
			String businessId = execution.getProcessBusinessKey();
			NoticeOfDecisionInfo nd = new NoticeOfDecisionInfo();
			Map<String, Object> map = nd.selectByBusinessId(businessId);
			String wfState = (String)map.get("WF_STATE");
			Map<String, Object> params = new HashMap<String, Object>();
			if("1".equals(wfState)){
				params.put("wfState", "3");
			}
			params.put("completeDate", new Date());
			nd.updateNoticeDecisionInfo(businessId, params);
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
		
	}

}

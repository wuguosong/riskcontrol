/**
 * 
 */
package sys.jobs;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import rcm.NoticeInfo;
import util.Util;

/**
 * @Description: 预警定时任务
 * @Author zhangkewei
 * @Date 2016年10月9日 下午9:17:52  
 */
public class WarningJob extends JobSchedule{
	Log loger  = LogFactory.getLog(WarningJob.class);
	private final static String executeTime = "8:00";
	
	@Override
	public Long getDelaySeconds() {
		return Util.getDelaySeconds(executeTime);
	}

	@Override
	public Long getPeriodSeconds() {
		return periordSeconds;
	}
	
	@Override
	public void run() {
		try {
			NoticeInfo ns = new NoticeInfo();
			ns.insertWarningInfo();
		} catch (Exception e) {
			loger.error(e);
		}
	}


}

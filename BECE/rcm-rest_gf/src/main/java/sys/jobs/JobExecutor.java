/**
 * 
 */
package sys.jobs;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @Description: TODO
 * @Author zhangkewei
 * @Date 2016年10月9日 下午9:30:18  
 */
public class JobExecutor {
	private ScheduledExecutorService service = null;
	public JobExecutor(){
		service = Executors.newSingleThreadScheduledExecutor();
	}
	
	public void add(JobSchedule job){
		service.scheduleAtFixedRate(job, job.getDelaySeconds(), job.getPeriodSeconds(), TimeUnit.SECONDS);
	}
	
	/**
	 * 如果需要添加其他定时任务，请在这个方法中添加调用入口
	 */
	public static void init(){
		JobExecutor executor = new JobExecutor();
		WarningJob wj = new WarningJob();
		executor.add(wj);
	}
}

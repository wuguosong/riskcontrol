/**
 * 
 */
package sys.jobs;

/**
 * @Description: TODO
 * @Author zhangkewei
 * @Date 2016年9月29日 下午2:26:55  
 */
public abstract class JobSchedule implements Runnable{
	protected Long periordSeconds = 60*60*24L;
	//延迟时间(秒)
	public abstract Long getDelaySeconds();
	//间隔时间(秒)
	public abstract Long getPeriodSeconds();
}

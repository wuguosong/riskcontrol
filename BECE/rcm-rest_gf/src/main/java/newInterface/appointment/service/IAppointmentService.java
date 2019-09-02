package newInterface.appointment.service;

/**
 * 
 * @author Sunny Qi
 * 
 * 20190411
 *
 */
public interface IAppointmentService {
	
	/**
	 * 将约会系统数据同步到会议预备表中
	 * */
	public void saveMeeting(String Json);
}

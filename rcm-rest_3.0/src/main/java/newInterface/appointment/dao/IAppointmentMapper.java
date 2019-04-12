package newInterface.appointment.dao;

import java.util.Map;

import com.yk.common.BaseMapper;

/**
 * 
 * @author Sunny Qi
 * 
 * 20190411
 *
 */
public interface IAppointmentMapper extends BaseMapper{
	
	/**
	 * 新增预备会议到Oracle
	 * */
	public void insertMeeting(Map<String, Object> dataForOracle);
	
}

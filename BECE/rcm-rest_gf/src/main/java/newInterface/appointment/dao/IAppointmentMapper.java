package newInterface.appointment.dao;

import java.util.Map;

import com.yk.common.BaseMapper;
import org.springframework.stereotype.Repository;

/**
 * 
 * @author Sunny Qi
 * 
 * 20190411
 *
 */
@Repository
public interface IAppointmentMapper extends BaseMapper{
	
	/**
	 * 新增预备会议到Oracle
	 * */
	public void insertMeeting(Map<String, Object> dataForOracle);
	
}

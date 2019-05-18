package newInterface.appointment.service.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;
import org.bson.Document;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import newInterface.appointment.dao.IAppointmentMapper;
import newInterface.appointment.service.IAppointmentService;
import util.DateUtil;

@Service
@Transactional
public class appointmentService implements IAppointmentService {
	
	@Resource
	private IAppointmentMapper appointmentMapper;

	@SuppressWarnings("unchecked")
	@Override
	public void saveMeeting(String Json) {
		//Document doc = Document.parse(Json);
		// Map<String, Object> dataForOracle = this.packageDataForOracle(doc);
		HashMap<String, Object> jsonMap = JSON.parseObject(Json, HashMap.class);
		jsonMap.put("flag", "0");
		this.appointmentMapper.insertMeeting(jsonMap);
	}
	
}

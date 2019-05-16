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
	
	@SuppressWarnings("unused")
	private Map<String, Object> packageDataForOracle(Document doc){
		Map<String, Object> data = new HashMap<String, Object>();
		
		data.put("AppointmentMeeTingId", data.get("AppointmentMeeTingId"));
		data.put("TypeCode", data.get("TypeCode"));
		data.put("MeeTingTheme", data.get("MeeTingTheme"));
		data.put("MeeTingContent", data.get("MeeTingContent"));
		data.put("Chairman", data.get("Chairman"));
		data.put("CommitteeMember", data.get("CommitteeMember"));
		data.put("MeeTingBeginTime", data.get("MeeTingBeginTime"));
		data.put("MeeTingEndTime", data.get("MeeTingEndTime"));
		data.put("flag", "0");
		
		return data;
	}
	
}

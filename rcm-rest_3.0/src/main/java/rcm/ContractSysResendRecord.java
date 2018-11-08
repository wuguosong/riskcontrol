package rcm;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.ibatis.session.SqlSession;

import util.DbUtil;

public class ContractSysResendRecord {
	private static final  SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
	private static Logger logger = Logger.getLogger("ContractSysResendRecord");
	
	public static void newRecord(long totalNum, String createDate){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("total", totalNum);
		try {
			params.put("createDate", sdf.parse(createDate));
		} catch (ParseException e) {
			logger.log(Level.WARNING, "Date format must be yyyyMMdd HH:mm:ss");
			e.printStackTrace();
		}
		
		SqlSession session = DbUtil.openSession();
		session.insert("csResend.newRecord", params);
		DbUtil.close();
	}
	
	public static void updateRecord(long successCount, long failCount, String createDate){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("successCount", successCount);
		params.put("failCount", failCount);
		params.put("createDate", createDate);
		
		SqlSession session = DbUtil.openSession();
		session.update("csResend.update", params);
		DbUtil.close();
	}
	
	public static List<Map<String, Object>> getRecords(){
		SqlSession session = DbUtil.openSession();
		return session.selectList("csResend.selectLatestRecords");
	}
}

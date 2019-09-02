/**
 * 
 */
package rcm;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import oracle.sql.CLOB;

import org.apache.ibatis.session.SqlSession;

import util.DbUtil;
import util.Util;

import common.BaseService;
import common.PageAssistant;

/**
 * @Description: 记录webservice调用日志
 * @Author zhangkewei
 * @Date 2016年11月25日 下午1:03:19
 */
public class WebServiceLog extends BaseService {

	public static void insert(String sysName, String messageSend, String messageReceived, String successFlag,
			long useTime, String errorMessage) {
		Map<String, Object> insertMap = new HashMap<String, Object>();
		insertMap.put("id", Util.getUUID());
		insertMap.put("sysName", sysName);
		insertMap.put("messageSend", messageSend);
		insertMap.put("messageReceived", messageReceived);
		insertMap.put("successFlag", successFlag);
		insertMap.put("useTime", useTime);
		insertMap.put("errorMessage", errorMessage);
		SqlSession session = DbUtil.openSession();
		session.insert("wsLog.insert", insertMap);
		DbUtil.close();
	}

	// 仅供portal同步待办、已办、待阅、已阅调用
	public static void portalInsert(String messageSend, String messageReceived, long useTime, String errorMessage) {
		String successFlag = "1";
		String regex = "<code>(\\d{1})</code>";
		Pattern p = Pattern.compile(regex);
		Matcher matcher = p.matcher(messageReceived);
		if (matcher.find()) {
			successFlag = matcher.group(1);
		}
		WebServiceLog.insert("portal", messageSend, messageReceived, successFlag, useTime, errorMessage);
	}

	// 接口日志列表
	public PageAssistant logList(String json) {
		PageAssistant assistant = new PageAssistant(json);
		this.selectByPage(assistant, "wsLog.list");
		return assistant;
	}
	//根据日志id获取日志详情
	public Map<String, Object> findLogById(String json){
		SqlSession session = DbUtil.openSession();
		Map<String, Object> ret = session.selectOne("wsLog.selectById", Util.parseJson2Map(json));
		if(ret != null){
			for(String key : ret.keySet()){
				if(ret.get(key) instanceof CLOB){
					CLOB c = (CLOB)ret.get(key);
					String str = Util.clobToString(c);
					ret.put(key, str);
				}
			}
		}
		return ret;
	}

}

package rcm;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bson.Document;

import common.BaseService;
import common.BusinessException;
import common.PageAssistant;
import util.DbUtil;
import util.Util;

public class Warning extends BaseService{
	public PageAssistant getAll(String json){
		PageAssistant assistant = new PageAssistant(json);
		this.selectByPage(assistant, "warning.selectWarning");
		return assistant;
	}
	public String createWarning(String json){
		Document bjson = Document.parse(json);
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("uuid", Util.getUUID());
		paramMap.put("model_code", bjson.get("MODEL_CODE"));
		paramMap.put("model_name", bjson.get("MODEL_NAME"));
		paramMap.put("warning_day", bjson.get("WARNING_DAY"));
		paramMap.put("state", bjson.get("STATE"));
		paramMap.put("type", bjson.get("TYPE"));
		
	    String count = DbUtil.openSession().selectOne("warning.selectCodeCount",paramMap);
	    DbUtil.close();
	    if( "0".equals(count)){
	    Integer o=	DbUtil.openSession().insert("warning.insertWarning", paramMap);
	    DbUtil.close();
	    return o.toString();
	    }else{
	    	throw new BusinessException("code不能重复");
	    }
	}
	
	public Map getWarningByID(String id){
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("uuid", id);
		Map doc =	DbUtil.openSession().selectOne("warning.selectOneWarning", paramMap);
		DbUtil.close();
		return doc;
	}
	
	public String updateWarning(String json){
		
		Document bjson = Document.parse(json);
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("uuid", bjson.get("UUID"));
		paramMap.put("model_code", bjson.get("MODEL_CODE"));
		paramMap.put("model_name", bjson.get("MODEL_NAME"));
		paramMap.put("warning_day", bjson.get("WARNING_DAY"));
		paramMap.put("state", bjson.get("STATE"));
		paramMap.put("type", bjson.get("TYPE"));
	    Integer o=	DbUtil.openSession().update("warning.updateWarning", paramMap);
	    DbUtil.close();
		return o.toString();
	}
	
	public String deleteWarningByID(String id){
		String[] array={};
		array=id.split(",");
		for(String ids :array){
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("uuid", ids);
			DbUtil.openSession().delete("warning.deleteWarning", paramMap);
			DbUtil.close();
		}
		return "";
	}
}

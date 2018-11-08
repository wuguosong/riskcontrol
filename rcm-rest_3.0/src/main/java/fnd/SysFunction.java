package fnd;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.MongoCursor;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

import common.BaseService;
import common.BusinessException;
import common.PageAssistant;
import util.CrudUtil;
import util.DbUtil;
import util.PageUtil;
import util.Util;



public class SysFunction extends BaseService{
	private static Logger log = Logger.getLogger(SysUser.class);

	
	/**
	 * 用户管理对象
	 */
	private static final long serialVersionUID = 1L;
	
    public List<Map> getOrg(){
	    List<Map> list = DbUtil.openSession().selectList("sysFun.selectOrgALl");
	    DbUtil.close();
	   return list;
    }
    
	public PageAssistant getAll(String json){
		PageAssistant assistant = new PageAssistant(json);
		this.selectByPage(assistant, "sysFun.selectSysFunction");
		return assistant;
	}
	public boolean  Validate(String func_id){
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("func_id", func_id);
		String resultData = DbUtil.openSession().selectOne("sysFun.selectOneFunc_id", paramMap);
		DbUtil.close();
		if(null!=resultData &&!"".equals(resultData)){
			int val=Integer.valueOf(resultData).intValue();
			if(0!=val){
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	}
	
	public String getAllCount(String json){
		Document bjson = Document.parse(json);
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("func_id", bjson.get("func_id"));
		paramMap.put("func_name", bjson.get("func_name"));
		paramMap.put("func_desc", bjson.get("func_desc"));
		paramMap.put("url", bjson.get("url"));
		paramMap.put("create_by", bjson.get("create_by"));
		paramMap.put("state", bjson.get("state"));

		String countNum = DbUtil.openSession().selectOne("sysFun.selectAllCount",paramMap);
		DbUtil.close();
		return countNum;
	}
	public Map getSysFunctionByID(String id){
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("func_id", id);
		Map doc =	DbUtil.openSession().selectOne("sysFun.selectOneSysFunction", paramMap);
		DbUtil.close();
		return doc;
	}
	public String createSysFunction(String json){
		Document bjson = Document.parse(json);
		Map<String, Object> paramMap = new HashMap<String, Object>();
		
		paramMap.put("func_pid", bjson.get("FUNC_PID"));
		paramMap.put("func_name", bjson.get("FUNC_NAME"));
		paramMap.put("state", bjson.get("STATE"));
		paramMap.put("func_desc", bjson.get("FUNC_DESC"));
		paramMap.put("url", bjson.get("URL"));
		paramMap.put("create_by", bjson.get("CREATE_BY"));
		paramMap.put("sort", bjson.get("SORT"));
	    String pId = DbUtil.openSession().selectOne("sysFun.selectPkSysFunction", paramMap);
	    DbUtil.close();
	    String count = DbUtil.openSession().selectOne("sysFun.selectNameCount",paramMap);
	    DbUtil.close();
	    int pkid=0;
	    if(null!=pId && !"".equals(pId) && "0".equals(count)){
	    	pkid=Integer.valueOf(pId).intValue();
		paramMap.put("func_id", pkid+1);
	    Integer o=	DbUtil.openSession().insert("sysFun.insertSysFunction", paramMap);
	    DbUtil.close();
	    return o.toString();
	    }else{
	    	throw new BusinessException("code 不能为空");
	    }
	}
	public String updateSysFunction(String json){
		
		Document bjson = Document.parse(json);
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("func_id", bjson.get("FUNC_ID"));
		
		paramMap.put("func_name", bjson.get("FUNC_NAME"));
		paramMap.put("func_desc", bjson.get("FUNC_DESC"));
		paramMap.put("url", bjson.get("URL"));
		paramMap.put("create_by", bjson.get("CREATE_BY"));
		paramMap.put("state", bjson.get("STATE"));
		paramMap.put("sort", bjson.get("SORT"));
	    Integer o=	DbUtil.openSession().update("sysFun.updateSysFunction", paramMap);
	    DbUtil.close();
		return o.toString();
	}
	
	public String delectSysFunctionByID(String id){
		String[] array={};
		array=id.split(",");
		for(String ids :array){
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("func_id", ids);
			DbUtil.openSession().delete("sysFun.deleteSysFunction", paramMap);
			DbUtil.close();
		}
		return "";
	}
	public boolean SelectCode(String id) {
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("func_id", id);
		String did = DbUtil.openSession().selectOne("sysFun.selectByDelete", paramMap);
		DbUtil.close();
		if ("0".equals(did)) {
			return true;
		} else {
			return false;
		}
	}
	 public List<Map> selectALl(){
		    List<Map> list = DbUtil.openSession().selectList("sysFun.selectALl");
		    DbUtil.close();
		   return list;
	 }
	 public List<Map> selectFuncTree(String json){
	    	Map params = Util.parseJson2Map(json);
		    List<Map> list = DbUtil.openSession().selectList("sysFun.selectFuncTree", params);
		    List<Map>  retList = new ArrayList<Map>();
		    if(Util.isNotEmpty(list)){
		    	for(Map<String, Object> map : list){
		    		String id = (String)map.get("FUNC_ID");
		    		String pid = (String)map.get("FUNC_PID");
		    		String name = (String)map.get("FUNC_NAME");
		    		Boolean isParent = Boolean.valueOf((String)map.get("ISPARENT"));
		    		Map<String, Object> retMap = new HashMap<String, Object>();
		    		retMap.put("id", id);
		    		retMap.put("pid", pid);
		    		retMap.put("name", name);
		    		retMap.put("isParent", isParent);
		    		retList.add(retMap);
		    	}
		    }
		   return retList;
	  }
}

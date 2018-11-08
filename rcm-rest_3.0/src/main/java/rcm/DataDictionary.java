package rcm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bson.Document;

import common.BaseService;
import common.BusinessException;
import common.PageAssistant;
import util.DbUtil;
import util.Util;

public class DataDictionary extends BaseService{
	public PageAssistant getAll(String json){
		PageAssistant assistant = new PageAssistant(json);
		this.selectByPage(assistant, "dictionary.selectDictionary");
		return assistant;
	}
	public String createDictionary(String json){
		Document bjson = Document.parse(json);
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("uuid", Util.getUUID());
		paramMap.put("dictionary_name", bjson.get("DICTIONARY_NAME"));
		paramMap.put("dictionary_code", bjson.get("DICTIONARY_CODE"));
		paramMap.put("dictionary_desc", bjson.get("DICTIONARY_DESC"));
		String pId = DbUtil.openSession().selectOne("dictionary.selectPkDictionary", paramMap);
		DbUtil.close();
	    if("0".equals(pId) ){
		    Integer o=	DbUtil.openSession().insert("dictionary.insertDictionary", paramMap);
		    DbUtil.close();
		    return o.toString();
	    }else{
	    	throw new BusinessException("code 不能为空");
	    	
	    }
	}
public String updateDictionary(String json){
		Document bjson = Document.parse(json);
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("uuid", bjson.get("UUID"));
		paramMap.put("dictionary_code", bjson.get("DICTIONARY_CODE"));
		paramMap.put("dictionary_name", bjson.get("DICTIONARY_NAME"));
		paramMap.put("dictionary_desc", bjson.get("DICTIONARY_DESC"));
//		Map<String, Object> paramMap = Util.parseJson2Map(json);
		String kid = DbUtil.openSession().selectOne("dictionary.selectPkDictionary", paramMap);
		DbUtil.close();
		if("0".equals(kid)){
			Integer o=	DbUtil.openSession().update("dictionary.updateDictionary", paramMap);
			DbUtil.close();
			return o.toString();
		}else{
			throw new BusinessException("Code已存在");
		}
	}
public Map getDictionaryByID(String id){
	Map<String, String> paramMap = new HashMap<String, String>();
	paramMap.put("uuid", id);
	Map doc =	DbUtil.openSession().selectOne("dictionary.selectOneDictionary", paramMap);
	DbUtil.close();
	return doc;
}
public String deleteDictionaryByID(String id){
	String[] array={};
	array=id.split(",");
	for(String ids :array){
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("uuid", ids);
		DbUtil.openSession().delete("dictionary.deleteDictionary", paramMap);
		DbUtil.close();
	}
	return "";
}
public boolean SelectCode(String arrId) {
	String[] array={};
	array=arrId.split(",");
	boolean flag=true;
	for(String id :array){
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("uuid", id);
		String count =DbUtil.openSession().selectOne("dictionary.selectByDelete", paramMap);
		DbUtil.close();
		if (!"0".equals(count)) {
			flag= false;
			break;
		} 
	}
	return flag;
}

	public List<Map> getDicByItemKind(String json){
		Map<String, Object> params = Util.parseJson2Map(json);
		List<Map> list = DbUtil.openSession().selectList("dictionary.selectByKind", params);
		return list;
	}

}



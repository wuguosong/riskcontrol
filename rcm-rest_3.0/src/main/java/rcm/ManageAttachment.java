package rcm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bson.Document;

import common.BaseService;
import common.PageAssistant;
import util.DbUtil;
import util.Util;

public class ManageAttachment extends BaseService {
	public PageAssistant getAll(String json) {
		PageAssistant assistant = new PageAssistant(json);
		this.selectByPage(assistant, "attachment.selectAttachment");
		return assistant;
	}
	
	public PageAssistant ListBusinessAndAttachment(String json) {
		PageAssistant assistant = new PageAssistant(json);
		this.selectByPage(assistant, "attachment.ListBusinessAndAttachment");
		return assistant;
	}
	
	public String Delete(String id){
		String[] array={};
		array=id.split(",");
		for(String ids :array){
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("uuid", ids);
			DbUtil.openSession().delete("attachment.deleteAll", paramMap);
			DbUtil.close();
		}
		return "";
	}

	public List<Map> SelectType(String json) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("code", json);
		List<Map> o=DbUtil.openSession().selectList("attachment.selecFunctionType", paramMap);
		DbUtil.close();
		return o;
	}
	public List<Map> SelectProjectType(String json) {
		Document bjson = Document.parse(json);
		Map<String, Object> paramMap = new HashMap<String, Object>();
		List<Map> o=DbUtil.openSession().selectList("attachment.selecProjectType", paramMap);
		DbUtil.close();
		return o;
	}
	
	public PageAssistant listBusiness(String json) {
		PageAssistant assistant = new PageAssistant(json);
		this.selectByPage(assistant, "attachment.listBusiness");
		return assistant;
	}

	public String addAttachment(String json) {
		Document bjson = Document.parse(json);
		String arrId = bjson.get("UUID").toString();
		String[] array = {};
		array = arrId.split(",");
		for (String id : array) {
			Map<String, Object> paramMap1 = new HashMap<String, Object>();
			Map<String, Object> paramMap2 = new HashMap<String, Object>();
			paramMap2.put("bu_atta_uuid", Util.getUUID());
			paramMap2.put("business_id", bjson.get("BUUID").toString());
			paramMap2.put("business_name", bjson.get("BBUSINESS_NAME").toString());
			paramMap2.put("state", "1");
			paramMap2.put("fuctiontype", bjson.get("BUSINESS_TYPE").toString());
//			paramMap2.put("describe", bjson.get("BITEM_NAME").toString());
			paramMap2.put("attachment_uuid", id);
			String obj=DbUtil.openSession().selectOne("attachment.selectDictionaryName", paramMap2);
			DbUtil.close();
			paramMap2.put("attachment_name",obj);
			String count=DbUtil.openSession().selectOne("attachment.selectCountAttachment", paramMap2);
			DbUtil.close();
			if("0".equals(count)){
			Integer o=	DbUtil.openSession().insert("attachment.insertAttachmentToBusiness", paramMap2);
			DbUtil.close();
			}
		}
		return "";
		
	}
}

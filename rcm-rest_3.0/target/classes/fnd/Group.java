package fnd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.bson.Document;

import util.DbUtil;
import util.ThreadLocalUtil;
import util.Util;

import com.yk.common.SpringUtil;
import com.yk.power.service.IOrgService;
import common.BaseService;
import common.BusinessException;
import common.Constants;
import common.PageAssistant;



public class Group extends BaseService{
	private static Logger log = Logger.getLogger(SysUser.class);

	
	/**
	 * 用户管理对象
	 */
	private static final long serialVersionUID = 1L;
	
    public List<Map> getOrg(String json){
    	Map params = Util.parseJson2Map(json);
	    List<Map> list = DbUtil.openSession().selectList("group.selectOrg", params);
	    List<Map>  retList = new ArrayList<Map>();
	    if(Util.isNotEmpty(list)){
	    	for(Map<String, Object> map : list){
	    		String id = (String)map.get("ID");
	    		String pid = (String)map.get("PID");
	    		String name = (String)map.get("NAME");
	    		Boolean isParent = Boolean.valueOf((String)map.get("ISPARENT"));
	    		Map<String, Object> retMap = new HashMap<String, Object>();
	    		retMap.put("id", id);
	    		retMap.put("pid", pid);
	    		retMap.put("name", name);
	    		retMap.put("isParent", isParent);
	    		retMap.put("cat", map.get("CATEGORYCODE"));
	    		retList.add(retMap);
	    	}
	    }
	    //DbUtil.close();
	   return retList;
    }
    public List<Map> getCommonOrg(String json){
    	Map params = Util.parseJson2Map(json);
	    List<Map> list = DbUtil.openSession().selectList("common.selectsCommonOrg", params);
	    List<Map>  retList = new ArrayList<Map>();
	    if(Util.isNotEmpty(list)){
	    	for(Map<String, Object> map : list){
	    		String id = (String)map.get("ID");
	    		String pid = (String)map.get("PID");
	    		String name = (String)map.get("NAME");
	    		Boolean isParent = Boolean.valueOf((String)map.get("ISPARENT"));
	    		Map<String, Object> retMap = new HashMap<String, Object>();
	    		retMap.put("id", id);
	    		retMap.put("pid", pid);
	    		retMap.put("name", name);
	    		retMap.put("isParent", isParent);
	    		retMap.put("cat", map.get("CATEGORYCODE"));
	    		retList.add(retMap);
	    	}
	    }
	    //DbUtil.close();
	   return retList;
    }
	public PageAssistant getAll(String json){
		PageAssistant assistant = new PageAssistant(json);
		this.selectByPage(assistant, "group.selectGroup");
		return assistant;
	}
	public boolean  Validate(String code){
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("code", code);
		String resultData = DbUtil.openSession().selectOne("group.selectOneCode", paramMap);
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
	
	public Map getGroupByID(String org_pk_value){
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("org_pk_value", org_pk_value);
		Map doc =	DbUtil.openSession().selectOne("group.selectOneGroup", paramMap);
		DbUtil.close();
		return doc;
	}
	public String createGroup(String json){
		Document bjson = Document.parse(json);
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("uuid", Util.getUUID());
		paramMap.put("address", bjson.get("ADDRESS"));
		paramMap.put("code", bjson.get("CODE"));
		paramMap.put("name", bjson.get("NAME"));
		paramMap.put("contacts", bjson.get("CONTACTS"));
		paramMap.put("org_short_name", bjson.get("ORG_SHORT_NAME"));
		paramMap.put("state", bjson.get("STATE"));
		paramMap.put("telephone", bjson.get("TELEPHONE"));
		paramMap.put("post_code", bjson.get("POST_CODE"));
		paramMap.put("area_code", bjson.get("AREA_CODE"));
		paramMap.put("character_code", bjson.get("CHARACTER_CODE"));
		paramMap.put("normal_character", bjson.get("NORMAL_CHARACTER"));
		paramMap.put("category_code", bjson.get("CATEGORY_CODE"));
		paramMap.put("change_type_code", bjson.get("CHANGE_TYPE_CODE"));
		paramMap.put("receive_org_code", bjson.get("RECEIVE_ORG_CODE"));
		paramMap.put("merge_org_code", bjson.get("MERGE_ORG_CODE"));
		paramMap.put("legal_person", bjson.get("LEGAL_PERSON"));
		paramMap.put("parent_pk_value", bjson.get("PARENT_PK_VALUE"));
		paramMap.put("market", bjson.get("MARKET"));
		paramMap.put("entity", bjson.get("ENTITY"));
		paramMap.put("financial_org_code", bjson.get("FINANCIAL_ORG_CODE"));
		paramMap.put("fax", bjson.get("FAX"));
		 String pId = DbUtil.openSession().selectOne("group.selectPkGroup", paramMap);
		 DbUtil.close();
		    String count = DbUtil.openSession().selectOne("group.selectCodeCount", paramMap);
		    DbUtil.close();
		    int pkid=0;
		    if(null==pId || "".equals(pId) || "null".equals(pId)){
		    	pId="0";
		    }
		    if(null!=pId && !"".equals(pId) && "0".equals(count)){
		    	pkid=Integer.valueOf(pId).intValue();
				paramMap.put("org_pk_value", pkid+1);
			    Integer o=	DbUtil.openSession().insert("group.insertGroup", paramMap);
			    DbUtil.close();
			    return o.toString();
		    }else{
		    	throw new BusinessException("code不能重复");
		    }
	}
	public String updateGroup(String json){
		
		Document bjson = Document.parse(json);
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("uuid", bjson.get("UUID"));
		paramMap.put("address", bjson.get("ADDRESS"));
		paramMap.put("code", bjson.get("CODE"));
		paramMap.put("name", bjson.get("NAME"));
		paramMap.put("contacts", bjson.get("CONTACTS"));
		paramMap.put("org_short_name", bjson.get("ORG_SHORT_NAME"));
		paramMap.put("state", bjson.get("STATE"));
		paramMap.put("telephone", bjson.get("TELEPHONE"));
		paramMap.put("post_code", bjson.get("POST_CODE"));
		paramMap.put("area_code", bjson.get("AREA_CODE"));
		paramMap.put("character_code", bjson.get("CHARACTER_CODE"));
		paramMap.put("normal_character", bjson.get("NORMAL_CHARACTER"));
		paramMap.put("category_code", bjson.get("CATEGORY_CODE"));
		paramMap.put("change_type_code", bjson.get("CHANGE_TYPE_CODE"));
		paramMap.put("receive_org_code", bjson.get("RECEIVE_ORG_CODE"));
		paramMap.put("merge_org_code", bjson.get("MERGE_ORG_CODE"));
		paramMap.put("legal_person", bjson.get("LEGAL_PERSON"));
		paramMap.put("market", bjson.get("MARKET"));
		paramMap.put("entity", bjson.get("ENTITY"));
		paramMap.put("financial_org_code", bjson.get("FINANCIAL_ORG_CODE"));
		paramMap.put("fax", bjson.get("FAX"));
	    String count = DbUtil.openSession().selectOne("group.selectGroupCode", paramMap);
	    DbUtil.close();
        if("0".equals(count)){
    	    Integer o=	DbUtil.openSession().update("group.updateGroup", paramMap);
    	    DbUtil.close();
		    return o.toString();
        }else{
        	throw new BusinessException("code不能重复");
        }
	}
	
	public String delectGroupByID(String id){
		String[] array={};
		array=id.split(",");
		for(String ids :array){
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("uuid", ids);
			DbUtil.openSession().delete("group.deleteGroup", paramMap);
			DbUtil.close();
		}
		return "";
	}
	public Map getGroupBypkValue(String json){
		Document bjson = Document.parse(json);
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("org_pk_value", bjson.getString("ORG_PK_VALUE"));
		Map doc =	DbUtil.openSession().selectOne("group.selectGroupBypkValue", paramMap);
		DbUtil.close();
		return doc;
	}

	public boolean SelectCode(String arrId) {
		String[] array={};
		array=arrId.split(",");
		boolean flag=true;
		for(String id :array){
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("uuid", id);
			Map doc =	DbUtil.openSession().selectOne("group.selectOneGroup", paramMap);
			DbUtil.close();
			paramMap.put("org_pk_value", doc.get("ORG_PK_VALUE").toString());
			String did =DbUtil.openSession().selectOne("group.selectByDelete", paramMap);
			DbUtil.close();
			if (!"0".equals(did)) {
				flag= false;
				break;
			} 
		}
		return flag;
	}
	
	public String getGroupAndUserAndReportingUnitByParam(Map<String, Object> paramMap){
		Map<String, String> paramMap1 = new HashMap<String, String>();
		if(null!=paramMap.get("ID")){
			paramMap1.put("ID", paramMap.get("ID").toString());
		}
		paramMap1.put("REPORTINGUNIT_ID", paramMap.get("REPORTINGUNIT_ID").toString());
		String count =	DbUtil.openSession().selectOne("group.selectGroupAndUserAndReportingUnitIDparam", paramMap1);
		return count;
	}
	
	public String CreateGroupUserReportingUnit(String json){
		Document bjson = Document.parse(json);
		Map<String, Object> paramMap = new HashMap<String, Object>();
		
		paramMap.put("REPORTINGUNIT_NAME", bjson.get("REPORTINGUNIT_NAME"));
		paramMap.put("REPORTINGUNIT_ID", bjson.get("REPORTINGUNIT_ID"));
		//paramMap.put("INVESTMENTMANAGER_NAME", bjson.get("INVESTMENTMANAGER_NAME"));
		//paramMap.put("INVESTMENTMANAGER_ID", bjson.get("INVESTMENTMANAGER_ID"));
		paramMap.put("COMPANYHEADER_NAME", bjson.get("COMPANYHEADER_NAME"));
		paramMap.put("COMPANYHEADER_ID", bjson.get("COMPANYHEADER_ID"));
		paramMap.put("GRASSROOTSLEGALSTAFF_NAME", bjson.get("GRASSROOTSLEGALSTAFF_NAME"));
		paramMap.put("GRASSROOTSLEGALSTAFF_ID", bjson.get("GRASSROOTSLEGALSTAFF_ID"));
		paramMap.put("ID", Util.getUUID());
		String pId = getGroupAndUserAndReportingUnitByParam(paramMap);
	    if(null!=pId && !"".equals(pId) && "0".equals(pId)){
		    Integer o=	DbUtil.openSession().insert("group.insertGroupUserReportingUnit", paramMap);
		    DbUtil.close();
		    return o.toString();
	    }else{
	    	throw new BusinessException("申报单位不能重复");
	    }
	}
	public String UpdateGroupUserReportingUnit(String json){
		Document bjson = Document.parse(json);
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("ID",  bjson.get("ID"));
		paramMap.put("REPORTINGUNIT_NAME", bjson.get("REPORTINGUNIT_NAME"));
		paramMap.put("REPORTINGUNIT_ID", bjson.get("REPORTINGUNIT_ID"));
		//paramMap.put("INVESTMENTMANAGER_NAME", bjson.get("INVESTMENTMANAGER_NAME"));
		//paramMap.put("INVESTMENTMANAGER_ID", bjson.get("INVESTMENTMANAGER_ID"));
		paramMap.put("COMPANYHEADER_NAME", bjson.get("COMPANYHEADER_NAME"));
		paramMap.put("COMPANYHEADER_ID", bjson.get("COMPANYHEADER_ID"));
		paramMap.put("GRASSROOTSLEGALSTAFF_NAME", bjson.get("GRASSROOTSLEGALSTAFF_NAME"));
		paramMap.put("GRASSROOTSLEGALSTAFF_ID", bjson.get("GRASSROOTSLEGALSTAFF_ID"));
		String pId = getGroupAndUserAndReportingUnitByParam(paramMap);
	    if(null!=pId && !"".equals(pId) && "0".equals(pId)){
		    Integer o=	DbUtil.openSession().update("group.updateGroupUserReportingUnit", paramMap);
		    DbUtil.close();
		    return o.toString();
	    }else{
	    	throw new BusinessException("投资经理不能重复");
	    }
	}
	public String delectGroupUserReportingUnitByID(String id){
		String[] array={};
		array=id.split(",");
		for(String ids :array){
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("ID", ids);
			DbUtil.openSession().delete("group.deleteGroupUserReportingUnit", paramMap);
			DbUtil.close();
		}
		return "";
	}
	@SuppressWarnings("unused")
	public Map getGroupUserReportingUnit(String userAccount){
		/*Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("ACCOUNT", userAccount);
		Map doc =	DbUtil.openSession().selectOne("group.selectGroupUserReportingUnit", paramMap);
		 
		if(null==doc){
		   String names =	DbUtil.openSession().selectOne("group.selectOrgNameByAccount", paramMap);
		   if(null!=names && !"".equals(names)){
			   if("北控水务（中国）投资有限公司".equals(names)){
					doc = DbUtil.openSession().selectOne("group.selectGroupUserReportingUnitByDeptpkvalue", paramMap);
			   }else{
				    doc = DbUtil.openSession().selectOne("group.selectGroupUserReportingUnitByAccount", paramMap);
			   }
		   }
		}*/
		
		IOrgService orgService = (IOrgService) SpringUtil.getBean("orgService");
		Map<String, Object> doc = orgService.queryGroupUserInfo(ThreadLocalUtil.getUserId());
		String pertainArea = (String) doc.get("REPORTINGUNIT_ID");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("reportUnitId", pertainArea);
		List<String> areaCodes = new ArrayList<String>();
		areaCodes.add(Constants.SYS_ORG_CODE_EAST);
		areaCodes.add(Constants.SYS_ORG_CODE_WEST);
		areaCodes.add(Constants.SYS_ORG_CODE_SOUTH);
		areaCodes.add(Constants.SYS_ORG_CODE_NORTH);
		areaCodes.add(Constants.SYS_ORG_CODE_CENTER);
		params.put("areaCodes", areaCodes);
		Map selectOne = DbUtil.openSession().selectOne("group.queryPertainAreaByPkvalue", params);
		if(!Util.isEmpty(selectOne)){
			doc.put("orgpkValue", selectOne.get("ORGPKVALUE"));
			doc.put("orgpkName", selectOne.get("NAME"));
		}
		return doc;
	}
	
	/*区域、直接负责人*/
	public PageAssistant getDirectUserReportingUnitAll(String json){
		PageAssistant assistant = new PageAssistant(json);
		this.selectByPage(assistant, "group.selectDirectUserReportingUnitList");
		return assistant;
	}
	public Map getDirectUserReportingUnitByID(String id){
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("id", id);
		Map doc =	DbUtil.openSession().selectOne("group.selectDirectUserReportingUnitID", paramMap);
		return doc;
	}
	public String getDirectUserReportingUnitByParam(Map<String, Object> paramMap){
		String count =	DbUtil.openSession().selectOne("group.selectDirectUserReportingUnitParam", paramMap);
		return count;
	}
	
	public String CreateDirectUserReportingUnit(String json){
		Document bjson = Document.parse(json);
		Map<String, Object> paramMap = new HashMap<String, Object>();
		
		paramMap.put("REPORTINGUNIT_NAME", bjson.get("REPORTINGUNIT_NAME"));
		paramMap.put("REPORTINGUNIT_ID", bjson.get("REPORTINGUNIT_ID"));
		paramMap.put("INVESTMENTPERSON_NAME", bjson.get("INVESTMENTPERSON_NAME"));
		paramMap.put("INVESTMENTPERSON_ID", bjson.get("INVESTMENTPERSON_ID"));
		paramMap.put("DIRECTPERSON_NAME", bjson.get("DIRECTPERSON_NAME"));
		paramMap.put("DIRECTPERSON_ID", bjson.get("DIRECTPERSON_ID"));
		paramMap.put("TYPE", bjson.get("TYPE"));
		String pId = getDirectUserReportingUnitByParam(paramMap);
		paramMap.put("ID", Util.getUUID());
	    if(null!=pId && !"".equals(pId) && "0".equals(pId)){
		    Integer o=	DbUtil.openSession().insert("group.insertDirectUserReportingUnit", paramMap);
		    DbUtil.close();
		    return o.toString();
	    }else{
	    	throw new BusinessException("申报单位不能重复");
	    }
	}
	public String UpdateDirectUserReportingUnit(String json){
		Document bjson = Document.parse(json);
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("ID",  bjson.get("ID"));
		paramMap.put("REPORTINGUNIT_NAME", bjson.get("REPORTINGUNIT_NAME"));
		paramMap.put("REPORTINGUNIT_ID", bjson.get("REPORTINGUNIT_ID"));
		paramMap.put("INVESTMENTPERSON_NAME", bjson.get("INVESTMENTPERSON_NAME"));
		paramMap.put("INVESTMENTPERSON_ID", bjson.get("INVESTMENTPERSON_ID"));
		paramMap.put("DIRECTPERSON_NAME", bjson.get("DIRECTPERSON_NAME"));
		paramMap.put("DIRECTPERSON_ID", bjson.get("DIRECTPERSON_ID"));
		paramMap.put("TYPE", bjson.get("TYPE"));
		String pId = getDirectUserReportingUnitByParam(paramMap);
	    if(null!=pId && !"".equals(pId) && "0".equals(pId)){
		    Integer o=	DbUtil.openSession().update("group.updateDirectUserReportingUnit", paramMap);
		    DbUtil.close();
		    return o.toString();
	    }else{
	    	throw new BusinessException("不能重复");
	    }
	}
	public String delectDirectUserReportingUnitByID(String id){
		String[] array={};
		array=id.split(",");
		for(String ids :array){
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("ID", ids);
			DbUtil.openSession().delete("group.deleteDirectUserReportingUnit", paramMap);
			DbUtil.close();
		}
		return "";
	}
	public List<Map> findDirectUserReportingUnitList(String json){
		Map params = Util.parseJson2Map(json);
		String type=params.get("TYPE").toString();
		String[] arr=type.split(",");
		Boolean a=Arrays.asList(arr).contains("4");
		List<Map> list = DbUtil.openSession().selectList("group.findDirectUserReportingUnitList", params);
		Map<String, String> paramMap = new HashMap<String, String>();
		if(a){
			paramMap.put("TYPE", "4");
			List<Map> list2 = DbUtil.openSession().selectList("group.findDirectUserReportingUnitList", paramMap);
			list.addAll(list2);
		}
		return list;
	}
}
    

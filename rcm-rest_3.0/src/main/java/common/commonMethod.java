package common;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.ibatis.session.SqlSession;
import org.apache.poi.util.SystemOutLogger;
import org.bson.Document;

import util.CrudUtil;
import util.DbUtil;
import util.ThreadLocalUtil;
import util.Util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCursor;

public class commonMethod extends BaseService{
		private final String DocumentNamePre=Constants.PRE_ASSESSMENT;
		private final String DocumentNameFormal=Constants.RCM_PROJECTFORMALREVIEW_INFO;
		private final String DocumentNameFormalReport=Constants.RCM_FORMALREPORT_INFO;
		public List<Map<String, Object>> getTeamList(){
			List<Map<String, Object>> commonTeam =	DbUtil.openSession().selectList("team.commonTeamPS");
			DbUtil.close();
			
			Map<Integer, List<Map<String, Object>>> tmMap = new TreeMap<Integer, List<Map<String, Object>>>();
			if(Util.isNotEmpty(commonTeam)){
				Integer teamNum = null;
				List<Map<String, Object>> subTeamList=null;
				for(Map<String, Object> map : commonTeam){
					Integer tm = ((BigDecimal)map.get("ORDERNUM")).intValue();
					if(tm.equals(teamNum)){
						subTeamList = tmMap.get(tm);
						subTeamList.add(map);
					}else{
						teamNum = tm;
						subTeamList = new ArrayList<Map<String, Object>>();
						subTeamList.add(map);
						tmMap.put(tm, subTeamList);
					}
				}
			}
			List<Map<String, Object>> retList = new ArrayList<Map<String, Object>>();
			for(Integer key : tmMap.keySet()){
				Map<String, Object> innerMap = new HashMap<String, Object>();
				innerMap.put("name", tmMap.get(key).get(0).get("TEAM_NAME"));
				innerMap.put("value", tmMap.get(key));
				retList.add(innerMap);
			}
			return retList;
		}
		public List<Map<String, Object>> getTeamListFLPS(){
		List<Map<String, Object>> commonTeam =	DbUtil.openSession().selectList("team.commonTeamFLPS");
		DbUtil.close();
		
		Map<Integer, List<Map<String, Object>>> tmMap = new TreeMap<Integer, List<Map<String, Object>>>();
		if(Util.isNotEmpty(commonTeam)){
			Integer teamName = null;
			List<Map<String, Object>> subTeamList=null;
			for(Map<String, Object> map : commonTeam){
				Integer tm = ((BigDecimal)map.get("ORDERNUM")).intValue();
				if(tm.equals(teamName)){
					subTeamList = tmMap.get(tm);
					subTeamList.add(map);
				}else{
					teamName=tm;
					subTeamList = new ArrayList<Map<String, Object>>();
					subTeamList.add(map);
					tmMap.put(tm, subTeamList);
				}
			}
		}
		List<Map<String, Object>> retList = new ArrayList<Map<String, Object>>();
		for(Integer key : tmMap.keySet()){
			Map<String, Object> innerMap = new HashMap<String, Object>();
			innerMap.put("name", tmMap.get(key).get(0).get("TEAM_NAME"));
			innerMap.put("value", tmMap.get(key));
			retList.add(innerMap);
		}
		return retList;
	}
		//添加预评审，加载字典表
		public Map<String, List<Map>> getRoleuser(String json) {
			Map<String, List<Map>> param = new HashMap<String, List<Map>>();
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("roleId", json);
			List<Map> obj=DbUtil.openSession().selectList("common.selectRoleUserByRoleID", paramMap);
			DbUtil.close();
			param.put("userRoleList", obj);
			return param;
		}
		
		//根据角色code查找角色用户
		public List<Map<String, Object>> getRoleuserByCode(String code) {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("code", code);
			List<Map<String, Object>> retList =DbUtil.openSession().selectList("common.selectRoleUserByRoleCode", paramMap);
			return retList;
		}
		
		public List<Map> selectDataDictionByCode(String json){
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("code", json);
			List<Map> list =DbUtil.openSession().selectList("common.selecFunctionType", paramMap);
			DbUtil.close();
			return list;
		}
		
		public List<Map> selectDataDictionByCode1(String json){
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("code", json);
			List<Map> list =DbUtil.openSession().selectList("common.selecFunctionType", paramMap);
			DbUtil.close();
			return list;
		}
		
		//获取预评审列表
		public List<Document> getProjectPreReviewList(String json){
			MongoCursor<Document> cursor= DbUtil.getColl(DocumentNamePre).find().iterator();
			List<Document> docList = CrudUtil.convertCursorToList(cursor);
			return docList;
		}
		//获取没有生成报告的评审列表 
		public List<Map<String, Object>> getProjectPreReviewNoReportList(String json){
			Document bjson=Document.parse(json);
			String type=bjson.get("type").toString();
			Map<String, Object> paramMap = new HashMap<String, Object>();
			if("pre".equals(type)){
				paramMap.put("type", Constants.PRE_ASSESSMENT);
			}else if("pfr".equals(type)){
				paramMap.put("type", Constants.FORMAL_ASSESSMENT);
			}
			paramMap.put("user_id", bjson.get("user_id"));
			
			List<Map<String, Object>> docList=DbUtil.openSession().selectList("projectInfo.selectReportByType", paramMap);
			return docList;
		}
		//获取正式评审列表
		public List<Document> getProjectFormalReviewList(String json){
			Document bjson=Document.parse(json);
			//业务承诺列表查询才添加这个参数，表示查询没有建立承诺列表的项目
			BasicDBObject queryAndWhere =new BasicDBObject();
			BasicDBObject queryVal =new BasicDBObject();
			BasicDBObject querypam =new BasicDBObject();
			if(null!=bjson.get("pfrBusinessUnitCommit") && !"".equals(bjson.get("pfrBusinessUnitCommit"))){
				 queryVal.append("$eq", null);
			     queryAndWhere.put("pfrBusinessUnitCommit",queryVal);
			     querypam.append("$ne", null);
			     queryAndWhere.put("policyDecision",querypam);
			}
			if(null!=bjson.get("pfrExperience") && !"".equals(bjson.get("pfrExperience"))){
				 queryVal.append("$eq", null);
			     queryAndWhere.put("pfrExperience",queryVal);
			     querypam.append("$ne", null);
			     queryAndWhere.put("policyDecision",querypam);
			}
			if(Util.isNotEmpty(bjson.get("user_id"))){
				if(!ThreadLocalUtil.getIsAdmin()){
					queryAndWhere.put("create_by", (String)bjson.get("user_id"));
				}
			}
			
			MongoCursor<Document> cursor= DbUtil.getColl(DocumentNameFormalReport).find(queryAndWhere).iterator();
			List<Document> docList = new ArrayList<Document>();
			
			//业务承诺列表查询 排除未提交决策通知书
			if(null!=bjson.get("pfrBusinessUnitCommit") && !"".equals(bjson.get("pfrBusinessUnitCommit"))){
				SqlSession session = DbUtil.openSession();
				while (cursor.hasNext()) {
					//转换Document的id值为常规方式
				    Document doc=cursor.next();
				    String formalId = doc.getString("projectFormalId");
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("formalId", formalId);
					List<Map<String, Object>> list = session.selectList("noticeOfDecisionInfo.selectByFormalId", params);
					if(Util.isNotEmpty(list)){
						doc.put("_id", doc.get("_id").toString());
						docList.add(doc);
					}
				}
				DbUtil.close();
			}else{
				docList = CrudUtil.convertCursorToList(cursor);
			}
			
			return docList;
		}
		public List<Map> selectsyncbusinessmodel(String key){
			Map<String, String> paramMap = new HashMap<String, String>();
			if (key.equals("0")){
				paramMap.put("key", "CUST_TEXT01");
			}else {
				paramMap.put("key", "KEY");
			}
			List<Map> list =DbUtil.openSession().selectList("common.selectsyncbusinessmodel", paramMap);
			DbUtil.close();
			return list;
		}
		public List<Map> selectsyncbusinessmodelBykey(String json){
			Document bjson = Document.parse(json);
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("parent_id", bjson.getString("parentId"));
			List<Map> list =DbUtil.openSession().selectList("common.selectsyncbusinessmodelBykey", paramMap);
			DbUtil.close();
			return list;
		}
		/*获取主数据中的项目名称及编码*/
		public PageAssistant getDirectiveCompanyList(String json){
			PageAssistant assistant = new PageAssistant(json);
			this.selectByPage(assistant, "common.selectCompanyList");
			return assistant;
		}
		//获取菜单列表
		public List<Map> getSysFuncList(String uerid){
			Map<String, String> paramP = new HashMap<String, String>();
			paramP.put("user_id", uerid);
			paramP.put("func_pid", "0");
			List<Map> sysFunList =	DbUtil.openSession().selectList("common.selectSysFunction",paramP);
			DbUtil.close();
			if(Util.isNotEmpty(sysFunList)){
				for(Map map : sysFunList){
					String tm = (String)map.get("FUNC_ID");
					Map<String, String> paramS = new HashMap<String, String>();
					paramS.put("func_pid", tm);
					paramS.put("user_id", uerid);
					List<Map> SubsysFun =	DbUtil.openSession().selectList("common.selectSysFunction",paramS);
					DbUtil.close();
					map.put("subFunc", SubsysFun);
					if(Util.isNotEmpty(SubsysFun)){
						for(Map map2 : SubsysFun){
							String tm2 = (String)map2.get("FUNC_ID");
							Map<String, String> paramS2 = new HashMap<String, String>();
							paramS2.put("func_pid", tm2);
							paramS2.put("user_id", uerid);
							List<Map> SubsysFun2 =	DbUtil.openSession().selectList("common.selectSysFunction",paramS2);
							DbUtil.close();
							if(Util.isNotEmpty(SubsysFun2)){
								map2.put("subsubFunc", SubsysFun2);
							}
							
						}
					}
				}
			}
			return sysFunList;
		}
		//根据字典子编码查询字典name
		public static String getSubDataDictionaryByCode(String code){
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("code", code);
			String codeName=DbUtil.openSession().selectOne("common.selectSubDataDictionaryByCode", paramMap);
			DbUtil.close();
			return codeName;
		}
		
		//只查询系统管理员与会议管理人员
		public String getSystemAndMeetAdmin(String json){
			Document doc = Document.parse(json);
			Map<String, Object> paramMapRole = new HashMap<String, Object>();
			paramMapRole.put("user_id", doc.get("user_id"));
			paramMapRole.put("code", "'"+Constants.ROLE_MEETING_ADMIN+"','"+Constants.ROLE_SYSTEM_ADMIN+"'");
			return DbUtil.openSession().selectOne("role.hasRole2",paramMapRole);
		}
		//决策委员会新建查询可以创建的项目
		public List<Map> selectProjectFormalForNoticeof(){
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("userId", ThreadLocalUtil.getUserId());
			List<Map> list =DbUtil.openSession().selectList("noticeOfDecisionInfo.selectProjectformaForNoticeof",params);
			DbUtil.close();
			return list;
		}
		/**
		 * 根据项目模式查询预评审列表
		 * @param json
		 * @return
		 */
		@SuppressWarnings("rawtypes")
		public List<Map> findProjectTypeList(String json){
			Map params = Util.parseJson2Map(json);
			List<Map> list =DbUtil.openSession().selectList("common.selectsyncbusinessmodelBykey", params);
			return list;
		}
		/**
		 * 查询所有的二级业务类型
		 * @return
		 */
		public List<Map<String, Object>> queryAllProjectTypes(){
			return DbUtil.openSession().selectList("common.queryAllProjectTypes");
		}
		
		/**
		 * 查询附件类型
		 * */
		public List<Map> getAttachmentType(String json){
			System.out.println(json);
			Map<String, Object> paramMap = Util.parseJson2Map(json);
			List<Map> list =DbUtil.openSession().selectList("common.getAttachmentList", paramMap);
			DbUtil.close();
			return list;
		}
		
		/**
		 * 通过组织key，查出对应大区的值
		 * */
		public List<Map> gePertainArea(String json){
			System.out.println(json);
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("ORGCODE", json);
			paramMap.put("CODE", Constants.SYS_ORG_CODE_ROOT);
			List<Map> list = DbUtil.openSession().selectList("common.gePertainArea", paramMap);
			DbUtil.close();
			return list;
		}
}
